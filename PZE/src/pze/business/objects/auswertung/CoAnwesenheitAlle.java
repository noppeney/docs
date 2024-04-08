package pze.business.objects.auswertung;

import framework.business.interfaces.fields.IField;
import pze.business.Format;
import pze.business.objects.AbstractCacheObject;
import pze.business.objects.personen.CoPerson;

/**
 * Abstraktes CacheObject für die Personen (Ansicht) der Anwesenheitsübersicht
 * 
 * @author Lisiecki
 *
 */
public class CoAnwesenheitAlle extends AbstractCacheObject {

	private static final int ANZ_PERSONEN_ZEILE = 7;
	
	private CoPerson m_coPerson;

	
	
	/**
	 * Kontruktor
	 * @throws Exception 
	 */
	public CoAnwesenheitAlle() throws Exception {
		super();
		
		createCo();
	}


	public void createCo() throws Exception{
		int anzZeilen, anzPersonen;
	
		setResID("anwesenheit.alle");

		begin();
		
		m_coPerson = new CoPerson();
		m_coPerson.loadForAnwesenheit(0);
		m_coPerson.moveFirst();
		
		anzPersonen = m_coPerson.getRowCount();
		anzZeilen = (int) Math.ceil(anzPersonen / (ANZ_PERSONEN_ZEILE * 1.0));

		addFields();
		addRows(anzZeilen);

		commit();
	}
	

	/**
	 * Felder für die Spalten hinzufügen
	 */
	private void addFields() {
		int iField;
		String resid, columnName, columnLabel;
		
		
		// prüfen, ob Daten vorhanden sind
		if (!m_coPerson.moveFirst())
		{
			return;
		}
		
		iField = 0;
		for (iField=0; iField<ANZ_PERSONEN_ZEILE; ++iField)
		{
			resid = "field.person.spalte." + iField + "";

			columnName = "spalte" + iField;
			columnLabel = columnName;

			addField(resid, columnName, columnLabel, false);
		}
		
	}


	/**
	 * Alle Zeilen hinzufügen
	 * @param anzZeilen 
	 * 
	 * @throws Exception
	 */
	private void addRows(int anzZeilen) throws Exception {
		int iZeile, iField;
		
		for (iZeile=0; iZeile<anzZeilen; ++iZeile)
		{
			add();
			
			for (iField=0; iField<ANZ_PERSONEN_ZEILE; ++iField)
			{
				
				if (m_coPerson.moveTo(iZeile + (iField * anzZeilen)))
				{
					getField(iField).setValue(m_coPerson.getAnzeigeAnwesenheit());
				}
			}
		}
	}

	
	/**
	 * zur Person gehen in coPerson
	 * 
	 * @param row
	 * @param col
	 * @return Person vorhanden
	 */
	public boolean moveToPerson(int row, int col){
		return m_coPerson.moveTo(row + (col * getRowCount()));
	}
	
	
	/**
	 * PersonID der Person an der übergebenen Zelle
	 * 
	 * @param row
	 * @param col
	 * @return PersonID oder 0
	 */
	public int getPersonID(int row, int col){
		if (moveToPerson(row, col))
		{
			return m_coPerson.getID();
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
		if (moveToPerson(row, col))
		{
			return Format.getIntValue(m_coPerson.getBuchungsartID());
		}
		
		return 0;
	}
	

	public IField getFieldStatusID() {
		return m_coPerson.getField("field.tblbuchung.statusid");
	}


}
