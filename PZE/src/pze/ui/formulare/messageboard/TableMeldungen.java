package pze.ui.formulare.messageboard;

import framework.ui.interfaces.controls.IControl;
import pze.business.objects.AbstractCoMessage;
import pze.business.objects.CoMessage;
import pze.business.objects.CoVerletzerliste;
import pze.business.objects.projektverwaltung.CoProjekt;
import pze.business.objects.reftables.CoMessageQuittierung;
import pze.ui.controls.SortedTableControl;
import pze.ui.formulare.person.DialogDienstreise;
import pze.ui.formulare.person.DialogVerletzermeldung;
import pze.ui.formulare.person.FormPerson;
import pze.ui.formulare.projektverwaltung.FormAbruf;
import pze.ui.formulare.projektverwaltung.FormAuftrag;
import pze.ui.formulare.projektverwaltung.FormProjekt;

/**
 * Klasse für die Tabelle der aktuellen Freigaben im Messageboard
 * 
 * @author Lisiecki
 */
public class TableMeldungen extends SortedTableControl {

	private AbstractCoMessage m_coMessage;

	private AbstractFormMessageboard m_abstractFormMessageboard;

	
	
	/**
	 * Konstruktor
	 * 
	 * @param tableControl
	 * @param m_coMessageOffen
	 * @param abstractFormMessageboard
	 * @throws Exception
	 */
	public TableMeldungen(IControl tableControl, AbstractCoMessage m_coMessageOffen, AbstractFormMessageboard abstractFormMessageboard) throws Exception{
		super(tableControl);
		
		m_abstractFormMessageboard = abstractFormMessageboard;
		
		m_coMessage = m_coMessageOffen;
		setData(m_coMessage);
	}

	
	/**
	 * Buttons zur Freigabe aktualisieren
	 * 
	 * @throws Exception 
	 * @see pze.ui.controls.SortedTableControl#tableSelected(framework.ui.interfaces.controls.IControl, java.lang.Object)
	 */
	@Override
	public void tableSelected(IControl arg0, Object arg1) {
		super.tableSelected(arg0, arg1);
		
		m_abstractFormMessageboard.refreshBtQuittierung();
	}
	
	
	/**
	 * Beim Doppelklick entsprechenden Reiter öffnen
	 * 
	 * @see pze.ui.controls.SortedTableControl#tableDefaultSelected(framework.ui.interfaces.controls.IControl, java.lang.Object)
	 */
	public void tableDefaultSelected(IControl arg0, Object arg1) throws Exception{ 
		int meldungID;
		FormPerson formPerson;
		FormProjekt formProjekt;

		formProjekt = null;
		
		// ggf. Projekt öffnen
		if (m_coMessage.getAuftragID() > 0)
		{
			formProjekt = FormAuftrag.open(getSession(), m_coMessage.getAuftragID());
		}
		else if (m_coMessage.getAbrufID() > 0)
		{
			formProjekt = FormAbruf.open(getSession(), m_coMessage.getAbrufID());
		}
		else if (m_coMessage.getDienstreiseID() > 0)
		{
			DialogDienstreise.showDialog(m_coMessage.getDienstreiseID());
			return;
		}
		// Bemerkung bei Verletzermeldungen
		else if (m_coMessage instanceof CoVerletzerliste && m_table.getSelectedCell().getField().equals(m_coMessage.getFieldBemerkung()))
		{
			DialogVerletzermeldung.showDialog((CoVerletzerliste) m_coMessage);
			return;
		}
		else
		{
			// sonst Person öffnen
			formPerson = FormPerson.open(getSession(), null, (m_coMessage).getPersonID());
			if (formPerson != null)
			{
				formPerson.showZeiterfassung((m_coMessage).getDatum());
			}
			return;
		}
		
		// ggf. Projektverfolgung öffnen
		if (formProjekt != null && m_coMessage instanceof CoMessage)
		{
			// Art der Meldung prüfen
			meldungID = ((CoMessage)m_coMessage).getMeldungID();
			if (meldungID == CoMessageQuittierung.ID_PROJEKTVERFOLGUNG_PROGNOSE_PRUEFEN)
			{
				formProjekt.showFormProjektverfolgung(CoMessageQuittierung.ID_PROJEKTVERFOLGUNG_PROGNOSE_PRUEFEN);
			}
			else if (meldungID == CoMessageQuittierung.ID_PROJEKTVERFOLGUNG_PROGNOSE_ERSTELLEN)
			{
				 // falls der AL-Vertreter die Message öffnet -> Prüfung als AL
				formProjekt.showFormProjektverfolgung(m_coMessage.getPersonID() == ((CoProjekt) formProjekt.getCo()).getAbteilungsleiterID() ?
						CoMessageQuittierung.ID_PROJEKTVERFOLGUNG_PROGNOSE_PRUEFEN : CoMessageQuittierung.ID_PROJEKTVERFOLGUNG_PROGNOSE_ERSTELLEN);
			}
		}

	}
	

//	@Override
//	public boolean mayEdit(Object bookmark, ISpreadCell cell) throws Exception {
//		String resID;
//		
//		resID = cell.getColumnDescription().getResID();
//
//		// Auswahl darf gemacht werden, keine weiteren Änderungen
//		if (resID.contains("isausgewaehlt"))
//		{
//			return true;
//		}
//
//		return false;
//	}

}
