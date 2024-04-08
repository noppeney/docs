/*
 * updates:
 * 07.04.12 ebb, Erstellung
 */
package framework.reftables.ui;

import framework.Application;
import framework.business.cacheobject.CacheObject;
import framework.business.interfaces.TableType;
import framework.business.interfaces.nodes.INode;
import framework.business.interfaces.session.ISession;
import framework.business.nodes.BaseNode;
import framework.business.resources.ResourceMapper;
import framework.cui.layout.UniLayout;
import framework.reftables.loader.ReftablesNavigationLoader;
import framework.ui.form.UniForm;
import framework.ui.interfaces.controls.IControl;
import framework.ui.interfaces.controls.INodeControl;
import framework.ui.interfaces.controls.ITabFolder;
import framework.ui.interfaces.controls.ITabItem;
import framework.ui.interfaces.selection.INodeListener;
import framework.ui.messagebox.MessageBox;
import pze.business.objects.reftables.ReftableCacheObject;


/**
 * Navigation für die Administration der Referenztabellen
 * 
 * @author Gerrit Ebbers
 * @version 1.1
 */
public class ReftablesNavigationForm extends UniForm {

	/*
	 * Member
	 */
	private final static String RESID = "form.reftables.navigation";
	private INodeControl navigation = null;
	private String addActionName;
	private String removeActionName;
	private ISession session;
	
	/**
	 * Konstruktion
	 * @param parent
	 * @param resid
	 * @throws Exception
	 */
	public ReftablesNavigationForm(ISession session, Object parent, String addActionName, String removeActionName) throws Exception {
		
		//------- Konstruktion
		
		super(parent, ReftablesNavigationForm.RESID);
		this.session = session;
		this.addActionName = addActionName;
		this.removeActionName = removeActionName;

		super.createChilds();
		UniLayout layout = new UniLayout();
		super.setLayout(layout);
		layout.setControl(this);
		
		//------- Navigation
		
		this.navigation = (INodeControl) super.findControl("tree.reftables");
		this.navigation.setRootNode(this.createRootNode());
		
		//------- Selektionen
		
		this.navigation.setNodeListener(new INodeListener() {

			/*
			 * (non-Javadoc)
			 * @see framework.ui.interfaces.selection.INodeListener#nodeSelected(framework.ui.interfaces.controls.IControl, framework.business.interfaces.nodes.INode)
			 */
			@Override
			public void nodeSelected(IControl control, INode node) {
				ReftablesNavigationForm.this.onNodeSelected(node);
			}

			/*
			 * (non-Javadoc)
			 * @see framework.ui.interfaces.selection.INodeListener#nodeDefaultSelected(framework.ui.interfaces.controls.IControl, framework.business.interfaces.nodes.INode)
			 */
			@Override
			public void nodeDefaultSelected(IControl control, INode node) {
				// wird nicht unterstützt
			}
			
		});

	}

	/**
	 * erzeugt den Root-Node
	 */
	private INode createRootNode() {
		BaseNode root = new BaseNode(super.getResID() + ".root");
		root.setLoader(new ReftablesNavigationLoader());
		return root;
	}

	/**
	 * Handler für Selektionen im Baum
	 */
	private void onNodeSelected(INode node) {
		
		//------- Messagebox Antwort auswerten
	
		
		//------- unzulässige Ebenen filtern
		
		String resid = node.getResID();
		
		if(TableType.CUSTOMER_REFERENCE_TABLE.toString().equals(resid) || 
		   TableType.SYSTEM_REFERENCE_TABLE.toString().equals(resid)) {
			super.getSession().getMainFrame().setContent(null);
			return;
		}
		
		//------- ist bereits ein Formular angemeldet?
		
		ITabFolder editFolder = session.getMainFrame().getEditFolder();
		String key = "reftable." + node.getResID();
		
		ITabItem item = editFolder.get(key);

		
		if(item != null) {
			
			//------- ist die Referenztabelle bereits in Bearbeitung?
			editFolder.setSelection(key);
			return;
		}
		//------- Referenztabelle laden
		ReftableForm form = null;
		CacheObject data = new ReftableCacheObject(node.getResID());

		try {
			Application.getLoaderBase().load(data, null, "2 ASC");
			form = new ReftableForm(editFolder, addActionName, removeActionName);
			form.setData(data);
			item = editFolder.add(node.getText(), key, form,true);
			editFolder.setSelection(key);
		}
		catch(Exception e) {
			
			String msg = 
				"Fehler beim Anzeigen der Referenztabelle " + node.getText() + "<br/><br/>" +
				ResourceMapper.getInstance().getErrorMessage(e);
			
			MessageBox.show("Referenztabellen", msg, "msgbox.stop");
			return;
			
		}			
	}
	
}
