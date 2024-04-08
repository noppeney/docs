package pze.ui.formulare.freigabecenter;

import framework.ui.interfaces.controls.IControl;
import framework.ui.interfaces.spread.ISpreadCell;
import pze.business.objects.personen.CoBuchung;
import pze.business.objects.reftables.CoMessageGruppe;
import pze.business.objects.reftables.buchungen.CoBuchungsart;
import pze.ui.controls.SortedTableControl;
import pze.ui.formulare.person.DialogBuchung;
import pze.ui.formulare.person.DialogDienstreise;

/**
 * Klasse für die Tabelle der aktuellen Freigaben im Freigabecenter
 * 
 * @author Lisiecki
 */
public class TableFreigaben extends SortedTableControl {

	private static final String COLOR_GRAU = "##DDDDDD";
	private static final String COLOR_ROT = "##FF6666";
	private static final String COLOR_ROT_GRAU = "##AF6666";

	private CoBuchung m_coBuchungen;

	private AbstractFormFreigabecenter m_formFreigabecenter;
	
	/**
	 * Genehmigungsstatus, der in der Tabelle vergeben werden soll
	 */
	private int m_nextStatusGenehmigungID;

	
	
	/**
	 * Konstruktor
	 * 
	 * @param tableControl
	 * @param coBuchung
	 * @param abstractFormFreigabecenter
	 * @param nextStatusGenehmigungID Genehmigungsstatus, der in der Tabelle vergeben werden soll
	 * @throws Exception
	 */
	public TableFreigaben(IControl tableControl, CoBuchung coBuchung, AbstractFormFreigabecenter abstractFormFreigabecenter, 
			int nextStatusGenehmigungID) throws Exception{
		super(tableControl);
		
		m_formFreigabecenter = abstractFormFreigabecenter;
		m_nextStatusGenehmigungID = nextStatusGenehmigungID;
		
		m_coBuchungen = coBuchung;
		setData(m_coBuchungen);
	}

	
	/**
	 * Hintergrund anpassen für die Zellen, die nicht bearbeitet werden dürfen
	 * @throws Exception 
	 * 
	 * @see pze.ui.controls.SortedTableControl#renderCell(framework.ui.interfaces.spread.ISpreadCell)
	 */
	@Override
	protected void renderCell(ISpreadCell cell) throws Exception {
		super.renderCell(cell);
		
		boolean isGeloescht;
		
		// keine Einträge in der Tabelle
		if (m_coBuchungen == null || m_coBuchungen.hasNoRows())
		{
			return;
		}
		
		// Header
		if (cell.getField() == null)
		{
			return;
		}
		
		// prüfen, ob die aktuelle Buchung freigegeben werden darf
		isGeloescht = m_coBuchungen.isStatusUngueltig();
		if (!m_coBuchungen.isFreigabeMoeglich(m_nextStatusGenehmigungID))
		{
			cell.setBackColor(isGeloescht ? COLOR_ROT_GRAU : COLOR_GRAU);
		}
		else if (isGeloescht)
		{
			cell.setBackColor(COLOR_ROT);
		}
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
		
		m_formFreigabecenter.refreshBtFreigabe();
	}
	
	
	/**
	 * Beim Doppelklick die Buchung öffnen
	 * 
	 * @see pze.ui.controls.SortedTableControl#tableDefaultSelected(framework.ui.interfaces.controls.IControl, java.lang.Object)
	 */
	public void tableDefaultSelected(IControl arg0, Object arg1) throws Exception{ 
		int buchungsartID;
		
		buchungsartID = m_coBuchungen.getBuchungsartID();
		
		// DR oder Buchung öffnen
		if (CoBuchungsart.isDrDg(buchungsartID))
		{
			// Dienstreise öffnen, als AL nur die Bemerkung ändern
			DialogDienstreise.showDialog(m_coBuchungen.getDienstreiseID(), CoMessageGruppe.ID_AL, 0);
//			m_formFreigabecenter.reloadTableData();
//			m_formFreigabecenter.clickedBuchungAendern(m_coBuchungen, this);
		}
		else
		{
			DialogBuchung.showDialogWithBuchung(m_coBuchungen.getID()); // TODO was ist am besten
		}
	}


}
