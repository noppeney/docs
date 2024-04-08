package pze.ui.formulare;

import framework.business.interfaces.session.ISession;
import framework.ui.interfaces.controls.ITabFolder;
import framework.ui.interfaces.controls.ITabItem;
import pze.business.objects.CoFirmenparameter;

/**
 * Formular für Firmenparameter
 * 
 * @author Lisiecki
 *
 */
public class FormFirmenparameter extends UniFormWithSaveLogic {

	public static String RESID = "form.firmenparameter";

	private static FormFirmenparameter m_formFirmenparameter;

	private CoFirmenparameter m_coFirmenparameter;


	
	/**
	 * Konstruktor
	 * @param parent		visueller Parent
	 * @throws Exception
	 */
	public FormFirmenparameter(Object parent) throws Exception {
		super(parent, RESID);
		
		m_coFirmenparameter = CoFirmenparameter.getInstance();
		m_coFirmenparameter.loadAll();
		setData(m_coFirmenparameter);		

		refresh(reasonDisabled, null);
	}
	

	/**
	 * Formular öffnen oder selektieren, wenn noch nicht vorhanden
	 * 
	 * @param session Session
	 * @throws Exception
	 */
	public static void open(ISession session) throws Exception {
		String key, name;
		ITabFolder editFolder;
		ITabItem item;
		
		key = getKey(0);
		
		editFolder = session.getMainFrame().getEditFolder();
		item = editFolder.get(key);

		if(item == null)
		{
			name = "Firmenparameter";

			m_formFirmenparameter = new FormFirmenparameter(editFolder);
			item = editFolder.add(name, key, m_formFirmenparameter, true);
			item.setBitmap(CoFirmenparameter.getInstance().getNavigationBitmap());
		}

		editFolder.setSelection(key);
	}

	
	
	/**
	 * @param id Primärschlüssel
	 * @return Item Key im Tabfolder
	 */
	public static String getKey(int id) {
		return "firmenparameter." + id;
	}
	
	
	@Override
	public String getKey() {
		return getKey(0);
	}
	
	
}
