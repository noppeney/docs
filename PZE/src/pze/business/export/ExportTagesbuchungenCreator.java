package pze.business.export;

import framework.Application;
import pze.business.Format;
import pze.business.objects.personen.CoBuchung;
import pze.business.objects.personen.CoFreigabe;
import pze.ui.controls.SortedTableControl;


/**
 * Klasse zum Erstellen einer HTML-Datei für den PDF-Export der Tagesbuchungen
 * 
 * @author Lisiecki
 */
public class ExportTagesbuchungenCreator extends ExportPdfCreator {

	private SortedTableControl m_tableBuchungen;
	private CoBuchung m_coBuchung;

	
	/**
	 * Gibt eine Kontowert-Tabelle in XHTML zurück.
	 * 
	 * @param tableBuchungen
	 * @return
	 * @throws Exception 
	 */
	public String createHtml(SortedTableControl tableBuchungen) throws Exception {
		m_tableBuchungen = tableBuchungen;
		m_coBuchung = (CoBuchung) tableBuchungen.getData();
		m_sb = new StringBuilder();
		
		writeHtmlOpen();
		
		// Footer am Beginn schreiben, da es eine allgemeine Darstellung ist
		writeFooter();
		
		writeSeite();

		writeHtmlClose();

		return m_sb.toString();
	}


	/**
	 * Eine Seite erstellen
	 * 
	 * @return Daten gefunden
	 * @throws Exception
	 */
	private boolean writeSeite() throws Exception {
		String stringDaten;
		
		// zuerst prüfen, ob Daten vorhanden sind
		stringDaten = getHtmlStringTable(m_tableBuchungen);
		if (stringDaten == null)
		{
			return false;
		}
		
		m_sb.append("<div class='page'>\n");
		
		writeHeader();
		
		m_sb.append("<div class='floatleft' style='width: 70%; margin-top: 20px;'>\n");
		
		m_sb.append("<h3>Buchungen: </h3>\n");
		m_sb.append(stringDaten);

		m_sb.append("</div>\n"); // floatleft
		
		// Freigaben
		appendFreigaben();

		m_sb.append("</div>\n"); // page

		return true;
	}


	/**
	 * Header mit Logo, Name, monat etc.
	 * 
	 * @throws Exception 
	 */
	protected void writeHeader() throws Exception {
		m_sb.append("<div class='doben'>\n");
		m_sb.append("<div class='dlinks'>\n");

		// Überschrift Monatseinsatzblatt
		m_sb.append("<h1>Buchungsprotokoll</h1>\n");
		
		// Einschränkungen Datum & Personen
		writeEinschraenkungen();
	
		m_sb.append("</div>\n"); // links
		
		// Logo
		m_sb.append("<div class='drechts'>\n");
		m_sb.append("<img src='/" + Application.getWorkingDirectory().replace("\\", "/") + "WTI_Logo_2023_Schwarz.png'></img><br /><br />\n");
		
		
		m_sb.append("</div>\n"); // rechts
		m_sb.append("</div>\n"); // oben
	}


	/**
	 * Einschränkungen Datum & Personen
	 * 
	 * @throws Exception
	 */
	private void writeEinschraenkungen() throws Exception {
		
		m_sb.append("<table class='unsichtbar'>\n");
		
		writeProjektbeschreibungLinksbuendig("Datum:", Format.getString(m_coBuchung.getDatum()));
		writeProjektbeschreibungLinksbuendig("Person:", m_coBuchung.getFieldPersonID().getDisplayValue());
		m_sb.append("</table>\n");

	}


	/**
	 * alle Freigaben ausgeben
	 * 
	 * @throws Exception
	 */
	private void appendFreigaben() throws Exception {
		CoFreigabe coFreigabe;
		
		coFreigabe = new CoFreigabe();
		
		// Buchungen durchlaufen und prüfen, ob Freigaben vorhanden sind
		m_coBuchung.moveFirst();
		do
		{
			coFreigabe = new CoFreigabe();
			coFreigabe.load(m_coBuchung.getID());
			
			// Freigaben vorhanden?
			if (coFreigabe.getRowCount() == 0)
			{
				continue;
			}

			// Tabelle erstellen
			m_sb.append("<div class='floatleft' style='width: 60%; margin-top: 20px;'>\n");
			m_sb.append("		<div class='eingerueckt platz_nach_unten volle_breite' style='margin-top: 20px'>\n");
			m_sb.append("			<h2 class='ueberstunden_h2'>Antrag " + m_coBuchung.getBuchungsart() + ":</h2>\n");
			m_sb.append("			<table class='" + getClassTable() + "' style='float: left; " + getStyleTable() + "'>\n");

			// Überschrift
			m_sb.append("			<tr>\n");
			m_sb.append("				<th >Status Buchung</th>\n");
			m_sb.append("				<th >Status Freigabe</th>\n");
			m_sb.append("				<th >Freigabe durch</th>\n");
			m_sb.append("				<th >Datum</th>\n");
			m_sb.append("			</tr>\n");

			// Freigaben der Buchung
			writeFreigabe(coFreigabe);

			m_sb.append("		</table>\n");
			m_sb.append("		</div>\n");
			m_sb.append("</div>\n"); // floatleft
			m_sb.append("\n");
		} while(m_coBuchung.moveNext());
	}


	/**
	 * Freigaben einer Buchung ausgeben
	 * 
	 * @param coFreigabe
	 * @throws Exception
	 */
	private void writeFreigabe(CoFreigabe coFreigabe) throws Exception {
		
		// Aufträge rausschreiben
		if (coFreigabe.moveFirst())
		{
			do
			{
				m_sb.append("			<tr>\n");
				m_sb.append("				<td >" + coFreigabe.getStatusBuchung() + "</td>\n");
				m_sb.append("				<td >" + coFreigabe.getStatusGenehmigung() + "</td>\n");
				m_sb.append("				<td >" + coFreigabe.getPerson() + "</td>\n");
				m_sb.append("				<td >" + Format.getStringMitUhrzeit(coFreigabe.getDatum()) + "</td>\n");
				m_sb.append("			</tr>\n");
			} while (coFreigabe.moveNext());
		}
	}


	@Override
	protected String getStand(){
		return "03/2023";
	}

}
