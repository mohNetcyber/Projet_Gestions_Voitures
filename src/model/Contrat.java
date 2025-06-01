package model;

public class Contrat {
    private int idContrat;
    private int idClient;
    private String immatricule;
    private int idAgenceDepart;
    private int idAgenceArrivee;
    private String dateDepart;
    private String dateRetourPrevue;
    private double forfaitJournalier;
    private int km_depart;
    private double tarifKm;

    // Constructeur
    public Contrat(int idContrat, int idClient, String immatricule, int idAgenceDepart, String dateDepart, String dateRetourPrevue, double forfaitJournalier, int km_depart, double tarifKm, int idAgenceArrivee) {
        this.idContrat = idContrat;
        this.idClient = idClient;
        this.immatricule = immatricule;
        this.idAgenceDepart = idAgenceDepart;
        this.dateDepart = dateDepart;
        this.dateRetourPrevue = dateRetourPrevue;
        this.forfaitJournalier = forfaitJournalier;
        this.km_depart = km_depart;
        this.tarifKm = tarifKm;
        this.idAgenceArrivee = idAgenceArrivee;
    }
    
    public Contrat() { 
		// Constructeur par défaut
	}

	// Getters et Setters
    public int getIdContrat() { return idContrat; }
    public void setIdContrat(int idContrat) { this.idContrat = idContrat; }

    public int getIdClient() { return idClient; }
    public void setIdClient(int idClient) { this.idClient = idClient; }

    public String getImmatricule() { return immatricule; }
    public void setImmatricule(String immatricule) { this.immatricule = immatricule; }

    public int getIdAgenceDepart() { return idAgenceDepart; }
    public void setIdAgenceDepart(int idAgenceDepart) { this.idAgenceDepart = idAgenceDepart; }

    public String getDateDepart() { return dateDepart; }
    public void setDateDepart(String dateDepart) { this.dateDepart = dateDepart; }

    public String getDateRetourPrevue() { return dateRetourPrevue; }
    public void setDateRetourPrevue(String dateRetourPrevue) { this.dateRetourPrevue = dateRetourPrevue; }

    public double getForfaitJournalier() { return forfaitJournalier; }
    public void setForfaitJournalier(double forfaitJournalier) { this.forfaitJournalier = forfaitJournalier; }
    
    public int getKm_depart() { return km_depart; } // Corrected getter
    public void setKm_depart(int km_depart) { this.km_depart = km_depart; } // Corrected setter
    
    public double getTarifKm() { return tarifKm; }
    public void setTarifKm(double tarifKm) { this.tarifKm = tarifKm;}
    
    public int getIdAgenceArrivee() { return idAgenceArrivee; }
    public void setIdAgenceArrivee(int idAgenceArrivee) { this.idAgenceArrivee = idAgenceArrivee;}
}
