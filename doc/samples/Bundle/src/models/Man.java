package models;

public class Man extends Person {

	private String	job;
	private boolean	married;

	public Man() {
	}

	public Man(String name, int age, Address address) {
		this.setName(name);
		this.setAge(age);
		this.setAdress(address);
	}

	public void setJob(String job) {
		this.job = job;
	}

	public String getJob() {
		return job;
	}

	public void setMarried(boolean married) {
		this.married = married;
	}

	public boolean isMarried() {
		return married;
	}

}
