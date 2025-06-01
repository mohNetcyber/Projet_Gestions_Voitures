package dao;
import model.Agence;
import model.Vehicule;
import utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehiculeDAO {
 
    
    public Agence getAgenceById(int id) {
		String query = "SELECT * FROM Agences WHERE id_agence = ?";
		try (PreparedStatement pst = DBConnection.getConnection().prepareStatement(query)) {
			pst.setInt(1, id);
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				return new Agence(rs.getInt("ID_Agence"), rs.getString("nomAgence"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

    // Récupérer tous les véhicules
    public List<Vehicule> getAllVehicules() {
        List<Vehicule> vehicules = new ArrayList<>();
        String query = "SELECT * FROM Vehicules";

        try (PreparedStatement pst = DBConnection.getConnection().prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                Vehicule vehicule = new Vehicule(
                    rs.getString("immatriculation"),
                    rs.getString("marque"),
                    rs.getString("type"),
                    rs.getString("categorie"),
                    rs.getString("carburant"),
                    rs.getInt("nbresPlaces"),
                    rs.getFloat("ForfaitJournalier"),
                    rs.getInt("id_agence"),
                    rs.getInt("disponible")
                );
                vehicules.add(vehicule);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vehicules;
    }
    
    // Modifier un véhicule
    public boolean modifierVehicule(Vehicule vehicule) {
		String sql = "UPDATE Vehicules SET marque = ?, type = ?, categorie = ?, nbresPlaces = ?, id_agence = ? WHERE immatriculation = ?";

		try (PreparedStatement pst = DBConnection.getConnection().prepareStatement(sql)) {
			pst.setString(1, vehicule.getMarque());
			pst.setString(2, vehicule.getType());
			pst.setString(3, vehicule.getCategorie());
			pst.setInt(4, vehicule.getNbresPlaces());
			pst.setInt(5, vehicule.getIdAgence());
			pst.setString(6, vehicule.getImmatriculation());

			return pst.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

    // Supprimer un véhicule
    public boolean supprimerVehicule(String immatriculation) {
        String sql = "DELETE FROM Vehicules WHERE immatriculation = ?";
        String sqlcontrat = "DELETE FROM Contrats WHERE immatriculation = ?";

        try (PreparedStatement pst = DBConnection.getConnection().prepareStatement(sql);
        	PreparedStatement pstcontrat = DBConnection.getConnection().prepareStatement(sqlcontrat)) {
            pst.setString(1, immatriculation);
            pstcontrat.setString(1, immatriculation);
            pstcontrat.executeUpdate();
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean setVehiculeIndisponible(String immatriculation) {
        String sql = "UPDATE Vehicules SET disponible = 0 WHERE immatriculation = ?";

        try (PreparedStatement pst = DBConnection.getConnection().prepareStatement(sql)) {
            pst.setString(1, immatriculation);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    
    public boolean setVehiculeDisponible(String immatriculation) {
        String sql = "UPDATE Vehicules SET disponible = 1 WHERE immatriculation = ?";

        try (PreparedStatement pst = DBConnection.getConnection().prepareStatement(sql)) {
            pst.setString(1, immatriculation);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    
    public List<Vehicule> getVehiculesDisponibles() {
        List<Vehicule> vehicules = new ArrayList<>();
        String query = "SELECT * FROM Vehicules WHERE disponible = 1";

        try (PreparedStatement pst = DBConnection.getConnection().prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                Vehicule vehicule = new Vehicule(
                    rs.getString("immatriculation"),
                    rs.getString("marque"),
                    rs.getString("type"),
                    rs.getString("categorie"),
                    rs.getString("carburant"),
                    rs.getInt("nbresPlaces"),
                    rs.getFloat("ForfaitJournalier"),
                    rs.getInt("id_agence"),
                    rs.getInt("disponible")
                );
                vehicules.add(vehicule);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vehicules;
    }

    public Vehicule getVehiculeByImmatricule(String immatricule) {
    	String query = "SELECT * FROM Vehicules WHERE immatriculation = ?";
    	try (PreparedStatement pst = DBConnection.getConnection().prepareStatement(query)) {
			pst.setString(1, immatricule);
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				Vehicule vehicule = new Vehicule(
					rs.getString("immatriculation"),
					rs.getString("marque"),
					rs.getString("type"),
					rs.getString("categorie"),
                    rs.getString("carburant"),
					rs.getInt("nbresPlaces"),
					rs.getInt("id_agence")
				);
				return vehicule; // Retourne le véhicule trouvé
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
    }
    
    
    public int countAllVehicules() {
        String sql = "SELECT COUNT(*) FROM Vehicules";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int countVehiculesLoues() {
        String sql = "SELECT COUNT(*) FROM Vehicules WHERE disponible = 0";
        try (
             PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public boolean updateVehicule(Vehicule vehicule) {
        String sql = "UPDATE Vehicules SET marque = ?, type = ?, categorie = ?, carburant = ?, nbresPlaces = ?, ForfaitJournalier = ?, disponible = ? WHERE immatriculation = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, vehicule.getMarque());
            stmt.setString(2, vehicule.getType());
            stmt.setString(3, vehicule.getCategorie());
            stmt.setString(4, vehicule.getCarburant());
            stmt.setInt(5, vehicule.getNbresPlaces());
            stmt.setFloat(6, vehicule.getForfaitJournalier());
            stmt.setInt(7, vehicule.getDisponible());
            stmt.setString(8, vehicule.getImmatriculation());

            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteVehicule(String immatriculation) {
        String sql = "DELETE FROM Vehicules WHERE immatriculation = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, immatriculation);
            int rowsDeleted = stmt.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean addVehicule(Vehicule vehicule) {
		String sql = "INSERT INTO Vehicules (immatriculation, marque, type, categorie, carburant, nbresPlaces, ForfaitJournalier, id_agence, disponible) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try (Connection conn = DBConnection.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, vehicule.getImmatriculation());
			stmt.setString(2, vehicule.getMarque());
			stmt.setString(3, vehicule.getType());
			stmt.setString(4, vehicule.getCategorie());
			stmt.setString(5, vehicule.getCarburant());
			stmt.setInt(6, vehicule.getNbresPlaces());
			stmt.setFloat(7, vehicule.getForfaitJournalier());
			stmt.setInt(8, vehicule.getIdAgence());
			stmt.setInt(9, vehicule.getDisponible());

			int rowsInserted = stmt.executeUpdate();
			return rowsInserted > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}



}
