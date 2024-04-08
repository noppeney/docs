package pze.ui.formulare.freigabecenter;

import pze.business.objects.personen.CoBuchung;
import pze.business.objects.reftables.buchungen.CoStatusGenehmigung;



/**
 * Formular für das Freigabecenter für Personal
 * 
 * @author Lisiecki
 *
 */
public class FormFreigabecenterPb extends AbstractFormFreigabecenter {
	
	protected static final String CAPTION = "Personalverwaltung";

	
	
	/**
	 * Konstruktor
	 * @param parent		visueller Parent
	 * @param formFreigabecenter 
	 * @throws Exception
	 */
	public FormFreigabecenterPb(Object parent, FormFreigabecenter formFreigabecenter) throws Exception {
		super(parent, formFreigabecenter, CoStatusGenehmigung.STATUSID_FREIGEGEBEN_PB, CAPTION);
	}


	@Override
	protected void loadAntraegeOfa(boolean zeitraum) throws Exception {
		m_coBuchung.loadAntraegePb(null, null, CoStatusGenehmigung.STATUSID_BEANTRAGT, 0, zeitraum, true, false);
	}


	@Override
	protected void loadAntraegeUrlaub(boolean zeitraum) throws Exception {
		m_coBuchungUrlaub.loadAntraegePb(null, null, CoStatusGenehmigung.STATUSID_BEANTRAGT, 0, zeitraum, false, false);
	}


	@Override
	protected void loadAntraegeDr(boolean zeitraum) throws Exception {
		m_coBuchungDr.loadAntraegePb(null, null, CoStatusGenehmigung.STATUSID_BEANTRAGT, 0, zeitraum, false, true);
	}

	
	@Override
	protected void loadAntraegeBearbeitet() throws Exception {
		m_coBuchungBearbeitet.loadAntraegePb(m_tfDatumVon.getField().getDateValue(), m_tfDatumBis.getField().getDateValue(), 
				CoStatusGenehmigung.STATUSID_GENEHMIGT, CoStatusGenehmigung.STATUSID_ABGELEHNT, false, false, false);
	}


	@Override
	protected void createFreigabe(CoBuchung coBuchung) throws Exception {
		if (coBuchung.equals(m_coBuchung))
		{
			m_coBuchung.createFreigabePb();
		}
		else
		{
			coBuchung.createFreigabeUrlaub(m_nextStatusGenehmigungID);
		}
	}


	public static String getKey(int id) {
		return "freigaben.pb." + id;
	}
	
	
	@Override
	public String getKey() {
		return getKey(getID());
	}
	


}
