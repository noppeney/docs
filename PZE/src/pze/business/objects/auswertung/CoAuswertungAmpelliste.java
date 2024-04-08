package pze.business.objects.auswertung;

import framework.business.interfaces.fields.IField;
import pze.business.Format;

/**
 * CacheObject für die Einstellungen der Auswertung der Ampelliste
 * 
 * @author Lisiecki
 *
 */
public class CoAuswertungAmpelliste extends CoAuswertung {

	public static final String TABLE_NAME = "tblauswertungampelliste";



	/**
	 * Kontruktor
	 */
	public CoAuswertungAmpelliste() {
		super(TABLE_NAME);
	}
	
	
	/**
	 * Kontruktor
	 */
	public CoAuswertungAmpelliste(String tableName) {
		super(tableName);
	}


	public IField getFieldAuftraegeAusgeben() {
		return getField("field." + getTableName() + ".auftraegeausgeben");
	}


	public boolean isAuftraegeAusgebenAktiv() {
		return Format.getBooleanValue(getFieldAuftraegeAusgeben().getValue());
	}


	public void setAuftraegeAusgeben(boolean isAktiv) {
		getFieldAuftraegeAusgeben().setValue(isAktiv);
	}


	public IField getFieldAbrufeAusgeben() {
		return getField("field." + getTableName() + ".abrufeausgeben");
	}


	public boolean isAbrufeAusgebenAktiv() {
		return Format.getBooleanValue(getFieldAbrufeAusgeben().getValue());
	}


	public void setAbrufeAusgeben(boolean isAktiv) {
		getFieldAbrufeAusgeben().setValue(isAktiv);
	}


	public IField getFieldAuftragID() {
		return getField("field." + getTableName() + ".auftragid");
	}


	public int getAuftragID() {
		return Format.getIntValue(getFieldAuftragID().getValue());
	}


	public IField getFieldAbrufID() {
		return getField("field." + getTableName() + ".abrufid");
	}


	public int getKostenstelleID() {
		return Format.getIntValue(getFieldKostenstelleID().getValue());
	}


	public IField getFieldKostenstelleID() {
		return getField("field." + getTableName() + ".kostenstelleid");
	}


	public int getAbrufID() {
		return Format.getIntValue(getFieldAbrufID().getValue());
	}


	public IField getFieldBestellNr() {
		return getField("field." + getTableName() + ".bestellnr");
	}


	public String getBestellNr() {
		return Format.getStringValue(getFieldBestellNr());
	}


	public IField getFieldKundeID() {
		return getField("field." + getTableName() + ".kundeid");
	}


	public int getKundeID() {
		return Format.getIntValue(getFieldKundeID().getValue());
	}


	public IField getFieldAbteilungKundeID() {
		return getField("field." + getTableName() + ".abteilungkundeid");
	}


	public int getAbteilungKundeID() {
		return Format.getIntValue(getFieldAbteilungKundeID().getValue());
	}


	public IField getFieldAnfordererKundeID() {
		return getField("field." + getTableName() + ".anfordererkundeid");
	}


	public int getAnfordererKundeID() {
		return Format.getIntValue(getFieldAnfordererKundeID().getValue());
	}


	public IField getFieldProjektleiterID() {
		return getField("field." + getTableName() + ".projektleiterid");
	}


	public int getProjektleiterID() {
		return Format.getIntValue(getFieldProjektleiterID().getValue());
	}


	public void setProjektleiterID(int projektleiterID) {
		getFieldProjektleiterID().setValue(projektleiterID);
	}


	public IField getFieldAbteilungsleiterID() {
		return getField("field." + getTableName() + ".abteilungsleiterid");
	}


	public int getAbteilungsleiterID() {
		return Format.getIntValue(getFieldAbteilungsleiterID().getValue());
	}


	public void setAbteilungsleiterID(int abteilungsleiterID) {
		getFieldAbteilungsleiterID().setValue(abteilungsleiterID);
	}


	public IField getFieldFachgebietID() {
		return getField("field." + getTableName() + ".fachgebietid");
	}


	public int getFachgebietID() {
		return Format.getIntValue(getFieldFachgebietID().getValue());
	}


	public IField getFieldAbrechnungsartID() {
		return getField("field." + getTableName() + ".abrechnungsartid");
	}


	public int getAbrechnungsartID() {
		return Format.getIntValue(getFieldAbrechnungsartID().getValue());
	}


//	public IField getFieldStatusID() {
//		return getField("field." + getTableName() + ".statusid");
//	}
//
//
//	public int getStatusID() {
//		return Format.getIntValue(getFieldStatusID().getValue());
//	}


//	public IField getFieldID() {
//		return getField("field." + getTableName() + ".");
//	}
//
//
//	public int getID() {
//		return Format.getIntValue(getFieldAuftragID().getValue());
//	}


}
