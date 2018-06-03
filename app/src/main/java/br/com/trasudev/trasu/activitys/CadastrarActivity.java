package br.com.trasudev.trasu.activitys;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import br.com.trasudev.trasu.R;
import br.com.trasudev.trasu.classes.Conexao;
import br.com.trasudev.trasu.entidades.Usuario;
import br.com.trasudev.trasu.mask.MaskEditTextChangedListener;

import static br.com.trasudev.trasu.activitys.LoginActivity.calledAlready;

public class CadastrarActivity extends AppCompatActivity {
    private EditText editNome,editTelefone,editDDD,editEmail,editSenha,editConfSenha;
    private Button btnCadastrar;
    private FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    private FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    private ProgressDialog progressDialog;

    private void initFirebase() {
        FirebaseApp.initializeApp(CadastrarActivity.this);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        if (!calledAlready) {
            firebaseDatabase.setPersistenceEnabled(true);
            calledAlready = true;
        }
        databaseReference = firebaseDatabase.getReference();
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth = Conexao.getFirebaseAuth();
        initFirebase();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initComponents();
        onClickButton();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getBaseContext(),LoginActivity.class));
        finish();
    }

    private void onClickButton() {
        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editEmail.getText().toString().equals("") || editNome.getText().toString().equals("") ||
                        editTelefone.getText().toString().equals("") || editDDD.getText().toString().equals("") ||
                        editSenha.getText().toString().equals("") || editConfSenha.getText().toString().equals("")){
                    alert("Preencha o(s) campo(s) vazio(s)");
                }else if (!editSenha.getText().toString().equals(editConfSenha.getText().toString())){
                    alert("Os campos de senha tem valores diferentes");
                }else if (editSenha.getText().toString().length() < 6 ||
                        editConfSenha.getText().toString().length() < 6){
                    alert("Os campos de senha precisam ter \n no mínimo 6 caracteres");
                } else {
                    progressDialog = new ProgressDialog(CadastrarActivity.this);
                    progressDialog.setMessage("Registrando. Aguarde...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String email = editEmail.getText().toString().trim();
                            String senha = editSenha.getText().toString().trim();
                            criarUser(email,senha);
                        }
                    }).start();
                }
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

    private void alert(String msg) {
        Toast.makeText(getBaseContext(),msg,Toast.LENGTH_SHORT).show();
    }

    private void initComponents() {
        editNome = (EditText) findViewById(R.id.editText_nome);
        //region Máscara do telefone
        editTelefone = (EditText) findViewById(R.id.editText_telefone);
        MaskEditTextChangedListener maskTelefone = new MaskEditTextChangedListener("(##)#####-####", editTelefone);
        editTelefone.addTextChangedListener(maskTelefone);
        //endregion
        //region Máscara do DDD
        editDDD = (EditText) findViewById(R.id.editText_DDD);
        MaskEditTextChangedListener maskDDD = new MaskEditTextChangedListener("+##", editDDD);
        editDDD.addTextChangedListener(maskDDD);
        //endregion
        editEmail = (EditText) findViewById(R.id.editText_email);
        editSenha = (EditText) findViewById(R.id.editText_senha);
        editConfSenha = (EditText) findViewById(R.id.editText_confSenha);
        btnCadastrar = (Button) findViewById(R.id.btnCadastrar);
    }

    private void criarUser(final String email,final String senha) {
        firebaseAuth.createUserWithEmailAndPassword(email,senha).addOnCompleteListener(CadastrarActivity.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        String msg;
                        if (task.isSuccessful()){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    new Usuario().cadastrar(databaseReference,firebaseAuth.getUid(),editNome.getText().toString(),
                                            editSenha.getText().toString(),editEmail.getText().toString(),
                                            editDDD.getText().toString()+editTelefone.getText().toString());
                                    Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        }else {
                            alert("Erro de cadastro");
                        }
                    }
                });

    }
}
