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

import com.l2jbr.gameserver.model.TradeList;
import com.l2jbr.gameserver.model.actor.instance.L2MerchantInstance;
import com.l2jbr.gameserver.model.actor.instance.L2PcInstance;


/**
 * This class ...
 * @version $Revision: 1.2.2.3.2.6 $ $Date: 2005/03/27 15:29:57 $
 */
public class PrivateStoreListSell extends L2GameServerPacket
{
	// private static final String _S__B4_PRIVATEBUYLISTSELL = "[S] 9b PrivateBuyListSell";
	private static final String _S__B4_PRIVATESTORELISTSELL = "[S] 9b PrivateStoreListSell";
	private L2PcInstance _storePlayer;
	private final L2PcInstance _activeChar;
	private final int _playerAdena;
	private final boolean _packageSale;
	private final TradeList.TradeItem[] _items;
	
	// player's private shop
	public PrivateStoreListSell(L2PcInstance player, L2PcInstance storePlayer)
	{
		_activeChar = player;
		_storePlayer = storePlayer;
		_playerAdena = _activeChar.getAdena();
		_items = _storePlayer.getSellList().getItems();
		_packageSale = _storePlayer.getSellList().isPackaged();
	}
	
	// lease shop
	@Deprecated
	public PrivateStoreListSell(L2PcInstance player, L2MerchantInstance storeMerchant)
	{
		_activeChar = player;
		_playerAdena = _activeChar.getAdena();
		_items = _storePlayer.getSellList().getItems();
		_packageSale = _storePlayer.getSellList().isPackaged();
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0x9b);
		writeD(_storePlayer.getObjectId());
		writeD(_packageSale ? 1 : 0);
		writeD(_playerAdena);
		
		writeD(_items.length);
		for (TradeList.TradeItem item : _items)
		{
			writeD(item.getItem().getType2());
			writeD(item.getObjectId());
			writeD(item.getItem().getItemId());
			writeD(item.getCount());
			writeH(0x00);
			writeH(item.getEnchant());
			writeH(0x00);
			writeD(item.getItem().getBodyPart());
			writeD(item.getPrice()); // your price
			writeD(item.getItem().getReferencePrice()); // store price
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.l2jbr.gameserver.serverpackets.ServerBasePacket#getType()
	 */
	@Override
	public String getType()
	{
		return _S__B4_PRIVATESTORELISTSELL;
	}
}
