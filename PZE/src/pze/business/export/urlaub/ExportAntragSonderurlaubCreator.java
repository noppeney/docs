package pze.business.export.urlaub;

import pze.business.objects.reftables.CoGrundSonderurlaub;
import pze.ui.formulare.person.DialogGrundSonderurlaub;

/**
 * Formular zur Erstellung des Antrags auf Sonderurlaub
 * 
 * @author lisiecki
 */
public class ExportAntragSonderurlaubCreator extends ExportAntragCreator
{	
	private static final String TITEL = "Antrag auf Sonderurlaub";
	

	@Override
	protected String getTitel() {
		return TITEL;
	}
	
	
	/**
	 * Grund Zusatzinfos
	 * @throws Exception 
	 */
	protected String getZusatzinfoPersonal() throws Exception {
		return "Grund: " + CoGrundSonderurlaub.getInstance().getBezeichnung(DialogGrundSonderurlaub.getGrundID());
	}
	

}