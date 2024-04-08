package pze.ui.formulare.auswertung;

import framework.business.interfaces.session.ISession;
import framework.business.interfaces.tables.IColumnDescription;
import framework.business.interfaces.tables.IHeaderDescription;
import framework.ui.controls.ComboControl;
import framework.ui.interfaces.controls.IButtonControl;
import framework.ui.interfaces.controls.IControl;
import framework.ui.interfaces.controls.ITabFolder;
import framework.ui.interfaces.controls.ITabItem;
import framework.ui.interfaces.selection.ISelectionListener;
import pze.business.Profile;
import pze.business.export.ExportVerletzerlisteListener;
import pze.business.objects.CoVerletzerliste;
import pze.business.objects.CoVerletzerlisteAuswertung;
import pze.business.objects.auswertung.CoAuswertung;
import pze.business.objects.auswertung.CoAuswertungVerletzerliste;
import pze.ui.controls.SortedTableControl;
import pze.ui.formulare.person.FormPerson;

/**
 * Formular für eine Übersicht über die Personen
 * 
 * @author Lisiecki
 *
 */
public class FormAuswertungVerletzerliste extends FormAuswertung {
	
	public static String RESID = "form.auswertung.verletzerliste";

	private ComboControl m_comboStatus;
	
//	private BooleanControl m_HinweisGleitzeitkonto;
//	private BooleanControl m_KeineBuchung;
	
	private IButtonControl m_btAlleStati;


	
	
	/**
	 * Konstruktor
	 * @param parent		visueller Parent
	 * @throws Exception
	 */
	private FormAuswertungVerletzerliste(Object parent) throws Exception {
		super(parent, RESID);
	}


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

		if(item == null)
		{
			name = "Verletzerliste";

			m_formAuswertung = new FormAuswertungVerletzerliste(editFolder);
			item = editFolder.add(name, key, m_formAuswertung, true);
			item.setBitmap(CoVerletzerliste.getInstance().getNavigationBitmap());
		}

		editFolder.setSelection(key);
	}


	/**
	 * Formularfelder und Listener initialisieren
	 * @throws Exception 
	 */
	@Override
	protected void initFormular() throws Exception {
		super.initFormular();
		
		m_comboStatus = (ComboControl) findControl(getResID() + ".statusid");
		
//		m_HinweisGleitzeitkonto = (BooleanControl) findControl(getResID() + ".hinweisgleitzeitkontoausblenden");
//		m_HinweisGleitzeitkonto = (BooleanControl) findControl(getResID() + ".keinebuchungausblenden");

		m_btAlleStati = (IButtonControl) findControl(getResID() + ".allestati");
		m_btAlleStati.setSelectionListener(new ISelectionListener() {
			
			@Override
			public void selected(IControl control, Object params) {
				m_comboStatus.getField().setValue(null);
				
				refresh(reasonDataChanged, null);
			}
			
			@Override
			public void defaultSelected(IControl control, Object params) {
			}
		});
	}


	@Override
	protected void initTable() throws Exception {

		m_table = new SortedTableControl(findControl("spread.auswertung.verletzerliste")){

			@Override
			public void tableDefaultSelected(IControl arg0, Object arg1) throws Exception {
//				IField field;
				FormPerson formPerson;

//				field = m_table.getSelectedCell().getField();
				
				// wenn der Status geklickt wird, nicht zu der Meldung wechseln
//				if (!FormAuswertungVerletzerliste.this.getData().isEditing() 
//						|| (!field.equals(m_co.getFieldStatusID()) && !field.equals(m_co.getFieldBemerkung()))
//						)
				{				
					formPerson = FormPerson.open(getSession(), null, getCoVerletzerlisteAuswertung().getPersonID());
					if (formPerson != null)
					{
						formPerson.showZeiterfassung(getCoVerletzerlisteAuswertung().getDatum());
					}
				}
			}
			
			
//			@Override
//			protected void endEditing(Object bookmark, IField fld)  throws Exception {
//				valueChanged();
//			}

		};
		
	}


	@Override
	protected void updateHeaderDescription() {
		IColumnDescription columnDescription;
		IHeaderDescription headerDescription;
		CoVerletzerliste coVerletzerliste;
		
		headerDescription = m_table.getHeaderDescription();
		try 
		{
			coVerletzerliste = new CoVerletzerliste();
			
			// Spalte ID soll nicht ausgegeben werden
			headerDescription.remove(coVerletzerliste.getFieldID().getFieldDescription().getResID());

			// Spaltenbreite der Meldung ändern
			headerDescription.getColumnDescription(coVerletzerliste.getFieldMeldungID().getFieldDescription().getResID()).setWidth(300);

			// Spaltenbreite der Person ändern
			headerDescription.getColumnDescription(coVerletzerliste.getFieldPersonID().getFieldDescription().getResID()).setWidth(130);

			// Spaltenbreite der Bemerkung ändern
			columnDescription = headerDescription.getColumnDescription(coVerletzerliste.getFieldBemerkung().getFieldDescription().getResID());
			if (columnDescription != null)
			{
				columnDescription.setWidth(300);
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	
		m_table.setHeaderDescription(headerDescription);
	}


	@Override
	protected void loadCo() throws Exception {
		m_co = new CoVerletzerlisteAuswertung();
		getCoVerletzerlisteAuswertung().load((CoAuswertungVerletzerliste) m_coAuswertung);
		addExcelExportListener(m_co, m_table, getDefaultExportName(), getProfilePathKey());
	}

	
	@Override
	protected CoAuswertung createCoAuswertung() throws Exception {
		return new CoAuswertungVerletzerliste();
	}


	/**
	 * @param id Primärschlüssel
	 * @return Item Key im Tabfolder
	 */
	public static String getKey(int id) {
		return "verletzerliste." + id;
	}
	
	
	@Override
	public String getKey() {
		return getKey(0);
	}
	

	@Override
	public void activate() {
		addPdfExportListener(new ExportVerletzerlisteListener(this));
		addExcelExportListener(m_co, m_table, getDefaultExportName(), getProfilePathKey());
//		super.activate();
	}
	

	/**
	 * Wenn das Formular bereits existiert muss es geschlossen und neu geöffnet werden, da sich die Anzahl der Spalten verändern kann
	 * 
	 * @see pze.ui.formulare.auswertung.FormAuswertung#loadData()
	 */
	protected void loadData() throws Exception {
		ISession session;
		String key;
		ITabFolder editFolder;

		if (getData() instanceof CoVerletzerlisteAuswertung)
		{
			session = getSession();
			key = getKey(0);

			editFolder = session.getMainFrame().getEditFolder();
			editFolder.remove(key);

			FormAuswertungVerletzerliste.open(session);
		}
		else
		{
			super.loadData();
		}
	}


//	/**
//	 * Bei geänderten Werten speichern, wer wann die Änderungen gemacht hat
//	 * 
//	 * @throws Exception
//	 */
//	private void valueChanged() throws Exception {
//
//		// Speichern von wem die Änderungen gemacht wurden
//		getCoVerletzerlisteAuswertung().valueChanged();
//		
//		refresh(reasonDataChanged, null);
//	}


	private CoVerletzerlisteAuswertung getCoVerletzerlisteAuswertung() {
		return (CoVerletzerlisteAuswertung) m_co;
	}



//	/**
//	 * Das co zum zum Speichern aufbereitet werden, da die Anzeige ggf. nur einen Teil abbildet
//	 */
//	@Override
//	public boolean validateAndSave() throws Exception {
//		boolean returnValue;
//		CoVerletzerliste coVerletzerliste;
//		CoVerletzerlisteAuswertung coVerletzerlisteAuswertung;
//
//		try 
//		{
//			coVerletzerlisteAuswertung = getCoVerletzerlisteAuswertung();
//			if (coVerletzerlisteAuswertung == null)
//			{
//				return false;
//			}
//
//			coVerletzerliste = coVerletzerlisteAuswertung.getCoVerletzerliste();
//			if (!coVerletzerlisteAuswertung.moveFirst())
//			{
//				return false;
//			}
//
//			// Daten auf das richtige CO übertragen
//			coVerletzerliste.begin();
//			do
//			{
//				if (coVerletzerlisteAuswertung.getCurrentRow().getRowState() == IBusinessObject.statusChanged)
//				{
//					coVerletzerliste.moveToID(coVerletzerlisteAuswertung.getID());
//					coVerletzerliste.setStatusID(coVerletzerlisteAuswertung.getStatusID());
//					coVerletzerliste.setBemerkung(coVerletzerlisteAuswertung.getBemerkung());
//					coVerletzerliste.setGeaendertVonID(coVerletzerlisteAuswertung.getGeaendertVonID());
//					coVerletzerliste.setGeaendertAm(Format.getStringMitUhrzeit(Format.getGregorianCalendar(null)));
//				}
//			} while (coVerletzerlisteAuswertung.moveNext());
//
//
//			// richtiges co setzen und speichern
//			setData(coVerletzerliste);
//			returnValue = super.validateAndSave();
//			setData(coVerletzerlisteAuswertung);
//
//			if (returnValue)
//			{
//				coVerletzerlisteAuswertung.commit();
//			}
//
//			return returnValue;
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//			return false;
//		}
//	}

	
	@Override
	public String getDefaultExportName() {
		return "Verletzerliste" + getCoAuswertung().getStringEinschraenkungDatumPerson();
	}


	@Override
	public String getProfilePathKey() {
		return Profile.KEY_VERLETZERLISTE;
	}
	

}
