package dao;
import model.Contrat;
import model.Facture;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import utils.DBConnection;

public class FactureDAO {
	
	public void saveFacture(Facture facture) throws SQLException {

        String sql = "INSERT INTO Factures (ID_Contrat, Date_Facture, DateRetour, Nbr_Jours, Km_arrive, Km_parcouru, Montant_Total, id_utilisateur, nom_prenom_client) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql);
        stmt.setInt(1, facture.getID_Contrat());
        stmt.setString(2, facture.getDate_Facture());
        stmt.setString(3, facture.getDateRetour());
        stmt.setInt(4, facture.getNbresJours());
        stmt.setInt(5, facture.getKm_arrive());
        stmt.setInt(6, facture.getKm_parcouru());
        stmt.setDouble(7, facture.getMontantTotal());
        stmt.setInt(8, facture.getId_utilisateur());	
        stmt.setString(9, facture.getNomPrenomClient());
        stmt.executeUpdate();
        stmt.close();
    }
	
	public List<Facture> getFacturesByUser(int userId) throws SQLException {
	    List<Facture> factures = new ArrayList<>();
	    String sql = "SELECT * FROM Factures WHERE id_utilisateur = ?";
	    PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql);
	    stmt.setInt(1, userId);
	    ResultSet rs = stmt.executeQuery();

	    while (rs.next()) {
	        Facture facture = new Facture(
	        rs.getInt("ID_Facture"),
	        rs.getInt("ID_Contrat"),
	        rs.getString("nom_prenom_client"),
	        rs.getString("Date_Facture"),
	        rs.getString("DateRetour"),
	        rs.getInt("Nbr_Jours"),
	        rs.getInt("Km_arrive"),
	        rs.getInt("Km_parcouru"),
	        rs.getDouble("Montant_Total"),
	        rs.getInt("id_utilisateur")
	        );
	        factures.add(facture);
	    }

	    rs.close();
	    stmt.close();
	    return factures;
	}
	
	
	public boolean factureExistePourContrat(int idContrat, int idUtilisateur) {
	    String query = "SELECT COUNT(*) FROM Factures WHERE ID_Contrat = ? AND id_utilisateur = ?";

	    try (
	         PreparedStatement stmt = DBConnection.getConnection().prepareStatement(query)) {
	        
	        stmt.setInt(1, idContrat);
	        stmt.setInt(2, idUtilisateur);
	        ResultSet rs = stmt.executeQuery();

	        if (rs.next()) {
	            return rs.getInt(1) > 0;
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return false;
	}
	
	public Contrat getContratByIdFacture(int idFacture) {
	    String sql = "SELECT c.* FROM Contrats c " +
	                 "JOIN Factures f ON c.ID_contrat = f.ID_Contrat " +
	                 "WHERE f.ID_Facture = ?";
	    try (PreparedStatement pst = DBConnection.getConnection().prepareStatement(sql)) {
	        pst.setInt(1, idFacture);
	        ResultSet rs = pst.executeQuery();
	        if (rs.next()) {
	            return new Contrat(
	                rs.getInt("ID_contrat"),
	                rs.getInt("id_client"),
	                rs.getString("immatricule"),
	                rs.getInt("ID_Agence_Depart"),
	                rs.getString("dateDepart"),
	                rs.getString("DateRetourPrevu"),
	                rs.getInt("Forfait_journalier"),
	                rs.getInt("Km_depart"),
	                rs.getDouble("Km_tarif"),
	                rs.getInt("ID_Agence_Arrivee")
	            );
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null;
	}


}


