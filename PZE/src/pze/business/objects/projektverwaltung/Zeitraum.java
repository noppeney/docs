package pze.business.objects.projektverwaltung;


/**
 * Klasse zum Zwischenspeichern eines Zeitraums bei der Projektauswertung.<br>
 * Jahre und Zeiträume
 * 
 * @author Lisiecki
 */
public class Zeitraum {

	private int m_zeitraumNr;
	private int m_jahr;
	
	
	
	public int getZeitraumNr() {
		return m_zeitraumNr;
	}
	
	
	public void setZeitraumNr(int zeitraumNr) {
		m_zeitraumNr = zeitraumNr;
	}
	
	
	public int getJahr() {
		return m_jahr;
	}
	
	
	public void setJahr(int jahr) {
		m_jahr = jahr;
	}
	
	
	
}
