package startup;

import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import framework.Application;
import framework.FW;
import framework.business.action.Action;
import framework.business.connections.ConnectionManager;
import framework.business.fields.FieldDescription;
import framework.business.interfaces.FieldType;
import framework.business.interfaces.application.PluginAdapter;
import framework.business.interfaces.connections.IConnection;
import framework.business.interfaces.refresh.IRefreshable;
import framework.business.interfaces.resources.ResType;
import framework.business.interfaces.session.ISession;
import framework.business.interfaces.session.IUserInfo;
import framework.business.interfaces.statusinfo.IStatusInfo;
import framework.business.logging.Logger;
import framework.business.resources.ResourceMapper;
import framework.business.session.Session;
import framework.business.statusinfo.StatusInfo;
import framework.business.useradmin.migration.UserAdminMigration;
import framework.business.useradmin.model.ChangePasswordModel;
import framework.cui.factory.ControlFactory;
import framework.reftables.actions.ReftablesActionListener;
import framework.ui.interfaces.controls.ISplitter;
import framework.ui.interfaces.controls.ITabFolder;
import framework.ui.interfaces.controls.ITabItem;
import framework.ui.interfaces.controls.ITreeControl;
import framework.ui.mainframe.MainFrame;
import framework.ui.messagebox.HtmlMessageBox;
import framework.ui.messagebox.MessageBox;
import framework.ui.permissions.AdminPermissionsActionListener;
import framework.ui.useradmin.extlogin.ExtLoginForm;
import framework.ui.useradmin.password.ChangePasswordForm;
import pze.business.Format;
import pze.business.MessageCreater;
import pze.business.Messages;
import pze.business.UserInformation;
import pze.business.navigation.NavigationManager;
import pze.business.navigation.treeloader.TreeLoader;
import pze.business.navigation.treeloader.TreeLoaderProjekte;
import pze.ui.StartForm;
import pze.ui.actions.ActionAktuelleBuchungen;
import pze.ui.actions.ActionAnwesenheit;
import pze.ui.actions.ActionAuswertungAmpelliste;
import pze.ui.actions.ActionAuswertungAnAbwesenheit;
import pze.ui.actions.ActionAuswertungAuszahlung;
import pze.ui.actions.ActionAuswertungBuchhaltungArbeitszeit;
import pze.ui.actions.ActionAuswertungBuchhaltungStundenuebersicht;
import pze.ui.actions.ActionAuswertungDienstreisen;
import pze.ui.actions.ActionAuswertungKGG;
import pze.ui.actions.ActionAuswertungKontowerte;
import pze.ui.actions.ActionAuswertungKontowerteZeitraum;
import pze.ui.actions.ActionAuswertungMonatseinsatzblatt;
import pze.ui.actions.ActionAuswertungProjektstundenuebersicht;
import pze.ui.actions.ActionAuswertungUrlaub;
import pze.ui.actions.ActionAuswertungUrlaubsplanung;
import pze.ui.actions.ActionAuswertungVerletzerliste;
import pze.ui.actions.ActionBrueckentage;
import pze.ui.actions.ActionBuchungsUpdate;
import pze.ui.actions.ActionFirmenparameter;
import pze.ui.actions.ActionFreigabecenter;
import pze.ui.actions.ActionMessageboard;
import pze.ui.actions.ActionPersonenAbteilungsrechte;
import pze.ui.actions.ActionPersonenBenutzergruppen;
import pze.ui.actions.ActionProfileReset;
import pze.ui.actions.ActionVorjahresdatenHProjekte;
import pze.ui.actions.CloseAllAction;
import pze.ui.formulare.FormBuchungsUpdate;
import pze.ui.formulare.auswertung.FormAnwesenheit;
import pze.ui.formulare.freigabecenter.FormFreigabecenter;
import pze.ui.formulare.messageboard.FormMessageboard;
import pze.ui.formulare.person.FormPerson;


/**
 * Startklasse der Anwendung
 * 
 * @author Lisiecki
 */
public class PZEStartupAdapter extends PluginAdapter {
	
	private static final boolean AUTO_LOGIN = false; 
	public static final boolean MODUS_ARBEITSPLAN = false; 
	
	/**
	 * Zeitintervall, nach dem auf eine neuen Programmversion geprüft wird
	 */
	private static final int INTERVALL_VERSIONSCHECK = 60 * 60 * 1000;
	
	/**
	 * Name des Programms zum versionsupdate
	 */
	private static final String FILENAME_VERSIONSUPDATE = "Versionsupdate.jar";

	/**
	 * Name des Programms zum versionsupdate
	 */
	private static final String FOLDER_NEUE_VERSION = "neueVersion";

		

	/**
	 * Hauptprogramm
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String [] args) throws Exception {

		Session.setSingleUserMode(true);

		Application.setPluginListener(new PZEStartupAdapter());

		FieldDescription.setTimeZone(Application.util.getTimeZoneUTC());
		ResourceMapper.getInstance().setCurrentLanguage("DE");
		
		try 
		{
			MessageBox.setMessageBox(new HtmlMessageBox());
			InputStream is = PZEStartupAdapter.class.getResourceAsStream("/resources/app.xml");
			Application.init(is);
		}
		catch(Exception e) 
		{
			e.printStackTrace();
			String msg = ResourceMapper.getInstance().getErrorMessage(e);
			Messages.showErrorMessage(PZEStartupAdapter.class.getSimpleName(), msg);
		}
		finally 
		{
			System.exit(-1);
		}
		
	}

	
	/**
	 * Beim Start des Programms das Login-Fenster öffnen
	 * 
	 * (non-Javadoc)
	 * @see framework.business.interfaces.application.PluginAdapter#onLogin()
	 */
	@Override
	public boolean onLogin() throws Exception {
		int xPosition, yPosition;
		IUserInfo userInfo = Session.getInstance().getUserInfo();
		
		if(!userInfo.isAuthenticated()) {
			
			if (AUTO_LOGIN)
			{
				Session.getInstance().getUserInfo().setLoginName("lisiecki");
//				Session.getInstance().getUserInfo().setLoginName("kleemann");
//				Session.getInstance().getUserInfo().setPassword("passwort");
			}
			else
			{
				// Position des Login-Fensters setzen
				xPosition = Format.getIntValue((Toolkit.getDefaultToolkit().getScreenSize().width - 316) / 2);
				yPosition = Format.getIntValue((Toolkit.getDefaultToolkit().getScreenSize().getHeight() - 224) / 2);
						
				Session.getInstance().getProfile().setItem("unidialog.x.form.extusers.login", xPosition, FieldType.INTEGER);
				Session.getInstance().getProfile().setItem("unidialog.y.form.extusers.login", yPosition, FieldType.INTEGER);
				
				ExtLoginForm form = new ExtLoginForm();
				
				// Focus auf Login-Namen setzen
				form.findControl("ctl.extusers.login.loginname").setFocus();
				form.getDialog().show();

				if(form.getDialog().getRetVal() != FW.OK)
				{
					return false;
				}

				String loginName = form.getLoginName();
				String password = form.getPassword();
				Session.getInstance().getUserInfo().setLoginName(loginName);
				Session.getInstance().getUserInfo().setPassword(password);
			}
		} 
		
		return true;
	}

	
	/**
	 * Initialsierung der Application
	 * 
	 * (non-Javadoc)
	 * @see framework.business.interfaces.application.PluginAdapter#afterLoadingDictionary()
	 */
	@Override
	public void afterLoadingDictionary() throws Exception {
		
		//------- Module anmelden
		Application.addModule(new ModUsers());
		Application.addModule(new ModUsersUI());
		ModUsersUI.useExtendedUserAttributes(true);
		
		//------- Module initialisieren
		Application.initModules();
		UserAdminMigration.process();

		//------- Session start
		Application.onStartSession(Session.getInstance());
		
	}


	/**
	 * Anzeige des Passwort-Ändern-Fensters
	 * 
	 * (non-Javadoc)
	 * @see framework.business.interfaces.application.PluginAdapter#onChangePassword()
	 */
	@Override
	public String onChangePassword() throws Exception {
		
		ChangePasswordForm form = new ChangePasswordForm();
		ChangePasswordModel data = new ChangePasswordModel();
		data.create();
		form.setData(data);
		form.getDialog().show();
		
		if(form.getDialog().getRetVal() != FW.OK)
			return null;
		else
			return data.getPassword();

	}

	
	/**
	 * Initialisierung der Anwendung nach der allgemeinen FW-Initialisierung
	 * 
	 *  (non-Javadoc)
	 * @see framework.business.interfaces.PluginAdapter#postInit()
	 */
	@Override
	public void postInit() throws Exception {
		
		StatusInfo.openStatus(5);
		
		initStatusBar();
		StatusInfo.tick();

		initApplication();
		StatusInfo.tick();

		initNavigationTree();
		StatusInfo.tick();
		
		// Startfenster öffnen
		new StartForm();
		StatusInfo.tick();
		
		// Freigabecenter, Messageboard und Anwesenheit öffnen
		if (!AUTO_LOGIN)
		{
			int personID = UserInformation.getPersonID();
			
			// Freigabecenter und Messageboard
			if (personID > 0)
			{
				FormFreigabecenter.open(Session.getInstance());
				FormMessageboard.open(Session.getInstance());
			}
			
			// Anwesenheit immer öffnen
			FormAnwesenheit.open(Session.getInstance());
			
			// Personenreiter öffnen
			if (personID > 0)
			{
				FormPerson.open(Session.getInstance(), null, UserInformation.getPersonID());
			}
		}

		// Startfenster anzeigen
		Session.getInstance().getMainFrame().getEditFolder().setSelection("start.report");
		
		// Actions initialisieren
		initActions();
		
		// StatusInfo schließen
		StatusInfo.done();
		StatusInfo.closeStatus();

		//------- Refresh	
		Application.getMainFrame().refresh(IRefreshable.reasonRedraw, null);
		
		// Prüfung auf neue Programmversionen starten
		startCheckVersionsupdate();

		// Konsolenausgabe in Datei umlenken/umleiten
//		stdoutInFile();

		
		// Test Archivierung
//		CoPerson coPerson;
//		coPerson = new CoPerson();
//		coPerson.loadAll();
//		coPerson.moveFirst();
//		do
//		{
//			if (coPerson.getID() != 99
//					&& coPerson.getStatusAktivInaktivID() == CoStatusAktivInaktiv.STATUSID_AUSGESCHIEDEN
//					)
//				Archivierer.archivierePerson(coPerson.getID());
//		} while (coPerson.moveNext());
		
		
//		CoArchivPerson coArchivPerson;
//		coArchivPerson = new CoArchivPerson();
//		coArchivPerson.loadArchiv();
//		coArchivPerson.moveFirst();
//		do
//		{
//			if (coArchivPerson.getID() != 99)
//		{
////			Archivierer.restorePerson(coArchivPerson.getID());
//			Archivierer.deletePerson(coArchivPerson.getID());
//		}
//		} while (coArchivPerson.moveNext());

		Logger.initFinished();	
	}


	/**
	 * Statusleiste am unteren Rand der Anwendung initialisieren
	 */
	private void initStatusBar() {
		ISession session = Session.getInstance();		
		IConnection con = ConnectionManager.getInstance().getConnection();

		//------- Statusbar mit 5 panes
		IStatusInfo bar = Application.getMainFrame().getStatusInfo();
		bar.openStatus(5);
		String data_connection = "";
		
		if(con != null)
			data_connection = con.getServer() + ":" + con.getCatalog();
		
		if(con != null && con.isOpen())
		{
			bar.setPaneBitmap(1, "lib.globe");
		}
		else
		{
			bar.setPaneBitmap(1, "lib.globe.disabled");
		}
			
		bar.setPaneBitmap(2, "lib.ok");

		bar.setPaneText(0, "");
		bar.setPaneText(1, data_connection);
		bar.setPaneText(2, session.getUserInfo().getUsername());
		bar.setPaneText(3, "");
	}


	/**
	 * Namen der Anwendung setzen 
	 */
	private void initApplication() {
		// Datum aus dem Programmnamen auslesen
		String caption = Application.getCaption();
		String datum = caption.substring(caption.indexOf("("));
		
		// Datum aus dem Namen der Anwendung löschen
		caption = caption.substring(0, caption.indexOf(" ("));		
		Application.getMainFrame().setCaption(caption + " " + Application.getVersionString() + " " + datum);
		
		
		checkMaximumSize();
	}


	/**
	 * Fenster ggf. maximieren.<br>
	 * Diese Funktion funktioniert über das Framwork nicht.
	 */
	private void checkMaximumSize() {
		boolean setMaximized = (boolean) Session.getInstance().getProfile().getValue("mainframe.maximized", false);
		if (setMaximized)
		{
			((MainFrame) Application.getMainFrame()).getShell().setMaximized(true);
		}
	}


	/**
	 * Initialisierung des Navigationsfensters mit Baum
	 * 
	 * @throws Exception
	 */
	private void initNavigationTree() throws Exception {
		ITreeControl m_tree;
		ITabFolder tfnav;
		ISplitter navsplitter;
		ITabItem tiTree;
		
		tfnav = Application.getMainFrame().getNavFolder();
		tfnav.setCloseAllEnabled(true);
		
		navsplitter = (ISplitter) ControlFactory.createControl(tfnav, "navigation.splitter", ResType.VSPLITTER);
		tiTree = tfnav.add("navigation", null, navsplitter, false);


		// Baum erzeugen und initialisieren (dynamische Navigation)
		m_tree = (ITreeControl) ControlFactory.createControl(navsplitter, getResIdTree(), ResType.TREE);
		m_tree.setNodeListener(NavigationManager.getInstance());
		
		NavigationManager.getInstance().setNavigationTabItem(tiTree);
		NavigationManager.getInstance().setTree(m_tree);
		NavigationManager.getInstance().init();

		// Selektion des tf-Tabs
		tfnav.setSelection("navigation");

		// ---- erstmaliges Laden des TreeControls
		m_tree.refresh(IRefreshable.reasonDataChanged, null);
	}


	/**
	 * ResID des Tree's in Abhängigkeit von dem Benutzer bestimmen 
	 * 
	 * @return
	 * @throws Exception
	 */
	private String getResIdTree() throws Exception {
		
		// Admins sehen alle Bäume
		if (UserInformation.getInstance().isAdmin())
		{
			return TreeLoader.RESID_TREE_ALL;
		}
		// Projekte für Projektleiter & aufwärts
//		else if (UserInformation.getInstance().isProjektleiter())
		{
			return TreeLoaderProjekte.ROOT;
		}
		
		// sonst nur Personen
//		return TreeLoaderPersonen.ROOT;
	}


	/**
	 * Aktionen des Menüs anmelden
	 */
	private void initActions() {
		Action.get("admin.permissions").addActionListener( new AdminPermissionsActionListener());
		
		Action.get("admin.reftable").addActionListener(new ReftablesActionListener("file.new", "edit.delete"));
		Action.get("windows.closeall").addActionListener(new CloseAllAction());

		Action.get("profile.reset").addActionListener(new ActionProfileReset());

		Action.get("auswertung.buchungsupdate").addActionListener(new ActionBuchungsUpdate());

		Action.get("auswertung.anwesenheit").addActionListener(new ActionAnwesenheit());

		Action.get("auswertung.freigaben").addActionListener(new ActionFreigabecenter());
		Action.get("auswertung.messages").addActionListener(new ActionMessageboard());

		Action.get("auswertung.urlaubsplanung").addActionListener(new ActionAuswertungUrlaubsplanung());

		Action.get("auswertung.aktuelleBuchungen").addActionListener(new ActionAktuelleBuchungen());
		Action.get("auswertung.verletzerliste").addActionListener(new ActionAuswertungVerletzerliste());
		Action.get("auswertung.dienstreisen").addActionListener(new ActionAuswertungDienstreisen());

		Action.get("auswertung.firmenparameter").addActionListener(new ActionFirmenparameter());
		Action.get("auswertung.brueckentage").addActionListener(new ActionBrueckentage());
		
		Action.get("auswertung.person.benutzergruppen").addActionListener(new ActionPersonenBenutzergruppen());
		Action.get("auswertung.person.abteilungsrechte").addActionListener(new ActionPersonenAbteilungsrechte());

		Action.get("auswertung.buchhaltung.stundenuebersicht").addActionListener(new ActionAuswertungBuchhaltungStundenuebersicht());
		Action.get("auswertung.person.arbeitszeit").addActionListener(new ActionAuswertungBuchhaltungArbeitszeit());

		Action.get("auswertung.kontowerte").addActionListener(new ActionAuswertungKontowerte());
		Action.get("auswertung.kontowerte.zeitraum").addActionListener(new ActionAuswertungKontowerteZeitraum());
		Action.get("auswertung.monatseinsatzblatt").addActionListener(new ActionAuswertungMonatseinsatzblatt());
		Action.get("auswertung.auszahlung").addActionListener(new ActionAuswertungAuszahlung());
		Action.get("auswertung.person.anabwesenheit").addActionListener(new ActionAuswertungAnAbwesenheit());
		Action.get("auswertung.person.urlaub").addActionListener(new ActionAuswertungUrlaub());
//		Action.get("auswertung.dienstreisen").addActionListener(new ActionAuswertungDienstreisen());
//		Action.get("auswertung.dienstreisenabrechnung").addActionListener(new ActionAuswertungDienstreisenAbrechnung());

		Action.get("auswertung.ampelliste").addActionListener(new ActionAuswertungAmpelliste());
		Action.get("auswertung.stundenuebersicht").addActionListener(new ActionAuswertungProjektstundenuebersicht());
		Action.get("auswertung.kgg").addActionListener(new ActionAuswertungKGG());
		Action.get("auswertung.jahresweise").addActionListener(new ActionVorjahresdatenHProjekte());
	}
	
	
	/**
	 * Default-DB-Verbindung öffnen.<br>
	 * Diese Funktion wird benötigt, wenn zuvor eine verbindung zur Geloc-DB zum Laden der terminal-Buchungen aufgebaut wurde.
	 * 
	 */
	public static void openDefaultDbConnection() {
		Application.getLoaderBase().setConnection(ConnectionManager.getInstance().getConnection("wti_pze"));
	}


	/**
	 * prüft, ob die Default-DB-Verbindung geöffnet ist
	 * @return 
	 * 
	 */
	public static boolean isDefaultDbConnection() {
		return Application.getLoaderBase().getConnection().getResID().equals("wti_pze");
	}


	/**
	 * DB-Verbindung zur Geloc-DB zum Laden der Terminal-Buchungen herstellen.<br>
	 * 
	 */
	public static void openGelocDbConnection() throws Exception {
		Application.getLoaderBase().setConnection(ConnectionManager.getInstance().getConnection("wti_time"));
		ConnectionManager.getInstance().getConnection("wti_time").open();
	}


//	/**
//	 * DB-Verbindung zur Intranet-DB zum Laden der geplanten Urlaubstage herstellen.<br>
//	 * 
//	 */
//	public static void openIntranetDbConnection() throws Exception {
//		Application.getLoaderBase().setConnection(ConnectionManager.getInstance().getConnection("wti_intranet"));
//		ConnectionManager.getInstance().getConnection("wti_intranet").open();
//	}

	
	/**
	 * Prüft zwischen 12 und 1 Uhr, ob eine neue Programmversion vorliegt. <br>
	 * Wenn ja, wird das aktuelle Programm beendet, damit die neue Version eingespielt werden kann.
	 * 
	 * @throws Exception
	 */
	private void startCheckVersionsupdate() throws Exception{
		
		Thread thread = new Thread()
		{
			public void run() {
				GregorianCalendar gregDatum;
				File file;

				file = new File(FOLDER_NEUE_VERSION);
				while (true)
				{
					try // try/catch in der Schleife, damit diese bei einem Fehler weiterläuft
					{
						gregDatum = Format.getGregorianCalendar(new Date());

						// nur zwischen 12 und 1 Uhr auf neue Version prüfen
						if (gregDatum.get(Calendar.HOUR_OF_DAY) == 0)
						{
							// wenn eine neue Version vorhanden ist, beende die Schleife und das Programm
							if (file != null && Arrays.asList(file.listFiles()).size() > 0)
							{
								break;
							}
						}

						// Meldungen für das Messageboard erstellen
						MessageCreater.createMessages(gregDatum);

						Thread.sleep(INTERVALL_VERSIONSCHECK);
					}
					catch (Exception e)
					{
						e.printStackTrace();
						Messages.showErrorMessage("Fehler in checkVersionsupdate()", e.getMessage() + "\n" + e.getStackTrace());
					}
				}

				// wenn in dieser Programmversion das Buchungsupdate läuft, starte das Versionsupdate
				if (FormBuchungsUpdate.isRunning())
				{
					try 
					{
						Runtime.getRuntime().exec("java -jar \"" + FILENAME_VERSIONSUPDATE + "\"");
					}
					catch (IOException e) 
					{
						e.printStackTrace();
					}
				}

				System.exit(-1);
			}
		};

		thread.start();
	}


//	/**
//	 * Konsolenausgabe in Datei umlenken/umleiten.<br>
//	 * Z. B. um einen Fehler zu finden.
//	 * 
//	 * @throws Exception
//	 * @throws FileNotFoundException
//	 */
//	private void stdoutInFile() throws Exception, FileNotFoundException {
//		if (UserInformation.getInstance().getPersonID() == 32 // Jonas
////				|| UserInformation.getInstance().getPersonID() == 46 // Lisiecki
//				)
//		{
//			System.setOut(new PrintStream(new File("p:/lisiecki/pze/log_" + System.currentTimeMillis() + ".log")));
//		}
//	}


}
