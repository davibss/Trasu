package br.com.trasudev.trasu.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import br.com.trasudev.trasu.R;
import br.com.trasudev.trasu.activitys.TarefaGrupalActivity;
import br.com.trasudev.trasu.classes.CartListAdapter;
import br.com.trasudev.trasu.classes.CartListGroupAdapter;
import br.com.trasudev.trasu.classes.CartUserAdapter;
import br.com.trasudev.trasu.classes.CircleTransform;
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
    StorageReference storageReference;
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

    private TextView textView;

    private RecyclerView recyclerViewIntegrante;
    private List<Usuario> cartListIntegrante;
    private CartUserAdapter mAdapterIntegrante;


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private Context context;

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
        textView = rootView.findViewById(R.id.textViewNothing);
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
                textView.setVisibility(View.INVISIBLE);
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
                //alert.setTitle("Cadastrar grupo");
                alert.setCustomTitle(customTitle(inflateDialog,"Cadastrar grupo"));
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
                if (item.getGrp_lider().equals(firebaseUser.getUid())){
                    holder.viewForeground.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, TarefaGrupalActivity.class);
                            intent.putExtra("grupoOBJ",item);
                            startActivity(intent);
                        }
                    });
                }
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
                                setImageLider(user, holder.img_lider,holder.progressBar);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context.getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLongClickable(true);
        eventoDatabaseCard();
    }

    private void setImageLider(final Usuario user, final ImageView imgLider,final ProgressBar progressBar) {
        progressBar.setVisibility(View.VISIBLE);
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
                                        .into(imgLider);
                                progressBar.setVisibility(ProgressBar.INVISIBLE);
                            }
                        });
                    }
                });
            }
        }).start();
    }

    private void eventoDatabaseCard() {
        databaseReference.child("grupo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                cartList.clear();
                for (DataSnapshot obj: dataSnapshot.getChildren()){
                    Grupo g = obj.getValue(Grupo.class);
                    if (g.getIntegrantes() != null) {
                        for (Usuario u : g.getIntegrantes().values()) {
                            if (u.getUser_id().equals(firebaseUser.getUid())){
                                cartList.add(g);
                            }
                        }
                    }
                    /*if (g.getIntegrantes().containsKey(firebaseUser.getUid())){
                        cartList.add(g);
                        mAdapter.notifyDataSetChanged();
                    }*/
                }
                mAdapter.notifyDataSetChanged();
                if (cartList.isEmpty()){
                    textView.setVisibility(View.VISIBLE);
                }else{
                    textView.setVisibility(View.INVISIBLE);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

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

    public void list_opcoes(final Grupo grupo, final int position){
        final ArrayList<String> itens = new ArrayList<String>();
        if (firebaseUser.getUid().equals(grupo.getGrp_lider())){
            itens.add("Visualizar/alterar grupo");
            itens.add("Gerenciar integrantes");
            itens.add("Excluir grupo");
        }else{
            itens.add("Ver tarefas");
            itens.add("Ver integrantes");
            itens.add("Sair do grupo");
        }
        //adapter utilizando um layout customizado (TextView)
        final ArrayAdapter adapter = new ArrayAdapter(context, R.layout.item_alerta, itens);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setSingleChoiceItems(adapter, 0, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                if (arg1 == itens.indexOf("Visualizar/alterar grupo")){
                    LayoutInflater inflateDialog = getLayoutInflater();
                    View alertLayout = inflateDialog.inflate(R.layout.cadastrar_grupo_layout, null);
                    final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    //alert.setTitle("Visualizar/alterar grupo");
                    alert.setCustomTitle(customTitle(inflateDialog,"Visualizar/alterar grupo"));
                    alert.setView(alertLayout);
                    dialog = alert.create();
                    dialog.show();
                    alterarComponentesGrupo(alertLayout, mAdapter.getItem(position));
                }else if (arg1 == itens.indexOf("Gerenciar integrantes")) {
                    LayoutInflater inflateDialog = getLayoutInflater();
                    View alertLayout = inflateDialog.inflate(R.layout.gerenciar_integrantes, null);
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    //alert.setTitle("Gerenciar integrantes");
                    alert.setCustomTitle(customTitle(inflateDialog,"Gerenciar integrantes"));
                    alert.setView(alertLayout);
                    dialog = alert.create();
                    dialog.show();
                    gerenciarIntegrantesGrupo(alertLayout,grupo);
                    eventoAddRemove(grupo);
                }else if (arg1 == itens.indexOf("Excluir grupo")){
                    new Grupo().excluir(databaseReference,grupo);
                    mAdapter.removeItem(position);
                }else if (arg1 == itens.indexOf("Ver integrantes")){
                    // Ver integrantes
                    LayoutInflater inflateDialog = getLayoutInflater();
                    View alertLayout = inflateDialog.inflate(R.layout.visualizar_integrantes, null);
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    //alert.setTitle("Ver integrantes");
                    alert.setCustomTitle(customTitle(inflateDialog,"Ver integrantes"));
                    alert.setView(alertLayout);
                    dialog = alert.create();
                    dialog.show();
                    visualizarIntegrantes(alertLayout,grupo);
                }else if (arg1 == itens.indexOf("Ver tarefas")){
                    Intent intent = new Intent(context, TarefaGrupalActivity.class);
                    intent.putExtra("grupoOBJ",grupo);
                    startActivity(intent);
                }else if (arg1 == itens.indexOf("Sair do grupo")){
                    databaseReference.child("grupo").child(grupo.getGrp_id()).
                            child("grp_integrantes").setValue(grupo.getGrp_integrantes()-1);
                    databaseReference.child("grupo").child(grupo.getGrp_id()).
                            child("integrantes").child(firebaseUser.getUid()).
                            removeValue();
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
        mAdapterIntegrante = new CartUserAdapter(getActivity(), cartListIntegrante){
            @Override
            public void onBindViewHolder(final MyViewHolder holder, int position) {
                super.onBindViewHolder(holder, position);
                final Usuario item = cartListIntegrante.get(position);
                mostrarIconeUsuarios(holder,item);
            }
        };
        RecyclerView.LayoutManager mLayoutManagerI = new LinearLayoutManager(context.getApplicationContext());
        recyclerViewIntegrante.setLayoutManager(mLayoutManagerI);
        recyclerViewIntegrante.setItemAnimator(new DefaultItemAnimator());
        recyclerViewIntegrante.setAdapter(mAdapterIntegrante);
        recyclerViewIntegrante.setLongClickable(true);
        for (Usuario user:grupo.getIntegrantes().values()) {
            cartListIntegrante.add(user);
            mAdapterIntegrante.notifyDataSetChanged();
        }
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
                mostrarIconeUsuarios(holder,item);
            }
        };
        RecyclerView.LayoutManager mLayoutManagerI = new LinearLayoutManager(context.getApplicationContext());
        recyclerViewIntegrante.setLayoutManager(mLayoutManagerI);
        recyclerViewIntegrante.setItemAnimator(new DefaultItemAnimator());
        recyclerViewIntegrante.setAdapter(mAdapterIntegrante);
        mAdapterIntegrante.setClickListener(new CustomItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                alert("Aguarde...");
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
                alert("Integrante removido");
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
                mostrarIconeUsuarios(holder,item);
            }
        };
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerViewUser.setLayoutManager(mLayoutManager);
        recyclerViewUser.setItemAnimator(new DefaultItemAnimator());
        recyclerViewUser.setAdapter(mAdapterUsuario);
        mAdapterUsuario.setClickListener(new CustomItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                alert("Aguarde...");
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
                alert("Usuário adicionado");
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
