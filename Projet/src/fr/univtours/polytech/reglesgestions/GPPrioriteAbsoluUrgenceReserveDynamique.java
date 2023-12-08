package fr.univtours.polytech.reglesgestions;

import fr.univtours.polytech.Planning;
import fr.univtours.polytech.Simulation;
import fr.univtours.polytech.entite.PatientUrgent;

/**
 * Regle de gestion qui lorsque qu'un patient urgent est declare dans la
 * simulation le planning change en mettant en premier dans la liste d'attente
 * des salles tres equipe le patient urgent
 */
public class GPPrioriteAbsoluUrgenceReserveDynamique implements GestionPlanning {
	private Simulation simulation;

	public GPPrioriteAbsoluUrgenceReserveDynamique(Simulation simulation) {
		this.simulation = simulation;
	}

	public Planning solution(PatientUrgent patientUrgent) {

		return null;
	}
}
