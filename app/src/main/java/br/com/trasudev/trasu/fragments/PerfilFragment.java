package br.com.trasudev.trasu.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import br.com.trasudev.trasu.R;
import br.com.trasudev.trasu.activitys.MainActivity;
import br.com.trasudev.trasu.classes.CircleTransform;
import br.com.trasudev.trasu.classes.Conexao;
import br.com.trasudev.trasu.entidades.TarefaIndividual;
import br.com.trasudev.trasu.entidades.Usuario;
import br.com.trasudev.trasu.mask.MaskEditTextChangedListener;
import jp.wasabeef.glide.transformations.BlurTransformation;

import static android.app.Activity.RESULT_OK;
import static br.com.trasudev.trasu.activitys.LoginActivity.calledAlready;
import static br.com.trasudev.trasu.activitys.MainActivity.txtWebsite;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PerfilFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PerfilFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PerfilFragment extends Fragment {
    private FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;

    private ImageView imgUser;
    private TextView redefinirSenha;
    private EditText nomeUser,DDDUser,telUser;
    private Button btnAlterar;
    private ProgressDialog progressDialog;
    private ProgressBar progressBar;

    private static Usuario usuarioStatic;

    private static final int GALERY_INTENT = 2;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private Context context;

    public PerfilFragment() {
        // Required empty public constructor
    }

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

    public static PerfilFragment newInstance(String param1, String param2) {
        PerfilFragment fragment = new PerfilFragment();
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
        inflater.inflate(R.menu.menu_blank, menu);
    }

    private void inicializarComponentes(View rootView){
        redefinirSenha = rootView.findViewById(R.id.redefinirSenha);
        imgUser = (ImageView) rootView.findViewById(R.id.img_user);
        nomeUser = (EditText) rootView.findViewById(R.id.editText_nome);
        DDDUser = (EditText) rootView.findViewById(R.id.editText_DDD);
        MaskEditTextChangedListener maskDDD = new MaskEditTextChangedListener("+##", DDDUser);
        DDDUser.addTextChangedListener(maskDDD);
        telUser = (EditText) rootView.findViewById(R.id.editText_telefone);
        MaskEditTextChangedListener maskTelefone = new MaskEditTextChangedListener("(##)#####-####", telUser);
        telUser.addTextChangedListener(maskTelefone);
        btnAlterar = (Button) rootView.findViewById(R.id.btnAlterar);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        progressDialog = new ProgressDialog(getActivity());
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseUser = Conexao.getFirebaseUser();
        databaseReference.child("usuario").child(firebaseUser.getUid()).
                addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Usuario user = dataSnapshot.getValue(Usuario.class);
                usuarioStatic = user;
                nomeUser.setText(user.getUser_nome());
                DDDUser.setText(user.getUser_telefone().substring(0,3));
                telUser.setText(user.getUser_telefone().substring(3,17));
                progressBar.setVisibility(View.VISIBLE);
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
                                                .into(imgUser);
                                        progressBar.setVisibility(ProgressBar.INVISIBLE);
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_perfil, container,false);
        inicializarComponentes(rootView);
        onClickEvent();
        onClickAlterarDados();
        return rootView;

    }

    private void onClickEvent() {
        imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,GALERY_INTENT);
            }
        });
        redefinirSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Aguarde...");
                progressDialog.show();
                firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.sendPasswordResetEmail(txtWebsite.getText().toString()).
                        addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    progressDialog.dismiss();
                                    Toast.makeText(context,
                                            "Verifique sua caixa de entrada \npara redefinir sua senha",
                                            Toast.LENGTH_LONG).show();
                                }else{
                                    Toast.makeText(context,
                                            "O processo falhou, tente novamente",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });
    }

    private void onClickAlterarDados(){
        btnAlterar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nomeUser.getText().toString().equals("") || DDDUser.getText().toString().equals("") ||
                        telUser.getText().toString().equals("")){
                    Toast.makeText(context,"Preencha os campos vazios",Toast.LENGTH_SHORT).show();
                }else{
                    progressDialog.setMessage("Atualizando...");
                    progressDialog.show();
                    new Usuario().alterar(databaseReference,firebaseUser,nomeUser,DDDUser,
                            telUser,usuarioStatic.getUser_icon(),usuarioStatic);
                    progressDialog.dismiss();
                    Toast.makeText(context,"Dados alterados",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void onClickAlterar(final Uri uri){
        btnAlterar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setMessage("Atualizando...");
                progressDialog.show();
                StorageReference filePath = storageReference.child("img_profiles").
                        child("user_icon_"+usuarioStatic.getUser_id()+uri.getLastPathSegment());
                filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        new Usuario().alterar(databaseReference,firebaseUser,nomeUser,DDDUser,
                                telUser,"user_icon_"+usuarioStatic.getUser_id()+uri.getLastPathSegment(),usuarioStatic);
                        Toast.makeText(context,"Dados alterados",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALERY_INTENT && resultCode == RESULT_OK){
            final Uri uri = data.getData();
            if (!getMimeType(context,uri).equals("gif")){
                Glide.with(context)
                        .load(uri)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.RESOURCE))
                        .apply(RequestOptions.centerCropTransform())
                        .apply(RequestOptions.fitCenterTransform())
                        .apply(RequestOptions.bitmapTransform(new CircleTransform()))
                        .thumbnail(0.5f)
                        .into(imgUser);
                onClickAlterar(uri);
            }else{
                Toast.makeText(context,"Formato não suportado",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static String getMimeType(Context context, Uri uri) {
        String extension;

        //Check uri format to avoid null
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //If scheme is a content
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());

        }

        return extension;
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
