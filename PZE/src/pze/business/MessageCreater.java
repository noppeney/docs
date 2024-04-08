package pze.business;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import framework.Application;
import framework.business.interfaces.loader.ILoaderBase;
import pze.business.objects.CoBrueckentag;
import pze.business.objects.CoMessage;
import pze.business.objects.auswertung.CoAnwesenheitUebersicht;
import pze.business.objects.personen.CoKontowert;
import pze.business.objects.personen.CoPerson;
import pze.business.objects.personen.monatseinsatzblatt.CoMonatseinsatzblatt;
import pze.business.objects.projektverwaltung.CoAbruf;
import pze.business.objects.projektverwaltung.CoAuftrag;
import pze.business.objects.projektverwaltung.CoProjekt;
import pze.business.objects.reftables.CoErsthelfer;
import pze.business.objects.reftables.personen.CoAbteilung;
import pze.business.objects.reftables.personen.CoBundesland;
import pze.business.objects.reftables.personen.CoPosition;
import pze.business.objects.reftables.personen.CoStatusAktivInaktiv;
import pze.business.objects.reftables.personen.CoStatusInternExtern;
import pze.ui.formulare.FormBuchungsUpdate;
import startup.PZEStartupAdapter;

/**
 * Klasse zum automatischen Erzeugen von täglichen und wöchentlichen Meldungen
 * 
 * @author lisiecki
 */
public class MessageCreater {
	private static final boolean DEBUG = false;
	
	private static Date m_lastDatumMessageCheck;

	

	/**
	 * Tägliche und wöchentiche Prüfung auf neue Einträge im Messageboard
	 * 
	 * @param gregDatum
	 * @throws Exception
	 */
	public static void createMessages(GregorianCalendar gregDatum) throws Exception {
		
		// wenn in dieser Programmversion das Buchungsupdate läuft, erstelle die Nachrichten, damit sie nur einmal erstellt werden
		if (!FormBuchungsUpdate.isRunning() && !DEBUG)
		{
 			return;
		}
		// TODO wenn nochmal Fehler auftaucht, beim Ändern der DB-Connection eine globale Variable zum DB-Sperren setzen, die beim MessageErstellen geprüft wird
		
		// am Wochenende, Feiertag und Brückentag müssen keine Meldungen erzeugt werden
		if (Format.isWochenende(gregDatum) || FeiertagGenerator.getInstance().isFeiertag(gregDatum, CoBundesland.ID_NRW) 
				|| CoBrueckentag.getInstance().isBrueckentag(gregDatum, CoBundesland.ID_NRW))
		{
			return;
		}
		
		// tägliche und wöchentliche Nachrichten erstellen
		createWeeklyMessages(gregDatum);
		createDailyMessages(gregDatum);
	}
	
	
	/**
	 * Wöchentliche Prüfung auf neue Einträge im Messageboard
	 * 
	 * @param datum
	 * @throws Exception
	 */
	private static void createWeeklyMessages(GregorianCalendar datum) throws Exception {
		int resturlaub;
		String where;
		GregorianCalendar gregDatum;
		CoKontowert coKontowert;
		

		// wenn diese Woche schon Messages generiert wurden
		if (checkDateWeeklyMessages(datum) && !DEBUG)
		{
			return;
		}
		
		
		// im Januar keine Prüfung des Resturlaubs, erst nach Abschluss der Urlaubsplanung
		if (datum.get(Calendar.MONTH) == Calendar.JANUARY)
		{
			return;
		}
		
		// Resturlaub zum Jahresende prüfen
		gregDatum = (GregorianCalendar) datum.clone();
		gregDatum.set(Calendar.MONTH, Calendar.DECEMBER);
		gregDatum.set(Calendar.DAY_OF_MONTH, 31);
		
		// nur aktive Personen prüfen
		where = "StatusAktivInaktivID=" + CoStatusAktivInaktiv.STATUSID_AKTIV + " AND StatusInternExternID=" + CoStatusInternExtern.STATUSID_INTERN
				+ " AND YEAR(Datum)=" + gregDatum.get(Calendar.YEAR) + " AND MONTH(Datum)=12 AND DAY(Datum)=31";

		// Kontowerte laden
		coKontowert = new CoKontowert();
		coKontowert.loadMitResturlaub(where, gregDatum.getTime(), true);
		
		// alle Personen durchlaufen und Resturlaub prüfen
		coKontowert.moveFirst();
		do
		{
			resturlaub = coKontowert.getResturlaubOffen();
			if (resturlaub > 5)
			{
				(new CoMessage()).createMessageResturlaub(coKontowert.getPersonID(), resturlaub);
			}
		} while (coKontowert.moveNext());

	}


	/**
	 * Tägliche Prüfung auf neue Einträge im Messageboard
	 * 
	 * @param gregDatum
	 * @throws Exception
	 */
	private static void createDailyMessages(GregorianCalendar gregDatum) throws Exception {

		// wenn heute schon Messages generiert wurden
		if (checkDateDailyMessages(gregDatum) && !DEBUG)
		{
			return;
		}

		// prüfen ob ein Ersthelfer da ist
		if (!checkErsthelfer(gregDatum))
		{
			(new CoMessage()).createMessageErsthelfer();
		}

		// Meldungen zu fehlenden Stunden im Monatseinsatzblatt
		createMessagesMonatseinsatzblatt();

		// Meldungen zur Projektverfolgung
		createMessagesProjektverfolgung();
		
		// Messages zum Erreichen einer %-Grenze der Stunden erstellen
		createMessagesProjektProzentmeldung();
		return;
	}


	/**
	 * Meldungen zu Fehlstunden im Monatseinsatzblatt erzeugen
	 * 
	 * @throws Exception
	 */
	private static void createMessagesMonatseinsatzblatt() throws Exception {
		CoPerson coPerson;
		CoKontowert coKontowert;
		CoMonatseinsatzblatt coMonatseinsatzblatt, coMonatseinsatzblattWsonstiges;
		
		coPerson = new CoPerson();
		coKontowert = new CoKontowert();
		coMonatseinsatzblatt = new CoMonatseinsatzblatt();
		coMonatseinsatzblattWsonstiges = new CoMonatseinsatzblatt();

		coPerson.loadByAktivIntern();
		coKontowert.loadSummeArbeitszeit();
		coMonatseinsatzblatt.loadSumme();
		coMonatseinsatzblattWsonstiges.loadWsonstiges();
		
		// prüfe die Stunden für jede Person
		coPerson.moveFirst();
		do
		{
			// nicht eingetragene Stunden
			checkMonatseinsatzblattFehlstunden(coPerson, coKontowert, coMonatseinsatzblatt);
			
			// w-sonstiges
			checkMonatseinsatzblattWsonstiges(coPerson, coMonatseinsatzblattWsonstiges);
			
		} while (coPerson.moveNext());
	}


	/**
	 * Message erzeugen, wenn zu wenig Stunden im Monatseinsatzblatt eingetragen wurden
	 * 
	 * @param coPerson
	 * @param coKontowert 
	 * @param coMonatseinsatzblatt
	 * @throws Exception
	 */
	private static void checkMonatseinsatzblattFehlstunden(CoPerson coPerson, CoKontowert coKontowert, CoMonatseinsatzblatt coMonatseinsatzblatt) throws Exception {
		int personID, wertZeit;

		personID = coPerson.getID();

		if (!coKontowert.moveToPersonID(personID))
		{
			return;
		}
		
		// Sonderfall Se trägt keine Stunden ein 
		if (coPerson.getAbteilungID() == CoAbteilung.ID_KL)
		{
			return;
		}
		
		// Stundendifferenz
		wertZeit = coKontowert.getWertArbeitszeit() - (coMonatseinsatzblatt.moveToPersonID(personID) ? coMonatseinsatzblatt.getWertZeit() : 0);
		if (wertZeit > CoMessage.WERT_FEHLZEIT_MONATSEINSATZBLATT)
		{
			new CoMessage().createMessageMonatseinsatzblattFehlstunden(personID, wertZeit);
		}
	}


	/**
	 * Message erzeugen, wenn mehr als 8 Stunden auf Abteilung-sonstiges gearbeitet wurde
	 * 
	 * @param coPerson
	 * @param coMonatseinsatzblattWsonstiges
	 * @throws Exception
	 */
	private static void checkMonatseinsatzblattWsonstiges(CoPerson coPerson, CoMonatseinsatzblatt coMonatseinsatzblattWsonstiges) throws Exception {
		int personID;

		personID = coPerson.getID();

		if (coPerson.getPositionID() == CoPosition.ID_AL || !coMonatseinsatzblattWsonstiges.moveToPersonID(personID))
		{
			return;
		}
		
		// Message erzeugen
		new CoMessage().createMessageMonatseinsatzblattWsonstiges(personID, coMonatseinsatzblattWsonstiges.getGregDatum(), 
				coMonatseinsatzblattWsonstiges.getWertZeit(), coMonatseinsatzblattWsonstiges.getAuftragID());
		
		// ggf. 2. Eintrag für Person für letzten und aktuellen Monat
		if (coMonatseinsatzblattWsonstiges.moveNext() && coMonatseinsatzblattWsonstiges.getPersonID() == personID)
		{
			new CoMessage().createMessageMonatseinsatzblattWsonstiges(personID, coMonatseinsatzblattWsonstiges.getGregDatum(), 
					coMonatseinsatzblattWsonstiges.getWertZeit(), coMonatseinsatzblattWsonstiges.getAuftragID());
		}
	}


	/**
	 * prüfen, ob Ersthelfer vor Ort ist
	 * 
	 * @return Ersthelfer vor Ort oder nicht
	 * @throws Exception
	 */
	private static boolean checkErsthelfer(GregorianCalendar gregDatum) throws Exception {
		String eintrag;
		CoErsthelfer coErsthelfer;
		CoAnwesenheitUebersicht coAnwesenheitUebersicht;
		
		coErsthelfer = CoErsthelfer.getInstance();
		coAnwesenheitUebersicht = new CoAnwesenheitUebersicht();
		
		// Ersthelfer prüfen
		if (coErsthelfer.moveFirst())
		{
			do
			{
				// Eintrag laden
				eintrag = coAnwesenheitUebersicht.bestimmeEintrag(coErsthelfer.getPersonID(), gregDatum.getTime(), 0);
				
				// wenn kein Eintrag dann ist ein Ersthelfer da
				if (eintrag == null)
				{
					return true;
				}
			} while (coErsthelfer.moveNext());
		}
		
		return false;
	}


	/**
	 * Messages zum Start einer neuen Runde der Projektverfolgung (ETC-Prüfung)
	 * 
	 * @throws Exception
	 */
	private static void createMessagesProjektverfolgung() throws Exception {
		int tag, tagLetzterCheck;
		GregorianCalendar gregDatum;
		CoMessage coMessage;

		coMessage = new CoMessage();
		gregDatum = Format.getGregorianCalendar12Uhr(new Date());
		tag = gregDatum.get(Calendar.DAY_OF_MONTH);
		tagLetzterCheck = Format.getGregorianCalendar(m_lastDatumMessageCheck).get(Calendar.DAY_OF_MONTH);
		
		// am 1. des Monats muss eine neue Runde der Projektverfolgung gestartet werden
		if (tag == 1 || tag < tagLetzterCheck) // falls am 1. kein Check gelaufen ist
		{
			createMessagesProjektverfolgungPrognoseErstellen(new CoAuftrag(), coMessage);
			createMessagesProjektverfolgungPrognoseErstellen(new CoAbruf(), coMessage);
		}
		else if (tag == 5 || (tagLetzterCheck < 5 && tag > 5)) // am 5. geht die Meldung an den AL
		{
			createMessagesProjektverfolgungPrognosePruefen(new CoAuftrag(), coMessage);
			createMessagesProjektverfolgungPrognosePruefen(new CoAbruf(), coMessage);
		}
	
		// Messages speichern
		if (coMessage != null && coMessage.isEditing())
		{
			coMessage.save();
		}
	}


	/**
	 * Messages zum Start einer neuen Runde der Projektverfolgung (ETC-Prüfung)
	 * 
	 * @param coProjekt Auftrag oder Abruf, Daten werden in dieser Methode geladen
	 * @param coMessage
	 * @throws Exception
	 */
	private static void createMessagesProjektverfolgungPrognoseErstellen(CoProjekt coProjekt, CoMessage coMessage) throws Exception {
		// alle Projekte zur Projektverfolgung laden
		coProjekt.loadForProjektverfolgung();
		if (!coProjekt.moveFirst())
		{
			return;
		}
		
		// Projekte durchlaufen und Messages erstellen
		do
		{
			coMessage.createMessageProjektverfolgungPrognoseErstellen(coProjekt);
		} while (coProjekt.moveNext());
	}


	/**
	 * Messages zum Start einer neuen Runde der Projektverfolgung (ETC-Prüfung) durch den AL, wenn der PL dies noch nicht erledigt hat
	 * 
	 * @param coProjekt Auftrag oder Abruf, Daten werden in dieser Methode geladen
	 * @param coMessage
	 * @throws Exception
	 */
	private static void createMessagesProjektverfolgungPrognosePruefen(CoProjekt coProjekt, CoMessage coMessage) throws Exception {
		// alle Projekte zur Projektverfolgung laden
		coProjekt.loadWhereProjektverfolgungPlOffen();
		if (!coProjekt.moveFirst())
		{
			return;
		}
		
		// Projekte durchlaufen und Messages erstellen
		do
		{
			coMessage.createMessageProjektverfolgungPrognosePruefen(coProjekt, false);
		} while (coProjekt.moveNext());
	}


	/**
	 * Messages zum Erreichen einer %-Grenze der Stunden erstellen
	 * 
	 * @throws Exception
	 */
	private static void createMessagesProjektProzentmeldung() throws Exception {
		CoMessage coMessage;

		coMessage = new CoMessage();

		// Meldungen erzeugen
		createMessagesProjektProzentmeldung(new CoAuftrag(), coMessage);
		createMessagesProjektProzentmeldung(new CoAbruf(), coMessage);

		// Messages speichern
		if (coMessage != null && coMessage.isEditing())
		{
			coMessage.save();
		}
	}


	/**
	 * Messages zum Erreichen einer %-Grenze der Stunden erstellen
	 * 
	 * @throws Exception
	 */
	private static void createMessagesProjektProzentmeldung(CoProjekt coProjekt, CoMessage coMessage) throws Exception {
		// alle relevanten Projekte laden
		coProjekt.loadForProzentmeldung();
		if (!coProjekt.moveFirst())
		{
			return;
		}
		
		// Projekte durchlaufen und Messages erstellen
		do
		{
			coMessage.createMessageProzentmeldung(coProjekt);
		} while (coProjekt.moveNext());
	}


	/**
	 * Letztes Datum des Messagechecks
	 * 
	 * @throws Exception
	 */
	private static Date getLastDateMessageCheck() throws Exception	{		
		String sql;
		Object lastDatum;


		// prüfen, ob die richtige DB-Verbindung geöffnet ist
		while (!PZEStartupAdapter.isDefaultDbConnection())
		{
			Thread.sleep(1000);
		}

		// letztes Datum des MessageChecks suchen
		sql = "SELECT Datum from sequenceMessageCheck";
		lastDatum = Application.getLoaderBase().executeScalar(sql);
		if(lastDatum == null) 
		{
			return null;
		}

		return Format.getDate12Uhr((Date) lastDatum);	
	}
	

	/**
	 * prüft, ob bereist ein MessageCheck durchgeführt wurde
	 * 
	 * @throws Exception
	 */
	private static boolean checkDateWeeklyMessages(GregorianCalendar gregDatum) throws Exception	{		
		int lastWeek;
		Object lastDatum;

		// letztes Datum des MessageChecks suchen
		lastDatum = getLastDateMessageCheck();
		if(lastDatum != null) 
		{
			// prüfe, ob bereist ein MessageCheck durchgeführt wurde
			lastWeek = Format.getGregorianCalendar((Date) lastDatum).get(Calendar.WEEK_OF_YEAR);
			if (lastWeek == gregDatum.get(Calendar.WEEK_OF_YEAR))
			{
				return true;
			}
		}

		return false;	
	}
	

	/**
	 * prüft, ob bereist ein MessageCheck durchgeführt wurde
	 * 
	 * @throws Exception
	 */
	private static boolean checkDateDailyMessages(GregorianCalendar gregDatum) throws Exception	{		
		String sql;
		Date datum;
		Object lastDatum;
		ILoaderBase loaderbase;

		loaderbase = Application.getLoaderBase();
		datum = Format.getDate12Uhr(new Date());

		// letztes Datum des MessageChecks suchen
		lastDatum = getLastDateMessageCheck();
		if(lastDatum != null) 
		{
			// prüfe, ob bereist ein MessageCheck durchgeführt wurde 
			if (datum.equals(lastDatum))
			{
				return true;
			}
		}
		// beim ersten Mal Datum eintragen
		else
		{
			loaderbase.execute("INSERT INTO sequenceMessageCheck (Datum) VALUES ('" + Format.getStringForDBmitUhrzeit(datum) + "')");
		}

		// sonst Datum aktualisieren
		m_lastDatumMessageCheck = (Date) lastDatum;
		sql = "update sequenceMessageCheck SET Datum='" + Format.getStringForDBmitUhrzeit(datum) + "'";
		loaderbase.execute(sql);

		return false;	
	}
	

}
