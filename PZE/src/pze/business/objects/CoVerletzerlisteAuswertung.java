package pze.business.objects;

import pze.business.Format;
import pze.business.UserInformation;
import pze.business.objects.auswertung.CoAuswertungVerletzerliste;


/**
 * CO mit den Daten zur Auswertung der Verletzerliste.<br>
 * In dem CO sind nur die Fields aus CoVerletzerliste enthalten, die in dem Auswertungs-CO ausgewählt wurden.
 * 
 * 
 * @author Lisiecki
 */
public class CoVerletzerlisteAuswertung extends AbstractCacheObject {

	/**
	 * CO mit Daten aus der DB, um Änderungen (Status) zu speichern
	 */
	private CoVerletzerliste m_coVerletzerliste;

	
	
	public CoVerletzerlisteAuswertung() {
	}


	/**
	 * Konstruktor
	 * 
	 * @param coAuswertungKontowerte
	 * @throws Exception
	 */
	public CoVerletzerlisteAuswertung(CoAuswertungVerletzerliste coAuswertungVerletzerliste) throws Exception{
		super();

		load(coAuswertungVerletzerliste);
	}
	

	/**
	 * Daten gemäß der Auswertung laden
	 * 
	 * @param coAuswertungKontowerte
	 * @throws Exception
	 */
	public void load(CoAuswertungVerletzerliste coAuswertungVerletzerliste) throws Exception{
		m_coVerletzerliste = new CoVerletzerliste();
		m_coVerletzerliste.load(coAuswertungVerletzerliste);

		setResID(m_coVerletzerliste.getResID());

		addFields(coAuswertungVerletzerliste, m_coVerletzerliste);
		addValues(m_coVerletzerliste);
		
		setModified(false);
		commit();
	}


	/**
	 * Fields gemäß der Auswertung hinzufügen
	 * 
	 * @param coAuswertungVerletzerliste
	 * @param coKontowert
	 */
	private void addFields(CoAuswertungVerletzerliste coAuswertungVerletzerliste, CoVerletzerliste coVerletzerliste) {

		// Person, Datum, Zeitinfo
		addField(coVerletzerliste.getFieldID().getFieldDescription());
		addField(coVerletzerliste.getFieldPersonID().getFieldDescription());
		addField(coVerletzerliste.getFieldDatum().getFieldDescription());
		addField(coVerletzerliste.getFieldZeitinfo().getFieldDescription());
		addField(coVerletzerliste.getFieldMeldungID().getFieldDescription());
		
		// Status-Infos
		if (!coAuswertungVerletzerliste.isStatusInfoAusgeblendet())
		{
			addField(coVerletzerliste.getFieldStatusID().getFieldDescription());
			addField(coVerletzerliste.getFieldGeaendertVon().getFieldDescription());
			addField(coVerletzerliste.getFieldGeaendertAm().getFieldDescription());
			addField(coVerletzerliste.getFieldBemerkung().getFieldDescription());
		}
	}
	
	
	/**
	 * Änderungen dokumentieren
	 * 
	 * @throws Exception
	 */
	public void valueChanged() throws Exception {
		String datum;

		datum = Format.getStringMitUhrzeit(Format.getGregorianCalendar(null));
				
		// Speichern von wem die Änderungen gemacht wurden
		setGeaendertVonID(UserInformation.getPersonID());
		setGeaendertAm(datum);
	}
	
	
	public CoVerletzerliste getCoVerletzerliste(){
		return m_coVerletzerliste;
	}

}
