/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package fr.univtours.polytech.vue;

import java.awt.Color;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import com.raven.event.EventTimePicker;

import fr.univtours.polytech.util.LoiDePoisson;

/**
 *
 * @author Arthur
 */
public class SaisiePatient extends javax.swing.JFrame {
	Integer id;

	Saisie saisie;
	Integer nbPatientsRDVPE;
	Integer nbPatientsRDVSE;
	Integer nbPatientsRDVTE;
	Integer nbPatientsUrgent;

	boolean currEstUrgent;
	LocalTime currHeureArrivee;
	Integer currGravite;
	Integer currTempsOperation;
	LocalTime currHeureDeclaration;

	Integer currTempsOperationMin;
	Integer currTempsOperationMax;

	Integer nbPatientsUrgentAcreer;

	Integer ligneAMod;
	Integer ligneASuppr;

	Map<Integer, List<LocalTime>> mapPE;
	Map<Integer, List<LocalTime>> mapSE;
	Map<Integer, List<LocalTime>> mapTE;
	Map<Integer, List<LocalTime>> mapUrgent;

	Map<Integer, Integer> mapIndex;

	public SaisiePatient() {
		initComponents();
	}

	/**
	 * Creates new form SaisiePatient
	 * 
	 * @param nbPatientsRDVPE
	 * @param nbPatientsRDVSE
	 * @param nbPatientsRDVTE
	 * @param nbPatientsUrgent
	 * @param map
	 */
	public SaisiePatient(Saisie saisie, Integer nbPatientsRDVPE, Integer nbPatientsRDVSE, Integer nbPatientsRDVTE,
			Integer nbPatientsUrgent, Map<Integer, List<LocalTime>> map) {

		this.saisie = saisie;
		this.nbPatientsRDVPE = 0;
		this.nbPatientsRDVSE = 0;
		this.nbPatientsRDVTE = 0;
		this.nbPatientsUrgent = 0;
		id = 0;

		initComponents();
		mapPE = new HashMap<Integer, List<LocalTime>>();
		mapSE = new HashMap<Integer, List<LocalTime>>();
		mapTE = new HashMap<Integer, List<LocalTime>>();
		mapUrgent = new HashMap<Integer, List<LocalTime>>();

		mapIndex = new HashMap<Integer, Integer>();

		for (int i = 0; i < nbPatientsRDVPE; i++) {
			ajouterPatientRDV(i, 1, map.get(i).get(0), map.get(i).get(1));
			mapPE.put(i, map.get(i));
			mapIndex.put(i, i);
		}
		for (int i = nbPatientsRDVPE; i < nbPatientsRDVPE + nbPatientsRDVSE; i++) {
			ajouterPatientRDV(i, 2, map.get(i).get(0), map.get(i).get(1));
			mapSE.put(i - nbPatientsRDVPE, map.get(i));
			mapIndex.put(i, i - nbPatientsRDVPE);
		}
		for (int i = nbPatientsRDVPE + nbPatientsRDVSE; i < nbPatientsRDVPE + nbPatientsRDVSE + nbPatientsRDVTE; i++) {
			ajouterPatientRDV(i, 3, map.get(i).get(0), map.get(i).get(1));
			mapTE.put(i - nbPatientsRDVPE - nbPatientsRDVSE, map.get(i));
			mapIndex.put(i, i - nbPatientsRDVPE - nbPatientsRDVSE);
		}
		for (int i = nbPatientsRDVPE + nbPatientsRDVSE + nbPatientsRDVTE; i < nbPatientsRDVPE + nbPatientsRDVSE
				+ nbPatientsRDVTE + nbPatientsUrgent; i++) {
			ajouterPatientUrgent(i, map.get(i).get(0), map.get(i).get(1), map.get(i).get(2));
			mapUrgent.put(i - nbPatientsRDVPE - nbPatientsRDVSE - nbPatientsRDVTE, map.get(i));
			mapIndex.put(i, i - nbPatientsRDVPE - nbPatientsRDVSE - nbPatientsRDVTE);
		}

		currEstUrgent = true;
		currGravite = 1;
		currTempsOperation = -1;

		currTempsOperationMin = -1;
		currTempsOperationMax = -1;
		ligneAMod = 0;
		ligneASuppr = 0;

		pickerHeureArrivee.set24hourMode(true);
		pickerHeureDeclaration.set24hourMode(true);

		nbPatientsUrgentAcreer = 0;

		LocalTime heureDebutSimulation = saisie.getHeureDebutJournee();

		pickerHeureArrivee.addEventTimePicker(new EventTimePicker() {
			@Override
			public void timeSelected(String string) {
				currHeureArrivee = getHeure(pickerHeureArrivee);
				btnHeureArrivee.setText(currHeureArrivee.toString());

			}
		});
		currHeureArrivee = heureDebutSimulation;
		btnHeureArrivee.setText(currHeureArrivee.toString());
		pickerHeureArrivee.setSelectedTime(Date.from(
				(Instant) heureDebutSimulation.atDate(LocalDate.now()).atZone(ZoneId.systemDefault()).toInstant()));
		pickerHeureDeclaration.addEventTimePicker(new EventTimePicker() {
			@Override
			public void timeSelected(String string) {
				currHeureDeclaration = getHeure(pickerHeureDeclaration);
				btnHeureDeclaration.setText(currHeureDeclaration.toString());
			}
		});
		currHeureDeclaration = heureDebutSimulation;
		btnHeureDeclaration.setText(currHeureDeclaration.toString());
		pickerHeureDeclaration.setSelectedTime(Date.from(
				(Instant) heureDebutSimulation.atDate(LocalDate.now()).atZone(ZoneId.systemDefault()).toInstant()));
	}

	private LocalTime getHeure(com.raven.swing.TimePicker picker) {
		String str = picker.getSelectedTime();
		char[] dest = new char[4];
		str.getChars(0, 2, dest, 0);
		str.getChars(3, 5, dest, 2);

		int i1 = (((int) dest[0]) - 48) * 10 + (((int) dest[1]) - 48);
		int i2 = (((int) dest[2]) - 48) * 10 + (((int) dest[3]) - 48);
		return LocalTime.of(i1, i2);
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed" desc="Generated
	// Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		pickerHeureArrivee = new com.raven.swing.TimePicker();
		pickerHeureDeclaration = new com.raven.swing.TimePicker();
		jPanel1 = new javax.swing.JPanel();
		jLabel2 = new javax.swing.JLabel();
		btnGenerer = new javax.swing.JToggleButton();
		btnValider = new javax.swing.JButton();
		btnAnnuler = new javax.swing.JButton();
		jLabel5 = new javax.swing.JLabel();
		strTempsOpeMax = new javax.swing.JTextField();
		strTempsOpeMin = new javax.swing.JTextField();
		jLabel4 = new javax.swing.JLabel();
		jLabel3 = new javax.swing.JLabel();
		jLabel1 = new javax.swing.JLabel();
		jLabel13 = new javax.swing.JLabel();
		boolUrgent = new javax.swing.JComboBox<>();
		jLabel12 = new javax.swing.JLabel();
		strGravite = new javax.swing.JComboBox<>();
		strTempsOpe = new javax.swing.JTextField();
		jLabel9 = new javax.swing.JLabel();
		btnHeureDeclaration = new javax.swing.JButton();
		jLabel11 = new javax.swing.JLabel();
		jLabel10 = new javax.swing.JLabel();
		btnHeureArrivee = new javax.swing.JButton();
		btnAjouter = new javax.swing.JButton();
		jScrollPane1 = new javax.swing.JScrollPane();
		tableau = new javax.swing.JTable();
		btnSuppr = new javax.swing.JButton();
		jLabel7 = new javax.swing.JLabel();
		jLabel8 = new javax.swing.JLabel();
		strNbPatientACreer = new javax.swing.JTextField();
		strTempsOpeMin1 = new javax.swing.JTextField();
		jLabel6 = new javax.swing.JLabel();
		strTempsOpeMax1 = new javax.swing.JTextField();
		jLabel14 = new javax.swing.JLabel();
		jLabel15 = new javax.swing.JLabel();
		jLabel16 = new javax.swing.JLabel();
		btnGenererPatientUrgent = new javax.swing.JButton();

		pickerHeureArrivee.setForeground(new java.awt.Color(236, 213, 129));

		pickerHeureDeclaration.setForeground(new java.awt.Color(236, 213, 129));

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setTitle("Saisie Patient");
		setMinimumSize(new java.awt.Dimension(720, 480));
		setResizable(false);

		jPanel1.setBackground(new java.awt.Color(236, 213, 129));
		jPanel1.setMinimumSize(new java.awt.Dimension(1185, 545));
		jPanel1.setPreferredSize(new java.awt.Dimension(1245, 645));

		jLabel2.setText("ou aléatoire :");

		btnGenerer.setBackground(new java.awt.Color(236, 213, 129));
		btnGenerer.setText("Génerer");
		btnGenerer.setPreferredSize(new java.awt.Dimension(70, 22));
		btnGenerer.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnGenererActionPerformed(evt);
			}
		});

		btnValider.setBackground(new java.awt.Color(236, 213, 129));
		btnValider.setText("Valider modifications");
		btnValider.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnValiderActionPerformed(evt);
			}
		});

		btnAnnuler.setBackground(new java.awt.Color(236, 213, 129));
		btnAnnuler.setText("Annuler modifications");
		btnAnnuler.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnAnnulerActionPerformed(evt);
			}
		});

		jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
		jLabel5.setText("Ajouter/Modifier un Patient :");

		strTempsOpeMax.setBackground(new java.awt.Color(236, 213, 129));
		strTempsOpeMax.setText("0");
		strTempsOpeMax.setMinimumSize(new java.awt.Dimension(65, 22));
		strTempsOpeMax.setPreferredSize(new java.awt.Dimension(70, 22));
		strTempsOpeMax.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				strTempsOpeMaxActionPerformed(evt);
			}
		});

		strTempsOpeMin.setBackground(new java.awt.Color(236, 213, 129));
		strTempsOpeMin.setText("0");
		strTempsOpeMin.setMinimumSize(new java.awt.Dimension(65, 22));
		strTempsOpeMin.setPreferredSize(new java.awt.Dimension(70, 22));
		strTempsOpeMin.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				strTempsOpeMinActionPerformed(evt);
			}
		});

		jLabel4.setText("max (en min)");

		jLabel3.setText("min (en min)");

		jLabel1.setText("Saisir :");

		jLabel13.setText("Urgent :");

		boolUrgent.setBackground(new java.awt.Color(236, 213, 129));
		boolUrgent.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Oui", "Non" }));
		boolUrgent.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				boolUrgentActionPerformed(evt);
			}
		});

		jLabel12.setText("Gravité :");

		strGravite.setBackground(new java.awt.Color(236, 213, 129));
		strGravite.setModel(
				new javax.swing.DefaultComboBoxModel<>(new String[] { "Peu equipee", "Semi equipee", "Tres equipee" }));
		strGravite.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				strGraviteActionPerformed(evt);
			}
		});

		strTempsOpe.setBackground(new java.awt.Color(236, 213, 129));
		strTempsOpe.setText("0");
		strTempsOpe.setMinimumSize(new java.awt.Dimension(65, 22));
		strTempsOpe.setPreferredSize(new java.awt.Dimension(70, 22));
		strTempsOpe.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				strTempsOpeActionPerformed(evt);
			}
		});

		jLabel9.setText("Heure Arrivée :");

		btnHeureDeclaration.setBackground(new java.awt.Color(236, 213, 129));
		btnHeureDeclaration.setText("18:00");
		btnHeureDeclaration.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnHeureDeclarationActionPerformed(evt);
			}
		});

		jLabel11.setText("Heure Déclaration :");

		jLabel10.setText("Bornes temps Opération  :");

		btnHeureArrivee.setBackground(new java.awt.Color(236, 213, 129));
		btnHeureArrivee.setText("8:00");
		btnHeureArrivee.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnHeureArriveeActionPerformed(evt);
			}
		});

		btnAjouter.setBackground(new java.awt.Color(236, 213, 129));
		btnAjouter.setText("Ajouter Patient");
		btnAjouter.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnAjouterActionPerformed(evt);
			}
		});

		tableau.setModel(new javax.swing.table.DefaultTableModel(new Object[][] {

		}, new String[] { "id", "urgent", "heure Arrivee", "temps Operation", "gravite", "heureDeclaration" }));
		tableau.setGridColor(new java.awt.Color(236, 213, 129));
		tableau.setSelectionBackground(new java.awt.Color(236, 213, 129));
		jScrollPane1.setViewportView(tableau);

		btnSuppr.setBackground(new java.awt.Color(236, 213, 129));
		btnSuppr.setText("Supprimer ligne(s) selectionnee(s)");
		btnSuppr.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnSupprActionPerformed(evt);
			}
		});

		jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
		jLabel7.setText("Supprimer un Patient :");

		jLabel8.setText("ou générer aléatoirement :");

		strNbPatientACreer.setBackground(new java.awt.Color(236, 213, 129));
		strNbPatientACreer.setText("0");
		strNbPatientACreer.setMinimumSize(new java.awt.Dimension(70, 22));
		strNbPatientACreer.setPreferredSize(new java.awt.Dimension(70, 22));
		strNbPatientACreer.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				strNbPatientACreerActionPerformed(evt);
			}
		});

		strTempsOpeMin1.setBackground(new java.awt.Color(236, 213, 129));
		strTempsOpeMin1.setText("0");
		strTempsOpeMin1.setMinimumSize(new java.awt.Dimension(65, 22));
		strTempsOpeMin1.setPreferredSize(new java.awt.Dimension(70, 22));
		strTempsOpeMin1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				strTempsOpeMin1ActionPerformed(evt);
			}
		});

		jLabel6.setText("min (en min)");

		strTempsOpeMax1.setBackground(new java.awt.Color(236, 213, 129));
		strTempsOpeMax1.setText("0");
		strTempsOpeMax1.setMinimumSize(new java.awt.Dimension(65, 22));
		strTempsOpeMax1.setPreferredSize(new java.awt.Dimension(70, 22));
		strTempsOpeMax1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				strTempsOpeMax1ActionPerformed(evt);
			}
		});

		jLabel14.setText("max (en min)");

		jLabel15.setText("Temps Opération  (en min) :");

		jLabel16.setText("Génerer");

		btnGenererPatientUrgent.setBackground(new java.awt.Color(236, 213, 129));
		btnGenererPatientUrgent.setText("Patients urgents");
		btnGenererPatientUrgent.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnGenererPatientUrgentActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel1Layout.createSequentialGroup()
						.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
										jPanel1Layout.createSequentialGroup()
												.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
												.addComponent(
														btnGenerer, javax.swing.GroupLayout.PREFERRED_SIZE, 71,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addGap(114, 114, 114))
								.addGroup(jPanel1Layout.createSequentialGroup().addGroup(jPanel1Layout
										.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addGroup(jPanel1Layout.createSequentialGroup().addGroup(jPanel1Layout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addGroup(jPanel1Layout.createSequentialGroup().addGap(53, 53, 53)
														.addGroup(jPanel1Layout.createParallelGroup(
																javax.swing.GroupLayout.Alignment.TRAILING)
																.addComponent(jLabel11).addComponent(
																		jLabel12)
																.addComponent(jLabel9).addComponent(jLabel13)))
												.addGroup(jPanel1Layout.createSequentialGroup().addGap(36, 36, 36)
														.addComponent(jLabel1).addGap(18, 18, 18).addComponent(
																strTempsOpe, javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE)))
												.addGroup(jPanel1Layout
														.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(jPanel1Layout.createSequentialGroup()
																.addGap(11, 11, 11).addComponent(jLabel2)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addGroup(jPanel1Layout.createParallelGroup(
																		javax.swing.GroupLayout.Alignment.LEADING,
																		false)
																		.addComponent(strTempsOpeMax,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				71, Short.MAX_VALUE)
																		.addComponent(strTempsOpeMin,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				Short.MAX_VALUE))
																.addGap(18, 18, 18)
																.addGroup(jPanel1Layout.createParallelGroup(
																		javax.swing.GroupLayout.Alignment.LEADING)
																		.addComponent(jLabel3).addComponent(jLabel4)))
														.addGroup(jPanel1Layout.createSequentialGroup()
																.addGap(30, 30, 30)
																.addGroup(jPanel1Layout.createParallelGroup(
																		javax.swing.GroupLayout.Alignment.LEADING)
																		.addComponent(boolUrgent,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addComponent(btnHeureArrivee)))
														.addGroup(jPanel1Layout.createSequentialGroup()
																.addGap(27, 27, 27)
																.addGroup(jPanel1Layout.createParallelGroup(
																		javax.swing.GroupLayout.Alignment.LEADING)
																		.addComponent(btnHeureDeclaration)
																		.addComponent(strGravite,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				javax.swing.GroupLayout.PREFERRED_SIZE)))))
										.addGroup(jPanel1Layout.createSequentialGroup().addGap(17, 17, 17)
												.addComponent(jLabel5))
										.addGroup(jPanel1Layout.createSequentialGroup().addGap(20, 20, 20)
												.addGroup(jPanel1Layout
														.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(jPanel1Layout.createSequentialGroup()
																.addComponent(jLabel7).addGap(36, 36, 36)
																.addComponent(btnSuppr))
														.addGroup(jPanel1Layout.createSequentialGroup()
																.addComponent(jLabel8)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(jLabel10)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																.addGroup(jPanel1Layout.createParallelGroup(
																		javax.swing.GroupLayout.Alignment.LEADING)
																		.addGroup(jPanel1Layout.createSequentialGroup()
																				.addComponent(strTempsOpeMax1,
																						javax.swing.GroupLayout.PREFERRED_SIZE,
																						71,
																						javax.swing.GroupLayout.PREFERRED_SIZE)
																				.addPreferredGap(
																						javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																				.addComponent(jLabel14))
																		.addGroup(jPanel1Layout.createSequentialGroup()
																				.addComponent(strTempsOpeMin1,
																						javax.swing.GroupLayout.PREFERRED_SIZE,
																						71,
																						javax.swing.GroupLayout.PREFERRED_SIZE)
																				.addPreferredGap(
																						javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																				.addComponent(jLabel6))))
														.addGroup(jPanel1Layout.createSequentialGroup()
																.addComponent(jLabel16)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(strNbPatientACreer,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addGap(18, 18, 18)
																.addComponent(btnGenererPatientUrgent))))
										.addGroup(jPanel1Layout.createSequentialGroup().addGap(108, 108, 108)
												.addComponent(btnAjouter)))
										.addGap(0, 60, Short.MAX_VALUE)))
						.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
								.addGroup(jPanel1Layout.createSequentialGroup().addComponent(btnAnnuler)
										.addGap(18, 18, 18).addComponent(btnValider))
								.addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 705,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGap(18, 18, 18))
				.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(jPanel1Layout.createSequentialGroup().addGap(16, 16, 16).addComponent(jLabel15)
								.addContainerGap(1021, Short.MAX_VALUE))));
		jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel1Layout.createSequentialGroup().addContainerGap().addGroup(jPanel1Layout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(jPanel1Layout.createSequentialGroup().addComponent(jLabel5)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
								.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(jLabel13).addComponent(boolUrgent,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE))
								.addGap(18, 18, 18)
								.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(jLabel9).addComponent(btnHeureArrivee))
								.addGap(46, 46, 46)
								.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(jLabel1)
										.addComponent(strTempsOpe, javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent(strTempsOpeMin, javax.swing.GroupLayout.PREFERRED_SIZE, 22,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent(jLabel3).addComponent(jLabel2))
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
								.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(strTempsOpeMax, javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent(jLabel4))
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
								.addComponent(btnGenerer, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 63,
										Short.MAX_VALUE)
								.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(jLabel12).addComponent(strGravite,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE))
								.addGap(18, 18, 18)
								.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(jLabel11).addComponent(btnHeureDeclaration))
								.addGap(18, 18, 18).addComponent(btnAjouter).addGap(24, 24, 24)
								.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addComponent(jLabel8)
										.addGroup(jPanel1Layout.createSequentialGroup().addGroup(jPanel1Layout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(jLabel10)
												.addComponent(strTempsOpeMin1, javax.swing.GroupLayout.PREFERRED_SIZE,
														22, javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(jLabel6))
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
												.addGroup(jPanel1Layout
														.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(strTempsOpeMax1,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(jLabel14))))
								.addGap(10, 10, 10)
								.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(strNbPatientACreer, javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent(jLabel16).addComponent(btnGenererPatientUrgent))
								.addGap(18, 18, 18)
								.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(btnSuppr).addComponent(jLabel7)))
						.addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 495,
								javax.swing.GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(btnValider).addComponent(btnAnnuler))
						.addContainerGap(58, Short.MAX_VALUE))
				.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(jPanel1Layout.createSequentialGroup().addGap(123, 123, 123).addComponent(jLabel15)
								.addContainerGap(427, Short.MAX_VALUE))));

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(0, 0, Short.MAX_VALUE)));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(0, 0, Short.MAX_VALUE)));

		pack();
	}// </editor-fold>//GEN-END:initComponents

	/**
	 * Méthode lié à l'évenement ouverture du timePicker pour l'heure d'arrivé du
	 * patient
	 * 
	 * @param evt
	 */
	private void btnHeureArriveeActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnHeureArriveeActionPerformed

		pickerHeureArrivee.showPopup(this, 350, 0);
	}// GEN-LAST:event_btnHeureArriveeActionPerformed

	/**
	 * Méthode lié à l'évenement ouverture du timePicker pour l'heure de déclaration
	 * du patient
	 * 
	 * @param evt
	 */
	private void btnHeureDeclarationActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnHeureDeclarationActionPerformed

		pickerHeureDeclaration.showPopup(this, 350, 30);
	}// GEN-LAST:event_btnHeureDeclarationActionPerformed

	/**
	 * Méthode lié à l'évenement changement de satut du patient
	 * 
	 * @param evt
	 */
	private void boolUrgentActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_boolUrgentActionPerformed

		if (boolUrgent.getSelectedItem().toString().equals("Oui")) {
			currEstUrgent = true;
		} else {
			currEstUrgent = false;
		}
	}// GEN-LAST:event_boolUrgentActionPerformed

	/**
	 * Méthode lié à l'évenement changement de gravité du patient
	 * 
	 * @param evt
	 */
	private void strGraviteActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_strGraviteActionPerformed

		if (strGravite.getSelectedItem().toString().equals("Peu equipee")) {
			currGravite = 1;
		} else {
			if (strGravite.getSelectedItem().toString().equals("Semi equipee")) {
				currGravite = 2;
			} else {
				currGravite = 3;
			}
		}
	}// GEN-LAST:event_strGraviteActionPerformed

	/**
	 * Méthode lié à l'évenement saisie valider pour le temps d'opération du patient
	 * 
	 * @param evt
	 */
	private void strTempsOpeActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_strTempsOpeActionPerformed

		currTempsOperation = recupererValeurLue(strTempsOpe);
	}// GEN-LAST:event_strTempsOpeActionPerformed

	/**
	 * Méthode lié à l'évenement saisie valider pour la borne minimale de temps
	 * d'opération dans la génération du temps d'opération
	 * 
	 * @param evt
	 */
	private void strTempsOpeMinActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_strTempsOpeMinActionPerformed

		int renvoi = recupererValeurLue(strTempsOpeMin);

		if (renvoi >= 0 && (renvoi <= currTempsOperationMax || currTempsOperationMax == -1)) {
			currTempsOperationMin = renvoi;
			strTempsOpeMin1.setText(currTempsOperationMin.toString());
			strTempsOpeMin1.setForeground(new Color(0, 200, 0));
		} else {
			strTempsOpeMin.setText("0");
			strTempsOpeMin.setForeground(new Color(255, 0, 0));
			strTempsOpeMin1.setText("0");
			strTempsOpeMin1.setForeground(new Color(255, 0, 0));
		}
	}// GEN-LAST:event_strTempsOpeMinActionPerformed

	/**
	 * Méthode lié à l'évenement saisie valider pour la borne maximale de temps
	 * d'opération dans la génération du temps d'opération
	 * 
	 * @param evt
	 */
	private void strTempsOpeMaxActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_strTempsOpeMaxActionPerformed

		int renvoi = recupererValeurLue(strTempsOpeMax);

		if (renvoi >= 0 && renvoi >= currTempsOperationMin) {
			currTempsOperationMax = renvoi;
			strTempsOpeMax1.setText(currTempsOperationMax.toString());
			strTempsOpeMax1.setForeground(new Color(0, 200, 0));
		} else {
			strTempsOpeMax.setText("0");
			strTempsOpeMax.setForeground(new Color(255, 0, 0));
			strTempsOpeMax1.setText("0");
			strTempsOpeMax1.setForeground(new Color(255, 0, 0));
		}
	}// GEN-LAST:event_strTempsOpeMaxActionPerformed

	/**
	 * Méthode lié à l'évenement saisie valider pour la borne minimale de temps
	 * d'opération dans la génération de patients urgents
	 * 
	 * @param evt
	 */
	private void strTempsOpeMin1ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_strTempsOpeMin1ActionPerformed

		int renvoi = recupererValeurLue(strTempsOpeMin1);

		if (renvoi >= 0 && (renvoi <= currTempsOperationMax || currTempsOperationMax == -1)) {
			currTempsOperationMin = renvoi;
			strTempsOpeMin.setText(currTempsOperationMin.toString());
			strTempsOpeMin.setForeground(new Color(0, 200, 0));
		} else {
			strTempsOpeMin.setText("0");
			strTempsOpeMin.setForeground(new Color(255, 0, 0));
			strTempsOpeMin1.setText("0");
			strTempsOpeMin1.setForeground(new Color(255, 0, 0));
		}
	}// GEN-LAST:event_strTempsOpeMin1ActionPerformed

	/**
	 * Méthode lié à l'évenement saisie valider pour la borne maximale de temps
	 * d'opération dans la génération de patients urgents
	 * 
	 * @param evt
	 */
	private void strTempsOpeMax1ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_strTempsOpeMax1ActionPerformed

		int renvoi = recupererValeurLue(strTempsOpeMax1);

		if (renvoi >= 0 && renvoi >= currTempsOperationMin) {
			currTempsOperationMax = renvoi;
			strTempsOpeMax.setText(currTempsOperationMax.toString());
			strTempsOpeMax.setForeground(new Color(0, 200, 0));
		} else {
			strTempsOpeMax.setText("0");
			strTempsOpeMax.setForeground(new Color(255, 0, 0));
			strTempsOpeMax1.setText("0");
			strTempsOpeMax1.setForeground(new Color(255, 0, 0));
		}
	}// GEN-LAST:event_strTempsOpeMax1ActionPerformed

	/**
	 * Méthode lié à l'évenement saisie valider pour le tnombre de patients urgents
	 * à créer
	 * 
	 * @param evt
	 */
	private void strNbPatientACreerActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_strNbPatientACreerActionPerformed

		nbPatientsUrgentAcreer = recupererValeurLue(strNbPatientACreer);
	}// GEN-LAST:event_strNbPatientACreerActionPerformed

	/**
	 * Méthode lié à l'évenement génération du temps d'opération dans la création de
	 * patient par saisie
	 * 
	 * @param evt
	 */
	private void btnGenererActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnGenererActionPerformed

		try {
			int pottCurrTempsOperationMin = recupererValeurLue(strTempsOpeMin);
			int pottCurrTempsOperationMax = recupererValeurLue(strTempsOpeMax);

			if (pottCurrTempsOperationMin != currTempsOperationMin) {
				currTempsOperationMin = pottCurrTempsOperationMin;
			}

			if (pottCurrTempsOperationMax != currTempsOperationMax) {
				currTempsOperationMax = pottCurrTempsOperationMax;
			}

			if (pottCurrTempsOperationMin > pottCurrTempsOperationMax) {
				currTempsOperationMin = -1;
			}

			if (currTempsOperationMin >= 0 && currTempsOperationMax >= 0) {
				strTempsOpeMin1.setText(currTempsOperationMin.toString());
				strTempsOpeMax1.setText(currTempsOperationMax.toString());

				strTempsOpeMin.setForeground(new Color(0, 200, 0));
				strTempsOpeMax.setForeground(new Color(0, 200, 0));
				strTempsOpeMin1.setForeground(new Color(0, 200, 0));
				strTempsOpeMax1.setForeground(new Color(0, 200, 0));

				currTempsOperation = LoiDePoisson.genererEntier(currTempsOperationMin, currTempsOperationMax, 50);
				strTempsOpe.setText(String.valueOf(currTempsOperation));
				strTempsOpe.setForeground(new Color(0, 200, 0));
			} else {
				if (currTempsOperationMin < 0) {
					strTempsOpeMin.setText("0");
					strTempsOpeMin1.setText("0");

					strTempsOpeMin.setForeground(new Color(255, 0, 0));
					strTempsOpeMin1.setForeground(new Color(255, 0, 0));
				}
				if (currTempsOperationMin < 0) {
					strTempsOpeMax.setText("0");
					strTempsOpeMax1.setText("0");

					strTempsOpeMax.setForeground(new Color(255, 0, 0));
					strTempsOpeMax1.setForeground(new Color(255, 0, 0));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}// GEN-LAST:event_btnGenererActionPerformed

	/**
	 * Méthode lié à l'évenement validation de la saisie du patient
	 * 
	 * @param evt
	 */
	private void btnAjouterActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnAjouterActionPerformed

		try {
			if (currTempsOperation < 0) {
				throw new IllegalArgumentException("Veuillez saisir un temps d'operation");
			}

			DefaultTableModel modele = (DefaultTableModel) tableau.getModel();

			String urgent;
			String gravite;

			if (currEstUrgent) {
				ajouterPatientUrgent(id, currHeureArrivee, null, currHeureDeclaration);
			} else {
				ajouterPatientRDV(id, currGravite, currHeureArrivee, null);
			}
		} catch (IllegalArgumentException e) {
			strTempsOpe.setText("0");
			strTempsOpe.setForeground(new Color(255, 0, 0));

			System.err.println(e.getMessage());
		}

	}// GEN-LAST:event_btnAjouterActionPerformed

	/**
	 * Méthode lié à la suppression de patients dans le tableau
	 * 
	 * @param evt
	 */
	private void btnSupprActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnSupprActionPerformed

		DefaultTableModel tableauModif = (DefaultTableModel) tableau.getModel();

		if (tableau.getSelectedRowCount() > 0) {
			int[] lignesSelectionnees = tableau.getSelectedRows();
			int indexSuppr = lignesSelectionnees[0];

			for (int i = 0; i < lignesSelectionnees.length; i++) {

				Integer index = mapIndex.get(indexSuppr);

				if (tableauModif.getValueAt((Integer) indexSuppr, 1).equals("Oui")) {
					mapUrgent.remove(index);
					nbPatientsUrgent--;
				} else {
					if (tableauModif.getValueAt((Integer) indexSuppr, 4).equals("salle Peu Equipe")) {
						mapPE.remove(index);
						nbPatientsRDVPE--;
					} else {
						if (tableauModif.getValueAt((Integer) indexSuppr, 4).equals("salle Semi Equipe")) {
							mapSE.remove(index);
							nbPatientsRDVSE--;
						} else {
							mapTE.remove(index);
							nbPatientsRDVTE--;
						}
					}
				}

				tableauModif.removeRow(indexSuppr);
			}

		} else {
			JOptionPane.showMessageDialog(this, "Il n'y a pas de patients à supprimer");
		}
	}// GEN-LAST:event_btnSupprActionPerformed

	/**
	 * Méthode lié à la génération de patients urgents aléatoire
	 * 
	 * @param evt
	 */
	private void btnGenererPatientUrgentActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnGenererPatientUrgentActionPerformed

		try {
			int pottCurrTempsOperationMin = recupererValeurLue(strTempsOpeMin1);
			int pottCurrTempsOperationMax = recupererValeurLue(strTempsOpeMax1);
			int pottNbPatientsUrgentAcreer = recupererValeurLue(strNbPatientACreer);

			if (pottCurrTempsOperationMin != currTempsOperationMin) {
				currTempsOperationMin = pottCurrTempsOperationMin;
			}

			if (pottCurrTempsOperationMax != currTempsOperationMax) {
				currTempsOperationMax = pottCurrTempsOperationMax;
			}

			if (pottNbPatientsUrgentAcreer != nbPatientsUrgentAcreer) {
				nbPatientsUrgentAcreer = pottNbPatientsUrgentAcreer;
			}

			if (pottCurrTempsOperationMin > pottCurrTempsOperationMax) {
				currTempsOperationMin = -1;
			}

			if (currTempsOperationMin >= 0 && currTempsOperationMax >= 0 && nbPatientsUrgentAcreer >= 0) {
				DefaultTableModel modele = (DefaultTableModel) tableau.getModel();

				strTempsOpeMin.setText(currTempsOperationMin.toString());
				strTempsOpeMax.setText(currTempsOperationMax.toString());

				strTempsOpeMin.setForeground(new Color(0, 200, 0));
				strTempsOpeMax.setForeground(new Color(0, 200, 0));
				strTempsOpeMin1.setForeground(new Color(0, 200, 0));
				strTempsOpeMax1.setForeground(new Color(0, 200, 0));

				for (int i = 0; i < nbPatientsUrgentAcreer; i++) {
					currTempsOperation = LoiDePoisson.genererEntier(currTempsOperationMin, currTempsOperationMax, 50);
					currHeureArrivee = LoiDePoisson.genererHeure(saisie.getHeureDebutJournee(),
							saisie.getHeureFinJournee(), 50);
					currHeureDeclaration = currHeureArrivee.minusMinutes(LoiDePoisson.genererEntier(5, 45, 50));

					ajouterPatientUrgent(id, currHeureArrivee, null, currHeureDeclaration);
				}
			} else {
				if (currTempsOperationMin < 0) {
					strTempsOpeMin.setText("0");
					strTempsOpeMin1.setText("0");

					strTempsOpeMin.setForeground(new Color(255, 0, 0));
					strTempsOpeMin1.setForeground(new Color(255, 0, 0));
				}
				if (currTempsOperationMin < 0) {
					strTempsOpeMax.setText("0");
					strTempsOpeMax1.setText("0");

					strTempsOpeMax.setForeground(new Color(255, 0, 0));
					strTempsOpeMax1.setForeground(new Color(255, 0, 0));
				}
				if (nbPatientsUrgentAcreer < 0) {
					strNbPatientACreer.setText("0");
					strNbPatientACreer.setForeground(new Color(255, 0, 0));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}// GEN-LAST:event_btnGenererPatientUrgentActionPerformed

	/**
	 * Méthode permettant la création d'un patient et sa sauvegarde dans les maps
	 * 
	 * @param id
	 * @param gravite
	 * @param heureArrive
	 * @param tempsOperation
	 */
	private void ajouterPatientRDV(Integer id, Integer gravite, LocalTime heureArrive, LocalTime tempsOperation) {

		if (currTempsOperation >= 0) {
			DefaultTableModel modele = (DefaultTableModel) tableau.getModel();

			String str;
			List<LocalTime> liste = new ArrayList();
			liste.add(currHeureArrivee);

			/* Si le patient est crée à partir de l'interaction avec la fenètre */
			if (tempsOperation == null) {
				LocalTime tempsOpe = LocalTime.of((currTempsOperation / 60) % 24, currTempsOperation % 60);
				liste.add(tempsOpe);
			}
			/*
			 * Si le patient est crée à l'ouverture de la fenêtre, lors de la récupération
			 * des patients déjà crée
			 */
			else {
				currTempsOperation = 60 * tempsOperation.getHour() + tempsOperation.getMinute();
				liste.add(tempsOperation);
			}

			switch (gravite) {
			case 1:
				str = "Peu Equipee";
				mapPE.put(nbPatientsRDVPE, liste);
				mapIndex.put(id, nbPatientsRDVPE);
				nbPatientsRDVPE++;
				break;
			case 2:
				str = "Semi Equipee";
				mapSE.put(nbPatientsRDVSE, liste);
				mapIndex.put(id, nbPatientsRDVSE);
				nbPatientsRDVSE++;
				break;
			default:
				str = "Tres Equipee";
				mapTE.put(nbPatientsRDVTE, liste);
				mapIndex.put(id, nbPatientsRDVTE);
				nbPatientsRDVTE++;
				break;
			}

			modele.addRow(new Object[] { id, "Non", heureArrive, currTempsOperation, str, null });
			this.id++;
		} else {
			strTempsOpe.setText("0");
			strTempsOpe.setForeground(new Color(255, 0, 0));
		}

	}

	private void ajouterPatientUrgent(Integer id, LocalTime heureArrive, LocalTime tempsOperation,
			LocalTime heureDeclaration) {
		DefaultTableModel modele = (DefaultTableModel) tableau.getModel();
		try {
			if (heureDeclaration.isAfter(heureArrive)) {
				throw new IllegalArgumentException("Le patient urgent ne peut pas etre declare apres etre arrive");
			}
			List<LocalTime> liste = new ArrayList();
			liste.add(heureArrive);

			/* Si le patient est crée à partir de l'interaction avec la fenêtre */
			if (tempsOperation == null) {
				LocalTime tempsOpe = LocalTime.of((currTempsOperation / 60) % 24, currTempsOperation % 60);
				liste.add(tempsOpe);
			}
			/*
			 * Si le patient est crée à l'ouverture de la fenêtre, lors de la récupération
			 * des patients déjà crée
			 */
			else {
				currTempsOperation = tempsOperation.getHour() * 60 + tempsOperation.getMinute();
				liste.add(tempsOperation);
			}
			liste.add(currHeureDeclaration);
			mapUrgent.put(nbPatientsUrgent, liste);
			mapIndex.put(id, nbPatientsUrgent);
			nbPatientsUrgent++;

			modele.addRow(
					new Object[] { id, "Oui", heureArrive, currTempsOperation, "Tres Equipee", heureDeclaration });
			this.id++;
		} catch (IllegalArgumentException e) {
			System.err.println(e.getMessage());
		}
	}

	/**
	 * Méthode utilisé avec les textField pour récupérer les entiers saisie
	 * 
	 * @param textField
	 * @return valeur si la saisie est correct, -1 sinon
	 */
	private Integer recupererValeurLue(javax.swing.JTextField textField) {
		String str = textField.getText();
		Integer valeur = 0;
		try {
			valeur = Integer.parseInt(str);
			if (valeur < 0) {
				throw new NumberFormatException();
			}
			textField.setForeground(new Color(0, 200, 0));
		} catch (NumberFormatException e) {
			textField.setText("0");
			textField.setForeground(new Color(255, 0, 0));
			return -1;
		}
		return valeur;
	}

	/**
	 * Méthode lié à l'annulation de la saisie de patients
	 * 
	 * @param evt
	 */
	private void btnAnnulerActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnAnnulerActionPerformed

		this.dispose(); // Aucun changements n'est effectué, les patients saisie sont perdus
	}// GEN-LAST:event_btnAnnulerActionPerformed

	/**
	 * Méthode lié à la validation de la saisie de patients
	 * 
	 * @param evt
	 */
	private void btnValiderActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnValiderActionPerformed

		Map<Integer, List<LocalTime>> map = new HashMap<Integer, List<LocalTime>>();
		int index = 0;

		for (int i : mapPE.keySet()) {
			// se balader dans le tableau
			map.put(index, mapPE.get(i));
			index++;
		}
		for (int i : mapSE.keySet()) {
			// se balader dans le tableau
			map.put(index, mapSE.get(i));
			index++;
		}
		for (int i : mapTE.keySet()) {
			// se balader dans le tableau
			map.put(index, mapTE.get(i));
			index++;
		}
		for (int i : mapUrgent.keySet()) {
			// se balader dans le tableau
			map.put(index, mapUrgent.get(i));
			index++;
		}

		/*
		 * On remplace les patients stockés dans la fenetre principale par ceux du
		 * tableaux
		 */
		saisie.setNbPatientsRDVPE(nbPatientsRDVPE);
		saisie.setNbPatientsRDVSE(nbPatientsRDVSE);
		saisie.setNbPatientsRDVTE(nbPatientsRDVTE);
		saisie.setNbPatientsUrgent(nbPatientsUrgent);
		saisie.setMap(map);
		this.dispose();
	}// GEN-LAST:event_btnValiderActionPerformed

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JComboBox<String> boolUrgent;
	private javax.swing.JButton btnAjouter;
	private javax.swing.JButton btnAnnuler;
	private javax.swing.JToggleButton btnGenerer;
	private javax.swing.JButton btnGenererPatientUrgent;
	private javax.swing.JButton btnHeureArrivee;
	private javax.swing.JButton btnHeureDeclaration;
	private javax.swing.JButton btnSuppr;
	private javax.swing.JButton btnValider;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel10;
	private javax.swing.JLabel jLabel11;
	private javax.swing.JLabel jLabel12;
	private javax.swing.JLabel jLabel13;
	private javax.swing.JLabel jLabel14;
	private javax.swing.JLabel jLabel15;
	private javax.swing.JLabel jLabel16;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JLabel jLabel4;
	private javax.swing.JLabel jLabel5;
	private javax.swing.JLabel jLabel6;
	private javax.swing.JLabel jLabel7;
	private javax.swing.JLabel jLabel8;
	private javax.swing.JLabel jLabel9;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JScrollPane jScrollPane1;
	private com.raven.swing.TimePicker pickerHeureArrivee;
	private com.raven.swing.TimePicker pickerHeureDeclaration;
	private javax.swing.JComboBox<String> strGravite;
	private javax.swing.JTextField strNbPatientACreer;
	private javax.swing.JTextField strTempsOpe;
	private javax.swing.JTextField strTempsOpeMax;
	private javax.swing.JTextField strTempsOpeMax1;
	private javax.swing.JTextField strTempsOpeMin;
	private javax.swing.JTextField strTempsOpeMin1;
	private javax.swing.JTable tableau;
	// End of variables declaration//GEN-END:variables
}
