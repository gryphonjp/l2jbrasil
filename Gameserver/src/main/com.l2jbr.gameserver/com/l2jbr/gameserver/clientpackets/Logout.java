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
import com.l2jbr.commons.L2DatabaseFactory;
import com.l2jbr.gameserver.Olympiad;
import com.l2jbr.gameserver.SevenSignsFestival;
import com.l2jbr.gameserver.communitybbs.Manager.RegionBBSManager;
import com.l2jbr.gameserver.datatables.SkillTable;
import com.l2jbr.gameserver.model.L2Party;
import com.l2jbr.gameserver.model.L2World;
import com.l2jbr.gameserver.model.actor.instance.L2PcInstance;
import com.l2jbr.gameserver.model.entity.TvTEvent;
import com.l2jbr.gameserver.network.SystemMessageId;
import com.l2jbr.gameserver.serverpackets.ActionFailed;
import com.l2jbr.gameserver.serverpackets.FriendList;
import com.l2jbr.gameserver.serverpackets.SystemMessage;
import com.l2jbr.gameserver.taskmanager.AttackStanceTaskManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


/**
 * This class ...
 * @version $Revision: 1.9.4.3 $ $Date: 2005/03/27 15:29:30 $
 */
public final class Logout extends L2GameClientPacket
{
	private static final String _C__09_LOGOUT = "[C] 09 Logout";
	private static Logger _log = LoggerFactory.getLogger(Logout.class.getName());
	
	@Override
	protected void readImpl()
	{
		
	}
	
	@Override
	protected void runImpl()
	{
		// Dont allow leaving if player is fighting
		L2PcInstance player = getClient().getActiveChar();
		
		if (player == null)
		{
			return;
		}
		
		player.getInventory().updateDatabase();
		
		if (AttackStanceTaskManager.getInstance().getAttackStanceTask(player))
		{
			if (Config.DEBUG)
			{
				_log.debug("Player " + player.getName() + " tried to logout while fighting");
			}
			
			player.sendPacket(new SystemMessage(SystemMessageId.CANT_LOGOUT_WHILE_FIGHTING));
			player.sendPacket(new ActionFailed());
			return;
		}
		
		if (player.atEvent)
		{
			player.sendPacket(SystemMessage.sendString("A superior power doesn't allow you to leave the event"));
			return;
		}
		
		if (player.isInOlympiadMode() || Olympiad.getInstance().isRegistered(player))
		{
			player.sendMessage("You cant logout in olympiad mode");
			return;
		}
		
		// Prevent player from logging out if they are a festival participant
		// and it is in progress, otherwise notify party members that the player
		// is not longer a participant.
		if (player.isFestivalParticipant())
		{
			if (SevenSignsFestival.getInstance().isFestivalInitialized())
			{
				player.sendMessage("You cannot log out while you are a participant in a festival.");
				return;
			}
			L2Party playerParty = player.getParty();
			
			if (playerParty != null)
			{
				player.getParty().broadcastToPartyMembers(SystemMessage.sendString(player.getName() + " has been removed from the upcoming festival."));
			}
		}
		if (player.isFlying())
		{
			player.removeSkill(SkillTable.getInstance().getInfo(4289, 1));
		}
		
		TvTEvent.onLogout(player);
		RegionBBSManager.getInstance().changeCommunityBoard();
		
		player.deleteMe();
		notifyFriends(player);
	}
	
	private void notifyFriends(L2PcInstance cha)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			 PreparedStatement statement = con.prepareStatement("SELECT friend_name FROM character_friends WHERE char_id=?"))
		{
			statement.setInt(1, cha.getObjectId());
			try (ResultSet rset = statement.executeQuery())
			{
				while (rset.next())
				{
					String friendName = rset.getString("friend_name");
					L2PcInstance friend = L2World.getInstance().getPlayer(friendName);
					
					if (friend != null) // friend logged in.
					{
						friend.sendPacket(new FriendList(friend));
					}
				}
			}
		}
		catch (Exception e)
		{
			_log.warn("could not restore friend data:" + e);
		}
	}
	
	@Override
	public String getType()
	{
		return _C__09_LOGOUT;
	}
}