package pze.ui.formulare;

import java.util.Iterator;

import org.eclipse.swt.widgets.Display;

import framework.ui.interfaces.controls.ITabFolder;
import framework.ui.interfaces.controls.ITabItem;
import pze.business.Format;
import pze.business.navigation.NavigationManager;


/**
 * Abstrakte Klasse für Hauptfenster Messageboard und Freigabecenter
 * 
 * @author lisiecki
 */
public abstract class AbstractAktionCenterMainForm extends UniFormWithSaveLogic {

	/**
	 * Zeitintervall, dem jeweils neue Daten aus der DB geladen werden sollen
	 */
	private static final int DB_UPDATE_INTERVALL = 15 * 60 * 1000;
	
	private String CAPTION;

	private ITabItem m_tabItem;
	protected ITabFolder m_subTabFolder;
	private Thread m_threadAutoRefresh;

	
	/**
	 * Konstruktor
	 * 
	 * @param parent
	 * @param resid
	 * @param caption
	 * @throws Exception
	 */
	public AbstractAktionCenterMainForm(Object parent, String resid, String caption) throws Exception {
		super(parent, resid);
		
		CAPTION = caption;
		
		refreshTabItem();

		// automatisches Aktualisieren der Buchungen starten
		startAutoRefresh();
	}

	
	/**
	 * Auto-Refresh in bestimmtem Intervall
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
						System.out.println("startAutoRefresh()");
						System.out.println(m_threadAutoRefresh.isInterrupted());
						System.out.println(m_threadAutoRefresh.isAlive());
						refreshAllTabs(true);
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
	 * Thread zum Aktualisieren der Anzeige beenden
	 * 
	 * @see framework.ui.form.UniForm#onClose()
	 */
	public void onClose() {
		m_threadAutoRefresh.interrupt();
	}
	

	/**
	 * Alle Reiter aktualisieren
	 * 
	 * @param neuLaden Daten neu laden oder nur die Caption aktualisieren
	 */
	public void refreshAllTabs(final boolean neuLaden) {
		long a = System.currentTimeMillis();
		
		// Test, da ab und zu noch Widget disposed Meldung kommt
		if (!m_threadAutoRefresh.isAlive() || m_threadAutoRefresh.isInterrupted())
		{
			return;
		}

		System.out.println("refreshAllTabs");
		Display.getDefault().syncExec(new Runnable() {

			@Override
			public void run() {
				try 
				{
					int anzAntraege, summeAntraege;
					boolean aktualisieren;
					IAktionsCenterAbstractForm formFreigabecenter;
					System.out.println("run");
					
					// aktuelles Tab
					String key = m_subTabFolder.getSelection();
					if (key == null)
					{
						return;
					}
					summeAntraege = 0;

					// TabItems durchlaufen
					ITabItem tabItem;
					Iterator<ITabItem> iter = m_subTabFolder.getTabItems();
					while (iter.hasNext())
					{
						tabItem = iter.next();

						// Freigabecenter aktualisieren
						if (tabItem.getControl() instanceof IAktionsCenterAbstractForm)
						{
							formFreigabecenter = ((IAktionsCenterAbstractForm) tabItem.getControl());

							// das aktuelle Tab wird nicht aktualisiert, wenn das Freigabecenter aktiv ist
							aktualisieren = !key.equals(tabItem.getKey()) || !NavigationManager.getSelectedTabItem().equals(m_tabItem);
							if (aktualisieren && neuLaden)
							{
								long a1 = System.currentTimeMillis();
								formFreigabecenter.reloadTableData();
								System.out.println("autoRefresh-reload " + formFreigabecenter.getClass() + ":" + Format.getFormat2NksPunkt((System.currentTimeMillis() - a1)/1000.));
							}
							long a2 = System.currentTimeMillis();

							// Anzahl der aktuellen Anträge
							anzAntraege = formFreigabecenter.getAnzAntraegeAktiviert();
							summeAntraege += anzAntraege;
							System.out.println("autoRefresh-anzAntraege " + Format.getFormat2NksPunkt((System.currentTimeMillis() - a2)/1000.));

							if (aktualisieren)
							{
								updateCaption(anzAntraege, formFreigabecenter, tabItem);
							}
						}
					}

					// Summe der offenen Freigaben
					m_tabItem.setCaption(CAPTION + (summeAntraege > 0 ? " (" + summeAntraege + ")" : ""));
				}
				catch (Exception e) 
				{
					e.printStackTrace();
					onClose();
				}		
			}
		});
		System.out.println("autoRefresh:" + Format.getFormat2NksPunkt((System.currentTimeMillis() - a)/1000.));
	}
	
	
	/**
	 * Caption für ein übergebenes TabItem ändern
	 * 
	 * @param abstractFormFreigabecenter aktuelles Tab
	 */
	public void refreshCaption(IAktionsCenterAbstractForm abstractFormFreigabecenter) {
		int anzAntraege;
		IAktionsCenterAbstractForm formFreigabecenter;


		// TabItems durchlaufen
		ITabItem tabItem;
		Iterator<ITabItem> iter = m_subTabFolder.getTabItems();
		while (iter.hasNext())
		{
			tabItem = iter.next();

			// nur das übergebene Tab wird aktualisiert
			if (abstractFormFreigabecenter.equals(tabItem.getControl()))
			{
				formFreigabecenter = ((IAktionsCenterAbstractForm) tabItem.getControl());
				anzAntraege = formFreigabecenter.getAnzAntraegeAktiviert();

				updateCaption(anzAntraege, formFreigabecenter, tabItem);
			}
		}
	}
		

	/**
	 * Caption für ein TabItem aktualisieren
	 * 
	 * @param anzAntraege
	 * @param formFreigabecenter
	 * @param tabItem
	 */
	private void updateCaption(int anzAntraege, IAktionsCenterAbstractForm formFreigabecenter, ITabItem tabItem) {
		tabItem.setCaption(formFreigabecenter.getDefaultCaption() + (anzAntraege > 0 ? " (" + anzAntraege + ")" : ""));
	}

	
	/**
	 * Caption für ein übergebenes TabItem ändern
	 * 
	 * @param key aktuelles Tab
	 */
	public void refreshCaption(String key) {
		int anzAntraege;
		IAktionsCenterAbstractForm formFreigabecenter;


		// TabItems durchlaufen
		ITabItem tabItem;
		Iterator<ITabItem> iter = m_subTabFolder.getTabItems();
		while (iter.hasNext())
		{
			tabItem = iter.next();

			// nur das übergebene Tab wird aktualisiert
			if (key.equals(tabItem.getKey()))
			{
				formFreigabecenter = ((IAktionsCenterAbstractForm) tabItem.getControl());
				anzAntraege = formFreigabecenter.getAnzAntraegeAktiviert();

				updateCaption(anzAntraege, formFreigabecenter, tabItem);
			}
		}
	}

	
	protected void setTabItem(ITabItem tabItem) {
		m_tabItem = tabItem;
		m_subTabFolder = m_tabItem.getSubFolder();
	}
	
	
}
