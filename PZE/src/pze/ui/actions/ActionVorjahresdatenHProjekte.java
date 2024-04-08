package pze.ui.actions;

import framework.Application;
import framework.business.action.ActionAdapter;
import pze.business.Messages;
import pze.business.objects.projektverwaltung.CoAbruf;
import pze.business.objects.projektverwaltung.CoAuftrag;
import pze.business.objects.reftables.projektverwaltung.CoStatusProjekt;

/**
 * Klasse zum Wegschreiben der Vorjahresdaten von Projekten mit jahresweisem Budget
 * 
 * @author Lisiecki
 *
 */
public class ActionVorjahresdatenHProjekte extends ActionAdapter {

	
	@Override
	public void activate(Object sender) throws Exception {
		
		if (Messages.showYesNoMessage("Vorjahresdaten übertragen", 
				"Möchten Sie die Vorjahresdaten der Projekte mit jahresweisem Budget auf die jeweiligen H-Projekte übertragen?<br>"
				+ "Diese Aktion kann nicht rückgängig gemacht werden."))
		{
			copyAuftraege();
		}
	}
	
	
	/**
	 * Alle Aufträge mit Flag "Budget jahresweise" kopieren/aktualisieren
	 * 
	 * @throws Exception
	 */
	public void copyAuftraege() throws Exception {
		int auftragIdBudgetJahresweise;
		CoAuftrag coAuftrag, coAuftragBudgetJahresweise;
		
		coAuftrag = new CoAuftrag();
		coAuftragBudgetJahresweise = new CoAuftrag();

		// alle Aufträge mit jahresweisem Budget laden
		coAuftrag.loadBudgetJahresweise();
		System.out.println("Anzahl Aufträge: " + coAuftrag.getRowCount());
		
		
		if (!coAuftrag.moveFirst())
		{
			return;
		}
		coAuftrag.begin();

		// Aufträge durchlaufen
		do
		{
			// Kopie des Auftrags mit bereits jahresweise archivierten Daten laden
			coAuftragBudgetJahresweise.loadByID(coAuftrag.getIDBudgetJahresweise());
			
			// wenn diese noch nicht existiert, erstelle sie
			if (!coAuftragBudgetJahresweise.moveFirst())
			{
				auftragIdBudgetJahresweise = coAuftragBudgetJahresweise.createNew();
			}
			else // sonst ID laden, Bearbeitung starten
			{
				auftragIdBudgetJahresweise = coAuftragBudgetJahresweise.getID();
				coAuftragBudgetJahresweise.begin();
			}
			
			
			// Daten des Originalauftrags auf Kopie schreiben bzw. aktualisieren
			coAuftragBudgetJahresweise.setAuftragsNr("H*" + coAuftrag.getAuftragsNr());
			coAuftragBudgetJahresweise.setBestellNr(coAuftrag.getFieldBestellNr().getValue());
			coAuftragBudgetJahresweise.setAngebotsNr(coAuftrag.getFieldAngebotsNr().getValue());
			coAuftragBudgetJahresweise.setEdvNr(coAuftrag.getFieldEdvNr().getValue());
			coAuftragBudgetJahresweise.setBeschreibung(coAuftrag.getFieldBeschreibung().getValue());
			
			coAuftragBudgetJahresweise.setDatumAngebot(coAuftrag.getFieldDatumAngebot().getValue());
			coAuftragBudgetJahresweise.setDatumBestellung(coAuftrag.getFieldDatumBestellung().getValue());
			coAuftragBudgetJahresweise.setDatumAuftragsbestaetigung(coAuftrag.getFieldDatumAuftragsbestaetigung().getValue());
			
			coAuftragBudgetJahresweise.setKundeID(coAuftrag.getFieldKundeID().getValue());
			coAuftragBudgetJahresweise.setStandortKundeID(coAuftrag.getFieldStandortKundeID().getValue());
			coAuftragBudgetJahresweise.setAbteilungKundeID(coAuftrag.getFieldAbteilungKundeID().getValue());
			coAuftragBudgetJahresweise.setAnfordererKundeID(coAuftrag.getFieldAnfordererKundeID().getValue());
			
			coAuftragBudgetJahresweise.setProjektleiterID(coAuftrag.getFieldProjektleiterID().getValue());
			coAuftragBudgetJahresweise.setProjektleiterID2(coAuftrag.getFieldProjektleiterID2().getValue());
			coAuftragBudgetJahresweise.setAbteilungID(coAuftrag.getFieldAbteilungID().getValue());
			coAuftragBudgetJahresweise.setAbteilungsleiterID(coAuftrag.getFieldAbteilungsleiterID().getValue());

			coAuftragBudgetJahresweise.setBestellwert(0);
			coAuftragBudgetJahresweise.setStatusID(CoStatusProjekt.STATUSID_H);

			
			// Daten speichern
			coAuftragBudgetJahresweise.save();
			
			// ID der Kopie in Original-Auftrag speichern
			coAuftrag.setIDBudgetJahresweise(auftragIdBudgetJahresweise);

			// Monatseinsatzblatt anpassen
			updateMonatseinsatzblatt("AuftragID", coAuftrag.getID(), auftragIdBudgetJahresweise);
			
			
			// Abrufe und Kostenstellen kopieren
			copyAbrufe(coAuftrag.getID(), auftragIdBudgetJahresweise);
//			copyKostenstellen(coAuftrag.getID(), auftragIdBudgetJahresweise);
		
		} while (coAuftrag.moveNext());

		
		// ID der Kopie in Original-Auftrag speichern
		coAuftrag.save();
		
		// Aufträge als Referenztabelle neu laden
		Application.getRefTableLoader().updateRefItems(coAuftrag.getResID());

		// Erfolgsmeldung
		Messages.showInfoMessage("Übertragung erfolgreich",
				"Die Vorjahresdaten der Projekte mit jahresweisem Budget wurden auf die jeweiligen H-Projekte übertragen.");
	}
	
	
	/**
	 * Abrufe der Auftragsnummer kopieren/aktualisieren
	 * 
	 * @param auftragIdAlt
	 * @param auftragIdNeu
	 * @throws Exception
	 */
	public void copyAbrufe(int auftragIdAlt, int auftragIdNeu) throws Exception {
		int abrufIdBudgetJahresweise;
		CoAbruf coAbruf, coAbrufBudgetJahresweise;
		
		coAbruf = new CoAbruf();
		coAbrufBudgetJahresweise = new CoAbruf();
		
		// alle Abrufe zu dem Auftrag laden
		coAbruf.loadByAuftragID(auftragIdAlt, true);
		System.out.println("Anzahl Abrufe: " + coAbruf.getRowCount());
		
		
		if (!coAbruf.moveFirst())
		{
			return;
		}
		coAbruf.begin();

		// Abrufe durchlaufen
		do
		{
			// Kopie des Abrufs mit bereits jahresweise archivierten Daten laden
			coAbrufBudgetJahresweise.loadByID(coAbruf.getIDBudgetJahresweise());
			
			// wenn diese noch nicht existiert, erstelle sie
			if (!coAbrufBudgetJahresweise.moveFirst())
			{
				abrufIdBudgetJahresweise = coAbrufBudgetJahresweise.createNew();
			}
			else // sonst ID laden, Bearbeitung starten
			{
				abrufIdBudgetJahresweise = coAbrufBudgetJahresweise.getID();
				coAbrufBudgetJahresweise.begin();
			}
			
			
			// Daten des Originalabrufs auf Kopie schreiben bzw. aktualisieren
			coAbrufBudgetJahresweise.setAbrufNr("H*" + coAbruf.getAbrufNr());
			coAbrufBudgetJahresweise.setAuftragID(auftragIdNeu);
			coAbrufBudgetJahresweise.setEdvNr(coAbruf.getFieldEdvNr().getValue());
			coAbrufBudgetJahresweise.setRevision(coAbruf.getFieldRevision().getValue());
			coAbrufBudgetJahresweise.setBeschreibung(coAbruf.getFieldBeschreibung().getValue());
			
			coAbrufBudgetJahresweise.setDatumAbruf(coAbruf.getFieldDatumAbruf().getValue());
			
			coAbrufBudgetJahresweise.setAbteilungKundeID(coAbruf.getFieldAbteilungKundeID().getValue());
			coAbrufBudgetJahresweise.setAnfordererKundeID(coAbruf.getFieldAnfordererKundeID().getValue());
			coAbrufBudgetJahresweise.setProjektleiterID(coAbruf.getFieldProjektleiterID().getValue());
			coAbrufBudgetJahresweise.setProjektleiterID2(coAbruf.getFieldProjektleiterID2().getValue());
			coAbrufBudgetJahresweise.setAbteilungsleiterID(coAbruf.getFieldAbteilungsleiterID().getValue());

			coAbrufBudgetJahresweise.setFachgebietID(coAbruf.getFieldFachgebiet().getValue());
			coAbrufBudgetJahresweise.setZuordnungID(coAbruf.getFieldZuordnungID().getValue());
			coAbrufBudgetJahresweise.setPaketID(coAbruf.getFieldPaketID().getValue());
			
			coAbrufBudgetJahresweise.setBestellwert(0);
			coAbrufBudgetJahresweise.setStatusID(CoStatusProjekt.STATUSID_H);

			
			// Daten speichern
			coAbrufBudgetJahresweise.save();
			
			// ID der Kopie in Original-Abruf speichern
			coAbruf.setIDBudgetJahresweise(abrufIdBudgetJahresweise);

			// Monatseinsatzblatt anpassen
			updateMonatseinsatzblatt("AbrufID", coAbruf.getID(), abrufIdBudgetJahresweise);
		} while (coAbruf.moveNext());

		
		// ID der Kopie in Original-Auftrag speichern
		coAbruf.save();
		
		// Abrufe als Referenztabelle neu laden
		Application.getRefTableLoader().updateRefItems(coAbruf.getResID());
	}

	
	/**
	 * Kostenstellen der Auftragsnummer kopieren/aktualisieren
	 * 
	 * @param auftragIdAlt
	 * @param auftragIdNeu
	 * @throws Exception
	 */
//	public void copyKostenstellen(int auftragIdAlt, int auftragIdNeu) throws Exception {
//		int abrufIdBudgetJahresweise;
//		CoKostenstelle coKostenstelle, coKostenstelleBudgetJahresweise;
//		
//		coKostenstelle = new CoKostenstelle();
//		coKostenstelleBudgetJahresweise = new CoKostenstelle();
//		
//		// alle Kostenstellen zu dem Auftrag laden
//		coKostenstelle.loadByAuftragID(auftragIdAlt);
//		System.out.println("Anzahl Kostenstellen: " + coKostenstelle.getRowCount());
//		
//		
//		if (!coKostenstelle.moveFirst())
//		{
//			return;
//		}
//		coKostenstelle.begin();
//
//		do
//		{
//			// Kopie der Kostenstellen mit bereits jahresweise archivierten Daten laden
//			coKostenstelleBudgetJahresweise.loadByID(coKostenstelle.getIDBudgetJahresweise());
//			
//			// wenn diese noch nicht existiert, erstelle sie
//			if (!coKostenstelleBudgetJahresweise.moveFirst())
//			{
//				abrufIdBudgetJahresweise = coKostenstelleBudgetJahresweise.createNew();
//			}
//			else // sonst ID laden, Bearbeitung starten
//			{
//				abrufIdBudgetJahresweise = coKostenstelleBudgetJahresweise.getID();
//				coKostenstelleBudgetJahresweise.begin();
//			}
//			
//			
//			// Daten der Originalkostenstellen auf Kopie schreiben bzw. aktualisieren
//			coKostenstelleBudgetJahresweise.setBezeichnung("H*" + coKostenstelle.getBezeichnung());
//			coKostenstelleBudgetJahresweise.setAuftragID(auftragIdNeu);
//
//			
//			// Daten speichern
//			coKostenstelleBudgetJahresweise.save();
//			
//			// ID der Kopie in Original-Abruf speichern
//			coKostenstelle.setIDBudgetJahresweise(abrufIdBudgetJahresweise);
//
//			
//			// Monatseinsatzblatt anpassen
//			updateMonatseinsatzblatt("KostenstelleID", coKostenstelle.getID(), abrufIdBudgetJahresweise);
//		
//		} while (coKostenstelle.moveNext());
//		
//		
//		// ID der Kopie in Original-Kostenstelle speichern
//		coKostenstelle.save();
//		
//		// Kostenstellen als Referenztabelle neu laden
//		Application.getRefTableLoader().updateRefItems(coKostenstelle.getResID());
//	}

	
	/**
	 * Monatseinsatzblatt aktualisieren:</br>
	 * die übergebene Spalte wird angepasst, alte ID durch neue ersetzt
	 * 
	 * @param spalte zu aktualisierende Spalte
	 * @param idAlt
	 * @param idNeu
	 * @throws Exception
	 */
	private void updateMonatseinsatzblatt(String spalte, int idAlt, int idNeu) throws Exception {
		String sql;
		
		sql = "UPDATE tblMonatseinsatzblatt SET " + spalte + "=" + idNeu + " WHERE  " + spalte + "=" + idAlt + " AND YEAR(Datum) < YEAR(GETDATE())";
		Application.getLoaderBase().execute(sql);
	}

}
