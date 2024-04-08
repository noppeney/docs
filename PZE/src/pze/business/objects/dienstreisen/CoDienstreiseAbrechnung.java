package pze.business.objects.dienstreisen;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;

import framework.Application;
import framework.business.interfaces.fields.IField;
import pze.business.Format;
import pze.business.UserInformation;
import pze.business.objects.AbstractCacheObject;
import pze.business.objects.CoFirmenparameter;
import pze.business.objects.personen.CoBuchung;
import pze.business.objects.personen.CoKontowert;
import pze.business.objects.reftables.buchungen.CoBuchungsart;
import pze.business.objects.reftables.buchungen.CoOrtVorNachDienstreise;
import pze.business.objects.reftables.buchungen.CoStatusBuchung;
import pze.business.objects.reftables.buchungen.CoStatusGenehmigung;

/**
 * CacheObject für Kontowerte
 * 
 * @author Lisiecki
 *
 */
public class CoDienstreiseAbrechnung extends AbstractCacheObject {

	public static final String TABLE_NAME = "tbldienstreiseabrechnung";
	

	
	/**
	 * Kontruktor
	 */
	public CoDienstreiseAbrechnung() {
		super("table." + TABLE_NAME);
	
		addField("field.tbldienstreise.personid");
//		addField("field.tbldienstreise.ort");
//		addField("field.tbldienstreise.kunde");
		addField("field.tbldienstreise.kundeid");
		addField("field.tbldienstreise.uebernachtung");
		addField("field.tbldienstreise.privatpkw");
		addField("virt.field.dienstreise.statusiddr");
		
		addField("field.tblperson.zeitwohnortwti");
		addField("field.tblperson.kmwohnortwti");

		addField("virt.field.dienstreise.datum");
		addField("virt.field.dienstreise.uhrzeitasint");
		addField("virt.field.dienstreise.endeasint");
		
		addField("virt.field.dienstreise.arbeitszeitwti");
		addField("virt.field.dienstreise.projektzeit");
		addField("virt.field.dienstreise.reisezeit");
		addField("virt.field.dienstreise.anrechenbarereisezeit");
		addField("virt.field.dienstreise.anrechenbarearbeitszeit");
	}
	
	
	@Override
	public void loadByID(int id) throws Exception {
		load(id, 0, null);
	}
	

	public void loadByPersonID(int personID) throws Exception {
		load(0, personID, null);
	}


	public void loadByPersonID(int personID, Date datum) throws Exception {
		load(0, personID, datum);
	}

	
	@Override
	public void loadAll() throws Exception {
		load(0, 0, null);
	}
	

	/**
	 * Funktion zum Laden der Einträge mit diversen optionalen Parametern
	 * 
	 * @param id
	 * @param personID
	 * @param datum 
	 * @throws Exception
	 */
	private void load(int id, int personID, Date datum) throws Exception {
		GregorianCalendar gregDatum;
		String where, sql;
		
		gregDatum = Format.getGregorianCalendar(datum);

		where = " b.StatusID != " + CoStatusBuchung.STATUSID_UNGUELTIG;
		where += (id > 0 ? " AND d.ID=" + id : "")
				+ (personID > 0 ? " AND dr.PersonID=" + personID : "")
				+ (datum == null ? "" : " AND YEAR(b.Datum)=" + gregDatum.get(GregorianCalendar.YEAR) 
				+ " AND MONTH(b.Datum)=" + (gregDatum.get(GregorianCalendar.MONTH)+1))
				;

		sql = "SELECT d.*, dr.PersonID, dr.Ort, dr.Kunde, dr.KundeID, dr.PrivatPkw, dr.StatusID AS StatusIDDR, "
				+ " b.datum, b.uhrzeitasint, p.KmWohnortWti, p.ZeitWohnortWti "
				+ " FROM " + TABLE_NAME + " d"
				+ " JOIN tblDienstreise dr ON (d.BuchungID = dr.BuchungID) "
				+ " JOIN tblbuchung b ON (d.BuchungID = b.ID) "
				+ " JOIN tblPerson p ON (dr.PersonID = p.ID) "
				+ " WHERE " + where
//				+ (where.isEmpty() ? "" : " WHERE " + where.substring(4)) // erstes "AND" abschneiden
				+ " ORDER BY " + getSortFieldName();

		emptyCache();
		Application.getLoaderBase().load(this, sql);
		
		doAfterLoad();
	}


	/**
	 * Aufbereitung der Daten, z. B. Kunden-Felder zusammenführen und Zeiten berechnen
	 * 
	 * @throws Exception 
	 */
	private void doAfterLoad() throws Exception {
		
		if (!moveFirst())
		{
			return;
		}
		
		if (!isEditing())
		{
			begin();
		}

		// alle DR durchlaufen
		do
		{
			checkEnde();
			checkKunde();
			loadZeitWti();
			aktualisiereZeiten();

		} while (moveNext());
	}


	/**
	 * Ende der Dienstreise bestimmen
	 * 
	 * @throws Exception
	 */
	private void checkEnde() throws Exception {
		int buchungsartID;
		CoBuchung coBuchung;

		// Buchungen für den Tag holen
		coBuchung = new CoBuchung();
		coBuchung.loadNichtGeloescht(getPersonID(), getDatum());
		
		// nächste Buchung nach der DR holen
		coBuchung.moveToID(getBuchungID());

		do
		{
			// Buchung für DR-Ende suchen
			buchungsartID = coBuchung.getBuchungsartID();
			if (buchungsartID == CoBuchungsart.ID_ENDE_DIENSTGANG_DIENSTREISE 
					|| buchungsartID == CoBuchungsart.ID_KOMMEN 
					|| buchungsartID == CoBuchungsart.ID_GEHEN
					|| buchungsartID == CoBuchungsart.ID_FA
					|| buchungsartID == CoBuchungsart.ID_KRANK)
			{
				setEnde(coBuchung.getUhrzeitAsInt());
				break;
			}
		} while (coBuchung.moveNext());
	}


	/**
	 * wenn manuell kein Kunde eingetragen wurde, den ausgewählten übernehmen
	 * 
	 * @throws Exception
	 */
	private void checkKunde() throws Exception {
		String kunde;
		
		kunde = getKunde();
		if (kunde == null || kunde.isEmpty())
		{
			setKunde(getFieldKundeID().getDisplayValue());
			getFieldKunde().setState(statusUnchanged);
		}
	}


	/**
	 * Arbeitszeit bei WTI laden
	 * 
	 * @throws Exception
	 */
	private void loadZeitWti() throws Exception {
		CoKontowert coKontowert;

		// Kontowerte für den Tag holen
		coKontowert = new CoKontowert();
		coKontowert.load(getPersonID(), getDatum());
		
		// Arbeitszeit bei WTI speichern
		setZeitWti(coKontowert.getWertAnwesend());
	}

	
	/**
	 * Aktuelisiere alle zu berechnenden Zeiten
	 */
	public void aktualisiereZeiten() {
		setProjektzeit(berechneProjektzeit());
		setReisezeit(berechneReisezeit());
		setAnrechenbareArbeitszeit(berechneAnrechenbareArbeitszeit());
		setAnrechenbareReisezeit(berechneAnrechenbareReisezeit());
	}
	

	@Override
	public String getNavigationBitmap() {
		return "lib.accounting"; 
	}


	/**
	 * Nach Datum sortieren
	 * 
	 * @see pze.business.objects.AbstractCacheObject#getSortFieldName()
	 */
	@Override
	protected String getSortFieldName() {
		return "Datum, Uhrzeit";
	}
	

	private IField getFieldBuchungID() {
		return getField("field." + getTableName() + ".buchungid");
	}


	private void setBuchungID(int buchungID) {
		getFieldBuchungID().setValue(buchungID);
	}


	public int getBuchungID() {
		return Format.getIntValue(getFieldBuchungID());
	}


	@Override
	public IField getFieldDatum() {
		return getField("virt.field.dienstreise.datum");
	}


	public IField getFieldPersonID() {
		return getField("field.tbldienstreise.personid");
	}


	private IField getFieldKunde() {
		return getField("field.tbldienstreise.kunde");
	}


	private void setKunde(String kunde) {
		getFieldKunde().setValue(kunde);
	}


	private String getKunde() {
		return Format.getStringValue(getFieldKunde());
	}


	private IField getFieldKundeID() {
		return getField("field.tbldienstreise.kundeid");
	}


	private IField getFieldUebernachtung() {
		return getField("field.tbldienstreise.uebernachtung");
	}


	private boolean isUebernachtung() {
		return Format.getBooleanValue(getFieldUebernachtung().getValue());
	}


	private IField getFieldPrivatPkw() {
		return getField("field.tbldienstreise.privatpkw");
	}


	public boolean isPrivatPkw() {
		return Format.getBooleanValue(getFieldPrivatPkw());
	}


//	private int getKundeID() {
//		return Format.getIntValue(getFieldKundeID());
//	}
//
//
////	public void setKunde(String kunde) {
////		getFieldKunde().setValue(kunde);
////	}
//
//
//	private void setKundeID(int kundeID) {
//		getFieldKundeID().setValue(kundeID);
//	}


//	@Override
//	public Date getDatum() {
//		return Format.getDateValue(getField("virt.field.dienstreise.datum").getValue());
//	}


	private IField getFieldEnde() {
		return getField("virt.field.dienstreise.endeasint");
	}


	private void setEnde(int ende) {
		getFieldEnde().setValue(ende);
	}


	private IField getFieldBeginnDr() {
		return getField("virt.field.dienstreise.uhrzeitasint");
	}


	public int getBeginnDr() {
		return Format.getIntValue(getFieldBeginnDr());
	}


	private IField getFieldEndeDr() {
		return getField("virt.field.dienstreise.endeasint");
	}


	public int getEndeDr() {
		return Format.getIntValue(getFieldEndeDr());
	}


	private IField getFieldZeitWohnortWti() {
		return getField("field.tblperson.zeitwohnortwti");
	}


	private int getZeitWohnortWti() {
		return Format.getIntValue(getFieldZeitWohnortWti());
	}


	private IField getFieldKmWohnortWti() {
		return getField("field.tblperson.kmwohnortwti");
	}


	private int getKmWohnortWti() {
		return Format.getIntValue(getFieldKmWohnortWti());
	}


	/**
	 * KM für Abzug der Strecke Wohnort-WTI, je nach Ort vor und nach der Dienstreise
	 * 
	 * @return
	 */
	public int getKmAbzugWohnortWti() {
		int kmWohnortWti;
		
		kmWohnortWti = 0;
		
		// Hinfahrt 
		if (CoOrtVorNachDienstreise.isIdWohnort(getOrtVorherID()))
		{
			kmWohnortWti += getKmWohnortWti();
		}
		
		// Rückfahrt
		if (CoOrtVorNachDienstreise.isIdWohnort(getOrtNachherID()))
		{
			kmWohnortWti += getKmWohnortWti();
		}
		
		return kmWohnortWti;
	}


	/**
	 * Abrechenbare KM nach Abzug der Strecke Wohnort-WTI
	 * 
	 * @return
	 */
	public int getKmNachAbzugWohnortWti() {
		return Math.max(0, getKm() - getKmAbzugWohnortWti());
	}


	/**
	 * Pauschale für abrechenbare KM nach Abzug der Strecke Wohnort-WTI
	 * 
	 * @return
	 * @throws Exception 
	 */
	public double getKmPauschale() throws Exception {
		return getKmNachAbzugWohnortWti() * CoFirmenparameter.getInstance().getKmPauschale();
	}


	private IField getFieldKm() {
		return getField("field." + getTableName() + ".km");
	}


	public int getKm() {
		return Format.getIntValue(getFieldKm());
	}


	private IField getFieldErhaltenFruehstueck() {
		return getField("field." + getTableName() + ".erhaltenfruehstueck");
	}


	public boolean getErhaltenFruehstueck() {
		return Format.getBooleanValue(getFieldErhaltenFruehstueck());
	}


	private IField getFieldErhaltenMittagessen() {
		return getField("field." + getTableName() + ".erhaltenmittag");
	}


	public boolean getErhaltenMittagessen() {
		return Format.getBooleanValue(getFieldErhaltenMittagessen());
	}


	private IField getFieldErhaltenAbendessen() {
		return getField("field." + getTableName() + ".erhaltenabend");
	}


	public boolean getErhaltenAbendessen() {
		return Format.getBooleanValue(getFieldErhaltenAbendessen());
	}


	/**
	 * String mit Kürzeln F, M, A für die erhaltenen Mahlzeiten
	 * 
	 * @return
	 */
	public String getErhalteneMahlzeiten() {
		String mahlzeiten;
		
		mahlzeiten = "";
		
		if (getErhaltenFruehstueck())
		{
			mahlzeiten += ", F";
		}
		
		if (getErhaltenMittagessen())
		{
			mahlzeiten += ", M";
		}
		
		if (getErhaltenAbendessen())
		{
			mahlzeiten += ", A";
		}
		
		return (mahlzeiten.isEmpty() ? "" : mahlzeiten.substring(2));
	}


	/**
	 * Verpflegungspauschale abzgl. der erhaltenen Mahlzeiten
	 * 
	 * @return
	 * @throws Exception 
	 */
	public double getVerpflegungspauschale() throws Exception {// TODO RS Andy	
		int ende, dauerDr;
		double betrag;
		CoFirmenparameter coFirmenparameter;
		
		betrag = 0;
		coFirmenparameter = CoFirmenparameter.getInstance();
		
		// Dauer der DR prüfen
		ende = (isUebernachtung() ? 24*60 : getEndeDr());
		dauerDr = ende - getBeginnDr(); // prüfen mit Anfang/Ende
		if (dauerDr < 8*60)
		{
			betrag = 0;
		}
		else if (dauerDr < 24*60)
		{
			betrag = coFirmenparameter.getVerpflegungspauschale8h();
		}
		else
		{
			betrag = coFirmenparameter.getVerpflegungspauschale24h();
		}

		
		// Mahlzeiten abziehen
		if (getErhaltenFruehstueck())
		{
			betrag -= coFirmenparameter.getVerpflegungspauschaleFruestueck();
		}
		
		if (getErhaltenMittagessen())
		{
			betrag -= coFirmenparameter.getVerpflegungspauschaleMittag();
		}
		
		if (getErhaltenAbendessen())
		{
			betrag -= coFirmenparameter.getVerpflegungspauschaleAbend();
		}
		
		return Math.max(0, betrag);
	}


	private IField getFieldOrtVorherID() {
		return getField("field." + getTableName() + ".ortvorherid");
	}


	private int getOrtVorherID() {
		return Format.getIntValue(getFieldOrtVorherID());
	}


	private IField getFieldOrtNachherID() {
		return getField("field." + getTableName() + ".ortnachherid");
	}


	private int getOrtNachherID() {
		return Format.getIntValue(getFieldOrtNachherID());
	}

	
	private IField getFieldAnkunftKunde() {
		return getField("field." + getTableName() + ".ankunftkunde");
	}


	private int getAnkunftKunde() {
		return Format.getIntValue(getFieldAnkunftKunde());
	}


	private IField getFieldAbfahrtKunde() {
		return getField("field." + getTableName() + ".abfahrtkunde");
	}


	private int getAbfahrtKunde() {
		return Format.getIntValue(getFieldAbfahrtKunde());
	}


	private IField getFieldPauseKunde() {
		return getField("field." + getTableName() + ".pausekunde");
	}


	private int getPauseKunde() {
		return Format.getIntValue(getFieldPauseKunde());
	}


	private IField getFieldPauseReise() {
		return getField("field." + getTableName() + ".pausereise");
	}


	private int getPauseReise() {
		return Format.getIntValue(getFieldPauseReise());
	}


	private IField getFieldZeitWti() {
		return getField("virt.field.dienstreise.arbeitszeitwti");
	}


	private int getZeitWti() {
		return Format.getIntValue(getFieldZeitWti());
	}


	private void setZeitWti(int zeit) {
		getFieldZeitWti().setValue(zeit);
	}


	private IField getFieldProjektzeit() {
		return getField("virt.field.dienstreise.projektzeit");
	}


	private int getProjektzeit() {
		return Format.getIntValue(getFieldProjektzeit());
	}


	private void setProjektzeit(int zeit) {
		getFieldProjektzeit().setValue(zeit);
	}


	private IField getFieldReisezeit() {
		return getField("virt.field.dienstreise.reisezeit");
	}


	private int getReisezeit() {
		return Format.getIntValue(getFieldReisezeit());
	}


	private void setReisezeit(int zeit) {
		getFieldReisezeit().setValue(zeit);
	}


	private IField getFieldAnrechenbareReisezeit() {
		return getField("virt.field.dienstreise.anrechenbarereisezeit");
	}


	public int getAnrechenbareReisezeit() {
		return Format.getIntValue(getFieldAnrechenbareReisezeit());
	}


	private void setAnrechenbareReisezeit(int zeit) {
		getFieldAnrechenbareReisezeit().setValue(zeit);
	}


	private IField getFieldAnrechenbareArbeitszeit() {
		return getField("virt.field.dienstreise.anrechenbarearbeitszeit");
	}


	public int getAnrechenbareArbeitszeit() {
		return Format.getIntValue(getFieldAnrechenbareArbeitszeit());
	}


	private void setAnrechenbareArbeitszeit(int zeit) {
		getFieldAnrechenbareArbeitszeit().setValue(zeit);
	}


	/**
	 * Projektzeit beim Kunden 
	 * (Zeit vor Ort - Pause)
	 * 
	 * @return
	 */
	private int berechneProjektzeit() {
		int projektzeit;
		
		// Abfahrt - Ankunft - Pause beim Kunden
		projektzeit = getAbfahrtKunde() - getAnkunftKunde() - getPauseKunde();
		
		return Math.max(0, projektzeit);
	}


	/**
	 * Reise-/Fahrtzeit zum Kunde und zurück
	 * 
	 * @return
	 */
	private int berechneReisezeit() {
		int reisezeit, abfahrtKunde;
		
		// Hinfahrt + Rückfahrt
		abfahrtKunde = getAbfahrtKunde();
		reisezeit = Math.max(0, getAnkunftKunde() - getBeginnDr()) + (abfahrtKunde > 0 ? Math.max(0, getEndeDr() - abfahrtKunde) : 0);
		
		// abzgl. Pause bei der Reise
		reisezeit -= getPauseReise();
		
		return Math.max(0, reisezeit);
	}


	/**
	 * Reise-/Fahrtzeit zum Kunde und zurück abzgl. der Fahrt zwischen Wohnort und WTI,
	 * falls Ort vor oder nach DR der Wohnort ist
	 * 
	 * @return
	 */
	private int berechneReisezeitOhneWohnortWti() {
		int reisezeit;
		
		// normale Reisezeit
		reisezeit = getReisezeit();
		
		// ggf. Hinfahrt abziehen
		if (CoOrtVorNachDienstreise.isIdWohnort(getOrtVorherID()))
		{
			reisezeit -= getZeitWohnortWti();
		}
		
		// ggf. Rückfahrt abziehen
		if (CoOrtVorNachDienstreise.isIdWohnort(getOrtNachherID()))
		{
			reisezeit -= getZeitWohnortWti();
		}
		
		return Math.max(0, reisezeit);
	}


	/**
	 * Arbeitszeit durch die DR entspricht der Reisezeit ohne Wohnort/WTI + Projektzeit beim Kunden
	 * 
	 * @return
	 */
	private int berechneArbeitszeitDr() {
		int arbeitszeit;
		
		// Reisezeit ohne Fahrt Wohnort/WTI + Projektzeit
		arbeitszeit = berechneReisezeitOhneWohnortWti() + getProjektzeit();
		
		return Math.max(0, arbeitszeit);
	}

	
	/**
	 * Anrechenbare Reisezeit entspricht der anr. Arbeitszeit abzgl. Projektzeit und Zeit bei WTI
	 * @return
	 */
	private int berechneAnrechenbareReisezeit() {
		int anrechenbareReisezeit;
		
		anrechenbareReisezeit = getAnrechenbareArbeitszeit() - getProjektzeit() - getZeitWti();
		
		return Math.max(0, anrechenbareReisezeit);
	}
	
	
	/**
	 * Anrechenbare Arbeitszeit berechnen, dabei Überstunden etc. beachten
	 * 
	 * @return
	 */
	private int berechneAnrechenbareArbeitszeit() {
		int projektzeit, reisezeit, arbeitszeit, sollArbeitszeit, maxArbeitszeit, ueberstunden;
		double anrechenbareArbeitszeit, faktorUeberstunden;
		
		sollArbeitszeit = 7*60 + 48;
		maxArbeitszeit = 10*60;
		faktorUeberstunden = 0.75;
		
		projektzeit = getProjektzeit();
		reisezeit = getReisezeit();
		arbeitszeit = berechneArbeitszeitDr();
		ueberstunden = arbeitszeit - sollArbeitszeit;
		
		// Überstunden bei der Dienstreise
		if (ueberstunden > 0)
		{
			// wenn Projektzeit < Sollarbeitszeit -> Überstunden werden mit 75% angerechnet
			if (projektzeit < sollArbeitszeit)
			{
				anrechenbareArbeitszeit = sollArbeitszeit + ueberstunden*faktorUeberstunden;
			}
			else // wenn Projektzeit > Sollarbeitszeit -> Reisezeiten werden mit 75% angerechnet
			{
				anrechenbareArbeitszeit = projektzeit + reisezeit*faktorUeberstunden;
			}
		}
		else // keine Überstunden bei der Dienstreise
		{
			anrechenbareArbeitszeit = arbeitszeit;
		}
		
		// ggf. Zeit bei WTI dazuzählen
		anrechenbareArbeitszeit += getZeitWti();
		
		// max. Arbeitszeit darf nicht überschritten werden
		anrechenbareArbeitszeit = Math.min(maxArbeitszeit, anrechenbareArbeitszeit);
		
		// es muss mind. die Projektzeit gutgeschrieben werden
		anrechenbareArbeitszeit = Math.max(projektzeit, anrechenbareArbeitszeit);
		
		return Math.max(0, Format.getRunden0Nks(anrechenbareArbeitszeit));
	}

	
	/**
	 * Verpflegungspauschale abzgl. der erhaltenen Mahlzeiten
	 * 
	 * @return
	 * @throws Exception 
	 */
	public double getUebernachtungspauschale() throws Exception { // TODO RS Andy nur bei privater Übernachtung?
		return 0;
	}


	private IField getFieldKostenFahrt() {
		return getField("field." + getTableName() + ".kostenfahrt");
	}


	public double getKostenFahrt() {
		return Format.getDoubleValue(getFieldKostenFahrt());
	}


	private IField getFieldKostenNacht() {
		return getField("field." + getTableName() + ".kostennacht");
	}


	public double getKostenNacht() {
		return Format.getDoubleValue(getFieldKostenNacht());
	}


	private IField getFieldKostenSonstiges() {
		return getField("field." + getTableName() + ".kostensonstiges");
	}


	public double getKostenSonstiges() {
		return Format.getDoubleValue(getFieldKostenSonstiges());
	}


	/**
	 * Neuen Datensatz erstellen für die Person und den Tag.<br>
	 * Die Kontowerte für diesen Tag werden durch die Buchungen berechnet.
	 * 
	 * @param buchungID 
	 */
	public int createNew(int buchungID) throws Exception	{ // TODO Buchung oder DRID?
		
		// neuen Datensatz anlegen
		super.createNew();
		
//		setBuchungID(buchungID);
//		setPersonID(personID);
		setStatusID(CoStatusGenehmigung.STATUSID_GEPLANT);
		
		// Speichern, damit der Datensatz erstellt wird und direkt wieder in den Edit-Modus wechseln
		save();
		begin();
		
		return getID();
	}


	/**
	 * Nach dem Speichern auch die Verletzerliste speichern.<br>
	 * Funktioniert nicht, wenn mehrere Datensätze geladen sind.
	 * 
	 * @see framework.business.cacheobject.CacheObject#save()
	 */
	public void save() throws Exception{

		// TODO ggf. Listener für alle Felder im Dialog, um bei Änderungen direkt zu Aktualisieren
		setGeaendertVonID(UserInformation.getInstance().getPersonID());
		setGeaendertAm(Format.getStringMitUhrzeit(Format.getGregorianCalendar(null)));

		// Daten speichern
		super.save();
		
		// Verletzerliste speichern, wenn es nur einen Datensatz gibt
		// (wenn es mehrere gibt, sollte auch nicht gespeichert werden, sonst funktionieren auch andere Funktionen nicht)
		if (getRowCount() == 1)
		{
			// Wochenarbeitszeit prüfen, kann erst nach dem Speichern der Kontowerte gemacht werden
//			m_coVerletzerliste.checkArbeitszeitWoche(getWertArbeitszeitWoche());
			
			// bei Dienstreisen muss die Reisezeit angegeben sein, prüfen falls die Reisezeit eingegeben wurde
//			updateVerletzerlisteReisezeit();

			// Verletzerliste speichern
//			if (m_coVerletzerliste != null && m_coVerletzerliste.isEditing())
//			{
//				m_coVerletzerliste.save();
//			}
		}
	}


	/**
	 * Prüfung, ob alle Pflichtfelder für die Erstellung eines Dienstreiseantrags ausgefüllt sind
	 * 
	 * @return spezielle Fehlermeldungen oder null
	 * @throws Exception
	 */
	@Override
	public String validate() throws Exception{ // TODO Zeiten angeben

//		if (getZweck() == null)
//		{
//			return "Es wurde kein Zweck angegeben.";
//		}
		
//		if (isUebernachtung() && getHotel() == null)
//		{
//			return "Es wurde kein Hotel angegeben.";
//		}

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
		if (getDatum() == null)
		{
			return felder;
		}

		return super.appendPflichtfelderFehler(felder, schonGeprueft);
	}

//
//	/**
//	 * Zu der Zeile mit dem Tag wechseln
//	 * 
//	 * @param tag
//	 * @return
//	 */
//	public boolean moveToTag(int tag) {
//		return moveTo(getDatum(tag), getFieldDatum().getFieldDescription().getResID());
//	}
//
//
//	/**
//	 * Datum für den übergebenen Tag des Monats bestimmen
//	 * 
//	 * @param tagDesMonats
//	 * @return
//	 */
//	private Date getDatum(int tagDesMonats) {
//		Date datum;
//		GregorianCalendar gregDatum;
//		
//		datum = getDatum();
//		if (datum == null)
//		{
//			return null;
//		}
//		
//		gregDatum = new GregorianCalendar();
//		gregDatum.setTime(datum);
//		gregDatum.set(Calendar.DAY_OF_MONTH, tagDesMonats);
//		
//		return new Timestamp(gregDatum.getTimeInMillis());
//	}
//
//
//	private IField getVirtFieldTag() {
//		return getField("virt.field.tblkontowert.tag");
//	}
//
//
//	public int getVirtTag() {
//		return Format.getIntValue(getVirtFieldTag().getValue());
//	}
//
//
//	private IField getVirtFieldMonat() {
//		return getField("virt.field.tblkontowert.monat");
//	}
//
//
//	public int getVirtMonat() {
//		return Format.getIntValue(getVirtFieldMonat().getValue());
//	}
//

}
