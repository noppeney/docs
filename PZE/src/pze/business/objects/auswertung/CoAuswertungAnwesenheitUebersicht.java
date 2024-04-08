package pze.business.objects.auswertung;

import pze.business.objects.personen.CoPerson;
import pze.business.objects.reftables.personen.CoStatusAktivInaktiv;

/**
 * CacheObject für die Einstellungen der Auswertung Anwesenheitsübersicht
 * 
 * @author Lisiecki
 *
 */
public class CoAuswertungAnwesenheitUebersicht extends CoAuswertung {

	public static final String TABLE_NAME = "tblauswertunganwesenheituebersicht";



	/**
	 * Kontruktor
	 */
	public CoAuswertungAnwesenheitUebersicht() {
		super(TABLE_NAME);
	}
	
	
	/**
	 * bei neuen Einträgen aktuelles Jahr und die aktuelle Person eintragen
	 * 
	 * @see pze.business.objects.AbstractCacheObject#createNew()
	 */
	@Override
	public int createNew(int userID) throws Exception	{
		int id;
		
		id = super.createNew(userID);
		
		// aktive Personen
		setStatusAktivInaktiv(CoStatusAktivInaktiv.STATUSID_AKTIV);
		
		return id;
	}
	

	/**
	 * Für die Anwesenheit dürfen alle Personen ausgewertet werden, deshalb wird die Funktion überschrieben
	 * 
	 * @return
	 * @throws Exception
	 */
	@Override
	public CoPerson getCoPersonByAuswahl() throws Exception {
		int abteilungID;
		int personID;
		int personenlisteID;
		CoPerson coPerson;

		abteilungID = getAbteilungID();
		personID = getPersonID();
		personenlisteID = getPersonenlisteID();
		
		coPerson = new CoPerson();
		
		
		if (abteilungID > 0)
		{
			coPerson.loadByAbteilungID(abteilungID);
			
			// wenn keine MA in der Abteilung sind darf kein leeres CO zurückgegeben werden, da sonst alle MA geladen werden
			if (coPerson.hasNoRows())
			{
				coPerson.createNew();
			}
		}
		else if (personID > 0)
		{
			coPerson.loadByID(personID);
		}
		else if (personenlisteID > 0)
		{
			coPerson.loadByPersonenlisteID(personenlisteID);
		}
		else // wenn keine Einschränkung der Personen gewählt ist, alle Personen für die man Berechtigungen besitzt
		{
			// für diese Auswertung dürfen alle Personen ausgewertet werden
		}
		
		return coPerson;
	}


}
