package pze.ui.formulare.uebersicht;

import framework.business.interfaces.session.ISession;
import framework.ui.interfaces.controls.IControl;
import framework.ui.interfaces.controls.ITabFolder;
import framework.ui.interfaces.controls.ITabItem;
import pze.business.Profile;
import pze.business.objects.CoZeitmodell;
import pze.ui.controls.SortedTableControl;
import pze.ui.formulare.FormZeitmodell;
import pze.ui.formulare.UniFormWithSaveLogic;

/**
 * Formular für eine Übersicht über die Personen
 * 
 * @author Lisiecki
 *
 */
public class FormUebersichtZeitmodelle extends UniFormWithSaveLogic {
	
	public static String RESID = "form.zeitmodelle";

	private static FormUebersichtZeitmodelle m_formZeitmodelle;
	
	private CoZeitmodell m_coZeitmodell;

	private SortedTableControl m_tableZeitmodelle;
	

	
	
	/**
	 * Konstruktor
	 * @param parent		visueller Parent
	 * @param statusAktivInaktiv 
	 * @throws Exception
	 */
	private FormUebersichtZeitmodelle(Object parent) throws Exception {
		super(parent, RESID);
		
		m_coZeitmodell = new CoZeitmodell();
		m_coZeitmodell.loadAll();
		setData(m_coZeitmodell);

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
			name = "Zeitmodelle";

			m_formZeitmodelle = new FormUebersichtZeitmodelle(editFolder);
			item = editFolder.add(name, key, m_formZeitmodelle, true);
			item.setBitmap(CoZeitmodell.getInstance().getNavigationBitmap());
		}

		editFolder.setSelection(key);
	}


	private void initTable() throws Exception {

		m_tableZeitmodelle = new SortedTableControl(findControl("spread.zeitmodelle")) {

			@Override
			public void tableDefaultSelected(IControl arg0, Object arg1) throws Exception {
				FormZeitmodell.open(getSession(), m_coZeitmodell.getID());
			}
		};
		
		m_tableZeitmodelle.setData(m_coZeitmodell);
		
//		m_tableZeitmodelle.setSelectionListener(new ISelectionListener() {
//
//			/**
//			 * Statusanzeige für die aktuelle Unterweisung anpassen
//			 * 
//			 * @see framework.ui.interfaces.selection.ISelectionListener#selected(framework.ui.interfaces.controls.IControl, java.lang.Object)
//			 */
//			@Override
//			public void selected(IControl arg0, Object arg1) {
//				// allgemeine selected-Funktion zum Sortieren aufrufen
//				m_tableZeitmodelle.tableSelected(arg0, arg1);
//			}
//
//
//			@Override
//			public void defaultSelected(IControl arg0, Object arg1) {
//
//				// Formular mit dem Zeitmodell öffnen
//				try
//				{
//					if (arg1 != null)
//					{
//						FormZeitmodell.open(getSession(), null, m_coZeitmodell.getID());
//					}
//				} catch (Exception e)
//				{
//					e.printStackTrace();
//				}
//			}
//		});
	}

	
	/**
	 * @param id Primärschlüssel
	 * @return Item Key im Tabfolder
	 */
	public static String getKey(int id) {
		return "zeitmodelle." + id;
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

		addExcelExportListener(m_coZeitmodell, m_tableZeitmodelle, "Uebersicht_Zeitmodelle", Profile.KEY_ADMINISTRATION);
		super.activate();
	}
	

}
