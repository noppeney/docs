package pze.business.objects.reftables;

import pze.business.objects.AbstractCacheObject;


/**
 * Erweitert das AbstractCacheObject für die Referenztabellen
 * 
 * @author Lisiecki
 *
 */
public class ReftableCacheObject extends AbstractCacheObject {

	public ReftableCacheObject() {
		super();
	}

	public ReftableCacheObject(String tableResID) {
		super(tableResID);
	}
	
	
	/**
	 * Speichert das aktuelle Cacheobject. 
	 * Falls dabei ein Fehler auftritt, wird ein Rollback durchgeführt.
	 * 
	 */
	@Override
	public void save() throws Exception {
//		boolean  success = false;
		try
		{
//			ConnectionManager.getInstance().getConnection().begin();
			super.save();
//			ConnectionManager.getInstance().getConnection().commit();
//			success = true;
			
		}
		finally
		{
//			if (!success)
//				ConnectionManager.getInstance().getConnection().rollback();
		}
	}


}
