package pze.business.objects.reftables.projektverwaltung;

import framework.Application;
import pze.business.Format;
import pze.business.objects.AbstractCacheObject;
import pze.business.objects.projektverwaltung.CoAuftrag;

/**
 * CacheObject für Kunden von Aufträgen
 * 
 * @author Lisiecki
 *
 */
public class CoKunde extends AbstractCacheObject {

	private static final String TABLE_NAME = "rtblkunde";

	private static CoKunde m_instance = null;
	


	/**
	 * Kontruktor
	 */
	public CoKunde() {
		super("table." + TABLE_NAME);
	}
	

	/**
	 * Getter Instanz der Klasse
	 * 
	 * @return m_instance
	 */
	public static CoKunde getInstance() throws Exception {
		if (CoKunde.m_instance == null)
		{
			CoKunde.m_instance = new CoKunde();
			CoKunde.m_instance.loadAll();
		}
		
		return CoKunde.m_instance;
	}


	@Override
	public String getNavigationBitmap() {
		return "house";
	}


	/**
	 * Alle Kunden mit aktiven/inaktiven Projekten laden
	 * 
	 * @param statusProjektID
	 * @param checkProjektleiter Projektleiter muss geprüft werden oder alle anzeigen
	 * @throws Exception
	 */
	public void loadAllWithProjekte(int statusProjektID, boolean checkProjektleiter) throws Exception {
		emptyCache();
		Application.getLoaderBase().load(this, "ID IN (" + CoAuftrag.getSelectKundeID(statusProjektID, checkProjektleiter) + ")", getSortFieldName());
	}


	public String getKuerzel() {
		return Format.getStringValue(getField("field." + getTableName() + ".kuerzel").getValue());
	}


}
