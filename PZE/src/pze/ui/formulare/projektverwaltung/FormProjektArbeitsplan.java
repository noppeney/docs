package pze.ui.formulare.projektverwaltung;

import framework.Application;
import pze.business.objects.projektverwaltung.CoArbeitsplan;
import pze.business.objects.projektverwaltung.CoProjekt;
import pze.ui.formulare.UniFormWithSaveLogic;


/**
 * Formular des Arbeitsplans für ein Projekt
 * 
 * @author Lisiecki
 *
 */
public class FormProjektArbeitsplan extends FormProjektMitarbeiter {
	public static final String RESID = "form.projekt.arbeitsplan";

	

	/**
	 * Konstruktor
	 * 
	 * @param parent visueller Parent
	 * @param coProjekt Co der geloggten Daten
	 * @param formProjekt Hauptformular des Projekts
	 * @throws Exception
	 */
	public FormProjektArbeitsplan(Object parent, CoProjekt coProjekt, UniFormWithSaveLogic formProjekt) throws Exception {
		super(parent, coProjekt, formProjekt, RESID);
	}

	
	@Override
	protected void initCo() throws Exception {
		m_coMitarbeiterProjekt = new CoArbeitsplan(m_coProjekt);
	}


	@Override
	protected void initTable() throws Exception {
		m_table = new TableProjektMitarbeiter(findControl("spread.projekt.arbeitsplan"), m_coMitarbeiterProjekt);
	}

	
	@Override
	protected void loadData() throws Exception {
		m_coMitarbeiterProjekt.loadByProjekt(false);
		
		setData(m_coMitarbeiterProjekt);
	}

	
	/**
	 * @param id Primärschlüssel
	 * @return Item Key im Tabfolder
	 */
	public static String getKey(int id) {
		return "projekt.arbeitsplan." + id;
	}
	
	
	@Override
	public String getKey() {
		return getKey(getID());
	}
	

	/**
	 * ggf. Meldungen generieren
	 * 
	 */
	@Override
	public void doBeforeSave() throws Exception {
		int rowState;
		
		// bei neuen Projekten Arbeitsplan anlegen
		if (m_coProjekt.isNew())
		{
			((CoArbeitsplan) m_coMitarbeiterProjekt).createNewArbeitsplan(); // TODO Arbeitsplan automatisch erzeugen?
		}
		
		if (!m_coMitarbeiterProjekt.moveFirst() || m_coMitarbeiterProjekt.hasNoRows())
		{
			return;
		}
		
		// Personen durchlaufen
//		m_coMessage = new CoMessage();
		do
		{
//			rowState = m_coMitarbeiterProjekt.getCurrentRow().getRowState();
//			
//			// neue Person zugeordnet
//			if (rowState == IBusinessObject.statusAdded)
//			{
//				m_coMessage.createMessageProjektZugeteilt(m_coMitarbeiterProjekt.getPersonID(), m_coProjekt);
//			}
//			// Daten geändert
//			else if (rowState == IBusinessObject.statusChanged)
//			{
//				m_coMessage.createMessageProjektzuteilungGeaendert(m_coMitarbeiterProjekt.getPersonID(), m_coProjekt, m_coMitarbeiterProjekt);
//			}
			
		} while(m_coMitarbeiterProjekt.moveNext());
		
	}

	
	/**
	 * RefTable-Items neu laden
	 * 
	 * (non-Javadoc)
	 * @see pze.ui.formulare.UniFormWithSaveLogic#doAfterSave()
	 */
	@Override
	public void doAfterSave() throws Exception {
		Application.getRefTableLoader().updateRefItems(getData().getResID());
	}


}
