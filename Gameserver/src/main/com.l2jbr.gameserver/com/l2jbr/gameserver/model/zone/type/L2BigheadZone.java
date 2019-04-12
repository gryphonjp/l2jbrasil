/* This program is free software; you can redistribute it and/or modify
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
package com.l2jbr.gameserver.model.zone.type;

import com.l2jbr.gameserver.model.L2Character;
import com.l2jbr.gameserver.model.actor.instance.L2PcInstance;
import com.l2jbr.gameserver.model.zone.L2ZoneType;


/**
 * Bighead zones give entering players big heads
 * @author durgus
 */
public class L2BigheadZone extends L2ZoneType
{
	public L2BigheadZone()
	{
		super();
	}
	
	@Override
	protected void onEnter(L2Character character)
	{
		if (character instanceof L2PcInstance)
		{
			character.startAbnormalEffect(0x2000);
		}
	}
	
	@Override
	protected void onExit(L2Character character)
	{
		if (character instanceof L2PcInstance)
		{
			character.stopAbnormalEffect((short) 0x2000);
		}
	}
	
	@Override
	protected void onDieInside(L2Character character)
	{
		onExit(character);
	}
	
	@Override
	protected void onReviveInside(L2Character character)
	{
		onEnter(character);
	}
	
}
