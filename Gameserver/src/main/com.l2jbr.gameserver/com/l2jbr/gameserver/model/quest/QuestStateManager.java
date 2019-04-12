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
package com.l2jbr.gameserver.model.quest;

import com.l2jbr.gameserver.ThreadPoolManager;
import com.l2jbr.gameserver.model.actor.instance.L2PcInstance;

import java.util.LinkedList;
import java.util.List;


public class QuestStateManager {
    // =========================================================
    // Schedule Task
    public class ScheduleTimerTask implements Runnable {
        @Override
        public void run() {
            try {
                cleanUp();
                ThreadPoolManager.getInstance().scheduleGeneral(new ScheduleTimerTask(), 60000);
            } catch (Throwable t) {
            }
        }
    }

    // =========================================================
    // Data Field
    private static QuestStateManager _instance;
    private List<QuestState> _questStates = new LinkedList<>();

    // =========================================================
    // Constructor
    public QuestStateManager() {
        ThreadPoolManager.getInstance().scheduleGeneral(new ScheduleTimerTask(), 60000);
    }

    // =========================================================
    // Method - Public

    /**
     * Add QuestState for the specified player instance
     *
     * @param quest
     * @param player
     * @param state
     * @param completed
     */
    public void addQuestState(Quest quest, L2PcInstance player, State state, boolean completed) {
        QuestState qs = getQuestState(player);
        if (qs == null) {
            qs = new QuestState(quest, player, state, completed);
        }
    }

    /**
     * Remove all QuestState for all player instance that does not exist
     */
    public void cleanUp() {
        for (int i = getQuestStates().size() - 1; i >= 0; i--) {
            if (getQuestStates().get(i).getPlayer() == null) {
                removeQuestState(getQuestStates().get(i));
                getQuestStates().remove(i);
            }
        }
    }

    // =========================================================
    // Method - Private

    /**
     * Remove QuestState instance
     *
     * @param qs
     */
    private void removeQuestState(QuestState qs) {
        qs = null;
    }

    // =========================================================
    // Property - Public
    public static final QuestStateManager getInstance() {
        if (_instance == null) {
            _instance = new QuestStateManager();
        }
        return _instance;
    }

    /**
     * Return QuestState for specified player instance
     *
     * @param player
     * @return
     */
    public QuestState getQuestState(L2PcInstance player) {
        for (int i = 0; i < getQuestStates().size(); i++) {
            if ((getQuestStates().get(i).getPlayer() != null) && (getQuestStates().get(i).getPlayer().getObjectId() == player.getObjectId())) {
                return getQuestStates().get(i);
            }

        }

        return null;
    }

    /**
     * Return all QuestState
     *
     * @return
     */
    public List<QuestState> getQuestStates() {
        if (_questStates == null) {
            _questStates = new LinkedList<>();
        }
        return _questStates;
    }
}
