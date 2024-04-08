package pze.business;

import framework.FW;
import framework.ui.messagebox.MessageBox;

/**
 * Klasse zum Anzeigen von Messageboxen
 * 
 * @author Lisiecki
 */
public class Messages {


	/**
	 * Messagebox mit der Caption "Information" und Info-Icon
	 * 
	 * @param meldung Meldungstext
	 */
	public static void showInfoMessage(String meldung){
		showInfoMessage("Information", meldung);
	}


	/**
	 * Messagebox mit Info-Icon
	 * 
	 * @param caption Caption der Messagebox
	 * @param meldung Meldungstext
	 */
	public static void showInfoMessage(String caption, String meldung){
		showMessage(caption, meldung, "msgbox.info");
	}


	/**
	 * Messagebox mit der Caption "Warnung" und Warnungs-Icon
	 * 
	 * @param meldung Meldungstext
	 */
	public static void showWarningMessage(String meldung){
		showWarningMessage("Warnung", meldung);
	}


	/**
	 * Messagebox mit Warnungs-Icon
	 * 
	 * @param caption Caption der Messagebox
	 * @param meldung Meldungstext
	 */
	public static void showWarningMessage(String caption, String meldung){
		showMessage(caption, meldung, "msgbox.warning");
	}


	/**
	 * Messagebox mit der Caption "Fehler" und Fehler-Icon
	 * 
	 * @param meldung Meldungstext
	 */
	public static void showErrorMessage(String meldung){
		showErrorMessage("Fehler", meldung);
	}


	/**
	 * Messagebox mit Fehler-Icon
	 * 
	 * @param caption Caption der Messagebox
	 * @param meldung Meldungstext
	 */
	public static void showErrorMessage(String caption, String meldung){
		showMessage(caption, meldung, "msgbox.stop");
	}


	/**
	 * Allgemeine Messagebox
	 * 
	 * @param caption Caption der Messagebox
	 * @param meldung Meldungstext
	 * @param bitmap anzuzeigendes Bitmap
	 */
	public static void showMessage(String caption, String meldung, String bitmap){
		MessageBox.show(caption, meldung, bitmap);
	}


	/**
	 * Messagebox mit ja/nein-Abfrage
	 * 
	 * @param caption Caption der Messagebox
	 * @param meldung Meldungstext
	 * @return ja geklickt 
	 */
	public static boolean showYesNoMessage(String caption, String meldung){
		return (MessageBox.show(caption, meldung, "msgbox.question", null, FW.YES | FW.NO) == FW.YES);
	}
	

	/**
	 * Messagebox mit ja/nein-Abfrage und Error-Bitmap
	 * 
	 * @param caption Caption der Messagebox
	 * @param meldung Meldungstext
	 * @return ja geklickt 
	 */
	public static boolean showYesNoErrorMessage(String caption, String meldung){
		return (MessageBox.show(caption, meldung, "msgbox.stop", null, FW.YES | FW.NO) == FW.YES);
	}
	

	/**
	 * Messagebox mit ja/nein/Abbrechen-Abfrage
	 * 
	 * @param caption Caption der Messagebox
	 * @param meldung Meldungstext
	 * @return geklickter Button (FW.YES | FW.NO | FW.CANCEL)
	 */
	public static int showYesNoCancelMessage(String caption, String meldung){
		return MessageBox.show(caption, meldung, "msgbox.question", null, FW.YES | FW.NO | FW.CANCEL);
	}
	
	
}
