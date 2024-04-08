package pze.business.objects.auswertung;

import java.util.Date;

import framework.Application;
import framework.business.interfaces.fields.IField;
import pze.business.Format;
import pze.business.UserInformation;
import pze.business.objects.AbstractCacheObject;
import pze.business.objects.personen.CoPerson;

/**
 * Abstraktes CacheObject für die allgemeinen Einstellungen der Auswertung
 * 
 * @author Lisiecki
 *
 */
public abstract class CoAuswertung extends AbstractCacheObject {


	/**
	 * Kontruktor
	 */
	public CoAuswertung(String tableName) {
		super("table." + tableName);
	}
	
	
	/**
	 * Über PersonID laden, wenn der Datensatz nicht existiert, lege ihn an
	 * 
	 * @param userID
	 * @throws Exception 
	 */
	public void loadByUserID(int userID) throws Exception {
		emptyCache();
		Application.getLoaderBase().load(this, "UserID=" + userID,"");
		
		if (getRowCount() == 0)
		{
			createNew(userID);
		}
	}
	
	
	/**
	 * Neuen Datensatz für die aktuell übergebene Person anlegen
	 * 
	 * @see pze.business.objects.AbstractCacheObject#createNew()
	 */
	// ggf. müsste immer noch ein save/begin aufgerufen werden (bei CoAuswertungProjekt gab es sonst ggf. einen Fehler, 15.01.2019)
	public int createNew(int userID) throws Exception	{
		int id;
		
		id = super.createNew();
		
		setUserID(userID);
		
		return id;
	}
	

	private IField getFieldUserID() {
		return getField("field." + getTableName() + ".userid");
	}


	public void setUserID(int userID) {
		getFieldUserID().setValue(userID);
	}


	public IField getFieldDatumVon() {
		return getField("field." + getTableName() + ".datumvon");
	}


	public Date getDatumVon() {
		return getDatum(getFieldDatumVon());
	}


	public Date getDatumVonOriginalValue() {
		return getDatumOriginalValue(getFieldDatumVon());
	}


	public void setDatumVon(Date datum) {
		getFieldDatumVon().setValue(datum);
	}


	public IField getFieldDatumBis() {
		return getField("field." + getTableName() + ".datumbis");
	}


	public Date getDatumBis() {
		return getDatum(getFieldDatumBis());
	}


	public Date getDatumBisOriginalValue() {
		return getDatumOriginalValue(getFieldDatumBis());
	}


	public Date getDatum(IField field) {
		if (field == null)
		{
			return null;
		}
		
		return Format.getDate12Uhr(Format.getDateValue(field.getValue()));
	}


	public Date getDatumOriginalValue(IField field) {
		if (field == null)
		{
			return null;
		}
		
		return Format.getDate12Uhr(Format.getDateValue(field.getOriginalValue()));
	}


	/**
	 * DatumBis als String zur Übergabe an eine SQL-Funktion<br> 
	 * wenn nur DatumVon angegeben ist nehme dieses, weil nur ein Stichtag berücksichtigt wird<br>
	 * sonst datum unbegrenzt -> aktuelles Datum +1 Jahr
	 * 
	 * @return
	 */
	public String getStringDatumBisForSql() {
		Date datumVon, datumBis;
		
		
		// Datum
		
		// wenn kein DatumBis angegeben
		datumBis = getDatumBis();
		if (datumBis == null)
		{
			// wenn DatumVon angegeben ist nehme dieses, weil nur ein Stichtag berücksichtigt wird
			datumVon = getDatumVon();
			if (datumVon != null)
			{
				datumBis = datumVon;
			}
			else // sonst Datum heute um 1 Jahr verschoben, weiter im Voraus wird wohl nicht eingegeben
			{
				datumBis = new Date();
				datumBis = Format.getDateVerschoben(datumBis, 365);
			}
		}

		// DatumBis einen Tag weiter setzen für einfache Abfrage im SQL-Statement
		datumBis = Format.getDateVerschoben(datumBis, 1);
		

		// SQL-Abfrage zusammensetzen
		return Format.getStringForDB(datumBis);
	}


	public void setDatumBis(Date datum) {
		getFieldDatumBis().setValue(datum);
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


	public String getPerson() {
		return getFieldPersonID().getDisplayValue();
	}


	public IField getFieldPersonenlisteID() {
		return getField("field." + getTableName() + ".personenlisteid");
	}


	public int getPersonenlisteID() {
		IField field;
		
		field = getFieldPersonenlisteID();
		if (field == null)
		{
			return 0;
		}

		return Format.getIntValue(getFieldPersonenlisteID());
	}


	public String getPersonenliste() {
		return getFieldPersonenlisteID().getDisplayValue();
	}


	public IField getFieldPositionID() {
		return getField("field." + getTableName() + ".positionid");
	}


	public int getPositionID() {
		return Format.getIntValue(getFieldPositionID());
	}


	public String getPosition() {
		return getFieldPositionID().getDisplayValue();
	}


	public IField getFieldStatusAktivInaktivID() {
		return getField("field." + getTableName() + ".statusaktivinaktivid");
	}


	public int getStatusAktivInaktivID() {
		return Format.getIntValue(getFieldStatusAktivInaktivID());
	}


	public String getStatusAktivInaktiv() {
		return getFieldStatusAktivInaktivID().getDisplayValue();
	}

	public void setStatusAktivInaktiv(int statusAktivInaktivID) {
		getFieldStatusAktivInaktivID().setValue(statusAktivInaktivID);
	}

	/**
	 * Where-Teil des SQL-Statement (ohne 'AND') für die Einschränkung des Datums erstellen.<br>
	 * Kann ein Zeitraum von ... bis sein, ein einzelner Tag oder ein Zeitraum ohne Beginn.
	 * 
	 * @return Where-Teil des SQL-Statement (ohne 'AND') oder null
	 * @throws Exception 
	 */
	public String getWhereDatum() throws Exception {
		return getWhereDatum(getDatumVon(), getDatumBis());
	}


	/**
	 * Where-Teil des SQL-Statement (ohne 'AND') für die Einschränkung des Datums erstellen.<br>
	 * Kann ein Zeitraum von ... bis sein, ein einzelner Tag oder ein Zeitraum ohne Beginn.
	 * 
	 * @return Where-Teil des SQL-Statement (ohne 'AND') oder null
	 * @throws Exception 
	 */
	public static String getWhereDatum(Date datumVon, Date datumBis) throws Exception {
		String where;
		
		// wenn kein DatumBis angegeben
		if (datumBis == null)
		{
			// DatumBis = DatumVon damit nur ein Tag angegeben wird, wenn beide null sind -> keine Einschränkung des Datums
			if (datumVon != null)
			{
				datumBis = datumVon;
			}
//			else
//			{
//				datumBis = new Date();
//			}
		}

		// DatumBis einen Tag weiter setzen für einfache Abfrage im SQL-Statement
		datumBis = Format.getDateVerschoben(datumBis, 1);
		

		// SQL-Abfrage zusammensetzen
		where = (datumVon == null ? "" : " AND datum > '" + Format.getStringForDB(datumVon) + "'")
				+ (datumBis == null ? "" : " AND datum < '" + Format.getStringForDB(datumBis) + "'");
		if (where.trim().startsWith("AND"))
		{
			where = where.substring(4);
		}
		
		return (where.isEmpty() ? null : where);
	}


	/**
	 * Where-Teil des SQL-Statement für die Einschränkung des Datums erstellen. <br>
	 * Dabei wird kein Datum für den heutigen Tag oder in der Zukunft zugelassen.
	 * 
	 * @throws Exception 
	 */
	public String getWhereDatumBisHeute() throws Exception {
		String where;
		
		where = getWhereDatum();
		
		return " datum < '" + Format.getString(new Date()) + "' " + (where == null ? "" : " AND " + where);
	}


	/**
	 * Where-Teil des SQL-Statement für die Einschränkung der Person erstellen (ohne externe Personen)
	 * 
	 * @throws Exception 
	 */
	public String getWherePerson() throws Exception {
		return getWherePerson(false);
	}


	/**
	 * Where-Teil des SQL-Statement für die Einschränkung der Person erstellen
	 * 
	 * @param mitExternen externe Personen auch auswählen
	 * @throws Exception 
	 */
	public String getWherePerson(boolean mitExternen) throws Exception {
		String where, whereStatusAktivInaktiv;
		CoPerson coPerson;
		
		coPerson = new CoPerson();

		
		// externe Personen können ggf. nicht ausgewertet werden
		where = "";
		if (!mitExternen)
		{
			coPerson.loadByStatusExtern();
			where = " PersonID NOT IN (" + coPerson.getIDs(); 
			where += ")";
		}

		// nur aktive Personen können ausgewertet werden, außer es wird nur eine ausgewählt
//		if (getPersonID() == 0)
//		{
//			coPerson.loadByStatusNichtAktiv();
//			where += ", " + coPerson.getIDs(); 
//		}
//		where += ")"; 

		// ausgewählte Personen
		coPerson = getCoPersonByAuswahl();
		// In Standard-Auswertungen (z. B. Urlaubsplanung) darf man auf seine ganze Abteilung... zugreifen
		// Einschränkung auf die Personen, auf die man Zugriff hat siehe CoAuswertungFreigabe
		
		// Status der Personen
		whereStatusAktivInaktiv = getWhereStatusAktivInaktiv();
		
		// Statement aus den ausgewählten und den nicht zugelassenen Personen zusammensetzen
		where += (coPerson != null && coPerson.getRowCount() > 0 ? " AND PersonID IN (" + coPerson.getIDs() + ")" : "")
				+ (whereStatusAktivInaktiv == null ? "" : whereStatusAktivInaktiv);
		
		if (where.trim().startsWith("AND"))
		{
			where = where.substring(4);
		}

		return where.trim().isEmpty() ? null : where;
	}


	/**
	 * Cacheobject mit den über Person/Abteilung ausgewählten Personen erstellen.<br>
	 * Nicht zugelassenen Personen, z. B. wegen Austritt oder Status werden nicht herausgefiltert und können somit ebenfalls in der Liste enthalten sein.
	 * 
	 * @return
	 * @throws Exception
	 */
	public CoPerson getCoPersonByAuswahl() throws Exception {
		int abteilungID;
		int personID;
		int personenlisteID;
		CoPerson coPerson;

		abteilungID = getAbteilungID();
		personID = getPersonID();
		personenlisteID = getPersonenlisteID();
		
		coPerson = new CoPerson();
		
		
		if (abteilungID > 0)
		{
			coPerson.loadByAbteilungID(abteilungID);
			
			// wenn keine MA in der Abteilung sind darf kein leeres CO zurückgegeben werden, da sonst alle MA geladen werden
			if (coPerson.hasNoRows())
			{
				coPerson.createNew();
			}
		}
		else if (personID > 0)
		{
			coPerson.loadByID(personID);
		}
		else if (personenlisteID > 0)
		{
			coPerson.loadByPersonenlisteID(personenlisteID);
		}
		else // wenn keine Einschränkung der Personen gewählt ist, alle Personen für die man Berechtigungen besitzt
		{
			// Personalansicht und alle übergeordneten haben für alle Berechtigungen
			if (!UserInformation.getInstance().isPersonalansicht())
			{
				coPerson.loadByCurrentUser();
			}
		}
		
		return coPerson;
	}


	/**
	 * Where-Teil des SQL-Statement für die Einschränkung der Position erstellen (mit führendem "AND")
	 * 
	 * @throws Exception 
	 */
	public String getWherePosition() throws Exception {
		int positionID;
		
		positionID = getPositionID();

		return (positionID > 0 ? " AND PositionID = " + positionID : null);
	}


	/**
	 * Where-Teil des SQL-Statement für die Einschränkung des Status Aktiv/Inaktiv erstellen (mit führendem "AND")
	 * 
	 * @throws Exception 
	 */
	private String getWhereStatusAktivInaktiv() throws Exception {
		int statusID;
		
		statusID = getStatusAktivInaktivID();

		return (statusID > 0 ? " AND StatusAktivInaktivID = " + statusID : null);
	}


	/**
	 * String mit Einschränkungen Datum und Person, zur Bestimmung des Dateinamens
	 * 
	 * @return 
	 */
	public String getStringEinschraenkungDatumPerson() {
		String stringValue;
		Date date;
		
		stringValue = "";
		
		
		// Datum
		date = getDatumVon();
		if (date != null)
		{
			stringValue += "_" + Format.getString(date);
		}
				
		date = getDatumBis();
		if (date != null)
		{
			stringValue += "_bis_" + Format.getString(date);
		}
		
		// Personen
		if (getAbteilungID() > 0)
		{
			stringValue += "_" + getAbteilung();
		}
		else if (getPersonID() > 0)
		{
			stringValue += "_" + getPerson();
		}
		else if (getPersonenlisteID() > 0)
		{
			stringValue += "_" + getPersonenliste();
		}
		
		stringValue = stringValue.replace(",", "");
		stringValue = stringValue.replace(" ", "_");
		stringValue = stringValue.replace("/", "_");
		stringValue = stringValue.replace("&", "und");
//		stringValue = stringValue.replace("ä", "ae");
//		stringValue = stringValue.replace("Ä", "Ae");
//		stringValue = stringValue.replace("ö", "oe");
//		stringValue = stringValue.replace("Ö", "Oe");
//		stringValue = stringValue.replace("ü", "ue");
//		stringValue = stringValue.replace("U", "Ue");
//		stringValue = stringValue.replace("ß", "ss");
		
		return stringValue;
	}
	

	/**
	 * Where-Teil des SQL-Statement für die Einschränkung des übergebenen Feldes mit der ID
	 * @param fieldName 
	 * @param id ID oder 0/-1
	 * 
	 * @throws Exception 
	 * @return Where-Teil ('AND fieldName=id' oder '')
	 */
	public String getWhereID(int id, String fieldName) throws Exception {
		return (id > 0 ? " AND " + fieldName + " = " + id : "");
	}


}
