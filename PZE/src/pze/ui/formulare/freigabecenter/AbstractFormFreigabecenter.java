package pze.ui.formulare.freigabecenter;

import java.util.Date;

import framework.ui.controls.TextControl;
import framework.ui.interfaces.controls.IButtonControl;
import framework.ui.interfaces.controls.IControl;
import framework.ui.interfaces.controls.ITableControl;
import framework.ui.interfaces.selection.ISelectionListener;
import pze.business.Format;
import pze.business.Messages;
import pze.business.UserInformation;
import pze.business.objects.CoMessage;
import pze.business.objects.dienstreisen.CoDienstreise;
import pze.business.objects.personen.CoBuchung;
import pze.business.objects.reftables.buchungen.CoStatusGenehmigung;
import pze.ui.controls.DateControlPze;
import pze.ui.controls.SortedTableControl;
import pze.ui.formulare.IAktionsCenterAbstractForm;
import pze.ui.formulare.AbstractAktionCenterMainForm;
import pze.ui.formulare.UniFormWithSaveLogic;
import pze.ui.formulare.person.DialogBuchung;



/**
 * Formular für das Freigabecenter
 * 
 * @author Lisiecki
 *
 */
public abstract class AbstractFormFreigabecenter extends UniFormWithSaveLogic implements IAktionsCenterAbstractForm{
	
	protected String m_caption;

	public static String RESID = "form.freigaben";

	private AbstractAktionCenterMainForm m_formFreigabecenter;
	
	protected CoBuchung m_coBuchung;
	protected CoBuchung m_coBuchungUrlaub;
	protected CoBuchung m_coBuchungDr;
	protected CoBuchung m_coBuchungBearbeitet;

	private IButtonControl m_btBuchungFreigeben;
	private IButtonControl m_btBuchungUrlaubFreigeben;
	private IButtonControl m_btBuchungDrFreigeben;
	private IButtonControl m_btBuchungAblehnen;
	private IButtonControl m_btBuchungUrlaubAblehnen;
	private IButtonControl m_btBuchungDrAblehnen;
	private IButtonControl m_btAktualisieren;

	protected TextControl m_tfDatumVon;
	protected TextControl m_tfDatumBis;

	private ITableControl m_tableControl;
	private ITableControl m_tableControlUrlaub;
	private ITableControl m_tableControlDr;
	
	private TableFreigaben m_tableBuchungen;
	private TableFreigaben m_tableBuchungenUrlaub;
	private TableFreigaben m_tableBuchungenDr;
	
	private SortedTableControl m_tableBuchungenBearbeitet;

	protected int m_nextStatusGenehmigungID;

	
	/**
	 * Konstruktor
	 * @param parent		visueller Parent
	 * @param caption 
	 * @throws Exception
	 */
	public AbstractFormFreigabecenter(Object parent, AbstractAktionCenterMainForm formFreigabecenter, int nextStatusGenehmigungID, String caption) throws Exception {
		super(parent, RESID);
		
		m_caption = caption;
		
		m_formFreigabecenter = formFreigabecenter;
		m_nextStatusGenehmigungID = nextStatusGenehmigungID;

		m_coBuchung = new CoBuchung();
		m_coBuchungUrlaub = new CoBuchung();
		m_coBuchungDr = new CoBuchung();
		m_coBuchungBearbeitet = new CoBuchung();
		setData(m_coBuchung);

		initBtBuchungFreigeben();
		initBtBuchungAblehnen();
		initBtAktualisieren();
		
		initTfDatum();

		initTableBuchungen();
		initTableBuchungenBearbeitet();
		
//		refreshTableData();
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


	private void initBtBuchungFreigeben() {
		m_btBuchungFreigeben = (IButtonControl) findControl("form.freigaben.genehmigen");
		if (m_btBuchungFreigeben != null)
		{
			m_btBuchungFreigeben.setSelectionListener(new ISelectionListener() {

				@Override
				public void selected(IControl control, Object params) {
					clickedBuchungFreigeben(m_coBuchung, m_tableBuchungen);
				}

	
				@Override
				public void defaultSelected(IControl control, Object params) {
				}
			});
		}

		
		m_btBuchungUrlaubFreigeben = (IButtonControl) findControl("form.freigaben.urlaub.genehmigen");
		if (m_btBuchungUrlaubFreigeben != null)
		{
			m_btBuchungUrlaubFreigeben.setSelectionListener(new ISelectionListener() {

				@Override
				public void selected(IControl control, Object params) {
					clickedBuchungFreigeben(m_coBuchungUrlaub, m_tableBuchungenUrlaub);
				}

	
				@Override
				public void defaultSelected(IControl control, Object params) {
				}
			});
		}

		
		m_btBuchungDrFreigeben = (IButtonControl) findControl("form.freigaben.dr.genehmigen");
		if (m_btBuchungDrFreigeben != null)
		{
			m_btBuchungDrFreigeben.setSelectionListener(new ISelectionListener() {

				@Override
				public void selected(IControl control, Object params) {
					clickedBuchungFreigeben(m_coBuchungDr, m_tableBuchungenDr);
				}

	
				@Override
				public void defaultSelected(IControl control, Object params) {
				}
			});
		}
	}


	private void initBtBuchungAblehnen() {
		m_btBuchungAblehnen = (IButtonControl) findControl("form.freigaben.ablehnen");
		if (m_btBuchungAblehnen != null)
		{
			m_btBuchungAblehnen.setSelectionListener(new ISelectionListener() {

				@Override
				public void selected(IControl control, Object params) {
					clickedBuchungAblehnen(m_coBuchung, m_tableBuchungen);
				}

	
				@Override
				public void defaultSelected(IControl control, Object params) {
				}
			});
		}

		
		m_btBuchungUrlaubAblehnen = (IButtonControl) findControl("form.freigaben.urlaub.ablehnen");
		if (m_btBuchungUrlaubAblehnen != null)
		{
			m_btBuchungUrlaubAblehnen.setSelectionListener(new ISelectionListener() {

				@Override
				public void selected(IControl control, Object params) {
					clickedBuchungAblehnen(m_coBuchungUrlaub, m_tableBuchungenUrlaub);
				}

	
				@Override
				public void defaultSelected(IControl control, Object params) {
				}
			});
		}

		
		m_btBuchungDrAblehnen = (IButtonControl) findControl("form.freigaben.dr.ablehnen");
		if (m_btBuchungDrAblehnen != null)
		{
			m_btBuchungDrAblehnen.setSelectionListener(new ISelectionListener() {

				@Override
				public void selected(IControl control, Object params) {
					clickedBuchungAblehnen(m_coBuchungDr, m_tableBuchungenDr);
				}

	
				@Override
				public void defaultSelected(IControl control, Object params) {
				}
			});
		}
	}


	/**
	 * Aktuell markierte Buchung freigeben
	 * 
	 * @param coBuchung
	 * @param tableBuchungen 
	 */
	private void clickedBuchungFreigeben(CoBuchung coBuchung, TableFreigaben tableBuchungen) {
		String message;
		Date datum, datumBis;
		
		try 
		{
			datum = coBuchung.getDatum();
			datumBis = coBuchung.getDatumBis();
			message = "Antrag '" + coBuchung.getBuchungsart() + "' vom "
					+ Format.getString(datum) + (datumBis == null || datumBis.equals(datum) ? "" : "-" + Format.getString(datumBis));
			
			if (Messages.showYesNoMessage("Antrag freigeben", "Möchten Sie den " + message + " für " + coBuchung.getPerson() + " wirklich freigeben?"))
			{
				if (!checkBeforeFreigabe(coBuchung))
				{
					return;
				}
				
				// aktuelle Zeile merken
				int row = getSelectedRowAntrag(coBuchung, tableBuchungen);

				createFreigabe(coBuchung);
				
				// Message erzeugen
				if (coBuchung.isGenehmigt())
				{
					new CoMessage().createMessageAntragGenehmigt(UserInformation.getPersonID(), coBuchung.getID(), message);
				}
				
				// Tabelle aktualisieren
				refreshTableData();
				
				// Zeile wieder markieren
				setSelectedRowAntrag(row, coBuchung, tableBuchungen);
			}
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}


	/**
	 * Prüfung, ob die Freigabe erteilt werden kann
	 * 
	 * @param coBuchung 
	 * @return
	 * @throws Exception 
	 */
	protected boolean checkBeforeFreigabe(CoBuchung coBuchung) throws Exception {
		return true;
	}


	/**
	 * Aktuell markierte Buchung ablehnen
	 * 
	 * @param coBuchung
	 * @param tableBuchungen
	 */
	private void clickedBuchungAblehnen(CoBuchung coBuchung, TableFreigaben tableBuchungen) {
		String message;
		Date datumBis;
		
		try 
		{
			datumBis = coBuchung.getDatumBis();
			message = "Antrag '" + coBuchung.getBuchungsart() + "' vom "
					+ Format.getString(coBuchung.getDatum()) + (datumBis == null ? "" : "-" + Format.getString(datumBis));
			
			if (Messages.showYesNoErrorMessage("Antrag ablehnen", "Möchten Sie den " + message + " für " + coBuchung.getPerson()
					+ " wirklich ablehnen?<br><br>"
					+ "Bitte geben Sie im nächsten Dialogfenster einen Grund als Bemerkung ein."))
			{
				// Dialog mit der Buchung öffnen, um einen Grund als Bemerkung einzutragen
				if (!DialogBuchung.showDialogWithBuchung(coBuchung.getID(), true))
//					if (!DialogBuchung.showDialogWithBuchung(coBuchung.equals(m_coBuchung) ? m_coBuchung.getID() : m_coBuchungUrlaub.getID(), true))
				{
					return;
				}

				// Message erzeugen
				new CoMessage().createMessageAntragAbgelehnt(UserInformation.getPersonID(), coBuchung.getID(), message);

				// aktuelle Zeile merken
				int row = getSelectedRowAntrag(coBuchung, tableBuchungen);

				// Freigabe-Eintrag erzeugen
				if (coBuchung.equals(m_coBuchung))
				{
					coBuchung.createFreigabeAbgelehnt();
				}
				else if (coBuchung.equals(m_coBuchungDr))
				{
					// Freigabe für alle Tage
					CoBuchung coBuchungTage;
					coBuchungTage = new CoBuchung();
					coBuchungTage.loadByDienstreiseID(coBuchung.getDienstreiseID());
					coBuchungTage.createFreigabeForAll(CoStatusGenehmigung.STATUSID_ABGELEHNT);
					
					// Grund auch in der DR speichern
					coBuchungTage = new CoBuchung();
					coBuchungTage.loadByID(coBuchung.getID());

					CoDienstreise coDienstreise;
					coDienstreise = new CoDienstreise();
					coDienstreise.loadByID(coBuchung.getDienstreiseID());
					
					coDienstreise.begin();
					coDienstreise.setBemerkung((coDienstreise.getBemerkung() == null ? "" : coDienstreise.getBemerkung() + "; ") + coBuchungTage.getBemerkung());
					coDienstreise.save();
					
				}
				else if (coBuchung.equals(m_coBuchungUrlaub))
				{
					coBuchung.createFreigabeUrlaub(CoStatusGenehmigung.STATUSID_ABGELEHNT);
				}

				
				refreshTableData();
				
				// Zeile wieder markieren
				setSelectedRowAntrag(row, coBuchung, tableBuchungen);
			}
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}


	/**
	 * Freigabe für den aktuell markierten Antrag erstellen 
	 * 
	 * @throws Exception
	 */
	protected abstract void createFreigabe(CoBuchung coBuchung) throws Exception;


	/**
	 * Aktuelle Zeile in Antrags-Tabelle oder -1
	 * @param tableBuchungen 
	 * @param coBuchung 
	 * 
	 * @return
	 */
	private int getSelectedRowAntrag(CoBuchung coBuchung, TableFreigaben tableBuchungen) {
		if (tableBuchungen.getSelectedBookmark() == null)
		{
			return -1;
		}
		
		return coBuchung.getCurrentRowIndex();
	}


	/**
	 * Markiert die übergebene Antrags-Zeile
	 * 
	 * @param row
	 * @param tableBuchungen 
	 * @param coBuchung 
	 */
	private void setSelectedRowAntrag(int row, CoBuchung coBuchung, TableFreigaben tableBuchungen) {
		// wenn nichts markiert war, beende Methode
		if (row < 0)
		{
			return;
		}
		
		// nächste Zeile markieren
		if (!coBuchung.moveTo(row))
		{
			// wenn es die letzte war, gehe wieder zur letzten
			if (!coBuchung.moveLast())
			{
				// falls es keine Zeile mehr gibt
				tableBuchungen.setSelectedBookmark(null);
			}
		}
		
		tableBuchungen.setSelectedBookmark(coBuchung.getBookmark());
		refreshBtFreigabe();
	}


	private void initTfDatum() {
		m_tfDatumVon = (DateControlPze) findControl(getResID() + ".al.datumvon");
		m_tfDatumBis = (TextControl) findControl(getResID() + ".al.datumbis");
		
		m_tfDatumVon.getField().setValue(Format.getDateVerschobenWochen(Format.getDate12Uhr(new Date()), -2));
		m_tfDatumBis.getField().setValue(Format.getDateVerschobenWochen(Format.getDate12Uhr(new Date()), 2));
	}


	private void initTableBuchungen() throws Exception {
		
		m_tableControl = (ITableControl) findControl("spread.freigaben");
		m_tableBuchungen = new TableFreigaben(m_tableControl, m_coBuchung, this, m_nextStatusGenehmigungID);
		
		m_tableControlUrlaub = (ITableControl) findControl("spread.freigaben.urlaub");
		m_tableBuchungenUrlaub = new TableFreigaben(m_tableControlUrlaub, m_coBuchungUrlaub, this, m_nextStatusGenehmigungID);

		m_tableControlDr = (ITableControl) findControl("spread.freigaben.dr");
		m_tableBuchungenDr = new TableFreigaben(m_tableControlDr, m_coBuchungDr, this, m_nextStatusGenehmigungID);

	}


	private void initTableBuchungenBearbeitet() throws Exception {

		m_tableBuchungenBearbeitet = new SortedTableControl(findControl("spread.freigaben.al2")){

			@Override
			public void tableSelected(IControl arg0, Object arg1){
				super.tableSelected(arg0, arg1);
				
				m_coBuchungBearbeitet.moveTo(m_tableBuchungenBearbeitet.getSelectedBookmark());
			}

			@Override
			public void tableDefaultSelected(IControl arg0, Object arg1) throws Exception {
				DialogBuchung.showDialogWithBuchung(m_coBuchungBearbeitet.getID());
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
		
		// aktuelle Anträge laden
		loadAntraegeOfa(false);
//		m_coBuchung.addField("virt.field.freigabe.isausgewaehlt");
		m_tableBuchungen.setData(m_coBuchung);
		
		// aktuelle Anträge laden
		loadAntraegeUrlaub(true);
//		m_coBuchung.addField("virt.field.freigabe.isausgewaehlt");
		m_tableBuchungenUrlaub.setData(m_coBuchungUrlaub);
		
		// aktuelle Anträge laden
		loadAntraegeDr(true);
//		m_coBuchung.addField("virt.field.freigabe.isausgewaehlt");
		m_tableBuchungenDr.setData(m_coBuchungDr);
		
		// bereits bearbeitete Anträge laden
		loadAntraegeBearbeitet();
		m_tableBuchungenBearbeitet.setData(m_coBuchungBearbeitet);
		
		refresh(reasonDataChanged, null);
	}


	/**
	 * Anträge für Tabelle zur Bearbeitung laden
	 * 
	 * @param zeitraum 
	 * @throws Exception
	 */
	protected abstract void loadAntraegeOfa(boolean zeitraum) throws Exception;


	/**
	 * Anträge für Tabelle zur Bearbeitung laden
	 * 
	 * @param zeitraum 
	 * @throws Exception
	 */
	protected abstract void loadAntraegeUrlaub(boolean zeitraum) throws Exception;


	/**
	 * Anträge für Tabelle zur Bearbeitung laden
	 * 
	 * @param zeitraum 
	 * @throws Exception
	 */
	protected abstract void loadAntraegeDr(boolean zeitraum) throws Exception;

	
	/**
	 * Bearbeitete Anträge für Tabelle laden
	 * 
	 * @throws Exception
	 */
	protected abstract void loadAntraegeBearbeitet() throws Exception;

	
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
		long a = System.currentTimeMillis();
		reloadTableData();
		refreshCaption();
		System.out.println("refresh TableData " + getClass() + ":" + Format.getFormat2NksPunkt((System.currentTimeMillis() - a)/1000.));
		
		m_formFreigabecenter.refreshAllTabs(false);
	}


	/**
	 * Caption und Key des Formulars anpassen an den Inhalt
	 * @return 
	 */
	@Override
	public void refreshCaption() {
		m_formFreigabecenter.refreshCaption(this); // TODO wird das benötigt, oder wird refreshAllTabs aufgerufe?s 
	}


	public CoBuchung getCoBuchung() {
		return m_coBuchung;
	}


	public CoBuchung getCoBuchungUrlaub() {
		return m_coBuchungUrlaub;
	}


	public CoBuchung getCoBuchungDr() {
		return m_coBuchungDr;
	}


	/**
	 * Caption und Key des Formulars anpassen an den Inhalt
	 * @return 
	 */
	@Override
	public int getAnzAntraegeAktiviert() {
		return m_coBuchung == null ? 0 : m_coBuchung.getAnzahlFreigabeMoeglich(m_nextStatusGenehmigungID)
				+ (m_coBuchungUrlaub == null ? 0 : m_coBuchungUrlaub.getAnzahlFreigabeMoeglich(m_nextStatusGenehmigungID))
				+ (m_coBuchungDr == null ? 0 : m_coBuchungDr.getAnzahlFreigabeMoeglich(m_nextStatusGenehmigungID))
				;
	}


	@Override
	public void refresh(int reason, Object element){
		super.refresh(reason, element);
		
		refreshBtFreigabe();
		
		// Datumseingabe ist immer möglich
		m_tfDatumVon.refresh(reasonEnabled, null);
		m_tfDatumBis.refresh(reasonEnabled, null);
		
		// aktualisieren ist immer möglich
		m_btAktualisieren.refresh(reasonEnabled, null);
	}
	

	/**
	 * Button zur Freigabe von Buchungen refreshen
	 */
	public void refreshBtFreigabe() {
		try 
		{
			refreshBtFreigabe(m_btBuchungFreigeben, m_btBuchungAblehnen, m_coBuchung, m_tableBuchungen);
			refreshBtFreigabe(m_btBuchungUrlaubFreigeben, m_btBuchungUrlaubAblehnen, m_coBuchungUrlaub, m_tableBuchungenUrlaub);
			refreshBtFreigabe(m_btBuchungDrFreigeben, m_btBuchungDrAblehnen, m_coBuchungDr, m_tableBuchungenDr);
		} catch (Exception e) 
		{
			e.printStackTrace();
		}
	}


	/**
	 * Button zur Freigabe von Buchungen refreshen
	 * 
	 * @param tableBuchungen
	 * @param coBuchung
	 * @param btBuchungAblehnen
	 * @param btBuchungFreigeben
	 */
	private void refreshBtFreigabe(IButtonControl btBuchungFreigeben, IButtonControl btBuchungAblehnen, CoBuchung coBuchung, TableFreigaben tableBuchungen) {
		// Buttons deaktivieren und bei Bedarf aktivieren
		btBuchungFreigeben.refresh(reasonDisabled, null);
		btBuchungAblehnen.refresh(reasonDisabled, null);

		if (coBuchung == null || coBuchung.getRowCount() == 0 || getSelectedRowAntrag(coBuchung, tableBuchungen) < 0)
		{
			return;
		}

		// prüfen, ob die aktuelle Buchung freigegeben werden darf
		if (coBuchung.isFreigabeMoeglich(m_nextStatusGenehmigungID))
		{
			btBuchungFreigeben.refresh(reasonEnabled, null);
			btBuchungAblehnen.refresh(reasonEnabled, null);
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
