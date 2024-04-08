package pze.ui.formulare.freigabecenter;

import java.util.Date;

import framework.ui.controls.TextControl;
import framework.ui.interfaces.controls.IButtonControl;
import framework.ui.interfaces.controls.IControl;
import framework.ui.interfaces.selection.ISelectionListener;
import pze.business.Format;
import pze.business.UserInformation;
import pze.business.objects.personen.CoBuchung;
import pze.ui.controls.DateControlPze;
import pze.ui.formulare.AbstractAktionCenterMainForm;
import pze.ui.formulare.IAktionsCenterAbstractForm;
import pze.ui.formulare.UniFormWithSaveLogic;



/**
 * Formular für das Freigabecenter
 * 
 * @author Lisiecki
 *
 */
public abstract class AbstractFormFreigabecenterMitarbeiter extends UniFormWithSaveLogic implements IAktionsCenterAbstractForm{
	
	protected String m_caption;

	private AbstractAktionCenterMainForm m_formFreigabecenter;
	
	protected CoBuchung m_coBuchungGeplant;
	protected CoBuchung m_coBuchungAktuell;
	protected CoBuchung m_coBuchungAbgeschlossen;

	private IButtonControl m_btBuchungGeplantBeantragen;
	private IButtonControl m_btBuchungGeplantAendern;
	private IButtonControl m_btBuchungGeplantLoeschen;
	
	protected IButtonControl m_btBuchungAktuellAendern;
	protected IButtonControl m_btBuchungAktuellLoeschen;
	
	private IButtonControl m_btBuchungAbgeschlossenAendern;
	private IButtonControl m_btBuchungAbgeschlossenLoeschen;
	
	private IButtonControl m_btAktualisieren;

	protected TextControl m_tfDatumVon;
	protected TextControl m_tfDatumBis;

	private TableAntraege m_tableBuchungenGeplant;
	protected TableAntraege m_tableBuchungenAktuell;
	private TableAntraege m_tableBuchungenAbgeschlossen;
	
	protected int m_personID;


	
	/**
	 * Konstruktor
	 * @param parent		visueller Parent
	 * @param caption 
	 * @param resID 
	 * @throws Exception
	 */
	public AbstractFormFreigabecenterMitarbeiter(Object parent, AbstractAktionCenterMainForm formFreigabecenter, String caption, String resID) throws Exception {
		super(parent, resID);
		
		m_personID = UserInformation.getPersonID();

		m_caption = caption;
		
		m_formFreigabecenter = formFreigabecenter;

		m_coBuchungGeplant = new CoBuchung();
		m_coBuchungAktuell = new CoBuchung();
		m_coBuchungAbgeschlossen = new CoBuchung();
		setData(m_coBuchungGeplant);

		initBtBuchungGeplantBeantragen();
		initBtBuchungGeplantAendern();
		initBtBuchungGeplantLoeschen();
		initBtBuchungAbgeschlossenAendern();
		initBtBuchungAbgeschlossenLoeschen();
		initBtBuchungAktuellAendern();
		initBtBuchungAktuellLoeschen();
		initBtAktualisieren();
		
		initTfDatum();

		initTableBuchungen();
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
	 * Daten neu laden
	 * 
	 * @throws Exception
	 */
	protected void clickedAktualisieren() throws Exception {
		refreshTableData();
	}


	private void initBtBuchungGeplantBeantragen() {
		m_btBuchungGeplantBeantragen = (IButtonControl) findControl(getResID() + ".geplant.beantragen");
		if (m_btBuchungGeplantBeantragen != null)
		{
			m_btBuchungGeplantBeantragen.setSelectionListener(new ISelectionListener() {

				@Override
				public void selected(IControl control, Object params) {
					try 
					{
						// aktuelle Zeile merken
						int row = getSelectedRowAntrag(m_coBuchungGeplant, m_tableBuchungenGeplant);

						clickedBuchungBeantragen(m_coBuchungGeplant, m_tableBuchungenGeplant);
						
						// Zeile wieder markieren
						setSelectedRowAntrag(row, m_coBuchungGeplant, m_tableBuchungenGeplant);
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


	private void initBtBuchungGeplantAendern() {
		m_btBuchungGeplantAendern = (IButtonControl) findControl(getResID() + ".geplant.aendern");
		if (m_btBuchungGeplantAendern != null)
		{
			m_btBuchungGeplantAendern.setSelectionListener(new ISelectionListener() {

				@Override
				public void selected(IControl control, Object params) {
					try 
					{
						// aktuelle Zeile merken
						int row = getSelectedRowAntrag(m_coBuchungGeplant, m_tableBuchungenGeplant);

						clickedBuchungAendern(m_coBuchungGeplant, m_tableBuchungenGeplant);
						
						// Zeile wieder markieren
						setSelectedRowAntrag(row, m_coBuchungGeplant, m_tableBuchungenGeplant);
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


	private void initBtBuchungGeplantLoeschen() {
		m_btBuchungGeplantLoeschen = (IButtonControl) findControl(getResID() + ".geplant.loeschen");
		if (m_btBuchungGeplantLoeschen != null)
		{
			m_btBuchungGeplantLoeschen.setSelectionListener(new ISelectionListener() {

				@Override
				public void selected(IControl control, Object params) {
					try 
					{
						// aktuelle Zeile merken
						int row = getSelectedRowAntrag(m_coBuchungGeplant, m_tableBuchungenGeplant);

						clickedBuchungLoeschen(m_coBuchungGeplant, m_tableBuchungenGeplant);
						
						// Zeile wieder markieren
						setSelectedRowAntrag(row, m_coBuchungGeplant, m_tableBuchungenGeplant);
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


	private void initBtBuchungAbgeschlossenAendern() {
		m_btBuchungAbgeschlossenAendern = (IButtonControl) findControl(getResID() + ".abgeschlossen.aendern");
		if (m_btBuchungAbgeschlossenAendern != null)
		{
			m_btBuchungAbgeschlossenAendern.setSelectionListener(new ISelectionListener() {

				@Override
				public void selected(IControl control, Object params) {
					try 
					{
						// aktuelle Zeile merken
						int row = getSelectedRowAntrag(m_coBuchungAbgeschlossen, m_tableBuchungenAbgeschlossen);

						clickedBuchungAendern(m_coBuchungAbgeschlossen, m_tableBuchungenAbgeschlossen);
						
						// Zeile wieder markieren
						setSelectedRowAntrag(row, m_coBuchungAbgeschlossen, m_tableBuchungenAbgeschlossen);
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


	private void initBtBuchungAbgeschlossenLoeschen() {
		m_btBuchungAbgeschlossenLoeschen = (IButtonControl) findControl(getResID() + ".abgeschlossen.loeschen");
		if (m_btBuchungAbgeschlossenLoeschen != null)
		{
			m_btBuchungAbgeschlossenLoeschen.setSelectionListener(new ISelectionListener() {

				@Override
				public void selected(IControl control, Object params) {
					try 
					{
						// aktuelle Zeile merken
						int row = getSelectedRowAntrag(m_coBuchungAbgeschlossen, m_tableBuchungenAbgeschlossen);

						clickedBuchungLoeschen(m_coBuchungAbgeschlossen, m_tableBuchungenAbgeschlossen);
						
						// Zeile wieder markieren
						setSelectedRowAntrag(row, m_coBuchungAbgeschlossen, m_tableBuchungenAbgeschlossen);
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


	private void initBtBuchungAktuellAendern() {
		m_btBuchungAktuellAendern = (IButtonControl) findControl(getResID() + ".aktuell.aendern");
		if (m_btBuchungAktuellAendern != null)
		{
			m_btBuchungAktuellAendern.setSelectionListener(new ISelectionListener() {

				@Override
				public void selected(IControl control, Object params) {
					try 
					{
						// aktuelle Zeile merken
						int row = getSelectedRowAntrag(m_coBuchungAktuell, m_tableBuchungenAktuell);

						clickedBuchungAendern(m_coBuchungAktuell, m_tableBuchungenAktuell);
						
						// Zeile wieder markieren
						setSelectedRowAntrag(row, m_coBuchungAktuell, m_tableBuchungenAktuell);
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


	private void initBtBuchungAktuellLoeschen() {
		m_btBuchungAktuellLoeschen = (IButtonControl) findControl(getResID() + ".aktuell.loeschen");
		if (m_btBuchungAktuellLoeschen != null)
		{
			m_btBuchungAktuellLoeschen.setSelectionListener(new ISelectionListener() {

				@Override
				public void selected(IControl control, Object params) {
					try 
					{
						// aktuelle Zeile merken
						int row = getSelectedRowAntrag(m_coBuchungAktuell, m_tableBuchungenAktuell);

						clickedBuchungLoeschen(m_coBuchungAktuell, m_tableBuchungenAktuell);
						
						// Zeile wieder markieren
						setSelectedRowAntrag(row, m_coBuchungAktuell, m_tableBuchungenAktuell);
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
	 * Aktuell markierte Buchung beantragen
	 * 
	 * @param coBuchung
	 * @param tableBuchungen 
	 */
	protected abstract void clickedBuchungBeantragen(CoBuchung coBuchung, TableAntraege tableBuchungen) throws Exception;


	/**
	 * Aktuell markierte Buchung ändern
	 * 
	 * @param coBuchung
	 * @param tableBuchungen
	 */
	protected abstract void clickedBuchungAendern(CoBuchung coBuchung, TableAntraege tableBuchungen) throws Exception;


	/**
	 * Aktuell markierte Buchung löschen
	 * 
	 * @param coBuchung
	 * @param tableBuchungen
	 */
	protected abstract void clickedBuchungLoeschen(CoBuchung coBuchung, TableAntraege tableBuchungen) throws Exception;


	/**
	 * Aktuelle Zeile in Antrags-Tabelle oder -1
	 * @param tableBuchungen 
	 * @param coBuchung 
	 * 
	 * @return
	 */
	private int getSelectedRowAntrag(CoBuchung coBuchung, TableAntraege tableBuchungen) {
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
	private void setSelectedRowAntrag(int row, CoBuchung coBuchung, TableAntraege tableBuchungen) {
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
		refreshBtAntraegeGeplant();
	}


	private void initTfDatum() {
		m_tfDatumVon = (DateControlPze) findControl(getResID() + ".datumvon");
		m_tfDatumBis = (TextControl) findControl(getResID() + ".datumbis");
		
		m_tfDatumVon.getField().setValue(Format.getDateVerschobenWochen(Format.getDate12Uhr(new Date()), -2));
		m_tfDatumBis.getField().setValue(Format.getDateVerschobenWochen(Format.getDate12Uhr(new Date()), 2));
	}


	private void initTableBuchungen() throws Exception {
		String resID;
		
		resID = getResID().replace("form", "spread");
		
		m_tableBuchungenGeplant = new TableAntraege(findControl(resID + ".geplant"), m_coBuchungGeplant, this);
		m_tableBuchungenAktuell = new TableAntraege(findControl(resID + ".aktuell"), m_coBuchungAktuell, this);
		m_tableBuchungenAbgeschlossen = new TableAntraege(findControl(resID + ".abgeschlossen"), m_coBuchungAbgeschlossen, this);
	}


	/**
	 * Daten für alle Tabellen laden und zuordnen
	 * 
	 * @throws Exception
	 */
	public void reloadTableData() throws Exception {
		
		// geplante Anträge laden
		loadAntraegeGeplant(false);
		m_tableBuchungenGeplant.setData(m_coBuchungGeplant);
		
		// aktuelle Anträge laden
		loadAntraegeAktuell(true);
		m_tableBuchungenAktuell.setData(m_coBuchungAktuell);
		
		// bereits abgeschlossene Anträge laden
		loadAntraegeAbgeschlossen();
		m_tableBuchungenAbgeschlossen.setData(m_coBuchungAbgeschlossen);
		
		refresh(reasonDataChanged, null);
	}


	/**
	 * geplante Anträge laden
	 * 
	 * @param zeitraum 
	 * @throws Exception
	 */
	protected abstract void loadAntraegeGeplant(boolean zeitraum) throws Exception;


	/**
	 *  aktuelle Anträge laden
	 * 
	 * @param zeitraum 
	 * @throws Exception
	 */
	protected abstract void loadAntraegeAktuell(boolean zeitraum) throws Exception;

	
	/**
	 * bereits abgeschlossene Anträge laden
	 * 
	 * @throws Exception
	 */
	protected abstract void loadAntraegeAbgeschlossen() throws Exception;

	
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
		
		m_formFreigabecenter.refreshAllTabs(false);
	}


	/**
	 * Caption und Key des Formulars anpassen an den Inhalt
	 * @return 
	 */
	@Override
	public void refreshCaption() {
		m_formFreigabecenter.refreshCaption(this);
	}


	public CoBuchung getCoBuchung() {
		return m_coBuchungGeplant;
	}


	public CoBuchung getCoBuchungUrlaub() {
		return m_coBuchungAktuell;
	}


	/**
	 * Anzahl Anträge für die Caption bestimmen
	 * @return 
	 */
	public int getAnzAntraegeAktiviert() {
		int anzahl;
		Date datum;
		
		if (m_coBuchungGeplant == null)
		{
			return 0;
		}

		// Buchungen durchlaufen und Datum prüfen
		anzahl = 0;
		datum = Format.getDateVerschobenMonate(Format.getDate0Uhr(new Date()), 2);
		if (m_coBuchungGeplant.moveFirst())
		{
			do
			{
				if (m_coBuchungGeplant.getDatum().before(datum))
				{
					++anzahl;
				}
			} while (m_coBuchungGeplant.moveNext());
		}
		
		return anzahl;
	}


	@Override
	public void refresh(int reason, Object element){
		super.refresh(reason, element);
		
		refreshBtAntraegeGeplant();
		refreshBtAntraegeAktuell();
		refreshBtAntraegeAbgeschlossen();
		
		// Datumseingabe ist immer möglich
		m_tfDatumVon.refresh(reasonEnabled, null);
		m_tfDatumBis.refresh(reasonEnabled, null);
		
		// aktualisieren ist immer möglich
		m_btAktualisieren.refresh(reasonEnabled, null);
	}
	

	/**
	 * Buttons für Anträge einer Tabelle aktivieren/deaktivieren
	 */
	public void refreshBtAntraege(TableAntraege table) {
		if (table.equals(m_tableBuchungenGeplant))
		{
			refreshBtAntraegeGeplant();
		}
		else if (table.equals(m_tableBuchungenAktuell))
		{
			refreshBtAntraegeAktuell();
		}
		else if (table.equals(m_tableBuchungenAbgeschlossen))
		{
			refreshBtAntraegeAbgeschlossen();
		}
	}
	

	/**
	 * Buttons für geplante Anträge aktivieren/deaktivieren
	 */
	private void refreshBtAntraegeGeplant() {

		m_btBuchungGeplantLoeschen.refresh(reasonDisabled, null);
		m_btBuchungGeplantAendern.refresh(reasonDisabled, null);
		m_btBuchungGeplantBeantragen.refresh(reasonDisabled, null);

		// wenn keine Buchung ausgewählt ist, alle Buttons deaktivieren
		if (!m_coBuchungGeplant.moveTo(m_tableBuchungenGeplant.getSelectedBookmark()))
		{
			return;
		}

		m_btBuchungGeplantLoeschen.refresh(reasonEnabled, null);
		m_btBuchungGeplantAendern.refresh(reasonEnabled, null);
		m_btBuchungGeplantBeantragen.refresh(reasonEnabled, null);

	}


	/**
	 * Buttons für aktuelle Anträge aktivieren/deaktivieren
	 */
	protected void refreshBtAntraegeAktuell() {
		try 
		{
			m_btBuchungAktuellAendern.refresh(reasonDisabled, null);
			m_btBuchungAktuellLoeschen.refresh(reasonDisabled, null);

			// wenn keine Buchung ausgewählt ist, alle Buttons deaktivieren
			if (!m_coBuchungAktuell.moveTo(m_tableBuchungenAktuell.getSelectedBookmark()))
			{
				return;
			}
			
			// nur abgelehnte Anträge können bearbeitet werden
			if (!m_coBuchungAktuell.isAbgelehnt())
			{
				return;
			}
			
			m_btBuchungAktuellAendern.refresh(reasonEnabled, null);
			m_btBuchungAktuellLoeschen.refresh(reasonEnabled, null);
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}


	/**
	 * Buttons für abgeschlossene Anträge aktivieren/deaktivieren
	 */
	private void refreshBtAntraegeAbgeschlossen() {
		try 
		{
			Date datum, datumHeute;
			
			m_btBuchungAbgeschlossenAendern.refresh(reasonDisabled, null);
			m_btBuchungAbgeschlossenLoeschen.refresh(reasonDisabled, null);

			// wenn keine Buchung ausgewählt ist, alle Buttons deaktivieren
			if (!m_coBuchungAbgeschlossen.moveTo(m_tableBuchungenAbgeschlossen.getSelectedBookmark()))
			{
				return;
			}
			
			// ungültige oder nicht genehmigte Anträge können nicht bearbeitet werden
			if (m_coBuchungAbgeschlossen.isStatusUngueltig() || !m_coBuchungAbgeschlossen.isGenehmigt())
			{
				return;
			}
			
			// keine Anträge in der Vergangenheit bearbeiten
			datumHeute = Format.getDate0Uhr(new Date());
			datum = m_coBuchungAbgeschlossen.getDatumBis();
			if ((datum != null && datum.before(datumHeute)) || m_coBuchungAbgeschlossen.getDatum().before(datumHeute))
			{
				return;
			}

			m_btBuchungAbgeschlossenAendern.refresh(reasonEnabled, null);
			m_btBuchungAbgeschlossenLoeschen.refresh(reasonEnabled, null);
		} 
		catch (Exception e)
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
