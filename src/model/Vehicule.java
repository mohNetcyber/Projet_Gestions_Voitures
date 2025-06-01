package model;

public class Vehicule {
    private String immatriculation; // Clé primaire
    private String marque;
    private String type;
    private String categorie;
    private String carburant;
    private int nbresPlaces;
    private int idAgence; // Clé étrangère
    private float forfaitJournalier;
    private int disponible;

    // Constructeur
    public Vehicule (String immatriculation, String marque, String type, String categorie,String carburant, int nbresPlaces, int idAgence) {
        this.immatriculation = immatriculation;
        this.marque = marque;
        this.type = type;
        this.categorie = categorie;
        this.carburant = carburant;
        this.nbresPlaces = nbresPlaces;
        this.idAgence = idAgence;
    }
    
    public Vehicule (String immatriculation, String marque, String type, String categorie, String carburant, int nbresPlaces, int idAgence, int disponible) {
        this.immatriculation = immatriculation;
        this.marque = marque;
        this.type = type;
        this.categorie = categorie;
        this.carburant = carburant;
        this.nbresPlaces = nbresPlaces;
        this.idAgence = idAgence;
        this.disponible = disponible;
    }
    
    public Vehicule (String immatriculation, String marque, String type, String categorie, String carburant, int nbresPlaces, float forfaitJournalier, int idAgence, int disponible) {
        this.immatriculation = immatriculation;
        this.marque = marque;
        this.type = type;
        this.categorie = categorie;
        this.carburant = carburant;
        this.nbresPlaces = nbresPlaces;
        this.forfaitJournalier = forfaitJournalier;
        this.idAgence = idAgence;
        this.disponible = disponible;
    }
    
    public Vehicule() { }
    
    // Getters et Setters
    public String getImmatriculation() { return immatriculation; }
    public void setImmatriculation(String immatriculation) { this.immatriculation = immatriculation; }

    public String getMarque() { return marque; }
    public void setMarque(String marque) { this.marque = marque; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }

    public int getNbresPlaces() { return nbresPlaces; }
    public void setNbresPlaces(int nbresPlaces) { this.nbresPlaces = nbresPlaces; }

    public int getIdAgence() { return idAgence; }
    public void setIdAgence(int idAgence) { this.idAgence = idAgence; }

	public int getDisponible() {
		return disponible;
	}

	public void setDisponible(int disponible) {
		this.disponible = disponible;
	}

	public String getCarburant() {
		return carburant;
	}

	public void setCarburant(String carburant) {
		this.carburant = carburant;
	}

	@Override
	public String toString() {
		return "Vehicule [immatriculation=" + immatriculation + ", marque=" + marque + "]";
	}

	public float getForfaitJournalier() {
		return forfaitJournalier;
	}

	public void setForfaitJournalier(float forfaitJournalier) {
		this.forfaitJournalier = forfaitJournalier;
	}
	
	
}
