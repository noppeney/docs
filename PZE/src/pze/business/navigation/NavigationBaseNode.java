package pze.business.navigation;

import framework.business.nodes.BaseNode;
import pze.business.objects.AbstractCacheObject;

/**
 * Basenode für die Navigation
 * @author Lisiecki
 *
 */
public class NavigationBaseNode extends BaseNode {

	/**
	 * Konstruktor
	 * @param resid RESID
	 */
	public NavigationBaseNode(String resid) {
		super(resid);
	}

	/**
	 * @return Primärschlüssel des Datensatzes des Knotens
	 */
	public int getID()
	{
		if (getObject() instanceof AbstractCacheObject)
		{		
			return getCacheObject().getID();
		}
		return 0;
	}

	/**
	 * @return CacheObject des Knotens, vorher wird zum richtigen Datensatz navigiert
	 */
	public AbstractCacheObject getCacheObject() {
		if (!(getObject() instanceof AbstractCacheObject))
		{
			return null;
		}
		
		AbstractCacheObject co = (AbstractCacheObject) getObject();
		co.moveTo(getBookmark());
		return co;
	}
	

}
