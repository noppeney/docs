package pze.business.objects.reftables;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import pze.business.Format;
import pze.business.objects.AbstractCacheObject;

/**
 * CacheObject für Halbjahresauswahl
 * 
 * @author Lisiecki
 *
 */
public class CoHalbjahresauswahl extends AbstractCacheObject {

	private static final String TABLE_NAME = "rtblhalbjahresauswahl";


	/**
	 * Kontruktor
	 * @param coPerson 
	 */
	public CoHalbjahresauswahl() {
		super("table." + TABLE_NAME);
	}

	
	/**
	 * CO füllen mit allen möglich Halbjahren ab 2017.<br>
	 * 
	 * @throws Exception
	 */
	public void createCo() throws Exception {
		GregorianCalendar gregDatum, gregDatumHeute;
		
		
		// Halbjahre ab 2017 bestimmen
		gregDatum = new GregorianCalendar(2017, Calendar.JANUARY, 1);
		
		// bis heute
		gregDatumHeute = Format.getGregorianCalendar12Uhr(null);

		// Datensätze erstellen ab dem 1.1.2017 bis heute
		begin();
		do
		{
			add();
			
			setID(getRowCount());
			setBezeichnung((gregDatum.get(Calendar.MONTH) == Calendar.JANUARY ? "1" : "2") + ". Halbjahr " + gregDatum.get(Calendar.YEAR));
			setDatum(new Timestamp(gregDatum.getTimeInMillis()));
			
			gregDatum.set(Calendar.MONTH, gregDatum.get(Calendar.MONTH) + 6);
		} while (!gregDatum.after(gregDatumHeute));
		
		
		// umgekehrt sortieren, den neuesten Eintrag nach oben
		sort(getFieldID().getFieldDescription().getResID(), true);
		
		// 1./neuesten Eintrag selektieren
		moveFirst();
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
	
}
