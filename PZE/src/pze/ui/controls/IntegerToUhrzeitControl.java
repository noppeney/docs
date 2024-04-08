package pze.ui.controls;


import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Text;

import framework.ui.controls.IntegerControl;
import framework.ui.grafic.ResourceManager;
import pze.business.Format;


/**
 * Integer Control, das den Wert in eine Uhrzeit im Format hh:mm umwandelt
 * 
 * @author Lisiecki
 */
public class IntegerToUhrzeitControl extends IntegerControl {

	/**
	 * Konstruktor
	 * @param parent		Parent
	 * @param resid			Resid
	 * @throws Exception
	 */
	public IntegerToUhrzeitControl(Object parent, String resid) throws Exception {
		super(parent, resid);
		
		// aktiviere die sofortige Aktualisierung
		setDirectModificationEnabled(true);
	}

	
	/**
	 * Hier darf nicht geparst werden. Dies geschiet erst beim setzen des Wertes
	 * 
	 * (non-Javadoc)
	 * @see framework.ui.controls.BaseFieldEditControl#getControlValue()
	 */
	@Override
	protected Object getControlValue() {
		return Format.getZeitAsInt(((Text) super.getActiveControl()).getText());
	}

	
	/**
	 * Wert setzen
	 * 
	 * (non-Javadoc)
	 * @see framework.ui.controls.BaseFieldEditControl#setControlValue()
	 */
	@Override
	protected void setControlValue() {
		Object value;
		String stringValue;
		
		// angezeigten Text laden, Feldwert laden um beim Abbrechen der Eingabe wieder ein leeres Feld anzuzeigen
		stringValue = ((Text) super.getActiveControl()).getText();
		value = super.getField().getValue();

		// wenn noch kein Text angezeigt wird (beim Erstellen des Textfeldes oder beim Abbrechen der Eingabe)
		if (value == null || stringValue.isEmpty())
		{
			// hole den aus der DB geladenen Wert (in Minuten)
			stringValue = super.getField().getDisplayValue();

			// wenn noch kein Wert vorhanden ist, trage nichts ein (nicht 0:00)
			if (stringValue.isEmpty())
			{
				resetValue();
				return;
			}
			
			stringValue = Format.getZeitAsText(Format.getIntValue(stringValue));
		}
		
		((Text) super.getActiveControl()).setText(Format.getZeitAsText(stringValue));
	}


	/**
	 * Wert setzen
	 * 
	 *  (non-Javadoc)
	 * @see framework.ui.controls.TextControl#onFocusLost()
	 */
	@Override
	protected void onFocusLost() {
		setControlValue();
	}

	
	/**
	 * Wert "" setzen.<br>
	 * Wird benötigt, wenn ein neues Cacheobject gesetzt wird und vorher bereits ein Wert eingetragen war
	 * 
	 */
	public void resetValue() {
		((Text) super.getActiveControl()).setText("");
	}


	/**
	 * Beim Refresh muss abgefragt werden, ob der Wert im CO-Field geändert wurde
	 * 
	 * @see framework.ui.controls.TextControl#refresh(int, java.lang.Object)
	 */
	public void refresh(int reason, Object element) {
		super.refresh(reason, element);
		
		String value;
		
		if (reason == reasonDataChanged)
		{
			// hole den aus der DB geladenen Wert (in Minuten)
			value = super.getField().getDisplayValue();

			// wenn noch kein Wert vorhanden ist, trage nichts ein (nicht 0:00)
			if (value.isEmpty())
			{
				return;
			}

			value = Format.getZeitAsText(Format.getIntValue(value));

			((Text) super.getActiveControl()).setText(Format.getZeitAsText(value));
		}
		
		// Textfelder werden nicht gesperrt, sondern nur schreibgeschützt,
		// damit das Scrollen und das Kopieren von Inhalten in die Zwischenablage funktioniert
		// funktioniert hier nicht, das CO wird als modified angezeigt (Diskette aktiviert & speichern gefordert)
//		if(reason == IRefreshable.reasonDisabled) {
//
//			((Composite) super.getComponent()).setEnabled(true);
//			((Text) super.getActiveControl()).setEditable(false);
//
//		} else if(reason == IRefreshable.reasonEnabled) {
//
//			((Text) super.getActiveControl()).setEditable(true);
//		}
	}

	
	/**
	 * Hintergrundfarbe setzen
	 * 
	 * @param colorValue
	 */
	public void setColor(String colorValue){
		Color clr;

		if (colorValue != null)
		{
			clr = ResourceManager.getWebHexColor(colorValue.replace("##", "#"));
			((Text) super.getActiveControl()).setBackground(clr);
		}
	}
	
}
