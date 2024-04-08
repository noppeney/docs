package pze.business.objects.auswertung;

import java.util.Date;

import framework.business.interfaces.fields.IField;
import pze.business.Format;
import pze.business.UserInformation;
import pze.business.objects.personen.CoPerson;
import pze.business.objects.reftables.personen.CoStatusAktivInaktiv;

/**
 * CacheObject für die Einstellungen der Auswertung der Freigaben
 * 
 * @author Lisiecki
 *
 */
public class CoAuswertungFreigabe extends CoAuswertung {

	public static final String TABLE_NAME = "tblauswertungfreigaben";



	/**
	 * Kontruktor
	 */
	public CoAuswertungFreigabe() {
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
		
		// heutiges Datum, damit nicht alles geladen wird
		setDatumVon(new Date());
		
		return id;
	}
	

	public IField getFieldDatumFreigabe() {
		return getField("field." + getTableName() + ".datumfreigabe");
	}


	public boolean getDatumFreigabe() {
		return Format.getBooleanValue(getFieldDatumFreigabe().getValue());
	}


	/**
	 * Where-Teil des SQL-Statement für die Einschränkung der Person erstellen
	 * 
	 * Einschränkung auf die Personen, auf die man wirklich Zugriff hat.
	 * In Standard-Auswertungen (z. B. Urlaubsplanung) darf man auf seine ganze Abteilung... zugreifen
	 * 
	 * @param mitExternen externe Personen auch auswählen
	 * @throws Exception 
	 */
	@Override
	public String getWherePerson(boolean mitExternen) throws Exception {
		String where;
		CoPerson coPerson;
		
		coPerson = new CoPerson();

		where = super.getWherePerson(mitExternen);
		if (where == null)
		{
			where = "";
		}

		// Einschränkung auf die Personen, auf die man wirklich Zugriff hat.
		// In Standard-Auswertungen (z. B. Urlaubsplanung) darf man auf seine ganze Abteilung... zugreifen
		if (!UserInformation.getInstance().isPersonalansicht())
		{
			coPerson.loadByCurrentUser();
		}
		
		// Statement aus den ausgewählten und den nicht zugelassenen Personen zusammensetzen
		where += (coPerson != null && coPerson.getRowCount() > 0 ? " AND PersonID IN (" + coPerson.getIDs() + ")" : "");
		
		if (where.trim().startsWith("AND"))
		{
			where = where.substring(4);
		}

		return where.trim().isEmpty() ? null : where;
	}

}
