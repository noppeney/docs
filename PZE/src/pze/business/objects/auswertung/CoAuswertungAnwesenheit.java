package pze.business.objects.auswertung;

import framework.business.interfaces.fields.IField;
import pze.business.Format;

/**
 * CacheObject für die Einstellungen der Auswertung der Kontowerte
 * 
 * @author Lisiecki
 *
 */
public class CoAuswertungAnwesenheit extends CoAuswertung {

	public static final String TABLE_NAME = "tblauswertunganwesenheit";



	/**
	 * Kontruktor
	 */
	public CoAuswertungAnwesenheit() {
		super(TABLE_NAME);
	}
	

	private IField getFieldVerwaltungAusgeben() {
		return getField("field." + getTableName() + ".verwaltungausgeben");
	}


	public boolean isVerwaltungAusgebenAktiv() {
		return Format.getBooleanValue(getFieldVerwaltungAusgeben().getValue());
	}


	private IField getFieldTechBerechnungenAusgeben() {
		return getField("field." + getTableName() + ".techberechnungenausgeben");
	}


	public boolean isTechBerechnungenAusgebenAktiv() {
		return Format.getBooleanValue(getFieldTechBerechnungenAusgeben().getValue());
	}


	private IField getFieldNukBerechnungenAusgeben() {
		return getField("field." + getTableName() + ".nukberechnungenausgeben");
	}


	public boolean isNukBerechnungenAusgebenAktiv() {
		return Format.getBooleanValue(getFieldNukBerechnungenAusgeben().getValue());
	}


	private IField getFieldRueckbauplanungAusgeben() {
		return getField("field." + getTableName() + ".rueckbauplanungausgeben");
	}


	public boolean isRueckbauplanungAusgebenAktiv() {
		return Format.getBooleanValue(getFieldRueckbauplanungAusgeben().getValue());
	}


	private IField getFieldEntsorgungsplanungAusgeben() {
		return getField("field." + getTableName() + ".entsorgungsplanung");
	}


	public boolean isEntsorgungsplanungAusgebenAktiv() {
		return Format.getBooleanValue(getFieldEntsorgungsplanungAusgeben().getValue());
	}


	private IField getFieldBauplanungAusgeben() {
		return getField("field." + getTableName() + ".bauplanungausgeben");
	}


	public boolean isBauplanungAusgebenAktiv() {
		return Format.getBooleanValue(getFieldBauplanungAusgeben().getValue());
	}


	private IField getFieldKlAusgeben() {
		return getField("field." + getTableName() + ".klausgeben");
	}


	public boolean isKlAusgebenAktiv() {
		return Format.getBooleanValue(getFieldKlAusgeben().getValue());
	}

	private IField getFieldGfAusgeben() {
		return getField("field." + getTableName() + ".gfausgeben");
	}


	public boolean isGfAusgebenAktiv() {
		return Format.getBooleanValue(getFieldGfAusgeben().getValue());
	}

}
