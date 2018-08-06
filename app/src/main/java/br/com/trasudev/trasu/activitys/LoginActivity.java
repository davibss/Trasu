package br.com.trasudev.trasu.activitys;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;

import br.com.trasudev.trasu.R;
import br.com.trasudev.trasu.classes.Conexao;
import br.com.trasudev.trasu.entidades.Usuario;

import static br.com.trasudev.trasu.activitys.MainActivity.txtWebsite;

public class LoginActivity extends AppCompatActivity {
    private EditText editEmail, editSenha;
    private TextView txvCadastro,txtEsqueceu;
    private Button btnLogin;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    public static boolean calledAlready = false;
    private ProgressDialog progressDialog;

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void verificarUser() {
        if (firebaseUser == null){
            //
        }else{

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firebaseAuth = Conexao.getFirebaseAuth();
        firebaseUser = Conexao.getFirebaseUser();
        initFirebase();
        verificarUser();
        initComponents();
        onClickButton();

    }

    private void initComponents() {
        editEmail = (EditText) findViewById(R.id.editText_email);
        editSenha = (EditText) findViewById(R.id.editText_senha);
        txvCadastro = (TextView) findViewById(R.id.txv_cadastro);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        txtEsqueceu = findViewById(R.id.esqueceuSenha);
    }

    private void onClickButton() {
        txvCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(), CadastrarActivity.class));
            }
        });

        txtEsqueceu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                //final EditText edittext = getLayoutInflater().inflate(R.layout.email_senha_recuperar,null);
                View view = getLayoutInflater().inflate(R.layout.email_senha_recuperar,null);
                final EditText editText = view.findViewById(R.id.esqueceuSenha);
                builder.setView(view);
                builder.setMessage("Recuperação de senha")
                        .setCancelable(false)
                        .setPositiveButton("ENVIAR", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                progressDialog = new ProgressDialog(LoginActivity.this);
                                progressDialog.setMessage("Aguarde...");
                                progressDialog.show();
                                firebaseAuth = FirebaseAuth.getInstance();
                                firebaseAuth.sendPasswordResetEmail(editText.getText().toString().trim()).
                                        addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    progressDialog.dismiss();
                                                    Toast.makeText(getBaseContext(),
                                                            "Verifique sua caixa de entrada \npara redefinir sua senha",
                                                            Toast.LENGTH_LONG).show();
                                                }else{
                                                    Toast.makeText(getBaseContext(),
                                                            "O processo falhou, tente novamente",
                                                            Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                            }
                        }).setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editEmail.getText().toString().equals("") ||  editSenha.getText().toString().equals("")){
                    alert("Preencha o(s) campo(s) vazio(s)");
                }else {
                    progressDialog = new ProgressDialog(LoginActivity.this);
                    progressDialog.setMessage("Logando...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    btnLogin.setEnabled(false);
                    new Thread(){
                        @Override
                        public void run() {
                            String email = editEmail.getText().toString().trim();
                            String senha = editSenha.getText().toString().trim();
                            login(email,senha);
                        }
                    }.start();
                }
            }
        });
    }

    private void login(final String email,final String senha) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                firebaseAuth.signInWithEmailAndPassword(email,senha).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Intent intent = new Intent(getBaseContext(),MainActivity.class);
                            intent.putExtra("senha",senha);
                            btnLogin.setEnabled(true);
                            startActivity(intent);
                            finish();
                        }else {
                            btnLogin.setEnabled(true);
                            progressDialog.dismiss();
                            alert("E-mail ou senha errados");
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (progressDialog != null){
            progressDialog.dismiss();
        }
    }

    public ArrayList<String> getContactList(Context context) {
        ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            ArrayList<String> phoneList = new ArrayList<>();
            ArrayList<String> phoneList2 = new ArrayList<>();
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        phoneList.add(phoneNo.replaceAll("[^0-9]",""));
                    }
                    pCur.close();
                }
            }
            Collections.sort(phoneList);
            for (int i=0;i<phoneList.size();i++){
                if (i!=0 && phoneList.get(i-1).equals(phoneList.get(i))){
                    //
                }else{
                    phoneList2.add(phoneList.get(i));
                    //Log.i(TAG,phoneList.get(i));
                }
            }
            return phoneList2;
        }
        if(cur!=null){
            cur.close();
        }
        return null;
    }

    private void alert(String msg) {
        Toast.makeText(getBaseContext(),msg, Toast.LENGTH_SHORT).show();
    }

    private void initFirebase() {
        FirebaseApp.initializeApp(LoginActivity.this);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        if (!calledAlready) {
            firebaseDatabase.setPersistenceEnabled(true);
            calledAlready = true;
        }
        databaseReference = firebaseDatabase.getReference();
    }
}
