package pze.ui.formulare.person;

import java.util.Date;

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
import pze.business.export.ExportDienstreiseantragListener;
import pze.business.objects.AbstractCacheObject;
import pze.business.objects.dienstreisen.CoDienstreise;
import pze.business.objects.personen.CoPerson;
import pze.business.objects.reftables.buchungen.CoStatusBuchung;
import pze.business.objects.reftables.buchungen.CoStatusGenehmigung;
import pze.ui.controls.SortedTableControl;
import pze.ui.formulare.UniFormWithSaveLogic;


/**
 * Formular der Rechte einer Person zur Verwaltung von Personen bestimmter Gruppen
 * 
 * @author Lisiecki
 *
 */
public class FormPersonDienstreisen extends UniFormWithSaveLogic { // TODO löschem, geht über Freigabecenter
	public static final String RESID = "form.person.dienstreisen";
//	private final static String RESID_ANSICHT = "dialog.buchung.ansicht"; so machen wenn auswertung mit dazu kommt
//	private final static String RESID_EINGABE = "dialog.buchung.eingabe";

	private SortedTableControl m_table;
	
	private CoPerson m_coPerson;
	private CoDienstreise m_coDienstreise;
	private CoDienstreise m_coDienstreiseAntrag;
			
	private UniFormWithSaveLogic m_formPerson;
	
	private IButtonControl m_btBearbeiten;
	private IButtonControl m_btAntrag;
	private IButtonControl m_btLoeschen;
	private IButtonControl m_btGenehmigen;
	private IButtonControl m_btAktualisieren;

//	protected ComboControl m_comboAbteilung;
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
			name = "Auswertung Dienstreisen";

			FormPersonDienstreisen m_formAuswertung = new FormPersonDienstreisen(editFolder, null, null);
			item = editFolder.add(name, key, m_formAuswertung, true);
			item.setBitmap(m_formAuswertung.getCoDienstreise().getNavigationBitmap());
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
	public FormPersonDienstreisen(Object parent, CoPerson coPerson, UniFormWithSaveLogic formPerson) throws Exception {
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
		m_table = new SortedTableControl(findControl("spread.person.dienstreisen")){

			@Override
			public void tableDefaultSelected(IControl arg0, Object arg1) throws Exception {
				FormPerson formPerson;

				formPerson = FormPerson.open(getSession(), null, (m_coDienstreise).getPersonID());
				if (formPerson != null)
				{
					formPerson.showZeiterfassung(m_coDienstreise.getDatum());
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

		m_btAntrag = (IButtonControl) findControl(getResID() + ".antrag");
		if (m_btAntrag != null)
		{
			m_btAntrag.setSelectionListener(new ISelectionListener() {

				@Override
				public void selected(IControl control, Object params) {
					try 
					{
						clickedAntrag();
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

		m_btLoeschen = (IButtonControl) findControl(getResID() + ".loeschen");
		if (m_btLoeschen != null)
		{
			m_btLoeschen.setSelectionListener(new ISelectionListener() {

				@Override
				public void selected(IControl control, Object params) {
					try 
					{
						clickedSetStatus(CoStatusGenehmigung.STATUSID_ABGELEHNT);
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

		m_btGenehmigen = (IButtonControl) findControl(getResID() + ".genehmigen");
		if (m_btGenehmigen != null)
		{
			m_btGenehmigen.setSelectionListener(new ISelectionListener() {

				@Override
				public void selected(IControl control, Object params) {
					try 
					{
						clickedSetStatus(CoStatusGenehmigung.STATUSID_GENEHMIGT);
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
		CoDienstreise coDienstreise;

		// prüfen, ob ein Eintrag ausgewählt ist
		if (!m_coDienstreise.moveTo(m_table.getSelectedBookmark()))
		{
			return;
		}

		// Dienstreise öffnen
		coDienstreise = new CoDienstreise();
		coDienstreise.loadByID(m_coDienstreise.getID());
//		coBuchung.getStatusGenehmigungID()
//		DialogDienstreise.showDialog(coDienstreise);
		
		// aktualisieren
		clickedAktualisieren();
	}

	
	/**
	 * Dienstreiseantrag erstellen
	 * 
	 * @throws Exception
	 */
	private void clickedAntrag() throws Exception {
		int kundeID, aktKundeID, auftragID, aktAuftragID, abrufID, aktAbrufID, kostenstelleID, aktKostenstelleID;
		String fehlertext;
		Date datum, aktDatum, datumHeute;
		Date datumAnfang, datumEnde; // TODO
	
		// prüfen, ob ein Eintrag ausgewählt ist
		if (!m_coDienstreise.moveTo(m_table.getSelectedBookmark()))
		{
			return;
		}
		
		m_coDienstreiseAntrag = new CoDienstreise();
		m_coDienstreiseAntrag.begin();


		// heutiges Datum
		datumHeute = Format.getDate0Uhr(new Date());
		
		// wenn der Antrag für die Vergangenheit gedruckt werden soll, nur für den Tag, ansonsten alle folgenden Tage mit gleichen Angaben
		datum = m_coDienstreise.getDatum();
		if (datum.before(datumHeute))
		{
			// Zeile kopieren, wenn sie vollständig ist
			m_coDienstreiseAntrag.add();
			m_coDienstreise.copyRow(m_coDienstreiseAntrag);
		}
		else
		{
			// Daten der markierten Dienstreise speichern
			kundeID = m_coDienstreise.getKundeID();
			auftragID = m_coDienstreise.getAuftragID();
			abrufID = m_coDienstreise.getAbrufID();
			kostenstelleID = m_coDienstreise.getKostenstelleID();

			// Kunde, Auftrag, Abruf, Kostenstelle und Datum sortieren // TODO ggf. nach Datum sortieren, wenn es mal zu viele werden
			m_coDienstreise.moveFirst();
			do 
			{
				aktDatum = m_coDienstreise.getDatum();
				aktKundeID = m_coDienstreise.getKundeID();
				aktAuftragID = m_coDienstreise.getAuftragID();
				aktAbrufID = m_coDienstreise.getAbrufID();
				aktKostenstelleID = m_coDienstreise.getKostenstelleID();

				// in diesem else Zweig nur Anträge für die Zukunft 
				if (aktDatum.before(datumHeute))
				{
					continue;
				}
				
				// übereinstimmende Dienstreisen suchen
				if (aktKundeID == kundeID && aktAuftragID == auftragID && aktAbrufID == abrufID && aktKostenstelleID == kostenstelleID)
				{
					// Zeile prüfen
					fehlertext = m_coDienstreise.validate();
					if (fehlertext != null)
					{
						Messages.showErrorMessage("Eingaben unvollständig", fehlertext + " Fehler am " + Format.getString(m_coDienstreise.getDatum()) + ".");
						return;
					}

					// Zeile kopieren, wenn sie vollständig ist
					m_coDienstreiseAntrag.add();
					m_coDienstreise.copyRow(m_coDienstreiseAntrag);
				}
			} while (m_coDienstreise.moveNext());
		}

		
		// nach Datum sortieren
		m_coDienstreiseAntrag.sortByDatum(true);
		
		// Antrag erstellen
		(new ExportDienstreiseantragListener(this)).activate(null);
	}

	
	/**
	 * Status der Dienstreise ändern
	 * @param statusID 
	 * 
	 * @throws Exception
	 */
	private void clickedSetStatus(int statusID) throws Exception {
		CoDienstreise coDienstreise;

		// prüfen, ob ein Eintrag ausgewählt ist
		if (!m_coDienstreise.moveTo(m_table.getSelectedBookmark()))
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
		coDienstreise = new CoDienstreise();
		coDienstreise.loadByID(m_coDienstreise.getID());
		coDienstreise.setStatusID(statusID);
		coDienstreise.save();
		
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
			m_coDienstreise = new CoDienstreise();
			m_coDienstreise.loadAll();
		}
		else
		{
			m_coDienstreise = m_coPerson.getCoDienstreise();
		}
		
//		setData(m_coDienstreise);
//		m_coPerson.addChild(m_coDienstreise);

		m_table.setData(m_coDienstreise);

	}


	public AbstractCacheObject getCoDienstreise() {
		return m_coDienstreise;
	}


	/**
	 * Co mit den Zeilen, die in den Dienstreiseantrag sollen
	 * 
	 * @return
	 */
	public AbstractCacheObject getCoDienstreiseAntrag() {
		return m_coDienstreiseAntrag;
	}


	public CoPerson getCoPerson(){
		return m_coPerson;
	}
	

	/**
	 * Datum der Dienstreise
	 * 
	 * @return
	 */
	public Date getCurrentDatum(){
		return m_coDienstreise.getDatum();
	}


	@Override
	public void refresh(int reason, Object element) {
		super.refresh(reason, element);
		
		// aktualisieren ist immer möglich
		m_btAktualisieren.refresh(reasonEnabled, null);

		// Tabelle ist immer deaktiviert
		m_table.refresh(reasonDisabled, null);

		// Buttons je nach Berechtigung
//		refreshButtons();
//
//		// Buttons erstmal immer aktivieren, später Berechtigungen implementieren
//		m_btBearbeiten.refresh(reasonEnabled, null);
//		m_btAntrag.refresh(reasonEnabled, null);
//		m_btLoeschen.refresh(reasonEnabled, null);
//		m_btGenehmigen.refresh(reasonEnabled, null);
	}


	private void refreshButtons() {
		boolean isPersonalverwaltung, isSelbst;
		boolean isVorlaeufig, isGenehmigt, isGeloescht;
		UserInformation userInformation;

		
		// Buttons sind je nach Berechtigung und Buchungsstatus aktiviert
		try 
		{
			m_btLoeschen.refresh(reasonDisabled, null);
			m_btGenehmigen.refresh(reasonDisabled, null);
			m_btAntrag.refresh(reasonDisabled, null);

			// wenn keine Buchung ausgewählt ist, alle Buttons deaktivieren
			if (!m_coDienstreise.moveTo(m_table.getSelectedBookmark()))
			{
				return;
			}

			userInformation = UserInformation.getInstance();
			isSelbst = userInformation.isPerson(m_coPerson.getID());
			isPersonalverwaltung = userInformation.isPersonalverwaltung() && !isSelbst; // Personalabteilung und Buchung nicht für sich selbst
			
//			isVorlaeufig = m_coDienstreise.isVorlaeufig();
//			isGenehmigt = m_coBuchung.isStatusOK(); // m_coBuchung.isGueltig();
//			isGeloescht = m_coDienstreise.isUngueltig();
			
			// Buchung löschen: vorläufige für sich selbst oder Personalverwaltung wenn nicht schon gelöscht
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
		return "person.dienstreise" + id;
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
