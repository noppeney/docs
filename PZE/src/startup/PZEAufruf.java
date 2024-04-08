package startup;
import java.io.IOException;

import javax.swing.JOptionPane;


/**
 * Klasse zum Aufrufen des Programms PZE (jar-Datei) abhängig davon, ob Java 32-Bit oder 64-Bit installiert ist.
 * 
 * @author Lisiecki/Wallenfang
 */
public class PZEAufruf

{
	/** Dateiname der jar-Datei, die bei 32-Bit Java ausgeführt wird. */
	public static final String PZE_32 = "PZE_32";
	
	/** Dateiname der jar-Datei, die bei 64-Bit Java ausgeführt wird. */
	public static final String PZE_64 = "PZE_64";
	
	
	
	public static void main(String[] args) throws IOException, InterruptedException
	{
		//Die auszuführenden Programme müssen in workDir liegen,
		//hier das Verzeichnis, in dem sich JavaVersionCheck befindet
		String workDir = System.getProperty("user.dir");


		if(System.getProperty("sun.arch.data.model").equals("32"))
		{
			Runtime.getRuntime().exec("java -jar " + workDir + "\\" + PZE_32 + ".jar");		
		}
		else if(System.getProperty("sun.arch.data.model").equals("64"))
		{
			Runtime.getRuntime().exec("java -jar " + workDir + "\\" + PZE_64 + ".jar");		
		}
		else
		{
			JOptionPane.showMessageDialog(null, "Es wurde keine gültige Java-Installation gefunden.\n"
					+ "Das Programm kann nicht gestartet werden. Bitte wenden Sie sich an einen Administrator."
					, "Fehler beim Programmstart", JOptionPane.ERROR_MESSAGE);
//			Messages.showErrorMessage("Fehler beim Programmstart", "Es wurde keine gültige Java-Installation gefunden.<br>"
//					+ "Das Programm kann nicht gestartet werden. Bitte wenden Sie sich an einen Administrator.");
		}
	}

	
	// Main Methode, für ein Programm, das den Link auf PZE ändert
//	public static void main(String[] args) throws IOException, InterruptedException
//	{
//		//Die auszuführenden Programme müssen in workDir liegen,
//		//hier das Verzeichnis, in dem sich JavaVersionCheck befindet
//		String workDir = System.getProperty("user.dir");
//
//		String command = workDir + "\\" + "switchlink.bat";
//		
//		// Sleep, weil sonst das Kommando nicht richtig ausgeführt wird
//		Thread.sleep(1000);
//		Runtime.getRuntime().exec(command);
//		Thread.sleep(2000);
//	}


}
