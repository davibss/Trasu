package br.com.trasudev.trasu.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import br.com.trasudev.trasu.R;
import br.com.trasudev.trasu.classes.CartListAdapter;
import br.com.trasudev.trasu.classes.CartListGrupalAdapter;
import br.com.trasudev.trasu.classes.CartUserAdapter;
import br.com.trasudev.trasu.classes.CircleTransform;
import br.com.trasudev.trasu.classes.Conexao;
import br.com.trasudev.trasu.classes.CustomItemClickListener;
import br.com.trasudev.trasu.classes.RecyclerItemTouchHelper;
import br.com.trasudev.trasu.entidades.Grupo;
import br.com.trasudev.trasu.entidades.Realiza;
import br.com.trasudev.trasu.entidades.TarefaGrupal;
import br.com.trasudev.trasu.entidades.TarefaIndividual;
import br.com.trasudev.trasu.entidades.Usuario;

import static br.com.trasudev.trasu.activitys.LoginActivity.calledAlready;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TarefaGrupalFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TarefaGrupalFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TarefaGrupalFragment extends Fragment {
    private FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    private Grupo grupo;

    private String checkValue;
    private AlertDialog dialog;
    private AlertDialog alerta;
    private FloatingActionButton floatingActionButton;

    private TextView textView;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    StorageReference storageReference;

    private RecyclerView recyclerView;
    private List<TarefaGrupal> cartList;
    private CartListGrupalAdapter mAdapter;
    private CoordinatorLayout coordinatorLayout;

    private RecyclerView recyclerViewRealizadores;
    private List<Usuario> cartListRealizadores;
    private CartUserAdapter mAdapterRealizadores;


    private String mParam1;
    private String mParam2;

    private Context context;

    private OnFragmentInteractionListener mListener;

    private void initFirebase() {
        FirebaseApp.initializeApp(getContext());
        firebaseDatabase = FirebaseDatabase.getInstance();
        if (!calledAlready) {
            firebaseDatabase.setPersistenceEnabled(true);
            calledAlready = true;
        }
        databaseReference = firebaseDatabase.getReference();
    }

    private void verificarUser() {
        if (firebaseUser == null){
            throw new RuntimeException(getContext().toString()
                    + " must implement OnFragmentInteractionListener");
        }else{
            //
        }
    }

    public TarefaGrupalFragment() {
        // Required empty public constructor
    }

    public static TarefaGrupalFragment newInstance(String param1, String param2) {
        TarefaGrupalFragment fragment = new TarefaGrupalFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        firebaseUser = Conexao.getFirebaseUser();
        verificarUser();
        initFirebase();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.notifications, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ordenarAZ:
                // Not implemented here
                eventoDatabaseCard(1);
                return true;
            case R.id.ordenarData:
                // Do Fragment menu item stuff here
                /*alert("DATA");*/
                eventoDatabaseCard(2);
                return true;
            case R.id.ordenarPrioridade:
                /*alert("PRIORIDADE");*/
                eventoDatabaseCard(3);
                return true;
            case R.id.ordenarFinalizadas:
                eventoDatabaseCard(4);
                return true;
            case  android.R.id.home:
                getActivity().finish();
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Bundle data = getArguments();
        grupo = (Grupo) data.getSerializable("grupoOBJ");
        View rootView = inflater.inflate(R.layout.fragment_tarefa_grupal, container,false);
        inicializarComponentes(rootView);
        onClickEvent();
        recyclerViewEvent(rootView);
        return rootView;
    }

    private void recyclerViewEvent(View rootView) {
        recyclerView = rootView.findViewById(R.id.recycler_viewGrupal);
        coordinatorLayout = rootView.findViewById(R.id.coordinator_layout);
        cartList = new ArrayList<>();
        mAdapter = new CartListGrupalAdapter(getActivity(), cartList) {
            @Override
            public void onBindViewHolder(MyViewHolder holder, final int position) {
                super.onBindViewHolder(holder, position);
                if (holder.prazo.getText().toString().equals("1")){
                    holder.dias.setText("Dia restante");
                }else if (holder.prazo.getText().toString().equals("0")){
                    holder.dias.setText("Termina hoje");
                }else if (holder.prazo.getText().toString().equals("-")){
                    holder.dias.setText("Expirada");
                }else{
                    holder.dias.setText("Dias restantes");
                }
                holder.viewForeground.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LayoutInflater inflateDialog = getLayoutInflater();
                        View alertLayout = inflateDialog.inflate(R.layout.cadastrar_tarefa_layout, null);
                        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                        //alert.setTitle("Visualizar/alterar tarefa");
                        alert.setCustomTitle(customTitle(inflateDialog,mAdapter.getItem(position).getTar_nome()));
                        alert.setView(alertLayout);
                        dialog = alert.create();
                        dialog.show();
                        visualizarTarefa(alertLayout,mAdapter.getItem(position));
                        //alterarComponentesTarefa(alertLayout, mAdapter.getItem(position));

                    }});
                if (grupo.getGrp_lider().equals(firebaseUser.getUid())){
                    holder.menu.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            list_opcoes(mAdapter.getItem(position), position);
                        }});
                }else{
                    holder.menu.setVisibility(View.INVISIBLE);
                }

            }};
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLongClickable(true);
        eventoDatabaseCard(2);
    }

    private void visualizarTarefa(View alertLayout, final TarefaGrupal tarefa){
        final EditText editNome = alertLayout.findViewById(R.id.editTextTarNome);
        final EditText editDescricao = alertLayout.findViewById(R.id.editTextTarDesc);
        final RadioGroup group = (RadioGroup) alertLayout.findViewById(R.id.radioGroup);
        final EditText editPrazo = (EditText) alertLayout.findViewById(R.id.editTextTarPrazo);
        final CheckBox checkBoxNotificacao = (CheckBox) alertLayout.findViewById(R.id.checkBoxNotificacao);
        final TextView textView = alertLayout.findViewById(R.id.prioridadeText);
        final TextView inicio = alertLayout.findViewById(R.id.inicio);
        final TextView fim = alertLayout.findViewById(R.id.fim);
        final EditText editInicio = alertLayout.findViewById(R.id.editTextTarDataIni);
        final EditText editFim = alertLayout.findViewById(R.id.editTextTarDataFin);
        final Button btnAlterar = (Button) alertLayout.findViewById(R.id.btnCadastrarTar);
        RadioButton buttonAlta = (RadioButton) group.findViewById(R.id.radioAlta);
        RadioButton buttonMedia = (RadioButton) group.findViewById(R.id.radioMedia);
        RadioButton buttonBaixa = (RadioButton) group.findViewById(R.id.radioBaixa);
        editDescricao.setBackgroundColor(Color.TRANSPARENT);
        textView.setVisibility(View.GONE);
        editNome.setVisibility(View.GONE);
        editDescricao.setKeyListener(null);
        group.setVisibility(View.GONE);
        editPrazo.setVisibility(View.GONE);
        editPrazo.setVisibility(View.GONE);
        editInicio.setVisibility(View.GONE);
        editFim.setVisibility(View.GONE);
        inicio.setVisibility(View.GONE);
        fim.setVisibility(View.GONE);
        checkBoxNotificacao.setVisibility(View.GONE);
        btnAlterar.setVisibility(View.GONE);
        buttonAlta.setVisibility(View.GONE);
        buttonMedia.setVisibility(View.GONE);
        buttonBaixa.setVisibility(View.GONE);
        btnAlterar.setVisibility(View.GONE);
        editDescricao.setText(tarefa.getTar_descricao());
    }

    private void alterarComponentesTarefa(View alertLayout,final TarefaGrupal tarefa) throws ParseException {
        final EditText editNome = alertLayout.findViewById(R.id.editTextTarNome);
        final EditText editDescricao = alertLayout.findViewById(R.id.editTextTarDesc);
        final RadioGroup group = (RadioGroup) alertLayout.findViewById(R.id.radioGroup);
        final EditText editPrazo = (EditText) alertLayout.findViewById(R.id.editTextTarPrazo);
        final CheckBox checkBoxNotificacao = (CheckBox) alertLayout.findViewById(R.id.checkBoxNotificacao);
        final Button btnAlterar = (Button) alertLayout.findViewById(R.id.btnCadastrarTar);
        final EditText editDataIni = alertLayout.findViewById(R.id.editTextTarDataIni);
        final EditText editDataFin = alertLayout.findViewById(R.id.editTextTarDataFin);
        final SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat format2 = new SimpleDateFormat("dd/MM/yyyy");
        editPrazo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals("")) {
                    int dias = Integer.parseInt(s.toString());
                    Calendar a = Calendar.getInstance();
                    try {
                        a.setTime(format.parse(tarefa.getTar_dataInicial()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    a.add(Calendar.DAY_OF_YEAR,dias);
                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                    editDataFin.setText(format.format(a.getTime()));
                }else{
                    editDataFin.setText("dd/MM/aaaa");
                }
            }
        });
        editDataIni.setText(format2.format(format.parse(tarefa.getTar_dataInicial())));
        editDataFin.setText(format2.format(format.parse(tarefa.getTar_dataFinal())));
        RadioButton buttonAlta = (RadioButton) group.findViewById(R.id.radioAlta);
        RadioButton buttonMedia = (RadioButton) group.findViewById(R.id.radioMedia);
        RadioButton buttonBaixa = (RadioButton) group.findViewById(R.id.radioBaixa);
        if (!firebaseUser.getUid().equals(grupo.getGrp_lider())){
            editNome.setKeyListener(null);
            editDescricao.setKeyListener(null);
            group.setEnabled(false);
            editPrazo.setKeyListener(null);
            checkBoxNotificacao.setKeyListener(null);
            btnAlterar.setKeyListener(null);
            buttonAlta.setKeyListener(null);
            buttonMedia.setKeyListener(null);
            buttonBaixa.setKeyListener(null);
            btnAlterar.setVisibility(View.GONE);
        }else {
            btnAlterar.setText("Alterar");
            btnAlterar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new TarefaGrupal().alterar(databaseReference,tarefa,
                            editNome.getText().toString(), editDescricao.getText().toString(),
                            checkValue, Integer.parseInt(editPrazo.getText().toString()),
                            checkBoxNotificacao.isChecked()?1:0, grupo);
                    dialog.dismiss();
                }
            });
        }
        editNome.setText(tarefa.getTar_nome());
        editDescricao.setText(tarefa.getTar_descricao());
        if (tarefa.getTar_prioridade().equals("Alta")){
            buttonAlta.setChecked(true);
            checkValue = buttonAlta.getText().toString();
        }else if (tarefa.getTar_prioridade().equals("Média")){
            buttonMedia.setChecked(true);
            checkValue = buttonMedia.getText().toString();
        }else if (tarefa.getTar_prioridade().equals("Baixa")){
            buttonBaixa.setChecked(true);
            checkValue = buttonBaixa.getText().toString();
        }
        editPrazo.setText(String.valueOf(tarefa.getTar_prazo()));
        if (tarefa.getTar_notificacao() == 1) {
            checkBoxNotificacao.setChecked(true);
        }
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton button = (RadioButton) group.findViewById(i);
                checkValue = button.getText().toString();
            }
        });
    }

    private void eventoDatabaseCard(final int menu) {
        databaseReference.child("grupo").child(grupo.getGrp_id()).
                child("tarefa_grupal").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        cartList.clear();
                        for (DataSnapshot obj: dataSnapshot.getChildren()){
                            TarefaGrupal p = obj.getValue(TarefaGrupal.class);
                            if (menu == 4){
                                if (p.getTar_status()==1){
                                    cartList.add(p);
                                }
                            }else{
                                if (p.getTar_status()==0) {
                                    cartList.add(p);
                                }
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                        if (cartList.isEmpty()){
                            textView.setVisibility(View.VISIBLE);
                        }else if (menu == 2){
                            textView.setVisibility(View.INVISIBLE);
                            Collections.sort(cartList, new Comparator<TarefaGrupal> () {
                                @Override
                                public int compare(TarefaGrupal o1, TarefaGrupal o2) {
                                    return converterData(o1.getTar_dataFinal()).
                                            compareTo(converterData(o2.getTar_dataFinal()));
                                }
                            });
                        }else if (menu == 1){
                            textView.setVisibility(View.INVISIBLE);
                            Collections.sort(cartList, new Comparator<TarefaGrupal> () {
                                @Override
                                public int compare(TarefaGrupal o1, TarefaGrupal o2) {
                                    return o1.getTar_nome().compareTo(o2.getTar_nome());
                                }
                            });
                        }else if (menu ==3){
                            textView.setVisibility(View.INVISIBLE);
                            Collections.sort(cartList, new Comparator<TarefaGrupal> () {
                                @Override
                                public int compare(TarefaGrupal o1, TarefaGrupal o2) {
                                    if (o1.getTar_prioridade().equals("Alta") &&
                                            o2.getTar_prioridade().equals("Média")){
                                        return -1;
                                    }else if (o1.getTar_prioridade().equals("Média") &&
                                            o2.getTar_prioridade().equals("Baixa")){
                                        return -1;
                                    }
                                    return 0;
                                }
                            });
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private Date converterData(String data){
        SimpleDateFormat formato = new SimpleDateFormat("dd-MM-yyyy HH");
        Date date = null;
        try {
            date = formato.parse(data);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    private void onClickEvent() {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflateDialog = getLayoutInflater();
                View alertLayout = inflateDialog.inflate(R.layout.cadastrar_tarefa_layout, null);
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                //alert.setTitle("Cadastrar tarefa");
                alert.setCustomTitle(customTitle(inflateDialog,"Cadastrar tarefa grupal"));
                alert.setView(alertLayout);
                dialog = alert.create();
                dialog.show();
                inicializarComponentesTarefa(alertLayout);
            }
        });
    }

    private void inicializarComponentesTarefa(View alertLayout) {
        final EditText editNome = alertLayout.findViewById(R.id.editTextTarNome);
        final EditText editDescricao = alertLayout.findViewById(R.id.editTextTarDesc);
        final RadioGroup group = (RadioGroup) alertLayout.findViewById(R.id.radioGroup);
        final EditText editPrazo = (EditText) alertLayout.findViewById(R.id.editTextTarPrazo);
        final CheckBox checkBoxNotificacao = (CheckBox) alertLayout.findViewById(R.id.checkBoxNotificacao);
        final Button btnCadastrar = (Button) alertLayout.findViewById(R.id.btnCadastrarTar);
        final EditText editDataIni = alertLayout.findViewById(R.id.editTextTarDataIni);
        final EditText editDataFin = alertLayout.findViewById(R.id.editTextTarDataFin);
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        editDataIni.setText(format.format(new Date()));
        checkValue = "";
        editPrazo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals("")) {
                    int dias = Integer.parseInt(s.toString());
                    Calendar a = Calendar.getInstance();
                    a.setTime(new Date());
                    a.add(Calendar.DAY_OF_YEAR,dias);
                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                    editDataFin.setText(format.format(a.getTime()));
                }else{
                    editDataFin.setText("dd/MM/aaaa");
                }
            }
        });
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton button = (RadioButton) group.findViewById(i);
                checkValue = button.getText().toString();
            }
        });
        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editNome.getText().equals("") || editDescricao.getText().equals("") ||
                        checkValue.equals("") || editPrazo.getText().toString().equals("")){
                    alert("Preencha o(s) campo(s) vazio(s)");
                } else if (Integer.parseInt(editPrazo.getText().toString()) < 0 ||
                        Integer.parseInt(editPrazo.getText().toString()) > 365) {
                    alert("O prazo deve estar entre 0 e 365 dias");
                } else{
                    new TarefaGrupal().cadastrar(databaseReference,editNome.getText().toString(),
                            editDescricao.getText().toString(),checkValue,
                            Integer.parseInt(editPrazo.getText().toString()),grupo.getGrp_id(),
                            checkBoxNotificacao.isChecked()?1:0);
                    dialog.dismiss();
                    textView.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void inicializarComponentes(View rootView) {
        floatingActionButton = rootView.findViewById(R.id.floatingActionButtonLider);
        textView = rootView.findViewById(R.id.textViewNothing);
        if (grupo.getGrp_lider().equals(firebaseUser.getUid())){
            floatingActionButton.setVisibility(View.VISIBLE);
        }
    }

    private View customTitle(LayoutInflater inflateDialog, String title){
        View customTitle = inflateDialog.inflate(R.layout.title_bar, null);
        ImageView imageView = customTitle.findViewById(R.id.btnVoltar);
        TextView textView = customTitle.findViewById(R.id.txtTitle);
        imageView.setImageResource(R.drawable.ic_arrow_back_black_24dp);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        textView.setText(title);
        return customTitle;
    }

    public void list_opcoes(final TarefaGrupal tarefa, final int position){
        ArrayList<String> itens = new ArrayList<String>();
        itens.add("Alterar tarefa");
        itens.add("Gerenciar realizadores");
        itens.add("Excluir");
        //adapter utilizando um layout customizado (TextView)
        ArrayAdapter adapter = new ArrayAdapter(getActivity(), R.layout.item_alerta, itens);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setSingleChoiceItems(adapter, 0, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                if (arg1 == 0){
                    LayoutInflater inflateDialog = getLayoutInflater();
                    View alertLayout = inflateDialog.inflate(R.layout.cadastrar_tarefa_layout, null);
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    //alert.setTitle("Visualizar/alterar tarefa");
                    alert.setCustomTitle(customTitle(inflateDialog,"Visualizar/alterar tarefa"));
                    alert.setView(alertLayout);
                    dialog = alert.create();
                    dialog.show();
                    try {
                        alterarComponentesTarefa(alertLayout, mAdapter.getItem(position));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else if (arg1 == 1){
                    LayoutInflater inflateDialog = getLayoutInflater();
                    View alertLayout = inflateDialog.inflate(R.layout.gerenciar_realizadores, null);
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    //alert.setTitle("Gerenciar realizadores");
                    alert.setCustomTitle(customTitle(inflateDialog,"Gerenciar realizadores"));
                    alert.setView(alertLayout);
                    dialog = alert.create();
                    dialog.show();
                    gerenciarRealizadoresGrupo(alertLayout,grupo,tarefa);
                } else if (arg1 == 2){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Deseja excluir a tarefa?")
                            .setMessage("Nome: " + tarefa.getTar_nome())
                            .setCancelable(false)
                            .setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    new TarefaGrupal().excluir(databaseReference,tarefa,
                                            grupo);
                                    mAdapter.removeItem(position);
                                }
                            }).setNegativeButton("NÃO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                /*if ((arg1 == 1)&&tarefa.getTar_status()==0&&!mAdapter.subtrairDatas(tarefa).equals("0")){
                    new TarefaGrupal().excluir(databaseReference,tarefa,
                            grupo);
                    mAdapter.removeItem(position);
                }else if ((arg1 == 1)&&tarefa.getTar_status()==1){
                    alert("Tarefa já finalizada");
                }else if ((arg1 == 1)&&mAdapter.subtrairDatas(tarefa).equals("0")){
                    alert("Tarefa expirada");
                }*/
                alerta.dismiss();
            }
        });
        alerta = builder.create();
        alerta.show();
    }

    private void gerenciarRealizadoresGrupo(View alertLayout,final Grupo grupo,final TarefaGrupal tarefaGrupal) {
        recyclerViewRealizadores = alertLayout.findViewById(R.id.scrollRealizadores);
        cartListRealizadores = new ArrayList<>();
        mAdapterRealizadores = new CartUserAdapter(getActivity(), cartListRealizadores){
            @Override
            public void onBindViewHolder(final MyViewHolder holder, final int position) {
                super.onBindViewHolder(holder, position);
                final Usuario item = cartListRealizadores.get(position);
                holder.img_move.setImageResource(R.drawable.ic_arrow_upward_black_24dp);
                holder.img_move.setVisibility(View.INVISIBLE);
                holder.checkBox.setVisibility(View.VISIBLE);
                if (tarefaGrupal.getRealiza()!=null){
                    for (Realiza realiza : tarefaGrupal.getRealiza().values()) {
                        if (realiza.getRea_user_id().equals(item.getUser_id())){
                            holder.checkBox.setChecked(true);
                            holder.textView.setVisibility(View.VISIBLE);
                            if (realiza.getRea_status() == 1){
                                /*holder.name.setText(item.getUser_nome()+": Completo");*/
                                holder.textView.setText("Completo");
                                holder.textView.setBackgroundColor(Color.rgb(0,230,118));
                            }else{
                                /*holder.name.setText(item.getUser_nome()+": Ainda não fez");*/
                                holder.textView.setText("Pendente");
                                holder.textView.setBackgroundColor(Color.rgb(176,190,197));
                            }
                        }
                    }
                }
                holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked){
                            new Realiza().cadastrar(databaseReference,grupo,tarefaGrupal,item);
                            holder.textView.setVisibility(View.VISIBLE);
                            holder.textView.setText("Pendente");
                            holder.textView.setBackgroundColor(Color.rgb(176,190,197));
                        }else{
                            new Realiza().remover(databaseReference,grupo,tarefaGrupal,item);
                            holder.textView.setVisibility(View.GONE);
                        }
                    }
                });
                mostrarIconeUsuarios(holder,item);
            }
        };
        RecyclerView.LayoutManager mLayoutManagerI = new LinearLayoutManager(context.getApplicationContext());
        recyclerViewRealizadores.setLayoutManager(mLayoutManagerI);
        recyclerViewRealizadores.setItemAnimator(new DefaultItemAnimator());
        recyclerViewRealizadores.setAdapter(mAdapterRealizadores);
        /*mAdapterRealizadores.setClickListener(new CustomItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                final Usuario item = cartListRealizadores.get(position);
                Usuario userSelect = new Usuario();
                userSelect.setUser_id(item.getUser_id());
                userSelect.setUser_nome(item.getUser_nome());
                userSelect.setUser_email(item.getUser_email());
            }
        });*/
        eventoDatabaseCardRealizadores(grupo);
    }

    private void eventoDatabaseCardRealizadores(Grupo grupo) {
        databaseReference.child("grupo").child(grupo.getGrp_id()).child("integrantes").
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        cartListRealizadores.clear();
                        for (DataSnapshot obj: dataSnapshot.getChildren()){
                            Usuario u = obj.getValue(Usuario.class);
                            cartListRealizadores.add(u);
                            mAdapterRealizadores.notifyDataSetChanged();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void mostrarIconeUsuarios(final CartUserAdapter.MyViewHolder holder,final Usuario item){
        databaseReference.child("usuario").child(item.getUser_id()).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final Usuario user = dataSnapshot.getValue(Usuario.class);
                        holder.progressBar.setVisibility(View.VISIBLE);
                        storageReference = FirebaseStorage.getInstance().getReference();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                StorageReference filePath = storageReference.child("img_profiles").
                                        child(user.getUser_icon());
                                filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(final Uri uri) {
                                        Activity activity = (Activity) context;
                                        activity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Glide.with(context.getApplicationContext())
                                                        .load(uri)
                                                        .transition(DrawableTransitionOptions.withCrossFade())
                                                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.RESOURCE))
                                                        .apply(RequestOptions.centerCropTransform())
                                                        .apply(RequestOptions.fitCenterTransform())
                                                        .apply(RequestOptions.bitmapTransform(new CircleTransform()))
                                                        .thumbnail(0.5f)
                                                        .into(holder.img_integrante);
                                                holder.progressBar.setVisibility(ProgressBar.INVISIBLE);
                                            }
                                        });
                                    }
                                });
                            }
                        }).start();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    private void alert(String msg) {
        Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
