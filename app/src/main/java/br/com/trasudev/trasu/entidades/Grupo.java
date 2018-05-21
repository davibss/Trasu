package br.com.trasudev.trasu.entidades;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import static br.com.trasudev.trasu.activitys.MainActivity.txtName;
@SuppressWarnings("serial")
public class Grupo implements Serializable{
    private String grp_id;
    private String grp_nome;
    private String grp_lider;
    private int grp_integrantes;
    private HashMap<String,Usuario> integrantes;
    private HashMap<String,TarefaGrupal> tarefa_grupal;

    public Grupo(){

    }

    public void cadastrar(DatabaseReference databaseReference, String nome, FirebaseUser user_id){
        this.setGrp_id(UUID.randomUUID().toString());
        this.setGrp_nome(nome);
        this.setGrp_lider(user_id.getUid());
        this.setGrp_integrantes(1);
        HashMap<String,Usuario> hashMap = new HashMap<String, Usuario>();
        Usuario user = new Usuario();
        user.setUser_id(user_id.getUid());
        user.setUser_email(user_id.getEmail());
        user.setUser_nome(txtName.getText().toString());
        hashMap.put(user_id.getUid(),user);
        this.setIntegrantes(hashMap);
        databaseReference.child("grupo").child(this.getGrp_id()).setValue(this);
    }

    public void excluir(DatabaseReference databaseReference, Grupo grupo){
        databaseReference.child("grupo").
                child(grupo.getGrp_id())
                .removeValue();
    }

    public void alterar(DatabaseReference databaseReference, Grupo grupo, String nome){
        grupo.setGrp_nome(nome);
        databaseReference.child("grupo").
                child(grupo.getGrp_id()).
                setValue(grupo);
    }

    public String getGrp_id() {
        return grp_id;
    }

    public void setGrp_id(String grp_id) {
        this.grp_id = grp_id;
    }

    public String getGrp_nome() {
        return grp_nome;
    }

    public void setGrp_nome(String grp_nome) {
        this.grp_nome = grp_nome;
    }

    public String getGrp_lider() {
        return grp_lider;
    }

    public void setGrp_lider(String grp_lider) {
        this.grp_lider = grp_lider;
    }

    public int getGrp_integrantes() {
        return grp_integrantes;
    }

    public void setGrp_integrantes(int grp_integrantes) {
        this.grp_integrantes = grp_integrantes;
    }

    public HashMap<String, Usuario> getIntegrantes() {
        return integrantes;
    }

    public void setIntegrantes(HashMap<String, Usuario> integrantes) {
        this.integrantes = integrantes;
    }

    public HashMap<String, TarefaGrupal> getTarefa_grupal() {
        return tarefa_grupal;
    }

    public void setTarefa_grupal(HashMap<String, TarefaGrupal> tarefa_grupal) {
        this.tarefa_grupal = tarefa_grupal;
    }
}
