package pze.business.objects.projektverwaltung;

import framework.business.interfaces.fields.IField;
import pze.business.Format;
import pze.business.objects.AbstractCacheObject;
import pze.business.objects.auswertung.CoAuswertungProjekt;
import pze.business.objects.personen.monatseinsatzblatt.CoMonatseinsatzblatt;
import pze.business.objects.reftables.projektverwaltung.CoAusgabezeitraum;


/**
 * CO mit den Daten zur Auswertung der Projekte.<br>
 * Person und Stunden inkl. Summenzeile/-spalte
 * 
 * @author Lisiecki
 */
public class CoProjektStundenuebersicht extends AbstractCacheObject {

	public static final String RESID_FIELD_ZEITRAUM = "field.zeitraum.";
	
	private IField m_fieldSumme;

	private CoAuswertungProjekt m_coAuswertungProjekt;

	/**
	 * Konstruktor
	 * 
	 * @param auftragID
	 * @param coAuswertungProjekt
	 * @throws Exception
	 */
	public CoProjektStundenuebersicht(CoAuswertungProjekt coAuswertungProjekt) throws Exception{
		super();

		m_coAuswertungProjekt = coAuswertungProjekt;
	}
	

	/**
	 * Daten gemäß der Auswertung für einen Auftrag laden
	 * 
	 * @param auftragID
	 * @throws Exception
	 */
	public void loadAuftrag(int auftragID, String stundenartID) throws Exception{
		CoMonatseinsatzblatt coMonatseinsatzblatt, coMonatseinsatzblattKomplett;
		
		coMonatseinsatzblatt = new CoMonatseinsatzblatt();
		coMonatseinsatzblatt.loadAuswertungAuftrag(auftragID, m_coAuswertungProjekt, stundenartID);
		
		coMonatseinsatzblattKomplett = new CoMonatseinsatzblatt();
		coMonatseinsatzblattKomplett.loadAuswertungAuftrag(auftragID, m_coAuswertungProjekt, null);
		
		createCo(coMonatseinsatzblatt, coMonatseinsatzblattKomplett);
	}


	/**
	 * Daten gemäß der Auswertung für einen Abruf laden
	 * 
	 * @param abrufID
	 * @throws Exception
	 */
	public void loadAbruf(int abrufID, String stundenartID) throws Exception{
		CoMonatseinsatzblatt coMonatseinsatzblatt, coMonatseinsatzblattKomplett;
		
		coMonatseinsatzblatt = new CoMonatseinsatzblatt();
		coMonatseinsatzblatt.loadAuswertungAbruf(abrufID, m_coAuswertungProjekt, stundenartID);
		
		coMonatseinsatzblattKomplett = new CoMonatseinsatzblatt();
		coMonatseinsatzblattKomplett.loadAuswertungAbruf(abrufID, m_coAuswertungProjekt, null);
		
		createCo(coMonatseinsatzblatt, coMonatseinsatzblattKomplett);
	}


	/**
	 * CO zur Anzeige aufbereiten
	 * 
	 * @param coMonatseinsatzblatt CO mit den Stunden gemäß der Stundenart
	 * @param coMonatseinsatzblattKomplett CO mit allen Stundenarten, um den zeitraum der Auswertung zu bestimmen
	 * @throws Exception
	 */
	private void createCo(CoMonatseinsatzblatt coMonatseinsatzblatt, CoMonatseinsatzblatt coMonatseinsatzblattKomplett) throws Exception{
		
		if (coMonatseinsatzblatt.getRowCount() == 0)
		{
			return;
		}

		setResID(coMonatseinsatzblatt.getResID());

		addFields(coMonatseinsatzblattKomplett);
		addRows(coMonatseinsatzblatt);
		
		setModified(false);
	}


	/**
	 * Fields gemäß Stundenwerten hinzufügen
	 * 
	 * @param coAuswertungProjekt
	 * @param coMonatseinsatzblatt
	 * @throws Exception 
	 */
	private void addFields(CoMonatseinsatzblatt coMonatseinsatzblatt) throws Exception {
		int iZeitraum, ausgabezeitraumID, jahr, anzZeitraeumeJahr;
		String resID, columnName, columnLabel;
		Zeitraum firstZeitraum, lastZeitraum;

		
		ausgabezeitraumID = m_coAuswertungProjekt.getAusgabezeitraumID();
		CoAusgabezeitraum.getInstance().moveToID(ausgabezeitraumID);
		anzZeitraeumeJahr = CoAusgabezeitraum.getInstance().getAnzahlZeitraeume();
		
		firstZeitraum = coMonatseinsatzblatt.getFirstZeitraum();
		lastZeitraum = coMonatseinsatzblatt.getLastZeitraum();

		
		// Person und Datum
		addField(coMonatseinsatzblatt.getFieldPersonID().getFieldDescription());

		// für jeden Zeitraum eine Spalte
		for (iZeitraum=firstZeitraum.getZeitraumNr(), jahr=firstZeitraum.getJahr(); 
				jahr<lastZeitraum.getJahr() || (jahr==lastZeitraum.getJahr() && iZeitraum<=lastZeitraum.getZeitraumNr()); ++iZeitraum)
		{
			resID = getFieldResID(jahr, iZeitraum);

			// Caption bestimmen
			columnName = getFieldCaption(ausgabezeitraumID, jahr, iZeitraum);
			if (columnName == null)
			{
				return;
			}

			// Feld hinzufügen
			columnLabel = columnName;
			addField(resID, columnName, columnLabel, false);
			setZeitFormat(resID);
			
			// nächstes Jahr
			if (iZeitraum == anzZeitraeumeJahr-1)
			{
				++jahr;
				iZeitraum = -1; // wird in for-Schleife auf 1 gesetzt
			}
		}
		
		addFieldSumme();
	}


	/**
	 * Caption bestimmen 
	 * 
	 * @param iZeitraum
	 * @param ausgabezeitraumID
	 * @param jahr
	 * @return
	 */
	private String getFieldCaption(int ausgabezeitraumID, int jahr, int iZeitraum) {
		switch (ausgabezeitraumID) 
		{
		case CoAusgabezeitraum.ID_JAEHRLICH:
			return "" + jahr;

		case CoAusgabezeitraum.ID_HALBJAEHRLICH:
			return (iZeitraum+1) + ". HJ " + jahr;

		case CoAusgabezeitraum.ID_VIERTELJAEHRLICH:
			return (iZeitraum+1) + ". VJ " + jahr;

		case CoAusgabezeitraum.ID_MONATLICH:
			return Format.getMonatAbkuerzung(iZeitraum) + " " + jahr;

		default:
			return null;
		}
	}


	/**
	 * ResID des Field bestimmen
	 * 
	 * @param iZeitraum
	 * @param jahr
	 * @return
	 */
	private String getFieldResID(int jahr, int iZeitraum) {
		return RESID_FIELD_ZEITRAUM + jahr + "." + iZeitraum;
	}
	

	/**
	 * Feld zur Anzeige der Summe der eingetragenen Stunden hinzufügen
	 */
	private void addFieldSumme() {
		String resid, columnName, columnLabel;

		resid = "field.zeitraum.summe";
		columnName = "Summe";
		columnLabel = columnName;

		addField(resid, columnName, columnLabel);
		setZeitFormat(resid);

		m_fieldSumme = getField(getColumnCount() - 1);
	}


	/**
	 * Co mit Daten füllen
	 * 
	 * @param coKontowert
	 * @throws Exception
	 */
	private void addRows(CoMonatseinsatzblatt coMonatseinsatzblatt) throws Exception {
		int personID, lastPersonID, wertZeit, summe;
		IField field;
		begin();
		
		if (!coMonatseinsatzblatt.moveFirst())
		{
			return;
		}
		
		// coMonatseinsatzblatt durchlaufen und Daten übertragen
		lastPersonID = 0;
		summe = 0;
		do
		{
			// ggf. neue Zeile für Person hinzufügen
			personID = coMonatseinsatzblatt.getPersonID();
			if (personID != lastPersonID)
			{
				lastPersonID = personID;
				
				// Summe der letzten Person eintragen
				if (summe > 0 ) // vor erster Person
				{
					m_fieldSumme.setValue(summe);
					summe = 0;
				}
				
				// neue zeile für die nächste Person
				add();
				setPersonID(coMonatseinsatzblatt.getPersonID());
			}

			wertZeit = coMonatseinsatzblatt.getWertZeit();

			field = getField(getFieldResID(coMonatseinsatzblatt.getJahr(), coMonatseinsatzblatt.getZeitraumNr()));
			field.setValue(wertZeit);
			
			summe += wertZeit;

		} while (coMonatseinsatzblatt.moveNext());

		
		// Summe der letzten Person
		m_fieldSumme.setValue(summe);

		
		// Summenzeile
		addRowSumme();
	}

}
