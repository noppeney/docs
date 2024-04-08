package pze.ui.formulare.messageboard;

import framework.business.interfaces.session.ISession;
import framework.ui.interfaces.controls.ITabFolder;
import framework.ui.interfaces.controls.ITabItem;
import pze.business.UserInformation;
import pze.business.objects.personen.CoPerson;
import pze.business.objects.reftables.CoFreigabeberechtigungen;
import pze.business.objects.reftables.CoMessageGruppe;
import pze.ui.formulare.AbstractAktionCenterMainForm;



/**
 * Formular für das Messageboard
 * 
 * @author Lisiecki
 *
 */
public class FormMessageboard extends AbstractAktionCenterMainForm {
	private static final String CAPTION = "Messageboard";

	private static String RESID = "form.meldungen";

	private static AbstractFormMessageboard m_formMessageboardMitarbeiter;
	private static AbstractFormMessageboard m_formMessageboardVerwaltung;
	private static AbstractFormMessageboard m_formMessageboardAl;
	
	private static AbstractFormMessageboard m_formMessageboardProjektcontrolling;
	
	private static AbstractFormMessageboard m_formMessageboardProjekteMitarbeiter;
	private static AbstractFormMessageboard m_formMessageboardProjektverfolgung;

	private static AbstractFormMessageboard m_formMessageboardVerletzerliste;
	private static AbstractFormMessageboard m_formMessageboardSekretariat;
	private static AbstractFormMessageboard m_formMessageboardDrInfo;

	
	/**
	 * Konstruktor
	 * @param parent		visueller Parent
	 * @throws Exception
	 */
	private FormMessageboard(Object parent) throws Exception {
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
		FormMessageboard formFreigabecenter;
		
		key = getKey(0);
		
		editFolder = session.getMainFrame().getEditFolder();
		tabItem = editFolder.get(key);

		if(tabItem == null)
		{
			tabItem = editFolder.add(CAPTION, key, null, true);
			tabItem.setBitmap("lib.info");
			subTabFolder = tabItem.getSubFolder();
			editFolder.setSelection(key);
			
			// weitere Reiter hinzufügen
			formFreigabecenter = new FormMessageboard(subTabFolder);
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
		
		// Messageboard Mitarbeiter
		m_formMessageboardMitarbeiter = new FormMessageboardMitarbeiter(m_subTabFolder, this);
		m_subTabFolder.add(FormMessageboardMitarbeiter.RESID, FormMessageboardMitarbeiter.getKey(0), m_formMessageboardMitarbeiter, false);			
		m_subTabFolder.get(FormMessageboardMitarbeiter.getKey(0)).setBitmap("user");
		keyFirstSubFolder = FormMessageboardMitarbeiter.getKey(0);

		// Messageboard Personalverwaltung
		isVerwaltung = UserInformation.getInstance().isPersonalverwaltungOhneAdmin();
		if (isVerwaltung)
		{
			m_formMessageboardVerwaltung = new FormMessageboardVerwaltung(m_subTabFolder, this);
			m_subTabFolder.add(FormMessageboardVerwaltung.RESID, FormMessageboardVerwaltung.getKey(0), m_formMessageboardVerwaltung, false);			

			if (UserInformation.getInstance().isDrInfo())
			{
				m_formMessageboardDrInfo = new FormMessageboardDrInfo(m_subTabFolder, this);
				m_subTabFolder.add(FormMessageboardDrInfo.RESID, FormMessageboardDrInfo.getKey(0), m_formMessageboardDrInfo, false);			
			}

			m_formMessageboardVerletzerliste = new FormMessageboardVerletzermeldungen(m_subTabFolder, this, 
					isVerwaltung ? CoMessageGruppe.ID_VERWALTUNG : CoMessageGruppe.ID_SEKRETAERIN);
			m_subTabFolder.add(FormMessageboardVerletzermeldungen.RESID_MESSAGE, FormMessageboardVerletzermeldungen.getKey(0), m_formMessageboardVerletzerliste, false);
		}
		
		// Messageboard AL
		isAl = CoFreigabeberechtigungen.hasBerechtigungen(personID);
		if (isAl)
		{
			m_formMessageboardAl = new FormMessageboardAL(m_subTabFolder, this);
			m_subTabFolder.add(FormMessageboardAL.RESID, FormMessageboardAL.getKey(0), m_formMessageboardAl, false);			
		}
		
		// Messageboard Projektcontrolling
		if (UserInformation.getInstance().isProjektcontrolling())
		{
			m_formMessageboardProjektcontrolling = new FormMessageboardProjektcontrolling(m_subTabFolder, this);
			m_subTabFolder.add(FormMessageboardProjektcontrolling.RESID, FormMessageboardProjektcontrolling.getKey(0), 
					m_formMessageboardProjektcontrolling, false);			
			m_subTabFolder.get(FormMessageboardProjektcontrolling.getKey(0)).setBitmap("page.find");
		}
		
		// Messageboard Projektzuordnung
		m_formMessageboardProjekteMitarbeiter = new FormMessageboardProjekteMitarbeiter(m_subTabFolder, this);
		m_subTabFolder.add(FormMessageboardProjekteMitarbeiter.RESID, FormMessageboardProjekteMitarbeiter.getKey(0), 
				m_formMessageboardProjekteMitarbeiter, false);			

		// Messageboard Projektverfolgung
		m_formMessageboardProjektverfolgung = new FormMessageboardProjektverfolgung(m_subTabFolder, this, CoMessageGruppe.ID_MITARBEITER);
		m_subTabFolder.add(FormMessageboardProjektverfolgung.RESID_MESSAGE, FormMessageboardProjektverfolgung.getKey(0), 
				m_formMessageboardProjektverfolgung, false);			

		// Messageboard Verletzerliste
		if (UserInformation.getInstance().isSekretariatOhneVerwaltung())
		{
			m_formMessageboardSekretariat = new FormMessageboardSekretariat(m_subTabFolder, this);
			m_subTabFolder.add(FormMessageboardSekretariat.RESID, FormMessageboardSekretariat.getKey(0), m_formMessageboardSekretariat, false);
			
			m_formMessageboardVerletzerliste = new FormMessageboardVerletzermeldungen(m_subTabFolder, this, 
					isVerwaltung ? CoMessageGruppe.ID_VERWALTUNG : CoMessageGruppe.ID_SEKRETAERIN);
			m_subTabFolder.add(FormMessageboardVerletzermeldungen.RESID_MESSAGE, FormMessageboardVerletzermeldungen.getKey(0), m_formMessageboardVerletzerliste, false);
		}
		
		return keyFirstSubFolder;
	}


	/**
	 * @param id Primärschlüssel
	 * @return Item Key im Tabfolder
	 */
	public static String getKey(int id) {
		return "messageboard." + id;
	}
	
	
	@Override
	public String getKey() {
		return getKey(getID());
	}

}
