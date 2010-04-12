package sample;

import core.Hermes;

public class Personne extends Hermes {

	private String	name;

	public Personne() {
		setTableName("personnel");
	}

	public void Validations() {
		validatePresenceOf("name", "name must be present yes!");
	}

	public String getName() {
		return name;
	}

	public void setName(String nom) {
		this.name = nom;
	}
}
