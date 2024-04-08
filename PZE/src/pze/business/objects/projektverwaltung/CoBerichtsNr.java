package pze.business.objects.projektverwaltung;

import java.util.HashSet;
import java.util.Set;

import framework.Application;
import framework.business.interfaces.fields.IField;
import pze.business.Format;
import pze.business.objects.AbstractCacheObject;

/**
 * CacheObject für die BerichtsNr
 * 
 * @author Lisiecki
 *
 */
public class CoBerichtsNr extends AbstractCacheObject {

	public static final String TABLE_NAME = "tblberichtsnr";



	/**
	 * Kontruktor
	 */
	public CoBerichtsNr() {
		super("table." + TABLE_NAME);
	}
	

//	/**
//	 * Alle Kostenstellen des Kunden und die übergebenen laden
//	 * 
//	 * @param kundeID
//	 * @param ids Liste von IDs als Komma-getrennter String
//	 * @throws Exception
//	 */
//	public void loadByKundeID(int kundeID, String ids) throws Exception {
//		emptyCache();
//		Application.getLoaderBase().load(this, "ID IN (" + ids + ") "
//				+ "OR (KundeID=" + kundeID + " AND StatusGueltigUnGueltigID=" + CoStatusGueltigUngueltig.STATUSID_GUELTIG + ")", 
//				getSortFieldName());
//	}
//
//
//	/**
//	 * Alle Kostenstellen des Abrufs laden. 
//	 * Hier muss der Status nicht abgefragt werden, weil man wirklich alle für den Abruf braucht.
//	 * 
//	 * @param abrufID
//	 * @throws Exception
//	 */
//	public void loadByAbrufID(int abrufID) throws Exception {
//		emptyCache();
//		Application.getLoaderBase().load(this, "ID IN (SELECT KostenstelleID FROM stblAbrufKostenstelle WHERE AbrufID=" + abrufID + ")"
//				, getSortFieldName());
//	}
//
//
//	/**
//	 * Alle Kostenstellen des Auftrags laden. 
//	 * Hier muss der Status nicht abgefragt werden, weil man wirklich alle für den Auftrag braucht.
//	 * 
//	 * @param auftragID
//	 * @throws Exception
//	 */
//	public void loadByAuftragID(int auftragID) throws Exception {
//		emptyCache();
//		Application.getLoaderBase().load(this, "ID IN (SELECT KostenstelleID FROM stblAuftragKostenstelle WHERE AuftragID=" + auftragID + ")"
//				, getSortFieldName());
//	}


	/**
	 * Alle Kostenstellen des Auftrags laden. 
	 * Hier muss der Status nicht abgefragt werden, weil man wirklich alle für den Auftrag braucht.
	 * 
	 * @param kostenstelleID
	 * @throws Exception
	 */
	public void loadByKostenstelleID(int kostenstelleID) throws Exception {
		emptyCache();
		Application.getLoaderBase().load(this, "KostenstelleID IN (" + kostenstelleID + ")", getSortFieldName());
	}


	/**
	 * Doppelte Einträge mit gleicher Bezeichnung für die Kostenstelle und unterschiedlicher ID laden.
	 * 
	 * @param id
	 * @param bezeichnung
	 * @param kostenstelleID
	 * @throws Exception
	 */
	private void loadDoppelteEintraege(int id, String bezeichnung, int kostenstelleID) throws Exception {
		emptyCache();
		Application.getLoaderBase().load(this, "ID<>" + id + " AND KostenstelleID=" + kostenstelleID + " AND Bezeichnung='" + bezeichnung + "'", getSortFieldName());
	}
	

	/**
	 * Laden der Items von Kostenstellen mit verschiedenen Filtermöglichkeiten
	 * 
	 * @param kundeID kundeID oder 0
	 * @param auftragID auftragID oder 0 
	 * @param abrufID abrufID oder 0
	 * @param kostenstelleID 
	 * @throws Exception
	 */
	public void loadItems(int kundeID, int auftragID, int abrufID, int kostenstelleID) throws Exception {
//		String from, where, sql;

		if (kostenstelleID > 0)
		{
			loadByKostenstelleID(kostenstelleID);
		}
		else if (auftragID == 0 && abrufID == 0)
		{
			loadAll();
		}
		else
		{
			
		}
		
		// FROM, WHERE bestimmen
//		from = " FROM " + getTableName();
//		where = " WHERE StatusGueltigUnGueltigID=" + CoStatusGueltigUngueltig.STATUSID_GUELTIG;
//
//
//		// über kundeID
//		if (kundeID != 0)
//		{
//			from += " JOIN rtblKunde k ON (" + TABLE_NAME + ".KundeID = k.ID) ";
//			where += " AND k.ID=" + kundeID;
//		}
//
//		if (abrufID != 0)// über Abruf
//		{
//			from += " JOIN stblAbrufKostenstelle sab ON (" + TABLE_NAME + ".ID = sab.KostenstelleID)";  
//			where += " AND sab.AbrufID=" + abrufID;
//		}
//		else if (auftragID != 0)// über Auftrag, wenn kein Abruf angegeben ist
//		{
//			from += " JOIN stblAuftragKostenstelle sau ON (" + TABLE_NAME + ".ID = sau.KostenstelleID)";  
//			where += " AND sau.AuftragID=" + auftragID;
//		}
//
//		// SQL-Statement
//		sql = "SELECT " + TABLE_NAME + ".ID, " + TABLE_NAME + ".Bezeichnung"  + from + where
//				+ " ORDER BY " + getSortFieldName();
//
//		emptyCache();
//		Application.getLoaderBase().load(this, sql);
		
		// wenn für den Auftrag keine Kostenstelle angegeben ist, lade die für Kunde oder Abruf
//		if (auftragID != 0 && getRowCount() == 0)
//		{
//			loadItems(kundeID, 0, abrufID);
//		}
	}
	

	private IField getFieldKostenstelleID(){
		return getField("field." + getTableName() + ".kostenstelleid");
	}
	
	
	private int getKostenstelleID(){
		return Format.getIntValue(getFieldKostenstelleID().getValue());
	}
	
	
	/**
	 * Prüft, dass keine Kostenstelle doppelt vorkommt
	 */
	@Override
	public String validate() throws Exception{
		int kostenstelleID;
		String bezeichnung, key;
		Set<String> setKostenstelleBezeichnung;
		CoBerichtsNr coKostenstelle;
		
		if (!moveFirst())
		{
			return null;
		}

		setKostenstelleBezeichnung = new HashSet<String>();
		coKostenstelle = new CoBerichtsNr();
		
		// alle Einträge prüfen
		do
		{
			if (!isNew() && !isModified())
			{
				continue;
			}
			
			// prüfen, ob der Eintrag doppelt in der aktuellen Liste existiert
			bezeichnung = getBezeichnung();
			kostenstelleID = getKostenstelleID();
			key = bezeichnung + "-" + kostenstelleID;
			
			// prüfen, ob der Eintrag nochmal in der DB existiert
			coKostenstelle.loadDoppelteEintraege(getID(), bezeichnung, kostenstelleID);
			if (coKostenstelle.getRowCount() > 0 || setKostenstelleBezeichnung.contains(key))
			{
				return "Die berichts-Nr. \"" + bezeichnung + "\" existiert bereits.";
			}
			
			setKostenstelleBezeichnung.add(key);
		} while (moveNext());
		
		return null;
	}

}
