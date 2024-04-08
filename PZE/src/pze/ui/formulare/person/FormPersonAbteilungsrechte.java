package pze.ui.formulare.person;

import pze.business.navigation.NavigationManager;
import pze.business.objects.personen.CoPerson;
import pze.business.objects.personen.CoPersonAbteilungsrechte;
import pze.ui.controls.SortedTableControl;
import pze.ui.formulare.UniFormWithSaveLogic;


/**
 * Formular der Rechte einer Person zur Verwaltung von Personen bestimmter Abteilungen
 * 
 * @author Lisiecki
 *
 */
public class FormPersonAbteilungsrechte extends UniFormWithSaveLogic {
	public static final String RESID = "form.person.abteilungsrechte";

	private SortedTableControl m_table;
	
	private CoPerson m_coPerson;
	private CoPersonAbteilungsrechte m_coPersonAbteilungsrechte;
			
	private UniFormWithSaveLogic m_formPerson;
	
	

	/**
	 * Konstruktor
	 * 
	 * @param parent visueller Parent
	 * @param coPerson Co der geloggten Daten
	 * @param formPerson Hauptformular der Person
	 * @throws Exception
	 */
	public FormPersonAbteilungsrechte(Object parent, CoPerson coPerson, UniFormWithSaveLogic formPerson) throws Exception {
		super(parent, RESID, true);
		
		m_formPerson = formPerson;

		m_coPerson = coPerson;
		m_coPersonAbteilungsrechte = m_coPerson.getCoPersonAbteilungsrechte(false);
		
		setData(coPerson);
		m_coPerson.addChild(m_coPersonAbteilungsrechte);

		m_table = new SortedTableControl(findControl("spread.person.abteilungsrechte"));
		m_table.setData(m_coPersonAbteilungsrechte);

		refresh(reasonDisabled, null);
	}

	
	/**
	 * @param id Primärschlüssel
	 * @return Item Key im Tabfolder
	 */
	public static String getKey(int id) {
		return "personabteilungsrechte" + id;
	}
	
	
	@Override
	public String getKey() {
		return getKey(getID());
	}
	

	@Override
	public void activate() {
		
		if (NavigationManager.getSelectedTabItemKey() != null && NavigationManager.getSelectedTabItemKey().equals(getResID()))
		{
			m_formPerson.addSaveHandler();
			refreshByEditMode();
			super.activate();
		}
	}
	
	
	@Override
	public void deactivate() {
		m_formPerson.removeSaveHandler();
		super.deactivate();
	}


}
