package pze.database;

import java.util.ArrayList;

import framework.business.interfaces.data.IBusinessObject;

/**
 * Erweitert die FW-Klasse Loaderbase
 * 
 * @author Lisiecki
 *
 */
public class LoaderBase extends framework.database.cacheobject.LoaderBase {
	
	@SuppressWarnings("serial")
	private static final ArrayList<String> REFTABLES_MIT_ORDERBY_SPALTE = new ArrayList<String>(){{
		add("rtblgrundaenderungbuchung");
		add("rtblgrundaenderungkontowert");
		add("rtblstatusprojekt");
		add("rtblausgabezeitraum");
	}};

	
	
	/**
	 * Laden eines Cacheobjectes mit voreingestellter Sortierung nach der zweiten Spalte, da in der ersten immer die fortlaufende ID steht
	 * 
	 * @see framework.database.cacheobject.LoaderBase#load_indexed(framework.business.interfaces.data.IBusinessObject, java.lang.String, java.lang.String)
	 */
	@Override
	public void load_indexed(IBusinessObject ob, String sql, String connectionid) throws Exception {
		
		// sql-Statement für Referenztabellen ändern, um die Sortierung anzupassen
		sql = updateSqlForRefTables(ob, sql);
		
//		System.out.println("Loaderbase.load_indexed: " + ob.getResID() + "   -> " + sql);

		// wenn nach der ersten Spalte sortiert werden soll, ersetze das Sortierkriterium durch die Spalte
		if (sql != null)
		{
			sql = sql.replace(" order by 1 asc",  " order by 2 asc");
		}
//		System.out.println("                    neu: " + ob.getResID() + "   -> " + sql);

		super.load_indexed(ob, sql, connectionid);
	}


	/**
	 * Laden eines Cacheobjectes
	 * 
	 * @see framework.database.cacheobject.LoaderBase#load(framework.business.interfaces.data.IBusinessObject, java.lang.String, java.lang.String)
	 */
	public void load(IBusinessObject ob, String where, String order) throws Exception{
		
		// Benutzer sollen nach dem Loginname, statt dem Benutzername sortiert werden
		if (ob.getResID().equals("table.tblextusers"))
		{
			order = "loginname";
		}
		
		super.load(ob, where, order);
//		System.out.println(ob.getResID() + "  ->  " + order);
	}

	
	private String updateSqlForRefTables(IBusinessObject ob, String sql) {
		String tableResID;
		
		tableResID = ob.getResID();
		
		if (REFTABLES_MIT_ORDERBY_SPALTE.contains(tableResID))
		{
			sql = sql.substring(0, sql.indexOf("order by")) + "ORDER BY orderby";
		}
		
		// bei Personen den kompletten Namen als Bezeichnung verwenden
		if (tableResID.equals("tblperson"))
		{
			sql = sql.replace("tblperson.Nachname", "Nachname + CASE WHEN Vorname IS NULL THEN '' ELSE ', ' + Vorname END AS Name");
		}
		else if (tableResID.equals("rtblanfordererkunde"))
		{
			sql = sql.replace("rtblanfordererkunde.Nachname", "Nachname + CASE WHEN Vorname IS NULL THEN '' ELSE ', ' + Vorname END AS Name");
		}
		// nur aktive Abteilungen
		else if (tableResID.equals("rtblabteilung"))
		{
			sql = sql.replace("order by", " where aktiv=1 order by");
		}
		// bei Aufträgen die Auftragsnummer mit Bezeichnung anzeigen
		else if (tableResID.equals("tblauftrag"))
		{
			sql = sql.replace("tblauftrag.Beschreibung", "(AuftragsNr + ', ' + Beschreibung) AS Beschreibung");
		}
			
		return sql;
	}
	
	// "RefTableLoader"
	// man könnte auf instance of co... abfragen und daher getSortFieldName() holen
	
	// AdminUsersNavigationForm createNavigationNode -> Laden der Benutzerverwaltung
}
