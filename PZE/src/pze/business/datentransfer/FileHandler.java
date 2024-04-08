package pze.business.datentransfer;


import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;

import framework.FW;
import framework.ui.messagebox.MessageBox;

public class FileHandler {
	
	
	/**
	 * Dateiauswahl-Dialog für Excel-Dateien anzeigen
	 * 
	 * @param filename voreingestellter Dateiname
	 * @return
	 */
	public static String showExcelFileDialog(String filename){
		String [] extensions = {"*.xls",  "*.*" };
		String [] names = { "Excel-Dateien [*.xls]", "Alle Dateien [*.*]" };
		String endung = ".xls";
		
		return showFileDialog(extensions, names, endung, filename);
	}


	/**
	 * Dateiauswahl-Dialog für PDF-Dateien anzeigen
	 * 
	 * @param filename voreingestellter Dateiname
	 * @return
	 */
	public static String showPdfFileDialog(String filename){
		String [] extensions = {"*.pdf",  "*.*" };
		String [] names = { "PDF-Dateien [*.pdf]", "Alle Dateien [*.*]" };
		String endung = ".pdf";
		
		return showFileDialog(extensions, names, endung, filename);
	}


	/**
	 * Dateiauswahl-Dialog für PNG-Dateien anzeigen
	 * 
	 * @param filename voreingestellter Dateiname
	 * @return
	 */
	public static String showPngFileDialog(String filename){
		String [] extensions = {"*.png",  "*.*" };
		String [] names = { "PNG-Dateien [*.png]", "Alle Dateien [*.*]" };
		String endung = ".png";
		
		return showFileDialog(extensions, names, endung, filename);
	}


	/**
	 * Dateiauswahl-Dialog anzeigen
	 * 
	 * @param extensions Endungen für den FileDialog
	 * @param names Namen für den FileDialog
	 * @param endung zulässige Endung oder null
	 * @param filename voreingestellter Dateiname
	 * @return
	 */
	private static String showFileDialog(String[] extensions, String[] names, String endung, String filename) {
		FileDialog fd = createFileDialog(extensions, names, filename);

		String path = fd.open();
		if (path == null)
		{
			return null;
		}
		
		path = validateFilename(path, endung);
		if (!validateFileExists(path))
		{
			return showFileDialog(extensions, names, endung, filename);
		}
		
		return path;
	}


	/**
	 * FileDialog erstellen
	 * 
	 * @param extensions Endungen für den FileDialog
	 * @param names Namen für den FileDialog
	 * @param filename voreingestellter Dateiname
	 * @return FileDialog
	 */
	private static FileDialog createFileDialog(String[] extensions, String[] names, String filename) {
		FileDialog fd = new FileDialog(Display.getCurrent().getActiveShell(), SWT.OPEN);
		fd.setText("Dateiauswahl");
		
		fd.setFilterExtensions(extensions);
		fd.setFilterNames(names);
		
		fd.setFileName(filename);
		return fd;
	}

	
	/**
	 * Endung der Datei prüfen und ggf. anhängen, Sonderzeichen entfernen/ersetzen
	 * 
	 * @param filename
	 * @param endung
	 * @return validierter Dateiname
	 */
	public static String validateFilename(String filename, String endung) {

		// Sonderzeichen aus dem Dateinamen entfernen
		filename = filename.replace("&", "_u_");
		filename = filename.replace("*", "_");
		
		// Endung anhängen, falls sie nicht angegeben wurde
		if (!filename.endsWith(endung))
		{
			filename += endung;
		}
		
		return filename;
	}

	
	/**
	 * Prüfe ob Datei bereits existiert bzw. überschrieben werden kann
	 * 
	 * @param filename
	 * @return Dateiname ok
	 */
	private static boolean validateFileExists(String filename) {

		if (new File(filename).exists())
		{
			if (MessageBox.show("Warnung", "Die Datei existiert bereits. Soll die Datei überschrieben werden?", FW.YES | FW.NO) == FW.NO)
			{
				return false;
			}
		}

		return true;
	}

	
	/**
	 * Datei öffnen
	 * 
	 * @param path	kompletter Pfad der Datei
	 * @throws Exception
	 */
	public static void openFile(String path) throws Exception {
		File file = new File(path);
		
		if (!file.exists() || !file.canRead())
		{
			MessageBox.show("Fehler beim Öffnen der Datei", "Die ausgewählte Datei konnte nicht geöffnet werden.");
		}
		else
		{
			Runtime.getRuntime().exec( new String[] { "cmd.exe", "/C",  path});
		}
	}

}
