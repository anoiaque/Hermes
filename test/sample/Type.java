package sample;

import java.util.Set;

import core.Hermes;

public class Type extends Hermes {

	private char			car;
	private Character	kcar;
	private byte			octet;
	private Byte			koctet;
	private short			court;
	private Short			kcourt;
	private int				entier;
	private Integer		kentier;
	private long			longue;
	private Long			klongue;
	private float			reel;
	private Float			kreel;
	private double		bigreal;
	private Double		kbigreal;
	private String		str;

	private Address		adress;
	private Set<Car>	cars;

	protected void Associations() {
		hasOne("adress", "dependent:destroy");
		hasMany("cars");
	}

	// Getters & Setters
	public char getCar() {
		return car;
	}

	public void setCar(char car) {
		this.car = car;
	}

	public Character getKcar() {
		return kcar;
	}

	public void setKcar(Character kcar) {
		this.kcar = kcar;
	}

	public byte getOctet() {
		return octet;
	}

	public void setOctet(byte octet) {
		this.octet = octet;
	}

	public Byte getKoctet() {
		return koctet;
	}

	public void setKoctet(Byte koctet) {
		this.koctet = koctet;
	}

	public short getCourt() {
		return court;
	}

	public void setCourt(short court) {
		this.court = court;
	}

	public Short getKcourt() {
		return kcourt;
	}

	public void setKcourt(Short kcourt) {
		this.kcourt = kcourt;
	}

	public int getEntier() {
		return entier;
	}

	public void setEntier(int entier) {
		this.entier = entier;
	}

	public Integer getKentier() {
		return kentier;
	}

	public void setKentier(Integer kentier) {
		this.kentier = kentier;
	}

	public long getLongue() {
		return longue;
	}

	public void setLongue(long longue) {
		this.longue = longue;
	}

	public Long getKlongue() {
		return klongue;
	}

	public void setKlongue(Long klongue) {
		this.klongue = klongue;
	}

	public float getReel() {
		return reel;
	}

	public void setReel(float reel) {
		this.reel = reel;
	}

	public Float getKreel() {
		return kreel;
	}

	public void setKreel(Float kreel) {
		this.kreel = kreel;
	}

	public double getBigreal() {
		return bigreal;
	}

	public void setBigreal(double bigreal) {
		this.bigreal = bigreal;
	}

	public Double getKbigreal() {
		return kbigreal;
	}

	public void setKbigreal(Double kbigreal) {
		this.kbigreal = kbigreal;
	}

	public String getStr() {
		return str;
	}

	public void setStr(String str) {
		this.str = str;
	}

	public Set<Car> getCars() {
		return cars;
	}

	public void setCars(Set<Car> cars) {
		this.cars = cars;
	}

	public void setAdress(Address adress) {
		this.adress = adress;
	}

	public Address getAdress() {
		return adress;
	}

}
