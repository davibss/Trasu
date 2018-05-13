package br.com.trasudev.trasu.entidades;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.UUID;

import br.com.trasudev.trasu.activitys.MainActivity;
import br.com.trasudev.trasu.classes.CircleTransform;

import static br.com.trasudev.trasu.activitys.LoginActivity.calledAlready;

public class Usuario {
    private String user_id;
    private String user_nome;
    private String user_senha;
    private String user_email;
    private Integer user_pontos;
    private String user_telefone;
    private String user_icon;

    private Usuario user;
    //private HashMap<String,TarefaIndividual> tarefas;

    public Usuario(){

    }

    public void cadastrar(DatabaseReference databaseReference, String UID,String editNome,String editSenha, String editEmail,
                          String editTelefone){
        Usuario user = new Usuario();
        user.setUser_id(UID);
        user.setUser_nome(editNome);
        user.setUser_telefone(editTelefone);
        user.setUser_pontos(0);
        user.setUser_senha(editSenha);
        user.setUser_email(editEmail);
        user.setUser_icon("ic_person-web.png");
        databaseReference.child("usuario").child(user.getUser_id()).setValue(user);
    }

    public void alterar(DatabaseReference databaseReference, FirebaseUser firebaseUser,
                        EditText nome, EditText ddd, EditText tel, String imagem, Usuario user){
        user.setUser_nome(nome.getText().toString());
        user.setUser_telefone(ddd.getText().toString()+tel.getText().toString());
        user.setUser_icon(imagem);
        databaseReference.child("usuario").child(firebaseUser.getUid()).setValue(user);
    }

    public void buscar(DatabaseReference databaseReference, final FirebaseUser firebaseUser,
                       final TextView txtWebsite, final TextView txtName, final TextView txtPontos){
        databaseReference.child("usuario").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot obj : dataSnapshot.getChildren()){
                    final Usuario userSelect = obj.getValue(Usuario.class);
                    if (userSelect.getUser_id().equals(firebaseUser.getUid())){
                        txtName.setText(userSelect.getUser_nome());
                        txtWebsite.setText(userSelect.getUser_email());
                        txtPontos.setText("Pontos: " + String.valueOf(userSelect.getUser_pontos()));
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public boolean equals (Object o) {
        if (o != null) {
            Usuario x = (Usuario) o;
            if (x.user_id == user_id) return true;
            return false;
        }else{
            return false;
        }
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_nome() {
        return user_nome;
    }

    public void setUser_nome(String user_nome) {
        this.user_nome = user_nome;
    }

    public String getUser_senha() {
        return user_senha;
    }

    public void setUser_senha(String user_senha) {
        this.user_senha = user_senha;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public Integer getUser_pontos() {
        return user_pontos;
    }

    public void setUser_pontos(Integer user_pontos) {
        this.user_pontos = user_pontos;
    }

    public String getUser_telefone() {
        return user_telefone;
    }

    public void setUser_telefone(String user_telefone) {
        this.user_telefone = user_telefone;
    }

    public String getUser_icon() {
        return user_icon;
    }

    public void setUser_icon(String user_icon) {
        this.user_icon = user_icon;
    }

}
