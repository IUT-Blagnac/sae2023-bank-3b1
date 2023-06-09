package model.orm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import model.data.Client;
import model.data.Employe;
import model.orm.exception.DataAccessException;
import model.orm.exception.DatabaseConnexionException;
import model.orm.exception.Order;
import model.orm.exception.RowNotFoundOrTooManyRowsException;
import model.orm.exception.Table;

/**
 * Classe d'accès aux Employe en BD Oracle.
 */
public class Access_BD_Employe {

	public Access_BD_Employe() {
	}

	/**
	 * Recherche d'un employé par son login / mot de passe.
	 *
	 * @param login    login de l'employé recherché
	 * @param password mot de passe donné
	 * @return un Employe ou null si non trouvé
	 * @throws RowNotFoundOrTooManyRowsException La requête renvoie plus de 1 ligne
	 * @throws DataAccessException               Erreur d'accès aux données (requête
	 *                                           mal formée ou autre)
	 * @throws DatabaseConnexionException        Erreur de connexion
	 */
	public Employe getEmploye(String login, String password)
			throws RowNotFoundOrTooManyRowsException, DataAccessException, DatabaseConnexionException {

		Employe employeTrouve;

		try {
			Connection con = LogToDatabase.getConnexion();
			String query = "SELECT * FROM Employe WHERE" + " login = ?" + " AND motPasse = ?";

			PreparedStatement pst = con.prepareStatement(query);
			pst.setString(1, login);
			pst.setString(2, password);

			ResultSet rs = pst.executeQuery();

			System.err.println(query);

			if (rs.next()) {
				int idEmployeTrouve = rs.getInt("idEmploye");
				String nom = rs.getString("nom");
				String prenom = rs.getString("prenom");
				String droitsAccess = rs.getString("droitsAccess");
				String loginTROUVE = rs.getString("login");
				String motPasseTROUVE = rs.getString("motPasse");
				int idAgEmploye = rs.getInt("idAg");

				employeTrouve = new Employe(idEmployeTrouve, nom, prenom, droitsAccess, loginTROUVE, motPasseTROUVE,
						idAgEmploye);
			} else {
				rs.close();
				pst.close();
				// Non trouvé
				return null;
			}

			if (rs.next()) {
				// Trouvé plus de 1 ... bizarre ...
				rs.close();
				pst.close();
				throw new RowNotFoundOrTooManyRowsException(Table.Employe, Order.SELECT,
						"Recherche anormale (en trouve au moins 2)", null, 2);
			}
			rs.close();
			pst.close();
			return employeTrouve;
		} catch (SQLException e) {
			throw new DataAccessException(Table.Employe, Order.SELECT, "Erreur accès", e);
		}
	}

	/**
	 * Recherche des employés par leur numéro ou nom prénom
	 *
	 * @param idAg le numéro de l’agence
	 * @param num le numéro de l’employé recherché
	 * @param debutNom le début du nom de l’employé recherché
	 * @param debutPrenom le début du prénom de l’employé recherché
	 * @return Une liste avec les employés correspondante à la recherche
	 * @throws DataAccessException Problème d’accès a la base de donnée (requête mal formée…)
	 * @throws DatabaseConnexionException Problème de connexion à la base de donnée (mauvais identifiant, problème
	 * 									d’internet…)
	 * @author Émilien FIEU
	 */
	public ArrayList<Employe> getEmployeList(int idAg, int num, String debutNom, String debutPrenom)
			throws DataAccessException, DatabaseConnexionException {

		ArrayList<Employe> employeList = new ArrayList<Employe>();

		try {
			Connection con = LogToDatabase.getConnexion();

			PreparedStatement pst;

			String query;
			if (num != -1) {
				query = "SELECT * FROM EMPLOYE where IDAG = ?";
				query += " AND IDEMPLOYE  = ?";
				query += " ORDER BY NOM";
				pst = con.prepareStatement(query);
				pst.setInt(1, idAg);
				pst.setInt(2, num);

			} else if (!debutNom.equals("")) {
				debutNom = debutNom.toUpperCase() + "%";
				debutPrenom = debutPrenom.toUpperCase() + "%";
				query = "SELECT * FROM EMPLOYE where IDAG = ?";
				query += " AND UPPER(NOM) like ?" + " AND UPPER(PRENOM) like ?";
				query += " ORDER BY NOM";
				pst = con.prepareStatement(query);
				pst.setInt(1, idAg);
				pst.setString(2, debutNom);
				pst.setString(3, debutPrenom);
			} else {
				query = "SELECT * FROM EMPLOYE where IDAG = ?";
				query += " ORDER BY NOM";
				pst = con.prepareStatement(query);
				pst.setInt(1, idAg);
			}


			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				int idEmploye = rs.getInt("IDEMPLOYE");
				String nom = rs.getString("NOM");
				String prenom = rs.getString("PRENOM");
				String droitsAccess = rs.getString("DROITSACCESS");
				String login = rs.getString("LOGIN");
				String motPasse = rs.getString("MOTPASSE");
				int idAgCli = rs.getInt("IDAG");

				employeList.add(new Employe(idEmploye, nom, prenom, droitsAccess, login, motPasse, idAgCli));
			}
			rs.close();
			pst.close();
		} catch (SQLException e) {
			throw new DataAccessException(Table.Client, Order.SELECT, "Erreur accès", e);
		}
		return employeList;
	}

	/**
	 * Ajoute un employé dans la base de donnée
	 * @param employe l’employé à ajouter
	 * @throws DatabaseConnexionException Problème de connexion à la base de donnée (mauvais identifiant, problème
	 * 									d’internet…)
	 * @throws RowNotFoundOrTooManyRowsException Trop ou pas de ligne·s ajoutée·s
	 * @throws DataAccessException Problème d’accès a la base de donnée (requête mal formée…)
	 * @author Émilien FIEU
	 */
	public void insertEmploye(Employe employe) throws DatabaseConnexionException, RowNotFoundOrTooManyRowsException, DataAccessException {
		try {
			System.out.println(employe);

			Connection con = LogToDatabase.getConnexion();

			String query = "INSERT INTO EMPLOYE VALUES (" + "seq_id_client.NEXTVAL" + ", " + "?" + ", " + "?" + ", "
					+ "?" + ", " + "?" + ", " + "?" + ", " + "?" + ")";
			PreparedStatement pst = con.prepareStatement(query);
			pst.setString(1, employe.nom);
			pst.setString(2, employe.prenom);
			pst.setString(3, employe.droitsAccess);
			pst.setString(4, employe.login);
			pst.setString(5, employe.motPasse);
			pst.setInt(6, employe.idAg);



			int result = pst.executeUpdate();
			pst.close();

			if (result != 1) {
				con.rollback();
				throw new RowNotFoundOrTooManyRowsException(Table.Employe, Order.INSERT,
						"Insert anormal (insert de moins ou plus d'une ligne)", null, result);
			}

			query = "SELECT seq_id_client.CURRVAL from DUAL";

			System.err.println(query);
			PreparedStatement pst2 = con.prepareStatement(query);

			ResultSet rs = pst2.executeQuery();
			rs.next();
			int numCliBase = rs.getInt(1);

			con.commit();
			rs.close();
			pst2.close();

			employe.idEmploye = numCliBase;
		} catch (SQLException e) {
			throw new DataAccessException(Table.Client, Order.INSERT, "Erreur accès", e);
		}
	}

	/**
	 * Modifie un employé existant
	 * @param employe l’employé modifié
	 * @throws DataAccessException Problème d’accès a la base de donnée (requête mal formée…)
	 * @throws DatabaseConnexionException Problème de connexion à la base de donnée (mauvais identifiant, problème
	 * 	  								d’internet…)
	 * @throws RowNotFoundOrTooManyRowsException Trop ou pas de ligne·s modifiée·s
	 * @author Émilien FIEU
	 */
	public void updateEmploye(Employe employe) throws DataAccessException, DatabaseConnexionException, RowNotFoundOrTooManyRowsException {
		try {
			Connection con = LogToDatabase.getConnexion();

			String query = "UPDATE EMPLOYE SET NOM = ?, PRENOM = ?, DROITSACCESS = ?, LOGIN = ?, MOTPASSE = ?, IDAG = ? WHERE IDEMPLOYE = ?";

			PreparedStatement pst = con.prepareStatement(query);
			pst.setString(1, employe.nom);
			pst.setString(2, employe.prenom);
			pst.setString(3, employe.droitsAccess);
			pst.setString(4, employe.login);
			pst.setString(5, employe.motPasse);
			pst.setInt(6, employe.idAg);
			pst.setInt(7, employe.idEmploye);

			int result = pst.executeUpdate();
			pst.close();

			if (result != 1) {
				con.rollback();
				throw new RowNotFoundOrTooManyRowsException(Table.Employe, Order.UPDATE,
						"Update anormal (update de moins ou plus d'une ligne)", null, result);
			}

			con.commit();

		} catch (SQLException e) {
			throw new DataAccessException(Table.Employe, Order.UPDATE, "Erreur accès", e);
		}
	}

	/**
	 * Supprime un employé de la base de donnée
	 * @param employe l’employé à supprimer
	 * @throws DatabaseConnexionException Problème de connexion à la base de donnée (mauvais identifiant, problème
	 * 									d’internet…)
	 * @throws RowNotFoundOrTooManyRowsException Trop ou pas de ligne·s supprimée·s
	 * @throws DataAccessException Problème d’accès a la base de donnée (requête mal formée…)
	 * @author Émilien FIEU
	 */
	public void supprimerEmploye(Employe employe) throws DatabaseConnexionException, RowNotFoundOrTooManyRowsException, DataAccessException {
		try {
			Connection con = LogToDatabase.getConnexion();

			String query = "DELETE FROM EMPLOYE WHERE IDEMPLOYE = ?";

			PreparedStatement pst = con.prepareStatement(query);
			pst.setInt(1, employe.idEmploye);

			int result = pst.executeUpdate();
			pst.close();

			if (result != 1) {
				con.rollback();
				throw new RowNotFoundOrTooManyRowsException(Table.Employe, Order.DELETE,
						"Delete anormal (delete de moins ou plus d'une ligne)", null, result);
			}

			con.commit();

		} catch (SQLException e) {
			throw new DataAccessException(Table.Employe, Order.DELETE, "Erreur accès", e);
		}
	}
}
