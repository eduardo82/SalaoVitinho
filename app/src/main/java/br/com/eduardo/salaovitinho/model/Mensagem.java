package br.com.eduardo.salaovitinho.model;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

/**
 * Created by Eduardo on 30/11/2017.
 */

public class Mensagem implements Serializable {

    @Exclude
    private Long id;
    private String remetente;
    private String mensagem;
    private String telefone;
    private Boolean lido;
    private String resposta;

    public Mensagem() {
        super();
    }

    public Mensagem(String remetente, String mensagem, String telefone) {
        this.remetente = remetente;
        this.mensagem = mensagem;
        this.telefone = telefone;
    }

    public Mensagem(String remetente, String mensagem, String telefone, Boolean lido, String resposta) {
        this.remetente = remetente;
        this.mensagem = mensagem;
        this.telefone = telefone;
        this.lido = lido;
        this.resposta = resposta;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean isLido() {
        return lido;
    }

    public void setLido(Boolean lido) {
        this.lido = lido;
    }

    public String getResposta() {
        return resposta;
    }

    public void setResposta(String resposta) {
        this.resposta = resposta;
    }

    public String getRemetente() {
        return remetente;
    }

    public void setRemetente(String remetente) {
        this.remetente = remetente;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    @Override
    public boolean equals(Object o2) {
        return this.getTelefone().equals(((Mensagem) o2).getTelefone());
    }
}
