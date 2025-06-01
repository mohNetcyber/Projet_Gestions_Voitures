package model;

public class Facture {
	private int ID_Facture;
	private int ID_Contrat;
	private String date_Facture;
	private int nbresJours;
	private String dateRetour;
	private int km_arrive;
	private int km_parcouru;
	private double montantTotal;
	private int id_utilisateur;
	private String nomPrenomClient;
	public Facture(int ID_Facture, int ID_Contrat,String nomPrenomClient, String date_Facture, String dateRetour, int nbresJours,
			int km_arrive, int km_parcouru, double montantTotal, int id_utilisateur) {
		this.ID_Facture = ID_Facture;
		this.ID_Contrat = ID_Contrat;
		this.nomPrenomClient = nomPrenomClient;
		this.date_Facture = date_Facture;
		this.nbresJours = nbresJours;
		this.dateRetour = dateRetour;
		this.km_arrive = km_arrive;
		this.km_parcouru = km_parcouru;
		this.montantTotal = montantTotal;
		this.setId_utilisateur(id_utilisateur);
	}
	

	public int getID_Facture() {
		return ID_Facture;
	}

	public void setID_Facture(int iD_Facture) {
		ID_Facture = iD_Facture;
	}

	public int getID_Contrat() {
		return ID_Contrat;
	}

	public void setID_Contrat(int iD_Contrat) {
		ID_Contrat = iD_Contrat;
	}

	public String getDate_Facture() {
		return date_Facture;
	}

	public void setDate_Facture(String date_Facture) {
		this.date_Facture = date_Facture;
	}

	public int getNbresJours() {
		return nbresJours;
	}

	public void setNbresJours(int nbresJours) {
		this.nbresJours = nbresJours;
	}

	public String getDateRetour() {
		return dateRetour;
	}

	public void setDateRetour(String dateRetourReel) {
		this.dateRetour = dateRetourReel;
	}

	public int getKm_arrive() {
		return km_arrive;
	}

	public void setKm_arrive(int km_arrivee) {
		this.km_arrive = km_arrivee;
	}

	public int getKm_parcouru() {
		return km_parcouru;
	}

	public void setKm_parcouru(int km_parcouru) {
		this.km_parcouru = km_parcouru;
	}


	public double getMontantTotal() {
		return montantTotal;
	}

	public void setMontantTotal(double montantTotal) {
		this.montantTotal = montantTotal;
	}


	public int getId_utilisateur() {
		return id_utilisateur;
	}


	public void setId_utilisateur(int id_utilisateur) {
		this.id_utilisateur = id_utilisateur;
	}


	public String getNomPrenomClient() {
		return nomPrenomClient;
	}


	public void setNomPrenomClient(String nomPrenomClient) {
		this.nomPrenomClient = nomPrenomClient;
	}
	
	
	
}
