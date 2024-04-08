package pze.business.objects;

import framework.Application;

/**
 * CacheObject für Dokumente eines beliebigen Objektes, z. B. eines Auftrags.
 * 
 * @author Lisiecki
 *
 */
public class CoDokumente extends AbstractCacheObject {

	/**
	 * Konstruktor
	 */
	public CoDokumente() {
		super("table.tbldokumente");
		addField("field.tbldokumente.dateiname.pure");
	}
	

	/**
	 * Alle Dokumente eines Objekts laden, z. B. einer Person.
	 * 
	 * @param objektid Objekt-ID
	 * @throws Exception aus Loaderbase
	 */
	public void loadByObjekt(int objektid) throws Exception {
		Application.getLoaderBase().load(this, "objektid=" + objektid, getSortFieldName());				
//		Application.getLoaderBase().load(this, "objektid=" + objektid + " and statusid=" + PZEStartupAdapter.STATUS_NICHT_GELOESCHT, "Beschreibung");				
	}
	
	
	/**
	 * neuen Datensatz erzeugen
	 * 
	 * @param objektID ObjektID
	 * @throws Exception 
	 */
	public int createNew(int objektID) throws Exception {
		int id = super.createNew();
		getField("field.tbldokumente.objektid").setValue(objektID);
		
		// Status 'nicht gelöscht'
//		setStatusNichtGeloescht();
		
		return id;
	}
	
	
	/**
	 * 
	 * @return Dateiname
	 */
	public String getDateiname(){
		return getField("field.tbldokumente.dateiname").getStringValue();
	}
	

	/**
	 * Sortiert nach Beschreibung
	 * 
	 * (non-Javadoc)
	 * @see pze.business.objects.AbstractCacheObject#getSortFieldName()
	 */
	@Override
	protected String getSortFieldName() {
		return "Beschreibung";
	}
	

}
