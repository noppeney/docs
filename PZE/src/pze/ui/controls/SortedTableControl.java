package pze.ui.controls;

import org.eclipse.swt.widgets.Display;

import framework.business.cacheobject.CacheObject;
import framework.business.fields.FieldDescription;
import framework.business.fields.HeaderDescription;
import framework.business.interfaces.FieldType;
import framework.business.interfaces.data.IBusinessObject;
import framework.business.interfaces.fields.IField;
import framework.business.interfaces.fields.IFieldDescription;
import framework.business.interfaces.nodes.INode;
import framework.business.interfaces.session.ISession;
import framework.business.interfaces.tables.IColumnDescription;
import framework.business.interfaces.tables.IHeaderDescription;
import framework.ui.interfaces.controls.IControl;
import framework.ui.interfaces.controls.IMenu;
import framework.ui.interfaces.controls.ITableControl;
import framework.ui.interfaces.keys.IKeyListener;
import framework.ui.interfaces.mouse.IMouseListener;
import framework.ui.interfaces.mouse.MouseData;
import framework.ui.interfaces.selection.IFocusListener;
import framework.ui.interfaces.selection.ISelectionListener;
import framework.ui.interfaces.spread.ICellEditListener;
import framework.ui.interfaces.spread.ICellRenderListener;
import framework.ui.interfaces.spread.ISpreadCell;
import pze.business.Format;

/**
 * TableControl, das das Sortieren der Daten ermöglicht
 * 
 * @author Lisiecki
 *
 */
public class SortedTableControl implements ITableControl{

	protected ITableControl m_table;

	private String m_lastResID;
	private boolean m_sortAsc;
	private boolean m_sortEnabled;
	private ISpreadCell m_cell;
	
	private int m_yKoordinate;



	/**
	 * Konstruktor
	 * 
	 * @param tableControl TableControl, das sortiert werden soll
	 * @throws Exception
	 */
	public SortedTableControl(IControl tableControl) throws Exception {

		m_table = (ITableControl) tableControl;
				
		m_sortAsc = false;
		m_lastResID = null;

		// Selection-Listener zum Sortieren
		setSelectionListener();
		
		// Focus-Listener beim aktivieren 
//		gibt es bisher nur für 2 Tabellen, daher noch nicht freigeschaltet 
//		setFocusListener();
		
		// Edit-Listener zum Formatieren der Uhrzeiten
		setEditListener();
		
		// Mouse-Listener, um zu prüfen wo geklickt wurde
		setMouseListener();
		
		// Listener zum Anzeigen der Sortier-Icons
		setCellRenderListener();
		
		m_sortEnabled = true;
		
//		disableStatus();
	}


	/**
	 * Status-Feld deaktivieren, wenn es kein Admin ist
	 * @throws Exception 
	 */
//	public void disableStatus() throws Exception {
//
//		if (UserInformation.getInstance().isAdmin() || UserInformation.getInstance().isEntwickler())
//		{
//			return;
//		}
//		
//		HeaderDescription hdesc = (HeaderDescription) m_table.getHeaderDescription();
//		enableColumn(hdesc.getColumnCount()-1, false);
//	}


	/**
	 * alle Felder aktivieren
	 * 
	 */
	public void enable(boolean enable) {
		int iCol, anzCol;
		
		HeaderDescription hdesc = (HeaderDescription) m_table.getHeaderDescription();

		anzCol = hdesc.getColumnCount();
		for (iCol=0; iCol<anzCol; ++iCol)
		{
			enableColumn(iCol, enable);
		}
	}


	/**
	 * Feld aktivieren
	 * 
	 */
	public void enableColumn(String resID, boolean enable) {
		int iCol, anzCol;
		
		HeaderDescription hdesc = (HeaderDescription) m_table.getHeaderDescription();

		anzCol = hdesc.getColumnCount();
		for (iCol=0; iCol<anzCol; ++iCol)
		{
			if (hdesc.getColumnDescription(iCol).getResID().equals(resID))
			{
				enableColumn(iCol, enable);
			}
		}
	}


	/**
	 * Feld aktivieren
	 * 
	 */
	public void enableColumn(int col, boolean enable) {

		HeaderDescription hdesc = (HeaderDescription) m_table.getHeaderDescription();
		IColumnDescription clm = hdesc.getColumnDescription(col);
		FieldDescription fdesc = enableField((FieldDescription) clm.getFieldDescription(), enable);
		clm.setFieldDescription(fdesc);
		m_table.setHeaderDescription(hdesc);
	}


	/**
	 * Feld aktivieren
	 * 
	 */
	public static FieldDescription enableField(FieldDescription fdesc, boolean enable) {
		fdesc.setEnabled(enable);
		return fdesc;
	}


	/**
	 * SelectionListener hinzufügen, der das Sortieren ermöglicht
	 */
	private void setSelectionListener() {
		
		setSelectionListener(new ISelectionListener() 
		{
			/**
			 * Sortieren anstoßen
			 * 
			 * @see framework.ui.interfaces.selection.ISelectionListener#selected(framework.ui.interfaces.controls.IControl, java.lang.Object)
			 */
			@Override
			public void selected(IControl arg0, Object arg1) {
				tableSelected(arg0, arg1);
			}

			@Override
			public void defaultSelected(IControl arg0, Object arg1) {
				try 
				{
					if (arg1 != null)
					{
						tableDefaultSelected(arg0, arg1);
					}
				} 
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}
	

	/**
	 * SelectionListener hinzufügen, der das Sortieren ermöglicht
	 */
	private void setEditListener() {
		
		setEditListener(new ICellEditListener() {
			
			@Override
			public void endEdit(Object bookmark, IField fld) {
				try 
				{
					endEditing(bookmark, fld);
				} 
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}

			@Override
			public void dataChanged(Object bookmark, IField fld) {
				try 
				{
					tableDataChanged(bookmark, fld);
				} 
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			
			@Override
			public boolean beginEdit(Object bookmark, ISpreadCell cell) {
				try 
				{
					return beginEditing(bookmark, cell);
				} 
				catch (Exception e)
				{
					e.printStackTrace();
					return false;
				}
			}

		});
	}
	

	/**
	 * SelectionListener hinzufügen, der das Sortieren ermöglicht
	 */
//	private void setFocusListener() {
//		
//		setFocusListener(new IFocusListener() {
//			
//			@Override
//			public void focusLost(IControl control) {
//				try 
//				{
//					onFocusLost(control);
//				} 
//				catch (Exception e)
//				{
//					e.printStackTrace();
//				}
//			}
//			
//			
//			@Override
//			public void focusGained(IControl control) {
//				try 
//				{
//					onFocusGained(control);
//				} 
//				catch (Exception e)
//				{
//					e.printStackTrace();
//				}
//			}
//		});
//	}
	

	/**
	 * MouseListener hinzufügen, der das Sortieren nur im Header zu ermöglicht
	 */
	private void setMouseListener() {
		
		setMouseListener(new IMouseListener() {
			
			@Override
			public void onMouseUp(IControl control, MouseData md) {
				m_yKoordinate = md.y;
			}
			
			@Override
			public void onMouseMove(IControl control, MouseData md) {
				
			}
			
			@Override
			public void onMouseHover(IControl control, MouseData md) {
				
			}
			
			@Override
			public void onMouseExit(IControl control, MouseData md) {
				
			}
			
			@Override
			public void onMouseEnter(IControl control, MouseData md) {
				
			}
			
			@Override
			public void onMouseDown(IControl control, MouseData md) {
				
			}
			
			@Override
			public void onMouseDoubleClick(IControl control, MouseData md) {
				
			}
			
			@Override
			public boolean isMouseMoveEnabled() {
				return false;
			}
		});
	}
	
	
	/**
	 * Fielddescription auf Text ändern, um den Editor für Date-Zellen anzupassen
	 * 
	 * @param cell
	 */
	protected void setDateFieldescriptionToText(ISpreadCell cell) {
		HeaderDescription hdesc = (HeaderDescription) m_table.getHeaderDescription();
		IColumnDescription clm = cell.getColumnDescription();
		FieldDescription fdesc = (FieldDescription) clm.getFieldDescription();
		fdesc.setFieldType(FieldType.TEXT);
		fdesc.setDataFormat("DATE"); // um in isDateField Date-Textfelder von den normalen zu unterscheiden
		clm.setFieldDescription(fdesc);
		m_table.setHeaderDescription(hdesc);
		
		// Zelle speichern, um Fielddescription wieder zurückzusetzen
		m_cell = cell;
	}


	/**
	 * Fielddescription zurück auf Date ändern, nachdem die Zelle bearbeitet wurde
	 * 
	 * @param cell
	 */
	protected void setDateFieldescriptionToDate() {
		HeaderDescription hdesc = (HeaderDescription) m_table.getHeaderDescription();
		IColumnDescription clm = m_cell.getColumnDescription();
		FieldDescription fdesc = (FieldDescription) clm.getFieldDescription();
		fdesc.setFieldType(FieldType.DATE);
		fdesc.setDataFormat("dd.MM.yyyy");
		clm.setFieldDescription(fdesc);
		m_table.setHeaderDescription(hdesc);
	} 


	/**
	 * Render-Listener zum Anzeigen der Sortier-Icons
	 */
	private void setCellRenderListener() {
		
		setCellRenderListener(new ICellRenderListener() 
		{
			/**
			 * Icons anzeigen
			 * 
			 * @see framework.ui.interfaces.spread.ICellRenderListener#onFormatCell(framework.ui.interfaces.spread.ISpreadCell)
			 */
			public void onFormatCell(ISpreadCell cell) {
				try
				{
					renderCell(cell);
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				}
			}
		});
	}


	/**
	 * Celle rendern (wird vom CellRenderListener aufgerufen)
	 * 
	 * @param cell
	 * @throws Exception 
	 */
	protected void renderCell(ISpreadCell cell) throws Exception {
		renderSortBitmap(cell);
		renderZeit(cell);
		// das rendern muss gemacht werden, weil sonst der Wert in den gerade nicht bearbeiteten Zellen nicht richtig angezeigt wird
		renderDate(cell);
	}

	
	/**
	 * Bitmap für die Sortierung anpassen
	 * 
	 * @param cell
	 */
	private void renderSortBitmap(ISpreadCell cell) {
		String resid = cell.getColumnDescription().getResID();                
		if (cell.getField() == null && m_lastResID != null && m_lastResID.equals(resid))
		{
			if(m_sortAsc)
			{
				cell.setBitmapID("misc.sort.up");
			}
			else
			{
				cell.setBitmapID("misc.sort.down");
			}

			return;
		}
	}


	/**
	 * Zeitangaben in Minuten müssen im Format h:mm dargestellt werden
	 * 
	 * @param cell
	 */
	private void renderZeit(ISpreadCell cell) {
		Object value;
		int minuten;

		// Format der Zelle prüfen
		if (isZeitField(cell.getField()))
		{
			value = cell.getField().getValue();
			if (value == null)
			{
				return;
			}
			
			minuten = Format.getIntValue(value);
			cell.setText(Format.getZeitAsText(minuten));
		}
	}


	/**
	 * Datum wird im Format dd.mm.yyyy dargestellt
	 * 
	 * @param cell
	 */
	private void renderDate(ISpreadCell cell) {
		String stringValue;

		// Format der Zelle prüfen
		if (isDateField(cell.getField()))
		{
			stringValue = Format.getStringValue(cell.getField());
			if (stringValue == null)
			{
				return;
			}

			cell.setText(stringValue);
		}
	}


	/**
	 * Prüfen, ob es sich um eine Zelle mit Uhrzeit-Format handelt
	 * 
	 * @param field
	 * @return
	 */
	public boolean isZeitField(IField field) {
		IFieldDescription fieldDescription;

		if (field == null)
		{
			return false;
		}

		// FieldDescription prüfen
		fieldDescription = field.getFieldDescription();

		return fieldDescription.getFieldType() == FieldType.TEXT && fieldDescription.getDataFormat().equals("0:00");
	}



	/**
	 * Prüfen, ob es sich um eine Zelle mit einem Datum handelt
	 * 
	 * @param field
	 * @return
	 */
	protected boolean isDateField(IField field) {
		IFieldDescription fieldDescription;

		if (field == null)
		{
			return false;
		}

		// FieldDescription prüfen
		fieldDescription = field.getFieldDescription();

		return fieldDescription.getFieldType() == FieldType.DATE || fieldDescription.getDataFormat() == "DATE";
	}


	/**
	 * Prüfen, ob es sich um eine Zelle mit Uhrzeit-Format handelt
	 * 
	 * @param field
	 * @return
	 */
	public boolean isProzentField(IField field) {
		IFieldDescription fieldDescription;

		if (field == null)
		{
			return false;
		}

		// FieldDescription prüfen
		fieldDescription = field.getFieldDescription();

		return fieldDescription.getFieldType() == FieldType.DOUBLE && fieldDescription.getDataFormat().equals("#0%");
	}


	/**
	 * Funktion wird aufgerufen, wenn ein Tabelleneintrag selektiert wird. So wird die Sortierfunktion angestoßen.
	 * 
	 * @param arg0
	 * @param arg1
	 */
	public void tableSelected(IControl arg0, Object arg1){
		try 
		{
			// wenn ein Eintrag ausgewählt wurde, springe im Cacheobjekt zu diesem Feld
			if (arg1 != null)
			{
				getData().moveTo(getSelectedBookmark());
			}
			// bei Auswahl in der Header-Zeile sortieren
			else if (m_yKoordinate < 50 && m_sortEnabled)
			{
				sort();            
			}
		}
		catch (Exception e) 
		{
		}	
	}


	/**
	 * Funktion wird beim Doppelklick auf ein Tabelleneintrag aufgerufen.<br>
	 * Defaultmäßig keine Aktion, wird z. B. zum Öffnen eines Formulars überschrieben
	 * 
	 * @param arg0
	 * @param arg1
	 */
	public void tableDefaultSelected(IControl arg0, Object arg1) throws Exception{
		
	}


	/**
	 * Wird beim Beenden der Eingabe für eine Zelle aufgerufen, wenn der Editor geändert wurde
	 * 
	 * @param bookmark
	 * @param fld
	 */
	protected void endEditing(Object bookmark, IField fld) throws Exception {
		String text;
		//				System.out.println(m_table.getSelectedCell().getColumnDescription().getFieldDescription().Text());
		//				System.out.println(bookmark.toString());
		//				System.out.println("ende: " + fld.getDisplayValue() + " - " + fld.getValue() + " - " + fld.getOriginalValue() + " - " );
		//				System.out.println(m_cell.getText());
		if (isZeitField(fld))
		{
			Integer zeit;
			String zeitString;

			// Zeit bestimmen
			zeitString = fld.getDisplayValue();
			if (zeitString.isEmpty()) // keine Zeit eingetragen bzw. Zeit gelöscht
			{
				fld.setValue(null);
				m_table.getSelectedCell().setText(null);
				return;
			}
			zeit = Format.getZeitAsInt(zeitString);

			//			fld.setValue(Format.getZeitAsText(zeit));
			fld.setValue(zeit);
			m_table.getSelectedCell().setText(Format.getZeitAsText(zeit));
		}
		else if (isDateField(fld)) // das Datum kann in verschiedenen Formaten eingegeben werden
		{
			// Datum formatieren
			text = fld.getDisplayValue();
			if (!text.isEmpty())
			{
				text = DateControlPze.formatDatum(fld.getDisplayValue()); // falls keine Punkte eingetragen wurden
				text = Format.getDateText(text); // Jahr vierstellig
			}
			
			setDateFieldescriptionToDate();

			// Datum in die Tabelle und das Feld setzen (mit Uhrzeit um es DB-konform zu machen)
			m_table.getSelectedCell().setText(text);
			if (!text.isEmpty())
			{
				fld.setValue(Format.getDate12Uhr(Format.getDateValue(text)));
			}
			else
			{
				fld.setValue(null);
			}
		}
	}


	/**
	 * Wird beim Beenden der Eingabe für eine Zelle aufgerufen, wenn der Editor geändert wurde
	 * 
	 * @param bookmark
	 * @param cell
	 * @return 
	 */
	protected boolean beginEditing(Object bookmark, ISpreadCell cell) throws Exception {
		// Editor für Date-Fields anpassen
		if (isDateField(cell.getField()))
		{
			// Inhalt der Zelle merken und nach Änderung der Fielddescription wieder zurücksetzen
			String stringValue = cell.getField().getDisplayValue();

			setDateFieldescriptionToText(cell);
			
			cell.setText(stringValue);
			cell.getField().setValue(stringValue);
		}

		else if (isZeitField(cell.getField()))
		{
			int i = Format.getIntValue(cell.getField().getValue());
			// angezeigt wird die Zeit als Text
			cell.setText(Format.getZeitAsText(i));
			
			// der Wert ist die Zeit als Integer
			cell.getField().setValue(Format.getZeitAsText(i));
		}

		return mayEdit(bookmark, cell);
	}

	
	/**
	 * Wird beim Verändern eines Wertes der Tabelle aufgerufen
	 * 
	 * @param bookmark
	 * @param fld
	 */
	protected void tableDataChanged(Object bookmark, IField fld) throws Exception {
////		fld.setValue("change");
//		System.out.println(m_table.getSelectedCell().getText());
//		ISpreadCell c = m_table.getSelectedCell();
//		System.out.println(bookmark);
////		fld.getFieldDescription().setContext("");
//		System.out.println("change: " + fld.getDisplayValue() + " - " + fld.getValue() + " - " + fld.getOriginalValue() + " - " );
//		System.out.println();
	}
	
	
//	/**
//	 * Wird beim Verändern eines Wertes der Tabelle aufgerufen
//	 * 
//	 * @param bookmark
//	 * @param fld
//	 */
//	protected void onFocusGained(IControl control) throws Exception {
//		m_hasFocus = true;
//	}
//	
//	
//	/**
//	 * Wird beim Verändern eines Wertes der Tabelle aufgerufen
//	 * 
//	 * @param bookmark
//	 * @param fld
//	 */
//	protected void onFocusLost(IControl control) throws Exception {
//		m_hasFocus = false;
//	}
//	

	/**
	 * Zelle darf bearbeitet werden
	 * 
	 * @param cell 
	 * @param bookmark 
	 * @return
	 */
	public boolean mayEdit(Object bookmark, ISpreadCell cell) throws Exception {
		//				System.out.println("begin");
//		
//		MultiSpread m = (MultiSpread) m_table;
//		SpreadCell s = (SpreadCell) m_table.getSelectedCell();
//		
//		m_table.getData().moveTo(m.getSelectedBookmark());
//		m_table.getData().getField(1).setValue("");
//		
//		m.setColumnAlignment(2, CaptionType.ALIGNCENTER);
//		m_table.getSelectedCell().getField().setValue("lala");
		
//		System.out.println(Format.getZeitAsText(Format.getIntValue((m_table.getSelectedCell().getField().getValue()))));
//		m_table.getSelectedCell().getField().setValue(Format.getZeitAsText(Format.getIntValue((m_table.getSelectedCell().getField().getValue()))));
//		cell.setText(Format.getZeitAsText(Format.getIntValue((m_table.getSelectedCell().getField().getValue()))));
//		m_cell = cell;
		return true;
	}
	

	/**
	 * Sortieren der Tabelle nach der angeklickten Spalte
	 * 
	 * @throws Exception
	 */
	protected void sort() throws Exception {
		getData().moveTo(getSelectedBookmark());
		CacheObject co = (CacheObject) getData();
		String resid = getSelectedCell().getColumnDescription().getResID();

		// bei Spaltenwechsel immer erst aufsteigend sortieren
		if (!resid.equals(m_lastResID))    
			m_sortAsc = false;

		// sortieren
		co.sortDisplayValue(resid, true);
		((CacheObject)getData()).sortDisplayValue(resid, m_sortAsc);
		m_sortAsc = !m_sortAsc;
		m_lastResID = resid;

		refresh(reasonDataChanged, null);
	}


	/**
	 * Den Bookmark aus dem Cacheobject in der Tabelle markieren und an diese Position scrollen.
	 * 
	 */
	public void showBookmark() {

		new Thread()
		{
			public void run() {

				Display.getDefault().syncExec(new Runnable() {

					@Override
					public void run() {
						Object bookmark;

						try 
						{
							// zum markierten Datensatz scrollen
							bookmark = getData().getBookmark();
							ensureVisible(bookmark);

							refresh(reasonDataChanged, null);
						}
						catch (Exception e) 
						{
							e.printStackTrace();
						}		

					}
				});
			}
		}.start();
	}


	/**
	 * prüft, ob die Zelle leer ist
	 * 
	 * @param cell
	 * @return
	 */
	protected static boolean isCellEmpty(ISpreadCell cell) {
		IField field;
		String value;
		
		if (cell == null)
		{
			return true;
		}
		
		field = cell.getField();
		if (field == null)
		{
			return true;
		}
		
		value = field.getDisplayValue();
		if (value == null || value.isEmpty())
		{
			return true;
		}

		return false;
	}

	
	public void disableSort() {
		m_sortEnabled = false;
	}


	@Override
	public String getResID() {
		return m_table.getResID();
	}


	@Override
	public INode getNode() {
		return m_table.getNode();
	}


	@Override
	public Object getComponent() {
		return m_table.getComponent();
	}


	@Override
	public IControl getParent() {
		return m_table.getParent();
	}


	@Override
	public ISession getSession() {
		return m_table.getSession();
	}


	@Override
	public boolean setFocus() {
		return m_table.setFocus();
	}


	@Override
	public boolean isVisible() {
		return m_table.isVisible();
	}


	@Override
	public void setKey(String key) {
		m_table.setKey(key);
	}


	@Override
	public String getKey() {
		return m_table.getKey();
	}


	@Override
	public void setBounds(int x, int y, int width, int height) {
		m_table.setBounds(x, y, width, height);
	}


	// wird in TableMonatseinsatzblatt überschrieben, dort wird die zuletzt markierte Zeile wieder markiert; falls es noch irgendwo Fehler gibt
	@Override
	public void refresh(int reason, Object element) {
		Object bm = m_table.getSelectedBookmark();
		m_table.refresh(reason, element);
		m_table.setSelectedBookmark(bm);
	}


	@Override
	public void setData(IBusinessObject data) throws Exception {
		m_table.setData(data);
	}


	@Override
	public IBusinessObject getData() {
		return m_table.getData();
	}


	@Override
	public void setHeaderDescription(IHeaderDescription hdesc) {
		m_table.setHeaderDescription(hdesc);
	}


	@Override
	public IHeaderDescription getHeaderDescription() {
		return m_table.getHeaderDescription();
	}


	@Override
	public void setSelectionListener(ISelectionListener listener) {
		m_table.setSelectionListener(listener);
	}


	@Override
	public void setEditListener(ICellEditListener listener) {
		m_table.setEditListener(listener);
	}


	@Override
	public void setCellRenderListener(ICellRenderListener listener) {
		m_table.setCellRenderListener(listener);
	}


	@Override
	public void setKeyListener(IKeyListener listener) {
		m_table.setKeyListener(listener);
	}


	@Override
	public void setMouseListener(IMouseListener listener) {
		m_table.setMouseListener(listener);
	}


	@Override
	public void setFocusListener(IFocusListener listener) {
		m_table.setFocusListener(listener);
	}


	@Override
	public Object[] getSelectedBookmarks() {
		return m_table.getSelectedBookmarks();
	}


	@Override
	public Object getSelectedBookmark() {
		return m_table.getSelectedBookmark();
	}


	@Override
	public void setSelectedBookmarks(Object[] bookmarks) {
		m_table.setSelectedBookmarks(bookmarks);
	}


	@Override
	public void setSelectedBookmark(Object bookmark) {
		m_table.setSelectedBookmark(bookmark);
	}


	@Override
	public ISpreadCell getSelectedCell() {
		return m_table.getSelectedCell();
	}


	@Override
	public void setMenu(IMenu menu) {
		m_table.setMenu(menu);
	}


	@Override
	public void saveColumnWidth() {
		m_table.saveColumnWidth();
	}


	@Override
	public void restoreColumnWidth() {
		m_table.restoreColumnWidth();
	}


	@Override
	public void setFixedCols(int num) {
		m_table.setFixedCols(num);
	}


	@Override
	public void ensureVisible(Object bookmark) {
		m_table.ensureVisible(bookmark);
	}


	@Override
	public void ensureVisible(String resid) {
		m_table.ensureVisible(resid);
	}


	@Override
	public void setMultiselectEnabled(boolean state) {
		m_table.setMultiselectEnabled(state);
	}

}
