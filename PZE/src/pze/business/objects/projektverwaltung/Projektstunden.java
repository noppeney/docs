package pze.business.objects.projektverwaltung;

/**
 * Klasse zum Zwischenspeichern von projektbezogenen Daten beim Eintragen des Monatseinsatzblattes
 * 
 * @author Lisiecki
 */
public class Projektstunden {

//	private int m_projektID;
	private int m_aktStunden;
//	private int m_sollstunden;
//	private int m_ueberbuchung;
	private CoProjekt m_coProjekt;
	
	
	
	public Projektstunden(CoProjekt coProjekt){
		m_coProjekt = coProjekt;
	}
	
	
	
//	public int getProjektID() {
//		return m_projektID;
//	}
//	
//	
//	public void setProjektID(int projektID) {
//		m_projektID = projektID;
//	}
	
	
	public int getAktStunden() {
		return m_aktStunden;
	}
	
	
	public void setAktStunden(int aktStunden) {
		m_aktStunden = aktStunden;
	}
	
	
//	public int getSollstunden() {
//		return m_sollstunden;
//	}
//	
//	
//	public void setSollstunden(int sollstunden) {
//		m_sollstunden = sollstunden;
//	}
//	
//	
//	public int getUeberbuchung() {
//		return m_ueberbuchung;
//	}
//	
//	
//	public void setUeberbuchung(int ueberbuchung) {
//		m_ueberbuchung = ueberbuchung;
//	}

	
	public String getProjektbezeichnung(){
		return m_coProjekt.getBezeichnung();
	}
	

	/**
	 * Prüft, ob auf das Projekt noch Stunden gebucht werden dürfen
	 * 
	 * @return Zeit in Minuten, um die das Kontingent überschritten wurde<br> < 0 bedeutet Kontingent ist nicht überschritten<br>= 0 bedeutet 100 % 
	 */
	public int getBudgetUeberschreitung() {
		return m_aktStunden - m_coProjekt.getVerfuegbareStunden();
	}
	
	
	
}
