package pze.ui.controls;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import framework.business.interfaces.refresh.IRefreshable;
import framework.ui.controls.TextControl;

/**
 * SWT-Control für Textfelder, in denen der Inhalt markiert und kopiert werden kann.
 * 
 * @author Lisiecki
 */
public class TextControlWti extends TextControl { 
	
	/**
	 * Konstruktor
	 * @param parent IControl parent mit SWT-Komponente
	 * @param resid Ressource-ID des Controls
	 * @throws Exception
	 */
	public TextControlWti(Object parent, String resid) throws Exception {
		super(parent, resid);
	}

	
	/*
	 * (non-Javadoc)
	 * @see framework.ui.controls.BaseFieldEditControl#refresh(int, java.lang.Object)
	 */
	@Override
	public void refresh(int reason, Object element) {

		super.refresh(reason, element);

		// Textfelder werden nicht gesperrt, sondern nur schreibgeschützt,
		// damit das Scrollen und das Kopieren von Inhalten in die Zwischenablage funktioniert
		if(reason == IRefreshable.reasonDisabled) {

			((Composite) super.getComponent()).setEnabled(true);
			((Text) super.getActiveControl()).setEditable(false);

		} else if(reason == IRefreshable.reasonEnabled) {

			((Text) super.getActiveControl()).setEditable(true);

		}
	}
}
