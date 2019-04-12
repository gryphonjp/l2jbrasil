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

import com.l2jbr.commons.Config;
import com.l2jbr.gameserver.idfactory.IdFactory;
import com.l2jbr.gameserver.model.actor.instance.L2StaticObjectInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;


public class StaticObjects {
    private static Logger _log = LoggerFactory.getLogger(StaticObjects.class.getName());

    private static StaticObjects _instance;
    private final Map<Integer, L2StaticObjectInstance> _staticObjects;

    public static StaticObjects getInstance() {
        if (_instance == null) {
            _instance = new StaticObjects();
        }
        return _instance;
    }

    public StaticObjects() {
        _staticObjects = new LinkedHashMap<>();
        parseData();
        _log.info("StaticObject: Loaded " + _staticObjects.size() + " StaticObject Templates.");
    }

    private void parseData() {
        LineNumberReader lnr = null;
        try {
            File doorData = new File(Config.DATAPACK_ROOT, "data/staticobjects.csv");
            lnr = new LineNumberReader(new BufferedReader(new FileReader(doorData)));

            String line = null;
            while ((line = lnr.readLine()) != null) {
                if ((line.trim().length() == 0) || line.startsWith("#")) {
                    continue;
                }

                L2StaticObjectInstance obj = parse(line);
                _staticObjects.put(obj.getStaticObjectId(), obj);
            }
        } catch (FileNotFoundException e) {
            _log.warn("staticobjects.csv is missing in data folder");
        } catch (Exception e) {
            _log.warn("error while creating StaticObjects table " + e);
        } finally {
            try {
                lnr.close();
            } catch (Exception e) {
            }
        }
    }

    public static L2StaticObjectInstance parse(String line) {
        StringTokenizer st = new StringTokenizer(line, ";");

        st.nextToken(); // Pass over static object name (not used in server)

        int id = Integer.parseInt(st.nextToken());
        int x = Integer.parseInt(st.nextToken());
        int y = Integer.parseInt(st.nextToken());
        int z = Integer.parseInt(st.nextToken());
        int type = Integer.parseInt(st.nextToken());
        String texture = st.nextToken();
        int map_x = Integer.parseInt(st.nextToken());
        int map_y = Integer.parseInt(st.nextToken());

        L2StaticObjectInstance obj = new L2StaticObjectInstance(IdFactory.getInstance().getNextId());
        obj.setType(type);
        obj.setStaticObjectId(id);
        obj.setXYZ(x, y, z);
        obj.setMap(texture, map_x, map_y);
        obj.spawnMe();

        return obj;
    }
}
