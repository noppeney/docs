package pze.business.objects.reftables;

import framework.business.interfaces.fields.IField;
import pze.business.Format;
import pze.business.objects.AbstractCacheObject;

/**
 * CacheObject für Gründe für Sonderurlaub
 * 
 * @author Lisiecki
 *
 */
public class CoGrundSonderurlaub extends AbstractCacheObject {

	private static final String TABLE_NAME = "rtblgrundsonderurlaub";

	private static CoGrundSonderurlaub m_instance = null;
	


	/**
	 * Kontruktor
	 */
	private CoGrundSonderurlaub() {
		super("table." + TABLE_NAME);
	}
	

	/**
	 * Getter Instanz 
	 * 
	 * @return Instanz der Klasse 
	 */
	public static CoGrundSonderurlaub getInstance() throws Exception {
		if (CoGrundSonderurlaub.m_instance == null)
		{
			CoGrundSonderurlaub.m_instance = new CoGrundSonderurlaub();
			CoGrundSonderurlaub.m_instance.loadAll();
		}
		
		return CoGrundSonderurlaub.m_instance;
	}

	
	public int getAnzahlTage(int id) {
		if (!moveToID(id))
		{
			return 0;
		}
		
		return getAnzahlTage();
	}


	private IField getFieldAnzahlTage() {
		return getField("field." + getTableName() + ".anzahltage");
	}


	private int getAnzahlTage() {
		return Format.getIntValue(getFieldAnzahlTage());
	}


}
