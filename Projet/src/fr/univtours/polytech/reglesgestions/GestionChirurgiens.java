package fr.univtours.polytech.reglesgestions;

import fr.univtours.polytech.ressource.Salle;
/**
 * Interface des regles de gestions pour les chirurgiens.
 */
public interface GestionChirurgiens extends Regle{
	public Salle solution();
	public void ajoutSalle(Salle salle);
}
