package pze.ui.formulare.auswertung;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;

import framework.business.interfaces.CaptionType;
import framework.business.interfaces.fields.IField;
import framework.business.interfaces.tables.IColumnDescription;
import framework.business.interfaces.tables.IHeaderDescription;
import framework.ui.controls.ComboControl;
import framework.ui.interfaces.controls.IButtonControl;
import framework.ui.interfaces.controls.IControl;
import framework.ui.interfaces.controls.ITabFolder;
import framework.ui.interfaces.selection.ISelectionListener;
import framework.ui.interfaces.spread.ISpreadCell;
import pze.business.Format;
import pze.business.Profile;
import pze.business.export.ExportAnwesenheitListener;
import pze.business.export.ExportTagesmeldungListener;
import pze.business.objects.auswertung.CoAnwesenheitUebersicht;
import pze.business.objects.auswertung.CoAuswertung;
import pze.business.objects.auswertung.CoAuswertungAnwesenheitUebersicht;
import pze.business.objects.reftables.CoJahresauswahl;
import pze.business.objects.reftables.CoWochenauswahl;
import pze.ui.controls.SortedTableControl;
import pze.ui.formulare.person.FormPerson;

/**
 * Formular für die Anwesenheitsübersicht
 * 
 * @author Lisiecki
 *
 */
public class FormAnwesenheitUebersicht extends FormAuswertung {
	
	public static String RESID = "form.auswertung.anwesenheit.uebersicht";

	private IButtonControl m_btDieseWoche;
	private IButtonControl m_btNaechsteWoche;
	private IButtonControl m_btWochenauswahl;
	private IButtonControl m_btTagesmeldung;

	private CoWochenauswahl m_coWochenauswahl1;
//	private CoWochenauswahl m_coWochenauswahl2;
	private CoJahresauswahl m_coJahresauswahl;
	
	private ComboControl m_comboWochenauswahl1;
//	private ComboControl m_comboWochenauswahl2;
	private ComboControl m_comboJahresauswahl;
	
	private GregorianCalendar m_gregDatum;
	private DateTime m_kalender;


	
	
	/**
	 * Konstruktor
	 * 
	 * @param parent		visueller Parent
	 * @throws Exception
	 */
	public FormAnwesenheitUebersicht(Object parent) throws Exception {
		super(parent, RESID);
	}

	
	/**
	 * zusätzliche Buttons initialisieren
	 */
	@Override
	protected void initFormular() throws Exception {
		super.initFormular();
		
		// Daten für die Wochenauswahl laden
		initComboWochenauswahl();

		m_btDieseWoche = (IButtonControl) findControl(getResID() + ".diesewoche");
		m_btDieseWoche.setSelectionListener(new ISelectionListener() {

			@Override
			public void selected(IControl control, Object params) {
				m_gregDatum = Format.getGregorianCalendar(null);
				
				try 
				{
					clickedAktualisieren();
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}

			@Override
			public void defaultSelected(IControl control, Object params) {
			}
		});
		
		
		m_btNaechsteWoche = (IButtonControl) findControl(getResID() + ".naechstewoche");
		m_btNaechsteWoche.setSelectionListener(new ISelectionListener() {

			@Override
			public void selected(IControl control, Object params) {
				m_gregDatum = Format.getGregorianCalendar(null);
				m_gregDatum.add(Calendar.WEEK_OF_YEAR, 1);
				
				try 
				{
					clickedAktualisieren();
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}

			@Override
			public void defaultSelected(IControl control, Object params) {
			}
		});
		
		
		m_btWochenauswahl = (IButtonControl) findControl(getResID() + ".wochenauswahl");
		m_btWochenauswahl.setSelectionListener(new ISelectionListener() {

			@Override
			public void selected(IControl control, Object params) {
				m_gregDatum = Format.getGregorianCalendar(null);
				
				// Datum an Jahres und Wochenauswahl anpassen
				m_gregDatum.set(Calendar.YEAR, Format.getIntValue(m_coJahresauswahl.getBezeichnung()));
				m_gregDatum.set(Calendar.WEEK_OF_YEAR, m_coWochenauswahl1.getID());
				
				try 
				{
					clickedAktualisieren();
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}

			@Override
			public void defaultSelected(IControl control, Object params) {
			}
		});
		
		
		m_btTagesmeldung = (IButtonControl) findControl(getResID() + ".tagesmeldung");
		m_btTagesmeldung.setSelectionListener(new ISelectionListener() {

			@Override
			public void selected(IControl control, Object params) {
				clickedExportTagesmeldung();
			}

			@Override
			public void defaultSelected(IControl control, Object params) {
			}
		});
		
		
		// Kalender für Tagesmeldung
		initKalender();
	}
	
	
	/**
	 * zusätzlich Tabellenheader mit Datum aktualisieren
	 */
	@Override
	protected void clickedAktualisieren() throws Exception {
		super.clickedAktualisieren();
		
		updateHeaderDescription();
	}
	

	/**
	 * Tagesmeldung exportieren
	 */
	private void clickedExportTagesmeldung() {
		try
		{
			GregorianCalendar gregDatum;
			
			// Datum aus dem Kalneder bestimmen
			gregDatum = Format.getGregorianCalendar12Uhr(null);
			gregDatum.set(Calendar.YEAR, m_kalender.getYear());
			gregDatum.set(Calendar.MONTH, m_kalender.getMonth());
			gregDatum.set(Calendar.DAY_OF_MONTH, m_kalender.getDay());
			
			(new ExportTagesmeldungListener(this, gregDatum)).activate(null);
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	
	/**
	 * Person beim Doppelklick öffnen
	 * 
	 * @see pze.ui.formulare.auswertung.FormAuswertung#initTable()
	 */
	@Override
	protected void initTable() throws Exception {
		
		m_table = new SortedTableControl(findControl("spread.auswertung.anwesenheit.uebersicht")){

			@Override
			public void tableDefaultSelected(IControl arg0, Object arg1) throws Exception {
				FormPerson formPerson;

				formPerson = FormPerson.open(getSession(), null, ((CoAnwesenheitUebersicht) m_co).getPersonID());
				if (formPerson != null)
				{
					formPerson.showZeiterfassung(Format.getDateValue(m_gregDatum));
				}
			}

			@Override
			protected void tableDataChanged(Object bookmark, IField fld) throws Exception {
			}
			
			@Override
			protected void renderCell(ISpreadCell cell) throws Exception {
				super.renderCell(cell);
				
				if (isCellEmpty(cell))
				{
					return;
				}
				
				// geplante Buchungen markieren
				if (((CoAnwesenheitUebersicht)m_co).isGeplanteBuchung(cell))
				{
					TableAnwesenheit.renderCellFettKursiv(cell);
				}
			}
		};
		
		m_table.disableSort();
	}


	/**
	 * Combo zur Wochenauswahl erstellen und füllen
	 * 
	 * @throws Exception
	 */
	private void initComboWochenauswahl() throws Exception {
		int kw;
		GregorianCalendar gregDatum;
		
		// aktuelle KW
		gregDatum = Format.getGregorianCalendar(null);
		kw = gregDatum.get(Calendar.WEEK_OF_YEAR);

		// Combo erstellen und Wert setzen
		m_coWochenauswahl1 = new CoWochenauswahl();
//		m_coWochenauswahl2 = new CoWochenauswahl();
		m_coJahresauswahl = new CoJahresauswahl();

		
		m_comboWochenauswahl1 = (ComboControl) findControl(RESID + ".wochenauswahl1");
//		m_comboWochenauswahl2 = (ComboControl) findControl(RESID + ".wochenauswahl2");
		m_comboJahresauswahl = (ComboControl) findControl(RESID + ".jahresauswahl");
		
		setItemsComboWochenauswahl(m_comboWochenauswahl1, m_coWochenauswahl1, Math.min(kw+2, 53));
//		setItemsComboWochenauswahl(m_comboWochenauswahl2, m_coWochenauswahl2, Math.min(kw+4, 53));
		setItemsComboJahresauswahl(m_comboJahresauswahl, m_coJahresauswahl, gregDatum.get(Calendar.YEAR));
		
	}
	

	/**
	 * Combo zur Wochenauswahl erstellen und füllen
	 * 
	 * @param coWochenauswahl 
	 * @param comboWochenauswahl 
	 * @param kw 
	 * @throws Exception
	 */
	private void setItemsComboWochenauswahl(ComboControl comboWochenauswahl, CoWochenauswahl coWochenauswahl, int kw) throws Exception {
		
		// Combo erstellen und Wert setzen
		coWochenauswahl.createCo();
		
		// zu dem Datensatz mit dem gespeicherten Datum springen
		coWochenauswahl.moveToID(kw);
		comboWochenauswahl.getField().setValue(coWochenauswahl.getID());

		// Items setzen
		refreshItems(comboWochenauswahl, coWochenauswahl, comboWochenauswahl.getField());
	}
	

	/**
	 * Combo zur Jahresauswahl erstellen und füllen
	 * 
	 * @param coJahresauswahl 
	 * @param comboJahresauswahl 
	 * @param jahr 
	 * @throws Exception
	 */
	private void setItemsComboJahresauswahl(ComboControl comboJahresauswahl, CoJahresauswahl coJahresauswahl, int jahr) throws Exception {
		
		// Combo erstellen und Wert setzen
		coJahresauswahl.createCo();
		
		// zu dem Datensatz mit dem gespeicherten Datum springen
		coJahresauswahl.moveToID(jahr);
		comboJahresauswahl.getField().setValue(coJahresauswahl.getID());

		// Items setzen
		refreshItems(comboJahresauswahl, coJahresauswahl, comboJahresauswahl.getField());
	}
	

	/**
	 * Kalender zur Datumsauswahl
	 */
	private void initKalender() {
		m_kalender = new DateTime ((Composite) findControl("group.auswertung.anwesenheit.uebersicht.tagesmeldung").getComponent(), SWT.CALENDAR );
		m_kalender.setBackground(new Color(Display.getCurrent(), new RGB(207, 235, 249)));
		m_kalender.setLocation(6, 30);
		m_kalender.setSize(200, 150);
		m_kalender.pack();
		m_kalender.setBackground(new Color(Display.getCurrent(), new RGB(207, 235, 249)));
	}


	/**
	 * Spaltenbreite der Person ändern
	 * 
	 * @see pze.ui.formulare.auswertung.FormAuswertung#updateHeaderDescription()
	 */
	@Override
	protected void updateHeaderDescription() {
		int iSpalten, anzSpalten;
		String caption;
		GregorianCalendar gregDatum;
		IHeaderDescription headerDescription;
		IColumnDescription columnDescription;
		
		// aktuelles Datum auf Montag setzen
		gregDatum = (GregorianCalendar) m_gregDatum.clone();
		gregDatum.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		

		// alle weiteren Spalten
		headerDescription = m_table.getHeaderDescription();
		anzSpalten = m_co.getColumnCount();
		for (iSpalten=1; iSpalten<anzSpalten; ++iSpalten)
		{
			columnDescription = headerDescription.getColumnDescription(iSpalten);

			// zentriert
			columnDescription.setAlignment(CaptionType.ALIGNCENTER);
			
			// Datum an den Wochentag anfügen
			caption = columnDescription.getCaption();
			columnDescription.setCaption((caption.contains(" ") ? caption.substring(0, caption.indexOf(" ")) : caption) + " "
					+ Format.getString(gregDatum));
			
			// nächster Tag
			gregDatum.add(Calendar.DAY_OF_YEAR, 1);
		}
		
		m_table.setHeaderDescription(headerDescription);
	}


	@Override
	protected void loadCo() throws Exception {
		long a = System.currentTimeMillis();
		
		// aktuelles Datum, falls noch keins festgelegt ist
		if (m_gregDatum == null)
		{
			m_gregDatum = Format.getGregorianCalendar(null);
		}
		
		m_co = new CoAnwesenheitUebersicht(m_coAuswertung, m_gregDatum);
		addExcelExportListener(m_co, m_table, getDefaultExportName(), getProfilePathKey());
		System.out.println("load: " + (System.currentTimeMillis()-a)/1000.);
	}


	/**
	 * Items nicht neu laden, da alle Abteilungen ausgewählt werden dürfen
	 * 
	 * @see pze.ui.formulare.auswertung.FormAuswertung#loadItemsComboAbteilung()
	 */
	protected void loadItemsComboAbteilung() throws Exception {
	}


	@Override
	protected CoAuswertung createCoAuswertung() throws Exception {
		return new CoAuswertungAnwesenheitUebersicht();
	}

	
	/**
	 * Zeitraum als Text
	 * 
	 * @return
	 */
	public String getKw() {
		return "KW " + m_gregDatum.get(Calendar.WEEK_OF_YEAR);
	}
	
	
	/**
	 * @param id Primärschlüssel
	 * @return Item Key im Tabfolder
	 */
	public static String getKey(int id) {
		return RESID + id;
	}
	
	
	@Override
	public String getKey() {
		return getKey(0);
	}
	

	@Override
	public void activate() {
		// activate wird für alle subForms aufgerufen, wenn der Reiter aktiviert wird, deshalb selection prüfen 
		if (((ITabFolder)getParent()).getSelection().equals(RESID))
		{
			addExcelExportListener(m_co, m_table, getDefaultExportName(), getProfilePathKey());
			addPdfExportListener(new ExportAnwesenheitListener(this));
		}
	}
	

	@Override
	public String getDefaultExportName() {
		return "Auswertung_Anwesenheit" + getCoAuswertung().getStringEinschraenkungDatumPerson() + "_" + getKw();
	}


	@Override
	public String getProfilePathKey() {
		return Profile.KEY_AUSWERTUNG_PERSONEN;
	}
	
}
