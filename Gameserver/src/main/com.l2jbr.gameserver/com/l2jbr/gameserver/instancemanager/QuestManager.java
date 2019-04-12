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
package com.l2jbr.gameserver.instancemanager;

import com.l2jbr.commons.Config;
import com.l2jbr.gameserver.model.quest.Quest;
import com.l2jbr.gameserver.model.quest.jython.QuestJython;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;


public class QuestManager {
    protected static final Logger _log = LoggerFactory.getLogger(QuestManager.class.getName());

    // =========================================================
    private static QuestManager _instance;

    public static final QuestManager getInstance() {
        if (_instance == null) {
            System.out.println("Initializing QuestManager");
            _instance = new QuestManager();
            if (!Config.ALT_DEV_NO_QUESTS) {
                _instance.load();
            }
        }
        return _instance;
    }

    // =========================================================

    // =========================================================
    // Data Field
    private Map<String, Quest> _quests = new LinkedHashMap<>();

    // =========================================================
    // Constructor
    public QuestManager() {
    }

    // =========================================================
    // Method - Public
    public final boolean reload(String questFolder) {
        Quest q = getQuest(questFolder);
        String path = "";
        if (q != null) {
            q.saveGlobalData();
            path = q.getPrefixPath();
        }
        return QuestJython.reloadQuest(path + questFolder);
    }

    /**
     * Reloads a the quest given by questId.<BR>
     * <B>NOTICE: Will only work if the quest name is equal the quest folder name</B>
     *
     * @param questId The id of the quest to be reloaded
     * @return true if reload was succesful, false otherwise
     */
    public final boolean reload(int questId) {
        Quest q = this.getQuest(questId);
        if (q == null) {
            return false;
        }
        q.saveGlobalData();
        return QuestJython.reloadQuest(q.getPrefixPath() + q.getName());
    }

    // =========================================================
    // Method - Private
    private final void load() {
        QuestJython.init();
        System.out.println("Loaded: " + getQuests().size() + " quests");
    }

    public final void save() {
        for (Quest q : getQuests().values()) {
            q.saveGlobalData();
        }
    }

    // =========================================================
    // Property - Public
    public final Quest getQuest(String name) {
        return getQuests().get(name);
    }

    public final Quest getQuest(int questId) {
        for (Quest q : getQuests().values()) {
            if (q.getQuestIntId() == questId) {
                return q;
            }
        }
        return null;
    }

    public final void addQuest(Quest newQuest) {
        if (getQuests().containsKey(newQuest.getName())) {
            _log.info("Replaced: " + newQuest.getName() + " with a new version");
        }

        // Note: FastMap will replace the old value if the key already exists
        // so there is no need to explicitly try to remove the old reference.
        getQuests().put(newQuest.getName(), newQuest);
    }

    public final LinkedHashMap<String, Quest> getQuests() {
        if (_quests == null) {
            _quests = new LinkedHashMap<>();
        }
        return (LinkedHashMap<String, Quest>) _quests;
    }
}
