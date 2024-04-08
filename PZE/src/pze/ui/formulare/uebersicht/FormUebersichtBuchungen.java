package pze.ui.formulare.uebersicht;

import java.util.Date;

import org.eclipse.swt.widgets.Display;

import framework.business.interfaces.session.ISession;
import framework.ui.controls.TextControl;
import framework.ui.interfaces.controls.IButtonControl;
import framework.ui.interfaces.controls.IControl;
import framework.ui.interfaces.controls.ITabFolder;
import framework.ui.interfaces.controls.ITabItem;
import framework.ui.interfaces.selection.ISelectionListener;
import pze.business.Profile;
import pze.business.objects.personen.CoBuchung;
import pze.ui.controls.SortedTableControl;
import pze.ui.formulare.UniFormWithSaveLogic;
import pze.ui.formulare.person.DialogBuchung;

/**
 * Formular für Buchungen
 * 
 * @author Lisiecki
 *
 */
public class FormUebersichtBuchungen extends UniFormWithSaveLogic {
	
	/**
	 * Zeitintervall, dem jeweils neue Daten aus der DB geladen werden sollen
	 */
	private static final int DB_UPDATE_INTERVALL = 30 * 1000;

	public static String RESID = "form.buchungen";
	
	private Thread m_threadAutoRefresh;

	private static FormUebersichtBuchungen m_formBuchungen;
	
	private CoBuchung m_coBuchungen;

	private IButtonControl m_btAktualisieren;
	private TextControl m_tfDatum;
	private SortedTableControl m_tableBuchungen;

	
	
	/**
	 * Konstruktor
	 * @param parent		visueller Parent
	 * @throws Exception
	 */
	private FormUebersichtBuchungen(Object parent) throws Exception {
		super(parent, RESID);
		
		m_coBuchungen = new CoBuchung();
		m_coBuchungen.loadAllFromToday();
		setData(m_coBuchungen);

		m_tfDatum = (TextControl) findControl(getResID() + ".datum");

		initBtBuchungenLaden();
		initTableBuchungen();
		
		// automatisches Aktualisieren der Buchungen
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
			name = "aktuelle Buchungen";

			m_formBuchungen = new FormUebersichtBuchungen(editFolder);
			item = editFolder.add(name, key, m_formBuchungen, true);
			item.setBitmap("clock.go");
		}

		editFolder.setSelection(key);
	}

	
	private void initBtBuchungenLaden(){
		m_btAktualisieren = (IButtonControl) findControl(getResID() + ".laden");
		if (m_btAktualisieren != null)
		{
			m_btAktualisieren.setSelectionListener(new ISelectionListener() {

				@Override
				public void selected(IControl control, Object params) {
					try 
					{
						Date datum;
						
						datum = m_tfDatum.getField().getDateValue();
						if (datum == null)
						{
							return;
						}
						
						m_coBuchungen.load(null, m_tfDatum.getField().getDateValue(), 0, false);
						m_tableBuchungen.setData(m_coBuchungen);
						
						// zum neuen Datensatz scrollen
						m_tableBuchungen.showBookmark();
						
						refresh(reasonDataChanged, null);
						
						// Aktualisierung beenden
						onClose();
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

	
	private void initTableBuchungen() throws Exception {

		m_tableBuchungen = new SortedTableControl(findControl("spread.buchungen")){

			@Override
			public void tableDefaultSelected(IControl arg0, Object arg1) throws Exception {
				DialogBuchung.showDialogWithBuchung(m_coBuchungen.getID());
			}
		};
		
		m_tableBuchungen.setData(m_coBuchungen);
	}

	
	/**
	 * @param id Primärschlüssel
	 * @return Item Key im Tabfolder
	 */
	public static String getKey(int id) {
		return "buchungen." + id;
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
		return false;
	}

	
	/**
	 * Buchungen importieren
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

						Thread.sleep(DB_UPDATE_INTERVALL);
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
	 * Info-text über die Updates setzen
	 * 
	 * @param infoText
	 */
	private void refreshTableData() {

		Display.getDefault().syncExec(new Runnable() {

			@Override
			public void run() {

				try 
				{
					m_coBuchungen.loadAllFromToday();
					m_tableBuchungen.setData(m_coBuchungen);

					// zum neuen Datensatz scrollen
					m_tableBuchungen.showBookmark();
					
					refresh(reasonDataChanged, null);
				}
				catch (Exception e) 
				{
					e.printStackTrace();
				}		

			}
		});
	}
	

	@Override
	public void refresh(int reason, Object element){
		super.refresh(reason, element);
		
		// alle Felder aktivieren
		m_tfDatum.refresh(reasonEnabled, null);
		m_btAktualisieren.refresh(reasonEnabled, null);
	}
	

	/**
	 * Thread zum Aktuelisieren der Anzeige beenden
	 * 
	 * @see framework.ui.form.UniForm#onClose()
	 */
	public void onClose() {
		m_threadAutoRefresh.interrupt();
	}
	

	@Override
	public void activate() {
		addExcelExportListener(m_coBuchungen, m_tableBuchungen, "Uebersicht_Buchungen", Profile.KEY_ADMINISTRATION);
		super.activate();
	}
	

}
