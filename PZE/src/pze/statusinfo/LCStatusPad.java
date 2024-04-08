package pze.statusinfo;

import java.io.InputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import framework.Application;
import framework.business.interfaces.nodes.INode;
import framework.business.interfaces.session.ISession;
import framework.business.interfaces.statusinfo.IStatusInfo;
import framework.business.resources.ResourceMapper;
import framework.ui.grafic.ResourceManager;
import framework.ui.images.Images;
import framework.ui.interfaces.controls.IControl;
import framework.ui.interfaces.controls.IStatusControl;

/**
 * StatusInfo mit einem AbbrechenKnopf auf der Oberfläche
 * @author veronika
 * @version 1.1
 */
public class LCStatusPad implements IStatusControl,IStatusInfo
{
	
	/**
	 * Ressource-ID des Fensters
	 */
	public static final String RESID = "statussplash.statusinfo";
	
	//------- Member
	
	private INode node = null;
	private Shell shell = null;
	private String [] labels = new String[0];
	private String taskname = "";
	private String caption = "";
	private int level = 0;
	private int value = 0;
	private int max = 1;
	private int last_progress_width = 0;
	private boolean is_visible = true;
	
	//------- konstante Grössen
	
	private int DLG_WIDTH = 350;
	private final int LABEL_HEIGHT = 25;
	private final int TASKNAME_HEIGHT = 30;
	private final int MARGIN = 15;
	private final int MARGIN_BOTTOM = 10;
	private final int PROGRESS_HEIGHT = 20;
	private final int RADIUS = 10; 
	private final int BUTTON_HEIGHT = 30;
	private final int BUTTON_WIDTH = 80;
	private final String BUTTON_TEXT = "Abbrechen";
	
	//------- Bereiche
	
	private Rectangle rcTaskname = null;
	private Rectangle [] rcLabels = null;
	private Rectangle rcProgress = null;
	private Rectangle rcLogo = null;
	
	//------- Farben
	
	private Color colorBar = null;
	private Color colorBarFrame = null;
	private int colorBackground = 0xF1EFE0;
	
	//------- Image
	
	private InputStream logo_stream = null;
	private Image logo = null;

	//------ AbbrechenKnopf
	private Button abbruchBtn = null;
	private boolean abbruchInklusive = false;
		
	/**
	 * Konstruktion
	 * AbbrechenKnopf soll auf Oberfläche erscheinen
	 * @param mitAbbruch boolean
	 */
	public LCStatusPad(boolean mitAbbruch)
	{
		this.abbruchInklusive = mitAbbruch;
		this.node = ResourceMapper.getInstance().getItem(LCStatusPad.RESID);
	}
	
	/*
	 * (non-Javadoc)
	 * @see framework.ui.interfaces.controls.IStatusControl#isVisible()
	 */
	public boolean isVisible()
	{
		return this.isVisible();
	}

	/*
	 * (non-Javadoc)
	 * @see framework.ui.interfaces.controls.IStatusControl#setVisible(boolean)
	 */
	public void setVisible(boolean state)
	{
		this.is_visible = state;
	}

	/**
	 * Getter is_visible
	 * @return is_visible boolean
	 */
	public boolean getIsVisible(){
		return this.is_visible;
	}
	
	/*
	 * (non-Javadoc)
	 * @see framework.ui.interfaces.controls.IControl#getComponent()
	 */
	public Object getComponent()
	{
		return this.shell;
	}

	/*
	 * (non-Javadoc)
	 * @see framework.ui.interfaces.controls.IControl#getNode()
	 */
	public INode getNode()
	{
		return this.node;
	}

	/*
	 * (non-Javadoc)
	 * @see framework.ui.interfaces.controls.IControl#getParent()
	 */
	public IControl getParent()
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see framework.ui.interfaces.controls.IControl#getResID()
	 */
	public String getResID()
	{
		return LCStatusPad.RESID;
	}

	/*
	 * (non-Javadoc)
	 * @see framework.ui.interfaces.controls.IControl#setBounds(int, int, int, int)
	 */
	public void setBounds(int x, int y, int width, int height)
	{
		this.shell.setBounds(x, y, width, height);
	}

	/*
	 * (non-Javadoc)
	 * @see framework.ui.interfaces.controls.IControl#setFocus()
	 */
	public boolean setFocus()
	{
		return this.shell.setFocus();
	}

	/*
	 * (non-Javadoc)
	 * @see framework.business.interfaces.refresh.IRefreshable#refresh(int, java.lang.Object)
	 */
	public void refresh(int reason, Object element)
	{
		//wird nicht benötigt
	}

	/*
	 * (non-Javadoc)
	 * @see framework.business.interfaces.statusinfo.IStatusInfo#beginTask(java.lang.String, int)
	 */
	public void beginTask(String name, int count)
	{
		this.level++;

		if(this.level > 1)
			return;
		
		if(this.shell == null)
			return;

//		if(!this.shell.isVisible())
//			this.shell.setVisible(true);
		
		if(name == null)
			this.taskname = "";
		else
			this.taskname = name;

		this.value = 0;
		this.last_progress_width = 0;
		this.max = count;
		
		Rectangle rc = this.rcTaskname;
		this.shell.redraw(rc.x, rc.y, rc.width, rc.height, false);
		this.shell.update();
	}

	/*
	 * (non-Javadoc)
	 * @see framework.business.interfaces.statusinfo.IStatusInfo#closeStatus()
	 */
	public void closeStatus()
	{
		this.level = 0;
		this.value = 0;
		this.last_progress_width = 0;
		
		if(this.shell == null)
			return;
		
		this.shell.setVisible(false);
	}
	
	/*
	 * (non-Javadoc)
	 * @see framework.business.interfaces.statusinfo.IStatusInfo#done()
	 */
	public void done()
	{
		if(this.level < 0)
			System.out.println("LCStatusSplash.done: level 0 already reached...");
		
		this.level--;

		if(this.level > 0)
			return;
		
		if(this.shell == null)
			return;
		
		this.value = 0;
		this.last_progress_width = 0;
		Rectangle rc = this.rcProgress;
		this.shell.redraw(rc.x, rc.y, rc.width, rc.height, false);
		this.shell.update();
	}

	/*
	 * (non-Javadoc)
	 * @see framework.business.interfaces.statusinfo.IStatusInfo#getLevel()
	 */
	public int getLevel()
	{
		return this.level;
	}

	/*
	 * (non-Javadoc)
	 * @see framework.business.interfaces.statusinfo.IStatusInfo#openStatus(int)
	 */
	public void openStatus(int count)
	{
		this.labels = new String[count];

		for(int i=0; i<count; i++)
			this.labels[i] = "";
		
		this.level = 0;
		this.value = 0;
		this.last_progress_width = 0;
		this.taskname = "";
	
		//------- Aufräumen
		
		if(this.shell != null) {
			if(!this.shell.isDisposed())
				this.shell.dispose();
		}
		
		//------- Dialog und Ressourcen erzeugen
		
		int decoration = SWT.NO_TRIM;
		
		if(!Application.isMainframeEnabled())
			decoration |= SWT.ON_TOP;
		
		this.shell = new Shell(Display.getCurrent(), decoration);
		this.shell.setText(this.caption);
		this.shell.setBackground(ResourceManager.getColor(this.colorBackground));
		//BUtton-Instanz kreieren
		this.shell.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				LCStatusPad.this.paint(e.gc);
			}
			
		});
		
		//------- Mouse
		
		MouseListener ml = new MouseListener();
		this.shell.addListener(SWT.MouseDown, ml);
		this.shell.addListener(SWT.MouseUp, ml);
		this.shell.addListener(SWT.MouseMove, ml);
		
		//------- Logo

		if(this.logo == null && this.logo_stream != null) {
			ImageData imgdata = new ImageData(this.logo_stream);
			this.logo = new Image(Display.getCurrent(), imgdata);
			this.rcLogo = this.logo.getBounds();
		}
		
		if(this.logo == null)
			this.rcLogo = new Rectangle(0, 0, 0, 0);

		//------- verwendete Farben
		
		this.colorBar = ResourceManager.getWebHexColor("#803380");
		this.colorBarFrame = ResourceManager.getWebHexColor("#808080");
			
		//------- Dialog Grösse und Position bestimmen
		
		int height = this.MARGIN + this.MARGIN_BOTTOM + 
					 this.TASKNAME_HEIGHT + 
					 count * this.LABEL_HEIGHT + 
					 this.PROGRESS_HEIGHT +
					 this.rcLogo.height + 5 + 
					 this.BUTTON_HEIGHT + 50;
		
		this.shell.setRegion(this.getRegion(this.DLG_WIDTH, height));
		Rectangle rect = Display.getCurrent().getClientArea();
		
		if(Application.isMainframeEnabled()) {
			//MainFrame frame = (MainFrame) Application.getMainFrame();
			
			if(Display.getCurrent().getActiveShell() != null)
				rect = Display.getCurrent().getActiveShell().getBounds();
		}
		
		this.shell.setBounds((rect.width-this.DLG_WIDTH) / 2, (rect.height-height) / 2, this.DLG_WIDTH, height);		
		
		//------- Bereiche für Grafik-Updates ermitteln
		
		int y = this.MARGIN + this.rcLogo.height;
		this.rcLabels = new Rectangle[count];
		
		for(int i=0; i<count; i++)
		{
			this.rcLabels[i] = new Rectangle(this.MARGIN, y, this.DLG_WIDTH-2*this.MARGIN, this.LABEL_HEIGHT);
			y += this.LABEL_HEIGHT;
		}
		
		this.rcTaskname = new Rectangle(this.MARGIN, y, this.DLG_WIDTH-2*this.MARGIN, this.TASKNAME_HEIGHT);
		y += this.TASKNAME_HEIGHT;
		
		this.rcProgress = new Rectangle(this.MARGIN, y, this.DLG_WIDTH-2*this.MARGIN, this.PROGRESS_HEIGHT);
		
		//------- Bitmap der Anwendung übernehmen
		
		Image img = Images.getImage("frame.icon");
		
		if(img != null)
			this.shell.setImage(img);
		
		//---- Abbruch-Button falls gewünscht kreieren
		if(this.abbruchInklusive)
		{
			this.abbruchBtn = new Button(this.shell,SWT.PUSH);
			this.abbruchBtn.setSize(this.BUTTON_WIDTH, this.BUTTON_HEIGHT);
			this.abbruchBtn.setToolTipText(this.BUTTON_TEXT);
			this.abbruchBtn.setText(this.BUTTON_TEXT);
			this.abbruchBtn.addSelectionListener(new SelectionListener()
			{
				/*
				 * (non-Javadoc)
				 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
				 */
				public void widgetDefaultSelected(SelectionEvent arg0)
				{
					// wird nicht benötigt ( = Aktion bei Doppelklick)
					
				}

				/*
				 * (non-Javadoc)
				 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
				 */
				public void widgetSelected(SelectionEvent e)
				{
					//Setze BerechnungsstopObserver-cancelIstErwuenscht-Flag auf entgegengesetzten Wert (= Ein- / oder Abschalten
					Statusinfo.setAbgebrochen(true);
				}
				
			});
		}
		
		//------- Dialog anzeigen
		
		this.shell.open();
		
	}

	/*
	 * (non-Javadoc)
	 * @see framework.business.interfaces.statusinfo.IStatusInfo#setBitmap(java.io.InputStream)
	 */
	public void setBitmap(InputStream stream)
	{
		this.logo_stream = stream;
	}

	/*
	 * (non-Javadoc)
	 * @see framework.business.interfaces.statusinfo.IStatusInfo#setMax(int)
	 */
	public void setMax(int max)
	{
		this.max = max;
	}

	/*
	 * (non-Javadoc)
	 * @see framework.business.interfaces.statusinfo.IStatusInfo#setPaneBitmap(int, java.lang.String)
	 */
	public void setPaneBitmap(int pane, String bitmapid)
	{
		
	}

	/*
	 * (non-Javadoc)
	 * @see framework.business.interfaces.statusinfo.IStatusInfo#setPaneText(int, java.lang.String)
	 */
	public void setPaneText(int pane, String message)
	{
		if(this.level > 1)
			return;
		
		if(this.shell == null)
			return;

		if(this.labels.length < pane+1)
			return;
		
		this.labels[pane] = message;
		
		Rectangle rc = this.rcLabels[pane];
		this.shell.redraw(rc.x, rc.y, rc.width, rc.height, false);
		this.shell.update();
	}

	/*
	 * (non-Javadoc)
	 * @see framework.business.interfaces.statusinfo.IStatusInfo#setTaskName(java.lang.String)
	 */
	public void setTaskName(String name) 
	{
		if(this.level > 1)
			return;
		
		if(this.shell == null)
			return;

		if(name != null)
			this.taskname = name;
		else
			this.taskname = "";
		
		Rectangle rc = this.rcTaskname;
		this.shell.redraw(rc.x, rc.y, rc.width, rc.height, false);
		this.shell.update();
	}

	/*
	 * (non-Javadoc)
	 * @see framework.business.interfaces.statusinfo.IStatusInfo#setValue(int)
	 */
	public void setValue(int value)
	{
		if(this.level > 1)
			return;
		
		if(this.shell == null)
			return;
		
		if(value <= this.max)
			this.value = value;
		else
			this.value = this.max;
		
		Rectangle rc = this.rcProgress;
		this.shell.redraw(rc.x, rc.y, rc.width, rc.height, false);
		this.shell.update();
	}

	/*
	 * (non-Javadoc)
	 * @see framework.business.interfaces.statusinfo.IStatusInfo#tick()
	 */
	public void tick() 
	{
		if(this.level > 1)
			return;
		
		if(this.shell == null)
			return;
		
		if(this.value < this.max)
			this.value++;

		
		//------- Unnötige updates vermeiden (Flicker)
		
		int pgap = 3;
		int current_width = 0;
		
		if(this.max > 0)
			current_width = (this.rcProgress.width - 2*pgap) * this.value / this.max;

		if(current_width != this.last_progress_width)
		{
			// Die Grösse des Balkens hat sich um mindestens einen Pixel geändert
			this.last_progress_width = current_width;
			Rectangle rc = this.rcProgress;
			this.shell.redraw(rc.x, rc.y, rc.width, rc.height, false);
			this.shell.update();
		}
		
	}
	
	/*
	 * gibt eine Region für die Form der Shell zurück
	 */
	private Region getRegion(int width, int height)
	{
		Region region = new Region();
		region.add(this.getCircle(this.RADIUS, this.RADIUS, this.RADIUS));
		region.add(this.getCircle(width-this.RADIUS, this.RADIUS, this.RADIUS));
		region.add(this.getCircle(this.RADIUS, height-this.RADIUS, this.RADIUS));
		region.add(this.getCircle(width-this.RADIUS, height-this.RADIUS, this.RADIUS));
		region.add(0, this.RADIUS, width, height-2*this.RADIUS);
		region.add(this.RADIUS, 0, width-2*this.RADIUS, this.RADIUS);
		region.add(this.RADIUS, height-this.RADIUS, width-2*this.RADIUS, this.RADIUS);
		return region;
	}
	
	/*
	 * Erzeugt ein Polygon zur Annäherung an einen Kreis
	 */
	private int[] getCircle(int x0, int y0, int r)
	{
		int[] polygon = new int[8 * r + 4];

		for (int i=0; i < (2 * r + 1); i++)
		{
			int x = i - r;
			int y = (int) Math.sqrt(r*r - x*x);
			polygon[2*i] = x0 + x;
			polygon[2*i+1] = y0 + y;
			polygon[8*r - 2*i - 2] = x0 + x;
			polygon[8*r - 2*i - 1] = y0 - y;
		}

		return polygon;
	}
	
	/*
	 * Zeichnen
	 */
	private void paint(GC gc)
	{
		//------- Rahmen
		
		gc.setForeground(this.colorBarFrame);
		gc.setLineWidth(2);
		Rectangle rcShell = this.shell.getClientArea();
		gc.drawRoundRectangle(rcShell.x, rcShell.y, rcShell.width-1, rcShell.height-1, 2*this.RADIUS, 2*this.RADIUS);
		
		//------- Logo
		
		if(this.logo != null)
			gc.drawImage(this.logo, 10, 10);
		
		//------- Panes
		
		gc.setLineWidth(1);
		gc.setForeground(this.shell.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		
		for(int i=0; i<this.labels.length; i++)
			gc.drawString(this.labels[i] + "", this.rcLabels[i].x, this.rcLabels[i].y);
		
		//------- Taskname
		
		gc.drawString(this.taskname, this.rcTaskname.x, this.rcTaskname.y);
		
		//------- Progressbar

		gc.setForeground(this.colorBarFrame);

		gc.drawRoundRectangle(
				this.rcProgress.x, 
				this.rcProgress.y, 
				this.rcProgress.width, 
				this.rcProgress.height, 7, 7);
		
	
		if(this.abbruchInklusive)
			this.abbruchBtn.setLocation(this.rcProgress.x,this.rcProgress.y + 80);
			
		gc.setForeground(this.colorBar);
		gc.setBackground(this.colorBar);
		
		if(this.max > 0)
		{
			int pgap = 3;
			int current_width = (this.rcProgress.width - 2*pgap) * this.value / this.max;
			
			gc.fillRectangle(
					this.rcProgress.x + pgap, 
					this.rcProgress.y + pgap, 
					current_width, 
					this.rcProgress.height - 2*pgap + 1);
			
		}
		
	}
	
	/**
	 * Mouse Events verarbeiten
	 */
	class MouseListener implements Listener 
	{
		Point origin = null;
		
		public void handleEvent(Event e) 
		{
			switch(e.type) 
			{
				case SWT.MouseDown:
					this.origin = new Point(e.x, e.y);
					break;
					
				case SWT.MouseUp:
					this.origin = null;
					break;
					
				case SWT.MouseMove:
					if(this.origin != null) 
					{
						Point p = LCStatusPad.this.shell.getDisplay().map(shell, null, e.x, e.y);
						LCStatusPad.this.shell.setLocation(p.x - origin.x, p.y - origin.y);
					}
					break;
			}
			
		}
		
	}

	public String getKey() {
		//wird nicht benötigt
		return null;
	}

	public void setKey(String key) {
		//wird nicht benötigt
		
	}

	@Override
	public ISession getSession() {
		return null;
	}

	@Override
	public void cancel() {
		//wird nicht benötigt
	}

	@Override
	public boolean isCancelled() {
		//wird nicht benötigt
		return false;
	}

	@Override
	public void openStatus(int arg0, boolean arg1) {
		//wird nicht benötigt
	}

	@Override
	public void setActionID(String arg0) {
		//wird nicht benötigt
	}
}
