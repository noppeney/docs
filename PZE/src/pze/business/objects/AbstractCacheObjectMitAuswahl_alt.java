package pze.business.objects;

import framework.business.interfaces.data.IBusinessObject;
import pze.business.Format;

/**
 * CacheObject für Tabellen mit Auswahlfunktion
 * 
 * @author Lisiecki
 *
 */
public abstract class AbstractCacheObjectMitAuswahl_alt extends AbstractCacheObject {

	private static final int COL_AUSGEWAEHLT = 3;

	
	
	/**
	 * Kontruktor
	 * @throws Exception 
	 */
	public AbstractCacheObjectMitAuswahl_alt(String tableResID) throws Exception {
		super(tableResID);
	}

	
	/**
	 * alle Namen aktualisieren
	 * 
	 * @throws Exception 
	 */
	protected void setIDs(int id) throws Exception {
		begin();
		
		
		// keine Einträge vorhanden
		if (!moveFirst())
		{
			return;
		}
		
		// Co durchlaufen und IDs setzen
		do 
		{
			if (getID() == 0)
			{
				setPersonID(id);
				setObjectID(getObjectID());
				setAusgewaehlt(false);

				getCurrentRow().setRowState(IBusinessObject.statusAdded);
			}
		} while (moveNext());
		
		setModified(false);
	}

	
	/**
	 * ID des Objects, über die Referenztabelle bestimmt
	 * 
	 * @return
	 * @throws Exception 
	 */
	protected abstract int getObjectID() throws Exception;

	
	/**
	 * ID setzen
	 * 
	 * @param objectID
	 */
	protected abstract void setObjectID(int objectID);


	public boolean isAusgewaehlt() {
		return Format.getBooleanValue(getField("field." + getTableName() + ".ausgewaehlt").getValue());
	}

	
	/**
	 * aktuellen Eintrag auswählen
	 * 
	 * @param ausgewaehlt Eintrag auswählen
	 */
	public void setAusgewaehlt(boolean ausgewaehlt) {
		getField("field." + getTableName() + ".ausgewaehlt").setValue(ausgewaehlt);
	}
	

	public void add(int auswertungAktivitaetID, int objectID) throws Exception {
		if (!isEditing())
		{
			begin();
		}
		
		add();
		setPersonID(auswertungAktivitaetID);
		setObjectID(objectID);
	}


	/**
	 * alle oder keinen Eintrag markieren, je nachdem wie die aktuelle Auswahl ist
	 */
	public void selectAll() {
		selectAll(!allSelected());
	}


	/**
	 * alle Felder ausgewählt
	 * 
	 * @return
	 */
	public boolean allSelected() {
		if (moveFirst())
		{
			do 
			{
				if (!isAusgewaehlt())
				{
					return false;
				}
			} while (moveNext());
		}
		
		return true;
	}


	/**
	 * alle oder keinen Eintrag markieren
	 * 
	 * @param auswaehlen Einträge auswählen
	 */
	public void selectAll(boolean auswaehlen) {
		if (moveFirst())
		{
			do 
			{
				setAusgewaehlt(auswaehlen);
			} while (moveNext());
		}
	}
	
	
	/**
	 * Ausgewählte Werte als Komma-getrennter String
	 * 
	 * @return ausgewählte Werte
	 */
//	public String getSelectedValues() {
//		String values = null;
//		
//		if (moveFirst())
//		{
//			values = "";
//			
//			do 
//			{
//				if (isAusgewaehlt())
//				{
//					values += "'" + getField(1).getDisplayValue() + "', ";
//				}
//			} while (moveNext());
//			
//			if (!values.isEmpty())
//			{
//				values = values.substring(0, values.length()-2);
//			}
//			else
//			{
//				values = "''";
//			}
//		}
//		
//		return values;
//	}

	
	/**
	 * Ausgewählte IDs als Komma-getrennter String
	 * 
	 * @return ausgewählte I
	 * @throws Exception 
	 */
	public String getSelectedIDs() throws Exception {
		String values = null;
		
		if (moveFirst() && getField(COL_AUSGEWAEHLT) != null)
		{
			values = "";
			
			do 
			{
				if (isAusgewaehlt())
				{
					values += getObjectID() + ", ";
				}
			} while (moveNext());
			
			values = listetoString(values);
		}
		
		return values;
	}

	
	/**
	 * alle IDs als Komma-getrennter String
	 * 
	 * @return 
	 * @throws Exception 
	 */
	@Override
	public String getIDs() throws Exception {
		String values = null;
		
		if (moveFirst())
		{
			values = "";
			
			do 
			{
				values += getObjectID() + ", ";
			} while (moveNext());
			
			values = listetoString(values);
		}
		
		return values;
	}


}
