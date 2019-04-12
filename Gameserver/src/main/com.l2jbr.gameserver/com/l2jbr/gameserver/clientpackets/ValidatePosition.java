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

import com.l2jbr.commons.Config;
import com.l2jbr.gameserver.TaskPriority;
import com.l2jbr.gameserver.Universe;
import com.l2jbr.gameserver.geoeditorcon.GeoEditorListener;
import com.l2jbr.gameserver.model.actor.instance.L2PcInstance;
import com.l2jbr.gameserver.serverpackets.PartyMemberPosition;
import com.l2jbr.gameserver.serverpackets.ValidateLocation;
import com.l2jbr.gameserver.serverpackets.ValidateLocationInVehicle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class ...
 * @version $Revision: 1.13.4.7 $ $Date: 2005/03/27 15:29:30 $
 */
public class ValidatePosition extends L2GameClientPacket
{
	private static Logger _log = LoggerFactory.getLogger(ValidatePosition.class.getName());
	private static final String _C__48_VALIDATEPOSITION = "[C] 48 ValidatePosition";
	
	/**
	 * Urgent messages, execute immediately.
	 * @return
	 */
	public TaskPriority getPriority()
	{
		return TaskPriority.PR_HIGH;
	}
	
	private int _x;
	private int _y;
	private int _z;
	private int _heading;
	@SuppressWarnings("unused")
	private int _data;
	
	@Override
	protected void readImpl()
	{
		_x = readD();
		_y = readD();
		_z = readD();
		_heading = readD();
		_data = readD();
	}
	
	@Override
	protected void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if ((activeChar == null) || activeChar.isTeleporting())
		{
			return;
		}
		
		if (Config.COORD_SYNCHRONIZE > 0)
		{
			activeChar.setClientX(_x);
			activeChar.setClientY(_y);
			activeChar.setClientZ(_z);
			activeChar.setClientHeading(_heading);
			int realX = activeChar.getX();
			int realY = activeChar.getY();
			// int realZ = activeChar.getZ();
			
			double dx = _x - realX;
			double dy = _y - realY;
			double diffSq = ((dx * dx) + (dy * dy));
			
			/*
			 * if (Config.DEVELOPER && false) { int dxs = (_x - activeChar._lastClientPosition.x); int dys = (_y - activeChar._lastClientPosition.y); int dist = (int)Math.sqrt(dxs*dxs + dys*dys); int heading = dist > 0 ? (int)(Math.atan2(-dys/dist, -dxs/dist) * 10430.378350470452724949566316381) +
			 * 32768 : 0; System.out.println("Client X:" + _x + ", Y:" + _y + ", Z:" + _z + ", H:" + _heading + ", Dist:" + activeChar.getLastClientDistance(_x, _y, _z)); System.out.println("Server X:" + realX + ", Y:" + realY + ", Z:" + realZ + ", H:" + activeChar.getHeading() + ", Dist:" +
			 * activeChar.getLastServerDistance(realX, realY, realZ)); }
			 */
			
			if ((diffSq > 0) && (diffSq < 250000)) // if too large, messes observation
			{
				if (((Config.COORD_SYNCHRONIZE & 1) == 1) && (!activeChar.isMoving() // character is not moving, take coordinates from client
				|| !activeChar.validateMovementHeading(_heading))) // Heading changed on client = possible obstacle
				{
					if (Config.DEVELOPER)
					{
						System.out.println(activeChar.getName() + ": Synchronizing position Client --> Server" + (activeChar.isMoving() ? " (collision)" : " (stay sync)"));
					}
					if (diffSq < 2500)
					{
						activeChar.setXYZ(realX, realY, _z);
					}
					else
					{
						activeChar.setXYZ(_x, _y, _z);
					}
					activeChar.setHeading(_heading);
				}
				else if (((Config.COORD_SYNCHRONIZE & 2) == 2) && (diffSq > 10000)) // more than can be considered to be result of latency
				{
					if (Config.DEVELOPER)
					{
						System.out.println(activeChar.getName() + ": Synchronizing position Server --> Client");
					}
					if (activeChar.isInBoat())
					{
						sendPacket(new ValidateLocationInVehicle(activeChar));
					}
					else
					{
						activeChar.sendPacket(new ValidateLocation(activeChar));
					}
				}
			}
			activeChar.setLastClientPosition(_x, _y, _z);
			activeChar.setLastServerPosition(activeChar.getX(), activeChar.getY(), activeChar.getZ());
		}
		else if (Config.COORD_SYNCHRONIZE == -1)
		{
			activeChar.setClientX(_x);
			activeChar.setClientY(_y);
			activeChar.setClientZ(_z);
			activeChar.setClientHeading(_heading);
			int realX = activeChar.getX();
			int realY = activeChar.getY();
			int realZ = activeChar.getZ();
			
			double dx = _x - realX;
			double dy = _y - realY;
			double diffSq = ((dx * dx) + (dy * dy));
			if (diffSq < 250000)
			{
				activeChar.setXYZ(realX, realY, _z);
			}
			int realHeading = activeChar.getHeading();
			
			// activeChar.setHeading(_heading);
			
			// TODO: do we need to validate?
			/*
			 * double dx = (_x - realX); double dy = (_y - realY); double dist = Math.sqrt(dx*dx + dy*dy); if ((dist < 500)&&(dist > 2)) //check it wasnt teleportation, and char isn't there yet activeChar.sendPacket(new CharMoveToLocation(activeChar));
			 */
			
			if (Config.DEBUG)
			{
				_log.debug("client pos: " + _x + " " + _y + " " + _z + " head " + _heading);
				_log.debug("server pos: " + realX + " " + realY + " " + realZ + " head " + realHeading);
			}
			
			if (Config.ACTIVATE_POSITION_RECORDER && !activeChar.isFlying() && Universe.getInstance().shouldLog(activeChar.getObjectId()))
			{
				Universe.getInstance().registerHeight(realX, realY, _z);
			}
			
			if (Config.DEVELOPER)
			{
				if (diffSq > 1000000)
				{
					if (Config.DEBUG)
					{
						_log.debug("client/server dist diff " + (int) Math.sqrt(diffSq));
					}
					if (activeChar.isInBoat())
					{
						sendPacket(new ValidateLocationInVehicle(activeChar));
					}
					else
					{
						activeChar.sendPacket(new ValidateLocation(activeChar));
					}
				}
			}
		}
		if (activeChar.getParty() != null)
		{
			activeChar.getParty().broadcastToPartyMembers(activeChar, new PartyMemberPosition(activeChar));
		}
		
		if (Config.ALLOW_WATER)
		{
			activeChar.checkWaterState();
		}
		
		if (Config.ACCEPT_GEOEDITOR_CONN)
		{
			if ((GeoEditorListener.getInstance().getThread() != null) && GeoEditorListener.getInstance().getThread().isWorking() && GeoEditorListener.getInstance().getThread().isSend(activeChar))
			{
				GeoEditorListener.getInstance().getThread().sendGmPosition(_x, _y, (short) _z);
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.l2jbr.gameserver.clientpackets.ClientBasePacket#getType()
	 */
	@Override
	public String getType()
	{
		return _C__48_VALIDATEPOSITION;
	}
	
	@Deprecated
	public boolean equal(ValidatePosition pos)
	{
		return (_x == pos._x) && (_y == pos._y) && (_z == pos._z) && (_heading == pos._heading);
	}
}
