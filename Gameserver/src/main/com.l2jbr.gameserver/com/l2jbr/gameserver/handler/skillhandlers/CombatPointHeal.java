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
package com.l2jbr.gameserver.handler.skillhandlers;

import com.l2jbr.gameserver.handler.ISkillHandler;
import com.l2jbr.gameserver.model.L2Character;
import com.l2jbr.gameserver.model.L2Object;
import com.l2jbr.gameserver.model.L2Skill;
import com.l2jbr.gameserver.model.L2Skill.SkillType;
import com.l2jbr.gameserver.network.SystemMessageId;
import com.l2jbr.gameserver.serverpackets.StatusUpdate;
import com.l2jbr.gameserver.serverpackets.SystemMessage;


/**
 * This class ...
 * @version $Revision: 1.1.2.2.2.1 $ $Date: 2005/03/02 15:38:36 $
 */

public class CombatPointHeal implements ISkillHandler
{
	// private static Logger _log = LoggerFactory.getLogger(CombatPointHeal.class.getName());
	
	/*
	 * (non-Javadoc)
	 * @see com.l2jbr.gameserver.handler.IItemHandler#useItem(com.l2jbr.gameserver.model.L2PcInstance, com.l2jbr.gameserver.model.L2ItemInstance)
	 */
	private static final SkillType[] SKILL_IDS =
	{
		SkillType.COMBATPOINTHEAL
	};
	
	/*
	 * (non-Javadoc)
	 * @see com.l2jbr.gameserver.handler.IItemHandler#useItem(com.l2jbr.gameserver.model.L2PcInstance, com.l2jbr.gameserver.model.L2ItemInstance)
	 */
	@Override
	public void useSkill(L2Character actChar, L2Skill skill, L2Object[] targets)
	{
		// L2Character activeChar = actChar;
		
		L2Character target = null;
		
		for (L2Object target2 : targets)
		{
			target = (L2Character) target2;
			
			double cp = skill.getPower();
			// int cLev = activeChar.getLevel();
			// hp += skill.getPower()/*+(Math.sqrt(cLev)*cLev)+cLev*/;
			SystemMessage sm = new SystemMessage(SystemMessageId.S1_CP_WILL_BE_RESTORED);
			sm.addNumber((int) cp);
			target.sendPacket(sm);
			target.setCurrentCp(cp + target.getCurrentCp());
			StatusUpdate sump = new StatusUpdate(target.getObjectId());
			sump.addAttribute(StatusUpdate.CUR_CP, (int) target.getCurrentCp());
			target.sendPacket(sump);
		}
	}
	
	@Override
	public SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}