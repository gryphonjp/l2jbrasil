/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package com.l2jbr.gameserver.handler;

import java.util.Map;
import java.util.TreeMap;


/**
 * This class manages handlers of items
 * @version $Revision: 1.1.4.3 $ $Date: 2005/03/27 15:30:09 $
 */
public class ItemHandler
{
	// private static Logger _log = LoggerFactory.getLogger(ItemHandler.class.getName());
	
	private static ItemHandler _instance;
	
	private final Map<Integer, IItemHandler> _datatable;
	
	/**
	 * Create ItemHandler if doesn't exist and returns ItemHandler
	 * @return ItemHandler
	 */
	public static ItemHandler getInstance()
	{
		if (_instance == null)
		{
			_instance = new ItemHandler();
		}
		return _instance;
	}
	
	/**
	 * Returns the number of elements contained in datatable
	 * @return int : Size of the datatable
	 */
	public int size()
	{
		return _datatable.size();
	}
	
	/**
	 * Constructor of ItemHandler
	 */
	private ItemHandler()
	{
		_datatable = new TreeMap<>();
	}
	
	/**
	 * Adds handler of item type in <I>datatable</I>.<BR>
	 * <BR>
	 * <B><I>Concept :</I></U><BR>
	 * This handler is put in <I>datatable</I> Map &lt;Integer ; IItemHandler &gt; for each ID corresponding to an item type (existing in classes of package itemhandlers) sets as key of the Map.
	 * @param handler (IItemHandler)
	 */
	public void registerItemHandler(IItemHandler handler)
	{
		// Get all ID corresponding to the item type of the handler
		int[] ids = handler.getItemIds();
		// Add handler for each ID found
		for (int id : ids)
		{
			_datatable.put(id, handler);
		}
	}
	
	/**
	 * Returns the handler of the item
	 * @param itemId : int designating the itemID
	 * @return IItemHandler
	 */
	public IItemHandler getItemHandler(int itemId)
	{
		return _datatable.get(itemId);
	}
}
