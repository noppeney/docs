package pze.ui.formulare;

import framework.business.interfaces.session.ISession;
import framework.ui.interfaces.controls.IControl;
import framework.ui.interfaces.controls.ITabFolder;
import framework.ui.interfaces.controls.ITabItem;
import pze.business.Profile;
import pze.business.objects.personen.CoPersonAbteilungsrechte;
import pze.business.objects.reftables.personen.CoStatusAktivInaktiv;
import pze.ui.controls.SortedTableControl;
import pze.ui.formulare.person.FormPerson;

/**
 * Formular für eine Übersicht über die Personen und ihre Abteilungsrechte
 * 
 * @author Lisiecki
 *
 */
public class FormUebersichtPersonenAbteilungsrechte extends UniFormWithSaveLogic {
	
	public static String RESID = "form.personen.abteilungsrechte";

	private static FormUebersichtPersonenAbteilungsrechte m_form;
	
	private CoPersonAbteilungsrechte m_coPersonAbteilungsrechte;

	private SortedTableControl m_tablePersonen;
	
	private int m_statusAktivInaktivID;

	
	
	/**
	 * Konstruktor
	 * @param parent		visueller Parent
	 * @param statusAktivInaktivID 
	 * @throws Exception
	 */
	private FormUebersichtPersonenAbteilungsrechte(Object parent, int statusAktivInaktivID) throws Exception {
		super(parent, RESID);
		
		m_statusAktivInaktivID = statusAktivInaktivID;
		
		m_coPersonAbteilungsrechte = new CoPersonAbteilungsrechte();
		m_coPersonAbteilungsrechte.loadByStatusID(statusAktivInaktivID);
		setData(m_coPersonAbteilungsrechte);

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
			name = "Personen - Abteilungsrechte (" + CoStatusAktivInaktiv.getInstance().getBezeichnung(statusAktivInaktivID) + ")";

			m_form = new FormUebersichtPersonenAbteilungsrechte(editFolder, statusAktivInaktivID);
			item = editFolder.add(name, key, m_form, true);
			item.setBitmap("group");
		}

		editFolder.setSelection(key);
	}


	private void initTable() throws Exception {

		m_tablePersonen = new SortedTableControl(findControl("spread.personen.abteilungsrechte")){

			@Override
			public void tableDefaultSelected(IControl arg0, Object arg1) throws Exception {
				FormPerson.open(getSession(), null, m_coPersonAbteilungsrechte.getPersonID());
			}
		};
		
		m_tablePersonen.setData(m_coPersonAbteilungsrechte);
		
	}

	
	/**
	 * @param statusAktivInaktivID Primärschlüssel
	 * @return Item Key im Tabfolder
	 */
	public static String getKey(int statusAktivInaktivID) {
		return "personen.abteilungsrechte." + statusAktivInaktivID;
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

		addExcelExportListener(m_coPersonAbteilungsrechte, m_tablePersonen, "Uebersicht_Personen_Abteilungsrechte", Profile.KEY_ADMINISTRATION);
		super.activate();
	}
	

}
