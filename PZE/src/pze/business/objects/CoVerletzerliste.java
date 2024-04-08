package pze.business.objects;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import framework.Application;
import framework.business.interfaces.fields.IField;
import pze.business.Format;
import pze.business.UserInformation;
import pze.business.objects.auswertung.CoAuswertung;
import pze.business.objects.auswertung.CoAuswertungVerletzerliste;
import pze.business.objects.personen.CoPerson;
import pze.business.objects.reftables.CoFreigabeberechtigungen;
import pze.business.objects.reftables.CoMeldungVerletzerliste;
import pze.business.objects.reftables.CoMessageGruppe;
import pze.business.objects.reftables.CoStatusMessage;
import pze.business.objects.reftables.CoStatusVerletzung;
import pze.business.objects.reftables.personen.CoStatusAktivInaktiv;

/**
 * CacheObject für Verletzerliste
 * 
 * @author Lisiecki
 *
 */
public class CoVerletzerliste extends AbstractCoMessage {

	public static final String TABLE_NAME = "tblverletzerliste";

	private static CoVerletzerliste m_instance = null;
	
	private CoPerson m_coPerson;
	private Date m_datum;

	private CoZeitmodell m_coZeitmodell;
	
	

	/**
	 * Kontruktor
	 * @throws Exception 
	 */
	public CoVerletzerliste() throws Exception {
		super("table." + TABLE_NAME);
		
		addField("field.tblperson.nachname");
		addField("field.tblperson.vorname");
		addField("field.rtblmeldungverletzerliste.bezeichnung");
	}
	
	
	/**
	 * Kontruktor
	 * 
	 * @param coPerson
	 * @param datum
	 * @throws Exception
	 */
	public CoVerletzerliste(CoPerson coPerson, Date datum) throws Exception {
		super("table." + TABLE_NAME);
		
		init(coPerson, datum);
		
//		load(m_coPerson, m_datum);
	}


	/**
	 * Klassenvariablen initialisieren
	 * 
	 * @param coPerson
	 * @param datum
	 * @throws Exception
	 */
	private void init(CoPerson coPerson, Date datum) throws Exception {
		m_coPerson = coPerson;
		m_datum = datum;
		
		if (coPerson != null && datum != null)
		{
			m_coZeitmodell = m_coPerson.getCoZeitmodell(m_datum);
		}
	}
	

	/**
	 * Getter Instanz der Klasse
	 * 
	 * @return m_instance
	 */
	public static CoVerletzerliste getInstance() throws Exception {
		if (CoVerletzerliste.m_instance == null)
		{
			CoVerletzerliste.m_instance = new CoVerletzerliste();
		}
		
		return CoVerletzerliste.m_instance;
	}


	@Override
	public String getNavigationBitmap() {
		return "exclamation";
	}

	
	/**
	 * Laden der Verletzermeldungen vor dem heutigen Tag, ggf. nur für eine Personen und/oder ein Datum 
	 * 
	 * @param coPerson CoPerson oder null
	 * @param datum Datum oder null
	 * @throws Exception
	 */
	public void load(CoPerson coPerson, Date datum) throws Exception {
		load(coPerson, datum, false);
	}

	
	/**
	 * Laden der Verletzermeldungen für ein Datum oder alle vor dem heutigen Tag, ggf. nur für eine Person
	 * 
	 * @param coPerson CoPerson oder null
	 * @param datum Datum oder null für alle Meldungen vor dem heutigen Tag
	 * @param nurOffene nur offene (noch nicht freigegebene) Meldungen oder alle
	 * @throws Exception
	 */
	public void load(CoPerson coPerson, Date datum, boolean nurOffene) throws Exception {
		String sql, where;
		Date datumEndePze;
		GregorianCalendar gregDatum;

//		m_coPerson = coPerson;
		init(coPerson, datum);
		
		// ggf. Datum bestimmen
		gregDatum = new GregorianCalendar();
		if (datum != null)
		{
			gregDatum.setTime(datum);
		}
		
		// Ende PZE prüfen, damit danach keine Meldungen mehr geladen werden
		datumEndePze = coPerson.getEndePze();
		if (datumEndePze != null)
		{
			datumEndePze = Format.getDateVerschoben(Format.getDate0Uhr(datumEndePze), 1);
		}
		

		where = (datum == null ?  "datum < '" + Format.getString(new Date()) + "'"
				: (" YEAR(datum)=" + gregDatum.get(Calendar.YEAR)  + " AND MONTH(datum)=" + (gregDatum.get(Calendar.MONTH) + 1) 
						+ " AND DAY(datum)=" + gregDatum.get(Calendar.DAY_OF_MONTH) ))

				+ (datumEndePze != null ? " AND datum < '" + Format.getString(datumEndePze) + "' " : "")
				
				+ (coPerson != null ? " AND v.PersonID=" + coPerson.getID() : "")

				+ (nurOffene ? " AND (v.StatusID=" + CoStatusVerletzung.STATUSID_OFFEN + ")" : "");

		sql = "SELECT v.* FROM " + getTableName() + " v LEFT OUTER JOIN tblPerson p ON (v.PersonID=p.ID) JOIN rtblMeldungVerletzerliste r ON (v.MeldungID=r.ID)"
				+ " WHERE " + where 
				+ " ORDER BY " + getSortFieldName();

		emptyCache();
		Application.getLoaderBase().load(this, sql);
	}


	/**
	 * Daten in Abhängigkeit der gewählten Parameter laden
	 * 
	 * @param coAuswertungVerletzerliste
	 * @throws Exception 
	 */
	public void load(CoAuswertungVerletzerliste coAuswertungVerletzerliste) throws Exception {
		int statusID;
		String wherePerson, wherePosition, where, sql;

		
		// Status
		statusID = coAuswertungVerletzerliste.getStatusID();
		
		// SQL-Abfrage zusammensetzen
		wherePerson = coAuswertungVerletzerliste.getWherePerson();
		wherePosition = coAuswertungVerletzerliste.getWherePosition();
		where = coAuswertungVerletzerliste.getWhereDatumBisHeute() 

				+ (wherePerson != null ? " AND (" + wherePerson + ")" : "")
				+ (wherePosition != null ? wherePosition : "")
				+ (statusID != 0 ? " AND (StatusID=" + statusID + ")" : "")

//				+ (coAuswertungVerletzerliste.isHinweisGleitzeitkontoAusgeblendet() 
//						? " AND (MeldungID != " + CoMeldungVerletzerliste.MELDUNGID_HINWEIS_GLEITZEITKONTO + ")" : "")
				
				+ (coAuswertungVerletzerliste.isKeineBuchungAusgeblendet() ? " AND (MeldungID != " + CoMeldungVerletzerliste.MELDUNGID_KEINE_BUCHUNG + ")" : "")
				;

		sql = "SELECT * FROM " + getTableName() + " v JOIN tblPerson p ON (v.PersonID = p.ID) JOIN rtblMeldungVerletzerliste r ON (v.MeldungID = r.ID)"
				+ " WHERE " + where + " ORDER BY " + getSortFieldName();

		emptyCache();
		Application.getLoaderBase().load(this, sql);
	}


	/**
	 * alle offenen Meldungen laden
	 * 
	 * @param messageGruppeID 
	 * @param loadProjekte 
	 * @throws Exception
	 */
	@Override
	public void loadByStatusOffen(int messageGruppeID, String meldungID) throws Exception {
		loadByStatus(CoStatusVerletzung.STATUSID_OFFEN, messageGruppeID, null, Format.getDateVerschoben(Format.getDate0Uhr(new Date()), -1));
	}
	

	/**
	 * alle quittierten Meldungen laden
	 * 
	 * @param messageGruppeID 
	 * @throws Exception
	 */
	@Override
	public void loadByStatusQuittiert(int messageGruppeID, String meldungID, Date datumVon, Date datumBis) throws Exception {
		loadByStatus(CoStatusVerletzung.STATUSID_FREIGEGEBEN, messageGruppeID, datumVon, datumBis);
	}
	

	/**
	 * CO für den übergebenen Status laden
	 * 
	 * @param statusID
	 * @param messageGruppeID 
	 * @param datumBis 
	 * @param datumVon 
	 * @param loadProjekte 
	 * @throws Exception
	 */
	private void loadByStatus(int statusID, int messageGruppeID, Date datumVon, Date datumBis) throws Exception {
		int personID;
		String sql, where, whereDatum, whereFreigabeBerechtigungen;
		CoPerson coPerson;
		CoFreigabeberechtigungen coFreigabeberechtigungen;

		personID = UserInformation.getPersonID();
		coPerson = new CoPerson();
		
		whereDatum = CoAuswertung.getWhereDatum(datumVon, datumBis);

		// Berechtigungen bestimmen
		coFreigabeberechtigungen = new CoFreigabeberechtigungen();
		whereFreigabeBerechtigungen = coFreigabeberechtigungen.createWhere(personID, false);
		

		// Abfrage erstellen
		where = (messageGruppeID == CoMessageGruppe.ID_VERWALTUNG ? "m.StatusQuittierungID" : "m.StatusID") + "=" + statusID 
				+ " AND p.StatusAktivInaktivID=" + CoStatusAktivInaktiv.STATUSID_AKTIV;

		// nach Berechtigungen unterscheiden

		// Ansicht Sekretariat
		if (messageGruppeID == CoMessageGruppe.ID_SEKRETAERIN)
		{
			coPerson.loadByCurrentUser();
			where += " AND PersonID IN (" + coPerson.getIDs() + ")";
			
			// bei Anzeige der offenen Meldungen Unterscheidung nach Meldungsart
			if (statusID == CoStatusVerletzung.STATUSID_OFFEN)
			{
				// bis zum Vortag
				where += " AND ((" + whereDatum + " AND istAnzeigeSekretariatVortag=1" + ")";
				
				// aktueller Tag
				whereDatum = CoAuswertung.getWhereDatum(datumVon, Format.getDateVerschoben(datumBis, 1));
				where += " OR (" + whereDatum + " AND istAnzeigeSekretariatAktuell=1" + "))";

				// Datumseinschränkung muss dann vor der Ausführung nicht mehr hinzugefügt werden
				whereDatum = null;
			}
			
//			where += " AND istAnzeigeSekretariat=1";
		}
		// Freigabe Sekretariat
//		else if (messageGruppeID == CoMessageGruppe.ID_)
//		{
//			coPerson.loadByCurrentUser();
//			where += " AND PersonID IN (" + coPerson.getIDs() + ")";
//			where += " AND IstFreigabeSekretariat=1";
//		}
		// Freigabe AL
		else if (messageGruppeID == CoMessageGruppe.ID_AL && whereFreigabeBerechtigungen != null)
		{
			where += " AND (" + whereFreigabeBerechtigungen + ")";
			where += " AND IstFreigabeAL=1";
			
			// nicht für die eigene Person
			where += " AND PersonID<>" + personID;
		}
		// Verwaltung sieht bestimmt Verletzermeldungen
		else if (messageGruppeID == CoMessageGruppe.ID_VERWALTUNG)
		{
			where += " AND IstMessageboardPB=1";
			
			// nicht für die eigene Person
			where += " AND PersonID<>" + personID;
		}
		else
		{
			emptyCache();
			return;
		}

		// Datum berücksichtigen
		where += (whereDatum != null ? " AND (" + whereDatum + ")" : "");

		// Statement zusammenbauen
		sql = "SELECT * FROM " + TABLE_NAME + " m JOIN rtblMeldungVerletzerliste r ON (m.MeldungID=r.ID) JOIN tblPerson p ON (m.PersonID=p.ID)"
				+ " WHERE " + where 
				+ " ORDER BY Datum, Nachname, Vorname, PersonID, MeldungID, Zeitinfo";
		
		System.out.println(sql);
		long a1 = System.currentTimeMillis();
		emptyCache();
		Application.getLoaderBase().load(this, sql);
		System.out.println("SQL Verletzerliste:" + Format.getFormat2NksPunkt((System.currentTimeMillis() - a1)/1000.));
	}
	

	protected String getSortFieldName() {
		return "Datum, Nachname, Vorname, PersonID, Bezeichnung, MeldungID, Zeitinfo";
	}
	

//
//	/**
//	 * Sortierfelder, bei denen der Name berücksichtigt wird.<br>
//	 * Dafür muss tblPerson mit ausgewählt werden.
//	 * 
//	 * @return
//	 */
//	private String getSortFieldNameMitPerson() {
//	}
//	

	public IField getFieldZeitinfo() {
		return getField("field." + getTableName() + ".zeitinfo");
	}

	
	private void setZeitinfo(int zeitinfo) {
		getFieldZeitinfo().setValue(zeitinfo);
	}


	public IField getFieldMeldungID() {
		return getField("field." + getTableName() + ".meldungid");
	}
	

	public int getMeldungID() {
		return Format.getIntValue(getFieldMeldungID().getValue());
	}
	

	private void setMeldungID(int meldungID) {
		getFieldMeldungID().setValue(meldungID);
	}
	
	
	public String getMeldung() throws Exception {
		return CoMeldungVerletzerliste.getInstance().getBezeichnung(getMeldungID());
	}
	
	
	@Override
	public String getBeschreibung(){
		try 
		{
			return getMeldung();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return null;
	}
	

	public String getStatus() throws Exception {
		return CoStatusVerletzung.getInstance().getBezeichnung(getStatusID());
	}


	private void setStatusOffen() throws Exception {
		setStatusID(CoStatusVerletzung.STATUSID_OFFEN);
	}


	private IField getFieldStatusQuittierungID() {
		return getField("field." + getTableName() + ".statusquittierungid");
	}


	private void setStatusQuittierungID(int statusID) {
		getFieldStatusQuittierungID().setValue(statusID);
	}

	private void setStatusQuittierungOffen() throws Exception {
		setStatusQuittierungID(CoStatusMessage.STATUSID_OFFEN);
	}


	/**
	 * Gehe zu dem Eintrag mit der MeldungID
	 * 
	 * @param id
	 */
	public boolean moveToMeldungID(int id) {
		return moveTo(id, "field." + getTableName() + ".meldungid");
	}
	

	/**
	 * Prüft eine Regel und erstellt ggf. eine Verletzermeldung.<br>
	 * Person und Datum müssen mit dem Konstruktor übergeben werden.<br>
	 * PK wird in der DB automatisch erzeugt, da per Trigger ebenfalls Einträge generiert werden.
	 * 
	 * @param isRegelEingehalten Regel, die eingehalten werden muss. Wird sie nicht eingehalten, erfolgt eine Verletzermeldung.
	 * @param meldungID ID der Meldung, falls die Regel nicht eingehalten wird
	 * @return Boolean: Prüfung war erfolgreich, keine Meldung generiert
	 * @throws Exception
	 */
	public boolean check(boolean isRegelEingehalten, int meldungID) throws Exception	{
		CoMeldungVerletzerliste coMeldungVerletzerliste;

		// wenn keine vollständigen Daten vorhanden sind kann keine Prüfung erfolgen
		if (m_coZeitmodell == null)
		{
			return true;
		}
		
		// je nach Art der meldung und Einstellung für den Benutzer ist keine Meldung notwendig
		coMeldungVerletzerliste = CoMeldungVerletzerliste.getInstance();
		coMeldungVerletzerliste.moveToID(meldungID);
		if ( (coMeldungVerletzerliste.isMeldungArbeitszeitBeginn() && !m_coZeitmodell.isMeldungArbeitszeitBeginnAktiv())
				|| (coMeldungVerletzerliste.isMeldungArbeitszeitEnde() && !m_coZeitmodell.isMeldungArbeitszeitEndeAktiv())
				|| (coMeldungVerletzerliste.isMeldungArbZgKontostand() && !m_coZeitmodell.isMeldungArbZgKontostandAktiv()) 
				|| (coMeldungVerletzerliste.isMeldungArbeitstag() && !m_coZeitmodell.isMeldungArbeitstagAktiv()) )
		{
			return true;
		}
		
		// Bearbeiten starten
		if (!isEditing())
		{
			begin();
		}
		
		// wenn bereits ein Datensatz mit dieser Meldung existiert, lösche ihn
		// ist nur relevant, bei Änderungen der Kontowerte über die Oberfläche
		if (getRowCount() > 0 && moveToMeldungID(meldungID))
		{
//			delete();
		}
		
		// Regel neu einfügem
		if (!isRegelEingehalten)
		{
			// TODO ID selbst bestimmen und eintragen, aktuell ist es noch ein auto-index
			// sonst gibt es manchmal probleme beim löschen, 
			// Beispiel: Reisezeit 0-> meldung, anschließend direkt eingetragen-> löschen funktioniert nicht weil die ID nicht bekannt ist
			// TODO nur eine Krücke weil sonst die Meldung bei Reisezeit = 0 (weil Weg zur Arbeit länger) immer wieder neu erzeugt wird
			add();
//			setID(nextID());
			setPersonID(m_coPerson.getID());
			setDatum(m_datum);
			setMeldungID(meldungID);
			setStatusOffen();
			
			// Status Quittierung für Meldungen an die PB
			if (coMeldungVerletzerliste.isAnzeigeMessageboardPB())
			{
				setStatusQuittierungOffen();
			}
			
			return false;
		}
		
		return true;
	}


	/**
	 * Prüft eine Regel und erstellt ggf. eine Verletzermeldung oder löscht eine bereits vorhandene.<br>
	 * Person und Datum müssen mit dem Konstruktor übergeben werden.<br>
	 * PK wird in der DB automatisch erzeugt, da per Trigger ebenfalls Einträge generiert werden.
	 * 
	 * @param isRegelEingehalten Regel, die eingehalten werden muss. Wird sie nicht eingehalten, erfolgt eine Verletzermeldung.
	 * @param meldungID ID der Meldung, falls die Regel nicht eingehalten wird
	 * @param zeitinfo Zeitinfo zur Verletzermeldung
	 * @throws Exception
	 */
	public void check(boolean isRegelEingehalten, int meldungID, int zeitinfo) throws Exception	{
		
		// wenn ein Fehler generiert wurde, speichere die Zeitinfo
		if (!check(isRegelEingehalten, meldungID))
		{
			setZeitinfo(zeitinfo);
			
			// ggf. eine zusätzliche Message für die Personalabteilung erstellen
//			if (CoMeldungVerletzerliste.getInstance().isAnzeigeMessageboardPB(meldungID))
//			{
//				new CoMessage().createMessageVerletzermeldung(meldungID, m_coPerson.getID(), m_datum, zeitinfo);
//			}
		}
	}


	/**
	 * Arbeitzeit der gesamten Woche prüfen über Trigger in CoKontowert
	 * 
	 * @throws Exception
	 */
//	public void checkArbeitszeitWoche(int arbeitszeitWoche) throws Exception {
//		
//		deleteMeldung(CoMeldungVerletzerliste.MELDUNG_UEBERSCHREITUNG_ARBEITSZEIT_WOCHE);
//		
//		check(arbeitszeitWoche <= CoFirmenparameter.getInstance().getMaxWochenarbeitszeit(),
//				CoMeldungVerletzerliste.MELDUNG_UEBERSCHREITUNG_ARBEITSZEIT_WOCHE, arbeitszeitWoche);
//	}


	/**
	 * Löschen einer Verletzermeldung, falls sie existiert
	 * 
	 * @param meldungID
	 * @throws Exception
	 */
	public boolean deleteMeldung(int meldungID) throws Exception {
		// Bearbeiten starten
		if (!isEditing())
		{
			begin();
		}

		if (getRowCount() > 0 && moveToMeldungID(meldungID))
		{
			delete();
			return true;
		}
		
		return false;
	}


	/**
	 * Quittierung für die aktuelle Message quittieren
	 * 
	 * @return Freigabe-Eintrag erstellt
	 * @throws Exception
	 */
	public void createQuittierung() throws Exception {
		Object bookmark;
		
		bookmark = getBookmark();

		// Status der Message setzen
		if (!isEditing())
		{
			begin();
		}
		
		// offene Meldung freigeben
		if (getStatusID() == CoStatusVerletzung.STATUSID_OFFEN)
		{
			setStatusID(CoStatusVerletzung.STATUSID_FREIGEGEBEN);

			// Änderung dokumentieren
			updateGeaendertVonAm();
		}
		else // freigegebene Meldungen quittieren durch PB
		{
			setStatusQuittierungID(CoStatusMessage.STATUSID_QUITTIERT);
		}
		
		// speichern
		save();

		moveTo(bookmark);
	}


	/**
	 * Änderungen dokumentieren
	 * 
	 * @throws Exception
	 */
	public void valueChanged() throws Exception {
		String datum;

		datum = Format.getStringMitUhrzeit(Format.getGregorianCalendar(null));
				
		// Speichern von wem die Änderungen gemacht wurden
		setGeaendertVonID(UserInformation.getPersonID());
		setGeaendertAm(datum);
	}


	/**
	 * prüft, ob es einen Eintrag vor dem übergebenen Datum gibt
	 * 
	 * @param datum
	 * @return
	 */
	public boolean hasEintragBefore(Date datum) {
		
		// Prüfdatum auf den nächsten Tag 0 Uhr setzen (zur einfacheren Prüfung)
		datum = Format.getDate0Uhr(Format.getDateVerschoben(datum, 1));
		
		if (moveFirst())
		{
			do
			{
				// jedes Datum prüfen
				if (getDatum().before(datum))
				{
					return true;
				}
			} while(moveNext());
		}
		
		return false;
	}
	




}
