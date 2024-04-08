package pze.business.objects.projektverwaltung;

import java.util.Date;

import framework.Application;
import framework.business.interfaces.fields.IField;
import pze.business.Format;
import pze.business.UserInformation;
import pze.business.objects.reftables.projektverwaltung.CoStatusProjekt;

/**
 * CacheObject für Aufträge
 * 
 * @author Lisiecki
 *
 */
public class CoAuftrag extends CoProjekt { // TODO Umstrukturierung bereich/Sparte löschen

	public static final String TABLE_NAME = "tblauftrag";

	public static final int ID_URLAUB_PRODUKTIV = 200;
	public static final int ID_URLAUB_UNPRODUKTIV = 201;
	public static final int ID_SONDERURLAUB_PRODUKTIV = 202;
	public static final int ID_SONDERURLAUB_UNPRODUKTIV = 203;
	public static final int ID_ELTERNZEIT_PRODUKTIV = 204;
	public static final int ID_ELTERNZEIT_UNPRODUKTIV = 205;
	public static final int ID_KRANK_PRODUKTIV = 206;
	public static final int ID_KRANK_UNPRODUKTIV = 207;
	public static final int ID_KRANK_OHNE_LFZ_PRODUKTIV = 208;
	public static final int ID_KRANK_OHNE_LFZ_UNPRODUKTIV = 209;



	/**
	 * Kontruktor
	 */
	public CoAuftrag() {
		super("table." + TABLE_NAME);
	}
	

	
	/**
	 * Berechnete Spalten mit laden
	 * 
	 * @see pze.business.objects.AbstractCacheObject#loadByID(int)
	 */
	@Override
	public void loadByID(int id) throws Exception {
		String sql;
		
		sql = "SELECT * FROM " + getTableName() + " OUTER APPLY funBudgetAuftrag(" + id + ") WHERE ID=" + id + " ORDER BY " + getSortFieldName();
		
		addField("virt.field.projekt.bestellwert");
		addField("virt.field.projekt.sollstunden");
		addField("virt.field.projekt.startwert");
		addField("virt.field.projekt.iststunden");
		addField("virt.field.projekt.wertzeitverbleibend");
		addField("virt.field.projekt.verbrauchbestellwert");
		addField("virt.field.projekt.verbrauchsollstunden");
		
		emptyCache();
		Application.getLoaderBase().load(this, sql);
	}
	

	/**
	 * Laden von Aufträgen in Abhängigkeit von ihrem Status und dem Kunden
	 * 
	 * @param statusProjektID
	 * @param kundeID KundeID oder 0
	 * @param checkProjektleiter soll der Projektleiter des Auftrags geprüft werden oder alle Aufträge mit Abrufen des PL laden
	 * @throws Exception
	 */
	public void load(int statusProjektID, int kundeID, boolean checkProjektleiterAuftrag) throws Exception {
		int projektleiterID;
		String sql;

		addField("virt.field.projekt.sparteid");

		// wenn der aktuelle Benutzer nur Projektleiter ist, darf er nur seine eigenen Projekte laden
		projektleiterID = UserInformation.getInstance().getPersonIDAlsProjektleiter();

		// SQL-Statement
		sql = "SELECT DISTINCT " + TABLE_NAME + ".*, rtblBereich.SparteID AS SparteID " + getFromAuftragJoinAbruf(statusProjektID)
		+ (kundeID > 0 ? " AND tblAuftrag.KundeID=" + kundeID : "")
		+ (projektleiterID > 0 ? " AND ("
				+ "("
				// PL der Abrufe prüfen
				+ "(" + (checkProjektleiterAuftrag ? CoAbruf.TABLE_NAME + ".ProjektleiterID IS NULL " + " OR " : "")
				+ CoAbruf.TABLE_NAME + ".ProjektleiterID=" + projektleiterID + " OR " + CoAbruf.TABLE_NAME + ".ProjektleiterID2=" + projektleiterID + ")"
				// PL der Aufträge bei Bedarf prüfen
				+ (checkProjektleiterAuftrag ? " AND " : " OR ") 
				+ "(" + TABLE_NAME + ".ProjektleiterID=" + projektleiterID + " OR "+ TABLE_NAME + ".ProjektleiterID2=" + projektleiterID + ")"
				+ ")"
				+ " OR " // oder MA als Bearbeiter eingetragen
				+ "(" + CoAbruf.TABLE_NAME + ".ID IN (SELECT AbrufID FROM tblMitarbeiterProjekt WHERE PersonID=" + projektleiterID + ")"
				+ (checkProjektleiterAuftrag ? " AND " : " OR ") 
				+ TABLE_NAME + ".ID IN (SELECT AuftragID FROM tblMitarbeiterProjekt WHERE PersonID=" + projektleiterID + ")"
				+ ")"
				+ ")" : "")
		+ " ORDER BY " + getSortFieldName();

		sql = sql.replace("WHERE", " LEFT OUTER JOIN rtblBereich ON (" + TABLE_NAME + ".BereichID=rtblBereich.ID) WHERE ");

		emptyCache();
		Application.getLoaderBase().load(this, sql);
	}


	/**
	 * Alle Aufträge laden
	 * 
	 * @throws Exception
	 */
	public void loadAllForUebersicht() throws Exception {
		int projektleiterID;
		String sql;

		addField("virt.field.projekt.sparteid");

		// wenn der aktuelle Benutzer nur Projektleiter ist, darf er nur seine eigenen Projekte laden
		projektleiterID = UserInformation.getInstance().getPersonIDAlsProjektleiter();

		// SQL-Statement
		sql = "SELECT DISTINCT " + TABLE_NAME + ".*, rtblBereich.SparteID AS SparteID FROM " + TABLE_NAME 
				+ " LEFT OUTER JOIN rtblBereich ON (" + TABLE_NAME + ".BereichID=rtblBereich.ID) "
//				+ (projektleiterID > 0 ? " WHERE (" + TABLE_NAME + ".ProjektleiterID=" + projektleiterID + ")" : "")  
				// PL der Aufträge prüfen
				+ (projektleiterID > 0 ? " WHERE (" + TABLE_NAME + ".ProjektleiterID=" + projektleiterID 
						+ " OR " + TABLE_NAME + ". ProjektleiterID2=" + projektleiterID + ")" : "")
				+ " ORDER BY " + getSortFieldName();

		emptyCache();
		Application.getLoaderBase().load(this, sql);
	}


	/**
	 * Alle Aufträge mit jahresweisem Budget laden
	 * 
	 * @throws Exception
	 */
	public void loadBudgetJahresweise() throws Exception {
		String sql;

		// SQL-Statement
		sql = "SELECT * FROM " + TABLE_NAME + " WHERE BudgetJahresweise = 1 " + " ORDER BY " + getSortFieldName();

		emptyCache();
		Application.getLoaderBase().load(this, sql);
	}


	/**
	 * Projektmerkmal für den Auftrag laden und in das Cacheobjekt eintragen
	 * 
	 * @throws Exception
	 */
	public void addProjektmerkmalAuftrag() throws Exception {
//		int iProjektmerkmal;
		CoAuftragProjektmerkmal coAuftragProjektmerkmal = new CoAuftragProjektmerkmal();
		
		if (getField("virt.field.projekt.auftragprojektmerkmalid") == null)
		{
			addField("virt.field.projekt.auftragprojektmerkmalid");
		}
		

		if (moveFirst())
		{
			do
			{
				coAuftragProjektmerkmal.loadByAuftragID(getID());

				if (coAuftragProjektmerkmal.moveFirst())
				{
//					iProjektmerkmal = 1;
					do
					{
						getField("virt.field.projekt.auftragprojektmerkmalid" /*+ iProjektmerkmal++ + "id"*/).
						setValue(coAuftragProjektmerkmal.getProjektmerkmalID());
					} while (coAuftragProjektmerkmal.moveNext());
				}
			} while (moveNext());
		}
	}
	

	/**
	 * Laden der Items von Aufträgen mit verschiedenen Filtermöglichkeiten
	 * 
	 * @param kundeID kundeID oder 0
	 * @param abrufID abrufID oder 0 
	 * @param kostenstelleID kostenstelleID oder 0
	 * @param joinAbruf Join über den Abruf oder den Kunden einer Kostenstelle
	 * @throws Exception
	 */
	public void loadItems(int kundeID, int abrufID, int kostenstelleID, boolean joinAbruf) throws Exception {
		String from, where, sql;
		
		
		// FROM
		from = " FROM " + getTableName();
		
		// über Abruf
		if (joinAbruf && (abrufID != 0 || kostenstelleID != 0))
		{
			from += " JOIN tblAbruf ab ON (" + TABLE_NAME + ".ID = ab.AuftragID) ";
			
			// über Abruf und Kostenstelle
			if (kostenstelleID != 0)
			{
				from += " JOIN stblAbrufKostenstelle s ON (ab.ID = s.AbrufID) JOIN tblKostenstelle k ON (s.KostenstelleID = k.ID)";  
			}
		}
		else if (!joinAbruf && kostenstelleID != 0) // über Kostenstelle und Kunde
		{
			from += " JOIN tblKostenstelle k ON (" + TABLE_NAME + ".KundeID = k.KundeID)";  
		}

		
		// WHERE
		where = ""
				+ (kundeID == 0 ? "" : " AND " + TABLE_NAME + ".kundeID=" + kundeID) 
				+ (abrufID == 0 ? "" : " AND ab.ID=" + abrufID) 
				+ (kostenstelleID == 0 ? "" : " AND k.ID=" + kostenstelleID)
				+ " AND " + TABLE_NAME + ".StatusID = " + CoStatusProjekt.STATUSID_LAUFEND;

		if (where.length() > 0)
		{
			where = " WHERE " + where.substring(4);
		}

		
		// SQL-Statement, getSortFieldName() muss im SELECT wegen DISTINCT angegeben werden (SQL-Regel)
		sql = "SELECT DISTINCT " + TABLE_NAME + ".ID, (AuftragsNr + ', ' + ISNULL(" + TABLE_NAME + ".Beschreibung, '')) AS AuftragsNr, " + getSortFieldName() 
		+ from + where
		+ " ORDER BY " + getSortFieldName();
		
		emptyCache();
		Application.getLoaderBase().load(this, sql);
		
		// wenn Aufträge zu einer Kostenstelle gesucht sind, aber keine zugeordnet sind, lade alle Kostenstellen des Kunden
		if (getRowCount() == 0 && joinAbruf && kostenstelleID != 0 && abrufID == 0)
		{
			loadItems(kundeID, abrufID, kostenstelleID, false);
		}
	}


	/**
	 * Laden der Items von Aufträgen für die KGG-Stundenauswertung
	 * 
	 * @throws Exception
	 */
	public void loadItemsKGG() throws Exception {
		String from, where, sql;
		
		
		// FROM
		from = " FROM " + getTableName();

		// über Abruf und Kostenstelle
		from += " JOIN tblAbruf ab ON (" + TABLE_NAME + ".ID = ab.AuftragID) ";
		from += " JOIN stblAbrufKostenstelle s ON (ab.ID = s.AbrufID) JOIN tblKostenstelle k ON (s.KostenstelleID = k.ID)";  
		from += " JOIN tblBerichtsNr b ON (b.KostenstelleID = k.ID)";  

		
		// WHERE
		where = ""
//				+ " AND " + TABLE_NAME + ".StatusID = " + CoStatusProjekt.STATUSID_LAUFEND
				;

		if (where.length() > 0)
		{
			where = " WHERE " + where.substring(4);
		}

		
		// SQL-Statement, getSortFieldName() muss im SELECT wegen DISTINCT angegeben werden (SQL-Regel)
//		sql = "SELECT DISTINCT " + TABLE_NAME + ".ID, (AuftragsNr + ', ' + ISNULL(" + TABLE_NAME + ".Beschreibung, '')) AS AuftragsNr, " + getSortFieldName() 
		sql = "SELECT DISTINCT " + TABLE_NAME + ".ID, AuftragsNr, " + getSortFieldName() 
		+ from + where
		+ " ORDER BY " + getSortFieldName();
		
		emptyCache();
		Application.getLoaderBase().load(this, sql);
	}


	/**
	 * FROM-Teil eines SQL-Statements, um den Status von Abrufen abzufragen
	 * 
	 * @param statusProjektID
	 * @return
	 */
	public static String getFromAuftragJoinAbruf(int statusProjektID) {
		String from;
		
		from = " FROM " + TABLE_NAME + " LEFT OUTER JOIN " + CoAbruf.TABLE_NAME + " ON (" + TABLE_NAME + ".ID = " + CoAbruf.TABLE_NAME + ".AuftragID) "
				+ " WHERE (" + getWhereProjektStatus(CoAbruf.TABLE_NAME, statusProjektID)
				+ " OR " + getWhereProjektStatus(TABLE_NAME, statusProjektID) + ") ";
		
		return from;
	}


	/**
	 * SELECT-Teil eines SQL-Statements, um die Kunden von Abrufen mit übergebenen Status abzufragen
	 * 
	 * @param statusProjektID
	 * @param checkProjektleiter Projektleiter muss geprüft werden oder alle anzeigen
	 * @return
	 * @throws Exception 
	 */
	public static String getSelectKundeID(int statusProjektID, boolean checkProjektleiter) throws Exception {
		int projektleiterID;
		
		// wenn der aktuelle Benutzer Projektleiter ist, darf er nur seine eigenen Projekte laden
		projektleiterID = UserInformation.getInstance().getPersonIDAlsProjektleiter();

		return "SELECT " + TABLE_NAME + ".KundeID " + CoAuftrag.getFromAuftragJoinAbruf(statusProjektID)
		+ (projektleiterID > 0 && checkProjektleiter ? " AND (" // Person
				// Person als PL eingetragen
				+ "(" + TABLE_NAME + ".ProjektleiterID=" + projektleiterID // PL der Aufträge prüfen	
				+ " OR " + TABLE_NAME + ".ProjektleiterID2=" + projektleiterID // PL der Aufträge prüfen	
				+ " OR " + CoAbruf.TABLE_NAME + ".ProjektleiterID=" + projektleiterID // PL der Abrufe prüfen	
				+ " OR " + CoAbruf.TABLE_NAME + ".ProjektleiterID2=" + projektleiterID + ")" // PL der Abrufe prüfen	
				+ " OR "
				// oder MA als Bearbeiter eingetragen
				+ "(" + TABLE_NAME + ".ID IN (SELECT AuftragID FROM tblMitarbeiterProjekt WHERE PersonID=" + projektleiterID + ") "
						+ "OR " +  CoAbruf.TABLE_NAME + ".ID IN (SELECT AbrufID FROM tblMitarbeiterProjekt WHERE PersonID=" + projektleiterID + "))"
				+ ")": ""); 
	}


	/**
	 * AuftragsNr + Bezeichnung
	 * 
	 * @see pze.business.objects.AbstractCacheObject#getSortFieldName()
	 */
	protected String getSortFieldName() {
		return TABLE_NAME + ".AuftragsNr, " + TABLE_NAME + ".Beschreibung";
	}
	

	@Override
	public int getAuftragID() {
		return getID();
	}


	public IField getFieldAuftragsNr() {
		return getField("field." + getTableName() + ".auftragsnr");
	}


	@Override
	public String getAuftragsNr() {
		return Format.getStringValue(getFieldAuftragsNr().getValue());
	}


	public void setAuftragsNr(String auftragsNr) {
		getFieldAuftragsNr().setValue(auftragsNr);
	}


	public IField getFieldBestellNr() {
		return getField("field." + getTableName() + ".bestellnr");
	}


	@Override
	public String getBestellNr() {
		return Format.getStringValue(getFieldBestellNr().getValue());
	}


	public void setBestellNr(Object bestellNr) {
		getFieldBestellNr().setValue(bestellNr);
	}


	public IField getFieldDatumBestellung() {
		return getField("field." + getTableName() + ".datumbestellung");
	}


	@Override
	public Date getDatumBestellung() {
		return Format.getDateValue(getFieldDatumBestellung().getValue());
	}


	public void setDatumBestellung(Object bestelldatum) {
		getFieldDatumBestellung().setValue(bestelldatum);
	}


	public IField getFieldAngebotsNr() {
		return getField("field." + getTableName() + ".angebotsnr");
	}


	public String getAngebotsNr() {
		return Format.getStringValue(getFieldAngebotsNr().getValue());
	}


	public void setAngebotsNr(Object angebotsNr) {
		getFieldAngebotsNr().setValue(angebotsNr);
	}


	public IField getFieldDatumAngebot() {
		return getField("field." + getTableName() + ".datumangebot");
	}


	public Date getDatumAngebot() {
		return Format.getDateValue(getFieldDatumAngebot().getValue());
	}


	public void setDatumAngebot(Object datumAngebot) {
		getFieldDatumAngebot().setValue(datumAngebot);
	}


	public IField getFieldDatumAuftragsbestaetigung() {
		return getField("field." + getTableName() + ".datumauftragsbestaetigung");
	}


	public Date getDatumAuftragsbestaetigung() {
		return Format.getDateValue(getFieldDatumAuftragsbestaetigung().getValue());
	}


	public void setDatumAuftragsbestaetigung(Object angebotsNr) {
		getFieldDatumAuftragsbestaetigung().setValue(angebotsNr);
	}


	public IField getFieldKundeID() {
		return getField("field." + getTableName() + ".kundeid");
	}


	@Override
	public int getKundeID() {
		return Format.getIntValue(getFieldKundeID().getValue());
	}


	public void setKundeID(Object kundeID) {
		getFieldKundeID().setValue(kundeID);
	}


	@Override
	public String getKunde() {
		return getFieldKundeID().getDisplayValue();
	}


	public IField getFieldStandortKundeID() {
		return getField("field." + getTableName() + ".standortkundeid");
	}


	public int getStandortKundeID() {
		return Format.getIntValue(getFieldStandortKundeID().getValue());
	}


	public void setStandortKundeID(Object standortKundeID) {
		getFieldStandortKundeID().setValue(standortKundeID);
	}


	public IField getFieldAbteilungID() {
		return getField("field." + getTableName() + ".abteilungid");
	}


	public int getAbteilungID() {
		return Format.getIntValue(getFieldAbteilungID().getValue());
	}


	public void setAbteilungID(Object abteilungID) {
		getFieldAbteilungID().setValue(abteilungID);
	}


	private IField getFieldMessage8Stunden() {
		return getField("field." + getTableName() + ".message8stunden");
	}


	public void setMessage8Stunden(boolean isAktiv) {
		getFieldMessage8Stunden().setValue(isAktiv);
	}

	
	@Override
	public String getKey() {
		return "auftrag." + getID();
	}
	

	/**
	 * Cacheobject mit den Projektmerkmalen des Auftrags
	 */
	public CoAuftragProjektmerkmal getCoAuftragProjektmerkmal() throws Exception {

		CoAuftragProjektmerkmal coAuftragProjektmerkmal;

		coAuftragProjektmerkmal = new CoAuftragProjektmerkmal();
		coAuftragProjektmerkmal.loadByAuftragID(getID());

		return coAuftragProjektmerkmal;

	}
	
	
	@Override
	public CoKostenstelle getCoKostenstelle() throws Exception {

		CoKostenstelle coKostenstelle;

		coKostenstelle = new CoKostenstelle();
		coKostenstelle.loadByAuftragID(getID());

		return coKostenstelle;

	}


}
