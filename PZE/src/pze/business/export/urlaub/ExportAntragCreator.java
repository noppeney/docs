package pze.business.export.urlaub;

import java.util.Date;

import framework.Application;
import pze.business.Format;
import pze.business.export.ExportPdfCreator;
import pze.business.objects.personen.CoBuchung;
import pze.business.objects.personen.CoKontowert;
import pze.business.objects.personen.CoPerson;
import pze.business.objects.personen.CoVertreter;
import pze.business.objects.reftables.buchungen.CoBuchungsart;
import pze.ui.formulare.UniFormWithSaveLogic;
import pze.ui.formulare.person.FormPersonUrlaubsplanung;

/**
 * Formular zur Erstellung des Urlaubsantrags, Sonderurlaub und FA
 * 
 * @author lisiecki
 */
public abstract class ExportAntragCreator extends ExportPdfCreator
{	
	protected CoPerson m_coPerson;
	protected CoBuchung m_coUrlaub;
	

	public String createHtml(UniFormWithSaveLogic formPersonUrlaubsantrag) throws Exception {
		
		m_coPerson = ((FormPersonUrlaubsplanung) formPersonUrlaubsantrag).getCoPerson();
		m_coUrlaub = ((FormPersonUrlaubsplanung) formPersonUrlaubsantrag).getCoUrlaub();
		
		m_sb = new StringBuilder();
		
		writeHtmlOpen();
		
		// Footer am Beginn schreiben, da es eine allgemeine Darstellung ist
		writeFooter();
		
		// Seite mit dem Antrag
		writeSeite();

		writeHtmlClose();

		return m_sb.toString();
	}


	/**
	 * Titel des Antrags
	 * @return
	 */
	protected abstract String getTitel();
	

	/**
	 * Seite mit dem Antrag schreiben
	 * 
	 * @return
	 * @throws Exception
	 */
	private boolean writeSeite() throws Exception {
		m_sb.append("<div class='page'>\n");
		
		m_sb.append("<div class='floatleft' style='width: 100%; margin-top: 20px; font-size: 15px;'>\n");
		
		// Antrag inhaltlich erstellen
		writeAntrag();

		m_sb.append("</div>\n"); // floatleft
		m_sb.append("</div>\n"); // page

		return true;
	}


	/**
	 * Antrag inhaltlich erstellen
	 * @throws Exception 
	 */
	protected void writeAntrag() throws Exception {
		appendPersonalDaten();
		appendStellvertreter();
		appendGenehmigung();
		appendAblehnung();
	}
	
	
	/**
	 * Abschnitt mit Personal- & Antragsdaten
	 * @throws Exception 
	 */
	private void appendPersonalDaten() throws Exception
	{
		int zeitVon, zeitBis;
		boolean einTag;
		String text, zusatzinfo1, zusatzinfo2;
		Date datumVon, datumBis;
		
		appendTabellenAnfang();

		m_sb.append("				<td colspan='5' class='borderBottom' style='font-size: 20px;'>" + insertTextFett(getTitel()) + "</td>\n");

		appendTabellenZeilenumbruchMitHoherZeile();
		
		// Person
		appendLeereZelleMitBreite(5);
		appendTextUnterstrichen("Name: " + m_coPerson.getNachname());
		appendTextUnterstrichen("Vorname: " + m_coPerson.getVorname());
		appendTextUnterstrichen("Personal-Nr.: " + m_coPerson.getPersonalnummer());
		appendLeereZelleMitBreite(5);
		
		appendTabellenZeilenumbruchMitHoherZeile();
		
		// Zeitraum
		appendLeereZelleMitBreite(5);
		zeitVon = m_coUrlaub.getUhrzeitAsInt();
		zeitBis = m_coUrlaub.getUhrzeitBisAsInt();
		datumVon = m_coUrlaub.getDatum();
		datumBis = m_coUrlaub.getDatumBis();
		einTag = datumVon.equals(datumBis);

		text = (einTag ? "am " : "vom ") + Format.getStringValue(datumVon) + " (" + (zeitVon > 0 ? "ab " + Format.getZeitAsText(zeitVon) : "")
				+ (einTag && zeitBis > 0 ? " bis " + Format.getZeitAsText(zeitBis) : "") + ")";
		appendTextUnterstrichen(text.replace("( ", "(").replace("()", "")); // "fehlerhafte" Ausgaben abfangen, ist so am einfachsten 
		appendTextUnterstrichen(einTag ? "" : ("bis zum " + Format.getStringValue(datumBis) + (zeitBis > 0 ? " (" + Format.getZeitAsText(zeitBis) + ")" : "")));
		appendLeereZelle(2);
		
		// Zusatzinformationen
		zusatzinfo1 = getZusatzinfoPersonal();

		zusatzinfo2 = getZusatzinfoZeitraum() + " " + getZusatzinfoVertretung();
		zusatzinfo2 = zusatzinfo2.trim();

		// neue Zeile 
		appendTabellenZeilenumbruchMitHoherZeile();
		appendLeereZelleMitBreite(5);

		if (!zusatzinfo1.isEmpty())
		{
			appendTextUnterstrichen(zusatzinfo1, 2);
		}
		
		// wenn es 2 Zeilen Zusatzinformationen gibt, nochmal neue Zeile
		if (!zusatzinfo1.isEmpty() && !zusatzinfo2.isEmpty())
		{
			appendLeereZelle(2);
			appendTabellenZeilenumbruchMitHoherZeile();
			appendLeereZelleMitBreite(5);
		}

		if (!zusatzinfo2.isEmpty())
		{
			appendTextUnterstrichen(zusatzinfo2, 2);
		}
		
		// wenn es keine Zusatzinformationen gibt, muss eine Dummy-Zelle erzeugt werden, 
		// damit Datum und Unterschrift an der richtigen Stelle stehen
		else if (zusatzinfo1.isEmpty() && zusatzinfo2.isEmpty())
		{
			appendLeereZelle(2);
		}

		
		m_sb.append("				<td class='unsichtbar textlinkseingerueckt'> " + Format.getString(new Date()) + "</td>\n");
		appendLeereZelle(1);

		appendTabellenEnde();
		
		appendDatumUnterschrift("Antragsteller");
	}

	
	/**
	 * Es wir eine Text erstellt auf einer Breite von 33% der Seite, wo das ganze Element unterstrichen ist
	 * 
	 * @param text
	 */
	private void appendTextUnterstrichen(String text) 
	{
		appendTextUnterstrichen(text, 1);
	}
	
	
	/**
	 * Es wir eine Text erstellt auf einer Breite von 33% der Seite, wo das ganze Element unterstrichen ist
	 * 
	 * @param text
	 */
	private void appendTextUnterstrichen(String text, int anzDrittel) 
	{
		m_sb.append("				<td colspan='" + anzDrittel + "' class='unsichtbar borderBottomDuenn textlinkseingerueckt' style='width:33%;'>" 
				+ text + "</td>\n");
	}
	

	/**
	 * Zusätzliche Infos wie Stand Zeitkonto, Grund...
	 * 
	 * @return
	 * @throws Exception 
	 */
	protected abstract String getZusatzinfoPersonal() throws Exception;


	/**
	 * Zusätzliche Infos ob Anträge ersetzt oder verlängert werden
	 * 
	 * @return
	 * @throws Exception 
	 */
	private String getZusatzinfoZeitraum() throws Exception {
		String meldung;
		
		
		// wenn Buchungen ersetzt werden
		if ((meldung = getZusatzinfoZeitraumErsatz()) != null)
		{
			return meldung;
		}
		
		// wenn Buchungen von den Vortagen verlängert werden 
		if ((meldung = getZusatzinfoZeitraumVorher()) != null)
		{
			return meldung;
		}
		
		// wenn Buchungen von den nachfolgenden Tagen verlängert werden 
		if ((meldung = getZusatzinfoZeitraumNachher()) != null)
		{
			return meldung;
		}
		
		return "";
	}


	/**
	 * Zusätzliche Infos ob Buchungen ersetzt werden
	 * 
	 * @return
	 * @throws Exception 
	 */
	private String getZusatzinfoZeitraumErsatz() throws Exception {
		String tagesbuchung, aktTagesbuchung;
		Date datumVon, datumBis;
		CoKontowert coKontowert;
		
		datumVon = null;
		datumBis = null;
		tagesbuchung = null;


		// alle Kontowerte für den Zeitraum der Buchung laden
		coKontowert = new CoKontowert();
		coKontowert.load(m_coUrlaub.getPersonID(), m_coUrlaub.getDatum(), m_coUrlaub.getDatumBis());
		
		if (!coKontowert.moveFirst())
		{
			return null;
		}
		
		// Kontowerte durchlaufen
		do
		{
			// wenn es ein Arbeitstag war, prüfe ob schon Buchungen vorhanden sind
			if (coKontowert.getWertSollArbeitszeit() > 0)
			{
				aktTagesbuchung = coKontowert.getBuchungsartTagesbuchung();
				// wenn es keine Tagesbuchung ist, aber es schon Tagesbuchungen gab, gebe diese zurück
				if (aktTagesbuchung == null) 
				{
					if (tagesbuchung != null)
					{
						break;
					}
				}
				else // wenn es eine Tagesbuchung ist
				{
					// wenn es die erste ist, speichere sie
					if (tagesbuchung == null)
					{
						tagesbuchung = aktTagesbuchung;
						datumVon = coKontowert.getDatum();
						datumBis = datumVon;
					}
					else if (tagesbuchung == aktTagesbuchung) // wenn es die gleiche wie vorher ist, Enddatum anpassen
					{
						datumBis = coKontowert.getDatum();
					}
					else // wenn es eine neue ist, beende die Prüfung
					{
						break;
					}
				}
			}
			
		} while (coKontowert.moveNext());
		
		
		return (tagesbuchung == null ? null : tagesbuchung + " (" + Format.getString(datumVon) + " - " + Format.getString(datumBis) + ")");
	}


	/**
	 * Zusätzliche Infos ob Buchungen an den vorherigen Tagen verlängert werden
	 * 
	 * @return
	 * @throws Exception 
	 */
	private String getZusatzinfoZeitraumVorher() throws Exception {
		int personID;
		String tagesbuchung, aktTagesbuchung;
		Date datum, datumVon, datumBis;
		CoKontowert coKontowert;
		
		personID = m_coUrlaub.getPersonID();
		datum = m_coUrlaub.getDatum();
		datumVon = null;
		datumBis = null;

		coKontowert = new CoKontowert();
		
		
		// Kontowerte durchlaufen
		tagesbuchung = null;
		do
		{
			// Kontowerte für den nächsten tag laden
			datum = Format.getDateVerschoben(datum, -1);
			coKontowert.load(personID, datum);
			if (!coKontowert.moveFirst())
			{
				break;
			}
			
			// wenn es ein Arbeitstag war, prüfe ob schon Buchungen vorhanden sind
			if (coKontowert.getWertSollArbeitszeit() > 0)
			{
				aktTagesbuchung = coKontowert.getBuchungsartTagesbuchung();
				// wenn es keine Tagesbuchung ist oder krank, ist die Prüfung beendet
				if (aktTagesbuchung == null || aktTagesbuchung.equals(CoBuchungsart.getInstance().getBezeichnung(CoBuchungsart.ID_KRANK))
						|| aktTagesbuchung.equals(CoBuchungsart.getInstance().getBezeichnung(CoBuchungsart.ID_KRANK_OHNE_LFZ)))
				{
//					if (tagesbuchung != null)
					{
						break;
					}
				}
				else // wenn es eine Tagesbuchung ist
				{
					// wenn es die erste ist, speichere sie
					if (tagesbuchung == null)
					{
						tagesbuchung = aktTagesbuchung;
						datumVon = coKontowert.getDatum();
						datumBis = datumVon;
					}
					else if (tagesbuchung == aktTagesbuchung) // wenn es die gleiche wie vorher ist, Startdatum anpassen
					{
						datumVon = coKontowert.getDatum();
					}
					else // wenn es eine neue ist, beende die Prüfung
					{
						break;
					}
				}
			}
			
		} while (true);
		
		
		return (tagesbuchung == null ? null : tagesbuchung + " (" + Format.getString(datumVon) + " - " + Format.getString(datumBis) + ")");
	}


	/**
	 * Zusätzliche Infos ob Buchungen an den nachfolgenden Tagen verlängert werden
	 * 
	 * @return
	 * @throws Exception 
	 */
	private String getZusatzinfoZeitraumNachher() throws Exception {
		int personID;
		String tagesbuchung, aktTagesbuchung;
		Date datum, datumVon, datumBis;
		CoKontowert coKontowert;
		
		personID = m_coUrlaub.getPersonID();
		datum = m_coUrlaub.getDatumBis();
		datumVon = null;
		datumBis = null;

		coKontowert = new CoKontowert();
		
		// Kontowerte durchlaufen
		tagesbuchung = null;
		do
		{
			// Kontowerte für den nächsten tag laden
			datum = Format.getDateVerschoben(datum, 1);
			coKontowert.load(personID, datum);
			if (!coKontowert.moveFirst())
			{
				break;
			}
			
			// wenn es ein Arbeitstag war, prüfe ob schon Buchungen vorhanden sind
			if (coKontowert.getWertSollArbeitszeit() > 0)
			{
				aktTagesbuchung = coKontowert.getBuchungsartTagesbuchung();
				// wenn es keine Tagesbuchung ist oder krank, ist die Prüfung beendet
				if (aktTagesbuchung == null || aktTagesbuchung.equals(CoBuchungsart.getInstance().getBezeichnung(CoBuchungsart.ID_KRANK))
						|| aktTagesbuchung.equals(CoBuchungsart.getInstance().getBezeichnung(CoBuchungsart.ID_KRANK_OHNE_LFZ)))
				{
//					if (tagesbuchung != null)
					{
						break;
					}
				}
				else // wenn es eine Tagesbuchung ist
				{
					// wenn es die erste ist, speichere sie
					if (tagesbuchung == null)
					{
						tagesbuchung = aktTagesbuchung;
						datumVon = coKontowert.getDatum();
						datumBis = datumVon;
					}
					else if (tagesbuchung == aktTagesbuchung) // wenn es die gleiche wie vorher ist, Enddatum anpassen
					{
						datumBis = coKontowert.getDatum();
					}
					else // wenn es eine neue ist, beende die Prüfung
					{
						break;
					}
				}
			}
			
		} while (true);
		
		
		return (tagesbuchung == null ? null : tagesbuchung + " (" + Format.getString(datumVon) + " - " + Format.getString(datumBis) + ")");
	}


	/**
	 * Info, ob die Person irgendwo als Vertretung eingetragen wurde
	 * 
	 * @return
	 * @throws Exception
	 */
	private String getZusatzinfoVertretung() throws Exception {
		CoVertreter coVertreter;
		coVertreter = new CoVertreter();
		coVertreter.loadForVertreter(m_coUrlaub.getPersonID(), m_coUrlaub.getDatum(), m_coUrlaub.getDatumBis());
		
		return coVertreter.getMeldungVertretungFuer();
	}


	/**
	 * Abschnitt mit Stellvertretern erstellen
	 * @throws Exception 
	 */
	private void appendStellvertreter() throws Exception
	{
		String stringTabelleStellvertreter;
		CoVertreter coVertreter;
		
		// Vertreter laden
		coVertreter = new CoVertreter();
		coVertreter.loadVertreter(m_coPerson.getID(), m_coUrlaub.getDatum(), m_coUrlaub.getDatumBis());
		stringTabelleStellvertreter = getStringTabelleStellvertreter(coVertreter);

		appendTabellenAnfang();

		m_sb.append("				<td colspan='" + (stringTabelleStellvertreter.isEmpty() ? 1 : 4) + "' class='unsichtbar textlinks' style='padding-left: 10px;'> "
				+ "Stellvertreter während meiner Abwesenheit ist: " + (stringTabelleStellvertreter.isEmpty() ? " nicht vorgesehen" : "") + "</td>\n");
		
		m_sb.append(stringTabelleStellvertreter);
		
		appendTabellenEnde();
		
		appendDatumUnterschrift(null);
//		appendDatumUnterschrift("Stellvertreter");
	}
	
	
	/**
	 * Tabelle mit Stellvertretern erstellen
	 * @param coVertreter 
	 * @return 
	 * @throws Exception 
	 */
	private String getStringTabelleStellvertreter(CoVertreter coVertreter) throws Exception
	{
		StringBuilder sb, sbUeberschrift;
		
		sb = new StringBuilder();
		sbUeberschrift = new StringBuilder();
		
		// Überschrift
		appendTabellenZeilenumbruch(sbUeberschrift);
		sbUeberschrift.append("				<td>" + insertTextFett("Name") + "</td>\n");
		sbUeberschrift.append("				<td>" + insertTextFett("Datum") + "</td>\n");
		sbUeberschrift.append("				<td>" + insertTextFett("bis") + "</td>\n");
		sbUeberschrift.append("				<td style='width: 250px' >" + insertTextFett("Datum, Unterschrift") + "</td>\n");
		
		// Vertreter durchlaufen
		coVertreter.moveFirst();
		do
		{
			if (coVertreter.getVertreterID() == 0)
			{
				break;
			}
			
			appendTabellenZeilenumbruchMitHoherZeile(sb);
//			appendTabellenZeilenumbruch(sb);
			sb.append("				<td>" + coVertreter.getVertreter() + "</td>\n");
			sb.append("				<td>" + Format.getString(coVertreter.getDatum()) + "</td>\n");
			sb.append("				<td>" + Format.getString(coVertreter.getDatumBis()) + "</td>\n");
			sb.append("				<td></td>\n");
		} while (coVertreter.moveNext());
		
		// prüfen ob Vertreter vorhanden sind
		if (sb.toString().isEmpty())
		{
			return "";
		}
		
		return sbUeberschrift.toString() + sb.toString();
	}

	
	private void appendGenehmigung()
	{
		appendTabellenAnfang();

		appendLeereZelleMitBreite(5);
		m_sb.append("				<td class='unsichtbar textlinkseingerueckt' style='height: 30px;'> Antrag </td>\n");
		appendCheckboxTextRechts("genehmigt", false);
		appendCheckboxTextRechts("nicht genehmigt", false);
		appendLeereZelleMitBreite(300);
		
		appendTabellenEnde();
		
		appendDatumUnterschrift("AL");
	}
	
	
	private void appendAblehnung()
	{
		appendTabellenAnfang(15);

		appendLeereZelleMitBreite(5);
		m_sb.append("				<td class='borderBottomDuenn unsichtbar textlinkseingerueckt' style='width:160px;'> Bei Ablehnung Grund: </td>\n");
		appendLeereZelle();
		
		appendTabellenZeilenumbruch();
		appendLeereZelle(3);
		appendTabellenZeilenumbruch();
		appendLeereZelle(3);
		
		appendTabellenEnde();
		
		m_sb.append("<br/>");
		m_sb.append("<span class='blank'></span> \n");
		m_sb.append("<span class='blank'></span> \n");
		m_sb.append("<span class='blank'></span> \n");
		m_sb.append("<span class='blank'></span> \n");
		m_sb.append("<span class='blank'></span> \n");
		m_sb.append("<span class='blank' style='font-size: 12px;'> gebucht am: </span> \n");
		m_sb.append("<span class='unterstrich'></span> \n");
		appendDatumUnterschrift("Personalabteilung");
	}

	
	/**
	 * Erstellt in kleiner Schrift am Ende einer Zeile die Schrift Datum, Unterschrift und dahinter die übergebene Personengruppe
	 * 
	 * @param personengruppe
	 */
	private void appendDatumUnterschrift(String personengruppe) 
	{
		m_sb.append("<div style='margin-left:70%; font-size: 9px; height:20px;'>" 
				+ (personengruppe == null ? "" : "Datum, Unterschrift " + personengruppe) + "</div>\n");
	}
	
	
	/**
	 * Erstellt die html Befehle für den Beginn einer Tabelle mit einer Zeilenhöhe von 25px
	 */
	private void appendTabellenAnfang()
	{
		appendTabellenAnfang(25);
	}
	
	
	/**
	 * Erstellt die html Befehle für den Beginn einer Tabelle mit einer der übergebenen Zeilenhöhe
	 * 
	 * @param hoehe
	 */
	protected void appendTabellenAnfang(int hoehe)
	{
		m_sb.append("        <table>\n");
		m_sb.append("            <tr style='height:" + hoehe + "px;'>\n");
	}
	
	
	/** 
	 * Es wird eine neue Tabellenzeile mit einer Höhe von 25px erzeugt und die vorherige geschlossen
	 */
	private void appendTabellenZeilenumbruchMitHoherZeile()
	{
		appendTabellenZeilenumbruchMitHoherZeile(m_sb);
	}
	
	
	/** 
	 * Es wird eine neue Tabellenzeile mit einer Höhe von 25px erzeugt und die vorherige geschlossen
	 * @param sb 
	 */
	private void appendTabellenZeilenumbruchMitHoherZeile(StringBuilder sb)
	{
		sb.append("            </tr>\n");
		sb.append("            <tr style='height:23px;'>\n");
	}
	

	@Override
	protected String getStand() {
		return "12/2020";
	}
	
	
	//TODO ab hier sind alle Methoden gleich wie beim Dienstreiseantrag können also gelöscht werden
	// prüfen mitr dienstreise-klasse
	/**
	 * Fügt den Text in Fett ein
	 * 
	 * @param text
	 * @return
	 */
	protected String insertTextFett(String text) 
	{
		return "						<b>" + text + "</b>"; 
	}
	
	
	/**
	 * Es wird eine Leere Zelle, die unsichtbar ist erzeugt mit der angegebenen Breite
	 */
	protected void appendLeereZelleMitBreite(int breite) 
	{
		m_sb.append("						<td class='unsichtbar' style='width: " + breite + "px'> &nbsp; </td>"); 
	}
	
	
	/**
	 * Es wird eine Leere Zelle, die unsichtbar ist erzeugt
	 */
	private void appendLeereZelle() 
	{
		appendLeereZelle(1);
	}
	
	
	/**
	 * Es werden so viele unsichtbare Leere Zellen in einer Zeile erzeugt wie mit dem Parameter angegeben werden
	 * 
	 * @param anzahlSpalten
	 */
	protected void appendLeereZelle(int anzahlSpalten) 
	{
		int i;
		for(i = 0; i < anzahlSpalten; i++) 
		{
			m_sb.append("						<td class='unsichtbar'> &nbsp; </td>"); 
		}
		
	}
	
	
	/**
	 * Erstellt eine Checkbox, die abhängig vom uebergebenen Wert ausgewaehlt ist oder nicht
	 * 
	 * @param checked
	 */
	private void appendCheckbox(boolean checked) 
	{
		if (checked)
		{
//			m_sb.append("<img class='imgboxen' src='checked.png'></img>\n");
			m_sb.append("<img class='imgboxen' src='/" + Application.getWorkingDirectory().replace("\\", "/") + "checked.png'></img>\n");
		}
		else
		{
//			m_sb.append("<img class='imgboxen' src='unchecked.png'></img>\n");
			m_sb.append("<img class='imgboxen' src='/" + Application.getWorkingDirectory().replace("\\", "/") + "unchecked.png'></img>\n");
		}
	}
	
	
	/** 
	 * Es wird eine Zelle mit einer Checkbox und eine mit der Beschriftung dahinter erzeugt
	 * 
	 * @param beschriftung
	 * @param checked Checkbox aktiviert oder nicht
	 */
	private void appendCheckboxTextRechts(String beschriftung, boolean checked)
	{
		// Zelle mit der Checkbox
		m_sb.append("                <td class='textlinks unsichtbar'>\n");
		appendCheckbox(checked);
		
		// Zelle mit der Beschriftung
		m_sb.append(beschriftung + "</td>\n");
	}
}