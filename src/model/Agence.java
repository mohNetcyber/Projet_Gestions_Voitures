package model;

public class Agence {
    private int idAgence; 
    private String nomAgence;
    private String adresse;
    private String ville;
    private String telephone;
    private String email;

    
    public Agence(int idAgence, String nomAgence, String adresse, String ville, String telephone, String email) {
        this.idAgence = idAgence;
        this.nomAgence = nomAgence;
        this.adresse = adresse;
        this.ville = ville;
        this.telephone = telephone;
        this.email = email;
    }
    
    public Agence(int idAgence, String nomAgence) {
		this.idAgence = idAgence;
		this.nomAgence = nomAgence;
	}
    
    public Agence() { }

    // Getters et Setters
    public int getIdAgence() { return idAgence; }
    public void setIdAgence(int idAgence) { this.idAgence = idAgence; }

    public String getNomAgence() { return nomAgence; }
    public void setNomAgence(String nomAgence) { this.nomAgence = nomAgence; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String toString() {
		return nomAgence;
	}
}
