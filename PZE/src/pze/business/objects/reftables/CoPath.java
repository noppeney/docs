package pze.business.objects.reftables;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import pze.business.Messages;
import pze.business.objects.AbstractCacheObject;

/**
 * Cacheobject für den Pfad der Dokumente.
 * Ist auch für das Laden und Anzeigen von Dokumenten zuständig
 * 
 * @author Lisiecki
 *
 */
public class CoPath extends AbstractCacheObject {

	
	/**
	 * Konstruktor
	 */
	public CoPath() {
		super("table.rtblpath");
	}
	
	
	/**
	 * Datei mit eindeutigem Namen hochladen
	 * 
	 * @param file	Dateiname
	 * @return	kopierter Datei
	 * @throws Exception
	 */
	public String upload(String file) throws Exception
	{
		File source = new File(file);
		String dest = getDokumentPath();
		UUID uuid =  UUID.randomUUID();
		dest += uuid.toString();
		new File(dest).mkdirs(); 
		dest += File.separator+source.getName();
		streamcopy(file, dest);
		new File(dest).setWritable(false);
		return uuid.toString()+File.separator+source.getName();
	}


	/**
	 * @return den Konfigurierten Root-Pfad
	 * @throws Exception
	 */
	public String getDokumentPath() throws Exception {
		
		// Pfad laden und prüfen ob er eindeutig ist
		loadAll();
		if (getRowCount() != 1)
		{
			Messages.showErrorMessage("Der Dokumentenpfad ist nicht eindeutig.<br>Es sind " + getRowCount() + " Pfade angegeben.<br>" +
					"Dies kann ggf. zu weiteren Fehlern führen.");
		}
		
		moveFirst();
		String dest = getField("field.rtblpath.path").getStringValue();
		if (!dest.endsWith(File.separator))
			dest += File.separator;
		return dest;
	}
	
	/**
	 * Datei öffnen
	 * 
	 * @param filename	Dateiname
	 * @throws Exception
	 */
	public void open(String filename) throws Exception {
		String datei = getDokumentPath()+filename;

		openFile(datei);
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
			Messages.showErrorMessage("Fehler beim Öffnen der Datei", "Die ausgewählte Datei konnte nicht geöffnet werden.<br>" +
					"Ggf. wurde der Dateipfad für die Dokumente des Programms geändert oder Sie besitzen keine Zugriffsberechtigung.");
		}
		else
		{
			Runtime.getRuntime().exec( new String[] { "cmd.exe", "/C",  path});
		}
	}
	
	/**
	 * Resource aus Package in Datei kopieren
	 * 
	 * @param pathfrom		von Datei 
	 * @param destfile		in Datei
	 * @throws Exception	
	 */
	public static void streamcopy(String pathfrom, String destfile) throws Exception 
	{
		streamcopy( new FileInputStream(pathfrom), destfile );
	}	



/**
	 * Resource aus Package in Datei kopieren
	 * 
	 * @param in			Input-Stream
	 * @param destfile		Zieldatei
	 * @throws Exception
	 */
	public static void streamcopy(InputStream in, String destfile) throws Exception {

		try
		{
			int len;
			OutputStream out = null;
			byte[] buf = new byte[20240];

			try
			{
				out = new FileOutputStream(destfile);			 

				while((len = in.read(buf)) > 0)
				{
					out.write(buf, 0, len);
				}
			}
			finally
			{
				in.close();
				if (out!=null)
					out.close();
			}
		}
		catch(Exception e)
		{
			throw new Exception( "Error copiing resource to destination '" + destfile + 
					"'" + ((e.getMessage()!=null)?"<br>" + e.getMessage():"") 
					+ ". Die Quelle ist nicht vorhanden oder die Zieldatei ist möglicherweise gesperrt.");
		}

	}
}
