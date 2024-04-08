package pze.business.datentransfer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;

import framework.business.action.Action;
import framework.business.action.ActionAdapter;
import framework.business.interfaces.fields.IField;
import framework.business.interfaces.refresh.IRefreshable;
import pze.business.Messages;
import pze.business.objects.reftables.CoPath;


/**
 * Allgemeiner Handler zum Upload, Anzeigen und Löschen von Dateien
 * 
 * @author Lisiecki
 */
public class DateiHandler
{

	private String action;
	private IField field;
	private UploadListener uploadlistener;
	private DeleteListener deletelistener;
	private OpenListener openlistener;
	private IRefreshable refresh;

	
	
	/**
	 * Konstruktor
	 * 
	 */
	public DateiHandler() {

	}
	

	/**
	 * Konstruktor
	 * 
	 * @param refresh		zu refreshendes Formular
	 * @param field			zu füllendes Feld
	 * @param action		Prefix für die Aktionen, diese müssen heissen: <action>.upload <action>.open
	 */
	public DateiHandler(IRefreshable refresh, IField field, String action)
	{
		this.action = action;
		this.field = field;
		this.refresh = refresh;
		
		this.uploadlistener = new UploadListener();
		this.openlistener = new OpenListener();
		this.deletelistener = new DeleteListener();
	}
	

	public void activate()
	{
		Action.get(action+".upload").addActionListener(uploadlistener);
		Action.get(action+".open").addActionListener(openlistener);
		Action.get(action+".delete").addActionListener(deletelistener);
	}
	
	
	public void deactivate()
	{
		Action.get(action+".upload").removeActionListener(uploadlistener);
		Action.get(action+".open").removeActionListener(openlistener);
		Action.get(action+".delete").removeActionListener(deletelistener);
	}
	
	
	/**
	 * Dateien hochladen
	 */
	class UploadListener extends ActionAdapter
	{
		FileDialog fd;
		
		public UploadListener(){
			String [] extensions = { "*.*", "*.pdf", "*.doc", "*.xls", "*.csv", "*.htm?" };
			String [] names = { "Alle Dateien [*.*]", "PDF Dateien [*.pdf]", "Word Dokumente [*.doc]", 
					"Excel Tabellen [*.xls]", "CSV Dateien [*.csv]", 
					"HTML Dateien [*.htm, *.html]" };

			fd = new FileDialog(Display.getCurrent().getActiveShell(), SWT.OPEN);
			fd.setText("Dateiauswahl");
			fd.setFilterExtensions(extensions);
			fd.setFilterNames(names);
		}
		
		/**
		 * setzt die übergebenen Filter statt den Default-Filtern
		 * 
		 * @param extensions
		 * @param names
		 */
		public void setFilter(String[] extensions, String[] names){
			fd.setFilterExtensions(extensions);
			fd.setFilterNames(names);		
		}
		
		/* (non-Javadoc)
		 * @see framework.business.action.ActionAdapter#activate(java.lang.Object)
		 */
		@Override
		public void activate(Object sender) throws Exception {
			super.activate(sender);
			
			String pathneu =  fd.open();
			if (pathneu != null)
			{
				CoPath path = new CoPath();
				field.setValue(path.upload(pathneu));
				refresh.refresh(IRefreshable.reasonDataChanged, null);
			}

		}
	}

	/**
	 * Dateien öffnen
	 */
	class OpenListener extends ActionAdapter
	{
		/* (non-Javadoc)
		 * @see framework.business.action.ActionAdapter#activate(java.lang.Object)
		 */
		@Override
		public void activate(Object sender) throws Exception {
			CoPath path = new CoPath();
			path.open(field.getStringValue());
			super.activate(sender);
		}
	}
	
	
	/**
	 * Dateien löschen
	 * Die Datei wird dabei nicht gelöscht, sondern nur der Link entfernt.
	 * 
	 */
	class DeleteListener extends ActionAdapter
	{
		/* (non-Javadoc)
		 * @see framework.business.action.ActionAdapter#activate(java.lang.Object)
		 */
		@Override
		public void activate(Object sender) throws Exception {
			
			if (!Messages.showYesNoMessage("Dokument löschen?", "Soll das Dokument wirklich gelöscht werden?"))
			{
				return;
			}

			field.setValue(null);
			refresh.refresh(IRefreshable.reasonDataChanged, null);

		}
	}
}