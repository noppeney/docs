package pze.business.navigation.treeloader;

import framework.business.interfaces.loader.INodeLoader;
import framework.business.interfaces.nodes.INode;
import framework.business.interfaces.refresh.IRefreshable;
import pze.business.navigation.NavigationBaseNode;
import pze.business.objects.AbstractCacheObject;


/**
 * Abstrakte TreeLoader-Klasse zum Erzeugen von Knoten
 * 
 * @author Lisiecki
 */
public abstract class TreeLoader implements INodeLoader {
	
	public static final String RESID_TREE_ALL = "pze.navigation";

	protected IRefreshable refresh;

	
	/**
	 * Refresh-Listner (Control) setzen
	 * @param refresh
	 */
	public void setRefresh(IRefreshable refresh) {
		this.refresh = refresh;
	}


	public abstract String getRoot();
	

	/**
	 * Aus Cacheobject-Datensätzen Navigationsknoten erzeugen
	 *  
	 * @param parent Parent-Knoten
	 * @param co CO mit allen Datensätzen
	 * @param inital
	 * @throws Exception 
	 */
	protected void createNodes(INode parent, AbstractCacheObject co) throws Exception {
		String key, resid;
		NavigationBaseNode node;
		
		key = parent.getResID() + ".";
		
		// Knoten
		if (co.moveFirst())
		{
			do
			{
				resid = key + co.getID();
				node = createNode(co, co.getNavigationBitmap(), resid);
				
				parent.addChild(node);
				
				// ggf. weitere child-Knoten laden
				load(node);
				
				refreshNewNode(node);
			} while (co.moveNext());
		}
	}

	
	/**
	 * einen einzelnen Konten erstellern
	 * 
	 * @param co			CacheObject
	 * @param bitmap		Bitmap
	 * @param resid			RESID
	 * @return neueer Knoten
	 */
	protected NavigationBaseNode createNode(AbstractCacheObject co, String bitmap, String resid) {
		NavigationBaseNode node;
		
		node = new NavigationBaseNode(resid);
		node.setBitmap(bitmap);
		node.setText(getNodeText(co));
		
		node.setObject(co);
		node.setBookmark(co.getBookmark());
		
		return node;
	}


	/**
	 * Knoten erstellen
	 * 
	 * @param parent
	 * @param resid
	 * @param bitmap
	 * @param caption
	 * @return Knoten
	 */
	protected NavigationBaseNode createNode(INode parent, String resid, String bitmap, String caption) {
		NavigationBaseNode node;

		node = new NavigationBaseNode(resid);
		node.setBitmap(bitmap);
		node.setText(caption);
		
		parent.addChild(node);
		refreshNewNode(node);

		return node;
	}


	/**
	 * Text des Knotens mit dem übergebenen CO.<br>
	 * Per default die Bezeichnung
	 * 
	 * @param co
	 * @return
	 */
	protected String getNodeText(AbstractCacheObject co) {
		return co.getBezeichnung();
	}


	/**
	 * Control über neue Knoten informieren
	 * 
	 * @param node	neuer Knoten
	 */
	protected void refreshNewNode(NavigationBaseNode node) {
		if (refresh != null)
		{
			refresh.refresh(IRefreshable.reasonDataAdded, node.getParentNode());
		}
	}



}
