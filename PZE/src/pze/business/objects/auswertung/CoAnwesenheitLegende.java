package pze.business.objects.auswertung;

import java.util.ArrayList;

import framework.business.interfaces.fields.IField;
import pze.business.objects.AbstractCacheObject;
import pze.business.objects.reftables.buchungen.CoBuchungsart;


/**
 * Klasse zur Anzeige der Legende der Anwesenheitsübersicht
 * 
 * @author Lisiecki
 */
public class CoAnwesenheitLegende extends AbstractCacheObject{

	/**
	 * Speichern der Farben für die Spalten
	 */
	private ArrayList<String> m_alColor;
	
	
	
	/**
	 * Konstruktor
	 * 
	 * @throws Exception
	 */
	public CoAnwesenheitLegende() throws Exception{
		super();
		
		m_alColor = new ArrayList<String>();
		
		createCo();
	}

	
	public void createCo() throws Exception{
	
		setResID("anwesenheit.alle.legende");

		begin();
		
		addField();
		addRows();

		commit();
	}
	

	/**
	 * Feld für die Spalten hinzufügen
	 */
	private void addField() {
		String resid, columnName, columnLabel;


		resid = "field.anwesenheit.legende";

		columnName = "Legende";
		columnLabel = columnName;

		addField(resid, columnName, columnLabel, false);
	}


	/**
	 * Alle Zeilen hinzufügen
	 * 
	 * @throws Exception
	 */
	private void addRows() throws Exception {
		IField field;

		field = getField(0);

		add();
		field.setValue("Anwesend");
		m_alColor.add(CoBuchungsart.COLOR_ANWESEND);

		add();
		field.setValue("ortsflexibles Arbeiten");
		m_alColor.add(CoBuchungsart.COLOR_ORTSFLEX_ARBEITEN);

		add();
		field.setValue("Dienstreise");
		m_alColor.add(CoBuchungsart.COLOR_DIENSTREISE);

		add();
		field.setValue("Dienstgang");
		m_alColor.add(CoBuchungsart.COLOR_DIENSTGANG);

		add();
		field.setValue("Vorlesung");
		m_alColor.add(CoBuchungsart.COLOR_VORLESUNG);

		add();
		field.setValue("Pause");
		m_alColor.add(CoBuchungsart.COLOR_PAUSE);

		add();
		field.setValue("Arbeitsunterbrechung");
		m_alColor.add(CoBuchungsart.COLOR_ARBEITSUNTERBRECHUNG);

		add();
		field.setValue("Private Unterbrechung");
		m_alColor.add(CoBuchungsart.COLOR_PRIVATE_UNTERBRECHUNG);

		add();
		field.setValue("Krank");
		m_alColor.add(CoBuchungsart.COLOR_KRANK);

		add();
		field.setValue("Urlaub");
		m_alColor.add(CoBuchungsart.COLOR_URLAUB);

		add();
		field.setValue("FA");
		m_alColor.add(CoBuchungsart.COLOR_FA);

		add();
		field.setValue("Elternzeit");
		m_alColor.add(CoBuchungsart.COLOR_ELTERNZEIT);

		add();
		field.setValue("Abwesend");
		m_alColor.add(CoBuchungsart.COLOR_ABWESEND);
	}


	/**
	 * Farbe für die Zeile zurückgeben
	 * 
	 * @param row
	 * @return
	 */
	public String getColor(int row) {
		return m_alColor.get(row);
	}


}
