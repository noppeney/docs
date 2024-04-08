package pze.business.objects.personen;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import framework.Application;
import framework.business.interfaces.fields.IField;
import pze.business.FeiertagGenerator;
import pze.business.Format;
import pze.business.UserInformation;
import pze.business.objects.AbstractCacheObject;
import pze.business.objects.CoBrueckentag;
import pze.business.objects.CoZeitmodell;
import pze.business.objects.archiv.Archivierer;
import pze.business.objects.dienstreisen.CoDienstreise;
import pze.business.objects.dienstreisen.CoDienstreiseAbrechnung;
import pze.business.objects.projektverwaltung.CoAuftrag;
import pze.business.objects.projektverwaltung.CoProjekt;
import pze.business.objects.reftables.buchungen.CoBuchungsart;
import pze.business.objects.reftables.buchungen.CoStatusBuchung;
import pze.business.objects.reftables.personen.CoAbteilung;
import pze.business.objects.reftables.personen.CoPosition;
import pze.business.objects.reftables.personen.CoStatusAktivInaktiv;
import pze.business.objects.reftables.personen.CoStatusInternExtern;

/**
 * CacheObject für Personen
 * 
 * @author Lisiecki
 *
 */
public class CoPerson extends AbstractCacheObject {

	public static final String TABLE_NAME = "tblperson";

	private static CoPerson m_instance = null;
	


	/**
	 * Kontruktor
	 */
	public CoPerson() {
		super("table." + TABLE_NAME);
	}
	

	/**
	 * Konstruktor
	 * 
	 * @param tableResID
	 */
	public CoPerson(String tableResID) {
		super(tableResID);
	}


	/**
	 * Getter Instanz mit allen Personen 
	 * 
	 * @return Instanz der Klasse 
	 */
	public static CoPerson getInstance() throws Exception {
		if (CoPerson.m_instance == null)
		{
			CoPerson.m_instance = new CoPerson();
			CoPerson.m_instance.loadAll();
		}
		
		return CoPerson.m_instance;
	}


//	/**
//	 * Alle Datensätze mit Status laden, Archiv-Person wird somit nicht geladen
//	 * 
//	 * @throws Exception aus Loaderbase
//	 */
//	@Override
//	public void loadAll() throws Exception {
//		emptyCache();
//		Application.getLoaderBase().load(this, "StatusAktivInaktivID IS NOT NULL", getSortFieldName());
//	}


	/**
	 * Alle Datensätze mit dem übergebenen Status laden.<br>
	 * Das aktuelle Zeitmodell wird geladen, wenn die Person noch aktiv ist.
	 * 
	 * @param statusAktivInaktivID
	 * @throws Exception aus Loaderbase
	 */
	public void loadAllWithZeitmodell(int statusAktivInaktivID) throws Exception {
		String sql;

		addField("field.stblpersonzeitmodell.zeitmodellid");

		sql = getSqlSelectWithZeitmodell(statusAktivInaktivID, Format.getGregorianCalendar12Uhr(null));
		
		Application.getLoaderBase().load(this, sql);
	}

	
	/**
	 * Alle Datensätze mit dem übergebenen Status laden.<br>
	 * Die Benutzergruppen der Benutzers werden geladen.
	 * 
	 * @param statusAktivInaktivID
	 * @throws Exception aus Loaderbase
	 */
	public void loadWithBenutzergruppe(int statusAktivInaktivID) throws Exception {
		String sql;

		addField("virt.field.person.name");
		addField("field.rtblgroups.groupname");

		sql = "SELECT *, (Nachname + ', ' + Vorname) AS Name"
				+ " FROM " + getTableName() + " p JOIN stblUsersGroups s ON (p.UserID = s.UserID) JOIN rtblGroups g ON (s.GroupID = g.GroupID)"
				+ " WHERE StatusAktivInaktivID = " + statusAktivInaktivID
				+ " ORDER BY " + getSortFieldName() + ", GroupName";
		
		Application.getLoaderBase().load(this, sql);
	}
	

	/**
	 * CO für die Person, die der userID zugeordnet ist laden
	 * 
	 * @param userID
	 * @throws Exception
	 */
	public void loadByUserID(int userID) throws Exception {
		emptyCache();
		Application.getLoaderBase().load(this, "userID=" + userID, getSortFieldName());
	}
	

//	/**
//	 * CO für die Person mit StatusAktivInaktivID=Aktiv laden
//	 * 
//	 * @throws Exception
//	 */
//	private void loadByStatusAktiv() throws Exception {
//		emptyCache();
//		Application.getLoaderBase().load(this, "StatusAktivInaktivID=" + CoStatusAktivInaktiv.STATUSID_AKTIV , getSortFieldName());
//	}
	

	/**
	 * CO für die Person mit StatusInternExtern=Extern laden
	 * 
	 * @throws Exception
	 */
	public void loadByStatusExtern() throws Exception {
		emptyCache();
		Application.getLoaderBase().load(this, "(StatusInternExternID=" + CoStatusInternExtern.STATUSID_EXTERN 
				+ " OR StatusInternExternID=" + CoStatusInternExtern.STATUSID_EXTERN_WTI + ")" , getSortFieldName());
	}
	

//	/**
//	 * CO für die Person mit dem übergebenen StatusInternExtern laden
//	 * 
//	 * @param statusInternExternID
//	 * @throws Exception
//	 */
//	private void loadByStatusInternExternID(int statusInternExternID) throws Exception {
//		emptyCache();
//		Application.getLoaderBase().load(this, "StatusInternExternID=" + statusInternExternID, getSortFieldName());
//	}
	

//	/**
//	 * CO für die Person mit StatusAktivInaktiv != Aktiv laden
//	 * 
//	 * @param statusInternExternID
//	 * @throws Exception
//	 */
//	private void loadByStatusNichtAktiv() throws Exception {
//		emptyCache();
//		Application.getLoaderBase().load(this, " StatusAktivInaktivID <> " + CoStatusAktivInaktiv.STATUSID_AKTIV, getSortFieldName());
//	}
	
	
	private static String getWhereNotArchivUser() {
		return " AND ID<>" + Archivierer.ID_ARCHIV + " ";
	}
	

	/**
	 * CO für die Person mit StatusAktivInaktivID=Aktiv laden
	 * 
	 * @throws Exception
	 */
	public void loadByAktivIntern() throws Exception {
		emptyCache();
		Application.getLoaderBase().load(this, "StatusAktivInaktivID=" + CoStatusAktivInaktiv.STATUSID_AKTIV 
				+ " AND StatusInternExternID=" + CoStatusInternExtern.STATUSID_INTERN + getWhereNotArchivUser(), getSortFieldName());
	}
	

	/**
	 * CO für die Person, die der Abteilung zugeordnet sind laden
	 * 
	 * @param abteilungID
	 * @throws Exception
	 */
	public void loadByAbteilungID(int abteilungID) throws Exception {
		emptyCache();
		Application.getLoaderBase().load(this, "AbteilungID=" + abteilungID, getSortFieldName());
	}
	
	
	/**
	 * CO mit den Personen laden, für die der aktuelle User Berechtigungen besitzt
	 * 
	 * @throws Exception
	 */
	public void loadByCurrentUser() throws Exception {
		String abteilungIDs;
	
		loadByID(UserInformation.getPersonID());
		abteilungIDs = getCoPersonAbteilungsrechte(true).getSelectedIDs();

		// Personen laden
		emptyCache();
		Application.getLoaderBase().load(this, getWhereByAbteilungIDs(abteilungIDs,CoStatusAktivInaktiv.STATUSID_AKTIV), getSortFieldName());
	}


	/**
	 * CO für die Person, die der Personenliste zugeordnet sind laden
	 * 
	 * @param personenlisteID
	 * @throws Exception
	 */
	public void loadByPersonenlisteID(int personenlisteID) throws Exception {
		emptyCache();
		Application.getLoaderBase().load(this, "ID IN (SELECT PersonID FROM stblPersonenPersonenliste WHERE PersonenlisteID=" + personenlisteID + ")", getSortFieldName());
	}
	
	
	public void load(String where) throws Exception {
		emptyCache();
		Application.getLoaderBase().load(this, where, getSortFieldName());
	}


	/**
	 * WHERE-Teil der SQL-Abfrage zum Laden von Personen in Abhängigkeit der Benutzerrechte
	 * 
	 * @param abteilungIDs
	 * @param statusAktivInaktivID
	 * @return
	 * @throws Exception
	 */
	private static String getWhereByAbteilungIDs(String abteilungIDs, int statusAktivInaktivID) throws Exception {
		return " StatusAktivInaktivID = " + statusAktivInaktivID
				+ " AND (AbteilungID IN (" + abteilungIDs + ")" 
//				+ (UserInformation.getInstance().isPersonalansicht() || UserInformation.getInstance().isAL() 
//						?  " OR (GruppeID IS NULL AND AbteilungID IN (" + coAbteilung.getIDs() + "))" : "")
				+ " OR ID = " + UserInformation.getPersonID()
				+ ")"
				;
	}


	/**
	 * CO mit den Items für die aktiven Personen
	 * 
	 * @throws Exception
	 */
	public void loadItemsAktiv() throws Exception {
		loadItems(" StatusAktivInaktivID=" + CoStatusAktivInaktiv.STATUSID_AKTIV
				+ getWhereNotArchivUser());
	}


	/**
	 * CO mit den Items für die aktiven, internen Personen
	 * 
	 * @throws Exception
	 */
	public void loadItemsAktivIntern() throws Exception {
		loadItems(" StatusAktivInaktivID=" + CoStatusAktivInaktiv.STATUSID_AKTIV + " AND StatusInternExternID=" + CoStatusInternExtern.STATUSID_INTERN
				+ getWhereNotArchivUser());
	}


	/**
	 * CO mit den Items für die Personen laden, für die der aktuelle User Berechtigungen besitzt
	 * 
	 * @throws Exception
	 */
	public void loadItemsOfCurrentUser() throws Exception {
		String abteilungIDs;
	
		loadByID(UserInformation.getPersonID());
		abteilungIDs = getCoPersonAbteilungsrechte(true).getSelectedIDs();

		// Personen laden
		loadItemsByAbteilungIDs(abteilungIDs, CoStatusAktivInaktiv.STATUSID_AKTIV);
	}


	/**
	 * CO mit den Items für die Personen, die in den übergebenen Gruppen sind laden
	 * 
	 * @param abteilungIDs AbteilungIDs mit Komma getrennt
	 * @throws Exception
	 */
	private void loadItemsByAbteilungIDs(String abteilungIDs, int statusAktivInaktivID) throws Exception {
		loadItems(getWhereByAbteilungIDs(abteilungIDs, statusAktivInaktivID) + getWhereNotArchivUser());
	}


	/**
	 * alle Projektleiter laden
	 * 
	 * @throws Exception 
	 */
	public void loadItemsProjektleiter() throws Exception{
		String where;

		where = " StatusAktivInaktivID = " + CoStatusAktivInaktiv.STATUSID_AKTIV 
				+ " AND (ID IN (SELECT ProjektleiterID FROM tblAuftrag) OR ID IN (SELECT ProjektleiterID FROM tblAbruf)"
				+ " OR ID IN (SELECT ProjektleiterID2 FROM tblAbruf) OR ID IN (SELECT ProjektleiterID2 FROM tblAbruf)) ";

		loadItems(where);
	}


	/**
	 * CO für die AL der Abteilung laden
	 * 
	 * @param abteilungID
	 * @throws Exception
	 */
	public void loadItemsAbteilungsleiter(int abteilungID) throws Exception {
		String where;

		where = "("
				+ (abteilungID > 0 ? "AbteilungID=" + abteilungID + " AND " : "")
				+ "StatusAktivInaktivID=" + CoStatusAktivInaktiv.STATUSID_AKTIV
				+ " AND (PositionID=" + CoPosition.ID_AL + " OR PositionID=" + CoPosition.ID_KL 
				+ " OR PositionID=" + CoPosition.ID_GFT + " OR PositionID=" + CoPosition.ID_GFV + "))"
				+ (abteilungID == CoAbteilung.ID_VERWALTUNG ? " OR PositionID=" + CoPosition.ID_KL : "")
				;

		loadItems(where);
	}
	

	/**
	 * CO für die AL des Auftrags laden
	 * 
	 * @param auftragID
	 * @throws Exception
	 */
	public void loadItemsAbteilungsleiterByAuftrag(int auftragID) throws Exception {
		CoAuftrag coAuftrag;
		
		coAuftrag = new CoAuftrag();
		coAuftrag.loadByID(auftragID);
		
		loadItemsAbteilungsleiter(coAuftrag.getAbteilungID());
	}
	

	/**
	 * Items laden
	 * 
	 * @param where WHERE-Teil der SQL-Abfrage zum Laden von Items
	 * @throws Exception 
	 */
	public void loadItems(String where) throws Exception{
		String sql;

		sql = "SELECT ID, (Nachname + CASE WHEN Vorname IS NULL THEN '' ELSE ', ' + Vorname END) AS Nachname FROM " + getTableName() + " WHERE "
//				+ "ID=" + Archivierer.ID_ARCHIV + " OR "
//				+ " ("
				+ "ID>0"
				+ " AND StatusAktivInaktivID IS NOT NULL"
				+ (where == null ? "" : (" AND  " + where) )
//				+ ")"
				+ " ORDER BY " + getSortFieldName();

		emptyCache();
		Application.getLoaderBase().load(this, sql);
	}


	/**
	 * CO für die Personen, die zu dem übergebenen Datum Kontodaten haben
	 * 
	 * @param gregDatumVortag 
	 * @throws Exception
	 */
	public void loadAllWithKontowerte(GregorianCalendar gregDatum) throws Exception {
		loadAllWithOrWithoutKontowerte(gregDatum, true);
	}


	/**
	 * CO für die Personen, die zu dem übergebenen Datum keine Kontodaten haben
	 * 
	 * @param gregDatumVortag 
	 * @throws Exception
	 */
	public void loadAllWithoutKontowerte(GregorianCalendar gregDatum) throws Exception {
		loadAllWithOrWithoutKontowerte(gregDatum, false);
	}


	/**
	 * CO für die Personen, die zu dem übergebenen Datum (keine) Kontodaten haben
	 * 
	 * @param gregDatumVortag 
	 * @param withKontowerte Personen mit oder ohne Kontodaten laden
	 * @throws Exception
	 */
	private void loadAllWithOrWithoutKontowerte(GregorianCalendar gregDatum, boolean withKontowerte) throws Exception {
		String sql, where;
		
		sql = getSqlSelectWithZeitmodell(CoStatusAktivInaktiv.STATUSID_AKTIV, gregDatum);

		where = "WHERE p.ID " + (withKontowerte ? "" : "NOT") + " IN (SELECT PersonID FROM tblKontowert WHERE YEAR(datum)=" + gregDatum.get(Calendar.YEAR)  
		+ " AND MONTH(datum)=" + (gregDatum.get(Calendar.MONTH) + 1) + " AND DAY(datum)=" + gregDatum.get(Calendar.DAY_OF_MONTH) + ") AND ";
		
		sql = sql.replace("WHERE", where);
		
		Application.getLoaderBase().load(this, sql);
	}


	/**
	 * SQL-Statement für SELECT-Befehl aller Personen mit gültigem Zeitmodell
	 * 
	 * @param statusAktivInaktiv
	 * @param gregDatum
	 * @return
	 * @throws Exception
	 */
	private String getSqlSelectWithZeitmodell(int statusAktivInaktivID, GregorianCalendar gregDatum) throws Exception {
		String sql, datum;
		
		addField("virt.field.person.name");

		datum = Format.getStringMitUhrzeit(gregDatum);

		sql = "SELECT *, (Nachname + ', ' + Vorname) AS Name FROM " + getTableName() + " p "
				+ (statusAktivInaktivID != CoStatusAktivInaktiv.STATUSID_ALLE ? // wenn alle Personen geladen werden sollen, keine Einschränkung 
						(statusAktivInaktivID == CoStatusAktivInaktiv.STATUSID_AKTIV ? // für aktive Personen auch das Zeitmodell laden
								" LEFT OUTER JOIN stblPersonZeitmodell z ON (p.ID = z.personID) "
								+ " WHERE ( z.ID IS NULL "
								+ " OR (GueltigVon <= '" + datum + "' AND '" + datum + "' <= GueltigBis) OR (GueltigVon <= '" + datum + "' AND GueltigBis IS NULL) ) AND "
								: " WHERE ")
						+ " statusAktivInaktivID = " + statusAktivInaktivID // Status (bei nicht alle Personen laden)
						: "") // wenn alle Personen geladen werden sollen, keine Einschränkung
				+ " ORDER BY " + getSortFieldName();
		
		return sql;
	}
	

	/**
	 * Alle Personen für die Darstellung der Anwesenheit laden
	 * 
	 * @param abteilungID
	 * @throws Exception 
	 */
	public void loadForAnwesenheit(int abteilungID) throws Exception {
		String where, sql, innerSql;
		
		addFields("table." + CoBuchung.TABLE_NAME);
		
		
		// die letzten beiden Buchungen laden, da bei Ende Dienstreise oder Dienstgang die vorhergehende Buchung geprüft werden muss
		innerSql = "SELECT TOP 2 * FROM tblBuchung "
				+ " WHERE PersonID=p.ID "
				+ " AND YEAR(Datum)=YEAR(getdate()) AND DATEPART(dy, Datum) = DATEPART(dy, getdate())"
				+ " AND (UhrzeitAsInt IS NULL OR UhrzeitAsInt <= (DATEPART(hh, getdate()) * 60 + DATEPART(mi, getdate())))"

				// Vorab-Kommen wird nicht berücksichtigt, z. B. nach Dienstreise/-gang; Vorab-OFA auch nicht, weil dann selbst mit OK gebucht wird
				+ " AND ((BuchungsartID <> " + CoBuchungsart.ID_KOMMEN + " AND BuchungsartID <> " + CoBuchungsart.ID_ORTSFLEX_ARBEITEN + ")"
				+ " OR StatusID <> " + CoStatusBuchung.STATUSID_VORLAEUFIG + ")"

				// keine ungültigen Buchungen
				+ " AND StatusID <> " + CoStatusBuchung.STATUSID_UNGUELTIG
				+ " ORDER BY UhrzeitAsInt DESC, Datum DESC";

		sql = "SELECT p.ID, p.Nachname, p.Vorname, p.TelefonNr, b.BuchungsartID, b.StatusID FROM " + getTableName() + " p"
				+ " OUTER APPLY (" + innerSql + ") as b";
		
		where = "StatusAktivInaktivID = " + CoStatusAktivInaktiv.STATUSID_AKTIV 
				+ " AND StatusInternExternID <> " + CoStatusInternExtern.STATUSID_EXTERN
				+ (abteilungID == 0 ? "" : " AND AbteilungID=" + abteilungID);
		
		sql += " WHERE " + where;
		sql += " ORDER BY " + getSortFieldName();
//		System.out.println("loadForAnwesenheit: " + sql);
//		long a = System.currentTimeMillis();
		Application.getLoaderBase().load(this, sql);
//		System.out.println("loadForAnwesenheit: " + (System.currentTimeMillis() - a));

		// Sonderfälle bei der Anzeige prüfen
		// bei Dienstreisen bzw. Dienstgang muss geprüft werden, ob vorher weitere Buchungen durchgeführt wurden
//		a = System.currentTimeMillis();
		checkSonderfallAnwesenheit();
//		System.out.println("checkSonderfallAnwesenheit: " + (System.currentTimeMillis() - a));
	}


	/**
	 * Letzte Kommen oder OFA-Buchung für die Darstellung der Anwesenheit laden
	 * 
	 * @param abteilungID
	 * @throws Exception 
	 */
	public void loadOrtsflexArbeitenForAnwesenheit() throws Exception {
		String where, sql, innerSql;
		
		addFields("table." + CoBuchung.TABLE_NAME);
		
		
		// die letzte Buchungen laden, die OFA oder Kommen ist
		innerSql = "SELECT TOP 1 * FROM tblBuchung "
				+ " WHERE PersonID=p.ID "
				+ " AND YEAR(Datum)=YEAR(getdate()) AND DATEPART(dy, Datum) = DATEPART(dy, getdate())"
				+ " AND (UhrzeitAsInt IS NULL OR UhrzeitAsInt <= (DATEPART(hh, getdate()) * 60 + DATEPART(mi, getdate())))"

				// die letzte Buchungen laden, die OFA oder Kommen ist
				+ " AND (BuchungsartID = " + CoBuchungsart.ID_KOMMEN + " OR BuchungsartID = " + CoBuchungsart.ID_ORTSFLEX_ARBEITEN + ")"
				+ " AND (StatusID = " + CoStatusBuchung.STATUSID_OK + " OR StatusID =" + CoStatusBuchung.STATUSID_GEAENDERT  
//				+ " OR StatusID =" + CoStatusBuchung.STATUSID_VORLAEUFIG // auch vorläufige für das Rendern der Zellen, Anzeige wenn jemand zu spät anfängt
				+ ")" 

				+ " ORDER BY UhrzeitAsInt DESC, Datum DESC";

		sql = "SELECT p.ID, p.Nachname, p.Vorname, p.TelefonNr, b.BuchungsartID FROM " + getTableName() + " p"
				+ " OUTER APPLY (" + innerSql + ") as b";
		
		where = "StatusAktivInaktivID = " + CoStatusAktivInaktiv.STATUSID_AKTIV 
				+ " AND StatusInternExternID <> " + CoStatusInternExtern.STATUSID_EXTERN;
		
		sql += " WHERE " + where;
		sql += " ORDER BY " + getSortFieldName();
		
		System.out.println("loadOrtsflexArbeitenForAnwesenheit: " + sql);
		long a = System.currentTimeMillis();
		emptyCache();
		Application.getLoaderBase().load(this, sql);
		System.out.println(System.currentTimeMillis() - a);
	}


	/**
	 * Alle Personen laden, die in dem Monat des Referenzdatums an dem Projekt gearbeitet haben
	 * 
	 * @param coProjekt
	 * @param datum
	 * @throws Exception 
	 */
	public void loadByProjekt(CoProjekt coProjekt, Date datum) throws Exception {
		String sql;
		GregorianCalendar gregDatum;

		gregDatum = Format.getGregorianCalendar(datum);

		sql = "SELECT * FROM " + getTableName() + " p WHERE ID IN "
				+ "(SELECT PersonID FROM tblMonatseinsatzblatt WHERE YEAR(Datum)=" + gregDatum.get(Calendar.YEAR)  
				+ " AND MONTH(Datum)=" + (gregDatum.get(Calendar.MONTH) + 1) + " AND WertZeit > 0 "
				+  "AND " + (coProjekt.isAuftrag() ? "AuftragID" : "AbrufID") + " = " + coProjekt.getID() + ")"
				+ " ORDER BY " + getSortFieldName();

		Application.getLoaderBase().load(this, sql);
	}


	/**
	 * 	Sonderfälle bei der Anzeige der Anwesenheit prüfen.<br>
	 *  Wenn die neueste Buchung ein EndeDG/DR ist prüfe auch die Buchung davor. <br>
	 *  Wenn diese Buchung nicht DG/DR ist (sondern z. B. Kommen weil die Person doch/früher gekommen ist), zeige diese Buchung an, sonst EndeDG/DR
	 *  
	 * @throws Exception 
	 */
	private void checkSonderfallAnwesenheit() throws Exception {
		int personID, lastPersonID, buchungsartID, statusBuchungID, currentIndex;
		CoPerson coPersonOfaWti;

		if (!moveFirst())
		{
			return;
		}
		
		coPersonOfaWti = new CoPerson();
		coPersonOfaWti.loadOrtsflexArbeitenForAnwesenheit();

		
		if (!isEditing())
		{
			begin();
		}

		lastPersonID = 0;
		
		do
		{
			personID = getID();
			statusBuchungID = getStatusBuchungID();

			// wenn keine Person angegeben ist (keine Buchung für die Person vorhanden), gehe zur nächsten Buchung
			if (personID == 0)
			{
				continue;
			}
			
			// wenn noch eine weitere (ältere, 2. Schleifendurchlauf) Buchung für die Person geladen wurde, entferne diese
			// wenn sie relevant ist wurde dies im 1. SChleifendurchlauf schon geprüft
			if (personID == lastPersonID)
			{
				// Index merken, sonst springt der currentIndex beim Löschen des letzten Eintrags zurück und löscht weitere Einträge
				currentIndex = getCurrentRowIndex();
				delete();
				
				if (currentIndex < getRowCount())
				{
					movePrev();
				}
				
				continue;
			}
			
			
			// Sonderfall Ende Dienstreisen wenn die letzte Buchung davor z. B. ein Kommen ist
			if (getBuchungsartID() == CoBuchungsart.ID_ENDE_DIENSTGANG_DIENSTREISE)
			{
				// prüfe die vorhergehende Buchung
				if (moveNext())
				{
					// wenn es keine vorhergehende Buchung für die Person gibt, gehe wieder einen Schritt zurück,
					// um die Buchung in der while-Schleife zu berücksichtigen
					if (personID != getID())
					{
						movePrev();
						continue;
					}
					
					// wenn dies eine DG/DR-Buchung ist, entferne sie aus der Liste
					buchungsartID = getBuchungsartID();
					if (buchungsartID == CoBuchungsart.ID_DIENSTREISE || buchungsartID == CoBuchungsart.ID_DIENSTGANG 
							|| buchungsartID == CoBuchungsart.ID_BERUFSSCHULE || buchungsartID == CoBuchungsart.ID_VORLESUNG)
					{
						// Index merken, sonst springt der currentIndex beim Löschen des letzten Eintrags zurück und löscht weitere Einträge
						currentIndex = getCurrentRowIndex();
						delete();
						
						if (currentIndex < getRowCount())
						{
							movePrev(); // zurück, weil man sonst ggf. zur nächsten Person springt
						}
					}
					else // sonst entferne die Ende DG/DR-Buchung, dann wird . z. B. das Kommen angezeigt
					{
						movePrev();
						delete(); // dadurch springt man wieder zum 2. Datensatz der gleichen Person -> kein movePrev() notwendig
					}
				}
			}
			
			// TODO Wunsch Ti, RS Kleemann noch machen, da dann bei vergessenen DR-Buchungen vorl. DR nicht angezeigt wird (wie vorl. Kommen)
			
			// Sonderfall vorläufige Dienstreisen wenn die letzte Buchung davor z. B. ein Kommen ist, also die DR noch nicht angetreten wurde
			if ((getBuchungsartID() == CoBuchungsart.ID_DIENSTGANG || getBuchungsartID() == CoBuchungsart.ID_DIENSTREISE 
					|| getBuchungsartID() == CoBuchungsart.ID_VORLESUNG) && statusBuchungID == CoStatusBuchung.STATUSID_VORLAEUFIG)
			{
				// prüfe die vorhergehende Buchung
				if (moveNext())
				{
					// wenn es keine vorhergehende Buchung für die Person gibt, gehe wieder einen Schritt zurück,
					// um die Buchung in der while-Schleife zu berücksichtigen
					if (personID != getID())
					{
						movePrev();
						continue;
					}
					
					// wenn dies keine Kommen- oder OFA-Buchung ist, entferne sie aus der Liste
					buchungsartID = getBuchungsartID();
					if (buchungsartID != CoBuchungsart.ID_KOMMEN && buchungsartID != CoBuchungsart.ID_ORTSFLEX_ARBEITEN)
					{
						// Index merken, sonst springt der currentIndex beim Löschen des letzten Eintrags zurück und löscht weitere Einträge
						currentIndex = getCurrentRowIndex();
						delete();
						
						if (currentIndex < getRowCount())
						{
							movePrev(); // zurück, weil man sonst ggf. zur nächsten Person springt
						}
					}
					else // sonst entferne die DG/DR-Buchung, dann wird . z. B. das Kommen angezeigt
					{
						movePrev();
						delete(); // dadurch springt man wieder zum 2. Datensatz der gleichen Person -> kein movePrev() notwendig
					}
				}
			}
			
			
			// Sonderfall FA ab x Uhr und Gehen kurznach x Uhr -> FA anzeigen
			if (getBuchungsartID() == CoBuchungsart.ID_GEHEN)
			{
				// prüfe die vorhergehende Buchung
				if (moveNext())
				{
					// wenn es keine vorhergehende Buchung für die Person gibt, gehe wieder einen Schritt zurück,
					// um die Buchung in der while-Schleife zu berücksichtigen
					if (personID != getID())
					{
						movePrev();
						continue;
					}
					
					// wenn dies FA ist, entferne die Gehen-Buchungen
					buchungsartID = getBuchungsartID();
					if (buchungsartID == CoBuchungsart.ID_FA)
					{
						movePrev();
						delete(); // dadaurch springt man wieder zum 2. Datensatz der gleichen Person -> kein movePrev() notwendig
					}
					// wenn dies vorl. Gehen im OFA ist, entferne die Gehen-Buchungen
					else if (statusBuchungID == CoStatusBuchung.STATUSID_VORLAEUFIG 
							&& coPersonOfaWti.moveToID(personID) && coPersonOfaWti.getBuchungsartID() == CoBuchungsart.ID_ORTSFLEX_ARBEITEN)
					{
						movePrev();
						delete(); // dadaurch springt man wieder zum 2. Datensatz der gleichen Person -> kein movePrev() notwendig
					}
					else // sonst die aktuelle/zeitlich vorherige Buchung löschen
					{
						// Index merken, sonst springt der currentIndex beim Löschen des letzten Eintrags zurück und löscht weitere Einträge
						currentIndex = getCurrentRowIndex();
						delete();
						
						if (currentIndex < getRowCount())
						{
							movePrev(); // zurück, weil man sonst ggf. zur nächsten Person springt
						}

					}
				}
			}
			

			lastPersonID = personID;
		} while (moveNext());
	}


	public IField getFieldBuchungsartID(){
		return getField("field.tblbuchung.buchungsartid");
	}
	

	public int getBuchungsartID(){
		return Format.getIntValue(getFieldBuchungsartID().getValue());
	}
	
	
	@Override
	public String getNavigationBitmap() {
		if (getStatusInternExternID() == CoStatusInternExtern.STATUSID_INTERN)
		{
			return "user";
		}
		else
		{
			return "user.red";
		}
	}


	/**
	 * Nachname, Vorname
	 * 
	 * @see pze.business.objects.AbstractCacheObject#getBezeichnung()
	 */
	@Override
	public String getBezeichnung() {
		return getNachname() + (hasVorname() ? ", " + getVorname() : "");
	}


	/**
	 * Nach Nachname, Vorname sortieren
	 * 
	 * (non-Javadoc)
	 * @see pze.business.objects.AbstractCacheObject#getSortFieldName()
	 */
	@Override
	protected String getSortFieldName() {
		return "Nachname, Vorname";
	}
	

	/**
	 * Name, zusammengesetzt aus "Vorname Nachname"
	 * @return
	 */
	public String getName() {
		return (hasVorname() ? getVorname() + " " : "") + getNachname();
	}


	/**
	 * Name, zusammengesetzt aus "Nachname, Vorname"
	 * @return
	 */
	public String getNachnameVorname() {
		return getNachname() + (hasVorname() ? ", " + getVorname() : "");
	}


	public String getVorname() {
		return Format.getStringValue(getField("field." + getTableName() + ".vorname").getValue());
	}


	public boolean hasVorname() {
		return getField("field." + getTableName() + ".vorname").getValue() != null;
	}


	public String getNachname() {
		return Format.getStringValue(getField("field." + getTableName() + ".nachname").getValue());
	}


	public String getKuerzel() {
		return Format.getStringValue(getField("field." + getTableName() + ".kuerzel").getValue());
	}


	public int getPersonalnummer() {
		return Format.getIntValue(getField("field." + getTableName() + ".personalnr"));
	}


	public int getJahresurlaub() {
		return Format.getIntValue(getField("field." + getTableName() + ".jahresurlaub"));
	}


	public String getTelefonNr() {
		return Format.getStringValue(getField("field." + getTableName() + ".telefonnr").getValue());
	}


	public String getAnzeigeAnwesenheit() {
//		return getBezeichnung();
		String telefonNr;

		telefonNr = getTelefonNr();
		if (telefonNr != null)
		{
			telefonNr = " (" + telefonNr + ")";
		}
		
		return getBezeichnung() + (telefonNr != null ? telefonNr : "");
	}


	public IField getFieldAbteilungID() {
		return getField("field." + getTableName() + ".abteilungid");
	}


	public int getAbteilungID() {
		return Format.getIntValue(getFieldAbteilungID());
	}


	public String getAbteilung() {
		return getFieldAbteilungID().getDisplayValue();
	}


	public int getUserID() {
		return Format.getIntValue(getField("field." + getTableName() + ".userid").getValue());
	}


	public void setUserID(int userID) {
		getField("field." + getTableName() + ".userid").setValue(userID);
	}


	public Date getBeginnPze() {
		return Format.getDateValue(getField("field." + getTableName() + ".beginnpze").getValue());
	}


	public Date getEndePze() {
		return Format.getDateValue(getField("field." + getTableName() + ".endepze").getValue());
	}


	public int getStandortID() {
		return Format.getIntValue(getField("field." + getTableName() + ".standortid").getValue());
	}


	public int getBundeslandID() {
		return Format.getIntValue(getField("field." + getTableName() + ".bundeslandid").getValue());
	}


	public int getPositionID() {
		return Format.getIntValue(getField("field." + getTableName() + ".positionid").getValue());
	}


	public int getKmWohnortWti() {
		return Format.getIntValue(getField("field." + getTableName() + ".kmwohnortwti").getValue());
	}


	public int getZeitWohnortWti() {
		return Format.getIntValue(getField("field." + getTableName() + ".zeitwohnortwti").getValue());
	}




	/**
	 * Gehe zu dem Eintrag mit der ChipkartenNr
	 * 
	 * @param bezeichnung
	 */
	public int getIdByChipkartenNr(String chipkartenNr) {
		
		if (!moveTo(chipkartenNr, "field." + getTableName() + ".chipkartennr"))
		{
			return 0;
		}
		
		return getID();
	}


	/**
	 * Gehe zu dem Eintrag mit der UserID
	 * 
	 * @param bezeichnung
	 */
	public int getIdByUserID(int userID) {
		
		if (!moveTo(userID, "field." + getTableName() + ".userid"))
		{
			return 0;
		}
		
		return getID();
	}


	private int getStatusInternExternID() {
		return Format.getIntValue(getField("field." + getTableName() + ".statusinternexternid").getValue());
	}


	private int getStatusAktivInaktivID() {
		return Format.getIntValue(getField("field." + getTableName() + ".statusaktivinaktivid").getValue());
	}


	public IField getFieldStatusBuchungID(){
		return getField("field.tblbuchung.statusid");
	}
	
	/**
	 * Status der Buchung, falls das Feld in Sonderfällen mit geladen wurde
	 * 
	 * @return
	 */
	private int getStatusBuchungID() {
		IField field;
		
		field = getFieldStatusBuchungID();
		
		return field == null ? 0 : Format.getIntValue(field);
	}


	public boolean isAktiv() throws Exception {
		return getStatusAktivInaktivID() == CoStatusAktivInaktiv.STATUSID_AKTIV;
	}


	public boolean isInaktiv() throws Exception {
		return getStatusAktivInaktivID() == CoStatusAktivInaktiv.STATUSID_INAKTIV;
	}


	public boolean isAusgeschieden() throws Exception {
		return getStatusAktivInaktivID() == CoStatusAktivInaktiv.STATUSID_AUSGESCHIEDEN;
	}


	public CoPersonZeitmodell getCoPersonZeitmodell() throws Exception {
		CoPersonZeitmodell coPersonZeitmodell;
		
		coPersonZeitmodell = new CoPersonZeitmodell();
		coPersonZeitmodell.loadByPersonID(getID());
		
		return coPersonZeitmodell;
	}


	/**
	 * Zeitmodell für das übergebene Datum
	 * 
	 * @param datum
	 * @return
	 * @throws Exception
	 */
	public CoZeitmodell getCoZeitmodell(Date datum) throws Exception {
		CoPersonZeitmodell coPersonZeitmodell;
		
		coPersonZeitmodell = getCoPersonZeitmodell();
		
		return coPersonZeitmodell.getCoZeitmodell(datum);
	}


	/**
	 * CO mit der Angabe, für welche Gruppen die Person Rechte hat
	 * 
	 * @return
	 * @throws Exception
	 */
	public CoPersonAbteilungsrechte getCoPersonAbteilungsrechte(boolean mitVertreterrechten) throws Exception {
		CoPersonAbteilungsrechte coPersonAbteilungsrechte;
		
		coPersonAbteilungsrechte = new CoPersonAbteilungsrechte();
		coPersonAbteilungsrechte.loadByPersonID(getID(), mitVertreterrechten);
		
		return coPersonAbteilungsrechte;
	}


//	/**
//	 * CO mit der Urlaubsplanung der Person
//	 * 
//	 * @return
//	 * @throws Exception
//	 */
//	public CoBuchung getCoUrlaubsplanung() throws Exception {
//		CoBuchung coUrlaubsplanung;
//		
//		coUrlaubsplanung = new CoBuchung();
//		coUrlaubsplanung.loadUrlaubsplanung(getID());
//		
//		return coUrlaubsplanung;
//	}


	/**
	 * CO mit den Dienstreisen der Person
	 * 
	 * @return
	 * @throws Exception
	 */
	public CoDienstreise getCoDienstreise() throws Exception {
		CoDienstreise coDienstreise;
		
		coDienstreise = new CoDienstreise();
		coDienstreise.loadByPersonID(getID());
		
		return coDienstreise;
	}


	/**
	 * CO mit den Dienstreise-Abrechnungen der Person
	 * 
	 * @return
	 * @throws Exception
	 */
	public CoDienstreiseAbrechnung getCoDienstreiseAbrechnung() throws Exception {
		CoDienstreiseAbrechnung coDienstreiseAbrechnung;
		
		coDienstreiseAbrechnung = new CoDienstreiseAbrechnung();
		coDienstreiseAbrechnung.loadByPersonID(getID());
		
		return coDienstreiseAbrechnung;
	}


	/**
	 * Prüft anhand der Sollarbeitszeit im Zeitmodell, Feiertagen und Brückentagen, ob es für die Person ein Arbeitstag ist
	 * 
	 * @return
	 * @throws Exception 
	 */
	public boolean isArbeitstag(GregorianCalendar gregDatum) throws Exception {
		return isArbeitstag(Format.getDateValue(gregDatum));
	}


	/**
	 * Prüft anhand der Sollarbeitszeit im Zeitmodell, Feiertagen und Brückentagen, ob es für die Person ein Arbeitstag ist
	 * 
	 * @return
	 * @throws Exception 
	 */
	public boolean isArbeitstag(Date datum) throws Exception {
		int bundeslandID;
		
		bundeslandID = getBundeslandID();
		
		return getSollArbeitszeitZeitmodell(datum) > 0 
				&& !FeiertagGenerator.getInstance().isFeiertag(datum, bundeslandID) 
				&& !CoBrueckentag.getInstance().isBrueckentag(datum, bundeslandID);
	}


	/**
	 * Sollarbeitszeit im Zeitmodell
	 * 
	 * @param datum
	 * @return
	 * @throws Exception 
	 */
	public int getSollArbeitszeitZeitmodell(Date datum) throws Exception {
		int sollArbeitszeit;
		CoZeitmodell coZeitmodell;
		
		sollArbeitszeit = 0;

		// SollArbeitszeit aus dem Zeitmodell holen
		coZeitmodell = getCoZeitmodell(datum);
		if (coZeitmodell != null)
		{
			sollArbeitszeit = coZeitmodell.getTagessoll(datum);
		}
		
		return sollArbeitszeit;
	}


}
