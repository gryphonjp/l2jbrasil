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
package com.l2jbr.gameserver.handler.admincommandhandlers;

import com.l2jbr.commons.Config;
import com.l2jbr.gameserver.handler.IAdminCommandHandler;
import com.l2jbr.gameserver.model.GMAudit;
import com.l2jbr.gameserver.model.L2Object;
import com.l2jbr.gameserver.model.actor.instance.L2PcInstance;
import com.l2jbr.gameserver.model.entity.TvTEvent;
import com.l2jbr.gameserver.model.entity.TvTEventTeleporter;


/**
 * @author FBIagent The class handles administrator commands for the TvT Engine which was first implemented by FBIagent
 */
public class AdminTvTEvent implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_tvt_add",
		"admin_tvt_remove"
	};
	
	private static final int REQUIRED_LEVEL = Config.GM_MIN;
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance adminInstance)
	{
		if (!Config.ALT_PRIVILEGES_ADMIN)
		{
			if (!(checkLevel(adminInstance.getAccessLevel()) && adminInstance.isGM()))
			{
				return false;
			}
		}
		
		GMAudit.auditGMAction(adminInstance.getName(), command, (adminInstance.getTarget() != null ? adminInstance.getTarget().getName() : "no-target"), "");
		
		if (command.equals("admin_tvt_add"))
		{
			L2Object target = adminInstance.getTarget();
			
			if ((target == null) || !(target instanceof L2PcInstance))
			{
				adminInstance.sendMessage("You should select a player!");
				return true;
			}
			
			add(adminInstance, (L2PcInstance) target);
		}
		else if (command.equals("admin_tvt_remove"))
		{
			L2Object target = adminInstance.getTarget();
			
			if ((target == null) || !(target instanceof L2PcInstance))
			{
				adminInstance.sendMessage("You should select a player!");
				return true;
			}
			
			remove(adminInstance, (L2PcInstance) target);
		}
		
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
	
	private boolean checkLevel(int level)
	{
		return level >= REQUIRED_LEVEL;
	}
	
	private void add(L2PcInstance adminInstance, L2PcInstance playerInstance)
	{
		if (TvTEvent.isPlayerParticipant(playerInstance.getName()))
		{
			adminInstance.sendMessage("Player already participated in the event!");
			return;
		}
		
		if (!TvTEvent.addParticipant(playerInstance))
		{
			adminInstance.sendMessage("Player instance could not be added, it seems to be null!");
			return;
		}
		
		if (TvTEvent.isStarted())
		{
			// we don't need to check return value of TvTEvent.getParticipantTeamCoordinates() for null, TvTEvent.addParticipant() returned true so target is in event
			new TvTEventTeleporter(playerInstance, TvTEvent.getParticipantTeamCoordinates(playerInstance.getName()), true, false);
		}
	}
	
	private void remove(L2PcInstance adminInstance, L2PcInstance playerInstance)
	{
		if (!TvTEvent.removeParticipant(playerInstance.getName()))
		{
			adminInstance.sendMessage("Player is not part of the event!");
			return;
		}
		
		new TvTEventTeleporter(playerInstance, Config.TVT_EVENT_PARTICIPATION_NPC_COORDINATES, true, true);
	}
}
