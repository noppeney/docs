package pze.business.objects.reftables;

import java.util.Calendar;
import java.util.GregorianCalendar;

import pze.business.Format;
import pze.business.objects.AbstractCacheObject;

/**
 * CacheObject für Wochenauswahl
 * 
 * @author Lisiecki
 *
 */
public class CoWochenauswahl extends AbstractCacheObject {

	private static final String TABLE_NAME = "rtblwochenauswahl";


	/**
	 * Kontruktor
	 * @param coPerson 
	 */
	public CoWochenauswahl() {
		super("table." + TABLE_NAME);
	}

	
	/**
	 * CO füllen mit allen Wochen von 1 bis 53
	 * 
	 * @throws Exception
	 */
	public void createCo() throws Exception {
		int iKw;
		GregorianCalendar gregDatum;
		
		
//		// KW 1 ist die mit dem ersten Donnerstag im Jahr
//		gregDatum.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
//		// wenn das Datum wieder in den Dezember gesprungen ist, gehe 7 Tage vor
//		if (gregDatum.get(Calendar.MONTH) != Calendar.JANUARY)
//		{
//			gregDatum.add(Calendar.DAY_OF_YEAR, 7);
//		}

		// KW 1 bis 53
		begin();
		for (iKw=1; iKw<54; ++iKw)
		{
			add();
			setID(iKw);
			setBezeichnung("KW " + iKw);
		}
		
		// aktuelle KW bestimmen, 2 Wochen weiter
		gregDatum = Format.getGregorianCalendar(null);
		gregDatum.set(Calendar.WEEK_OF_YEAR, gregDatum.get(Calendar.WEEK_OF_YEAR)+2);

		// 1. Eintrag selektieren
		moveFirst();
	}

}
