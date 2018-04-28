package br.com.trasudev.trasu.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import br.com.trasudev.trasu.R;
import br.com.trasudev.trasu.classes.Conexao;
import br.com.trasudev.trasu.entidades.Grupo;
import br.com.trasudev.trasu.entidades.TarefaIndividual;

import static br.com.trasudev.trasu.activitys.LoginActivity.calledAlready;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GrupoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GrupoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GrupoFragment extends Fragment {

    private FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private FloatingActionButton floatingActionButton;
    private AlertDialog dialog;


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

    private void onClickButton() {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = getLayoutInflater();
                View alertLayout = inflater.inflate(R.layout.cadastrar_tarefa_layout, null);
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setTitle("Cadastrar grupo");
                alert.setView(alertLayout);
                dialog = alert.create();
                dialog.show();
                inicializarComponentesTarefa(alertLayout);
            }
        });
    }

    private void inicializarComponentes(View rootView){
        floatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.floatingActionButton);
    }

    private void inicializarComponentesTarefa(View alertLayout) {
        final EditText editNome = alertLayout.findViewById(R.id.editTextGrpNome);
        final Button btnCadastrar = (Button) alertLayout.findViewById(R.id.btnCadastrarGrp);
        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Grupo().cadastrar(databaseReference,editNome.getText().toString(),
                        firebaseUser.getUid());
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
                inicializarComponentesTarefa(alertLayout);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_grupos, container,false);
        inicializarComponentes(rootView);
        onClickEvent();
        //eventoDatabase();
        return rootView;
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
