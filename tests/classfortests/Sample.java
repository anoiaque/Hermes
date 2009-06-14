package classfortests;

import hermes.Hermes;

public class Sample extends Hermes {

	private int number = 0;
	private String chaine = "";
	private  Adress adresse;

	
	
	public Sample(){
	    hasOne("adresse");
	}
	
	
	public void setNumber(int number) {
		this.number = number;
	}

	public int getNumber() {
		return number;
	}

	public void setChaine(String chaine) {
		this.chaine = chaine;
	}

	public String getChaine() {
		return chaine;
	}

    public void setAdresse(Adress adresse) {
        this.adresse = adresse;
    }

    public Adress getAdresse() {
        return adresse;
    }

}
