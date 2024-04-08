package pze.business.objects.reftables.personen;

import framework.Application;
import pze.business.UserInformation;
import pze.business.objects.AbstractCacheObject;
import pze.business.objects.personen.CoPerson;

/**
 * CacheObject für Abteilungen
 * 
 * @author Lisiecki
 *
 */
public class CoAbteilung extends AbstractCacheObject {

	private static final String TABLE_NAME = "rtblabteilung";

	private static CoAbteilung m_instance = null;
	
//	public static final int ID_VERWALTUNG = 1;
//	public static final int ID_BERECHNUNGEN = 2;
//	public static final int ID_ENTSORGUNG = 3;
//	public static final int ID_ANLAGENPLANUNG = 4;
	public static final int ID_KL = 5;
//	public static final int ID_TL = 6;
	public static final int ID_GESCHAEFTSFUEHRUNG = 7;
	public static final int ID_VERWALTUNG = 8;
	public static final int ID_TECH_BERECHNUNGEN = 9;
	public static final int ID_NUK_BERECHNUNGEN = 10;
	public static final int ID_BAUPLANUNG = 11;
	public static final int ID_ENTSORGUNGSPLANUNG = 12;
	public static final int ID_RUECKBAUPLANUNG = 13;



	/**
	 * Kontruktor
	 */
	public CoAbteilung() {
		super("table." + TABLE_NAME);
	}
	

	/**
	 * Getter Instanz 
	 * 
	 * @return Instanz der Klasse 
	 */
	public static CoAbteilung getInstance() throws Exception {
		if (CoAbteilung.m_instance == null)
		{
			CoAbteilung.m_instance = new CoAbteilung();
			CoAbteilung.m_instance.loadAll();
		}
		
		return CoAbteilung.m_instance;
	}


	/**
	 * CO für den aktuellen User laden<br>
	 * Entscheidend ist hier die Abteilungszuordnung, nicht die Benutzergruppe (z. B. sind Personal/Admin ohne Abteilungszuordnung)
	 * 
	 * @throws Exception
	 */
	public void loadByCurrentUser() throws Exception {
		CoPerson coPerson;
		
		coPerson = new CoPerson();
		coPerson.loadByID(UserInformation.getPersonID());
		
		loadByIDs(coPerson.getCoPersonAbteilungsrechte(true).getSelectedIDs(), true);
	}


	/**
	 * CO für die übergebenen Abteilungen laden
	 * 
	 * @param abteilungIDs AbteilungIDs mit Komma getrennt
	 * @param eigeneAbteilung auch die Abteilung der Person
	 * @throws Exception
	 */
	private void loadByIDs(String abteilungIDs, boolean eigeneAbteilung) throws Exception {
		int abteilungID;
		String where;
		CoPerson coPerson;
		
		emptyCache();

		// Abteilungen geladen
		if (abteilungIDs != null)
		{
			where = "ID  IN (" + abteilungIDs + ")";

			// ggf. zusätzlich die eigene
			if (eigeneAbteilung)
			{
				coPerson =  CoPerson.getInstance();
				coPerson.moveToID(UserInformation.getPersonID());
				
				abteilungID = coPerson.getAbteilungID();
				if (abteilungID > 0)
				{
					where += " OR ID = " + abteilungID;
				}
			}

			Application.getLoaderBase().load(this, where, getSortFieldName());
		}
		// ohne Berechtigungen nur die eigene Abteilung laden
		else if (eigeneAbteilung)
		{
			coPerson =  CoPerson.getInstance();
			coPerson.moveToID(UserInformation.getPersonID());
			
			abteilungID = coPerson.getAbteilungID();
			if (abteilungID > 0)
			{
				where = " ID = " + abteilungID;
				Application.getLoaderBase().load(this, where, getSortFieldName());
			}
		}
			
	}


}
