package pze.business.export;

import java.util.Date;

import framework.Application;
import framework.business.fields.HeaderDescription;
import framework.business.interfaces.tables.IHeaderDescription;
import pze.business.Format;
import pze.business.Messages;
import pze.business.objects.auswertung.CoAuswertung;
import pze.business.objects.projektverwaltung.VirtCoProjekt;
import pze.ui.controls.SortedTableControl;
import pze.ui.formulare.UniFormWithSaveLogic;
import pze.ui.formulare.auswertung.FormAuswertung;


/**
 * Klasse zum Erstellen einer HTML-Datei für den PDF-Export der Ampelliste
 * 
 * @author Lisiecki
 */
public class ExportAmpellisteCreator extends ExportPdfCreator {

	private static FormAuswertung m_formAmpelliste;
	

	/**
	 * Datei immer im Querformat ausgeben
	 * 
	 * @see pze.business.export.ExportPdfCreator#isQuerformat()
	 */
	protected boolean isQuerformat() {
		return true;
	}


	
	/**
	 * Gibt eine Stundenübersicht in XHTML zurück.
	 * 
	 * @param formAmpelliste
	 * @return
	 * @throws Exception 
	 */
	@Override
	public String createHtml(UniFormWithSaveLogic formAmpelliste) throws Exception {
		m_formAmpelliste = (FormAuswertung) formAmpelliste;
		m_sb = new StringBuilder();
		
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
		
		// zuerst prüfen, ob Daten für das Projekt vorhanden sind
		stringDaten = getHtmlStringTable(m_formAmpelliste.getTable());
		if (stringDaten == null)
		{
			Messages.showErrorMessage("Keine Daten zur Ausgabe gefunden.");
			return false;
		}
		
		m_sb.append("<div class='page'>\n");
		
		writeHeader();

		m_sb.append("<div class='floatleft' style='width: 100%;'>\n");
		
		// Tabellenüberschrift
		writeTabellenBeschriftung();
		// Tabellendaten
		m_sb.append(stringDaten);
		
		m_sb.append("</div>\n"); // floatleft
		
		m_sb.append("</div>\n"); // page

		return true;
	}


	/**
	 * es werden nicht alle Spalten ausgegeben
	 * @throws Exception 
	 * 
	 * @see pze.business.export.ExportPdfCreator#manipuliereCo(pze.business.objects.AbstractCacheObject)
	 */
	@Override
	protected IHeaderDescription manipuliereHeaderDescription(SortedTableControl table) {
		HeaderDescription manipulierteHeaderDescription;
		VirtCoProjekt virtCoProjekt;
		
		virtCoProjekt = (VirtCoProjekt) table.getData();
		manipulierteHeaderDescription = new HeaderDescription(table.getHeaderDescription().getResID());
		
		manipulierteHeaderDescription.add(virtCoProjekt.getFieldKundeID().getFieldDescription());
		manipulierteHeaderDescription.add(virtCoProjekt.getFieldAuftragsNr().getFieldDescription());
		manipulierteHeaderDescription.add(virtCoProjekt.getFieldAbrufNr().getFieldDescription());
		manipulierteHeaderDescription.add(virtCoProjekt.getFieldProjektleiterID().getFieldDescription());
		manipulierteHeaderDescription.add(virtCoProjekt.getFieldAbrechnungsartID().getFieldDescription());
		manipulierteHeaderDescription.add(virtCoProjekt.getFieldBestellwert().getFieldDescription());
		manipulierteHeaderDescription.add(virtCoProjekt.getFieldSollstunden().getFieldDescription());
		manipulierteHeaderDescription.add(virtCoProjekt.getFieldIstStunden().getFieldDescription());
		manipulierteHeaderDescription.add(virtCoProjekt.getFieldWertZeitVerbleibend().getFieldDescription());
		manipulierteHeaderDescription.add(virtCoProjekt.getFieldVerbrauchBestellwert().getFieldDescription());
		manipulierteHeaderDescription.add(virtCoProjekt.getFieldVerbrauchSollstunden().getFieldDescription());
		manipulierteHeaderDescription.add(virtCoProjekt.getFieldDatumMeldungVersendet().getFieldDescription());
		manipulierteHeaderDescription.add(virtCoProjekt.getFieldStatusID().getFieldDescription());
		manipulierteHeaderDescription.add(virtCoProjekt.getFieldLiefertermin().getFieldDescription());
		manipulierteHeaderDescription.add(virtCoProjekt.getFieldBeschreibung().getFieldDescription());
		
		return manipulierteHeaderDescription;
	}

	
	@Override
	protected String getClassTable(){
		return "fontsize10";
	}

	
	/**
	 * Header mit Logo, Name, monat etc.
	 * 
	 * @throws Exception 
	 */
	protected void writeHeader() throws Exception {

		m_sb.append("<div class='doben'>\n");
		m_sb.append("<div class='dlinks'>\n");

		// Überschrift Ampelliste
		m_sb.append("<h1>Ampelliste</h1>\n");
		
		// Projektdaten links
//		writeProjektdatenLinks();
	
		m_sb.append("</div>\n"); // links
		
		// Logo
		m_sb.append("<div class='drechts'>\n");
		m_sb.append("<img src='/" + Application.getWorkingDirectory().replace("\\", "/") + "WTI_Logo_2023_Schwarz.png'></img>\n");
		
		m_sb.append("</div>\n"); // rechts
		m_sb.append("</div>\n"); // oben
	}



	private void writeTabellenBeschriftung() {
		Date datumVon;
		Date datumBis;
		String zeitraum;
		CoAuswertung coAuswertung;

		coAuswertung = m_formAmpelliste.getCoAuswertung();
		datumVon = coAuswertung.getDatumVonOriginalValue();
		datumBis = coAuswertung.getDatumBisOriginalValue();
		if (datumVon == null && datumBis == null)
		{
			zeitraum = "vollständig";
		}
		else
		{
			zeitraum = (datumVon != null ? Format.getString(datumVon) : "") + (datumBis != null ? " bis " + Format.getString(datumBis) : "");
		}
		m_sb.append("<h3 style='margin-top: 0px;' >Datum: " + zeitraum + "</h3>\n");
	}

	
	@Override
	protected String getClassTdDaten(int iRow, int iField) {
		String classValue;
		
		classValue = super.getClassTdDaten(iRow, iField);
		
		if (iField == 9 || iField == 11)
		{
			classValue += "textmitte ";
		}
		else
		{
			classValue += (iField < 5 || iField > 8 ? "textlinks " : "textrechts ");
		}
		
		return classValue;
	}

	
	@Override
	protected String getHtmlAttribute(int iRow, int iField) {
		String attribute;
		
		attribute = super.getHtmlAttribute(iRow, iField);
		
		
		if (m_headerDescription.getColumnDescription(iField).getResID().contains("verbrauchbestellwert"))
		{
			attribute += getHtmlAttributeVerbrauch(Format.getDoubleValue(m_co.getField(
					((VirtCoProjekt)m_formAmpelliste.getCo()).getFieldVerbrauchBestellwert().getFieldDescription().getResID()).getValue()));
		}
		else if (m_headerDescription.getColumnDescription(iField).getResID().contains("verbrauchsollstunden"))
		{
			attribute += getHtmlAttributeVerbrauch(Format.getDoubleValue(m_co.getField(
					((VirtCoProjekt)m_formAmpelliste.getCo()).getFieldVerbrauchSollstunden().getFieldDescription().getResID()).getValue()));
		}

		return attribute;
	}


	/**
	 * Hintergrundfarbe/-bild aufgrund des Verbrauchs bestimmen
	 * 
	 * @param verbrauch
	 * @return
	 */
	protected String getHtmlAttributeVerbrauch(double verbrauch) {
		String attribute;

		attribute = "background='file://";

		if (verbrauch < 0.6)
		{
			attribute += "/" + Application.getWorkingDirectory().replace("\\", "/") + "gruen.png";
		}
		else if (verbrauch < 0.8)
		{
			attribute += "/" + Application.getWorkingDirectory().replace("\\", "/") + "gelb.png";
		}
		else
		{
			attribute += "/" + Application.getWorkingDirectory().replace("\\", "/") + "rot.png";
		}
		attribute += "'";

		return attribute;
	}

	
	@Override
	protected String getStand(){
		return "03/2023";
	}


}
