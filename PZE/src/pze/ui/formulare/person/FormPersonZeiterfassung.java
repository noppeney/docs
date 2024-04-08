package pze.ui.formulare.person;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;

import framework.Application;
import framework.business.interfaces.FW;
import framework.business.interfaces.data.IBusinessObject;
import framework.business.interfaces.fields.IField;
import framework.ui.controls.ComboControl;
import framework.ui.controls.TextControl;
import framework.ui.interfaces.controls.IButtonControl;
import framework.ui.interfaces.controls.IControl;
import framework.ui.interfaces.controls.IFieldControl;
import framework.ui.interfaces.selection.IFocusListener;
import framework.ui.interfaces.selection.ISelectionListener;
import framework.ui.interfaces.selection.IValueChangeListener;
import pze.business.Format;
import pze.business.Messages;
import pze.business.UserInformation;
import pze.business.export.ExportTagesbuchungenListener;
import pze.business.navigation.NavigationManager;
import pze.business.objects.CoVerletzerliste;
import pze.business.objects.CoZeitmodell;
import pze.business.objects.personen.CoBuchung;
import pze.business.objects.personen.CoKontowert;
import pze.business.objects.personen.CoPerson;
import pze.business.objects.reftables.CoStatusKontowert;
import pze.business.objects.reftables.buchungen.CoBuchungsart;
import pze.business.objects.reftables.buchungen.CoBuchungserfassungsart;
import pze.business.objects.reftables.buchungen.CoBuchungstyp;
import pze.business.objects.reftables.buchungen.CoStatusBuchung;
import pze.ui.controls.DateControlPze;
import pze.ui.controls.IntegerToUhrzeitControl;
import pze.ui.controls.SortedTableControl;
import pze.ui.formulare.UniFormWithSaveLogic;
import pze.ui.formulare.auswertung.FormAmpelliste;


/**
 * Formular der Daten zur Zeiterfassung/Kontowerte einer Person
 * 
 * @author Lisiecki
 *
 */
public class FormPersonZeiterfassung extends UniFormWithSaveLogic {
	public static final String RESID = "form.person.buchungen";

	private UniFormWithSaveLogic m_formPerson;
	
	private CoPerson m_coPerson;
	private CoBuchung m_coBuchung;
	private CoKontowert m_coKontowert;
	private CoVerletzerliste m_coVerletzerliste;

	private SortedTableControl m_tableBuchungen;
	private SortedTableControl m_tableVerletzungen;
	
	private IButtonControl m_btDatumHeute;
	private IButtonControl m_btDatumVor;
	private IButtonControl m_btDatumZurueck;
	
	private IFieldControl m_tfWochentag;
	private DateControlPze m_tfDatum;
	private DateTime m_kalender;
	
	private IButtonControl m_btNeueBuchung;
	private IButtonControl m_btNeueBuchungOfa;
	private IButtonControl m_btNeueBuchungKommen;
	private IButtonControl m_btNeueBuchungGehen;
	private IButtonControl m_btNeueBuchungPause;
	private IButtonControl m_btNeueBuchungPrivateUnterbrechung;
	private IButtonControl m_btNeueBuchungArbeitsunterbrechung;
	private IButtonControl m_btNeueBuchungDienstreise;
	private IButtonControl m_btNeueBuchungDienstgang;
	private IButtonControl m_btTagesprotokoll;
	
	private TextControl m_tfArbeitszeit;
	private TextControl m_tfUeberstunden;
	private TextControl m_tfSummeUeberstunden;
	private TextControl m_tfReisezeit;
	private TextControl m_tfPausenAenderung;
	
	private TextControl m_tfAuszahlungUeberstundenProjekt;
	private TextControl m_tfAuszahlungUeberstundenReise;
	private ComboControl m_comboStatusAuszahlung;

	private TextControl m_tfResturlaub;
	private TextControl m_tfResturlaubGenehmigt;

	private TextControl m_tfAbwesenheit;

//	private ComboControl m_comboStatus;
//	private ComboControl m_comboGeaendertVon;
	private ComboControl m_comboGrundAenderung;
	
	private TextControl m_tfBemerkung;
	
	private Date m_datumEndePze;
		
	

	

	/**
	 * Konstruktor
	 * 
	 * @param parent visueller Parent
	 * @param coPerson Co der geloggten Daten
	 * @param formPerson Hauptformular der Person
	 * @throws Exception
	 */
	public FormPersonZeiterfassung(Object parent, CoPerson coPerson, UniFormWithSaveLogic formPerson) throws Exception {
		super(parent, RESID, true);
		
		m_formPerson = formPerson;
		m_coPerson = coPerson;
		m_coBuchung = new CoBuchung();
		m_coKontowert = new CoKontowert();
		m_coVerletzerliste = new CoVerletzerliste();
		
		// Kontowerte als Data (in setDatum) setzen, damit die Oberfläche gefüllt werden kann
		m_coPerson.addChild(m_coKontowert);
		
		// Datum Ende PZE
		m_datumEndePze = m_coPerson.getEndePze();
		if (m_datumEndePze != null)
		{
			m_datumEndePze = Format.getDateVerschoben(Format.getDate0Uhr(m_datumEndePze), 1);
		}

		initGui();

		// Datum auf den letzten Arbeitstag setzen
		initDatum();
	}


	private void initGui() throws Exception {

		initGuiDatumNavigation();
		
		initGuiBtNeueBuchung();
		initGuiBtTagesprotokoll();

		initGuiTableBuchungen();
		
		initGuiTableVerletzungen();
		
		initGuiFormularKontowerte();
		
		initKalender();
	}


	private void initGuiDatumNavigation() {
		m_tfWochentag = (IFieldControl) findControl("form.person.buchungen.wochentag");
		m_tfDatum = (DateControlPze) findControl("form.person.buchungen.datum");
		m_tfDatum.enableCopyMode(false); // sonst gibt es ein Problem beim datumswechsel; ist auch nicht notwendig, da dieses Textfeld immer aktiviert ist

		
		m_btDatumHeute = (IButtonControl) findControl("form.person.buchungen.datumheute");
		m_btDatumVor = (IButtonControl) findControl("form.person.buchungen.datumvor");
		m_btDatumZurueck = (IButtonControl) findControl("form.person.buchungen.datumzurueck");

		
		// Listener auf das Datumsfeld, damit der Wochentag bei Änderungen angepasst wird
		m_tfDatum.setValueChangeListener(new IValueChangeListener() {
			
			@Override
			public void valueChanged(IControl control, Object originalValue, Object lastValue, Object currentValue) throws Exception {
				setDatum(getDatum());
			}
		});
		
		
		m_btDatumHeute.setSelectionListener(new ISelectionListener() {
			
			@Override
			public void selected(IControl control, Object params) {
				try
				{
					initDatum();
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
		
		
		m_btDatumVor.setSelectionListener(new ISelectionListener() {
			
			@Override
			public void selected(IControl control, Object params) {
				try
				{
					setDatumVor();
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
		
		
		m_btDatumZurueck.setSelectionListener(new ISelectionListener() {
			
			@Override
			public void selected(IControl control, Object params) {
				try
				{
					setDatumZurueck();
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


	private void initGuiBtNeueBuchung() {
		ISelectionListener selectionListenerNeueBuchung;
		
		// Button neue Buchung allgemein
		m_btNeueBuchung = (IButtonControl) findControl("form.person.buchungen.neu");
		m_btNeueBuchung.setSelectionListener(new ISelectionListener() {
			
			@Override
			public void selected(IControl control, Object params) {
				try
				{
					if (DialogBuchung.showDialogNewBuchung(m_coPerson.getID()))
					{
						// wird jetzt vom Dialog gesteuert
//						valueBuchungChanged();
//						setDatum(DialogBuchung.getDatum());
					}
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
		
		
		// Listener für neue, aktuelle Buchungen
		selectionListenerNeueBuchung = new ISelectionListener() {
			
			@Override
			public void selected(IControl control, Object params) {
				try 
				{
					createNeueBuchung(control);
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			
			@Override
			public void defaultSelected(IControl control, Object params) {
			}
		};
		
		
		// Buttons für neue, aktuelle Buchungen
		m_btNeueBuchungOfa = (IButtonControl) findControl("form.person.buchungen.neu.ofa");
		m_btNeueBuchungKommen = (IButtonControl) findControl("form.person.buchungen.neu.kommen");
		m_btNeueBuchungGehen = (IButtonControl) findControl("form.person.buchungen.neu.gehen");
		m_btNeueBuchungPause = (IButtonControl) findControl("form.person.buchungen.neu.pause");
		m_btNeueBuchungPrivateUnterbrechung = (IButtonControl) findControl("form.person.buchungen.neu.privateunterbrechung");
		m_btNeueBuchungArbeitsunterbrechung = (IButtonControl) findControl("form.person.buchungen.neu.arbeitsunterbrechung");
		m_btNeueBuchungDienstreise = (IButtonControl) findControl("form.person.buchungen.neu.dienstreise");
		m_btNeueBuchungDienstgang = (IButtonControl) findControl("form.person.buchungen.neu.dienstgang");
		
		m_btNeueBuchungOfa.setSelectionListener(selectionListenerNeueBuchung);
		m_btNeueBuchungKommen.setSelectionListener(selectionListenerNeueBuchung);
		m_btNeueBuchungGehen.setSelectionListener(selectionListenerNeueBuchung);
		m_btNeueBuchungPause.setSelectionListener(selectionListenerNeueBuchung);
		m_btNeueBuchungPrivateUnterbrechung.setSelectionListener(selectionListenerNeueBuchung);
		m_btNeueBuchungArbeitsunterbrechung.setSelectionListener(selectionListenerNeueBuchung);
		m_btNeueBuchungDienstreise.setSelectionListener(selectionListenerNeueBuchung);
		m_btNeueBuchungDienstgang.setSelectionListener(selectionListenerNeueBuchung);

	}


	private void initGuiBtTagesprotokoll() {
		
		// Button Tagesprotokoll
		m_btTagesprotokoll = (IButtonControl) findControl("form.person.buchungen.tagesprotokoll");
		m_btTagesprotokoll.setSelectionListener(new ISelectionListener() {
			
			@Override
			public void selected(IControl control, Object params) {
				try 
				{
					(new ExportTagesbuchungenListener(m_tableBuchungen)).activate(null);
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
	
	
	private void initGuiTableBuchungen() throws Exception {
		m_tableBuchungen = new SortedTableControl(findControl("spread.person.buchungen")){

			@Override
			public void tableDefaultSelected(IControl arg0, Object arg1) throws Exception{
				if (DialogBuchung.showDialogWithBuchung(m_coBuchung.getID()))
				{
					valueBuchungChanged();
				}
			}
		};

		m_tableBuchungen.setData(m_coBuchung);

//			/**
//			 * Statusanzeige für die aktuelle Unterweisung anpassen
//			 * 
//			 * @see framework.ui.interfaces.selection.ISelectionListener#selected(framework.ui.interfaces.controls.IControl, java.lang.Object)
//			 */
//			@Override
//			public void selected(IControl arg0, Object arg1) {
//				// allgemeine selected-Funktion zum Sortieren aufrufen
//				m_tableBuchungen.tableSelected(arg0, arg1);
//			}
//
//
//			@Override
//			public void defaultSelected(IControl arg0, Object arg1) {
//
//				try
//				{
//					// beim Doppelklick den Dialog mit der Buchung öffnen
//					if (arg1 != null)
//					{
//						if (DialogBuchung.showDialogWithBuchung(m_coBuchung.getID()))
//						{
//							valueBuchungChanged();
//						}
//					}
//				} catch (Exception e)
//				{
//					e.printStackTrace();
//				}
//			}
//		});
		
	}


	private void initGuiTableVerletzungen() throws Exception {
		m_tableVerletzungen = new SortedTableControl(findControl("spread.person.kontowerte.verletzerliste")){
			
			/**
			 * Verletzungen dokumentieren
			 * 
			 * @see pze.ui.controls.SortedTableControl#endEditing(java.lang.Object, framework.business.interfaces.fields.IField)
			 */
			@Override
			protected void endEditing(Object bookmark, IField fld)  throws Exception {
				m_coVerletzerliste.valueChanged();
			}
		};
	}


	private void initGuiFormularKontowerte() throws Exception {
		
		m_tfArbeitszeit = (TextControl) findControl("form.person.kontowerte.wertbezahltearbeitszeit");
		m_tfUeberstunden = (TextControl) findControl("form.person.kontowerte.wertueberstunden");
		m_tfSummeUeberstunden = (TextControl) findControl("form.person.kontowerte.wertueberstundengesamt");
		m_tfReisezeit = (TextControl) findControl("form.person.kontowerte.wertreisezeit");
		m_tfPausenAenderung = (TextControl) findControl("form.person.kontowerte.wertpausenaenderung");
		
		m_tfAuszahlungUeberstundenProjekt = (TextControl) findControl("form.person.kontowerte.wertauszahlungueberstundenprojekt");
		m_tfAuszahlungUeberstundenReise = (TextControl) findControl("form.person.kontowerte.wertauszahlungueberstundenreise");
		m_comboStatusAuszahlung = (ComboControl) findControl("form.person.kontowerte.statusidauszahlung");
		
		m_tfResturlaub = (TextControl) findControl("form.person.kontowerte.resturlaub");
		m_tfResturlaubGenehmigt = (TextControl) findControl("form.person.kontowerte.resturlaubgenehmigt");
		
		m_tfAbwesenheit = (TextControl) findControl("form.person.kontowerte.abwesenheitsbuchung");

//		m_comboStatus = (ComboControl) findControl("form.person.kontowerte.statusid");
//		m_comboGeaendertVon = (ComboControl) findControl("form.person.kontowerte.geaendertvon");
		m_comboGrundAenderung = (ComboControl) findControl("form.person.kontowerte.grundaenderungid");
		
		m_tfBemerkung = (TextControl) findControl("form.person.kontowerte.bemerkung");
		
		initListenerFormularKontowerte();
	}


	/**
	 * Kalender zur Datumsauswahl
	 */
	private void initKalender() {
		m_kalender = new DateTime ((Composite) findControl("group.person.buchungen.datum").getComponent(), SWT.CALENDAR );
		m_kalender.setBackground(new Color(Display.getCurrent(), new RGB(207, 235, 249)));
		m_kalender.setLocation(73, 60);
		m_kalender.setSize(200, 150);
		m_kalender.pack();
		m_kalender.setBackground(new Color(Display.getCurrent(), new RGB(207, 235, 249)));
		
		m_kalender.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
//				System.out.println ("Calendar date selected (DD.MM.YYYY) = " + (calendar.getMonth () + 1) + "/" + calendar.getDay () + "/" + calendar.getYear ());				
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				Date datum = new Date();
				String tmpDatum =  m_kalender.getDay () + "." + (m_kalender.getMonth () + 1) + "." + m_kalender.getYear ();
				SimpleDateFormat f = new SimpleDateFormat("d.MM.yyyy");
				
				try 
				{
					datum = f.parse(tmpDatum);
					
					setDatum(Format.getDate12Uhr(datum));
				} 
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}


	private void initListenerFormularKontowerte() throws Exception {
		IValueChangeListener valueChangeListener;
		
//		m_addBuchungListener = new AddListener();
//		m_deleteBuchungListener = new TableDeleteListener(this, m_coBuchung, m_tableBuchungen);
	
		
		// ValueChangeListener, um Änderungen zu protokollieren
		valueChangeListener = new IValueChangeListener() {
			
			@Override
			public void valueChanged(IControl control, Object originalValue, Object lastValue, Object currentValue) throws Exception {
				valueKontowertChanged();
			}
		};
		
		m_tfResturlaub.setValueChangeListener(valueChangeListener);
		m_comboGrundAenderung.setValueChangeListener(valueChangeListener);
		m_tfBemerkung.setFocusListener(new IFocusListener() {
			
			@Override
			public void focusLost(IControl control) {
				try 
				{
					valueKontowertChanged();
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				}
			}
			
			@Override
			public void focusGained(IControl control) {
			}
		});

		
		// Listener auf das Feld Arbeitszeit, damit die Überstunden bei Änderungen angepasst werden
		m_tfArbeitszeit.setFocusListener(new IFocusListener() {
			
			@Override
			public void focusLost(IControl control) {
				try 
				{
					IField field;
					
					field = m_coKontowert.getFieldArbeitszeit();
					
					// prüfen, ob sich der Wert geändert hat
					if (field.getState() == IBusinessObject.statusUnchanged)
					{
						return;
					}

					m_coKontowert.updateArbeitszeitMonat();
					m_coKontowert.updateUeberstunden();
					m_coKontowert.updateUeberstundenSummen();

					valueKontowertChanged();
				} 
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			
			@Override
			public void focusGained(IControl control) {
			}
		});

		
		// Listener auf das Feld Überstunden, damit die Summen bei Änderungen angepasst werden
		m_tfUeberstunden.setFocusListener(new IFocusListener() {
			
			@Override
			public void focusLost(IControl control) {
				try 
				{
					IField field;
					
					field = m_coKontowert.getFieldUeberstunden();
					
					// prüfen, ob sich der Wert geändert hat
					if (field.getState() == IBusinessObject.statusUnchanged)
					{
						return;
					}
					
					m_coKontowert.updatePlusMinusStunden();
					m_coKontowert.updateUeberstundenSummen();
					valueKontowertChanged();
				} 
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			
			@Override
			public void focusGained(IControl control) {
			}
		});

		
		// Listener auf das Feld Reisezeit, damit die Überstunden bei Änderungen angepasst werden
 		m_tfReisezeit.setFocusListener(new IFocusListener() {
			
			@Override
			public void focusLost(IControl control) {
				try 
				{
					IField field;
					
					field = m_coKontowert.getFieldReisezeit();
					
					// prüfen, ob sich der Wert geändert hat
					if (field.getState() == IBusinessObject.statusUnchanged)
					{
						return;
					}

					m_coKontowert.updatePlusMinusStunden();
					m_coKontowert.updateUeberstundenSummen();
					
					// Verletzermeldung prüfen
					m_coKontowert.updateVerletzerlisteReisezeit();
					
					valueKontowertChanged();
				} 
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			
			@Override
			public void focusGained(IControl control) {
			}
		});

		
		// Listener auf das Feld Pausenänderung, damit die Summen bei Änderungen angepasst werden
		m_tfPausenAenderung.setFocusListener(new IFocusListener() {
			
			@Override
			public void focusLost(IControl control) {
				try 
				{
					IField field;
					
					field = m_coKontowert.getFieldPausenaenderung();
					
					// prüfen, ob sich der Wert geändert hat
					if (field.getState() == IBusinessObject.statusUnchanged)
					{
						return;
					}
					
					m_coKontowert.updateArbeitszeitWgPausenaenderung();
					m_coKontowert.updateArbeitszeitMonat();
					m_coKontowert.updateUeberstunden();
					m_coKontowert.updateUeberstundenSummen();

					valueKontowertChanged();
					
					// Info, dass die Arbeitszeit neu berechnet wird und manuelle Änderungen verloren gehen
					Messages.showInfoMessage("Die Arbeitszeit wurde neu berechnet. Manuelle Änderungen der Arbeitszeit wurden dabei nicht berücksichtigt.");
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			
			@Override
			public void focusGained(IControl control) {
			}
		});

		
		// Listener für Felder zur Auszahlung Überstunden
		if (UserInformation.getInstance().isPersonalverwaltung() && m_coPerson.getID() != UserInformation.getPersonID())
		{
			m_tfAuszahlungUeberstundenProjekt.setValueChangeListener(valueChangeListener);
			m_tfAuszahlungUeberstundenReise.setValueChangeListener(valueChangeListener);
			m_comboStatusAuszahlung.setValueChangeListener(valueChangeListener);
		}
	}


	/**
	 * Datum auf den letzten Arbeitstag setzen bzw. den letzten Tag an dem gearbeitet wurde
	 * 
	 * @throws Exception
	 */
	private void initDatum() throws Exception {
		int counter;
		Date beginnPze;
		

		// Startdatum ist der aktuelle Tag
		setDatum(new Date());
		
		// bei neuen Benutzern kann das letzte relevante Datum nicht bestimmt werden, es bleibt das aktuelle Datum stehen
		beginnPze = m_coPerson.getBeginnPze();
		if (beginnPze == null)
		{
			return;
		}
		
		// Datum auf den letzten relevanten Tag setzen
		counter = 0;
		do
		{
			verschiebeDatum(-1);
			++counter; // damit z. B. bei Elternzeit nicht zu lange gesucht wird
		} while ( (!m_coPerson.isArbeitstag(getDatum()) && m_coKontowert.getWertArbeitszeit() == 0) && getDatum().after(beginnPze) && counter != 50);

		// aktuelles Datum, falls kein anderes gefunden wurde
		if (counter == 50)
		{
			setDatum(new Date());
		}
	}


	/**
	 * Verletzerliste neu laden, wenn Kontowerte gesetzt werden
	 * 
	 * @see framework.cui.controls.base.BaseCompositeControl#setData(framework.business.interfaces.data.IBusinessObject)
	 */
	@Override
	public void setData(IBusinessObject data) throws Exception {
		super.setData(data);
		
		m_coVerletzerliste.load(m_coPerson, getDatum());
		m_tableVerletzungen.setData(m_coVerletzerliste);
		
		// Verletzerliste als Child hinzufügen (altes vom anderen Datum entfernen)
		if (data instanceof CoKontowert)
		{
			((CoKontowert) data).removeAllChilds();
			data.addChild(m_coVerletzerliste);
			if (!m_coVerletzerliste.isEditing())
			{
				m_coVerletzerliste.begin();
			}
		}
	}

	
	/**
	 * @param id Primärschlüssel
	 * @return Item Key im Tabfolder
	 */
	public static String getKey(int id) {
		return "personzeiterfassung" + id;
	}
	
	
	@Override
	public String getKey() {
		return getKey(getID());
	}
	

	@Override
	public void activate() {
		
		if (NavigationManager.getSelectedTabItemKey() != null && NavigationManager.getSelectedTabItemKey().equals(getResID()))
		{
//			Action.get("file.new").addActionListener(m_addBuchungListener);
//			Action.get("edit.delete").addActionListener(m_deleteBuchungListener);
			m_formPerson.addSaveHandler();
			refresh(reasonDataChanged, null); // aktualisieren beim Anlegen einer neuen Person
			super.activate();
		}
	}
	
	
	@Override
	public void deactivate() {
//		Action.get("file.new").removeActionListener(m_addBuchungListener);
//		Action.get("edit.delete").removeActionListener(m_deleteBuchungListener);
		m_formPerson.removeSaveHandler();
		super.deactivate();
	}

	
	@Override
	public boolean mayEdit() {
		return false;
	}
	

	/**
	 * Bezeichnungs-Datensatz hinzufügen
	 *
	 */
//	class AddListener extends ActionAdapter
//	{
//		@Override
//		public void activate(Object sender) throws Exception {
////			m_coBuchung.createNew(m_coPerson.getID());
////			m_coFilteredPersonFirma.filter();
//			m_tableBuchungen.refresh(reasonDataAdded, m_coBuchung.getBookmark());
//			super.activate(sender);
//		}
//		
//		/* (non-Javadoc)
//		 * @see framework.business.action.ActionAdapter#getEnabled()
//		 */
//		@Override
//		public boolean getEnabled() {
//			return m_coBuchung.isEditing();
//		}
//	}
	
	private Date getDatum() {
		return Format.getDateValue(m_tfDatum.getField().getValue());
	}


	/**
	 * Datum und Wochentag setzen und die Daten (Buchungen und Kontowerte für den Tag laden)
	 * 
	 * @param datum
	 * @throws Exception 
	 */
	public void setDatum(Date datum) throws Exception {
		int returnValue;
		
		if (datum == null)
		{
			return;
		}

		// Datum auf 12 Uhr formatieren wegen Zeitumstellung
		datum = Format.getDate12Uhr(datum);
		
		// wenn CoKontowert im Edit-Modus ist, Daten speichern oder verwerfen
		if (m_coKontowert.isEditing() && m_coKontowert.isModified())
		{
			returnValue = Messages.showYesNoCancelMessage("Kontowerte geändert", "Sollen die geänderten Daten gespeichert werden?");

			switch (returnValue) 
			{
			case FW.YES:
				// Speichern der Person aufrufen (wie Speichern-Symbol in Symbolleiste)
				m_formPerson.validateAndSave();
//				m_coPerson.save();
				break;

//			case FW.NO:
//				getCo().cancel();
//				break;
//
			case FW.CANCEL:
				return;

			default:
				break;
			}
		}
		else
		{
//			getCo().cancel();
		}

//		updateEditToolbarButton();
//	}
		
		m_tfDatum.getField().setValue(datum);

		setWochentag(datum);
		
		checkDatumKalener();
		
		loadData(datum);
	}


	/**
	 * Datum des Kalenders prüfen und ggf. aktualisieren
	 */
	private void checkDatumKalener() {
		int jahr, monat, tag;
		GregorianCalendar gregDatum;
		
		gregDatum = Format.getGregorianCalendar(getDatum());
		jahr = gregDatum.get(Calendar.YEAR);
		monat = gregDatum.get(Calendar.MONTH);
		tag = gregDatum.get(Calendar.DAY_OF_MONTH);
		
		if (m_kalender.getYear() != jahr || m_kalender.getMonth() != monat || m_kalender.getDay() != tag)
		{
			m_kalender.setDate(jahr, monat, tag);
		}
	}


	/**
	 * Datum einen Tag weiter setzen
	 * 
	 * @throws Exception 
	 */
	private void setDatumVor() throws Exception {
		verschiebeDatum(1);
	}


	/**
	 * Datum einen Tag zurueck setzen
	 * 
	 * @throws Exception 
	 */
	private void setDatumZurueck() throws Exception {
		verschiebeDatum(-1);
	}


	/**
	 * Datum um die übergebene Anzahl Tage verschieben
	 * 
	 * @param anzTage
	 * @throws Exception 
	 */
	private void verschiebeDatum(int anzTage) throws Exception {
		Date datum;
		
		datum = getDatum();
		datum = Format.getDateVerschoben(datum, anzTage);

		setDatum(datum);
	}


	/**
	 * Wochentag setzen
	 * 
	 * @param datum
	 */
	private void setWochentag(Date datum) {
		
		if (datum == null)
		{
			return;
		}
		
		// Wocentag bestimmen
		m_tfWochentag.getField().setValue(Format.getWochentag(datum));
	}


	/**
	 * Tabellen und Formulare mit Buchungen und Kontowerten für den übergebenen Tag laden
	 * 
	 * @param datum
	 * @throws Exception
	 */
	private void loadData(Date datum) throws Exception {
		
		// angezeigte Werte aus IntegerToUhrzeitControls löschen
		resetIntegerToUhrzeitControls();

		loadDataBuchungen(datum);

		loadDataKontowerte(datum);

		refresh(reasonDataChanged, null);
	}


	/**
	 * Daten der Buchungen für den Tag laden 
	 * 
	 * @param datum
	 * @throws Exception
	 */
	private void loadDataBuchungen(Date datum) throws Exception {
		
		// Daten nur anzeigen, wenn die Person an dem Datum noch aktiv war
		if (m_datumEndePze == null || datum.before(m_datumEndePze))
		{
			m_coBuchung.load(m_coPerson, datum, 0, false);
		}
		else
		{
			m_coBuchung.emptyCache();
		}
		
		m_tableBuchungen.setData(m_coBuchung);
	}


	/**
	 * Daten der Kontowerte für den Tag laden 
	 * 
	 * @param datum
	 * @throws Exception
	 */
	private void loadDataKontowerte(Date datum) throws Exception {
		
		// Daten nur anzeigen, wenn die Person an dem Datum noch aktiv war
		if (m_datumEndePze == null || datum.before(m_datumEndePze))
		{
			m_coKontowert.load(m_coPerson.getID(), datum);
		}
		else
		{
			m_coKontowert.emptyCache();
		}
		
		// wenn für den Tag keine Kontodaten existieren, lege einen leeen Datensatz ab
		if (!m_coKontowert.moveFirst())
		{
			m_coKontowert.createNew();
			// Bearbeitungsmodus beenden
			m_coKontowert.commit();
		}
		
		setData(m_coKontowert);
		// ggf. Edit-Modus starten, wenn sich die Person im Edit-Modus befindet
		if (m_coKontowert.getParent().isEditing())
		{
			m_coKontowert.begin();
			if (!m_coVerletzerliste.isEditing())
			{
				m_coVerletzerliste.begin();
			}
		}
		
		
		// Abwesenheitsbuchung anzeigen
		m_tfAbwesenheit.getField().setValue(m_coKontowert.getBuchungsartTagesbuchung());
		
		
		// bereits genehmigten Urlaub anzeigen, nur eintragen, wenn ein korrekter Datensatz vorhanden ist
		if (m_coKontowert.isGueltig())
		{
			m_tfResturlaubGenehmigt.getField().setValue(m_coKontowert.getResturlaubGenehmigt(null));
		}
		else // null statt 0 anzeigen
		{
			m_tfResturlaubGenehmigt.getField().setValue(null);
		}
	}


	/**
	 * Bei geänderten Werten Kontowerte neu berechnen
	 * 
	 * @throws Exception
	 */
	private void valueBuchungChanged() throws Exception {
		
		if (m_coKontowert.isEditing())
		{
			Messages.showWarningMessage("Die Kontowerte werden neu berechnet. Ggf. durchgeführte und noch nicht gespeicherte Änderungen werden verworfen.");
		}
		
		loadData(getDatum());
	}


	/**
	 * Bei geänderten Werten speichern, wer wann die Änderungen gemacht hat
	 * 
	 * @throws Exception
	 */
	private void valueKontowertChanged() throws Exception {

		if (!m_coKontowert.isModified())
		{
			return;
		}

		// Speichern von wem die Änderungen gemacht wurden
		m_coKontowert.setGeaendertVonID(UserInformation.getPersonID());
		m_coKontowert.setGeaendertAm(Format.getStringMitUhrzeit(Format.getGregorianCalendar(null)));
		m_coKontowert.setStatusID(CoStatusKontowert.STATUSID_GEAENDERT);
		setData(m_coKontowert);
		
		refresh(reasonDataChanged, null);
	}

	
	/**
	 * Neue Buchung aufgrund des geklickten Buttons erstellen
	 * 
	 * @param control
	 * @throws Exception 
	 */
	private void createNeueBuchung(IControl control) throws Exception {
		int personID, buchungsartID;
		Date datum;
		CoBuchung coBuchung;
		
		// Buchungsart über den Button festlegen
		if (control.equals(m_btNeueBuchungOfa))
		{
			buchungsartID = CoBuchungsart.ID_ORTSFLEX_ARBEITEN;
		}
		else if (control.equals(m_btNeueBuchungKommen))
		{
			buchungsartID = CoBuchungsart.ID_KOMMEN;
		}
		else if (control.equals(m_btNeueBuchungGehen))
		{
			buchungsartID = CoBuchungsart.ID_GEHEN;
		}
		else if (control.equals(m_btNeueBuchungPause))
		{
			buchungsartID = CoBuchungsart.ID_PAUSE;
		}
		else if (control.equals(m_btNeueBuchungPrivateUnterbrechung))
		{
			buchungsartID = CoBuchungsart.ID_PRIVATE_UNTERBRECHUNG;
		}
		else if (control.equals(m_btNeueBuchungArbeitsunterbrechung))
		{
			buchungsartID = CoBuchungsart.ID_ARBEITSUNTERBRECHUNG;
		}
		else if (control.equals(m_btNeueBuchungDienstreise))
		{
			buchungsartID = CoBuchungsart.ID_DIENSTREISE;
		}
		else if (control.equals(m_btNeueBuchungDienstgang))
		{
			buchungsartID = CoBuchungsart.ID_DIENSTGANG;
		}
		else
		{
			return;
		}
		
		
		// Sicherheitsabfrage vor dem Speichern
		if (!Messages.showYesNoMessage("Buchung erstellen", "Möchten Sie wirklich eine aktuelle Buchung '" 
				+ CoBuchungsart.getInstance().getBezeichnung(buchungsartID) + "' erstellen?"))
		{
			return;
		}
		
		// neue Buchung erstellen
		coBuchung = new CoBuchung();
		coBuchung.createNew();
		personID = m_coKontowert.getPersonID();
		coBuchung.setPersonID(personID);
		
		// aktuellen Zeitpunkt aus der DB holen
		datum = Format.getDateValue(Application.getLoaderBase().executeScalar("SELECT SYSDATETIME()"));
		coBuchung.setZeitpunkt(datum);
		coBuchung.setBuchungsartID(buchungsartID);

		// es werden nur Zeitbuchungen am PC gemacht
		coBuchung.setBuchungserfassungsartID(CoBuchungserfassungsart.ID_PC);
		coBuchung.setBuchungsTyp(CoBuchungstyp.ZEITBUCHUNG);
		coBuchung.setStatusID(CoStatusBuchung.STATUSID_OK);

		// Speichern von wem die Änderungen gemacht wurden
		coBuchung.setGeaendertVonID(personID);
		coBuchung.setGeaendertAm(Format.getStringMitUhrzeit(Format.getGregorianCalendar(null)));

		// Buchung speichern
		coBuchung.save();
		
		// Kontowerte  aktualisieren
		coBuchung.updateKontowerte();

		// GUI für den Tag neu laden
		setDatum(datum);
	}
	

	/**
	 * Datum und Navigationsbuttons immer aktivieren <br>
	 * Wochentag immer deaktivieren
	 * 
	 * @see pze.ui.formulare.UniFormWithSaveLogic#refresh(int, java.lang.Object)
	 */
	@Override
	public void refresh(int reason, Object element) {
		try
		{
			super.refresh(reason, element);
			
			// standardmäßig alles deaktivieren
			super.refresh(reasonDisabled, null);
			
			// Ausgabe zur Fehlerfindung
//			System.out.println("-----------------------------------------------------------------------------------------------------");
//			System.out.println("Personalabteilung: " + UserInformation.getInstance().isPersonalverwaltung());
//			System.out.println("isEditing: " + getData().isEditing());
//			System.out.println("isGueltig: " + m_coKontowert.isGueltig());
//			System.out.println("m_tfPausenAenderung: " + m_tfPausenAenderung);
//			System.out.println("m_tfReisezeit: " + m_tfReisezeit);
//			System.out.println("m_tfArbeitszeit: " + m_tfArbeitszeit);
//			System.out.println("m_tfUeberstunden: " + m_tfUeberstunden);
//			System.out.println("m_tfResturlaub: " + m_tfResturlaub);
//			System.out.println("-----------------------------------------------------------------------------------------------------");

			UserInformation userInformation;
			userInformation = UserInformation.getInstance();
			
			// beim Bearbeiten ausgewählte Kontowerte aktivieren
			// die eigenen Daten dürfen nicht geändert werden
			if (getData().isEditing() && m_coKontowert.isGueltig() 
					&& userInformation.isSekretariat() // mindestens die rechte einer Sekretärin/AL
					&& m_coKontowert.getPersonID() != UserInformation.getPersonID() // nicht die eigenen Daten ändern
					)
			{
				// nur Personalverwaltung hat Rechte für Verletzermeldungen
				if (userInformation.isPersonalverwaltung())
				{
					m_tableVerletzungen.refresh(reasonEnabled, element);
					// hier könnte auf AL/Sekretariat abgefragt werden, dann müssen aber Meldungen unterschieden werden
					// in initGuiTableVerletzungen beginEditing implementieren oder mayEdit wäre wohl noch besser
				}
				
				if (userInformation.isSekretariat())
				{
					m_tfArbeitszeit.refresh(reasonEnabled, null);
					m_tfReisezeit.refresh(reasonEnabled, null);
					m_tfPausenAenderung.refresh(reasonEnabled, null);

					// die Personalverwaltung darf weitere Felder bearbeiten
					if (UserInformation.getInstance().isPersonalverwaltung())
					{
						m_tfUeberstunden.refresh(reasonEnabled, null);
						m_tfResturlaub.refresh(reasonEnabled, null);

						m_tfAuszahlungUeberstundenProjekt.refresh(reasonEnabled, null);
						m_tfAuszahlungUeberstundenReise.refresh(reasonEnabled, null);
						m_comboStatusAuszahlung.refresh(reasonEnabled, null);
					}

					m_comboGrundAenderung.refresh(reasonEnabled, null);

					m_tfBemerkung.refresh(reasonEnabled, null);
				}
				
			}

			// Datum, Wochentag und Navigationsbuttons aktivieren
			refreshDatumNavigation();

			// Button neue Buchung ggf. aktivieren
			refreshBtNeueBuchung();
			
			// Stand Gleitzeitkonto ggf. markieren
			refreshTfSummeUeberstunden();
		} 
		catch (Exception e)
		{
			System.out.println("Error in FormPersonZeiterfassung.refresh()");
			e.printStackTrace();
		}
	}


	/**
	 * Formularfelder für Datum, Wochentag und Navigationsbuttons aktivieren
	 */
	private void refreshDatumNavigation() {
		m_tfDatum.refresh(reasonEnabled, null);
		m_btDatumHeute.refresh(reasonEnabled, null);
		m_btDatumVor.refresh(reasonEnabled, null);
		m_btDatumZurueck.refresh(reasonEnabled, null);
		
		m_tfWochentag.refresh(reasonDisabled, null);
	}


	/**
	 * Button für neue Buchung je nach Berechtigung aktivieren
	 * @throws Exception 
	 */
	private void refreshBtNeueBuchung() throws Exception {
		boolean selbstbuchung;
		
		// für sich selbst oder ab Sekretariat möglich
		selbstbuchung = UserInformation.isPerson(m_coKontowert.getPersonID());
		if (selbstbuchung || UserInformation.getInstance().isSekretariat())
		{
			m_btNeueBuchung.refresh(reasonEnabled, null);
			
			// neue aktuelle Buchungen nur für sich selbst
			if (selbstbuchung)
			{
				m_btNeueBuchungOfa.refresh(reasonEnabled, null);
				m_btNeueBuchungKommen.refresh(reasonEnabled, null);
				m_btNeueBuchungGehen.refresh(reasonEnabled, null);
				m_btNeueBuchungPause.refresh(reasonEnabled, null);
				m_btNeueBuchungPrivateUnterbrechung.refresh(reasonEnabled, null);
				m_btNeueBuchungArbeitsunterbrechung.refresh(reasonEnabled, null);
				m_btNeueBuchungDienstreise.refresh(reasonEnabled, null);
				m_btNeueBuchungDienstgang.refresh(reasonEnabled, null);
			}
		}
		else
		{
			m_btNeueBuchung.refresh(reasonDisabled, null);
		}

		
		// Tagesprotokoll kann erzeugt werden, wenn Buchungen existieren
		m_btTagesprotokoll.refresh(m_coBuchung.getRowCount() > 0 ? reasonEnabled : reasonDisabled, null);
	}


	/**
	 * Feld mit Stand Gleitzeitkonto farblich markieren
	 * @throws Exception 
	 */
	private void refreshTfSummeUeberstunden() throws Exception {
		int wertUeberstundenGesamt;
		String colorValue;
		CoZeitmodell coZeitmodell;

		
		// bei AL nicht markieren
		coZeitmodell = m_coPerson.getCoZeitmodell(getDatum());
		if (coZeitmodell != null && !coZeitmodell.isMeldungArbZgKontostandAktiv())
		{
			return;
		}
		
		// Feld mit Stand Gleitzeitkonto farblich markieren
		wertUeberstundenGesamt = Format.getIntValue(m_tfSummeUeberstunden.getField().getValue());
		colorValue = null;

		// rot bei < 0
		if (wertUeberstundenGesamt < 0)
		{
			colorValue = FormAmpelliste.COLOR_ROT;
		}
		// gelb bei > 30
		else if (wertUeberstundenGesamt > 1800)
		{
			colorValue = FormAmpelliste.COLOR_GELB;
		}

		if (colorValue != null)
		{
			((IntegerToUhrzeitControl) m_tfSummeUeberstunden).setColor(colorValue);
		}
	}

	
	/**
	 * Wenn keine daten vorhanden sind, kann nicht gespeichert werden
	 * 
	 * @throws Exception Validieren und Speichern
	 * @return Daten waren korrekt und wurden gespeichert
	 */
	public boolean validateAndSave() throws Exception {
		if (m_coKontowert.getDatum() == null)
		{
			return true;
		}
		
		if (!m_coVerletzerliste.isEditing())
		{
			m_coVerletzerliste.begin();
		}

		return super.validateAndSave();
	}
	

	/**
	 * Nach dem Speichern der Änderungen, Daten neu laden
	 * 
	 * @see pze.ui.formulare.UniFormWithSaveLogic#doAfterSave()
	 */
	public void doAfterSave() throws Exception {
		setData(m_coKontowert);
		
//		((FormPerson) m_formPerson).reloadTableVerletzerliste();
		// Auszahlungen neu laden, der Rest wird beim activate geladen, Auszahlungen dauern aber zu lange
		((FormPerson) m_formPerson).reloadTableAuszahlungen();
	}


}
