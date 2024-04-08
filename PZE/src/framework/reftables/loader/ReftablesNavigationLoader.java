/*
 * updates:
 * 07.04.12 ebb, Erstellung
 */
package framework.reftables.loader;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import framework.business.interfaces.TableType;
import framework.business.interfaces.loader.INodeLoader;
import framework.business.interfaces.nodes.INode;
import framework.business.interfaces.tables.ITableDescription;
import framework.business.nodes.BaseNode;
import framework.business.resources.ResourceMapper;
import pze.business.UserInformation;

/**
 * Loader für die Referenztabellen-Navigation
 * 
 * @author Gerrit Ebbers/Lisiecki
 */
public class ReftablesNavigationLoader implements INodeLoader {

	/*
	 * (non-Javadoc)
	 * @see framework.business.interfaces.loader.INodeLoader#load(framework.business.interfaces.nodes.INode)
	 */
	@Override
	public void load(INode node) throws Exception {
		String tableName;
		ITableDescription tdesc;
		Map<String, ITableDescription> mapTables;
		Iterator<String> iterTablename;
		
		if(node.getParentNode() != null)
			return;
		
		//------- fachliche Referenztabellen
		BaseNode cnode = new BaseNode(TableType.CUSTOMER_REFERENCE_TABLE.toString());
		cnode.setText("Anwender");
		cnode.setBitmap("misc.table");
		node.addChild(cnode);
		
		//------- System Referenztabellen
		BaseNode snode = new BaseNode(TableType.SYSTEM_REFERENCE_TABLE.toString());
		snode.setText("System");
		snode.setBitmap("misc.table");
		node.addChild(snode);
		
		//------- alle Tabellen durchlaufen und zwischenspeichern zum Sortieren
		Iterator<ITableDescription> tdescs = ResourceMapper.getInstance().getTableDescriptions();
		mapTables = new TreeMap<String, ITableDescription>();
		
		while(tdescs.hasNext()) 
		{
			tdesc = tdescs.next();
			mapTables.put(tdesc.getText(), tdesc);
		}
		
		// Tabellen sortiert in Baum anfügen
		iterTablename = mapTables.keySet().iterator();
		while (iterTablename.hasNext())
		{
			tableName = iterTablename.next();
			tdesc = mapTables.get(tableName);
			
			// KGG-User dürfen die entsprechenden Referenztabellen bearbeiten
			if (UserInformation.getInstance().isKGG())
			{
				if (!tableName.equals("Kostenstellen") && !tableName.equals("Berichts-Nr."))
				{
					continue;
				}
			}

			// für Admin-User
			if (tdesc.getTableType() == TableType.CUSTOMER_REFERENCE_TABLE)
			{
				BaseNode bn = new BaseNode(tdesc, false);
				cnode.addChild(bn);
			}
			else if (tdesc.getTableType() == TableType.SYSTEM_REFERENCE_TABLE && UserInformation.getInstance().isEntwickler())
			{
				BaseNode sn = new BaseNode(tdesc, false);
				snode.addChild(sn);
			}
			
		}
		
	}

}
