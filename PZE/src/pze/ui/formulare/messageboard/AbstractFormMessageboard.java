package pze.ui.formulare.messageboard;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import framework.ui.controls.TextControl;
import framework.ui.interfaces.controls.IButtonControl;
import framework.ui.interfaces.controls.IControl;
import framework.ui.interfaces.controls.ITableControl;
import framework.ui.interfaces.selection.ISelectionListener;
import pze.business.Format;
import pze.business.Messages;
import pze.business.objects.AbstractCoMessage;
import pze.business.objects.CoMessage;
import pze.ui.controls.DateControlPze;
import pze.ui.controls.SortedTableControl;
import pze.ui.formulare.IAktionsCenterAbstractForm;
import pze.ui.formulare.AbstractAktionCenterMainForm;
import pze.ui.formulare.UniFormWithSaveLogic;
import pze.ui.formulare.person.FormPerson;
import pze.ui.formulare.projektverwaltung.FormAbruf;
import pze.ui.formulare.projektverwaltung.FormAuftrag;



/**
 * Formular für das Messageboard
 * 
 * @author Lisiecki
 *
 */
public abstract class AbstractFormMessageboard extends UniFormWithSaveLogic implements IAktionsCenterAbstractForm{
	
	protected String m_caption;

	public static String RESID = "form.meldungen";

	private AbstractAktionCenterMainForm m_formMessageboard;
	
	protected AbstractCoMessage m_coMessageOffen;
	protected AbstractCoMessage m_coMessageQuittiert;

	protected IButtonControl m_btInfoQuittieren;
	private IButtonControl m_btAktualisieren;

	protected TextControl m_tfDatumVon;
	protected TextControl m_tfDatumBis;

	private ITableControl m_tableControl;
	private TableMeldungen m_tableMeldungenOffen;
	private SortedTableControl m_tableMeldungenQuittiert;
	
	private int m_messageGruppeID;


	
	/**
	 * Konstruktor
	 * @param parent		visueller Parent
	 * @param caption 
	 * @throws Exception
	 */
	public AbstractFormMessageboard(Object parent, FormMessageboard formMessageboard, String caption, int messageGruppeID) throws Exception {
		this(parent, formMessageboard, caption, messageGruppeID, RESID);
	}

	
	/**
	 * Konstruktor
	 * @param parent		visueller Parent
	 * @param caption 
	 * @throws Exception
	 */
	public AbstractFormMessageboard(Object parent, AbstractAktionCenterMainForm formMessageboard, String caption, int messageGruppeID, String resID) 
			throws Exception {
		super(parent, resID);
		
		m_caption = caption;
		m_formMessageboard = formMessageboard;
		m_messageGruppeID = messageGruppeID;

		initCo();
		setData(m_coMessageOffen);

		initBtMeldungQuittieren();
		initBtAktualisieren();
		
		initTfDatum();

		initTableMeldungenOffen();
		initTableMeldungenQuittiert();
	}


	/**
	 * Standard-CO mit Messages initialisieren
	 * 
	 * @throws Exception
	 */
	protected void initCo() throws Exception {
		m_coMessageOffen = new CoMessage();
		m_coMessageQuittiert = new CoMessage();
	}

	
	private void initBtAktualisieren(){
		m_btAktualisieren = (IButtonControl) findControl(getResID() + ".aktualisieren");
		if (m_btAktualisieren != null)
		{
			m_btAktualisieren.setSelectionListener(new ISelectionListener() {

				@Override
				public void selected(IControl control, Object params) {
					try 
					{
						refreshTableData();
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


	private void initBtMeldungQuittieren() {
		m_btInfoQuittieren = (IButtonControl) findControl(getResID() + ".quittieren");
		if (m_btInfoQuittieren != null)
		{
			m_btInfoQuittieren.setSelectionListener(new ISelectionListener() {

				@Override
				public void selected(IControl control, Object params) {
					try 
					{
						int row, iRow, anzRows, anzBookmarks;
						Set<Object> bookmarks;

						// aktuelle Zeile merken
						row = getSelectedRowMeldung();
						anzRows = getAnzahlSelectedRowsMeldung();
						
						if (Messages.showYesNoMessage("Meldung " + getBezeichnugQuittierung(), "Möchten Sie die "
								+ (anzRows == 1 ? "Meldung '" + m_coMessageOffen.getBeschreibung() + "'" : anzRows + " ausgewählten Meldungen")
								+ " wirklich " + getBezeichnugQuittierung() + "?"))
						{
							// Anzahl ausgewählter Zeilen prüfen
							anzBookmarks = getAnzahlSelectedRowsMeldung();
							
							// wenn nur eine Zeile markiert ist, Quittierung erzeugen
							if (anzBookmarks == 1)
							{
								createQuittierung();
							}
							else
							{
								// alle Zeilen durchlaufen und prüfen, ob die Zeile markiert ist
								bookmarks = new HashSet<Object>(Arrays.asList(m_tableMeldungenOffen.getSelectedBookmarks()));
								anzRows = m_coMessageOffen.getRowCount();
								for (iRow=0; iRow<anzRows; ++iRow)
								{
									setSelectedRowMeldung(iRow);

									// wenn sie markiert ist, gebe sie frei
									if (bookmarks.contains(m_tableMeldungenOffen.getSelectedBookmark()))
									{
										createQuittierung();
									}
								}
							}

							// Daten neu laden
							refreshTableData();
							
							// Zeile wieder markieren
							setSelectedRowMeldung(row);
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
		}
	}
	
	
	/**
	 * Bezeichnung der Quittierung (kann z. B. auch Freigabe sein)
	 * 
	 * @return
	 */
	protected String getBezeichnugQuittierung() {
		return "quittieren";
	}


	/**
	 * Message quittieren
	 * 
	 * @throws Exception
	 */
	protected boolean createQuittierung() throws Exception {
		m_coMessageOffen.createQuittierung();
		
		return true;
	}


	/**
	 * Aktuelle Zeile in Antrags-Tabelle oder -1
	 * 
	 * @return
	 */
	protected int getSelectedRowMeldung() {
		if (m_tableMeldungenOffen.getSelectedBookmark() == null)
		{
			return -1;
		}
		
		return m_coMessageOffen.getCurrentRowIndex();
	}


	/**
	 * Markiert die übergebene Zeile
	 * 
	 * @param row
	 */
	protected void setSelectedRowMeldung(int row) {
		// wenn nichts markiert war, beende Methode
		if (row < 0)
		{
			return;
		}
		
		// Zeile markieren
		if (!m_coMessageOffen.moveTo(row))
		{
			// wenn es die letzte war, gehe wieder zur letzten
			if (!m_coMessageOffen.moveLast())
			{
				// falls es keine Zeile mehr gibt
				m_tableMeldungenOffen.setSelectedBookmark(null);
			}
		}
		
		m_tableMeldungenOffen.setSelectedBookmark(m_coMessageOffen.getBookmark());
		refreshBtQuittierung();
	}


	/**
	 * Aktuelle Anzahl markierte Zeilen in Antrags-Tabelle oder -1
	 * 
	 * @return
	 */
	protected int getAnzahlSelectedRowsMeldung() {
		return m_tableMeldungenOffen.getSelectedBookmarks().length;
	}


	private void initTfDatum() {
		m_tfDatumVon = (DateControlPze) findControl(getResID() + ".al.datumvon");
		m_tfDatumBis = (TextControl) findControl(getResID() + ".al.datumbis");
		
		m_tfDatumVon.getField().setValue(Format.getDateVerschobenWochen(Format.getDate12Uhr(new Date()), -2));
		m_tfDatumBis.getField().setValue(Format.getDate12Uhr(new Date()));
	}


	private void initTableMeldungenOffen() throws Exception {
		m_tableControl = (ITableControl) findControl(getResID().replace("form", "spread") + ".offen");
		m_tableMeldungenOffen = new TableMeldungen(m_tableControl, m_coMessageOffen, this);
	}


	private void initTableMeldungenQuittiert() throws Exception {

		m_tableMeldungenQuittiert = new SortedTableControl(findControl(getResID().replace("form", "spread") + ".quittiert")){

			@Override
			public void tableSelected(IControl arg0, Object arg1){
				super.tableSelected(arg0, arg1);
				
				m_coMessageQuittiert.moveTo(m_tableMeldungenQuittiert.getSelectedBookmark());
			}

			@Override
			public void tableDefaultSelected(IControl arg0, Object arg1) throws Exception {
				FormPerson formPerson;

				// ggf. Projekt öffnen
				if (m_coMessageQuittiert.getAuftragID() > 0)
				{
					FormAuftrag.open(getSession(), m_coMessageQuittiert.getAuftragID());
				}
				else if (m_coMessageQuittiert.getAbrufID() > 0)
				{
					FormAbruf.open(getSession(), m_coMessageQuittiert.getAbrufID());
				}
				else
				{
					// sonst Person öffnen
					formPerson = FormPerson.open(getSession(), null, (m_coMessageQuittiert).getPersonID());
					if (formPerson != null)
					{
						formPerson.showZeiterfassung((m_coMessageQuittiert).getDatum());
					}
				}
			}
		};
		
	}


	/**
	 * Daten für alle Tabellen laden und zuordnen
	 * 
	 * @throws Exception
	 */
	@Override
	public void reloadTableData() throws Exception {
		
		// offene Meldungen laden
		loadMeldungenOffen();
		m_tableMeldungenOffen.setData(m_coMessageOffen);
		
		// bereits quittierte Meldungen laden
		loadMeldungenQuittiert();
		m_tableMeldungenQuittiert.setData(m_coMessageQuittiert);
		
		refresh(reasonDataChanged, null);
	}

	
	/**
	 * offene Meldungen laden
	 * 
	 * @throws Exception
	 */
	protected void loadMeldungenOffen() throws Exception {
		m_coMessageOffen.loadByStatusOffen(m_messageGruppeID, getMeldungsTyp());
	}


	/**
	 * quittierte Meldungen laden
	 * 
	 * @throws Exception
	 */
	protected void loadMeldungenQuittiert() throws Exception {
		m_coMessageQuittiert.loadByStatusQuittiert(m_messageGruppeID, getMeldungsTyp(), m_tfDatumVon.getField().getDateValue(), m_tfDatumBis.getField().getDateValue());
	}

	
	/**
	 * Feldbezeichnung in der Reftabelle, um die Meldungen zu filtern
	 * 
	 * @return
	 */
	protected String getMeldungsTyp() {
		return "IstMessagePerson";
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
	public String getDefaultCaption() {
		return m_caption;
	}
	
	
	/**
	 * Tabellen neu laden und ggf. TabItem anpassen
	 * @return 
	 * 
	 * @throws Exception
	 */
	@Override
	public void refreshTableData() throws Exception {
		reloadTableData();
		refreshCaption();
		
		m_formMessageboard.refreshAllTabs(false);
	}


	/**
	 * Caption und Key des Formulars anpassen an den Inhalt
	 * @return 
	 */
	@Override
	public void refreshCaption() {
		m_formMessageboard.refreshCaption(this);
	}


	/**
	 * Caption und Key des Formulars anpassen an den Inhalt
	 * @return 
	 */
	@Override
	public int getAnzAntraegeAktiviert() {
		return m_coMessageOffen == null ? 0 : m_coMessageOffen.getRowCount();
//		return m_coMessageOffen == null ? 0 : m_coMessageOffen.getAnzahlFreigabeMoeglich(m_nextStatusGenehmigungID);
	}


	@Override
	public void refresh(int reason, Object element){
		super.refresh(reason, element);
		
		refreshBtQuittierung();
		
		// Datumseingabe ist immer möglich
		m_tfDatumVon.refresh(reasonEnabled, null);
		m_tfDatumBis.refresh(reasonEnabled, null);
		
		// aktualisieren ist immer möglich
		m_btAktualisieren.refresh(reasonEnabled, null);
	}
	

	/**
	 * Button zur Quittierung refreshen
	 */
	public void refreshBtQuittierung() {
		if (m_btInfoQuittieren == null)
		{
			return;
		}
		
		try 
		{
			// Buttons deaktivieren und bei Bedarf aktivieren
			m_btInfoQuittieren.refresh(reasonDisabled, null);

			// keine Meldungen oder keine Meldung ausgewählt
			if (m_coMessageOffen == null || m_coMessageOffen.getRowCount() == 0 
					// im Messageboard können mehrere Verletzermeldungen gleichzeitig freigegeben werden, im Freigabecenter nicht
					|| (getSelectedRowMeldung() < 0 && (getAnzahlSelectedRowsMeldung() == 0 || getResID().equals(FormMessageboardVerletzermeldungen.RESID_FREIGABE))))
			{
				return;
			}

			// prüfen, ob die aktuelle Buchung freigegeben werden darf
//			if (m_coMessageOffen.isFreigabeMoeglich(m_nextStatusGenehmigungID))
			{
				m_btInfoQuittieren.refresh(reasonEnabled, null);
			}
		} catch (Exception e) 
		{
			e.printStackTrace();
		}
	}


	@Override
	public void activate() {
		try
		{
			// Daten bei Aktivierung des Reiters neu laden
			refreshTableData();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
//		addExcelExportListener(m_coBuchungen, m_tableBuchungen, "Uebersicht_Freigaben", Profile.KEY_ADMINISTRATION);
		super.activate();
	}
	

}
