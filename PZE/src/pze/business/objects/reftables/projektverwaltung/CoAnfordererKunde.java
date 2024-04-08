package pze.business.objects.reftables.projektverwaltung;

import framework.Application;
import pze.business.Format;
import pze.business.objects.AbstractCacheObject;

/**
 * CacheObject für Anforderer von Aufträgen
 * 
 * @author Lisiecki
 *
 */
public class CoAnfordererKunde extends AbstractCacheObject {

	private static final String TABLE_NAME = "rtblanfordererkunde";



	/**
	 * Kontruktor
	 */
	public CoAnfordererKunde() {
		super("table." + TABLE_NAME);
	}
	

	/**
	 * Alle Anforderer der Kunden laden.
	 * 
	 * @param kundeID
	 * @param id diese ID muss auch mit geladen werden
	 * @throws Exception
	 */
	public void loadByKundeID(int kundeID, int id) throws Exception {
		String sql;

		sql = "SELECT a.ID, (Nachname + ', ' + ISNULL(Vorname, '')) AS Nachname" + ""
				+ " FROM " + getTableName() + " AS a WHERE ID = " + id + " OR (kundeID=" + kundeID 
				+ " AND StatusGueltigUnGueltigID=" + CoStatusGueltigUngueltig.STATUSID_GUELTIG + ")"
				+ " ORDER BY " + getSortFieldName();
		
		emptyCache();
		Application.getLoaderBase().load(this, sql);
	}


	/**
	 * Nachname, Vorname
	 * 
	 * (non-Javadoc)
	 * @see pze.business.objects.AbstractCacheObject#getBezeichnung()
	 */
	@Override
	public String getBezeichnung() {
		return getNachname() + ", " + getVorname();
	}


	/**
	 * Nach Nachname, Vorname sortieren
	 * 
	 * (non-Javadoc)
	 * @see pze.business.objects.AbstractCacheObject#getSortFieldName()
	 */
	@Override
	protected String getSortFieldName() {
		return "Nachname, Vorname";
	}
	

	/**
	 * Name, zusammengesetzt aus "Vorname Nachname"
	 * @return
	 */
	public String getName() {
		return getVorname() + " " + getNachname();
	}


	/**
	 * Name, zusammengesetzt aus "Nachname, Vorname"
	 * @return
	 */
	public String getNachnameVorname() {
		return getNachname() + ", " + getVorname();
	}


	public String getVorname() {
		return Format.getStringValue(getField("field." + getTableName() + ".vorname").getValue());
	}


	public String getNachname() {
		return Format.getStringValue(getField("field." + getTableName() + ".nachname").getValue());
	}


}
