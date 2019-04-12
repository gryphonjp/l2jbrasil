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

package com.l2jbr.gameserver.handler.usercommandhandlers;

import com.l2jbr.gameserver.handler.IUserCommandHandler;
import com.l2jbr.gameserver.model.actor.instance.L2PcInstance;
import com.l2jbr.gameserver.serverpackets.NpcHtmlMessage;


/**
 * Support for clan penalty user command.
 *
 * @author Tempy
 */
public class ClanPenalty implements IUserCommandHandler {
    private static final int[] COMMAND_IDS =
            {
                    100
            };

    /*
     * (non-Javadoc)
     * @see com.l2jbr.gameserver.handler.IUserCommandHandler#useUserCommand(int, com.l2jbr.gameserver.model.L2PcInstance)
     */
    @Override
    public boolean useUserCommand(int id, L2PcInstance activeChar) {
        if (id != COMMAND_IDS[0]) {
            return false;
        }

        String penaltyStr = "No current penalties in effect.";

        StringBuilder htmlContent = new StringBuilder("<html><body>");
        htmlContent.append("<center><table width=\"270\" border=\"0\" bgcolor=\"111111\">");
        htmlContent.append("<tr><td width=\"170\">Penalty</td>");
        htmlContent.append("<td width=\"100\" align=\"center\">Expiration Date</td></tr>");
        htmlContent.append("</table><table width=\"270\" border=\"0\">");
        htmlContent.append("<tr><td>" + penaltyStr + "</td></tr>");
        htmlContent.append("</table></center>");
        htmlContent.append("</body></html>");

        NpcHtmlMessage penaltyHtml = new NpcHtmlMessage(0);
        penaltyHtml.setHtml(htmlContent.toString());
        activeChar.sendPacket(penaltyHtml);

        return true;
    }

    /*
     * (non-Javadoc)
     * @see com.l2jbr.gameserver.handler.IUserCommandHandler#getUserCommandList()
     */
    @Override
    public int[] getUserCommandList() {
        return COMMAND_IDS;
    }
}
