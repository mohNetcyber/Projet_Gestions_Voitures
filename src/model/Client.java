package model;

public class Client {
    @Override
	public String toString() {
		return  nom + " " + prenom ;
	}
	private int id;
    private String nom;
    private String prenom;
    private String adresse;
    private String telephone;
    private int idUtilisateur; // Clé étrangère
    private Utilisateur user;
    
    public Client() {}

    public Client(String nom, String prenom, String adresse, String telephone) {
        this.nom = nom;
        this.prenom = prenom;
        this.adresse = adresse;
        this.telephone = telephone;
    }
    public Client(String nom, String prenom, String adresse, String telephone, int idUtilisateur) {
		this.nom = nom;
		this.prenom = prenom;
		this.adresse = adresse;
		this.telephone = telephone;
		this.setIdUtilisateur(idUtilisateur);
	}
    public Client(int id, String nom, String prenom, String adresse, String telephone) {
		this.id = id;
		this.nom = nom;
		this.prenom = prenom;
		this.adresse = adresse;
		this.telephone = telephone;
	}
    
    public Client(int id, String nom, String prenom, String adresse, String telephone, int idUtilisateur) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.adresse = adresse;
        this.telephone = telephone;
        this.setIdUtilisateur(idUtilisateur);
    }


    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

	public Utilisateur getUser() {
		return user;
	}

	public void setUser(Utilisateur user) {
		this.user = user;
	}

	public int getIdUtilisateur() {
		return idUtilisateur;
	}

	public void setIdUtilisateur(int idUtilisateur) {
		this.idUtilisateur = idUtilisateur;
	}
}
