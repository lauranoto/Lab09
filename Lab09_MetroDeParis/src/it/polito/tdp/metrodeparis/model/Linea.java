package it.polito.tdp.metrodeparis.model;

public class Linea {

	private int id_linea;
	private String nome;
	private double velocita;
	private double intervallo;

	public Linea(int id_linea, String nome, double velocita, double intervallo) {
		this.id_linea = id_linea;
		this.nome = nome;
		this.velocita = velocita;
		this.intervallo = intervallo;
	}

	public int getId_linea() {
		return id_linea;
	}

	public void setId_linea(int id_linea) {
		this.id_linea = id_linea;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public double getVelocita() {
		return velocita;
	}

	public void setVelocita(double velocita) {
		this.velocita = velocita;
	}

	public double getIntervallo() {
		return intervallo;
	}

	public void setIntervallo(double intervallo) {
		this.intervallo = intervallo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id_linea;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Linea other = (Linea) obj;
		if (id_linea != other.id_linea)
			return false;
		return true;
	}

	
	
}
