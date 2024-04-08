package pze.business.objects.auswertung;

import framework.business.interfaces.fields.IField;
import pze.business.Format;
import pze.business.objects.reftables.personen.CoStatusAktivInaktiv;

/**
 * CacheObject für die Einstellungen der Auswertung der Verletzerliste
 * 
 * @author Lisiecki
 *
 */
public class CoAuswertungVerletzerliste extends CoAuswertung {

	public static final String TABLE_NAME = "tblauswertungverletzerliste";



	/**
	 * Kontruktor
	 */
	public CoAuswertungVerletzerliste() {
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
	

	public IField getFieldStatusInfoAusblenden() {
		return getField("field." + getTableName() + ".statusinfoausblenden");
	}


	public boolean isStatusInfoAusgeblendet() {
		return Format.getBooleanValue(getFieldStatusInfoAusblenden().getValue());
	}


	public void setStatusInfoAusblenden(boolean isAktiv) {
		getFieldStatusInfoAusblenden().setValue(isAktiv);
	}


//	public IField getFieldHinweisGleitzeitkontoAusblenden() {
//		return getField("field." + getTableName() + ".hinweisgleitzeitkontoausblenden");
//	}
//
//
//	public boolean isHinweisGleitzeitkontoAusgeblendet() {
//		return Format.getBooleanValue(getFieldHinweisGleitzeitkontoAusblenden().getValue());
//	}
//
//
//	public void setHinweisGleitzeitkontoAusblenden(boolean isAktiv) {
//		getFieldHinweisGleitzeitkontoAusblenden().setValue(isAktiv);
//	}


	public IField getFieldKeineBuchungAusblenden() {
		return getField("field." + getTableName() + ".keinebuchungausblenden");
	}


	public boolean isKeineBuchungAusgeblendet() {
		return Format.getBooleanValue(getFieldKeineBuchungAusblenden().getValue());
	}


	public void setKeineBuchungAusblenden(boolean isAktiv) {
		getFieldKeineBuchungAusblenden().setValue(isAktiv);
	}


}
