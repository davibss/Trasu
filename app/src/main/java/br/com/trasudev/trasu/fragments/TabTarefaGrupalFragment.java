package br.com.trasudev.trasu.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
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
import br.com.trasudev.trasu.classes.RecyclerItemGrupalClickListener;
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
 * {@link TabTarefaGrupalFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TabTarefaGrupalFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TabTarefaGrupalFragment extends Fragment implements
        RecyclerItemGrupalClickListener.RecyclerItemTouchHelperListener{

    private FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    private String checkValue;
    private AlertDialog dialog;
    private AlertDialog alerta;

    private RecyclerView recyclerView;
    private List<TarefaGrupal> cartList;
    private CartListGrupalAdapter mAdapter;
    private CoordinatorLayout coordinatorLayout;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

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

    public TabTarefaGrupalFragment() {
        // Required empty public constructor
    }

    public static TabTarefaGrupalFragment newInstance(String param1, String param2) {
        TabTarefaGrupalFragment fragment = new TabTarefaGrupalFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_tab_tarefa_grupal, container,false);
        inicializarComponentes(rootView);
        recyclerViewEvent(rootView);
        return rootView;
    }

    private void recyclerViewEvent(View rootView) {
        recyclerView = rootView.findViewById(R.id.recycler_view);
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
                holder.menu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        list_opcoes(mAdapter.getItem(position), position);
                    }
                });
            }};
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLongClickable(true);
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemGrupalClickListener(0, ItemTouchHelper.LEFT,
                this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
        eventoDatabaseCard();
        eventoDatabaseCard();
    }

    private void eventoDatabaseCard() {
        databaseReference.child("grupo").
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        cartList.clear();
                        for (DataSnapshot obj: dataSnapshot.getChildren()){
                            Grupo g = obj.getValue(Grupo.class);
                            if (g.getTarefa_grupal()!= null) {
                                for (TarefaGrupal tg : g.getTarefa_grupal().values()) {
                                    if (tg.getRealiza()!= null) {
                                        for (Realiza realiza : tg.getRealiza().values()) {
                                            if (realiza.getRea_user_id().equals(firebaseUser.getUid())&&
                                                    realiza.getRea_status()==0){
                                                cartList.add(tg);
                                                mAdapter.notifyDataSetChanged();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public void list_opcoes(final TarefaGrupal tarefa, final int position){
        ArrayList<String> itens = new ArrayList<String>();
        itens.add("Abandonar tarefa");
        //itens.add("Excluir");
        //adapter utilizando um layout customizado (TextView)
        ArrayAdapter adapter = new ArrayAdapter(getActivity(), R.layout.item_alerta, itens);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setSingleChoiceItems(adapter, 0, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                if (arg1 == 0){
                    LayoutInflater inflateDialog = getLayoutInflater();
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setTitle("Abandonar tarefa?");
                    alert.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mAdapter.removeItem(position);
                            databaseReference.child("grupo").child(tarefa.getTar_id_grp()).
                                                child("tarefa_grupal").child(tarefa.getTar_id()).
                                                    child("realiza").child(firebaseUser.getUid()).
                                                        removeValue();
                        }
                    }).setNegativeButton("Não", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialog = alert.create();
                    dialog.show();
                }
                alerta.dismiss();
            }
        });
        alerta = builder.create();
        alerta.show();
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

    private void inicializarComponentes(View rootView) {
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction,final int position) {
        if (viewHolder instanceof CartListGrupalAdapter.MyViewHolder) {
            // get the removed item name to display it in snack bar
            //String name = cartList.get(viewHolder.getAdapterPosition()).getTar_nome();

            // backup of removed item for undo purpose
            //final TarefaIndividual deletedItem = cartList.get(viewHolder.getAdapterPosition());
            //final int deletedIndex = viewHolder.getAdapterPosition();

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Você ganhou "+new TarefaGrupal().equacaoPontos(
                    mAdapter.subtrairDatas(cartList.get(viewHolder.getAdapterPosition())),
                    cartList.get(viewHolder.getAdapterPosition()), false)+" pontos!")
                    .setCancelable(false)
                    .setPositiveButton("RECEBER", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            new TarefaGrupal().finalizar(databaseReference, cartList.get(viewHolder.getAdapterPosition()),
                                    firebaseUser, mAdapter.subtrairDatas(cartList.get(viewHolder.getAdapterPosition())));
                            mAdapter.removeItem(position);
                        }
                    }).setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //mAdapter.notifyItemRemoved(position +1);
                            mAdapter.notifyItemRangeChanged(position, mAdapter.getItemCount());
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
