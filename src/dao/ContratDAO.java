package dao;
import model.Client;
import model.Contrat;
import model.Vehicule;
import utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContratDAO {
    // Ajouter un contrat
    public boolean ajouterContrat(Contrat contrat) {
        String sql = "INSERT INTO Contrats (id_client, immatricule, ID_Agence_Depart, dateDepart, dateRetourPrevu, Forfait_journalier, km_Depart, km_tarif) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pst = DBConnection.getConnection().prepareStatement(sql)) {
            pst.setInt(1, contrat.getIdClient());
            pst.setString(2, contrat.getImmatricule());
            pst.setInt(3, contrat.getIdAgenceDepart());
            pst.setString(4, contrat.getDateDepart());
            pst.setString(5, contrat.getDateRetourPrevue());
            pst.setDouble(6, contrat.getForfaitJournalier());
            pst.setInt(7, contrat.getKm_depart());
            pst.setDouble(8, contrat.getTarifKm());

            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Récupérer tous les contrats
    public List<Contrat> getAllContrats() {
        List<Contrat> contrats = new ArrayList<>();
        String query = "SELECT * FROM Contrats";

        try (PreparedStatement pst = DBConnection.getConnection().prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                Contrat contrat = new Contrat(
                    rs.getInt("ID_contrat"),
                    rs.getInt("id_client"),
                    rs.getString("immatricule"),
                    rs.getInt("ID_Agence_Depart"),
                    rs.getString("dateDepart"),
                    rs.getString("dateRetourPrevu"),
                    rs.getDouble("Forfait_Journalier"),
                    rs.getInt("Km_depart"),
                    rs.getDouble("Km_tarif"),
                    rs.getInt("ID_Agence_Arrivee") 
                );
                contrats.add(contrat);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contrats;
    }

    // Supprimer un contrat
    public boolean supprimerContrat(int idContrat) {
        String sql = "DELETE FROM Contrats WHERE ID_contrat = ?";

        try (PreparedStatement pst = DBConnection.getConnection().prepareStatement(sql)) {
            pst.setInt(1, idContrat);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Contrat> getContratsByUserId(int userId) {
		List<Contrat> contrats = new ArrayList<>();
		String query = "SELECT c.*, cl.* FROM Contrats c JOIN clients cl ON c.id_client = cl.id_client WHERE cl.id_utilisateur = ? ";

		try (PreparedStatement pst = DBConnection.getConnection().prepareStatement(query)) {
			pst.setInt(1, userId);
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				Contrat contrat = new Contrat(
					rs.getInt("ID_contrat"),
					rs.getInt("id_client"),
					rs.getString("immatricule"),
					rs.getInt("ID_Agence_Depart"),
					rs.getString("dateDepart"),
					rs.getString("dateRetourPrevu"),
					rs.getDouble("Forfait_Journalier"),
					rs.getInt("Km_depart"),
					rs.getDouble("Km_tarif"),
					rs.getInt("ID_Agence_Arrivee")
				);
				contrats.add(contrat);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return contrats;
	}
    
    public List<Vehicule> getVehiculeByClientId(int idClient){
    	String sql = "SELECT v.* FROM Vehicules v JOIN Contrats c ON v.immatriculation = c.immatricule WHERE c.id_client = ?";
    	List<Vehicule> vehicules = new ArrayList<>();
    	try (PreparedStatement pst = DBConnection.getConnection().prepareStatement(sql)) {
			pst.setInt(1, idClient);
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				Vehicule vehicule = new Vehicule(
					rs.getString("immatriculation"),
					rs.getString("marque"),
					rs.getString("type"),
					rs.getString("categorie"),
					rs.getString("carburant"),
					rs.getInt("nbresPlaces"),
					rs.getInt("id_agence"),
					rs.getInt("disponible")
				);
				vehicules.add(vehicule);
			}
			return vehicules;
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	return null;
    }
    
    public Contrat getContartByIdClientImmatricule(int idClient, String immatricule) {
		String sql = "SELECT * FROM Contrats WHERE id_client = ? AND immatricule = ?";
		try (PreparedStatement pst = DBConnection.getConnection().prepareStatement(sql)) {
			pst.setInt(1, idClient);
			pst.setString(2, immatricule);
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				Contrat contrat = new Contrat(
					rs.getInt("ID_contrat"),
					rs.getInt("id_client"),
					rs.getString("immatricule"),
					rs.getInt("ID_Agence_Depart"),
					rs.getString("dateDepart"),
					rs.getString("dateRetourPrevu"),
					rs.getDouble("Forfait_Journalier"),
					rs.getInt("Km_depart"),
					rs.getDouble("Km_tarif"),
					rs.getInt("ID_Agence_Arrivee")
				);
				return contrat;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
    }
    
    public Client getClientByIdClient(int idClient) {
        String sql = "SELECT * FROM Clients WHERE id_client = ?";
        try (PreparedStatement pst = DBConnection.getConnection().prepareStatement(sql)) {
            pst.setInt(1, idClient);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return new Client(
                    rs.getInt("id_client"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("adresse"),
                    rs.getString("tel"),
                    rs.getInt("id_utilisateur")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public Client getClientByIdContrat(int idContrat) {
        String sql = "SELECT c.* FROM Clients c " +
                     "JOIN Contrats co ON c.id_client = co.id_client " +
                     "WHERE co.ID_contrat = ?";
        try (PreparedStatement pst = DBConnection.getConnection().prepareStatement(sql)) {
            pst.setInt(1, idContrat);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return new Client(
                    rs.getInt("id_client"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("adresse"),
                    rs.getString("tel"),
                    rs.getInt("id_utilisateur")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean modifierContrat(Contrat contrat) {
        String sql = "UPDATE Contrats SET dateDepart = ?, dateRetourPrevu = ?, " +
                     "Forfait_Journalier = ?, Km_depart = ?, Km_tarif = ? " +
                     "WHERE ID_contrat = ?";

        try (PreparedStatement pst = DBConnection.getConnection().prepareStatement(sql)) {
            pst.setString(1, contrat.getDateDepart());
            pst.setString(2, contrat.getDateRetourPrevue());
            pst.setDouble(3, contrat.getForfaitJournalier());
            pst.setInt(4, contrat.getKm_depart());
            pst.setDouble(5, contrat.getTarifKm());
            pst.setInt(6, contrat.getIdContrat());

            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}


