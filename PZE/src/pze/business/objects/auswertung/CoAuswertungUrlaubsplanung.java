package pze.business.objects.auswertung;

import java.util.Calendar;
import java.util.GregorianCalendar;

import pze.business.Format;
import pze.business.UserInformation;
import pze.business.objects.reftables.personen.CoStatusAktivInaktiv;

/**
 * CacheObject für die Einstellungen der Auswertung der Urlaubsplanung
 * 
 * @author Lisiecki
 *
 */
public class CoAuswertungUrlaubsplanung extends CoAuswertung {

	public static final String TABLE_NAME = "tblauswertungurlaubsplanung";



	/**
	 * Kontruktor
	 */
	public CoAuswertungUrlaubsplanung() {
		super(TABLE_NAME);
	}
	
	
	/**
	 * bei neuen Einträgen aktuelles Jahr und die aktuelle Person eintragen
	 * 
	 * @see pze.business.objects.AbstractCacheObject#createNew()
	 */
	@Override
	public int createNew(int userID) throws Exception	{
		int id;
		GregorianCalendar gregDatum;
		
		id = super.createNew();
		
		setUserID(userID);
		
		// Auswertung für aktuelle Person öffnen
		setPersonID(UserInformation.getPersonID());
		
		// Auswertungszeitraum das aktuelle Jahr
		gregDatum = Format.getGregorianCalendar(null);
		gregDatum.set(GregorianCalendar.MONTH, Calendar.JANUARY);
		gregDatum.set(GregorianCalendar.DAY_OF_MONTH, 1);
		setDatumVon(Format.getDateValue(gregDatum));
		
		gregDatum.set(GregorianCalendar.MONTH, Calendar.DECEMBER);
		gregDatum.set(GregorianCalendar.DAY_OF_MONTH, 31);
		setDatumBis(Format.getDateValue(gregDatum));
		
		// aktive Personen
		setStatusAktivInaktiv(CoStatusAktivInaktiv.STATUSID_AKTIV);
		
		return id;
	}
	

}
