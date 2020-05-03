package it.unimib.bicap.service;

import android.net.Uri;

import java.util.ArrayList;

public class Progetto {

    private String nomeProgetto;
    private String desctizioneProgetto;
    private String autoreProgetto;
    private int completato;
    private int numeroPassi;
    public ArrayList<Passo> passi;

    public Progetto(String nomeProgetto, String descrizioneProgetto, String autoreProgetto, int numeroPassi) {
        this.nomeProgetto = nomeProgetto;
        this.desctizioneProgetto = descrizioneProgetto;
        this.autoreProgetto = autoreProgetto;
        this.completato = 0;
        this.numeroPassi = numeroPassi;
        passi = new ArrayList<Passo>();
    }

    public Progetto(){
        this.passi = new ArrayList<Passo>();
    }


    public String getNomeProgetto() {
        return nomeProgetto;
    }

    public String getDesctizioneProgetto() {
        return desctizioneProgetto;
    }

    public String getAutoreProgetto() {
        return autoreProgetto;
    }

    public int getCompletato() {
        return completato;
    }

    public int getNumeroPassi() {
        return numeroPassi;
    }

    public void setNomeProgetto(String nomeProgetto) {
        this.nomeProgetto = nomeProgetto;
    }

    public void setDesctizioneProgetto(String desctizioneProgetto) {
        this.desctizioneProgetto = desctizioneProgetto;
    }

    public void setAutoreProgetto(String autoreProgetto) {
        this.autoreProgetto = autoreProgetto;
    }

    public void setCompletato(int completato) {
        this.completato = completato;
    }

    public void setNumeroPassi(int numeroPassi) {
        this.numeroPassi = numeroPassi;
    }

    public static class Passo {
        private String tipo;
        private Uri link;
        private int completato;

        public Passo(String tipo, Uri link) {
            this.tipo = tipo;
            this.link = link;
            this.completato = 0;
        }

        public Passo(String tipo, Uri link, int completato) {
            this.tipo = tipo;
            this.link = link;
            this.completato = completato;
        }

        public String getTipo() {
            return tipo;
        }

        public void setTipo(String tipo) {
            this.tipo = tipo;
        }

        public Uri getLink() {
            return link;
        }

        public void setLink(Uri link) {
            this.link = link;
        }

        public int getCompletato() {
            return completato;
        }

        public void setCompletato(int completato) {
            this.completato = completato;
        }
    }
}
