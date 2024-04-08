package pze.business.objects.auswertung;

import java.util.Date;

import framework.business.interfaces.fields.IField;
import pze.business.Format;
import pze.business.objects.reftables.personen.CoStatusAktivInaktiv;

/**
 * CacheObject für die Einstellungen der Auswertung der Kontowerte
 * 
 * @author Lisiecki
 *
 */
public class CoAuswertungKontowerte extends CoAuswertung {

	public static final String TABLE_NAME = "tblauswertungkontowerte";



	/**
	 * Kontruktor
	 */
	public CoAuswertungKontowerte() {
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
	

	public IField getFieldDatumGeplantBis() {
		return getField("field." + getTableName() + ".datumgeplantbis");
	}


	public Date getDatumGeplantBis() {
		return getDatum(getFieldDatumGeplantBis());
	}


	public IField getFieldStandGleitzeitkontoAusgeben() {
		return getField("field." + getTableName() + ".standgleitzeitkontoausgeben");
	}


	public boolean isStandGleitzeitkontoAusgebenAktiv() {
		return Format.getBooleanValue(getFieldStandGleitzeitkontoAusgeben().getValue());
	}


	public void setStandGleitzeitkontoAusgeben(boolean isAktiv) {
		getFieldStandGleitzeitkontoAusgeben().setValue(isAktiv);
	}


	public IField getFieldStandResturlaubAusgeben() {
		return getField("field." + getTableName() + ".standresturlaubausgeben");
	}


	public boolean isStandResturlaubAusgebenAktiv() {
		return Format.getBooleanValue(getFieldStandResturlaubAusgeben().getValue());
	}


	public void setStandResturlaubAusgeben(boolean isAktiv) {
		getFieldStandResturlaubAusgeben().setValue(isAktiv);
	}


	public IField getFieldStandResturlaubDetailsAusgeben() {
		return getField("field." + getTableName() + ".standresturlaubdetailsausgeben");
	}


	public boolean isStandResturlaubDetailsAusgebenAktiv() {
		return Format.getBooleanValue(getFieldStandResturlaubDetailsAusgeben().getValue());
	}


	public void setStandResturlaubDetailsAusgeben(boolean isAktiv) {
		getFieldStandResturlaubDetailsAusgeben().setValue(isAktiv);
	}


}
