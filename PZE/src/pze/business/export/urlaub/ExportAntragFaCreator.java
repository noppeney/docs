package pze.business.export.urlaub;

import java.util.Date;

import pze.business.Format;
import pze.business.objects.personen.CoKontowert;

/**
 * Formular zur Erstellung des FA-Antrags
 * 
 * @author lisiecki
 */
public class ExportAntragFaCreator extends ExportAntragCreator
{	
	private static final String TITEL = "Antrag auf Abbau von Zeitguthaben";
	

	@Override
	protected String getTitel() {
		return TITEL;
	}
	
	
	/**
	 * Keine Zusatzinfos
	 * @throws Exception 
	 */
	protected String getZusatzinfoPersonal() throws Exception {
		CoKontowert coKontowert;
		
		coKontowert = new CoKontowert();
		coKontowert.load(m_coPerson.getID(), Format.getDateVerschoben(new Date(), -1));
		
		return "Aktueller Saldo Arbeitszeitkonto: " + Format.getZeitAsText(coKontowert.getWertUeberstundenGesamt());
	}
	

}