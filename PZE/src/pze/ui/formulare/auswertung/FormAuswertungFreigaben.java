package pze.ui.formulare.auswertung;

import framework.business.interfaces.fields.IField;
import framework.ui.interfaces.controls.IControl;
import pze.business.Profile;
import pze.business.objects.auswertung.CoAuswertung;
import pze.business.objects.auswertung.CoAuswertungFreigabe;
import pze.business.objects.personen.CoBuchung;
import pze.ui.controls.SortedTableControl;
import pze.ui.formulare.person.FormPerson;

/**
 * Formular für die Auswertung der Freigaben
 * 
 * @author Lisiecki
 *
 */
public class FormAuswertungFreigaben extends FormAuswertung {
	
	public static String RESID = "form.auswertung.freigaben";

	
	/**
	 * Konstruktor
	 * 
	 * @param parent		visueller Parent
	 * @throws Exception
	 */
	public FormAuswertungFreigaben(Object parent) throws Exception {
		super(parent, RESID);
	}


	@Override
	protected void initFormular() throws Exception {
		super.initFormular();
	}


	/**
	 * Person beim Doppelklick öffnen
	 * 
	 * @see pze.ui.formulare.auswertung.FormAuswertung#initTable()
	 */
	@Override
	protected void initTable() throws Exception {
		m_table = new SortedTableControl(findControl("spread.auswertung.freigaben")){

			@Override
			public void tableDefaultSelected(IControl arg0, Object arg1) throws Exception {
				FormPerson formPerson;

				formPerson = FormPerson.open(getSession(), null, ((CoBuchung) m_co).getPersonID());
				if (formPerson != null)
				{
					formPerson.showZeiterfassung(m_co.getDatum());
				}
			}

			@Override
			protected void tableDataChanged(Object bookmark, IField fld) throws Exception {
			}
		};

	}



	@Override
	protected void loadCo() throws Exception {
		m_co = new CoBuchung();
		((CoBuchung)m_co).loadAntraege((CoAuswertungFreigabe) m_coAuswertung);

//		addExcelExportListener(m_co, m_table, getDefaultExportName(), getProfilePathKey());
	}

	
	@Override
	protected CoAuswertung createCoAuswertung() throws Exception {
		return new CoAuswertungFreigabe();
	}

	
	/**
	 * @param id Primärschlüssel
	 * @return Item Key im Tabfolder
	 */
	public static String getKey(int id) {
		return "auswertung.freigaben." + id;
	}
	
	
	@Override
	public String getKey() {
		return getKey(0);
	}
	

	@Override
	public void activate() {
		// activate wird für alle subForms aufgerufen, wenn der Reiter aktiviert wird, deshalb selection prüfen 
//		if (RESID.equals(((ITabFolder)getParent()).getSelection()))
//		{
//			addExcelExportListener(m_co, m_table, getDefaultExportName(), getProfilePathKey());
//		}
	}
	

	@Override
	public String getDefaultExportName() {
		return "Auswertung_Freigaben" + getCoAuswertung().getStringEinschraenkungDatumPerson();
	}


	@Override
	public String getProfilePathKey() {
		return Profile.KEY_AUSWERTUNG_BUCHHALTUNG;
	}
	

}
