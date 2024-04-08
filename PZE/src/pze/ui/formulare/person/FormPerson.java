package pze.ui.formulare.person;

import java.util.Calendar;
import java.util.Date;

import framework.Application;
import framework.business.interfaces.session.ISession;
import framework.business.session.Session;
import framework.ui.button.Button;
import framework.ui.form.Scroller;
import framework.ui.interfaces.controls.IButtonControl;
import framework.ui.interfaces.controls.IControl;
import framework.ui.interfaces.controls.ITabFolder;
import framework.ui.interfaces.controls.ITabItem;
import framework.ui.interfaces.selection.ISelectionListener;
import pze.business.Format;
import pze.business.Messages;
import pze.business.UserInformation;
import pze.business.export.ExportAuszahlungUeberstundenListener;
import pze.business.navigation.NavigationBaseNode;
import pze.business.navigation.NavigationManager;
import pze.business.navigation.treeloader.TreeLoaderPersonen;
import pze.business.objects.CoVerletzerliste;
import pze.business.objects.personen.CoKontowert;
import pze.business.objects.personen.CoPerson;
import pze.business.objects.reftables.buchungen.CoStatusAuszahlung;
import pze.ui.controls.SortedTableControl;
import pze.ui.formulare.UniFormWithSaveLogic;
import pze.ui.formulare.person.monatseinsatzblatt.FormPersonMonatseinsatzblatt;

/**
 * Personenformular
 * 
 * @author Lisiecki
 *
 */
public class FormPerson extends UniFormWithSaveLogic {

	public static String RESID_ALLGEMEIN = "form.person.allgemein";
	public static String RESID_VERWALTUNG = "form.person.verwaltung";
	public static String RESID_ZEITMODELL = "form.person.zeitmodell";

	// Formulare
	private FormPersonVerwaltung m_formPersonVerwaltung;
	private FormPersonZeitmodelle m_formPersonZeitmodelle;
	private FormPersonAbteilungsrechte m_formPersonAbteilungsrechte;
	private FormPersonZeiterfassung m_formPersonZeiterfassung;
	private FormPersonMonatseinsatzblatt m_FormPersonMonatseinsatzblatt;
	private FormPersonDienstreisen m_formPersonDienstreisen; 
//	private FormPersonDienstreisenAbrechnung m_formPersonDienstreisenAbrechnung; 
	private FormPersonUrlaubsplanung m_formPersonUrlaubsplanung; 

	private SortedTableControl m_tableVerletzungen;
	private SortedTableControl m_tableAuszahlungen;

	private IButtonControl m_btAuszahlungEintragen;
	private IButtonControl m_btAuszahlungsAntrag;
	
	private CoPerson m_coPerson;
	private CoVerletzerliste m_coVerletzerliste;
	private CoKontowert m_coAuszahlungen;

	private ITabFolder m_subTabFolder;
	
	private boolean m_hasOffeneAuszahlung;
	
	
	
	/**
	 * Konstruktor
	 * @param node			Navigationskonten
	 * @param parent		visueller Parent
	 * @param coPerson	 zu editierendes CacheObject
	 * @throws Exception
	 */
	public FormPerson(Object parent, CoPerson coPerson) throws Exception {
		super(parent, RESID_ALLGEMEIN);
		m_coPerson = coPerson;
		setData(m_coPerson);		

		m_hasOffeneAuszahlung = false;

//		initTableBuchungen();
//		initTableBuchungenAktuell();
		initTableVerletzerliste();
		initTableAuszahlungen();

//		initBtBuchungBeantragen();
//		initBtAntragLoeschen();
//		initBtBeantragteBuchungen();
		
		initBtAuszahlungEintragen();
		initBtAuszahlungsAntrag();
		
		
		// Info-Message bei offenen Buchungen
//		showInfoOffeneBuchungen();

		refresh(reasonDisabled, null);
	}


	/**
	 * Info, wenn für die Vergangenheit oder heute offene, nicht beantragte Buchungen bestehen
	 * @throws Exception 
	 */
//	private void showInfoOffeneBuchungen() throws Exception {
//		Date datum;
//		GregorianCalendar gregDatum, gregDatumHeute;
//		
//		if (m_coBuchung.moveFirst() && m_coBuchung.isSelbstbuchung())
//		{
//			// offene Buchungen vorhanden
//			datum = m_coBuchung.getDatum();
//			if (datum == null)
//			{
//				return;
//			}
//			
//			// Datum um eine Woche verschoben, um Buchungen der nächsten Woche abzufragen
//			gregDatum = Format.getGregorianCalendar(Format.getDateVerschoben(Format.getDate12Uhr(datum), 0));
//			gregDatumHeute = Format.getGregorianCalendar(Format.getDate12Uhr(new Date()));
//
//			if (!gregDatum.after(gregDatumHeute))
//			{
//				Messages.showInfoMessage("Bitte geplante Anträge prüfen", "Sie haben geplante Anträge für die Vergangenheit oder heute.<br>"
//						+ "Bitte Anträge prüfen und Freigabe beantragen oder die entsprechenden Anträge löschen.");
//			}
//		}
//	}


//	private void initTableBuchungen() throws Exception {
//		m_tableBuchungen = new SortedTableControl(findControl("spread.person.allgemein.buchungen")){
//
//			@Override
//			public void tableSelected(IControl arg0, Object arg1){
//				super.tableSelected(arg0, arg1);
//				
//				m_coBuchung.moveTo(m_tableBuchungen.getSelectedBookmark());
//				refreshBtBuchungBeantragen();
//			}
//
//			@Override
//			public void tableDefaultSelected(IControl arg0, Object arg1) throws Exception{
//				// Zeiterfassung für den Tag öffnen
//				showZeiterfassung(m_coBuchung.getDatum());
//			}
//		};
//		
//		m_coBuchung = new CoBuchung();
//		
//		reloadTableBuchungen();
//	}


//	private void initTableBuchungenAktuell() throws Exception {
//		m_tableBuchungenAktuell = new SortedTableControl(findControl("spread.person.allgemein.buchungen.aktuell")){
//
//			@Override
//			public void tableSelected(IControl arg0, Object arg1){
//				super.tableSelected(arg0, arg1);
//				
//				m_coBuchungAktuell.moveTo(m_tableBuchungenAktuell.getSelectedBookmark());
//				refreshBtBuchungBeantragen();
//			}
//
//			@Override
//			public void tableDefaultSelected(IControl arg0, Object arg1) throws Exception{
//				// Zeiterfassung für den Tag öffnen
//				showZeiterfassung(m_coBuchungAktuell.getDatum());
//			}
//		};
//		
//		m_coBuchungAktuell = new CoBuchung();
//		
//		reloadTableBuchungenAktuell();
//	}


	private void initTableVerletzerliste() throws Exception {
		m_tableVerletzungen = new SortedTableControl(findControl("spread.person.allgemein.verletzerliste")){

			@Override
			public void tableDefaultSelected(IControl arg0, Object arg1) throws Exception{
				// Zeiterfassung für den Tag öffnen
				showZeiterfassung(m_coVerletzerliste.getDatum());
			}
		};
		
		m_coVerletzerliste = new CoVerletzerliste();
		
		reloadTableVerletzerliste();
	}


	private void initTableAuszahlungen() throws Exception {
		m_tableAuszahlungen = new SortedTableControl(findControl("spread.person.allgemein.auszahlung")){

			@Override
			public void tableSelected(IControl arg0, Object arg1){
				super.tableSelected(arg0, arg1);
				
				m_coAuszahlungen.moveTo(m_tableAuszahlungen.getSelectedBookmark());
				refreshBtAuszahlungEintragen();
				refreshBtAntragAuszahlungDrucken();
			}

			@Override
			public void tableDefaultSelected(IControl arg0, Object arg1) throws Exception{
				// Zeiterfassung für den Tag öffnen
//				showZeiterfassung(m_coVerletzerliste.getDatum());
			}
		};
		
		m_coAuszahlungen = new CoKontowert();
		
		reloadTableAuszahlungen();
	}


//	private void initBtBuchungBeantragen() {
//		m_btBuchungBeantragen = (IButtonControl) findControl("form.person.allgemein.buchungen.beantragen");
//		if (m_btBuchungBeantragen != null)
//		{
//			m_btBuchungBeantragen.setSelectionListener(new ISelectionListener() {
//
//				@Override
//				public void selected(IControl control, Object params) {
//					try 
//					{
//						if (Messages.showYesNoMessage("Freigabe beantragen", "Möchten Sie die Freigabe des Antrags '" + m_coBuchung.getBuchungsart() + "' am "
//								+ Format.getString(m_coBuchung.getDatum()) + " wirklich beantragen?"))
//						{
//							// AL dürfen direkt die Freigabe für sich selbst erstellen 
//							if (UserInformation.getInstance().isGruppeOfaOhneGenehmigung())
//							{
//								m_coBuchung.createFreigabeGenehmigt();
//								Messages.showInfoMessage("Buchung genehmigt", "Ihre Buchung wurde automatisch genehmigt.");
//							}
//							else
//							{
//								m_coBuchung.createFreigabeBeantragt();
//							}
//							reloadTableBuchungen();
//							reloadTableBuchungenAktuell();
//						}
//					} 
//					catch (Exception e)
//					{
//						e.printStackTrace();
//					}
//				}
//
//	
//				@Override
//				public void defaultSelected(IControl control, Object params) {
//				}
//			});
//		}
//	}
//
//
//	private void initBtAntragLoeschen() {
//		m_btAntragLoeschen = (IButtonControl) findControl("form.person.allgemein.buchungen.loeschen");
//		if (m_btAntragLoeschen != null)
//		{
//			m_btAntragLoeschen.setSelectionListener(new ISelectionListener() {
//
//				@Override
//				public void selected(IControl control, Object params) {
//					try 
//					{
//						if (Messages.showYesNoMessage("Antrag löschen", "Möchten Sie den Antrag '" + m_coBuchung.getBuchungsart() + "' am "
//								+ Format.getString(m_coBuchung.getDatum()) + " wirklich löschen?"))
//						{
//							m_coBuchung.deleteAntrag();
//							reloadTableBuchungen();
//							reloadTableBuchungenAktuell();
//						}
//					} 
//					catch (Exception e)
//					{
//						e.printStackTrace();
//					}
//				}
//
//	
//				@Override
//				public void defaultSelected(IControl control, Object params) {
//				}
//			});
//		}
//	}
//
//
//	private void initBtBeantragteBuchungen() {
//		m_btBeantragteBuchungen = (IButtonControl) findControl("form.person.allgemein.buchungen.anzeigen");
//		if (m_btBeantragteBuchungen != null)
//		{
//			m_btBeantragteBuchungen.setSelectionListener(new ISelectionListener() {
//
//				@Override
//				public void selected(IControl control, Object params) {
//					try 
//					{
//						DialogBuchungenBeantragt.showDialog(m_coPerson.getID());
//					} 
//					catch (Exception e)
//					{
//						e.printStackTrace();
//					}
//				}
//
//	
//				@Override
//				public void defaultSelected(IControl control, Object params) {
//				}
//			});
//		}
//	}


	private void initBtAuszahlungEintragen() {
		m_btAuszahlungEintragen = (IButtonControl) findControl("form.person.allgemein.auszahlung.bestaetigen");
		if (m_btAuszahlungEintragen != null)
		{
			m_btAuszahlungEintragen.setSelectionListener(new ISelectionListener() {

				@Override
				public void selected(IControl control, Object params) {
					try 
					{
						showDialogAuszahlung();
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


	private void initBtAuszahlungsAntrag() {
		m_btAuszahlungsAntrag = (Button) findControl("form.person.allgemein.auszahlung.antragdrucken");
		if (m_btAuszahlungsAntrag != null)
		{
			m_btAuszahlungsAntrag.setSelectionListener(new ISelectionListener() {

				@Override
				public void selected(IControl control, Object params) {
					try 
					{
						// PDF erstellen, richtigen Eintrag zur Sicherheit nochmal markieren
	 					m_coAuszahlungen.moveTo(m_tableAuszahlungen.getSelectedBookmark());
	 					new ExportAuszahlungUeberstundenListener(m_coAuszahlungen).activate(null);
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
	 * Formular öffnen oder selektieren, wenn noch nicht vorhanden
	 * 
	 * @param session Session
	 * @param node Knoten in Navigation
	 * @throws Exception
	 */
	public static void open(ISession session, NavigationBaseNode node) throws Exception {
		int id = (node==null ? -1 : node.getID());
		
		open(session, node, id);
	}

	
	static long time;
	/**
	 * Formular öffnen oder selektieren, wenn noch nicht vorhanden
	 * 
	 * @param session Session
	 * @param node Knoten in Navigation
	 * @param personID 
	 * @throws Exception
	 */
	// TODO löschen: Session wird hier nicht mehr benötigt
	public static FormPerson open(ISession session, NavigationBaseNode node, int personID) throws Exception {
		ITabFolder editFolder, subTabFolder;
		String key;
		ITabItem item;
		FormPerson formPerson;
		time = System.currentTimeMillis();

		// prüfen, ob der Personenreiter geöffnet werden darf
		if (personID > 0 && !maySeePerson(personID))
		{
			return null;
		}
		editFolder = Session.getInstance().getMainFrame().getEditFolder();
		key = getKey(personID);
		item = editFolder.get(key);
		System.out.println("1: " + (System.currentTimeMillis() - time));
		time = System.currentTimeMillis();

		if (item == null)
		{
			CoPerson coPerson = new CoPerson();
			String name;

			// neue Person anlegen
			if (personID == -1 )
			{
				coPerson.createNew();
				key = getKey(coPerson.getID());
				name = "<neue Person>";
			}
			else // Daten der Person laden
			{
				coPerson.loadByID(personID);
				name = coPerson.getBezeichnung();
			}
						
			item = editFolder.add(name, key, null,true);
			item.setBitmap(coPerson.getNavigationBitmap());
			subTabFolder = item.getSubFolder();
			editFolder.setSelection(key);	

			// Daten laden
			formPerson = new FormPerson(subTabFolder, coPerson);

			// ggf. Dialog zur Auszahlung von Plusstunden öffnen
			if (!formPerson.checkOffeneAuszahlung())
			{
				// wenn die Angaben nicht korrekt gemacht wurden wird der Reiter nicht geöffnet, Vorschlag muss so nicht umgesetzt werden
				// editFolder.remove(key);
				// return null;
			}
			
			// Reiter erstellen
			subTabFolder.add("Allgemeine Daten", FormPerson.RESID_ALLGEMEIN, formPerson, false);
			formPerson.setSubTabFolder(subTabFolder);

			if (UserInformation.getInstance().isPersonalansicht())
			{
				// Verwaltung
				formPerson.addFormVerwaltung();

	            // Zeitmodelle
				formPerson.addFormZeitmodelle();

				if (UserInformation.getInstance().isAdmin())
				{
					// Abteilungsrechte
					formPerson.addFormAbteilungsrechte();
				}
			}
			System.out.println("2 Person & Verwaltung: " + (System.currentTimeMillis() - time));
			time = System.currentTimeMillis();

			// Personalansicht, AL und aufwärts dürfen den Reiter Zeiterfassung von allen ihnen zugeordneten Personen sehen
			if (UserInformation.getInstance().isAL() || UserInformation.getInstance().isPersonalansicht() 
					|| coPerson.getID() == UserInformation.getPersonID()) // eigene Person
			{
				// Zeiterfassung
				formPerson.addFormZeiterfassung();
			}
			System.out.println("3 Zeiterfassung: " + (System.currentTimeMillis() - time));
			time = System.currentTimeMillis();

			// Monatseinsatzblatt
			formPerson.addFormMonatseinsatzblatt();

			// Sekretariat und AL dürfen Dienstreisen sehen
			if (UserInformation.getInstance().isAL() || UserInformation.getInstance().isPersonalansicht() 
					|| coPerson.getID() == UserInformation.getPersonID()) // eigene Person
			{
				// Dienstreisen
				// TODO DR-Abrechnung
//				if (DialogBuchung.DR)
				{
//					formPerson.addFormDienstreisen();
				}
//				formPerson.addFormDienstreisenAbrechnung();
			}

			// Personalverwaltung darf Urlaubsplanung sehen
			if (UserInformation.getInstance().isPersonalansicht() && coPerson.getID() != UserInformation.getPersonID())
			{
				// Urlaubsplanung
				formPerson.addFormUrlaubsplanung();
			}
			System.out.println("4 Monatseinsatzblatt & folgende: " + (System.currentTimeMillis() - time));

			subTabFolder.setActivateSubFolder(true);
			subTabFolder.setSelection(RESID_ALLGEMEIN);
			
			item.setBitmap(coPerson.getNavigationBitmap());
		}

		editFolder.setSelection(key);

		return (FormPerson) item.getSubFolder().get(RESID_ALLGEMEIN).getControl();
	}


	private void addFormVerwaltung() throws Exception {
		Scroller scroller;
		
		scroller = new Scroller(m_subTabFolder, "scroller.personverwaltung");
		m_formPersonVerwaltung = new FormPersonVerwaltung(scroller, m_coPerson, this);
		scroller.setControl(m_formPersonVerwaltung);
		m_subTabFolder.add("Verwaltung", FormPersonVerwaltung.RESID, scroller, false);			
		addAdditionalForm(m_formPersonVerwaltung);
	}


	private void addFormZeitmodelle() throws Exception {
		m_formPersonZeitmodelle = new FormPersonZeitmodelle(m_subTabFolder, m_coPerson, this);
		m_subTabFolder.add("Zeitmodelle", FormPersonZeitmodelle.RESID, m_formPersonZeitmodelle, false);			
		addAdditionalForm(m_formPersonZeitmodelle);
	}


	private void addFormAbteilungsrechte() throws Exception {
		m_formPersonAbteilungsrechte = new FormPersonAbteilungsrechte(m_subTabFolder, m_coPerson, this);
		m_subTabFolder.add("Abteilungsrechte", FormPersonAbteilungsrechte.RESID, m_formPersonAbteilungsrechte, false);			
		addAdditionalForm(m_formPersonAbteilungsrechte);
	}


	private void addFormZeiterfassung() throws Exception {
		Scroller scroller;
		
		scroller = new Scroller(m_subTabFolder, "scroller.personzeiterfassung");
		m_formPersonZeiterfassung = new FormPersonZeiterfassung(scroller, m_coPerson, this);
		scroller.setControl(m_formPersonZeiterfassung);
		m_subTabFolder.add("Zeiterfassung", FormPersonZeiterfassung.RESID, scroller, false);			
		addAdditionalForm(m_formPersonZeiterfassung);
	}


	private void addFormMonatseinsatzblatt() throws Exception {
		Scroller scroller;
		
		scroller = new Scroller(m_subTabFolder, "scroller.monatseinsatzblatt");
		m_FormPersonMonatseinsatzblatt = new FormPersonMonatseinsatzblatt(scroller, m_coPerson, this, null);
		scroller.setControl(m_FormPersonMonatseinsatzblatt);
		m_subTabFolder.add("Monatseinsatzblatt", FormPersonMonatseinsatzblatt.RESID, scroller, false);			
		addAdditionalForm(m_FormPersonMonatseinsatzblatt);
	}


	/**
	 * Formular hinzufühen
	 * 
	 * @param datum Datum in dem Monat
	 * @throws Exception
	 */
	private void addFormMonatseinsatzblatt(Date datum) throws Exception {
		Scroller scroller;
		
		scroller = new Scroller(m_subTabFolder, "scroller.monatseinsatzblatt");
		m_FormPersonMonatseinsatzblatt = new FormPersonMonatseinsatzblatt(scroller, m_coPerson, this, datum);
		scroller.setControl(m_FormPersonMonatseinsatzblatt);
		m_subTabFolder.add("Monatseinsatzblatt", FormPersonMonatseinsatzblatt.RESID, scroller, false);			
		addAdditionalForm(m_FormPersonMonatseinsatzblatt);

		activate();
	}

	// TODO DR-Abrechnung

	private void addFormDienstreisen() throws Exception {
		m_formPersonDienstreisen = new FormPersonDienstreisen(m_subTabFolder, m_coPerson, this);
		m_subTabFolder.add("Dienstreisen", FormPersonDienstreisen.RESID, m_formPersonDienstreisen, false);			
		addAdditionalForm(m_formPersonDienstreisen);
	}


//	private void addFormDienstreisenAbrechnung() throws Exception {
//		m_formPersonDienstreisenAbrechnung = new FormPersonDienstreisenAbrechnung(m_subTabFolder, m_coPerson, this);
//		m_subTabFolder.add("Dienstreisen-Abrechnung", FormPersonDienstreisenAbrechnung.RESID, m_formPersonDienstreisenAbrechnung, false);			
//		addAdditionalForm(m_formPersonDienstreisenAbrechnung);
//	}


	private void addFormUrlaubsplanung() throws Exception {
		m_formPersonUrlaubsplanung = new FormPersonUrlaubsplanung(m_subTabFolder, m_coPerson, this);
		m_subTabFolder.add("Urlaubsplanung", FormPersonUrlaubsplanung.RESID, m_formPersonUrlaubsplanung, false);			
		addAdditionalForm(m_formPersonUrlaubsplanung);
	}


	private void removeFormMonatseinsatzblatt() throws Exception {
		m_subTabFolder.remove(FormPersonMonatseinsatzblatt.RESID);			
		removeAdditionalForm(m_FormPersonMonatseinsatzblatt);
	}


	/**
	 * Formular Monatseinsatzblatt neu laden wegen geänderter Projekte oder einem anderen Monat.<br>
	 * Beim Aktualisieren treten Probleme auf, deshalb wir das Formular neu erstellt.
	 * 
	 * @param datum Datum in dem Monat
	 * @throws Exception
	 */
	public void reloadFormMonatseinsatzblatt(boolean showMonatseinsatzblatt) throws Exception {
		removeFormMonatseinsatzblatt();	
		addFormMonatseinsatzblatt(m_FormPersonMonatseinsatzblatt.getCurrentDatum());
		
		if (showMonatseinsatzblatt)
		{
			showMonatseinsatzblatt();
		}
		
		activate();
	}


	
	/**
	 * Öffnet das Formular Monatseinsatzblatt
	 * 
	 * @param datum
	 * @throws Exception
	 */
	public void showMonatseinsatzblatt() throws Exception {
		m_subTabFolder.setSelection(FormPersonMonatseinsatzblatt.RESID);
	}

	
	/**
	 * Öffnet das Formular zur Zeiterfassung mit dem übergebenen Datum
	 * 
	 * @param datum
	 * @throws Exception
	 */
	public void showZeiterfassung(Date datum) throws Exception {
		
		// wenn es offene (noch nicht gestellte) Anträge gibt, zeige nicht direkt die Zeiterfassung
//		if (UserInformation.isPerson(m_coPerson.getID()) && m_coBuchung.hasRows())
//		{
//			return;
//		}
		
		m_subTabFolder.setSelection(FormPersonZeiterfassung.RESID);
		m_formPersonZeiterfassung.setDatum(datum);
		m_formPersonZeiterfassung.activate();
	}


	private void setSubTabFolder(ITabFolder subTabFolder) {
		m_subTabFolder = subTabFolder;
	}


	/**
	 * @param id Primärschlüssel
	 * @return Item Key im Tabfolder
	 */
	public static String getKey(int id) {
		return "person." + id;
	}
	
	
	@Override
	public String getKey() {
		return getKey(getID());
	}
	
	
	/**
	 * Tab-Caption anpassen, Baum und RefTable-Items neu laden
	 * 
	 * (non-Javadoc)
	 * @see pze.ui.formulare.UniFormWithSaveLogic#doAfterSave()
	 */
	@Override
	public void doBeforeSave() throws Exception {
		
		// auf untergeordneten Formularen ggf. Aktionen durchführen
		for (UniFormWithSaveLogic addForm : additionalForms)
		{
			addForm.doBeforeSave();
		}
	}

	
	/**
	 * Tab-Caption anpassen, Baum und RefTable-Items neu laden
	 * 
	 * (non-Javadoc)
	 * @see pze.ui.formulare.UniFormWithSaveLogic#doAfterSave()
	 */
	@Override
	public void doAfterSave() throws Exception {
		refreshTabItem();
		
		Application.getRefTableLoader().updateRefItems(getData().getResID());
		
		NavigationManager.getInstance().reloadRootNode(TreeLoaderPersonen.ROOT);
		
		// auf untergeordneten Formularen ggf. Daten neu laden, z. B. Verletzermeldungen
		for (UniFormWithSaveLogic addForm : additionalForms)
		{
			addForm.doAfterSave();
		}
	}


	/**
	 * Prüft, ob offene Auszahlungen für die Person vorliegen, zu der noch Daten eingetragen werden müssen. <br>
	 * Offene Auszahlungen können nur für die eigene Person vorkommen.
	 * 
	 * @return Prüfung OK, der Reiter kann geöffnet werden oder er darf nicht geöffnet werden weil Auszahlung offen sind
	 * @throws Exception
	 */
	private boolean checkOffeneAuszahlung() throws Exception {

		m_hasOffeneAuszahlung = false;

		// offene Auszahlungen werden nur geöffnet, wenn die eine Person den eigenen Personenreiter öffnet
		if (m_coPerson.getID() == UserInformation.getPersonID())
		{
			// aktuelle Auszahlung offen, bei mehreren sollte dies über die Personalabteilung laufen
			if (m_coAuszahlungen.moveFirst() && m_coAuszahlungen.getStatusIDAuszahlung() == 0)
			{
				if (!showDialogAuszahlung() && Format.getGregorianCalendar(null).get(Calendar.DAY_OF_MONTH) > 5)
				{
					Messages.showInfoMessage("Offene Entscheidungen über Plusstunden", 
							"Sie können keine weiteren Daten im Monatseinsatzblatt eingeben, "
							+ "solange nicht alle Angaben zu offenen Entscheidungen über Plusstunden gemacht wurden.");
					m_hasOffeneAuszahlung = true;
				}
			}
		}
		
		return !m_hasOffeneAuszahlung;
	}


	/**
	 * Öffnet den Dialog zur Auszahlung für den ausgewählten Eintrag.<br>
	 * Vorher wird geprüft, ob die Eintragung wegen Verletzermeldungen zulässig sind.
	 * 
	 * @return Der Dialog wurde mit OK bestätiget
	 * @throws Exception
	 */
	private boolean showDialogAuszahlung() throws Exception {
		boolean returnValue;
		
		if (m_coVerletzerliste.hasEintragBefore(m_coAuszahlungen.getDatum()))
		{
			Messages.showErrorMessage("Für den Zeitraum bis zum " + Format.getString(m_coAuszahlungen.getDatum()) 
			+ " gibt es noch offene Verletzermeldungen.<br>"
			+ "Die Angaben zur Auszahlung können erst nach der Freigabe aller Meldungen eingetragen werden.");
			return false;
		}

		returnValue = DialogAuszahlung.showDialog(m_coAuszahlungen);
		m_tableAuszahlungen.refresh(reasonDataChanged, null);

		// zum ausgewählten Datensatz wechseln, damit beim Drucken des Antrags der richtige ausgewählt ist
		m_coAuszahlungen.moveTo(m_tableAuszahlungen.getSelectedBookmark());

		// Button ggf. aktivieren
		refreshBtAntragAuszahlungDrucken();

		// PDF erzeugen, wenn Dialogfeld mit OK bestätigt wurde und eine Auszahlung beantragt wird
		if (returnValue && m_coAuszahlungen.getStatusIDAuszahlung() == CoStatusAuszahlung.STATUSID_BEANTRAGT)
		{
			new ExportAuszahlungUeberstundenListener(m_coAuszahlungen).activate(null);
		}
		
		return returnValue;
	}

	
	@Override
	public void activate() {
		try 
		{
			// Tabellen neu laden
			reloadTableVerletzerliste();
		}
		catch (Exception e)
		{
		}
		
		super.activate();
	}
	
	
	/**
	 * Verletzermeldungen und Auszahlungen dürfen nicht bearbeitet werden
	 * 
	 * @see pze.ui.formulare.UniFormWithSaveLogic#refresh(int, java.lang.Object)
	 */
	public void refresh(int reason, Object element) {
		super.refresh(reason, element);
		
		// wenn man nicht zur Personalverwaltung gehört, darf man nichts ändern
		if (!UserInformation.getInstance().isPersonalverwaltung())
		{
			super.refresh(reasonDisabled, null);
		}
		
		// Tabellen nicht ändern
		m_tableVerletzungen.refresh(reasonDisabled, null);
		m_tableAuszahlungen.refresh(reasonDisabled, null);
		
		refreshBtAuszahlungEintragen();
		refreshBtAntragAuszahlungDrucken();
	}
	

	/**
	 * Button zur Eintragung der Auszahlung in Abhängigkeit von den Berechtigungen refreshen
	 */
	private void refreshBtAuszahlungEintragen() {
		boolean eigenesPersonenFormular;
		
		try 
		{
			if (m_coAuszahlungen == null || m_coAuszahlungen.hasNoRows())
			{
				return;
			}
			
			eigenesPersonenFormular = UserInformation.getPersonID() == m_coAuszahlungen.getPersonID();
			
			// Eintrag ausgewählt
			if (m_tableAuszahlungen.getSelectedBookmark() != null
					&& (
							// für sich selbst darf man nur die 1. Auszahlung eintragen, falls dies noch nicht gemacht wurde
							(eigenesPersonenFormular && m_coAuszahlungen.getCurrentRowIndex() == 0 &&  m_coAuszahlungen.getStatusIDAuszahlung() == 0) 
							
							// oder für andere, falls man zur Personalverwaltung gehört
							|| (!eigenesPersonenFormular && UserInformation.getInstance().isPersonalverwaltung())
							)
					)
			{
				m_btAuszahlungEintragen.refresh(reasonEnabled, null);
			}
			else
			{
				m_btAuszahlungEintragen.refresh(reasonDisabled, null);
			}
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}


	/**
	 * Button zum Antrag der Auszahlung in Abhängigkeit refreshen, wenn ein gültiger Eintrag gewählt wurde
	 */
	private void refreshBtAntragAuszahlungDrucken() {
		try 
		{
			if (m_coAuszahlungen == null || m_coAuszahlungen.hasNoRows())
			{
				return;
			}

			// Eintrag mit Auszahlung ausgewählt
			if (m_tableAuszahlungen.getSelectedBookmark() != null 
					&& (m_coAuszahlungen.getWertAuszahlungUeberstundenProjekt() > 0 || m_coAuszahlungen.getWertAuszahlungUeberstundenReise() > 0))
			{
				m_btAuszahlungsAntrag.refresh(reasonEnabled, null);
			}
			else
			{
				m_btAuszahlungsAntrag.refresh(reasonDisabled, null);
			}
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}


	/**
	 * Tabelle mit den geplanten Anträgen neu laden
	 * 
	 * @throws Exception
	 */
//	private void reloadTableBuchungen() throws Exception {
//		int row;
//		
//		// Zeile merken
//		row = m_coBuchung.getCurrentRowIndex();
//		if (m_tableBuchungen.getSelectedBookmark() == null)
//		{
//			row = -1;
//		}
//		
//		// Daten aktualisieren
//		m_coBuchung.loadGeplantOfa(m_coPerson.getID());
//		m_tableBuchungen.setData(m_coBuchung);
//
//		// wenn nichts markiert war, beende Methode
//		if (row < 0)
//		{
//			return;
//		}
//		
//		// nächste Zeile markieren
//		if (!m_coBuchung.moveTo(row))
//		{
//			// wenn es die letzte war, gehe wieder zur letzten
//			if (!m_coBuchung.moveLast())
//			{
//				// falls es keine Zeile mehr gibt
//				m_tableBuchungen.setSelectedBookmark(null);
//				refreshBtBuchungBeantragen();
//			}
//		}
//		m_tableBuchungen.setSelectedBookmark(m_coBuchung.getBookmark());
//	}
//
//
//	/**
//	 * Tabelle mit den aktuellen Anträgen neu laden
//	 * 
//	 * @throws Exception
//	 */
//	private void reloadTableBuchungenAktuell() throws Exception {
//		m_coBuchungAktuell.loadAntraegeAktuellOfa(m_coPerson.getID());
//
//		m_tableBuchungenAktuell.setData(m_coBuchungAktuell);
//	}


	/**
	 * Tabelle mit den Verletzermeldungen neu laden
	 * 
	 * @throws Exception
	 */
	private void reloadTableVerletzerliste() throws Exception {
		m_coVerletzerliste.load(m_coPerson, null, true);

		m_tableVerletzungen.setData(m_coVerletzerliste);
	}


	/**
	 * Tabelle mit den Auszahlungen neu laden
	 * 
	 * @throws Exception
	 */
	public void reloadTableAuszahlungen() throws Exception {
		
		m_coAuszahlungen.loadAuszahlungen(m_coPerson.getID());

		m_tableAuszahlungen.setData(m_coAuszahlungen);
	}

	
	/**
	 * Das Personenformular darf nur bei der eigenen Person geändert werden, außer man gehört zur Gruppe der Sekretärinnen bzw. Personalverwaltung
	 * 
	 * @see pze.ui.formulare.UniFormWithSaveLogic#mayEdit()
	 */
	public boolean mayEdit() {
		try
		{
			// das eigene Formular nur ohne offene Auszahlungen bearbeiten, alle anderen bei entsprechender Berechtigung
			return !m_hasOffeneAuszahlung 
					|| (UserInformation.getPersonID() != m_coPerson.getID() && UserInformation.getInstance().isSekretariat()); 
		}
		catch (Exception e) 
		{
			return false;
		}
	}
	
	
	/**
	 * In den Editiermodus wechseln, wenn man noch nicht drin war Editiermodus war
	 * 
	 * @throws Exception
	 */
	public void beginEditing() throws Exception{
		if (!getData().isEditing())
		{
			editlistener.activate(null);
//			getData().cancel();
		}
	}

	
	/**
	 * Editiermodus beenden, wenn man im Editiermodus war
	 * 
	 * @throws Exception
	 */
	public void cancelEditing() throws Exception{
		if (getData().isEditing())
		{
			editlistener.activate(null);
//			getData().cancel();
		}
	}


	/**
	 * Aktionen nach dem Abbrechen der Bearbeitung
	 * @throws Exception 
	 */
	public void doAfterCancelEditing() throws Exception {
		reloadFormMonatseinsatzblatt(NavigationManager.getSelectedTabItemKey().equals(FormPersonMonatseinsatzblatt.RESID));
	}


}
