package pze.business.navigation.treeloader;


import framework.business.interfaces.nodes.INode;
import pze.business.navigation.NavigationBaseNode;
import pze.business.objects.archiv.CoArchivBewegungsdaten;
import pze.business.objects.archiv.CoArchivPerson;
import pze.business.objects.archiv.CoArchivProjekte;

/**
 * Loader für den Navigationsbaum mit dem Archiv
 * 
 * @author Lisiecki
 */
public class TreeLoaderArchiv extends TreeLoader {
	
	public static final String ROOT = "pze.navigation.archiv";
	
	private static final String BITMAP_PERSONEN = "group";
	private static final String BITMAP_BEWEGUNGSDATEN = "bricks";
	private static final String BITMAP_PROJEKTE = "box";

	private NavigationBaseNode m_nodePersonen;
	private NavigationBaseNode m_nodeJahre;
	private NavigationBaseNode m_nodeProjekte;

	
	/* (non-Javadoc)
	 * @see framework.business.interfaces.loader.INodeLoader#load(framework.business.interfaces.nodes.INode)
	 */
	@Override
	public void load(INode node) throws Exception {

		if (node.getResID().equals(ROOT))
		{
			// Personen
			createParentNodePersonen(node);
			loadArchivPersonen(m_nodePersonen);
			
			// Bewegungsdaten
			createParentNodeBewegungsdaten(node);
			loadArchivBewegungsdaten(m_nodeJahre);
			
			// Projekte
			createParentNodeProjekte(node);
			loadArchivProjekte(m_nodeProjekte);
		}
	}


	@Override
	public String getRoot() {
		return ROOT;
	}


	/**
	 * Parent-Knoten für Personen
	 *  
	 * @param parent Parent-Knoten
	 * @return 
	 */
	private void createParentNodePersonen(INode parent) {
		String resid;
		
		resid = parent.getResID() + ".personen";
		m_nodePersonen = createNode(parent, resid, BITMAP_PERSONEN, "Personen");
	}


	/**
	 * Parent-Knoten für Bewegungsdaten
	 *  
	 * @param parent Parent-Knoten
	 * @return 
	 */
	private void createParentNodeBewegungsdaten(INode parent) {
		String resid;
		
		resid = parent.getResID() + ".bewegungsdaten";
		m_nodeJahre = createNode(parent, resid, BITMAP_BEWEGUNGSDATEN, "Bewegungsdaten");
	}


	/**
	 * Parent-Knoten für Projekte
	 *  
	 * @param parent Parent-Knoten
	 * @return 
	 */
	private void createParentNodeProjekte(INode parent) {
		String resid;
		
		resid = parent.getResID() + ".projekte";
		m_nodeProjekte = createNode(parent, resid, BITMAP_PROJEKTE, "Projekte (Projektstunden)");
	}


	/**
	 * alle Personenknoten laden
	 * 
	 * @throws Exception aus Loaderbase
	 */
	private void loadArchivPersonen(INode parent) throws Exception {
		CoArchivPerson coPerson = new CoArchivPerson();
		
		coPerson.loadArchiv();
		createNodes(parent, coPerson);
	}


	/**
	 * alle Bewegungsdaten-Knoten laden
	 * 
	 * @throws Exception aus Loaderbase
	 */
	private void loadArchivBewegungsdaten(INode parent) throws Exception {
		CoArchivBewegungsdaten coJahre = new CoArchivBewegungsdaten();
		
		coJahre.loadArchiv();
		createNodes(parent, coJahre);
	}


	/**
	 * alle Projektknoten laden
	 * 
	 * @throws Exception aus Loaderbase
	 */
	private void loadArchivProjekte(INode parent) throws Exception {
		CoArchivProjekte coJahre = new CoArchivProjekte();
		
		coJahre.loadArchivJahre();
		createNodes(parent, coJahre);
	}

}
