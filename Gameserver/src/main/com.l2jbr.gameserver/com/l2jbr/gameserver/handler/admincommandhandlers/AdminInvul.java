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
package com.l2jbr.gameserver.handler.admincommandhandlers;

import com.l2jbr.commons.Config;
import com.l2jbr.gameserver.handler.IAdminCommandHandler;
import com.l2jbr.gameserver.model.GMAudit;
import com.l2jbr.gameserver.model.L2Object;
import com.l2jbr.gameserver.model.actor.instance.L2PcInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class handles following admin commands: - invul = turns invulnerability on/off
 * @version $Revision: 1.2.4.4 $ $Date: 2007/07/31 10:06:02 $
 */
public class AdminInvul implements IAdminCommandHandler
{
	private static Logger _log = LoggerFactory.getLogger(AdminInvul.class.getName());
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_invul",
		"admin_setinvul"
	};
	private static final int REQUIRED_LEVEL = Config.GM_GODMODE;
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		if (!Config.ALT_PRIVILEGES_ADMIN)
		{
			if (!(checkLevel(activeChar.getAccessLevel()) && activeChar.isGM()))
			{
				return false;
			}
		}
		
		GMAudit.auditGMAction(activeChar.getName(), command, (activeChar.getTarget() != null ? activeChar.getTarget().getName() : "no-target"), "");
		
		if (command.equals("admin_invul"))
		{
			handleInvul(activeChar);
		}
		if (command.equals("admin_setinvul"))
		{
			L2Object target = activeChar.getTarget();
			if (target instanceof L2PcInstance)
			{
				handleInvul((L2PcInstance) target);
			}
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
		return (level >= REQUIRED_LEVEL);
	}
	
	private void handleInvul(L2PcInstance activeChar)
	{
		String text;
		if (activeChar.isInvul())
		{
			activeChar.setIsInvul(false);
			text = activeChar.getName() + " is now mortal";
			if (Config.DEBUG)
			{
				_log.debug("GM: Gm removed invul mode from character " + activeChar.getName() + "(" + activeChar.getObjectId() + ")");
			}
		}
		else
		{
			activeChar.setIsInvul(true);
			text = activeChar.getName() + " is now invulnerable";
			if (Config.DEBUG)
			{
				_log.debug("GM: Gm activated invul mode for character " + activeChar.getName() + "(" + activeChar.getObjectId() + ")");
			}
		}
		activeChar.sendMessage(text);
	}
}
