package pze.business.export;

import java.util.Date;

import framework.Application;
import pze.business.Format;
import pze.business.objects.auswertung.CoAuswertungVerletzerliste;
import pze.business.objects.reftables.CoStatusVerletzung;
import pze.ui.formulare.UniFormWithSaveLogic;
import pze.ui.formulare.auswertung.FormAuswertungVerletzerliste;


/**
 * Klasse zum Erstellen einer HTML-Datei für den PDF-Export der Verletzerliste
 * 
 * @author Lisiecki
 */
public class ExportVerletzerlisteCreator extends ExportPdfCreator {

	private static FormAuswertungVerletzerliste m_formVerletzerliste;
	

	
	/**
	 * Gibt eine Tätigkeitsnachweis-Tabelle in XHTML zurück.
	 * 
	 * @param formPersonMonatseinsatzblatt
	 * @return
	 * @throws Exception 
	 */
	@Override
	public String createHtml(UniFormWithSaveLogic formVerletzerliste) throws Exception {
		m_formVerletzerliste = (FormAuswertungVerletzerliste) formVerletzerliste;
		m_sb = new StringBuilder();
		
		writeHtmlOpen();
		
		// Footer am Beginn schreiben, da es eine allgemeine Darstellung ist
		writeFooter();

		// Seiten der Tätigkeitsnachweise erstellen
		writeSeite();

		writeHtmlClose();

		return m_sb.toString();
	}

	
	/**
	 * Querformat, wenn die Statusinformationen ausgegeben werden sollen
	 * 
	 * @see pze.business.export.ExportPdfCreator#isQuerformat()
	 */
	@Override
	protected boolean isQuerformat() {
		return !((CoAuswertungVerletzerliste) m_formVerletzerliste.getCoAuswertung()).isStatusInfoAusgeblendet();
	}

	
	/**
	 * Eine Seite des Monatseinsatzblattes erstellen
	 * 
	 * @return Daten gefunden
	 * @throws Exception
	 */
	private boolean writeSeite() throws Exception {
		String stringDaten;
		
		// zuerst prüfen, ob Daten für das Projekt vorhanden sind
		stringDaten = getHtmlStringTable(m_formVerletzerliste.getTable());
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
		m_sb.append("<h1>Verletzerliste</h1>\n");
		
		// Projektbeschreibung
		writeKennwerteAuswertung();
	
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
	private void writeKennwerteAuswertung() throws Exception {
		String stringValue;
		Date datumVon, datumBis;
		
		
		CoAuswertungVerletzerliste coAuswertungVerletzerliste;

		coAuswertungVerletzerliste = (CoAuswertungVerletzerliste) m_formVerletzerliste.getCoAuswertung();
		coAuswertungVerletzerliste.moveTo(0);


		m_sb.append("<table class='unsichtbar'>\n");
		
		stringValue = "";
		datumVon = coAuswertungVerletzerliste.getDatumVon();
		datumBis = coAuswertungVerletzerliste.getDatumBis();

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
		writeProjektbeschreibungLinksbuendig("Abteilung:", coAuswertungVerletzerliste.getAbteilung());
		writeProjektbeschreibungLinksbuendig("Person:", coAuswertungVerletzerliste.getPerson());
		writeProjektbeschreibungLinksbuendig("Status:", CoStatusVerletzung.getInstance().getBezeichnung(coAuswertungVerletzerliste.getStatusID()));
		m_sb.append("</table>\n");

	}

	
	@Override
	protected String getClassTdDaten(int iRow, int iField) {
		String classValue;
		
		classValue = super.getClassTdDaten(iRow, iField);
		
		classValue += (iField == 0 || iField == 3 ? "textlinks" : "textmitte ");
		
		return classValue;
	}


	@Override
	protected String getStand(){
		return "03/2023";
	}

}
