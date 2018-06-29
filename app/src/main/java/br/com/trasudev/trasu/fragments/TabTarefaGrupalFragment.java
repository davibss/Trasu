package br.com.trasudev.trasu.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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

    private TextView textView;

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

    private void recyclerViewEvent(View rootView) {
        recyclerView = rootView.findViewById(R.id.recycler_view);
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
                        alert.setCustomTitle(customTitle(inflateDialog,mAdapter.getItem(position).getTar_nome()));
                        //alert.setTitle("Visualizar tarefa grupal");
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
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                        if (cartList.isEmpty()){
                            textView.setVisibility(View.VISIBLE);
                        }else{
                            textView.setVisibility(View.INVISIBLE);
                            Collections.sort(cartList, new Comparator<TarefaGrupal>() {
                                @Override
                                public int compare(TarefaGrupal o1, TarefaGrupal o2) {
                                    return converterData(o1.getTar_dataFinal()).
                                            compareTo(converterData(o2.getTar_dataFinal()));
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
                    alert.setMessage("Nome: " + tarefa.getTar_nome())
                         .setTitle("Abandonar tarefa?");
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
        final TextView textView = alertLayout.findViewById(R.id.prioridadeText);
        final CheckBox checkBoxNotificacao = (CheckBox) alertLayout.findViewById(R.id.checkBoxNotificacao);
        final Button btnAlterar = (Button) alertLayout.findViewById(R.id.btnCadastrarTar);
        RadioButton buttonAlta = (RadioButton) group.findViewById(R.id.radioAlta);
        RadioButton buttonMedia = (RadioButton) group.findViewById(R.id.radioMedia);
        RadioButton buttonBaixa = (RadioButton) group.findViewById(R.id.radioBaixa);
        editDescricao.setBackgroundColor(Color.TRANSPARENT);
        textView.setVisibility(View.GONE);
        editNome.setVisibility(View.GONE);
        editDescricao.setKeyListener(null);
        group.setEnabled(false);
        editPrazo.setVisibility(View.GONE);
        checkBoxNotificacao.setVisibility(View.GONE);
        btnAlterar.setVisibility(View.GONE);
        buttonAlta.setVisibility(View.GONE);
        buttonMedia.setVisibility(View.GONE);
        buttonBaixa.setVisibility(View.GONE);
        btnAlterar.setVisibility(View.GONE);
        editDescricao.setText(tarefa.getTar_descricao());
    }

    private void inicializarComponentes(View rootView) {
        textView = rootView.findViewById(R.id.textViewNothing);
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
            final TarefaGrupal deletedItem = cartList.get(viewHolder.getAdapterPosition());
            //final int deletedIndex = viewHolder.getAdapterPosition();

            if (deletedItem.getTar_status()==0&&!mAdapter.subtrairDatas(deletedItem).equals("0")){
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Nome: " + deletedItem.getTar_nome())
                        .setMessage("Você ganhou "+new TarefaGrupal().equacaoPontos(
                        mAdapter.subtrairDatas(cartList.get(viewHolder.getAdapterPosition())),
                        cartList.get(viewHolder.getAdapterPosition()), false)+" pontos!")
                        .setCancelable(false)
                        .setPositiveButton("Receber", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                new TarefaGrupal().finalizar(databaseReference, deletedItem,
                                        firebaseUser, mAdapter.subtrairDatas(deletedItem));
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

            /*AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
            alert.show();*/
        }
    }

    private void alert(String msg) {
        Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
