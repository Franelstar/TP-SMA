package model;

import javafx.beans.property.SimpleStringProperty;

public class Article {
	private SimpleStringProperty nom;
	private SimpleStringProperty vendeur;
	protected int prix;
	protected int qnte;
	protected int total;
	
	/**
	 * @param nom
	 * @param prix
	 * @param qnte
	 */
	public Article(String nom, int prix, int qnte, String vendeur) {
		super();
		this.nom = new SimpleStringProperty(nom);
		this.vendeur = new SimpleStringProperty(vendeur);
		this.prix = prix;
		this.qnte = qnte;
		this.total = prix * qnte;
	}

	/**
	 * @return the prixT
	 */
	public int getTotal() {
		return total;
	}

	/**
	 * @return the nom
	 */
	public String getNom() {
		return nom.get();
	}

	/**
	 * @param nom the nom to set
	 */
	public void setNom(String nom) {
		this.nom = new SimpleStringProperty(nom);
	}

	/**
	 * @return the prix
	 */
	public int getPrix() {
		return prix;
	}

	/**
	 * @param prix the prix to set
	 */
	public void setPrix(int prix) {
		this.prix = prix;
	}

	/**
	 * @return the qnte
	 */
	public int getQnte() {
		return qnte;
	}

	/**
	 * @param qnte the qnte to set
	 */
	public void setQnte(int qnte) {
		this.qnte = qnte;
	}

	/**
	 * @return the vendeur
	 */
	public String getVendeur() {
		return vendeur.get();
	}
}
