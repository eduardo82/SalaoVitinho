package br.com.eduardo.salaovitinho.model;

public class Telefone {
    private String nome;
    private String numero;
    private Boolean autorizado;
    private Boolean novo;

    public Telefone() {
        super();
    }

    public Telefone(String nome, String numero, Boolean autorizado, Boolean novo) {
        this.nome = nome;
        this.numero = numero;
        this.autorizado = autorizado;
        this.novo = novo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Boolean getAutorizado() {
        return autorizado;
    }

    public Boolean getNovo() {
        return novo;
    }

    public void setNovo(Boolean novo) {
        this.novo = novo;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public void setAutorizado(Boolean autorizado) {
        this.autorizado = autorizado;
    }
}
