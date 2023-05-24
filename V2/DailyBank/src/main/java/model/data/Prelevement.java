package model.data;

import java.time.LocalDate;

/*
 * Attributs mis en public car cette classe ne fait que "véhiculer" des données.
 */

public class Prelevement {

	public int id;
	public int montant;
	public int dateReccurente;
	public String beneficiaire;
	public int idCompteProprio;

	public Prelevement(int id, int montant, int date, String beneficiaire, int idCompte) {
		super();
		this.id = id;
		this.montant = montant;
		this.dateReccurente = date;
		this.beneficiaire = beneficiaire;
		this.idCompteProprio = idCompte;
	}

	public Prelevement(Prelevement p) {
		this(p.id, p.montant, p.dateReccurente, p.beneficiaire, p.idCompteProprio);
	}

	public Prelevement() {
		this(-1, 0, 0, "", -1);
	}

	@Override
	public String toString() {
		return "[" + this.id + "]  " + this.idCompteProprio + " ---( " + this.montant + "€ )---> " + this.beneficiaire
				+ ". > jour "+ this.dateReccurente+" <";
	}

}
