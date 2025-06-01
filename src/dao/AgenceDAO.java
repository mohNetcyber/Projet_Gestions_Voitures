package dao;
import model.Agence;
import utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AgenceDAO {
    // Ajouter une agence
    public boolean ajouterAgence(Agence agence) {
        String sql = "INSERT INTO Agences (nomAgence, adresse, ville, telephone, email) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pst = DBConnection.getConnection().prepareStatement(sql)) {
            pst.setString(1, agence.getNomAgence());
            pst.setString(2, agence.getAdresse());
            pst.setString(3, agence.getVille());
            pst.setString(4, agence.getTelephone());
            pst.setString(5, agence.getEmail());

            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Récupérer toutes les agences
    public List<Agence> getAllAgences() {
        List<Agence> agences = new ArrayList<>();
        String query = "SELECT * FROM Agences";

        try (PreparedStatement pst = DBConnection.getConnection().prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                Agence agence = new Agence(
                    rs.getInt("ID_Agence"),
                    rs.getString("nomAgence"),
                    rs.getString("adresse"),
                    rs.getString("ville"),
                    rs.getString("telephone"),
                    rs.getString("email")
                );
                agences.add(agence);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return agences;
    }

    // Supprimer une agence
    public boolean supprimerAgence(int idAgence) {
        String sql = "DELETE FROM Agences WHERE ID_Agence = ?";
        String sqlcontrat = "DELETE FROM Contrats WHERE ID_Agence_Depart = ?";
        String sqlvehicule = "DELETE FROM Vehicules WHERE id_agence = ?";
        try (
        	PreparedStatement pst = DBConnection.getConnection().prepareStatement(sql);
        	PreparedStatement pstcontrat = DBConnection.getConnection().prepareStatement(sqlcontrat);
        	PreparedStatement pstvehicule = DBConnection.getConnection().prepareStatement(sqlvehicule)) {
        	pstcontrat.setInt(1, idAgence);
            pst.setInt(1, idAgence);
            pstvehicule.setInt(1, idAgence);
            pstcontrat.executeUpdate();
            pstvehicule.executeUpdate();
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateAgence(Agence agence) {
        String sql = "UPDATE Agences SET nomAgence=?, adresse=?, ville=?, telephone=?, email=? WHERE ID_Agence=?";

        try (PreparedStatement pst = DBConnection.getConnection().prepareStatement(sql)) {
            pst.setString(1, agence.getNomAgence());
            pst.setString(2, agence.getAdresse());
            pst.setString(3, agence.getVille());
            pst.setString(4, agence.getTelephone());
            pst.setString(5, agence.getEmail());
            pst.setInt(6, agence.getIdAgence()); // ID de l'agence à mettre à jour

            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public Agence getAgenceById(int idagence) {
    	String sql = "SELECT * FROM Agences WHERE ID_Agence = ?";
    	try (PreparedStatement pst = DBConnection.getConnection().prepareStatement(sql)) {
			pst.setInt(1, idagence);
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				return new Agence(
					rs.getInt("ID_Agence"),
					rs.getString("nomAgence"),
					rs.getString("adresse"),
					rs.getString("ville"),
					rs.getString("telephone"),
					rs.getString("email")
				);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
    }
    
    public Agence getAgenceByIdVehicule(String immatriculation) {
        String sql = "SELECT a.* FROM Agences a " +
                     "JOIN Vehicules v ON v.id_agence = a.ID_Agence " +
                     "WHERE v.immatriculation = ?";

        try (PreparedStatement pst = DBConnection.getConnection().prepareStatement(sql)) {
            pst.setString(1, immatriculation);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                return new Agence(
                    rs.getInt("ID_Agence"),
                    rs.getString("nomAgence"),
                    rs.getString("adresse"),
                    rs.getString("ville"),
                    rs.getString("telephone"),
                    rs.getString("email")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    
}
