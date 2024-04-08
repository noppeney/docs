package pze.business.export;

import java.util.HashMap;
import java.util.Map;

import framework.Application;
import framework.business.interfaces.fields.IField;
import pze.business.Format;
import pze.business.objects.personen.CoPerson;
import pze.business.objects.personen.monatseinsatzblatt.CoMonatseinsatzblattAnzeige;
import pze.business.objects.projektverwaltung.VirtCoProjekt;
import pze.ui.formulare.UniFormWithSaveLogic;
import pze.ui.formulare.person.monatseinsatzblatt.FormPersonMonatseinsatzblatt;
import pze.ui.formulare.person.monatseinsatzblatt.TableMonatseinsatzblatt;


/**
 * Klasse zum Erstellen einer HTML-Datei für den PDF-Export des Monatseinsatzblattes
 * 
 * @author Lisiecki
 */
public class ExportMonatseinsatzblattCreator extends ExportPdfCreator {

	private static final int MAX_ANZ_SPALTEN = 8;
	
	private int m_anzSeiten;
	private Map<Integer, Integer> m_mapIndexProjektSeite;
	private Map<Integer, Double> m_mapAnzSpaltenSeite;
	private Map<Integer, Integer> m_mapAnzProjekteSeite;

	private FormPersonMonatseinsatzblatt m_formPersonMonatseinsatzblatt;
	
	/**
	 * Kopie des Cacheobjektes aus dem Formular Monatseinsatzblatt
	 */
	private CoMonatseinsatzblattAnzeige m_coMonatseinsatzblattAnzeige;
	private VirtCoProjekt m_virtCoProjekt;

	
	/**
	 * Gibt eine Monatseinsatzblatt-Tabelle in XHTML zurück.
	 * 
	 * @param formPersonMonatseinsatzblatt
	 * @return
	 * @throws Exception 
	 */
	@Override
	public String createHtml(UniFormWithSaveLogic formPersonMonatseinsatzblatt) throws Exception {
		int seite;
		
		m_formPersonMonatseinsatzblatt = (FormPersonMonatseinsatzblatt) formPersonMonatseinsatzblatt;
		m_sb = new StringBuilder();
		
		// Cacheobjekte initialisieren und leere Spalten löschen
		initCoMonatseinsatzblatt();

		writeHtmlOpen();
		
		// Footer am Beginn schreiben, da es eine allgemeine Darstellung ist
		writeFooter();

		// 8 Projekte pro Seite
//		anzSeiten = Format.getIntValue(Math.ceil(m_virtCoProjekt.getRowCount() / (1. * MAX_ANZ_PROJEKTE)));
		if (m_anzSeiten == 0)
		{
			// leeres Monatseinsatzblatt
			++m_anzSeiten;
//			Messages.showErrorMessage("Keine Daten gefunden", "Es konnten keine Daten zur Erstellung des Monatseinsatzblattes gefunden werden.");
//			return null;
		}
		
		// Seiten durchlaufen
		for (seite=1; seite<=m_anzSeiten; ++seite)
		{
			writeSeite(seite, m_anzSeiten);
		}
	
		writeZeileUnterschrift();

		writeHtmlClose();

		return m_sb.toString();
	}


	/**
	 * Cacheobjekte initialisieren und für den Export leere Spalten aus dem Monatseinsatzblatt löschen
	 * 
	 * @throws Exception
	 */
	private void initCoMonatseinsatzblatt() throws Exception {
		int iProjekt, anzProjekte, startIndexProjektspalten, wertZeit;
		IField field;
	
		
		m_coMonatseinsatzblattAnzeige = new CoMonatseinsatzblattAnzeige(m_formPersonMonatseinsatzblatt.getCoPerson().getID(), 
				m_formPersonMonatseinsatzblatt.getCurrentDatum());
		m_virtCoProjekt = m_coMonatseinsatzblattAnzeige.getVirtCoProjekt();
		
		if (!m_coMonatseinsatzblattAnzeige.isEditing())
		{
			m_coMonatseinsatzblattAnzeige.begin();
		}
		
		if (!m_virtCoProjekt.isEditing())
		{
			m_virtCoProjekt.begin();
		}
		
		
		anzProjekte = m_virtCoProjekt.getRowCount();
		startIndexProjektspalten = m_coMonatseinsatzblattAnzeige.getStartindexProjektspalten();
		m_coMonatseinsatzblattAnzeige.moveToSummenzeile();

		
		// Projekte durchlaufen und die ohne Stunden löschen
		for (iProjekt=anzProjekte-1; iProjekt>=0; --iProjekt)
		{
			field = m_coMonatseinsatzblattAnzeige.getField(startIndexProjektspalten + iProjekt);
			wertZeit = Format.getZeitAsInt(field.getDisplayValue());
			
			if (wertZeit == 0)
			{
				// Spalte aus Monatseinsatzblatt löschen
				m_coMonatseinsatzblattAnzeige.removeField(field.getFieldDescription().getResID());
				
				// Zeile aus Projekt-Co löschen
				if (m_virtCoProjekt.moveTo(iProjekt))
				{
					m_virtCoProjekt.delete();
				}
			}
		}
		
		
		// Anzahl Spalten pro Seite bestimmen
		m_mapAnzSpaltenSeite = new HashMap<Integer, Double>();
		m_mapAnzProjekteSeite = new HashMap<Integer, Integer>();
		m_mapIndexProjektSeite = new HashMap<Integer, Integer>();

		m_anzSeiten = 1;
		m_mapIndexProjektSeite.put(m_anzSeiten, 0);
		m_mapAnzSpaltenSeite.put(m_anzSeiten, 0.);
		m_mapAnzProjekteSeite.put(m_anzSeiten, 0);

//		m_mapIndexProjektSeite.put(m_anzSeiten, 0);
		
		if (!m_virtCoProjekt.moveFirst())
		{
			return;
		}
		
		do
		{
			// ggf. neue Seite
			if (!m_mapAnzSpaltenSeite.containsKey(m_anzSeiten) || m_mapAnzSpaltenSeite.get(m_anzSeiten) >= MAX_ANZ_SPALTEN)
			{
				++m_anzSeiten;
				m_mapIndexProjektSeite.put(m_anzSeiten, m_virtCoProjekt.getCurrentRowIndex());
				m_mapAnzSpaltenSeite.put(m_anzSeiten, 0.);
				m_mapAnzProjekteSeite.put(m_anzSeiten, 0);
			}

			// Anzahl Spalten und Projekte für die Anzeige zählen
			m_mapAnzSpaltenSeite.put(m_anzSeiten, m_mapAnzSpaltenSeite.get(m_anzSeiten)+1);
			m_mapAnzProjekteSeite.put(m_anzSeiten, m_mapAnzProjekteSeite.get(m_anzSeiten)+1);
			
			// Projekte mit BerichtsNr benötigen 2 Spalten
			if (m_virtCoProjekt.getBerichtsNrID() > 0)
			{
				m_mapAnzSpaltenSeite.put(m_anzSeiten, m_mapAnzSpaltenSeite.get(m_anzSeiten)+0.6);
			}
	
		} while(m_virtCoProjekt.moveNext());

//		System.out.println();
	}


	/**
	 * Eine Seite des Monatseinsatzblattes erstellen
	 * 
	 * @param seite
	 * @param anzSeiten
	 * @throws Exception
	 */
	private void writeSeite(int seite, int anzSeiten) throws Exception {
		writeHeader(seite, anzSeiten);
		
		writeTabellenUeberschriften(seite);
		writeTabellenDaten(seite, anzSeiten);
		
		writeProjektbeschreibungen(seite);
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
		m_sb.append("<h1>Monatseinsatzblatt</h1>\n");
		
		// Monat
		m_sb.append("<table class='textspalten unsichtbar'>");
		m_sb.append("<tr><td class='unsichtbar'><h3>Monat:&nbsp;</h3></td><td class='unsichtbar'><h3>" 
				+ m_formPersonMonatseinsatzblatt.getCurrentMonat() 
//				+ " (Soll-Arbeitszeit: " + Format.getZeitAsText(getSollArbeitszeitMonat()) + ")"
				+ "</h3></td></tr>\n");

		// Name
		m_sb.append("<tr><td class='unsichtbar'><h3>Name:&nbsp;</h3></td><td class='unsichtbar'><h3>" 
				+ m_formPersonMonatseinsatzblatt.getCoPerson().getBezeichnung() + "</h3></td></tr>\n");
		m_sb.append("</table>\n");
		m_sb.append("</div>\n"); // links
		
		// Logo
		m_sb.append("<div class='drechts'>\n");
		m_sb.append("<img src='/" + Application.getWorkingDirectory().replace("\\", "/") + "WTI_Logo_2023_Schwarz.png'></img><br />\n");
		
		// Seite
//		m_sb.append("<div class='drechts'><h3>Seite " + seite + " von " + anzSeiten + "</h3></div>\n");
		
		m_sb.append("</div>\n"); // rechts
		m_sb.append("</div>\n"); // oben
		m_sb.append("<br /><br /><br /><br /><br /><br /><br /><br /><br />\n");
	}


//	/**
//	 * Bestimmt die Sollarbeitszeit für den aktuellen Monat und die aktuelle Person
//	 * 
//	 * @return
//	 * @throws Exception
//	 */
//	private int getSollArbeitszeitMonat() throws Exception {
//		Date datum;
//		CoKontowert coKontowert;
//		
//		// Datum für den letzten Tag des Monats bestimmen
//		datum = Format.getDatumLetzterTagdesMonats(m_coMonatseinsatzblattAnzeige.getDatum());
//		
//		// Kontowerte für die Person und den Tag laden
//		coKontowert = new CoKontowert();
//		coKontowert.load(m_coMonatseinsatzblattAnzeige.getPersonID(), datum);
//		
//		return coKontowert.getWertSollArbeitszeitMonat();
//	}


	/**
	 * Überschriften der Tabelle
	 * 
	 * @param seite 
	 */
	private void writeTabellenUeberschriften(int seite) {
		int iProjekt, anzProjekte;
		VirtCoProjekt virtCoProjekt;
		
		virtCoProjekt = getVirtCoProjekt(seite);
		anzProjekte = getAnzProjekte(seite);

		m_sb.append("<table class='unsichtbar'>\n");
		m_sb.append("<tr>\n");
		m_sb.append("<th class='moneinkeinprojekt oben'>");
		m_sb.append("<table class='innen unsichtbar'>"
				+ "<tr><td class='umrandet oben'>Kunde</td>\n</tr>\n"
				+ "<tr><td class='umrandet'>AN</td>\n</tr>\n"
				+ "<tr><td class='umrandet'>Abruf</td>\n</tr>\n"
				+ "<tr><td class='umrandet'>Kst</td>\n</tr>\n"
				+ "<tr><td class='umrandet'>PL</td>\n</tr>\n"
				+ "<tr><td class='kleiner umrandet'>Stundenart</td>\n</tr>\n"
				+ "</table>\n</th>\n");
		
		
		// Projekte
		for (iProjekt=0; iProjekt<anzProjekte; ++iProjekt)
		{
			appendProjekt(virtCoProjekt.getKundeKuerzel(), 
					virtCoProjekt.getKostenstelle(), virtCoProjekt.getAbrufNr(), virtCoProjekt.getAuftragsNr(),
					virtCoProjekt.getProjektleiter(), virtCoProjekt.getStundenart());
			virtCoProjekt.moveNext();
		} 
		
		// leere Projekte
		for (iProjekt=Format.getIntValue(m_mapAnzSpaltenSeite.get(seite)); iProjekt<MAX_ANZ_SPALTEN; ++iProjekt) 
		{
			appendProjekt(null, null, null, null, null, null);
		}
		
		// Summe, Arbeitszeit Krank, Urlaub
		m_sb.append("<th class='moneinkeinprojekt'>Gesamt-<br />stunden</th>\n");
		m_sb.append("<th class='moneinkeinprojekt'>Arbeits-<br />zeit<br />(PZE)</th>\n");
		m_sb.append("<th class='moneinkeinprojekt'>Krank</th>\n");
		m_sb.append("<th class='moneinkeinprojekt'>Urlaub<br />/FA</th>\n");
		m_sb.append("</tr>\n");
	}


	/**
	 * Projekt in Tabellenüberschriften hinzufügen
	 * 
	 * @param sb
	 * @param kunde
	 * @param kostenstelle
	 * @param abruf
	 * @param auftrag
	 * @param projektleiter
	 * @param stundenart
	 */
	private void appendProjekt(String kunde, String kostenstelle, String abruf, String auftrag, String projektleiter, String stundenart) {
		m_sb.append("<th class='moneinprojekt oben'> <table class='innen unsichtbar'>");
		m_sb.append("<tr><td class='oben'>" + (kunde == null ? "" : Format.getConformXml(kunde)) + "</td>\n</tr>\n");
		m_sb.append("<tr><td class='umrandet'>" + (auftrag == null ? "" : auftrag) + "</td>\n</tr>\n");
		m_sb.append("<tr><td class='kleiner umrandet'>" + (abruf == null ? "" : abruf) + "</td>\n</tr>\n");
		m_sb.append("<tr><td class='" + (kostenstelle == null || kostenstelle.length() < 10 ? "klein" : "kleiner") + " umrandet'>" 
				+ (kostenstelle == null ? "" : kostenstelle) + "</td>\n</tr>\n");
		m_sb.append("<tr><td class='umrandet'>" + (projektleiter == null ? "" : projektleiter) + "</td>\n</tr>\n");
		m_sb.append("<tr><td class='kleiner umrandet'>" + (stundenart == null ? "" : stundenart) + "</td>\n</tr>\n");
		m_sb.append("</table>\n</th>\n");
	}
	

	/**
	 * Projektstunden der Tabelle
	 * 
	 * @param seite 
	 * @throws Exception
	 */
	private void writeTabellenDaten(int seite, int anzSeiten) throws Exception {
		int iProjekt, anzProjekte;
		int tag;
		int anzTage;
		int iField, startFieldProjektdaten;
		int anzFields, intValue;
		boolean isLetzteSeite;
		String color, value;
		IField field;
		CoPerson coPerson;
		
		isLetzteSeite = true; //(seite == anzSeiten);
		
		coPerson = m_formPersonMonatseinsatzblatt.getCoPerson();
		
		anzTage = Format.getAnzTageDesMonats(m_coMonatseinsatzblattAnzeige.getDatum());
		anzProjekte = getAnzProjekte(seite);
		startFieldProjektdaten = m_coMonatseinsatzblattAnzeige.getStartindexProjektspalten() + m_mapIndexProjektSeite.get(seite);
		m_coMonatseinsatzblattAnzeige.moveToTag(1);
		
		for (tag=1; tag<=anzTage+1; ++tag)
		{
			// Farbe für Arbeitstag/kein Arbeitstag
			color = "";
			if (!coPerson.isArbeitstag(m_coMonatseinsatzblattAnzeige.getDatum())) 
			{
				color = " bgcolor='" + TableMonatseinsatzblatt.COLOR_ARBEITSFREI.replace("##", "#") + "' ";
			}

			// Tag des Monats
			m_sb.append("<tr><td class='moneinkeinprojekt umrandet'" + color + ">").append(tag > anzTage ? "Summe" : tag).append("</td>\n");

			// Projektstunden 
			anzFields = startFieldProjektdaten + anzProjekte;
			for (iField=startFieldProjektdaten; iField<anzFields; ++iField)
			{
				m_sb.append("<td class='moneinprojekt" + (tag > anzTage ? " umrandet" : "") + "'" + color + ">").append(
						m_coMonatseinsatzblattAnzeige.getField(iField).getDisplayValue()).append("</td>\n");
			}
			
			// leere Projektspalten
			for (iProjekt=Format.getIntValue(m_mapAnzSpaltenSeite.get(seite)); iProjekt<MAX_ANZ_SPALTEN; ++iProjekt) 
			{
				m_sb.append("<td class='moneinprojekt" + (tag > anzTage ? " umrandet" : "") + "'" + color + ">").append("").append("</td>\n");
			}

			// Summe, Urlaub, Krank
			anzFields = m_coMonatseinsatzblattAnzeige.getStartindexProjektspalten() + getVirtCoProjekt(seite).getRowCount();
			for (iField=anzFields; iField<anzFields+3; ++iField) 
			{
				// Summe Monat abfangen, da 2 Werte darin enthalten sind
				field = m_coMonatseinsatzblattAnzeige.getField(iField);
				if (field.equals(m_coMonatseinsatzblattAnzeige.getFieldSumme()))
				{
					intValue = m_coMonatseinsatzblattAnzeige.getSummeStunden();
					if (intValue > 0)
					{
						value = Format.getZeitAsText(intValue);
					}
					else
					{
						value = "";
					}
				}
				else
				{
					value = field.getDisplayValue();
				}
				
				m_sb.append("<td class='moneinkeinprojekt umrandet'" + color + ">").append(
						isLetzteSeite ? value : "").append("</td>\n");
				
				// Arbeitszeit eintragen
				if (field.equals(m_coMonatseinsatzblattAnzeige.getFieldSumme()))
				{
					field = m_coMonatseinsatzblattAnzeige.getFieldArbeitszeit();
					value = field.getDisplayValue();
					m_sb.append("<td class='moneinkeinprojekt umrandet'" + color + ">").append(
							isLetzteSeite ? value : "").append("</td>\n");
				}
			}
			m_sb.append("</tr>\n");
			
			m_coMonatseinsatzblattAnzeige.moveNext();
		}
		
		// Einzelne Zelle mit Summe des Monats
		m_sb.append("<tr><td class='moneinkeinprojekt unsichtbar'></td>\n");
		anzProjekte = m_mapAnzProjekteSeite.get(seite) + Math.max(0, MAX_ANZ_SPALTEN - Format.getIntValue(m_mapAnzSpaltenSeite.get(seite)));
		for (int j = 0; j < anzProjekte; ++j)
		{
			m_sb.append("<td class='moneinprojekt unsichtbar'></td>\n");
		}
		m_sb.append("<td class='moneinkeinprojekt umrandet'>");
		m_sb.append(isLetzteSeite ? Format.getZeitAsText(m_coMonatseinsatzblattAnzeige.getSummeInklKrankUrlaub()) : "&nbsp;").append("</td>\n");
		for (int j = 0; j < 2; ++j)
		{
			m_sb.append("<td class='moneinkeinprojekt unsichtbar'></td>\n");
		}
		m_sb.append("</tr>\n");
		
		m_sb.append("</table>\n");
	}


	/**
	 * Verwendete Projektbeschreibungen
	 * 
	 * @param seite 
	 * @param m_virtCoProjekt
	 */
	private void writeProjektbeschreibungen(int seite) {
		int iProjekt, anzProjekte;
		VirtCoProjekt virtCoProjekt;
		
		virtCoProjekt = getVirtCoProjekt(seite);
		anzProjekte = getAnzProjekte(seite);
		
		m_sb.append("<div class='dunten'>\n");
		m_sb.append("<table class='textspalten2 unsichtbar'>\n");
		
		for (iProjekt=0; iProjekt<anzProjekte; ++iProjekt)
		{
			if (virtCoProjekt.getAbrufID() > 0)
			{
				m_sb.append("<tr><td class='unsichtbar'>" + virtCoProjekt.getAbrufNr() + "&nbsp;</td><td class='unsichtbar'>" 
						+ Format.getConformXml(virtCoProjekt.getAbrufBeschreibung()));
			}
			else
			{
				m_sb.append("<tr><td class='unsichtbar'>" + virtCoProjekt.getAuftragsNr() + "&nbsp;</td><td class='unsichtbar'>" 
						+ Format.getConformXml(virtCoProjekt.getAuftragsBeschreibung()));
			}
			m_sb.append(" (" + virtCoProjekt.getStundenart() + ")</td></tr>\n");
			virtCoProjekt.moveNext();
		}
		
		m_sb.append("</table>\n");
		m_sb.append("</div>\n");
		m_sb.append("</div>\n"); // page
	}


	/**
	 * Zeile für die Unterschrift
	 */
	private void writeZeileUnterschrift() {
		
		m_sb.append("<footer>\n");
		m_sb.append("<div>\n");
		m_sb.append("<span class='blank'></span>\n");
		m_sb.append("<span class='blank'></span>\n");
		m_sb.append("<span class='blank'></span>\n");
		m_sb.append("<span class='blank'></span>\n");
		m_sb.append("<span class='blank'></span>\n");
		m_sb.append("<span class='blank'></span>\n");
		m_sb.append("<span class='unterstrich'></span>\n");
		m_sb.append("</div>\n");
		
		m_sb.append("<div>\n");
		m_sb.append("<span class='blank'></span>\n");
		m_sb.append("<span class='blank'></span>\n");
		m_sb.append("<span class='blank'></span>\n");
		m_sb.append("<span class='blank'></span>\n");
		m_sb.append("<span class='blank'></span>\n");
		m_sb.append("<span class='blank'></span>\n");
		m_sb.append("<span class='text'>WTI-Gegenzeichnung</span>\n");
		m_sb.append("</div>\n");
		m_sb.append("</footer>\n");
	}


	
	@Override
	protected String getStand(){
		return "03/2024";
	}


	/**
	 * Co mit Bookmark auf dem ersten Projekt für diese Seite
	 * 
	 * @param seite
	 * @return
	 */
	private VirtCoProjekt getVirtCoProjekt(int seite) {
		m_virtCoProjekt.moveTo(Format.getIntValue(m_mapIndexProjektSeite.get(seite)));
		
		return m_virtCoProjekt;
	}


	/**
	 * Anzahl Projekte für diese Seite
	 * 
	 * @return
	 */
	private int getAnzProjekte(int seite) {
//		if (m_mapAnzProjekteSeite.get(seite) > 0)
		{
			return m_mapAnzProjekteSeite.get(seite);
		}
		
//		return MAX_ANZ_SPALTEN;
	}

//
//	/**
//	 * Anzahl Projekte für diese Seite
//	 * 
//	 * @param virtCoProjekt
//	 * @return
//	 */
//	private int getAnzProjekte(VirtCoProjekt virtCoProjekt) {
//		return Math.min(virtCoProjekt.getRowCount() - virtCoProjekt.getCurrentRowIndex(), MAX_ANZ_PROJEKTE);
//	}
//

}
