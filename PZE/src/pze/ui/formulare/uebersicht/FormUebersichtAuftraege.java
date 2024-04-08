package pze.ui.formulare.uebersicht;

import framework.business.interfaces.nodes.INode;
import framework.business.interfaces.session.ISession;
import framework.ui.interfaces.controls.IControl;
import framework.ui.interfaces.controls.ITabFolder;
import framework.ui.interfaces.controls.ITabItem;
import pze.business.Profile;
import pze.business.objects.projektverwaltung.CoAbruf;
import pze.business.objects.projektverwaltung.CoAuftrag;
import pze.business.objects.reftables.projektverwaltung.CoKunde;
import pze.business.objects.reftables.projektverwaltung.CoStatusProjekt;
import pze.ui.controls.SortedTableControl;
import pze.ui.formulare.UniFormWithSaveLogic;
import pze.ui.formulare.projektverwaltung.FormAuftrag;
import pze.ui.formulare.projektverwaltung.FormAuftragAbrufe;

/**
 * Formular für eine Übersicht über Aufträge
 * 
 * @author Lisiecki
 *
 */
public class FormUebersichtAuftraege extends UniFormWithSaveLogic {
	
	public static String RESID_AUFTRAG = "form.auftraege";
	public static String RESID_ABRUF = "form.auftraege.abrufe";

	private FormAuftragAbrufe m_formAuftragAbrufe;
	
	protected ITabFolder m_subTabFolder;

	private CoAuftrag m_coAuftrag;
	private CoAbruf m_coAbruf;

	private SortedTableControl m_tableAuftraege;
	
	private int m_statusAktivInaktiv;
	private int m_kundeID;

	
	
	/**
	 * Konstruktor
	 * @param parent		visueller Parent
	 * @param statusProjektID 
	 * @throws Exception
	 */
	private FormUebersichtAuftraege(Object parent, int statusProjektID, int kundeID) throws Exception {
		super(parent, RESID_AUFTRAG);
		
		m_statusAktivInaktiv = statusProjektID;
		m_kundeID = kundeID;

		m_coAuftrag = new CoAuftrag();
		m_coAbruf = new CoAbruf();
		// alle Aufträge ausgeben oder Filter verwenden
		if (statusProjektID == 0)
		{
			m_coAuftrag.loadAllForUebersicht();
			m_coAbruf.loadByProjektleiterID();
		}
		else
		{
			// Abrufe laden
			m_coAuftrag.load(statusProjektID, kundeID, false);
			m_coAbruf.loadByAuftragID_Status(m_coAuftrag.getIDs(), statusProjektID);
			
			// Aufträge laden
			m_coAuftrag.load(statusProjektID, kundeID, true);
		}
		m_coAuftrag.addProjektmerkmalAuftrag();
		m_coAuftrag.setModified(false);
		setData(m_coAuftrag);

		initTable();
	}


	/**
	 * Formular öffnen oder selektieren, wenn noch nicht vorhanden
	 * 
	 * @param session Session
	 * @param statusProjektID 
	 * @param kundeID KundeID oder 0
	 * @param node 
	 * @throws Exception
	 */
	public static void open(ISession session, int statusProjektID, int kundeID, INode node) throws Exception {
		String key, name;
		ITabFolder editFolder;
		ITabItem item;
		FormUebersichtAuftraege m_formAuftraege;

		key = getKey(statusProjektID, kundeID);
		
		editFolder = session.getMainFrame().getEditFolder();
		item = editFolder.get(key);

		if(item == null)
		{
			name = (statusProjektID == 0 ? "" : CoStatusProjekt.getInstance().getBezeichnung(statusProjektID))
					+ (kundeID > 0 ? (statusProjektID == 0 ? "" : ", ") + CoKunde.getInstance().getBezeichnung(kundeID) : "");
			
			name = "Aufträge " + (name.isEmpty() ? "" : "(" + name + ")");

			item = editFolder.add(name, key, null, true);
			item.setBitmap(node.getBitmap());
			
			ITabFolder subTabFolder = item.getSubFolder();
			editFolder.setSelection(key);	

			// Auftrag
			m_formAuftraege = new FormUebersichtAuftraege(subTabFolder, statusProjektID, kundeID);
            subTabFolder.add("Aufträge", RESID_AUFTRAG, m_formAuftraege, false);
            m_formAuftraege.setSubTabFolder(subTabFolder);

            // Abrufe
			m_formAuftraege.addFormAbrufe();
			
			subTabFolder.setActivateSubFolder(true);
			subTabFolder.setSelection(RESID_AUFTRAG);
			
			if (m_formAuftraege.getCo().hasRows())
			{
				item.setBitmap(m_formAuftraege.getCo().getNavigationBitmap());
			}
		}

		editFolder.setSelection(key);
	}


	/**
	 * Formular Abrufe hinzufügen
	 * 
	 * @throws Exception
	 */
	private void addFormAbrufe() throws Exception {
		m_formAuftragAbrufe = new FormAuftragAbrufe(m_subTabFolder, m_coAbruf, this); 
		m_subTabFolder.add("Abrufe", RESID_ABRUF, m_formAuftragAbrufe, false);			
		addAdditionalForm(m_formAuftragAbrufe);
	}


	protected void setSubTabFolder(ITabFolder subTabFolder) {
		m_subTabFolder = subTabFolder;
	}


	private void initTable() throws Exception {

		m_tableAuftraege = new SortedTableControl(findControl("spread.auftraege")){

			@Override
			public void tableDefaultSelected(IControl arg0, Object arg1) throws Exception{
				FormAuftrag.open(getSession(), m_coAuftrag.getID());
			}
		};
		
		m_tableAuftraege.setData(m_coAuftrag);
		
	}

	
	/**
	 * @param statusProjektID Primärschlüssel
	 * @return Item Key im Tabfolder
	 */
	public static String getKey(int statusProjektID, int kundeID) {
		return "auftraege." + statusProjektID + "." + kundeID;
	}
	
	
	@Override
	public String getKey() {
		return getKey(m_statusAktivInaktiv, m_kundeID);
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

		addExcelExportListener(m_coAuftrag, m_tableAuftraege, "Uebersicht_Auftraege", Profile.KEY_AUSWERTUNG_PROJEKTE);
		super.activate();
	}
	

}
