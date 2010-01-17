package sample;

import java.util.Set;

import core.Hermes;

public class Person extends Hermes {

  private int age;
  private String nom;
  private Address adresse;
  private Set<Pet> pets;
  private Set<Car> cars;

  public Person() {
    hasOne("adresse", "dependent:destroy");
    manyToMany("pets");
    hasMany("cars");
  }

  public Set<Car> getCars() {
    return cars;
  }

  public void setCars(Set<Car> cars) {
    this.cars = cars;
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

  public void setAdresse(Address adresse) {
    this.adresse = adresse;
  }

  public Address getAdresse() {
    return adresse;
  }

  public Set<Pet> getPets() {
    return pets;
  }

  public void setPets(Set<Pet> pets) {
    this.pets = pets;
  }
}
