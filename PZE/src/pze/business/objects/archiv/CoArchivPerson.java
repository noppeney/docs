package pze.business.objects.archiv;

import framework.Application;
import pze.business.objects.personen.CoPerson;

/**
 * Klasse zum Laden der Archiv-Einträge für Personen
 * 
 * @author lisiecki
 */
public class CoArchivPerson extends CoPerson {


	/**
	 * Kontruktor
	 */
	public CoArchivPerson() {
		super("table." + CoPerson.TABLE_NAME);
	}
	
	
	/**
	 * Alle Personen laden
	 * 
	 * @throws Exception
	 */
	public void loadArchiv() throws Exception {
		emptyCache();
		Application.getLoaderBase().load(this, "SELECT * FROM ARCHIV" + CoPerson.TABLE_NAME + " ORDER BY " +  getSortFieldName());
	}


}
