package pze.business.export;

import java.util.Calendar;
import java.util.GregorianCalendar;

import framework.Application;
import pze.business.Format;
import pze.business.objects.AbstractCacheObject;
import pze.business.objects.personen.CoKontowert;
import pze.business.objects.personen.CoPerson;
import pze.business.objects.personen.monatseinsatzblatt.CoMonatseinsatzblatt;


/**
 * Klasse zum Erstellen einer HTML-Datei für den PDF-Export Antrag Auszahlung Überstunden
 * 
 * @author Lisiecki
 */
public class ExportAuszahlungUeberstundenCreator extends ExportPdfCreator {

	private CoKontowert m_coKontowert;

	
	/**
	 * Gibt einen Antrag auf Auszahlung von Überstunden in XHTML zurück.
	 * 
	 * @param coKontowert
	 * @return
	 * @throws Exception 
	 */
	@Override
	public String createHtml(AbstractCacheObject coKontowert) throws Exception {
		m_coKontowert = (CoKontowert) coKontowert;
		m_sb = new StringBuilder();
		
		writeHtmlOpen();
		
		// Footer am Beginn schreiben, da es eine allgemeine Darstellung ist
		writeFooter();
		
		writeSeite();

		writeHtmlClose();

		return m_sb.toString();
	}


	/**
	 * Open-Tag und Einbindung CSS
	 */
	protected void writeHtmlOpen() {
		m_sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"" 
				+ " \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n "
				+ "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n");
		
		// Einbindung der CSS-Datei
		m_sb.append("<head>\n");
		m_sb.append("<link rel='stylesheet' href='/" + Application.getWorkingDirectory().replace("\\", "/") + "PdfExport.css' ></link>\n");
		m_sb.append("<link rel='stylesheet' href='/" + Application.getWorkingDirectory().replace("\\", "/") + "PdfExportAuszahlungUeberstunden.css' ></link>\n");
		m_sb.append("<link rel='stylesheet' href='/" + Application.getWorkingDirectory().replace("\\", "/") + "FooterStand.css' ></link>\n");
		if (isQuerformat())
		{
			m_sb.append("<style type='text/css'> @page {size: landscape} </style>");
		}
		m_sb.append("</head>\n");
		m_sb.append("<body>\n");
	}


	/**
	 * Eine Seite erstellen
	 * 
	 * @return Daten gefunden
	 * @throws Exception
	 */
	private boolean writeSeite() throws Exception {
		
		// zuerst prüfen, ob Daten für das Projekt vorhanden sind
		if (m_coKontowert.isEmpty())
		{
			return false;
		}
		
		m_sb.append("<div class='containerVertikal'> <!-- Gesamte Seite -->\n");
		m_sb.append("<div class='containerVertikal dickerRahmen'> <!-- Anfang eingerahmter Bereich --> \n");
		
		writeUeberschrift();

		writeName();
		writeAnzahlStunden();
		writeStandZeitguthaben();

		// Unterschrift
		writeUnterschriftAntragsteller();
		
		// Tabelle mit Zuordnung Stunden-Projekte
		writeTableProjektzuordnung();
//		writeTableProjektzuordnung("<u>Reisestunden</u>");

		// Unterschrift
		writeUnterschriftLeiter();

		// Anlage Monatseinsatzplatz
		writeAnlageMonatseinsatzblatt();
		
		m_sb.append("</div> <!-- Anfang eingerahmter Bereich --> \n");

		// Unterschriftenzeile "gebucht am"
		writeUnterschriftGebuchtAm();
		
		m_sb.append("</div> <!-- Gesamte Seite -->\n");
		
		return true;
	}


	/**
	 * Überschrift des Dokuments
	 */
	protected void writeUeberschrift() {
		m_sb.append("		<div class='volle_breite borderBottom platz_nach_unten'>\n");
		m_sb.append("			<h1 class='echter_h1 textmitte'>Antrag auf Verg&uuml;tung von Plusstunden</h1>\n");
		m_sb.append("		</div>\n");
		m_sb.append("\n");
	}


	/**
	 * Name der Person
	 * 
	 * @throws Exception 
	 */
	protected void writeName() throws Exception {
		CoPerson coPerson;
		
		coPerson = new CoPerson();
		coPerson.loadByID(m_coKontowert.getPersonID());
		
		m_sb.append("		<div class='containerHorizontal eingerueckt volle_breite  borderBottomDuenn platz_nach_unten' style='padding-top:2px'>\n");
		m_sb.append("			<table class='ohne_rand'>				<tr>");
		m_sb.append("					<td class='unsichtbar'>");
		m_sb.append("			<h2 class='ueberstunden_h2' style='text-align:left'>Name: " + coPerson.getNachnameVorname() + "</h2></td>\n");
		m_sb.append("					<td class='unsichtbar'>");
		m_sb.append("			<h2 class='ueberstunden_h2' style='text-align:left'>Personal-Nr.: " + coPerson.getPersonalnummer() + "</h2></td></tr>\n");
		m_sb.append("			</table>\n");
		m_sb.append("		</div>\n");
		m_sb.append("\n");
	}


	/**
	 * Anzahl der auszuzahlenden Stunden
	 */
	protected void writeAnzahlStunden() {
		int auszahlungProjekt, auszahlungReise;
		
		auszahlungProjekt = m_coKontowert.getWertAuszahlungUeberstundenProjekt();
		auszahlungReise = m_coKontowert.getWertAuszahlungUeberstundenReise();
		
		m_sb.append("		<div class='eingerueckt volle_breite borderBottomDuenn platz_nach_unten' style='padding-top:2px'>\n");
//		m_sb.append("			<h2 class='ueberstunden_h2' style='line-height:2'>Hiermit beantrage ich die Verg&uuml;tung von "
//				+ "<u>" + Format.getZeitAsText(m_coKontowert.getWertAuszahlungUeberstunden()) + "</u> Stunden "
//				+ "(davon <u>" + Format.getZeitAsText(0) + "</u> Reisestunden)<br/>"
		//				+ " meines Arbeitszeitguthabens mit der n&auml;chsten Gehaltsabrechnung.</h2>\n");
		m_sb.append("			<h2 class='ueberstunden_h2' style='line-height:2'>Hiermit beantrage ich die Verg&uuml;tung von "
				+ "<span style='background-color:#FFFF00'><u>" + Format.getZeitAsText(auszahlungProjekt + auszahlungReise) + "</u></span> Stunden "
				+ "(<span style='background-color:#FFFF00'><u>" + Format.getZeitAsText(auszahlungProjekt) + "</u></span> Projektstunden "
				+ "und <span style='background-color:#FFFF00'><u>" + Format.getZeitAsText(auszahlungReise) + "</u></span> Reisestunden)"
				+ " meines Arbeitszeitguthabens mit der n&auml;chsten Gehaltsabrechnung.</h2>\n");
		m_sb.append("		</div>\n");
		m_sb.append("\n");
	}


	/**
	 * Aktueller Stand des Gleitzeitguthabens
	 */
	protected void writeStandZeitguthaben() {
		int plusstundenProjekt, plusstundenReise;
		
		plusstundenProjekt = m_coKontowert.getWertAuszahlbareUeberstundenProjekt();
		plusstundenReise = m_coKontowert.getWertAuszahlbareUeberstundenReise();

		
		m_sb.append("		<div class='eingerueckt volle_breite padding-oben' style='margin-bottom: 0px'> <!-- Arbeitszeitguthaben -->\n");

		m_sb.append("<h2 class='ueberstunden_h2' style='line-height:2'>Mein Arbeitszeitguthaben betr&auml;gt per Monatsende " +  getStringMonatJahr() + ": " 
				+  "<span style='background-color:#FFFF00'><u>" + Format.getZeitAsText(m_coKontowert.getWertUeberstundenGesamt()) + "</u></span> Stunden.<br/>"
//				+ "Plusstunden in diesem Monat: " + Format.getZeitAsText(m_coKontowert.getWertAuszahlbareUeberstunden())
				//				+ " (davon " + Format.getZeitAsText(0) + " Reisestunden)"
				+ "Auszahlbare Plusstunden in diesem Monat: <span style='background-color:#FFFF00'><u>" + Format.getZeitAsText(plusstundenProjekt + plusstundenReise)
				+ "</u></span> Stunden (<span style='background-color:#FFFF00'><u>" + Format.getZeitAsText(plusstundenProjekt) + "</u></span> Projektstunden "
				+ "und <span style='background-color:#FFFF00'><u>" + Format.getZeitAsText(plusstundenReise) + "</u></span> Reisestunden)<br/>"
				+ "Bei den geleisteten Plusstunden handelt es sich um genehmigte und <u>abrechenbare Stunden</u>.\n"
				+ "</h2>\n");

		m_sb.append("			<div style='margin-bottom:30px'><span class='platz'></span></div>\n");
		m_sb.append("		</div> \n");
		m_sb.append("\n");
	}


	/**
	 * Unterschiftenzeile "Datum, Unterschrift -person-"
	 * 
	 */
	protected void writeUnterschriftAntragsteller() {
		writeUnterschrift("			<div class='gleichverteilen' />\n"
				+ "			<div class='gleichverteilen'/>\n"
				+ "			<div class='gleichverteilen DatumUnterschrift'>Datum, Unterschrift Antragsteller</div>\n"
				+ "\n");
	}


	/**
	 * Unterschiftenzeile "Datum, Unterschrift -person-"
	 * 
	 */
	protected void writeUnterschriftLeiter() {
		writeUnterschrift("			<div>"
				+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; "
				+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
				+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
				+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
//				+ "Datum, Unterschrift GL "
				
				+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
				+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
				+ "Datum, Unterschrift AL"
				
				+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
				+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
				+ "Datum, Unterschrift KL"
				+ "			</div>");
	}


	/**
	 * Unterschiftenzeile "Datum, Unterschrift -person-"
	 * 
	 * @param divPersonen 
	 */
	protected void writeUnterschrift(String divPersonen) {
		m_sb.append("		<div class='containerHorizontal volle_breite borderTopDuenn platz_nach_unten' style='margin-bottom: 20px; margin-top: 60px'>");
		m_sb.append(divPersonen);
		m_sb.append("		</div>\n");
		m_sb.append("\n");
	}


	/**
	 * Tabelle zur Zuordnung der Überstunden zu Projekten
	 * 
	 * @throws Exception 
	 */
	protected void writeTableProjektzuordnung() throws Exception {
		m_sb.append("		<div class='eingerueckt platz_nach_unten volle_breite' style='margin-top: 20px'>\n");
		m_sb.append("			<h2 class='ueberstunden_h2'>Stundenübersicht " + getStringMonatJahr() + ":</h2>\n");
		m_sb.append("			<table class='ohne_rand table-gross' style='margin-top: 20px'>\n");
		
		// Überschrift
		m_sb.append("			<tr>\n");
		m_sb.append("				<td class='spalte1 unsichtbar'></td>\n");
		m_sb.append("				<td class='spalte2 unsichtbar'></td>\n");
		m_sb.append("				<td class='spalte3'>Projektstunden</td>\n");
		m_sb.append("				<td class='spalte3'>Reisestunden</td>\n");
		m_sb.append("				<td class='spalte4'>Auszahlung Projektstunden</td>\n");
		m_sb.append("				<td class='spalte5'>Auszahlung Reisestunden</td>\n");
		m_sb.append("			</tr>\n");

		writeAuftraegeProjektzuordnung();
		
		m_sb.append("		</table>\n");
		m_sb.append("		</div>\n");
		m_sb.append("\n");
	}


	protected void writeAuftraegeProjektzuordnung() throws Exception {
		CoMonatseinsatzblatt coMonatseinsatzblatt;
		coMonatseinsatzblatt = new CoMonatseinsatzblatt();
		coMonatseinsatzblatt.loadAuftraegeAuszahlung(m_coKontowert.getPersonID(), m_coKontowert.getDatum());
		
		// Aufträge rausschreiben
		if (coMonatseinsatzblatt.moveFirst())
		{
			do
			{
				writeZeileProjektzuordnung(coMonatseinsatzblatt.getFieldAuftrag().getDisplayValue(), 
						coMonatseinsatzblatt.getWertProjektZeit(), coMonatseinsatzblatt.getWertReiseZeit());
			} while (coMonatseinsatzblatt.moveNext());
		}
	}


	/**
	 * Leere Zeile der Tabelle zur Zuordnung der Überstunden zu Projekten
	 * @param auftrag 
	 * @param stunden 
	 */
	protected void writeZeileProjektzuordnung(String auftrag, int projektStunden, int reiseStunden) {
		m_sb.append("			<tr>\n");
		m_sb.append("				<td class='spalte1'>WTI-Auftragsnr.:</td>\n");
		m_sb.append("				<td class='spalte2'>" + auftrag + "</td>\n");
		m_sb.append("				<td class='spalte3'>" + Format.getZeitAsText(projektStunden) + "</td>\n");
		m_sb.append("				<td class='spalte3'>" + Format.getZeitAsText(reiseStunden) + "</td>\n");
		m_sb.append("				<td class='spalte4'></td>\n");
		m_sb.append("				<td class='spalte5'></td>\n");
		m_sb.append("			</tr>\n");
	}


	/**
	 * Hinweis zum Monatseinsatzblatt gelb markiert
	 */
	protected void writeAnlageMonatseinsatzblatt() {
		m_sb.append("		<div class='eingerueckt'> <!-- Anlage -->\n");
		m_sb.append("			<p><span style='background-color:#FFFF00'>Anlage: Monatseinsatzblatt</span></p>\n");
		m_sb.append("		</div>\n");
		m_sb.append("\n");
	}


	/**
	 * Linie für die Unterschrift nach der Buchung
	 */
	protected void writeUnterschriftGebuchtAm() {
		m_sb.append("<div style='margin-top:50px'> <!-- gebucht am -->\n");
		m_sb.append("	<div class='containerHorizontal volle_breite' style='margin-bottom: 0px'> <!-- Linie für Unterschrift -->\n");
		m_sb.append("		<div class='gleichverteilen'></div>\n");
		m_sb.append("		<div class='gleichverteilen1'>gebucht am:</div>\n");
		m_sb.append("		<div class='gleichverteilen'>\n");
		m_sb.append("			<span class='blank'></span>\n");
		m_sb.append("		</div>\n");
		m_sb.append("	</div>\n");
		m_sb.append("	<div class='containerHorizontal volle_breite' style='margin-top: 0px; padding-top: 0px'> 	<!-- Datum, Unterschrift -->\n");
		m_sb.append("		<div class='unsichtbarFeld'></div>\n");
		m_sb.append("		<div class='unsichtbarFeld'></div>\n");
		m_sb.append("		<div class='unterschriftFeld borderTopDuenn' id='Personalabteilung'>\n");
		m_sb.append("			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\n");
		m_sb.append("			Datum, Unterschrift Personalabteilung\n");
		m_sb.append("		</div>\n");
		m_sb.append("	</div>\n");
		m_sb.append("</div>\n");
		m_sb.append("\n");
	}


	/**
	 * String im Format Monat/Jahr
	 * 
	 * @return
	 */
	protected String getStringMonatJahr() {
		GregorianCalendar gregDatum;
		
		gregDatum = Format.getGregorianCalendar(m_coKontowert.getDatum());
		
		return (gregDatum.get(Calendar.MONTH)+1) + "/" + gregDatum.get(Calendar.YEAR);
	}


	@Override
	protected String getStand(){
		return "03/23";
	}

}
