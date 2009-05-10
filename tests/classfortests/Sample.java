package classfortests;

import datarecord.DataRecord;

public class Sample extends DataRecord {

	private int number = 0;
	private String chaine = "";

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

}
