package fr.univtours.polytech.ressource;

import java.time.LocalTime;

public class Chirurgien extends Ressource {
	public listeEtats getEtat() {
		return etat;
	}

	public Chirurgien(Integer id, LocalTime heureDebut) {
		super(id, heureDebut);
	}
}
