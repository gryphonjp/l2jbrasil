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


public class ShortBuffStatusUpdate extends L2GameServerPacket
{
	private static final String _S__F4_SHORTBUFFSTATUSUPDATE = "[S] F4 ShortBuffStatusUpdate";
	private final int _skillId;
	private final int _skillLvl;
	private final int _duration;
	
	public ShortBuffStatusUpdate(int skillId, int skillLvl, int duration)
	{
		_skillId = skillId;
		_skillLvl = skillLvl;
		_duration = duration;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0xF4);
		writeD(_skillId);
		writeD(_skillLvl);
		writeD(_duration);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.l2jbr.gameserver.serverpackets.ServerBasePacket#getType()
	 */
	@Override
	public String getType()
	{
		return _S__F4_SHORTBUFFSTATUSUPDATE;
	}
}
