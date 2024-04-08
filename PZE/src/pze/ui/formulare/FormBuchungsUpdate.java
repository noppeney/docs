package pze.ui.formulare;

import java.util.GregorianCalendar;

import org.eclipse.swt.widgets.Display;

import framework.Application;
import framework.business.interfaces.session.ISession;
import framework.business.resources.ResourceMapper;
import framework.ui.interfaces.controls.ITabFolder;
import framework.ui.interfaces.controls.ITabItem;
import pze.business.Format;
import pze.business.MessageCreater;
import pze.business.Messages;
import pze.business.datentransfer.ImportGelocDaten;
import pze.business.objects.geloc.CoInfoTextBuchungsupdate;

/**
 * Formular zur darstellung der Buchungs-Updates von der Geloc-DB zur PZE-DB
 * 
 * @author Lisiecki
 *
 */
public class FormBuchungsUpdate extends UniFormWithSaveLogic {

	/**
	 * Zeitintervall, dem jeweils neue Daten aus der Geloc-DB geladen werden sollen
	 */
	private static final int DB_UPDATE_INTERVALL = 20 * 1000;
	
	private static String RESID = "form.buchungsupdate";
	
	private static Thread m_thread;

	private static FormBuchungsUpdate m_BuchungsUpdateForm;

	private CoInfoTextBuchungsupdate m_coInfoText;
	
	
	/**
	 * Konstruktor, der das Update der Buchungen startet
	 * 
	 * @param parent		visueller Parent
	 * @throws Exception
	 */
	public FormBuchungsUpdate(Object parent) throws Exception {
		super(parent, RESID, false);
		
		m_coInfoText = new CoInfoTextBuchungsupdate();
		m_coInfoText.begin();
		m_coInfoText.add();
		m_coInfoText.setInfoText("Start des Updates...");
		
		setData(m_coInfoText);

		// Buchungsupdate starten
		importBuchungenFromGelocDB();
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
		CoInfoTextBuchungsupdate coInfoText;
		
		key = getKey(0);
		
		editFolder = session.getMainFrame().getEditFolder();
		item = editFolder.get(key);

		if(item == null)
		{
			coInfoText = new CoInfoTextBuchungsupdate();

			coInfoText.createNew();
			name = "Info über Buchungs-Update";

			m_BuchungsUpdateForm = new FormBuchungsUpdate(editFolder);
			item = editFolder.add(name, key, m_BuchungsUpdateForm, true);
			item.setBitmap(coInfoText.getNavigationBitmap());
			
			// Beim Start des Buchungsupdate Meldungen erstellen, falls PZE morgens neu gestartet wurde
			Thread.sleep(1000*60*5); // Toleranz um Buchungen zu laden
			MessageCreater.createMessages(Format.getGregorianCalendar(null));
		}

		editFolder.setSelection(key);
	}

	
	@Override
	public boolean canclose() {
//		String caption;
		
		// untergeordnete Formulare können nicht geschlossen werden
		if (isAdditionalForm)
		{
			return true;
		}

		try 
		{
			if (Messages.showYesNoMessage("Buchungs-Update beenden?", 
					"Das Update der Buchungen für die Projektzeiterfassung wird beendet.<br/>Wollen Sie wirklich beenden?"))
				//						+ (caption == null ? "" : "\"" + getCaption() + "\"") + " wirklich schließen?"))
			{
				m_thread.interrupt();
				return true;
			}
			else 
			{
				return false;
			}
		}
		catch(Exception e) 
		{
			String msg = ResourceMapper.getInstance().getErrorMessage(e);
			Messages.showErrorMessage(Application.getCaption(), msg);
		}
		
		return false;
	}

	
	/**
	 * @param id Primärschlüssel
	 * @return Item Key im Tabfolder
	 */
	public static String getKey(int id) {
		return "buchungsupdate." + id;
	}
	
	
	@Override
	public String getKey() {
		return "buchungsupdate";
	}

	
	/**
	 * Dieses Formular darf nicht bearbeitet werden. Die Daten werden importiert.
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
	private void importBuchungenFromGelocDB() throws Exception{

		m_thread = new Thread()
		{
			public void run() {

				try 
				{
					int anzNewBuchungen, counter;
					String infoText;

					counter = 0;

					while (true)
					{
						// Update
						anzNewBuchungen = ImportGelocDaten.updateBuchungen();
						++counter;

						// Info mir der Anzahl der neuen Buchungen
						infoText = "DB-Update " + counter + " (" + Format.getStringMitUhrzeit(new GregorianCalendar()) + "): "
								+ anzNewBuchungen + " neue Buchungen";
						setInfoText(infoText);

						Thread.sleep(DB_UPDATE_INTERVALL);
					} 
				}
				catch (InterruptedException e)
				{
				}
			}
		};
		
		m_thread.start();
	}


	/**
	 * Info-text über die Updates setzen
	 * 
	 * @param infoText
	 */
	protected void setInfoText(final String infoText) {

		Display.getDefault().syncExec(new Runnable() {

			@Override
			public void run() {

				try 
				{
					m_coInfoText.setInfoText(infoText + "\n" + m_coInfoText.getInfoText());
					setData(m_coInfoText);
					
					refresh(reasonDisabled, null);
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
	 * Gibt an, ob das Buchungsupdate in diesem programm läuft
	 * 
	 * @return
	 */
	public static boolean isRunning(){
		return m_thread != null && !m_thread.isInterrupted();
	}
}
