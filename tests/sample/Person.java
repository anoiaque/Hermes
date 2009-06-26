package sample;

import java.util.Set;

import core.Hermes;
import core.Relation;
import core.Relation.Cascade;

public class Person extends Hermes {

    private int      age;
    private String   nom;
    private Adress   adresse;
    private Set<Pet> pets;

    public Person() {
        hasOne("adresse",new Relation(Cascade.DELETE));
        manyToMany("pets",new Relation(Cascade.DELETE));
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setAdresse(Adress adresse) {
        this.adresse = adresse;
    }

    public Adress getAdresse() {
        return adresse;
    }

    public Set<Pet> getPets() {
        return pets;
    }

    public void setPets(Set<Pet> pets) {
        this.pets = pets;
    }
}
