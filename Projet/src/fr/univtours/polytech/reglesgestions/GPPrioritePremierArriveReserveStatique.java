package fr.univtours.polytech.reglesgestions;

// Import des classes nécessaires
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.univtours.polytech.Planning;
import fr.univtours.polytech.Simulation;
import fr.univtours.polytech.entite.Patient;
import fr.univtours.polytech.entite.PatientRDV;
import fr.univtours.polytech.ressource.Salle;

// Classe implémentant l'interface GestionPlanning
public class GPPrioritePremierArriveReserveStatique implements GestionPlanning {
	private Simulation simulation;

	private List<Patient> nouvListePatient;
	List<Salle> pileSalle;

	// Constructeur prenant une simulation en paramètre
	public GPPrioritePremierArriveReserveStatique(Simulation simulation) {
		this.simulation = simulation;
	}


	/**
	 * Méthode de résolution de la planification
	 * 
	 * @param patientUrgent, le patient urgent qui vient d'etre declare
	 */
	public Planning solution(Patient patientUrgent) {
		// Initialisation de la nouvelle liste de patients
		nouvListePatient = new ArrayList<>();

		if (patientUrgent != null) {
			// Si un patient urgent est donné, récupérer le planning existant et l'ajouter à
			// la nouvelle liste
			Planning ancienPlanning = simulation.getPlanning();

			nouvListePatient = ancienPlanning.extraiteDonnee();
			nouvListePatient.add(patientUrgent);

			System.out.println("changememnt planning");
		} else {
			// Sinon,cela signifie que c'est la création du premier et planning et donc il
			// faut récupérer les patients prévus pour un rendez-vous et les ajouter à la
			// nouvelle liste
			System.out.println("premier planning");
			List<PatientRDV> patientsRDV = simulation.getPatientsRDV();

			for (Patient p : patientsRDV) {
				nouvListePatient.add(p);
			}
		}
		// Tri de la liste de patient en fcontion du temps d'arrivée
		Collections.sort(nouvListePatient, (p1, p2) -> p1.getHeureArrive().compareTo(p2.getHeureArrive()));

		// Récupération des salles de la simulation
		Map<Salle.typeSalles, List<Salle>> sallesMap = simulation.getSalles();
		pileSalle = new ArrayList<Salle>();

		Map<Salle, List<Patient>> renvoi = triDesSalles(sallesMap);

		return new Planning(placementDesPatients(renvoi));
	}

	/**
	 * Crée la pile des salles qui sera utilisé pour placer les patients
	 * 
	 * @param sallesMap
	 * @return renvoi, le map qui sera utiliser pout initialiser le planning avec
	 *         toutes ses listes initialiser
	 */
	private Map<Salle, List<Patient>> triDesSalles(Map<Salle.typeSalles, List<Salle>> sallesMap) {
		pileSalle = new ArrayList<Salle>();
		Map<Salle, List<Patient>> renvoi = new HashMap<Salle, List<Patient>>();
		// Tri des salles
		for (Salle.listeEtats etat : Salle.listeEtats.values()) {
			for (Salle.typeSalles type : Salle.typeSalles.values()) {
				if (sallesMap.containsKey(type)) {
					for (Salle salle : sallesMap.get(type)) {
						if (salle.getEtat() == etat) {
							pileSalle.add(salle);
							renvoi.put(salle, new ArrayList<Patient>());
						}
					}
				}

			}
		}

		return renvoi;
	}

	/**
	 * Méthode qui gère l'affectation des patients dans les salles
	 * 
	 * @param renvoi
	 * @return renvoi, la map qui permettra de faire le planning
	 */
	private Map<Salle, List<Patient>> placementDesPatients(Map<Salle, List<Patient>> renvoi) {
		for (Patient patient : nouvListePatient) {
			int indice = 0;

			boolean place = false;
			Salle salle;

			/* On répète l'opération jusqu'à ce que le patient soit affecté à une salle */
			while (!place && indice < pileSalle.size()) {
				salle = pileSalle.get(indice);

				if (patient.estUrgent()) {
					if (salle.getType() == Salle.typeSalles.TRESEQUIPE || salle.getType() == Salle.typeSalles.RESERVE) {
						renvoi.get(salle).add(patient);
						place = true;

						pileSalle.remove(indice);
						pileSalle.add(salle);
					}
				} else {
					if (patient.getGravite() == PatientRDV.listeGravite.TRESEQUIPE) {
						if (salle.getType() == Salle.typeSalles.TRESEQUIPE) {
							renvoi.get(salle).add(patient);
							place = true;

							pileSalle.remove(indice);
							pileSalle.add(salle);
						}
					} else {
						if (patient.getGravite() == PatientRDV.listeGravite.SEMIEQUIPE) {
							if (salle.getType() == Salle.typeSalles.TRESEQUIPE
									|| salle.getType() == Salle.typeSalles.SEMIEQUIPE) {
								renvoi.get(salle).add(patient);
								place = true;

								pileSalle.remove(indice);
								pileSalle.add(salle);
							}
						} else {
							if (salle.getType() == Salle.typeSalles.TRESEQUIPE
									|| salle.getType() == Salle.typeSalles.SEMIEQUIPE
									|| salle.getType() == Salle.typeSalles.PEUEQUIPE) {

								renvoi.get(salle).add(patient);
								place = true;

								pileSalle.remove(indice);
								pileSalle.add(salle);
							}
						}
					}

				}

				indice++;
			}
		}

		return renvoi;
	}
}
