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
package com.l2jbr.gameserver.util;

import com.l2jbr.commons.Config;
import com.l2jbr.commons.util.Rnd;
import com.l2jbr.gameserver.datatables.NpcTable;
import com.l2jbr.gameserver.idfactory.IdFactory;
import com.l2jbr.gameserver.model.L2MinionData;
import com.l2jbr.gameserver.model.actor.instance.L2MinionInstance;
import com.l2jbr.gameserver.model.actor.instance.L2MonsterInstance;
import com.l2jbr.gameserver.templates.L2NpcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


/**
 * This class ...
 *
 * @version $Revision: 1.2 $ $Date: 2004/06/27 08:12:59 $
 */

public class MinionList {
    private static Logger _log = LoggerFactory.getLogger(L2MonsterInstance.class.getName());

    /**
     * List containing the current spawned minions for this L2MonsterInstance
     */
    private final List<L2MinionInstance> minionReferences;
    protected Map<Long, Integer> _respawnTasks = new LinkedHashMap<>();
    private final L2MonsterInstance master;

    public MinionList(L2MonsterInstance pMaster) {
        minionReferences = new LinkedList<>();
        master = pMaster;
    }

    public int countSpawnedMinions() {
        synchronized (minionReferences) {
            return minionReferences.size();
        }
    }

    public int countSpawnedMinionsById(int minionId) {
        int count = 0;
        synchronized (minionReferences) {
            for (L2MinionInstance minion : getSpawnedMinions()) {
                if (minion.getNpcId() == minionId) {
                    count++;
                }
            }
        }
        return count;
    }

    public boolean hasMinions() {
        return getSpawnedMinions().size() > 0;
    }

    public List<L2MinionInstance> getSpawnedMinions() {
        return minionReferences;
    }

    public void addSpawnedMinion(L2MinionInstance minion) {
        synchronized (minionReferences) {
            minionReferences.add(minion);
        }
    }

    public int lazyCountSpawnedMinionsGroups() {
        Set<Integer> seenGroups = new LinkedHashSet<>();
        for (L2MinionInstance minion : getSpawnedMinions()) {
            seenGroups.add(minion.getNpcId());
        }
        return seenGroups.size();
    }

    public void removeSpawnedMinion(L2MinionInstance minion) {
        synchronized (minionReferences) {
            minionReferences.remove(minion);
        }
    }

    public void moveMinionToRespawnList(L2MinionInstance minion) {
        Long current = System.currentTimeMillis();
        synchronized (minionReferences) {
            minionReferences.remove(minion);
            if (_respawnTasks.get(current) == null) {
                _respawnTasks.put(current, minion.getNpcId());
            } else {
                // nice AoE
                for (int i = 1; i < 30; i++) {
                    if (_respawnTasks.get(current + i) == null) {
                        _respawnTasks.put(current + i, minion.getNpcId());
                        break;
                    }
                }
            }
        }
    }

    public void clearRespawnList() {
        _respawnTasks.clear();
    }

    /**
     * Manage respawning of minions for this RaidBoss.<BR>
     * <BR>
     */
    public void maintainMinions() {
        if ((master == null) || master.isAlikeDead()) {
            return;
        }
        Long current = System.currentTimeMillis();
        if (_respawnTasks != null) {
            for (long deathTime : _respawnTasks.keySet()) {
                double delay = Config.RAID_MINION_RESPAWN_TIMER;
                if ((current - deathTime) > delay) {
                    spawnSingleMinion(_respawnTasks.get(deathTime));
                    _respawnTasks.remove(deathTime);
                }
            }
        }
    }

    /**
     * Manage the spawn of all Minions of this RaidBoss.<BR>
     * <BR>
     * <B><U> Actions</U> :</B><BR>
     * <BR>
     * <li>Get the Minion data of all Minions that must be spawn</li> <li>For each Minion type, spawn the amount of Minion needed</li>
     */
    public void spawnMinions() {
        if ((master == null) || master.isAlikeDead()) {
            return;
        }
        List<L2MinionData> minions = master.getTemplate().getMinionData();

        synchronized (minionReferences) {
            int minionCount, minionId, minionsToSpawn;
            for (L2MinionData minion : minions) {
                minionCount = minion.getAmount();
                minionId = minion.getMinionId();

                minionsToSpawn = minionCount - countSpawnedMinionsById(minionId);

                for (int i = 0; i < minionsToSpawn; i++) {
                    spawnSingleMinion(minionId);
                }
            }
        }
    }

    /**
     * Init a Minion and add it in the world as a visible object.<BR>
     * <BR>
     * <B><U> Actions</U> :</B><BR>
     * <BR>
     * <li>Get the template of the Minion to spawn</li> <li>Create and Init the Minion and generate its Identifier</li> <li>Set the Minion HP, MP and Heading</li> <li>Set the Minion leader to this RaidBoss</li> <li>Init the position of the Minion and add it in the world as a visible object</li><BR>
     * <BR>
     *
     * @param minionid The I2NpcTemplate Identifier of the Minion to spawn
     */
    public void spawnSingleMinion(int minionid) {
        // Get the template of the Minion to spawn
        L2NpcTemplate minionTemplate = NpcTable.getInstance().getTemplate(minionid);

        // Create and Init the Minion and generate its Identifier
        L2MinionInstance monster = new L2MinionInstance(IdFactory.getInstance().getNextId(), minionTemplate);

        // Set the Minion HP, MP and Heading
        monster.setCurrentHpMp(monster.getMaxHp(), monster.getMaxMp());
        monster.setHeading(master.getHeading());

        // Set the Minion leader to this RaidBoss
        monster.setLeader(master);

        // Init the position of the Minion and add it in the world as a visible object
        int spawnConstant;
        int randSpawnLim = 170;
        int randPlusMin = 1;
        spawnConstant = Rnd.nextInt(randSpawnLim);
        // randomize +/-
        randPlusMin = Rnd.nextInt(2);
        if (randPlusMin == 1) {
            spawnConstant *= -1;
        }
        int newX = master.getX() + Math.round(spawnConstant);
        spawnConstant = Rnd.nextInt(randSpawnLim);
        // randomize +/-
        randPlusMin = Rnd.nextInt(2);
        if (randPlusMin == 1) {
            spawnConstant *= -1;
        }
        int newY = master.getY() + Math.round(spawnConstant);

        monster.spawnMe(newX, newY, master.getZ());

        if (Config.DEBUG) {
            _log.debug("Spawned minion template " + minionTemplate.npcId + " with objid: " + monster.getObjectId() + " to boss " + master.getObjectId() + " ,at: " + monster.getX() + " x, " + monster.getY() + " y, " + monster.getZ() + " z");
        }
    }
}
