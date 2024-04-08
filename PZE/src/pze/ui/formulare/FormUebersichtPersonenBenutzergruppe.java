package pze.ui.formulare;

import framework.business.interfaces.session.ISession;
import framework.ui.interfaces.controls.IControl;
import framework.ui.interfaces.controls.ITabFolder;
import framework.ui.interfaces.controls.ITabItem;
import pze.business.Profile;
import pze.business.objects.personen.CoPerson;
import pze.business.objects.reftables.personen.CoStatusAktivInaktiv;
import pze.ui.controls.SortedTableControl;
import pze.ui.formulare.person.FormPerson;

/**
 * Formular für eine Übersicht über die Personen mit den Benutzergruppen
 * 
 * @author Lisiecki
 *
 */
public class FormUebersichtPersonenBenutzergruppe extends UniFormWithSaveLogic {
	
	public static String RESID = "form.personen.benutzergruppen";

	private static FormUebersichtPersonenBenutzergruppe m_formPersonen;
	
	private CoPerson m_coPerson;

	private SortedTableControl m_tablePersonen;
	
	private int m_statusAktivInaktivID;

	
	
	/**
	 * Konstruktor
	 * @param parent		visueller Parent
	 * @param statusAktivInaktivID 
	 * @throws Exception
	 */
	private FormUebersichtPersonenBenutzergruppe(Object parent, int statusAktivInaktivID) throws Exception {
		super(parent, RESID);
		
		m_statusAktivInaktivID = statusAktivInaktivID;
		
		m_coPerson = new CoPerson();
		m_coPerson.loadWithBenutzergruppe(statusAktivInaktivID);
		setData(m_coPerson);

		initTable();
	}


	/**
	 * Formular öffnen oder selektieren, wenn noch nicht vorhanden
	 * 
	 * @param session Session
	 * @param statusAktivInaktivID 
	 * @param node Knoten in Navigation
	 * @throws Exception
	 */
	public static void open(ISession session, int statusAktivInaktivID) throws Exception {
		String key, name;
		ITabFolder editFolder;
		ITabItem item;
		
		key = getKey(statusAktivInaktivID);
		
		editFolder = session.getMainFrame().getEditFolder();
		item = editFolder.get(key);

		if(item == null)
		{
			name = "Personen - Benutzergruppen (" + CoStatusAktivInaktiv.getInstance().getBezeichnung(statusAktivInaktivID) + ")";

			m_formPersonen = new FormUebersichtPersonenBenutzergruppe(editFolder, statusAktivInaktivID);
			item = editFolder.add(name, key, m_formPersonen, true);
			item.setBitmap("group");
		}

		editFolder.setSelection(key);
	}


	private void initTable() throws Exception {

		m_tablePersonen = new SortedTableControl(findControl("spread.personen.benutzergruppen")) {

			@Override
			public void tableDefaultSelected(IControl arg0, Object arg1) throws Exception {
				FormPerson.open(getSession(), null, m_coPerson.getID());
			}
		};
		
		m_tablePersonen.setData(m_coPerson);
		
//		m_tablePersonen.setSelectionListener(new ISelectionListener() {
//
//			/**
//			 * Statusanzeige für die aktuelle Unterweisung anpassen
//			 * 
//			 * @see framework.ui.interfaces.selection.ISelectionListener#selected(framework.ui.interfaces.controls.IControl, java.lang.Object)
//			 */
//			@Override
//			public void selected(IControl arg0, Object arg1) {
//				// allgemeine selected-Funktion zum Sortieren aufrufen
//				m_tablePersonen.tableSelected(arg0, arg1);
//			}
//
//
//			@Override
//			public void defaultSelected(IControl arg0, Object arg1) {
//
//				// Formular mit der Person öffnen
//				try
//				{
//					if (arg1 != null)
//					{
//						FormPerson.open(getSession(), null, m_coPerson.getID());
//					}
//				} catch (Exception e)
//				{
//					e.printStackTrace();
//				}
//			}
//		});
	}

	
	/**
	 * @param statusAktivInaktivID Primärschlüssel
	 * @return Item Key im Tabfolder
	 */
	public static String getKey(int statusAktivInaktivID) {
		return "personen.benutzergruppen." + statusAktivInaktivID;
	}
	
	
	@Override
	public String getKey() {
		return getKey(m_statusAktivInaktivID);
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

		addExcelExportListener(m_coPerson, m_tablePersonen, "Uebersicht_Personen_Benutzergruppen", Profile.KEY_ADMINISTRATION);
		super.activate();
	}
	

}
