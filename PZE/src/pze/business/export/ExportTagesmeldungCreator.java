package pze.business.export;

import java.util.Calendar;
import java.util.GregorianCalendar;

import framework.Application;
import pze.business.Format;
import pze.business.Messages;
import pze.business.objects.auswertung.CoAnwesenheitTagesmeldung;
import pze.business.objects.auswertung.CoAnwesenheitUebersicht;
import pze.business.objects.reftables.CoErsthelfer;


/**
 * Klasse zum Erstellen einer HTML-Datei für den PDF-Export der Tagesmeldung
 * 
 * @author Lisiecki
 */
public class ExportTagesmeldungCreator extends ExportPdfCreator {

	private GregorianCalendar m_gregDatum;

	
	/**
	 * Gibt die Datei in XHTML zurück.
	 * 
	 * @param tableBuchungen
	 * @return
	 * @throws Exception 
	 */
	public String createHtml(GregorianCalendar gregDatum) throws Exception {
		m_sb = new StringBuilder();
		m_gregDatum = gregDatum;
		
		// Daten laden
		m_co = new CoAnwesenheitTagesmeldung(null, gregDatum);
		
		writeHtmlOpen();
		
		// Footer am Beginn schreiben, da es eine allgemeine Darstellung ist
		writeFooter();
		
		if (!writeSeite())
		{
			return null;
		}

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
		stringDaten = getHtmlStringTableCo(m_co);
		if (stringDaten == null)
		{
			Messages.showErrorMessage("Keine Daten zur Ausgabe gefunden.");
			return false;
		}
		
		m_sb.append("<div class='page'>\n");
		
		writeHeader();
		
		m_sb.append("<div class='floatleft' style='min-width: 70%; margin-top: 20px;'>\n");
		
//		m_sb.append("<h3>Buchungen: </h3>\n");
		m_sb.append(stringDaten);

		m_sb.append("</div>\n"); // floatleft
		
		// Ersthelfer
		appendErsthelfer();

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

		// Überschrift
		m_sb.append("<h1 style='width:100%'>Tagesmeldung <br/>" + Format.getWochentag(m_gregDatum) 
		+ ", " + Format.getString(m_gregDatum) + " (KW " + m_gregDatum.get(Calendar.WEEK_OF_YEAR) + ")" + "</h1>\n");
		
		m_sb.append("</div>\n"); // links
		
		// Logo
		m_sb.append("<div class='drechts'>\n");
		m_sb.append("<img src='/" + Application.getWorkingDirectory().replace("\\", "/") + "WTI_Logo_2023_Schwarz.png'></img><br /><br />\n");
		
		
		m_sb.append("</div>\n"); // rechts
		m_sb.append("</div>\n"); // oben
	}


	/**
	 * alle Freigaben ausgeben
	 * 
	 * @throws Exception
	 */
	private void appendErsthelfer() throws Exception {

		// Tabelle erstellen
		m_sb.append("<div class='floatleft' style='width: 60%; margin-top: 20px;'>\n");
		m_sb.append("		<div class='eingerueckt platz_nach_unten volle_breite' style='margin-top: 20px'>\n");
		//			m_sb.append("			<h2 class='ueberstunden_h2'>Antrag " + m_co.getBuchungsart() + ":</h2>\n");
		m_sb.append("			<table class='" + getClassTable() + "' style='float: left; " + getStyleTable() + "'>\n");

		// Überschrift
		m_sb.append("			<tr>\n");
		m_sb.append("				<th >Erst-/Brandschutzhelfer</th>\n");
		m_sb.append("				<th >Eintrag</th>\n");
		m_sb.append("			</tr>\n");

		// Freigaben der Buchung
		writeErsthelfer();

		m_sb.append("		</table>\n");
		m_sb.append("		</div>\n");
		m_sb.append("</div>\n"); // floatleft
		m_sb.append("\n");
	}


	/**
	 * Ersthelfer ausgeben
	 * 
	 * @throws Exception
	 */
	private void writeErsthelfer() throws Exception {
		String eintrag, htmlAttribute;
		CoErsthelfer coErsthelfer;
		CoAnwesenheitUebersicht coAnwesenheitUebersicht;
		
		coErsthelfer = CoErsthelfer.getInstance();
		coAnwesenheitUebersicht = new CoAnwesenheitUebersicht();
		
		// Ersthelfer rausschreiben
		if (coErsthelfer.moveFirst())
		{
			do
			{
				// Eintrag und Hintergrundfarbe bestimmen
				eintrag = coAnwesenheitUebersicht.bestimmeEintrag(coErsthelfer.getPersonID(), m_gregDatum.getTime(), 0);
				htmlAttribute = getHtmlAttributeErsthelfer(eintrag);
				
				m_sb.append("			<tr>\n");
				m_sb.append("				<td " + htmlAttribute + ">" + coErsthelfer.getPerson() + "</td>\n");
				m_sb.append("				<td " + htmlAttribute + ">" + (eintrag != null ? eintrag : "") + "</td>\n");
//				m_sb.append("				<td class='" + getClassTdDaten(0, 0) + "' >" + coErsthelfer.getPerson() + "</td>\n");
//				m_sb.append("				<td class='" + getClassTdDaten(0, 0) + "' >" + (eintrag != null ? eintrag : "") + "</td>\n");
				m_sb.append("			</tr>\n");
			} while (coErsthelfer.moveNext());
		}
	}


	/**
	 * Hintergrundfarbe/-bild aufgrund des Eintrags der Ersthelfer bestimmen
	 * 
	 * @param eintrag
	 * @return
	 */
	protected String getHtmlAttributeErsthelfer(String eintrag) {
		return "background='file://" +  "/" + Application.getWorkingDirectory().replace("\\", "/") + (eintrag == null ? "gruen.png" : "rot.png") + "'";
	}


	/**
	 * Table-Class für die Datentabelle
	 * 
	 * @return
	 */
	@Override
	protected String getClassTdDaten(int iRow, int iField){
		return "textlinks";
	}


	@Override
	protected String getStand(){
		return "03/2023";
	}

}
