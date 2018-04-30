package br.com.trasudev.trasu.entidades;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.Date;
import java.util.UUID;

public class Grupo {
    private String grp_id;
    private String grp_nome;
    private String grp_lider;
    private int grp_integrantes;

    public Grupo(){

    }

    public void cadastrar(DatabaseReference databaseReference, String nome, String userUUID){
        this.setGrp_id(UUID.randomUUID().toString());
        this.setGrp_nome(nome);
        this.setGrp_lider(userUUID);
        this.setGrp_integrantes(1);
        databaseReference.child("grupo").child(this.getGrp_id()).setValue(this);
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
}