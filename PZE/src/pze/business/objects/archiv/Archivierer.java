package pze.business.objects.archiv;

import java.util.Date;

import framework.Application;
import pze.business.Format;
import pze.business.Messages;
import pze.business.objects.CoBrueckentag;
import pze.business.objects.CoFerien;
import pze.business.objects.CoMessage;
import pze.business.objects.CoVerletzerliste;
import pze.business.objects.auswertung.CoAuswertungAmpelliste;
import pze.business.objects.auswertung.CoAuswertungAmpellisteAbteilung;
import pze.business.objects.auswertung.CoAuswertungAmpellisteProjektleiter;
import pze.business.objects.auswertung.CoAuswertungAnAbwesenheit;
import pze.business.objects.auswertung.CoAuswertungAnwesenheit;
import pze.business.objects.auswertung.CoAuswertungAnwesenheitUebersicht;
import pze.business.objects.auswertung.CoAuswertungArbeitszeit;
import pze.business.objects.auswertung.CoAuswertungAuszahlung;
import pze.business.objects.auswertung.CoAuswertungBuchhaltungStundenuebersicht;
import pze.business.objects.auswertung.CoAuswertungDienstreisen;
import pze.business.objects.auswertung.CoAuswertungFreigabe;
import pze.business.objects.auswertung.CoAuswertungKontowerte;
import pze.business.objects.auswertung.CoAuswertungKontowerteZeitraum;
import pze.business.objects.auswertung.CoAuswertungMonatseinsatzblatt;
import pze.business.objects.auswertung.CoAuswertungProjekt;
import pze.business.objects.auswertung.CoAuswertungProjektstundenauswertung;
import pze.business.objects.auswertung.CoAuswertungUrlaub;
import pze.business.objects.auswertung.CoAuswertungUrlaubsplanung;
import pze.business.objects.auswertung.CoAuswertungVerletzerliste;
import pze.business.objects.dienstreisen.CoDienstreise;
import pze.business.objects.dienstreisen.CoDienstreiseAbrechnung;
import pze.business.objects.dienstreisen.CoDienstreisezeit;
import pze.business.objects.personen.CoBuchung;
import pze.business.objects.personen.CoFreigabe;
import pze.business.objects.personen.CoKontowert;
import pze.business.objects.personen.CoPerson;
import pze.business.objects.personen.CoPersonAbteilungsrechte;
import pze.business.objects.personen.CoPersonZeitmodell;
import pze.business.objects.personen.CoVertreter;
import pze.business.objects.personen.monatseinsatzblatt.CoMonatseinsatzblatt;
import pze.business.objects.personen.monatseinsatzblatt.CoMonatseinsatzblattProjekt;
import pze.business.objects.projektverwaltung.CoAbruf;
import pze.business.objects.projektverwaltung.CoAbrufKostenstelle;
import pze.business.objects.projektverwaltung.CoAbrufProjektmerkmal;
import pze.business.objects.projektverwaltung.CoArbeitsplan;
import pze.business.objects.projektverwaltung.CoAuftrag;
import pze.business.objects.projektverwaltung.CoAuftragKostenstelle;
import pze.business.objects.projektverwaltung.CoAuftragProjektmerkmal;
import pze.business.objects.projektverwaltung.CoMitarbeiterProjekt;
import pze.business.objects.projektverwaltung.CoProjektverfolgung;
import pze.business.objects.reftables.CoErsthelfer;
import pze.business.objects.reftables.CoFreigabeberechtigungen;
import pze.business.objects.reftables.projektverwaltung.CoStatusProjekt;
import startup.PZEStartupAdapter;

/**
 * Klasse zum Archivieren von Daten
 * 
 * @author lisiecki
 */
public class Archivierer {

	public static final int ID_ARCHIV = 99;
	private static final String ARCHIV = "ARCHIV";

	
	/**
	 * alle Daten einer Person archivieren bzw. löschen, wenn sie nicht archiviert werden müssen
	 * 
	 * @param personID
	 * @return
	 * @throws Exception
	 */
	public static boolean archivierePerson(int personID) throws Exception {
		int userID;
		CoPerson coPerson;
		
		coPerson = CoPerson.getInstance();
		coPerson.moveToID(personID);
		userID = coPerson.getUserID();
		
		try 
		{
			// weitere Daten der Person löschen
			deleteByPersonID(CoVertreter.TABLE_NAME, personID);
			deleteByPersonID(CoVerletzerliste.TABLE_NAME, personID);
			deleteByPersonID(CoPersonZeitmodell.TABLE_NAME, personID);
			deleteByPersonID(CoErsthelfer.TABLE_NAME, personID);
			deleteByPersonID(CoFreigabeberechtigungen.TABLE_NAME, personID);
			deleteByID(CoFreigabeberechtigungen.TABLE_NAME, "VertreterID", personID);
			deleteByPersonID(CoMonatseinsatzblattProjekt.TABLE_NAME, personID);
			deleteByPersonID(CoPersonAbteilungsrechte.TABLE_NAME, personID);
			deleteByPersonID("stblpersonenpersonenliste", personID);

			// Auswertungen mit der Person als Auswahlkriterium anonymisieren
			anonymisiereEintragByPersonID(CoAuswertungAmpelliste.TABLE_NAME, personID);
			anonymisiereEintragByPersonID(CoAuswertungAnAbwesenheit.TABLE_NAME, personID);
			anonymisiereEintragByPersonID(CoAuswertungAnwesenheitUebersicht.TABLE_NAME, personID);
			anonymisiereEintragByPersonID(CoAuswertungAuszahlung.TABLE_NAME, personID);
			anonymisiereEintragByPersonID(CoAuswertungArbeitszeit.TABLE_NAME, personID);
			anonymisiereEintragByPersonID(CoAuswertungBuchhaltungStundenuebersicht.TABLE_NAME, personID);
			anonymisiereEintragByPersonID(CoAuswertungFreigabe.TABLE_NAME, personID);
			anonymisiereEintragByPersonID(CoAuswertungDienstreisen.TABLE_NAME, personID);
			anonymisiereEintragByPersonID(CoAuswertungKontowerte.TABLE_NAME, personID);
			anonymisiereEintragByPersonID(CoAuswertungKontowerteZeitraum.TABLE_NAME, personID);
			anonymisiereEintragByPersonID(CoAuswertungMonatseinsatzblatt.TABLE_NAME, personID);
			anonymisiereEintragByPersonID(CoAuswertungProjekt.TABLE_NAME, personID);
			anonymisiereEintragByPersonID(CoAuswertungUrlaub.TABLE_NAME, personID);
			anonymisiereEintragByPersonID(CoAuswertungUrlaubsplanung.TABLE_NAME, personID);
			anonymisiereEintragByPersonID(CoAuswertungVerletzerliste.TABLE_NAME, personID);
			anonymisiereEintragByPersonID(CoAuswertungAmpellisteProjektleiter.TABLE_NAME, "ProjektleiterID", personID);

			// Auswertungen der Person löschen
			if (userID > 0)
			{
				deleteByAuswertung(CoAuswertungAmpellisteAbteilung.TABLE_NAME, userID);
				deleteByAuswertung(CoAuswertungAmpellisteProjektleiter.TABLE_NAME, userID);
				deleteByUserID(CoAuswertungAmpelliste.TABLE_NAME, userID);
				deleteByUserID(CoAuswertungAnAbwesenheit.TABLE_NAME, userID);
				deleteByUserID(CoAuswertungAnwesenheit.TABLE_NAME, userID);
				deleteByUserID(CoAuswertungAnwesenheitUebersicht.TABLE_NAME, userID);
				deleteByUserID(CoAuswertungAuszahlung.TABLE_NAME, userID);
				deleteByUserID(CoAuswertungArbeitszeit.TABLE_NAME, userID);
				deleteByUserID(CoAuswertungBuchhaltungStundenuebersicht.TABLE_NAME, userID);
				deleteByUserID(CoAuswertungFreigabe.TABLE_NAME, userID);
				deleteByUserID(CoAuswertungKontowerte.TABLE_NAME, userID);
				deleteByUserID(CoAuswertungKontowerteZeitraum.TABLE_NAME, userID);
				deleteByUserID(CoAuswertungMonatseinsatzblatt.TABLE_NAME, userID);
				deleteByUserID(CoAuswertungProjekt.TABLE_NAME, userID);
				deleteByUserID(CoAuswertungProjektstundenauswertung.TABLE_NAME, userID);
				deleteByUserID(CoAuswertungUrlaub.TABLE_NAME, userID);
				deleteByUserID(CoAuswertungUrlaubsplanung.TABLE_NAME, userID);
				deleteByUserID(CoAuswertungVerletzerliste.TABLE_NAME, userID);
			}
			

			// Person anonymisieren
			anonymisiereEintragByPersonID(CoVertreter.TABLE_NAME, "VertreterID", personID);
			anonymisiereEintragByPersonID(CoAuftrag.TABLE_NAME, "ProjektleiterID", personID);
			anonymisiereEintragByPersonID(ARCHIV+CoAuftrag.TABLE_NAME, "ProjektleiterID", personID);
			anonymisiereEintragByPersonID(CoAuftrag.TABLE_NAME, "ProjektleiterID2", personID);
			anonymisiereEintragByPersonID(ARCHIV+CoAuftrag.TABLE_NAME, "ProjektleiterID2", personID);
			anonymisiereEintragByPersonID(CoAuftrag.TABLE_NAME, "AbteilungsleiterID", personID);
			anonymisiereEintragByPersonID(ARCHIV+CoAuftrag.TABLE_NAME, "AbteilungsleiterID", personID);
			anonymisiereEintragByPersonID(CoAbruf.TABLE_NAME, "AbteilungsleiterAbrufID", personID);
			anonymisiereEintragByPersonID(ARCHIV+CoAbruf.TABLE_NAME, "AbteilungsleiterAbrufID", personID);
			anonymisiereEintragByPersonID(CoAbruf.TABLE_NAME, "ProjektleiterID", personID);
			anonymisiereEintragByPersonID(ARCHIV+CoAbruf.TABLE_NAME, "ProjektleiterID", personID);
			anonymisiereEintragByPersonID(CoAbruf.TABLE_NAME, "ProjektleiterID2", personID);
			anonymisiereEintragByPersonID(ARCHIV+CoAbruf.TABLE_NAME, "ProjektleiterID2", personID);
			
			anonymisiereEintragByPersonID(CoProjektverfolgung.TABLE_NAME, "PLID", personID);
			anonymisiereEintragByPersonID(ARCHIV+CoProjektverfolgung.TABLE_NAME, "PLID", personID);
			anonymisiereEintragByPersonID(CoProjektverfolgung.TABLE_NAME, "ALID", personID);
			anonymisiereEintragByPersonID(ARCHIV+CoProjektverfolgung.TABLE_NAME, "ALID", personID);

			anonymisiereEintragByPersonID(CoFreigabe.TABLE_NAME, "PersonID", personID);
			anonymisiereEintragByPersonID(ARCHIV+CoFreigabe.TABLE_NAME, "PersonID", personID);
			
			anonymisiereEintragByPersonID(CoMitarbeiterProjekt.TABLE_NAME, "PersonID", personID);
			anonymisiereEintragByPersonID(ARCHIV+CoMitarbeiterProjekt.TABLE_NAME, "PersonID", personID);
			
			anonymisiereEintragByPersonID(CoAuswertungAmpelliste.TABLE_NAME, "AbteilungsleiterID", personID);
			anonymisiereEintragByPersonID(CoAuswertungProjektstundenauswertung.TABLE_NAME, "AbteilungsleiterID", personID);
		
			
			// Änderungen der Person anonymisieren
			anonymisiereEintragGeaendertVon(CoBuchung.TABLE_NAME, personID);
			anonymisiereEintragGeaendertVon(ARCHIV+CoBuchung.TABLE_NAME, personID);
			anonymisiereEintragGeaendertVon(CoKontowert.TABLE_NAME, personID);
			anonymisiereEintragGeaendertVon(ARCHIV+CoKontowert.TABLE_NAME, personID);
			anonymisiereEintragGeaendertVon(CoVerletzerliste.TABLE_NAME, personID);
			anonymisiereEintragGeaendertVon(CoAuftrag.TABLE_NAME, personID);
			anonymisiereEintragGeaendertVon(ARCHIV+CoAuftrag.TABLE_NAME, personID);
			anonymisiereEintragGeaendertVon(CoAbruf.TABLE_NAME, personID);
			anonymisiereEintragGeaendertVon(ARCHIV+CoAbruf.TABLE_NAME, personID);
			anonymisiereEintragGeaendertVon(CoMonatseinsatzblatt.TABLE_NAME, personID);
			anonymisiereEintragGeaendertVon(ARCHIV+CoMonatseinsatzblatt.TABLE_NAME, personID);
			anonymisiereEintragGeaendertVon(CoMitarbeiterProjekt.TABLE_NAME, personID);
			anonymisiereEintragGeaendertVon(ARCHIV+CoMitarbeiterProjekt.TABLE_NAME, personID);
			anonymisiereEintragGeaendertVon(CoMessage.TABLE_NAME, personID);
			anonymisiereEintragGeaendertVon(ARCHIV+CoMessage.TABLE_NAME, personID);
			anonymisiereEintragGeaendertVon(CoDienstreise.TABLE_NAME, personID);
			anonymisiereEintragGeaendertVon(ARCHIV+CoDienstreise.TABLE_NAME, personID);
			anonymisiereEintragGeaendertVon(CoDienstreiseAbrechnung.TABLE_NAME, personID);
//			anonymisiereEintragGeaendertVon(ARCHIV+CoDienstreiseAbrechnung.TABLE_NAME, personID); //TODO
			if (PZEStartupAdapter.MODUS_ARBEITSPLAN)
			{
				anonymisiereEintragGeaendertVon(CoArbeitsplan.TABLE_NAME, personID);
				anonymisiereEintragGeaendertVon(ARCHIV+CoArbeitsplan.TABLE_NAME, personID);
			}

			
			// Buchungen, Kontowerte etc. archivieren
			String whereEndePze = getWhereEndePZE(personID);
			moveByBuchung(CoFreigabe.TABLE_NAME, personID, whereEndePze, true); // Freigaben der Buchungen
			moveByBuchung(CoDienstreisezeit.TABLE_NAME, personID, whereEndePze, true); // DR-Zeiten der Buchungen
			moveByPersonID(CoBuchung.TABLE_NAME, personID, whereEndePze, true, true, false);
			moveByPersonID(CoDienstreise.TABLE_NAME, personID, whereEndePze, true, true, false); // DR der Person
			moveByPersonID(CoKontowert.TABLE_NAME, personID, whereEndePze, true, true, false);
			moveByPersonID(CoMessage.TABLE_NAME, personID, whereEndePze, true, true, false);
			
			// Monatseinsatzblatt archivieren und anonymisieren für Projektauswertung
			deleteNullFromMonatseinsatzblattByPerson(CoMonatseinsatzblatt.TABLE_NAME, personID);
			moveByPersonID(CoMonatseinsatzblatt.TABLE_NAME, personID, whereEndePze, true, false, false);
			anonymisiereEintragByPersonID(CoMonatseinsatzblatt.TABLE_NAME, "PersonID", personID);

			// UserID löschen, damit der User gelöscht werden kann
			setNullByPersonID(CoPerson.TABLE_NAME, "UserID", personID);
			
			// Person archivieren
			moveByID(CoPerson.TABLE_NAME, personID, true);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			Messages.showErrorMessage("Archivierung fehlerhaft abgebrochen. Bitte wenden Sie sich an den Administrator.");
			return false;
		}
		
		return true;
	}


	/**
	 * archivierte Daten einer Person wiederherstellen
	 * 
	 * @param personID
	 * @return
	 * @throws Exception
	 */
	public static boolean restorePerson(int personID) throws Exception {
		try
		{
			// Person wiederherstellen; erst die Person, dann die Daten wegen Tabellenbeziehungen
			moveByID(CoPerson.TABLE_NAME, personID, false);

			// Buchungen, Kontowerte etc. wiederherstellen
			moveByPersonID(CoDienstreise.TABLE_NAME, personID, null, false, true, false); // Dienstreisen der Person
			moveByPersonID(CoBuchung.TABLE_NAME, personID, null, false, true, false);
			moveByBuchung(CoFreigabe.TABLE_NAME, personID, null, false); // Freigaben der Buchungen
			moveByBuchung(CoDienstreisezeit.TABLE_NAME, personID, null, false); // Dienstreisezeiten der Buchungen
			moveByPersonID(CoKontowert.TABLE_NAME, personID, null, false, true, false);
			moveByPersonID(CoMessage.TABLE_NAME, personID, null, false, true, false);
			moveByPersonID(CoMonatseinsatzblatt.TABLE_NAME, personID, null, false, true, true);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			Messages.showErrorMessage("Wiederherstellung fehlerhaft abgebrochen. Bitte wenden Sie sich an den Administrator.");
			return false;
		}

		return true;
	}
	

	/**
	 * archivierte Daten einer Person löschen
	 * 
	 * @param personID
	 * @return
	 * @throws Exception
	 */
	public static boolean deletePerson(int personID) throws Exception {
		try
		{
			// Buchungen, Kontowerte und Person löschen
			deleteByBuchung(ARCHIV + CoFreigabe.TABLE_NAME, personID, 0); // Freigaben der Buchungen
			deleteByBuchung(ARCHIV + CoDienstreisezeit.TABLE_NAME, personID, 0); // DR-Zeiten der Buchungen
			deleteByPersonID(ARCHIV + CoDienstreise.TABLE_NAME, personID); // DR der Person
			deleteByPersonID(ARCHIV + CoBuchung.TABLE_NAME, personID);
			deleteByPersonID(ARCHIV + CoKontowert.TABLE_NAME, personID);
			deleteByPersonID(ARCHIV + CoMessage.TABLE_NAME, personID);
			deleteByPersonID(ARCHIV + CoMonatseinsatzblatt.TABLE_NAME, personID);
			deleteByID(ARCHIV + CoPerson.TABLE_NAME, "ID", personID);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			Messages.showErrorMessage("Löschen fehlerhaft abgebrochen. Bitte wenden Sie sich an den Administrator.");
			return false;
		}

		return true;
	}
	
	
	/**
	 * archiviere alle Bewegungsdaten eines Jahres
	 * 
	 * @param jahr
	 * @return
	 * @throws Exception
	 */
	public static boolean archiviereBewegungsdaten(int jahr) throws Exception {
		
		try
		{
			// Verletzerliste und Vertreter löschen
			deleteByJahr(CoVerletzerliste.TABLE_NAME, jahr);
			deleteByJahr(CoVertreter.TABLE_NAME, jahr);
			
			// Buchungen und Kontowerte archivieren
			moveByBuchungJahr(CoFreigabe.TABLE_NAME, jahr, true); // Freigaben der Buchungen
			moveByBuchungJahr(CoDienstreisezeit.TABLE_NAME, jahr, true); // DR-Zeiten der Buchungen
			moveByJahr(CoBuchung.TABLE_NAME, jahr, true, true, false);
			moveDienstreisenByBuchungJahr(CoDienstreise.TABLE_NAME, jahr, true); // DR der Buchungen
			moveByJahr(CoKontowert.TABLE_NAME, jahr, true, true, false);
			moveByJahr(CoMessage.TABLE_NAME, jahr, true, true, false);

			// Monatseinsatzblatt archivieren und anonymisieren für Projektauswertung
//			moveByJahr(CoMonatseinsatzblatt.TABLE_NAME, jahr, true, false, false);
//			archiviereEintragByJahr(CoMonatseinsatzblatt.TABLE_NAME, jahr);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			Messages.showErrorMessage("Archivierung fehlerhaft abgebrochen. Bitte wenden Sie sich an den Administrator.");
			return false;
		}

		return true;
	}

	
	/**
	 * archivierte Daten eines Jahres wiederherstellen
	 * 
	 * @param jahr
	 * @return
	 * @throws Exception
	 */
	public static boolean restoreBewegungsdaten(int jahr) throws Exception {
		try
		{
			// Buchungen und Kontowerte wiederherstellen
			moveDienstreisenByBuchungJahr(CoDienstreise.TABLE_NAME, jahr, false); // DR der Buchungen
			moveByJahr(CoBuchung.TABLE_NAME, jahr, false, true, false);
			moveByBuchungJahr(CoDienstreisezeit.TABLE_NAME, jahr, false); // DR-Zeiten der Buchungen
			moveByBuchungJahr(CoFreigabe.TABLE_NAME, jahr, false); // Freigaben der Buchungen
			moveByJahr(CoKontowert.TABLE_NAME, jahr, false, true, false);
			moveByJahr(CoMessage.TABLE_NAME, jahr, false, true, false);
//			moveByJahr(CoMonatseinsatzblatt.TABLE_NAME, jahr, false, true, true);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			Messages.showErrorMessage("Wiederherstellung fehlerhaft abgebrochen. Bitte wenden Sie sich an den Administrator.");
			return false;
		}

		return true;
	}

	
	/**
	 * archivierte Daten eines Jahres löschen
	 * 
	 * @param jahr
	 * @return
	 * @throws Exception
	 */
	public static boolean deleteBewegungsdaten(int jahr) throws Exception {
		try
		{
			// Buchungen und Kontowerte löschen
			deleteDienstreisenByBuchung(ARCHIV + CoDienstreise.TABLE_NAME, 0, jahr); // DR der Buchungen
			deleteByBuchung(ARCHIV + CoDienstreisezeit.TABLE_NAME, 0, jahr); // DR-Zeiten der Buchungen
			deleteByBuchung(ARCHIV + CoFreigabe.TABLE_NAME, 0, jahr); // Freigaben der Buchungen
			deleteByJahr(ARCHIV + CoBuchung.TABLE_NAME, jahr);
			deleteByJahr(ARCHIV + CoKontowert.TABLE_NAME, jahr);
			deleteByJahr(ARCHIV + CoMessage.TABLE_NAME, jahr);
//			deleteByJahr(ARCHIV + CoMonatseinsatzblatt.TABLE_NAME, jahr);
			
			// Referenzdaten
			deleteByJahr(CoFerien.TABLE_NAME, "DatumBis", jahr);
			deleteByJahr(CoBrueckentag.TABLE_NAME, jahr);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			Messages.showErrorMessage("Löschen fehlerhaft abgebrochen. Bitte wenden Sie sich an den Administrator.");
			return false;
		}

		return true;
	}

	
	/**
	 * archiviere alle Projektdaten eines Jahres
	 * 
	 * @param jahr
	 * @return
	 * @throws Exception
	 */
	public static boolean archiviereProjekte(int jahr) throws Exception {
		
		try
		{
			// Status der H*Projekte von abgeschlossenen Projekten auf abgeschlossen setzen
			updateStatusHProjekte(true);

			// NULL-Values aus dem Monatseinsatzblatt löschen, die müssen nicht kopiert werden
			deleteNullFromMonatseinsatzblattByJahr(CoMonatseinsatzblatt.TABLE_NAME, jahr, "AbrufID", false, false);
			// Monatseinsatzblatt archivieren, alle abgeschlossenen Projekte, auf die in dem Jahr zuletzt gebucht wurde
			moveProjekteByJahr(CoMonatseinsatzblatt.TABLE_NAME, jahr, "AbrufID", false, false, true);
			
			// erst nach dem Archivieren der Abrufe die Aufträge, um zu archivierende Rahmenaufträge zu erkennen
			deleteNullFromMonatseinsatzblattByJahr(CoMonatseinsatzblatt.TABLE_NAME, jahr, "AuftragID", false, true);
			moveProjekteByJahr(CoMonatseinsatzblatt.TABLE_NAME, jahr, "AuftragID", false, true, true);
			
			// Projekte in Auswertung etc. löschen
			setNullByProjekt(CoAuswertungAmpelliste.TABLE_NAME, "AbrufID", true);
			setNullByProjekt(CoAuswertungAmpelliste.TABLE_NAME, "AuftragID", true);
			setNullByProjekt(CoAuswertungProjektstundenauswertung.TABLE_NAME, "AbrufID", true);
			setNullByProjekt(CoAuswertungProjektstundenauswertung.TABLE_NAME, "AuftragID", true);
			setNullByProjekt(CoDienstreise.TABLE_NAME, "AbrufID", true); // TODO ggf. muss das wiederherstellbar sein, wenn DR-Korrektur etc. gemacht wird
			setNullByProjekt(CoDienstreise.TABLE_NAME, "AuftragID", true); // ggf. DR mit move(nicht überschreiben) schon archivieren
			// auch in ArchivCoDr =null? sonst Fehler wenn DR archiviert, dann projekte löschen
			// DR-Archivierung nochmal genau prüfen, auch wo evtl. personen eingetragen sind
			deleteByProjekt(CoMonatseinsatzblattProjekt.TABLE_NAME, "AbrufID");
			deleteByProjekt(CoMonatseinsatzblattProjekt.TABLE_NAME, "AuftragID");
			
			// Messages, Projektverfolgung, Projekt-Mitarbeiterzuordnung, Projektmerkmale und Kostenstellen archivieren
			// alle abgeschlossenen Projekte für die keine Stunden mehr gebucht sind (die sind dann bereits archiviert)
			moveByProjekt(CoProjektverfolgung.TABLE_NAME, "AbrufID", false, false, true, true);
			moveByProjekt(CoProjektverfolgung.TABLE_NAME, "AuftragID", false, true, false, true);
			moveByProjekt(CoMessage.TABLE_NAME, "AuftragID", false, true, false, true);
			moveByProjekt(CoMessage.TABLE_NAME, "AbrufID", false, false, true, true);
			moveByProjekt(CoAuftragProjektmerkmal.TABLE_NAME, "AuftragID", false, true, false, true);
			moveByProjekt(CoAbrufProjektmerkmal.TABLE_NAME, "AbrufID", false, false, true, true);
			moveByProjekt(CoAuftragKostenstelle.TABLE_NAME, "AuftragID", false, true, false, true);
			moveByProjekt(CoAbrufKostenstelle.TABLE_NAME, "AbrufID", false, false, true, true);
			moveByProjekt(CoMitarbeiterProjekt.TABLE_NAME, "AbrufID", false, false, true, true);
			moveByProjekt(CoMitarbeiterProjekt.TABLE_NAME, "AuftragID", false, true, false, true);
			if (PZEStartupAdapter.MODUS_ARBEITSPLAN)
			{
				moveByProjekt(CoArbeitsplan.TABLE_NAME, "AbrufID", false, false, true, true);
				moveByProjekt(CoArbeitsplan.TABLE_NAME, "AuftragID", false, true, false, true);
			}

			// Projekte archivieren, alle abgeschlossenen Projekte für die keine Stunden mehr gebucht sind (die sind dann bereits archiviert)
			moveByProjekt(CoAbruf.TABLE_NAME, "AbrufID", true, false, true, true);
			moveByProjekt(CoAuftrag.TABLE_NAME, "AuftragID", true, true, false, true);

			// Status der H*Projekte von abgeschlossenen Projekten wieder zurücksetzen, 
			// falls sie nicht archiviert wurden, sollen sie nicht bei den abgeschlossenen Projekten auftauchen
			updateStatusHProjekte(false);

			
			// alle Projekte im Archiv ohne Stunden löschen, die müssen nicht im Archiv bleiben
			// ist so mit vorhanden Methoden möglich, als wenn die Projekte vor der Archivierung gelöscht werden
			deleteProjekte(0);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			Messages.showErrorMessage("Archivierung fehlerhaft abgebrochen. Bitte wenden Sie sich an den Administrator.");
			return false;
		}

		return true;
	}

	
	/**
	 * archivierte Projektdaten wiederherstellen
	 * 
	 * @param jahr
	 * @return
	 * @throws Exception
	 */
	public static boolean restoreProjekte(int jahr) throws Exception {
		
		try
		{
			// Projekte wiederherstellen, berechnete Spalte Sollstunden muss entfernt und neu hinzugefügt werden, sonst funktioniert das Kopieren nicht
			removeSollstundenBerechnung(CoAbruf.TABLE_NAME);
			removeSollstundenBerechnung(CoAuftrag.TABLE_NAME);
			moveProjekteByJahr(CoAuftrag.TABLE_NAME, jahr, "AuftragID", true, true, false);
			moveProjekteByJahr(CoAbruf.TABLE_NAME, jahr, "AbrufID", true, false, false);
			addSollstundenBerechnung(CoAbruf.TABLE_NAME);
			addSollstundenBerechnung(CoAuftrag.TABLE_NAME);

			// Messages, Projekt-Mitarbeiterzuordnung, Projektmerkmale und Kostenstellen wiederherstellen
			restoreByProjekt(CoMessage.TABLE_NAME, "AuftragID", true, false, false);
			restoreByProjekt(CoMessage.TABLE_NAME, "AbrufID", false, true, false);
			restoreByProjekt(CoAuftragProjektmerkmal.TABLE_NAME, "AuftragID", true, false, false);
			restoreByProjekt(CoAbrufProjektmerkmal.TABLE_NAME, "AbrufID", false, true, false);
			restoreByProjekt(CoAuftragKostenstelle.TABLE_NAME, "AuftragID", true, false, false);
			restoreByProjekt(CoAbrufKostenstelle.TABLE_NAME, "AbrufID", false, true, false);
			restoreByProjekt(CoProjektverfolgung.TABLE_NAME, "AuftragID", true, false, false);
			restoreByProjekt(CoProjektverfolgung.TABLE_NAME, "AbrufID", false, true, false);
			restoreByProjekt(CoMitarbeiterProjekt.TABLE_NAME, "AbrufID", false, true, false);
			restoreByProjekt(CoMitarbeiterProjekt.TABLE_NAME, "AuftragID", true, false, false);
			if (PZEStartupAdapter.MODUS_ARBEITSPLAN)
			{
				restoreByProjekt(CoArbeitsplan.TABLE_NAME, "AbrufID", false, true, false);
				restoreByProjekt(CoArbeitsplan.TABLE_NAME, "AuftragID", true, false, false);
			}
			
			// Monatseinsatzblatt wiederherstellen
			restoreByProjekt(CoMonatseinsatzblatt.TABLE_NAME, jahr, "AuftragID", true, true, false);
			restoreByProjekt(CoMonatseinsatzblatt.TABLE_NAME, jahr, "AbrufID", false, true, false);
			
			// Status der wiederhergestellten H*Projekte auf H*Projekt setzen
			updateStatusHProjekte(false);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			Messages.showErrorMessage("Wiederherstellung fehlerhaft abgebrochen. Bitte wenden Sie sich an den Administrator.");
			return false;
		}

		return true;
	}

	
	/**
	 * archivierte Daten eines Jahres löschen
	 * 
	 * @param jahr
	 * @return
	 * @throws Exception
	 */
	public static boolean deleteProjekte(int jahr) throws Exception {
		String selectAuftragID, selectAbrufID;
		
		try
		{
			// Projekte für ein Jahr löschen
			if (jahr > 0)
			{
				// Projekte löschen
				deleteProjekteByJahr(CoAuftrag.TABLE_NAME, jahr, "AuftragID", true, true);
				deleteProjekteByJahr(CoAbruf.TABLE_NAME, jahr, "AbrufID", true, false);
				selectAuftragID = null;
				selectAbrufID = null;
			}
			else // IDs aller zu löschen Projekte ohne Stunden, unabhängig vom Jahr
			{
				selectAuftragID = getSelectIDsOhneStunden(CoAuftrag.TABLE_NAME, true, false);
				selectAbrufID = getSelectIDsOhneStunden(CoAbruf.TABLE_NAME, false, false);
			}
			
			// Messages, Projekt-Mitarbeiterzuordnung, Projektmerkmale und Kostenstellen löschen
			deleteByProjekt(CoMessage.TABLE_NAME, "AuftragID", true, false, selectAuftragID);
			deleteByProjekt(CoMessage.TABLE_NAME, "AbrufID", false, true, selectAbrufID);
			deleteByProjekt(CoAuftragProjektmerkmal.TABLE_NAME, "AuftragID", true, false, selectAuftragID);
			deleteByProjekt(CoAbrufProjektmerkmal.TABLE_NAME, "AbrufID", false, true, selectAbrufID);
			deleteByProjekt(CoAuftragKostenstelle.TABLE_NAME, "AuftragID", true, false, selectAuftragID);
			deleteByProjekt(CoAbrufKostenstelle.TABLE_NAME, "AbrufID", false, true, selectAbrufID);
			deleteByProjekt(CoProjektverfolgung.TABLE_NAME, "AuftragID", true, false, selectAuftragID);
			deleteByProjekt(CoProjektverfolgung.TABLE_NAME, "AbrufID", false, true, selectAbrufID);
			deleteByProjekt(CoMitarbeiterProjekt.TABLE_NAME, "AbrufID", false, true, selectAbrufID);
			deleteByProjekt(CoMitarbeiterProjekt.TABLE_NAME, "AuftragID", true, false, selectAuftragID);
			if (PZEStartupAdapter.MODUS_ARBEITSPLAN)
			{
				deleteByProjekt(CoArbeitsplan.TABLE_NAME, "AbrufID", false, true, selectAbrufID);
				deleteByProjekt(CoArbeitsplan.TABLE_NAME, "AuftragID", true, false, selectAuftragID);
			}
			
			// Monatseinsatzblatt löschen
			deleteByProjekt(CoMonatseinsatzblatt.TABLE_NAME, "AuftragID", true, true, selectAuftragID);
			deleteByProjekt(CoMonatseinsatzblatt.TABLE_NAME, "AbrufID", false, true, selectAbrufID);
			
			
			// alle Projekte ohne Stunden aus dem Archiv löschen
			if (jahr == 0)
			{
				deleteByProjekt(CoAuftrag.TABLE_NAME, "ID", true, false, selectAuftragID);
				deleteByProjekt(CoAbruf.TABLE_NAME, "ID", false, true, selectAbrufID);
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			Messages.showErrorMessage("Löschen fehlerhaft abgebrochen. Bitte wenden Sie sich an den Administrator.");
			return false;
		}

		return true;
	}


	/**
	 * Berechnung der Sollstunden beim Wiederherstellen entfernen
	 * 
	 * @param tblName
	 * @throws Exception
	 */
	private static void removeSollstundenBerechnung(String tblName) throws Exception {
		String sql, colName;
		
		colName = "Sollstunden";
		
		sql = "ALTER TABLE " + tblName + " DROP COLUMN " + colName;
		Application.getLoaderBase().execute(sql);
		
		sql = "ALTER TABLE " + tblName + " ADD " + colName + " INT";
		Application.getLoaderBase().execute(sql);
	}

	
	/**
	 * Berechnung der Sollstunden wieder einfügen
	 * 
	 * @param tblName
	 * @throws Exception
	 */
	private static void addSollstundenBerechnung(String tblName) throws Exception {
		String sql, colName;
		
		colName = "Sollstunden";
		
		sql = "ALTER TABLE " + tblName + " DROP COLUMN " + colName;
		Application.getLoaderBase().execute(sql);

		sql = "ALTER TABLE " + tblName + " ADD " + colName + " AS (([Bestellwert]-[UVG])-[Puffer])";
		Application.getLoaderBase().execute(sql);
	}

	
	/**
	 * NULL-Werte aus dem Monatseinsatzblatt entfernen, die müssen nicht archiviert werden
	 * 
	 * @param tblName
	 * @param jahr
	 * @param id
	 * @throws Exception
	 */
	private static void deleteNullFromMonatseinsatzblattByJahr(String tblName, int jahr, String id, boolean isPK, boolean isAuftrag) throws Exception {
		String sql;

		// Statement zum Löschen
		sql = " DELETE FROM " + getPraefixMoveFromTbl(true) + tblName 
				+ " WHERE " 
				+ "("
				+ (isPK ? "ID" : id) + " IN (SELECT " +  id + " FROM " + CoArchivProjekte.getDatabase(false, isAuftrag, !isAuftrag) + " WHERE jahr=" + jahr + ")"
				+ " OR "
				+ (isPK ? "ID" : id) + " NOT IN " + getSelectProjekteMitStunden(id, true)
				+ ")"
				+ " AND AbrufID IS " + (isAuftrag ? "" : " NOT ") + " NULL"
				+ " AND (WertZeit IS NULL OR WertZeit = 0) "; // Null-Values filtern

		
		System.out.println(sql);
		Application.getLoaderBase().execute(sql);
	}

	
	/**
	 * NULL-Werte aus dem Monatseinsatzblatt entfernen, die müssen nicht archiviert werden
	 * 
	 * @param tblName
	 * @param personID
	 * @throws Exception
	 */
	private static void deleteNullFromMonatseinsatzblattByPerson(String tblName, int personID) throws Exception {
		String sql;

		// Statement zum Löschen
		sql = " DELETE FROM " + getPraefixMoveFromTbl(true) + tblName 
				+ " WHERE PersonID=" + personID
				+ " AND (WertZeit IS NULL OR WertZeit = 0) "; // Null-Values filtern

		
		System.out.println(sql);
		Application.getLoaderBase().execute(sql);
	}


	/**
	 * Daten einer Person in einer Tabelle löschen
	 * 
	 * @param tblName
	 * @param personID
	 * @throws Exception
	 */
	private static void deleteByPersonID(String tblName, int personID) throws Exception {
		deleteByID(tblName, "PersonID", personID);
	}
	

	/**
	 * Daten eines Users in einer Tabelle löschen
	 * 
	 * @param tblName
	 * @param userID
	 * @throws Exception
	 */
	private static void deleteByUserID(String tblName, int userID) throws Exception {
		// prüfen ob UserID noch existiert
		if (userID == 0)
		{
			return;
		}

		// Daten löschen
		deleteByID(tblName, "UserID", userID);
	}
	

	/**
	 * Daten einer ID in einer Tabelle löschen
	 * 
	 * @param tblName
	 * @param ID
	 * @param colName Spaltenname mit der PersonID
	 * @throws Exception
	 */
	private static void deleteByID(String tblName, String colName, int ID) throws Exception {
		String sql;
		
		sql = "DELETE FROM " + tblName + " WHERE "
				+ (ID > 0 ? colName + "=" + ID : "");
		
		// Daten löschen
		Application.getLoaderBase().execute(sql);
	}
	

	/**
	 * Daten einer Archiv-Tabelle anhand der Buchungen einer Person oder eines Jahres löschen
	 * 
	 * @param tblName
	 * @param personID
	 * @param jahr
	 * @throws Exception
	 */
	private static void deleteByBuchung(String tblName, int personID, int jahr) throws Exception {
		String sql;
		
		sql = "DELETE FROM " + tblName + " WHERE BuchungID IN (SELECT ID FROM ARCHIVtblBuchung WHERE"
				+ (personID > 0 ? " PersonID=" + personID : "") 
				+ (jahr > 0 ? " YEAR(Datum)=" + jahr : "")
				+ ")";
		
		// Daten löschen
		Application.getLoaderBase().execute(sql);
	}


	/**
	 * Dienstreisen einer Archiv-Tabelle anhand der Buchungen einer Person oder eines Jahres löschen
	 * 
	 * @param tblName
	 * @param personID
	 * @param jahr
	 * @throws Exception
	 */
	private static void deleteDienstreisenByBuchung(String tblName, int personID, int jahr) throws Exception {
		String sql;
		
		sql = "DELETE FROM " + tblName + " WHERE ID IN (SELECT DienstreiseID FROM ARCHIVtblBuchung WHERE"
				+ (personID > 0 ? " PersonID=" + personID : "") 
				+ (jahr > 0 ? " YEAR(Datum)=" + jahr : "")
				+ ")";
		
		// Daten löschen
		Application.getLoaderBase().execute(sql);
	}


	/**
	 * Daten einer Archiv-Tabelle anhand der Buchungen einer Person oder eines Jahres löschen
	 * 
	 * @param tblName
	 * @param userID
	 * @param jahr
	 * @throws Exception
	 */
	private static void deleteByAuswertung(String tblName, int userID) throws Exception {
		String sql;
		
		// prüfen ob UserID noch existiert
		if (userID == 0)
		{
			return;
		}
		
		// Auswertung ders Users löschen
		sql = "DELETE FROM " + tblName + " WHERE AuswertungID IN (SELECT ID FROM tblAuswertungAmpelliste WHERE"
				+ (userID > 0 ? " UserID=" + userID : "") 
				+ ")";
		
		// Daten löschen
		Application.getLoaderBase().execute(sql);
	}


	/**
	 * Daten eines Jahres in einer Tabelle löschen
	 * 
	 * @param tblName
	 * @param jahr
	 * @throws Exception
	 */
	private static void deleteByJahr(String tblName, int jahr) throws Exception {
		deleteByJahr(tblName, "Datum", jahr);
	}
	

	/**
	 * Daten eines Jahres in einer Tabelle löschen
	 * 
	 * @param tblName
	 * @param colName
	 * @param jahr
	 * @throws Exception
	 */
	private static void deleteByJahr(String tblName, String colName, int jahr) throws Exception {
		String sql;
		
		sql = "DELETE FROM " + tblName + " WHERE YEAR(" + colName + ")=" + jahr;
		
		// Daten löschen
		Application.getLoaderBase().execute(sql);
	}
	

	/**
	 * Setze die PersonID für den "Geändert-von"-Eintrag in einer Tabelle auf die Archiv-Person, damit die Person archiviert werden kann
	 * 
	 * @param tblName
	 * @param personID
	 * @throws Exception
	 */
	private static void anonymisiereEintragGeaendertVon(String tblName, int personID) throws Exception {
		anonymisiereEintragByPersonID(tblName, "GeaendertVonID", personID);
	}

	
	/**
	 * Setze die PersonID für einen Eintrag in einer Tabelle auf die Archiv-Person, damit die Person archiviert werden kann
	 * 
	 * @param tblName
	 * @param eintragColName
	 * @param personID
	 * @throws Exception
	 */
	private static void anonymisiereEintragByPersonID(String tblName, int personID) throws Exception {
		anonymisiereEintragByPersonID(tblName, "PersonID", personID);
	}
	
	
	/**
	 * Setze die PersonID für einen Eintrag in einer Tabelle auf die Archiv-Person, damit die Person archiviert werden kann
	 * 
	 * @param tblName
	 * @param eintragColName
	 * @param personID
	 * @throws Exception
	 */
	private static void anonymisiereEintragByPersonID(String tblName, String eintragColName, int personID) throws Exception {
		String sql;
		
		sql = "UPDATE " + tblName + " SET " + eintragColName + "= " + ID_ARCHIV + " WHERE " + eintragColName + "=" + personID;
		
		// Update ausführen
		Application.getLoaderBase().execute(sql);
	}
	
	
//	/**
//	 * Setze die PersonID für einen Eintrag in einer Tabelle auf die Archiv-Person, damit das Jahr archiviert wird
//	 * 
//	 * @param tblName
//	 * @param jahr
//	 * @throws Exception
//	 */
//	private static void archiviereEintragByJahr(String tblName, int jahr) throws Exception {
//		String sql;
//		
//		sql = "UPDATE " + tblName + " SET PersonID= " + ID_ARCHIV + " WHERE YEAR(Datum)=" + jahr;
//		
//		// Update ausführen
//		Application.getLoaderBase().execute(sql);
//	}
	

//	/**
//	 * Daten einer Tabelle anhand der Buchungen einer Person archivieren und die alten löschen
//	 * 
//	 * @param tblName
//	 * @param personID
//	 * @param whereEndePze 
//	 * @throws Exception
//	 */
//	private static void copyToArchivByBuchung(String tblName, int personID, String whereEndePze) throws Exception {
//		String select;
//		
//		select = " SELECT * FROM " + tblName + " WHERE BuchungID IN (SELECT ID FROM tblBuchung WHERE"
//				+ (personID > 0 ? " PersonID=" + personID 
//						+ (whereEndePze != null ? whereEndePze : "")
//						: "") + ")";
//		
//		copyToArchiv(tblName, select, whereEndePze);
//	}
	

	/**
	 * Daten einer Tabelle anhand der Buchungen einer Person archivieren und die alten Daten löschen
	 * 
	 * @param tblName
	 * @param personID
	 * @param whereEndePze 
	 * @throws Exception
	 */
	private static void moveByBuchung(String tblName, int personID, String whereEndePze, boolean toArchiv) throws Exception {
		String select;

		select = " SELECT * FROM " + getPraefixMoveFromTbl(toArchiv) + tblName + " WHERE BuchungID IN "
				+ " (SELECT ID FROM " + /* getPraefixMoveFromTbl(toArchiv) + */ "tblBuchung WHERE"
				+ (personID > 0 ? " PersonID=" + personID 
						+ (whereEndePze != null ? whereEndePze : "")
						: "") + ")";

		move(tblName, select, whereEndePze, toArchiv);
	}

	
	/**
	 * Daten einer Tabelle für eine Person archivieren und die alten Daten löschen
	 * 
	 * @param tblName
	 * @param personID
	 * @param whereEndePze 
	 * @throws Exception
	 */
	private static void moveByPersonID(String tblName, int personID, String whereEndePze, boolean toArchiv, boolean datenLoeschen, boolean datenUeberschreiben)
			throws Exception
	{
		String select;

		select = " SELECT * FROM " + getPraefixMoveFromTbl(toArchiv) + tblName + " WHERE "
				+ (personID > 0 ? " PersonID=" + personID 
						+ (whereEndePze != null && !tblName.equals(CoDienstreise.TABLE_NAME) ? whereEndePze : "")
						: "")
				+ (tblName.equals(CoMonatseinsatzblatt.TABLE_NAME) ? " AND AuftragID IN (SELECT ID FROM tblAuftrag) "
						+ " AND (AbrufID IS NULL OR AbrufID IN (SELECT ID FROM tblAbruf))" : "")
				+ (tblName.equals(CoMessage.TABLE_NAME) ? " AND (AuftragID IS NULL OR AuftragID IN (SELECT ID FROM tblAuftrag)) "
						+ " AND (AbrufID IS NULL OR AbrufID IN (SELECT ID FROM tblAbruf))" : "")
				;
		
		move(tblName, select, whereEndePze, toArchiv, datenLoeschen, datenUeberschreiben);
	}


//	/**
//	 * Daten einer Tabelle für eine Person archivieren und die alten löschen
//	 * 
//	 * @param tblName
//	 * @param personID
//	 * @param whereEndePze 
//	 * @throws Exception
//	 */
//	private static void copyToArchivByPersonID(String tblName, int personID, String whereEndePze) throws Exception {
//		String select;
//
//		select = " SELECT * FROM " + tblName + " WHERE "
//				+ (personID > 0 ? " PersonID=" + personID 
//						+ (whereEndePze != null ? whereEndePze : "")
//						: "");
//		
//		copyToArchiv(tblName, select, whereEndePze);
//	}

	
//	/**
//	 * Daten einer Tabelle für eine ID archivieren und die alten löschen
//	 * 
//	 * @param tblName
//	 * @param id
//	 * @throws Exception
//	 */
//	private static void copyToArchivByID(String tblName, int id) throws Exception {
//		String select;
//		
//		select = " SELECT * FROM " + tblName + " WHERE "
//				+ (id > 0 ? " ID=" + id : "");
//		
//		copyToArchiv(tblName, select, null);
//	}
//	
	
	/**
	 * Daten einer Tabelle für eine ID archivieren und die alten Daten löschen
	 * 
	 * @param tblName
	 * @param id
	 * @throws Exception
	 */
	private static void moveByID(String tblName, int id, boolean toArchiv) throws Exception {
		String select;
		
		select = " SELECT * FROM " + getPraefixMoveFromTbl(toArchiv) + tblName + " WHERE "
				+ (id > 0 ? " ID=" + id : "");
		
		move(tblName, select, null, toArchiv);
	}
	
	
//	/**
//	 * Daten einer Tabelle für ein Jahr anhand der Buchungen archivieren und die alten löschen
//	 * 
//	 * @param tblName
//	 * @param jahr
//	 * @throws Exception
//	 */
//	private static void copyToArchivByBuchungJahr(String tblName, int jahr) throws Exception {
//		String select;
//		
//		select = " SELECT * FROM " + tblName + " WHERE BuchungID IN (SELECT ID FROM tblBuchung WHERE YEAR(Datum)=" + jahr + ")";
//		
//		copyToArchiv(tblName, select, null);
//	}


	/**
	 * Daten einer Tabelle für ein Jahr anhand der Buchungen archivieren und die alten Daten löschen
	 * 
	 * @param tblName
	 * @param jahr
	 * @throws Exception
	 */
	private static void moveByBuchungJahr(String tblName, int jahr, boolean toArchiv) throws Exception {
		String select;
		
		select = " SELECT * FROM " + getPraefixMoveFromTbl(toArchiv) + tblName + " WHERE BuchungID IN "
				+ " (SELECT ID FROM " + /* getPraefixMoveFromTbl(toArchiv) +*/ "tblBuchung "
				+ " WHERE PersonID IN (SELECT ID FROM tblPerson) AND YEAR(Datum)=" + jahr + ")";
		
		move(tblName, select, null, toArchiv);
	}


	/**
	 * Dienstreisen für ein Jahr anhand der Buchungen archivieren und die alten Daten löschen
	 * 
	 * @param tblName
	 * @param jahr
	 * @throws Exception
	 */
	private static void moveDienstreisenByBuchungJahr(String tblName, int jahr, boolean toArchiv) throws Exception {
		String select;
		
		select = " SELECT * FROM " + getPraefixMoveFromTbl(toArchiv) + tblName + " WHERE ID IN "
				+ " (SELECT DienstreiseID ID FROM ARCHIVtblBuchung "
				+ " WHERE PersonID IN (SELECT ID FROM tblPerson) AND YEAR(Datum)=" + jahr + ")";
		
		move(tblName, select, null, toArchiv);
	}


//	/**
//	 * Daten einer Tabelle für ein Jahr archivieren und die alten löschen
//	 * 
//	 * @param tblName
//	 * @param personID
//	 * @throws Exception
//	 */
//	private static void copyToArchivByJahr(String tblName, int jahr) throws Exception { 
//		String select;
//		
//		select = " SELECT * FROM " + tblName + " WHERE YEAR(Datum)=" + jahr;
//		
//		copyToArchiv(tblName, select, null);
//	}
	
	
	/**
	 * Daten einer Tabelle für ein Jahr archivieren und die alten Daten löschen
	 * 
	 * @param tblName
	 * @param jahr
	 * @throws Exception
	 */
	private static void moveByJahr(String tblName, int jahr, boolean toArchiv, boolean datenLoeschen, boolean datenUeberschreiben) throws Exception {
		String select;

		select = " SELECT * FROM " + getPraefixMoveFromTbl(toArchiv) + tblName + " WHERE PersonID<>" + ID_ARCHIV 
				+ " AND PersonID IN (SELECT ID FROM tblPerson) AND YEAR(Datum)=" + jahr;
		
		move(tblName, select, null, toArchiv, datenLoeschen, datenUeberschreiben);
	}


//	/**
//	 * Daten einer Tabelle archivieren und die alten löschen
//	 * 
//	 * @param tblName
//	 * @param select vollständiges SQL-Statement zur Einschränkung der Daten der Tabelle
//	 * @param whereEndePze
//	 * @throws Exception
//	 */
//	private static void copyToArchiv(String tblName, String select, String whereEndePze) throws Exception {
//		String sql;
//		
//		// Daten ins Archiv kopieren
//		sql = "INSERT INTO " + ARCHIV + tblName + select;
//		Application.getLoaderBase().execute(sql);
//		
//		// alte Daten löschen
//		sql = select.replace("SELECT *", "DELETE");
//		// alle daten, nicht nur bis zum PZE-Ende der Person
//		if (whereEndePze != null)
//		{
//			sql = sql.replace(whereEndePze, "");
//		}
//		Application.getLoaderBase().execute(sql);
//	}
	

	/**
	 * Daten einer Tabelle verschieben und in der ursprünglichen Tabelle löschen
	 * 
	 * @param tblName
	 * @param select vollständiges SQL-Statement zur Einschränkung der Daten der Tabelle
	 * @param whereEndePze
	 * @throws Exception
	 */
	private static void move(String tblName, String select, String whereEndePze, boolean toArchiv) throws Exception {
		move(tblName, select, whereEndePze, toArchiv, true, false);
	}
	

	/**
	 * 
	 * @param tblName
	 * @param select
	 * @param whereEndePze
	 * @param toArchiv
	 * @param datenLoeschen alte Daten Löschen oder nur kopieren
	 * @param datenUeberschreiben Daten in Zieldatei überschreiben (sofern vorhanden)
	 * @throws Exception
	 */
	private static void move(String tblName, String select, String whereEndePze, boolean toArchiv,
			boolean datenLoeschen, boolean datenUeberschreiben) throws Exception 
	{
		String sql;
		System.out.println(select);
		
		// ggf. bereits vorhandene Daten löschen
		if (datenUeberschreiben)
		{
			sql = "DELETE FROM " + getPraefixMoveToTbl(toArchiv) + tblName + " WHERE ID IN (" + select.replace("SELECT *", "SELECT ID") + ")";
			Application.getLoaderBase().execute(sql);
		}
		
		// Daten kopieren, sofern sie nicht noch vorhanden sind
		sql = "INSERT INTO " + getPraefixMoveToTbl(toArchiv) + tblName + " " + select 
				+ " AND ID NOT IN (SELECT ID FROM " + getPraefixMoveToTbl(toArchiv) + tblName + ")";
		Application.getLoaderBase().execute(sql);

		// alte Daten löschen
		if (datenLoeschen)
		{
			sql = select.replace("SELECT *", "DELETE");
			// alle Daten, nicht nur bis zum PZE-Ende der Person
			if (whereEndePze != null)
			{
				sql = sql.replace(whereEndePze, "");
			}
			
			// ggf. vor den H*Projekte erst die normalen löschen, sonst kommt es zu DB-Verletzungen
			// dann nur die, für die das normale Projekt auch bereits archiviert ist
			if (tblName.equals(CoAuftrag.TABLE_NAME))
			{
				Application.getLoaderBase().execute(sql + " AND AuftragIDBudgetJahresweise IS NOT NULL"); 
				Application.getLoaderBase().execute(sql + " AND ID NOT IN (SELECT DISTINCT AuftragIDBudgetJahresweise FROM " + CoAuftrag.TABLE_NAME 
						+ " WHERE AuftragIDBudgetJahresweise IS NOT NULL)");
			}
			else if (tblName.equals(CoAbruf.TABLE_NAME))
			{
				Application.getLoaderBase().execute(sql + " AND AbrufIDBudgetJahresweise IS NOT NULL");
				Application.getLoaderBase().execute(sql + " AND ID NOT IN (SELECT DISTINCT AbrufIDBudgetJahresweise FROM " + CoAbruf.TABLE_NAME 
						+ " WHERE AbrufIDBudgetJahresweise IS NOT NULL)");
			}
			else
			{
				Application.getLoaderBase().execute(sql);
			}
		}
	}


	/**
	 * Daten einer Tabelle für ein Jahr archivieren und die alten Daten löschen
	 * 
	 * @param tblName
	 * @param jahr
	 * @param id
	 * @param isAuftrag
	 * @param toArchiv
	 * @throws Exception
	 */
	private static void moveProjekteByJahr(String tblName, int jahr, String id, boolean isPK, boolean isAuftrag, boolean toArchiv) throws Exception {
		String select;

		select = getSelectProjekteByJahr(tblName, jahr, id, isPK, isAuftrag, toArchiv);

		// bei Projekten ggf. erst H*Projekte wiederherstellen
		if (!toArchiv)
		{
//			System.out.println(select);
//			System.out.println("SELECT * FROM ARCHIVtblAuftrag WHERE ID IN (SELECT DISTINCT AuftragIDBudgetJahresweise " + select.substring(select.indexOf("FROM")) + ")");
			if (tblName.equals(CoAuftrag.TABLE_NAME))
			{
				move(tblName, " SELECT * FROM ARCHIVtblAuftrag WHERE ID IN (SELECT DISTINCT AuftragIDBudgetJahresweise "
						+ select.substring(select.indexOf("FROM")) + ")",
						null, toArchiv, true, toArchiv);
			}
			else if (tblName.equals(CoAbruf.TABLE_NAME))
			{
				move(tblName, " SELECT * FROM ARCHIVtblAbruf WHERE ID IN (SELECT DISTINCT AbrufIDBudgetJahresweise "
						+ select.substring(select.indexOf("FROM")) + ")",
						null, toArchiv, true, toArchiv);
			}
		}
		
		// Projekte verschieben
		select = getSelectProjekteByJahr(tblName, jahr, id, isPK, isAuftrag, toArchiv);
		move(tblName, select, null, toArchiv, true, toArchiv);
		
//		move(tblName, select, null, toArchiv, true, true);
//		beides nicht gut, ID kann bei H* in beiden sein
	}

	
	/**
	 * Daten einer Tabelle für ein Jahr löschen 
	 * 
	 * @param tblName
	 * @param jahr
	 * @param id
	 * @param isAuftrag
	 * @throws Exception
	 */
	private static void deleteProjekteByJahr(String tblName, int jahr, String id, boolean isPK, boolean isAuftrag) throws Exception {
		String select;

		// Daten für ein Jahr löschen
		if (jahr > 0)
		{
			select = getSelectProjekteByJahr(tblName, jahr, id, isPK, isAuftrag, false);
		}
		else // Projekte ohne Daten löschen
		{
			return;
//			select = " SELECT * FROM " + getPraefixMoveFromTbl(toArchiv) + tblName 
//					+ " WHERE " + (isPK ? "ID" : id) + " NOT IN "
//					+ "(SELECT DISTINCT " + (isAuftrag ? "AuftragID" : "AbrufID") + " FROM " + getPraefixMoveFromTbl(toArchiv) + "tblMonatseinsatzblatt "
//					+ "WHERE Wertzeit IS NOT NULL AND Wertzeit>0 "
//					+ (isAuftrag ? "" : " AND AbrufID IS NOT NULL")
//					+ ")";
		}

		// Daten löschen
		System.out.println(select);
		Application.getLoaderBase().execute(select.replace("SELECT *", "DELETE"));
	}

	
	/**
	 * SELECT-Statement für Projekte ohne Stunden im Monatseinsatzblatt
	 * 
	 * @param tblName
	 * @param isAuftrag
	 * @param toArchiv
	 * @return
	 * @throws Exception
	 */
	private static String getSelectIDsOhneStunden(String tblName, boolean isAuftrag, boolean toArchiv) throws Exception {
		String select;

		// Projekte ohne Daten löschen
		select = " SELECT ID FROM " + getPraefixMoveFromTbl(toArchiv) + tblName 
				+ " WHERE ID NOT IN "
				+ "(SELECT DISTINCT " + (isAuftrag ? "AuftragID" : "AbrufID") + " FROM " + getPraefixMoveFromTbl(toArchiv) + "tblMonatseinsatzblatt "
				+ "WHERE Wertzeit IS NOT NULL AND Wertzeit>0 "
				+ (isAuftrag ? "" : " AND AbrufID IS NOT NULL")
				+ ")";
						
		
		// wenn Projekte im Archiv gelöscht werden sollen, prüfe vorher, ob es ein H*Projekt dazu gibt, diese sollen erhalten bleiben
		// evtl. müssen dies auch beim wiederherstellen gesondert betrachtet werden
		if (tblName.equals(CoAuftrag.TABLE_NAME))
		{
			select += " AND AuftragIDBudgetJahresweise IS NULL";
		}
		else if (tblName.equals(CoAbruf.TABLE_NAME))
		{
			select += " AND AbrufIDBudgetJahresweise IS NULL";
		}
		

		return select;
	}

	
	/**
	 * Daten einer Tabelle für ein Jahr archivieren und die alten Daten löschen
	 * 
	 * @param tblName
	 * @param jahr
	 * @param id
	 * @param isAuftrag
	 * @param toArchiv
	 * @return 
	 * @throws Exception
	 */
	private static String getSelectProjekteByJahr(String tblName, int jahr, String id, boolean isPK, boolean isAuftrag, boolean toArchiv) throws Exception {
		String select;

		select = " SELECT * FROM " + getPraefixMoveFromTbl(toArchiv) + tblName 
				+ " WHERE " + (isPK ? "ID" : id) + " IN (SELECT " +  id + " FROM " + CoArchivProjekte.getDatabase(!toArchiv, isAuftrag, !isAuftrag || !toArchiv) 
				+ " WHERE jahr=" + jahr + ")"
				+ (toArchiv ? " AND AbrufID IS " + (isAuftrag ? "" : " NOT ") + " NULL " : "")
				;

		System.out.println(select);
		return select;
	}

	
	/**
	 * Daten einer Tabelle für Projekte archivieren und die alten Daten löschen
	 * alle abgeschlossenen Projekte für die keine Stunden mehr gebucht sind (die sind dann bereits archiviert)
	 * 
	 * @param tblName
	 * @param jahr
	 * @throws Exception
	 */
	private static void moveByProjekt(String tblName, String id, boolean isPK, boolean checkStatusAuftrag, boolean checkStatusAbruf, boolean toArchiv) 
			throws Exception {
		String praefix, statusIDs;
		String select;
		
		praefix = getPraefixMoveFromTbl(toArchiv);
		statusIDs = CoArchivProjekte.getStatusIDsArchivierung();
		
		// wenn aus dem Archiv zurück kopiert wird, muss der Status der Projekte nicht geprüft werden
		if (!toArchiv)
		{
			checkStatusAuftrag = false;
			checkStatusAbruf = false;
		}

		// Statement erstellen
		select = " SELECT * FROM " + praefix + tblName
				+ " WHERE " + (isPK ? "ID" : id) + " NOT IN " + getSelectProjekteMitStunden(id, toArchiv)
				+ (checkStatusAuftrag ? " AND " + (isPK ? "ID" : id) + " IN (SELECT ID FROM " + praefix + "tblAuftrag au WHERE au.StatusID IN " + statusIDs + ")" : "")
				+ (checkStatusAbruf ? " AND " + (isPK ? "ID" : id) + " IN (SELECT ID FROM " + praefix + "tblAbruf ab  WHERE ab.StatusID IN " + statusIDs + ")" : "");

		System.out.println(select);
		move(tblName, select, null, toArchiv, true, true);
	}

	
	/**
	 * Daten einer Tabelle für Projekte archivieren und die alten Daten löschen
	 * alle abgeschlossenen Projekte für die keine Stunden mehr gebucht sind (die sind dann bereits archiviert)
	 * 
	 * @param tblName
	 * @param jahr
	 * @throws Exception
	 */
	private static void restoreByProjekt(String tblName, String id, boolean checkStatusAuftrag, boolean checkStatusAbruf, boolean toArchiv) 
			throws Exception {
		restoreByProjekt(tblName, 0, id, checkStatusAuftrag, checkStatusAbruf, toArchiv);
	}

	
	/**
	 * Daten einer Tabelle für Projekte archivieren und die alten Daten löschen
	 * alle abgeschlossenen Projekte für die keine Stunden mehr gebucht sind (die sind dann bereits archiviert)
	 * 
	 * @param tblName
	 * @param jahr
	 * @throws Exception
	 */
	private static void restoreByProjekt(String tblName, int jahr, String id, boolean checkStatusAuftrag, boolean checkStatusAbruf, boolean toArchiv) 
			throws Exception {
		String select;
		

		// Statement erstellen
		select = getSelectProjekteFromArchiv(tblName, jahr, id, checkStatusAuftrag, checkStatusAbruf, toArchiv);

		// Daten von archivierten Personen nicht wiederherstellen
		if (tblName.equals(CoMonatseinsatzblatt.TABLE_NAME) || tblName.equals(CoMessage.TABLE_NAME))
		{
			select += " AND (PersonID IS NULL OR PersonID IN (SELECT ID FROM tblPerson))";
		}
		
		move(tblName, select, null, toArchiv, true, true);
	}

	
	/**
	 * Daten einer Tabelle löschen
	 * 
	 * @param tblName
	 * @param id
	 * @param checkStatusAuftrag
	 * @param checkStatusAbruf
	 * @param selectIDs
	 * @throws Exception
	 */
	private static void deleteByProjekt(String tblName, String id, boolean checkStatusAuftrag, boolean checkStatusAbruf, String selectIDs) 
			throws Exception {
		String praefix;
		String sql;
		boolean isPK = false;
		praefix = getPraefixMoveFromTbl(false);

		sql = " DELETE FROM " + praefix + tblName
				+ " WHERE ";
//				+ (checkStatusAbruf && checkStatusAuftrag ?
//						(isPK ? "ID" : id) + " IN (SELECT " +  id + " FROM " 
//						+ CoArchivProjekte.getDatabase(!toArchiv, checkStatusAuftrag, false)
//						.replace("ARCHIVtblAuftrag", "tblAuftrag").replace("ARCHIVtblAbruf", "tblAbruf")
//						+ " WHERE jahr=" + jahr + ") AND " 
//						: "")

		if (selectIDs == null)
		{
			sql += (checkStatusAuftrag ? (isPK ? "ID" : id) + " NOT IN (SELECT ID FROM " + praefix + "tblAuftrag)" : "")
			+ (checkStatusAbruf ? (checkStatusAuftrag ? " AND AbrufID IS NULL" : (isPK ? "ID" : id) + " NOT IN (SELECT ID FROM " + praefix + "tblAbruf)") : "")
			;
		}
		else
		{
			sql +=  id + " IN (" + selectIDs + ")";
		}
		
		// bei Projekten prüfen ob es H*Projekte gibt, diese Projekte bleiben
		// auf H*Projekt sind Stunde, auf den normalen nicht, dann soll das normale erhalten bleiben
		if (tblName.equals(CoAuftrag.TABLE_NAME))
		{
			Application.getLoaderBase().execute(sql + " AND AuftragIDBudgetJahresweise IS NULL");
		}
		else if (tblName.equals(CoAbruf.TABLE_NAME))
		{
			Application.getLoaderBase().execute(sql + " AND AbrufIDBudgetJahresweise IS NULL");
		}


		// Daten löschen
		System.out.println(sql);
		Application.getLoaderBase().execute(sql);
	}

	
	/**
	 * Projekte aus dem Archiv laden
	 * 
	 * @param tblName
	 * @param jahr
	 * @param id
	 * @param checkStatusAuftrag
	 * @param checkStatusAbruf
	 * @param toArchiv
	 * @return
	 * @throws Exception
	 */
	private static String getSelectProjekteFromArchiv(String tblName, int jahr, String id, boolean checkStatusAuftrag, boolean checkStatusAbruf, boolean toArchiv) 
			throws Exception {
		String praefix, statusIDs;
		String select;
		boolean isPK = false;
		praefix = getPraefixMoveFromTbl(toArchiv);
		statusIDs = CoArchivProjekte.getStatusIDsArchivierung();
		

		// Statement erstellen
		select = " SELECT * FROM " + praefix + tblName
				+ " WHERE "
				+ (checkStatusAbruf && checkStatusAuftrag ?
						(isPK ? "ID" : id) + " IN (SELECT " +  id + " FROM " 
						+ CoArchivProjekte.getDatabase(!toArchiv, checkStatusAuftrag, false)
						.replace("ARCHIVtblAuftrag", "tblAuftrag").replace("ARCHIVtblAbruf", "tblAbruf")
						+ " WHERE jahr=" + jahr + ") AND " 
						: "")

				+ (checkStatusAuftrag ? "  " + (isPK ? "ID" : id) + " IN (SELECT ID FROM tblAuftrag au WHERE au.StatusID IN " + statusIDs + ")" : "")
				+ (checkStatusAbruf ? (checkStatusAuftrag ? " AND AbrufID IS NULL" 
						: "  " + (isPK ? "ID" : id) + " IN (SELECT ID FROM tblAbruf ab  WHERE ab.StatusID IN " + statusIDs + ")") : "");

		System.out.println(select);
		return select;
	}


	/**
	 * Daten einer Person in einer Tabelle löschen
	 * 
	 * @param tblName
	 * @param personID
	 * @throws Exception
	 */
	private static void setNullByPersonID(String tblName, String colName, int personID) throws Exception {
		String sql;

		sql = "UPDATE " + tblName + " SET " +  colName + "=NULL WHERE ID=" +  personID;
		
		// Update ausführen
		Application.getLoaderBase().execute(sql);
	}
	

	/**
	 * Daten in einer Tabelle für Projekte löschen
	 * alle abgeschlossenen Projekte für die keine Stunden mehr gebucht sind (die sind dann bereits archiviert)
	 * 
	 * @param tblName
	 * @param id
	 * @param toArchiv
	 * @throws Exception
	 */
	private static void setNullByProjekt(String tblName, String id, boolean toArchiv) throws Exception {
		String sql;

		sql = "UPDATE " + getPraefixMoveFromTbl(toArchiv) + tblName + " SET " +  id + "=NULL WHERE " +  id + " NOT IN " + getSelectProjekteMitStunden(id, toArchiv);
		
		// Update ausführen
		Application.getLoaderBase().execute(sql);
	}

	
	private static String getSelectProjekteMitStunden(String id, boolean toArchiv) {
		return "(SELECT DISTINCT " +  id + " FROM " + getPraefixMoveFromTbl(toArchiv) + CoMonatseinsatzblatt.TABLE_NAME 
				+ " WHERE " + id + " IS NOT NULL "
						+ "AND WertZeit>0"
						+ ")";
	}
	
	
	/**
	 * Daten in einer Tabelle für Projekte löschen
	 * alle abgeschlossenen Projekte für die keine Stunden mehr gebucht sind (die sind dann bereits archiviert)
	 * 
	 * @param tblName
	 * @param id
	 * @param toArchiv
	 * @throws Exception
	 */
	private static void deleteByProjekt(String tblName, String id) throws Exception {
		String sql;

		sql = "DELETE FROM " + tblName + " WHERE " +  id + " NOT IN " + getSelectProjekteMitStunden(id, true);
		
		// Update ausführen
		Application.getLoaderBase().execute(sql);
	}


	/**
	 * 	Status der H*Projekte ändern
	 * 
	 * @param toArchiv zum Archivieren von H auf abgeschlossen oder andersrum
	 * @throws Exception
	 */
	private static void updateStatusHProjekte(boolean toArchiv) throws Exception {
		updateStatusHProjekte(CoAuftrag.TABLE_NAME, toArchiv);
		updateStatusHProjekte(CoAbruf.TABLE_NAME, toArchiv);
	}


	/**
	 * 	Status der H*Projekte ändern
	 * 
	 * @param toArchiv zum Archivieren von H auf abgeschlossen oder andersrum
	 * @param tableName
	 * @throws Exception
	 */
	private static void updateStatusHProjekte(String tableName, boolean toArchiv) throws Exception {
		String sql, id, statusID;
		
		id = tableName.replace("tbl", "") + "IDBudgetJahresweise";
		
		// Status abgeschlossen zum archivieren oder H beim wiederherstellen
		statusID = toArchiv ? "(SELECT StatusID FROM " + tableName + " a WHERE a." + id + "=" + tableName + ".ID)" : "" + CoStatusProjekt.STATUSID_H;
		sql = "UPDATE " + tableName + " SET StatusID= " + statusID
				+ " WHERE ID IN (SELECT DISTINCT " + id + " FROM " + tableName + "  WHERE " + id + " IS NOT NULL "
				+ " AND StatusID IN " + CoArchivProjekte.getStatusIDsArchivierung() + ")";
		Application.getLoaderBase().execute(sql);

		statusID = toArchiv ? "(SELECT StatusID FROM " + ARCHIV + tableName + " a WHERE a." + id + "=" + tableName + ".ID)" : "" + CoStatusProjekt.STATUSID_H;
		sql = "UPDATE " + tableName + " SET StatusID= " + statusID
				+ " WHERE ID IN (SELECT DISTINCT " + id + " FROM " + ARCHIV + tableName + "  WHERE " + id + " IS NOT NULL "
				+ " AND StatusID IN " + CoArchivProjekte.getStatusIDsArchivierung() + ")";
		Application.getLoaderBase().execute(sql);

		//				+ " OR ID IN (SELECT DISTINCT " + id + " FROM " + ARCHIV + tableName + "  WHERE " + id + " IS NOT NULL " 
//				+ " AND StatusID IN " + CoArchivProjekte.getStatusIDsArchivierung() + ")";
		
		// Statement ausführen
//		Application.getLoaderBase().execute(sql);
	}


	/**
	 * Where-Teil eines SQL-Statements erstellen, das nur Buchungen bis zum PZE-Ende einer Person berücksichtigt
	 * 
	 * @param personID
	 * @return
	 * @throws Exception
	 */
	private static String getWhereEndePZE(int personID) throws Exception {
		Date datum;
		
		CoPerson.getInstance().moveToID(personID);
		datum = CoPerson.getInstance().getEndePze();
		
		if (datum == null)
		{
			return null;
		}
		
		return " AND Datum < '" + Format.getStringForDB(Format.getDateVerschoben(CoPerson.getInstance().getEndePze(), 1)) + "'";
	}


	/**
	 * Tabellen-Präfix "ARCHIV", wenn Daten aus einer Archiv-Tabelle verschoben werden soll (toArchiv=false)
	 * 
	 * @param toArchiv in oder aus einer Archiv-Tabelle verschieben
	 * @return
	 */
	public static String getPraefixMoveFromTbl(boolean toArchiv) {
		return toArchiv ? "" : ARCHIV;
	}
	

	/**
	 * Tabellen-Präfix "ARCHIV", wenn Daten in eine Archiv-Tabelle verschoben werden soll (toArchiv=true)
	 * 
	 * @param toArchiv in oder aus einer Archiv-Tabelle verschieben
	 * @return
	 */
	private static String getPraefixMoveToTbl(boolean toArchiv) {
		return toArchiv ? ARCHIV : "";
	}
	

}
