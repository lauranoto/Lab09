package it.polito.tdp.metrodeparis.model;

import java.util.List;
import java.util.Map;

import org.jgrapht.Graphs;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DirectedWeightedMultigraph;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

import it.polito.tdp.metrodeparis.dao.MetroDAO;

public class Model {

	private MetroDAO dao;
	private DirectedWeightedMultigraph<FermataSuLinea, Arco> grafo;
	private Map<Integer, Fermata> fermate;
	private Map<Integer, Linea> linee;
	private List<Connessione> connessioni;
	private List<FermataSuLinea> fermateSuLinea;

	private List<Arco> pathEdgeList;
	private double pathTempoTotale = 0.0;

	public Model() {
		this.dao = new MetroDAO();
	}

	public Map<Integer, Fermata> getFermate() {

		if (this.fermate == null) {
			this.fermate = dao.getAllFermate();
		}
		return fermate;
	}

	public void creaGrafo() {

		this.grafo = new DirectedWeightedMultigraph<>(Arco.class);

		this.linee = dao.getAllLinee();
		this.connessioni = dao.getAllConnessioni(fermate, linee);
		this.fermateSuLinea = dao.getAllFermateSuLinea(fermate, linee);

		//aggiunta dei vertici
		Graphs.addAllVertices(grafo, fermateSuLinea);

		//aggiunta archi tra fermate diverse
		for (Connessione c : connessioni) {

			Map<String, FermataSuLinea> fermateSuLineaDataFermata = c.getFermataPartenza().getFermateSuLinea();
			FermataSuLinea fslPartenza = fermateSuLineaDataFermata
					.get(new String("" + c.getFermataPartenza().getIdFermata() + ", " + c.getLinea().getId_linea()));

			fermateSuLineaDataFermata = c.getFermataArrivo().getFermateSuLinea();
			FermataSuLinea fslArrivo = fermateSuLineaDataFermata
					.get(new String("" + c.getFermataArrivo().getIdFermata() + ", " + c.getLinea().getId_linea()));

			if (fslPartenza != null && fslArrivo != null) {
				Arco e = grafo.addEdge(fslPartenza, fslArrivo);
				if (e != null) {
					grafo.setEdgeWeight(e, this.getPeso(c));
					e.setLinea(c.getLinea());
				}
			}
		}

		//aggiunta archi sulla stessa fermata 
		for (Fermata fermata : fermate.values()) {
			for (FermataSuLinea fslP : fermata.getFermateSuLinea().values()) {
				for (FermataSuLinea fslA : fermata.getFermateSuLinea().values()) {
					if (!fslP.equals(fslA)) {
						Arco e = grafo.addEdge(fslP, fslA);
						if (e != null) {
							Linea linea = fslA.getLinea();
							grafo.setEdgeWeight(e, linea.getIntervallo() * 60);
							e.setLinea(linea);
						}
					}
				}
			}
		}

	}

	private double getPeso(Connessione c) {

		double velocita = c.getLinea().getVelocita();
		LatLng f1 = new LatLng(c.getFermataPartenza().getCoords().getLatitude(),
				c.getFermataPartenza().getCoords().getLongitude());
		LatLng f2 = new LatLng(c.getFermataArrivo().getCoords().getLatitude(),
				c.getFermataArrivo().getCoords().getLongitude());
		double distanza = LatLngTool.distance(f1, f2, LengthUnit.KILOMETER);
		double tempo = (distanza / velocita) * 60 * 60;
		return tempo;
	}

	public String getPercorso() {

		String result = "";

		Linea lineaTemp = pathEdgeList.get(0).getLinea();
		result += "Prendo linea: " + lineaTemp.getNome() + "\n";

		for (Arco e : pathEdgeList) {
			result += grafo.getEdgeTarget(e).getNome();
			if (!e.getLinea().equals(lineaTemp)) {
				result += "\n\nCambio su linea: " + e.getLinea().getNome() + "\n";
				lineaTemp = e.getLinea();
			} else if (e == pathEdgeList.get(pathEdgeList.size()-1)) {
				result += "\n";
			} else {
				result += ", ";
			}
		}

		result += "\nTempo di percorrenza (in minuti): " + (pathTempoTotale / 60);
		return result;

	}

	public void calcolaPercorso(Fermata partenza, Fermata arrivo) {

		DijkstraShortestPath<FermataSuLinea, Arco> dijkstra;

		double pathTempoTotaleTemp;
		
		List<Arco> bestPathEdgeList = null;
		double bestPathTempoTotale = Double.MAX_VALUE;

		for (FermataSuLinea fslP : partenza.getFermateSuLinea().values()) {
			for (FermataSuLinea fslA : arrivo.getFermateSuLinea().values()) {
				dijkstra = new DijkstraShortestPath<FermataSuLinea, Arco>(grafo, fslP, fslA);

				pathTempoTotaleTemp = dijkstra.getPathLength();

				if (pathTempoTotaleTemp < bestPathTempoTotale) {
					bestPathTempoTotale = pathTempoTotaleTemp;
					bestPathEdgeList = dijkstra.getPathEdgeList();
				}
			}
		}

		pathEdgeList = bestPathEdgeList;
		pathTempoTotale = bestPathTempoTotale;

		if (pathEdgeList.size() - 1 > 0) {
			pathTempoTotale += (pathEdgeList.size() - 1) * 30;
		}
	}

	public DirectedWeightedMultigraph<FermataSuLinea, Arco> getGrafo() {

		if (this.grafo == null) {
			this.creaGrafo();
		}
		return grafo;
	}

}
