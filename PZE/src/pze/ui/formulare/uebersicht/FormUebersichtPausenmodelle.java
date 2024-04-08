package pze.ui.formulare.uebersicht;

import framework.business.interfaces.session.ISession;
import framework.ui.interfaces.controls.IControl;
import framework.ui.interfaces.controls.ITabFolder;
import framework.ui.interfaces.controls.ITabItem;
import pze.business.Profile;
import pze.business.objects.CoPausenmodell;
import pze.ui.controls.SortedTableControl;
import pze.ui.formulare.FormPausenmodell;
import pze.ui.formulare.UniFormWithSaveLogic;

/**
 * Formular für eine Übersicht über die Personen
 * 
 * @author Lisiecki
 *
 */
public class FormUebersichtPausenmodelle extends UniFormWithSaveLogic {
	
	public static String RESID = "form.pausenmodelle";

	private static FormUebersichtPausenmodelle m_formPausenmodelle;
	
	private CoPausenmodell m_coPausenmodell;

	private SortedTableControl m_tablePausenmodelle;
	

	
	
	/**
	 * Konstruktor
	 * @param parent		visueller Parent
	 * @param statusAktivInaktiv 
	 * @throws Exception
	 */
	private FormUebersichtPausenmodelle(Object parent) throws Exception {
		super(parent, RESID);
		
		m_coPausenmodell = new CoPausenmodell();
		m_coPausenmodell.loadAll();
		setData(m_coPausenmodell);

		initTable();
	}


	/**
	 * Formular öffnen oder selektieren, wenn noch nicht vorhanden
	 * 
	 * @param session Session
	 * @param statusAktivInaktiv 
	 * @param node Knoten in Navigation
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
			name = "Pausenmodelle";

			m_formPausenmodelle = new FormUebersichtPausenmodelle(editFolder);
			item = editFolder.add(name, key, m_formPausenmodelle, true);
			item.setBitmap(CoPausenmodell.getInstance().getNavigationBitmap());
		}

		editFolder.setSelection(key);
	}


	private void initTable() throws Exception {

		m_tablePausenmodelle = new SortedTableControl(findControl("spread.pausenmodelle")){

			@Override
			public void tableDefaultSelected(IControl arg0, Object arg1) throws Exception {
				FormPausenmodell.open(getSession(), m_coPausenmodell.getID());
			}
		};
		
		m_tablePausenmodelle.setData(m_coPausenmodell);
	}

	
	/**
	 * @param id Primärschlüssel
	 * @return Item Key im Tabfolder
	 */
	public static String getKey(int id) {
		return "pausenmodelle." + id;
	}
	
	
	@Override
	public String getKey() {
		return getKey(0);
	}
	
	
	/**
	 * Das Formular darf nicht bearbeitet werden
	 * 
	 * (non-Javadoc)
	 * @see pze.ui.formulare.UniFormWithSaveLogic#mayEdit()
	 */
	@Override
	public boolean mayEdit() {
		return false;
	}
	

	@Override
	public void activate() {

		addExcelExportListener(m_coPausenmodell, m_tablePausenmodelle, "Uebersicht_Pausenmodelle", Profile.KEY_ADMINISTRATION);
		super.activate();
	}
	

}
