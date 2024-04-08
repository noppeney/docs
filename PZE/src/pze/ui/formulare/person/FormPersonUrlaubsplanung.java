package pze.ui.formulare.person;

import java.util.Date;
import java.util.GregorianCalendar;

import framework.business.interfaces.fields.IField;
import framework.business.interfaces.session.ISession;
import framework.ui.controls.TextControl;
import framework.ui.interfaces.controls.IButtonControl;
import framework.ui.interfaces.controls.IControl;
import framework.ui.interfaces.controls.ITabFolder;
import framework.ui.interfaces.controls.ITabItem;
import framework.ui.interfaces.selection.ISelectionListener;
import pze.business.Format;
import pze.business.Messages;
import pze.business.Profile;
import pze.business.UserInformation;
import pze.business.export.urlaub.ExportAntragFaListener;
import pze.business.export.urlaub.ExportAntragListener;
import pze.business.export.urlaub.ExportAntragSonderurlaubListener;
import pze.business.export.urlaub.ExportAntragUrlaubListener;
import pze.business.objects.personen.CoBuchung;
import pze.business.objects.personen.CoKontowert;
import pze.business.objects.personen.CoPerson;
import pze.business.objects.reftables.CoGrundSonderurlaub;
import pze.business.objects.reftables.buchungen.CoBuchungsart;
import pze.ui.controls.SortedTableControl;
import pze.ui.formulare.UniFormWithSaveLogic;
import pze.ui.formulare.freigabecenter.FormFreigabecenterUrlaub;


/**
 * Formular der Rechte einer Person zur Verwaltung von Personen bestimmter Gruppen
 * 
 * @author Lisiecki
 *
 */
public class FormPersonUrlaubsplanung extends UniFormWithSaveLogic {
	public static final String RESID = "form.person.urlaubsplanung";

	private SortedTableControl m_table;
	
	private CoPerson m_coPerson;
	private CoBuchung m_coBuchung;
			
	private IButtonControl m_btLoeschen;
	private IButtonControl m_btAendern;
	private IButtonControl m_btBeantragen;
	private IButtonControl m_btAntrag;
	
	private IButtonControl m_btUrlaubsplanungAktuell;
	private IButtonControl m_btUrlaubsplanungNaechstesJahr;

	private IButtonControl m_btAktualisieren;

	private TextControl m_tfResturlaub;
	private TextControl m_tfResturlaubGeplant;

	
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
			name = "Urlaubsplanung";

			FormPersonUrlaubsplanung m_formAuswertung = new FormPersonUrlaubsplanung(editFolder, null, null);
			item = editFolder.add(name, key, m_formAuswertung, true);
			item.setBitmap(m_formAuswertung.getCoUrlaub().getNavigationBitmap());
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
	public FormPersonUrlaubsplanung(Object parent, CoPerson coPerson, UniFormWithSaveLogic formPerson) throws Exception {
		super(parent, RESID, true);

		m_coPerson = coPerson;
		
		initFormular();
		initTable();
		
		setData(coPerson);

		loadData();

		refresh(reasonDisabled, null);
	}

	
	@Override
	public void activate() {
		try 
		{
			super.activate();
			
			// Daten neu laden
			clickedAktualisieren();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	private void initTable() throws Exception {
		m_table = new SortedTableControl(findControl("spread.person.urlaubsplanung")){

			@Override
			public void tableSelected(IControl arg0, Object arg1){
				// ggf. Buttons aktivieren/deaktivieren
				refreshButtons();
			}

			@Override
			public void tableDefaultSelected(IControl arg0, Object arg1) throws Exception {
				FormPerson formPerson;

				formPerson = FormPerson.open(getSession(), null, (m_coBuchung).getPersonID());
				if (formPerson != null)
				{
					formPerson.showZeiterfassung(m_coBuchung.getDatum());
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

		m_tfResturlaub = (TextControl) findControl(getResID() + ".resturlaub");
		m_tfResturlaubGeplant = (TextControl) findControl(getResID() + ".resturlaubgeplant");

		m_btLoeschen = (IButtonControl) findControl(getResID() + ".loeschen");
		if (m_btLoeschen != null)
		{
			m_btLoeschen.setSelectionListener(new ISelectionListener() {

				@Override
				public void selected(IControl control, Object params) {
					try 
					{
						clickedLoeschen();
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

		m_btAendern = (IButtonControl) findControl(getResID() + ".aendern");
		if (m_btAendern != null)
		{
			m_btAendern.setSelectionListener(new ISelectionListener() {

				@Override
				public void selected(IControl control, Object params) {
					try 
					{
						clickedAendern();
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

		m_btBeantragen = (IButtonControl) findControl(getResID() + ".beantragen");
		if (m_btBeantragen != null)
		{
			m_btBeantragen.setSelectionListener(new ISelectionListener() {

				@Override
				public void selected(IControl control, Object params) {
					try 
					{
						clickedBeantragen();
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

		m_btUrlaubsplanungAktuell = (IButtonControl) findControl(getResID() + ".aktuellesjahr");
		if (m_btUrlaubsplanungAktuell != null)
		{
			m_btUrlaubsplanungAktuell.setSelectionListener(new ISelectionListener() {

				@Override
				public void selected(IControl control, Object params) {
					try 
					{
						clickedUrlaubsplanung(0);
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

		m_btUrlaubsplanungNaechstesJahr = (IButtonControl) findControl(getResID() + ".naechstesjahr");
		if (m_btUrlaubsplanungNaechstesJahr != null)
		{
			m_btUrlaubsplanungNaechstesJahr.setSelectionListener(new ISelectionListener() {

				@Override
				public void selected(IControl control, Object params) {
					try 
					{
						clickedUrlaubsplanung(1);
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
	 * Status ungültig für alles Urlaubstage der aktuellen Auswahl setzen
	 * 
	 * @throws Exception
	 */
	private void clickedLoeschen() throws Exception {
		FormFreigabecenterUrlaub.clickedLoeschen(m_coBuchung, m_table);
		clickedAktualisieren();
	}


	/**
	 * Datum der Urlaubsbuchung ändern
	 * 
	 * @throws Exception
	 */
	private void clickedAendern() throws Exception {
		DialogBuchungAendern.showDialogWithBuchung(m_coBuchung);
		clickedAktualisieren();
	}

	
	/**
	 * Status OK für alles Urlaubstage der aktuellen Auswahl setzen
	 * @return 
	 * 
	 * @throws Exception
	 */
	private void clickedBeantragen() throws Exception {
		FormFreigabecenterUrlaub.clickedBeantragen(m_coBuchung, m_table);
		clickedAktualisieren();
	}


	/**
	 * Antrag erstellen
	 * 
	 * @throws Exception
	 */
	private void clickedAntrag() throws Exception {
		int buchungsartID;
		ExportAntragListener exportAntragListener;
		
		// prüfen, ob ein Eintrag ausgewählt ist
		if (!m_coBuchung.moveTo(m_table.getSelectedBookmark()))
		{
			return;
		}

		// Vertreter eingeben
		if (!DialogVertreter.showDialog(m_coBuchung.getPersonID(), m_coBuchung.getDatum(), m_coBuchung.getDatumBis(), true))
		{
			return;
		}

		// Urlaub, Sonderurlaub, FA unterscheiden
		buchungsartID = m_coBuchung.getBuchungsartID();
		exportAntragListener = null;
		switch (buchungsartID)
		{
		case CoBuchungsart.ID_URLAUB:
			int personID, resturlaub, anzahlUrlaubBeantragt;
			Date datum;
			GregorianCalendar gregDatum;
			CoKontowert coKontowert;
			
			coKontowert = new CoKontowert();

			// Personendaten
			personID = m_coBuchung.getPersonID();
			
			// Datum des Urlaubs
			datum = m_coBuchung.getDatum();
			gregDatum = Format.getGregorianCalendar(datum);

			// Resturlaub aktuellen Jahres (Jahr des Urlaubs) setzen
			gregDatum.set(GregorianCalendar.MONTH, GregorianCalendar.DECEMBER);
			gregDatum.set(GregorianCalendar.DAY_OF_MONTH, 31);
			coKontowert.load(personID, Format.getDateValue(gregDatum));
			// wenn der Eintrag nicht existiert, lade den letzten
			if (coKontowert.getRowCount() == 0)
			{
				coKontowert.loadLastEintrag(personID);
			}
			resturlaub = coKontowert.getResturlaub();
			
			// Urlaubstage dieses Antrags
			anzahlUrlaubBeantragt = CoBuchung.getAnzahlGeplantenUrlaub(personID, datum, m_coBuchung.getDatumBis());

			// Anzahl Urlaubstage prüfen
			if (anzahlUrlaubBeantragt > resturlaub)
			{
				Messages.showErrorMessage("Die Anzahl der mit diesem Antrag geplanten Urlaubstage (" + anzahlUrlaubBeantragt 
						+ ") ist größer als die Anzahl der Resturlaubstage (" + resturlaub + ").");
				break;
			}
			
			exportAntragListener = new ExportAntragUrlaubListener(this);
			break;
		case CoBuchungsart.ID_SONDERURLAUB:
			int anzahlTageGeplant, anzahlTageZulaessig;
			
			// Grund für den Sonderurlaub abfragen
			if (!DialogGrundSonderurlaub.showDialog())
			{
				break;
			}
			
			// Anzahl der Sonderurlaubstage prüfen
			anzahlTageGeplant = CoBuchung.getAnzahlGeplantenSonderurlaub(m_coBuchung.getPersonID(), m_coBuchung.getDatum(), m_coBuchung.getDatumBis());
			anzahlTageZulaessig = CoGrundSonderurlaub.getInstance().getAnzahlTage(DialogGrundSonderurlaub.getGrundID());
			if (anzahlTageGeplant > anzahlTageZulaessig)
			{
				Messages.showErrorMessage("Die Anzahl der geplanten Sonderurlaubstage (" + anzahlTageGeplant 
						+ ") ist größer als die Anzahl der für diesen Grund zulässigen Sonderurlaubstage (" + anzahlTageZulaessig + ").");
				break;
			}
			
			// Antrag erstellen
			exportAntragListener = new ExportAntragSonderurlaubListener(this);
			break;
		case CoBuchungsart.ID_FA:
			
			// Zeitkonto prüfen
			if (!FormFreigabecenterUrlaub.checkZeitkontoFa(m_coBuchung))
			{
				return;
			}
			
			exportAntragListener = new ExportAntragFaListener(this);
			break;
		}

		if (exportAntragListener != null)
		{
			exportAntragListener.activate(null);
		}
	}

	
	/**
	 * PDF-Urlaubsplanung ausgeben
	 * 
	 * @param diffJahre Differenz in Jahren, wenn nicht für das aktuelle Jahr. 1 => nächstes Jahr
	 */
	private void clickedUrlaubsplanung(int diffJahre) {
		FormFreigabecenterUrlaub.clickedUrlaubsplanung(getCoPerson().getID(), diffJahre);
	}


	/**
	 * Einstellungen speichern und Daten neu laden
	 * 
	 * @throws Exception
	 */
	private void clickedAktualisieren() throws Exception {
		// Tabelle neu laden
		loadData();
	}


	/**
	 * Daten laden und der Tabelle zuweisen
	 * 
	 * @throws Exception
	 */
	protected void loadData() throws Exception {

		m_coBuchung = new CoBuchung();

		if (m_coPerson != null)
		{
			m_coBuchung.loadUrlaubsuebersicht(m_coPerson.getID());
			
			loadResturlaub();
		}
		
		m_coBuchung.sortByDatum(false);
		m_table.setData(m_coBuchung);
		
		refresh(reasonDataChanged, null);
	}


	/**
	 * Resturlaub laden und anzeigen 
	 * 
	 * @throws Exception
	 */
	private void loadResturlaub() throws Exception {
		int personID, resturlaub;
		GregorianCalendar gregDatum;
		CoKontowert coKontowert;

		personID = m_coPerson.getID();
		gregDatum = Format.getGregorianCalendar(null);
		gregDatum.set(GregorianCalendar.MONTH, GregorianCalendar.DECEMBER);
		gregDatum.set(GregorianCalendar.DAY_OF_MONTH, 31);
		
		coKontowert = new CoKontowert();
		coKontowert.load(personID, Format.getDateValue(gregDatum));
		resturlaub = coKontowert.getResturlaub();

		m_tfResturlaub.getField().setValue(resturlaub);
		m_tfResturlaubGeplant.getField().setValue(CoBuchung.getAnzahlGeplantenUrlaub(personID, new Date(), Format.getDateValue(gregDatum)));
	}


	public CoBuchung getCoUrlaub() {
		return m_coBuchung;
	}


	public CoPerson getCoPerson(){
		return m_coPerson;
	}


	@Override
	public void refresh(int reason, Object element) {
		super.refresh(reason, element);
		
		// Urlaubsplanung ist immer möglich
		m_btUrlaubsplanungAktuell.refresh(reasonEnabled, null);
		m_btUrlaubsplanungNaechstesJahr.refresh(reasonEnabled, null);
		
		// aktualisieren ist immer möglich
		m_btAktualisieren.refresh(reasonEnabled, null);

		// Tabelle ist immer deaktiviert
		m_table.refresh(reasonDisabled, null);

		// Buttons je nach Berechtigung
		refreshButtons();
	}


	private void refreshButtons() {
		boolean isPersonalverwaltung, isSelbst;
		boolean isVorlaeufig, isGeloescht, isGeplant, isAbgelehnt;
		UserInformation userInformation;

		
		// Buttons sind je nach Berechtigung und Buchungsstatus aktiviert
		try 
		{
			m_btLoeschen.refresh(reasonDisabled, null);
			m_btAendern.refresh(reasonDisabled, null);
			m_btBeantragen.refresh(reasonDisabled, null);
			m_btAntrag.refresh(reasonDisabled, null);

			// wenn keine Buchung ausgewählt ist, alle Buttons deaktivieren
			if (!m_coBuchung.moveTo(m_table.getSelectedBookmark()))
			{
				return;
			}

			userInformation = UserInformation.getInstance();
			isSelbst = UserInformation.isPerson(m_coPerson.getID());
			isPersonalverwaltung = userInformation.isPersonalverwaltung() && !isSelbst; // Personalabteilung und Buchung nicht für sich selbst
			
			isVorlaeufig = m_coBuchung.isVorlaeufig();
			isGeloescht = m_coBuchung.isUngueltig();
			isGeplant = m_coBuchung.isGeplant();
			isAbgelehnt = m_coBuchung.isAbgelehnt();
			
			// Buchung beantragen: vorläufige für sich selbst oder Personalverwaltung wenn nicht schon gelöscht
			if ((isSelbst && isVorlaeufig && (isGeplant || isAbgelehnt)) || (isPersonalverwaltung && !isGeloescht))
			{
				m_btBeantragen.refresh(reasonEnabled, null);
			}
			
			// Buchung löschen: vorläufige und genehmigte für sich selbst oder Personalverwaltung wenn nicht schon gelöscht
//			if ((isSelbst && ((isVorlaeufig && isGeplant) || (isGenehmigt)))
			if ( ((isSelbst && (!Format.getDate0Uhr(m_coBuchung.getDatumBis()).before(new Date())))
					|| (isPersonalverwaltung))
					&& !isGeloescht)
			{
				// Buchungen mit Zeiten können aktuell nicht geändert werden
				// TODO Buchung mit Uhrzeit ändern noch nicht, da ggf. die Gehen-Buchung oder eine andere angepast werden muss
				if (m_coBuchung.getUhrzeitAsInt() == 0 && m_coBuchung.getUhrzeitBisAsInt() == 0)
				{
					m_btAendern.refresh(reasonEnabled, null);
				}
				m_btLoeschen.refresh(reasonEnabled, null);
			}


			// Antrag drucken: Personalverwaltung für vorläufige, falls etwas mit den digitalen Freigabe nicht funktioniert
			if (isPersonalverwaltung && isVorlaeufig)
			{
				m_btAntrag.refresh(reasonEnabled, null);
			}
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
		return "person.urlaub" + id;
	}
	
	
	@Override
	public String getKey() {
		return getKey(getID());
	}


	public String getDefaultExportName() {
		String stringValue;
		Date date;
		
		stringValue = m_coPerson.getKuerzel();
		
		// Datum
		date = m_coBuchung.getDatum();
		stringValue += "_" + Format.getReverseUnterstrichString(date);
				
		date = m_coBuchung.getDatumBis();
		if (date != null)
		{
			stringValue += "_bis_" + Format.getReverseUnterstrichString(date);
		}

		return stringValue;
	}


	public String getProfilePathKey() {
		return Profile.KEY_AUSWERTUNG_URLAUBSPLANUNG;
	}
	

}
