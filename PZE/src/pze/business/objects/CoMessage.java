package pze.business.objects;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import framework.Application;
import framework.business.interfaces.fields.IField;
import pze.business.Format;
import pze.business.UserInformation;
import pze.business.objects.auswertung.CoAuswertung;
import pze.business.objects.dienstreisen.CoDienstreise;
import pze.business.objects.personen.CoBuchung;
import pze.business.objects.personen.CoKontowert;
import pze.business.objects.personen.CoPerson;
import pze.business.objects.projektverwaltung.CoAbruf;
import pze.business.objects.projektverwaltung.CoAuftrag;
import pze.business.objects.projektverwaltung.CoMitarbeiterProjekt;
import pze.business.objects.projektverwaltung.CoProjekt;
import pze.business.objects.projektverwaltung.CoProjektverfolgung;
import pze.business.objects.reftables.CoFreigabeberechtigungen;
import pze.business.objects.reftables.CoMessageGruppe;
import pze.business.objects.reftables.CoMessageQuittierung;
import pze.business.objects.reftables.CoStatusMessage;
import pze.business.objects.reftables.buchungen.CoStatusBuchung;
import pze.business.objects.reftables.personen.CoAbteilung;
import pze.business.objects.reftables.personen.CoPosition;

/**
 * CacheObject für Info-Meldungen
 * 
 * @author Lisiecki
 */
public class CoMessage extends AbstractCoMessage {

	public static final String TABLE_NAME = "tblmessage";
	
	/**
	 * Wert der im Monatseinsatzblatt fehlen darf, vebor eine Meldung erzeugt wird
	 */
	public static final int WERT_FEHLZEIT_MONATSEINSATZBLATT = 25*60;
	
	private CoPerson m_coPerson;


	/**
	 * Kontruktor
	 * @throws Exception 
	 */
	public CoMessage() throws Exception {
		super("table." + TABLE_NAME);
		
		m_coPerson = CoPerson.getInstance();
	}
	

	/**
	 * Neue Message erstellen
	 * 
	 * @param personID
	 * @param messageQuittierungID
	 * @param beschreibung
	 * @param messageGruppeID 
	 * @param namenAusgeben 
	 * @param coProjekt 
	 * @param bemerkung 
	 * @throws Exception
	 */
	private void createNew(int personID, int messageQuittierungID, String beschreibung, int messageGruppeID, boolean namenAusgeben, 
			CoProjekt coProjekt, String bemerkung) 
			throws Exception {
		createNew(personID, messageQuittierungID, beschreibung, new Date(), messageGruppeID, namenAusgeben, coProjekt, 0, bemerkung);
	}
	

	/**
	 * Neue Message erstellen
	 * 
	 * @param personID
	 * @param messageQuittierungID
	 * @param beschreibung
	 * @param messageGruppeID 
	 * @param namenAusgeben 
	 * @param coProjekt 
	 * @param bemerkung 
	 * @throws Exception
	 */
	private void createNew(int personID, int messageQuittierungID, String beschreibung, int messageGruppeID, boolean namenAusgeben, 
			CoProjekt coProjekt, int dienstreiseID, String bemerkung) 
			throws Exception {
		createNew(personID, messageQuittierungID, beschreibung, new Date(), messageGruppeID, namenAusgeben, coProjekt, dienstreiseID, bemerkung);
	}
	
	
	/**
	 * Neue Message erstellen
	 * 
	 * @param personID
	 * @param messageQuittierungID
	 * @param beschreibung
	 * @param messageGruppeID 
	 * @param namenAusgeben 
	 * @param coProjekt 
	 * @param bemerkung 
	 * @throws Exception
	 */
	private void createNew(int personID, int messageQuittierungID, String beschreibung, Date datum, int messageGruppeID, boolean namenAusgeben, 
			CoProjekt coProjekt, int dienstreiseID, String bemerkung) throws Exception
	{
		super.createNew();
		
		// auslösende Person speichern
		if (personID > 0)
		{
			setPersonID(personID);
		}
		setMeldungID(messageQuittierungID);
		setDatum(Format.getDate12Uhr(datum));
		setMessageGruppeID(messageGruppeID);
		setStatusOffen();
		updateGeaendertVonAm();
		
		// Projektinformationen
		if (coProjekt instanceof CoAuftrag)
		{
			setAuftragID(((CoAuftrag) coProjekt).getID());
		}
		if (coProjekt instanceof CoAbruf)
		{
			setAbrufID(((CoAbruf) coProjekt).getID());
		}

		// Dienstreise
		if (dienstreiseID > 0)
		{
			setDienstreiseID(dienstreiseID);
		}

		// zu der Person gehen und Meldung erstellen
		setBeschreibung((namenAusgeben ? getName(personID) : "") + beschreibung);
		
		// Bemerkung
		setBemerkung(bemerkung);
		
		// ggf. Projektbeschreibung als Bemerkung
		if (CoMessageQuittierung.isMessageProjekt(messageQuittierungID) || CoMessageQuittierung.isMessageProjektverfolgung(messageQuittierungID))
		{
			setBemerkung(coProjekt.getBeschreibung());
		}
	}


	/**
	 * Neue Message erstellen
	 * 
	 * @param personID
	 * @param beschreibung
	 * @param messageQuittierungID 
	 * @throws Exception
	 */
	private void createMessage(int personID, String beschreibung, int messageQuittierungID) throws Exception {
		createMessage(personID, beschreibung, messageQuittierungID, true);
	}
	
	
	/**
	 * Neue Message erstellen
	 * 
	 * @param personID
	 * @param beschreibung
	 * @param messageQuittierungID 
	 * @param namenAusgeben 
	 * @throws Exception
	 */
	private void createMessage(int personID, String beschreibung, int messageQuittierungID, boolean namenAusgeben) throws Exception {
		createMessage(personID, beschreibung, messageQuittierungID, namenAusgeben, null, true);
	}

	
	/**
	 * Neue Message erstellen
	 * 
	 * @param personID
	 * @param beschreibung
	 * @param messageQuittierungID 
	 * @param namenAusgeben 
	 * @param bemerkung 
	 * @throws Exception
	 */
	private void createMessage(int personID, String beschreibung, int messageQuittierungID, boolean namenAusgeben, int dienstreiseID, String bemerkung) throws Exception {
		createMessage(personID, beschreibung, messageQuittierungID, namenAusgeben, null, dienstreiseID, true, bemerkung);
	}


	/**
	 * Neue Message erstellen
	 * 
	 * @param personID
	 * @param beschreibung
	 * @param messageQuittierungID 
	 * @param namenAusgeben 
	 * @param speichern 
	 * @throws Exception
	 */
	private void createMessage(int personID, String beschreibung, int messageQuittierungID, boolean namenAusgeben, CoProjekt coProjekt, boolean speichern)
			throws Exception {
		createMessage(personID, beschreibung, messageQuittierungID, namenAusgeben, coProjekt, 0, speichern, null);
	}

	
	/**
	 * Neue Message erstellen
	 * 
	 * @param personID
	 * @param beschreibung
	 * @param messageQuittierungID
	 * @param namenAusgeben
	 * @param coProjekt
	 * @param speichern
	 * @param bemerkung
	 * @throws Exception
	 */
	private void createMessage(int personID, String beschreibung, int messageQuittierungID, boolean namenAusgeben, CoProjekt coProjekt, 
			int dienstreiseID, boolean speichern, String bemerkung) throws Exception
	{
		CoPerson coPerson;
		CoMessageQuittierung coMessageQuittierung;
		
		coPerson = new CoPerson();

		coMessageQuittierung = CoMessageQuittierung.getInstance();
		coMessageQuittierung.moveToID(messageQuittierungID);
		
		
		// Messages für den Mitarbeiter
		if (coMessageQuittierung.isQuittierungMitarbeiter())
		{
			if (personID == 0)
			{
				// wenn keine Person übergeben wurde, erstelle für jede Person eine Meldung
				coPerson.loadByAktivIntern();
				coPerson.moveFirst();
				
				do
				{
					createNew(coPerson.getID(), messageQuittierungID, beschreibung, CoMessageGruppe.ID_MITARBEITER, namenAusgeben, coProjekt, bemerkung);
				} while (coPerson.moveNext());
			}
			else
			{
				createNew(personID, messageQuittierungID, beschreibung, CoMessageGruppe.ID_MITARBEITER, namenAusgeben, coProjekt, bemerkung);
			}
		}
		
		coPerson.loadByID(personID);

		// Messages für den AL
		if (coMessageQuittierung.isQuittierungAl())
		{
			// Sekretärinnen bekommen eine Message für AL-Verwaltung
			if (coPerson.getPositionID() == CoPosition.ID_SEKRETAERIN)
			{
				createNew(personID, messageQuittierungID, beschreibung, CoMessageGruppe.ID_SEKRETAERIN, namenAusgeben, coProjekt, bemerkung);

				// in der Verwaltung nur eine Message
				if (coPerson.getAbteilungID() != CoAbteilung.ID_VERWALTUNG)
				{
					createNew(personID, messageQuittierungID, beschreibung, CoMessageGruppe.ID_AL, namenAusgeben, coProjekt, bemerkung);
				}
			}
			else
			{
				createNew(personID, messageQuittierungID, beschreibung, CoMessageGruppe.ID_AL, namenAusgeben, coProjekt, bemerkung);
			}
		}
		
		// Messages für die Verwaltung
		if (coMessageQuittierung.isQuittierungVerwaltung())
		{
			// Sonderfall Meldung Resturlaub: die soll für die Verwaltung nur erzeugt werden, wenn die Urlaubsplanung abgeschlossen ist
			if (messageQuittierungID == CoMessageQuittierung.ID_RESTURLAUB 
					&& Format.getGregorianCalendar(null).get(GregorianCalendar.MONTH) == GregorianCalendar.JANUARY)
			{
				
			}
			else
			{
				createNew(personID, messageQuittierungID, beschreibung, CoMessageGruppe.ID_VERWALTUNG, namenAusgeben, coProjekt, bemerkung);
			}
		}
		
		// Messages für das Sekretariat
		if (coMessageQuittierung.isQuittierungSekretariat())
		{
			createNew(personID, messageQuittierungID, beschreibung, CoMessageGruppe.ID_SEKRETARIAT, namenAusgeben, coProjekt, dienstreiseID, null);
		}
		
		// DR-Info-Messages (Sentis)
		if (coMessageQuittierung.isDrInfo() && bemerkung.equals("DR"))
		{
			createNew(personID, messageQuittierungID, beschreibung, CoMessageGruppe.ID_DR_INFO, namenAusgeben, coProjekt, dienstreiseID, null);
		}
		
		// Messages speichern
		if (speichern)
		{
			save();
		}
	}


	/**
	 * Neue Message erstellen, weil eine genehmigte Buchung geändert wurde
	 * 
	 * @param personID
	 * @param datumOriginal
	 * @param datumBisOriginal
	 * @param datumGeaendert
	 * @param datumBisGeaendert
	 * @param buchungsart
	 * @param anzahlTage 
	 * @throws Exception
	 */
	public void createMessageUrlaubGeaendert(int personID, Date datumOriginal, Date datumBisOriginal, Date datumGeaendert, Date datumBisGeaendert, 
			String buchungsart, int statusBuchung, int anzahlTage) 
			throws Exception {
		
		// neue Message erstellen
		createMessageUrlaub(personID, datumOriginal, " hat seine/ihre " + getBezeichnungStatusUrlaub(statusBuchung) + " Buchung '" + buchungsart 
				+ "' vom " + Format.getString(datumOriginal)
				+ (datumOriginal.equals(datumBisOriginal) ? "" : "-" + Format.getString(datumBisOriginal))
				+ " auf " + Format.getString(datumGeaendert) 
				+ (datumGeaendert.equals(datumBisGeaendert) ? "" : "-" + Format.getString(datumBisGeaendert))
				+ " geändert.", statusBuchung, anzahlTage);
	}


	/**
	 * Neue Message erstellen, weil eine genehmigte Buchung gelöscht wurde
	 * 
	 * @param personID
	 * @param datum
	 * @param datumBis
	 * @param buchungsart
	 * @param anzahlTage 
	 * @throws Exception
	 */
	public void createMessageUrlaubGeloescht(int personID, Date datum, Date datumBis, String buchungsart, int statusBuchung, int anzahlTage) 
			throws Exception {
		
		// neue Message erstellen
		createMessageUrlaub(personID, datum, " hat seine/ihre " + getBezeichnungStatusUrlaub(statusBuchung) + " Buchung '" + buchungsart 
				+ "' vom " + Format.getString(datum)
				+ (datum.equals(datumBis) ? "" : "-" + Format.getString(datumBis))
				+ " gelöscht.", statusBuchung, anzahlTage);
	}


	/**
	 * Neue Message erstellen, weil eine Buchung geändert wurde
	 * 
	 * @param personID
	 * @param datum
	 * @param message
	 * @param statusBuchung
	 * @param anzahlTage
	 * @throws Exception
	 */
	private void createMessageUrlaub(int personID, Date datum, String message, int statusBuchung, int anzahlTage) throws Exception {
		int messageQuittierungID;
		GregorianCalendar gregDatumHeute;
		
		messageQuittierungID = getQuittierungUrlaubID(statusBuchung);
		
		
		// bei Änderungen an genehmigten Buchungen Message erstellen
		if (messageQuittierungID == CoMessageQuittierung.ID_AENDERUNG_URLAUB_GENEHMIGT)
		{
			// neue Message erstellen
			createMessage(personID, message, messageQuittierungID);
			return;
		}

		
		// Änderungen an geplantem Urlaub
		gregDatumHeute = Format.getGregorianCalendar12Uhr(new Date());

		// im Januar keine Meldung erzeugen wegen Ende der Urlaubsplanung
		if (gregDatumHeute.get(Calendar.MONTH) == Calendar.JANUARY)
		{
			return;
		}
		
		// er werden nur Meldungen für die Urlaubsplanung in diesem Jahr erzeugt
		if (gregDatumHeute.get(Calendar.YEAR) != Format.getGregorianCalendar12Uhr(datum).get(Calendar.YEAR))
		{
			return;
		}
		
		// neue Message nur bei mehr als 3 Tagen erstellen
		if (anzahlTage > 3)
		{
			createMessage(personID, message, messageQuittierungID);
		}

	}


	/**
	 * Neue Message erstellen, weil eine Eintragung als Vertreter (wegen gelöschtem Urlaub) gelöscht wurde
	 * 
	 * @param person Person die man vertreten wollte
	 * @param vertreterID vertreter
	 * @param datum
	 * @param datumBis
	 * @throws Exception
	 */
	public void createMessageVertretungGeloescht(String person, int vertreterID, Date datum, Date datumBis) throws Exception {
		createMessage(vertreterID, "Ihre Eintragung als Vertreter für " +  person
				+ " am " + Format.getString(datum)
				+ (datumBis == null || datum.equals(datumBis) ? "" : "-" + Format.getString(datumBis))
				+ " wurde gelöscht.", CoMessageQuittierung.ID_VERTRETUNG_GELOESCHT, false);
	}


	/**
	 * Bestimmt die Bezeichnung des Status der Buchung, z. B. genehmigt/geplant
	 * 
	 * @param statusBuchung
	 * @return
	 */
	private String getBezeichnungStatusUrlaub(int statusBuchung) {
		switch (statusBuchung)
		{
		case CoStatusBuchung.STATUSID_VORLAEUFIG:	
			return "geplante";
		case CoStatusBuchung.STATUSID_OK:	
			return "genehmigte";
		case CoStatusBuchung.STATUSID_GEAENDERT:	
			return "genehmigte";

		default:
			return "";
		}
	}
	
	
	/**
	 * Bestimmt die QuittierungID der Buchung, z. B. genehmigter/geplanter Urlaub geändert
	 * 
	 * @param statusBuchung
	 * @return
	 */
	private int getQuittierungUrlaubID(int statusBuchung) {
		switch (statusBuchung)
		{
		case CoStatusBuchung.STATUSID_VORLAEUFIG:	
			return CoMessageQuittierung.ID_AENDERUNG_URLAUB_GEPLANT;
		case CoStatusBuchung.STATUSID_OK:	
			return CoMessageQuittierung.ID_AENDERUNG_URLAUB_GENEHMIGT;
		case CoStatusBuchung.STATUSID_GEAENDERT:	
			return CoMessageQuittierung.ID_AENDERUNG_URLAUB_GENEHMIGT;

		default:
			return 0;
		}
	}
	

	/**
	 * Neue Message zur genehmigten Buchung erstellen
	 * 
	 * @param personID
	 * @param buchungID
	 * @throws Exception
	 */
	public void createMessageAntragGenehmigt(int personID, int buchungID, String message) throws Exception {
		createMessageAntragbearbeitet(personID, buchungID, message, true);
	}


	/**
	 * Neue Message zur abgelehnten Buchung erstellen
	 * 
	 * @param personID Person, die den Urlaub abgelehnt hat
	 * @param buchungID
	 * @param message 
	 * @throws Exception
	 */
	public void createMessageAntragAbgelehnt(int personID, int buchungID, String message) throws Exception {
		createMessageAntragbearbeitet(personID, buchungID, message, false);
	}


	/**
	 * Neue Message zur bearbeiteten Buchung erstellen
	 * 
	 * @param personID Person, die den Urlaub abgelehnt hat
	 * @param buchungID
	 * @param message 
	 * @throws Exception
	 */
	private void createMessageAntragbearbeitet(int personID, int buchungID, String message, boolean isGenehmigt) throws Exception {
		CoBuchung coBuchung;
		
		coBuchung = new CoBuchung();
		coBuchung.loadByID(buchungID);
		
		createMessage(coBuchung.getPersonID(), getName(personID) + " hat ihren " + message + " " + (isGenehmigt ? "genehmigt" : "abgelehnt") + ".", 
				(isGenehmigt ? CoMessageQuittierung.ID_ANTRAG_GENEHMIGT : CoMessageQuittierung.ID_ANTRAG_ABGELEHNT), false, 0, coBuchung.getBemerkung());
	}


	/**
	 * Neue Message zum Resturlaub erstellen
	 * 
	 * @param personID
	 * @param anzahlResturlaub
	 * @throws Exception
	 */
	public void createMessageResturlaub(int personID, int anzahlResturlaub) throws Exception {
		createMessage(personID, " hat noch " + anzahlResturlaub + " nicht geplante Tage Resturlaub.", CoMessageQuittierung.ID_RESTURLAUB);
	}


	/**
	 * Neue Message wegen neuem DR-Antrag
	 * 
	 * @param personID
	 * @param wertZeit
	 * @throws Exception
	 */
	public void createMessageDrAntrag(CoBuchung coBuchung) throws Exception {
		String drDg;
		Date datum, datumBis;
		CoDienstreise coDienstreise;

		datum = coBuchung.getDatum();
		datumBis = coBuchung.getDatumBis();
		
		drDg = (coBuchung.isDr() ? "DR" : "DG");
		
		coDienstreise = new CoDienstreise();
		coDienstreise.loadByID(coBuchung.getDienstreiseID());

		createMessage(coBuchung.getPersonID(), "Neuer " + drDg + "-Antrag von " + coBuchung.getPerson() 
		+ " (" + Format.getString(datum) + (datumBis != null && !datum.equals(datumBis) ? "-" + Format.getString(coBuchung.getDatumBis()) : "") 
		+ " zu " + coDienstreise.getZiel() + ")", CoMessageQuittierung.ID_DR_INFO, false, coBuchung.getDienstreiseID(), drDg);
	}


	/**
	 * Neue Message zur A1-Bescheinigung
	 * 
	 * @param personID
	 * @param coBuchung 
	 * @param coDienstreise
	 * @throws Exception
	 */
	public void createMessageA1Bescheinigung(int personID, CoDienstreise coDienstreise, CoBuchung coBuchung) throws Exception {
		createMessage(personID, " benötigt für seine/ihre Dienstreise am " + Format.getString(coBuchung.getDatum()) 
		+ " zu " + coDienstreise.getZiel() + " (" + coDienstreise.getLand() + ")" + " eine A1-Bescheinigung.", CoMessageQuittierung.ID_A1_BESCHEINIGUNG);
	}


	/**
	 * Neue Message zum Monatseinsatzblatt eintragen
	 * 
	 * @param personID
	 * @param wertZeit
	 * @throws Exception
	 */
	public void createMessageMonatseinsatzblattFehlstunden(int personID, int wertZeit) throws Exception {
		createMessage(personID, " hat " + Format.getZeitAsText(wertZeit) + " Stunden noch nicht im Monatseinsatzblatt eingetragen.", 
				CoMessageQuittierung.ID_MONATSEINSATZBLATT_EINTRAGEN);
	}


	/**
	 * Neue Message zu Projekten (allgemeinen w-Aufträgen), wenn 8 Stunden oder mehr pro Person eingetragen wurden
	 * 
	 * @param personID
	 * @param gregDatum 
	 * @param wertZeit
	 * @param auftragID 
	 * @throws Exception
	 */
	public void createMessageMonatseinsatzblattWsonstiges(int personID, GregorianCalendar gregDatum, int wertZeit, int auftragID) throws Exception {
		CoAuftrag coAuftrag;
		
		coAuftrag = new CoAuftrag();
		coAuftrag.loadByID(auftragID);
		
		createMessage(coAuftrag.getAbteilungsleiterID(), getName(personID) + " hat im " + Format.getMonat(gregDatum) + " " + Format.getZeitAsText(wertZeit) 
		+ " Stunden auf " + coAuftrag.getAuftragsNr() 
		+ "  eingetragen.", CoMessageQuittierung.ID_W_SONSTIGES8STUNDEN, false, coAuftrag, true);
	}


	/**
	 * Neue Message zu allgemeinen w-Aufträgen, wenn 8 Stunden oder mehr eingetragen wurden
	 * 
	 * @param personID
	 * @param gregDatum 
	 * @param wertZeit
	 * @param auftragID 
	 * @throws Exception
	 */
	public void createMessageProzentmeldung(CoProjekt coProjekt) throws Exception {
		String beschreibung;
		
		beschreibung = "Bei dem " + (coProjekt instanceof CoAuftrag ? "Auftrag" : "Abruf") + " '" + coProjekt.getProjektNr() 
				+ "' wurden " + coProjekt.getVerbrauchBestellwertInProzent() + " des Bestellwerts erreicht.";
//		createMessage(coProjekt.getProjektleiterID(), "Bei dem " 
//				+ (coProjekt instanceof CoAuftrag ? "Auftrag" : "Abruf") + " '" + coProjekt.getProjektNr() 
//				+ "' wurden " + coProjekt.getVerbrauchBestellwertInProzent() + " des Bestellwerts erreicht.", 
//				CoMessageQuittierung.ID_PROJEKT_PROZENTMELDUNG, false, coProjekt, true);
	
		createNew(0, CoMessageQuittierung.ID_PROJEKT_PROZENTMELDUNG, beschreibung, CoMessageGruppe.ID_VERWALTUNG, false, coProjekt, null);
		createNew(coProjekt.getAbteilungsleiterID(), CoMessageQuittierung.ID_PROJEKT_PROZENTMELDUNG, beschreibung, CoMessageGruppe.ID_MITARBEITER, false, coProjekt, null);
		if (coProjekt.getAbteilungsleiterID() != coProjekt.getProjektleiterID())
		{
			createNew(coProjekt.getProjektleiterID(), CoMessageQuittierung.ID_PROJEKT_PROZENTMELDUNG, beschreibung, CoMessageGruppe.ID_MITARBEITER, false, coProjekt, null);
		}
	}


	/**
	 * Message zur neuen Projektzuteilung erstellen
	 * 
	 * @param personID
	 * @param coProjekt
	 * @param projektNeu 
	 * @throws Exception
	 */
	public void createMessageProjektZugeteilt(int personID, CoProjekt coProjekt, boolean projektNeu) throws Exception {
		createMessage(personID, "Sie wurden dem " + (coProjekt instanceof CoAuftrag ? "Auftrag" : "Abruf") + " " 
				+ coProjekt.getProjektNr() + " zugeteilt."
				+ (projektNeu ? " Der " + (coProjekt instanceof CoAuftrag ? "Auftrag" : "Abruf") + " wurde Ihrem Monatseinsatzblatt hinzugefügt." : ""), 
				CoMessageQuittierung.ID_PROJEKT_ZUGETEILT, false, coProjekt, false);
	}


	/**
	 * Message zur geänderten Projektzuteilung erstellen
	 * 
	 * @param personID
	 * @param coProjekt
	 * @param coMitarbeiterProjekt 
	 * @throws Exception
	 */
	public void createMessageProjektzuteilungGeaendert(int personID, CoProjekt coProjekt, CoMitarbeiterProjekt coMitarbeiterProjekt) throws Exception {
		int budget;
		Date datum, datumBis;
		
		budget = coMitarbeiterProjekt.getWertZeit();
		datum = coMitarbeiterProjekt.getDatum();
		datumBis = coMitarbeiterProjekt.getDatumBis();
		
		createMessage(personID, ("Ihre Zuteilung zum " + (coProjekt instanceof CoAuftrag ? "Auftrag" : "Abruf") + " " 
				+ coProjekt.getProjektNr() + " wurde geändert ("
				+ (datum != null ? "ab " + Format.getString(datum) + " " : "")
				+ (datumBis != null ? "bis " + Format.getString(datumBis) + " " : "")
				+ (budget > 0 ? ", " + Format.getZeitAsText(budget) + " Stunden" : "")
					+ ").").replace(" )", ")").replace("(, ", "(").replace(" ,", ",").replace(" ()", ""), 
				CoMessageQuittierung.ID_PROJEKTZUTEILUNG_GEAENDERT, false, coProjekt, false);
	}


	/**
	 * Messages zum Start einer neuen Runde der Projektverfolgung (ETC-Prüfung)
	 * 
	 * @param coProjekt
	 * @throws Exception 
	 */
	public void createMessageProjektverfolgungPrognoseErstellen(CoProjekt coProjekt) throws Exception {
		createMessage(coProjekt.getProjektleiterID(), "Bitte prüfen Sie als " 
				// Message anpassen wenn AL=PL 
				+ (coProjekt.getAbteilungsleiterID() == coProjekt.getProjektleiterID() ? "Abteilungsleiter" : "Projektleiter")
				+ " Termin/Kosten zum " + (coProjekt instanceof CoAuftrag ? "Auftrag" : "Abruf") + " '" 
				+ coProjekt.getProjektNr() + "'.", 
				CoMessageQuittierung.ID_PROJEKTVERFOLGUNG_PROGNOSE_ERSTELLEN, false, coProjekt, false);
	}


	/**
	 * Messages zum Prüfen der Prognose des PLs
	 * 
	 * @param coProjekt
	 * @throws Exception 
	 */
	public void createMessageProjektverfolgungPrognosePruefen(CoProjekt coProjekt, boolean prognoseVorhanden) throws Exception {
		String message;
		
		// die Message unterscheidet sich, je nachdem ob die Prognose durch den PL bereits erstellt wurde
		if (prognoseVorhanden)
		{
			message = "Bitte prüfen Sie die Termin/Kosten-Prognose des Projektleiters zum " 
					+ (coProjekt instanceof CoAuftrag ? "Auftrag" : "Abruf") + " '" + coProjekt.getProjektNr() + "'.";
		}
		else
		{
			message = "Es wurde noch keine Termin/Kosten-Prognose des Projektleiters zum " 
					+ (coProjekt instanceof CoAuftrag ? "Auftrag" : "Abruf") + " '" + coProjekt.getProjektNr() + "' erstellt.";
		}
		
		createMessage(coProjekt.getAbteilungsleiterID(), message, 
				CoMessageQuittierung.ID_PROJEKTVERFOLGUNG_PROGNOSE_PRUEFEN, false, coProjekt, true);
	}


	/**
	 * Messages bei unterschiedlicher Termin/Kosten-Prognose von PL und AL
	 * 
	 * @param coProjekt
	 * @param coProjektverfolgung 
	 * @throws Exception 
	 */
	public void createMessageProjektTerminKostenAenderungUnterschied(CoProjekt coProjekt, CoProjektverfolgung coProjektverfolgung) throws Exception {
		String meldung;
		
		meldung = "Ihre Termin/Kosten-Prognose zum " 
				+ (coProjekt instanceof CoAuftrag ? "Auftrag" : "Abruf") + " '" + coProjekt.getProjektNr() + "' wurde von " 
				+ getName(coProjektverfolgung.getALID()) + " nicht übernommen.";
		
		// Message erzeugen
		createMessage(coProjektverfolgung.getPLID(), meldung, CoMessageQuittierung.ID_PROJEKTAENDERUNG_NICHT_UEBERNOMMEN, false, coProjekt, true);
	}


	/**
	 * Messages zum Übernehmen der geänderten Werte durch die Buchhaltung
	 * 
	 * @param coProjekt
	 * @param coProjektverfolgung 
	 * @throws Exception 
	 */
	public void createMessageProjektTerminKostenAenderungUebernehmen(CoProjekt coProjekt, CoProjektverfolgung coProjektverfolgung) throws Exception {
		String termin, meldung;
		
		meldung = "Bitte übernehmen Sie die Termin/Kosten-Prognose zum " 
				+ (coProjekt instanceof CoAuftrag ? "Auftrag" : "Abruf") + " '" + coProjekt.getProjektNr() + "' (";
		
		// neue Daten
		termin = Format.getString(coProjektverfolgung.getTerminAL());
		meldung += termin == null ? "" : termin + "/";
		meldung += Format.getZeitAsText(coProjektverfolgung.getKostenAL()) + ", bisher ";
		
		// bisherige Daten
		termin = Format.getString(coProjekt.getLiefertermin());
		meldung += termin == null ? "" : termin + "/";
		meldung += Format.getZeitAsText(coProjekt.getBestellwert()) + ")";
		
		// Message erzeugen
		createMessage(0, meldung, CoMessageQuittierung.ID_PROJEKTAENDERUNG_UEBERNEHMEN, false, coProjekt, true);
		
		// ID der Message in Projektverfolgung speichern
		saveIdInProjektverfolgung(coProjektverfolgung);
		
		// ggf. Info-Message an PL
		createPlInfoMessage(coProjekt, coProjektverfolgung, meldung, CoMessageQuittierung.ID_PLINFO_PROJEKTAENDERUNG_UEBERNEHMEN);
	}


	/**
	 * Messages zum Schließen eines Projektes durch die Buchhaltung
	 * 
	 * @param coProjekt
	 * @throws Exception 
	 */
	public void createMessageProjektSchliessen(CoProjekt coProjekt, CoProjektverfolgung coProjektverfolgung) throws Exception {
		String meldung;

		meldung = "Bitte schließen Sie den " 
				+ (coProjekt instanceof CoAuftrag ? "Auftrag" : "Abruf") + " '" + coProjekt.getProjektNr() + "'.";

		createMessage(0, meldung, CoMessageQuittierung.ID_PROJEKT_SCHLIESSEN, false, coProjekt, true);

		// ID der Message in Projektverfolgung speichern
		saveIdInProjektverfolgung(coProjektverfolgung);

		// ggf. Info-Message an PL
		createPlInfoMessage(coProjekt, coProjektverfolgung, meldung, CoMessageQuittierung.ID_PLINFO_PROJEKT_SCHLIESSEN);
	}


	/**
	 * Info-Message über Buchhaltungs-Aktion
	 * 
	 * @param coProjekt
	 * @param coProjektverfolgung
	 * @param meldung
	 * @param messageQuittierungID
	 * @throws Exception
	 */
	private void createPlInfoMessage(CoProjekt coProjekt, CoProjektverfolgung coProjektverfolgung, String meldung, int messageQuittierungID)
			throws Exception {
		int plID, alID, plProjektID, alProjektID;
	
		meldung = "Aktion bei Buchh. beantragt: " + meldung;
		alID = coProjektverfolgung.getALID();
		plID = coProjektverfolgung.getPLID();
		alProjektID = coProjekt.getAbteilungsleiterID();
		plProjektID = coProjekt.getProjektleiterID();
		
		// Message an AL, wenn ein anderer die Aktion ausgelöst hat
		if (alID != alProjektID)
		{
			createMessage(alProjektID, meldung, messageQuittierungID, false, coProjekt, true);
		}
		
		// Message an PL, wenn es nicht der AL ist
		if (alID != plProjektID && alProjektID != plProjektID)
		{
			createMessage(plProjektID, meldung, messageQuittierungID, false, coProjekt, true);
		}
		
		// Message an PL2, falls der die Projektverfolgung angestoßen hat
		if (plID > 0 && plID != plProjektID)
		{
			createMessage(plID, meldung, messageQuittierungID, false, coProjekt, true);
		}
	}


	/**
	 * ID der Message in Projektverfolgung speichern
	 * 
	 * @param coProjektverfolgung
	 * @throws Exception
	 */
	private void saveIdInProjektverfolgung(CoProjektverfolgung coProjektverfolgung) throws Exception {
		if (!coProjektverfolgung.isEditing())
		{
			coProjektverfolgung.begin();
		}
		coProjektverfolgung.setMeldungID(getID());
		coProjektverfolgung.save();
	}


	/**
	 * Neue Message zum Resturlaub erstellen
	 * 
	 * @param personID
	 * @param anzahlResturlaub
	 * @throws Exception
	 */
	public void createMessageErsthelfer() throws Exception {
		createMessage(0, "Heute ist kein Erst-/Brandschutzhelfer vor Ort.", CoMessageQuittierung.ID_KEIN_ERSTHELFER, false);
	}


//	/**
//	 * Message zu einer Verletzermeldung erzeugen
//	 * 
//	 * @param meldungID
//	 * @param personID
//	 * @param datum
//	 * @param zeitinfo
//	 * @throws Exception
//	 */
//	public void createMessageVerletzermeldung(int meldungID, int personID, Date datum, int zeitinfo) throws Exception {
//		CoMeldungVerletzerliste coMeldungVerletzerliste;
//		
//		coMeldungVerletzerliste = CoMeldungVerletzerliste.getInstance();
//		coMeldungVerletzerliste.moveToID(meldungID);
//		
//		// Meldung erstellen
//		createNew(personID, coMeldungVerletzerliste.getBezeichnung() + (zeitinfo > 0 ? " (" + Format.getZeitAsText(zeitinfo) + ")" : ""), datum, 
//				CoMessageGruppe.ID_VERWALTUNG, false, null, null);
//		
//		// speichern
//		save();
//	}


	/**
	 * Message bei zu hohem Gleitzeitkontostand zum Jahresende
	 * 
	 * @param coKontowert
	 * @throws Exception 
	 */
	public void createMessageUeberstunden(CoKontowert coKontowert) throws Exception {
		int monat, standGleitzeitkonto;
		
		// nicht jeden Monat prüfen
		monat = coKontowert.getGregDatum().get(Calendar.MONTH);
		if (monat < Calendar.SEPTEMBER || monat == Calendar.DECEMBER)
		{
			return;
		}
		
		// Stand Gleitzeitkonto nach Auszahlung
		standGleitzeitkonto = coKontowert.getWertUeberstundenGesamt() 
				- coKontowert.getWertAuszahlungUeberstundenProjekt() - coKontowert.getWertAuszahlungUeberstundenReise();
		if (standGleitzeitkonto > 20*60)
		{
			createMessage(coKontowert.getPersonID(), "Ihr aktueller Gleitzeitkontostand beträgt " + Format.getZeitAsText(standGleitzeitkonto) 
			+ ", bitte beachten Sie die Grenze von 20:00 Stunden zum Jahresende.", CoMessageQuittierung.ID_GLEITZEITKONTOSTAND, false);
		}
		
	}


	/**
	 * alle offenen Meldungen laden
	 * 
	 * @param messageGruppeID 
	 * @param meldungsTyp 
	 * @throws Exception
	 */
	@Override
	public void loadByStatusOffen(int messageGruppeID, String meldungsTyp) throws Exception {
		loadByStatus(CoStatusMessage.STATUSID_OFFEN, messageGruppeID, meldungsTyp, null, null);
	}
	

	/**
	 * alle quittierten Meldungen laden
	 * 
	 * @param messageGruppeID 
	 * @throws Exception
	 */
	@Override
	public void loadByStatusQuittiert(int messageGruppeID, String meldungsTyp, Date datumVon, Date datumBis) throws Exception {
		loadByStatus(CoStatusMessage.STATUSID_QUITTIERT, messageGruppeID, meldungsTyp, datumVon, datumBis);
	}
	

	/**
	 * CO für den übergebenen Status laden
	 * 
	 * @param statusID
	 * @param messageGruppeID 
	 * @param meldungsTyp 
	 * @param datumBis 
	 * @param datumVon 
	 * @throws Exception
	 */
	private void loadByStatus(int statusID, int messageGruppeID, String meldungsTyp, Date datumVon, Date datumBis) throws Exception {
		int personID;
		boolean joinTblPerson;
		String sql, where, whereDatum, whereFreigabeBerechtigungen;
		CoPerson coPerson;
		CoFreigabeberechtigungen coFreigabeberechtigungen;

		personID = UserInformation.getPersonID();
		joinTblPerson = false;
		coPerson = new CoPerson();
		coPerson.loadByID(personID);
		
		// Berechtigungen bestimmen
		coFreigabeberechtigungen = new CoFreigabeberechtigungen();
	
		// Abfrage erstellen
		whereDatum = CoAuswertung.getWhereDatum(datumVon, datumBis);
		where = "StatusID=" + statusID + (whereDatum != null ? " AND (" + whereDatum + ")" : "");

		// nach Berechtigungen unterscheiden
		
		// AL nur Meldungen der eigenen Mitarbeiter je nach Berechtigung
		if (messageGruppeID == CoMessageGruppe.ID_AL)
		{
			// Berechtigungen bestimmen
			whereFreigabeBerechtigungen = coFreigabeberechtigungen.createWhere(personID, false);
			
			// wenn die Person keine Berechtigungen mehr hat, können keine Daten geladen werden
			if (whereFreigabeBerechtigungen == null)
			{
				emptyCache();
				return;
			}
			where += " AND (" + whereFreigabeBerechtigungen + ")";
			
			// Sonderfall Sekretärinnen
			if (coFreigabeberechtigungen.isFreigabeVerwaltungErlaubt())
			{
				where += " AND (";
				// normale MA aus der Abteilung
				where += " ( MessageGruppeID=" + messageGruppeID;
				where += " AND PersonID NOT IN (SELECT ID FROM tblPerson WHERE PositionID=" + CoPosition.ID_SEKRETAERIN + ") )";
				
				where += " OR ";
				// Sekretärinnen
				where += " ( MessageGruppeID=" + CoMessageGruppe.ID_SEKRETAERIN;
				where += " AND PersonID IN (SELECT ID FROM tblPerson WHERE PositionID=" + CoPosition.ID_SEKRETAERIN + ") )";

				where += ")";
			}
			else
			{
				where += " AND MessageGruppeID=" + messageGruppeID;
			}
			
			// Infos aus tblPerson werden benötigt
			joinTblPerson = true;
		}
		else // nicht AL-Message
		{
			where += " AND MessageGruppeID=" + messageGruppeID;
		}
		
		// Meldungen für das Sekretariat nach Personen unterscheiden
		if (messageGruppeID == CoMessageGruppe.ID_SEKRETARIAT)
		{
			// Infos aus tblPerson werden benötigt
			joinTblPerson = true;
			where += " AND AbteilungID IN (" + coPerson.getCoPersonAbteilungsrechte(false).getSelectedIDs() + ")";
		}

		// persönliche Meldungen für den Mitarbeiter 
		if (messageGruppeID == CoMessageGruppe.ID_MITARBEITER)
		{
			where += " AND (PersonID=" + personID;
			
			// Berechtigungen bestimmen, bei der Projektverfolgung sollen auch Vertreter die Meldungen sehen
			whereFreigabeBerechtigungen = coFreigabeberechtigungen.createWherePerson(personID);
			if (whereFreigabeBerechtigungen != null && meldungsTyp.equals("IstMessageProjektverfolgung"))
			{
				where += " OR (" + whereFreigabeBerechtigungen + ")";
			}
		
			where += ")";
		}
		
		
		// Meldungen zu Projekten oder Personal/sonstige
//		if (FormMessageboard.MESSAGEBOARD_PROJEKTE_NEU)
		{
			where += " AND MeldungID IN (SELECT ID FROM rtblMessage WHERE " + meldungsTyp + "=1)";
		}
//		else
//		{
//			where += !meldungsTyp.equals("IstMessagePerson") ? " AND (AuftragID IS NOT NULL OR AbrufID IS NOT NULL) " : " AND AuftragID IS NULL AND AbrufID IS NULL ";
//		}

		// Statement zusammenbauen
		sql = "SELECT * FROM " + TABLE_NAME + " m " + (joinTblPerson ? " JOIN tblPerson p ON (m.PersonID=p.ID) " : "") 
				+ " WHERE " + where + " ORDER BY Datum DESC, Beschreibung";
		
		emptyCache();
		Application.getLoaderBase().load(this, sql);
	}
	

	/**
	 * CO mit Messages für ein Projekt laden
	 * 
	 * @param coProjekt
	 * @param meldungID
	 * @throws Exception
	 */
	public void loadByProjekt(CoProjekt coProjekt, int meldungID) throws Exception {
		String where;

		// Abfrage erstellen
		where = (coProjekt.isAuftrag() ? " AuftragID=" + coProjekt.getID() : " AbrufID=" + coProjekt.getID());
		where += " AND StatusID=" + CoStatusMessage.STATUSID_OFFEN;
		where += " AND MeldungID=" + meldungID;

		
		emptyCache();
		Application.getLoaderBase().load(this, where, getSortFieldName());
	}
	

	private IField getFieldMeldungID() {
		return getField("field." + getTableName() + ".meldungid");
	}


	/**
	 * Welche Art Message ist es/Grund der Erstellung
	 * 
	 * @return
	 */
	private void setMeldungID(int messageID) {
		getFieldMeldungID().setValue(messageID);
	}


	/**
	 * Welche Art Message ist es/Grund der Erstellung
	 * 
	 * @return
	 */
	public int getMeldungID() {
		return Format.getIntValue(getFieldMeldungID());
	}


	private IField getFieldMessageGruppeID() {
		return getField("field." + getTableName() + ".messagegruppeid");
	}


	/**
	 * Für welche Gruppe MA ist die Message (MA, AL...)
	 * 
	 * @return
	 */
	private void setMessageGruppeID(int messageGruppeID) {
		getFieldMessageGruppeID().setValue(messageGruppeID);
	}


//	public int getMessageGruppeID() {
//		return Format.getIntValue(getFieldMessageGruppeID());
//	}

	
	private void setAuftragID(int auftragID) {
		getFieldAuftragID().setValue(auftragID);
	}


	private void setAbrufID(int abrufID) {
		getFieldAbrufID().setValue(abrufID);
	}


	private void setDienstreiseID(int dienstreiseID) {
		getFieldDienstreiseID().setValue(dienstreiseID);
	}


	private void setStatusOffen() throws Exception {
		setStatusID(CoStatusMessage.STATUSID_OFFEN);
	}


	public void setStatusQuittiert() throws Exception {
		setStatusID(CoStatusMessage.STATUSID_QUITTIERT);
	}


	private IField getFieldBeschreibung() {
		return getField("field." + getTableName() + ".beschreibung");
	}


	@Override
	public String getBeschreibung() {
		return Format.getStringValue(getFieldBeschreibung().getValue());
	}


	private void setBeschreibung(String beschreibung) {
		getFieldBeschreibung().setValue(beschreibung);
	}


	/**
	 * auszugebener Name für die Person
	 * 
	 * @param personID
	 * @return
	 * @throws Exception
	 */
	private String getName(int personID) throws Exception {
		if (!m_coPerson.moveToID(personID))
		{
			// wenn z. B. eine neue Person nicht gefunden wurde, lade die Personen neu
			m_coPerson.loadAll();
			if (!m_coPerson.moveToID(personID))
			{
				return "";
			}
		}
		
		return m_coPerson.getNachnameVorname();
	}


	/**
	 * Quittierung für die aktuelle Message quittieren
	 * 
	 * @return Freigabe-Eintrag erstellt
	 * @throws Exception
	 */
	@Override
	public void createQuittierung() throws Exception {
		Object bookmark;
		
		bookmark = getBookmark();

		// Status-Eintrag erzeugen
//		coFreigabe = new CoFreigabe();
//		coFreigabe.createNew(getID(), getStatusID(), statusGenehmigungID, UserInformation.getPersonID(), new Date());
//		coFreigabe.save();


		// Status der Message setzen
		if (!isEditing())
		{
			begin();
		}
		setStatusQuittiert();
		

		// Änderung dokumentieren
		updateGeaendertVonAm();
		save();

		moveTo(bookmark);
	}


}
