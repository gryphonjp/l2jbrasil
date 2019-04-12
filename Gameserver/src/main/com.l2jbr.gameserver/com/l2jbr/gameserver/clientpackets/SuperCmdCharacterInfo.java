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
package com.l2jbr.gameserver.clientpackets;


/**
 * Format chS c: (id) 0x39 h: (subid) 0x00 S: the character name (or maybe cmd string ?)
 * @author -Wooden-
 */
public final class SuperCmdCharacterInfo extends L2GameClientPacket
{
	private static final String _C__39_00_SUPERCMDCHARACTERINFO = "[C] 39:00 SuperCmdCharacterInfo";
	@SuppressWarnings("unused")
	private String _characterName;
	
	@Override
	protected void readImpl()
	{
		_characterName = readS();
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.l2jbr.gameserver.clientpackets.ClientBasePacket#runImpl()
	 */
	@Override
	protected void runImpl()
	{
		// TODO Auto-generated method stub
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.l2jbr.gameserver.BasePacket#getType()
	 */
	@Override
	public String getType()
	{
		return _C__39_00_SUPERCMDCHARACTERINFO;
	}
	
}