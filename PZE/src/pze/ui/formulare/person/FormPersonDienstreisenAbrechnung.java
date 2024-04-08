package pze.ui.formulare.person;

import framework.business.interfaces.fields.IField;
import framework.business.interfaces.session.ISession;
import framework.ui.interfaces.controls.IButtonControl;
import framework.ui.interfaces.controls.IControl;
import framework.ui.interfaces.controls.ITabFolder;
import framework.ui.interfaces.controls.ITabItem;
import framework.ui.interfaces.selection.ISelectionListener;
import pze.business.Format;
import pze.business.Messages;
import pze.business.UserInformation;
import pze.business.objects.AbstractCacheObject;
import pze.business.objects.dienstreisen.CoDienstreiseAbrechnung;
import pze.business.objects.personen.CoKontowert;
import pze.business.objects.personen.CoPerson;
import pze.business.objects.reftables.CoGrundAenderungKontowert;
import pze.business.objects.reftables.CoStatusKontowert;
import pze.business.objects.reftables.buchungen.CoStatusGenehmigung;
import pze.ui.controls.SortedTableControl;
import pze.ui.formulare.UniFormWithSaveLogic;


/**
 * Formular der Rechte einer Person zur Verwaltung von Personen bestimmter Gruppen
 * 
 * @author Lisiecki
 *
 */
public class FormPersonDienstreisenAbrechnung extends UniFormWithSaveLogic {
	public static final String RESID = "form.person.dienstreisenabrechnung";
//	private final static String RESID_ANSICHT = "dialog.buchung.ansicht"; so machen wenn auswertung mit dazu kommt
//	private final static String RESID_EINGABE = "dialog.buchung.eingabe";

	private SortedTableControl m_table;
	
	private CoPerson m_coPerson;
	private CoDienstreiseAbrechnung m_coDienstreiseAbrechnung;
			
	private UniFormWithSaveLogic m_formPerson;
	
	private IButtonControl m_btBearbeiten;
	private IButtonControl m_btZeitBestaetigen;
	private IButtonControl m_btKostenBestaetigen;
	private IButtonControl m_btAktualisieren;

//	protected ComboControl m_comboAbteilung;
//	protected ComboControl m_comboGruppe;
//	protected ComboControl m_comboPerson;
//	protected ComboControl m_comboPersonenliste;
//	private ComboControl m_comboPosition;
//	private ComboControl m_comboStatusAktivInaktiv;
//	
//	protected IButtonControl m_btAllePersonen;
//	private IButtonControl m_btAllePositionen;
//	private IButtonControl m_btAlleStatusAktivInaktiv;


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

		if (item == null)
		{
			name = "Auswertung Dienstreisen-Abrechnung";

			FormPersonDienstreisenAbrechnung m_formAuswertung = new FormPersonDienstreisenAbrechnung(editFolder, null, null);
			item = editFolder.add(name, key, m_formAuswertung, true);
			item.setBitmap(m_formAuswertung.getCoDienstreise().getNavigationBitmap()); // lib.accounting
		}

		editFolder.setSelection(key);
	}


	/**
	 * Konstruktor
	 * 
	 * @param parent visueller Parent
	 * @param coPerson Co der geloggten Daten
	 * @param formPerson Hauptformular der Person
	 * @throws Exception
	 */
	public FormPersonDienstreisenAbrechnung(Object parent, CoPerson coPerson, UniFormWithSaveLogic formPerson) throws Exception {
		super(parent, RESID, true);
		
		m_formPerson = formPerson;

		m_coPerson = coPerson;
//		m_coDienstreise = m_coPerson.getCoDienstreise();
		
		initFormular();
		initTable();
		
		setData(coPerson);
//		m_coPerson.addChild(m_coDienstreise);

		loadData();

//		m_table.setData(m_coDienstreise);

		refresh(reasonDisabled, null);
	}


	private void initTable() throws Exception {
		m_table = new SortedTableControl(findControl("spread.person.dienstreisenabrechnung")){

			@Override
			public void tableDefaultSelected(IControl arg0, Object arg1) throws Exception {
				FormPerson formPerson;

				formPerson = FormPerson.open(getSession(), null, (m_coDienstreiseAbrechnung).getPersonID());
				if (formPerson != null)
				{
					formPerson.showZeiterfassung(m_coDienstreiseAbrechnung.getDatum());
				}
			}

			@Override
			protected void tableDataChanged(Object bookmark, IField fld) throws Exception {
			}
		};
	}


	/**
	 * Formularfelder und Listener initialisieren
	 * @throws Exception 
	 */
	private void initFormular() throws Exception {
//		m_tfDatumBis = (TextControl) findControl(getResID() + ".datumbis");

//
//		m_comboAbteilung = (ComboControl) findControl(getResID() + ".abteilungid");
//		if (m_comboAbteilung != null)
//		{
//			m_comboAbteilung.setValueChangeListener(new IValueChangeListener() {
//
//				@Override
//				public void valueChanged(IControl control, Object originalValue, Object lastValue, Object currentValue) throws Exception {
//					resetCombosEinschraenkungPerson(control, currentValue);
//				}
//			});
//		}
//
//
//		m_comboPerson = (ComboControl) findControl(getResID() + ".personid");
//		if (m_comboPerson != null)
//		{
//			m_comboPerson.setValueChangeListener(new IValueChangeListener() {
//
//				@Override
//				public void valueChanged(IControl control, Object originalValue, Object lastValue, Object currentValue) throws Exception {
//					resetCombosEinschraenkungPerson(control, currentValue);
//				}
//			});
//		}
//
//
//		m_comboPersonenliste = (ComboControl) findControl(getResID() + ".personenlisteid");
//		if (m_comboPersonenliste != null)
//		{
//			m_comboPersonenliste.setValueChangeListener(new IValueChangeListener() {
//
//				@Override
//				public void valueChanged(IControl control, Object originalValue, Object lastValue, Object currentValue) throws Exception {
//					resetCombosEinschraenkungPerson(control, currentValue);
//				}
//			});
//		}
//
//
//		m_btAllePersonen = (IButtonControl) findControl(getResID() + ".allepersonen");
//		if (m_btAllePersonen != null)
//		{
//			m_btAllePersonen.setSelectionListener(new ISelectionListener() {
//
//				@Override
//				public void selected(IControl control, Object params) {
//					resetCombosEinschraenkungPerson();
//				}
//
//				@Override
//				public void defaultSelected(IControl control, Object params) {
//				}
//			});
//		}
//
//		m_comboPosition = (ComboControl) findControl(getResID() + ".positionid");
//
//		m_btAllePositionen = (IButtonControl) findControl(getResID() + ".allepositionen");
//		if (m_btAllePositionen != null)
//		{
//			m_btAllePositionen.setSelectionListener(new ISelectionListener() {
//
//				@Override
//				public void selected(IControl control, Object params) {
//					m_comboPosition.getField().setValue(null);
//
//					refresh(reasonDataChanged, null);
//				}
//
//				@Override
//				public void defaultSelected(IControl control, Object params) {
//				}
//			});
//		}
//
//		m_comboStatusAktivInaktiv = (ComboControl) findControl(getResID() + ".statusaktivinaktivid");
//
//		m_btAlleStatusAktivInaktiv = (IButtonControl) findControl(getResID() + ".allestatusaktivinaktiv");
//		if (m_btAlleStatusAktivInaktiv != null)
//		{
//			m_btAlleStatusAktivInaktiv.setSelectionListener(new ISelectionListener() {
//
//				@Override
//				public void selected(IControl control, Object params) {
//					m_comboStatusAktivInaktiv.getField().setValue(null);
//
//					refresh(reasonDataChanged, null);
//				}
//
//				@Override
//				public void defaultSelected(IControl control, Object params) {
//				}
//			});
//		}

		m_btBearbeiten = (IButtonControl) findControl(getResID() + ".bearbeiten");
		if (m_btBearbeiten != null)
		{
			m_btBearbeiten.setSelectionListener(new ISelectionListener() {

				@Override
				public void selected(IControl control, Object params) {
					try 
					{
						clickedBearbeiten();
					} 
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}

				@Override
				public void defaultSelected(IControl control, Object params) {
				}
			});
		}

		m_btZeitBestaetigen = (IButtonControl) findControl(getResID() + ".arbeitszeitbestaetigen");
		if (m_btZeitBestaetigen != null)
		{
			m_btZeitBestaetigen.setSelectionListener(new ISelectionListener() {

				@Override
				public void selected(IControl control, Object params) {
					try 
					{
						clickedZeitBestaetigen();
					} 
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}

				@Override
				public void defaultSelected(IControl control, Object params) {
				}
			});
		}

		m_btKostenBestaetigen = (IButtonControl) findControl(getResID() + ".kostenbestaetigen");
		if (m_btKostenBestaetigen != null)
		{
			m_btKostenBestaetigen.setSelectionListener(new ISelectionListener() {

				@Override
				public void selected(IControl control, Object params) {
					try 
					{
						clickedKostenBestaetigen();
					} 
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}

				@Override
				public void defaultSelected(IControl control, Object params) {
				}
			});
		}

		m_btAktualisieren = (IButtonControl) findControl(getResID() + ".aktualisieren");
		if (m_btAktualisieren != null)
		{
			m_btAktualisieren.setSelectionListener(new ISelectionListener() {

				@Override
				public void selected(IControl control, Object params) {
					try 
					{
						clickedAktualisieren();
					} 
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}

				@Override
				public void defaultSelected(IControl control, Object params) {
				}
			});
		}
	}

	
	/**
	 * Formular zum Bearbeiten der Dienstreise öffnen
	 * 
	 * @throws Exception
	 */
	private void clickedBearbeiten() throws Exception {
		CoDienstreiseAbrechnung coDienstreiseAbrechnung;

		// prüfen, ob ein Eintrag ausgewählt ist
		if (!m_coDienstreiseAbrechnung.moveTo(m_table.getSelectedBookmark()))
		{
			return;
		}

		// Dienstreise öffnen
		coDienstreiseAbrechnung = new CoDienstreiseAbrechnung();
		coDienstreiseAbrechnung.loadByID(m_coDienstreiseAbrechnung.getID());
		DialogDienstreiseAbrechnung.showDialog(coDienstreiseAbrechnung);
		
		// aktualisieren
		clickedAktualisieren();
	}

	
	/**
	 * Status der Dienstreise ändern
	 * 
	 * @throws Exception
	 */
	private void clickedZeitBestaetigen() throws Exception {
		CoKontowert coKontowert;

		// prüfen, ob ein Eintrag ausgewählt ist
		if (!m_coDienstreiseAbrechnung.moveTo(m_table.getSelectedBookmark()))
		{
			return;
		}

		// Sicherheitsabfrage
		if (!Messages.showYesNoMessage("Arbeitszeit bestätigen", "Soll die Arbeitszeit für " + m_coDienstreiseAbrechnung.getPerson() + " am "
				+ Format.getString(m_coDienstreiseAbrechnung.getDatum()) + " wirklich geändert werden?"))
		{
			return;
		}
		
		// Zeiten für den Tag in virtuellen Feldern berechnen // TODO löschen wenn die in der Tabelle angezeigt werden, dann wurden sie bereits berechnet
		m_coDienstreiseAbrechnung.aktualisiereZeiten();

		// Kontowerte für den Tag laden
		coKontowert = new CoKontowert();
		coKontowert.load(m_coDienstreiseAbrechnung.getPersonID(), m_coDienstreiseAbrechnung.getDatum());
		
		// Kontowerte anpassen
		coKontowert.begin();
		coKontowert.setWertArbeitszeit(m_coDienstreiseAbrechnung.getAnrechenbareArbeitszeit());
		coKontowert.setWertReisezeit(m_coDienstreiseAbrechnung.getAnrechenbareReisezeit());
		
		// Zeiten aktualisieren
		coKontowert.updateArbeitszeitMonat();
		coKontowert.updateUeberstunden();
		coKontowert.updateUeberstundenSummen();
		
		// Änderungen speichern
		coKontowert.setGrundAenderungID(CoGrundAenderungKontowert.ID_ZEITERFASSUNG_DIENSTREISE);
		coKontowert.setGeaendertVonID(UserInformation.getInstance().getPersonID());
		coKontowert.setGeaendertAm(Format.getStringMitUhrzeit(Format.getGregorianCalendar(null)));
		coKontowert.setStatusID(CoStatusKontowert.STATUSID_GEAENDERT);
		coKontowert.save();
		
		// aktualisieren
		clickedAktualisieren();
	}

	
	/**
	 * Status der Dienstreise ändern
	 * 
	 * @throws Exception
	 */
	private void clickedKostenBestaetigen() throws Exception {
		CoDienstreiseAbrechnung coDienstreiseAbrechnung;

		// prüfen, ob ein Eintrag ausgewählt ist
//		if (!m_coDienstreiseAbrechnung.moveTo(m_table.getSelectedBookmark()))
//		{
//			return;
//		}
//
//		// Sicherheitsabfrage
//		if (!Messages.showYesNoMessage("Status ändern", "Soll der Status wirklich auf '" 
//				+ CoStatusDienstreise.getInstance().getBezeichnung(statusID) + "' geändert werden?")) // TODO DB-Tabelle mit neuer Version löschen
//		{
//			return;
//		}
//
//		// Status ändern
//		coDienstreiseAbrechnung = new CoDienstreiseAbrechnung();
//		coDienstreiseAbrechnung.loadByID(m_coDienstreiseAbrechnung.getID());
//		coDienstreiseAbrechnung.setStatusID(statusID);
//		coDienstreiseAbrechnung.save();
		
		// aktualisieren
		clickedAktualisieren();
	}

	
	/**
	 * Status der Dienstreise ändern
	 * @param statusID 
	 * 
	 * @throws Exception
	 */
	private void clickedSetStatus(int statusID) throws Exception {
		CoDienstreiseAbrechnung coDienstreiseAbrechnung;

		// prüfen, ob ein Eintrag ausgewählt ist
		if (!m_coDienstreiseAbrechnung.moveTo(m_table.getSelectedBookmark()))
		{
			return;
		}

		// Sicherheitsabfrage
		if (!Messages.showYesNoMessage("Status ändern", "Soll der Status wirklich auf '" 
				+ CoStatusGenehmigung.getInstance().getBezeichnung(statusID) + "' geändert werden?"))
		{
			return;
		}

		// Status ändern
		coDienstreiseAbrechnung = new CoDienstreiseAbrechnung();
		coDienstreiseAbrechnung.loadByID(m_coDienstreiseAbrechnung.getID());
		coDienstreiseAbrechnung.setStatusID(statusID);
		coDienstreiseAbrechnung.save();
		
		// aktualisieren
		clickedAktualisieren();
	}

	
	/**
	 * Einstellungen speichern und Daten neu laden
	 * 
	 * @throws Exception
	 */
	private void clickedAktualisieren() throws Exception {
		// Angaben zur Auswertung speichern
//		m_coAuswertung.save();
//		m_coAuswertung.begin();

		// Tabelle neu laden
		loadData();
	}


	/**
	 * Daten laden und der Tabelle zuweisen
	 * 
	 * @throws Exception
	 */
	protected void loadData() throws Exception {
		
		if (m_coPerson == null)
		{
			m_coDienstreiseAbrechnung = new CoDienstreiseAbrechnung();
			m_coDienstreiseAbrechnung.loadAll();
		}
		else
		{
			m_coDienstreiseAbrechnung = m_coPerson.getCoDienstreiseAbrechnung();
		}
		
//		setData(m_coDienstreise);
//		m_coPerson.addChild(m_coDienstreise);

		m_table.setData(m_coDienstreiseAbrechnung);

	}


	public AbstractCacheObject getCoDienstreise() {
		return m_coDienstreiseAbrechnung;
	}


	@Override
	public void refresh(int reason, Object element) {
		super.refresh(reason, element);
		
		// aktualisieren ist immer möglich
		m_btAktualisieren.refresh(reasonEnabled, null);

		// Tabelle ist immer deaktiviert
		m_table.refresh(reasonDisabled, null);

		// Buttons je nach Berechtigung
		refreshButtons();

		// Buttons erstmal immer aktivieren, später Berechtigungen implementieren
		m_btBearbeiten.refresh(reasonEnabled, null);
		m_btZeitBestaetigen.refresh(reasonEnabled, null);
		m_btKostenBestaetigen.refresh(reasonEnabled, null);
	}


	private void refreshButtons() {
		boolean isPersonalverwaltung, isSelbst;
		boolean isVorlaeufig, isGenehmigt, isGeloescht;
		UserInformation userInformation;

		
		// Buttons sind je nach Berechtigung und Buchungsstatus aktiviert
		try 
		{
			m_btBearbeiten.refresh(reasonDisabled, null);
			m_btZeitBestaetigen.refresh(reasonDisabled, null);
			m_btKostenBestaetigen.refresh(reasonDisabled, null);

			// wenn keine Buchung ausgewählt ist, alle Buttons deaktivieren
//			if (!m_coDienstreise.moveTo(m_table.getSelectedBookmark()))
//			{
//				return;
//			}
//
//			userInformation = UserInformation.getInstance();
//			isSelbst = userInformation.isPerson(m_coPerson.getID());
//			isPersonalverwaltung = userInformation.isPersonalverwaltung() && !isSelbst; // Personalabteilung und Buchung nicht für sich selbst
//			
//			isVorlaeufig = m_coDienstreise.isVorlaeufig();
////			isGenehmigt = m_coBuchung.isStatusOK(); // m_coBuchung.isGueltig();
//			isGeloescht = m_coDienstreise.isUngueltig();
//			
//			// Buchung löschen: vorläufige für sich selbst oder Personalverwaltung wenn nicht schon gelöscht
//			if ((isSelbst && isVorlaeufig) || (isPersonalverwaltung && !isGeloescht))
//			{
//				m_btLoeschen.refresh(reasonEnabled, null);
//			}
//
//			// Buchung genehmigen: Personalverwaltung wenn nicht schon genehmigt
//			if (isPersonalverwaltung)// && !isGenehmigt) temporär, bis alle alten Buchungen korrigiert sind
//			{
//				m_btGenehmigen.refresh(reasonEnabled, null);
//			}
//
//			// Antrag drucken: Personalverwaltung oder selbst für vorläufige
//			if (isPersonalverwaltung || isVorlaeufig)
//			{
//				m_btAntrag.refresh(reasonEnabled, null);
//			}
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * @param id Primärschlüssel
	 * @return Item Key im Tabfolder
	 */
	public static String getKey(int id) {
		return "person.dienstreiseabrechnung" + id;
	}
	
	
	@Override
	public String getKey() {
		return getKey(getID());
	}
	
//
//	@Override
//	public void activate() {
//		
//		if (NavigationManager.getSelectedTabItemKey() != null && NavigationManager.getSelectedTabItemKey().equals(getResID()))
//		{
//			m_formPerson.addSaveHandler();
//			refreshByEditMode();
//			super.activate();
//		}
//	}
//	
//	
//	@Override
//	public void deactivate() {
//		m_formPerson.removeSaveHandler();
//		super.deactivate();
//	}


}
