package sample;

import core.Hermes;

public class Adress extends Hermes {
    private int    numero;
    private String rue;

    public Adress(){
        
    }
    public Adress(int num, String rue) {
        this.numero = num;
        this.rue = rue;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getRue() {
        return rue;
    }

    public void setRue(String rue) {
        this.rue = rue;
    }
}
