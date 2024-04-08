package pze.business.export.urlaub;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import framework.Application;
import pze.business.FeiertagGenerator;
import pze.business.Format;
import pze.business.UserInformation;
import pze.business.export.ExportPdfCreator;
import pze.business.objects.CoBrueckentag;
import pze.business.objects.CoFerien;
import pze.business.objects.auswertung.CoAuswertung;
import pze.business.objects.personen.CoBuchung;
import pze.business.objects.personen.CoKontowert;
import pze.business.objects.personen.CoPerson;
import pze.business.objects.personen.CoVertreter;
import pze.business.objects.reftables.buchungen.CoBuchungsart;
import pze.business.objects.reftables.buchungen.CoStatusBuchung;
import pze.business.objects.reftables.personen.CoBundesland;
import pze.ui.controls.SortedTableControl;
import pze.ui.formulare.UniFormWithSaveLogic;
import pze.ui.formulare.auswertung.FormAuswertungUrlaubsplanung;


/**
 * Klasse zum Erstellen einer HTML-Datei für den PDF-Export der Urlaubsplanung
 * 
 * @author Lisiecki
 */
public class ExportUrlaubsplanungCreator extends ExportPdfCreator {

	public static final String COLOR_FERIEN = "##ADD8E6";
	public static final String COLOR_FEIERTAG = "##5CA381";
	public static final String COLOR_BRUECKENTAG = "##88AC88";
	public static final String COLOR_ABWESEND = "##AAAAAA";

	public static final String IMAGE_COLOR_URLAUB = "blau";
	public static final String IMAGE_COLOR_URLAUB_NACHTRAEGLICH = "rot";
	public static final String IMAGE_COLOR_FA = "gelb";
	public static final String IMAGE_COLOR_URLAUB_GEPLANT_ANHANG = "_weiss";
	
	private FormAuswertungUrlaubsplanung m_formAuswertung;
	
	private FeiertagGenerator m_feiertagGenerator;
	private CoBrueckentag m_coBrueckentag;
	private CoFerien m_coFerien;
	
	private Date m_datumVon;
	private Date m_datumBis;
	
	private boolean m_uebersichtErstellt;
	private Map<Integer, String> m_mapPersonName;

	
	
	/**
	 * Konstruktor
	 * @throws Exception 
	 */
	public ExportUrlaubsplanungCreator() throws Exception {
		
		m_feiertagGenerator = FeiertagGenerator.getInstance();
		m_coBrueckentag = CoBrueckentag.getInstance();
		m_coFerien = CoFerien.getInstance();
	}


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
		m_formAuswertung = (FormAuswertungUrlaubsplanung) formAuswertung;
		m_sb = new StringBuilder();
		
		m_mapPersonName = new TreeMap<Integer, String>();
		m_uebersichtErstellt = false;

		long a = System.currentTimeMillis();

		writeHtmlOpen();
		
		// Footer am Beginn schreiben, da es eine allgemeine Darstellung ist
		writeFooter();

		if (!writeSeite())
		{
			return null;
		}

		writeHtmlClose();
		System.out.println("createHtml: " + (System.currentTimeMillis()-a)/1000.);

		return m_sb.toString();
	}


	/**
	 * Eine Seite erstellen
	 * 
	 * @return Daten gefunden
	 * @throws Exception
	 */
	private boolean writeSeite() throws Exception {
		
		writeHeader();
		
		// Tabellenüberschrift
		m_sb.append("<table class='unsichtbar'>\n");
		m_sb.append("	<tr>");
		m_sb.append("	<td class='unsichtbar' style='width:40%;'>\n");
		writeTabellenBeschriftung();
		m_sb.append("	</td>");
		
		// Abstandhalter
		m_sb.append("	<td class='unsichtbar' style='width:60px;'>");
		m_sb.append("	</td>");

		// Legende
		m_sb.append("	<td class='unsichtbar'>\n");
		appendLegende();
		m_sb.append("	</td>");
		appendTabellenEnde();

		
		// Tabellendaten
		getHtmlStringTable(m_formAuswertung.getTable());

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
		return "width:100%; margin-top: 4px;";
	}


	/**
	 * Header mit Logo, Name, monat etc.
	 * 
	 * @throws Exception 
	 */
	protected void writeHeader() throws Exception {

		m_sb.append("<div style='width: 100%; white-space: no-wrap'>\n");
		m_sb.append("<div style='width: 66%; display: inline-block; text-align: left; vertical-align: top; '>\n");

		// Überschrift
		m_sb.append("<h1>Urlaubsplanung</h1>\n");
		
		m_sb.append("</div>\n"); // links
		
		// Logo
		m_sb.append("<div style='width: 33%; display: inline-block; text-align: right'>\n");
		m_sb.append("<img src='/" + Application.getWorkingDirectory().replace("\\", "/") + "WTI_Logo_2023_Farbe.png'></img>\n");
		
		m_sb.append("</div>\n"); // rechts
		m_sb.append("</div>\n"); // oben
	}


	/**
	 * Auswahl der Auswertungsparameter ausgeben
	 */
	private void writeTabellenBeschriftung() {
		String stringValue;
		CoAuswertung coAuswertung;

		coAuswertung = m_formAuswertung.getCoAuswertung();
		coAuswertung.moveTo(0);


		m_sb.append("<table class='unsichtbar'>\n");
		
		stringValue = "";
		m_datumVon = coAuswertung.getDatumVon();
		m_datumBis = coAuswertung.getDatumBis();

		if (m_datumVon != null)
		{
			stringValue += Format.getString(m_datumVon) + " ";
		}
		
		// Datum bis heute, wenn kein datum angegeben
		if (m_datumBis == null && m_datumVon == null)
		{
			m_datumBis = new Date();
		}
		if (m_datumBis != null)
		{
			stringValue += "bis " + Format.getString(m_datumBis);
		}
		
		writeProjektbeschreibungLinksbuendig("Datum:", stringValue);
		writeProjektbeschreibungLinksbuendig("Abteilung:", coAuswertung.getAbteilung());
		writeProjektbeschreibungLinksbuendig("Person:", coAuswertung.getPerson());
		writeProjektbeschreibungLinksbuendig("Personenliste:", coAuswertung.getPersonenliste());
		m_sb.append("</table>\n");

	}

	
	/**
	 * Legende ausgeben
	 */
	private void appendLegende() 
	{
		m_sb.append("<table class='" + getClassTable() + "'>");
		m_sb.append("	<tr>");
		m_sb.append("		<td class='legendebreit'>geplanter<br/>Urlaub:</td>");
		m_sb.append("		<td class='legendeschmal' " + getBackgroundImage(IMAGE_COLOR_URLAUB + IMAGE_COLOR_URLAUB_GEPLANT_ANHANG) + "></td>");
		m_sb.append("		<td class='legendebreit'>genehmigter<br/>Urlaub:</td>");
		m_sb.append("		<td class='legendeschmal' " + getBackgroundImage(IMAGE_COLOR_URLAUB) + ">U</td>");
		m_sb.append("		<td class='legendebreit'> nachträglich<br/>entfernter Urlaub:</td>");
		m_sb.append("		<td class='legendeschmal' " + getBackgroundImage(IMAGE_COLOR_URLAUB + IMAGE_COLOR_URLAUB_GEPLANT_ANHANG) + ">"
				+ getTextDurchgestrichen(null) + "</td>");
		appendTabellenZeilenumbruch();
		
//		m_sb.append("		<td class='legendebreit'>nachträglich geplanter<br/>Urlaub:</td>");
//		m_sb.append("		<td class='legendeschmal' " + getBackgroundImage(IMAGE_COLOR_URLAUB_NACHTRAEGLICH + IMAGE_COLOR_URLAUB_GEPLANT_ANHANG) + "></td>");
		m_sb.append("		<td class='legendebreit'>nachträglich geplanter,<br/>genehmigter Urlaub:</td>");
		m_sb.append("		<td class='legendeschmal' " + getBackgroundImage(IMAGE_COLOR_URLAUB_NACHTRAEGLICH) + ">U</td>");
		m_sb.append("		<td class='legendebreit'>genehmigter<br/>FA-Tag:</td>");
		m_sb.append("		<td class='legendeschmal' " + getBackgroundImage(IMAGE_COLOR_FA) + ">FA</td>");
//		m_sb.append("		<td class='legendebreit'>geplanter<br/>FA-Tag:</td>");
//		m_sb.append("		<td class='legendeschmal' " + getBackgroundImage(IMAGE_COLOR_FA + IMAGE_COLOR_URLAUB_GEPLANT_ANHANG) + "></td>");
		m_sb.append("		<td class='legendebreit'>Vertretung von<br/>Urlaub<br/></td>");
		m_sb.append("		<td class='legendeschmal'>V</td>");
		appendTabellenZeilenumbruch();
		
		m_sb.append("		<td class='legendebreit'>Schulferien:</td>");
		m_sb.append("		<td class='legendeschmal' " + getBackgroundColor(COLOR_FERIEN) + "></td>");
		m_sb.append("		<td class='legendebreit'>Feiertage:</td>");
		m_sb.append("		<td class='legendeschmal' " + getBackgroundColor(COLOR_FEIERTAG) + "></td>");
		m_sb.append("		<td class='legendebreit'>Brückentage:</td>");
		m_sb.append("		<td class='legendeschmal' " + getBackgroundColor(COLOR_BRUECKENTAG) + "></td>");
		appendTabellenEnde();
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
	 * Urlaubsplanung ausgeben
	 * 
	 * @return
	 * @throws Exception
	 */
	protected String getHtmlStringTableDaten(SortedTableControl table) throws Exception {
		int personID, lastPersonID, monat, lastMonat, tag, lastTag, jahr;
		int buchungsStatusID, aktStatusID, buchungsartID;
		String value, color, farbe, name;
		Date datum, lastDatum, datumHeute, erstelltAm, aktErstelltAm, aktGeaendertAm, datumNachtraeglich;
		GregorianCalendar gregDatumRotBlau;
		Set<Date> setVertretung = null;
		Map<Integer, Set<Date>> mapPersonVertretung;
		CoPerson coPerson;
		CoKontowert coKontowert;
		CoBuchung coBuchung;
		CoVertreter coVertreter;

		mapPersonVertretung = new TreeMap<Integer, Set<Date>>();

		long a = System.currentTimeMillis();
		coPerson = CoPerson.getInstance();
		coVertreter = new CoVertreter();
		coKontowert = m_formAuswertung.getCoKontowerte();
		coBuchung = m_formAuswertung.getCoBuchung();
		coBuchung.moveFirst();

		System.out.println("geladen: " + (System.currentTimeMillis()-a)/1000.);
		if (!coKontowert.moveFirst())
		{
			return m_sb.toString();
		}
	
		lastPersonID = 0;
		lastMonat = 0;
		lastTag = 0;
		lastDatum = null;
		erstelltAm = null;
		farbe = null;
		
		// heutiges Datum und Datum für Grenze nachträglich eingetragenen urlaub bestimmen
//		datumHeute = Format.getDateVerschoben(Format.getDate0Uhr(new Date()), 1);
		datumHeute = Format.getDate0Uhr(new Date());
	
		gregDatumRotBlau = Format.getGregorianCalendar0Uhr(datumHeute);
		gregDatumRotBlau.set(GregorianCalendar.MONTH, GregorianCalendar.FEBRUARY);
		gregDatumRotBlau.set(GregorianCalendar.DAY_OF_MONTH, 1);
		datumNachtraeglich = Format.getDateValue(gregDatumRotBlau);
		a = System.currentTimeMillis();

		do 
		{
			personID = coKontowert.getPersonID();
			jahr = coKontowert.getJahr();
			monat = coKontowert.getVirtMonat();
			tag = coKontowert.getVirtTag();
			datum = Format.getDate12Uhr(coKontowert.getDatum());
			
			// neuer Monat bedeutet neue Tabelle
			if (monat != lastMonat)
			{
				lastMonat = monat;
				lastPersonID = 0;

				// Tabelle schließen, beim ersten Eintrag muss noch keine alte Tabelle geschlossen werden
				if (coKontowert.getCurrentRowIndex() > 0)
				{
					closeTable();
					
					// beim Jahreswechsel eine Urlaubsübersicht erstellen
					if (monat == 1)
					{
						appendUebersicht();
					}
				}
				
				// Label mit Monat
				writeLabelMonat(monat);

				// neue Tabelle
				writeNewTableMitTagenDesMonats(datum);
			}

			
			// neue Person bedeutet neue Zeile
			if (personID != lastPersonID)
			{
				// Personenliste erstellen
				if (!m_mapPersonName.containsKey(personID))
				{
					coPerson.moveToID(personID);
					name = coPerson.getNachnameVorname();
					m_mapPersonName.put(personID, name);

					// Vertretungstage speichern
					mapPersonVertretung.put(personID, coVertreter.loadDateSetForVertreter(personID, m_datumVon, m_datumBis));
				}
				name = m_mapPersonName.get(personID);
				
				// beim ersten Eintrag jeder Tabelle muss noch keine alte Zeile geschlossen werden
				if (lastPersonID > 0)
				{
					// ggf. restliche Tage des Monats ausgeben (wenn diese nicht im Auswertungszeitraum liegen)
					writeLeereTage(lastTag+1, Format.getAnzTageDesMonats(lastDatum));

					m_sb.append("</tr>\n");
				}
				
				lastPersonID = personID;


				// Person in die erste Spalte der neuen Zeile schreiben
				m_sb.append("<tr>\n");
				m_sb.append("<td class='" + getClassTdDaten(0, 0) + "' " + getHtmlAttribute(0, 0) + ">");
				m_sb.append(name);
				m_sb.append("</td>\n");
				
				
				// Tage bis zum 1. Tag der Auswertung ausgeben (wenn diese nicht im Auswertungszeitraum liegen)
				writeLeereTage(1, coKontowert.getVirtTag() - 1);
				
				// Vertretungstage laden
				setVertretung = mapPersonVertretung.get(personID);
			}

			lastTag = tag;
			lastDatum = datum;
			value = null;
			color = null;
			
			// Arbeitstag oder nicht; ohne Lfz. soll geplanter Urlaub trotzdem angezeigt werden
			if (coKontowert.getWertSollArbeitszeit() == 0 && coKontowert.getAnzahlKrankOhneLfz() == 0)
			{
				// Feier- und Brückentage prüfen
				color = getColorFreierTag(datum);
				value = null;
			}
			// normaler Arbeitstag
			else
			{
				value = null;
				color = null;
			}
			
			
			// alle Buchungen der Person zu dem Datum betrachten
			buchungsStatusID = 0;
			buchungsartID = 0;
			farbe = null;
			erstelltAm = datumNachtraeglich;
			do
			{
				if (coBuchung.getPersonID() == personID && coBuchung.getVirtMonat() == monat && coBuchung.getVirtTag() == tag && coBuchung.getJahr() == jahr)
				{
					// wenn schon ein Eintrag bestimmt wurde, z. B. Krank oder Elternzeit, müssen die Buchungen nicht betrachtet werden
					// sie müssen aber durchlaufen werden, um danach die Buchungen zum richtigen Tag markiert zu haben
					if (color != null)
					{
						continue;
					}

					aktStatusID = coBuchung.getStatusID();
					aktErstelltAm = coBuchung.getErstelltAm();
					aktGeaendertAm = coBuchung.getGeaendertAm();
					
					// wenn noch keine Buchung oder nur ungültige betrachtet wurden, übernehme die aktuelle; 
					if (buchungsStatusID == 0 || buchungsStatusID == CoStatusBuchung.STATUSID_UNGUELTIG 
							// wenn die aktuelle OK ist,  übernehme die aktuelle; 
							|| aktStatusID == CoStatusBuchung.STATUSID_OK || aktStatusID == CoStatusBuchung.STATUSID_GEAENDERT
							// wenn die aktuelle vorläufig ist bisher nur ungültige,  übernehme die aktuelle; 
							|| (aktStatusID == CoStatusBuchung.STATUSID_VORLAEUFIG && buchungsStatusID == CoStatusBuchung.STATUSID_UNGUELTIG))
					{
						
						// vor dem Stichtag 1. Februar wieder gelöschte Urlaubsbuchungen nicht berücksichtigen
						if (aktStatusID == CoStatusBuchung.STATUSID_UNGUELTIG && (aktGeaendertAm != null && aktGeaendertAm.before(datumNachtraeglich)))
						{
							continue;
						}

						buchungsStatusID = aktStatusID;
						buchungsartID = coBuchung.getBuchungsartID();
					}
					
					// das früheste Datum wird gespeichert, falls Urlaub gelöscht und neu angelegt wird
					if (aktErstelltAm == null || (erstelltAm != null && aktErstelltAm.before(erstelltAm)))
					{
						erstelltAm = aktErstelltAm;
					}
				}
				else // wenn die Buchung nicht zu dem aktuellen Tag/Person gehört mache nichts (es gibt nicht zu jedem Tag eine Buchung)
				{
					break;
				}
			} while (coBuchung.moveNext()); // weiter bis alle Buchungen für den Tag abgearbeitet sind

			
			// Farbe und Beschriftung für die Buchung bestimmen
			if (buchungsartID > 0)
			{
				// Farbe für Sonderurlaub
				if (erstelltAm == null || erstelltAm.before(datumNachtraeglich))
				{
					farbe = IMAGE_COLOR_URLAUB;
				}
				else
				{
					farbe = IMAGE_COLOR_URLAUB_NACHTRAEGLICH;
				}

				// Urlaub
				if (buchungsartID == CoBuchungsart.ID_URLAUB)
				{
					value = "U";
				}
				// Sonderurlaub
				else if (buchungsartID == CoBuchungsart.ID_SONDERURLAUB)
				{
					value = detailsAnzeigen() ? "SU" : "U";
				}
				// FA
				else if (buchungsartID == CoBuchungsart.ID_FA)
				{
					value = "FA";
					farbe = IMAGE_COLOR_FA;
				}
				
				// vorläufig und ungültig gestrichelt
				if (buchungsStatusID == CoStatusBuchung.STATUSID_VORLAEUFIG || buchungsStatusID == CoStatusBuchung.STATUSID_UNGUELTIG)
				{
					farbe += IMAGE_COLOR_URLAUB_GEPLANT_ANHANG;
				}
			}
			
			// Sonderfall Krank ohne Lfz: Arbeitszeit=0, wie bei freien Tagen, soll aber nicht grau angezeigt werden
			if (coKontowert.getAnzahlKrankOhneLfz() > 0)
			{
				farbe = null;
				color = null;
			}

			// Vertretung prüfen, nur für Arbeitstage
			if (color != COLOR_ABWESEND && setVertretung.contains(datum))
			{
				value = "V";
			}
			
			// Leerzeichen bei keinem Eintrag
			if (value == null || value.isEmpty())
			{
				value = "&nbsp;";
			}

			
			// Feld erzeugen
			m_sb.append("<td class='" + getClassTdDaten(0, 1) + "' " 
					+ getBackgroundImage(farbe)
//					+ getHtmlAttribute(0, 1) 
					+ getBackgroundColor(color) 
					+ ">");
			// ungültige oder geplant und vor dem aktuellen Tag liegende Urlaubstage durchgestricken
			m_sb.append(buchungsStatusID == CoStatusBuchung.STATUSID_UNGUELTIG
					|| (buchungsStatusID == CoStatusBuchung.STATUSID_VORLAEUFIG && datum.before(datumHeute)) ? getTextDurchgestrichen(value) : value);
			m_sb.append("</td>\n");

		} while (coKontowert.moveNext());
		
		// ggf. restliche Tage des Monats ausgeben (wenn diese nicht im Auswertungszeitraum liegen)
		writeLeereTage(lastTag+1, Format.getAnzTageDesMonats(lastDatum));
		System.out.println("schleife ende: " + (System.currentTimeMillis()-a)/1000.);

		closeTable();
		
		
		// Übersicht ausgeben
		a = System.currentTimeMillis();
		appendUebersicht();
		System.out.println("appendUebersicht: " + (System.currentTimeMillis()-a)/1000.);

		return m_sb.toString();
	}

	
	/**
	 * Übersicht der Anzahl Urlaubstage erstellen
	 * 
	 * @throws Exception
	 */
	private void appendUebersicht() throws Exception 
	{
		int anzUrlaubAnfang, anzUrlaubEnde, anzUrlaubGeplant, anzUrlaubRest, personID;
		Date datumBis, datumTmp;
		GregorianCalendar gregDatumVon, gregDatumBis;
		Set<String> setPerson;
		Set<Integer> setPersonID;
		Map<String, Integer> mapPersonID;
		CoKontowert coKontowert;
		CoPerson coPerson;

		coPerson = CoPerson.getInstance();
		
		// die Übersicht wird nur für Sekretärinnen, AL und höhere erstellt oder für sich selbst
		if (!detailsAnzeigen())
		{
			return;
		}

		// die Übersicht wird nur einmal erstellt
		if (m_uebersichtErstellt)
		{
			return;
		}
		m_uebersichtErstellt = true;

		coKontowert = new CoKontowert();
		
		gregDatumVon = Format.getGregorianCalendar(m_datumVon);
		gregDatumBis = Format.getGregorianCalendar(m_datumBis);
		datumBis = m_datumBis;

		// wenn du Urlaubsplanung über die Jahresgrenze geht, wird die Übersicht nur am Jahresende erstellt
		if (gregDatumVon.get(GregorianCalendar.YEAR) != gregDatumBis.get(GregorianCalendar.YEAR))
		{
			gregDatumBis.set(GregorianCalendar.YEAR, gregDatumVon.get(GregorianCalendar.YEAR));
			gregDatumBis.set(GregorianCalendar.MONTH, GregorianCalendar.DECEMBER);
			gregDatumBis.set(GregorianCalendar.DAY_OF_MONTH, 31);
			datumBis = Format.getDateValue(gregDatumBis);
		}
		
		// Überschriften
		appendBr(2);
		m_sb.append("<table class='" + getClassTable() + "'>");
		m_sb.append("	<tr>");
		m_sb.append("		<td class='legendebreit textlinks'>Person</td>");
		m_sb.append("		<td class='legendebreit'>Urlaubstage " + Format.getString(m_datumVon) + "</td>");
		m_sb.append("		<td class='legendebreit'>genehmigte Urlaubstage</td>");
		m_sb.append("		<td class='legendebreit'>Urlaubstage " + Format.getString(datumBis) + "</td>");
		m_sb.append("		<td class='legendebreit'>geplante Urlaubstage</td>");
		m_sb.append("		<td class='legendebreit'>Resturlaub</td>");
		
		
		// Personen nach Namen sortieren
		mapPersonID = new TreeMap<String, Integer>();
		setPersonID = m_mapPersonName.keySet();
		for (Integer aktPpersonID : setPersonID)
		{
			mapPersonID.put(m_mapPersonName.get(aktPpersonID), aktPpersonID);
		}
		
		// Personenliste durchlaufen und Urlaub eintragen
		setPerson = mapPersonID.keySet();
		for (String name : setPerson)
		{
			personID = mapPersonID.get(name);
			coPerson.moveToID(personID);
			
			appendTabellenZeilenumbruch();
			m_sb.append("		<td class='textlinks'>" + name + "</td>");
			
			// Urlaub Anfang
			datumTmp = coPerson.getBeginnPze(); // falls die Person in dem Jahr erst angefangen hat
			coKontowert.loadEinfach(personID, m_datumVon.after(datumTmp) ? m_datumVon : datumTmp);
			anzUrlaubAnfang = coKontowert.getResturlaub();
			m_sb.append("		<td class='legendebreit'>" + anzUrlaubAnfang + "</td>");
			
			// Urlaub Ende
			datumTmp = coPerson.getEndePze(); // falls die Person in dem Jahr aufgehört hat
			if (datumTmp != null && datumTmp.before(datumBis))
			{
				datumBis = datumTmp;
			}
			coKontowert.loadEinfach(personID, datumBis);
			anzUrlaubEnde = coKontowert.getResturlaub();

			// genehmigter Urlaub
			m_sb.append("		<td class='legendebreit'>" + (anzUrlaubAnfang - anzUrlaubEnde) + "</td>");

			m_sb.append("		<td class='legendebreit'>" + anzUrlaubEnde + "</td>");
			
			anzUrlaubGeplant = CoBuchung.getAnzahlGeplantenUrlaub(personID, m_datumVon, datumBis);
			m_sb.append("		<td class='legendebreit'>" + anzUrlaubGeplant + "</td>");
			
			anzUrlaubRest = anzUrlaubEnde - anzUrlaubGeplant;
			m_sb.append("		<td class='legendebreit'>" + anzUrlaubRest + "</td>");
		}
		appendTabellenEnde();
		
	}


	/**
	 * Farbe für freie Tage bestimmen
	 * 
	 * @param datum
	 * @return
	 */
	private String getColorFreierTag(Date datum) {
		if (isFeiertag(datum)) 
		{
			return COLOR_FEIERTAG;
		}
		else if (isBrueckentag(datum))
		{
			return COLOR_BRUECKENTAG;
		}
		
		return COLOR_ABWESEND;
	}



	private void writeLabelMonat(int monat) {
		m_sb.append("<div class='no-break'>\n");
		m_sb.append("<h3 style='margin-top: 9px; margin-bottom: 6px;' > " + Format.getMonat(monat-1) + ":</h3>\n");
	}


	/**
	 * Neue Tabelle mit den Tagen des Monats erstellen
	 * 
	 * @param datum
	 */
	private void writeNewTableMitTagenDesMonats(Date datum) {
		int iTag;
		int anzTage;
		String color;
		GregorianCalendar gregDatum;

		//				m_sb.append("<div class='floatleft' style='width: 100%; margin-top: 20px;'>\n");
		m_sb.append("<table class='" + getClassTable() + "' style=' " + getStyleTable() + "'>\n");
		m_sb.append("<tr>\n");

		
		// Beschriftung der Tabelle ("Person")
		m_sb.append("<td class='" + getClassTdDaten(0, 0) + "' " + getHtmlAttribute(0, 0) + ">");
		m_sb.append("Person");
		m_sb.append("</td>\n");

		
		// Tage des Monats und Wochentage ausgeben
		gregDatum = Format.getGregorianCalendar(datum);
		anzTage = Format.getAnzTageDesMonats(datum);
		for (iTag=1; iTag<=anzTage; ++iTag)
		{
			gregDatum.set(GregorianCalendar.DAY_OF_MONTH, iTag);
			
			if (isFerientag(gregDatum))
			{
				color = COLOR_FERIEN;
			}
			else
			{
				color = null;
			}

			m_sb.append("<td class='" + getClassTdDaten(0, 1)  +  "' " 
					+ (color == null ? "" : "bgcolor='" + color.replace("##", "#") +  "' ")
					+ getHtmlAttribute(0, 1) + ">");
			m_sb.append(iTag + "<br />" + Format.getWochentagAbkuerzung(gregDatum));
			m_sb.append("</td>\n");
		}

		m_sb.append("</tr>\n");
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
			m_sb.append("<td class='" + getClassTdDaten(0, 1) + "' " + getHtmlAttribute(0, 1) 
				+ " bgcolor='" + COLOR_ABWESEND.replace("##", "#") + "' >");
			m_sb.append("</td>\n");
		}
	}


	/**
	 * letzte Zeile und Tabelle schließen
	 */
	private void closeTable() {
		m_sb.append("</tr>\n");
		m_sb.append("</table>\n");
		m_sb.append("</div>\n");
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


	/**
	 * AL, Sekretariat und höhere dürfen Details sehen, alle anderen nur für sich selbst
	 * 
	 * @return
	 * @throws Exception
	 */
	private boolean detailsAnzeigen() throws Exception {
		int personID;
		UserInformation userInformation;
		
		userInformation = UserInformation.getInstance();
		personID = m_formAuswertung.getCoAuswertung().getPersonID();
		
		// AL und alle höheren dürfen mehr sehen
		if (userInformation.isAL() || userInformation.isPersonalansicht())
		{
			return true;
		}
		
		// für sich selbst darf man die Details sehen
		if (UserInformation.isPerson(personID))
		{
			return true;
		}
		
		return false;
	}


	/**
	 * Prüft, ob das Datum ein Feiertag ist
	 * 
	 * @param datum
	 * @return
	 */
	private boolean isFeiertag(Date datum)
	{
		return m_feiertagGenerator.isFeiertag(datum, CoBundesland.ID_NRW);
	}


	/**
	 * Prüft, ob das Datum ein Brückentag ist
	 * 
	 * @param datum
	 * @return
	 */
	private boolean isBrueckentag(Date datum)
	{
		return m_coBrueckentag.isBrueckentag(datum, CoBundesland.ID_NRW);
	}


	/**
	 * Prüft, ob das Datum ein Ferientag ist
	 * 
	 * @param datum
	 * @return
	 */
	private boolean isFerientag(GregorianCalendar datum)
	{
		return m_coFerien.isFerientag(datum);
	}


	private String getBackgroundColor(String color) {
		return color == null ? "" : " bgcolor='" + color.replace("##", "#") + "' ";
	}


	private String getBackgroundImage(String farbe) {
		String value;
		
		if (farbe == null)
		{
			return "";
		}
		
		value = " background='file://";
		value += "/" + Application.getWorkingDirectory().replace("\\", "/") + "Urlaub_" + farbe + ".png";
		value += "' ";

		return value;
	}

	
	/**
	 * Text mit umschließendem Div zurückgeben, um eine durchgestrichene zelle zu erzeugen
	 * 
	 * @param text
	 * @return
	 */
	private String getTextDurchgestrichen(String text) {
		int length, margin;
		String stringValue;
		
		margin = 0;
		
		if (text == null)
		{
			text = "";
		}
		else
		{
			length = text.length();
			if (length == 1)
			{
				margin = 5;
			}
			else if (length == 2)
			{
				margin = 3;
			}
		}
		
		stringValue = "";
		stringValue += "			<div class='parent'> \n";
		stringValue += "			<div class='blackbar childTop' style='width: 18px;'></div> \n";
//		stringValue += "			<div class='childBack' style='margin-left: 5px;'>" + (text == null ? "" : text) + "</div> \n";
		stringValue += "			<div class='childBack' style='margin-left: " + margin + "px;'>" + text + "</div> \n";
		stringValue += "			</div> \n";

		return stringValue;
//		return "<div class='blackbar'>" + (text == null ? "" : text) + "</div>";
	}
	
}
