package pze.business.navigation;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import framework.Application;
import framework.business.action.Action;
import framework.business.action.ActionAdapter;
import framework.business.interfaces.fields.IFieldDescription;
import framework.business.interfaces.nodes.INode;
import framework.business.interfaces.refresh.IRefreshable;
import framework.business.interfaces.session.ISession;
import framework.business.nodes.BaseNode;
import framework.business.resources.ResourceMapper;
import framework.business.session.Session;
import framework.ui.interfaces.controls.IControl;
import framework.ui.interfaces.controls.ITabFolder;
import framework.ui.interfaces.controls.ITabItem;
import framework.ui.interfaces.controls.ITreeControl;
import framework.ui.interfaces.selection.INodeListener;
import framework.ui.menutoolbar.MainMenu;
import framework.ui.tabcontrol.TabFolder;
import framework.ui.tabcontrol.TabItem;
import pze.business.Messages;
import pze.business.UserInformation;
import pze.business.navigation.action.archiv.ActionArchivierenBewegungsdaten;
import pze.business.navigation.action.archiv.ActionArchivierenPerson;
import pze.business.navigation.action.archiv.ActionArchivierenProjekte;
import pze.business.navigation.action.archiv.ActionLoeschenBewegungsdaten;
import pze.business.navigation.action.archiv.ActionLoeschenPerson;
import pze.business.navigation.action.archiv.ActionLoeschenProjekte;
import pze.business.navigation.action.archiv.ActionWiederherstellenBewegungsdaten;
import pze.business.navigation.action.archiv.ActionWiederherstellenPerson;
import pze.business.navigation.action.archiv.ActionWiederherstellenProjekte;
import pze.business.navigation.treeloader.TreeLoader;
import pze.business.navigation.treeloader.TreeLoaderArchiv;
import pze.business.navigation.treeloader.TreeLoaderPausenmodelle;
import pze.business.navigation.treeloader.TreeLoaderPersonen;
import pze.business.navigation.treeloader.TreeLoaderProjekte;
import pze.business.navigation.treeloader.TreeLoaderZeitmodelle;
import pze.business.objects.AbstractCacheObject;
import pze.business.objects.CoPausenmodell;
import pze.business.objects.CoZeitmodell;
import pze.business.objects.archiv.CoArchivBewegungsdaten;
import pze.business.objects.archiv.CoArchivPerson;
import pze.business.objects.archiv.CoArchivProjekte;
import pze.business.objects.personen.CoPerson;
import pze.business.objects.projektverwaltung.CoAbruf;
import pze.business.objects.projektverwaltung.CoAuftrag;
import pze.business.objects.reftables.personen.CoStatusAktivInaktiv;
import pze.business.objects.reftables.projektverwaltung.CoKunde;
import pze.business.objects.reftables.projektverwaltung.CoStatusProjekt;
import pze.ui.formulare.FormPausenmodell;
import pze.ui.formulare.FormZeitmodell;
import pze.ui.formulare.person.FormPerson;
import pze.ui.formulare.projektverwaltung.FormAbruf;
import pze.ui.formulare.projektverwaltung.FormAuftrag;
import pze.ui.formulare.uebersicht.FormUebersichtAuftraege;
import pze.ui.formulare.uebersicht.FormUebersichtPausenmodelle;
import pze.ui.formulare.uebersicht.FormUebersichtPersonen;
import pze.ui.formulare.uebersicht.FormUebersichtZeitmodelle;

/**
 * Navigationsmanager
 * 
 * @author Lisiecki
 */
public class NavigationManager implements INodeListener {
	
	private static NavigationManager m_instance = null;
	
	private ArrayList<TreeLoader> alLoader = null;
	
	private ITabItem m_tabItem = null;
	private ITreeControl m_tree = null;
	
	private INode m_lastSelectedNode = null;

	private Hashtable<String, MainMenu> m_mapContextMenu = new Hashtable<String, MainMenu>();


	/**
	 * Getter Instanz NavigationManager
	 * 
	 * @return m_instance NavigationManager
	 */
	public static NavigationManager getInstance() throws Exception {
		if (NavigationManager.m_instance == null)
		{
			NavigationManager.m_instance = new NavigationManager();
		}

		return NavigationManager.m_instance;
	}


	private NavigationManager() throws Exception {
	}


	public void init() throws Exception {
		initLoader();
		initContextMenu();
	}


	private void initLoader() throws Exception{
		TreeLoader loader;
		
		alLoader = new ArrayList<TreeLoader>();
		
		// Personen-Loader
		loader = new TreeLoaderPersonen();
		m_tree.addLoader(TreeLoaderPersonen.ROOT, loader);
		alLoader.add(loader);
		
		// Pausen- und Zeitmodelle für Administratoren
		if (UserInformation.getInstance().isAdmin())
		{
			// Pausenmodell-Loader
			loader = new TreeLoaderPausenmodelle();
			m_tree.addLoader(TreeLoaderPausenmodelle.ROOT, loader);
			alLoader.add(loader);

			// Zeitmodell-Loader
			loader = new TreeLoaderZeitmodelle();
			m_tree.addLoader(TreeLoaderZeitmodelle.ROOT, loader);
			alLoader.add(loader);
			
			// Archiv
			loader = new TreeLoaderArchiv();
			m_tree.addLoader(TreeLoaderArchiv.ROOT, loader);
			alLoader.add(loader);
		}
		
		// Projekt-Loader
		loader = new TreeLoaderProjekte();
		m_tree.addLoader(TreeLoaderProjekte.ROOT, loader);
		alLoader.add(loader);
	}


	private void initContextMenu() {
		
		// Neues Objekt erstellen
		Action.get("new.object").setActionListener(new ActionAdapter()
		{
			/*
			 * (non-Javadoc)
			 * @see framework.business.action.ActionAdapter#activate(java.lang.Object)
			 */
			@Override
			public void activate(Object sender) throws Exception {
				neuesObject(sender);
			}
			
			/*
			 * (non-Javadoc)
			 * @see framework.business.action.ActionAdapter#getEnabled()
			 */
			public boolean getEnabled() {
				TreeLoader activeLoader;

				activeLoader = getActiveLoader();

				return activeLoader instanceof TreeLoaderPersonen
						|| activeLoader instanceof TreeLoaderPausenmodelle
						|| activeLoader instanceof TreeLoaderZeitmodelle
						|| activeLoader instanceof TreeLoaderProjekte;
			}
		});
		
		// Neues Objekt erstellen
		Action.get("delete.object").setActionListener(new ActionAdapter()
		{
			/*
			 * (non-Javadoc)
			 * @see framework.business.action.ActionAdapter#activate(java.lang.Object)
			 */
			@Override
			public void activate(Object sender) throws Exception {
				deleteObject(sender);
			}
			
			/*
			 * (non-Javadoc)
			 * @see framework.business.action.ActionAdapter#getEnabled()
			 */
			public boolean getEnabled() {
				TreeLoader activeLoader;
				AbstractCacheObject co;
				CoZeitmodell coZeitmodell;
				boolean inUse;


				// prüfen, ob das Zeitmodell gelöscht werden kann
				inUse = true;
				co =  getSelectedCoObject();
				if (co != null)
				{
					try
					{
						coZeitmodell = (CoZeitmodell) co;
						if (coZeitmodell.hasRows() && !coZeitmodell.isInUse())
						{
							inUse = false;
						}
					} 
					catch (Exception e)
					{
					}
				}

				activeLoader = getActiveLoader();
				return (activeLoader instanceof TreeLoaderZeitmodelle)
						&& !inUse
						;

			}
		});
		
		
		// Archiv-Funktionen
		Action.get("archivieren.person").setActionListener(new ActionArchivierenPerson(this));
		Action.get("restore.person").setActionListener(new ActionWiederherstellenPerson(this));
		Action.get("delete.person").setActionListener(new ActionLoeschenPerson(this));
		
		Action.get("archivieren.bewegungsdaten.jahr").setActionListener(new ActionArchivierenBewegungsdaten(this));
		Action.get("restore.bewegungsdaten.jahr").setActionListener(new ActionWiederherstellenBewegungsdaten(this));
		Action.get("delete.bewegungsdaten.jahr").setActionListener(new ActionLoeschenBewegungsdaten(this));
		
		Action.get("archivieren.projekte.jahr").setActionListener(new ActionArchivierenProjekte(this));
		Action.get("restore.projekte.jahr").setActionListener(new ActionWiederherstellenProjekte(this));
		Action.get("delete.projekte.jahr").setActionListener(new ActionLoeschenProjekte(this));

		
		// Kontext-Menüs speichern
		addContextMenu("context.person");
		addContextMenu("context.pausenmodell");
		addContextMenu("context.zeitmodell");
		addContextMenu("context.auftrag");
		addContextMenu("context.abruf");
		
		addContextMenu("context.archiv.person");
		addContextMenu("context.archiv.bewegungsdaten");
		addContextMenu("context.archiv.projekte");
	}

	
	/**
	 * Setzt das Kontextemenü des Navigationsbaums
	 * @param menu_resid
	 */
	private void addContextMenu(String menu_resid) {
		MainMenu context_menu = new MainMenu();
		context_menu.setRootNode(ResourceMapper.getInstance().getItem(menu_resid));
		m_mapContextMenu.put(menu_resid, context_menu);
	}


	/*
	 * (non-Javadoc)
	 * @see framework.ui.interfaces.selection.INodeListener#nodeSelected(framework.ui.interfaces.controls.IControl, framework.business.interfaces.nodes.INode)
	 */
	@Override
	public void nodeSelected(IControl control, INode node) throws Exception {
		String resID;
		NavigationBaseNode baseNode;
		Object object;
		
		resID = node.getResID();
		baseNode = (NavigationBaseNode) node;

		try 
		{
			object = baseNode.getCacheObject();
					
			if (object instanceof CoArchivPerson)
			{
				m_tree.setMenu(m_mapContextMenu.get("context.archiv.person"));
			}
			else if (object instanceof CoArchivBewegungsdaten)
			{
				m_tree.setMenu(m_mapContextMenu.get("context.archiv.bewegungsdaten"));
			}
			else if (object instanceof CoArchivProjekte || resID.contains("archiv.projekte"))
			{
				m_tree.setMenu(m_mapContextMenu.get("context.archiv.projekte"));
			}
			else if (resID.contains("archiv"))
			{
				m_tree.setMenu(null);
			}
			else if (object instanceof CoPerson || resID.contains("person"))
			{
				m_tree.setMenu(m_mapContextMenu.get("context.person"));
			}
			else if (object instanceof CoPausenmodell || resID.contains("pausenmodell"))
			{
				m_tree.setMenu(m_mapContextMenu.get("context.pausenmodell"));
			}
			else if (object instanceof CoZeitmodell || resID.contains("zeitmodell"))
			{
				m_tree.setMenu(m_mapContextMenu.get("context.zeitmodell"));
			}
			else if (object instanceof CoAuftrag || object instanceof CoAbruf)
			{
				m_tree.setMenu(m_mapContextMenu.get("context.abruf"));
			}
			else if (resID.contains("projekt"))
			{
				m_tree.setMenu(m_mapContextMenu.get("context.auftrag"));
			}
			else
			{
				m_tree.setMenu(null);
			}
		}		
		catch(Exception e) {
			String msg = ResourceMapper.getInstance().getErrorMessage(e);
			Messages.showErrorMessage(Application.getCaption(), msg);
		}
		

		
		// ------- wurde der selbe Knoten nochmal angeklickt?
		boolean same_selection = m_lastSelectedNode == node;

		// ------- ausgewählten Baum und Knoten abspeichern und in der
		// Statuszeile anzeigen

		m_lastSelectedNode = node;

		if (Application.isMainframeEnabled())
			Application.getMainFrame().getStatusInfo().setPaneText(0,
					node.getText());

		if (same_selection)
			return;

		// ------- Caption der Registerkarte setzen


		if (resID != null) {

			IFieldDescription fdesc = ResourceMapper.getInstance()
					.getFieldDescription(resID);

			if (fdesc != null) {
				String caption = fdesc.getCaption() + " " + node.getText();
				m_tabItem.setCaption(caption);
			} else {
				// Beladungsknoten ist nicht im Dictionary definiert
				// Caption wird statisch hinterlegt
				String caption = node.getText();
				m_tabItem.setCaption(caption);

			}
		}

		// ------- Bitmap auf der Registerkarte setzen

		String bitmapid = node.getBitmap();

		if (bitmapid != null)
		{
			m_tabItem.setBitmap(bitmapid);
		}
	}


	/*
	 * (non-Javadoc)
	 * @see framework.ui.interfaces.selection.INodeListener#nodeDefaultSelected(framework.ui.interfaces.controls.IControl, framework.business.interfaces.nodes.INode)
	 */
	@Override
	public void nodeDefaultSelected(IControl control, INode node) {
		int id;
		Object object;
		AbstractCacheObject co;
		
		try
		{
			object = node.getObject();
			
			if (object instanceof CoArchivPerson)
			{
				// damit Person nicht geöffnet wird
			}
			else if (object instanceof CoPerson)
			{
				FormPerson.open(getSession(), (NavigationBaseNode) node);
			}
			else if (object instanceof CoPausenmodell)
			{
				FormPausenmodell.open(getSession(), (NavigationBaseNode) node);
			}
			else if (object instanceof CoZeitmodell)
			{
				FormZeitmodell.open(getSession(), (NavigationBaseNode) node);
			}
			else if (object instanceof CoAuftrag)
			{
				FormAuftrag.open(getSession(), (NavigationBaseNode) node);
			}
			else if (object instanceof CoAbruf)
			{
				FormAbruf.open(getSession(), (NavigationBaseNode) node);
			}
			
			else
			{
				// Übersichten der Personen Zeit- und Pausenmodelle für Gruppe Personalansicht
				if (UserInformation.getInstance().isPersonalansicht())
				{
					if (node.getResID().endsWith(".allepersonen"))
					{
						FormUebersichtPersonen.open(getSession(), CoStatusAktivInaktiv.STATUSID_ALLE);
					}
					else if (node.getResID().endsWith(".aktivepersonen"))
					{
						FormUebersichtPersonen.open(getSession(), CoStatusAktivInaktiv.STATUSID_AKTIV);
					}
					else if (node.getResID().endsWith(".inaktivepersonen"))
					{
						FormUebersichtPersonen.open(getSession(), CoStatusAktivInaktiv.STATUSID_INAKTIV);
					}
					else if (node.getResID().endsWith(".ausgeschiedenepersonen"))
					{
						FormUebersichtPersonen.open(getSession(), CoStatusAktivInaktiv.STATUSID_AUSGESCHIEDEN);
					}

					if (UserInformation.getInstance().isAdmin())
					{
						if (node.getResID().endsWith(".zeitmodelle"))
						{
							FormUebersichtZeitmodelle.open(getSession());
						}
						else if (node.getResID().endsWith(".pausenmodelle"))
						{
							FormUebersichtPausenmodelle.open(getSession());
						}
					}
				}

				// Übersichten der Projekte für Gruppe Projektleiter und -auswerter
//				if (UserInformation.getInstance().isProjektleiter())
				{
					id = 0;

					if (object instanceof CoKunde)
					{
						co = (AbstractCacheObject) object;
						co.moveTo(node.getBookmark());
						id = co.getID();
					}

					if (node.getResID().endsWith(TreeLoaderProjekte.RESID_UEBERSICHT_ALLE))
					{
						FormUebersichtAuftraege.open(getSession(), 0, id, node);
					}
					else if (node.getResID().contains(TreeLoaderProjekte.RESID_UEBERSICHT_AKTIV))
					{
						FormUebersichtAuftraege.open(getSession(), CoStatusProjekt.STATUSID_LAUFEND, id, node);
					}
					else if (node.getResID().contains(TreeLoaderProjekte.RESID_UEBERSICHT_RUHEND))
					{
						FormUebersichtAuftraege.open(getSession(), CoStatusProjekt.STATUSID_RUHEND, id, node);
					}
					else if (node.getResID().contains(TreeLoaderProjekte.RESID_UEBERSICHT_ABGESCHLOSSEN))
					{
						FormUebersichtAuftraege.open(getSession(), CoStatusProjekt.STATUSID_ABGESCHLOSSEN, id, node);
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		m_tree.expand(node);

	}


	/**
	 * neues Object erstellen und Formular öffnen
	 * 
	 * @param sender 
	 * @throws Exception
	 */
	private void neuesObject(Object sender) throws Exception {
		int auftragID;
		String resID;
		TreeLoader activeLoader;
		AbstractCacheObject selectedCoObject;

		activeLoader = getActiveLoader();

		if (activeLoader instanceof TreeLoaderPersonen)
		{
			FormPerson.open(getSession(), null);			
		}
		else if (activeLoader instanceof TreeLoaderPausenmodelle)
		{
			FormPausenmodell.open(getSession(), null);			
		}
		else if (activeLoader instanceof TreeLoaderZeitmodelle)
		{
			FormZeitmodell.open(getSession(), null);			
		}
		else if (activeLoader instanceof TreeLoaderProjekte)
		{
			resID = ((BaseNode)sender).getResID();
			
			if (resID.contains("auftrag"))
			{
				FormAuftrag.open(getSession(), null);
			}
			else if (resID.contains("abruf"))
			{
				auftragID = 0;
				selectedCoObject = getSelectedCoObject();
				
				if (selectedCoObject instanceof CoAuftrag)
				{
					auftragID = selectedCoObject.getID();
				}
				else if (selectedCoObject instanceof CoAbruf)
				{
					auftragID = ((CoAbruf) selectedCoObject).getAuftragID();
				}
				
				FormAbruf.openNew(getSession(), null, auftragID);
			}
		}
	}
	

	/**
	 * Object löschen
	 * 
	 * @param sender 
	 * @throws Exception
	 */
	private void deleteObject(Object sender) throws Exception {
		AbstractCacheObject co =  getSelectedCoObject();
	
		// Zeitmodell
		if (co instanceof CoZeitmodell)
		{
			if (!Messages.showYesNoMessage("Zeitmodell löschen", "Möchten Sie das Zeitmodell \"" + co.getBezeichnung() + "\" wirklich löschen?"))
			{
				return;
			}
			else
			{
				// Zeitmodell löschen und Baum neu laden
				co.begin();
				co.delete();
				co.save();
				
				reloadRootNode();
			}
		}

		// Kontextmenü löschen
//		m_tree.setMenu(null);
		
//		else if (co instanceof CoAnlage)
//		{
//			UniFormWithSaveLogic.deleteObject(getSession(), co, "die Auswertung", null);
//		}
	}

/******************************** ab hier nur noch Hilfsfunktionen ohne notwendige Anpassungen ******************************************************/
	
	
	/**
	 * setzt TabItem für Navigation 
	 * 
	 * @param ti
	 *            ITabItem
	 */
	public void setNavigationTabItem(ITabItem ti) {
		m_tabItem = ti;
	}

	
	/**
	 * Setter TreeControl
	 * 
	 * @param tree
	 *            ITreeControl
	 */
	public void setTree(ITreeControl tree) {
		m_tree = tree;
	}


	/* (non-Javadoc)
	 * @see framework.cui.controls.base.BaseControl#getSession()
	 */
	public ISession getSession() {
		return Session.getInstance();
	}


	/**
	 * @return aktuell selektiertes Objekte zurückgeben
	 */
	public AbstractCacheObject getSelectedCoObject() {
		if (m_tree.getSelectedNode() == null)
			return null;
		else
			return ((NavigationBaseNode)m_tree.getSelectedNode()).getCacheObject();
	}


	/**
	 * Nur den Knotenaktualiseren: TreeControl und INode angleichen, ohne Kinder
	 * @param node	neu zu ladenden Knoten
	 * @throws Exception
	 */
//	public void refreshDataChanged ( NavigationBaseNode node) throws Exception {
//		m_tree.refresh(IRefreshable.reasonDataChanged, node);
//	}
	
	
	/**
	 * Alles neu laden
	 * @throws Exception
	 */
	public void reloadRootNode() throws Exception {
		reloadNode(getActiveRootNode());
	}
	

	/**
	 * kompletten Baum neu laden
	 * 
	 * @param resID Root-Knoten
	 * @throws Exception
	 */
	public void reloadRootNode(String resID) throws Exception {
		
		// nur neu laden, wenn der Root-Knoten dem aktuellen Loader entspricht
		if (getActiveLoader().getRoot().equals(resID))
		{
			reloadRootNode();
//			m_tree.setSelectedNode(node); funktioniert nicht, da der node sich geändert hat
		}
	}
	

	/**
	 * Konten aus der Datenbank neu laden (inkl. Kinder)
	 * 
	 * @param node	neu zu ladender Knoten
	 * @throws Exception
	 */
	public void reloadNode (BaseNode node) throws Exception{
		TreeLoader loader = getActiveLoader();

		List<String> exp = getExpandedNodesHelper(node);
		while (node.hasChilds())
		{
			INode child =node.getChilds().next();
			node.removeChild(child);
			m_tree.refresh(IRefreshable.reasonDataRemoved, child);
		}
		loader.setRefresh(m_tree);
		loader.load(node);	
		loader.setRefresh(null);
		
		m_tree.refresh(IRefreshable.reasonItemsChanged, node);
		m_tree.refresh(IRefreshable.reasonDataChanged, node);
		expandNodes(node, exp);
	}
	

	/**
	 * Gibt den aktiven Rootknoten zurück
	 * 
	 * @return BaseNode
	 */
	private BaseNode getActiveRootNode() {
		return (BaseNode) m_tree.getRootNode();
	}
	

	/**
	 * momentan aktiven Loader
	 *  
	 * @return Loader
	 */
	public TreeLoader getActiveLoader() {
		String resID;
		
		// resID des root-Knoten
		resID = getActiveRootNode().getResID();
		
		return getLoader(resID);
	}
	

	/**
	 * Loader des übergebenen Root-Knotens
	 *  
	 * @return Loader
	 */
	private TreeLoader getLoader(String resID) {
		int iLoader, anzLoader;
		TreeLoader loader;
		
		// aktiven Loader herausfinden
		anzLoader = alLoader.size();
		for (iLoader=0; iLoader<anzLoader; ++iLoader)
		{
			loader = alLoader.get(iLoader);
			
			// redID des root-Knotens vergleichen
			if (loader.getRoot().equals(resID))
			{
				return loader;
			}
		}
		
		return null;
	}
	

	/**
	 * @param rootNode Anfang der Suche
	 * @return	gibt derzeit expandierte Knoten zurück
	 */
	private List<String> getExpandedNodesHelper(INode rootNode) {
		List<String> expandedNodes = new ArrayList<String>();
		Queue<INode> nodes = new LinkedList<INode>();
		nodes.add(rootNode);
		
		while (!nodes.isEmpty()) {
			BaseNode node = (BaseNode) nodes.poll();
			if (node != null && (node.equals(rootNode) || m_tree.isExpanded(node))) {
				expandedNodes.add(node.getResID());
				Iterator<INode> children = node.getChilds();
				while (children.hasNext())
					nodes.add(children.next());
			}
		}
		return expandedNodes;
	}
	
	
	/**
	 * Expandiert Knoten
	 * @param rootNode	Root, von dem die Expandierung gestarte weren soll
	 * @param expandedNodes	List der zu expandierenen Knoten
	 */
	private void expandNodes(INode rootNode, List<String> expandedNodes) {
		Queue<INode> nodes = new LinkedList<INode>();
		nodes.add(rootNode);
		
		while (!nodes.isEmpty()) {
			BaseNode node = (BaseNode) nodes.poll();
			
			if (expandedNodes.contains(node.getResID())) {
				m_tree.setExpanded(node, true);
				Iterator<INode> children = node.getChilds();
				while (children.hasNext())
					nodes.add(children.next());
			}
		}
	}


	/**
	 * Bestimmt das aktuell selectierte TabItem
	 * 
	 * @return TabItem
	 */
	public static TabItem getSelectedTabItem(){
//		String key = null;
		
		// TabItems holen
		ITabFolder tf = Session.getInstance().getMainFrame().getEditFolder();
		TabItem tabItem = (TabItem) tf.get(tf.getSelection());
		
//		TabFolder subTf = tabItem.getSubFolder();
//		if (subTf != null)
//		{
//			TabItem subTabItem = (TabItem) subTf.get(subTf.getSelection());
//			key = subTabItem.getKey();
//		}

		return tabItem;
	}
	

	/**
	 * Bestimmt das TabItem
	 * 
	 * @param key Key des TabItem
	 * @return TabItem
	 */
	public static TabItem getTabItem(String key){

		ITabFolder tf = Session.getInstance().getMainFrame().getEditFolder();
		TabItem tabItem = (TabItem) tf.get(key);
		
		return tabItem;
	}
	

	/**
	 * Bestimmt das aktuell selectierte TabItem
	 * 
	 * @return Key des TabItems
	 */
	public static String getSelectedTabItemKey(){
		String key = null;
		
		// TabItems holen
		ITabFolder tf = Session.getInstance().getMainFrame().getEditFolder();
		TabItem tabItem = (TabItem) tf.get(tf.getSelection());
		
		TabFolder subTf = tabItem.getSubFolder();
		if (subTf != null)
		{
			TabItem subTabItem = (TabItem) subTf.get(subTf.getSelection());
			key = subTabItem.getKey();
		}

		return key;
	}
	

}
