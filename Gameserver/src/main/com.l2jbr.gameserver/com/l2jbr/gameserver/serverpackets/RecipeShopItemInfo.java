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
package com.l2jbr.gameserver.serverpackets;

import com.l2jbr.gameserver.model.L2World;
import com.l2jbr.gameserver.model.actor.instance.L2PcInstance;


/**
 * ddddd
 * @version $Revision: 1.1.2.3.2.3 $ $Date: 2005/03/27 15:29:39 $
 */
public class RecipeShopItemInfo extends L2GameServerPacket
{
	
	private static final String _S__DA_RecipeShopItemInfo = "[S] da RecipeShopItemInfo";
	private final int _shopId;
	private final int _recipeId;
	
	public RecipeShopItemInfo(int shopId, int recipeId)
	{
		_shopId = shopId;
		_recipeId = recipeId;
	}
	
	@Override
	protected final void writeImpl()
	{
		if (!(L2World.getInstance().findObject(_shopId) instanceof L2PcInstance))
		{
			return;
		}
		
		L2PcInstance manufacturer = (L2PcInstance) L2World.getInstance().findObject(_shopId);
		writeC(0xda);
		writeD(_shopId);
		writeD(_recipeId);
		writeD(manufacturer != null ? (int) manufacturer.getCurrentMp() : 0);
		writeD(manufacturer != null ? manufacturer.getMaxMp() : 0);
		writeD(0xffffffff);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.l2jbr.gameserver.serverpackets.ServerBasePacket#getType()
	 */
	@Override
	public String getType()
	{
		return _S__DA_RecipeShopItemInfo;
	}
}
