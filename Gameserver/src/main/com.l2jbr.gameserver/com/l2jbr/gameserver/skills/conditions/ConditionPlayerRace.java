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
package com.l2jbr.gameserver.skills.conditions;

import com.l2jbr.gameserver.model.actor.instance.L2PcInstance;
import com.l2jbr.gameserver.model.base.Race;
import com.l2jbr.gameserver.skills.Env;


/**
 * @author mkizub TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class ConditionPlayerRace extends Condition
{
	
	private final Race _race;
	
	public ConditionPlayerRace(Race race)
	{
		_race = race;
	}
	
	@Override
	public boolean testImpl(Env env)
	{
		if (!(env.player instanceof L2PcInstance))
		{
			return false;
		}
		return ((L2PcInstance) env.player).getRace() == _race;
	}
}
