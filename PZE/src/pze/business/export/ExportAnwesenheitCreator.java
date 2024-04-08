package pze.business.export;

import framework.Application;
import pze.business.objects.auswertung.CoAnwesenheitUebersicht;
import pze.business.objects.auswertung.CoAuswertungAnwesenheitUebersicht;
import pze.ui.formulare.UniFormWithSaveLogic;
import pze.ui.formulare.auswertung.FormAnwesenheitUebersicht;
import pze.ui.formulare.auswertung.FormAuswertung;


/**
 * Klasse zum Erstellen einer HTML-Datei für den PDF-Export der AnwesenheitsUebersicht
 * 
 * @author Lisiecki
 */
public class ExportAnwesenheitCreator extends ExportPdfCreator {

	private static FormAuswertung m_formAuswertung;
	

	
	/**
	 * Gibt eine Kontowert-Tabelle in XHTML zurück.
	 * 
	 * @param formAnwesenheitUebersicht
	 * @return
	 * @throws Exception 
	 */
	@Override
	public String createHtml(UniFormWithSaveLogic formAnwesenheitUebersicht) throws Exception {
		m_formAuswertung = (FormAuswertung) formAnwesenheitUebersicht;
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
		
		// zuerst prüfen, ob Daten für das Projekt vorhanden sind
		stringDaten = getHtmlStringTable(m_formAuswertung.getTable());
		if (stringDaten == null)
		{
			return false;
		}
		
		m_sb.append("<div class='page'>\n");
		
		writeHeader();
		
		m_sb.append("<div class='floatleft' style='width: 100%; margin-top: 20px;'>\n");
		
		m_sb.append(stringDaten);

		m_sb.append("</div>\n"); // floatleft
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
		m_sb.append("<h1>Anwesenheit</h1>\n");
		
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
	 * Projektbeschreibung
	 * 
	 * @throws Exception
	 */
	private void writeEinschraenkungen() throws Exception {
		CoAuswertungAnwesenheitUebersicht coAuswertung;

		coAuswertung = (CoAuswertungAnwesenheitUebersicht) m_formAuswertung.getCoAuswertung();
		coAuswertung.moveTo(0);


		m_sb.append("<table class='unsichtbar'>\n");
		
		writeProjektbeschreibungLinksbuendig("Woche:", ((FormAnwesenheitUebersicht) m_formAuswertung).getKw());
		writeProjektbeschreibungLinksbuendig("Abteilung:", coAuswertung.getAbteilung());
		writeProjektbeschreibungLinksbuendig("Person:", coAuswertung.getPerson());
		writeProjektbeschreibungLinksbuendig("Liste:", coAuswertung.getPersonenliste());
		m_sb.append("</table>\n");

	}

	
	@Override
	protected String getClassTdUeberschriften(int iField) {
		String classValue;
		
		classValue = getClassSpaltenbreite(iField);

		return classValue;
	}

	
	@Override
	protected String getClassTdDaten(int iRow, int iField) {
		String classValue;
		
		classValue = super.getClassTdDaten(iRow, iField);
		
		classValue += (iField == 0 ? "textlinks" : "textmitte");
		classValue += getClassSpaltenbreite(iField);

		classValue += (((CoAnwesenheitUebersicht)m_co).isGeplanteBuchung(iRow, iField) ? " fettkursiv" : "");
		
		return classValue;
	}


	private String getClassSpaltenbreite(int iField) {
		if (iField == 0) // Person
		{
			return " ";
		}
		else
		{
			return " breiteTagAnwesenheit";
		}
	}


	@Override
	protected String getStand(){
		return "03/2023";
	}

}
