package dao;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Utilisateur;
import utils.DBConnection;

public class UtilisateurDAO {
	private static Utilisateur utilisateurConnecte;
	
	public Utilisateur authenticate(String login, String password, String role) {
	    // First, hash the provided password
	    String hashedPassword = hashPassword(password);
	    if (hashedPassword == null) {
	        System.out.println("Erreur de hashage du mot de passe");
	        return null;
	    }

	    String sql = "SELECT * FROM Utilisateur WHERE login=? AND MotDePasse=? AND role=? AND valide ='oui' ";
	    
	    try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
	        stmt.setString(1, login);
	        stmt.setString(2, hashedPassword);
	        stmt.setString(3, role);
	        ResultSet rs = stmt.executeQuery();
	        
	        if (rs.next()) {
	            utilisateurConnecte = new Utilisateur();
	            utilisateurConnecte.setId(rs.getInt("ID_Utilisateur"));
	            utilisateurConnecte.setNom(rs.getString("nom"));
	            utilisateurConnecte.setPrenom(rs.getString("prenom"));
	            utilisateurConnecte.setLogin(login);
	            utilisateurConnecte.setMotDePasse(hashedPassword); // Store hashed password
	            utilisateurConnecte.setRole(rs.getString("role"));
	            utilisateurConnecte.setValide(rs.getString("valide"));
	            
	            
	            System.out.println("Utilisateur connecté : " + utilisateurConnecte.getNom());
	            return utilisateurConnecte;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    System.out.println("Échec de l'authentification");
	    return null;
	}

	private String hashPassword(String password) {
	    try {
	        MessageDigest digest = MessageDigest.getInstance("SHA-256");
	        byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
	        StringBuilder hexString = new StringBuilder();
	        for (byte b : hash) {
	            String hex = Integer.toHexString(0xff & b);
	            if (hex.length() == 1) hexString.append('0');
	            hexString.append(hex);
	        }
	        return hexString.toString();
	    } catch (NoSuchAlgorithmException e) {
	        e.printStackTrace();
	        return null;
	    }
	}


	public static Utilisateur getUtilisateurConnecte() {
	    return utilisateurConnecte;
	}
	
	public List<Utilisateur> getAllUsers(){
	
		String sql = "SELECT * FROM Utilisateur";
		List<Utilisateur> utilisateurs = new ArrayList<>();
		
		try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				Utilisateur utilisateur = new Utilisateur();
				utilisateur.setId(rs.getInt("ID_Utilisateur"));
				utilisateur.setNom(rs.getString("nom"));
				utilisateur.setPrenom(rs.getString("prenom"));
				utilisateur.setLogin(rs.getString("login"));
				utilisateur.setMotDePasse(rs.getString("MotDePasse"));
				utilisateur.setRole(rs.getString("role"));
				utilisateur.setValide(rs.getString("valide"));
	            
				
				utilisateurs.add(utilisateur);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return utilisateurs;
	}
	
	public boolean deleteUser(int idUser) {
	    Connection conn = null;
	    try {
	        conn = DBConnection.getConnection();
	        conn.setAutoCommit(false);

	        String sql = "SELECT id_client FROM Clients WHERE ID_Utilisateur=?";
	        
	        try (PreparedStatement clientStmt = conn.prepareStatement(sql)) {
	            clientStmt.setInt(1, idUser);
	            ResultSet rs = clientStmt.executeQuery(); // Un seul appel suffit

	            ClientDAO clientDAO = new ClientDAO(); // Initialiser une seule fois
	            while (rs.next()) {
	                int idClient = rs.getInt("id_client");
	                boolean clientDeleted = clientDAO.supprimerClient(idClient);
	                if (!clientDeleted) {
	                    conn.rollback();
	                    return false;
	                }
	            }
	        }

	        String deleteUserSQL = "DELETE FROM Utilisateur WHERE ID_Utilisateur=?";
	        try (PreparedStatement userStmt = conn.prepareStatement(deleteUserSQL)) {
	            userStmt.setInt(1, idUser);
	            int rowsAffected = userStmt.executeUpdate();
	            conn.commit();
	            return rowsAffected > 0;
	        }
	    } catch (SQLException e) {
	        try {
	            if (conn != null) conn.rollback();
	        } catch (SQLException ex) {
	            ex.printStackTrace();
	        }
	        e.printStackTrace();
	        return false;
	    } finally {
	        try {
	            if (conn != null) conn.setAutoCommit(true);
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
	}


	
	public boolean addUser(Utilisateur utilisateur) {
		String sql = "INSERT INTO Utilisateur (nom, prenom, login, MotDePasse, role, valide) VALUES (?, ?, ?, ?, ?, ?)";
		
		try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
			stmt.setString(1, utilisateur.getNom());
			stmt.setString(2, utilisateur.getPrenom());
			stmt.setString(3, utilisateur.getLogin());
			stmt.setString(4, utilisateur.getMotDePasse());
			stmt.setString(5, utilisateur.getRole());
			stmt.setString(6, utilisateur.getValide());
			
			int rowsAffected = stmt.executeUpdate();
			return rowsAffected > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public boolean updateUser(Utilisateur user) {
	    String sqlWithPassword = "UPDATE Utilisateur SET nom = ?, prenom = ?, login = ?, MotDePasse = ?, role = ?, valide = ? WHERE ID_Utilisateur = ?";
	    String sqlWithoutPassword = "UPDATE Utilisateur SET nom = ?, prenom = ?, login = ?, role = ?, valide = ? WHERE id = ?";

	    try (
	         PreparedStatement stmt = user.getMotDePasse() != null && !user.getMotDePasse().isEmpty()
	                 ? DBConnection.getConnection().prepareStatement(sqlWithPassword)
	                 : DBConnection.getConnection().prepareStatement(sqlWithoutPassword)) {

	        stmt.setString(1, user.getNom());
	        stmt.setString(2, user.getPrenom());
	        stmt.setString(3, user.getLogin());

	        if (user.getMotDePasse() != null && !user.getMotDePasse().isEmpty()) {
	            stmt.setString(4, user.getMotDePasse());
	            stmt.setString(5, user.getRole());
	            stmt.setString(6, user.getValide());
	            stmt.setInt(7, user.getId());
	        } else {
	            stmt.setString(4, user.getRole());
	            stmt.setInt(5, user.getId());
	            stmt.setString(6, user.getValide());
	        }

	        int rowsUpdated = stmt.executeUpdate();
	        return rowsUpdated > 0;

	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	
	public int countAllUsers() {
	    String sql = "SELECT COUNT(*) FROM Utilisateur";
	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql);
	         ResultSet rs = stmt.executeQuery()) {
	        if (rs.next()) return rs.getInt(1);
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return 0;
	}


    
    
}
