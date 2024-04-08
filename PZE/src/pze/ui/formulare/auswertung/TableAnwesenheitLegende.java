package pze.ui.formulare.auswertung;

import framework.business.interfaces.CaptionType;
import framework.business.interfaces.tables.IColumnDescription;
import framework.business.interfaces.tables.IHeaderDescription;
import framework.ui.interfaces.controls.IControl;
import framework.ui.interfaces.spread.ISpreadCell;
import pze.business.objects.auswertung.CoAnwesenheitLegende;
import pze.business.objects.reftables.buchungen.CoBuchungsart;
import pze.ui.controls.SortedTableControl;


/**
 * Klasse für die Legende der Anwesenheitsübersicht
 * 
 * @author Lisiecki
 *
 */
public class TableAnwesenheitLegende extends SortedTableControl {

	private CoAnwesenheitLegende m_coAnwesenheitLegende;


	public TableAnwesenheitLegende(IControl tableControl) throws Exception {
		super(tableControl);
		
		m_coAnwesenheitLegende = new CoAnwesenheitLegende();

		setData(m_coAnwesenheitLegende);
		
		formatTable();
	}


	/**
	 * Tabelle formatieren.<br>
	 * Spaltenbreite, Alignment...
	 * 
	 */
	private void formatTable() {
		int iCol, anzCols;
		IHeaderDescription headerDescription;
		IColumnDescription columnDescription;
		
		
		headerDescription = getHeaderDescription();
		anzCols = getHeaderDescription().getColumnCount();
		
		// alle Spalten mit AlignCenter
		for (iCol=0; iCol<anzCols; ++iCol)
		{
			columnDescription = headerDescription.getColumnDescription(iCol);
			
			columnDescription.setAlignment(CaptionType.ALIGNCENTER);
			columnDescription.setWidth(140);
		}
		
		// Änderungen anwenden
		setHeaderDescription(headerDescription);
	}
	

	/**
	 * Farbe setzen
	 * 
	 * @see pze.ui.controls.SortedTableControl#renderCell(framework.ui.interfaces.spread.ISpreadCell)
	 */
	@Override
	protected void renderCell(ISpreadCell cell) throws Exception {
		super.renderCell(cell);
		
		if (isCellEmpty(cell))
		{
			return;
		}
		
		int row;
		String color;

		// aktuell markierte Zeile bestimmen
		row = m_coAnwesenheitLegende.getCurrentRowIndex();

		// Farben anhand der Buchungsart bestimmen
		color = m_coAnwesenheitLegende.getColor(row);
		if (color != null)
		{
			cell.setBackColor(color);
			
			// Aussehen der Zelle bei ortsflexiblem Arbeiten
			if (color.equals(CoBuchungsart.COLOR_ORTSFLEX_ARBEITEN))
			{
				TableAnwesenheit.renderCellOrtsflexibel(cell);
			}
		}
	}
	
	
	/**
	 * Sortieren für diese Tabelle nicht möglich
	 */
	@Override
	protected void sort() throws Exception {
	}


}
