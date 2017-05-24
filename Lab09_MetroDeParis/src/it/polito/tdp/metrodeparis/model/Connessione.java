package it.polito.tdp.metrodeparis.model;

public class Connessione {

	private int id;
	private Linea linea;
	private Fermata fermataPartenza;
	private Fermata fermataArrivo;

	public Connessione(int id_connessione, Linea l, Fermata partenza, Fermata arrivo) {
		this.id= id_connessione;
		this.linea= l;
		this.fermataPartenza= partenza;
		this.fermataArrivo= arrivo;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Linea getLinea() {
		return linea;
	}

	public void setLinea(Linea linea) {
		this.linea = linea;
	}

	public Fermata getFermataPartenza() {
		return fermataPartenza;
	}

	public void setFermataPartenza(Fermata fermataPartenza) {
		this.fermataPartenza = fermataPartenza;
	}

	public Fermata getFermataArrivo() {
		return fermataArrivo;
	}

	public void setFermataArrivo(Fermata fermataArrivo) {
		this.fermataArrivo = fermataArrivo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
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
		Connessione other = (Connessione) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	

}
