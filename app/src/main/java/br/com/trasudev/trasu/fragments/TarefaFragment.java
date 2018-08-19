package br.com.trasudev.trasu.fragments;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.InputFilter;
import android.text.LoginFilter;
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
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Random;

import br.com.trasudev.trasu.R;
import br.com.trasudev.trasu.activitys.MainActivity;
import br.com.trasudev.trasu.classes.AlarmNotificationReceiver;
import br.com.trasudev.trasu.classes.CartListAdapter;
import br.com.trasudev.trasu.classes.Conexao;
import br.com.trasudev.trasu.classes.RecyclerItemClickListener;
import br.com.trasudev.trasu.classes.RecyclerItemTouchHelper;
import br.com.trasudev.trasu.classes.TarefaAdapter;
import br.com.trasudev.trasu.entidades.TarefaIndividual;

import static br.com.trasudev.trasu.activitys.LoginActivity.calledAlready;
import static br.com.trasudev.trasu.activitys.MainActivity.navItemIndex;


public class TarefaFragment extends Fragment implements
        RecyclerItemTouchHelper.RecyclerItemTouchHelperListener{
    private FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    public static DatabaseReference databaseReference;
    private String checkValue;
    private AlertDialog dialog;
    private AlertDialog alerta;
    private FloatingActionButton floatingActionButton;
    private RecyclerView recyclerView;
    private List<TarefaIndividual> cartList;
    private CartListAdapter mAdapter;
    private CoordinatorLayout coordinatorLayout;
    private TextView textView;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static String titulo;
    public static String contexto;
    public static String prioridade;
    public static int id_notify;
    public static String id_user;

    private String mParam1;
    private String mParam2;

    private Context context;

    @Override
    public void onStart() {
        super.onStart();
    }

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

    private OnFragmentInteractionListener mListener;

    public TarefaFragment() {
        // Required empty public constructor
    }

    public static TarefaFragment newInstance(String param1, String param2) {
        TarefaFragment fragment = new TarefaFragment();
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
                /*alert("AZ");*/
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
            default:
                break;
        }
        return false;
    }

    private void inicializarComponentes(View rootView){
        floatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.floatingActionButton);
        textView = rootView.findViewById(R.id.textViewNothing);
    }

    private void conditionNotification (final CheckBox checkBox, final TextView textView, final EditText editPrazo){
        Calendar mcurrentTime = Calendar.getInstance();
        final int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        final int minuto = mcurrentTime.get(Calendar.MINUTE);
        if (checkBox.isChecked()){
            final TimePickerDialog timePickerDialog;
            timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    if (Integer.parseInt(editPrazo.getText().toString()) == 0){
                        if (hourOfDay*60 + minute < hour * 60 + minuto){
                            alert("Não pode notificar no passado!");
                            checkBox.setChecked(false);
                        }else{
                            textView.setText("Horário: " +(hourOfDay < 10 ? "0" : "")+hourOfDay+
                                    ":"+(minute < 10 ? "0" : "")+minute);
                        }
                    }else{
                        textView.setText("Horário: " +(hourOfDay < 10 ? "0" : "")+hourOfDay+
                                ":"+(minute < 10 ? "0" : "")+minute);
                    }
                }
            },hour,minuto,true);
            timePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    checkBox.setChecked(false);
                }
            });
            timePickerDialog.setTitle("Escolha o horário");
            timePickerDialog.show();
        }
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
        final TextView textView = alertLayout.findViewById(R.id.textViewAjuda);
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        editDataIni.setText(format.format(new Date()));
        checkValue = "";
        checkBoxNotificacao.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (editPrazo.getText().toString().isEmpty()){
                    checkBoxNotificacao.setChecked(false);
                    alert("Coloque um prazo antes de notificar!");
                }else{
                    conditionNotification(checkBoxNotificacao,textView,editPrazo);
                }
                if (!isChecked){
                    textView.setText("");
                }
            }
        });
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
                        Integer.parseInt(editPrazo.getText().toString()) > 365){
                    alert("O prazo deve estar entre 0 e 365 dias");
                } else{
                    id_notify = new Random().nextInt();
                    if (checkBoxNotificacao.isChecked()){
                        notificar(Integer.parseInt(editPrazo.getText().toString()), "Nome: "+editNome.getText().toString(),
                                editDescricao.getText().toString(),checkValue, textView.getText().toString(), id_notify);
                    }
                    new TarefaIndividual().cadastrar(databaseReference,editNome.getText().toString(),
                            editDescricao.getText().toString(),checkValue,
                            Integer.parseInt(editPrazo.getText().toString()),firebaseUser.getUid(),
                            checkBoxNotificacao.isChecked()?1:0);
                    dialog.dismiss();
                    textView.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void notificar(int prazo, String tit, String desc, String prio, String horario, int id) {
        titulo = tit;
        contexto = desc;
        prioridade = prio;
        id_user = firebaseUser.getUid();
        String hora = horario.substring(9,11);
        String minuto = horario.substring(12,14);
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DAY_OF_YEAR,prazo);
        c.set(Calendar.HOUR_OF_DAY,Integer.parseInt(hora));
        c.set(Calendar.MINUTE,Integer.parseInt(minuto));
        c.set(Calendar.SECOND,0);
        AlarmManager manager = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent myIntent = new Intent(getActivity(),AlarmNotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(),id,myIntent,0);
        manager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(),pendingIntent);
    }

    private void visualizarTarefa(View alertLayout, final TarefaIndividual tarefa){
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

    private void alterarComponentesTarefa(View alertLayout, final TarefaIndividual tarefa) throws ParseException {
        final EditText editNome = alertLayout.findViewById(R.id.editTextTarNome);
        final EditText editDescricao = alertLayout.findViewById(R.id.editTextTarDesc);
        final RadioGroup group = (RadioGroup) alertLayout.findViewById(R.id.radioGroup);
        final EditText editPrazo = (EditText) alertLayout.findViewById(R.id.editTextTarPrazo);
        final CheckBox checkBoxNotificacao = (CheckBox) alertLayout.findViewById(R.id.checkBoxNotificacao);
        final Button btnCadastrar = (Button) alertLayout.findViewById(R.id.btnCadastrarTar);
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
        editNome.setText(tarefa.getTar_nome());
        editDescricao.setText(tarefa.getTar_descricao());
        RadioButton buttonAlta = (RadioButton) group.findViewById(R.id.radioAlta);
        RadioButton buttonMedia = (RadioButton) group.findViewById(R.id.radioMedia);
        RadioButton buttonBaixa = (RadioButton) group.findViewById(R.id.radioBaixa);
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
        btnCadastrar.setText("Alterar");
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
                new TarefaIndividual().alterar(databaseReference,tarefa,
                        editNome.getText().toString(), editDescricao.getText().toString(),
                        checkValue, Integer.parseInt(editPrazo.getText().toString()),
                                checkBoxNotificacao.isChecked()?1:0, firebaseUser);
                dialog.dismiss();
            }
        });
    }

    private void onClickEvent() {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflateDialog = getLayoutInflater();
                View alertLayout = inflateDialog.inflate(R.layout.cadastrar_tarefa_layout, null);
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setCustomTitle(customTitle(inflateDialog,"Cadastrar tarefa"));
                alert.setView(alertLayout);
                dialog = alert.create();
                dialog.show();
                inicializarComponentesTarefa(alertLayout);
            }
        });
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container,false);
        inicializarComponentes(rootView);
        onClickEvent();
        recyclerViewEvent(rootView);
        return rootView;
    }

    private void recyclerViewEvent(View rootView) {
        recyclerView = rootView.findViewById(R.id.recycler_view);
        coordinatorLayout = rootView.findViewById(R.id.coordinator_layout);
        cartList = new ArrayList<>();
        mAdapter = new CartListAdapter(getActivity(), cartList) {
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
                        alert.setCustomTitle(customTitle(inflateDialog,mAdapter.getItem(position).getTar_nome()));
                        alert.setView(alertLayout);
                        dialog = alert.create();
                        dialog.show();
                        //alterarComponentesTarefa(alertLayout, mAdapter.getItem(position));
                        visualizarTarefa(alertLayout,mAdapter.getItem(position));
                        }});
                holder.menu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        list_opcoes(mAdapter.getItem(position), position);
                        }});
                }};
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLongClickable(true);
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT,
                this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
        eventoDatabaseCard(2);
    }

    private void eventoDatabaseCard(final int menu) {
        databaseReference.child("usuario").child(firebaseUser.getUid()).
                child("tarefa_individual").
                addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                cartList.clear();
                for (DataSnapshot obj: dataSnapshot.getChildren()){
                    TarefaIndividual p = obj.getValue(TarefaIndividual.class);
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
                    Collections.sort(cartList, new Comparator<TarefaIndividual>() {
                        @Override
                        public int compare(TarefaIndividual o1, TarefaIndividual o2) {
                            return converterData(o1.getTar_dataFinal()).
                                    compareTo(converterData(o2.getTar_dataFinal()));
                        }
                    });
                }else if (menu == 1){
                    textView.setVisibility(View.INVISIBLE);
                    Collections.sort(cartList, new Comparator<TarefaIndividual>() {
                        @Override
                        public int compare(TarefaIndividual o1, TarefaIndividual o2) {
                            return o1.getTar_nome().compareTo(o2.getTar_nome());
                        }
                    });
                }else if (menu ==3){
                    textView.setVisibility(View.INVISIBLE);
                    Collections.sort(cartList, new Comparator<TarefaIndividual>() {
                        @Override
                        public int compare(TarefaIndividual o1, TarefaIndividual o2) {
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
        SimpleDateFormat formato = new SimpleDateFormat("dd-MM-yyyy");
        Date date = null;
        try {
            date = formato.parse(data);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    @Override
    public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction, final int position) {
        if (viewHolder instanceof CartListAdapter.MyViewHolder) {
            // get the removed item name to display it in snack bar
            String name = cartList.get(viewHolder.getAdapterPosition()).getTar_nome();

            // backup of removed item for undo purpose
            final TarefaIndividual deletedItem = cartList.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            if (deletedItem.getTar_status()==0&&!mAdapter.subtrairDatas(deletedItem).equals("0")){
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Nome: " + deletedItem.getTar_nome())
                        .setMessage("Você ganhou "+new TarefaIndividual().equacaoPontos(
                        mAdapter.subtrairDatas(cartList.get(viewHolder.getAdapterPosition())),
                        cartList.get(viewHolder.getAdapterPosition()), false)+" pontos!")
                        .setCancelable(false)
                        .setPositiveButton("Receber", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                new TarefaIndividual().finalizar(databaseReference, deletedItem,
                                        firebaseUser, mAdapter.subtrairDatas(deletedItem), getActivity());
                                mAdapter.removeItem(position);
                            }
                        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //mAdapter.notifyItemRemoved(position +1);
                        mAdapter.notifyItemRangeChanged(position, mAdapter.getItemCount());
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }else if (deletedItem.getTar_status()==1){
                alert("Tarefa já finalizada");
                mAdapter.notifyItemRangeChanged(position, mAdapter.getItemCount());
            }else if (mAdapter.subtrairDatas(deletedItem).equals("0")){
                alert("Tarefa expirada");
                mAdapter.notifyItemRangeChanged(position, mAdapter.getItemCount());
            }
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        /*if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }*/
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }



    private void alert(String msg) {
        Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
    }

    public void list_opcoes(final TarefaIndividual tarefa, final int position){
        ArrayList<String> itens = new ArrayList<String>();
        itens.add("Alterar tarefa");
        itens.add("Excluir tarefa");
        //adapter utilizando um layout customizado (TextView)
        ArrayAdapter adapter = new ArrayAdapter(getActivity(), R.layout.item_alerta, itens);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setSingleChoiceItems(adapter, 0, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                if (arg1 == 1){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Nome: " + tarefa.getTar_nome())
                            .setTitle("Deseja excluir a tarefa?")
                            .setCancelable(false)
                            .setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    new TarefaIndividual().excluir(databaseReference,tarefa,firebaseUser, getActivity());
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
                }else if (arg1 == 0){
                    LayoutInflater inflateDialog = getLayoutInflater();
                    View alertLayout = inflateDialog.inflate(R.layout.cadastrar_tarefa_layout, null);
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setCustomTitle(customTitle(inflateDialog,"Visualizar/alterar tarefa"));
                    alert.setView(alertLayout);
                    dialog = alert.create();
                    dialog.show();
                    try {
                        alterarComponentesTarefa(alertLayout, mAdapter.getItem(position));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                alerta.dismiss();
            }
        });
        alerta = builder.create();
        alerta.show();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
