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

import com.l2jbr.gameserver.model.actor.instance.L2PcInstance;


/**
 * This class ...
 * @version $Revision: 1.4.2.1.2.3 $ $Date: 2005/03/27 15:29:57 $
 * @author godson
 */
public class ExOlympiadUserInfo extends L2GameServerPacket
{
	// chcdSddddd
	private static final String _S__FE_29_OLYMPIADUSERINFO = "[S] FE:2C OlympiadUserInfo";
	
	/**
	 * @param player
	 */
	public ExOlympiadUserInfo(L2PcInstance player)
	{
	}
	
	@Override
	protected final void writeImpl()
	{
		// TODO: Implement.
		/*
		 * writeC(0xfe); writeH(0x2c); writeD(_activeChar.getObjectId()); writeS(_activeChar.getName()); writeD(_activeChar.getClassId().getId()); writeD((int)_activeChar.getCurrentHp()); writeD(_activeChar.getMaxHp()); writeD((int)_activeChar.getCurrentCp()); writeD(_activeChar.getMaxCp());
		 */
	}
	
	@Override
	public String getType()
	{
		return _S__FE_29_OLYMPIADUSERINFO;
	}
}
