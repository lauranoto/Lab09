package it.polito.tdp.metrodeparis.model;

import org.jgrapht.graph.DefaultWeightedEdge;

public class Arco extends DefaultWeightedEdge {

	private static final long serialVersionUID = 1L;

	private Linea linea;

	public Arco() {

	}

	public Arco(Linea linea) {
		this.linea = linea;
	}

	public Linea getLinea() {
		return this.linea;
	}

	public void setLinea(Linea linea) {
		this.linea = linea;
	}
}
