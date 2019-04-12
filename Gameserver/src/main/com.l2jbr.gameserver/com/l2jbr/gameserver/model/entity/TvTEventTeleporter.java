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
 * [URL]http://www.gnu.org/copyleft/gpl.html[/URL]
 */
package com.l2jbr.gameserver.model.entity;

import com.l2jbr.commons.Config;
import com.l2jbr.gameserver.ThreadPoolManager;
import com.l2jbr.gameserver.model.L2Effect;
import com.l2jbr.gameserver.model.L2Summon;
import com.l2jbr.gameserver.model.actor.instance.L2PcInstance;


public class TvTEventTeleporter implements Runnable
{
	/** The instance of the player to teleport */
	private final L2PcInstance _playerInstance;
	/** Coordinates of the spot to teleport to */
	private int[] _coordinates = new int[3];
	/** Admin removed this player from event */
	private final boolean _adminRemove;
	
	/**
	 * Initialize the teleporter and start the delayed task
	 * @param playerInstance
	 * @param coordinates
	 * @param fastSchedule
	 * @param adminRemove
	 */
	public TvTEventTeleporter(L2PcInstance playerInstance, int[] coordinates, boolean fastSchedule, boolean adminRemove)
	{
		_playerInstance = playerInstance;
		_coordinates = coordinates;
		_adminRemove = adminRemove;
		
		// in config as seconds
		long delay = (TvTEvent.isStarted() ? Config.TVT_EVENT_RESPAWN_TELEPORT_DELAY : Config.TVT_EVENT_START_LEAVE_TELEPORT_DELAY) * 1000;
		
		if (fastSchedule)
		{
			delay = 0;
		}
		
		ThreadPoolManager.getInstance().scheduleGeneral(this, delay);
	}
	
	/**
	 * The task method to teleport the player<br>
	 * 1. Unsummon pet if there is one 2. Remove all effects 3. Revive and full heal the player 4. Teleport the player 5. Broadcast status and user info
	 * @see Runnable#run()
	 */
	@Override
	public void run()
	{
		if (_playerInstance == null)
		{
			return;
		}
		
		L2Summon summon = _playerInstance.getPet();
		
		if (summon != null)
		{
			summon.unSummon(_playerInstance);
		}
		
		for (L2Effect effect : _playerInstance.getAllEffects())
		{
			if (effect != null)
			{
				effect.exit();
			}
		}
		
		_playerInstance.doRevive();
		_playerInstance.setCurrentCp(_playerInstance.getMaxCp());
		_playerInstance.setCurrentHp(_playerInstance.getMaxHp());
		_playerInstance.setCurrentMp(_playerInstance.getMaxMp());
		_playerInstance.teleToLocation(_coordinates[0], _coordinates[1], _coordinates[2], false);
		
		if (TvTEvent.isStarted() && !_adminRemove)
		{
			_playerInstance.setTeam(TvTEvent.getParticipantTeamId(_playerInstance.getName()) + 1);
		}
		else
		{
			_playerInstance.setTeam(0);
		}
		
		_playerInstance.broadcastStatusUpdate();
		_playerInstance.broadcastUserInfo();
	}
}
