package pze.business.navigation.treeloader;


import framework.business.interfaces.nodes.INode;
import pze.business.UserInformation;
import pze.business.navigation.NavigationBaseNode;
import pze.business.objects.personen.CoPerson;

/**
 * Loader für den Navigationsbaum mit den Personen
 * 
 * @author Lisiecki
 */
public class TreeLoaderPersonen extends TreeLoader {
	
	public static final String ROOT = "pze.navigation.projekte.personen";
	
	public static final String BITMAP_PERSONEN_ALLE = "group.link";
	public static final String BITMAP_PERSONEN_AKTIV = "group";
	public static final String BITMAP_PERSONEN_INAKTIV = "group.error";
	public static final String BITMAP_PERSONEN_AUSGESCHIEDEN = "group.delete";
	
	private NavigationBaseNode m_nodeAktivePersonen;
	private NavigationBaseNode m_nodeInaktivePersonen;
	private NavigationBaseNode m_nodeAusgeschiedenePersonen;
	
	private CoPerson m_coAktPerson;

	
	/* (non-Javadoc)
	 * @see framework.business.interfaces.loader.INodeLoader#load(framework.business.interfaces.nodes.INode)
	 */
	@Override
	public void load(INode node) throws Exception {
		
		if (node.getResID().equals(ROOT))
		{
			// der eigene User ist für alle sichtbar
			loadAktUser(node);
			
			// Personalansicht darf alle aktiven und inaktiven Personen laden
			if (UserInformation.getInstance().isPersonalansicht())
			{
				// Parent-Knoten für alle Personen (für Übersichtstabelle)
				node = createParentNodeAllePersonen(node);
				
				createParentNodeAktivePersonen(node);
				createParentNodeInaktivePersonen(node);
				createParentNodeAusgeschiedenePersonen(node);
				
				loadAllPersonen();
			}
			// Benutzer der Gruppe AL und aufwärts dürfen nur aktive Personen ausgewählter Abteilungen laden
			else if (UserInformation.getInstance().isAL())
			{
				createParentNodeAktivePersonen(node);
				
				loadAktivePersonen(m_coAktPerson.getCoPersonAbteilungsrechte(true).getSelectedIDs());
			}
		}
	}


	@Override
	public String getRoot() {
		return ROOT;
	}


	/**
	 * Personenknoten für die aktuelle angemeldete Person laden
	 * 
	 * @param parent   Parent-Knoten
	 * @throws Exception aus Loaderbase
	 */
	private void loadAktUser(INode parent) throws Exception {
		
		m_coAktPerson = new CoPerson();
		m_coAktPerson.loadByUserID(UserInformation.getUserID());

		createNodes(parent, m_coAktPerson);
	}


	/**
	 * Parent-Knoten für alle Personen
	 *  
	 * @param parent Parent-Knoten
	 * @return 
	 * @return 
	 */
	private NavigationBaseNode createParentNodeAllePersonen(INode parent) {
		String resid;
		
		// Knoten für alle Personen
		resid = parent.getResID() + ".allepersonen";
		return createNode(parent, resid, BITMAP_PERSONEN_ALLE, "alle Personen");
	}


	/**
	 * Parent-Knoten für aktive Personen
	 *  
	 * @param parent Parent-Knoten
	 * @return 
	 */
	private void createParentNodeAktivePersonen(INode parent) {
		String resid;
		
		// Knoten für aktive Personen
		resid = parent.getResID() + ".aktivepersonen";
		m_nodeAktivePersonen = createNode(parent, resid, BITMAP_PERSONEN_AKTIV, "aktive Personen");
	}


	/**
	 * Parent-Knoten für inaktive Personen
	 *  
	 * @param parent Parent-Knoten
	 * @return 
	 */
	private void createParentNodeInaktivePersonen(INode parent) {
		String resid;
		
		// Knoten für inaktive Personen
		resid = parent.getResID() + ".inaktivepersonen";
		m_nodeInaktivePersonen = createNode(parent, resid, BITMAP_PERSONEN_INAKTIV, "inaktive Personen");
	}


	/**
	 * Parent-Knoten für ausgeschiedene Personen
	 *  
	 * @param parent Parent-Knoten
	 * @return 
	 */
	private void createParentNodeAusgeschiedenePersonen(INode parent) {
		String resid;
		
		// Knoten für ausgeschiedene Personen
		resid = parent.getResID() + ".ausgeschiedenepersonen";
		m_nodeAusgeschiedenePersonen = createNode(parent, resid, BITMAP_PERSONEN_AUSGESCHIEDEN, "ausgeschiedene Personen");
	}


	/**
	 * alle Personenknoten laden
	 * 
	 * @throws Exception aus Loaderbase
	 */
	private void loadAllPersonen() throws Exception {
		CoPerson coPerson = new CoPerson();
		
		coPerson.loadAll();
		createNodes(coPerson);
	}


	/**
	 * Personenknoten der übergebenen Gruppen laden
	 * 
	 * @param abteilungIDs Abteilungen mit Komma getrennt
	 * @throws Exception aus Loaderbase
	 */
	private void loadAktivePersonen(String abteilungIDs) throws Exception {
		CoPerson coPerson = new CoPerson();
		
		coPerson.loadByCurrentUser();
		createNodes(coPerson);
	}


	/**
	 * Aus Cacheobject-Datensätzen Navigationsknoten erzeugen
	 *  
	 * @param parent Parent-Knoten
	 * @param co CO mit allen Datensätzen
	 * @param inital
	 * @throws Exception 
	 */
	private void createNodes(CoPerson co) throws Exception {
		String resid;
		NavigationBaseNode parent, nodePerson;
		
		
		if (co.moveFirst())
		{
			do
			{
				if (co.isAktiv())
				{
					parent = m_nodeAktivePersonen;
				}
				else if (co.isInaktiv())
				{
					parent = m_nodeInaktivePersonen;
				}
				else if (co.isAusgeschieden())
				{
					parent = m_nodeAusgeschiedenePersonen;
				}
				else
				{
					continue;
				}
				
				resid = parent.getResID() + "." + co.getID();
				nodePerson = createNode(co, co.getNavigationBitmap(), resid);
				
				parent.addChild(nodePerson);
				refreshNewNode(nodePerson);
			} while (co.moveNext());
		}
	}


	/**
	 * Aus Cacheobject-Datensätzen Navigationsknoten erzeugen
	 *  
	 * @param parent Parent-Knoten
	 * @param co CO mit allen Datensätzen
	 * @param inital
	 */
	private void createNodes(INode parent, CoPerson co) {
		String resid;
		NavigationBaseNode nodePerson;
		
		
		if (co.moveFirst())
		{
			do
			{
				resid = parent.getResID() + "." + co.getID();
				nodePerson = createNode(co, co.getNavigationBitmap(), resid);
				
				parent.addChild(nodePerson);
				refreshNewNode(nodePerson);
			} while ( co.moveNext());
		}
	}


	
}
