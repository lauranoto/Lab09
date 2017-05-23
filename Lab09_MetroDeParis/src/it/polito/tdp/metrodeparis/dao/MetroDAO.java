package it.polito.tdp.metrodeparis.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.javadocmd.simplelatlng.LatLng;

import it.polito.tdp.metrodeparis.model.Connessione;
import it.polito.tdp.metrodeparis.model.Fermata;

public class MetroDAO {

	public Map<Fermata, Fermata> getAllFermate() {

		final String sql = "SELECT id_fermata, nome, coordx, coordy FROM fermata ORDER BY nome ASC";
		Map<Fermata, Fermata> fermate = new HashMap<>();

		try {
			Connection conn = DBConnect.getInstance().getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Fermata f = new Fermata(rs.getInt("id_Fermata"), rs.getString("nome"),
						new LatLng(rs.getDouble("coordx"), rs.getDouble("coordy")));
				fermate.put(f, f);
			}

			st.close();
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Errore di connessione al Database.");
		}

		return fermate;
	}

	public List<Connessione> coppieFermateCollegate() {

		final String sql = "SELECT f1.id_fermata as id1, f1.nome as nome1, f1.coordx as coordx1, f1.coordy as coordy1, "
				+ "f2.id_fermata as id2, f2.nome as nome2, f2.coordx as coordx2, f2.coordy as coordy2 "
				+ "FROM connessione, fermata f1, fermata f2 " + "WHERE connessione.id_stazP=f1.id_fermata "
				+ "AND connessione.id_stazA = f2.id_fermata";

		List<Connessione> collegate = new ArrayList<>();

		try {
			Connection conn = DBConnect.getInstance().getConnection();
			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Fermata f1 = new Fermata(rs.getInt("id1"), rs.getString("nome1"),
						new LatLng(rs.getDouble("coordx1"), rs.getDouble("coordy1")));
				Fermata f2 = new Fermata(rs.getInt("id2"), rs.getString("nome2"),
						new LatLng(rs.getDouble("coordx2"), rs.getDouble("coordy2")));
				Connessione c = new Connessione(f1, f2);
				collegate.add(c);

			}

			rs.close();
			conn.close();
			return collegate;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public double getVelocita(Connessione c) {

		final String sql = "SELECT linea.velocita as velocita FROM linea, connessione "
				+ "WHERE connessione.id_linea = linea.id_linea "
				+ "AND connessione.id_stazP=? AND connessione.id_stazA=?";

		double velocita = 0.0;

		try {
			Connection conn = DBConnect.getInstance().getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			
			st.setInt(1, c.getF1().getIdFermata());
			st.setInt(2, c.getF2().getIdFermata());

			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				velocita = rs.getDouble("velocita");
			}

			rs.close();
			conn.close();
			return velocita;

		} catch (SQLException e) {
			e.printStackTrace();
			return 0.0;
		}

	}

}
