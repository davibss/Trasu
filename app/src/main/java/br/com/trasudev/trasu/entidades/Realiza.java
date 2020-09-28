package br.com.trasudev.trasu.entidades;

import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;

public class Realiza implements Serializable{
    private String rea_id;
    private String rea_user_id;
    private int rea_status;

    public Realiza(){

    }

    public void cadastrar(DatabaseReference databaseReference, Grupo grupo, TarefaGrupal tarefaGrupal,
                          Usuario usuario){
        Realiza realiza = new Realiza();
        realiza.setRea_id(usuario.getUser_id());
        realiza.setRea_user_id(usuario.getUser_id());
        realiza.setRea_status(0);
        databaseReference.child("grupo").child(grupo.getGrp_id()).
                child("tarefa_grupal").child(tarefaGrupal.getTar_id()).
                child("realiza").child(realiza.getRea_id()).setValue(realiza);
    }

    public void remover(DatabaseReference databaseReference, Grupo grupo, TarefaGrupal tarefaGrupal,
                        Usuario usuario){
        databaseReference.child("grupo").child(grupo.getGrp_id()).
                child("tarefa_grupal").child(tarefaGrupal.getTar_id())
                .child("realiza").child(usuario.getUser_id())
                .removeValue();
    }

    public String getRea_id() {
        return rea_id;
    }

    public void setRea_id(String rea_id) {
        this.rea_id = rea_id;
    }

    public String getRea_user_id() {
        return rea_user_id;
    }

    public void setRea_user_id(String rea_user_id) {
        this.rea_user_id = rea_user_id;
    }

    public int getRea_status() {
        return rea_status;
    }

    public void setRea_status(int rea_status) {
        this.rea_status = rea_status;
    }
}
