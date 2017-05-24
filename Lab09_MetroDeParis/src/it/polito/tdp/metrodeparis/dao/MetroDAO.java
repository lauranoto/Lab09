package it.polito.tdp.metrodeparis.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.javadocmd.simplelatlng.LatLng;

import it.polito.tdp.metrodeparis.model.Connessione;
import it.polito.tdp.metrodeparis.model.Fermata;
import it.polito.tdp.metrodeparis.model.FermataSuLinea;
import it.polito.tdp.metrodeparis.model.Linea;

public class MetroDAO {

	public Map<Integer, Fermata> getAllFermate() {

		final String sql = "SELECT id_fermata, nome, coordx, coordy FROM fermata ORDER BY nome ASC";
		Map<Integer, Fermata> fermate = new HashMap<>();

		try {
			Connection conn = DBConnect.getInstance().getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				int idFermata = rs.getInt("id_fermata");
				Fermata f = new Fermata(idFermata, rs.getString("nome"), new LatLng(rs.getDouble("coordx"), rs.getDouble("coordy")));
				fermate.put(idFermata, f);
			}

			st.close();
			conn.close();
			return fermate;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}

	}

	public Map<Integer, Linea> getAllLinee() {
		
		final String sql = "SELECT id_linea, nome, velocita, intervallo FROM linea";
		Map<Integer, Linea> linee = new HashMap<>();

		try {
			Connection conn = DBConnect.getInstance().getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				int idLinea = rs.getInt("id_linea");
				Linea l = new Linea(idLinea, rs.getString("nome"), rs.getDouble("velocita"), rs.getDouble("intervallo"));
				linee.put(idLinea, l);
			}

			st.close();
			conn.close();
			return linee;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<Connessione> getAllConnessioni(Map<Integer, Fermata> fermate, Map<Integer, Linea> linee) {
		
		final String sql = "SELECT * FROM connessione";
		List<Connessione> connessioni = new LinkedList<>();

		try {
			Connection conn = DBConnect.getInstance().getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
			
				int idLinea = rs.getInt("id_linea");
				int idStazP = rs.getInt("id_stazP");
				int idStazA = rs.getInt("id_stazA");

				Linea l = linee.get(idLinea);
				Fermata partenza = fermate.get(idStazP);
				Fermata arrivo = fermate.get(idStazA);
				
				Connessione c = new Connessione(rs.getInt("id_connessione"), l, partenza, arrivo);
				connessioni.add(c);
			}

			st.close();
			conn.close();
			return connessioni;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<FermataSuLinea> getAllFermateSuLinea(Map<Integer, Fermata> fermate, Map<Integer, Linea> linee) {
		
		final String sql = "SELECT DISTINCT fermata.id_fermata, linea.id_linea FROM fermata, linea, connessione WHERE (fermata.id_fermata = connessione.id_stazP OR fermata.id_fermata = connessione.id_stazA) AND connessione.id_linea = linea.id_linea";
		List<FermataSuLinea> fermateSuLinea = new ArrayList<FermataSuLinea>();

		try {
			Connection conn = DBConnect.getInstance().getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {

				int idLinea = rs.getInt("id_linea");
				int idFermata = rs.getInt("id_fermata");

				Linea linea = linee.get(idLinea);
				Fermata fermata = fermate.get(idFermata);

				FermataSuLinea fermataSuLinea = new FermataSuLinea(fermata, linea);
				fermata.addFermataSuLinea(fermataSuLinea);
				fermateSuLinea.add(fermataSuLinea);
			}

			st.close();
			conn.close();
			return fermateSuLinea;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}

	
	}
}
