package fr.univtours.polytech;

import fr.univtours.polytech.evenements.Deroulement;
import fr.univtours.polytech.initialisation.DonneeInitialisation;
import fr.univtours.polytech.initialisation.LectureDeFichierSimulation;

public class MainSimulation {
	public static void main(String[] args) throws Exception {		
		LectureDeFichierSimulation lecture = new LectureDeFichierSimulation("../fichiers/fichier.txt");

		DonneeInitialisation data = lecture.initialiserSimulation();
		
		Simulation s = data.creerSimultation();
		
		if(s == null) {
			throw new Exception("La simulation n'a pas pu être crée");
		}
		Deroulement d = s.getDeroulement();

		d.execution();
		ExtractionJSON e = new ExtractionJSON(s);
		e.extraiteDonnees();
	}	
}
