package pze.business.objects.projektverwaltung;

import java.util.HashSet;
import java.util.Set;

import framework.Application;
import framework.business.interfaces.fields.IField;
import pze.business.Format;
import pze.business.objects.AbstractCacheObject;
import pze.business.objects.reftables.projektverwaltung.CoStatusGueltigUngueltig;

/**
 * CacheObject für die Kostenstellen
 * 
 * @author Lisiecki
 *
 */
public class CoKostenstelle extends AbstractCacheObject {

	public static final String TABLE_NAME = "tblkostenstelle";



	/**
	 * Kontruktor
	 */
	public CoKostenstelle() {
		super("table." + TABLE_NAME);
	}
	

	/**
	 * Alle Kostenstellen des Kunden und die übergebenen laden
	 * 
	 * @param kundeID
	 * @param ids Liste von IDs als Komma-getrennter String
	 * @throws Exception
	 */
	public void loadByKundeID(int kundeID, String ids) throws Exception {
		emptyCache();
		Application.getLoaderBase().load(this, "ID IN (" + ids + ") "
				+ "OR (KundeID=" + kundeID + " AND StatusGueltigUnGueltigID=" + CoStatusGueltigUngueltig.STATUSID_GUELTIG + ")", 
				getSortFieldName());
	}


	/**
	 * Alle Kostenstellen des Abrufs laden. 
	 * Hier muss der Status nicht abgefragt werden, weil man wirklich alle für den Abruf braucht.
	 * 
	 * @param abrufID
	 * @throws Exception
	 */
	public void loadByAbrufID(int abrufID) throws Exception {
		emptyCache();
		Application.getLoaderBase().load(this, "ID IN (SELECT KostenstelleID FROM stblAbrufKostenstelle WHERE AbrufID=" + abrufID + ")"
				, getSortFieldName());
	}


	/**
	 * Alle Kostenstellen des Auftrags laden. 
	 * Hier muss der Status nicht abgefragt werden, weil man wirklich alle für den Auftrag braucht.
	 * 
	 * @param auftragID
	 * @throws Exception
	 */
	public void loadByAuftragID(int auftragID) throws Exception {
		emptyCache();
		Application.getLoaderBase().load(this, "ID IN (SELECT KostenstelleID FROM stblAuftragKostenstelle WHERE AuftragID=" + auftragID + ")"
				, getSortFieldName());
	}


	/**
	 * Doppelte Einträge mit gleicher Bezeichnung für den Kunden und unterschiedlicher ID laden.
	 * 
	 * @param id
	 * @param bezeichnung
	 * @param kundeID
	 * @throws Exception
	 */
	private void loadDoppelteEintraege(int id, String bezeichnung, int kundeID) throws Exception {
		emptyCache();
		Application.getLoaderBase().load(this, "ID<>" + id + " AND KundeID=" + kundeID + " AND Bezeichnung='" + bezeichnung + "'", getSortFieldName());
	}
	

	/**
	 * Laden der Items von Kostenstellen mit verschiedenen Filtermöglichkeiten
	 * 
	 * @param kundeID kundeID oder 0
	 * @param auftragID auftragID oder 0 
	 * @param abrufID abrufID oder 0
	 * @throws Exception
	 */
	public void loadItems(int kundeID, int auftragID, int abrufID, int berichtsNr) throws Exception {
		String from, where, sql;


		// FROM, WHERE bestimmen
		from = " FROM " + getTableName();
		where = " WHERE StatusGueltigUnGueltigID=" + CoStatusGueltigUngueltig.STATUSID_GUELTIG;


		// über kundeID
		if (kundeID != 0)
		{
			from += " JOIN rtblKunde k ON (" + TABLE_NAME + ".KundeID = k.ID) ";
			where += " AND k.ID=" + kundeID;
		}

		if (berichtsNr != 0) // über BerichtsNr zuerst, falls eine ausgewählt ist
		{
			from += " JOIN tblBerichtsNr b ON (" + TABLE_NAME + ".ID = b.KostenstelleID)";  
			where += " AND b.ID=" + berichtsNr;
		}
		else if (abrufID != 0)// über Abruf
		{
			from += " JOIN stblAbrufKostenstelle sab ON (" + TABLE_NAME + ".ID = sab.KostenstelleID)";  
			where += " AND sab.AbrufID=" + abrufID;
		}
		else if (auftragID != 0)// über Auftrag, wenn kein Abruf angegeben ist
		{
			from += " JOIN stblAuftragKostenstelle sau ON (" + TABLE_NAME + ".ID = sau.KostenstelleID)";  
			where += " AND sau.AuftragID=" + auftragID;
		}

		// SQL-Statement
		sql = "SELECT " + TABLE_NAME + ".ID, " + TABLE_NAME + ".Bezeichnung"  + from + where
				+ " ORDER BY " + getSortFieldName();

		emptyCache();
		Application.getLoaderBase().load(this, sql);
		
		// wenn für den Auftrag keine Kostenstelle angegeben ist, lade die für Kunde oder Abruf
//		if (auftragID != 0 && getRowCount() == 0)
//		{
//			loadItems(kundeID, 0, abrufID);
//		}
	}
	

	/**
	 * Laden der Items von Kostenstellen für die KGG-Stundenauswertung
	 * 
	 * @throws Exception
	 */
	public void loadItemsKGG() throws Exception {
		String from, where, sql;
		
		
		// FROM
		from = " FROM " + getTableName();

		// über Abruf und Kostenstelle
		from += " JOIN tblBerichtsNr b ON (" + TABLE_NAME + ".ID = b.KostenstelleID)";  

		
		// WHERE
		where = ""
				+ " AND StatusGueltigUngueltigID = " + CoStatusGueltigUngueltig.STATUSID_GUELTIG
//				+ " AND " + TABLE_NAME + ".StatusID = " + CoStatusProjekt.STATUSID_LAUFEND
				;

		if (where.length() > 0)
		{
			where = " WHERE " + where.substring(4);
		}

		
		// SQL-Statement, getSortFieldName() muss im SELECT wegen DISTINCT angegeben werden (SQL-Regel)
//		sql = "SELECT DISTINCT " + TABLE_NAME + ".ID, (AuftragsNr + ', ' + ISNULL(" + TABLE_NAME + ".Beschreibung, '')) AS AuftragsNr, " + getSortFieldName() 
		sql = "SELECT DISTINCT " + TABLE_NAME + ".*"
		+ from + where
		+ " ORDER BY " + getSortFieldName();
		
		emptyCache();
		Application.getLoaderBase().load(this, sql);
	}


	public IField getFieldKundeID(){
		return getField("field." + getTableName() + ".auftragid");
	}
	
	
	public int getKundeID(){
		return Format.getIntValue(getFieldKundeID().getValue());
	}
	
	

	public static String getResIdPSP(){
		return "field." + TABLE_NAME + ".psp";
	}
	

//	public void setKundeID(int auftragID){
//		getFieldKundeID().setValue(auftragID);
//	}
	

//	public IField getFieldIDBudgetJahresweise() {
//		return getField("field." + getTableName() + ".idbudgetjahresweise");
//	}
//
//
//	public int getIDBudgetJahresweise() {
//		return Format.getIntValue(getFieldIDBudgetJahresweise().getValue());
//	}
//
//
//	public void setIDBudgetJahresweise(int id) {
//		getFieldIDBudgetJahresweise().setValue(id);
//	}


	/**
	 * Neuen Eintrag für die Person anlegen
	 * 
	 * @param auftragID
	 * @return
	 * @throws Exception
	 */
//	public int createNew(int auftragID) throws Exception {
//		int id = super.createNew();
//		
//		setAuftragID(auftragID);
//		
//		return id;
//	}
	
	
	/**
	 * Prüft, dass keine Kostenstelle doppelt vorkommt
	 */
	@Override
	public String validate() throws Exception{
		int kundeID;
		String bezeichnung, key;
		Set<String> setKundeBezeichnung;
		CoKostenstelle coKostenstelle;
		
		if (!moveFirst())
		{
			return null;
		}

		setKundeBezeichnung = new HashSet<String>();
		coKostenstelle = new CoKostenstelle();
		
		// alle Einträge prüfen
		do
		{
			if (!isNew() && !isModified())
			{
				continue;
			}
			
			// prüfen, ob der Eintrag doppelt in der aktuellen Liste existiert
			bezeichnung = getBezeichnung();
			kundeID = getKundeID();
			key = bezeichnung + "-" + kundeID;
			
			// prüfen, ob der Eintrag nochmal in der DB existiert
			coKostenstelle.loadDoppelteEintraege(getID(), bezeichnung, kundeID);
			if (coKostenstelle.getRowCount() > 0 || setKundeBezeichnung.contains(key))
			{
				return "Die Kostenstelle \"" + bezeichnung + "\" existiert bereits.";
			}
			
			setKundeBezeichnung.add(key);
		} while (moveNext());
		
		return null;
	}

}
