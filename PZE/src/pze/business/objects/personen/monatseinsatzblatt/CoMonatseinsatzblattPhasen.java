package pze.business.objects.personen.monatseinsatzblatt;

import framework.Application;
import framework.business.interfaces.fields.IField;
import pze.business.Format;
import pze.business.objects.AbstractCacheObject;
import pze.business.objects.projektverwaltung.CoArbeitsplan;
import pze.business.objects.projektverwaltung.VirtCoProjekt;

/**
 * CacheObject für die Zuordnung von Stunden in Monatseinatzblättern zu Projektphasen.<br>
 * 
 * @author Lisiecki
 *
 */
public class CoMonatseinsatzblattPhasen extends AbstractCacheObject {

	public static final String TABLE_NAME = "tblmonatseinsatzblattphasen";



	/**
	 * Kontruktor
	 */
	public CoMonatseinsatzblattPhasen() {
		super("table." + TABLE_NAME);
	}
	

	/**
	 * Laden für den Eintrag im Monatseinsatzblatt
	 * 
	 * @param monatseinsatzblattID
	 * @throws Exception
	 */
	public void load(int monatseinsatzblattID, VirtCoProjekt virtCoProjekt) throws Exception {

		emptyCache();
		Application.getLoaderBase().load(this, "MonatseinsatzblattID=" + monatseinsatzblattID, getSortFieldName());
		
		// Phasen hinzufügen, die noch nicht enthalten sind
		addPhasen(monatseinsatzblattID, virtCoProjekt);
	}

	
	/**
	 * Anzahl Stunden der Phase
	 * 
	 * @param arbeitsplanID
	 * @throws Exception
	 */
	public void loadSummeStunden(int arbeitsplanID) throws Exception {
		String sql, where;
		
		// SQL-Abfrage zusammensetzen
		where = "ArbeitsplanID=" + arbeitsplanID;
		
		// Bezeichnung für die Spalte mit dem Zeitraum
		sql = "SELECT SUM(WertZeit) AS WertZeit"
				+ " FROM " + getTableName() + ""
				+ " WHERE " + where;

		emptyCache();
		Application.getLoaderBase().load(this, sql);
	}
	

	/**
	 * Phasen hinzufügen, die noch nicht im aktuellen CO enthalten sind
	 * 
	 * @param monatseinsatzblattID
	 * @param virtCoProjekt
	 * @throws Exception
	 */
	private void addPhasen(int monatseinsatzblattID, VirtCoProjekt virtCoProjekt) throws Exception {
		int arbeitsplanID;
		CoArbeitsplan coArbeitsplan;
		
		coArbeitsplan = new CoArbeitsplan(null);
		coArbeitsplan.loadByProjekt(virtCoProjekt);
		
		if (!coArbeitsplan.moveFirst())
		{
			return;
		}
		
		// Phasen durchlaufen
		begin();
		do
		{
			arbeitsplanID = coArbeitsplan.getID();
			if (coArbeitsplan.isPhase() && !moveToArbeitsplan(arbeitsplanID))
			{
				createNew();
				setMonatseinsatzblattID(monatseinsatzblattID);
				setArbeitsplanID(arbeitsplanID);
//				setWertZeit(0);
			}
		} while (coArbeitsplan.moveNext());
		save();
		
		sortDisplayValue(getResIdArbeitsplanID(), false);
	}


//	/**
//	 * Laden für den Eintrag im Monatseinsatzblatt
//	 * 
//	 * @param monatseinsatzblattID
//	 * @throws Exception
//	 */
//	public void load(int monatseinsatzblattID) throws Exception {
//		emptyCache();
//		Application.getLoaderBase().load(this, "MonatseinsatzblattID=" + monatseinsatzblattID, getSortFieldName());
//	}


	private IField getFieldMonatseinsatzblattID() {
		return getField("field." + getTableName() + ".monatseinsatzblattid");
	}


	private void setMonatseinsatzblattID(int monatseinsatzblattID) {
		getFieldMonatseinsatzblattID().setValue(monatseinsatzblattID);
	}


	public int getMonatseinsatzblattID() {
		return Format.getIntValue(getFieldMonatseinsatzblattID().getValue());
	}


	public static String getResIdArbeitsplanID() {
		return "field." + TABLE_NAME + ".arbeitsplanid";
	}


	private IField getFieldArbeitsplanID() {
		return getField(getResIdArbeitsplanID());
	}


	private void setArbeitsplanID(int arbeitsplanID) {
		getFieldArbeitsplanID().setValue(arbeitsplanID);
	}


	private int getArbeitsplanID() {
		return Format.getIntValue(getFieldArbeitsplanID().getValue());
	}

	
	private IField getFieldWertZeit() {
		return getField("field." + getTableName() + ".wertzeit");
	}


	public int getWertZeit() {
		return Format.getIntValue(getFieldWertZeit().getValue());
	}


	private void setWertZeit(Integer zeit) {
		getFieldWertZeit().setValue(zeit);
	}


//	private IField getFieldTaetigkeit() {
//		return getField("field." + getTableName() + ".taetigkeit");
//	}
//
//
//	private String getTaetigkeit() {
//		return Format.getStringValue(getFieldTaetigkeit().getValue());
//	}
//
//
//	private void setTaetigkeit(String taetigkeit) {
//		getFieldTaetigkeit().setValue(taetigkeit);
//	}


	/**
	 * Zum Datensatz mit den übergebenen Projekt springen
	 * 
	 * @param virtCoProjekt
	 * @return Datensatz vorhanden
	 * @throws Exception 
	 */
	private boolean moveToArbeitsplan(int arbeitsplanID) throws Exception {
		return moveTo(arbeitsplanID, getFieldArbeitsplanID().getFieldDescription().getResID());
	}


}
