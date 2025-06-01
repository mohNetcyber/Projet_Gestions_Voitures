package dao;

import model.Client;
import model.Utilisateur;
import utils.DBConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClientDAO {
	public boolean ajouterClient(Client client, int userId) {
	    String sql = "INSERT INTO Clients (id_utilisateur, nom, prenom, adresse, tel) VALUES (?, ?, ?, ?, ?)";
	    
	    try (PreparedStatement statement = DBConnection.getConnection().prepareStatement(sql)) {
	        statement.setString(2, client.getNom());
	        statement.setString(3, client.getPrenom());
	        statement.setString(4, client.getAdresse());
	        statement.setString(5, client.getTelephone());
	        statement.setInt(1, userId); // Ajoute l'ID de l'utilisateur connecté

	        int rowsInserted = statement.executeUpdate();
	        return rowsInserted > 0;

	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}

    
    public List<Client> getClientsByUserk(int userId) {
    	System.out.println("Récupération des clients pour user_id: " + userId);

        List<Client> clients = new ArrayList<>();
        String query = "SELECT * FROM clients WHERE id_utilisateur = ?"; 

        try (PreparedStatement pst = DBConnection.getConnection().prepareStatement(query)) {
            pst.setInt(1, userId); // Filtrer par l'utilisateur connecté
            
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Client client = new Client(
                    	rs.getInt("id_client"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("adresse"),
                        rs.getString("tel")
                    );
                    clients.add(client);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return clients;
    }
    
    public boolean modifierClient(Client client) {
        String sql = "UPDATE clients SET nom=?, prenom=?, adresse=?, tel=? WHERE id_client=?"; 

        try (PreparedStatement pst = DBConnection.getConnection().prepareStatement(sql)) {
            pst.setString(1, client.getNom());
            pst.setString(2, client.getPrenom());
            pst.setString(3, client.getAdresse());
            pst.setString(4, client.getTelephone());
            pst.setInt(5, client.getId()); // Assure que l'ID est bien utilisé

            int rowsUpdated = pst.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    
    public boolean supprimerClient(int idClient) {
        // First delete all contracts associated with this client
        String deleteContrats = "DELETE FROM Contrats WHERE id_client = ?";
        // Then delete the client
        String deleteClient = "DELETE FROM Clients WHERE id_client = ?";
        
        try (
             PreparedStatement psContrats = DBConnection.getConnection().prepareStatement(deleteContrats);
             PreparedStatement psClient = DBConnection.getConnection().prepareStatement(deleteClient)) {
            
            // Delete contracts first
            psContrats.setInt(1, idClient);
            psContrats.executeUpdate();
            
            // Then delete client
            psClient.setInt(1, idClient);
            return psClient.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    
    public List<Client> getClientsByUserId(int userId) {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT * FROM Clients WHERE id_utilisateur = ?";
        
        try (
             PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
             
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Client client = new Client(
                    rs.getInt("id_client"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("adresse"),
                    rs.getString("tel"),
                    rs.getInt("id_utilisateur")
                );
                clients.add(client);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return clients;
    }

    
    public Client getClientById(int id_client) {
    	try {
			String sql = "SELECT * FROM Clients WHERE id_client = ?";
			PreparedStatement statement = DBConnection.getConnection().prepareStatement(sql);
			statement.setInt(1, id_client);
			ResultSet resultSet = statement.executeQuery();
			
			if (resultSet.next()) {
				Client client = new Client();
				client.setNom(resultSet.getString("nom"));
				client.setPrenom(resultSet.getString("prenom"));
				client.setAdresse(resultSet.getString("adresse"));
				client.setTelephone(resultSet.getString("tel"));
				return client;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
    }
    
    //Admin
	public List<Client> getAllClients() {
		List<Client> clients = new ArrayList<>();
		String sql = "SELECT * FROM Clients";

		try (PreparedStatement statement = DBConnection.getConnection().prepareStatement(sql);
			 ResultSet resultSet = statement.executeQuery()) {

			while (resultSet.next()) {
				Client client = new Client();
				client.setNom(resultSet.getString("nom"));
				client.setPrenom(resultSet.getString("prenom"));
				client.setAdresse(resultSet.getString("adresse"));
				client.setTelephone(resultSet.getString("tel"));
				clients.add(client);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return clients;
	}
	
	
	public List<Client> getAllClientsWithUsers() {
	    List<Client> clients = new ArrayList<>();
	    String sql = "SELECT c.*, u.nom , u.prenom, " +
	                 "u.login, u.role, u.valide " +
	                 "FROM Clients c " +
	                 "JOIN Utilisateur u ON c.id_utilisateur = u.ID_Utilisateur";

	    try (PreparedStatement pst = DBConnection.getConnection().prepareStatement(sql);
	         ResultSet rs = pst.executeQuery()) {

	        while (rs.next()) {
	            Client client = new Client();
	            client.setId(rs.getInt("ID_Client"));
	            client.setNom(rs.getString("nom"));
	            client.setPrenom(rs.getString("prenom"));
	            client.setAdresse(rs.getString("adresse"));
	            client.setTelephone(rs.getString("tel"));

	            Utilisateur user = new Utilisateur();
	            user.setId(rs.getInt("ID_Utilisateur"));
	            user.setNom(rs.getString("nom"));
	            user.setPrenom(rs.getString("prenom"));
	            user.setLogin(rs.getString("login"));
	            user.setRole(rs.getString("role"));
	            user.setValide(rs.getString("valide"));

	            client.setUser(user);
	            clients.add(client);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return clients;
	}
	
	public Utilisateur getUserByClientId(int clientId) {
	    String sql = "SELECT u.* FROM Utilisateur u " +
	                 "JOIN Clients c ON u.ID_Utilisateur = c.id_utilisateur " +
	                 "WHERE c.id_client = ?";
	    
	    try (PreparedStatement pst = DBConnection.getConnection().prepareStatement(sql)) {
	        pst.setInt(1, clientId);
	        ResultSet rs = pst.executeQuery();
	        
	        if (rs.next()) {
	            Utilisateur user = new Utilisateur();
	            user.setId(rs.getInt("ID_Utilisateur"));
	            user.setNom(rs.getString("nom"));
	            user.setPrenom(rs.getString("prenom")); 
	            user.setLogin(rs.getString("login"));
	            user.setRole(rs.getString("role"));
	            user.setValide(rs.getString("valide"));
	            return user;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null;
	}



}
