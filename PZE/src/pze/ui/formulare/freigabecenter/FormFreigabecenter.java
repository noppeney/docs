package pze.ui.formulare.freigabecenter;

import framework.business.interfaces.session.ISession;
import framework.ui.interfaces.controls.ITabFolder;
import framework.ui.interfaces.controls.ITabItem;
import pze.business.UserInformation;
import pze.business.objects.personen.CoBuchung;
import pze.business.objects.personen.CoPerson;
import pze.business.objects.reftables.CoFreigabeberechtigungen;
import pze.business.objects.reftables.CoMessageGruppe;
import pze.ui.formulare.AbstractAktionCenterMainForm;
import pze.ui.formulare.auswertung.FormAuswertungFreigaben;
import pze.ui.formulare.messageboard.AbstractFormMessageboard;
import pze.ui.formulare.messageboard.FormMessageboardProjektverfolgung;
import pze.ui.formulare.messageboard.FormMessageboardVerletzermeldungen;



/**
 * Formular für das Freigabecenter
 * 
 * @author Lisiecki
 *
 */
public class FormFreigabecenter extends AbstractAktionCenterMainForm{
	private static final String CAPTION = "Freigabecenter";

	private static String RESID = "form.freigaben";

	private static AbstractFormFreigabecenterMitarbeiter m_formFreigabecenterMitarbeiter;
	private static AbstractFormFreigabecenterMitarbeiter m_formFreigabecenterUrlaub;
	private static AbstractFormFreigabecenterMitarbeiter m_formFreigabecenterDr;
	
	private static AbstractFormFreigabecenter m_formFreigabecenterPb;
	private static AbstractFormFreigabecenter m_formFreigabecenterAl;
	private static AbstractFormFreigabecenter m_formFreigabecenterVertreter;
	private static AbstractFormMessageboard m_formMessageboardVerletzermeldungen;
	private static AbstractFormMessageboard m_formMessageboardProjektverfolgung;
	
	private static FormAuswertungFreigaben m_formAuswertungFreigaben;
	private static FormfreigabecenterBerechtigungen m_formBerechtigungen;

	
	
	/**
	 * Konstruktor
	 * @param parent		visueller Parent
	 * @throws Exception
	 */
	private FormFreigabecenter(Object parent) throws Exception {
		super(parent, RESID, CAPTION);
	}


	/**
	 * Formular öffnen oder selektieren, wenn noch nicht vorhanden
	 * 
	 * @param session Session
	 * @param node Knoten in Navigation
	 * @throws Exception
	 */
	public static void open(ISession session) throws Exception {
		String key, keyFirstSubFolder;
		ITabItem tabItem;
		ITabFolder subTabFolder;
		ITabFolder editFolder;
		FormFreigabecenter formFreigabecenter;
		
		key = getKey(0);
		
		editFolder = session.getMainFrame().getEditFolder();
		tabItem = editFolder.get(key);

		if(tabItem == null)
		{
			tabItem = editFolder.add(CAPTION, key, null, true);
			tabItem.setBitmap("lib.apply");
			subTabFolder = tabItem.getSubFolder();
			editFolder.setSelection(key);
			
			// weitere Reiter hinzufügen
			formFreigabecenter = new FormFreigabecenter(subTabFolder);
			formFreigabecenter.setTabItem(tabItem);
			keyFirstSubFolder = formFreigabecenter.addSubForms();

			// ersten Reiter auswählen
			formFreigabecenter.refreshCaption(keyFirstSubFolder); // wird sonst als aktueller Tab nicht aktualisiert
			subTabFolder.setActivateSubFolder(true);
			subTabFolder.setSelection(keyFirstSubFolder);
		}

		editFolder.setSelection(key);
	}


	/**
	 * Reiter für Genehmigungsschritte hinzufügen
	 * 
	 * @return
	 * @throws Exception
	 */
	private String addSubForms() throws Exception {
		int personID;
		String keyFirstSubFolder;
		boolean isAl, isVerwaltung;
		CoPerson coPerson;
		
		keyFirstSubFolder = null;
		
		personID = UserInformation.getPersonID();
		coPerson = new CoPerson();
		coPerson.loadByID(personID);
		
		
		// Anträge Mitarbeiter
		m_formFreigabecenterMitarbeiter = new FormFreigabecenterOfa(m_subTabFolder, this);
		m_subTabFolder.add(FormFreigabecenterOfa.RESID, FormFreigabecenterOfa.getKey(0), m_formFreigabecenterMitarbeiter, false);
		//			m_subTabFolder.get(FormFreigabecenterMitarbeiter.getKey(0)).setBitmap("lib.apply");
		keyFirstSubFolder = keyFirstSubFolder == null ? FormFreigabecenterOfa.getKey(0) : keyFirstSubFolder;

		m_formFreigabecenterUrlaub = new FormFreigabecenterUrlaub(m_subTabFolder, this);
		m_subTabFolder.add(FormFreigabecenterUrlaub.RESID, FormFreigabecenterUrlaub.getKey(0), m_formFreigabecenterUrlaub, false);

		m_formFreigabecenterDr = new FormFreigabecenterDr(m_subTabFolder, this);
		m_subTabFolder.add(FormFreigabecenterDr.RESID, FormFreigabecenterDr.getKey(0), m_formFreigabecenterDr, false);

		// Freigaben Personalverwaltung
		isVerwaltung = UserInformation.getInstance().isPersonalverwaltungOhneAdmin();
		if (isVerwaltung)
		{
			// Freigabe von Anträgen
			m_formFreigabecenterPb = new FormFreigabecenterPb(m_subTabFolder, this);
			m_subTabFolder.add(FormFreigabecenterPb.RESID, FormFreigabecenterPb.getKey(0), m_formFreigabecenterPb, false);
			keyFirstSubFolder = keyFirstSubFolder == null ? FormFreigabecenterPb.getKey(0) : keyFirstSubFolder;
		}

		// Anträge über Projektverfolgung
		if (UserInformation.getInstance().isProjektcontrolling())
		{
			m_formMessageboardProjektverfolgung = new FormMessageboardProjektverfolgung(m_subTabFolder, this, CoMessageGruppe.ID_VERWALTUNG);
			m_subTabFolder.add(FormMessageboardProjektverfolgung.RESID_FREIGABE, FormMessageboardProjektverfolgung.getKey(0), 
					m_formMessageboardProjektverfolgung, false);			
		}

		// Freigaben als AL
		isAl = CoFreigabeberechtigungen.hasBerechtigungen(personID);
		if (isAl)
		{
			m_formFreigabecenterAl = new FormFreigabecenterAl(m_subTabFolder, this);
			m_subTabFolder.add(FormFreigabecenterAl.RESID, FormFreigabecenterAl.getKey(0), m_formFreigabecenterAl, false);
			keyFirstSubFolder = keyFirstSubFolder == null ? FormFreigabecenterAl.getKey(0) : keyFirstSubFolder;
			
			// Freigaben Verletzermeldungen
			m_formMessageboardVerletzermeldungen = new FormMessageboardVerletzermeldungen(m_subTabFolder, this, CoMessageGruppe.ID_AL);
			m_subTabFolder.add(FormMessageboardVerletzermeldungen.RESID_FREIGABE, FormMessageboardVerletzermeldungen.getKey(0), 
					m_formMessageboardVerletzermeldungen, false);
			keyFirstSubFolder = keyFirstSubFolder == null ? FormMessageboardVerletzermeldungen.getKey(0) : keyFirstSubFolder;
		}
		
		// Freigaben als Vertreter
		m_formFreigabecenterVertreter = new FormFreigabecenterVertreter(m_subTabFolder, this);
		m_subTabFolder.add(FormFreigabecenterVertreter.RESID, FormFreigabecenterVertreter.getKey(0), m_formFreigabecenterVertreter, false);
		keyFirstSubFolder = keyFirstSubFolder == null ? FormFreigabecenterVertreter.getKey(0) : keyFirstSubFolder;

		// Auswertung
		m_formAuswertungFreigaben = new FormAuswertungFreigaben(m_subTabFolder);
		m_subTabFolder.add(FormAuswertungFreigaben.RESID, FormAuswertungFreigaben.getKey(0), m_formAuswertungFreigaben, false);			

		// Berechtigungen
		if (isAl)
		{
			m_formBerechtigungen = new FormfreigabecenterBerechtigungen(m_subTabFolder);
			m_subTabFolder.add(FormfreigabecenterBerechtigungen.RESID, FormfreigabecenterBerechtigungen.getKey(0), m_formBerechtigungen, false);			
		}

		return keyFirstSubFolder;
	}


	/**
	 * Diese Methoden müssen noch aktualisiert werden, wenn es 2 Tabellen für OFA/Urlaub gibt
	 * 
	 * @param coBuchung
	 * @throws Exception
	 */
	public void checkFreigabeAl(CoBuchung coBuchung) throws Exception {
		if (m_formFreigabecenterAl != null
				&& (m_formFreigabecenterAl.getCo().moveToID(coBuchung.getID()) || m_formFreigabecenterAl.getCoBuchungUrlaub().moveToID(coBuchung.getID())))
		{
			m_formFreigabecenterAl.createFreigabe(coBuchung);
		}
	}
	
	
	/**
	 * @param id Primärschlüssel
	 * @return Item Key im Tabfolder
	 */
	public static String getKey(int id) {
		return "freigaben." + id;
	}
	
	
	@Override
	public String getKey() {
		return getKey(getID());
	}

}
