package pze.business.objects.auswertung;

import java.util.HashMap;
import java.util.Map;

import pze.business.Format;
import pze.business.objects.AbstractCacheObject;
import pze.business.objects.personen.CoPerson;
import pze.business.objects.reftables.personen.CoAbteilung;

/**
 * Abstraktes CacheObject für die Personen (Ansicht) der Anwesenheitsübersicht
 * 
 * @author Lisiecki
 *
 */
public class CoAnwesenheitAbteilung extends AbstractCacheObject {

	private CoAuswertungAnwesenheit m_coAuswertungAnwesenheit;
	
	private Map<Integer, CoPerson> m_mapCoPerson;
	
	private int m_anzZeilen;
	

	
	/**
	 * Kontruktor
	 * @throws Exception 
	 */
	public CoAnwesenheitAbteilung(CoAuswertungAnwesenheit coAuswertungAnwesenheit) throws Exception {
		super();
		
		m_coAuswertungAnwesenheit = coAuswertungAnwesenheit;
		
		m_anzZeilen = 0;
		
		createCo();
	}


	public void createCo() throws Exception{
	
		setResID("anwesenheit.abteilung");

		begin();
		
		m_mapCoPerson = new HashMap<Integer, CoPerson>();
		
		if (m_coAuswertungAnwesenheit.isBauplanungAusgebenAktiv())
		{
			addField(CoAbteilung.ID_BAUPLANUNG);
		}
		
		if (m_coAuswertungAnwesenheit.isEntsorgungsplanungAusgebenAktiv())
		{
			addField(CoAbteilung.ID_ENTSORGUNGSPLANUNG);
		}
		
		if (m_coAuswertungAnwesenheit.isNukBerechnungenAusgebenAktiv())
		{
			addField(CoAbteilung.ID_NUK_BERECHNUNGEN);
		}
		
		if (m_coAuswertungAnwesenheit.isTechBerechnungenAusgebenAktiv())
		{
			addField(CoAbteilung.ID_TECH_BERECHNUNGEN);
		}
		
		if (m_coAuswertungAnwesenheit.isRueckbauplanungAusgebenAktiv())
		{
			addField(CoAbteilung.ID_RUECKBAUPLANUNG);
		}
		
		if (m_coAuswertungAnwesenheit.isVerwaltungAusgebenAktiv())
		{
			addField(CoAbteilung.ID_VERWALTUNG);
		}

		if (m_coAuswertungAnwesenheit.isKlAusgebenAktiv())
		{
			addField(CoAbteilung.ID_KL);
		}

		if (m_coAuswertungAnwesenheit.isGfAusgebenAktiv())
		{
			addField(CoAbteilung.ID_GESCHAEFTSFUEHRUNG);
		}

		
//		if (m_coAuswertungAnwesenheit.isVerwaltungAusgebenAktiv())
//		{
//			addField(CoAbteilung.ID_VERWALTUNG);
//		}
//
//		if (m_coAuswertungAnwesenheit.isBauplanungAusgebenAktiv())
//		{
//			addField(CoAbteilung.ID_BAUPLANUNG);
//		}
//		
//		if (m_coAuswertungAnwesenheit.isNukBerechnungenAusgebenAktiv())
//		{
//			addField(CoAbteilung.ID_NUK_BERECHNUNGEN);
//		}
//		
//		if (m_coAuswertungAnwesenheit.isTechBerechnungenAusgebenAktiv())
//		{
//			addField(CoAbteilung.ID_TECH_BERECHNUNGEN);
//		}
//		
//		if (m_coAuswertungAnwesenheit.isEntsorgungsplanungAusgebenAktiv())
//		{
//			addField(CoAbteilung.ID_ENTSORGUNGSPLANUNG);
//		}
//		
//		if (m_coAuswertungAnwesenheit.isRueckbauplanungAusgebenAktiv())
//		{
//			addField(CoAbteilung.ID_RUECKBAUPLANUNG);
//		}
//		
//		if (m_coAuswertungAnwesenheit.isKlAusgebenAktiv())
//		{
//			addField(CoAbteilung.ID_KL);
//		}
//
//		if (m_coAuswertungAnwesenheit.isGfAusgebenAktiv())
//		{
//			addField(CoAbteilung.ID_GESCHAEFTSFUEHRUNG);
//		}
	
		
		addRows();

		commit();
	}


	/**
	 * Spalte für die Abteilung hinzufügen
	 * 
	 * @param abteilungID
	 * @throws Exception
	 */
	private void addField(int abteilungID) throws Exception {
		CoPerson coPerson;
		
		// Personen laden
		coPerson = new CoPerson();
		coPerson.loadForAnwesenheit(abteilungID);
		m_anzZeilen = Math.max(m_anzZeilen, coPerson.getRowCount());
		
		// Co der Personen speichern
		m_mapCoPerson.put(getColumnCount(), coPerson);
		
		// Spalte hinzufügen
		addFieldAbteilung(CoAbteilung.getInstance().getBezeichnung(abteilungID));
	}
	

	/**
	 * Felder für die Abteilungen hinzufügen
	 */
	private void addFieldAbteilung(String abteilung) {
		String resid, columnName, columnLabel;


		resid = "field.person.spalte." + abteilung + "." + getColumnCount();

		columnName = abteilung;
		columnLabel = columnName;

		addField(resid, columnName, columnLabel, false);
	}


	/**
	 * Alle Zeilen hinzufügen (max. Anzahl Personen aller Gruppen)
	 * 
	 * @throws Exception
	 */
	private void addRows() throws Exception {
		int iZeile, iField;
		CoPerson coPerson;
		
		for (iZeile=0; iZeile<m_anzZeilen; ++iZeile)
		{
			add();
			
			for (iField=0; iField<m_mapCoPerson.size(); ++iField)
			{
				coPerson = m_mapCoPerson.get(iField);
				if (coPerson.moveTo(iZeile))
				{
					getField(iField).setValue(coPerson.getAnzeigeAnwesenheit());
				}
			}
		}
	}

	
	/**
	 * zur Person gehen und CoPerson zurückgeben
	 * 
	 * @param row
	 * @param col
	 * @return CoPerson oder null
	 */
	public CoPerson getCoPerson(int row, int col){
		CoPerson coPerson;
		
		coPerson = m_mapCoPerson.get(col);
		
		if (coPerson.moveTo(row))
		{
			return coPerson;
		}
		
		return null;
	}
	

	/**
	 * PersonID der person an der übergebenen Zelle
	 * 
	 * @param row
	 * @param col
	 * @return PersonID oder 0
	 */
	public int getPersonID(int row, int col){
		CoPerson coPerson;
		
		coPerson = getCoPerson(row, col);
		
		if (coPerson != null)
		{
			return coPerson.getID();
		}
		
		return 0;
	}
	
	
	/**
	 * PersonID der Person an der übergebenen Zelle
	 * 
	 * @param row
	 * @param col
	 * @return PersonID oder 0
	 */
	public int getBuchungsartID(int row, int col){
		CoPerson coPerson;
		
		coPerson = getCoPerson(row, col);
		
		if (coPerson != null)
		{
			return Format.getIntValue(coPerson.getBuchungsartID());
		}
		
		return 0;
	}
	


	/**
	 * StatusID, nicht in jeder Tabelle vorhanden
	 * @param col 
	 * @param row 
	 * 
	 * @return StatusID oder 0
	 */
	public int getStatusID(int row, int col) {
		return getCoPerson(row, col).getField("field.tblbuchung.statusid").getIntValue();
	}



}
