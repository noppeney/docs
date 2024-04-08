package pze.business.objects.reftables.projektverwaltung;

import framework.Application;
import pze.business.Format;
import pze.business.objects.AbstractCacheObject;

/**
 * CacheObject für die Meldungen auf der Verletzerliste
 * 
 * @author Lisiecki
 *
 */
public class CoAusgabezeitraum extends AbstractCacheObject {

	private static final String TABLE_NAME = "rtblausgabezeitraum";

	public static final int ID_MONATLICH = 1;
	public static final int ID_VIERTELJAEHRLICH = 2;
	public static final int ID_HALBJAEHRLICH = 3;
	public static final int ID_JAEHRLICH = 4;
	

	private static CoAusgabezeitraum m_instance = null;
	


	/**
	 * Kontruktor
	 */
	public CoAusgabezeitraum() {
		super("table." + TABLE_NAME);
	}
	

	/**
	 * Getter Instanz der Klasse
	 * 
	 * @return m_instance
	 */
	public static CoAusgabezeitraum getInstance() throws Exception {
		if (CoAusgabezeitraum.m_instance == null)
		{
			CoAusgabezeitraum.m_instance = new CoAusgabezeitraum();
			CoAusgabezeitraum.m_instance.loadAll();
		}
		
		return CoAusgabezeitraum.m_instance;
	}

	
	public void loadForProjektbericht() throws Exception {
		emptyCache();
		Application.getLoaderBase().load(this, "ID IN (" + ID_MONATLICH + ", " + ID_VIERTELJAEHRLICH + ")", "OrderBy");
	}

	
	public int getAnzahlZeitraeume(){
		return Format.getIntValue(getField("field." + getTableName() + ".anzahlzeitraeume").getValue());
	}


	public int getAnzahlMonate(){
		return Format.getIntValue(getField("field." + getTableName() + ".anzahlmonate").getValue());
	}



}
