package pze.ui.formulare.auswertung;

import org.eclipse.swt.widgets.Display;

import framework.business.interfaces.CaptionType;
import framework.business.interfaces.session.ISession;
import framework.business.interfaces.tables.IColumnDescription;
import framework.business.interfaces.tables.IHeaderDescription;
import framework.ui.controls.BooleanControl;
import framework.ui.interfaces.controls.IControl;
import framework.ui.interfaces.controls.ITabFolder;
import framework.ui.interfaces.selection.IValueChangeListener;
import pze.business.objects.auswertung.CoAnwesenheitAbteilung;
import pze.business.objects.auswertung.CoAuswertung;
import pze.business.objects.auswertung.CoAuswertungAnwesenheit;
import pze.business.objects.personen.CoPerson;

/**
 * Formular für die Anwesenheitsübersicht
 * 
 * @author Lisiecki
 *
 */
public class FormAnwesenheitAbteilung extends FormAuswertung {
	
	public static String RESID = "form.auswertung.anwesenheit.abteilung";
	private static boolean isActive;

	private Thread m_threadAutoRefresh;

	private BooleanControl m_verwaltungAusgeben;
	private BooleanControl m_techBerechnungenAusgeben;
	private BooleanControl m_nukBerechnungenAusgeben;
	private BooleanControl m_rueckbauplanungAusgeben;
	private BooleanControl m_entsorgungsplanungAusgeben;
	private BooleanControl m_bauplanungAusgeben;
	private BooleanControl m_klAusgeben;
	private BooleanControl m_gfAusgeben;

	private CoPerson m_coPersonOfaWti;

	private TableAnwesenheitAbteilung m_table;

	
	
	/**
	 * Konstruktor
	 * @param parent		visueller Parent
	 * @throws Exception
	 */
	public FormAnwesenheitAbteilung(Object parent) throws Exception {
		super(parent, RESID);
		
		formatTable();
		
		initLegende();

		// automatisches Aktualisieren der Buchungen
		startAutoRefresh();
	}

	
	protected void initFormular() throws Exception {
		super.initFormular();

		// Daten beim Auswählen der Abteilungen aktualisieren
		IValueChangeListener valueChangeListener;
		valueChangeListener = new IValueChangeListener() {
			
			@Override
			public void valueChanged(IControl control, Object originalValue, Object lastValue, Object currentValue) throws Exception {
				clickedAktualisieren();
			}
		};
		
		m_verwaltungAusgeben = (BooleanControl) findControl(getResID() + ".verwaltungausgeben");
		m_techBerechnungenAusgeben = (BooleanControl) findControl(getResID() + ".techberechnungenausgeben");
		m_nukBerechnungenAusgeben = (BooleanControl) findControl(getResID() + ".nukberechnungenausgeben");
		m_rueckbauplanungAusgeben = (BooleanControl) findControl(getResID() + ".rueckbauplanungausgeben");
		m_entsorgungsplanungAusgeben = (BooleanControl) findControl(getResID() + ".entsorgungsplanungausgeben");
		m_bauplanungAusgeben = (BooleanControl) findControl(getResID() + ".bauplanungausgeben");
		m_klAusgeben = (BooleanControl) findControl(getResID() + ".klausgeben");
		m_gfAusgeben = (BooleanControl) findControl(getResID() + ".gfausgeben");

		m_verwaltungAusgeben.setValueChangeListener(valueChangeListener);
		m_techBerechnungenAusgeben.setValueChangeListener(valueChangeListener);
		m_nukBerechnungenAusgeben.setValueChangeListener(valueChangeListener);
		m_rueckbauplanungAusgeben.setValueChangeListener(valueChangeListener);
		m_entsorgungsplanungAusgeben.setValueChangeListener(valueChangeListener);
		m_bauplanungAusgeben.setValueChangeListener(valueChangeListener);
		m_klAusgeben.setValueChangeListener(valueChangeListener);
		m_gfAusgeben.setValueChangeListener(valueChangeListener);
	}
	
	
	protected void initTable() throws Exception {
		m_table = new TableAnwesenheitAbteilung(findControl("spread.auswertung.anwesenheit.abteilung"));
	}


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
			columnDescription.setWidth(FormAnwesenheit.SPALTENBREITE);
		}
		
		// Änderungen anwenden
		m_table.setHeaderDescription(headerDescription);
	}
	

	private void initLegende() throws Exception {
		new TableAnwesenheitLegende(findControl("spread.auswertung.anwesenheit.abteilung.legende"));
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
		return "anwesenheit.abteilungen." + id;
	}
	
	
	@Override
	public String getKey() {
		return getKey(getID());
	}
	

	@Override
	protected void loadCo() throws Exception {
		m_co = new CoAnwesenheitAbteilung(getCoAuswertungAnwesenheit());
		
		m_coPersonOfaWti = new CoPerson();
		m_coPersonOfaWti.loadOrtsflexArbeitenForAnwesenheit();

		m_table.setData(m_co, m_coPersonOfaWti);
	}

	
	@Override
	protected CoAuswertung createCoAuswertung() throws Exception {
		return new CoAuswertungAnwesenheit();
	}

	
	private CoAuswertungAnwesenheit getCoAuswertungAnwesenheit(){
		return (CoAuswertungAnwesenheit) m_coAuswertung;
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

						Thread.sleep(FormAnwesenheit.UPDATE_INTERVALL);
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

					loadCo();

					refresh(reasonDataChanged, null);
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
		
		m_table.refresh(reasonEnabled, null);
	}
	

	/**
	 * Wenn das Formular bereits existiert muss es geschlossen und neu geöffnet werden, da sich die Anzahl der Spalten verändern kann
	 * 
	 * @see pze.ui.formulare.auswertung.FormAuswertung#loadData()
	 */
	@Override
	protected void loadData() throws Exception {
		ISession session;
		String key;
		ITabFolder editFolder;

		if (getData() instanceof CoAnwesenheitAbteilung)
		{
			session = getSession();
			key = FormAnwesenheit.getKey(0);

			editFolder = session.getMainFrame().getEditFolder();
			editFolder.remove(key);

			FormAnwesenheit.open(session);
			FormAnwesenheit.getSubFolder().setSelection(FormAnwesenheitAbteilung.RESID);
		}
		else
		{
			super.loadData();
		}
	}


	@Override
	public String getDefaultExportName() {
		return null;
	}


	@Override
	public String getProfilePathKey() {
		return null;
	}
	

}
