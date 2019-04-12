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
import com.l2jbr.gameserver.model.L2ItemInstance;
import com.l2jbr.gameserver.model.actor.instance.L2PcInstance;
import com.l2jbr.gameserver.network.SystemMessageId;
import com.l2jbr.gameserver.serverpackets.InventoryUpdate;
import com.l2jbr.gameserver.serverpackets.SystemMessage;
import com.l2jbr.gameserver.templates.L2Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class ...
 * @version $Revision: 1.8.2.3.2.7 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestUnEquipItem extends L2GameClientPacket
{
	private static final String _C__11_REQUESTUNEQUIPITEM = "[C] 11 RequestUnequipItem";
	private static Logger _log = LoggerFactory.getLogger(RequestUnEquipItem.class.getName());
	
	// cd
	private int _slot;
	
	/**
	 * packet type id 0x11 format: cd
	 */
	@Override
	protected void readImpl()
	{
		_slot = readD();
	}
	
	@Override
	protected void runImpl()
	{
		if (Config.DEBUG)
		{
			_log.debug("request unequip slot " + _slot);
		}
		
		L2PcInstance activeChar = getClient().getActiveChar();
		
		if (activeChar == null)
		{
			return;
		}
		
		L2ItemInstance item = activeChar.getInventory().getPaperdollItemByL2ItemId(_slot);
		if ((item != null) && item.isWear())
		{
			// Wear-items are not to be unequipped
			return;
		}
		// Prevent of unequiping a cursed weapon
		if ((_slot == L2Item.SLOT_LR_HAND) && activeChar.isCursedWeaponEquiped())
		{
			// Message ?
			return;
		}
		
		// Prevent player from unequipping items in special conditions
		if (activeChar.isStunned() || activeChar.isSleeping() || activeChar.isParalyzed() || activeChar.isAlikeDead())
		{
			activeChar.sendMessage("Your status does not allow you to do that.");
			return;
		}
		if (activeChar.isAttackingNow() || activeChar.isCastingNow())
		{
			return;
		}
		
		// Remove augmentation boni
		if ((item != null) && item.isAugmented())
		{
			item.getAugmentation().removeBoni(activeChar);
		}
		
		L2ItemInstance[] unequiped = activeChar.getInventory().unEquipItemInBodySlotAndRecord(_slot);
		
		// show the update in the inventory
		InventoryUpdate iu = new InventoryUpdate();
		
		for (L2ItemInstance element : unequiped)
		{
			activeChar.checkSSMatch(null, element);
			
			iu.addModifiedItem(element);
		}
		
		activeChar.sendPacket(iu);
		
		activeChar.abortAttack();
		activeChar.broadcastUserInfo();
		
		// this can be 0 if the user pressed the right mousebutton twice very fast
		if (unequiped.length > 0)
		{
			
			SystemMessage sm = null;
			if (unequiped[0].getEnchantLevel() > 0)
			{
				sm = new SystemMessage(SystemMessageId.EQUIPMENT_S1_S2_REMOVED);
				sm.addNumber(unequiped[0].getEnchantLevel());
				sm.addItemName(unequiped[0].getItemId());
			}
			else
			{
				sm = new SystemMessage(SystemMessageId.S1_DISARMED);
				sm.addItemName(unequiped[0].getItemId());
			}
			activeChar.sendPacket(sm);
			sm = null;
		}
	}
	
	@Override
	public String getType()
	{
		return _C__11_REQUESTUNEQUIPITEM;
	}
}
