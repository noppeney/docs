package pze.business.objects.projektverwaltung;

import java.util.Date;

import framework.Application;
import framework.business.interfaces.fields.IField;
import pze.business.Format;
import pze.business.objects.AbstractCacheObject;
import pze.business.objects.reftables.CoMessageQuittierung;
import pze.business.objects.reftables.CoStatusMessage;
import pze.business.objects.reftables.projektverwaltung.CoAusgabezeitraum;
import pze.business.objects.reftables.projektverwaltung.CoStatusProjekt;

/**
 * Abstraktes CacheObject für Projekte mit Methoden, die für Aufträge und Abrufe existieren
 * 
 * @author Lisiecki
 *
 */
public abstract class CoProjekt extends AbstractCacheObject {



	/**
	 * Kontruktor
	 * @param tableResID 
	 */
	public CoProjekt(String tableResID) {
		super(tableResID);
	}
	

	@Override
	public int createNew() throws Exception	{
		int id = super.createNew();
		
		// Projektverfolgung ist standardmäßig aktiviert
		setProjektverfolgungAktiv(true);
		setIntervallProjektverfolgungID(CoAusgabezeitraum.ID_MONATLICH);
		
		return id;
	}
	

	/**
	 * Laden aller Projekte zur Projektverfolgung
	 * 
	 * @throws Exception
	 */
	public void loadForProjektverfolgung() throws Exception {
		String where;

		// SQL-Statement
		where = "StatusID=" + CoStatusProjekt.STATUSID_LAUFEND + " AND ProjektverfolgungAktiv=1";

		emptyCache();
		Application.getLoaderBase().load(this, where, getSortFieldName());
	}


	/**
	 * Laden aller Projekte, bei denen die Termin/Kosten-Prognose der Projektverfolgung durch den PL aktuell offen ist
	 * 
	 * @throws Exception
	 */
	public void loadWhereProjektverfolgungPlOffen() throws Exception {
		String where;

		// SQL-Statement
		where = "StatusID=" + CoStatusProjekt.STATUSID_LAUFEND + " AND ProjektverfolgungAktiv=1"
				+ " AND ID IN (SELECT " + (isAuftrag() ? "Auftrag" : "Abruf") + "ID FROM tblMessage WHERE MeldungID=" 
				+ CoMessageQuittierung.ID_PROJEKTVERFOLGUNG_PROGNOSE_ERSTELLEN 
				+ " AND StatusID=" + CoStatusMessage.STATUSID_OFFEN + ")";

		emptyCache();
		Application.getLoaderBase().load(this, where, getSortFieldName());
	}


	/**
	 * Laden aller Projekte für die Erstellung der Prozentmeldung
	 * 
	 * @throws Exception
	 */
	public void loadForProzentmeldung() throws Exception {
		String id, where, sql;

		id = (isAuftrag() ? "Auftrag" : "Abruf") + "ID";
		
		where = " (VerbrauchBestellwert > (r1.Prozent/100.) " 
				+ " AND " + getTableName() + ".ID NOT IN "
				+ "(SELECT DISTINCT " + id + " FROM tblMessage WHERE " + id + " IS NOT NULL AND MeldungID=" + CoMessageQuittierung.ID_PROJEKT_PROZENTMELDUNG + ""
				// Meldung mit der %-Zahl prüfen, falls %-Zahl geändert wurde muss ggf. eine neue Meldung erzeugt werden
				+ " AND  CAST(SUBSTRING(Beschreibung, CHARINDEX('% des ', Beschreibung)-3, 3) AS INTEGER) > r1.Prozent))";


		sql = "SELECT * FROM " + getTableName() 
		+ " LEFT OUTER JOIN rtblProzentmeldung r1 ON (" + getTableName() + ".ProzentmeldungID=r1.ID) "
		+ " LEFT OUTER JOIN rtblProzentmeldung r2 ON (" + getTableName() + ".ProzentmeldungID2=r2.ID) "
		+ " OUTER APPLY funBudget" + (isAuftrag() ? "Auftrag" : "Abruf") + "(" + getTableName() + ".ID) "
		+ " WHERE StatusID=" + CoStatusProjekt.STATUSID_LAUFEND
		+ " AND (" + where + " OR " + where.replace("r1", "r2") + ")"
		+ " ORDER BY " + getSortFieldName();

		addField("virt.field.projekt.verbrauchbestellwert");

		emptyCache();
		Application.getLoaderBase().load(this, sql);
	}


	@Override
	public String getNavigationBitmap() {
		int statusID;
		
		statusID = getStatusID();
		switch (statusID)
		{
		case CoStatusProjekt.STATUSID_LAUFEND:
			return "page.green";
		case CoStatusProjekt.STATUSID_RUHEND:
			return "page.green";
		case CoStatusProjekt.STATUSID_ABGESCHLOSSEN:
			return "page.red";

		default:
			return "page.green";
		}
	}


	private String getFieldResID() {
		return "field." + getTableName() + ".";
	}


	private String getFieldResIDVirtual() {
		return "virt.field.projekt.";
	}

	
	public boolean isAuftrag(){
		return (this instanceof CoAuftrag);
	}
	
	
	public boolean isAbruf(){
		return (this instanceof CoAbruf);
	}
	

	/**
	 * prüfen, ob das aktuelle Projekt ein KGG-Abruf mit BerichtsNr ist
	 * 
	 * @throws Exception
	 */
	public boolean isKGG() throws Exception {
		CoAbruf coAbruf;
		
		if (isAuftrag())
		{
			return false;
		}
		
		// KGG-Abrufe laden
		coAbruf = new CoAbruf();
		coAbruf.loadItemsKGG();
		
		// prüfen, ob der aktuelle dabei ist
		return coAbruf.moveToID(getID());
	}


	public IField getFieldBeschreibung() {
		return getField(getFieldResID() + "beschreibung");
	}


	public String getBeschreibung() {
		return Format.getStringValue(getFieldBeschreibung().getValue());
	}


	public void setBeschreibung(Object beschreibung) {
		getFieldBeschreibung().setValue(beschreibung);
	}


	/**
	 * AuftragNr oder AbrufNr
	 * 
	 * @return
	 */
	public String getProjektNr() {
		if (isAbruf())
		{
			return getAbrufNr();
		}
		else
		{
			return getAuftragsNr();
		}
	}


	/**
	 * Bezeichnung aus Auftrags-/Abrufnummer und Beschreibung zusammensetzen
	 * 
	 * @see pze.business.objects.AbstractCacheObject#getBezeichnung()
	 */
	@Override
	public String getBezeichnung() {
		String projektNr, beschreibung;
		
		projektNr = getProjektNr();
		
		beschreibung = getBeschreibung();
		return projektNr + (beschreibung == null ? "" : " (" + beschreibung + ")");
	}


	public abstract int getAuftragID();


	public abstract String getAuftragsNr();


	public String getAbrufNr(){
		return null;
	}


	public IField getFieldEdvNr() {
		return getField(getFieldResID() + "edvnr");
	}


	public String getEdvNr() {
		return Format.getStringValue(getFieldEdvNr().getValue());
	}


	public void setEdvNr(Object edvNr) {
		getFieldEdvNr().setValue(edvNr);
	}


	public abstract String getBestellNr() throws Exception;
	
	
	public abstract Date getDatumBestellung() throws Exception;
	
	
	public abstract int getKundeID() throws Exception;

		
	public abstract String getKunde() throws Exception;


	public IField getFieldAbteilungKundeID() {
		return getField(getFieldResID() + "abteilungkundeid");
	}


	public int getAbteilungKundeID() {
		return Format.getIntValue(getFieldAbteilungKundeID().getValue());
	}


	public void setAbteilungKundeID(Object abteilungKundeID) {
		getFieldAbteilungKundeID().setValue(abteilungKundeID);
	}


	public String getAbteilungKunde() {
		return getFieldAbteilungKundeID().getDisplayValue();
	}


	public IField getFieldAnfordererKundeID() {
		return getField(getFieldResID() + "anfordererkundeid");
	}


	public int getAnfordererKundeID() {
		return Format.getIntValue(getFieldAnfordererKundeID().getValue());
	}


	public void setAnfordererKundeID(Object anfordererKundeID) {
		getFieldAnfordererKundeID().setValue(anfordererKundeID);
	}


	public String getAnfordererKunde() {
		return getFieldAnfordererKundeID().getDisplayValue();
	}


	public IField getFieldAbteilungsleiterID() {
		return getField("field." + getTableName() + ".abteilungsleiterid");
	}


	public int getAbteilungsleiterID() {
		return Format.getIntValue(getFieldAbteilungsleiterID().getValue());
	}


	public void setAbteilungsleiterID(Object abteilungsleiterID) {
		getFieldAbteilungsleiterID().setValue(abteilungsleiterID);
	}


	public IField getFieldProjektleiterID() {
		return getField(getFieldResID() + "projektleiterid");
	}


	public int getProjektleiterID() {
		return Format.getIntValue(getFieldProjektleiterID().getValue());
	}


	public void setProjektleiterID(Object projektleiterID) {
		getFieldProjektleiterID().setValue(projektleiterID);
	}


	public String getProjektleiter() {
		return getFieldProjektleiterID().getDisplayValue();
	}


	public IField getFieldProjektleiterID2() {
		return getField(getFieldResID() + "projektleiterid2");
	}


	public int getProjektleiterID2() {
		return Format.getIntValue(getFieldProjektleiterID2().getValue());
	}


	public void setProjektleiterID2(Object projektleiterID) {
		getFieldProjektleiterID2().setValue(projektleiterID);
	}


	public IField getFieldLiefertermin() {
		return getField(getFieldResID() + "datumtermin");
	}


	public Date getLiefertermin() {
		return Format.getDateValue(getFieldLiefertermin().getValue());
	}


	public void setLiefertermin(Object termin) {
		getFieldLiefertermin().setValue(termin);
	}


	public IField getFieldLieferterminOriginal() {
		return getField(getFieldResID() + "datumterminoriginal");
	}


	public void setLieferterminOriginal(Object termin) {
		getFieldLieferterminOriginal().setValue(termin);
	}


	public IField getFieldDatumFertigmeldung() {
		return getField(getFieldResID() + "datumfertigmeldung");
	}


	public Date getDatumFertigmeldung() {
		return Format.getDateValue(getFieldDatumFertigmeldung().getValue());
	}


	public IField getFieldDatumFreigabeRechnungAG() {
		return getField(getFieldResID() + "datumfreigaberechnungag");
	}


	public Date getDatumFreigabeRechnungAG() {
		return Format.getDateValue(getFieldDatumFreigabeRechnungAG());
	}


	public IField getFieldDatumBerechnetBis() {
		return getField(getFieldResID() + "datumberechnetbis");
	}


	public Date getDatumBerechnetBis() {
		return Format.getDateValue(getFieldDatumBerechnetBis().getValue());
	}


	public IField getFieldDatumMeldungVersendet() {
		return getField(getFieldResID() + "datummeldungversendet");
	}


	public Date getDatumMeldungVersendet() {
		return Format.getDateValue(getFieldDatumMeldungVersendet().getValue());
	}


	private IField getFieldBestellwert() {
		return getField(getFieldResID() + "bestellwert");
	}


	public int getBestellwert() {
		return Format.getIntValue(getFieldBestellwert());
	}


	public void setBestellwert(Object bestellwert) {
		getFieldBestellwert().setValue(bestellwert);
	}


	private IField getFieldBestellwertOriginal() {
		return getField(getFieldResID() + "bestellwertoriginal");
	}


	public void setBestellwertOriginal(Object bestellwert) {
		getFieldBestellwertOriginal().setValue(bestellwert);
	}


	private IField getFieldUvg() {
		return getField(getFieldResID() + "uvg");
	}


	public int getUvg() {
		return Format.getIntValue(getFieldUvg());
	}


	private IField getFieldPuffer() {
		return getField(getFieldResID() + "puffer");
	}


	public int getPuffer() {
		return Format.getIntValue(getFieldPuffer());
	}


	public void setPuffer(Object puffer) {
		getFieldPuffer().setValue(puffer);
	}


	private IField getFieldSollstunden() {
		return getField(getFieldResID() + "sollstunden");
	}


	public int getSollstunden() {
		return Format.getIntValue(getFieldSollstunden().getValue());
	}
	// setSollstunden geht nicht, da es ein berechnetes Feld ist


	private IField getFieldUeberbuchung() {
		return getField(getFieldResID() + "ueberbuchung");
	}

	
	public int getUeberbuchung() {
		return Format.getIntValue(getFieldUeberbuchung().getValue());
	}


	private IField getFieldStartwert() {
		return getField(getFieldResID() + "startwert");
	}

	
	public int getStartwert() {
		return Format.getIntValue(getFieldStartwert().getValue());
	}


//
//	public IField getFieldWertZeit() {
//		return getField(getFieldResIDVirtual() + "wertzeit");
//	}
//
//
//	public int getWertZeit() {
//		return Format.getIntValue(getFieldWertZeit().getValue());
//	}


	public IField getFieldIstStunden() {
		return getField(getFieldResIDVirtual() + "iststunden");
	}


	public int getIstStunden() {
		return Format.getIntValue(getFieldIstStunden().getValue());
	}


	public IField getFieldWertZeitVerbleibend() {
		return getField(getFieldResIDVirtual() + "wertzeitverbleibend");
	}


	public int getWertZeitVerbleibend() {
		return Format.getIntValue(getFieldWertZeitVerbleibend().getValue());
	}


	public IField getFieldVerbrauchBestellwert() {
		return getField(getFieldResIDVirtual() + "verbrauchbestellwert");
	}


	public double getVerbrauchBestellwert() {
		return Format.getDoubleValue(getFieldVerbrauchBestellwert().getValue());
	}


	public String getVerbrauchBestellwertInProzent() {
		return Format.getIntValue(getVerbrauchBestellwert() * 100) + "%";
	}


	public IField getFieldVerbrauchSollstunden() {
		return getField(getFieldResIDVirtual() + "verbrauchsollstunden");
	}


	public double getVerbrauchSollstunden() {
		return Format.getDoubleValue(getFieldVerbrauchSollstunden().getValue());
	}


	public String getVerbrauchSollstundenInProzent() {
		return Format.getIntValue(getVerbrauchSollstunden() * 100) + "%";
	}


	public IField getFieldIDBudgetJahresweise() {
		return getField("field." + getTableName() + ".idbudgetjahresweise");
	}


	public int getIDBudgetJahresweise() {
		return Format.getIntValue(getFieldIDBudgetJahresweise().getValue());
	}


	public void setIDBudgetJahresweise(int id) {
		getFieldIDBudgetJahresweise().setValue(id);
	}


	public IField getFieldProjektverfolgungAktiv() {
		return getField("field." + getTableName() + ".projektverfolgungaktiv");
	}


	public boolean isProjektverfolgungAktiv() {
		return Format.getBooleanValue(getFieldProjektverfolgungAktiv());
	}


	public void setProjektverfolgungAktiv(boolean isAktiv) {
		getFieldProjektverfolgungAktiv().setValue(isAktiv);
	}


	public IField getFieldProjektbericht() {
		return getField("field." + getTableName() + ".projektbericht");
	}


	public boolean isProjektbericht() {
		return Format.getBooleanValue(getFieldProjektbericht());
	}


	public IField getFieldIntervallProjektverfolgungID() {
		return getField("field." + getTableName() + ".intervallprojektverfolgungid");
	}


	public int getIntervallProjektverfolgungID() {
		return Format.getIntValue(getFieldIntervallProjektverfolgungID().getValue());
	}


	public void setIntervallProjektverfolgungID(Object intervallID) {
		getFieldIntervallProjektverfolgungID().setValue(intervallID);
	}


	public IField getFieldIntervallProjektberichtID() {
		return getField("field." + getTableName() + ".intervallprojektberichtid");
	}


	public int getIntervallProjektberichtID() {
		return Format.getIntValue(getFieldIntervallProjektberichtID().getValue());
	}


	public void setIntervallProjektberichtID(Object intervallID) {
		getFieldIntervallProjektberichtID().setValue(intervallID);
	}


	/**
	 * Zeit in Minuten
	 * 
	 * @return
	 */
	// TODO aus DB-Funktion abfragen
	public int getVerfuegbareStunden() {
		return getSollstunden() + getUeberbuchung() - getStartwert();
	}


	public IField getFieldAbrechnungsartID() {
		return getField(getFieldResID() + "abrechnungsartid");
	}


	public int getAbrechnungsartID() {
		return Format.getIntValue(getFieldAbrechnungsartID().getValue());
	}


	public String getAbrechnungsart() {
		return getFieldAbrechnungsartID().getDisplayValue();
	}


	@Override
	public IField getFieldStatusID() {
		return getField(getFieldResID() + "statusid");
	}


	@Override
	public int getStatusID() {
		return Format.getIntValue(getFieldStatusID().getValue());
	}


	public String getStatus() {
		return getFieldStatusID().getDisplayValue();
	}


	/**
	 * Cacheobject mit den Kostenstellen des Projekts
	 */
	public abstract CoKostenstelle getCoKostenstelle() throws Exception;
	

	/**
	 * WHERE-Teil eines SQL-Statements, um den Status von Abrufen abzufragen (ohne 'WHERE')
	 * @param tableName 
	 * 
	 * @param statusProjektID
	 * @return " table.StatusID = ..."
	 */
	public static String getWhereProjektStatus(String tableName, int statusProjektID) {
		return " (" + tableName + ".StatusID = " + statusProjektID + ") ";
//		return " (" + tableName + ".StatusID = " + CoStatusProjekt.STATUSID_LAUFEND 
//				+ " OR "+ tableName + ".StatusID = " + CoStatusProjekt.STATUSID_RUHEND + ") ";
	}

	
	@Override
	public String validate() throws Exception{
		
		if (!moveFirst())
		{
			return null;
		}

		// bei neuen Projekten termin und Bestellwert im Original speichern
		if (isNew())
		{
			setLieferterminOriginal(getLiefertermin());
			setBestellwertOriginal(getBestellwert());
		}
		
		// wenn Projektverfolgung aktiviert wurde, muss das Intervall angegeben werden
		if (isProjektverfolgungAktiv() && getIntervallProjektverfolgungID() == 0)
		{
			setIntervallProjektverfolgungID(CoAusgabezeitraum.ID_MONATLICH);
		}
		
		// wenn Projektbericht aktiviert wurde, muss das Intervall angegeben werden
		if (isProjektbericht() && getIntervallProjektberichtID() == 0)
		{
			setIntervallProjektberichtID(CoAusgabezeitraum.ID_MONATLICH);
		}
		
		return null;
	}
}
