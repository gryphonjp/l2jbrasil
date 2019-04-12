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
package com.l2jbr.gameserver.handler;

import com.l2jbr.commons.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * This class ...
 *
 * @version $Revision: 1.1.4.5 $ $Date: 2005/03/27 15:30:09 $
 */
public class VoicedCommandHandler {
    private static Logger _log = LoggerFactory.getLogger(ItemHandler.class.getName());

    private static VoicedCommandHandler _instance;

    private final Map<String, IVoicedCommandHandler> _datatable;

    public static VoicedCommandHandler getInstance() {
        if (_instance == null) {
            _instance = new VoicedCommandHandler();
        }
        return _instance;
    }

    private VoicedCommandHandler() {
        _datatable = new LinkedHashMap<>();
    }

    public void registerVoicedCommandHandler(IVoicedCommandHandler handler) {
        String[] ids = handler.getVoicedCommandList();
        for (String id : ids) {
            if (Config.DEBUG) {
                _log.debug("Adding handler for command " + id);
            }
            _datatable.put(id, handler);
        }
    }

    public IVoicedCommandHandler getVoicedCommandHandler(String voicedCommand) {
        String command = voicedCommand;
        if (voicedCommand.indexOf(" ") != -1) {
            command = voicedCommand.substring(0, voicedCommand.indexOf(" "));
        }
        if (Config.DEBUG) {
            _log.debug("getting handler for command: " + command + " -> " + (_datatable.get(command) != null));
        }
        return _datatable.get(command);
    }

    /**
     * @return
     */
    public int size() {
        return _datatable.size();
    }
}
