/* This program is free software; you can redistribute it and/or modify
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


/**
 * Format: ch d[Sdd]
 * @author KenM
 */
public class ExMPCCShowPartyMemberInfo extends L2GameServerPacket
{
	private static final String _S__FE_4A_EXMPCCSHOWPARTYMEMBERINFO = "[S] FE:4A ExMPCCShowPartyMemberInfo";
	
	@Override
	protected void writeImpl()
	{
		writeC(0xfe);
		writeH(0x4a);
		
		// TODO this packet has a list, so im not going to add temp vars ^^
	}
	
	@Override
	public String getType()
	{
		return _S__FE_4A_EXMPCCSHOWPARTYMEMBERINFO;
	}
}
