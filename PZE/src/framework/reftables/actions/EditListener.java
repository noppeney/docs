/*
 * updates:
 * 0.05.12 ebb, Erstellung
 */
package framework.reftables.actions;

import framework.business.action.ActionAdapter;
import framework.business.interfaces.data.IBusinessObject;
import framework.business.interfaces.refresh.IRefreshable;
import framework.business.resources.ResourceMapper;

/**
 * Listener für das Editieren von Referenztabellen
 * 
 * @author Gerrit Ebbers / LP-IT
 * @version 1.1
 */
public class EditListener extends ActionAdapter {

	
	
	private IBusinessObject data;
	private IRefreshable content;

	public EditListener(IBusinessObject data, IRefreshable content) {
		this.data = data;
		this.content = content;
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see framework.business.action.ActionAdapter#activate(java.lang.Object)
	 */
	@Override
	public void activate(Object sender) throws Exception {

		
		//------- Modus umschalten
		
		if(data.isEditing()) {
			
			data.cancel();
			content.refresh(IRefreshable.reasonDisabled, null);
			
		} else {
			
			data.begin();
			content.refresh(IRefreshable.reasonEnabled, null);
			
		}
		
		//------- Toolbar-Button umschalten
		
		super.getSession().getMainFrame().getToolbar().updateEditMode(data.isEditing());
		
	}

	/*
	 * (non-Javadoc)
	 * @see framework.business.action.ActionAdapter#getEnabled()
	 */
	@Override
	public boolean getEnabled() {
	
		
		if(data == null)
			return false;
		
		String resid = data.getResID();
		
		//------- Berechtigungen für die Bearbeitung prüfen
		
		int [] groupids = super.getSession().getUserInfo().getGroups();
		boolean enabled = ResourceMapper.getInstance().isEnabled(resid, groupids);
		return enabled;
		
	}

}
