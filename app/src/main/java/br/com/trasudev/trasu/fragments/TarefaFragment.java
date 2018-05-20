package br.com.trasudev.trasu.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import br.com.trasudev.trasu.R;
import br.com.trasudev.trasu.activitys.MainActivity;
import br.com.trasudev.trasu.classes.CartListAdapter;
import br.com.trasudev.trasu.classes.Conexao;
import br.com.trasudev.trasu.classes.RecyclerItemClickListener;
import br.com.trasudev.trasu.classes.RecyclerItemTouchHelper;
import br.com.trasudev.trasu.classes.TarefaAdapter;
import br.com.trasudev.trasu.entidades.TarefaIndividual;

import static br.com.trasudev.trasu.activitys.LoginActivity.calledAlready;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TarefaFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TarefaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TarefaFragment extends Fragment implements
        RecyclerItemTouchHelper.RecyclerItemTouchHelperListener{
    private FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private String checkValue;
    private AlertDialog dialog;
    private AlertDialog alerta;
    private FloatingActionButton floatingActionButton;
    private RecyclerView recyclerView;
    private List<TarefaIndividual> cartList;
    private CartListAdapter mAdapter;
    private CoordinatorLayout coordinatorLayout;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
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

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TarefaFragment.
     */
    // TODO: Rename and change types and number of parameters
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
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        firebaseUser = Conexao.getFirebaseUser();
        verificarUser();
        initFirebase();
    }

    private void inicializarComponentes(View rootView){
        floatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.floatingActionButton);
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
                new TarefaIndividual().cadastrar(databaseReference,editNome.getText().toString(),
                        editDescricao.getText().toString(),checkValue,
                        Integer.parseInt(editPrazo.getText().toString()),firebaseUser.getUid(),
                        checkBoxNotificacao.isChecked()?1:0);
                dialog.dismiss();

            }
        });
    }

    private void alterarComponentesTarefa(View alertLayout, final TarefaIndividual tarefa){
        final EditText editNome = alertLayout.findViewById(R.id.editTextTarNome);
        final EditText editDescricao = alertLayout.findViewById(R.id.editTextTarDesc);
        final RadioGroup group = (RadioGroup) alertLayout.findViewById(R.id.radioGroup);
        final EditText editPrazo = (EditText) alertLayout.findViewById(R.id.editTextTarPrazo);
        final CheckBox checkBoxNotificacao = (CheckBox) alertLayout.findViewById(R.id.checkBoxNotificacao);
        final Button btnCadastrar = (Button) alertLayout.findViewById(R.id.btnCadastrarTar);
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
                alert.setTitle("Cadastrar tarefa");
                alert.setView(alertLayout);
                dialog = alert.create();
                dialog.show();
                inicializarComponentesTarefa(alertLayout);
            }
        });
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
        eventoDatabaseCard();
    }

    private void eventoDatabaseCard() {
        databaseReference.child("usuario").child(firebaseUser.getUid()).
                child("tarefa_individual").orderByChild("tar_dataFinal").
                addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                cartList.clear();
                for (DataSnapshot obj: dataSnapshot.getChildren()){
                    TarefaIndividual p = obj.getValue(TarefaIndividual.class);
                    if (p.getTar_status()==0) {
                        cartList.add(p);
                        mAdapter.notifyDataSetChanged();
                    }
                }
                /*Collections.sort(cartList, new Comparator<TarefaIndividual>() {
                    @Override
                    public int compare(TarefaIndividual o1, TarefaIndividual o2) {
                        return o1.getTar_nome().compareTo(o2.getTar_nome());
                    }
                });
                mAdapter.notifyDataSetChanged();*/
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof CartListAdapter.MyViewHolder) {
            // get the removed item name to display it in snack bar
            String name = cartList.get(viewHolder.getAdapterPosition()).getTar_nome();

            // backup of removed item for undo purpose
            final TarefaIndividual deletedItem = cartList.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Você ganhou "+new TarefaIndividual().equacaoPontos(
                    mAdapter.subtrairDatas(cartList.get(viewHolder.getAdapterPosition())),
                    cartList.get(viewHolder.getAdapterPosition()), false)+" pontos!")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            new TarefaIndividual().finalizar(databaseReference, cartList.get(viewHolder.getAdapterPosition()),
                                    firebaseUser, mAdapter.subtrairDatas(cartList.get(viewHolder.getAdapterPosition())));
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
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
        itens.add("Excluir");
        //adapter utilizando um layout customizado (TextView)
        ArrayAdapter adapter = new ArrayAdapter(getActivity(), R.layout.item_alerta, itens);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setSingleChoiceItems(adapter, 0, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                if ((arg1 == 0)&&tarefa.getTar_status()==0&&!mAdapter.subtrairDatas(tarefa).equals("0")){
                    new TarefaIndividual().excluir(databaseReference,tarefa,
                            firebaseUser);
                    mAdapter.removeItem(position);
                }else if ((arg1 == 0)&&tarefa.getTar_status()==1){
                    alert("Tarefa já finalizada");
                }else if ((arg1 == 0)&&mAdapter.subtrairDatas(tarefa).equals("0")){
                    alert("Tarefa expirada");
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
