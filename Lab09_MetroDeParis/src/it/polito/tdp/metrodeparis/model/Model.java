package it.polito.tdp.metrodeparis.model;

import java.util.List;
import java.util.Map;

import org.jgrapht.Graphs;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.jgrapht.graph.WeightedMultigraph;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

import it.polito.tdp.metrodeparis.dao.MetroDAO;

public class Model {

	private MetroDAO dao;
	private WeightedMultigraph<Fermata, DefaultWeightedEdge> grafo;
	private Map<Fermata, Fermata> fermate;
	private List<DefaultWeightedEdge> shortestPathEdgeList;
	private double shortestPathTempoTotale;

	public Model() {
		this.dao = new MetroDAO();
	}

	public Map<Fermata, Fermata> getFermate() {

		if (this.fermate == null) {
			this.fermate = dao.getAllFermate();
		}

		return fermate;
	}

	public void creaGrafo() {

		this.grafo = new WeightedMultigraph<>(DefaultWeightedEdge.class);

		// nodi del grafo
		Graphs.addAllVertices(grafo, this.getFermate().values());
		
		// archi del grafo
		for (Connessione c : dao.coppieFermateCollegate()) {
				DefaultWeightedEdge e = grafo.addEdge(c.getF1(), c.getF2());
				grafo.setEdgeWeight(e, this.getPeso(c));
			

		}

	}

	private double getPeso(Connessione c) {

		double velocita = dao.getVelocita(c);
		LatLng f1 = new LatLng(c.getF1().getCoords().getLatitude(), c.getF1().getCoords().getLongitude());
		LatLng f2 = new LatLng(c.getF2().getCoords().getLatitude(), c.getF2().getCoords().getLongitude());
		double distanza = LatLngTool.distance(f1, f2, LengthUnit.KILOMETER);
		double tempo = (distanza / velocita) * 60 * 60;
		return tempo;
	}

	public String getPercorso() {

		String result = "";

		for (DefaultWeightedEdge e : shortestPathEdgeList) {
			result += (this.getGrafo().getEdgeTarget(e).getNome()) + "\n";
		}

		result += "Tempo di percorrenza (in minuti): " + (shortestPathTempoTotale/60);
		return result;

	}

	public void calcolaPercorso(Fermata partenza, Fermata arrivo) {

		DijkstraShortestPath<Fermata, DefaultWeightedEdge> d = new DijkstraShortestPath<Fermata, DefaultWeightedEdge>(
				this.getGrafo(), partenza, arrivo);

		shortestPathEdgeList = d.getPathEdgeList();
		System.out.println(shortestPathEdgeList);
		shortestPathTempoTotale = d.getPathLength();

		if (shortestPathEdgeList.size() - 1 > 0) {
			shortestPathTempoTotale += (shortestPathEdgeList.size() - 1) * 30;
		}
	}

	private WeightedMultigraph<Fermata, DefaultWeightedEdge> getGrafo() {

		if (this.grafo == null) {
			this.creaGrafo();
		}
		return grafo;
	}

}
