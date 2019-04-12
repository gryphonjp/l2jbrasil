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

import com.l2jbr.gameserver.instancemanager.CastleManager;
import com.l2jbr.gameserver.model.L2Object;
import com.l2jbr.gameserver.model.actor.instance.L2PcInstance;
import com.l2jbr.gameserver.model.actor.instance.L2StaticObjectInstance;
import com.l2jbr.gameserver.serverpackets.ActionFailed;
import com.l2jbr.gameserver.serverpackets.ChairSit;


/**
 * This class ...
 * @version $Revision: 1.1.4.3 $ $Date: 2005/03/27 15:29:30 $
 */
public final class ChangeWaitType2 extends L2GameClientPacket
{
	private static final String _C__1D_CHANGEWAITTYPE2 = "[C] 1D ChangeWaitType2";
	
	private boolean _typeStand;
	
	@Override
	protected void readImpl()
	{
		_typeStand = (readD() == 1);
	}
	
	@Override
	protected void runImpl()
	{
		if (getClient() == null)
		{
			return;
		}
		final L2PcInstance player = getClient().getActiveChar();
		if (player == null)
		{
			return;
		}
		
		if (player.isOutOfControl())
		{
			player.sendPacket(new ActionFailed());
			return;
		}
		
		if (player.getMountType() != 0) // prevent sit/stand if you riding
		{
			return;
		}
		
		final L2Object target = player.getTarget();
		if ((target != null) && !player.isSitting() && (target instanceof L2StaticObjectInstance) && (((L2StaticObjectInstance) target).getType() == 1) && (CastleManager.getInstance().getCastle(target) != null) && player.isInsideRadius(target, L2StaticObjectInstance.INTERACTION_DISTANCE, false, false))
		{
			ChairSit cs = new ChairSit(player, ((L2StaticObjectInstance) target).getStaticObjectId());
			player.sendPacket(cs);
			player.sitDown();
			player.broadcastPacket(cs);
		}
		
		if (_typeStand)
		{
			player.standUp();
		}
		else
		{
			player.sitDown();
		}
	}
	
	@Override
	public String getType()
	{
		return _C__1D_CHANGEWAITTYPE2;
	}
}
