package pze.ui.formulare.messageboard;

import framework.ui.interfaces.controls.IButtonControl;
import framework.ui.interfaces.controls.IControl;
import framework.ui.interfaces.selection.ISelectionListener;
import pze.business.Messages;
import pze.ui.formulare.AbstractAktionCenterMainForm;
import pze.ui.formulare.projektverwaltung.FormAbruf;
import pze.ui.formulare.projektverwaltung.FormAuftrag;
import pze.ui.formulare.projektverwaltung.FormProjekt;

/**
 * Formular für das Messageboard der Projektverfolgung
 * 
 * @author Lisiecki
 *
 */
public class FormMessageboardProjektverfolgung extends AbstractFormMessageboardProjektverfolgung {
	
	private static final String CAPTION_MESSAGE = "Projektverfolgung";
	private static final String CAPTION_FREIGABE = "Projektcontrolling";

	protected IButtonControl m_btAenderungenUebernehmen;

	
	
	/**
	 * Konstruktor
	 * 
	 * @param parent		visueller Parent
	 * @param formMessageboard 
	 * @throws Exception
	 */
	public FormMessageboardProjektverfolgung(Object parent, AbstractAktionCenterMainForm formMessageboard, int messageGruppeID) throws Exception {
		super(parent, formMessageboard, formMessageboard instanceof FormMessageboard ? CAPTION_MESSAGE : CAPTION_FREIGABE, messageGruppeID);
		
		initBtAenderungenUebernehmen();
	}


	private void initBtAenderungenUebernehmen() {
		m_btAenderungenUebernehmen = (IButtonControl) findControl(getResID() + ".uebernehmen");
		if (m_btAenderungenUebernehmen != null)
		{
			m_btAenderungenUebernehmen.setSelectionListener(new ISelectionListener() {

				@Override
				public void selected(IControl control, Object params) {
					try 
					{
						int row, anzBookmarks;

						// aktuelle Zeile merken
						row = getSelectedRowMeldung();
						
						if (Messages.showYesNoMessage("Änderungen übernehmen", "Möchten Sie die Änderungen wirklich übernehmen?"))
						{
							// Anzahl ausgewählter Zeilen prüfen
							anzBookmarks = getAnzahlSelectedRowsMeldung();
							
							// wenn nur eine Zeile markiert ist, Quittierung erzeugen
							if (anzBookmarks == 1)
							{
								aenderungenUebernehmen();
							}

							// Daten neu laden
							refreshTableData();
							
							// Zeile wieder markieren
							setSelectedRowMeldung(row);
						}
					} 
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}

	
				@Override
				public void defaultSelected(IControl control, Object params) {
				}
			});
		}
	}
	

	@Override
	protected String getMeldungsTyp() {
		return "IstMessageProjektverfolgung";
	}
	

	public static String getKey(int id) {
		return "messageboard.mitarbeiter.projektverfolgung." + id;
	}
	
	
	@Override
	public String getKey() {
		return getKey(getID());
	}
	
	
	/**
	 * Änderungen aus der Projektverfolgung übernehmen
	 */
	private boolean aenderungenUebernehmen() throws Exception {
		FormProjekt formProjekt;

		formProjekt = null;
		
		// Projekt öffnen
		if (m_coMessageOffen.getAuftragID() > 0)
		{
			formProjekt = FormAuftrag.open(getSession(), m_coMessageOffen.getAuftragID());
		}
		else if (m_coMessageOffen.getAbrufID() > 0)
		{
			formProjekt = FormAbruf.open(getSession(), m_coMessageOffen.getAbrufID());
		}

		// Daten laden
		formProjekt.setDatenProjektverfolgung(m_coMessageOffen.getID());
		
		return true;
	}
	

	@Override
	public void refreshBtQuittierung() {
		if (m_btAenderungenUebernehmen == null)
		{
			return;
		}
		
		try 
		{
			// Buttons deaktivieren und bei Bedarf aktivieren
			m_btInfoQuittieren.refresh(reasonDisabled, null);
			m_btAenderungenUebernehmen.refresh(reasonDisabled, null);

			// keine Meldungen oder keine Meldung ausgewählt
//			if (m_coMessageOffen == null || m_coMessageOffen.getRowCount() == 0 
//					// im Messageboard können mehrere Verletzermeldungen gleichzeitig freigegeben werden, im Freigabecenter nicht
//					|| (getSelectedRowMeldung() < 0 && (getAnzahlSelectedRowsMeldung() == 0)))
			if (m_coMessageOffen == null || m_coMessageOffen.getRowCount() == 0 || getSelectedRowMeldung() < 0)
			{
				return;
			}

			// prüfen, ob die aktuelle Buchung freigegeben werden darf
//			if (m_coMessageOffen.isFreigabeMoeglich(m_nextStatusGenehmigungID))
			{
				m_btInfoQuittieren.refresh(reasonEnabled, null);
				m_btAenderungenUebernehmen.refresh(reasonEnabled, null);
			}
		} catch (Exception e) 
		{
			e.printStackTrace();
		}
	}



}
