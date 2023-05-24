
CREATE OR REPLACE PROCEDURE Debit_Exceptionnel	(
	vidNumCompte CompteCourant.idNumCompte%TYPE,
	vMontantDebit Operation.montant%TYPE,
	vTypeOp TypeOperation.idTypeOp%TYPE,
	retour OUT NUMBER)
IS
-- Procédure supposée exécutée par un chef d'agence qui seul a le droit
-- de faire un debit exceptionnel

-- On ne teste pas les exceptions ici, on suppose que cela est fait au niveau JAVA :
-- (cela peut être changé mais il faut être capable d'intercepter et traiter les exceptions
-- SQL depuis JDBC...
-- Contraintes qu'il faut verifier :
--			   le compte n'est pas clôturé (pas fait ci-dessous)

	vSolde CompteCourant.solde%TYPE;
	vNouveauSolde CompteCourant.solde%TYPE;
	
BEGIN
	-- on n'insère pas la dateOp car elle est définie dans la table pour prendre la valeur du jour par défaut
	-- on insere la date du jour (sysdate) + 2 jours pour la date de valeur de l'operation
    
	-- la valeur vMontantDebit est passée en valeur absolue (ex : 120) donc on ajoute un signe - pour que cela devienne un débit (ex: -120)
	
	SELECT solde into  vSolde FROM CompteCourant WHERE idNumCompte = vidNumCompte;
	
	-- ex: -300   := 150    - 450
	vNouveauSolde := vSolde - vMontantDebit;
	-- ex: -300      >=    -400 donc on peut faire le débit

	INSERT INTO Operation (idOperation, montant, dateValeur, idNumCompte, idTypeOp)
	VALUES (seq_id_operation.NEXTVAL, -vMontantDebit, sysdate +2, vidNumCompte, vTypeOp);

	-- on met à jour le solde du compte correspondant à l'opération
	UPDATE CompteCourant
	SET solde = vNouveauSolde
	WHERE idNumCompte = vidNumCompte;
		
	COMMIT;
	retour := 0;
	
END;
/
