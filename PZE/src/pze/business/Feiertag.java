package pze.business;

import java.util.Calendar;


/**
 * Ein Feiertag Objekt beschreibt einen gesetzlichen Feiertag mit Name, Tag, Monat und Jahr.
 * Das zugehörige Bundesland ist nicht in den Attributen enthalten.
 * 
 * @author Wallenfang
 */
public class Feiertag
{
	
	private String m_name;
	private int m_tag;
	/**
	 * Monat von 0 bis 11
	 */
	private int m_monat;
	private int m_jahr;

	
	public Feiertag(String name, int tag, int monat, int jahr)
	{
		m_name = name;
		m_tag = tag;
		m_monat = monat;
		m_jahr = jahr;
	}

	/**
	 * 
	 * @param name
	 * @param datum
	 */
	public Feiertag(String name, Calendar datum)
	{
		m_name = name;
		m_tag = datum.get(Calendar.DAY_OF_MONTH);
		m_monat = datum.get(Calendar.MONTH);
		m_jahr = datum.get(Calendar.YEAR);
		
	}
	
	
	public String getName()
	{
		return m_name;
	}


	public void setName(String name)
	{
		m_name = name;
	}

	
	public int getTag()
	{
		return m_tag;
	}

	
	public void setTag(int tag)
	{
		m_tag = tag;
	}

	
	/**
	 * 
	 * @return Monat beginnend mit 0 
	 */
	public int getMonat()
	{
		return m_monat;
	}
	
	/**
	 * 
	 * @param monat Monat beginnend mit 0
	 */
	public void setMonat(int monat)
	{
		m_monat = monat;
	}

	public int getJahr()
	{
		return m_jahr;
	}

	public void setJahr(int m_jahr)
	{
		this.m_jahr = m_jahr;
	}

	/**
	 * Gibt einen String zurueck, der Name und Datum (DD/MM/YYYY) enth�lt
	 * @return
	 */
	public String ausgeben()
	{
		String ausgabe = "";
		ausgabe += "" + getName() + ": ";
		if(this.getTag() / 10 == 0)
		{
			ausgabe += "0";
		}
		
		ausgabe += + this.getTag() + "." ;
		
		if((this.getMonat() + 1) / 10 == 0)
		{
			ausgabe += "0";
		}
		
		ausgabe += + (this.getMonat()+1) + "." + this.getJahr();
		return ausgabe;
	}

}
