package pze.business.navigation.treeloader;


import framework.business.interfaces.nodes.INode;
import pze.business.navigation.NavigationBaseNode;
import pze.business.objects.CoZeitmodell;

/**
 * Loader für den Navigationsbaum mit den Zeitmodellen
 * 
 * @author Lisiecki
 */
public class TreeLoaderZeitmodelle extends TreeLoader {
	
	public static final String ROOT = "pze.navigation.zeitmodelle";

	
	/* (non-Javadoc)
	 * @see framework.business.interfaces.loader.INodeLoader#load(framework.business.interfaces.nodes.INode)
	 */
	@Override
	public void load(INode node) throws Exception {
		
		if (node.getResID().equals(ROOT))
		{
			node = createNodePausenmodelle(node);
			loadZeitmodelle(node);
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
	 */
	private NavigationBaseNode createNodePausenmodelle(INode parent) {
		String key, resid;
		NavigationBaseNode nodeZeitmodelle;
		
		key = parent.getResID() + ".";
		resid = key + "zeitmodelle";
		
		// Vater-Knoten
		nodeZeitmodelle = new NavigationBaseNode(resid);
		nodeZeitmodelle.setBitmap("clock");
		nodeZeitmodelle.setText("Zeitmodelle");
		
		parent.addChild(nodeZeitmodelle);
		refreshNewNode(nodeZeitmodelle);

		return nodeZeitmodelle;
	}


	/**
	 * Zeitmodellknoten laden
	 * 
	 * @param parent   Root-Knoten
	 * @throws Exception aus Loaderbase
	 */
	private void loadZeitmodelle(INode parent) throws Exception {
		CoZeitmodell coZeitmodell = new CoZeitmodell();
		coZeitmodell.loadAll();
		createNodes(parent, coZeitmodell);
	}

	
	
}
