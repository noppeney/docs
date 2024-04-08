package pze.ui.formulare.messageboard;

import pze.business.Messages;
import pze.business.objects.CoVerletzerliste;
import pze.business.objects.reftables.CoMeldungVerletzerliste;
import pze.business.objects.reftables.CoStatusVerletzung;
import pze.ui.formulare.AbstractAktionCenterMainForm;
import pze.ui.formulare.person.DialogVerletzermeldung;

/**
 * Formular für das Messageboard/Freigabecenter der Verletzermeldungen
 * 
 * @author Lisiecki
 *
 */
public class FormMessageboardVerletzermeldungen extends AbstractFormMessageboard {
	
	public static String RESID_MESSAGE = "form.meldungen.verletzermeldungen";
	public static String RESID_FREIGABE = "form.freigaben.verletzermeldungen";
	public static final String CAPTION = "Verletzermeldungen";

	
	
	/**
	 * Konstruktor
	 * 
	 * @param parent		visueller Parent
	 * @param formMessageboard 
	 * @param messageGruppeID
	 * @throws Exception
	 */
	public FormMessageboardVerletzermeldungen(Object parent, AbstractAktionCenterMainForm formMessageboard, int messageGruppeID) throws Exception {
		super(parent, formMessageboard, CAPTION, messageGruppeID, formMessageboard instanceof FormMessageboard ? RESID_MESSAGE : RESID_FREIGABE);
	}


	/**
	 * CO mit Verletzermeldungen initialisieren
	 * 
	 * @throws Exception
	 */
	@Override
	protected void initCo() throws Exception {
		m_coMessageOffen = new CoVerletzerliste();
		m_coMessageQuittiert = new CoVerletzerliste();
	}


	/**
	 * Button zur Quittierung refreshen
	 */
	@Override
	public void refreshBtQuittierung() {
		// offene Verletzermeldungen können nicht quittiert werden
		// freigegebene Verletzermeldungen werden ggf. von PB quittiert
		if (getResID().equals(RESID_MESSAGE) && m_coMessageOffen.getStatusID() == CoStatusVerletzung.STATUSID_OFFEN && getAnzahlSelectedRowsMeldung() == 1)
		{
			m_btInfoQuittieren.refresh(reasonDisabled, null);
		}
		else
		{
			super.refreshBtQuittierung();
		}
	}

	
	/**
	 * Bezeichnung der Quittierung (kann z. B. auch Freigabe sein)
	 * 
	 * @return
	 */
	@Override
	protected String getBezeichnugQuittierung() {
		return getResID().equals(RESID_FREIGABE) ?  "freigeben" : super.getBezeichnugQuittierung();
	}

	
	/**
	 * Ggf. muss vor der Quittierung eine Bemerkung eingetragen werden
	 */
	@Override
	protected boolean createQuittierung() throws Exception {
		CoVerletzerliste coVerletzerliste;

		coVerletzerliste = (CoVerletzerliste) m_coMessageOffen;
		
		// Freigaben im Freigabecenter
		if (getResID().equals(RESID_FREIGABE))
		{
			// wenn eine Meldung eingegeben werden muss
			if (CoMeldungVerletzerliste.getInstance().isBemerkungPflicht(coVerletzerliste.getMeldungID()))
			{
				Messages.showInfoMessage("Bemerkung erforderlich", "Bitte geben Sie im nächsten Dialogfenster einen Grund als Bemerkung ein.");
				int row = getSelectedRowMeldung();

				// wenn eine Bemerkung eingetragen wurde, kann die Quittierung erfolgen
				if (DialogVerletzermeldung.showDialog(coVerletzerliste))
				{
					// Zeile wieder markieren
					setSelectedRowMeldung(row);
					return super.createQuittierung();
				}
				else
				{
					return false;
				}
			}
			else // keine Bemerkung notwendig
			{
				return super.createQuittierung();
			}
		}
		else // Quittierung im Messageboard
		{
			if (m_coMessageOffen.getStatusID() == CoStatusVerletzung.STATUSID_OFFEN)
			{
				Messages.showErrorMessage("Es können nur freigegebene Meldungen quittiert werden. Fehler bei Meldung '" + m_coMessageOffen.getBeschreibung()
				+ "' für " + m_coMessageOffen.getPerson() + ".");
				return false;
			}
			
			return super.createQuittierung();
		}
	}
	
	
	public static String getKey(int id) {
		return "messageboard.verletzermeldungen." + id;
	}
	

	@Override
	public String getKey() {
		return getKey(getID());
	}
	

}
