package pze.business.objects.auswertung;

import framework.Application;
import framework.business.interfaces.fields.IField;
import pze.business.objects.AbstractCacheObjectMitAuswahl;

/**
 * CacheObject für die Tabelle zur Auswahl der Abteilungsleiter bei der Ampelliste
 * 
 * @author Lisiecki
 *
 */
public class CoAuswertungAmpellisteProjektleiter extends AbstractCacheObjectMitAuswahl { // TODO Klasse momentan nicht verwendet

	public static final String TABLE_NAME = "stblampellisteprojektleiter";
	

	/**
	 * Kontruktor
	 * 
	 * @throws Exception 
	 */
	public CoAuswertungAmpellisteProjektleiter() throws Exception {
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

		sql = "SELECT tbl.ID AS ID, tbl.AuswertungID AS AuswertungID, tbl.IstAusgewaehlt AS IstAusgewaehlt, rtbl.ID AS ProjektleiterID FROM"
				+ " (SELECT * FROM "+ TABLE_NAME +" WHERE AuswertungID = " + auswertungID + " OR AuswertungID IS NULL) tbl "
				+ " RIGHT OUTER JOIN"
				+ " (SELECT * From tblperson "
				+ " WHERE ID IN (SELECT ProjektleiterID FROM tblAuftrag ) OR ID IN (SELECT ProjektleiterID FROM tblAbruf)"
				+ " WHERE ID IN (SELECT ProjektleiterID2 FROM tblAuftrag ) OR ID IN (SELECT ProjektleiterID2 FROM tblAbruf)"
				+ ") rtbl"
				// TODO ggf. Status berücksichtigen
				+ " ON tbl.ProjektleiterID = rtbl.ID" + " ORDER BY rtbl.Nachname, rtbl.Vorname";

		Application.getLoaderBase().load(this, sql);
		init(auswertungID);
	}


	@Override
	protected IField getFieldRefTableObjectID() {
		return getField("field." + getTableName() + ".projektleiterid");
	}



}
