package pze.ui.controls;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import framework.business.interfaces.refresh.IRefreshable;
import framework.ui.controls.DateControl;
import framework.ui.interfaces.controls.IControl;
import framework.ui.interfaces.keys.IKeyListener;
import framework.ui.interfaces.keys.KeyData;


/**
 * DateControl um die Eingabe eines Datums ohne Punkte zuzulassen
 * 
 * @author Lisiecki
 */
public class DateControlPze extends DateControl{

	private boolean m_isCopyModeEnabled = true;
	
	

	/**
	 * Konstruktion des Controls
	 * @param parent IControl Parent mit SWT-Komponente
	 * @param resid Ressource-ID des Controls
	 * @throws Exception
	 */
	public DateControlPze(Object parent, String resid) throws Exception {
		super(parent, resid);
		
		// Änderungen sofort registrieren
//		super.setDirectModificationEnabled(true); geht nicht, weil sonst bei der EIngabe direkt formatiert wird, bevor das Datum vollständig eingegeben ist
		
		// Bei Enter zum Bestätigen das Datum formatieren, falls es ohne Punkte eingegeben wurde
		setKeyListener(new IKeyListener() {
			
			@Override
			public boolean onKeyReleased(IControl control, KeyData data) {
				return true;
			}
			
			@Override
			public boolean onKeyPressed(IControl control, KeyData data) {
				if (data.keyCode == 13)
				{
					formatDatum();
				}
				
				return true;
			}
		});
	}


	/**
	 * Datum ohne Punkte formatieren
	 * 
	 * @see framework.ui.controls.TextControl#onFocusLost()
	 */
	@Override
	protected void onFocusLost() {
		formatDatum();

		super.onFocusLost();
	}


	/**
	 * Datum ohne Punkte formatieren
	 */
	private void formatDatum() {
		String text;

		text = ((Text) super.getActiveControl()).getText();

		// wenn keine Punkte im datum vorhanden sind, füge sie ein
//		if (!text.contains(".") && text.length() > 4)
		{
//			text = text.substring(0, 2) + "." + text.substring(2, 4) + "." + text.substring(4);
			((Text) super.getActiveControl()).setText(formatDatum(text));
		}
	}


	/**
	 * Datum ohne Punkte formatieren, z. B. aus 010120 -> 01.01.2020
	 * 
	 * @return geänderter Text als Datum oder der originale Text
	 */
	public static String formatDatum(String datum) {

		// wenn keine Punkte im datum vorhanden sind, füge sie ein
		if (!datum.contains(".") && datum.length() > 4)
		{
			datum = datum.substring(0, 2) + "." + datum.substring(2, 4) + "." + datum.substring(4);
			return datum;
		}
		
		return datum;
	}

	
	/*
	 * (non-Javadoc)
	 * @see framework.ui.controls.BaseFieldEditControl#refresh(int, java.lang.Object)
	 */
	@Override
	public void refresh(int reason, Object element) {

		super.refresh(reason, element);

		// prüfen, ob das Feld aktiviert werden darf
		if (!m_isCopyModeEnabled)
		{
			return;
		}
		
		// Textfelder werden nicht gesperrt, sondern nur schreibgeschützt,
		// damit das Scrollen und das Kopieren von Inhalten in die Zwischenablage funktioniert
		if(reason == IRefreshable.reasonDisabled)
		{
//			System.out.println("disable" + getResID());
			((Composite) super.getComponent()).setEnabled(true);
			((Text) super.getActiveControl()).setEditable(false);
//			onFocusLost();
		} 
		else if(reason == IRefreshable.reasonEnabled) 
		{
//			System.out.println("enable" + getResID());
			((Text) super.getActiveControl()).setEditable(true);
		}
	}

	
	/**
	 * Modus zum Kopieren des Inhaltes auch bei inaktiven Feldern aktivieren/deaktivieren
	 * 
	 * @param enableCopyMode
	 */
	public void enableCopyMode(boolean enableCopyMode){
		m_isCopyModeEnabled = enableCopyMode;
	}
}
