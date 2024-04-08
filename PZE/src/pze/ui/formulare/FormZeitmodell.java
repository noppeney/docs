package pze.ui.formulare;

import framework.Application;
import framework.business.interfaces.session.ISession;
import framework.ui.interfaces.controls.ITabFolder;
import framework.ui.interfaces.controls.ITabItem;
import pze.business.navigation.NavigationBaseNode;
import pze.business.navigation.NavigationManager;
import pze.business.navigation.treeloader.TreeLoaderZeitmodelle;
import pze.business.objects.CoZeitmodell;

/**
 * Formular für Zeitmodelle
 * 
 * @author Lisiecki
 *
 */
public class FormZeitmodell extends UniFormWithSaveLogic {

	public static String RESID_ALLGEMEIN = "form.zeitmodell.allgemein";

	private static FormZeitmodell m_formZeitmodell;

	private CoZeitmodell m_coZeitmodell;


	
	/**
	 * Konstruktor
	 * @param parent		visueller Parent
	 * @param coZeitmodell	 zu editierendes CacheObject
	 * @throws Exception
	 */
	public FormZeitmodell(Object parent, CoZeitmodell coZeitmodell) throws Exception {
		super(parent, RESID_ALLGEMEIN);
		
		m_coZeitmodell = coZeitmodell;
		setData(m_coZeitmodell);		

		refresh(reasonDisabled, null);
	}
	

	/**
	 * Formular öffnen oder selektieren, wenn noch nicht vorhanden
	 * 
	 * @param session Session
	 * @param node Knoten in Navigation
	 * @throws Exception
	 */
	public static void open(ISession session, NavigationBaseNode node) throws Exception {
		int id = (node==null ? -1 : node.getID());
		
		open(session, id);
	}

	
	/**
	 * Formular öffnen oder selektieren, wenn noch nicht vorhanden
	 * 
	 * @param session Session
	 * @param id 
	 * @throws Exception
	 */
	public static void open(ISession session, int id) throws Exception {
		ITabFolder editFolder = session.getMainFrame().getEditFolder();
		String key = getKey(id);
		
		ITabItem item = editFolder.get(key);
		if(  item == null )
		{
			CoZeitmodell coZeitmodell = new CoZeitmodell();
			String name;

			if (id == -1 )
			{
				coZeitmodell.createNew();
				key = getKey(coZeitmodell.getID());
				name = "<neues Zeitmodell>";
			}
			else
			{
				coZeitmodell.loadByID(id);
				name = coZeitmodell.getBezeichnung();
			}
						
			item = editFolder.add(name, key, null,true);
			item.setBitmap(coZeitmodell.getNavigationBitmap());
			ITabFolder sub = item.getSubFolder();
			editFolder.setSelection(key);	

			m_formZeitmodell = new FormZeitmodell( sub, coZeitmodell);
			sub.add("Allgemeine Daten", FormZeitmodell.RESID_ALLGEMEIN, m_formZeitmodell, false);

//			m_formPersonVerwaltung = new FormPersonVerwaltung(sub, coZeitmodell, m_formPerson);
//			sub.add("Verwaltung", FormPersonVerwaltung.RESID, m_formPersonVerwaltung, false);			
//			m_formPerson.addAdditionalForm(m_formPersonVerwaltung);
//
//			m_formPersonZeitmodell = new FormPersonZeitmodell(sub, coZeitmodell, m_formPerson);
//			sub.add("Zeitmodelle", FormPersonZeitmodell.RESID, m_formPersonZeitmodell, false);			
//			m_formPerson.addAdditionalForm(m_formPersonZeitmodell);
			
			sub.setActivateSubFolder(true);
			sub.setSelection(RESID_ALLGEMEIN);
			
			item.setBitmap(coZeitmodell.getNavigationBitmap());
		}

		editFolder.setSelection(key);
	}

	
	
	/**
	 * @param id Primärschlüssel
	 * @return Item Key im Tabfolder
	 */
	public static String getKey(int id) {
		return "zeitmodell." + id;
	}
	
	
	@Override
	public String getKey() {
		return getKey(getID());
	}
	
	
	
	/**
	 * Tab-Caption anpassen, Baum und RefTable-Items neu laden
	 * 
	 * (non-Javadoc)
	 * @see pze.ui.formulare.UniFormWithSaveLogic#doAfterSave()
	 */
	@Override
	public void doAfterSave() throws Exception {
		refreshTabItem();
		
		Application.getRefTableLoader().updateRefItems(getData().getResID());
		
		NavigationManager.getInstance().reloadRootNode(TreeLoaderZeitmodelle.ROOT);
	}


}
