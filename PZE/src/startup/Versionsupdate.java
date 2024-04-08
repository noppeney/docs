package startup;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;

import javax.swing.JOptionPane;

/**
 * Klasse zum Installieren einer neuen Version des Programms PZE.
 * 
 * @author Lisiecki/Wallenfang
 */
public class Versionsupdate
{
	/** Name des Ordners, in dem sich die neue Version befindet */
	private static final String ORDNER_UPDATE = "neueVersion";
	
	/** Zeit in ms, die nach jedem Kopierversuch gewartet wird bis zum nächsten Versuch */
	private static final long  INTERVALL = 30 * 1000;
	
	
	
	public static void main(String[] args) throws InterruptedException
	{
		File verzeichnisNeueVersion = new File(ORDNER_UPDATE);
		List<File> dateiListe;

		// überpruefen, ob sich Dateien in dem Ordner neueVersion befinden
		// alle Dateien des Ordners "neueVersion" in eine Liste bringen
		dateiListe = Arrays.asList(verzeichnisNeueVersion.listFiles());

		// Alle Dateien dieser Liste in das übergeordnete Verzeichnis verschieben
		// Für jede Datei wird der move Befehl unendlich oft versucht. Das Programm wird beendet, sobald alle Dateien bewegt wurden.
		for (File datei : dateiListe)
		{
			// PZE.jar bleibt drin als Startprogramm für die Testversionen
			if (datei.getName().endsWith("PZE.jar"))
			{
				continue;
			}
			
			while (true)
			{
				try
				{
					Files.move(datei.toPath(), new File(datei.getName()).toPath(), StandardCopyOption.REPLACE_EXISTING);
					break;
				} 
				catch (IOException e)
				{
					// Diese Exception wird geworfen, wenn die Datei in einem anderen Prozess geöffnet ist.
					e.printStackTrace();
				}
				
				// nach einem Zeitintervall wird erneut probiert, die Datei zu überschreiben
				Thread.sleep(INTERVALL);
			}
		}
		
		
		// Erfolgsmeldung
		JOptionPane.showMessageDialog(null, "Die Programmversion wurde aktualisiert. Bitte starten Sie das Programm PZE und die Funktion zum Buchungsupdate neu."
				, "Version aktualisiert", JOptionPane.INFORMATION_MESSAGE);
//		Messages.showInfoMessage("Version aktualisiert", 
//				"Die Programmversion wurde aktualisiert. Bitte starten Sie das Programm PZE und die Funktion zum Buchungsupdate neu.");
	}
}
