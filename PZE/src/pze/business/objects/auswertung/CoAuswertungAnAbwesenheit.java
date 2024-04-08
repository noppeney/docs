package pze.business.objects.auswertung;

import framework.business.interfaces.fields.IField;
import pze.business.Format;

/**
 * CacheObject für die Einstellungen der Auswertung des An/Abwesenheit
 * 
 * @author Lisiecki
 *
 */
public class CoAuswertungAnAbwesenheit extends CoAuswertung {

	public static final String TABLE_NAME = "tblauswertunganabwesenheit";



	/**
	 * Kontruktor
	 */
	public CoAuswertungAnAbwesenheit() {
		super(TABLE_NAME);
	}
	

	/**
	 * Kontruktor
	 */
	public CoAuswertungAnAbwesenheit(String tableName) {
		super(tableName);
	}


	public IField getFieldWertDienstreiseAusgeben() {
		return getField("field." + getTableName() + ".wertdienstreiseausgeben");
	}


	public boolean isWertDienstreiseAusgebenAktiv() {
		return Format.getBooleanValue(getFieldWertDienstreiseAusgeben());
	}


//	public void setWertDienstreiseAusgeben(boolean isAktiv) {
//		getFieldWertDienstreiseAusgeben().setValue(isAktiv);
//	}


	public IField getFieldWertDienstgangAusgeben() {
		return getField("field." + getTableName() + ".wertdienstgangausgeben");
	}


	public boolean isWertDienstgangAusgebenAktiv() {
		return Format.getBooleanValue(getFieldWertDienstgangAusgeben());
	}


//	public void setWertDienstgangAusgeben(boolean isAktiv) {
//		getFieldWertDienstgangAusgeben().setValue(isAktiv);
//	}


	public IField getFieldWertReisezeitAusgeben() {
		return getField("field." + getTableName() + ".wertreisezeitausgeben");
	}


	public boolean isWertReisezeitAusgebenAktiv() {
		return Format.getBooleanValue(getFieldWertReisezeitAusgeben());
	}


//	public void setWertReisezeitAusgeben(boolean isAktiv) {
//		getFieldWertReisezeitAusgeben().setValue(isAktiv);
//	}


	public IField getFieldWertVorlesungAusgeben() {
		return getField("field." + getTableName() + ".wertvorlesungausgeben");
	}


	public boolean isWertVorlesungAusgebenAktiv() {
		return Format.getBooleanValue(getFieldWertVorlesungAusgeben());
	}


//	public void setWertVorlesungAusgeben(boolean isAktiv) {
//		getFieldWertVorlesungAusgeben().setValue(isAktiv);
//	}


	public IField getFieldWertOfaAusgeben() {
		return getField("field." + getTableName() + ".wertofaausgeben");
	}


	public boolean isWertOfaAusgebenAktiv() {
		return Format.getBooleanValue(getFieldWertOfaAusgeben());
	}

//
//	public void setWertOfaAusgeben(boolean isAktiv) {
//		getFieldWertOfaAusgeben().setValue(isAktiv);
//	}


	public IField getFieldAnzahlKrankAusgeben() {
		return getField("field." + getTableName() + ".anzahlkrankausgeben");
	}


	public boolean isAnzahlKrankAusgebenAktiv() {
		return Format.getBooleanValue(getFieldAnzahlKrankAusgeben());
	}


//	public void setAnzahlKrankAusgeben(boolean isAktiv) {
//		getFieldAnzahlKrankAusgeben().setValue(isAktiv);
//	}


	public IField getFieldAnzahlKrankOhneLfzAusgeben() {
		return getField("field." + getTableName() + ".anzahlkrankohnelfzausgeben");
	}


	public boolean isAnzahlKrankOhneLfzAusgebenAktiv() {
		return Format.getBooleanValue(getFieldAnzahlKrankOhneLfzAusgeben());
	}


//	public void setAnzahlKrankOhneLfzAusgeben(boolean isAktiv) {
//		getFieldAnzahlKrankOhneLfzAusgeben().setValue(isAktiv);
//	}


	public IField getFieldAnzahlUrlaubAusgeben() {
		return getField("field." + getTableName() + ".anzahlurlaubausgeben");
	}


	public boolean isAnzahlUrlaubAusgebenAktiv() {
		return Format.getBooleanValue(getFieldAnzahlUrlaubAusgeben());
	}


//	public void setAnzahlUrlaubAusgeben(boolean isAktiv) {
//		getFieldAnzahlUrlaubAusgeben().setValue(isAktiv);
//	}


	public IField getFieldAnzahlSonderurlaubAusgeben() {
		return getField("field." + getTableName() + ".anzahlsonderurlaubausgeben");
	}


	public boolean isAnzahlSonderurlaubAusgebenAktiv() {
		return Format.getBooleanValue(getFieldAnzahlSonderurlaubAusgeben());
	}


//	public void setAnzahlSonderurlaubAusgeben(boolean isAktiv) {
//		getFieldAnzahlSonderurlaubAusgeben().setValue(isAktiv);
//	}


	public IField getFieldAnzahlFaAusgeben() {
		return getField("field." + getTableName() + ".anzahlfaausgeben");
	}


	public boolean isAnzahlFaAusgebenAktiv() {
		return Format.getBooleanValue(getFieldAnzahlFaAusgeben());
	}


//	public void setAnzahlFaAusgeben(boolean isAktiv) {
//		getFieldAnzahlFaAusgeben().setValue(isAktiv);
//	}


	public IField getFieldAnzahlElternzeitAusgeben() {
		return getField("field." + getTableName() + ".anzahlelternzeitausgeben");
	}


	public boolean isAnzahlElternzeitAusgebenAktiv() {
		return Format.getBooleanValue(getFieldAnzahlElternzeitAusgeben());
	}


//	public void setAnzahlElternzeitAusgeben(boolean isAktiv) {
//		getFieldAnzahlElternzeitAusgeben().setValue(isAktiv);
//	}


}
