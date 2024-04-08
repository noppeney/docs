package pze.ui.formulare.projektverwaltung;

import framework.business.interfaces.fields.IField;
import framework.ui.interfaces.controls.IControl;
import pze.business.objects.personen.CoPerson;
import pze.business.objects.projektverwaltung.CoMitarbeiterProjekt;
import pze.ui.controls.SortedTableControl;


/**
 * Tabelle für die Zuordnung der Mitarbeiter zu Projekten
 * 
 * 
 * @author lisiecki
 */
public class TableProjektMitarbeiter extends SortedTableControl{

	private CoMitarbeiterProjekt m_coMitarbeiterProjekt;


	/**
	 * Konstruktor
	 * 
	 * @param tableControl
	 * @param coMitarbeiterProjekt
	 * @throws Exception
	 */
	public TableProjektMitarbeiter(IControl tableControl, CoMitarbeiterProjekt coMitarbeiterProjekt) throws Exception {
		super(tableControl);
		
		m_coMitarbeiterProjekt = coMitarbeiterProjekt;
		setData(m_coMitarbeiterProjekt);
	
		// auswählbare Personen einschränken
		setItemsPersonID();
		
		
		enableColumn(m_coMitarbeiterProjekt.getFieldWertIstZeit().getFieldDescription().getResID(), false);
		enableColumn(m_coMitarbeiterProjekt.getFieldGeaendertVon().getFieldDescription().getResID(), false);
		enableColumn(m_coMitarbeiterProjekt.getFieldGeaendertAm().getFieldDescription().getResID(), false);
		// wenn das nicht funktioniert über mayEdit wie im Monatseinsatzblatt

	}


	/**
	 * nur aktive Personen auswählbar
	 * 
	 * @throws Exception
	 */
	private void setItemsPersonID() throws Exception {
		IField field;
		
		// prüfen ob das Feld existiert
		field = m_coMitarbeiterProjekt.getFieldPersonID();
		if (field == null)
		{
			return;
		}
		
		// nur aktive Personen anzeigen
		CoPerson coPerson;
		coPerson = new CoPerson();
		coPerson.loadItemsAktiv();
		coPerson.addEmtyItem();
		
		field.setItems(coPerson);
	}

	
	@Override
	public void tableDefaultSelected(IControl arg0, Object arg1) throws Exception {
//		FormAbruf.open(getSession(), null, m_coMitarbeiterProjekt.getID());
	}
	
	
}
