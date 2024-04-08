package pze.business.objects.reftables;

import java.util.Date;

import framework.Application;
import framework.business.interfaces.data.IBusinessObject;
import framework.business.interfaces.fields.IField;
import pze.business.Format;
import pze.business.Messages;
import pze.business.UserInformation;
import pze.business.objects.AbstractCacheObject;
import pze.business.objects.reftables.personen.CoAbteilung;
import pze.business.objects.reftables.personen.CoPosition;

/**
 * CacheObject für Freigabeberechtigungen
 * 
 * @author Lisiecki
 *
 */
public class CoFreigabeberechtigungen extends AbstractCacheObject {

	public static final String TABLE_NAME = "rtblfreigabeberechtigung";

//	private static CoFreigabeberechtigungen m_instance = null;
	


	/**
	 * Kontruktor
	 */
	public CoFreigabeberechtigungen() {
		super("table." + TABLE_NAME);
	}
	

	/**
	 * Kontruktor
	 * @throws Exception 
	 */
	public CoFreigabeberechtigungen(int personID) throws Exception {
		super("table." + TABLE_NAME);
		loadByPersonID(personID);
	}
	

//	/**
//	 * Getter Instanz der Klasse
//	 * 
//	 * @return m_instance
//	 */
//	public static CoFreigabeberechtigungen getInstance() throws Exception {
//		if (CoFreigabeberechtigungen.m_instance == null)
//		{
//			CoFreigabeberechtigungen.m_instance = new CoFreigabeberechtigungen();
//			CoFreigabeberechtigungen.m_instance.loadAll();
//		}
//		
//		return CoFreigabeberechtigungen.m_instance;
//	}


	/**
	 * Prüfen, ob die Person Freigabeberechtigungen besitzt
	 * 
	 * @param personID
	 * @throws Exception
	 */
	public static boolean hasBerechtigungen(int personID) throws Exception {
		CoFreigabeberechtigungen coFreigabeberechtigungen;
		
		coFreigabeberechtigungen = new CoFreigabeberechtigungen(personID);
		Application.getLoaderBase().load(coFreigabeberechtigungen, getWherePerson(personID, true)
				+ " AND (TechBerechnungen=1 OR NukBerechnungen=1 OR Rueckbauplanung=1 OR Entsorgungsplanung=1 OR Bauplanung=1 OR Verwaltung=1"
				+ " OR AbteilungsleiterV=1 OR AbteilungsleiterT=1 OR Bereichsleiter=1 OR GF=1 OR Azubi=1)", null);
		
		return coFreigabeberechtigungen.hasRows();
	}
	

//	private IField getFieldBerechnungen() {
//		return getField("field." + getTableName() + ".berechnungen");
//	}
//

//	public boolean isBerechnungenFreigabeErlaubt() {
//		return Format.getBooleanValue(getFieldBerechnungen());
//	}


	private IField getFieldTechBerechnungen() {
		return getField("field." + getTableName() + ".techberechnungen");
	}


	private boolean isTechBerechnungenFreigabeErlaubt() {
		return Format.getBooleanValue(getFieldTechBerechnungen());
	}


	private IField getFieldNukBerechnungen() {
		return getField("field." + getTableName() + ".nukberechnungen");
	}


	private boolean isNukBerechnungenFreigabeErlaubt() {
		return Format.getBooleanValue(getFieldNukBerechnungen());
	}


	public boolean isFreigabeNukBerechnungenErlaubt() {
		if (!moveFirst())
		{
			return false;
		}
		
		do
		{
			if (isNukBerechnungenFreigabeErlaubt())
			{
				return true;
			}
		} while (moveNext());
		
		return false;
	}


	private IField getFieldRueckbauplanung() {
		return getField("field." + getTableName() + ".rueckbauplanung");
	}


	private boolean isRueckbauplanungFreigabeErlaubt() {
		return Format.getBooleanValue(getFieldRueckbauplanung());
	}


	private IField getFieldEntsorgungsplanung() {
		return getField("field." + getTableName() + ".entsorgungsplanung");
	}


	private boolean isEntsorgungsplanungFreigabeErlaubt() {
		return Format.getBooleanValue(getFieldEntsorgungsplanung());
	}


	private IField getFieldBauplanung() {
		return getField("field." + getTableName() + ".bauplanung");
	}


	private boolean isBauplanungFreigabeErlaubt() {
		return Format.getBooleanValue(getFieldBauplanung());
	}


	private IField getFieldVerwaltung() {
		return getField("field." + getTableName() + ".verwaltung");
	}


	private boolean isVerwaltungFreigabeErlaubt() {
		return Format.getBooleanValue(getFieldVerwaltung());
	}


	public boolean isFreigabeVerwaltungErlaubt() {
		if (!moveFirst())
		{
			return false;
		}
		
		do
		{
			if (isVerwaltungFreigabeErlaubt())
			{
				return true;
			}
		} while (moveNext());
		
		return false;
	}


	private IField getFieldAbteilungsleiterV() {
		return getField("field." + getTableName() + ".abteilungsleiterv");
	}


	private boolean isAbteilungsleiterVFreigabeErlaubt() {
		return Format.getBooleanValue(getFieldAbteilungsleiterV());
	}


	private IField getFieldAbteilungsleiterT() {
		return getField("field." + getTableName() + ".abteilungsleitert");
	}


	private boolean isAbteilungsleiterTFreigabeErlaubt() {
		return Format.getBooleanValue(getFieldAbteilungsleiterT());
	}


	private IField getFieldBereichsleiter() {
		return getField("field." + getTableName() + ".bereichsleiter");
	}


	private boolean isBereichsleiterFreigabeErlaubt() {
		return Format.getBooleanValue(getFieldBereichsleiter());
	}


	private IField getFieldGF() {
		return getField("field." + getTableName() + ".gf");
	}


	private boolean isGfFreigabeErlaubt() {
		return Format.getBooleanValue(getFieldGF());
	}


	private IField getFieldAzubi() {
		return getField("field." + getTableName() + ".azubi");
	}


	private boolean isAzubiFreigabeErlaubt() {
		return Format.getBooleanValue(getFieldAzubi());
	}


	public boolean isFreigabeAzubiErlaubt() {
		if (!moveFirst())
		{
			return false;
		}
		
		do
		{
			if (isAzubiFreigabeErlaubt())
			{
				return true;
			}
		} while (moveNext());
		
		return false;
	}


	private IField getFieldDrAusland() {
		return getField("field." + getTableName() + ".drausland");
	}


	private boolean isDrAuslandFreigabeErlaubt() {
		return Format.getBooleanValue(getFieldDrAusland());
	}


	public boolean isFreigabeDrAuslandErlaubt() {
		if (!moveFirst())
		{
			return false;
		}
		
		do
		{
			if (isDrAuslandFreigabeErlaubt())
			{
				return true;
			}
		} while (moveNext());
		
		return false;
	}


	private IField getFieldVertreterID() {
		return getField("field." + getTableName() + ".vertreterid");
	}


	private int getVertreterID() {
		return Format.getIntValue(getFieldVertreterID());
	}


	private void setVertreterID(int vertreterID) {
		getFieldVertreterID().setValue(vertreterID);
	}


	private IField getFieldDatumVon() {
		return getField("field." + getTableName() + ".datumvon");
	}


	private Date getDatumVon() {
		return getDatum(getFieldDatumVon());
	}


	private IField getFieldDatumBis() {
		return getField("field." + getTableName() + ".datumbis");
	}


	private Date getDatumBis() {
		return getDatum(getFieldDatumBis());
	}


	private Date getDatum(IField field) {
		if (field == null)
		{
			return null;
		}
		
		return Format.getDate12Uhr(Format.getDateValue(field.getValue()));
	}

	
	/**
	 * Datum der Einträge prüfen
	 */
	@Override
	public String validate() throws Exception{
		int rowState;
		
		if (moveFirst())
		{
			do
			{
				rowState = getCurrentRow().getRowState();
				// geänderte Zeile oder neue Zeile 
				if ((rowState == IBusinessObject.statusAdded || rowState == IBusinessObject.statusChanged)
						&& getDatumVon() != null && getDatumBis() == null)
				{
					if (!Messages.showYesNoMessage("Warnung", "Bei Eintragungen ohne 'Datum bis' gelten die Berechtigungen zeitlich unbegrenzt. "
							+ "Möchten Sie fortfahren?"))
					{
						return "Bearbeitung abgebrochen.";
					}
				}
			} while (moveNext());
		}
		
		return super.validate();
	}
	
	
	/**
	 * alle Daten für das Freigabecenter der Person laden
	 * 
	 * @param personID
	 * @throws Exception
	 */
	public void loadForFreigabecenter(int personID) throws Exception {
		// Berechtigungen der Person
		loadByPersonID(personID, true);
		
		// Berechtigungen von Vertretern dazu laden
		Application.getLoaderBase().load(this, "VertreterID=" + personID, getSortFieldName());
	}


	/**
	 * alle Daten für die Person laden
	 * 
	 * @param personID
	 * @throws Exception
	 */
	@Override
	public void loadByPersonID(int personID) throws Exception {
		loadByPersonID(personID, true);
	}


	/**
	 * alle Daten für die Person laden
	 * 
	 * @param personID
	 * @param mitVertreterrechten alle Rechte laden oder ohne die als Vertreter
	 * @throws Exception
	 */
	private void loadByPersonID(int personID, boolean mitVertreterrechten) throws Exception {
		String where;
		
		where = getWherePerson(personID, mitVertreterrechten);
		
		emptyCache();
		Application.getLoaderBase().load(this, where, getSortFieldName());
	}


	/**
	 * Where-Teil um die Berechtigungen für eine Person zu laden
	 * 
	 * @param personID
	 * @param mitVertreterrechten
	 * @return
	 */
	private static String getWherePerson(int personID, boolean mitVertreterrechten) {
		String where;
		Date datumHeute;
		Date datumMorgen;
		
		datumHeute = Format.getDate0Uhr(new Date());
		datumMorgen = Format.getDateVerschoben(datumHeute, 1);
		
		where = " PersonID= " + personID;
		
		// ggf. nicht als Vertreter
		where += mitVertreterrechten ? "" : " AND VertreterID IS NULL";
		
		// Datum beachten
		where += " AND (DatumVon IS NULL OR DatumVon < '" + Format.getStringForDB(datumMorgen) + "')"
				+ " AND (DatumBis IS NULL OR DatumBis > '" + Format.getStringForDB(datumHeute) + "')";
		
		return where;
	}


	/**
	 * Select-Statement für derzeit gültige Personen, die die übergebene Person vertritt
	 * 
	 * @param personID
	 * @return
	 * @throws Exception
	 */
	public static String getSelectVertreterID(int personID) throws Exception {
		String sql;
		
		sql = "SELECT DISTINCT VertreterID FROM " + TABLE_NAME + " WHERE " 
		+ getWherePerson(personID, true)
		+ " AND VertreterID IS NOT NULL";
		
		return sql;
	}


	/**
	 * @return Bezeichnung des Sortierungsfeldes, voreingestellt, zweites Feld...
	 */
	@Override
	protected String getSortFieldName() {
		return "VertreterID, DatumVon";
	}
	

	/**
	 * Where-Teil mit Berechtigungen der Person als Vertreter für eine andere Person, unabhängig von den Berechtigungen
	 * 
	 * @param personID
	 * @return String mit where-Teil oder null für keine Berechtigungen
	 * @throws Exception
	 */
	public String createWherePerson(int personID) throws Exception {
		String where, aktWhere;
		
		// Berechtigungen laden
		loadByPersonID(personID);
		
		// keine Berechtigungen
		if (hasNoRows())
		{
			return null;
		}
		
		// Berechtigungen durchlaufen
		where = null;
		moveFirst();
		do
		{
			aktWhere = " PersonID=" + getVertreterID();

			if (where == null)
			{
				where = aktWhere;
			}
			else // Person mehr als einmal eingtragen
			{
				where += " OR " + aktWhere;
			}
		} while (moveNext());

		return where;
	}


	/**
	 * Where-Teil mit Berechtigungen der Person für bestimmte Personengruppen
	 * 
	 * @param personID
	 * @param sekretaerinnenVerwaltung Sekretärinnen sollen auch bei Berechtigungen für die Verwaltung geladen werden
	 * @return String mit where-Teil oder null für keine Berechtigungen
	 * @throws Exception
	 */
	public String createWhere(int personID, boolean sekretaerinnenVerwaltung) throws Exception {
		String where, aktWhere;
		
		// Berechtigungen laden
		loadByPersonID(personID);
		
		// keine Berechtigungen
		if (hasNoRows())
		{
			return null;
		}
		
		// Berechtigungen durchlaufen
		where = null;
		moveFirst();
		do
		{
			aktWhere = getWhere(sekretaerinnenVerwaltung);

			// keine Berechtigungen
			if (aktWhere == null)
			{
				continue;
			}

			// Berechtigungen speichern
			if (where == null)
			{
				where = aktWhere;
			}
			else // Person mehr als einmal eingtragen
			{
				where += " OR " + aktWhere;
			}
		} while (moveNext());

		return where;
	}


	/**
	 * Where-Teil mit Berechtigungen für die aktuelle Zeile
	 * 
	 * @param sekretaerinnenVerwaltung Sekretärinnen sollen auch bei Berechtigungen für die Verwaltung geladen werden
	 * @return String mit where-Teil oder null für keine Berechtigungen
	 * @throws Exception
	 */
	private String getWhere(boolean sekretaerinnenVerwaltung) {
		String where;
		where = "";
		
		// Berechtigungen für Abteilungen
 		if (isTechBerechnungenFreigabeErlaubt())
		{
			where += " OR AbteilungID=" + CoAbteilung.ID_TECH_BERECHNUNGEN;
		}
		
		if (isNukBerechnungenFreigabeErlaubt())
		{
			where += " OR AbteilungID=" + CoAbteilung.ID_NUK_BERECHNUNGEN;
		}
		
		if (isRueckbauplanungFreigabeErlaubt())
		{
			where += " OR AbteilungID=" + CoAbteilung.ID_RUECKBAUPLANUNG;
		}
		
		if (isBauplanungFreigabeErlaubt())
		{
			where += " OR AbteilungID=" + CoAbteilung.ID_BAUPLANUNG;
		}
		
		if (isEntsorgungsplanungFreigabeErlaubt())
		{
			where += " OR AbteilungID=" + CoAbteilung.ID_ENTSORGUNGSPLANUNG;
		}
		
		if (isVerwaltungFreigabeErlaubt()) // Verwaltung gibt auch für Sekretärinnen frei
		{
			where += " OR AbteilungID=" + CoAbteilung.ID_VERWALTUNG;
			where += sekretaerinnenVerwaltung ? " OR PositionID=" + CoPosition.ID_SEKRETAERIN : "";
		}
		
		// Berechtigungen für Positionen/Funktionen
		if (isBereichsleiterFreigabeErlaubt())
		{
			where += " OR PositionID=" + CoPosition.ID_KL + " OR PositionID=" + CoPosition.ID_TL;
		}

		if (isGfFreigabeErlaubt())
		{
			where += " OR AbteilungID=" + CoAbteilung.ID_GESCHAEFTSFUEHRUNG;
		}

		// Azubi
		if (isAzubiFreigabeErlaubt())
		{
			where += " OR PositionID=" + CoPosition.ID_AZUBI;
		}

		// AL werden separat, nicht in der Abteilung freigegeben
		if (!where.isEmpty())
		{
			where = where.trim();
			where = "((" + where.substring(where.indexOf(" ")) + ")";
			
			where += " AND PositionID<>" + CoPosition.ID_AL + ")";
		}

		if (isAbteilungsleiterVFreigabeErlaubt())
		{
			where += (where.isEmpty() ? "" : " OR ") + "(PositionID=" + CoPosition.ID_AL 
					+ " AND (AbteilungID=" + CoAbteilung.ID_TECH_BERECHNUNGEN + " OR AbteilungID=" + CoAbteilung.ID_NUK_BERECHNUNGEN + "))";
		}
		if (isAbteilungsleiterTFreigabeErlaubt())
		{
			where += (where.isEmpty() ? "" : " OR ") + "(PositionID=" + CoPosition.ID_AL 
					+ " AND (AbteilungID=" + CoAbteilung.ID_BAUPLANUNG + " OR AbteilungID=" + CoAbteilung.ID_ENTSORGUNGSPLANUNG
					+ " OR AbteilungID=" + CoAbteilung.ID_RUECKBAUPLANUNG + "))";
		}

		
		// aktuell keine Berechtigungen
		if (where.isEmpty())
		{
			return null;
		}
		
		return "(" + where + ")";
	}


	/**
	 * Neuen Datensatz mit neuem Primärschlüssel erzeugen
	 * 
	 * @return neue ID
	 * @throws Exception
	 */
	@Override
	public int createNew() throws Exception	{
		if(!isEditing())
		{
			begin();
		}
		
		int id = nextID();
		
		add();
		setID(id);
		setVertreterID(UserInformation.getPersonID());
		return id;
	}


}
