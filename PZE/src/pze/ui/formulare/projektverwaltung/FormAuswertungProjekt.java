package pze.ui.formulare.projektverwaltung;

import java.util.Date;

import framework.business.interfaces.CaptionType;
import framework.business.interfaces.tables.IColumnDescription;
import framework.business.interfaces.tables.IHeaderDescription;
import framework.business.statusinfo.StatusInfo;
import framework.ui.controls.TextControl;
import framework.ui.interfaces.controls.IButtonControl;
import framework.ui.interfaces.controls.IControl;
import framework.ui.interfaces.selection.ISelectionListener;
import pze.business.Messages;
import pze.business.Profile;
import pze.business.UserInformation;
import pze.business.export.ExportStundenuebersichtProjekteCreator;
import pze.business.export.ExportStundenuebersichtProjekteListener;
import pze.business.export.ExportTaetigkeitsnachweisProjektListener;
import pze.business.objects.auswertung.CoAuswertung;
import pze.business.objects.auswertung.CoAuswertungProjekt;
import pze.business.objects.personen.monatseinsatzblatt.CoMonatseinsatzblatt;
import pze.business.objects.projektverwaltung.CoAbruf;
import pze.business.objects.projektverwaltung.CoProjekt;
import pze.business.objects.projektverwaltung.CoProjektStundenuebersicht;
import pze.business.objects.reftables.CoStundenart;
import pze.business.objects.reftables.projektverwaltung.CoKunde;
import pze.ui.controls.SortedTableControl;
import pze.ui.formulare.auswertung.FormAuswertung;
import pze.ui.formulare.person.FormPerson;


/**
 * Formular für die Auswertung der Projektstunden
 * 
 * @author Lisiecki
 */
public class FormAuswertungProjekt extends FormAuswertung {
	
	public static String RESID = "form.auswertung.projekte";

	private FormProjekt m_formProjekt;

	private TextControl m_tfBestellwert;
	private TextControl m_tfSollstunden;
	private TextControl m_tfStartwert;
	private TextControl m_tfVerbleibendeStunden;
	private TextControl m_tfVerbrauchBestellwert;
	private TextControl m_tfVerbrauchSollstunden;
	private TextControl m_tfDatumExport;

	private IButtonControl m_btExportTaetigkeitsnachweis;
	private IButtonControl m_btExportAbrufe;
	private IButtonControl m_btJahresweise;
	
	private boolean m_isKGG;

	
	
	/**
	 * Konstruktor
	 * 
	 * @param parent		visueller Parent
	 * @param formProjekt 
	 * @throws Exception
	 */
	public FormAuswertungProjekt(Object parent, FormProjekt formProjekt) throws Exception {
		super(parent, RESID);
		
		m_formProjekt = formProjekt;
		m_isKGG = getCoProjekt().isKGG();
		
		// Formularfelder initialisieren
		m_tfBestellwert = (TextControl) findControl(RESID + ".bestellwert");
		m_tfSollstunden = (TextControl) findControl(RESID + ".sollstunden");
		m_tfStartwert = (TextControl) findControl(RESID + ".startwert");
		m_tfVerbleibendeStunden = (TextControl) findControl(RESID + ".wertzeitverbleibend");
		m_tfVerbrauchBestellwert = (TextControl) findControl(RESID + ".verbrauchbestellwert");
		m_tfVerbrauchSollstunden = (TextControl) findControl(RESID + ".verbrauchsollstunden");
		m_tfDatumExport = (TextControl) findControl(getResID() + ".datumexport");

		m_btExportTaetigkeitsnachweis = (IButtonControl) findControl("form.auswertung.projekte.exporttaetigkeitsnachweis");
		m_btExportTaetigkeitsnachweis.setSelectionListener(new ISelectionListener() {
			
			@Override
			public void selected(IControl control, Object params) {
				exportTaetigkeitsnachweis();		
			}


			@Override
			public void defaultSelected(IControl control, Object params) {
			}
		});

		// Projektdaten anzeigen
		setData(m_formProjekt.getData());
		
		// Daten erst nach dem Zuordnen des Auftrags laden, da sonst die ID des Auftrags fehlt
		loadData();
		
		// Spaltenbreite anpassen
		updateHeaderDescription();
		
		// TODO muss hier nochmal gemacht werden, keine Ahnung warum nicht auch bei anderen Klassen (z. B. Form AuswertungKontowerte)
		// in FormAuswertung wird es comitted
		if (!m_coAuswertung.isEditing())
		{
			m_coAuswertung.begin();
		}

		refresh(reasonDataChanged, null);
	}


	@Override
	protected void initFormular() throws Exception {
		super.initFormular();

		// zusätzlichen Button jahresweise... initialisieren
		m_btJahresweise = (IButtonControl) findControl(getResID() + ".jahresweise");
		if (m_btJahresweise != null)
		{
			m_btJahresweise.setSelectionListener(new ISelectionListener() {

				@Override
				public void selected(IControl control, Object params) {
					try 
					{
						openHProjekt();

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
		
		m_btExportAbrufe = (IButtonControl) findControl("form.auswertung.projekte.exportabrufe");
		m_btExportAbrufe.setSelectionListener(new ISelectionListener() {
			
			@Override
			public void selected(IControl control, Object params) {
				exportAbrufe();		
			}


			@Override
			public void defaultSelected(IControl control, Object params) {
			}
		});

	}
	

	/**
	 * Person beim Doppelklick öffnen
	 * 
	 * @see pze.ui.formulare.auswertung.FormAuswertung#initTable()
	 */
	@Override
	protected void initTable() throws Exception {
		m_table = new SortedTableControl(findControl("spread.auswertung.projekte")){
		
			/**
			 *  Person öffnen
			 *  
			 * @see pze.ui.controls.SortedTableControl#tableDefaultSelected(framework.ui.interfaces.controls.IControl, java.lang.Object)
			 */
			@Override
			public void tableDefaultSelected(IControl arg0, Object arg1) throws Exception {
				int personID;
				FormPerson formPerson;

				personID = m_co.getPersonID();
				formPerson = FormPerson.open(getSession(), null, personID);
				if (formPerson != null)
				{
					formPerson.showMonatseinsatzblatt();
				}
			}

		};

	}


	/**
	 * Person beim Doppelklick öffnen
	 * 
	 * @see pze.ui.formulare.auswertung.FormAuswertung#initTable()
	 */
	@Override
	protected void initTable2() throws Exception {
		m_table2 = new SortedTableControl(findControl("spread.auswertung.projekte.reisezeit")){
		
			/**
			 *  Person öffnen
			 *  
			 * @see pze.ui.controls.SortedTableControl#tableDefaultSelected(framework.ui.interfaces.controls.IControl, java.lang.Object)
			 */
			@Override
			public void tableDefaultSelected(IControl arg0, Object arg1) throws Exception {
				int personID;
				FormPerson formPerson;

				personID = m_co2.getPersonID();
				formPerson = FormPerson.open(getSession(), null, personID);
				if (formPerson != null)
				{
					formPerson.showMonatseinsatzblatt();
				}
			}

		};

	}


	@Override
	protected void loadCo() throws Exception {
		int id;
		String stundenartID;
		
		if (m_formProjekt != null)
		{
			id = m_formProjekt.getCo().getID();
			stundenartID = "" + (m_isKGG ? CoStundenart.STATUSID_ERSTELLUNG
					: (CoStundenart.STATUSID_INGENIEURSTUNDEN + ", " + CoStundenart.STATUSID_ERSTELLUNG + ", " + CoStundenart.STATUSID_QS));
			m_co = new CoProjektStundenuebersicht(getCoAuswertungProjekt());
			
			if (m_formProjekt instanceof FormAuftrag)
			{
				((CoProjektStundenuebersicht) m_co).loadAuftrag(id, stundenartID);
			}
			else if (m_formProjekt instanceof FormAbruf)
			{
				((CoProjektStundenuebersicht) m_co).loadAbruf(id, stundenartID);
			}
		}
	}


	@Override
	protected void loadCo2() throws Exception {
		int id; 
		String stundenartID;
		
		if (m_formProjekt != null)
		{
			id = m_formProjekt.getCo().getID();
			stundenartID = "" + (m_isKGG ? CoStundenart.STATUSID_QS : CoStundenart.STATUSID_REISEZEIT);
			m_co2 = new CoProjektStundenuebersicht(getCoAuswertungProjekt());
			
			if (m_formProjekt instanceof FormAuftrag)
			{
				((CoProjektStundenuebersicht) m_co2).loadAuftrag(id, stundenartID);
			}
			else if (m_formProjekt instanceof FormAbruf)
			{
				((CoProjektStundenuebersicht) m_co2).loadAbruf(id, stundenartID);
			}
		}
	}

	
	@Override
	protected CoAuswertung createCoAuswertung() throws Exception {
		return new CoAuswertungProjekt();
	}


	@Override
	protected void updateHeaderDescription() {
		updateHeaderDescription(m_table, (m_isKGG ? "Erstellung" : "Projektstunden"));
		updateHeaderDescription(m_table2, (m_isKGG ? "QS" : "Reisezeit"));
	}


	protected void updateHeaderDescription(SortedTableControl table, String caption) {
		int iCol, anzCol;
		IHeaderDescription headerDescription;
		IColumnDescription columnDescription;
		
		headerDescription = table.getHeaderDescription();
		if (headerDescription == null || headerDescription.getColumnCount() == 0)
		{
			return;
		}
		
		// Spaltenbreite der Person ändern
		columnDescription = headerDescription.getColumnDescription(0);
		columnDescription.setWidth(130);
		columnDescription.setCaption(caption);

		// Spaltenbreiten Zeiträume
		anzCol = getCo().getColumnCount();
		for (iCol=1; iCol<anzCol; ++iCol)
		{
			columnDescription = headerDescription.getColumnDescription(iCol);
			if (columnDescription == null)
			{
				continue;
			}
			
			columnDescription.setAlignment(CaptionType.ALIGNCENTER);
			columnDescription.setWidth(75);
		}

		table.setHeaderDescription(headerDescription);
	}

	
	/**
	 * Tätigkeitsnachweise für das Projekt exportieren
	 */
	private void exportTaetigkeitsnachweis() {
		try
		{
			Date datum = getDatumExport();
			
			if (datum == null)
			{
				Messages.showErrorMessage("Fehlende Eingabe", "Bitte Datum für den Export eingeben.");
				return;
			}
			
			(new ExportTaetigkeitsnachweisProjektListener(this)).activate(null);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}


	/**
	 * Auswertung für alle Abrufe des Auftrags exportieren
	 * 
	 */
	private void exportAbrufe() {
		int abrufID, anzAbrufe;
		boolean letztenMonatPruefen;
		String html, htmlAbruf;
		Date datum;
		CoAbruf coAbruf, coAbrufeAuftrag;
		FormAbruf formAbruf;
		FormAuswertungProjekt formAuswertungProjekt;
		ExportStundenuebersichtProjekteListener listener;
		ExportStundenuebersichtProjekteCreator creator;

		try
		{
			letztenMonatPruefen = ((CoAuswertungProjekt) m_coAuswertung).isLetztenMonatPruefenAktiv();
//			System.out.println(getCo().getClass());
//			System.out.println(getCoProjekt().getClass());
//			System.out.println(m_formProjekt.getCo().getClass());
//			System.out.println();
			
			// alle Abrufe des Auftrags laden
			coAbrufeAuftrag = new CoAbruf();
			coAbrufeAuftrag.loadByAuftragID(getCoProjekt().getID(), true);
			anzAbrufe = coAbrufeAuftrag.getRowCount();
			System.out.println("Anzahl Abrufe:" + anzAbrufe);

			// Statusinfo 
			StatusInfo.openStatus(0);
			StatusInfo.beginTask("Auswertung der Abrufe wird exportiert...", anzAbrufe);
			

			// Abrufe durchlaufen
			if (coAbrufeAuftrag.moveFirst())
			{
				coAbruf = new CoAbruf();
				coAbruf.loadByID(coAbrufeAuftrag.getID());
				
				// Formulare mit den daten zum export initialisieren
				formAbruf = new FormAbruf(getParent(), coAbruf);
				formAuswertungProjekt = new FormAuswertungProjekt(null, formAbruf);
				listener = new ExportStundenuebersichtProjekteListener(formAuswertungProjekt);

				// HTML-Kopf schreiben
				creator = new ExportStundenuebersichtProjekteCreator();
				html = creator.createHtmlBegin(formAuswertungProjekt);

				do
				{
					abrufID = coAbrufeAuftrag.getID();
					coAbruf.loadByID(abrufID);
					StatusInfo.tick();
					
					// ggf. prüfen ob im letzten Monat Stunden angefallen sind
					datum = m_coAuswertung.getDatumBis();
					if (letztenMonatPruefen && datum != null && CoMonatseinsatzblatt.getWertZeitMonat(abrufID, datum) == 0)
					{
						// ggf. Monat überspringen
						continue;
					}

					// Formulare mit den Daten des nächsten Abrufs initialisieren
					// neue Formulare erstellen führt zum Programmabruch, zu viele Handles
					formAbruf.init(coAbruf);
					formAuswertungProjekt.setData(formAbruf.getData());
					formAuswertungProjekt.loadData();

					// da die Tabellen nicht normal angelegt werden, muss die HeaderDescription manuell angepasst werden
					formAuswertungProjekt.updateHeaderDescriptionManuell();

					// HTML-Seite für den Abruf erzeugen
					creator = new ExportStundenuebersichtProjekteCreator();
					htmlAbruf = creator.createHtmlSeite(formAuswertungProjekt);
					if (htmlAbruf != null)
					{
						html += htmlAbruf;
					}

					System.gc();

				} while (coAbrufeAuftrag.moveNext());
				
				// HTML-Ende hinzufügen
				html += creator.createHtmlEnde();
				
				// Datei öffnen
				listener.saveAndOpen(html);
//				((FormAuftrag) m_formProjekt).testAsuwertungprojektAlle(getKey());
			}
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			Messages.showErrorMessage("Unbekannter Fehler beim Export. Bitte wenden Sie sich an den Administrator.");
		}
		finally
		{
			System.gc();
			
			// StatusInfo schließen
			StatusInfo.done();
			StatusInfo.closeStatus();
		}

	}


	/**
	 * Formular für die Auswertung des Abrufs erzeugen
	 * 
	 * @param coAbruf
	 * @return
	 * @throws Exception
	 */
//	protected FormAuswertungProjekt createFormAuswertungAbruf(CoAbruf coAbruf) throws Exception {
//		FormAbruf formAbruf;
//		
//		formAbruf = new FormAbruf(m_node, getParent(), coAbruf);
//		return new FormAuswertungProjekt(null, formAbruf);
//	}


	/**
	 * Formular für H-Projekt mit jahresweise archivierten Daten laden
	 * 
	 * @throws Exception
	 */
	protected void openHProjekt() throws Exception {
		int idBudgetJahresweise;
		
		// prüfen, ob eine ID mit jahresweise archivierten Daten existiert 
		idBudgetJahresweise = ((CoProjekt) m_formProjekt.getCo()).getIDBudgetJahresweise();
		if (idBudgetJahresweise == 0)
		{
			return;
		}
		
		// Formular für Auftrag oder Abruf öffnen
		if (m_formProjekt instanceof FormAuftrag)
		{
			FormAuftrag.open(getSession(), idBudgetJahresweise);
		}
		else if (m_formProjekt instanceof FormAbruf)
		{
			FormAbruf.open(getSession(), idBudgetJahresweise);
		}
	}
	

	/**
	 * @param id Primärschlüssel
	 * @return Item Key im Tabfolder
	 */
	public static String getKey(int id) {
		return "auswertung.projekt.stundenuebersicht." + id;
	}
	
	
	@Override
	public String getKey() {
		return getKey(0);
	}
	

	@Override
	public void activate() {
		addPdfExportListener(new ExportStundenuebersichtProjekteListener(this));
	}
	

	/**
	 * Wenn das Formular bereits existiert muss da Tab geschlossen und neu geöffnet werden, da sich die Anzahl der Spalten verändern kann
	 * 
	 * @see pze.ui.formulare.auswertung.FormAuswertung#loadData()
	 */
	protected void loadData() throws Exception {

		if (getData() instanceof CoProjektStundenuebersicht)
		{
			m_formProjekt.reloadFormAuswertung();
		}
		else
		{
			super.loadData();
		}
	}

	
	private CoAuswertungProjekt getCoAuswertungProjekt(){
		return (CoAuswertungProjekt) m_coAuswertung;
	}
	
	
	/**
	 * Co des Projektes mit den Auftrags-/Abrufdaten
	 * 
	 * @return 
	 */
	public CoProjekt getCoProjekt(){
		return (CoProjekt) m_formProjekt.getCo();
	}
	
	
	/**
	 * Das für den Export ausgewählte Datum
	 * 
	 * @return
	 */
	public Date getDatumExport() {
		Date datum;
		datum = m_tfDatumExport.getField().getDateValue();
		return datum;
	}


	/**
	 * Filterfunktionen sind immer aktiv
	 * 
	 * @see pze.ui.formulare.UniFormWithSaveLogic#refresh(int, java.lang.Object)
	 */
	@Override
	public void refresh(int reason, Object element){
		super.refresh(reason, element);
		
		// Tabelle und Formularfelder mit Projektdaten deaktivieren
		if (m_tfSollstunden != null)
		{
			m_tfBestellwert.refresh(reasonDisabled, null);
			m_tfSollstunden.refresh(reasonDisabled, null);
			m_tfStartwert.refresh(reasonDisabled, null);
			m_tfVerbleibendeStunden.refresh(reasonDisabled, null);
			m_tfVerbrauchSollstunden.refresh(reasonDisabled, null);
			m_tfVerbrauchBestellwert.refresh(reasonDisabled, null);
			m_table.refresh(reasonDisabled, null);
			m_table2.refresh(reasonDisabled, null);
		}
		
		// Button zur jahresweisen Auswertung der H-Projekte nur für Buchhaltung ermöglichen, und wenn jahresweise Daten vorhanden sind
		if (m_formProjekt == null || ((CoProjekt) m_formProjekt.getCo()).getIDBudgetJahresweise() == 0 || !UserInformation.getInstance().isProjektverwaltung())
		{
			m_btJahresweise.refresh(reasonDisabled, null);
		}
		else
		{
			m_btJahresweise.refresh(reasonEnabled, null);
		}
		
		// Button zur Exportieren der Abrufe nur für Projektverwaltung
		if (m_formProjekt instanceof FormAuftrag && UserInformation.getInstance().isProjektverwaltung())
		{
			m_btExportAbrufe.refresh(reasonEnabled, null);
		}
		else
		{
			m_btExportAbrufe.refresh(reasonDisabled, null);
		}
	}
	

	@Override
	public String getDefaultExportName() throws Exception {
		CoProjekt coProjekt;
		CoKunde coKunde;
		
		coProjekt = getCoProjekt();
		
		coKunde = CoKunde.getInstance();
		coKunde.moveToID(coProjekt.getKundeID());
		
		return "Stundenuebersicht_" + coKunde.getKuerzel() + "_" + coProjekt.getProjektNr() + getCoAuswertung().getStringEinschraenkungDatumPerson();
	}


	@Override
	public String getProfilePathKey() {
		return Profile.KEY_AUSWERTUNG_PROJEKTE;
	}
	

}
