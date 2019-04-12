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

import com.l2jbr.gameserver.model.actor.instance.L2BoatInstance;


/**
 * @author Maktakien
 */
public class VehicleInfo extends L2GameServerPacket
{
	private final L2BoatInstance _boat;
	
	/**
	 * @param boat
	 */
	public VehicleInfo(L2BoatInstance boat)
	{
		_boat = boat;
	}
	
	@Override
	protected void writeImpl()
	{
		writeC(0x59);
		writeD(_boat.getObjectId());
		writeD(_boat.getX());
		writeD(_boat.getY());
		writeD(_boat.getZ());
		writeD(_boat.getPosition().getHeading());
		
	}
	
	@Override
	public String getType()
	{
		return "[S] 59 VehicleInfo";
	}
}
