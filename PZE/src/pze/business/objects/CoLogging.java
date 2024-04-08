package pze.business.objects;

import framework.Application;

/**
 * CacheObject für Logging-Daten
 * 
 * @author Lisiecki
 *
 */
public class CoLogging extends AbstractCacheObject {

	private static String RESID = "table.stblprotokoll";

	/**
	 * Kontruktor
	 */
	public CoLogging() {
		super(RESID);
	}


	/**
	 * Alle Daten einer Person laden
	 * 
	 * @param personID
	 * @throws Exception
	 */
	public void loadByPerson(int personID) throws Exception {
		String where;

		// alle Tabellen mit Personeninformationen zusammenfügen
		where = "(tabelle='tblPerson' AND schluessel='" + personID + "') OR ";
		where += "(tabelle='stblPersonFirma' AND schluessel IN (SELECT ID FROM stblPersonFirma WHERE PersonID=" + personID + ")) OR ";
		where += "(tabelle='tblUntersuchung' AND schluessel IN (SELECT UntersuchungID FROM tblUntersuchung WHERE PersonID=" + personID + ")) OR ";
		where += "(tabelle='tblUnterweisung' AND schluessel IN (SELECT UnterweisungID FROM tblUnterweisung WHERE PersonID=" + personID + ")) OR ";
		where += "(tabelle='tblDokumente' AND schluessel IN (SELECT DokumentID FROM tblDokumente WHERE ObjektID=" + personID + "))";

		Application.getLoaderBase().load(this, where, "datumaenderung");		
	}


	/**
	 * Alle Daten einer Firma laden
	 * 
	 * @param firmaID
	 * @throws Exception
	 */
	public void loadByFirma(int firmaID) throws Exception {
		String where;

		where = "(tabelle='tblFirma' AND schluessel='" + firmaID + "')";

		Application.getLoaderBase().load(this, where, "datumaenderung");		
	}


	/**
	 * Alle Daten eines Dosimeters laden
	 * 
	 * @param dosimeterID
	 * @throws Exception
	 */
	public void loadByDosimeter(int dosimeterID) throws Exception {
		String where;

		where = "(tabelle='tblDosimeter' AND schluessel='" + dosimeterID + "')";

		Application.getLoaderBase().load(this, where, "datumaenderung");		
	}


	/**
	 * Alle Daten eines Zugangs laden
	 * 
	 * @param zugangID
	 * @throws Exception
	 */
	public void loadByZugang(int zugangID) throws Exception {
		String where;

		where = "(tabelle='tblZugang' AND schluessel='" + zugangID + "')";

		Application.getLoaderBase().load(this, where, "datumaenderung");		
	}

}
