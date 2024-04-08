package pze.business.export;

import java.util.HashMap;
import java.util.Map;

import framework.Application;
import pze.business.Format;
import pze.business.Messages;
import pze.business.objects.personen.CoPerson;
import pze.business.objects.personen.monatseinsatzblatt.CoMonatseinsatzblatt;
import pze.business.objects.personen.monatseinsatzblatt.CoMonatseinsatzblattAnzeige;
import pze.business.objects.projektverwaltung.CoAuftrag;
import pze.business.objects.projektverwaltung.VirtCoProjekt;
import pze.ui.formulare.UniFormWithSaveLogic;
import pze.ui.formulare.person.monatseinsatzblatt.FormPersonMonatseinsatzblatt;
import pze.ui.formulare.person.monatseinsatzblatt.TableMonatseinsatzblatt;


/**
 * Klasse zum Erstellen einer HTML-Datei für den PDF-Export des Tätigkeitsnachweises
 * 
 * @author Lisiecki
 */
public class ExportTaetigkeitsnachweisCreator extends ExportPdfCreator {

	private FormPersonMonatseinsatzblatt m_formPersonMonatseinsatzblatt;
	
	protected CoPerson m_coPerson;
	protected CoMonatseinsatzblattAnzeige m_coMonatseinsatzblattAnzeige;

	protected int m_kggProjektNr;
	protected Map<Integer, Integer> m_mapKggZeit;
	protected Map<Integer, String> m_mapKggTaetigkeiten;
	
	
	/**
	 * Gibt eine Tätigkeitsnachweis-Tabelle in XHTML zurück.
	 * 
	 * @param formPersonMonatseinsatzblatt
	 * @return
	 * @throws Exception 
	 */
	@Override
	public String createHtml(UniFormWithSaveLogic formPersonMonatseinsatzblatt) throws Exception {
		int anzProjekteMitDaten;
		
		m_sb = new StringBuilder();
		m_mapKggZeit = new HashMap<Integer, Integer>();
		m_mapKggTaetigkeiten = new HashMap<Integer, String>();
		
		writeHtmlOpen();
		
		// Footer am Beginn schreiben, da es eine allgemeine Darstellung ist
		writeFooter();

		// Daten schreiben
		anzProjekteMitDaten = writeSeiten(formPersonMonatseinsatzblatt);
		
		// prüfen, ob Tätigkeiten gefunden wurden
		if (anzProjekteMitDaten == 0)
		{
			Messages.showErrorMessage("Keine Daten gefunden", "Es konnten keine Daten zur Erstellung des Tätigkeitsnachweises gefunden werden.");
			return null;
		}

		writeZeileUnterschrift();

		writeHtmlClose();

		return m_sb.toString();
	}


	/**
	 * Alle Seiten mit den Daten der Tätigkeitsnachweise erstellen
	 * 
	 * @param formPersonMonatseinsatzblatt
	 * @return Anzahl Seiten mit Tätigkeiten
	 * @throws Exception
	 */
	protected int writeSeiten(UniFormWithSaveLogic formPersonMonatseinsatzblatt) throws Exception {
		int iProjekt;
		int anzProjekte;
		int anzProjekteMitDaten;
		
		m_formPersonMonatseinsatzblatt = (FormPersonMonatseinsatzblatt) formPersonMonatseinsatzblatt;
		m_coPerson = m_formPersonMonatseinsatzblatt.getCoPerson();
		m_coMonatseinsatzblattAnzeige = m_formPersonMonatseinsatzblatt.getCoMonatseinsatzblattAnzeige();

		
		// Anzahl Projekte prüfen
		anzProjekte = getAnzProjekte();
		if (anzProjekte == 0)
		{
			return 0;
		}
		
		// Seiten der Tätigkeitsnachweise erstellen
		anzProjekteMitDaten = 0;
		for (iProjekt=0; iProjekt<anzProjekte; ++iProjekt)
		{
			if (writeSeite(iProjekt, anzProjekte))
			{
				++anzProjekteMitDaten;
			}
			
			// ggf. die Nr. der KGG-Projekte speichern
			if (m_kggProjektNr == 0 && m_mapKggTaetigkeiten.size() > 0)
			{
				m_kggProjektNr = iProjekt;
			}
		}
		
		// KGG-Tätigkeitsnachweis
		if (m_kggProjektNr > 0)
		{
			++anzProjekteMitDaten;
			writeSeite(m_kggProjektNr, anzProjekte, getStringTabellenDatenKGG());
		}
		
		return anzProjekteMitDaten;
	}


	/**
	 * Eine Seite des Monatseinsatzblattes erstellen
	 * 
	 * @param iProjekt
	 * @param anzProjekte
	 * @return Daten gefunden
	 * @throws Exception
	 */
	protected boolean writeSeite(int iProjekt, int anzProjekte) throws Exception {
		return writeSeite(iProjekt, anzProjekte, getStringTabellenDaten(iProjekt));
	}


	/**
	 * Eine Seite des Monatseinsatzblattes erstellen
	 * 
	 * @param iProjekt
	 * @param anzProjekte
	 * @return Daten gefunden
	 * @throws Exception
	 */
	protected boolean writeSeite(int iProjekt, int anzProjekte, String stringDaten) throws Exception {
		
		// zuerst prüfen, ob Daten für das Projekt vorhanden sind
		if (stringDaten == null)
		{
			return false;
		}
		
		// prüfen, ob der Tätigkeitsnachweis erstellt werden soll
		VirtCoProjekt virtCoProjekt;
		virtCoProjekt = getVirtCoProjekt();
		virtCoProjekt.moveTo(iProjekt);
		
		if (virtCoProjekt.getBerichtsNrID() > 0 && iProjekt != m_kggProjektNr)
		{
			return false;
		}
		
		
		writeHeader(iProjekt, anzProjekte);

		m_sb.append("<div class='floatleft' style='width: 100%; margin-top: 20px;'>\n");

		writeTabellenUeberschriften();
		
		m_sb.append(stringDaten);

		m_sb.append("</div>\n"); // floatleft
		
		return true;
	}


	/**
	 * Footer ohne Seitenzahl einbinden
	 * 
	 * @see pze.business.export.ExportPdfCreator#writeHtmlOpen()
	 */
	@Override
	protected void writeHtmlOpen() {
		m_sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"" 
				+ " \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n "
				+ "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n");
		
		// Einbindung der CSS-Datei
		m_sb.append("<head>\n");
		m_sb.append("<link rel='stylesheet' href='/" + Application.getWorkingDirectory().replace("\\", "/") + "PdfExport.css' >\n</link>\n");
		m_sb.append("<link rel='stylesheet' href='/" + Application.getWorkingDirectory().replace("\\", "/") + "FooterStand.css' >\n</link>\n");
		m_sb.append("</head>\n");
		m_sb.append("<body>\n");
	}

	
	/**
	 * Header mit Logo, Name, monat etc.
	 * 
	 * @param seite
	 * @param anzSeiten
	 * @throws Exception 
	 */
	private void writeHeader(int seite, int anzSeiten) throws Exception {
		m_sb.append("<div class='page'>\n");
		m_sb.append("<div class='doben'>\n");
		m_sb.append("<div class='dlinks'>\n");

		// Überschrift Monatseinsatzblatt
		m_sb.append("<h1>Tätigkeitsnachweis</h1>\n");
		
		// Monat
		m_sb.append("<table class='textspalten unsichtbar'>");
		m_sb.append("<tr><td class='unsichtbar'><h3>Monat:&nbsp;</h3></td><td class='unsichtbar'><h3>" 
				+ getMonat() + "</h3></td></tr>\n");

		// Name
		m_sb.append("<tr><td class='unsichtbar'><h3>Name:&nbsp;</h3></td><td class='unsichtbar'><h3>" 
				+ getName() + "</h3></td></tr>\n");
		m_sb.append("</table>\n");
		m_sb.append("</div>\n"); // links
		
		// Logo
		m_sb.append("<div class='drechts'>\n");
		m_sb.append("<img src='/" + Application.getWorkingDirectory().replace("\\", "/") + "WTI_Logo_2023_Farbe.png'></img><br /><br />\n");
		
		// Seite
//		m_sb.append("<h3 style='margin-left: 70px'>Seite " + seite + " von " + anzSeiten + "</h3>\n");
		
		// Projektbeschreibung
		writeProjektbeschreibung(seite);
		
		m_sb.append("</div>\n"); // rechts
		m_sb.append("</div>\n"); // oben
	}


	/**
	 * Monat des Tätigkeitsnachweises
	 * 
	 * @return
	 */
	protected String getMonat() {
		return m_formPersonMonatseinsatzblatt.getCurrentMonat();
	}


	/**
	 * Aktuelle Person des Tätigkeitsnachweises
	 * 
	 * @return
	 */
	protected String getName() {
		return m_formPersonMonatseinsatzblatt.getCoPerson().getBezeichnung();
	}


	/**
	 * Projektbeschreibung
	 * 
	 * @param iProjekt
	 * @throws Exception
	 */
	private void writeProjektbeschreibung(int iProjekt) throws Exception {
		boolean isKGG;
		VirtCoProjekt virtCoProjekt;
		CoAuftrag coAuftrag;

		isKGG = (iProjekt == m_kggProjektNr) && m_mapKggTaetigkeiten.size() > 0;
		virtCoProjekt = getVirtCoProjekt();
		virtCoProjekt.moveTo(iProjekt);

		coAuftrag = new CoAuftrag();
		coAuftrag.loadByID(virtCoProjekt.getAuftragID());

		m_sb.append("<table class='unsichtbar'>\n");
		writeProjektbeschreibung("Kunde:", Format.getConformXml(virtCoProjekt.getKunde()));
		writeProjektbeschreibung("Auftrags-Nr:", virtCoProjekt.getAuftragsNr());
		writeProjektbeschreibung("Bestell-Nr.:", coAuftrag.getBestellNr());
		writeProjektbeschreibung("Abruf-Nr:", virtCoProjekt.getAbrufNr());
		writeProjektbeschreibung("Kostenstelle:", isKGG ? null : virtCoProjekt.getKostenstelle());
		writeProjektbeschreibung("Stundenart:", isKGG ? null : virtCoProjekt.getStundenart());
		m_sb.append("</table>\n");

	}


	/**
	 * Eintrag der Projektbeschreibung schreiben
	 * 
	 * @param beschriftung
	 * @param wert
	 */
	private void writeProjektbeschreibung(String beschriftung, String wert) {
		m_sb.append("<tr><td class='unsichtbar textlinks'>" + beschriftung + "&nbsp;</td><td class='unsichtbar textlinks'>" 
				+ (wert == null ? "" : wert) + "</td></tr>\n");
	}


	/**
	 * Überschriften der Tabelle
	 * 
	 * @param seite 
	 */
	private void writeTabellenUeberschriften() {
		
		m_sb.append("<table>\n");
		m_sb.append("<tr>\n");
		
		m_sb.append("<th class='tnlinks'>Datum</th>\n");
		m_sb.append("<th class='tnlinks'>Std.</th>\n");
		m_sb.append("<th class='rechts'>Tätigkeitsnachweis</th>\n");
		m_sb.append("</tr>\n");
	}


	/**
	 * Html-Tabelle der Projektstunden der Tabelle oder null, wenn keine Daten für das Projekt vorhanden sind
	 * 
	 * @param iProjekt 
	 * @return
	 * @throws Exception
	 */
	private String getStringTabellenDaten(int iProjekt) throws Exception {
		int tag;
		int anzTage;
		int iField, startFieldProjektdaten;
		boolean isKGG;
		String color, stunden, taetigkeit, kggTaetigkeit;
		StringBuilder sb;
		VirtCoProjekt virtCoProjekt;
		CoMonatseinsatzblatt coMonatseinsatzblatt;
		
		sb = new StringBuilder();
		stunden = null;
		
		coMonatseinsatzblatt = m_coMonatseinsatzblattAnzeige.getCoMonatseinsatzblatt();
		
		virtCoProjekt = getVirtCoProjekt();
		virtCoProjekt.moveTo(iProjekt);
		isKGG = virtCoProjekt.getBerichtsNrID() > 0;

		anzTage = Format.getAnzTageDesMonats(m_coMonatseinsatzblattAnzeige.getDatum());
		startFieldProjektdaten = m_coMonatseinsatzblattAnzeige.getStartindexProjektspalten();
		m_coMonatseinsatzblattAnzeige.moveToTag(1);
		iField = startFieldProjektdaten + iProjekt;
	
		for (tag=1; tag<=anzTage+1; ++tag) // +1 für Summe
		{
			// Farbe für Arbeitstag/kein Arbeitstag
			color = "";
			if (!m_coPerson.isArbeitstag(m_coMonatseinsatzblattAnzeige.getDatum())) 
			{
				color = " bgcolor='" + TableMonatseinsatzblatt.COLOR_ARBEITSFREI.replace("##", "#") + "' ";
			}

			// Tag des Monats
			sb.append("<tr><td class='tnlinks umrandet'" + color + ">").append(tag > anzTage ? "Summe" : tag).append("</td>\n");

			// Projektstunden
			stunden = m_coMonatseinsatzblattAnzeige.getField(iField).getDisplayValue();
			sb.append("<td class='tnlinks" + (tag > anzTage ? " umrandet" : "") + "'" + color + ">").append(stunden).append("</td>\n");
			
			// Tätigkeit
			if (coMonatseinsatzblatt.moveTo(virtCoProjekt, tag))
			{
				taetigkeit = (stunden.isEmpty() ? null : coMonatseinsatzblatt.getTaetigkeit());
				
				// KGG-Daten speichern
				if (isKGG && taetigkeit != null)
				{
					// Zeit je Tag summieren
					m_mapKggZeit.put(tag, Format.getIntValue(m_mapKggZeit.get(tag)) + Format.getZeitAsInt(stunden));
					
					// Tätigkeiten je Tag speichern
					kggTaetigkeit = m_mapKggTaetigkeiten.get(tag);
					m_mapKggTaetigkeiten.put(tag, (kggTaetigkeit == null ? "" : kggTaetigkeit + "<br/>")
							+ stunden + " Std.: " + virtCoProjekt.getStundenart() + " Dokumentation " + virtCoProjekt.getBerichtsNr() 
							+ " (" + virtCoProjekt.getKostenstelle() + ")");
				}
			}
			else
			{
				taetigkeit = null;
			}
			taetigkeit = Format.getConformXml(taetigkeit); // TODO überall abfragen -> append Funktion
			sb.append("<td class='tnrechts" + (tag > anzTage ? " umrandet" : "") + "'" + color + ">").append(
					taetigkeit == null ? "" : taetigkeit).append("</td>\n");

			sb.append("</tr>\n");
			
			// KGG-Summe speichern, Zeit je Tag summieren
			if (isKGG &&tag > anzTage)
			{
				m_mapKggZeit.put(tag, Format.getIntValue(m_mapKggZeit.get(tag)) + Format.getZeitAsInt(stunden));
			}
			
			m_coMonatseinsatzblattAnzeige.moveNext();
		}
		
		sb.append("</table>\n");
		sb.append("</div>\n"); // page
		
		// anhand der Summe prüfen, ob Daten vorhanden sind
		return (stunden == null || stunden.isEmpty() || Format.getZeitAsInt(stunden) == 0 ? null : sb.toString());
	}


	/**
	 * Html-Tabelle der Projektstunden der Tabelle oder null, wenn keine Daten für das Projekt vorhanden sind
	 * 
	 * @return
	 * @throws Exception
	 */
	protected String getStringTabellenDatenKGG() throws Exception {
		int tag;
		int anzTage, zeit;
		String color, stunden, taetigkeit;
		StringBuilder sb;
		
		sb = new StringBuilder();
		stunden = null;
		
		anzTage = Format.getAnzTageDesMonats(m_coMonatseinsatzblattAnzeige.getDatum());
		m_coMonatseinsatzblattAnzeige.moveToTag(1);
	
		for (tag=1; tag<=anzTage+1; ++tag) // +1 für Summe
		{
			// Farbe für Arbeitstag/kein Arbeitstag
			color = "";
			if (!m_coPerson.isArbeitstag(m_coMonatseinsatzblattAnzeige.getDatum())) 
			{
				color = " bgcolor='" + TableMonatseinsatzblatt.COLOR_ARBEITSFREI.replace("##", "#") + "' ";
			}

			// Tag des Monats
			sb.append("<tr><td class='tnlinks umrandet'" + color + ">").append(tag > anzTage ? "Summe" : tag).append("</td>\n");

			// Projektstunden
			zeit = m_mapKggZeit.containsKey(tag) ? m_mapKggZeit.get(tag) : 0;
			stunden = zeit > 0 ? Format.getZeitAsText(zeit) : "";
			sb.append("<td class='tnlinks" + (tag > anzTage ? " umrandet" : "") + "'" + color + ">").append(stunden).append("</td>\n");
			
			// Tätigkeit
			taetigkeit = m_mapKggTaetigkeiten.containsKey(tag) ? m_mapKggTaetigkeiten.get(tag) : null;
			taetigkeit = Format.getConformXml(taetigkeit); // TODO überall abfragen -> append Funktion
			sb.append("<td class='tnrechts" + (tag > anzTage ? " umrandet" : "") + "'" + color + ">").append(
					taetigkeit == null ? "" : taetigkeit).append("</td>\n");

			sb.append("</tr>\n");
			
			m_coMonatseinsatzblattAnzeige.moveNext();
		}
		
		sb.append("</table>\n");
		sb.append("</div>\n"); // page
		
		return sb.toString();
	}


	/**
	 * Zeile für die Unterschrift
	 */
	private void writeZeileUnterschrift() {
		
		m_sb.append("<footer>\n");
		m_sb.append("<div>\n");
		m_sb.append("<span class='blank'></span>\n");
		m_sb.append("<span class='blank'></span>\n");
		m_sb.append("<span class='unterstrich'></span>\n");
		m_sb.append("<span class='blank'></span>\n");
		m_sb.append("<span class='blank'></span>\n");
		m_sb.append("<span class='unterstrich'></span>\n");
		m_sb.append("</div>\n");
		
		m_sb.append("<div>\n");
		m_sb.append("<span class='blank'></span>\n");
		m_sb.append("<span class='blank'></span>\n");
		m_sb.append("<span class='text'>Auftraggeber-Gegenzeichnung</span>\n");
		m_sb.append("<span class='blank'></span>\n");
		m_sb.append("<span class='blank'></span>\n");
		m_sb.append("<span class='text'>WTI-Gegenzeichnung</span>\n");
		m_sb.append("</div>\n");
		m_sb.append("</footer>\n");
	}


	@Override
	protected String getStand(){
		return "01/2024";
	}


	/**
	 * Co mit Bookmark auf dem ersten Projekt für diese Seite
	 * 
	 * @param seite
	 * @return
	 */
	protected VirtCoProjekt getVirtCoProjekt() {
		VirtCoProjekt virtCoProjekt;
		
		virtCoProjekt = m_formPersonMonatseinsatzblatt.getCoMonatseinsatzblattAnzeige().getVirtCoProjekt();
		virtCoProjekt.moveFirst();
		
		return virtCoProjekt;
	}


	/**
	 * Anzahl Projekte für diese Seite
	 * 
	 * @return
	 */
	protected int getAnzProjekte() {
		return getAnzProjekte(getVirtCoProjekt());
	}


	/**
	 * Anzahl Projekte für diese Seite
	 * 
	 * @param virtCoProjekt
	 * @return
	 */
	private int getAnzProjekte(VirtCoProjekt virtCoProjekt) {
		return virtCoProjekt.getRowCount();
	}


}
