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
package com.l2jbr.gameserver.clientpackets;

import com.l2jbr.gameserver.instancemanager.CursedWeaponsManager;
import com.l2jbr.gameserver.model.L2Character;
import com.l2jbr.gameserver.serverpackets.ExCursedWeaponList;

import java.util.LinkedList;
import java.util.List;


/**
 * Format: (ch)
 *
 * @author -Wooden-
 */
public class RequestCursedWeaponList extends L2GameClientPacket {
    private static final String _C__D0_22_REQUESTCURSEDWEAPONLIST = "[C] D0:22 RequestCursedWeaponList";

    @Override
    protected void readImpl() {
        // nothing to read it's just a trigger
    }

    @Override
    protected void runImpl() {
        L2Character activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }

        // send a ExCursedWeaponList :p
        List<Integer> list = new LinkedList<>();
        for (int id : CursedWeaponsManager.getInstance().getCursedWeaponsIds()) {
            list.add(id);
        }
        activeChar.sendPacket(new ExCursedWeaponList(list));
    }

    @Override
    public String getType() {
        return _C__D0_22_REQUESTCURSEDWEAPONLIST;
    }
}
