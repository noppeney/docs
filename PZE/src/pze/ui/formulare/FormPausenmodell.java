package pze.ui.formulare;

import framework.Application;
import framework.business.interfaces.session.ISession;
import framework.ui.interfaces.controls.ITabFolder;
import framework.ui.interfaces.controls.ITabItem;
import pze.business.navigation.NavigationBaseNode;
import pze.business.navigation.NavigationManager;
import pze.business.navigation.treeloader.TreeLoaderPausenmodelle;
import pze.business.objects.CoPausenmodell;

/**
 * Formular für Pausenmodelle
 * 
 * @author Lisiecki
 *
 */
public class FormPausenmodell extends UniFormWithSaveLogic {

	public static String RESID_ALLGEMEIN = "form.pausenmodell.allgemein";

	private static FormPausenmodell m_formPausenmodell;

	private CoPausenmodell m_coPausenmodell;


	
	/**
	 * Konstruktor
	 * @param parent		visueller Parent
	 * @param coPausenmodell	 zu editierendes CacheObject
	 * @throws Exception
	 */
	public FormPausenmodell(Object parent, CoPausenmodell coPausenmodell) throws Exception {
		super(parent, RESID_ALLGEMEIN);
		
		m_coPausenmodell = coPausenmodell;
		setData(m_coPausenmodell);

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
			CoPausenmodell coPausenmodell = new CoPausenmodell();
			String name;

			if (id == -1 )
			{
				coPausenmodell.createNew();
				key = getKey(coPausenmodell.getID());
				name = "<neues Pausenmodell>";
			}
			else
			{
				coPausenmodell.loadByID(id);
				name = coPausenmodell.getBezeichnung();
			}
						
			item = editFolder.add(name, key, null,true);
			item.setBitmap(coPausenmodell.getNavigationBitmap());
			ITabFolder sub = item.getSubFolder();
			editFolder.setSelection(key);	

			m_formPausenmodell = new FormPausenmodell(sub, coPausenmodell);
			sub.add("Allgemeine Daten", FormPausenmodell.RESID_ALLGEMEIN, m_formPausenmodell, false);

//			m_formPersonVerwaltung = new FormPersonVerwaltung(sub, coZeitmodell, m_formPerson);
//			sub.add("Verwaltung", FormPersonVerwaltung.RESID, m_formPersonVerwaltung, false);			
//			m_formPerson.addAdditionalForm(m_formPersonVerwaltung);
//
//			m_formPersonZeitmodell = new FormPersonZeitmodell(sub, coZeitmodell, m_formPerson);
//			sub.add("Zeitmodelle", FormPersonZeitmodell.RESID, m_formPersonZeitmodell, false);			
//			m_formPerson.addAdditionalForm(m_formPersonZeitmodell);
			
			sub.setActivateSubFolder(true);
			sub.setSelection(RESID_ALLGEMEIN);
			
			item.setBitmap(coPausenmodell.getNavigationBitmap());
		}

		editFolder.setSelection(key);
	}

	
	
	/**
	 * @param id Primärschlüssel
	 * @return Item Key im Tabfolder
	 */
	public static String getKey(int id) {
		return "pausenmodell." + id;
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
		
		NavigationManager.getInstance().reloadRootNode(TreeLoaderPausenmodelle.ROOT);
	}


}
