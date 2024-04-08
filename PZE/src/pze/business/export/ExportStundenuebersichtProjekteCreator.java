package pze.business.export;

import java.util.Date;

import framework.Application;
import pze.business.Format;
import pze.business.Messages;
import pze.business.objects.auswertung.CoAuswertung;
import pze.business.objects.projektverwaltung.CoKostenstelle;
import pze.business.objects.projektverwaltung.CoProjekt;
import pze.ui.formulare.UniFormWithSaveLogic;
import pze.ui.formulare.projektverwaltung.FormAuswertungProjekt;


/**
 * Klasse zum Erstellen einer HTML-Datei für den PDF-Export der Stundenübersicht von Projekten
 * 
 * @author Lisiecki
 */
public class ExportStundenuebersichtProjekteCreator extends ExportPdfCreator {

	private FormAuswertungProjekt m_formAuswertungProjekt;
	

	/**
	 * Gibt eine Stundenübersicht in XHTML zurück.
	 * 
	 * @param formAuswertungProjekt
	 * @return
	 * @throws Exception 
	 */
	@Override
	public String createHtml(UniFormWithSaveLogic formAuswertungProjekt) throws Exception {
		m_formAuswertungProjekt = (FormAuswertungProjekt) formAuswertungProjekt;
		m_sb = new StringBuilder();
		
		writeHtmlOpen();
		
		// Footer am Beginn schreiben, da es eine allgemeine Darstellung ist
		writeFooter();

		if (!writeSeite(true))
		{
			return null;
		}

		writeHtmlClose();

		return m_sb.toString();
	}


	public String createHtmlBegin(UniFormWithSaveLogic formAuswertungProjekt) throws Exception {
		m_formAuswertungProjekt = (FormAuswertungProjekt) formAuswertungProjekt;
		m_sb = new StringBuilder();
		
		writeHtmlOpen();
		
		// Footer am Beginn schreiben, da es eine allgemeine Darstellung ist
		writeFooter();

		return m_sb.toString();
	}

	public String createHtmlSeite(UniFormWithSaveLogic formAuswertungProjekt) throws Exception {
		m_formAuswertungProjekt = (FormAuswertungProjekt) formAuswertungProjekt;
		m_sb = new StringBuilder();

		if (!writeSeite(false))
		{
			return null;
		}

		return m_sb.toString();
	}

	public String createHtmlEnde() throws Exception {
		m_sb = new StringBuilder();

		writeHtmlClose();

		return m_sb.toString();
	}


	/**
	 * Eine Seite erstellen
	 * 
	 * @return Daten gefunden
	 * @throws Exception
	 */
	public boolean writeSeite(boolean mitMeldung) throws Exception {
		String stringDaten;
		
		// zuerst prüfen, ob Daten für das Projekt vorhanden sind
		stringDaten = getHtmlStringTable(m_formAuswertungProjekt.getTable());
		if (stringDaten == null)
		{
			if (mitMeldung)
			{
				Messages.showErrorMessage("Keine Daten zur Ausgabe gefunden.");
			}
			return false;
		}
		
		m_sb.append("<div class='page'>\n");
		
		writeHeader();

		m_sb.append("<div class='floatleft' style='width: 100%; margin-top: 20px;'>\n");
		
		// Tabellenüberschrift
		writeTabellenBeschriftung();
		// Tabellendaten
		m_sb.append(stringDaten);
		
		
		// Tabelle mit Reisestunden
		stringDaten = getHtmlStringTable(m_formAuswertungProjekt.getTable2());
		if (stringDaten != null)
		{
			// Abstand zwischen den Tabellen
			m_sb.append("<br />\n");
			m_sb.append("<div class='dlinks' style='width: 100%; '>\n");
			m_sb.append("<br />\n");
			m_sb.append("<br />\n");
			m_sb.append("</div>\n");
			m_sb.append("<br />\n");
			
			m_sb.append(stringDaten);
		}

		
		m_sb.append("</div>\n"); // floatleft
		m_sb.append("</div>\n"); // page

		return true;
	}


	@Override
	protected String getClassTable(){
		return "feste_groesse fontsize11";
	}

	
	@Override
	/**
	 * Table-Style für die Datentabelle
	 * 
	 * @return
	 */
	protected String getStyleTable(){
		return "width:100px;";
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
		m_sb.append("<h1>Stundenübersicht Projekte</h1>\n");
		
		// Projektdaten links
		writeProjektdatenLinks();
	
		m_sb.append("</div>\n"); // links
		
		// Logo
		m_sb.append("<div class='drechts'>\n");
		m_sb.append("<img src='/" + Application.getWorkingDirectory().replace("\\", "/") + "WTI_Logo_2023_Schwarz.png'></img><br /><br />\n");
		
		// Projektdaten rechts
		writeProjektdatenRechts();
	
		m_sb.append("</div>\n"); // rechts
		m_sb.append("</div>\n"); // oben

	}



	private void writeTabellenBeschriftung() {
		Date datumVon;
		Date datumBis;
		String zeitraum;
		CoAuswertung coAuswertung;

		coAuswertung = m_formAuswertungProjekt.getCoAuswertung();
		datumVon = coAuswertung.getDatumVonOriginalValue();
		datumBis = coAuswertung.getDatumBisOriginalValue();
		if (datumVon == null && datumBis == null)
		{
			zeitraum = "komplett";
		}
		else
		{
			zeitraum = (datumVon != null ? Format.getString(datumVon) : "") + (datumBis != null ? " bis " + Format.getString(datumBis) : "");
		}
		m_sb.append("<h3>Ausgabezeitraum: " + zeitraum + "</h3>\n");
	}


	/**
	 * Projektbeschreibung
	 * 
	 * @throws Exception
	 */
	private void writeProjektdatenLinks() throws Exception {
		String kostenstellen;
		CoProjekt coProjekt;
		CoKostenstelle coKostenstelle;
		
		coProjekt = m_formAuswertungProjekt.getCoProjekt();
		coProjekt.moveFirst();

		// alle Kostenstellen als String
		coKostenstelle = coProjekt.getCoKostenstelle();
		kostenstellen = coKostenstelle.getBezeichnungen();
		if (kostenstellen != null)
		{
			kostenstellen = kostenstellen.replace(", ", "<br />");
		}

		m_sb.append("<table class='unsichtbar'>\n");
		
		writeProjektbeschreibungLinksbuendig("WTI-Auftrags-Nr.:", coProjekt.getAuftragsNr());
		writeProjektbeschreibungLinksbuendig("Abruf-Nr.:", coProjekt.getAbrufNr());
		writeProjektbeschreibungLinksbuendig("Beschreibung:", coProjekt.getBeschreibung());
		writeProjektbeschreibungLinksbuendig("Kostenstellen:", kostenstellen);
		writeProjektbeschreibungLinksbuendig("Kunde:", coProjekt.getKunde());
		writeProjektbeschreibungLinksbuendig("Abteilung Kunde:", coProjekt.getAbteilungKunde());
		appendBr(1);
		
		writeProjektbeschreibungLinksbuendig("Bestell-Nr.:", coProjekt.getBestellNr());
		writeProjektbeschreibungLinksbuendig("Bestell-Datum:", Format.getString(coProjekt.getDatumBestellung()));
		appendBr(1);
		
		writeProjektbeschreibungLinksbuendig("Projektleiter:", coProjekt.getProjektleiter());
		writeProjektbeschreibungLinksbuendig("Anforderer Kunde:", coProjekt.getAnfordererKunde());
		appendBr(1);
		
		writeProjektbeschreibungLinksbuendig("Datum Fertigmeldung:", Format.getString(coProjekt.getDatumFertigmeldung()));
		writeProjektbeschreibungLinksbuendig("Datum Freigabe Rechn. AG:", Format.getString(coProjekt.getDatumFreigabeRechnungAG()));
		writeProjektbeschreibungLinksbuendig("Prozentmeldung versendet:", Format.getString(coProjekt.getDatumMeldungVersendet()));
		appendBr(1);
		
		writeProjektbeschreibungLinksbuendig("Liefertermin:", Format.getString(coProjekt.getLiefertermin()));
		writeProjektbeschreibungLinksbuendig("Status:", coProjekt.getStatus());
		
		m_sb.append("</table>\n");

	}


	/**
	 * Projektbeschreibung
	 * 
	 * @throws Exception
	 */
	private void writeProjektdatenRechts() throws Exception {
		CoProjekt coProjekt;
		
		coProjekt = m_formAuswertungProjekt.getCoProjekt();
		coProjekt.moveFirst();


		m_sb.append("<table class='unsichtbar'>\n");
		
		writeProjektbeschreibungRechtsbuendig("Abrechnungsart:", coProjekt.getAbrechnungsart());
		appendBr(1);

		writeProjektbeschreibungRechtsbuendig("Bestellwert (h):", Format.getZeitAsText(coProjekt.getBestellwert()));
		writeProjektbeschreibungRechtsbuendig("UVG (h):", Format.getZeitAsText(coProjekt.getUvg()));
		writeProjektbeschreibungRechtsbuendig("Puffer (h):", Format.getZeitAsText(coProjekt.getPuffer()));
		writeProjektbeschreibungRechtsbuendig("Sollstunden:", Format.getZeitAsText(coProjekt.getSollstunden()));
		writeProjektbeschreibungRechtsbuendig("Startwert (h):", Format.getZeitAsText(coProjekt.getStartwert()));
		appendBr(1);

		writeProjektbeschreibungRechtsbuendig("Gesamtstunden:", Format.getZeitAsText(coProjekt.getIstStunden()));
		appendBr(1);
		
		writeProjektbeschreibungRechtsbuendig("verbleibende Stunden:", Format.getZeitAsText(coProjekt.getWertZeitVerbleibend()));
		appendBr(1);
		
		writeProjektbeschreibungRechtsbuendig("Verbrauch (Bestellwert):", coProjekt.getVerbrauchBestellwertInProzent());
		writeProjektbeschreibungRechtsbuendig("Verbrauch (Sollstunden):", coProjekt.getVerbrauchSollstundenInProzent());
		appendBr(1);
		
		writeProjektbeschreibungRechtsbuendig("berechnet bis:", Format.getString(coProjekt.getDatumBerechnetBis()));
		appendBr(1);
		
		writeProjektbeschreibungRechtsbuendig("Stand:", Format.getString(new Date()));
		
		m_sb.append("</table>\n");

	}


	@Override
	protected String getStand(){
		return "03/2023";
	}

	
	@Override
	protected String getClassTdUeberschriften(int iField) {
		String classValue;
		
		classValue = super.getClassTdUeberschriften(iField);
		
		classValue += "textmitte ";
		classValue += getClassSpaltenbreite(iField, classValue);

		return classValue;
	}

	
	@Override
	protected String getClassTdDaten(int iRow, int iField) {
		String classValue;
		
		classValue = super.getClassTdDaten(iRow, iField);
		
		classValue += (iField == 0 ? "textlinks" : "textrechts ");
		classValue += getClassSpaltenbreite(iField, classValue);
		
		return classValue;
	}



	private String getClassSpaltenbreite(int iField, String classValue) {
		if (iField == 0) // Person
		{
			return " breite95px";
		}
		else if (iField == m_formAuswertungProjekt.getTable().getData().getFieldCount()-1)  // Summe
		{
			return " breite50px"; 
		}
		else
		{
			return " breite41px";
		}
	}

}
