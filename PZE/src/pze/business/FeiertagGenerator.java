package pze.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import pze.business.objects.reftables.personen.CoBundesland;

/**
 * Klasse zur Generierung von Feiertagen und Prüfung, ob ein Datum ein Feiertag ist
 * 
 * @author Lisiecki/Wallenfang
 */
public class FeiertagGenerator
{
	private static FeiertagGenerator m_instance = null;
	
	private Map<String, ArrayList<Feiertag>> m_mapFeiertage;
	


	/**
	 * Konstruktor
	 * @throws Exception 
	 */
	private FeiertagGenerator(){
		m_mapFeiertage = new HashMap<String, ArrayList<Feiertag>>();
	}
	

	public static FeiertagGenerator getInstance() {
		if (FeiertagGenerator.m_instance == null)
		{
			FeiertagGenerator.m_instance = new FeiertagGenerator();
		}
		
		return FeiertagGenerator.m_instance;
	}


	/**
	 * Abhaengig vom angegebenen Bundesland und Jahr werden die gesetzlichen
	 * Feiertage berechnet 
	 * 
	 * @param jahr
	 * @param bundeslandID
	 */
	private ArrayList<Feiertag> berechneListFeiertage(int jahr, int bundeslandID)
	{
		ArrayList<Feiertag> feiertage = new ArrayList<Feiertag>();

		Calendar ostern = getDatumOstern(jahr);

		Calendar karfreitag = Calendar.getInstance();
		karfreitag.setTime(ostern.getTime());
		karfreitag.add(Calendar.DAY_OF_MONTH, -2);

		Calendar ostermontag = Calendar.getInstance();
		ostermontag.setTime(ostern.getTime());
		ostermontag.add(Calendar.DAY_OF_MONTH, 1);

		Calendar himmelfahrt = Calendar.getInstance();
		himmelfahrt.setTime(ostern.getTime());
		himmelfahrt.add(Calendar.DAY_OF_MONTH, 39);

		Calendar pfingstmontag = Calendar.getInstance();
		pfingstmontag.setTime(ostern.getTime());
		pfingstmontag.add(Calendar.DAY_OF_MONTH, 50);

		Calendar fronleichnam = Calendar.getInstance();
		fronleichnam.setTime(ostern.getTime());
		fronleichnam.add(Calendar.DAY_OF_MONTH, 60);

		feiertage.add(new Feiertag("Neujahr", 1, Calendar.JANUARY, jahr));
		feiertage.add(new Feiertag("Karfreitag", karfreitag));
		feiertage.add(new Feiertag("Ostermontag", ostermontag));
		feiertage.add(new Feiertag("Erster Mai, Tag der Arbeit", 1, Calendar.MAY, jahr));
		feiertage.add(new Feiertag("Christi Himmelfahrt", himmelfahrt));
		feiertage.add(new Feiertag("Pfingstmontag", pfingstmontag));
		feiertage.add(new Feiertag("Fronleichnam", fronleichnam));
		feiertage.add(new Feiertag("Tag der Deutschen Einheit", 3, Calendar.OCTOBER, jahr));
		feiertage.add(new Feiertag("Allerheiligen", 1, Calendar.NOVEMBER, jahr));
		feiertage.add(new Feiertag("1. Weihnachtsfeiertag", 25, Calendar.DECEMBER, jahr));
		feiertage.add(new Feiertag("2. Weihnachtsfeiertag", 26, Calendar.DECEMBER, jahr));

		if (jahr == 2017)
		{
			feiertage.add(new Feiertag("500. Reformationstag", 31, Calendar.OCTOBER, jahr));
		}

		if (bundeslandID == CoBundesland.ID_BAYERN)
		{
			feiertage.add(new Feiertag("Heilige Drei Koenige", 6, Calendar.JANUARY, jahr));
			feiertage.add(new Feiertag("Mariae Himmelfahrt", 15, Calendar.AUGUST, jahr));
		}

		if (bundeslandID == CoBundesland.ID_BADEN_WUERTTEMBERG)
		{
			feiertage.add(new Feiertag("Heilige Drei Koenige", 6, Calendar.JANUARY, jahr));
		}

		return feiertage;
	}
	
	
	/**
	 * Untersucht, ob das uebergebene Datum ein gesetzlicher Feiertag in dem uebergebenen Bundesland ist.
	 * @param datum
	 * @param bundesland
	 * @return
	 */
	public boolean isFeiertag(Date datum, int bundeslandID)
	{
		return isFeiertag(Format.getGregorianCalendar(datum), bundeslandID);
	}
	

	/**
	 * Untersucht, ob das angegebene Datum vom Typ Calendar ein gesetzlicher Feiertag in dem uebergebenen Bundesland ist.
	 * @param datum
	 * @param bundeslandID
	 * @return
	 */
	public boolean isFeiertag(Calendar datum, int bundeslandID)
	{
		return getFeiertag(datum, bundeslandID) != null;
	}


	/**
	 * Untersucht, ob das angegebene Datum vom Typ Calendar ein gesetzlicher Feiertag in dem uebergebenen Bundesland ist.
	 * @param datum
	 * @param bundeslandID
	 * @return Bezeichnung des Feiertags oder null
	 */
	public String getFeiertag(Calendar datum, int bundeslandID)
	{
		int iFeiertag;
		String key;
		Feiertag feiertag;
		ArrayList<Feiertag> alFeiertage;
		
		key = getKey(datum, bundeslandID);
		
		// Feiertage für das Jahr und das Bundesland generieren, wenn sie noch nicht existieren
		if (!m_mapFeiertage.containsKey(key))
		{
			m_mapFeiertage.put(key, berechneListFeiertage(datum.get(Calendar.YEAR), bundeslandID));
		}
		
		// Feiertage durchlaufen und prüfen
		alFeiertage = m_mapFeiertage.get(key);
		for(iFeiertag = 0; iFeiertag < alFeiertage.size(); iFeiertag++)
		{
			feiertag = alFeiertage.get(iFeiertag);
			
			if (feiertag.getMonat() == datum.get(Calendar.MONTH) && feiertag.getTag() == datum.get(Calendar.DAY_OF_MONTH))
			{
				return feiertag.getName();
			}
		}
		
		return null;
	}


	/**
	 * Key aus Jahr und Bundesland für die Feiertagsliste
	 * 
	 * @param datum
	 * @param bundeslandID
	 * @return
	 */
	private String getKey(Calendar datum, int bundeslandID) {
		return datum.get(Calendar.YEAR) + "-" + bundeslandID;
	}


	/**
	 * Mit Hilfe der Gaussschen Osterformel wird Ostersonntag berechnet und als Calendar zurueckgegeben.
	 * 
	 * @param jahr
	 * @return Calendar
	 */
	private Calendar getDatumOstern(int jahr)
	{
		Calendar datumOstern = Calendar.getInstance();
		datumOstern.set(Calendar.YEAR, jahr);
		datumOstern.set(Calendar.MONTH, Calendar.MARCH);
		datumOstern.set(Calendar.DAY_OF_MONTH, 0);
		// Gausssche Osterformel
		int a = jahr % 19;
		int b = jahr % 4;
		int c = jahr % 7;
		int k = jahr / 100;
		int p = (8 * k + 13) / 25;
		int q = k / 4;
		int m = (15 + k - p - q) % 30;
		int d = (19 * a + m) % 30;
		int n = (4 + k - q) % 7;
		int e = (2 * b + 4 * c + 6 * d + n) % 7;
		int tag = 22 + d + e;
		// Ende Formel
		
		datumOstern.add(Calendar.DAY_OF_MONTH, tag);
		return datumOstern;

	}

}
