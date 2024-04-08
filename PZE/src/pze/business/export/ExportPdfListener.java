package pze.business.export;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.xhtmlrenderer.pdf.ITextRenderer;

import framework.business.action.ActionAdapter;
import pze.business.Messages;
import pze.business.Profile;
import pze.business.datentransfer.FileHandler;


/**
 * Listener zur Erstellung einer PDF-Datei
 * 
 * @author Lisiecki
 */
public abstract class ExportPdfListener extends ActionAdapter {


	/*
	 * (non-Javadoc)
	 * 
	 * @see framework.business.action.IActionListener#activate(java.lang.Object)
	 */
	public void activate(Object sender) throws Exception {
		String xhtmlCode;

		// html-Datei erzeugen
		xhtmlCode = createHtmlCode();
		if (xhtmlCode == null)
		{
			return;
		}
//		System.out.println(xhtmlCode);
		
		saveAndOpen(xhtmlCode);
	}


	/**
	 * Datei speichern und öffnen
	 * 
	 * @param xhtmlCode
	 * @throws Exception
	 */
	public void saveAndOpen(String xhtmlCode) throws Exception {
		String filename;
		// pdf-Datei erstellen
		filename = generateFilename();
		if (filename == null)
		{
			return;
		}
		createPdf(xhtmlCode, filename);

		// Datei öffnen
		FileHandler.openFile(filename);
		
		// Pfad speichern
		Profile.setProfileItem(getProfilePathKey(), filename.substring(0, filename.lastIndexOf("\\")));
	}


	/**
	 * Html-Datei als String generieren
	 * 
	 * @return
	 * @throws Exception
	 */
	protected abstract String createHtmlCode() throws Exception;


	/**
	 * Dateinamen bestimmen über FileDialog
	 * 
	 * @return
	 * @throws Exception 
	 */
	private String generateFilename() throws Exception {
		String filename;
		
		filename = getDefaultFilename();
		
		filename = FileHandler.validateFilename(filename, ".pdf");
		
		return FileHandler.showPdfFileDialog(Profile.getProfileItem(getProfilePathKey()) + "\\" + filename);
	}


	/**
	 * Im Profile gespeicherter Pfad
	 * 
	 * @return
	 */
	protected abstract String getProfilePathKey();
	

	/**
	 * Default-Filename beim Öffnen des Dateiauswahlfensters
	 * 
	 * @return
	 * @throws Exception 
	 */
	protected abstract String getDefaultFilename() throws Exception;
	
	
	/**
	 * Erstellt eine PDF-Datei aus dem übergebenen html-String
	 * 
	 * @param htmlString Inhalt der Datei als html-Code
	 * @param filename Zielpfad
	 */
	private static void createPdf(String htmlString, String filename) {
		ITextRenderer renderer;
		FileOutputStream fos;
		
		try
		{
			fos = new FileOutputStream(filename);

			renderer = new ITextRenderer();
			renderer.setDocumentFromString(htmlString);
			renderer.layout();
			renderer.createPDF(fos);
		} 
		catch (FileNotFoundException e) 
		{
			Messages.showErrorMessage("Fehler beim Export", "Die Datei konnte nicht erstellt werden. Möglicherweise ist sie bereits geöffnet.");
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			Messages.showErrorMessage("Fehler beim Export", "Fehler beim Erstellen der Datei.<br>Bitte Administrator kontaktieren.");
		}
//		test
//		PDFConverter pd4ml = new PDFConverter();
//		try
//		{
//			pd4ml.convert(htmlString, filename.substring(0, filename.lastIndexOf("\\")+1), filename.substring(filename.lastIndexOf("\\")+1).replace(".pdf", ""), false);
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//		}
		
		
		
	}

	
	/*
	 * (non-Javadoc)
	 * @see framework.business.action.IActionListener#getEnabled()
	 */
	public boolean getEnabled() {
		boolean reportExists = true;
		
//		ITableControl table = m_ergzusForm.getTable();
//		if (table == null)
//			return false;
//
//		if (table.getData() == null)
//			return false;
//
//		if (table.getData().getRowCount() > 0)
//			return true;

		return reportExists;
	}

}
