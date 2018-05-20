package br.com.trasudev.trasu.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.com.trasudev.trasu.R;
import br.com.trasudev.trasu.classes.CartListAdapter;
import br.com.trasudev.trasu.classes.CartListGrupalAdapter;
import br.com.trasudev.trasu.classes.Conexao;
import br.com.trasudev.trasu.classes.RecyclerItemTouchHelper;
import br.com.trasudev.trasu.entidades.Grupo;
import br.com.trasudev.trasu.entidades.TarefaGrupal;
import br.com.trasudev.trasu.entidades.TarefaIndividual;

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

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private RecyclerView recyclerView;
    private List<TarefaGrupal> cartList;
    private CartListGrupalAdapter mAdapter;
    private CoordinatorLayout coordinatorLayout;


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
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        firebaseUser = Conexao.getFirebaseUser();
        verificarUser();
        initFirebase();
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
                holder.viewForeground.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LayoutInflater inflateDialog = getLayoutInflater();
                        View alertLayout = inflateDialog.inflate(R.layout.cadastrar_tarefa_layout, null);
                        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                        alert.setTitle("Visualizar/alterar tarefa");
                        alert.setView(alertLayout);
                        dialog = alert.create();
                        dialog.show();
                        alterarComponentesTarefa(alertLayout, mAdapter.getItem(position));
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
        eventoDatabaseCard();
    }

    private void alterarComponentesTarefa(View alertLayout,final TarefaGrupal tarefa) {
        final EditText editNome = alertLayout.findViewById(R.id.editTextTarNome);
        final EditText editDescricao = alertLayout.findViewById(R.id.editTextTarDesc);
        final RadioGroup group = (RadioGroup) alertLayout.findViewById(R.id.radioGroup);
        final EditText editPrazo = (EditText) alertLayout.findViewById(R.id.editTextTarPrazo);
        final CheckBox checkBoxNotificacao = (CheckBox) alertLayout.findViewById(R.id.checkBoxNotificacao);
        final Button btnAlterar = (Button) alertLayout.findViewById(R.id.btnCadastrarTar);
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
            btnAlterar.setText("Voltar");
            btnAlterar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
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

    private void eventoDatabaseCard() {
        databaseReference.child("grupo").child(grupo.getGrp_id()).
                child("tarefa_grupal").orderByChild("tar_dataFinal").
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        cartList.clear();
                        for (DataSnapshot obj: dataSnapshot.getChildren()){
                            TarefaGrupal p = obj.getValue(TarefaGrupal.class);
                            if (p.getTar_status()==0) {
                                cartList.add(p);
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

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
                alert.setTitle("Cadastrar tarefa");
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
                new TarefaGrupal().cadastrar(databaseReference,editNome.getText().toString(),
                        editDescricao.getText().toString(),checkValue,
                        Integer.parseInt(editPrazo.getText().toString()),grupo.getGrp_id(),
                        checkBoxNotificacao.isChecked()?1:0);
                dialog.dismiss();

            }
        });
    }

    private void inicializarComponentes(View rootView) {
        floatingActionButton = rootView.findViewById(R.id.floatingActionButtonLider);
        if (grupo.getGrp_lider().equals(firebaseUser.getUid())){
            floatingActionButton.setVisibility(View.VISIBLE);
        }
    }

    public void list_opcoes(final TarefaGrupal tarefa, final int position){
        ArrayList<String> itens = new ArrayList<String>();
        itens.add("Gerenciar realizadores");
        itens.add("Excluir");
        //adapter utilizando um layout customizado (TextView)
        ArrayAdapter adapter = new ArrayAdapter(getActivity(), R.layout.item_alerta, itens);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setSingleChoiceItems(adapter, 0, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                if (arg1 == 0){
                    alert("CLICOU EM GERENCIAR");
                }
                if ((arg1 == 1)&&tarefa.getTar_status()==0&&!mAdapter.subtrairDatas(tarefa).equals("0")){
                    new TarefaGrupal().excluir(databaseReference,tarefa,
                            grupo);
                    mAdapter.removeItem(position);
                }else if ((arg1 == 1)&&tarefa.getTar_status()==1){
                    alert("Tarefa já finalizada");
                }else if ((arg1 == 1)&&mAdapter.subtrairDatas(tarefa).equals("0")){
                    alert("Tarefa expirada");
                }
                alerta.dismiss();
            }
        });
        alerta = builder.create();
        alerta.show();
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
