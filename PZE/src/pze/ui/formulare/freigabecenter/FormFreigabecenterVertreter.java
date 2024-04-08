package pze.ui.formulare.freigabecenter;

import pze.business.objects.personen.CoBuchung;
import pze.business.objects.personen.CoPerson;
import pze.business.objects.reftables.buchungen.CoStatusGenehmigung;
import pze.business.objects.reftables.personen.CoAbteilung;



/**
 * Formular für das Freigabecenter für Vertreter
 * 
 * @author Lisiecki
 *
 */
public class FormFreigabecenterVertreter extends AbstractFormFreigabecenter {
	
	protected static final String CAPTION = "Vertreter";

	
	
	/**
	 * Konstruktor
	 * @param parent		visueller Parent
	 * @param formFreigabecenter 
	 * @throws Exception
	 */
	public FormFreigabecenterVertreter(Object parent, FormFreigabecenter formFreigabecenter) throws Exception {
		super(parent, formFreigabecenter, CoStatusGenehmigung.STATUSID_FREIGEGEBEN_VERTRETER, CAPTION);
	}

	
	@Override
	protected void loadAntraegeOfa(boolean zeitraum) throws Exception {
		m_coBuchung.loadAntraegeVertreter(null, null, CoStatusGenehmigung.STATUSID_BEANTRAGT, CoStatusGenehmigung.STATUSID_FREIGEGEBEN_PB, 0, zeitraum, true, false);
	}

	
	@Override
	protected void loadAntraegeUrlaub(boolean zeitraum) throws Exception {
		m_coBuchungUrlaub.loadAntraegeVertreter(null, null, CoStatusGenehmigung.STATUSID_BEANTRAGT, CoStatusGenehmigung.STATUSID_FREIGEGEBEN_PB, 0, zeitraum, false, false);
	}

	
	@Override
	protected void loadAntraegeDr(boolean zeitraum) throws Exception {
		m_coBuchungUrlaub.loadAntraegeVertreter(null, null, CoStatusGenehmigung.STATUSID_BEANTRAGT, CoStatusGenehmigung.STATUSID_FREIGEGEBEN_PB, 0, zeitraum, false, true);
	}

	
	@Override
	protected void loadAntraegeBearbeitet() throws Exception {
		m_coBuchungBearbeitet.loadAntraegeVertreter(m_tfDatumVon.getField().getDateValue(), m_tfDatumBis.getField().getDateValue(), 
				CoStatusGenehmigung.STATUSID_GENEHMIGT, CoStatusGenehmigung.STATUSID_ABGELEHNT, 0, false, false, false);
	}


	@Override
	protected void createFreigabe(CoBuchung coBuchung) throws Exception {
		if (coBuchung.equals(m_coBuchung))
		{
			m_coBuchung.createFreigabeVertreter();
		}
		else // Urlaub freigeben
		{
			coBuchung.createFreigabeUrlaub(m_nextStatusGenehmigungID);

			// Urlaub von Geschäftsführern muss nur vom vertreter freigegeben werden
			CoPerson coPerson;
			coPerson = CoPerson.getInstance();
			coPerson.moveToID(coBuchung.getPersonID());
			
			if (coPerson.getAbteilungID() == CoAbteilung.ID_GESCHAEFTSFUEHRUNG)
			{
				coBuchung.createFreigabeUrlaub(CoStatusGenehmigung.STATUSID_GENEHMIGT);
			}
		}
	}


	public static String getKey(int id) {
		return "freigaben.vertreter." + id;
	}
	
	
	@Override
	public String getKey() {
		return getKey(getID());
	}
	


}
