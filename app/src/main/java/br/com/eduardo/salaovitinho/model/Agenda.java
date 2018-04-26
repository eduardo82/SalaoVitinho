package br.com.eduardo.salaovitinho.model;

/**
 * Created by eduardo.vasconcelos on 09/11/2017.
 */

public class Agenda {

    private String dia;
    private String primeiraHoraInicio;
    private String primeiraHoraFim;
    private String segundaHoraInicio;
    private String segundaHoraFim;
    private boolean cancelada;
    private boolean horaPadrao;

    public Agenda() {
        super();
    }

    public Agenda(String dia, String primeiraHoraInicio, String primeiraHoraFim, String segundaHoraInicio, String segundaHoraFim, boolean cancelada, boolean horaPadrao) {
        this.dia = dia;
        this.primeiraHoraInicio = primeiraHoraInicio;
        this.primeiraHoraFim = primeiraHoraFim;
        this.segundaHoraInicio = segundaHoraInicio;
        this.segundaHoraFim = segundaHoraFim;
        this.cancelada = cancelada;
        this.horaPadrao = horaPadrao;
    }

    public String getDia() {
        return dia;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }

    public boolean getCancelada() {
        return cancelada;
    }

    public void setCancelada(boolean cancelada) {
        this.cancelada = cancelada;
    }

    public boolean getHoraPadrao() {
        return horaPadrao;
    }

    public void setHoraPadrao(boolean horaPadrao) {
        this.horaPadrao = horaPadrao;
    }

    public String getPrimeiraHoraInicio() {
        return primeiraHoraInicio;
    }

    public void setPrimeiraHoraInicio(String primeiraHoraInicio) {
        this.primeiraHoraInicio = primeiraHoraInicio;
    }

    public String getPrimeiraHoraFim() {
        return primeiraHoraFim;
    }

    public void setPrimeiraHoraFim(String primeiraHoraFim) {
        this.primeiraHoraFim = primeiraHoraFim;
    }

    public String getSegundaHoraInicio() {
        return segundaHoraInicio;
    }

    public void setSegundaHoraInicio(String segundaHoraInicio) {
        this.segundaHoraInicio = segundaHoraInicio;
    }

    public String getSegundaHoraFim() {
        return segundaHoraFim;
    }

    public void setSegundaHoraFim(String segundaHoraFim) {
        this.segundaHoraFim = segundaHoraFim;
    }
}
