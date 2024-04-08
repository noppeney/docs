package pze.ui.formulare.auswertung;

import framework.business.fields.HeaderDescription;
import framework.ui.controls.ComboControl;
import framework.ui.interfaces.controls.IButtonControl;
import framework.ui.interfaces.controls.IControl;
import framework.ui.interfaces.selection.ISelectionListener;
import framework.ui.interfaces.selection.IValueChangeListener;
import pze.business.UserInformation;
import pze.business.objects.AbstractCacheObject;
import pze.business.objects.auswertung.CoAuswertung;
import pze.business.objects.personen.CoPerson;
import pze.business.objects.reftables.CoPersonenliste;
import pze.business.objects.reftables.personen.CoAbteilung;
import pze.ui.controls.SortedTableControl;
import pze.ui.formulare.UniFormWithSaveLogic;

/**
 * Allgemeine Formular-Klasse
 * 
 * @author Lisiecki
 *
 */
public abstract class FormAuswertung extends UniFormWithSaveLogic {
	

	protected static FormAuswertung m_formAuswertung;
	
	protected CoAuswertung m_coAuswertung;
	protected AbstractCacheObject m_co;
	protected AbstractCacheObject m_co2;

//	private TextControl m_tfDatumVon;
//	private TextControl m_tfDatumBis;

	protected ComboControl m_comboAbteilung;
	protected ComboControl m_comboPerson;
	protected ComboControl m_comboPersonenliste;
	private ComboControl m_comboPosition;
	private ComboControl m_comboStatusAktivInaktiv;
	
	protected IButtonControl m_btAllePersonen;
	private IButtonControl m_btAllePositionen;
	private IButtonControl m_btAlleStatusAktivInaktiv;
	private IButtonControl m_btAktualisieren;

	protected SortedTableControl m_table;
	protected SortedTableControl m_table2;
	

	
	
	/**
	 * Konstruktor
	 * 
	 * @param parent		visueller Parent
	 * @param resID
	 * @throws Exception
	 */
	protected FormAuswertung(Object parent, String resID) throws Exception {
		super(parent, resID);
		
		
		initFormular();
		initTable();
		initTable2();
	
		// letzte Einstellungen der Auswertung laden
		m_coAuswertung = createCoAuswertung();
		m_coAuswertung.loadByUserID(UserInformation.getUserID());
		if (!m_coAuswertung.isEditing())
		{
			m_coAuswertung.begin();
		}

		// Daten der Auswertung setzen damit die Formularfelder gefüllt werden, werden dann mit denen der Tabelle überschrieben
		setData(m_coAuswertung);

		// Combo-Items anpassen, nachdem alle Daten geladen wurden
		if (!UserInformation.getInstance().isPersonalansicht())
		{
			loadItemsComboAbteilung();
			loadItemsComboPerson(false);
			loadItemsComboPersonenliste();
		}
		else
		{
			loadItemsComboPerson(true);
		}
		
		// Daten laden
		loadData();
		// TODO seltsamer Fehler (momentan OK) Auswertungstabellen aktiviert (wenn das nicht gemacht wird ist Tabelle bei Ausw. Kontowerte aktiviert)
		// zu Beginn sollte co nicht im Edit-Modus sein, kommt aber z. B. bei Auswertung Kontodaten vor, weil das co manuell verändert wird
		// ggf. hat es etwas damit zu tun, wenn getData() das CoAuswertung ist
		// (in CoAuswertungProjekt.createNew() habe ich ein save/begin gepackt, 15.01.19)
		if (getData().isEditing())
		{
			getData().commit(); 
		}

		// Spaltenbreite anpassen
		updateHeaderDescription();
		
		refresh(reasonDataChanged, null);
	}


	/**
	 * Formularfelder und Listener initialisieren
	 * @throws Exception 
	 */
	protected void initFormular() throws Exception {
//		m_tfDatumVon = (TextControl) findControl(getResID() + ".datum");
//		m_tfDatumBis = (TextControl) findControl(getResID() + ".datumbis");


		m_comboAbteilung = (ComboControl) findControl(getResID() + ".abteilungid");
		if (m_comboAbteilung != null)
		{
			m_comboAbteilung.setValueChangeListener(new IValueChangeListener() {

				@Override
				public void valueChanged(IControl control, Object originalValue, Object lastValue, Object currentValue) throws Exception {
					resetCombosEinschraenkungPerson(control, currentValue);
				}
			});
		}


		m_comboPerson = (ComboControl) findControl(getResID() + ".personid");
		if (m_comboPerson != null)
		{
			m_comboPerson.setValueChangeListener(new IValueChangeListener() {

				@Override
				public void valueChanged(IControl control, Object originalValue, Object lastValue, Object currentValue) throws Exception {
					resetCombosEinschraenkungPerson(control, currentValue);
				}
			});
		}


		m_comboPersonenliste = (ComboControl) findControl(getResID() + ".personenlisteid");
		if (m_comboPersonenliste != null)
		{
			m_comboPersonenliste.setValueChangeListener(new IValueChangeListener() {

				@Override
				public void valueChanged(IControl control, Object originalValue, Object lastValue, Object currentValue) throws Exception {
					resetCombosEinschraenkungPerson(control, currentValue);
				}
			});
		}


		m_btAllePersonen = (IButtonControl) findControl(getResID() + ".allepersonen");
		if (m_btAllePersonen != null)
		{
			m_btAllePersonen.setSelectionListener(new ISelectionListener() {

				@Override
				public void selected(IControl control, Object params) {
					resetCombosEinschraenkungPerson();
				}

				@Override
				public void defaultSelected(IControl control, Object params) {
				}
			});
		}

		m_comboPosition = (ComboControl) findControl(getResID() + ".positionid");

		m_btAllePositionen = (IButtonControl) findControl(getResID() + ".allepositionen");
		if (m_btAllePositionen != null)
		{
			m_btAllePositionen.setSelectionListener(new ISelectionListener() {

				@Override
				public void selected(IControl control, Object params) {
					resetCombosEinschraenkungPosition();
				}

				@Override
				public void defaultSelected(IControl control, Object params) {
				}
			});
		}

		m_comboStatusAktivInaktiv = (ComboControl) findControl(getResID() + ".statusaktivinaktivid");

		m_btAlleStatusAktivInaktiv = (IButtonControl) findControl(getResID() + ".allestatusaktivinaktiv");
		if (m_btAlleStatusAktivInaktiv != null)
		{
			m_btAlleStatusAktivInaktiv.setSelectionListener(new ISelectionListener() {

				@Override
				public void selected(IControl control, Object params) {
					m_comboStatusAktivInaktiv.getField().setValue(null);

					refresh(reasonDataChanged, null);
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

						//					(new ExportVerletzerlisteListener(FormVerletzerliste.this)).activate(null);
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
	 * Werte der Combos für die Einschränkung der Personen auf NULL setzen und den übergebenen Wert setzen
	 * 
	 * @param currentValue 
	 * @param control 
	 */
	private void resetCombosEinschraenkungPerson(IControl control, Object currentValue) {
		resetCombosEinschraenkungPerson();
		
		((ComboControl) control).getField().setValue(currentValue);
		
		refresh(reasonDataChanged, null);
	}


	/**
	 * Werte der Combos für die Einschränkung der Personen auf NULL setzen
	 */
	public void resetCombosEinschraenkungPerson() {
		if (m_comboAbteilung != null)
		{
			m_comboAbteilung.getField().setValue(null);
		}
		
		if (m_comboPerson != null)
		{
			m_comboPerson.getField().setValue(null);
		}
		
		if (m_comboPersonenliste != null)
		{
			m_comboPersonenliste.getField().setValue(null);
		}
		
		refresh(reasonDataChanged, null);
	}


	/**
	 * Werte der Combos für die Einschränkung der Position auf NULL setzen
	 */
	public void resetCombosEinschraenkungPosition() {
		m_comboPosition.getField().setValue(null);

		refresh(reasonDataChanged, null);
	}

	
	/**
	 * Einstellungen speichern und Daten neu laden
	 * 
	 * @throws Exception
	 */
	protected void clickedAktualisieren() throws Exception {
		// Angaben zur Auswertung speichern
		m_coAuswertung.save();
		m_coAuswertung.begin();

		// Tabelle neu laden
		loadData();
	}

	
	protected abstract void initTable() throws Exception;

	
	protected void initTable2() throws Exception {
	}


	/**
	 * Daten laden und der Tabelle zuweisen
	 * 
	 * @throws Exception
	 */
	protected void loadData() throws Exception {
		loadCo();
		loadCo2();
		
		
		// falls das CO aufbereitet wurde, setzte es auf nicht modified, da keine Daten gespeichert werden sollen
		if (m_co != null)
		{
			m_co.setModified(false);
		}

		// Daten dem Formular zuweisen
		setData(m_co);
		if (m_table != null)
		{
			m_table.setData(m_co);
		}
		
		if (m_table2 != null)
		{
			m_table2.setData(m_co2);
		}
	}


	/**
	 * HeaderDescription manuell anpassen,
	 * Wenn nur Daten geladen werden und die Oberfläche nicht aufgebaut wird, ist dies notwendig.
	 * 
	 * @throws Exception
	 */
	public void updateHeaderDescriptionManuell() throws Exception {
		if (m_table != null)
		{
			if (m_co != null)
			{
				m_table.setHeaderDescription(new HeaderDescription(m_co.getFields()));
			}
		}
		
		if (m_table2 != null)
		{
			if (m_co2 != null)
			{
				m_table2.setHeaderDescription(new HeaderDescription(m_co2.getFields()));
			}
		}
	}


	/**
	 * Co laden
	 * @throws Exception 
	 */
	protected abstract void loadCo() throws Exception;


	/**
	 * Co laden
	 * @throws Exception 
	 */
	protected void loadCo2() throws Exception {
	}


	/**
	 * CoAuswertung erstellen
	 * @return 
	 * @throws Exception 
	 */
	protected abstract CoAuswertung createCoAuswertung() throws Exception;

	
	public CoAuswertung getCoAuswertung(){
		return m_coAuswertung;
	}
	

	public SortedTableControl getTable() {
		return m_table;
	}


	public SortedTableControl getTable2() {
		return m_table2;
	}


	/**
	 * Spaltenbreiten, Beschriftungen etc. bearbeiten
	 */
	protected void updateHeaderDescription() {
	}


	/**
	 * Filterfunktionen sind immer aktiv
	 * 
	 * @see pze.ui.formulare.UniFormWithSaveLogic#refresh(int, java.lang.Object)
	 */
	@Override
	public void refresh(int reason, Object element){
		super.refresh(reason, element);
		
		// alle Felder aktivieren
		super.refresh(reasonEnabled, null);
		
		// Tabelle je nach Modus aktivieren, z. B. zum Freigeben der Verletzerliste
		// TODO seltsamer Fehler (momentan OK) Auswertungstabellen aktiviert
		if (!getData().isEditing())
		{
			if (m_table != null)
			{
				m_table.refresh(reasonDisabled, null);
			}
			
			if (m_table2 != null)
			{
				m_table2.refresh(reasonDisabled, null);
			}
		}
		
		// der Status darf nur von ausgewählten Benutzergruppen geändert werden
		if (!UserInformation.getInstance().isPersonalverwaltung() && m_comboStatusAktivInaktiv != null)
		{
			m_comboStatusAktivInaktiv.refresh(reasonDisabled, null);
			m_btAlleStatusAktivInaktiv.refresh(reasonDisabled, null);
		}
	}
	

	/**
	 * Aktualisieren der Items der Abteilungs-Combo
	 * 
	 * @throws Exception
	 */
	protected void loadItemsComboAbteilung() throws Exception {
		CoAbteilung coAbteilung;

		if (m_comboAbteilung == null)
		{
			return;
		}

		coAbteilung = new CoAbteilung();
		coAbteilung.loadByCurrentUser();
		
		refreshItems(m_comboAbteilung, coAbteilung, m_comboAbteilung.getField());
	}


	/**
	 * Aktualisieren der Items der Person-Combo
	 * 
	 * @throws Exception
	 */
	private void loadItemsComboPerson(boolean loadAll) throws Exception {
		CoPerson coPerson;
		
		if (m_comboPerson == null)
		{
			return;
		}

		coPerson = new CoPerson();
		if (loadAll)
		{
			coPerson.loadItems(null);
		}
		else
		{
			coPerson.loadItemsOfCurrentUser();
		}
		
		refreshItems(m_comboPerson, coPerson, m_comboPerson.getField());
	}


	/**
	 * Aktualisieren der Items der Personliste-Combo
	 * 
	 * @throws Exception
	 */
	private void loadItemsComboPersonenliste() throws Exception {
		CoPersonenliste coPersonenliste;
		
		if (m_comboPersonenliste == null)
		{
			return;
		}

		coPersonenliste = CoPersonenliste.getInstance();
		coPersonenliste.loadItemsOfCurrentUser();
		
		refreshItems(m_comboPersonenliste, coPersonenliste, m_comboPersonenliste.getField());
	}


	/**
	 * Default-Name für eine Auswertungsdatei
	 * 
	 * @return
	 * @throws Exception 
	 */
	public abstract String getDefaultExportName() throws Exception;
	

	/**
	 * Profile-Key für den Export-Pfad
	 * 
	 * @return
	 */
	public abstract String getProfilePathKey();
	

}
