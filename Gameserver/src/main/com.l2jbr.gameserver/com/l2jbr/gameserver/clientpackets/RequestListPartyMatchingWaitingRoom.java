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
 * Format: (ch) this is just a trigger : no data
 * @author -Wooden-
 */
public class RequestListPartyMatchingWaitingRoom extends L2GameClientPacket
{
	private static final String _C__D0_16_REQUESTLISTPARTYMATCHINGWAITINGROOM = "[C] D0:16 RequestListPartyMatchingWaitingRoom";
	
	@Override
	protected void readImpl()
	{
		// trigger
	}
	
	@Override
	protected void runImpl()
	{
		// TODO
		System.out.println("C5: RequestListPartyMatchingWaitingRoom");
	}
	
	@Override
	public String getType()
	{
		return _C__D0_16_REQUESTLISTPARTYMATCHINGWAITINGROOM;
	}
}