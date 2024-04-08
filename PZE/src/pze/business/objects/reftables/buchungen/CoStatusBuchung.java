package pze.business.objects.reftables.buchungen;

import framework.Application;
import pze.business.UserInformation;
import pze.business.objects.AbstractCacheObject;
import pze.business.objects.personen.CoPerson;
import pze.business.objects.reftables.personen.CoPosition;

/**
 * CacheObject für Status von Buchungen (ok, geändert...)
 * 
 * @author Lisiecki
 *
 */
public class CoStatusBuchung extends AbstractCacheObject {

	private static final String TABLE_NAME = "rtblstatusbuchung";

	public static final int STATUSID_OK = 1;
	public static final int STATUSID_GEAENDERT = 2;
	public static final int STATUSID_UNGUELTIG = 3;
	public static final int STATUSID_VORLAEUFIG= 4;

	private static CoStatusBuchung m_instance = null;
	


	/**
	 * Kontruktor
	 */
	public CoStatusBuchung() {
		super("table." + TABLE_NAME);
	}
	

	/**
	 * Getter Instanz 
	 * 
	 * @return Instanz der Klasse 
	 */
	public static CoStatusBuchung getInstance() throws Exception {
		if (CoStatusBuchung.m_instance == null)
		{
			CoStatusBuchung.m_instance = new CoStatusBuchung();
			CoStatusBuchung.m_instance.loadAll();
		}
		
		return CoStatusBuchung.m_instance;
	}

	
	/**
	 * Alle Datensätze laden
	 * @throws Exception aus Loaderbase
	 */
	public void loadSelbstbuchungAenderungZulaessig(int id) throws Exception {
		CoPerson.getInstance().moveToID(UserInformation.getPersonID());
		emptyCache();
		// Azubis dürfen für Vorlesung auch OK-Buchungen erstellen
		Application.getLoaderBase().load(this, "ID = " + id + " OR ID =" + STATUSID_VORLAEUFIG
				// TODO Gleitzeitkorrekturbeleg, VL Wiebe, alle Stellen markiert
				+ (CoPerson.getInstance().getPositionID() == CoPosition.ID_AZUBI ? " OR ID =" + STATUSID_OK : ""), getSortFieldName());
	}
	

	/**
	 * Die Buchung mit dem übergebenen Status darf von der angemeldeten Person selbst geändert werden, da sie vorläufig ist
	 * 
	 * @return
	 * @throws Exception
	 */
	public static boolean isSelbstbuchungAenderungZulaessig(int id) throws Exception {
		return id == 0 || id == STATUSID_VORLAEUFIG; 
	}
	

}
