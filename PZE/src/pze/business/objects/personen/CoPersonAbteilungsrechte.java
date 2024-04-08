package pze.business.objects.personen;

import framework.Application;
import framework.business.interfaces.fields.IField;
import pze.business.Format;
import pze.business.objects.AbstractCacheObjectMitAuswahl_alt;
import pze.business.objects.reftables.CoFreigabeberechtigungen;

/**
 * CacheObject für die Zuordnung von Zugriffsrechten auf Abteilungen
 * 
 * @author Lisiecki
 *
 */
public class CoPersonAbteilungsrechte extends AbstractCacheObjectMitAuswahl_alt {

	public static final String TABLE_NAME = "stblpersonabteilungsrechte";


	/**
	 * Kontruktor
	 * @throws Exception 
	 */
	public CoPersonAbteilungsrechte() throws Exception {
		super("table." + TABLE_NAME);
		
		addField("virt.field.stblpersonabteilungsrechte.abteilungid");
	}
	

	/**
	 * Alle für die Person ausgewählten Abteilungen.<br>
	 * Select über 2 einzelne Unterabfragen, weil sonst die nicht ausgewählten Abteilungen nicht erkannt werden
	 * 
	 * @param personID
	 * @throws Exception 
	 */
	@Override
	public void loadByPersonID(int personID) throws Exception {
		loadByPersonID(personID, false);
	}


	/**
	 * Alle für die Person ausgewählten Abteilungen.<br>
	 * Select über 2 einzelne Unterabfragen, weil sonst die nicht ausgewählten Abteilungen nicht erkannt werden
	 * 
	 * @param personID
	 * @param mitVertreterrechten auch die Berechtigungen als Vertreter
	 * @throws Exception 
	 */
	public void loadByPersonID(int personID, boolean mitVertreterrechten) throws Exception {
		String sql;

		sql = "SELECT *, a.ID AS VirtAbteilungID FROM"
				+ " (SELECT * FROM "+ TABLE_NAME + " WHERE PersonID = " + personID 
				+ (mitVertreterrechten ? " OR PersonID IN (" + CoFreigabeberechtigungen.getSelectVertreterID(personID) + ")" : "")
				+ ") stbl"
				+ " RIGHT OUTER JOIN (SELECT * From rtblAbteilung WHERE Aktiv=1) a ON stbl.AbteilungID = a.ID" 
				+ " ORDER BY a.Bezeichnung";

		Application.getLoaderBase().load(this, sql);
		setIDs(personID);
	}


	/**
	 * Alle Rechte für die aktiven/inaktiven Personen 
	 * 
	 * @param statusAktivInaktivID
	 * @throws Exception 
	 */
	public void loadByStatusID(int statusAktivInaktivID) throws Exception { 
		String sql;
		
		sql = "SELECT * FROM " + getTableName() + " s JOIN tblPerson p ON (s.PersonID = p.ID) "
				+ "JOIN rtblAbteilung r ON (s.AbteilungID = r.ID) "
				+ "WHERE ausgewaehlt=1 AND statusAktivInaktivID = " + statusAktivInaktivID
				+ " ORDER BY Nachname, Vorname, r.Bezeichnung";

		Application.getLoaderBase().load(this, sql);
	}

	
	private IField getFieldAbteilungID() {
		return getField("field." + TABLE_NAME + ".abteilungid");
	}


	private int getVirtAbteilungID() {
		return Format.getIntValue(getField("virt.field.stblpersonabteilungsrechte.abteilungid").getValue());
	}

	
	private void setAbteilungID(int abteilungID) {
		getFieldAbteilungID().setValue(abteilungID);
	}

	
	@Override
	protected int getObjectID() throws Exception {
		return getVirtAbteilungID();
	}

	
	@Override
	protected void setObjectID(int objectID) {
		setAbteilungID(objectID);
	}


}
