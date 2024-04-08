package pze.ui.formulare.auswertung;

import org.eclipse.swt.widgets.Display;

import framework.business.interfaces.CaptionType;
import framework.business.interfaces.session.ISession;
import framework.business.interfaces.tables.IColumnDescription;
import framework.business.interfaces.tables.IHeaderDescription;
import framework.ui.interfaces.controls.ITabFolder;
import framework.ui.interfaces.controls.ITabItem;
import pze.business.objects.auswertung.CoAnwesenheitAlle;
import pze.business.objects.personen.CoPerson;
import pze.ui.formulare.UniFormWithSaveLogic;

/**
 * Formular für die Anwesenheitsübersicht
 * 
 * @author Lisiecki
 *
 */
public class FormAnwesenheit extends UniFormWithSaveLogic {
	
	/**
	 * Zeitintervall, dem jeweils neue Daten aus der DB geladen werden sollen
	 */
	public static final int UPDATE_INTERVALL = 60 * 1000;
	public static final int SPALTENBREITE = 175;

	public static String RESID = "form.auswertung.anwesenheit.alle";
	private static boolean isActive;

	
	private Thread m_threadAutoRefresh;

	private static ITabFolder m_subTabFolder;

	private static FormAnwesenheit m_formAnwesenheit;
	private static FormAnwesenheitAbteilung m_formAnwesenheitAbteilung;
	private static FormAnwesenheitUebersicht m_formAnwesenheitUebersicht;
	
	private CoAnwesenheitAlle m_coAnwesenheit;
	private CoPerson m_coPersonOfaWti;

	private TableAnwesenheit m_table;


//	private TextControl m_tfVorname;
//	private TextControl m_tfNachname;

	
	
	/**
	 * Konstruktor
	 * @param parent		visueller Parent
	 * @throws Exception
	 */
	private FormAnwesenheit(Object parent) throws Exception {
		super(parent, RESID);
		
		m_coAnwesenheit = new CoAnwesenheitAlle();
		
		m_coPersonOfaWti = new CoPerson();
		m_coPersonOfaWti.loadOrtsflexArbeitenForAnwesenheit();
		
		setData(m_coAnwesenheit);

		initTable();
		
		initLegende();
		
//		m_tfVorname = (TextControl) findControl(RESID + ".person.vorname");
//		m_tfNachname = (TextControl) findControl(RESID + ".person.nachname");
//		
//		// Pflichtfelder mit Dummy-Werten füllen, da sie sonst rot angezeigt werden
//		m_tfVorname.getField().setValue(" ");
//		m_tfNachname.getField().setValue(" ");
//		refresh(reasonDataChanged, null);
		
		// automatisches Aktualisieren der Buchungen
		isActive = true;
		startAutoRefresh();
	}


	/**
	 * Formular öffnen oder selektieren, wenn noch nicht vorhanden
	 * 
	 * @param session Session
	 * @param node Knoten in Navigation
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
			name = "Anwesenheitsübersicht";

			item = editFolder.add(name, key, null,true);
			item.setBitmap("color.swatch");
			m_subTabFolder = item.getSubFolder();
			editFolder.setSelection(key);	

			m_formAnwesenheit = new FormAnwesenheit(m_subTabFolder);
			m_subTabFolder.add("Alle", FormAnwesenheit.RESID, m_formAnwesenheit, false);

			m_formAnwesenheitAbteilung = new FormAnwesenheitAbteilung(m_subTabFolder);
			m_subTabFolder.add("Abteilungen", FormAnwesenheitAbteilung.RESID, m_formAnwesenheitAbteilung, false);			
			m_formAnwesenheit.addAdditionalForm(m_formAnwesenheitAbteilung);
			
			// Übersicht 
			m_formAnwesenheitUebersicht = new FormAnwesenheitUebersicht(m_subTabFolder);
			m_subTabFolder.add("Übersicht", FormAnwesenheitUebersicht.RESID, m_formAnwesenheitUebersicht, false);			
			m_formAnwesenheit.addAdditionalForm(m_formAnwesenheitUebersicht);

			m_subTabFolder.setActivateSubFolder(true);
			m_subTabFolder.setSelection(FormAnwesenheit.RESID);
		}

		editFolder.setSelection(key);
	}

	
	static int i=0;
	/**
	 * Tabelle erstellen
	 * 
	 * @throws Exception
	 */
	private void initTable() throws Exception {
		m_table = new TableAnwesenheit(findControl("spread.auswertung.anwesenheit.alle"));
			// TODO Interfaces für FormAuswertung(Abteilung) und CoAnwesenheit(Abteilung)

		m_table.setData(m_coAnwesenheit, m_coPersonOfaWti);

		formatTable();
	}


	/**
	 * Daten der ausgewählten Person anzeigen
	 */
//	protected void showPersonendaten() {
//		int personID;
//
//		personID = getSelectedPersonID();
//		if (personID == 0)
//		{
//			return;
//		}
//		
//		refreshPersonendaten(personID);
//	}


	/**
	 * Felder mit den Daten der übergebenen Person füllen
	 * 
	 * @param personID
	 */
//	protected void refreshPersonendaten(int personID) {
//		CoPerson coPerson;
//		try 
//		{
//			coPerson = new CoPerson();
//			coPerson.loadByID(personID);
//			
//			m_tfVorname.getField().setValue(coPerson.getVorname());
//			m_tfNachname.getField().setValue(coPerson.getNachname());
//			
//			m_tfVorname.refresh(reasonDataChanged, null);
//			m_tfNachname.refresh(reasonDataChanged, null);
//		} 
//		catch (Exception e) 
//		{
//		}
//	}
	

	/**
	 * Tabelle formatieren.<br>
	 * Spaltenbreite, Alignment...
	 * 
	 */
	private void formatTable() {
		int iCol, anzCols;
		IHeaderDescription headerDescription;
		IColumnDescription columnDescription;
		
		
		headerDescription = m_table.getHeaderDescription();
		anzCols = m_table.getHeaderDescription().getColumnCount();
		
		// alle Spalten mit AlignCenter
		for (iCol=0; iCol<anzCols; ++iCol)
		{
			columnDescription = headerDescription.getColumnDescription(iCol);
			
			columnDescription.setAlignment(CaptionType.ALIGNCENTER);
			columnDescription.setWidth(SPALTENBREITE);
		}
		
		// Änderungen anwenden
		m_table.setHeaderDescription(headerDescription);
	}
	

	private void initLegende() throws Exception {
		new TableAnwesenheitLegende(findControl("spread.auswertung.anwesenheit.alle.legende"));
	}


	@Override
	public void activate() {
		// activate wird für alle subForms aufgerufen, wenn der Reiter aktiviert wird, deshalb selection prüfen 
		if (RESID.equals(((ITabFolder)getParent()).getSelection()))
		{
			isActive = true;
			refreshTableData();
		}
	}
	
	
	@Override
	public void deactivate() {
		super.deactivate();
		isActive = false;
	}
	
	
	/**
	 * @param id Primärschlüssel
	 * @return Item Key im Tabfolder
	 */
	public static String getKey(int id) {
		return "anwesenheit.alle." + id;
	}
	
	
	@Override
	public String getKey() {
		return getKey(getID());
	}
	
	
	/**
	 * Das Formular darf nicht bearbeitet werden
	 * 
	 * (non-Javadoc)
	 * @see pze.ui.formulare.UniFormWithSaveLogic#mayEdit()
	 */
	@Override
	public boolean mayEdit() {
		return true;
	}


	/**
	 * Aktualisieren starten
	 * 
	 * @throws Exception
	 */
	private void startAutoRefresh() throws Exception{

		m_threadAutoRefresh = new Thread()
		{
			public void run() {

				try 
				{
					while (true)
					{
						// Update
						refreshTableData();
						
						Thread.sleep(UPDATE_INTERVALL);
					} 
				}
				catch (InterruptedException e)
				{
					// hier kommt man hin, wenn das Fenster geschlossen und der Thread beendet wird 
				}
			}
		};
		
		m_threadAutoRefresh.start();
	}


	/**
	 * Tabelle neu laden
	 * 
	 */
	private void refreshTableData() {

		Display.getDefault().syncExec(new Runnable() {

			@Override
			public void run() {

				try 
				{
					// nur aktualisieren, wenn das Tab aktiv ist
					if (!isActive)
					{
						return;
					}
					System.out.println("Daten laden");
					long a = System.currentTimeMillis();
				
					m_coAnwesenheit = new CoAnwesenheitAlle(); // TODO abgelehnte Buchungen nicht laden - wird das gerade gemacht?
					
					m_coPersonOfaWti = new CoPerson();
					m_coPersonOfaWti.loadOrtsflexArbeitenForAnwesenheit();

					m_table.setData(m_coAnwesenheit, m_coPersonOfaWti);
					System.out.println("Daten laden: " + (System.currentTimeMillis() - a));

					a = System.currentTimeMillis();
					refresh(reasonDataChanged, null);
					System.out.println("Daten laden refresh: " + (System.currentTimeMillis() - a));
				}
				catch (Exception e) 
				{
					e.printStackTrace();
				}		

			}
		});
	}
	
	
	/**
	 * Thread zum Aktuelisieren der Anzeige beenden
	 * 
	 * @see framework.ui.form.UniForm#onClose()
	 */
	public void onClose() {
		m_threadAutoRefresh.interrupt();
	}
	
	
	public void refresh(int reason, Object element) {
		super.refresh(reason, element);

		System.out.println("refresh");
		// Tabelle ist immer aktiviert, um Zellauswahl zuzulassen
		m_table.refresh(reasonEnabled, null);
		
		// Personenfelder sind deaktiviert
//		m_tfVorname.refresh(reasonDisabled, null);
//		m_tfNachname.refresh(reasonDisabled, null);
	}
	
	
	/**
	 * Subfolder, dem die beiden Tabs zugeordnet sind
	 * 
	 * @return
	 */
	public static ITabFolder getSubFolder(){
		return m_subTabFolder;
	}
	

}
