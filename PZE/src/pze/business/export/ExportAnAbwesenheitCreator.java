package pze.business.export;

import java.util.Date;
import java.util.GregorianCalendar;

import framework.Application;
import pze.business.Format;
import pze.business.Messages;
import pze.business.objects.auswertung.CoAuswertung;
import pze.business.objects.auswertung.CoAuswertungAnAbwesenheit;
import pze.business.objects.personen.CoBuchung;
import pze.business.objects.personen.CoKontowert;
import pze.business.objects.reftables.buchungen.CoBuchungsart;
import pze.ui.controls.SortedTableControl;
import pze.ui.formulare.UniFormWithSaveLogic;
import pze.ui.formulare.auswertung.FormAuswertung;


/**
 * Klasse zum Erstellen einer HTML-Datei für den PDF-Export der An-/Abwesenheit
 * 
 * @author Lisiecki
 */
public class ExportAnAbwesenheitCreator extends ExportPdfCreator {

	private static FormAuswertung m_formAuswertung;
	
	private StringBuilder m_sbTable;

	

	/**
	 * Datei immer im Querformat ausgeben
	 * 
	 * @see pze.business.export.ExportPdfCreator#isQuerformat()
	 */
	protected boolean isQuerformat() {
		return false;
	}


	
	/**
	 * Gibt die Übersicht in XHTML zurück.
	 * 
	 * @param formAuswertung
	 * @return
	 * @throws Exception 
	 */
	@Override
	public String createHtml(UniFormWithSaveLogic formAuswertung) throws Exception {
		m_formAuswertung = (FormAuswertung) formAuswertung;
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
		stringDaten = getHtmlStringTable(m_formAuswertung.getTable());
		if (stringDaten == null)
		{
			Messages.showErrorMessage("Keine Daten zur Ausgabe gefunden.");
			return false;
		}
		
//		m_sb.append("<div class='page'>\n");
		
		writeHeader();

//		m_sb.append("<div class='floatleft' style='width: 100%;'>\n");
		
		// Tabellenüberschrift
		writeTabellenBeschriftung();
		
		// Tabellendaten
		m_sb.append(stringDaten);
		
//		m_sb.append("</div>\n"); // floatleft
		
//		m_sb.append("</div>\n"); // page

		return true;
	}


	@Override
	protected String getClassTable(){
		return "feste_groesse fontsize9";
	}

	
	@Override
	/**
	 * Table-Style für die Datentabelle
	 * 
	 * @return
	 */
	protected String getStyleTable(){
		return "width:100%;";
	}


	/**
	 * Header mit Logo, Name, monat etc.
	 * 
	 * @throws Exception 
	 */
	protected void writeHeader() throws Exception {

//		m_sb.append("<div class='doben'>\n");
//		m_sb.append("<div class='dlinks'>\n");
		m_sb.append("<div style='width: 100%; white-space: no-wrap'>\n");
		m_sb.append("<div style='width: 66%; display: inline-block; text-align: left; vertical-align: top; '>\n");

		// Überschrift
		m_sb.append("<h1>An-/Abwesenheitsübersicht</h1>\n");
		
		// Projektdaten links
//		writeProjektdatenLinks();
	
		m_sb.append("</div>\n"); // links
		
		// Logo
//		m_sb.append("<div class='drechts'>\n");
		m_sb.append("<div style='width: 33%; display: inline-block; text-align: right'>\n");
		m_sb.append("<img src='/" + Application.getWorkingDirectory().replace("\\", "/") + "WTI_Logo_2023_Farbe.png'></img>\n");
		
		m_sb.append("</div>\n"); // rechts
		m_sb.append("</div>\n"); // oben
	}



	private void writeTabellenBeschriftung() {
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
		writeProjektbeschreibungLinksbuendig("Personenliste:", coAuswertung.getPersonenliste());
		m_sb.append("</table>\n");

	}


	/**
	 * String mit Table-Tag für das CO
	 * 
	 * @param table
	 * @return
	 * @throws Exception
	 */
	protected String getHtmlStringTable(SortedTableControl table) throws Exception {
		return getHtmlStringTableDaten(table);
	}
	

	/**
	 * Row-Tags mit Daten des CO
	 * 
	 * @return
	 * @throws Exception
	 */
	protected String getHtmlStringTableDaten(SortedTableControl table) throws Exception {
		int personID, lastPersonID, monat, lastMonat, tag, lastTag;
		String value, color;
		Date datum, lastDatum;
		CoKontowert coKontowert;
		CoAuswertungAnAbwesenheit coAuswertungAnAbwesenheit;
		
		m_sbTable = new StringBuilder();
		coKontowert = (CoKontowert) m_formAuswertung.getCo();
		coAuswertungAnAbwesenheit = (CoAuswertungAnAbwesenheit) m_formAuswertung.getCoAuswertung();
		
		if (!coKontowert.moveFirst())
		{
			return m_sbTable.toString();
		}
	

		lastPersonID = 0;
		lastMonat = 0;
		lastTag = 0;
		lastDatum = null;
	
		do 
		{
			personID = coKontowert.getPersonID();
			monat = coKontowert.getVirtMonat();
			tag = coKontowert.getVirtTag();
			datum = coKontowert.getDatum();

			
			// neuer Monat bedeutet neue Tabelle
			if (monat != lastMonat)
			{
				lastMonat = monat;
				lastPersonID = 0;

				// beim ersten Eintrag muss noch keine alte Tabelle geschlossen werden
				if (coKontowert.getCurrentRowIndex() > 0)
				{
					closeTable();
				}
				
				// Label mit Monat
				writeLabelMonat(monat);

				// neue Tabelle
				writeNewTableMitTagenDesMonats(datum);
			}

			
			// neue Person bedeutet neue Zeile
			if (personID != lastPersonID)
			{
				// beim ersten Eintrag jeder Tabelle muss noch keine alte Zeile geschlossen werden
				if (lastPersonID > 0)
				{
					// ggf. restliche Tage des Monats ausgeben
					writeLeereTage(lastTag+1, Format.getAnzTageDesMonats(lastDatum));

					m_sbTable.append("</tr>\n");
				}
				
				lastPersonID = personID;


				// Person in die erste Spalte der neuen Zeile schreiben
				m_sbTable.append("<tr>\n");
				m_sbTable.append("<td class='" + getClassTdDaten(0, 0) + "' " + getHtmlAttribute(0, 0) + ">");
				m_sbTable.append(coKontowert.getPerson());
				m_sbTable.append("</td>\n");
				
				
				// Tage bis zum 1. Tag der Auswertung ausgeben
				writeLeereTage(1, coKontowert.getVirtTag() - 1);
			}

			lastTag = tag;
			lastDatum = datum;
			value = null;
			color = null;
			

			// Tagesabwesenheiten nur ausgeben, wenn das entsprechende Feld markiert wurde, sonst wird es als Arbeitstag betrachtet
			// 2. Abfrage nicht in 1. if, weil die Sollarbeitszeit (z. B. bei Krank o. Lfz.) = 0 sein kann
			
			// Urlaub
			if (coKontowert.getAnzahlUrlaub() > 0)
			{
				if (coAuswertungAnAbwesenheit.isAnzahlUrlaubAusgebenAktiv())
				{
					value = "U";
					color = CoBuchungsart.COLOR_URLAUB;
				}
			}
			// Sonderurlaub
			else if (coKontowert.getAnzahlSonderurlaub() > 0)
			{
				if (coAuswertungAnAbwesenheit.isAnzahlSonderurlaubAusgebenAktiv())
				{
					value = "SU";
					color = CoBuchungsart.COLOR_SONDERURLAUB;
				}
			}
			// FA
			else if (coKontowert.getAnzahlFa() > 0)
			{
				if (coAuswertungAnAbwesenheit.isAnzahlFaAusgebenAktiv())
				{
					value = "FA";
					color = CoBuchungsart.COLOR_FA;
				}
			}
			// Elternzeit
			else if (coKontowert.getAnzahlElternzeit() > 0)
			{
				if (coAuswertungAnAbwesenheit.isAnzahlElternzeitAusgebenAktiv())
				{
					value = "E";
					color = CoBuchungsart.COLOR_ELTERNZEIT;
				}	
			}
			// Krank
			else if (coKontowert.getAnzahlKrank() > 0)
			{
				if (coAuswertungAnAbwesenheit.isAnzahlKrankAusgebenAktiv())
				{
					value = "K";
					color = CoBuchungsart.COLOR_KRANK;
				}
			}
			// Krank ohne Lfz.
			else if (coKontowert.getAnzahlKrankOhneLfz() > 0)
			{
				if (coAuswertungAnAbwesenheit.isAnzahlKrankOhneLfzAusgebenAktiv())
				{
					value = "K";
					color = CoBuchungsart.COLOR_KRANK_OHNE_LFZ;
				}
			}

			// Priorität bei Teilabwesenheit: Vorlesung, Dienstreise, Dienstgang, OFA
			
			// Vorlesung
			else if (coKontowert.getWertVorlesung() > 0 && coAuswertungAnAbwesenheit.isWertVorlesungAusgebenAktiv())
			{
				value = "Vo";
				color = CoBuchungsart.COLOR_VORLESUNG;
			}
			// Dienstreise
			else if (coKontowert.getWertDienstreise() > 0 && coAuswertungAnAbwesenheit.isWertDienstreiseAusgebenAktiv())
			{
				value = "DR";
				color = CoBuchungsart.COLOR_DIENSTREISE;
			}
			// Dienstgang
			else if (coKontowert.getWertDienstgang() > 0 && coAuswertungAnAbwesenheit.isWertDienstgangAusgebenAktiv())
			{
				value = "DG";
				color = CoBuchungsart.COLOR_DIENSTGANG;
			}
			// OFA
			else if (coAuswertungAnAbwesenheit.isWertOfaAusgebenAktiv() && CoBuchung.isOfa(personID, tag, monat, Format.getJahr(datum)))
			{
				value = "O";
				color = CoBuchungsart.COLOR_ORTSFLEX_ARBEITEN;
			}
			
			// Arbeitsfrei
			else if (coKontowert.getWertSollArbeitszeit() == 0)
			{
				value = null;
				color = CoBuchungsart.COLOR_ABWESEND;
			}
			// normaler Arbeitstag
			else
			{
				value = null;
				color = null;
			}
			
			if (value == null || value.isEmpty())
			{
				value = "&nbsp;";
			}
			
			m_sbTable.append("<td class='" + getClassTdDaten(0, 1) + "' " + getHtmlAttribute(0, 1) + (color == null ? "" : "bgcolor='" + color.replace("##", "#") + "' ") + ">");
			m_sbTable.append(value);
			m_sbTable.append("</td>\n");

			
		} while (coKontowert.moveNext());
		
		// ggf. restliche Tage des Monats ausgeben
		writeLeereTage(lastTag+1, Format.getAnzTageDesMonats(lastDatum));

		closeTable();

		return m_sbTable.toString();
	}



	private void writeLabelMonat(int monat) {
		m_sbTable.append("<div class='no-break'>\n");
//		m_sbTable.append("<div class='floatleft' style='width: 100%;'>\n");
		m_sbTable.append("<h3 style='margin-top: 10px;' > " + Format.getMonat(monat-1) + ":</h3>\n");
//		m_sbTable.append("</div>\n");
	}


	/**
	 * Neue Tabelle mit den Tagen des Monats erstellen
	 * 
	 * @param datum
	 */
	private void writeNewTableMitTagenDesMonats(Date datum) {
		int iTag;
		int anzTage;
		GregorianCalendar gregDatum;

		//				m_sb.append("<div class='floatleft' style='width: 100%; margin-top: 20px;'>\n");
		m_sbTable.append("<table class='" + getClassTable() + "' style=' " + getStyleTable() + "'>\n");
		m_sbTable.append("<tr>\n");

		
		// Beschriftung der Tabelle ("Person")
		m_sbTable.append("<td class='" + getClassTdDaten(0, 0) + "' " + getHtmlAttribute(0, 0) + ">");
		m_sbTable.append("Person");
		m_sbTable.append("</td>\n");

		
		// Tage des Monats und Wochentage ausgeben
		gregDatum = Format.getGregorianCalendar(datum);
		anzTage = Format.getAnzTageDesMonats(datum);
		for (iTag=1; iTag<=anzTage; ++iTag)
		{
			gregDatum.set(GregorianCalendar.DAY_OF_MONTH, iTag);

			m_sbTable.append("<td class='" + getClassTdDaten(0, 1) + "' " + getHtmlAttribute(0, 1) + ">");
			m_sbTable.append(iTag + "<br />" + Format.getWochentagAbkuerzung(gregDatum));
			m_sbTable.append("</td>\n");
		}

		m_sbTable.append("</tr>\n");
	}


	/**
	 * leere Tage (Zellen) ausgeben
	 * 
	 * @param iTag 1. Tag
	 * @param anzTage letzter Tag
	 * @return
	 */
	private void writeLeereTage(int iTag, int anzTage) {
		
		for (; iTag<=anzTage; ++iTag)
		{
			m_sbTable.append("<td class='" + getClassTdDaten(0, 1) + "' " + getHtmlAttribute(0, 1) 
				+ " bgcolor='" + CoBuchungsart.COLOR_ABWESEND.replace("##", "#") + "' >");
			m_sbTable.append("</td>\n");
		}
	}


	/**
	 * letzte Zeile und Tabelle schließen
	 */
	private void closeTable() {
		m_sbTable.append("</tr>\n");
		m_sbTable.append("</table>\n");
		m_sbTable.append("</div>\n");
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
		
		classValue += (iField == 0 ? "textlinks" : "textmitte ");
		classValue += getClassSpaltenbreite(iField, classValue);
		
		return classValue;
	}



	private String getClassSpaltenbreite(int iField, String classValue) {
		if (iField == 0) // Person
		{
			return " breite95px";
		}
		else
		{
			return " breite10px";
		}
	}


}
