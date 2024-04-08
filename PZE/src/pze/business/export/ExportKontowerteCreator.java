package pze.business.export;

import java.util.Date;

import framework.Application;
import pze.business.Format;
import pze.business.objects.auswertung.CoAuswertung;
import pze.business.objects.auswertung.CoAuswertungKontowerteZeitraum;
import pze.ui.formulare.UniFormWithSaveLogic;
import pze.ui.formulare.auswertung.FormAuswertung;


/**
 * Klasse zum Erstellen einer HTML-Datei für den PDF-Export der Kontowerte
 * 
 * @author Lisiecki
 */
public class ExportKontowerteCreator extends ExportPdfCreator {

	private static FormAuswertung m_formAuswertung;
	

	/**
	 * Prüfen, ob die Datei im Querformat ausgegeben werden soll
	 * 
	 * @return
	 */
	protected boolean isQuerformat() {
		CoAuswertung coAuswertung;

		coAuswertung = m_formAuswertung.getCoAuswertung();
		
		// bei der Zeitraumauswertung wird bei mehr als 5 Spalten im Querformat ausgegeben
		if (coAuswertung instanceof CoAuswertungKontowerteZeitraum 
				&& ((CoAuswertungKontowerteZeitraum) coAuswertung).getAnzSpaltenAusgewaehlt() > 5)
		{
			return true;
		}
		
		return false;
	}


	
	/**
	 * Gibt eine Kontowert-Tabelle in XHTML zurück.
	 * 
	 * @param formAuswertungKontowerte
	 * @return
	 * @throws Exception 
	 */
	@Override
	public String createHtml(UniFormWithSaveLogic formAuswertungKontowerte) throws Exception {
		m_formAuswertung = (FormAuswertung) formAuswertungKontowerte;
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

		// Überschrift Monatseinsatzblatt
		m_sb.append("<h1>Kontowerte</h1>\n");
		
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
		String stringValue;
		Date datumVon, datumBis;
		
		
		CoAuswertung coAuswertung;

		coAuswertung = m_formAuswertung.getCoAuswertung();
		coAuswertung.moveTo(0);


		m_sb.append("<table class='unsichtbar'>\n");
		
		stringValue = "";
		datumVon = coAuswertung.getDatumVon();
		datumBis = coAuswertung.getDatumBis();

		if (datumVon != null)
		{
			stringValue += Format.getString(datumVon) + " ";
		}
		
		// Datum bis heute, wenn kein datum angegeben
		if (datumBis == null && datumVon == null)
		{
			datumBis = new Date();
		}
		if (datumBis != null)
		{
			stringValue += "bis " + Format.getString(datumBis);
		}
		
		writeProjektbeschreibungLinksbuendig("Datum:", stringValue);
		writeProjektbeschreibungLinksbuendig("Abteilung:", coAuswertung.getAbteilung());
		writeProjektbeschreibungLinksbuendig("Person:", coAuswertung.getPerson());
		m_sb.append("</table>\n");

	}

	
	/**
	 * Formatierung für rechtsbündige Zeiten (auf 3-stellige Zahlen normiert)
	 * 
	 * @see pze.business.export.ExportPdfCreator#getZeitAsText(java.lang.String)
	 */
	@Override
	protected String getZeitAsText(String stringValue) {
		int minuten;
		
		minuten = Format.getIntValue(stringValue);
		
		stringValue = Format.getZeitAsText(minuten);
		
		// Formatierung für rechtsbündige Zeiten (auf 3-stellige Zahlen normiert)
		if (minuten < 0) // negative Zahlen
		{
			stringValue = "&nbsp;" + stringValue;
			
			if (minuten > -10*60)
			{
				stringValue = "&nbsp;&nbsp;" + stringValue;
			}
		}
		else // positive Zahlen
		{
			if (minuten < 100*60)
			{
				stringValue = "&nbsp;&nbsp;" + stringValue;
			}
			if (minuten < 10*60)
			{
				stringValue = "&nbsp;&nbsp;" + stringValue;
			}
		}
		
		return stringValue;
	}


	/**
	 * Formatierung für rechtsbündige Zahlen (auf 3-stellige Zahlen normiert)
	 * 
	 * @see pze.business.export.ExportPdfCreator#getIntegerAsText(java.lang.String)
	 */
	@Override
	protected String getIntegerAsText(String stringValue) {
		int intValue;
		
		intValue = Format.getIntValue(stringValue);
		
		// Formatierung für rechtsbündige Zeiten (auf 2-stellige Zahlen normiert)
		if (intValue < 0) // negative Zahlen
		{
			if (intValue > -10)
			{
				stringValue = "&nbsp;&nbsp;" + stringValue;
			}
		}
		else // positive Zahlen
		{
			stringValue = "&nbsp;" + stringValue;

			if (intValue < 10)
			{
				stringValue = "&nbsp;&nbsp;" + stringValue;
			}
		}
		
		return stringValue;
	}


	@Override
	protected String getClassTdDaten(int iRow, int iField) {
		String classValue;
		
		classValue = super.getClassTdDaten(iRow, iField);
		
		classValue += (iField == 0 ? "textlinks" : "textmitte");
		
		return classValue;
	}


	@Override
	protected String getStand(){
		return "03/2023";
	}

}
