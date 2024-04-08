package pze.business.objects.dienstreisen;

import java.util.HashSet;

import framework.business.interfaces.fields.IField;
import pze.business.Format;
import pze.business.objects.AbstractCacheObject;
import pze.business.objects.personen.CoBuchung;
import pze.business.objects.reftables.CoLandDienstreise;

/**
 * CacheObject für Kontowerte
 * 
 * @author Lisiecki
 *
 */
public class CoDienstreise extends AbstractCacheObject {

	public static final String TABLE_NAME = "tbldienstreise";
	
	private static String m_hinweisLandOriginal;
	private static String m_hinweisArbeitszeitOriginal;

	
	/**
	 * Kontruktor
	 */
	public CoDienstreise() {
		super("table." + TABLE_NAME);
		
		addField("virt.field.dienstreise.datum");
		addField("virt.field.dienstreise.uhrzeitasint");
		addField("virt.field.dienstreise.endeasint");
	}
	
	
//	@Override
//	public void loadByID(int id) throws Exception {
//		load(id, 0, 0);
//	}
	

//	public void loadByPersonID(int personID) throws Exception {
//		load(0, personID, 0);
//	}


//	/*
//	 * Dienstreise für die übergebene BuchungsID laden
//	 */
//	public void loadByBuchungID(int buchungID) throws Exception {
//		load(0, 0, buchungID);
//	}

	
//	@Override
//	public void loadAll() throws Exception {
//		load(0, 0, 0);
//	}
	

//	/**
//	 * Funktion zum Laden der Einträge mit diversen optionalen Parametern
//	 * 
//	 * @param id
//	 * @param personID
//	 * @param buchungID
//	 * @throws Exception
//	 */
//	private void load(int id, int personID, int buchungID) throws Exception {
//		String where, sql;
//
//		where = " b.StatusID != " + CoStatusBuchung.STATUSID_UNGUELTIG;
//		where += (id > 0 ? " AND d.ID=" + id : "")
//				+ (personID > 0 ? " AND d.PersonID=" + personID : "")
//				+ (buchungID > 0 ? " AND d.BuchungID=" + buchungID : "")
//				;
//
//		sql = "SELECT d.*, b.datum, b.uhrzeitasint FROM " + TABLE_NAME + " d JOIN tblbuchung b ON (d.buchungID = b.ID) "
//				+ " WHERE " + where
////				+ (where.isEmpty() ? "" : " WHERE " + where.substring(4)) // erstes "AND" abschneiden
//				+ " ORDER BY " + getSortFieldName();
//
//		emptyCache();
//		Application.getLoaderBase().load(this, sql);
//		
////		doAfterLoad();
//	}


	/**
	 * DR-Ende anhand der Tagesbuchungen bestimmen und die beiden Kunden-Felder zusammenführen
	 * 
	 * @throws Exception 
	 */
//	private void doAfterLoad() throws Exception {
//		
//		if (!moveFirst())
//		{
//			return;
//		}
//		
//		if (!isEditing())
//		{
//			begin();
//		}
//
//		// alle DR durchlaufen
//		do
//		{
//			checkEnde();
////			checkKunde();
//
//		} while (moveNext());
//	}


//	/**
//	 * Ende der Dienstreise bestimmen
//	 * 
//	 * @throws Exception
//	 */
//	private void checkEnde() throws Exception {
//		int buchungsartID;
//		CoBuchung coBuchung;
//
//		// Buchungen für den Tag holen
//		coBuchung = new CoBuchung();
//		coBuchung.loadNichtGeloescht(getPersonID(), getDatum());
//		
//		// nächste Buchung nach der DR holen
//		coBuchung.moveToID(getBuchungID());
//
//		do
//		{
//			// Buchung für DR-Ende suchen
//			buchungsartID = coBuchung.getBuchungsartID();
//			if (buchungsartID == CoBuchungsart.ID_ENDE_DIENSTGANG_DIENSTREISE 
//					|| buchungsartID == CoBuchungsart.ID_KOMMEN 
//					|| buchungsartID == CoBuchungsart.ID_GEHEN
//					|| buchungsartID == CoBuchungsart.ID_FA
//					|| buchungsartID == CoBuchungsart.ID_KRANK)
//			{
//				setEnde(coBuchung.getUhrzeitAsInt());
//				break;
//			}
//		} while (coBuchung.moveNext());
//	}


	/**
	 * wenn manuell kein Kunde eingetragen wurde, den ausgewählten übernehmen
	 * 
	 * @throws Exception
	 */
//	private void checkKunde() throws Exception {
//		String kunde;
//		
//		kunde = getKunde();
//		if (kunde == null || kunde.isEmpty())
//		{
//			setKunde(getFieldKundeID().getDisplayValue());
//		}
//	}


	@Override
	public String getNavigationBitmap() {
		return "door.out"; 
	}


//	/**
//	 * Nach Datum sortieren
//	 * 
//	 * @see pze.business.objects.AbstractCacheObject#getSortFieldName()
//	 */
//	@Override
//	protected String getSortFieldName() {
//		return "Datum, Uhrzeit";
//	}
	
//
//	private IField getFieldBuchungID() {
//		return getField("field." + getTableName() + ".buchungid");
//	}
//
//
//	public void setBuchungID(int buchungID) {
//		getFieldBuchungID().setValue(buchungID);
//	}
//
//
//	public int getBuchungID() {
//		return Format.getIntValue(getFieldBuchungID());
//	}


//	@Override
//	public IField getFieldDatum() {
//		return getField("virt.field.dienstreise.datum");
//	}

//
//	public IField getFieldAnfang() {
//		return getField("virt.field.dienstreise.uhrzeitasint");
//	}
//
//
//	public int getAnfang() {
//		return Format.getIntValue(getFieldAnfang());
//	}
//
//
//	public IField getFieldEnde() {
//		return getField("virt.field.dienstreise.endeasint");
//	}
//
//
//	public int getEnde() {
//		return Format.getIntValue(getFieldEnde());
//	}


	private static String getResIdLandID() {
		return "field." + TABLE_NAME + ".landid";
	}


	private IField getFieldLandID() {
		return getField(getResIdLandID());
	}


	private void setLandID(int landID) {
		getFieldLandID().setValue(landID);
	}


	public int getLandID() {
		return Format.getIntValue(getFieldLandID());
	}


	public String getLand() {
		return getFieldLandID().getDisplayValue();
	}


	public static String getResIdZielID() {
		return "field." + TABLE_NAME + ".zielid";
	}


	public IField getFieldZielID() {
		return getField(getResIdZielID());
	}


	public int getZielID() {
		return Format.getIntValue(getFieldZielID());
	}


	public static String getResIdZiel() {
		return "field." + TABLE_NAME + ".ziel";
	}


	private IField getFieldZiel() {
		return getField(getResIdZiel());
	}


	private String getZielSonstige() {
		return Format.getStringValue(getFieldZiel());
	}


	/**
	 * ausgewähltes oder eingegebenes Ziel
	 * 
	 * @return
	 */
	public String getZiel() {
		String ziel;
		
		ziel = getZielSonstige();

		return (ziel == null || ziel.isEmpty() ? getFieldZielID().getDisplayValue() : ziel);
	}


	public static String getResIdZweckID() {
		return "field." + TABLE_NAME + ".zweckid";
	}


	private IField getFieldZweckID() {
		return getField(getResIdZweckID());
	}


	public String getZweck() {
		return Format.getStringValue(getFieldZweckID());
	}


	public static String getResIdThema() {
		return "field." + TABLE_NAME + ".thema";
	}


	private IField getFieldThema() {
		return getField(getResIdThema());
	}


//	public IField getFieldKunde() {
//		return getField("field." + getTableName() + ".kunde");
//	}
//
//
//	public void setKunde(String kunde) {
//		getFieldKunde().setValue(kunde);
//	}
//
//
//	public String getKunde() {
//		return Format.getStringValue(getFieldKunde());
//	}


	public IField getFieldKundeID() {
		return getField("field." + getTableName() + ".kundeid");
	}


	public int getKundeID() {
		return Format.getIntValue(getFieldKundeID());
	}


	public void setKundeID(int kundeID) {
		getFieldKundeID().setValue(kundeID);
	}


	public IField getFieldAuftragID() {
		return getField("field." + getTableName() + ".auftragid");
	}


	public int getAuftragID() {
		return Format.getIntValue(getFieldAuftragID());
	}


	public String getAuftragsNr() {
		return getFieldAuftragID().getDisplayValue();
	}


	public IField getFieldAbrufID() {
		return getField("field." + getTableName() + ".abrufid");
	}


	public int getAbrufID() {
		return Format.getIntValue(getFieldAbrufID());
	}


	public String getAbrufNr() {
		return getFieldAbrufID().getDisplayValue();
	}


	public IField getFieldKostenstelleID() {
		return getField("field." + getTableName() + ".kostenstelleid");
	}


	public int getKostenstelleID() {
		return Format.getIntValue(getFieldKostenstelleID());
	}


	public String getKostenstelle() {
		return getFieldKostenstelleID().getDisplayValue();
	}


	private IField getFieldUebernachtung() {
		return getField("field." + getTableName() + ".uebernachtung");
	}


	public boolean isUebernachtung() {
		return Format.getBooleanValue(getFieldUebernachtung());
	}


	private IField getFieldUebernachtungKunde() {
		return getField("field." + getTableName() + ".hotelueberkunde");
	}


	public boolean isUebernachtungKunde() {
		return Format.getBooleanValue(getFieldUebernachtungKunde());
	}


	private IField getFieldHotel() {
		return getField("field." + getTableName() + ".hotel");
	}


	public String getHotel() {
		return Format.getStringValue(getFieldHotel());
	}


	private IField getFieldMietwagen() {
		return getField("field." + getTableName() + ".mietwagen");
	}


	public boolean isMietwagen() {
		return Format.getBooleanValue(getFieldMietwagen());
	}


	private IField getFieldDienstwagen() {
		return getField("field." + getTableName() + ".dienstwagen");
	}


	public boolean isDienstwagen() {
		return Format.getBooleanValue(getFieldDienstwagen());
	}


	private IField getFieldPrivatPkw() {
		return getField("field." + getTableName() + ".privatpkw");
	}


	public boolean isPrivatPkw() {
		return Format.getBooleanValue(getFieldPrivatPkw());
	}


	private IField getFieldBahn() {
		return getField("field." + getTableName() + ".bahn");
	}


	public boolean isBahn() {
		return Format.getBooleanValue(getFieldBahn());
	}


	private IField getFieldTaxi() {
		return getField("field." + getTableName() + ".taxi");
	}


	public boolean isTaxi() {
		return Format.getBooleanValue(getFieldTaxi());
	}


	private IField getFieldFlugzeug() {
		return getField("field." + getTableName() + ".flugzeug");
	}


	public boolean isFlugzeug() {
		return Format.getBooleanValue(getFieldFlugzeug());
	}


//	public IField getFieldDienstwagenID() {
//		return getField("field." + getTableName() + ".dienstwagenid");
//	}
//
//
//	public int getDienstwagenID() {
//		return Format.getIntValue(getFieldDienstwagenID());
//	}
//
//
//	public String getDienstwagen() {
//		return getFieldDienstwagenID().getDisplayValue();
//	}


	private IField getFieldFahrtKunde() {
		return getField("field." + getTableName() + ".fahrtueberkunde");
	}


	private boolean isFahrtKunde() {
		return Format.getBooleanValue(getFieldFahrtKunde());
	}


	private IField getFieldMitfahrtBei() {
		return getField("field." + getTableName() + ".mitfahrt");
	}


	private String getMitfahrtBei() {
		return Format.getStringValue(getFieldMitfahrtBei());
	}


	/**
	 * Alle Beförderungsmittel mit Komma getrennt
	 * 
	 * @return
	 */
	public String getBefoerderungsmittel() {
		String befoederungsmittel;
		
		befoederungsmittel = "";
		
		if (isPrivatPkw())
		{
			befoederungsmittel += ", Privat-PKW";
		}
		
		if (isDienstwagen())
		{
			befoederungsmittel += ", Dienstwagen";
		}
		
		if (isMietwagen())
		{
			befoederungsmittel += ", Mietwagen";
		}
		
		if (isBahn())
		{
			befoederungsmittel += ", Bahn/ÖPNV";
		}
		
		if (isFlugzeug())
		{
			befoederungsmittel += ", Flugzeug";
		}
		
		if (isTaxi())
		{
			befoederungsmittel += ", Taxi";
		}
		
		if (isFahrtKunde())
		{
			befoederungsmittel += ", über Kunde";
		}
		
		if (getMitfahrtBei() != null && !getMitfahrtBei().isEmpty())
		{
			befoederungsmittel += ", " + getMitfahrtBei();
		}
		
		return (befoederungsmittel.isEmpty() ? "" : befoederungsmittel.substring(2));
	}


	private IField getFieldHinweiseGelesen() {
		return getField("field." + getTableName() + ".hinweisegelesen");
	}


	private boolean getHinweiseGelesen() {
		return Format.getBooleanValue(getFieldHinweiseGelesen());
	}


	private IField getFieldHinweise() {
		return getField("field." + getTableName() + ".hinweise");
	}


	private String getHinweise() {
		return Format.getStringValue(getFieldHinweise().getValue());
	}


	public boolean hasHinweise() {
		return getHinweise() != null && !getHinweise().trim().isEmpty();
	}


	private void setHinweise(String hinweise) {
		getFieldHinweise().setValue(hinweise);
	}


	/**
	 * Länderhinweise aktualisieren
	 *  
	 * @throws Exception 
	 */
	public void updateHinweisLand() throws Exception {
		String hinweise, hinweisLand;
		
		// aktuelle Hinweise
		hinweise = getHinweise();
		if (hinweise == null)
		{
			hinweise = "";
		}
		
		// vorherigen Hinweis bestimmen
		if (m_hinweisLandOriginal == null)
		{
			m_hinweisLandOriginal = CoLandDienstreise.getHinweise(Format.getIntValue(getFieldLandID().getOriginalValue()));
		}
		
		// alten Hinweis löschen
		if (m_hinweisLandOriginal != null && !m_hinweisLandOriginal.isEmpty())
		{
			hinweise = hinweise.replace(m_hinweisLandOriginal, "");
		}
		
		// aktueller Hinweis
		hinweisLand = CoLandDienstreise.getHinweise(getLandID());
		
		// neuen Hinweis hinzufügen
		if (hinweisLand != null && !hinweisLand.isEmpty())
		{
			hinweise = hinweisLand + "\n\n" + hinweise;
		}
		
		// Hinweise speichern
		setHinweise(hinweise.replaceAll("\n\n\n\n", "\n\n").replaceAll("\n\n\n", "\n\n"));
		m_hinweisLandOriginal = hinweisLand;
	}


	/**
	 * Hinweise zur Arbeitszeit aktualisieren
	 * 
	 * @throws Exception 
	 */
	public void updateHinweisArbeitszeit(CoBuchung coBuchung) throws Exception {
		String hinweise, hinweisArbeitszeit;
		
		// aktuelle Hinweise
		hinweise = getHinweise();
		if (hinweise == null)
		{
			hinweise = "";
		}
		
		// vorherigen Hinweis bestimmen
		if (m_hinweisArbeitszeitOriginal == null)
		{
//			m_hinweisArbeitszeitOriginal = CoLandDienstreise.getHinweise(Format.getIntValue(getFieldLandID().getOriginalValue()));
		}
		
		// alten Hinweis löschen
		if (m_hinweisArbeitszeitOriginal != null && !m_hinweisArbeitszeitOriginal.isEmpty())
		{
			hinweise = hinweise.replace(m_hinweisArbeitszeitOriginal, "");
		}
		
		// aktueller Hinweis
		hinweisArbeitszeit = createHinweisArbeitszeit(coBuchung);
		
		// neuen Hinweis hinzufügen
		if (hinweisArbeitszeit != null && !hinweisArbeitszeit.isEmpty())
		{
			hinweise = hinweisArbeitszeit + "\n\n" + hinweise;
		}
		
		// Hinweise speichern
		setHinweise(hinweise.replaceAll("\n\n\n\n", "\n\n").replaceAll("\n\n\n", "\n\n"));
		m_hinweisArbeitszeitOriginal = hinweisArbeitszeit;
	}


	/**
	 * Hinweise zur Einhaltung der Arbeitszeit erstellen
	 * 
	 * @param coBuchung
	 * @return
	 */
	private String createHinweisArbeitszeit(CoBuchung coBuchung) {
		int uhrzeitAnfang, uhrzeitEnde;
		boolean hinweisArbeitszeit, hinweisRuhezeit, hinweisRuhezeitNichtEigehalten;
		String hinweis;
		
		uhrzeitEnde = 0;
		hinweisRuhezeit = false;
		hinweisRuhezeitNichtEigehalten = false;
		hinweisArbeitszeit = false;
		hinweis = "";
		
		// Tage durchlaufen
		coBuchung.moveFirst();
		do
		{
			// Buchungen je nach Status berücksichtigen
			if (coBuchung.isAbgelehnt() || coBuchung.isGeloescht())
			{
				continue;
			}
			
			// Prüfung der Ruhezeit
			uhrzeitAnfang = coBuchung.getUhrzeitAsInt();
			if (uhrzeitAnfang + (24*60 - uhrzeitEnde) < 11*60)
			{
				hinweisRuhezeitNichtEigehalten = true;
			}

			// bei Ende ab 19 Uhr Hinweis auf Ruhezeit
			uhrzeitEnde = coBuchung.getUhrzeitBisAsInt();
			if (uhrzeitEnde >= 19*60)
			{
				hinweisRuhezeit = true;
			}
			
			// Arbeitszeit
			if ((uhrzeitEnde - uhrzeitAnfang) > 10*60)
			{
				hinweisArbeitszeit = true;
			}
		} while(coBuchung.moveNext());
		
		
		// Hinweis erzeugen
		if (hinweisArbeitszeit)
		{
			hinweis += "Bitte beachten Sie die gesetzliche Maximalarbeitszeit von 10 Stunden pro Tag.\n";
		}	
		if (hinweisRuhezeit)
		{
			hinweis += "Bitte beachten Sie die Einhaltung von mindestens 11 Stunden Ruhezeit nach Arbeitsende.\n";
		}
		if (hinweisRuhezeitNichtEigehalten)
		{
			hinweis += "Nach Ihrer aktuellen Planung wird die Ruhezeit von mindestens 11 Stunden nach Arbeitsende nicht eingehalten.\n";
		}

		return hinweis;
	}

	
	/**
	 * Hinweise zur Einhaltung der Arbeitszeit erstellen
	 * 
	 * @param coBuchung
	 */
	public void initHinweisArbeitszeit(CoBuchung coBuchung) {
		m_hinweisArbeitszeitOriginal = createHinweisArbeitszeit(coBuchung);
		
		// ggf. Hinweis eintragen beim ersten Öffnen
		if (getHinweise() == null && m_hinweisArbeitszeitOriginal != null)
		{
			setHinweise(m_hinweisArbeitszeitOriginal);
		}
	}
	
	
	public String getHinweisArbeitszeit() {
		return m_hinweisArbeitszeitOriginal;
	}
	

	private IField getFieldBemerkungAl() {
		return getField("field." + getTableName() + ".bemerkungal");
	}


	public String getBemerkungAl() {
		return Format.getStringValue(getFieldBemerkungAl().getValue());
	}


	public static String getResIdFieldAbgerechnet() {
		return "field." + TABLE_NAME + ".abgerechnet";
	}


	public static String getResIdFieldReisekostenabrechnung() {
		return "field." + TABLE_NAME + ".reisekostenabrechnung";
	}


	public static String getResIdFieldBemerkungBH() {
		return "field." + TABLE_NAME + ".bemerkungbh";
	}


//	public IField getFieldAbgerechnet() {
//		return getField("field." + getTableName() + ".abgerechnet");
//	}
//
//
//	public IField getFieldReisekostenabrechnung() {
//		return getField("field." + getTableName() + ".reisekostenabrechnung");
//	}
//
//
//	public IField getFieldBemerkungBH() {
//		return getField("field." + getTableName() + ".bemerkungbh");
//	}



//	/**
//	 * Die Buchung hat den Status ungültig
//	 * 
//	 * @return
//	 * @throws Exception
//	 */
//	public boolean isUngueltig() throws Exception {
//		return CoStatusGenehmigung.isUngueltig(getStatusID());
//	}
//
//
//	/**
//	 * Die Buchung hat den Status vorläufig
//	 * 
//	 * @return
//	 * @throws Exception
//	 */
//	public boolean isVorlaeufig() throws Exception {
//		return CoStatusGenehmigung.isGeplant(getStatusID());
//	}


	/**
	 * Neuen Datensatz erstellen für die Person.<br>
	 * 
	 * @param buchungID 
	 */
	public int createNew(int personID) throws Exception	{
		
		// neuen Datensatz anlegen
		super.createNew();
		
		setPersonID(personID);
		setLandID(CoLandDienstreise.STATUSID_DEUTSCHLAND);
		updateGeaendertVonAm();
		
		// Speichern, damit der Datensatz erstellt wird und direkt wieder in den Edit-Modus wechseln
		save();
		begin();
		
		return getID();
	}


	/**
	 * Prüfung, ob alle Pflichtfelder für die Erstellung eines Dienstreiseantrags ausgefüllt sind
	 * 
	 * @return spezielle Fehlermeldungen oder null
	 * @throws Exception
	 */
	@Override
	public String validate() throws Exception{
		String felder;
		
		felder = appendPflichtfelderFehler("", new HashSet<IField>());
		if(!felder.isEmpty())
		{
			return "Bitte füllen Sie alle Pflichtfelder aus.<br/> " +
					"Folgende Pflichtfelder fehlen noch:<br/>" + felder;
		}

		if (getZielID() == 0 && getZielSonstige() == null)
		{
			return "Es wurde kein Ziel angegeben.";
		}
		
		// Projekt, momentan nur Kunde Pflicht
//		if (getAuftragID() == 0 && getAbrufID() == 0 && getKostenstelleID() == 0)
//		{
//			return "Es wurde kein Projekt angegeben.";
//		}
		
		if (isUebernachtung() && (getHotel() == null && !isUebernachtungKunde()))
		{
			return "Es wurde kein Hotel angegeben.";
		}
		
		if (getBefoerderungsmittel().isEmpty())
//			if (!isDienstwagen() && !isMietwagen() && !isPrivatPkw() && !isBahn() && !isFlugzeug())
		{
			return "Es wurde kein Beförderungsmittel angegeben.";
		}
		
		if (hasHinweise() && !getHinweiseGelesen())
		{
			return "Die Hinweise wurden nicht als gelesen markiert.";
		}
		
		return null;
	}


	/**
	 * Erweitert die übergebene Liste der fehlenden Felder um die im akt. Cacheobjekt fehlenden
	 * 
	 * @param felder	Felder, die aus anderen COs Fehler haben
	 * @param schonGeprueft Liste der bereits geprüften Felder
	 * @return Felder, die als Pflichtfelder nicht ausgefüllt sind.
	 */
	@Override
	public String appendPflichtfelderFehler(String felder, HashSet<IField> schonGeprueft) {

		felder = checkPflichfeld(felder, schonGeprueft, getFieldLandID());
		felder = checkPflichfeld(felder, schonGeprueft, getFieldZweckID());
		felder = checkPflichfeld(felder, schonGeprueft, getFieldThema());
		felder = checkPflichfeld(felder, schonGeprueft, getFieldKundeID());
		
		return super.appendPflichtfelderFehler(felder, schonGeprueft);
	}


}
