package pze.ui.formulare.uebersicht;

import framework.business.interfaces.session.ISession;
import framework.ui.interfaces.controls.IControl;
import framework.ui.interfaces.controls.ITabFolder;
import framework.ui.interfaces.controls.ITabItem;
import pze.business.Profile;
import pze.business.navigation.treeloader.TreeLoaderPersonen;
import pze.business.objects.personen.CoPerson;
import pze.business.objects.reftables.personen.CoStatusAktivInaktiv;
import pze.ui.controls.SortedTableControl;
import pze.ui.formulare.UniFormWithSaveLogic;
import pze.ui.formulare.person.FormPerson;

/**
 * Formular für eine Übersicht über die Personen
 * 
 * @author Lisiecki
 *
 */
public class FormUebersichtPersonen extends UniFormWithSaveLogic {
	
	public static String RESID = "form.personen";

	private static FormUebersichtPersonen m_formPersonen;
	
	private CoPerson m_coPerson;

	private SortedTableControl m_tablePersonen;
	
	private int m_statusAktivInaktivID;

	
	
	/**
	 * Konstruktor
	 * @param parent		visueller Parent
	 * @param statusAktivInaktivID 
	 * @throws Exception
	 */
	private FormUebersichtPersonen(Object parent, int statusAktivInaktivID) throws Exception {
		super(parent, RESID);
		
		m_statusAktivInaktivID = statusAktivInaktivID;
		
		m_coPerson = new CoPerson();
		m_coPerson.loadAllWithZeitmodell(statusAktivInaktivID);
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
		String key, name, bitmap, bezeichnung;
		ITabFolder editFolder;
		ITabItem item;
		
		key = getKey(statusAktivInaktivID);
		
		editFolder = session.getMainFrame().getEditFolder();
		item = editFolder.get(key);

		if(item == null)
		{
			bezeichnung = CoStatusAktivInaktiv.getInstance().getBezeichnung(statusAktivInaktivID);
			name = "Personen" + (bezeichnung != null ? " (" + CoStatusAktivInaktiv.getInstance().getBezeichnung(statusAktivInaktivID) + ")" : "");

			m_formPersonen = new FormUebersichtPersonen(editFolder, statusAktivInaktivID);
			item = editFolder.add(name, key, m_formPersonen, true);
			
			// Bitmap bestimmen
			switch (statusAktivInaktivID)
			{
			case CoStatusAktivInaktiv.STATUSID_ALLE:
				bitmap = TreeLoaderPersonen.BITMAP_PERSONEN_ALLE;
				break;
				
			case CoStatusAktivInaktiv.STATUSID_AKTIV:
				bitmap = TreeLoaderPersonen.BITMAP_PERSONEN_AKTIV;
				break;
				
			case CoStatusAktivInaktiv.STATUSID_INAKTIV:
				bitmap = TreeLoaderPersonen.BITMAP_PERSONEN_INAKTIV;
				break;
				
			case CoStatusAktivInaktiv.STATUSID_AUSGESCHIEDEN:
				bitmap = TreeLoaderPersonen.BITMAP_PERSONEN_AUSGESCHIEDEN;
				break;
				
			default:
				bitmap = "";
				break;
			}
			item.setBitmap(bitmap);
		}

		editFolder.setSelection(key);
	}


	private void initTable() throws Exception {

		m_tablePersonen = new SortedTableControl(findControl("spread.personen")) {

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
	 * @param m_statusAktivInaktivID Primärschlüssel
	 * @return Item Key im Tabfolder
	 */
	public static String getKey(int m_statusAktivInaktivID) {
		return "personen." + m_statusAktivInaktivID;
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

		addExcelExportListener(m_coPerson, m_tablePersonen, "Uebersicht_Personen", Profile.KEY_AUSWERTUNG_PERSONEN);
		super.activate();
	}
	

}
