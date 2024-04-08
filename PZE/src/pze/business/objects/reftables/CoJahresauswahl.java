package pze.business.objects.reftables;

import java.util.Calendar;
import java.util.GregorianCalendar;

import pze.business.Format;
import pze.business.objects.AbstractCacheObject;

/**
 * CacheObject für Jahresauswahl
 * 
 * @author Lisiecki
 *
 */
public class CoJahresauswahl extends AbstractCacheObject {

	private static final String TABLE_NAME = "rtbljahresauswahl";


	/**
	 * Kontruktor
	 * @param coPerson 
	 */
	public CoJahresauswahl() {
		super("table." + TABLE_NAME);
	}

	
	/**
	 * CO füllen mit allen Jahren ab 2017.<br>
	 * 
	 * @throws Exception
	 */
	public void createCo() throws Exception {
		int iJahr, aktJahr;
		GregorianCalendar gregDatum;
		
		gregDatum = Format.getGregorianCalendar(null);
		aktJahr = gregDatum.get(Calendar.YEAR);
		
		// ab November auch das nächste Jahr
		if (gregDatum.get(Calendar.MONTH) > Calendar.OCTOBER)
		{
			++aktJahr;
		}
		
		// Jahre ab 2017
		begin();
		for (iJahr=2017; iJahr<=aktJahr; ++iJahr)
		{
			add();
			setID(iJahr);
			setBezeichnung("" + iJahr);
		}
		
		// 1. Eintrag selektieren
		moveFirst();
	}

}
