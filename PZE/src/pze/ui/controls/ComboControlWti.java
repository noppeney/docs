/*
 * updates:
 * 19.05.09 ebb, Erstellung
 * 12.11.10 rst, VisibleItemCount auf 25 gesetzt
 * 16.01.11 ebb, UnterstÃ¼tzung von Werten, die nicht in der Liste stehen
 * 07.07.11 ebb, AutoComplete Funktion
 * 27.04.16 ebb, Ãœbernahme der Werte per Enter-Taste und Traverse-Keys
 */
package pze.ui.controls;

import framework.ui.controls.ComboControl;
import framework.ui.interfaces.controls.IControl;
import framework.ui.interfaces.keys.IKeyListener;
import framework.ui.interfaces.keys.KeyData;

/**
 * SWT-Control fÃ¼r die Typen "control.xxx.comboro" und "control.xxx.comborw".
 * 
 * @author G. Ebbers
 * @version 1.8
 */
public class ComboControlWti extends ComboControl { 
	
	/**
	 * Combobox Framework 6 Konstruktor
	 * @param parent IControl parent mit SWT-Komponente
	 * @param resid Ressource-ID des Controls
	 * @throws Exception
	 */
	public ComboControlWti(Object parent, String resid) throws Exception {
		super(parent, resid);

		setKeyListener(new IKeyListener() {

			@Override
			public boolean onKeyPressed(IControl control, KeyData data) {

				// mit "Entf" und "<-" Eintrag in der Tabelle löschen
				if (data.keyCode == 8 || data.keyCode == 127)
				{
					getField().setValue(null);
					refresh(reasonDataChanged, null);
//					refresh(reasonDisabled, null);
//					refresh(reasonEnabled, null);
				}

				
//				System.out.println(data);
////				System.out.println(control.getResID());
////				System.out.println(this);
//				// mit "Entf" und "<-" Eintrag in der Tabelle löschen
//				if (data.keyCode == 8 || data.keyCode == 127)
//				{
//					getField().setValue(null);
//					setControlValue();
////					System.out.println(getField().getValue() + " - " + getControlValue());
////					((ComboControl) control).setControlValue();
//					refresh(reasonDataChanged, null);
////					System.out.println(getField().getValue() + " - " + getControlValue());
//					refresh(reasonEnabled, null);
//					refresh(reasonInvisible, null);
//					refresh(reasonVisible, null);
//					refresh(reasonCanceled, null);
//					refresh(reasonDisabled, null);
//					refresh(reasonDataAdded, null);
//					refresh(reasonItemsChanged, null);
//					refresh(reasonNewType, null);
//					refresh(reasonPermissions, null);
//					refresh(reasonRedraw, null);
//					refresh(reasonTerminate, null);
////					System.out.println(getField().getValue() + " - " + getControlValue());
//					refresh(reasonEnabled, null);
////					System.out.println(getField().getValue() + " - " + getControlValue());
//					onKeyReleased(control, data);
//					control.setFocus();
//					setFocus();
//					getActiveControl().forceFocus();
//					data.keyCode = 13;
//					data.character = '\n';
//					onKeyPressed(control, data);
//					onKeyReleased(control, data);
//				}
					
				return true;
			}

			@Override
			public boolean onKeyReleased(IControl control, KeyData data) {
				return false;
			}
		});
	}

}
