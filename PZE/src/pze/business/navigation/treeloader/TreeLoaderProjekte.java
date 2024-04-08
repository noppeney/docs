package pze.business.navigation.treeloader;


import framework.business.interfaces.nodes.INode;
import pze.business.navigation.NavigationBaseNode;
import pze.business.objects.AbstractCacheObject;
import pze.business.objects.projektverwaltung.CoAbruf;
import pze.business.objects.projektverwaltung.CoAuftrag;
import pze.business.objects.reftables.projektverwaltung.CoKunde;
import pze.business.objects.reftables.projektverwaltung.CoStatusProjekt;

/**
 * Loader für den Navigationsbaum mit den Projekten
 * 
 * @author Lisiecki
 */
public class TreeLoaderProjekte extends TreeLoader {
	
	public static final String ROOT = "pze.navigation.projekte";

	public static final String RESID_UEBERSICHT_ALLE = "alleprojekte";
	public static final String RESID_UEBERSICHT_AKTIV = "aktiveprojekte";
	public static final String RESID_UEBERSICHT_RUHEND = "ruhendeprojekte";
	public static final String RESID_UEBERSICHT_ABGESCHLOSSEN = "abgeschlosseneprojekte";

	private NavigationBaseNode m_nodeProjekte;
	private NavigationBaseNode m_nodeAktiveProjekte;
	private NavigationBaseNode m_nodeRuhendeProjekte;
	private NavigationBaseNode m_nodeAbgeschlosseneProjekte;
	
	private int m_statusProjektID;

	
	/* (non-Javadoc)
	 * @see framework.business.interfaces.loader.INodeLoader#load(framework.business.interfaces.nodes.INode)
	 */
	@Override
	public void load(INode node) throws Exception {
		
		if (node.getResID().equals(ROOT))
		{
			// übergeordnete Knoten
			createParentNodeProjekte(node);
			createParentNodeAktiveProjekte(m_nodeProjekte);
			createParentNodeRuhendeProjekte(m_nodeProjekte);
			createParentNodeAbgeschlosseneProjekte(m_nodeProjekte);

			// Kunden-Knoten
			m_statusProjektID = CoStatusProjekt.STATUSID_LAUFEND;
			loadKunden(m_nodeAktiveProjekte);
			m_statusProjektID = CoStatusProjekt.STATUSID_RUHEND;
			loadKunden(m_nodeRuhendeProjekte);
			m_statusProjektID = CoStatusProjekt.STATUSID_ABGESCHLOSSEN;
			loadKunden(m_nodeAbgeschlosseneProjekte);
			m_statusProjektID = 0;
		}
		else if (node.getObject() instanceof CoKunde)
		{
			loadAuftraege(node, m_statusProjektID);
		}
		else if (node.getObject() instanceof CoAuftrag)
		{
			loadAbrufe(node, m_statusProjektID);
		}
	}


	@Override
	public String getRoot() {
		return ROOT;
	}


	/**
	 * Parent-Knoten für alle Projekte
	 *  
	 * @param parent Parent-Knoten
	 * @return 
	 */
	private void createParentNodeProjekte(INode parent) {
		String resid;
		
		// Knoten für aktive Personen
		resid = parent.getResID() + "." + RESID_UEBERSICHT_ALLE;
		m_nodeProjekte = createNode(parent, resid, "page", "Projekte");
	}


	/**
	 * Parent-Knoten für aktive Projekte
	 *  
	 * @param parent Parent-Knoten
	 * @return 
	 */
	private void createParentNodeAktiveProjekte(INode parent) {
		String resid;
		
		// Knoten für aktive Projekte
		resid = parent.getResID() + "." + RESID_UEBERSICHT_AKTIV;
		m_nodeAktiveProjekte = createNode(parent, resid, "page.edit", "aktive Projekte");
	}


	/**
	 * Parent-Knoten für ruhende Projekte
	 *  
	 * @param parent Parent-Knoten
	 * @return 
	 */
	private void createParentNodeRuhendeProjekte(INode parent) {
		String resid;
		
		// Knoten für ruhende Projekte
		resid = parent.getResID() + "." + RESID_UEBERSICHT_RUHEND;
		m_nodeRuhendeProjekte = createNode(parent, resid, "page.refresh", "ruhende Projekte");
	}


	/**
	 * Parent-Knoten für abgeschlossene Projekte
	 *  
	 * @param parent Parent-Knoten
	 * @return 
	 */
	private void createParentNodeAbgeschlosseneProjekte(INode parent) {
		String resid;
		
		// Knoten für aktive Personen
		resid = parent.getResID() + "." + RESID_UEBERSICHT_ABGESCHLOSSEN;
		m_nodeAbgeschlosseneProjekte = createNode(parent, resid, "page.delete", "abgeschlossene Projekte");
	}


	/**
	 * Kundenknoten laden für alle aktiven/inaktiven Projekte
	 * 
	 * @param parent   Parent-Knoten
	 * @throws Exception aus Loaderbase
	 */
	private void loadKunden(INode parent) throws Exception {
		CoKunde coKunde = new CoKunde();
		
		coKunde.loadAllWithProjekte(m_statusProjektID, true);
		
		createNodes(parent, coKunde);
	}


	/**
	 * Auftragsknoten für einen Kundenknoten laden
	 * 
	 * @param nodeKunde   Parent-Knoten des Kunden
	 * @throws Exception aus Loaderbase
	 */
	private void loadAuftraege(INode nodeKunde, int statusProjektID) throws Exception {
		CoKunde coKunde;
		CoAuftrag coAuftrag = new CoAuftrag();
		
		coKunde = (CoKunde) nodeKunde.getObject();
		coKunde.moveTo(nodeKunde.getBookmark());
		
		coAuftrag.load(statusProjektID, coKunde.getID(), false);
		
		createNodes(nodeKunde, coAuftrag);
	}


	/**
	 * Abrufknoten für einen Auftragsknoten laden
	 * 
	 * @param nodeAuftrag   Parent-Knoten des Auftrags
	 * @throws Exception aus Loaderbase
	 */
	private void loadAbrufe(INode nodeAuftrag, int statusProjektID) throws Exception {
		CoAuftrag coAuftrag;
		CoAbruf coAbruf;
		
		coAuftrag = (CoAuftrag) nodeAuftrag.getObject();
		coAuftrag.moveTo(nodeAuftrag.getBookmark());
		
		coAbruf = new CoAbruf();
		coAbruf.loadByAuftragID_Status(coAuftrag.getID(), statusProjektID);
		
		createNodes(nodeAuftrag, coAbruf);
	}

	
	/**
	 * Für Aufträge die Bezeichnung mit in die Knotenbezeichnung schreiben
	 * 
	 * @see pze.business.navigation.treeloader.TreeLoader#getNodeText(pze.business.objects.AbstractCacheObject)
	 */
	protected String getNodeText(AbstractCacheObject co) {
		return co.getBezeichnung();
	}

	
}
