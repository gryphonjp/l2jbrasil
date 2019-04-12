/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jbr.gameserver.handler.admincommandhandlers;

import com.l2jbr.commons.Config;
import com.l2jbr.gameserver.handler.IAdminCommandHandler;
import com.l2jbr.gameserver.model.GMAudit;
import com.l2jbr.gameserver.model.L2Object;
import com.l2jbr.gameserver.model.actor.instance.L2PcInstance;
import com.l2jbr.gameserver.model.actor.instance.L2PlayableInstance;
import com.l2jbr.gameserver.model.base.Experience;
import com.l2jbr.gameserver.network.SystemMessageId;
import com.l2jbr.gameserver.serverpackets.SystemMessage;

import java.util.StringTokenizer;


public class AdminLevel implements IAdminCommandHandler
{
	private static final int REQUIRED_LEVEL = Config.GM_CHAR_EDIT;
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_add_level",
		"admin_set_level"
	};
	
	/*
	 * (non-Javadoc)
	 * @see com.l2jbr.gameserver.handler.IAdminCommandHandler#useAdminCommand(java.lang.String, com.l2jbr.gameserver.model.L2PcInstance)
	 */
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		if (activeChar == null)
		{
			return false;
		}
		
		if (!Config.ALT_PRIVILEGES_ADMIN)
		{
			if (activeChar.getAccessLevel() < REQUIRED_LEVEL)
			{
				return false;
			}
		}
		
		L2Object targetChar = activeChar.getTarget();
		String target = (targetChar != null ? targetChar.getName() : "no-target");
		GMAudit.auditGMAction(activeChar.getName(), command, target, "");
		
		StringTokenizer st = new StringTokenizer(command, " ");
		String actualCommand = st.nextToken(); // Get actual command
		
		String val = "";
		if (st.countTokens() >= 1)
		{
			val = st.nextToken();
		}
		
		if (actualCommand.equalsIgnoreCase("admin_add_level"))
		{
			try
			{
				if (targetChar instanceof L2PlayableInstance)
				{
					((L2PlayableInstance) targetChar).getStat().addLevel(Byte.parseByte(val));
				}
			}
			catch (NumberFormatException e)
			{
				activeChar.sendMessage("Wrong Number Format");
			}
		}
		else if (actualCommand.equalsIgnoreCase("admin_set_level"))
		{
			try
			{
				if ((targetChar == null) || !(targetChar instanceof L2PcInstance))
				{
					activeChar.sendPacket(new SystemMessage(SystemMessageId.TARGET_IS_INCORRECT)); // incorrect target!
					return false;
				}
				L2PcInstance targetPlayer = (L2PcInstance) targetChar;
				
				byte lvl = Byte.parseByte(val);
				if ((lvl >= 1) && (lvl <= Experience.MAX_LEVEL))
				{
					long pXp = targetPlayer.getExp();
					long tXp = Experience.LEVEL[lvl];
					
					if (pXp > tXp)
					{
						targetPlayer.removeExpAndSp(pXp - tXp, 0);
					}
					else if (pXp < tXp)
					{
						targetPlayer.addExpAndSp(tXp - pXp, 0);
					}
				}
				else
				{
					activeChar.sendMessage("You must specify level between 1 and " + Experience.MAX_LEVEL + ".");
					return false;
				}
			}
			catch (NumberFormatException e)
			{
				activeChar.sendMessage("You must specify level between 1 and " + Experience.MAX_LEVEL + ".");
				return false;
			}
		}
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.l2jbr.gameserver.handler.IAdminCommandHandler#getAdminCommandList()
	 */
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
	
}
