package br.com.trasudev.trasu.entidades;

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
}
