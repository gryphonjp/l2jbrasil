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
package com.l2jbr.gameserver.datatables;

import com.l2jbr.commons.L2DatabaseFactory;
import com.l2jbr.gameserver.model.L2TeleportLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * This class ...
 *
 * @version $Revision: 1.3.2.2.2.3 $ $Date: 2005/03/27 15:29:18 $
 */
public class TeleportLocationTable {
    private static Logger _log = LoggerFactory.getLogger(TeleportLocationTable.class.getName());

    private static TeleportLocationTable _instance;

    private Map<Integer, L2TeleportLocation> _teleports;

    public static TeleportLocationTable getInstance() {
        if (_instance == null) {
            _instance = new TeleportLocationTable();
        }
        return _instance;
    }

    private TeleportLocationTable() {
        reloadAll();
    }

    public void reloadAll() {
        _teleports = new LinkedHashMap<>();

        java.sql.Connection con = null;
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = con.prepareStatement("SELECT Description, id, loc_x, loc_y, loc_z, price, fornoble FROM teleport");
            ResultSet rset = statement.executeQuery();
            L2TeleportLocation teleport;

            while (rset.next()) {
                teleport = new L2TeleportLocation();

                teleport.setTeleId(rset.getInt("id"));
                teleport.setLocX(rset.getInt("loc_x"));
                teleport.setLocY(rset.getInt("loc_y"));
                teleport.setLocZ(rset.getInt("loc_z"));
                teleport.setPrice(rset.getInt("price"));
                teleport.setIsForNoble(rset.getInt("fornoble") == 1);

                _teleports.put(teleport.getTeleId(), teleport);
            }

            rset.close();
            statement.close();

            _log.info("TeleportLocationTable: Loaded " + _teleports.size() + " Teleport Location Templates.");
        } catch (Exception e) {
            _log.warn("error while creating teleport table " + e);
        } finally {
            try {
                con.close();
            } catch (Exception e) {
            }
        }
    }

    /**
     * @param id
     * @return
     */
    public L2TeleportLocation getTemplate(int id) {
        return _teleports.get(id);
    }
}
