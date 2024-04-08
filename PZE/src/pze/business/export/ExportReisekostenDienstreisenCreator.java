package pze.business.export;

import java.util.GregorianCalendar;

import framework.Application;
import pze.business.Format;
import pze.business.objects.dienstreisen.CoDienstreise;
import pze.business.objects.dienstreisen.CoDienstreiseAbrechnung;
import pze.ui.formulare.UniFormWithSaveLogic;
import pze.ui.formulare.person.monatseinsatzblatt.FormPersonMonatseinsatzblatt;


/**
 * Klasse zum Erstellen einer HTML-Datei für den PDF-Export des Monatseinsatzblattes
 * 
 * @author Lisiecki/Noppeney
 */
public class ExportReisekostenDienstreisenCreator extends ExportPdfCreator{
	
	private FormPersonMonatseinsatzblatt m_formPersonMonatseinsatzblatt;

	
	/**
	 * Gibt eine Monatseinsatzblatt-Tabelle in XHTML zurück.
	 * 
	 * @param formPersonMonatseinsatzblatt
	 * @return
	 * @throws Exception 
	 */
	@Override
	public String createHtml(UniFormWithSaveLogic formPersonMonatseinsatzblatt) throws Exception {
		m_formPersonMonatseinsatzblatt = (FormPersonMonatseinsatzblatt) formPersonMonatseinsatzblatt;

		m_sb = new StringBuilder();
		
		writeHtmlOpen();
		
		writeFooter();
		
		writeSeite();

		writeHtmlClose();

		return m_sb.toString();
	}
	
	
	@Override
	protected boolean isQuerformat() {
		return true;
	}


	private boolean writeSeite() throws Exception {
		m_sb.append("<div class='page'>\n");
		
		m_sb.append("<div class='floatleft' style='width: 100%; margin-top: 20px;'>\n");
		
		appendUeberschrift();
		appendTabelleKopf();
		appendTabelleInhalt();
		appendLegendeMahlzeiten();

		m_sb.append("</div>\n"); // floatleft
		m_sb.append("</div>\n"); // page

		return true;
	}
	
	
	/**
	 * Titel, Name und Monat
	 */
	private void appendUeberschrift() {
		m_sb.append("        <table class='unsichtbar' style='width: 1025px; margin-left: -29px;'>\n");
		m_sb.append("			<tr>\n");

		m_sb.append("				<td class='unsichtbar textlinks'>  <h2> Reisekostenabrechnung - Inland </h2> </td>\n");
		
		m_sb.append("				<td class='unsichtbar'> Monat: " + m_formPersonMonatseinsatzblatt.getCurrentMonat() + "</td>\n");
		
//		m_sb.append("				<td class='unsichtbar'> Jahr: " + "2021" + "</td>\n");
		
		m_sb.append("				<td class='unsichtbar'> Name: " + m_formPersonMonatseinsatzblatt.getCoPerson().getBezeichnung() + "</td>\n");
		
		m_sb.append("				<td class='unsichtbar'> <img src='/" + Application.getWorkingDirectory().replace("\\", "/") + "WTI_Logo_2023_Schwarz.png' style='width: 50px; margin-left: 80px; margin-right: -30px;'></img> </td>\n");

		m_sb.append("			</tr>\n");
		m_sb.append("        </table>\n");
	}
	

	/**
	 * Überschriften der Spalten
	 */
	private void appendTabelleKopf()
	{
		m_sb.append("        <table class='duennerRahmen' style='font-size: 10px; width: 1025px; margin-left: -29px;'>\n");
		m_sb.append("			<tr style='height: 20px;'>\n");

		m_sb.append("				<td rowspan='2'> Tag </td>\n");
		
		m_sb.append("				<td colspan='2'> Reise </td>\n");
		
		m_sb.append("				<td rowspan='2'> Über- <br /> nachtung </td>\n");
		
		m_sb.append("				<td colspan='3'> Ziel </td>\n");
		
		m_sb.append("				<td colspan='4'> Beförderungsmittel </td>\n");
		
		m_sb.append("				<td colspan='3'> Pauschalbeträge </td>\n");
		
		m_sb.append("				<td colspan='3'> Einzelkosten gem. Nachweis </td>\n");
		
		m_sb.append("				<td rowspan='2'> Gesamt- <br /> kosten <br /> € </td>\n");
		
		m_sb.append("				<td rowspan='2'> Kunde <br /> Kostenstelle-Nr./ <br /> Abruf-Nr. </td>\n");
		
		m_sb.append("				<td rowspan='2'> WTI <br /> Auftragsnummer </td>\n");

		m_sb.append("			</tr>\n");
		m_sb.append("			<tr style='height: 40px;'>\n");
		
		m_sb.append("				<td> Antritt </td>\n");
		
		m_sb.append("				<td> Ende </td>\n");
		
		m_sb.append("				<td> Ort </td>\n");
		
		m_sb.append("				<td> Firma </td>\n");
		
		m_sb.append("				<td> erhaltene <br /> Mahlzeiten </td>\n");
		
		m_sb.append("				<td> Beförderungs- <br /> mittel </td>\n");
		
		m_sb.append("				<td> Gesamt <br /> km </td>\n");
		
		m_sb.append("				<td> Abzug Fahrten <br /> Wohnung-WTI/ <br /> WTI-Wohnung </td>\n");
		
		m_sb.append("				<td> Kfz-km </td>\n");
		
		m_sb.append("				<td> km-Geld <br /> (gesamt) <br /> € </td>\n");
		
		m_sb.append("				<td> Verpflegung <br /> € </td>\n");
		
		m_sb.append("				<td> Über- <br /> nachtung <br /> € </td>\n");
		
		m_sb.append("				<td> Flug, Bahn <br /> usw. <br /> € </td>\n");
		
		m_sb.append("				<td> Über- <br /> nachtung <br /> € </td>\n");
		
		m_sb.append("				<td> Sonstige <br /> Kosten <br /> € </td>\n");
		
		m_sb.append("			</tr>\n");
	}
	
	
	/**
	 * Inhalt der Abrechnung
	 * @throws Exception 
	 */
	private void appendTabelleInhalt() throws Exception{
		double betrag, summeKm, summeVerpflegung, summeUebernachtung, summeKostenFahrt, summeKostenNacht, summeKostenSonstiges, summe, gesamtsumme;
		CoDienstreise coDienstreise;
		CoDienstreiseAbrechnung coDienstreiseAbrechnung;
		
		summeKm = 0;
		summeVerpflegung = 0;
		summeUebernachtung = 0;
		summeKostenFahrt = 0;
		summeKostenNacht = 0;
		summeKostenSonstiges = 0;
		gesamtsumme = 0;

		
		coDienstreise = new CoDienstreise();
		coDienstreiseAbrechnung = new CoDienstreiseAbrechnung();
		coDienstreiseAbrechnung.loadByPersonID(m_formPersonMonatseinsatzblatt.getCoPerson().getID(), m_formPersonMonatseinsatzblatt.getCurrentDatum());

		if (coDienstreiseAbrechnung.moveFirst())
		{
			do
			{
				summe = 0;
				coDienstreise.loadByID(coDienstreiseAbrechnung.getBuchungID()); // TODO prüfen welche ID hier benötigt wird
				m_sb.append("		<tr style='height: 15px;'>\n");

				// Tag & Uhrzeit
				m_sb.append("			<td>&nbsp;&nbsp;" + coDienstreiseAbrechnung.getGregDatum().get(GregorianCalendar.DAY_OF_MONTH) + "&nbsp;&nbsp;</td>\n");			

				// Beginn, Ende, Übernachtung
				m_sb.append("			<td>" + Format.getZeitAsText(coDienstreiseAbrechnung.getBeginnDr()) + "</td> \n");
				m_sb.append("			<td>" + Format.getZeitAsText(coDienstreiseAbrechnung.getEndeDr()) + "</td> \n");
				m_sb.append("			<td>" + (coDienstreise.isUebernachtung() ? "ja" : "Nein") + "</td>\n");
				
				// Ort & Kunden
//				m_sb.append("			<td>" + coDienstreise.getOrt() + "</td>\n");
//				m_sb.append("			<td>" + coDienstreise.getKunde() + "</td>\n");
				
				// Mahlzeiten, Beförderungsmittel
				m_sb.append("			<td>" + coDienstreiseAbrechnung.getErhalteneMahlzeiten() + "</td>\n");
				m_sb.append("			<td>" + coDienstreise.getBefoerderungsmittel() + "</td>\n");
				m_sb.append("			<td>" + coDienstreiseAbrechnung.getKm() + "</td>\n");
				m_sb.append("			<td>" + coDienstreiseAbrechnung.getKmAbzugWohnortWti() + "</td>\n");
				m_sb.append("			<td>" + coDienstreiseAbrechnung.getKmNachAbzugWohnortWti() + "</td>\n");
				
				// Pauschalbeträge
				betrag = coDienstreiseAbrechnung.getKmPauschale();
				summeKm += betrag;
				summe += betrag;
				m_sb.append("			<td>" + Format.getFormat2NksKomma(betrag) + "</td>\n");
				
				betrag = coDienstreiseAbrechnung.getVerpflegungspauschale();
				summeVerpflegung += betrag;
				summe += betrag;
				m_sb.append("			<td>" + Format.getFormat2NksKomma(betrag) + "</td>\n");
				
				betrag = coDienstreiseAbrechnung.getUebernachtungspauschale();
				summeUebernachtung += betrag;
				summe += betrag;
				m_sb.append("			<td>" + Format.getFormat2NksKomma(betrag) + "</td>\n");
				
				// Einzelkosten
				betrag = coDienstreiseAbrechnung.getKostenFahrt();
				summeKostenFahrt += betrag;
				summe += betrag;
				m_sb.append("			<td>" + Format.getFormat2NksKomma(betrag) + "</td>\n");
				
				betrag = coDienstreiseAbrechnung.getKostenNacht();
				summeKostenNacht += betrag;
				summe += betrag;
				m_sb.append("			<td>" + Format.getFormat2NksKomma(betrag) + "</td>\n");
				
				betrag = coDienstreiseAbrechnung.getKostenSonstiges();
				summeKostenSonstiges += betrag;
				summe += betrag;
				m_sb.append("			<td>" + Format.getFormat2NksKomma(betrag) + "</td>\n");
				
				// Gesamtkosten
				gesamtsumme += summe;
				m_sb.append("			<td>" + Format.getFormat2NksKomma(summe) + "</td>\n");
				
				// Kunde, Projekt
//				m_sb.append("			<td>" + coDienstreise.getKunde() + ", " + coDienstreise.getAbrufNr() + ", " + coDienstreise.getKostenstelle() + "</td>\n");
				m_sb.append("			<td>" + coDienstreise.getAuftragsNr() + "</td>\n");

				m_sb.append("		</tr>\n");
			} while (coDienstreiseAbrechnung.moveNext());
		}

		m_sb.append("		<tr class='unsichtbar' style='height: 15px;'>\n");
		
		m_sb.append("			<td colspan='10'> </td>\n");			
		m_sb.append("			<td>" + "Summe" + "</td> \n");
		m_sb.append("			<td>" + Format.getFormat2NksKomma(summeKm) + "</td>\n");
		m_sb.append("			<td>" + Format.getFormat2NksKomma(summeVerpflegung) + "</td>\n");
		m_sb.append("			<td>" + Format.getFormat2NksKomma(summeUebernachtung) + "</td>\n");
		m_sb.append("			<td>" + Format.getFormat2NksKomma(summeKostenFahrt) + "</td>\n");
		m_sb.append("			<td>" + Format.getFormat2NksKomma(summeKostenNacht) + "</td>\n");
		m_sb.append("			<td>" + Format.getFormat2NksKomma(summeKostenSonstiges) + "</td>\n");
		m_sb.append("			<td>" + Format.getFormat2NksKomma(gesamtsumme) + "</td>\n");
		m_sb.append("			<td colspan='2'> </td>\n");	
		m_sb.append("		</tr>\n");
		
		m_sb.append("        </table>\n");

	
	
//		for(int i = 1; i<=10; i++)
//		{
//			m_sb.append("		<tr style='height: 15px;'>\n");
//			
//			m_sb.append("			<td>&nbsp;&nbsp;" + i + "&nbsp;&nbsp;</td>\n");			
//			
//			m_sb.append("			<td>" + "11:23" + "</td> \n");
//			m_sb.append("			<td>" + "02:56" + "</td>\n");
//			m_sb.append("			<td>" + "Nein" + "</td>\n");
//			m_sb.append("			<td>" + "Jülich an der Ruhr" + "</td>\n");
//			m_sb.append("			<td>" + "Forschungszentrum" + "</td>\n");
//			m_sb.append("			<td>" + "2" + "</td>\n");
//			m_sb.append("			<td>" + "Flugzeug" + "</td>\n");
//			m_sb.append("			<td>" + "2034" + "</td>\n");
//			m_sb.append("			<td>" + "1032" + "</td>\n");
//			m_sb.append("			<td>" + "2463" + "</td>\n");
//			m_sb.append("			<td>" + "2356.24" + "</td>\n");
//			m_sb.append("			<td>" + "2454.34" + "</td>\n");
//			m_sb.append("			<td>" + "4235.26" + "</td>\n");
//			m_sb.append("			<td>" + "7123.99" + "</td>\n");
//			m_sb.append("			<td>" + "2931.32" + "</td>\n");
//			m_sb.append("			<td>" + "1233.33" + "</td>\n");
//			m_sb.append("			<td>" + "1932.38" + "</td>\n");
//			m_sb.append("			<td>" + "GNS 13442244" + "</td>\n");
//			m_sb.append("			<td>" + "w12345" + "</td>\n");
//			
//			m_sb.append("		</tr>\n");
//		}
//		
//		m_sb.append("		<tr class='unsichtbar' style='height: 15px;'>\n");
//		
//		m_sb.append("			<td colspan='10'> </td>\n");			
//		m_sb.append("			<td>" + "Summe" + "</td> \n");
//		m_sb.append("			<td> </td>\n");
//		m_sb.append("			<td>" + "84,00" + "</td>\n");
//		m_sb.append("			<td> </td>\n");
//		m_sb.append("			<td>" + "0,00" + "</td>\n");
//		m_sb.append("			<td>" + "0,00" + "</td>\n");
//		m_sb.append("			<td>" + "0,00" + "</td>\n");
//		m_sb.append("			<td>" + "84,00" + "</td>\n");
//		m_sb.append("			<td colspan='2'> </td>\n");	
//		m_sb.append("		</tr>\n");
//		
//		m_sb.append("        </table>\n");
	
	}

	
	/**
	 * Legende unter der Tabelle
	 */
	private void appendLegendeMahlzeiten() {
		m_sb.append("<br/> <br/> \n");
		
		m_sb.append("	<div style='font-size: 8px;'>\n");
		m_sb.append("		erhaltene Mahlzeiten: <br/> \n");
		m_sb.append("		&nbsp; &nbsp; F = Frühstück <br/> \n");
		m_sb.append("		&nbsp; &nbsp; M = Mittagessen <br/> \n");
		m_sb.append("		&nbsp; &nbsp; A = Abendessen <br/> \n");
		m_sb.append("	</div>\n");
	}
	
	
	@Override
	protected String getStand(){
		return "03/2023";
	}

}

