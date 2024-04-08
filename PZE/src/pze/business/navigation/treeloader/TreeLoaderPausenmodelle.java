package pze.business.navigation.treeloader;


import framework.business.interfaces.nodes.INode;
import pze.business.navigation.NavigationBaseNode;
import pze.business.objects.CoPausenmodell;

/**
 * Loader für den Navigationsbaum mit den Pausenmodellen
 * 
 * @author Lisiecki
 */
public class TreeLoaderPausenmodelle extends TreeLoader {
	
	public static final String ROOT = "pze.navigation.pausenmodelle";

	
	/* (non-Javadoc)
	 * @see framework.business.interfaces.loader.INodeLoader#load(framework.business.interfaces.nodes.INode)
	 */
	@Override
	public void load(INode node) throws Exception {
		
		if (node.getResID().equals(ROOT))
		{
			node = createNodePausenmodelle(node);
			loadPausenmodelle(node);
		}
	}


	@Override
	public String getRoot() {
		return ROOT;
	}


	/**
	 * Aus Cacheobject-Datensätzen Navigationsknoten erzeugen
	 *  
	 * @param parent Parent-Knoten
	 * @return 
	 * @throws Exception 
	 */
	private NavigationBaseNode createNodePausenmodelle(INode parent) throws Exception {
		String key, resid;
		NavigationBaseNode nodePausenmodelle;
		
		key = parent.getResID() + ".";
		resid = key + "pausenmodelle";
		
		// Vater-Knoten 
		nodePausenmodelle = new NavigationBaseNode(resid);
		nodePausenmodelle.setBitmap(CoPausenmodell.getInstance().getNavigationBitmap());
		nodePausenmodelle.setText("Pausenmodelle");
		
		parent.addChild(nodePausenmodelle);
		refreshNewNode(nodePausenmodelle);

		return nodePausenmodelle;
	}


	/**
	 * Pausenknoten laden
	 * 
	 * @param parent   Root-Knoten
	 * @throws Exception aus Loaderbase
	 */
	private void loadPausenmodelle(INode parent) throws Exception {
		CoPausenmodell coPausenmodell = new CoPausenmodell();
		coPausenmodell.loadAll();
		createNodes(parent, coPausenmodell);
	}

	
	
}
