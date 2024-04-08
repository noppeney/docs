package pze.business.export.urlaub;

import java.util.Date;
import java.util.GregorianCalendar;

import pze.business.Format;
import pze.business.objects.personen.CoBuchung;
import pze.business.objects.personen.CoKontowert;
import pze.business.objects.reftables.buchungen.CoBuchungsart;

/**
 * Formular zur Erstellung des Urlaubsantrags
 * 
 * @author lisiecki
 */
public class ExportAntragUrlaubCreator extends ExportAntragCreator
{	
	private static final String TITEL = "Urlaubsantrag";
	

	@Override
	protected String getTitel() {
		return TITEL;
	}
	

	
	/**
	 * zusätzlich Urlaubstage, Resturlaub etc. ausgeben
	 * @throws Exception 
	 */
	@Override
	protected void writeAntrag() throws Exception {
		super.writeAntrag();
		appendResturlaub();
	}
	
	
	/**
	 * Keine Zusatzinfos
	 */
	protected String getZusatzinfoPersonal() {
		return "";
	}
	

	/**
	 * Abschnitt mit dem Resturlaub erstellen
	 * @throws Exception 
	 */
	private void appendResturlaub() throws Exception
	{
		int personID, jahr;
		int jahresurlaub, resturlaub, resturlaubVorjahr, anzahlUrlaubBeantragt;
		Date datum;
		GregorianCalendar gregDatum;
		CoKontowert coKontowert;
		
		coKontowert = new CoKontowert();

		// Personendaten
		personID = m_coUrlaub.getPersonID();
		jahresurlaub = m_coPerson.getJahresurlaub();
		
		// Datum des Urlaubs
		datum = m_coUrlaub.getDatum();
		gregDatum = Format.getGregorianCalendar(datum);
		jahr = gregDatum.get(GregorianCalendar.YEAR);

		// Resturlaub aktuellen Jahres (Jahr des Urlaubs) setzen
		gregDatum.set(GregorianCalendar.MONTH, GregorianCalendar.DECEMBER);
		gregDatum.set(GregorianCalendar.DAY_OF_MONTH, 31);
		coKontowert.load(personID, Format.getDateValue(gregDatum));
		// wenn der Eintrag nicht existiert, lade den letzten
		if (coKontowert.getRowCount() == 0)
		{
			coKontowert.loadLastEintrag(personID);
		}
		resturlaub = coKontowert.getResturlaub();
				
		// Resturlaub Vorjahr
		gregDatum.set(GregorianCalendar.YEAR, jahr-1);
		coKontowert.load(personID, Format.getDateValue(gregDatum));
		if (coKontowert.getRowCount() == 0)
		{
			resturlaubVorjahr = 0;
		}
		else
		{
			resturlaubVorjahr = coKontowert.getResturlaub();
		}
	
		// Urlaubstage dieses Antrags
		anzahlUrlaubBeantragt = CoBuchung.getAnzahlGenehmigteTage(personID, datum, m_coUrlaub.getDatumBis(), CoBuchungsart.ID_URLAUB);
		
		
		// Überschrift
		appendTabellenAnfang(15);
		m_sb.append("				<td colspan='6' class='borderBottom' style='font-size: 15px;'>" + insertTextFett("&nbsp;") 
		+ "</td>\n");
		appendTabellenZeilenumbruch();


		// Resturlaub Vorjahr
		appendResturlaubTage("Resturlaub aus " + (jahr-1), resturlaubVorjahr);
		appendLeereZelle(3);
		appendTabellenZeilenumbruch();
		
		// Jahresurlaub TODO Eintritt beachtem
		appendResturlaubTage("zustehender Erholungsurlaub " + jahr, jahresurlaub);
		appendLeereZelle(3);
		appendTabellenZeilenumbruch();
		
		// genehmigte Urlaubstage aktuelles Jahr
		appendResturlaubTage("bereits erhalten", resturlaubVorjahr+jahresurlaub - resturlaub);
		appendLeereZelle(3);
		appendTabellenZeilenumbruch();
		
		// Anzahl Urlaub beantragt mit diesem Antrag
		appendResturlaubTage("mit dieser Meldung beantragt", anzahlUrlaubBeantragt);
		appendLeereZelle(3);
		appendTabellenZeilenumbruch();
		
		// Resturlaub aktuelles Jahr
		appendResturlaubTage("Resturlaub " + jahr, resturlaub-anzahlUrlaubBeantragt);
		appendLeereZelleMitBreite(60);
		
		m_sb.append("				<td class='unsichtbar borderTopDuenn'> Personalabteilung </td>\n");
		appendLeereZelleMitBreite(30);
		
		m_sb.append("			</tr>\n");
		m_sb.append("        </table>\n");
	}

	
	/**
	 * Erzeugt zwei Elemente die Unterstrichen sind, in dem ersten steht der übergebene Text und im zweiten die Anzahl der Tage
	 * 
	 * @param text
	 * @param anzahlTage
	 */
	private void appendResturlaubTage(String text, int anzahlTage) 
	{
		appendLeereZelleMitBreite(5);
		m_sb.append("				<td class='unsichtbar borderBottomDuenn textlinkseingerueckt' style='height:20px;'>" + text + "</td>\n");
		m_sb.append("				<td class='unsichtbar borderBottomDuenn textlinkseingerueckt'>" + anzahlTage + " Tage </td>\n");
	}
	

}