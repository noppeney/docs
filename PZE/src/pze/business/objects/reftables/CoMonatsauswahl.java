package pze.business.objects.reftables;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import pze.business.Format;
import pze.business.UserInformation;
import pze.business.objects.AbstractCacheObject;
import pze.business.objects.CoFirmenparameter;
import pze.business.objects.personen.CoPerson;

/**
 * CacheObject für Monatsauswahl im Monatseinsatzblatt
 * 
 * @author Lisiecki
 *
 */
public class CoMonatsauswahl extends AbstractCacheObject {

	private static final String TABLE_NAME = "rtblmonatsauswahl"; 

	private CoPerson m_coPerson;



	/**
	 * Kontruktor
	 * @param coPerson 
	 */
	public CoMonatsauswahl(CoPerson coPerson) {
		super("table." + TABLE_NAME);
		
		m_coPerson = coPerson;
	}

	
	/**
	 * CO füllen mit allen möglich Monaten.<br>
	 * Für Admins ab Beginn PZE, sonst ab dem in den Firmenparametern angegebenen datum. 
	 * 
	 * @throws Exception
	 */
	public void createCo() throws Exception {
		Date datumMonatseinsatzblatt;
		GregorianCalendar gregDatum, gregDatumHeute;
		UserInformation userInformation;
		
		userInformation = UserInformation.getInstance();
		
		// 1. Datum in Abhängigkeit der Rechte bestimmen
		if (userInformation.isProjektverwaltung() || userInformation.isPersonalverwaltung() || userInformation.isNurAL())
		{
			datumMonatseinsatzblatt = m_coPerson.getBeginnPze();
		}
		else
		{
			datumMonatseinsatzblatt = CoFirmenparameter.getInstance().getDatumMonatseinsatzblatt();
		}
		
		// Tag des Monats auf den 1. setzen, falls jemand am 15. anfängt
		gregDatum = Format.getGregorianCalendar12Uhr(datumMonatseinsatzblatt);
		gregDatum.set(Calendar.DAY_OF_MONTH, 1);
		
		
		// Datensätze erstellen
		gregDatumHeute = Format.getGregorianCalendar12Uhr(null);
		gregDatumHeute.set(GregorianCalendar.HOUR, 13); // 13 Uhr, damit am 1. des Monats auch ein Eintrag erzeugt wird
		begin();
		while (gregDatum.before(gregDatumHeute))
		{
			add();
			
			setID(getRowCount());
			setBezeichnung(Format.getMonat(gregDatum) + " " + gregDatum.get(Calendar.YEAR));
			setDatum(new Timestamp(gregDatum.getTimeInMillis()));
			
			gregDatum.set(Calendar.MONTH, gregDatum.get(Calendar.MONTH) + 1);
		} 
		
		
		// umgekehrt sortieren, den neuesten Eintrag nach oben
		sort(getFieldID().getFieldDescription().getResID(), true);
	}


	/**
	 * Zum Monat des übergebenen Datums gehen
	 * 
	 * @param datum
	 * @return
	 */
	public boolean movetoMonat(Date datum) {
		GregorianCalendar gregDatum, currentGregDatum;
		
		if (!moveFirst())
		{
			return false;
		}
		
		gregDatum = Format.getGregorianCalendar(datum);
		
		do
		{
			currentGregDatum = getGregDatum();
			
			if (gregDatum.get(Calendar.YEAR) == currentGregDatum.get(Calendar.YEAR) && gregDatum.get(Calendar.MONTH) == currentGregDatum.get(Calendar.MONTH))
			{
				return true;
			}
		} while (moveNext());
		
		return false;
	}
	

//	/**
//	 * Getter Instanz der Klasse
//	 * 
//	 * @return m_instance
//	 */
//	public static CoMonatsauswahl getInstance() throws Exception {
//		if (CoMonatsauswahl.m_instance == null)
//		{
//			CoMonatsauswahl.m_instance = new CoMonatsauswahl();
//			CoMonatsauswahl.m_instance.loadAll();
//		}
//		
//		return CoMonatsauswahl.m_instance;
//	}


}
