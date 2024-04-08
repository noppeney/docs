package pze.business.datentransfer;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.Date;

import framework.Application;
import pze.business.Format;
import pze.business.Messages;
import pze.business.objects.geloc.CoBuchungenGeloc;
import pze.business.objects.personen.CoBuchung;
import pze.business.objects.personen.CoKontowert;
import pze.business.objects.personen.CoPerson;


/**
 * Klasse zur Steuerung des Imports von Buchungen aus Geloc in die DB
 * 
 * @author Lisiecki
 */
public class ImportGelocDaten {

	
	/**
	 * Führt ein Update der Buchungs-Tabelle aus.<br>
	 * Alle noch nicht vorhanden Buchungen werden aus der Geloc-DB geladen.
	 * 
	 * @return Anzahl der neuen/importierten Datensätze
	 */
	public static int updateBuchungen() {
		CoBuchungenGeloc coBuchungenGeloc;
		
		try 
		{
			// Neue Buchungen laden
			coBuchungenGeloc = loadNewBuchungen();
			
			// Buchungen in PZE-DB übertragen
			saveNewBuchungen(coBuchungenGeloc);
			
			return coBuchungenGeloc.getRowCount();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			
			StringWriter writer = new StringWriter();
			PrintWriter out = new PrintWriter(writer);
			e.printStackTrace(out);
			
			Messages.showErrorMessage("Fehler beim Laden neuer Datensätze aus der Geloc/Time-DB\n" 
			+ "------------------------------------------------\n" + writer.toString());
			return 0;
		}
	}


	/**
	 * Neue Buchungen vom Geloc-Server laden
	 * 
	 * @return
	 * @throws Exception
	 */
	private static CoBuchungenGeloc loadNewBuchungen() throws Exception {
		CoBuchungenGeloc coBuchungenGeloc;
		
		coBuchungenGeloc = new CoBuchungenGeloc();
		coBuchungenGeloc.loadNewBuchungen();
		
		return coBuchungenGeloc;
	}
	

	/**
	 * Neue Buchungen in der PZE-DB speichern
	 * 
	 * @param coBuchungenGeloc
	 * @throws Exception
	 */
	private static void saveNewBuchungen(CoBuchungenGeloc coBuchungenGeloc) throws Exception {
		int tag, lastTag;
		Date datum;
		CoBuchung coBuchungen;
		
		lastTag = 0;
		coBuchungen = new CoBuchung();
		coBuchungen.begin();
		
		if (coBuchungenGeloc.moveFirst())
		{
			do
			{
				// Buchung "die ID ist nicht im System bekannt" rausfiltern
				if (coBuchungenGeloc.getEventNr() == 7000)
				{
					continue;
				}
				
				coBuchungen.createNew();
				
				// Buchung übertragen
				coBuchungen.setBuchungsNr(coBuchungenGeloc.getBuchungsNr());
				coBuchungen.setZeitpunkt(coBuchungenGeloc.getZeitpunkt());
				coBuchungen.setSystemNr(coBuchungenGeloc.getSystemNr());
				coBuchungen.setEventNr(coBuchungenGeloc.getEventNr());
				coBuchungen.setIdX(coBuchungenGeloc.getIdX());
				coBuchungen.setChipkartenNrHex(coBuchungenGeloc.getChipkartenNrHex());
				coBuchungen.setParam1(coBuchungenGeloc.getParam1());
				coBuchungen.setParam2(coBuchungenGeloc.getParam2());
				
				// Status OK setzen
				coBuchungen.setStatusOk();
				
				
				// Datum prüfen und ggf. prüfen, ob alle Kontowerte des Vortages da (für Meldung "keine Buchung") 
				// und korrekt (z. B. Sollarbeitszeit beim Zeitmodellwechsel) sind
				// wenn die Daten mehrerer Tage gleichzeitig importiert werden (durch Geloc-Absturz), jeden Tag prüfen
				datum = coBuchungen.getDatum();
				tag = Format.getGregorianCalendar(datum).get(Calendar.DAY_OF_MONTH);
				if (tag != lastTag)
				{
					lastTag = tag;
					updateKontowerteVortag(datum);
					
					// Personen neu laden, falls Chipnummern geändert wurden
					Application.getRefTableLoader().updateRefItems("table." + CoPerson.TABLE_NAME);
				}
				
			} while (coBuchungenGeloc.moveNext());

			coBuchungen.save();
		}
	}


	/**
	 * Kontodaten für den Vortag des übergebenen Datums für alle Personen prüfen und ggf. einfügen (falls keine Kontowerte verhanden sind)
	 * 
	 * @param datum 
	 * @throws Exception
	 */
	private static void updateKontowerteVortag(Date datum) throws Exception {
		CoKontowert coKontowert;
		
		if (datum == null)
		{
			return;
		}

		coKontowert = new CoKontowert();

		// Kontowerte für den Vortag für alle Personen erstellen, wenn keine Buchung vorhanden ist
		coKontowert.updateKontowerteVortag(datum);
	}


	
}
