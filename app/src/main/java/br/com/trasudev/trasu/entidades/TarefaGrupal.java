package br.com.trasudev.trasu.entidades;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class TarefaGrupal {
    private String tar_id;
    private String tar_nome;
    private String tar_descricao;
    private String tar_prioridade;
    private String tar_dataInicial;
    private String tar_dataFinal;
    private int tar_prazo;
    private String tar_id_grp;
    private int tar_status;
    private int tar_notificacao;

    public TarefaGrupal(){

    }

    public void cadastrar(DatabaseReference databaseReference, String nome, String descricao,
                          String prioridade, int prazo, String idGrupo, int notificacao){
        this.setTar_id(UUID.randomUUID().toString());
        this.setTar_nome(nome);
        this.setTar_descricao(descricao);
        this.setTar_prioridade(prioridade);
        //region Data
        Date data = new Date();
        this.setTar_dataInicial(dataTransformer(data));
        this.setTar_dataFinal(somarData(prazo,data));
        //endregion
        this.setTar_prazo(prazo);
        this.setTar_id_grp(idGrupo);
        this.setTar_status(0);
        this.setTar_notificacao(notificacao);
        databaseReference.child("grupo").child(idGrupo).
                child("tarefa_grupal").child(this.getTar_id()).setValue(this);
    }

    public void excluir(DatabaseReference databaseReference, TarefaGrupal tarefa, Grupo grupo){
        databaseReference.child("grupo").child(grupo.getGrp_id()).
                child("tarefa_grupal").child(tarefa.getTar_id())
                .removeValue();
    }

    public void alterar(DatabaseReference databaseReference, TarefaGrupal tarefa,
                        String nome, String descricao, String prioridade, int prazo,
                        int notificacao, Grupo grupo){
        tarefa.setTar_nome(nome);
        tarefa.setTar_descricao(descricao);
        tarefa.setTar_prioridade(prioridade);
        //region Data
        SimpleDateFormat formato = new SimpleDateFormat("dd-MM-yyyy HH");
        Date data = new Date();
        try {
            data = formato.parse(tarefa.getTar_dataInicial());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        tarefa.setTar_dataFinal(somarData(prazo,data));
        //endregion
        tarefa.setTar_prazo(prazo);
        tarefa.setTar_notificacao(notificacao);
        databaseReference.child("grupo").child(grupo.getGrp_id()).
                child("tarefa_grupal").child(tarefa.getTar_id()).
                setValue(tarefa);
    }


    public String getTar_id() {
        return tar_id;
    }

    public void setTar_id(String tar_id) {
        this.tar_id = tar_id;
    }

    public String getTar_nome() {
        return tar_nome;
    }

    public void setTar_nome(String tar_nome) {
        this.tar_nome = tar_nome;
    }

    public String getTar_descricao() {
        return tar_descricao;
    }

    public void setTar_descricao(String tar_descricao) {
        this.tar_descricao = tar_descricao;
    }

    public String getTar_prioridade() {
        return tar_prioridade;
    }

    public void setTar_prioridade(String tar_prioridade) {
        this.tar_prioridade = tar_prioridade;
    }

    public String getTar_dataInicial() {
        return tar_dataInicial;
    }

    public void setTar_dataInicial(String tar_dataInicial) {
        this.tar_dataInicial = tar_dataInicial;
    }

    public String getTar_dataFinal() {
        return tar_dataFinal;
    }

    public void setTar_dataFinal(String tar_dataFinal) {
        this.tar_dataFinal = tar_dataFinal;
    }

    public int getTar_prazo() {
        return tar_prazo;
    }

    public void setTar_prazo(int tar_prazo) {
        this.tar_prazo = tar_prazo;
    }

    public String getTar_id_grp() {
        return tar_id_grp;
    }

    public void setTar_id_grp(String tar_id_grp) {
        this.tar_id_grp = tar_id_grp;
    }

    public int getTar_status() {
        return tar_status;
    }

    public void setTar_status(int tar_status) {
        this.tar_status = tar_status;
    }

    public int getTar_notificacao() {
        return tar_notificacao;
    }

    public void setTar_notificacao(int tar_notificacao) {
        this.tar_notificacao = tar_notificacao;
    }

    public static String somarData (int dias,Date data) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(data);
        calendar.add(Calendar.DATE, dias);
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH");
        return format.format(calendar.getTime());
    }

    public static String dataTransformer(Date data){
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH");
        return format.format(data.getTime());
    }
}
