package pze.ui.formulare;

import framework.business.action.Action;
import framework.business.action.ActionAdapter;
import framework.business.interfaces.actions.IActionListener;
import framework.business.interfaces.session.ISession;
import framework.ui.interfaces.controls.ITabFolder;
import framework.ui.interfaces.controls.ITabItem;
import pze.business.objects.CoBrueckentag;
import pze.ui.controls.SortedTableControl;

/**
 * Formular für die Brückentage
 * 
 * @author Lisiecki
 *
 */
public class FormBrueckentage extends UniFormWithSaveLogic {
	
	public static String RESID = "form.brueckentage";

	private static FormBrueckentage m_formBrueckentage;
	
	private CoBrueckentag m_coBrueckentag;

	private SortedTableControl m_table;
	
	private IActionListener addlistener, deletelistener;

	
	
	/**
	 * Konstruktor
	 * @param parent		visueller Parent
	 * @throws Exception
	 */
	private FormBrueckentage(Object parent) throws Exception {
		super(parent, RESID);
		
		m_coBrueckentag = CoBrueckentag.getInstance();
		m_coBrueckentag.loadAll();
		setData(m_coBrueckentag);

		initTable();
		
		addlistener = new AddListener();
		deletelistener = new TableDeleteListener(this, m_coBrueckentag, m_table);
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
			name = "Brückentage";

			m_formBrueckentage = new FormBrueckentage(editFolder);
			item = editFolder.add(name, key, m_formBrueckentage, true);
			item.setBitmap(CoBrueckentag.getInstance().getNavigationBitmap());
		}

		editFolder.setSelection(key);
	}


	private void initTable() throws Exception {

		m_table = new SortedTableControl(findControl("spread.brueckentage"));
		m_table.setData(m_coBrueckentag);
//		m_table.setSelectionListener(new ISelectionListener() {
//
//			/**
//			 * Statusanzeige für die aktuelle Unterweisung anpassen
//			 * 
//			 * @see framework.ui.interfaces.selection.ISelectionListener#selected(framework.ui.interfaces.controls.IControl, java.lang.Object)
//			 */
//			@Override
//			public void selected(IControl arg0, Object arg1) {
//				// allgemeine selected-Funktion zum Sortieren aufrufen
//				m_table.tableSelected(arg0, arg1);
//			}
//
//
//			@Override
//			public void defaultSelected(IControl arg0, Object arg1) {
//
//			}
//		});
	}

	
	/**
	 * @param id Primärschlüssel
	 * @return Item Key im Tabfolder
	 */
	public static String getKey(int id) {
		return "brueckentage." + id;
	}
	
	
	@Override
	public String getKey() {
		return getKey(0);
	}
	

	@Override
	public void activate() {
		Action.get("file.new").addActionListener(addlistener);
		Action.get("edit.delete").addActionListener(deletelistener);
		super.activate();
	}


	@Override
	public void deactivate() {
		Action.get("file.new").removeActionListener(addlistener);
		Action.get("edit.delete").removeActionListener(deletelistener);
		super.deactivate();
	}

	
	/**
	 * Bezeichnungs-Datensatz hinzufügen
	 *
	 */
	class AddListener extends ActionAdapter
	{
		@Override
		public void activate(Object sender) throws Exception {
			m_coBrueckentag.createNew();
			m_table.refresh(reasonDataAdded, m_coBrueckentag.getBookmark());
			super.activate(sender);
		}
		
		/* (non-Javadoc)
		 * @see framework.business.action.ActionAdapter#getEnabled()
		 */
		@Override
		public boolean getEnabled() {
			return m_coBrueckentag.isEditing();
		}
	}


}
