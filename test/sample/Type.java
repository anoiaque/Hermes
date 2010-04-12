package sample;

import java.util.Set;

import core.Hermes;

public class Type extends Hermes {

	char			car;
	Character	kcar;
	byte			octet;
	Byte			koctet;
	short			court;
	Short			kcourt;
	int				entier;
	Integer		kentier;
	long			longue;
	Long			klongue;
	float			reel;
	Float			kreel;
	double		bigreal;
	Double		kbigreal;
	String		str;
	
	Adress adresse;
  Set<Car> cars;
	
	public Type() {
    hasOne("adresse");
    hasMany("cars");
  }

   
}
