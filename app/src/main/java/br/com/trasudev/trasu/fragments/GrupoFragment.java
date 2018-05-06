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
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.com.trasudev.trasu.R;
import br.com.trasudev.trasu.classes.CartListAdapter;
import br.com.trasudev.trasu.classes.CartListGroupAdapter;
import br.com.trasudev.trasu.classes.CartUserAdapter;
import br.com.trasudev.trasu.classes.Conexao;
import br.com.trasudev.trasu.classes.CustomItemClickListener;
import br.com.trasudev.trasu.classes.RecyclerItemClickListener;
import br.com.trasudev.trasu.classes.RecyclerItemTouchHelper;
import br.com.trasudev.trasu.entidades.Grupo;
import br.com.trasudev.trasu.entidades.TarefaIndividual;
import br.com.trasudev.trasu.entidades.Usuario;

import static br.com.trasudev.trasu.activitys.LoginActivity.calledAlready;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GrupoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GrupoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GrupoFragment extends Fragment{

    private FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private FloatingActionButton floatingActionButton;
    private AlertDialog dialog;
    private AlertDialog alerta;

    private RecyclerView recyclerView;
    private List<Grupo> cartList;
    private CartListGroupAdapter mAdapter;
    private CoordinatorLayout coordinatorLayout;

    private RecyclerView recyclerViewUser;
    private List<Usuario> cartListUsuario;
    private CartUserAdapter mAdapterUsuario;

    private RecyclerView recyclerViewIntegrante;
    private List<Usuario> cartListIntegrante;
    private CartUserAdapter mAdapterIntegrante;


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public GrupoFragment() {
        // Required empty public constructor
    }

    public static GrupoFragment newInstance(String param1, String param2) {
        GrupoFragment fragment = new GrupoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseUser = Conexao.getFirebaseUser();
        verificarUser();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFirebase();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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

    private void inicializarComponentes(View rootView){
        floatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.floatingActionButton);
    }

    private void inicializarComponentesGrupo(View alertLayout) {
        final EditText editNome = alertLayout.findViewById(R.id.editTextGrpNome);
        final Button btnCadastrar = (Button) alertLayout.findViewById(R.id.btnCadastrarGrp);
        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Grupo().cadastrar(databaseReference,editNome.getText().toString(),
                        firebaseUser);
                dialog.dismiss();
            }
        });
    }

    private void onClickEvent() {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflateDialog = getLayoutInflater();
                View alertLayout = inflateDialog.inflate(R.layout.cadastrar_grupo_layout, null);
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setTitle("Cadastrar");
                alert.setView(alertLayout);
                dialog = alert.create();
                dialog.show();
                inicializarComponentesGrupo(alertLayout);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_grupos, container,false);
        inicializarComponentes(rootView);
        onClickEvent();
        recyclerViewEvent(rootView);
        return rootView;
    }

    private void recyclerViewEvent(View rootView) {
        recyclerView = rootView.findViewById(R.id.recycler_view);
        coordinatorLayout = rootView.findViewById(R.id.coordinator_layout);
        cartList = new ArrayList<>();
        mAdapter = new CartListGroupAdapter(getActivity(), cartList){
            @Override
            public void onBindViewHolder(final MyViewHolder holder,final int position) {
                super.onBindViewHolder(holder, position);
                final Grupo item = cartList.get(position);
                holder.menu_grupo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        list_opcoes(mAdapter.getItem(position),position);
                    }
                });
                databaseReference.child("usuario").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot obj: dataSnapshot.getChildren()){
                            Usuario user = obj.getValue(Usuario.class);
                            if (user.getUser_id().equals(item.getGrp_lider())){
                                holder.lider.setText(String.valueOf("Líder: " + user.getUser_nome()));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLongClickable(true);
        eventoDatabaseCard();
    }

    private void eventoDatabaseCard() {
        databaseReference.child("grupo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //listTarefa.clear();
                cartList.clear();
                for (DataSnapshot obj: dataSnapshot.getChildren()){
                    Grupo g = obj.getValue(Grupo.class);
                    if (g.getIntegrantes().containsKey(firebaseUser.getUid())){
                        cartList.add(g);
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void list_opcoes(final Grupo grupo, final int position){
        final ArrayList<String> itens = new ArrayList<String>();
        if (firebaseUser.getUid().equals(grupo.getGrp_lider())){
            itens.add("Visualizar/Alterar");
            itens.add("Gerenciar integrantes");
            itens.add("Excluir");
        }
        itens.add("Ver integrantes");
        //adapter utilizando um layout customizado (TextView)
        final ArrayAdapter adapter = new ArrayAdapter(getActivity(), R.layout.item_alerta, itens);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setSingleChoiceItems(adapter, 0, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                if (arg1 == itens.indexOf("Visualizar/Alterar")){
                    LayoutInflater inflateDialog = getLayoutInflater();
                    View alertLayout = inflateDialog.inflate(R.layout.cadastrar_grupo_layout, null);
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setTitle("Visualizar/alterar grupo");
                    alert.setView(alertLayout);
                    dialog = alert.create();
                    dialog.show();
                    alterarComponentesGrupo(alertLayout, grupo);
                }else if (arg1 == itens.indexOf("Gerenciar integrantes")) {
                    LayoutInflater inflateDialog = getLayoutInflater();
                    View alertLayout = inflateDialog.inflate(R.layout.gerenciar_integrantes, null);
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setTitle("Gerenciar integrantes");
                    alert.setView(alertLayout);
                    dialog = alert.create();
                    dialog.show();
                    gerenciarIntegrantesGrupo(alertLayout,grupo);
                    eventoAddRemove(grupo);
                }else if (arg1 == itens.indexOf("Excluir")){
                    new Grupo().excluir(databaseReference,grupo);
                    mAdapter.removeItem(position);
                }else if (arg1 == itens.indexOf("Ver integrantes")){
                    // Ver integrantes
                    LayoutInflater inflateDialog = getLayoutInflater();
                    View alertLayout = inflateDialog.inflate(R.layout.visualizar_integrantes, null);
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setTitle("Ver integrantes");
                    alert.setView(alertLayout);
                    dialog = alert.create();
                    dialog.show();
                    visualizarIntegrantes(alertLayout,grupo);
                    for (Usuario user:grupo.getIntegrantes().values()) {
                        Log.d("ID",user.getUser_id());
                    }
                }
                alerta.dismiss();
            }
        });
        alerta = builder.create();
        alerta.show();
    }

    private void eventoAddRemove(final Grupo grupo) {
        databaseReference.child("grupo").child(grupo.getGrp_id()).
                child("integrantes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                databaseReference.child("grupo").child(grupo.getGrp_id()).
                        child("grp_integrantes").setValue(dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    private void visualizarIntegrantes(View alertLayout,final Grupo grupo) {
        recyclerViewIntegrante = alertLayout.findViewById(R.id.scrollIntegrantes);
        cartListIntegrante = new ArrayList<>();
        mAdapterIntegrante = new CartUserAdapter(getActivity(), cartListIntegrante);
        RecyclerView.LayoutManager mLayoutManagerI = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerViewIntegrante.setLayoutManager(mLayoutManagerI);
        recyclerViewIntegrante.setItemAnimator(new DefaultItemAnimator());
        recyclerViewIntegrante.setAdapter(mAdapterIntegrante);
        recyclerViewIntegrante.setLongClickable(true);
        for (Usuario user:grupo.getIntegrantes().values()) {
            cartListIntegrante.add(user);
            mAdapterIntegrante.notifyDataSetChanged();
        }
    }

    private void gerenciarIntegrantesGrupo(View alertLayout, final Grupo grupo) {
        /*
            RECYCLER VIEW DOS INTEGRANTES
         */
        recyclerViewIntegrante = alertLayout.findViewById(R.id.scrollIntegrantes);
        cartListIntegrante = new ArrayList<>();
        mAdapterIntegrante = new CartUserAdapter(getActivity(), cartListIntegrante){
            @Override
            public void onBindViewHolder(MyViewHolder holder,final int position) {
                super.onBindViewHolder(holder, position);
                final Usuario item = cartListIntegrante.get(position);
                holder.img_move.setImageResource(R.drawable.ic_arrow_upward_black_24dp);
            }
        };
        RecyclerView.LayoutManager mLayoutManagerI = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerViewIntegrante.setLayoutManager(mLayoutManagerI);
        recyclerViewIntegrante.setItemAnimator(new DefaultItemAnimator());
        recyclerViewIntegrante.setAdapter(mAdapterIntegrante);
        mAdapterIntegrante.setClickListener(new CustomItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                final Usuario item = cartListIntegrante.get(position);
                Usuario userSelect = new Usuario();
                userSelect.setUser_id(item.getUser_id());
                userSelect.setUser_nome(item.getUser_nome());
                userSelect.setUser_email(item.getUser_email());
                eventoDatabaseCardUsuario();
                mAdapterIntegrante.removeItem(position);
                databaseReference.child("grupo").child(grupo.getGrp_id()).
                        child("integrantes").child(userSelect.getUser_id()).
                        removeValue();
            }
        });
        eventoDatabaseCardIntegrante(grupo);
        /*
            RECYCLER VIEW DOS USUÁRIOS
         */
        recyclerViewUser = alertLayout.findViewById(R.id.scrollUsuarios);
        cartListUsuario = new ArrayList<>();
        mAdapterUsuario = new CartUserAdapter(getActivity(), cartListUsuario){
            @Override
            public void onBindViewHolder(MyViewHolder holder,final int position) {
                super.onBindViewHolder(holder, position);
                final Usuario item = cartListUsuario.get(position);
                holder.img_move.setImageResource(R.drawable.ic_arrow_downward_black_24dp);
            }
        };
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerViewUser.setLayoutManager(mLayoutManager);
        recyclerViewUser.setItemAnimator(new DefaultItemAnimator());
        recyclerViewUser.setAdapter(mAdapterUsuario);
        mAdapterUsuario.setClickListener(new CustomItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                final Usuario item = cartListUsuario.get(position);
                Usuario userSelect = new Usuario();
                userSelect.setUser_id(item.getUser_id());
                userSelect.setUser_nome(item.getUser_nome());
                userSelect.setUser_email(item.getUser_email());
                eventoDatabaseCardIntegrante(grupo);
                mAdapterUsuario.removeItem(position);
                databaseReference.child("grupo").child(grupo.getGrp_id()).
                        child("integrantes").child(userSelect.getUser_id()).
                        setValue(userSelect);
                eventoDatabaseCardUsuario();
            }
        });
        eventoDatabaseCardUsuario();
    }

    private void eventoDatabaseCardIntegrante(final Grupo grupo) {
        databaseReference.child("grupo").child(grupo.getGrp_id()).child("integrantes").
                addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                cartListIntegrante.clear();
                for (DataSnapshot obj: dataSnapshot.getChildren()){
                    Usuario u = obj.getValue(Usuario.class);
                    if (!u.getUser_id().equals(firebaseUser.getUid())) {
                        cartListIntegrante.add(u);
                        mAdapterIntegrante.notifyDataSetChanged();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void eventoDatabaseCardUsuario() {
        databaseReference.child("usuario").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                cartListUsuario.clear();
                for (DataSnapshot obj: dataSnapshot.getChildren()){
                    Usuario u = obj.getValue(Usuario.class);
                    boolean add = false;
                    for (Usuario user: cartListIntegrante) {
                        if (!u.getUser_id().equals(firebaseUser.getUid())){
                            if (user.getUser_id().equals(u.getUser_id())){
                                add = true;
                            }
                        }
                    }
                    if (!add&&!u.getUser_id().equals(firebaseUser.getUid())){
                        cartListUsuario.add(u);
                        mAdapterUsuario.notifyDataSetChanged();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void alterarComponentesGrupo(View alertLayout,final Grupo grupo) {
        final EditText editNome = alertLayout.findViewById(R.id.editTextGrpNome);
        final Button btnCadastrar = (Button) alertLayout.findViewById(R.id.btnCadastrarGrp);
        editNome.setText(grupo.getGrp_nome());
        btnCadastrar.setText("Alterar");
        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Grupo().alterar(databaseReference,grupo,editNome.getText().toString());
                dialog.dismiss();
            }
        });
    }

    private void alert(String msg) {
        Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
