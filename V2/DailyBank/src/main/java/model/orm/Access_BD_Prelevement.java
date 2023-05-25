package model.orm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import model.data.Client;
import model.data.Prelevement;
import model.orm.exception.DataAccessException;
import model.orm.exception.DatabaseConnexionException;
import model.orm.exception.Order;
import model.orm.exception.RowNotFoundOrTooManyRowsException;
import model.orm.exception.Table;

/**
 * Classe d'accès aux Prelevement en BD Oracle.
 */
public class Access_BD_Prelevement {

	/**
	 * Constructeur.
	 */
	public Access_BD_Prelevement() {
	}


	/**
	 * Recherche des prélèvements par leur identifiant de prélèvement. 
	 *
	 * @param id l'identifiant du prélèvement à rechercher (-1 pour absent)
	 * @param idCompte l'identifiant du compte qui execute le prélèvement.
	 * @return Une liste avec les prélèvements correspondant à la recherche.
	 * @throws DataAccessException Problème d’accès a la base de donnée (requête mal formée…)
	 * @throws DatabaseConnexionException Problème de connexion à la base de donnée (mauvais identifiant, problème
	 * 									d’internet…)
	 * @author Vincent Barette
	 */
	public ArrayList<Prelevement> getPrelevementList(int id, int idCompte)
			throws DataAccessException, DatabaseConnexionException {

		ArrayList<Prelevement> PrelevementList = new ArrayList<Prelevement>();

		try {
			Connection con = LogToDatabase.getConnexion();

			PreparedStatement pst;

			String query;
			if (id != -1) {
				query = "SELECT * FROM PrelevementAutomatique WHERE idPrelev = ?";
				query += " AND idNumCompte = ?";
				query += " ORDER BY idprelev";
				pst = con.prepareStatement(query);
				pst.setInt(1, id);
				pst.setInt(2, idCompte);

			} else {
				query = "SELECT * FROM PrelevementAutomatique WHERE idNumCompte = ?";
				query += " ORDER BY idPrelev";
				pst = con.prepareStatement(query);
				pst.setInt(1, idCompte);
			}


			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				int idPrelev = rs.getInt("idPrelev");
				int montant = rs.getInt("montant");
				int date = rs.getInt("dateRecurrente");
				String beneficiaire = rs.getString("beneficiaire");
				int idNumCompte = rs.getInt("idNumCompte");

				PrelevementList.add(new Prelevement(idPrelev, montant, date, beneficiaire, idNumCompte));
			}
			rs.close();
			pst.close();
		} catch (SQLException e) {
			throw new DataAccessException(Table.Client, Order.SELECT, "Erreur accès", e);
		}
		return PrelevementList;
	}

	/**
	 * Ajoute un prélèvement dans la base de donnée
	 * @param Prelevement Le nouveau prélèvement à ajouter
	 * @throws DatabaseConnexionException Problème de connexion à la base de donnée (mauvais identifiant, problème
	 * 									d’internet…)
	 * @throws RowNotFoundOrTooManyRowsException Trop ou pas de ligne·s ajoutée·s
	 * @throws DataAccessException Problème d’accès a la base de donnée (requête mal formée…)
	 * @author Vincent BARETTE
	 */
	public void insertPrelevement(Prelevement Prelevement) throws DatabaseConnexionException, RowNotFoundOrTooManyRowsException, DataAccessException {
		try {
			System.out.println(Prelevement);

			Connection con = LogToDatabase.getConnexion();

			String query = "INSERT INTO PrelevementAutomatique VALUES (" + "seq_id_prelevauto.NEXTVAL" + ", " + "?" + ", " + "?" + ", "
					+ "?" + ", " + "?" + ")";
			PreparedStatement pst = con.prepareStatement(query);
			pst.setInt(1, Prelevement.montant);
			pst.setInt(2, Prelevement.dateReccurente);
			pst.setString(3, Prelevement.beneficiaire);
			pst.setInt(4, Prelevement.idCompteProprio);



			int result = pst.executeUpdate();
			pst.close();

			if (result != 1) {
				con.rollback();
				throw new RowNotFoundOrTooManyRowsException(Table.PrelevementAutomatique, Order.INSERT,
						"Insert anormal (insert de moins ou plus d'une ligne)", null, result);
			}

			query = "SELECT seq_id_prelevauto.CURRVAL from DUAL";

			System.err.println(query);
			PreparedStatement pst2 = con.prepareStatement(query);

			ResultSet rs = pst2.executeQuery();
			rs.next();
			int numCliBase = rs.getInt(1);

			con.commit();
			rs.close();
			pst2.close();

			Prelevement.id = numCliBase;
		} catch (SQLException e) {
			throw new DataAccessException(Table.Client, Order.INSERT, "Erreur accès", e);
		}
	}

	/**
	 * Modifie un prélèvement existant
	 * @param Prelevement Le prélèvement modifié
	 * @throws DataAccessException Problème d’accès a la base de donnée (requête mal formée…)
	 * @throws DatabaseConnexionException Problème de connexion à la base de donnée (mauvais identifiant, problème
	 * 	  								d’internet…)
	 * @throws RowNotFoundOrTooManyRowsException Trop ou pas de ligne·s modifiée·s
	 * @author Vincent BARETTE
	 */
	public void updatePrelevement(Prelevement Prelevement) throws DataAccessException, DatabaseConnexionException, RowNotFoundOrTooManyRowsException {
		try {
			Connection con = LogToDatabase.getConnexion();

			String query = "UPDATE PrelevementAutomatique SET idPrelev = ?, montant = ?, dateRecurrente = ?, beneficiaire = ?, idNumCompte = ? WHERE IDPrelev = ?";

			PreparedStatement pst = con.prepareStatement(query);
			pst.setInt(1, Prelevement.id);
			pst.setInt(2, Prelevement.montant);
			pst.setInt(3, Prelevement.dateReccurente);
			pst.setString(4, Prelevement.beneficiaire);
			pst.setInt(5, Prelevement.idCompteProprio);
			pst.setInt(6, Prelevement.id);
			
			System.out.println(Prelevement);

			int result = pst.executeUpdate();
			pst.close();

			if (result != 1) {
				con.rollback();
				throw new RowNotFoundOrTooManyRowsException(Table.PrelevementAutomatique, Order.UPDATE,
						"Update anormal (update de moins ou plus d'une ligne)", null, result);
			}

			con.commit();

		} catch (SQLException e) {
			throw new DataAccessException(Table.PrelevementAutomatique, Order.UPDATE, "Erreur accès", e);
		}
	}

	/**
	 * Supprime un prélèvement de la base de donnée
	 * @param Prelevement Le prélèvement à supprimer
	 * @throws DatabaseConnexionException Problème de connexion à la base de donnée (mauvais identifiant, problème
	 * 									d’internet…)
	 * @throws RowNotFoundOrTooManyRowsException Trop ou pas de ligne·s supprimée·s
	 * @throws DataAccessException Problème d’accès a la base de donnée (requête mal formée…)
	 * @author Vincent BARETTE
	 */
	public void supprimerPrelevement(Prelevement Prelevement) throws DatabaseConnexionException, RowNotFoundOrTooManyRowsException, DataAccessException {
		try {
			Connection con = LogToDatabase.getConnexion();

			String query = "DELETE FROM PrelevementAutomatique WHERE IDPrelev = ?";

			PreparedStatement pst = con.prepareStatement(query);
			pst.setInt(1, Prelevement.id);

			int result = pst.executeUpdate();
			pst.close();

			if (result != 1) {
				con.rollback();
				throw new RowNotFoundOrTooManyRowsException(Table.PrelevementAutomatique, Order.DELETE,
						"Delete anormal (delete de moins ou plus d'une ligne)", null, result);
			}

			con.commit();

		} catch (SQLException e) {
			throw new DataAccessException(Table.PrelevementAutomatique, Order.DELETE, "Erreur accès", e);
		}
	}
}
