package br.com.eduardo.salaovitinho.model;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by eduardo.vasconcelos on 25/10/2017.
 */

public class Horario implements Serializable, Comparable<Horario> {

    private String horaAtendimento;
    private String diaAtendimento;
    private String nome;
    private String telefone;
    private boolean disponivel;
    private boolean autorizado;
    private boolean recusado;
    private boolean verificado;
    private String motivo;
    private String profissional;

    public String getProfissional() {
        return profissional;
    }

    public void setProfissional(String profissional) {
        this.profissional = profissional;
    }

    public boolean isVerificado() {
        return verificado;
    }

    public void setVerificado(boolean verificado) {
        this.verificado = verificado;
    }

    public String getHoraAtendimento() {
        return horaAtendimento;
    }

    public void setHoraAtendimento(String horaAtendimento) {
        this.horaAtendimento = horaAtendimento;
    }

    public String getDiaAtendimento() {
        return diaAtendimento;
    }

    public void setDiaAtendimento(String diaAtendimento) {
        this.diaAtendimento = diaAtendimento;
    }

    public Horario() {
        super();
    }

    public Horario(String nome, String telefone, String diaAtendimento, String horaAtendimento, boolean disponivel, boolean autorizado, boolean recusado, boolean verificado) {
        this.diaAtendimento = diaAtendimento;
        this.telefone = telefone;
        this.nome = nome;
        this.disponivel = disponivel;
        this.autorizado = autorizado;
        this.horaAtendimento = horaAtendimento;
        this.recusado = recusado;
        this.verificado = verificado;
    }



    public boolean isRecusado() {
        return recusado;
    }

    public void setRecusado(boolean recusado) {
        this.recusado = recusado;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public boolean isAutorizado() {
        return autorizado;
    }

    public void setAutorizado(boolean autorizado) {
        this.autorizado = autorizado;
    }

    public boolean isDisponivel() {
        return disponivel;
    }

    public void setDisponivel(boolean disponivel) {
        this.disponivel = disponivel;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public int compareTo(@NonNull Horario o) {
        int sComp = this.getDiaAtendimento().compareTo(o.getDiaAtendimento());

        if (sComp != 0) {
            return sComp;
        } else {
            return this.getHoraAtendimento().compareTo(o.getHoraAtendimento());
        }

    }

    @Override
    public boolean equals(Object h) {
        return this.getDiaAtendimento().equals(((Horario)h).getDiaAtendimento()) &&
            this.getHoraAtendimento().equals(((Horario)h).getHoraAtendimento());
    }
}