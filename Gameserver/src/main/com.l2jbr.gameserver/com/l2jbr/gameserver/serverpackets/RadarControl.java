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


public class RadarControl extends L2GameServerPacket
{
	private static final String _S__EB_RadarControl = "[S] EB RadarControl";
	private final int _showRadar;
	private final int _type;
	private final int _x;
	private final int _y;
	private final int _z;
	
	/**
	 * 0xEB RadarControl ddddd
	 * @param showRadar
	 * @param type
	 * @param x
	 * @param y
	 * @param z
	 */
	public RadarControl(int showRadar, int type, int x, int y, int z)
	{
		_showRadar = showRadar; // showRader?? 0 = showradar; 1 = delete radar;
		_type = type; // radar type??
		_x = x;
		_y = y;
		_z = z;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0xEB);
		writeD(_showRadar);
		writeD(_type); // maybe type
		writeD(_x); // x
		writeD(_y); // y
		writeD(_z); // z
	}
	
	@Override
	public String getType()
	{
		return _S__EB_RadarControl;
	}
}
