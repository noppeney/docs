package pze.business.objects.projektverwaltung;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import framework.Application;
import framework.business.interfaces.fields.IField;
import pze.business.Format;
import pze.business.Messages;
import pze.business.UserInformation;
import pze.business.objects.reftables.projektverwaltung.CoStatusProjekt;

/**
 * CacheObject für Abrufe
 * 
 * @author Lisiecki
 *
 */
public class CoAbruf extends CoProjekt {

	public static final String TABLE_NAME = "tblabruf";
	
	private CoAuftrag m_coAuftrag;



	/**
	 * Kontruktor
	 */
	public CoAbruf() {
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
		
		sql = "SELECT * FROM " + getTableName() + " OUTER APPLY funBudgetAbruf(" + id + ") WHERE ID=" + id + " ORDER BY " + getSortFieldName();
		
		addField("virt.field.projekt.bestellwert");
		addField("virt.field.projekt.sollstunden");
		addField("virt.field.projekt.startwert");// TODO nicht notwendig? auch bei coauftrag
		addField("virt.field.projekt.iststunden"); // wird verwendet, könnte aber ggf. auf die direkten felder umgestellt werden
		addField("virt.field.projekt.wertzeitverbleibend");
		addField("virt.field.projekt.verbrauchbestellwert");
		addField("virt.field.projekt.verbrauchsollstunden");
		
		emptyCache();
		Application.getLoaderBase().load(this, sql);
	}
	

	/**
	 * Laden von Abrufen in Abhängigkeit von ihrem Auftrag
	 * 
	 * @param auftragID AuftragID
	 * @throws Exception
	 */
	public void loadByAuftragID(int auftragID, boolean loadAll) throws Exception {
		loadByAuftragID(Format.getFormat0Nks(auftragID), loadAll);
	}


	/**
	 * Laden von Abrufen in Abhängigkeit von ihrem Auftrag
	 * 
	 * @param auftragID AuftragID, kann auch eine mit Komma getrennte Liste sein
	 * @param alle Abrufe laden, oder nur die mit Berechtigungen als PL
	 * @throws Exception
	 */
	public void loadByAuftragID(String auftragID, boolean loadAll) throws Exception {
		int projektleiterID;

		// wenn der aktuelle Benutzer Projektleiter ist, darf er nur seine eigenen Projekte laden
		projektleiterID = UserInformation.getInstance().getPersonIDAlsProjektleiter();

		emptyCache();
		Application.getLoaderBase().load(this, "AuftragID IN (" + auftragID + ") " 
				+ (!loadAll && projektleiterID > 0 ? " AND (ProjektleiterID=" + projektleiterID + " OR ProjektleiterID2=" + projektleiterID + ")" : ""),
				getSortFieldName());
	}


	/**
	 * Laden von Abrufen des Projektleiters
	 * 
	 * @param auftragID AuftragID, kann auch eine mit Komme getrennte Liste sein
	 * @throws Exception
	 */
	public void loadByProjektleiterID() throws Exception {
		int projektleiterID;

		// wenn der aktuelle Benutzer Projektleiter ist, darf er nur seine eigenen Projekte laden
		projektleiterID = UserInformation.getInstance().getPersonIDAlsProjektleiter();

		emptyCache();
		Application.getLoaderBase().load(this, (projektleiterID > 0 ? " (ProjektleiterID=" + projektleiterID + " OR ProjektleiterID2=" + projektleiterID + ")" : ""),
				getSortFieldName());
	}


	/**
	 * Laden von Abrufen in Abhängigkeit von ihrem Status und dem Auftrag
	 * 
	 * @param statusProjektID
	 * @param auftragID AuftragID oder 0
	 * @throws Exception
	 */
	public void loadByAuftragID_Status(int auftragID, int statusProjektID) throws Exception {
		loadByAuftragID_Status(auftragID > 0 ? Format.getFormat0Nks(auftragID) : null, statusProjektID);
	}
	

	/**
	 * Laden von Abrufen in Abhängigkeit von ihrem Status und dem Auftrag
	 * 
	 * @param statusProjektID
	 * @param AuftragID AuftragID oder 0
	 * @throws Exception
	 */
	public void loadByAuftragID_Status(String auftragID, int statusProjektID) throws Exception {
		int projektleiterID;
		String sql;
		
		// wenn der aktuelle Benutzer Projektleiter ist, darf er nur seine eigenen Projekte laden
		projektleiterID = UserInformation.getInstance().getPersonIDAlsProjektleiter();

		// SQL-Statement 
		sql = "SELECT * FROM " + getTableName() + " WHERE " + getWhereProjektStatus(TABLE_NAME, statusProjektID)
		+ (auftragID != null ? " AND AuftragID IN (" + auftragID + ") "  : "")
		+ (projektleiterID > 0 ? " AND ("
				+ "(" + TABLE_NAME + ".ProjektleiterID=" + projektleiterID // PL
				+ " OR " + TABLE_NAME + ".ProjektleiterID2=" + projektleiterID + ")"
				+ " OR " // oder MA als Bearbeiter eingetragen
				+ "(" 
				+ TABLE_NAME + ".ID IN (SELECT AuftragID FROM tblMitarbeiterProjekt WHERE PersonID=" + projektleiterID + ") "
				+ "OR " 
				+  CoAbruf.TABLE_NAME + ".ID IN (SELECT AbrufID FROM tblMitarbeiterProjekt WHERE PersonID=" + projektleiterID + "))"
				+ ")" : "")
		+ " ORDER BY " + getSortFieldName();

		emptyCache();
		Application.getLoaderBase().load(this, sql);
	}
	

	/**
	 * Doppelte Einträge mit gleicher Bezeichnung für den Auftrag und unterschiedlicher ID laden.
	 * 
	 * @param id
	 * @param abrufNr
	 * @param auftragID
	 * @throws Exception
	 */
	private void loadDoppelteEintraege(int id, String abrufNr, int auftragID) throws Exception {
		emptyCache();
		Application.getLoaderBase().load(this, "ID<>" + id + " AND AuftragID=" + auftragID + " AND AbrufNr='" + abrufNr + "'", getSortFieldName());
	}
	

	/**
	 * Laden der Items von Abrufen mit verschiedenen Filtermöglichkeiten
	 * 
	 * @param kundeID kundeID oder 0
	 * @param auftragID abrufID oder 0 
	 * @param kostenstelleID kostenstelleID oder 0
	 * @throws Exception
	 */
	public void loadItems(int kundeID, int auftragID, int kostenstelleID) throws Exception {
		String from, where, sql;
		
		
		// FROM
		from = " FROM " + getTableName();
		
		// über kundeID und/oder Auftrag
		if (kundeID != 0 || auftragID != 0)
		{
			from += " JOIN tblAuftrag au ON (" + TABLE_NAME + ".AuftragID = au.ID) ";
		}
		
		if (kostenstelleID != 0)// über Kostenstelle
		{
			from += " JOIN stblAbrufKostenstelle s ON (" + TABLE_NAME + ".ID = s.AbrufID) JOIN tblKostenstelle k ON (s.KostenstelleID = k.ID)";  
		}

		
		// WHERE
		where = ""
				+ (kundeID == 0 ? "" : " AND au.kundeID=" + kundeID) 
				+ (auftragID == 0 ? "" : " AND au.ID=" + auftragID) 
				+ (kostenstelleID == 0 ? "" : " AND k.ID=" + kostenstelleID)
				+ " AND " + TABLE_NAME + ".StatusID = " + CoStatusProjekt.STATUSID_LAUFEND;

		if (where.length() > 0)
		{
			where = " WHERE " + where.substring(4);
		}

		
		// SQL-Statement
		sql = "SELECT " + TABLE_NAME + ".ID, (AbrufNr + ', ' + ISNULL(" + TABLE_NAME + ".Beschreibung, '')) AS AbrufNr"  + from + where
				+ " ORDER BY " + getSortFieldName();
		
		emptyCache();
		Application.getLoaderBase().load(this, sql);
	}


	/**
	 * Laden der Items von Abrufen für die KGG-Stundenauswertung
	 * 
	 * @throws Exception
	 */
	public void loadItemsKGG() throws Exception {
		String from, where, sql;
		
		
		// FROM
		from = " FROM " + getTableName();

		// über Abruf und Kostenstelle
		from += " JOIN stblAbrufKostenstelle s ON (" + getTableName() + ".ID = s.AbrufID) JOIN tblKostenstelle k ON (s.KostenstelleID = k.ID)";  
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
		sql = "SELECT DISTINCT " + TABLE_NAME + ".*"
		+ from + where
		+ " ORDER BY " + getSortFieldName();
		
		emptyCache();
		Application.getLoaderBase().load(this, sql);
	}


	/**
	 * Projektmerkmal für den Abruf laden und in das Cacheobjekt eintragen
	 * 
	 * @throws Exception
	 */
	public void addProjektmerkmalAbruf() throws Exception {
//		int iProjektmerkmal;
		CoAbrufProjektmerkmal coAbrufProjektmerkmal = new CoAbrufProjektmerkmal();
		
		if (getField("virt.field.projekt.abrufprojektmerkmalid") == null)
		{
			addField("virt.field.projekt.abrufprojektmerkmalid");
		}
		
		if (moveFirst())
		{
			do
			{
				coAbrufProjektmerkmal.loadByAbrufID(getID());

				if (coAbrufProjektmerkmal.moveFirst())
				{
//					iProjektmerkmal = 1;
					do
					{
						getField("virt.field.projekt.abrufprojektmerkmalid" /*+ iProjektmerkmal++ + "id"*/).
						setValue(coAbrufProjektmerkmal.getProjektmerkmalID());
					} while (coAbrufProjektmerkmal.moveNext());
				}
			} while (moveNext());
		}
	}
	
	
	/**
	 * AbrufNr + Bezeichnung
	 * 
	 * @see pze.business.objects.AbstractCacheObject#getSortFieldName()
	 */
	protected String getSortFieldName() {
		return TABLE_NAME + ".AbrufNr, " + TABLE_NAME + ".Beschreibung";
	}
	
	
	public IField getFieldAuftragID() {
		return getField("field." + getTableName() + ".auftragid");
	}


	@Override
	public int getAuftragID() {
		return Format.getIntValue(getFieldAuftragID().getValue());
	}

	
	@Override
	public String getAuftragsNr() {
		return getFieldAuftragID().getDisplayValue();
	}


	public void setAuftragID(int auftragID) {
		getFieldAuftragID().setValue(auftragID);
	}


	private CoAuftrag getCoAuftrag() throws Exception {
		if (m_coAuftrag == null || m_coAuftrag.isEmpty())
		{
			m_coAuftrag = new CoAuftrag();
			m_coAuftrag.loadByID(getAuftragID());
		}

		return m_coAuftrag;
	}

	
	@Override
	public String getBestellNr() throws Exception{
		return getCoAuftrag().getBestellNr();
	}

	
	@Override
	public Date getDatumBestellung() throws Exception{
		return getCoAuftrag().getDatumBestellung();
	}


	@Override
	public int getKundeID() throws Exception {
		return getCoAuftrag().getKundeID();
	}


	@Override
	public String getKunde() throws Exception {
		return getCoAuftrag().getKunde();
	}

	
	public IField getFieldAbrufNr() {
		return getField("field." + getTableName() + ".abrufnr");
	}


	@Override
	public String getAbrufNr() {
		return Format.getStringValue(getFieldAbrufNr().getValue());
	}


	public void setAbrufNr(Object abrufNr) {
		getFieldAbrufNr().setValue(abrufNr);
	}


	public IField getFieldDatumAbruf() {
		return getField("field." + getTableName() + ".datumabruf");
	}


	public Date getDatumAbruf() {
		return Format.getDateValue(getFieldDatumAbruf().getValue());
	}


	public void setDatumAbruf(Object datumAbruf) {
		getFieldDatumAbruf().setValue(datumAbruf);
	}


	public IField getFieldRevision() {
		return getField("field." + getTableName() + ".revision");
	}


	public int getRevision() throws Exception {
		return Format.getIntValue(getFieldRevision().getValue());
	}


	public void setRevision(Object revision) {
		getFieldRevision().setValue(revision);
	}


	public IField getFieldFachgebiet() {
		return getField("field." + getTableName() + ".fachgebietid");
	}

	
	public int getFachgebietID() throws Exception {
		return Format.getIntValue(getFieldFachgebiet().getValue());
	}
	

	public void setFachgebietID(Object fachgebietID) {
		getFieldFachgebiet().setValue(fachgebietID);
	}


	public IField getFieldPNr() {
		return getField("field." + getTableName() + ".pnummerid");
	}

	
	public IField getFieldZuordnungID() {
		return getField("field." + getTableName() + ".zuordnungid");
	}

	
	public int getZuordnungID() throws Exception {
		return Format.getIntValue(getFieldZuordnungID().getValue());
	}
	

	public void setZuordnungID(Object zuordnungID) {
		getFieldZuordnungID().setValue(zuordnungID);
	}

	
	public IField getFieldPaketID() {
		return getField("field." + getTableName() + ".paketid");
	}

	
	public int getPaketID() throws Exception {
		return Format.getIntValue(getFieldPaketID().getValue());
	}
	

	public void setPaketID(Object paketID) {
		getFieldPaketID().setValue(paketID);
	}

	
	@Override
	public String getKey() {
		return "abruf." + getID();
	}
	
	
	/**
	 * Cacheobject mit den Projektmerkmalen des Abrufs
	 */
	public CoAbrufProjektmerkmal getCoAbrufProjektmerkmal() throws Exception {

		CoAbrufProjektmerkmal coAbrufProjektmerkmal;

		coAbrufProjektmerkmal = new CoAbrufProjektmerkmal();
		coAbrufProjektmerkmal.loadByAbrufID(getID());

		return coAbrufProjektmerkmal;

	}
	
	
	@Override
	public CoKostenstelle getCoKostenstelle() throws Exception {

		CoKostenstelle coKostenstelle;

		coKostenstelle = new CoKostenstelle();
		coKostenstelle.loadByAbrufID(getID());

		return coKostenstelle;

	}

	
	/**
	 * Prüft, dass keine Kostenstelle doppelt vorkommt
	 */
	@Override
	public String validate() throws Exception{
		int auftragID;
		String abrufNr, key;
		Set<String> setAuftragAbrufNr;
		CoAbruf coAbruf;
		
		if (!moveFirst())
		{
			return null;
		}

		// übergeordnet für Projekte
		super.validate();

		setAuftragAbrufNr = new HashSet<String>();
		coAbruf = new CoAbruf();
		
		// alle Einträge prüfen
		do
		{
			if (!isNew() && !isModified())
			{
				continue;
			}
			
			// prüfen, ob der Eintrag doppelt in der aktuellen Liste existiert
			abrufNr = getAbrufNr();
			auftragID = getAuftragID();
			key = abrufNr + "-" + auftragID;
			
			// prüfen, ob der Eintrag nochmal in der DB existiert
			coAbruf.loadDoppelteEintraege(getID(), abrufNr, getAuftragID());
			if (coAbruf.getRowCount() > 0 || setAuftragAbrufNr.contains(key))
			{
				return "Der Abruf \"" + abrufNr + "\" existiert bereits.";
			}
			
			// prüfen, ob die AuftragsNr geändert wurde, dann stimmt ggf. die Zuordnung im Monatseinsatzblatt nicht mehr
			if (!isNew() && Format.getIntValue(getFieldAuftragID().getOriginalValue()) != getAuftragID())
			{
				if (!Messages.showYesNoMessage("Auftrags-Nr. geändert", "Sie sind dabei die Auftrags-Nr. zu ändern. Bereits im Monatseinsatzblatt "
						+ "eingetragene Stunden bleiben dem bisherigen Auftrag zugeordnet und werden nicht automatisch auf den neuen Auftrag übertragen. <br>"
						+ "Möchten Sie fortfahren?"))
				{
					return "Abbruch wegen geänderter Auftrags-Nr.";
				}
			}

			setAuftragAbrufNr.add(key);
		} while (moveNext());
		
		return null;
	}


}
