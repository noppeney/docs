package pze.statusinfo;

import org.eclipse.swt.widgets.Display;

import framework.Application;
import framework.business.statusinfo.StatusInfo;
import framework.ui.statusinfo.StatusPad;

public class Statusinfo {

	private static Object m_statusInfo;

	private static boolean m_isAbgebrochen;

	
	
	/**
	 * Starten eines StatusFensters
	 * 
	 * @param beschreibung
	 * @param anzTicks
	 */
	public static void startStatusInfoOhneAbbruchButton(final String beschreibung, final int anzTicks) {
		
		Application.getMainFrame().setWaitCursor(true);

		StatusInfo.openStatus(0);
//		StatusInfo.setPaneText(0, beschreibung);
		StatusInfo.beginTask(beschreibung, anzTicks);

		m_isAbgebrochen = false;
		m_statusInfo = StatusInfo.getInstance();
	}
	

	/**
	 * Starten eines StatusFensters
	 * 
	 * @param beschreibung
	 * @param anzTicks
	 */
	public static void startStatusInfoMitAbbruchButton(final String beschreibung, final int anzTicks) {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				Application.getMainFrame().setWaitCursor(true);
				LCStatusInfo.openStatusMitAbbruchButton(1);
				LCStatusInfo.setPaneText(0,"Optimierung...");
				LCStatusInfo.beginTask(beschreibung, anzTicks);
				LCStatusInfo.tick();
			}
		});
		
		m_isAbgebrochen = false;
		m_statusInfo = LCStatusInfo.getInstance();
	}
	
	
	/**
	 * Neustart eines StatusFensters
	 * 
	 * @param beschreibung
	 * @param anzTicks
	 */
	public static void restartStatusInfo(final String beschreibung, final int anzTicks) {
		if (m_statusInfo instanceof LCStatusPad)
		{
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					LCStatusInfo.done();
					LCStatusInfo.setPaneText(0,"Optimierung...");
					LCStatusInfo.beginTask(beschreibung, anzTicks);
					LCStatusInfo.tick();
				}
			});
		}
		else if (m_statusInfo instanceof StatusPad)
		{
			StatusInfo.done();
//			StatusInfo.setPaneText(0, beschreibung);
			StatusInfo.beginTask(beschreibung, anzTicks);
		}
	}
	

	/**
	 * Stoppen des StatusFensters
	 * 
	 */
	public static void stoppStatusInfo() {
		if (m_statusInfo instanceof LCStatusPad)
		{
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					LCStatusInfo.done();
					LCStatusInfo.closeStatus();
					Application.getMainFrame().setWaitCursor(false);
				}
			});
		}
		else if (m_statusInfo instanceof StatusPad)
		{
			StatusInfo.tick();
			StatusInfo.done();
			StatusInfo.closeStatus();
			Application.getMainFrame().setWaitCursor(false);
		}
	}


	public static void setTextStatusInfo(final String text, final int pane) {
		if (m_statusInfo instanceof LCStatusPad)
		{
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					LCStatusInfo.setPaneText(pane, text);
				}
			});
		}
		else if (m_statusInfo instanceof StatusPad)
		{
			StatusInfo.tick();
			StatusInfo.setPaneText(pane, text);
		}
	}


	public static void tickStatusInfo() {
		if (m_statusInfo instanceof LCStatusPad)
		{
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					LCStatusInfo.tick();
				}
			});
		}
		else if (m_statusInfo instanceof StatusPad)
		{
			StatusInfo.tick();
		}
	}


	public static boolean isAbgebrochen() {
		return m_isAbgebrochen;
	}


	public static void setAbgebrochen(boolean isAbgebrochen) {
		Statusinfo.m_isAbgebrochen = isAbgebrochen;
	}


}
