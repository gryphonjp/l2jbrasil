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
 * @author Maktakien
 */
public class StopMoveInVehicle extends L2GameServerPacket
{
	
	private final L2PcInstance _activeChar;
	private final int _boatId;
	
	/**
	 * @param player
	 * @param boatid
	 */
	public StopMoveInVehicle(L2PcInstance player, int boatid)
	{
		_activeChar = player;
		_boatId = boatid;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.l2jbr.gameserver.serverpackets.ServerBasePacket#writeImpl()
	 */
	@Override
	protected void writeImpl()
	{
		writeC(0x72);
		writeD(_activeChar.getObjectId());
		writeD(_boatId);
		writeD(_activeChar.getInBoatPosition().getX());
		writeD(_activeChar.getInBoatPosition().getY());
		writeD(_activeChar.getInBoatPosition().getZ());
		writeD(_activeChar.getPosition().getHeading());
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.l2jbr.gameserver.BasePacket#getType()
	 */
	@Override
	public String getType()
	{
		// TODO Auto-generated method stub
		return "[S] 72 StopMoveInVehicle";
	}
	
}
