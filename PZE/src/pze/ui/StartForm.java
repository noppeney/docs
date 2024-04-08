package pze.ui;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStream;

import framework.Application;
import framework.business.fields.Field;
import framework.business.interfaces.refresh.IRefreshable;
import framework.business.reporting.TemplateParser;
import framework.business.resources.ResourceMapper;
import framework.business.statusinfo.StatusInfo;
import framework.ui.form.BrowserForm;
import framework.ui.interfaces.controls.ITabFolder;
import framework.ui.interfaces.controls.ITabItem;
import pze.business.Messages;
import startup.PZEStartupAdapter;

/**
 * Startfenster der Anwendung
 * 
 * @author Lisiecki
 */
public class StartForm {

	public StartForm() {
		String itemkey, template, html, filename, programm, datum, version;
		Field fieldVersion, flddate;
		
		itemkey = "start.report";
		ITabFolder tf = Application.getMainFrame().getEditFolder();
		if (tf.setSelection(itemkey))
			return;
		

		// Einlesen des HTML-Reports
		InputStream is = PZEStartupAdapter.class.getResourceAsStream("/templates/start.html");
		template = Application.util.readStream(is);
		TemplateParser report = new TemplateParser();

		// Name der Anwendung
		programm = Application.getCaption();
		
		// Datum aus dem Programmnamen auslesen
		datum = programm.substring(programm.indexOf("("));
		
		// Datum aus dem Namen der Anwendung löschen
		programm = programm.substring(0, programm.indexOf(" ("));
		
		// Version aus Versionsnummer und Datum zusammensetzen
		version = Application.getVersionString() + " " + datum;
		
		
		// Programmname
		fieldVersion = new Field("field.report.programm");
		fieldVersion.setValue(programm.replace("ü", "&uuml;"));

		// Version mit Datum
		flddate = new Field("field.report.version");
		flddate.setValue(version);

		// Felder dem Report zuweisen
		report.add(fieldVersion);
		report.add(flddate);

		
		// Parsen
		html = "";
		try {
			html = report.parse(template);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Ergebnis wegschreiben
		filename = Application.getWorkingDirectory() + "start.html";
		try 
		{
			BufferedWriter out = new BufferedWriter(new FileWriter(filename));
			out.write(html);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Registerkarte öffnen
		try 
		{
			BrowserForm form = new BrowserForm(tf, itemkey);
			form.setCaption("Start PZE");
			form.setUrl(filename);

			ITabItem ti = tf.add("start.report", itemkey, form);
			ti.setBitmap("misc.elements");
			ti.setCaption("Start PZE");

			tf.refresh(IRefreshable.reasonDisabled, null);
			tf.setSelection(itemkey);

		}
		catch (Exception e) {
			String msg = "Fehler bei der Anzeige des Startbildschirms" + ResourceMapper.getInstance().getErrorMessage(e);

			Messages.showErrorMessage(msg);
			return;
		}
		StatusInfo.closeStatus();
		StatusInfo.done();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see framework.business.action.IActionListener#getEnabled()
	 */
	public boolean getEnabled() {
		return false;

	}

}
