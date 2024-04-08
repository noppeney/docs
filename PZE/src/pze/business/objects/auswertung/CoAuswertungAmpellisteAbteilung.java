package pze.business.objects.auswertung;

import framework.Application;
import framework.business.interfaces.fields.IField;
import pze.business.objects.AbstractCacheObjectMitAuswahl;

/**
 * CacheObject für die Tabelle zur Auswahl der Gruppen bei der Ampelliste
 * 
 * @author Lisiecki
 *
 */
public class CoAuswertungAmpellisteAbteilung extends AbstractCacheObjectMitAuswahl {

	public static String TABLE_NAME = "stblampellisteabteilung";
	

	/**
	 * Kontruktor
	 * 
	 * @throws Exception 
	 */
	public CoAuswertungAmpellisteAbteilung() throws Exception {
		super("table." + TABLE_NAME);
	}
	

	/**
	 * Alle für die Auswertung ausgewählten Gruppen.<br>
	 * Select über 2 einzelne Unterabfragen, weil sonst die bereits ausgewählten Gruppen nicht erkannt werden
	 * 
	 * @param auswertungID
	 * @throws Exception 
	 */
	public void loadByAuswertungID(int auswertungID) throws Exception {
		String sql;

		sql = "SELECT tbl.ID AS ID, tbl.AuswertungID AS AuswertungID, tbl.IstAusgewaehlt AS IstAusgewaehlt, rtbl.ID AS AbteilungID FROM"
				+ " (SELECT * FROM "+ TABLE_NAME +" WHERE AuswertungID = " + auswertungID + " OR AuswertungID IS NULL) tbl "
				+ " RIGHT OUTER JOIN"
				+ " (SELECT * From rtblAbteilung) rtbl"
				+ " ON tbl.AbteilungID = rtbl.ID" + " ORDER BY rtbl.Bezeichnung";

		Application.getLoaderBase().load(this, sql);
		init(auswertungID);
	}


	@Override
	protected IField getFieldRefTableObjectID() {
		return getField("field." + getTableName() + ".abteilungis");
	}



}
