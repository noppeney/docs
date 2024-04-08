/*
 * updates:
 * 02.05.12 ebb, Erstellung
 * 12.08.12 ebb, Aktualisierung der Referenzlisten
 * 25.03.13 ebb, RefItems werden nicht mehr kopiert, sondern nur zentral im ResourceMapper gehalten
 */
package framework.reftables.actions;

import java.util.Iterator;

import framework.Application;
import framework.business.action.ActionAdapter;
import framework.business.interfaces.ListType;
import framework.business.interfaces.data.IBusinessObject;
import framework.business.interfaces.fields.IFieldDescription;
import framework.business.interfaces.nodes.INode;
import framework.business.interfaces.refresh.IRefreshable;
import framework.business.interfaces.tables.ITableDescription;
import framework.business.resources.ResourceMapper;
import framework.ui.messagebox.MessageBox;

/**
 * Listener für das Speichern der Änderungen beim Bearbeiten von Referenztabellen
 * 
 * @author Gerrit Ebbers
 * @version 1.1
 */
public class SaveListener extends ActionAdapter {

	
	private IBusinessObject data;
	private IRefreshable content;

	public SaveListener(IBusinessObject data, IRefreshable content) {
		this.data = data;
		this.content =content;
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see framework.business.action.ActionAdapter#activate(java.lang.Object)
	 */
	@Override
	public void activate(Object sender) throws Exception {
		
		//------- Änderungen speichern
		try
		{
			data.save();
		}	
		catch(Exception e)
		{
			String msg = 
					"Fehler beim Speichern der Änderungen:<br/><br/>" + 
			ResourceMapper.getInstance().getErrorMessage(e);
			MessageBox.show("Änderungen speichern", msg, "msgbox.stop");
			return;
		}
		
		//------- Edit-Modus ausschalten
		content.refresh(IRefreshable.reasonDisabled, null);
		super.getSession().getMainFrame().getToolbar().updateEditMode(false);
		
		//------- Refresh aller betroffenen Referenztabellen
		updateRefTable(data);
	}
	
	
	/**
	 * Datadictionary-Referenzen aktualisieren
	 * @param data Referenztabellen
	 * @throws Exception
	 */
	public static void updateRefTable(IBusinessObject data) throws Exception {
		ITableDescription tdesc = ResourceMapper.getInstance().getTableDescription(data.getResID());
		
		if(tdesc != null) {
		
			String tableName = tdesc.getName();
			Iterator<ITableDescription> tdescs = ResourceMapper.getInstance().getTableDescriptions();
			
			while(tdescs.hasNext()) {
				
				tdesc = tdescs.next();
				Iterator<INode> fdescs = tdesc.getChilds();
				
				while(fdescs.hasNext()) {
					
					//------- hat das Feld hinterlegte Referenzeinträge?
					
					IFieldDescription fdesc = (IFieldDescription) fdescs.next();
					
					if(fdesc.getListType() == ListType.NONE)
						continue;
					
					//------- wird die gerade bearbeitete Referenztabelle referenziert?
					
					String refTableName = fdesc.getRefTableName();
					
					if(!tableName.equalsIgnoreCase(refTableName))
						continue;
					
					//------- werden Felder referenziert?
					
					String keyFieldName = fdesc.getKeyFieldName();
					String displayFieldName = fdesc.getDisplayFieldName();
					
					if(keyFieldName == null || displayFieldName == null)
						continue;
					
					//------- Referenzeinträge neu laden
					
					try {
						// Globale RefItems aktualisieren
						Application.getRefTableLoader().load(fdesc);
					}
					
					catch(Exception e) {
						String msg = 
								"Fehler beim Aktualisieren der Referenzlisten:<br/><br/>" + 
						ResourceMapper.getInstance().getErrorMessage(e);
						MessageBox.show("Referenzlisten aktualisieren", msg, "msgbox.stop");
						return;
					}
					
				}
				
			}
		
		}
	}

	
	/*
	 * (non-Javadoc)
	 * @see framework.business.action.ActionAdapter#getEnabled()
	 */
	@Override
	public boolean getEnabled() {
		
		
		
		//------- Modified
		
		return data.isModified();
		
	}

}
