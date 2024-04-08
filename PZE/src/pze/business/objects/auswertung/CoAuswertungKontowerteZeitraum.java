package pze.business.objects.auswertung;

import framework.business.interfaces.fields.IField;
import pze.business.Format;
import pze.business.objects.reftables.personen.CoStatusAktivInaktiv;

/**
 * CacheObject für die Einstellungen der Auswertung der Kontowerte (Zeitraum)
 * 
 * @author Lisiecki
 *
 */
public class CoAuswertungKontowerteZeitraum extends CoAuswertung {

	public static final String TABLE_NAME = "tblauswertungkontowertezeitraum";



	/**
	 * Kontruktor
	 */
	public CoAuswertungKontowerteZeitraum() {
		super(TABLE_NAME);
	}
	
	
	/**
	 * bei neuen Einträgen nur aktive MA
	 * 
	 * @see pze.business.objects.AbstractCacheObject#createNew()
	 */
	@Override
	public int createNew(int userID) throws Exception	{
		int id;
		
		id = super.createNew(userID);
		
		// aktive Personen
		setStatusAktivInaktiv(CoStatusAktivInaktiv.STATUSID_AKTIV);
		
		return id;
	}
	

	public IField getFieldAnzahlArbeitstage() {
		return getField("field." + getTableName() + ".anzahlarbeitstageausgeben");
	}


	public boolean isAnzahlArbeitstageAusgebenAktiv() {
		return Format.getBooleanValue(getFieldAnzahlArbeitstage().getValue());
	}


	public void setAnzahlArbeitstageAusgeben(boolean isAktiv) {
		getFieldAnzahlArbeitstage().setValue(isAktiv);
	}


	public IField getFieldWertSollarbeitszeitAusgeben() {
		return getField("field." + getTableName() + ".wertsollarbeitszeitausgeben");
	}


	public boolean isWertSollarbeitszeitAusgebenAktiv() {
		return Format.getBooleanValue(getFieldWertSollarbeitszeitAusgeben().getValue());
	}


	public void setWertSollarbeitszeitAusgeben(boolean isAktiv) {
		getFieldWertSollarbeitszeitAusgeben().setValue(isAktiv);
	}


	public IField getFieldWertArbeitszeitAusgeben() {
		return getField("field." + getTableName() + ".wertarbeitszeitausgeben");
	}


	public boolean isWertArbeitszeitAusgebenAktiv() {
		return Format.getBooleanValue(getFieldWertArbeitszeitAusgeben().getValue());
	}


	public void setWertArbeitszeitAusgeben(boolean isAktiv) {
		getFieldWertArbeitszeitAusgeben().setValue(isAktiv);
	}


	public IField getFieldWertUeberstundenAusgeben() {
		return getField("field." + getTableName() + ".wertueberstundenausgeben");
	}


	public boolean isWertUeberstundenAusgebenAktiv() {
		return Format.getBooleanValue(getFieldWertUeberstundenAusgeben().getValue());
	}


	public void setWertUeberstundenAusgeben(boolean isAktiv) {
		getFieldWertUeberstundenAusgeben().setValue(isAktiv);
	}


	public IField getFieldAenderungGleitzeitkontoAusgeben() {
		return getField("field." + getTableName() + ".aenderunggleitzeitkontoausgeben");
	}


	public boolean isAenderungGleitzeitkontoAusgebenAktiv() {
		return Format.getBooleanValue(getFieldAenderungGleitzeitkontoAusgeben().getValue());
	}


	public void setAenderungGleitzeitkontoAusgeben(boolean isAktiv) {
		getFieldAenderungGleitzeitkontoAusgeben().setValue(isAktiv);
	}


	public IField getFieldWertPlusstundenAusgeben() {
		return getField("field." + getTableName() + ".wertplusstundenausgeben");
	}


	public boolean isWertPlusstundenAusgebenAktiv() {
		return Format.getBooleanValue(getFieldWertPlusstundenAusgeben().getValue());
	}


	public void setWertPlusstundenAusgeben(boolean isAktiv) {
		getFieldWertPlusstundenAusgeben().setValue(isAktiv);
	}


	public IField getFieldWertPlusstundenProjektAusgeben() {
		return getField("field." + getTableName() + ".wertplusstundenprojektausgeben");
	}


	public boolean isWertPlusstundenProjektAusgebenAktiv() {
		return Format.getBooleanValue(getFieldWertPlusstundenProjektAusgeben().getValue());
	}


	public void setWertPlusstundenProjektAusgeben(boolean isAktiv) {
		getFieldWertPlusstundenProjektAusgeben().setValue(isAktiv);
	}


	public IField getFieldWertPlusstundenReiseAusgeben() {
		return getField("field." + getTableName() + ".wertplusstundenreiseausgeben");
	}


	public boolean isWertPlusstundenReiseAusgebenAktiv() {
		return Format.getBooleanValue(getFieldWertPlusstundenReiseAusgeben().getValue());
	}


	public void setWertPlusstundenReiseAusgeben(boolean isAktiv) {
		getFieldWertPlusstundenReiseAusgeben().setValue(isAktiv);
	}


	public IField getFieldWertMinusstundenAusgeben() {
		return getField("field." + getTableName() + ".wertminusstundenausgeben");
	}


	public boolean isWertMinusstundenAusgebenAktiv() {
		return Format.getBooleanValue(getFieldWertMinusstundenAusgeben().getValue());
	}


	public void setWertMinusstundenAusgeben(boolean isAktiv) {
		getFieldWertMinusstundenAusgeben().setValue(isAktiv);
	}


	public IField getFieldWertAuszahlungProjektstundenAusgeben() {
		return getField("field." + getTableName() + ".wertauszahlungplusstundenprojektausgeben");
	}


	public boolean isWertAuszahlungProjektstundenAusgebenAktiv() {
		return Format.getBooleanValue(getFieldWertAuszahlungProjektstundenAusgeben().getValue());
	}


	public void setWertAuszahlungProjektstunden(boolean isAktiv) {
		getFieldWertAuszahlungProjektstundenAusgeben().setValue(isAktiv);
	}


	public IField getFieldWertAuszahlungReisestundenAusgeben() {
		return getField("field." + getTableName() + ".wertauszahlungplusstundenreiseausgeben");
	}


	public boolean isWertAuszahlungReisestundenAusgebenAktiv() {
		return Format.getBooleanValue(getFieldWertAuszahlungReisestundenAusgeben().getValue());
	}


	public void setWertAuszahlungReisestunden(boolean isAktiv) {
		getFieldWertAuszahlungReisestundenAusgeben().setValue(isAktiv);
	}


	public IField getFieldWertAnwesendAusgeben() {
		return getField("field." + getTableName() + ".wertanwesendausgeben");
	}


	public boolean isWertAnwesendAusgebenAktiv() {
		return Format.getBooleanValue(getFieldWertAnwesendAusgeben().getValue());
	}


	public void setWertAnwesendAusgeben(boolean isAktiv) {
		getFieldWertAnwesendAusgeben().setValue(isAktiv);
	}


	public IField getFieldWertDienstreiseAusgeben() {
		return getField("field." + getTableName() + ".wertdienstreiseausgeben");
	}


	public boolean isWertDienstreiseAusgebenAktiv() {
		return Format.getBooleanValue(getFieldWertDienstreiseAusgeben().getValue());
	}


	public void setWertDienstreiseAusgeben(boolean isAktiv) {
		getFieldWertDienstreiseAusgeben().setValue(isAktiv);
	}


	public IField getFieldWertDienstgangAusgeben() {
		return getField("field." + getTableName() + ".wertdienstgangausgeben");
	}


	public boolean isWertDienstgangAusgebenAktiv() {
		return Format.getBooleanValue(getFieldWertDienstgangAusgeben().getValue());
	}


	public void setWertDienstgangAusgeben(boolean isAktiv) {
		getFieldWertDienstgangAusgeben().setValue(isAktiv);
	}


	public IField getFieldWertReisezeitAusgeben() {
		return getField("field." + getTableName() + ".wertreisezeitausgeben");
	}


	public boolean isWertReisezeitAusgebenAktiv() {
		return Format.getBooleanValue(getFieldWertReisezeitAusgeben().getValue());
	}


	public void setWertReisezeitAusgeben(boolean isAktiv) {
		getFieldWertReisezeitAusgeben().setValue(isAktiv);
	}


	public IField getFieldWertVorlesungAusgeben() {
		return getField("field." + getTableName() + ".wertvorlesungausgeben");
	}


	public boolean isWertVorlesungAusgebenAktiv() {
		return Format.getBooleanValue(getFieldWertVorlesungAusgeben().getValue());
	}


	public void setWertVorlesungAusgeben(boolean isAktiv) {
		getFieldWertVorlesungAusgeben().setValue(isAktiv);
	}


	public IField getFieldWertPauseAusgeben() {
		return getField("field." + getTableName() + ".wertpauseausgeben");
	}


	public boolean isWertPauseAusgebenAktiv() {
		return Format.getBooleanValue(getFieldWertPauseAusgeben().getValue());
	}


	public void setWertPauseAusgeben(boolean isAktiv) {
		getFieldWertPauseAusgeben().setValue(isAktiv);
	}


	public IField getFieldWertPausenAenderungAusgeben() {
		return getField("field." + getTableName() + ".wertpausenaenderungausgeben");
	}


	public boolean isWertPausenAenderungAusgebenAktiv() {
		return Format.getBooleanValue(getFieldWertPausenAenderungAusgeben().getValue());
	}


	public void setWertPausenAenderungAusgeben(boolean isAktiv) {
		getFieldWertPausenAenderungAusgeben().setValue(isAktiv);
	}


	public IField getFieldWertArbeitsunterbrechungAusgeben() {
		return getField("field." + getTableName() + ".wertarbeitsunterbrechungausgeben");
	}


	public boolean isWertArbeitsunterbrechungAusgebenAktiv() {
		return Format.getBooleanValue(getFieldWertArbeitsunterbrechungAusgeben().getValue());
	}


	public void setWertArbeitsunterbrechungAusgeben(boolean isAktiv) {
		getFieldWertArbeitsunterbrechungAusgeben().setValue(isAktiv);
	}


	public IField getFieldWertPrivateUnterbrechungAusgeben() {
		return getField("field." + getTableName() + ".wertprivateunterbrechungausgeben");
	}


	public boolean isWertPrivateUnterbrechungAusgebenAktiv() {
		return Format.getBooleanValue(getFieldWertPrivateUnterbrechungAusgeben().getValue());
	}


	public void setWertPrivateUnterbrechungAusgeben(boolean isAktiv) {
		getFieldWertPrivateUnterbrechungAusgeben().setValue(isAktiv);
	}


	public IField getFieldWertKrankAusgeben() {
		return getField("field." + getTableName() + ".wertkrankausgeben");
	}


	public boolean isWertKrankAusgebenAktiv() {
		return Format.getBooleanValue(getFieldWertKrankAusgeben().getValue());
	}


	public void setWertKrankAusgeben(boolean isAktiv) {
		getFieldWertKrankAusgeben().setValue(isAktiv);
	}


	public IField getFieldAnzahlKrankAusgeben() {
		return getField("field." + getTableName() + ".anzahlkrankausgeben");
	}


	public boolean isAnzahlKrankAusgebenAktiv() {
		return Format.getBooleanValue(getFieldAnzahlKrankAusgeben().getValue());
	}


	public void setAnzahlKrankAusgeben(boolean isAktiv) {
		getFieldAnzahlKrankAusgeben().setValue(isAktiv);
	}


	public IField getFieldAnzahlKrankOhneLfzAusgeben() {
		return getField("field." + getTableName() + ".anzahlkrankohnelfzausgeben");
	}


	public boolean isAnzahlKrankOhneLfzAusgebenAktiv() {
		return Format.getBooleanValue(getFieldAnzahlKrankOhneLfzAusgeben().getValue());
	}


	public void setAnzahlKrankOhneLfzAusgeben(boolean isAktiv) {
		getFieldAnzahlKrankOhneLfzAusgeben().setValue(isAktiv);
	}


	public IField getFieldAnzahlUrlaubAusgeben() {
		return getField("field." + getTableName() + ".anzahlurlaubausgeben");
	}


	public boolean isAnzahlUrlaubAusgebenAktiv() {
		return Format.getBooleanValue(getFieldAnzahlUrlaubAusgeben().getValue());
	}


	public void setAnzahlUrlaubAusgeben(boolean isAktiv) {
		getFieldAnzahlUrlaubAusgeben().setValue(isAktiv);
	}


	public IField getFieldAnzahlSonderurlaubAusgeben() {
		return getField("field." + getTableName() + ".anzahlsonderurlaubausgeben");
	}


	public boolean isAnzahlSonderurlaubAusgebenAktiv() {
		return Format.getBooleanValue(getFieldAnzahlSonderurlaubAusgeben().getValue());
	}


	public void setAnzahlSonderurlaubAusgeben(boolean isAktiv) {
		getFieldAnzahlSonderurlaubAusgeben().setValue(isAktiv);
	}


	public IField getFieldAnzahlFaAusgeben() {
		return getField("field." + getTableName() + ".anzahlfaausgeben");
	}


	public boolean isAnzahlFaAusgebenAktiv() {
		return Format.getBooleanValue(getFieldAnzahlFaAusgeben().getValue());
	}


	public void setAnzahlFaAusgeben(boolean isAktiv) {
		getFieldAnzahlFaAusgeben().setValue(isAktiv);
	}


	public IField getFieldAnzahlElternzeitAusgeben() {
		return getField("field." + getTableName() + ".anzahlelternzeitausgeben");
	}


	public boolean isAnzahlElternzeitAusgebenAktiv() {
		return Format.getBooleanValue(getFieldAnzahlElternzeitAusgeben().getValue());
	}


	public void setAnzahlElternzeitAusgeben(boolean isAktiv) {
		getFieldAnzahlElternzeitAusgeben().setValue(isAktiv);
	}


	/**
	 * Anzahl der zu Auswertung angegebenen Spalten
	 * 
	 * @return
	 */
	public int getAnzSpaltenAusgewaehlt() {
		int anzSpaltenAusgewaehlt;
		
		anzSpaltenAusgewaehlt = 0;
		
//		anzSpaltenAusgewaehlt += (isAnzahlArbeitstageAusgebenAktiv() ? 1 : 0);
		
		anzSpaltenAusgewaehlt += (isWertSollarbeitszeitAusgebenAktiv() ? 1 : 0);
		anzSpaltenAusgewaehlt += (isWertArbeitszeitAusgebenAktiv() ? 1 : 0);
		anzSpaltenAusgewaehlt += (isWertUeberstundenAusgebenAktiv() ? 1 : 0);
		anzSpaltenAusgewaehlt += (isAenderungGleitzeitkontoAusgebenAktiv() ? 1 : 0);

		anzSpaltenAusgewaehlt += (isWertPlusstundenAusgebenAktiv() ? 1 : 0);
		anzSpaltenAusgewaehlt += (isWertPlusstundenProjektAusgebenAktiv() ? 1 : 0);
		anzSpaltenAusgewaehlt += (isWertPlusstundenReiseAusgebenAktiv() ? 1 : 0);
		anzSpaltenAusgewaehlt += (isWertMinusstundenAusgebenAktiv() ? 1 : 0);
		anzSpaltenAusgewaehlt += (isWertAuszahlungProjektstundenAusgebenAktiv() ? 1 : 0);
		anzSpaltenAusgewaehlt += (isWertAuszahlungReisestundenAusgebenAktiv() ? 1 : 0);

		anzSpaltenAusgewaehlt += (isWertAnwesendAusgebenAktiv() ? 1 : 0);
		anzSpaltenAusgewaehlt += (isWertDienstgangAusgebenAktiv() ? 1 : 0);
		anzSpaltenAusgewaehlt += (isWertDienstreiseAusgebenAktiv() ? 1 : 0);
		anzSpaltenAusgewaehlt += (isWertReisezeitAusgebenAktiv() ? 1 : 0);
		anzSpaltenAusgewaehlt += (isWertVorlesungAusgebenAktiv() ? 1 : 0);

		anzSpaltenAusgewaehlt += (isWertPauseAusgebenAktiv() ? 1 : 0);
		anzSpaltenAusgewaehlt += (isWertPausenAenderungAusgebenAktiv() ? 1 : 0);
		anzSpaltenAusgewaehlt += (isWertArbeitsunterbrechungAusgebenAktiv() ? 1 : 0);
		anzSpaltenAusgewaehlt += (isWertPrivateUnterbrechungAusgebenAktiv() ? 1 : 0);
		anzSpaltenAusgewaehlt += (isWertKrankAusgebenAktiv() ? 1 : 0);
		
		anzSpaltenAusgewaehlt += (isAnzahlKrankAusgebenAktiv() ? 1 : 0);
		anzSpaltenAusgewaehlt += (isAnzahlKrankOhneLfzAusgebenAktiv() ? 1 : 0);
		anzSpaltenAusgewaehlt += (isAnzahlUrlaubAusgebenAktiv() ? 1 : 0);
		anzSpaltenAusgewaehlt += (isAnzahlSonderurlaubAusgebenAktiv() ? 1 : 0);
		anzSpaltenAusgewaehlt += (isAnzahlFaAusgebenAktiv() ? 1 : 0);
		anzSpaltenAusgewaehlt += (isAnzahlElternzeitAusgebenAktiv() ? 1 : 0);

		return anzSpaltenAusgewaehlt;
	}


}
