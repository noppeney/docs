package pze.business.export;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import framework.Application;
import pze.business.Format;
import pze.business.objects.dienstreisen.CoDienstreise;
import pze.business.objects.personen.CoPerson;
import pze.ui.formulare.UniFormWithSaveLogic;
import pze.ui.formulare.person.FormPersonDienstreisen;

/**
 * Klasse zum Erstellen einer HTML-Datei für den PDF-Export des DienstreiseAntrags
 * 
 * @author Noppeney
 */
public class ExportDienstreiseantragCreator extends ExportPdfCreator {

	private CoPerson m_coPerson;
	private CoDienstreise m_coDienstreise;
	
	// Texteingaben werden im Set gesammelt, um doppelte Ausgaben zu vermeiden
	private Set<String> m_setOrt;
	private Set<String> m_setZweck;
	private Set<String> m_setBemerkung;
	private Set<String> m_setHotel;
	private Set<String> m_setDienstwagen;

	// Felder zum ankreuzen müssen bei mehrtägigen Buchungen nur einmal angekreuzt sein
	boolean m_uebernachtung;
	boolean m_mietwagen;
	boolean m_dienstwagen;
	boolean m_privatPkw;
	boolean m_bahn;
	boolean m_flugzeug;
	

	/**
	 * Gibt einen DienstreiseAntrag in XHTML zurück.
	 * 
	 * @param m_formAuswertungDienstreisen 
	 * @return
	 * @throws Exception 
	 */
	@Override
	public String createHtml(UniFormWithSaveLogic form) throws Exception{
		m_coPerson = ((FormPersonDienstreisen) form).getCoPerson();
		m_coDienstreise = (CoDienstreise) ((FormPersonDienstreisen) form).getCoDienstreiseAntrag();
		m_sb = new StringBuilder();
		
		// Daten der Dienstreisen auslesen und aufbereiten, insbesondere für mehrtägige Dienstreisen
		initData();
		
		writeHtmlOpen();
		
		// Footer am Beginn schreiben, da es eine allgemeine Darstellung ist
		writeFooter();
		
		writeSeite();

		writeHtmlClose();

		return m_sb.toString();
	}


	/**
	 * Daten der Dienstreisen auslesen und aufbereiten, insbesondere für mehrtägige Dienstreisen
	 */
	private void initData() {
		
		m_setOrt = new HashSet<String>();
		m_setZweck = new HashSet<String>();
		m_setBemerkung = new HashSet<String>();
		m_setHotel = new HashSet<String>();
		m_setDienstwagen = new HashSet<String>();
		
		m_mietwagen = false;
		m_dienstwagen = false;
		m_privatPkw = false;
		m_bahn = false;
		m_flugzeug = false;
		m_uebernachtung = false;
		
		
		// alle zu betrachtenden Dienstreisen durchlaufen und Daten sammeln
		m_coDienstreise.moveFirst();
		do 
		{
//			Format.addStringToSet(m_coDienstreise.getOrt(), m_setOrt);
			Format.addStringToSet(m_coDienstreise.getZweck(), m_setZweck);
			Format.addStringToSet(m_coDienstreise.getBemerkung(), m_setBemerkung);
			
			// Hotel und Dienstwagen nur wenn Checkbox aktiviert ist
			if (m_coDienstreise.isUebernachtung())
			{
				Format.addStringToSet(m_coDienstreise.getHotel(), m_setHotel);
			}
//			if (m_coDienstreise.isDienstwagen())
//			{
//				Format.addStringToSet(m_coDienstreise.getDienstwagen(), m_setDienstwagen);
//			}
			
			m_mietwagen = m_mietwagen || m_coDienstreise.isMietwagen();
			m_dienstwagen = m_dienstwagen || m_coDienstreise.isDienstwagen();
			m_privatPkw = m_privatPkw || m_coDienstreise.isPrivatPkw();
			m_bahn = m_bahn || m_coDienstreise.isBahn();
			m_flugzeug = m_flugzeug || m_coDienstreise.isFlugzeug();
			m_uebernachtung = m_uebernachtung || m_coDienstreise.isUebernachtung();
			
		} while (m_coDienstreise.moveNext());
	}

	
	/**
	 * Eine Seite erstellen
	 * 
	 * @return Daten gefunden
	 * @throws Exception
	 */
	private boolean writeSeite() throws Exception {
		
		m_sb.append("<div class='page'>\n");
		
		writeHeader();
		
		m_sb.append("<div class='floatleft' style='width: 100%; margin-top: 20px;'>\n");
		
		appendHtmlStringTable();

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

		// Überschrift 
		m_sb.append("<h1>Dienstreiseantrag</h1>\n");
		
		m_sb.append("</div>\n"); // links
		
		// Logo
		m_sb.append("<div class='drechts'>\n");
		m_sb.append("<img src='/" + Application.getWorkingDirectory().replace("\\", "/") + "WTI_Logo_2023_Schwarz.png'></img>\n");
		
		
		m_sb.append("</div>\n"); // rechts
		m_sb.append("</div>\n"); // oben
	}
	

	/**
	 * Es wird der Html-String erzeugt und an den Stringbuilder m_sb angehängt
	 * 
	 */
	private void appendHtmlStringTable() 
	{
		appendBlockMitarbeiterProjekt();
		appendBlockReiseziel();
		appendBlockZweck();
		appendBlockDauer();
		appendBlockUebernachtung();
		appendBlockBefoerderungsmittelBemerkung();
		appendBlockGenehmigung();
	}
	
	
	/**
	 * Es wir ein Block erstellt, in dem der Mitarbeiter und die Projektdaten eintragen werden
	 * 
	 */
	private void appendBlockMitarbeiterProjekt() 
	{
		appendTabellenAnfang(true, 2, 1);
		appendTabellenZeilenAnfang();
		appendText(insertTextFett("Name des Mitarbeiters"));
		appendText(insertTextFett("WTI-Abteilung"));
		
		appendText(insertTextFett("Auftrags-Nr.: ") + insertTextGroß(m_coDienstreise.getAuftragsNr()));
		appendTabellenZeilenumbruch();
		
		appendTextUeberZweizeilen(m_coPerson.getName(), true);
		appendTextUeberZweizeilen(m_coPerson.getAbteilung(), true);
		
		appendText(insertTextFett("Kostenstelle: ") + insertTextGroß(m_coDienstreise.getKostenstelle()));
		appendTabellenZeilenumbruch();
		
		appendText(insertTextFett("Abruf-Nr.: ") + insertTextGroß(m_coDienstreise.getAbrufNr()));
		appendTabellenEnde();
	}
	
	
	/**
	 * Es wir ein Block erstellt, in dem das Reiseziel und der Kunde eintragen werden
	 */
	private void appendBlockReiseziel() 
	{
		appendTabellenAnfang(true, 0, 1);
		appendTabellenZeilenAnfang(25);
		m_sb.append("						<td class='textlinkseingerueckt unsichtbar borderRightDuenn' style='width:68%'>" + insertTextFett("Reiseziel/Reiseverlauf") + "</td>");
		m_sb.append("						<td class='textlinkseingerueckt unsichtbar' style='width:34%;'>" + insertTextFett("Kunde:") + "</td>"); 
		appendLeereZelle();
		appendTabellenZeilenumbruch();
		appendTextUeberZweizeilen(Format.getStringValue(m_setOrt), true);
//		appendTextUeberZweizeilen(m_coDienstreise.getKunde(), true);
//		m_sb.append("						<td rowspan='2' class='textlinkseingerueckt unsichtbar'>"); 
//		m_sb.append("1");
//		appendCheckbox(true);
//		m_sb.append(" &nbsp; &nbsp; &nbsp; &nbsp; 2");
//		appendCheckbox(false);
//		m_sb.append(" &nbsp; &nbsp; &nbsp; &nbsp; 3");
//		appendCheckbox(false);
//		m_sb.append(" </td>");
		appendLeereZelle();
		appendTabellenZeilenumbruch();
		appendText("&nbsp;");
		appendTabellenEnde();
	}
	
	
	/**
	 * Es wir ein Block erstellt, in dem man den Zweck der Reise eingetragen wird
	 * 
	 */
	private void appendBlockZweck() 
	{
		appendBlock("Zweck der Reise/ Begründung der dienstlichen Notwendigkeit (Einladung, Programme, TOP etc.)", Format.getStringValue(m_setZweck));
	}
	
	
	/**
	 * Block mit einer Bezeichnung für die Überschrift und dem Wert, der in dem Block eingetragen werden soll
	 * 
	 * @param bezeichnung
	 * @param wert
	 */
	private void appendBlock(String bezeichnung, String wert) 
	{
		appendTabellenAnfang(false, 0, 1);
		appendTabellenZeilenAnfang(25);
		m_sb.append("						<td class='unsichtbar textlinkseingerueckt'>" + insertTextFett(bezeichnung) + "</td>"); 
		appendLeereZelle();
		appendTabellenZeilenumbruch();
		appendTextUeberZweizeilen(wert, false);
		appendLeereZelle();
		appendTabellenZeilenumbruch();
		appendLeereZelle();
		appendTabellenEnde();
	}
	
	
	/**
	 * Es wir ein Block erstellt, indem das Datum und die Uhrzeit für den Begin bzw. Ende der Reise eingetragen werden kann
	 * 
	 */
	private void appendBlockDauer()
	{
		appendTabellenAnfang(true, 0, 1);
		appendTabellenZeilenAnfang(25);
		m_sb.append("						<td colspan='2'>" + insertTextFett("Beginn der Reise:") + "</td>"); 
		m_sb.append("						<td colspan='2'>" + insertTextFett("Vorauss. Ende der Reise:") + "</td>");
		appendTabellenZeilenumbruch();
		
		m_sb.append("						<td> Tag / Mon / Jahr </td>"); 
		m_sb.append("						<td> Uhrzeit </td>");
		m_sb.append("						<td> Tag / Mon / Jahr </td>"); 
		m_sb.append("						<td> Uhrzeit </td>");

		
		// Dienstreisen durchlaufen
		m_coDienstreise.moveFirst();
		do 
		{
			appendTabellenZeilenEnde();
			appendTabellenZeilenAnfang(30);
//			appendTextOhneBorderBottonFett(Format.getString(m_coDienstreise.getDatum()));
//			appendTextOhneBorderBottonFett(Format.getZeitAsText(m_coDienstreise.getAnfang()) + " Uhr");
//			appendTextOhneBorderBottonFett(Format.getString(m_coDienstreise.getDatum()));
//			appendTextOhneBorderBottonFett(Format.getZeitAsText(m_coDienstreise.getEnde()) + " Uhr");
		} while (m_coDienstreise.moveNext());
		
		appendTabellenEnde();
	}
	
	
	/**
	 * Es wir ein Block erstellt, indem angekreuzt werden kann ob man eine Übernachtung hat und falls ja kann man eintragen in welchem Hotel
	 * 
	 */
	private void appendBlockUebernachtung() 
	{
		appendTabellenAnfang(false, 0, 1);
		appendTabellenZeilenAnfang();
		m_sb.append("						<td class='textlinkseingerueckt unsichtbar breite150px textobeneingerueckt'>" + insertTextFett("Übernachtung:") + "</td>");
		appendLeereZelle(2);
		appendTabellenZeilenumbruch();
		appendLeereZelle();
		m_sb.append("						<td class='textlinks unsichtbar breite150px'>");
		
		appendCheckbox(m_uebernachtung);
		m_sb.append(" ja </td>");
		
		appendText("Hotel: " + (m_uebernachtung ? Format.getStringValue(m_setHotel) : ""), 15);
		appendTabellenZeilenumbruch();
		appendLeereZelle();
		m_sb.append("						<td class='textlinks unsichtbar'>");
		
		appendCheckbox(!m_uebernachtung);
		m_sb.append(" nein </td>");
		
		appendLeereZelle();
		appendTabellenZeilenumbruch();
		appendLeereZelle(3);
		appendTabellenEnde();
	}
	
	
	/**
	 * Es wir ein Block erstellt, in dem angekreuzt Beförderungmittel und die Bemerkungen eingetragen werden
	 * 
	 */
	private void appendBlockBefoerderungsmittelBemerkung() 
	{
		String dienstwagen;
		
		dienstwagen = Format.getStringValue(m_setDienstwagen);
		
		appendTabellenAnfang(false, 0, 0);
		appendTabellenZeilenAnfang();
		m_sb.append("						<td colspan='2' class='textlinkseingerueckt unsichtbar textobeneingerueckt'>" + insertTextFett("Beförderungsmittel:") + "</td>");
		m_sb.append("						<td class='textlinkseingerueckt unsichtbar textobeneingerueckt' style='width:50%;'>" + insertTextFett("Bemerkung:") + "</td>");
		appendLeereZelleMitBreite(25);
		appendTabellenZeilenumbruch();
		appendLeereZelle(4);
		
		appendTabellenZeilenumbruch();
		appendLeereZelleMitBreite(25);
		appendCheckboxTextRechts("Privat-PKW", m_privatPkw);
		
		// Bemerkung eintragen
		appendTextfeldBemerkung();
		
		appendTabellenZeilenumbruch();
		appendLeereZelleMitBreite(25);
		appendCheckboxTextRechts("Dienstwagen " + (dienstwagen == null ? "" : dienstwagen), m_dienstwagen);
		appendTabellenZeilenumbruch();
		appendLeereZelleMitBreite(25);
		appendCheckboxTextRechts("Mietwagen", m_mietwagen);
		appendTabellenZeilenumbruch();
		appendLeereZelleMitBreite(25);
		appendCheckboxTextRechts("Bahn", m_bahn);
		appendTabellenZeilenumbruch();
		appendLeereZelleMitBreite(25);
		appendCheckboxTextRechts("Flugzeug", m_flugzeug);
		appendTabellenEnde();
	
		appendBlockUnterschrift("Mitarbeiters", false, true);
	}
	
	
	/**
	 * Es wir ein Textfeld für Bemerkungen erstellt
	 * 
	 */
	private void appendTextfeldBemerkung() 
	{
		String bemerkung;
		
		bemerkung = Format.getStringValue(m_setBemerkung);
		
		m_sb.append("						<td rowspan='5' class='textlinkseingerueckt unsichtbar' style='vertical-align:top'>");
		
		if (bemerkung != null)
		{
			m_sb.append(bemerkung);
		}
		
		m_sb.append("						</td>");
		m_sb.append("						<td rowspan='5' class='unsichtbar'></td>");
	}
	

	/**
	 * Es wir ein Block für die Unterschrift des Abteilungsleiters erstellt
	 * 
	 */
	private void appendBlockGenehmigung() 
	{
		appendTabellenAnfang(false, 0, 0);
		appendTabellenZeilenAnfang();
		m_sb.append("						<td colspan='2' class='textlinkseingerueckt unsichtbar breite150px textobeneingerueckt'>" 
				+ insertTextFett("Genehmigung:") + "</td>");
		appendTabellenZeilenumbruch();
		appendLeereZelle(2);
//		appendTabellenZeilenumbruch();
//		appendLeereZelleMitBreite(25);
//		appendCheckboxTextRechts("Dienstreise wird genehmigt", true);
//		appendTabellenZeilenumbruch();
//		appendLeereZelleMitBreite(25);
//		appendCheckboxTextRechts("Dienstreise wird nicht genehmigt", false);
		appendTabellenEnde();

		appendBlockUnterschrift("Abteilungsleiters", true, false);
	}
	
	
	/**
	 * Es wird ein Block erstellt, indem nach einigen Leerzeilen, zwei Striche erstellt werden, um das Formular mit Datum und Unterschrift unterschreiben zu können
	 * 
	 * @param personengruppe Unterschrift des ...
	 * @param isLetzterBlock
	 */
	private void appendBlockUnterschrift(String personengruppe, boolean isLetzterBlock, boolean datumEintragen) 
	{
		// Wenn es der letzte Block auf der Seite ist, wird die Umrandung unten 2px breit anstatt 1px
		appendTabellenAnfang(false, 0, (isLetzterBlock ? 2 : 1));
		appendTabellenZeilenAnfang();
		appendLeereZelle(5);
		appendTabellenZeilenumbruch();
		appendLeereZelle(5);
		appendTabellenZeilenumbruch();
		appendLeereZelle(5);
		appendTabellenZeilenumbruch();
		appendLeereZelle(5);
		appendTabellenZeilenumbruch();
		appendLeereZelleMitBreite(10);
		m_sb.append("						<td class='textlinks unsichtbar noBorderBottom breite95px' style='border-top: 1px;'> "
				+ (datumEintragen ? "" : "<hr />") + "Jülich, den " + (datumEintragen ? Format.getString(new Date()) : "") + "</td>");
		appendLeereZelleMitBreite(150);
		m_sb.append("						<td class='textlinks unsichtbar noBorderBottom breite150px' style='border-top: 1px;'> <hr /> Unterschrift des " 
				+ personengruppe + " </td>");
		appendLeereZelleMitBreite(95);
		appendTabellenZeilenumbruch();
		appendLeereZelle(5);
		appendTabellenEnde();
	}
	

	/**
	 * Es wird eine Zelle mit der Höhe von 25px erstellt, bei der die Umrandung unsichtbar ist und nur rechts eine Dünne Grenze enthält
	 * Der Text wird linksbündig eingefügt 
	 * 
	 * @param text
	 */
	private void appendText(String text) 
	{
		appendText(text, 25);
	}
	
	
	/**
	 * Es wird eine Zelle erstellt, bei der die Umrandung unsichtbar ist und nur rechts eine Dünne Grenze enthält
	 * Der Text wird linksbündig eingefügt
	 * 
	 * @param text
	 */
	private void appendText(String text, int height) 
	{
		m_sb.append("						<td class='textlinkseingerueckt unsichtbar borderRightDuenn' style='height:" + height + "px;'>" 
				+ text + "</td>"); 
	}
	
	
	/**
	 * Es wird eine Zelle erstellt, bei der die Umrandung unsichtbar ist und nur rechts eine Dünne Grenze enthält
	 * Der Text wird linksbündig über zwei Zeilen mittig eingefügt 
	 * 
	 * @param text
	 */
	private void appendTextUeberZweizeilen(String text, boolean isBorderRight) 
	{
		m_sb.append("						<td rowspan='2' class='textlinkseingerueckt unsichtbar" + (isBorderRight ? " borderRightDuenn" : "") 
				+ "' style='height: 25px;'>" + insertTextGroß(text) + "</td>"); 
	}
	
	
	/**
	 * Es wird eine Zelle erstellt, die nur unten keinen Rahmen hat und der übergebene Text wird mittig und fett hinzugefügt
	 * 
	 * @param text
	 */
	private void appendTextOhneBorderBottonFett(String text) 
	{
		m_sb.append("						<td class='noBorderBottom fontweightbold fontsize16 '>" + text + "</td>"); 
	}
	
	
	/**
	 * Fügt den Text in Fett ein
	 * 
	 * @param text
	 * @return
	 */
	private String insertTextFett(String text) 
	{
		return "						<b>" + text + "</b>"; 
	}
	
	
	/**
	 * Fügt den Text in großer Schrift in der selben Zeile ein
	 * 
	 * @param text
	 * @return
	 */
	private String insertTextGroß(String text) 
	{
		return "						<inline class='fontsize16'>" + text + "</inline>"; 
	}
	
	
	/**
	 * Es wird eine Leere Zelle, die unsichtbar ist erzeugt mit der angegebenen Breite
	 */
	private void appendLeereZelleMitBreite(int breite) 
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
	private void appendLeereZelle(int anzahlSpalten) 
	{
		int i;
		for(i = 0; i < anzahlSpalten; i++) 
		{
			m_sb.append("						<td class='unsichtbar'> &nbsp; </td>"); 
		}
		
	}
	
	
	/** 
	 * Es werden eine neue Tabelle mit der Klasse feste_groesse erzeugt und die Border Breite kann übergeben werden
	 *
	 * @param isFesteGroesse true wenn alle Spalten die selbe Größe haben sollen
	 * @param borderTopWidth
	 * @param borderBottomWidth
	 */
	private void appendTabellenAnfang(boolean isFesteGroesse, int borderTopWidth, int borderBottomWidth)
	{
		m_sb.append("<table class='" + (isFesteGroesse ? "feste_groesse" : "") + "' style='border-top: " + borderTopWidth + "px solid; border-bottom: " 
				+ borderBottomWidth + "px solid;'>\n");
	}
	
	
	/**
	 * Es wird eine neue Tabellenspalte erzeugt, mit der standard Höhe eines <tr> wie es in der CSS-Datei steht
	 * 
	 * @param hoehe
	 */
	private void appendTabellenZeilenAnfang()
	{
		appendTabellenZeilenAnfang(0);
	}
	
	
	/**
	 * Es wird eine neue Tabellenspalte erzeugt, mit der übergebenen Höhe
	 * 
	 * @param hoehe
	 */
	private void appendTabellenZeilenAnfang(int hoehe)
	{
		m_sb.append("            <tr style='height: "+ (hoehe == 0 ? "" : hoehe) + "px;'>\n");
	}
	
	
	/** 
	 * Es wird eine Tabellenzeile geschlossen
	 */
	private void appendTabellenZeilenEnde()
	{
		m_sb.append("            </tr>\n");
	}
	

	/**
	 * Erstellt eine Checkbox, die abhängig vom uebergebenen Wert ausgewaehlt ist oder nicht
	 * 
	 * @param checked
	 */
	private void appendCheckbox(boolean checked) 
	{
		//Wenn der uebergebende Wert true ist, dann wird ein angekreuzte Checkbox hinzugefügt und ansonsten eine Leere Checkbox
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
	

	protected String getStand() {
		return "03/2023";
	}

}
