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
package com.l2jbr.gameserver.model.actor.instance;

import com.l2jbr.gameserver.datatables.HennaTreeTable;
import com.l2jbr.gameserver.model.L2Character;
import com.l2jbr.gameserver.model.L2HennaInstance;
import com.l2jbr.gameserver.serverpackets.HennaEquipList;
import com.l2jbr.gameserver.templates.L2NpcTemplate;


/**
 * This class ...
 *
 * @version $Revision$ $Date$
 */
public class L2SymbolMakerInstance extends L2FolkInstance {
    // private static Logger _log = LoggerFactory.getLogger(L2SymbolMakerInstance.class.getName());

    @Override
    public void onBypassFeedback(L2PcInstance player, String command) {
        if (command.equals("Draw")) {
            L2HennaInstance[] henna = HennaTreeTable.getInstance().getAvailableHenna(player.getClassId());
            HennaEquipList hel = new HennaEquipList(player, henna);
            player.sendPacket(hel);
        } else if (command.equals("RemoveList")) {
            showRemoveChat(player);
        } else if (command.startsWith("Remove ")) {
            int slot = Integer.parseInt(command.substring(7));
            player.removeHenna(slot);
        } else {
            super.onBypassFeedback(player, command);
        }
    }

    private void showRemoveChat(L2PcInstance player) {
        StringBuilder html1 = new StringBuilder("<html><body>");
        html1.append("Select symbol you would like to remove:<br><br>");
        boolean hasHennas = false;

        for (int i = 1; i <= 3; i++) {
            L2HennaInstance henna = player.getHenna(i);

            if (henna != null) {
                hasHennas = true;
                html1.append("<a action=\"bypass -h npc_%objectId%_Remove " + i + "\">" + henna.getName() + "</a><br>");
            }
        }
        if (!hasHennas) {
            html1.append("You don't have any symbol to remove!");
        }
        html1.append("</body></html>");
        insertObjectIdAndShowChatWindow(player, html1.toString());
    }

    public L2SymbolMakerInstance(int objectID, L2NpcTemplate template) {
        super(objectID, template);
    }

    @Override
    public String getHtmlPath(int npcId, int val) {
        return "data/html/symbolmaker/SymbolMaker.htm";
    }

    /*
     * (non-Javadoc)
     * @see com.l2jbr.gameserver.model.L2Object#isAttackable()
     */
    @Override
    public boolean isAutoAttackable(L2Character attacker) {
        return false;
    }
}
