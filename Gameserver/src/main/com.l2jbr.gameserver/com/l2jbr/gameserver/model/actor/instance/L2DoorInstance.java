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
package com.l2jbr.gameserver.model.actor.instance;

import com.l2jbr.commons.Config;
import com.l2jbr.gameserver.ThreadPoolManager;
import com.l2jbr.gameserver.ai.CtrlIntention;
import com.l2jbr.gameserver.ai.L2CharacterAI;
import com.l2jbr.gameserver.ai.L2DoorAI;
import com.l2jbr.gameserver.instancemanager.CastleManager;
import com.l2jbr.gameserver.model.*;
import com.l2jbr.gameserver.model.actor.knownlist.DoorKnownList;
import com.l2jbr.gameserver.model.actor.stat.DoorStat;
import com.l2jbr.gameserver.model.actor.status.DoorStatus;
import com.l2jbr.gameserver.model.entity.Castle;
import com.l2jbr.gameserver.model.entity.ClanHall;
import com.l2jbr.gameserver.network.L2GameClient;
import com.l2jbr.gameserver.serverpackets.*;
import com.l2jbr.gameserver.templates.L2CharTemplate;
import com.l2jbr.gameserver.templates.L2Weapon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;


/**
 * This class ...
 *
 * @version $Revision: 1.3.2.2.2.5 $ $Date: 2005/03/27 15:29:32 $
 */
public class L2DoorInstance extends L2Character {
    protected static final Logger log = LoggerFactory.getLogger(L2DoorInstance.class.getName());

    /**
     * The castle index in the array of L2Castle this L2NpcInstance belongs to
     */
    private int _castleIndex = -2;
    private int _mapRegion = -1;

    // when door is closed, the dimensions are
    private int _rangeXMin = 0;
    private int _rangeYMin = 0;
    private int _rangeZMin = 0;
    private int _rangeXMax = 0;
    private int _rangeYMax = 0;
    private int _rangeZMax = 0;

    protected final int _doorId;
    protected final String _name;
    private int _open;
    private final boolean _unlockable;

    private ClanHall _clanHall;

    protected int _autoActionDelay = -1;
    private ScheduledFuture<?> _autoActionTask;

    /**
     * This class may be created only by L2Character and only for AI
     */
    public class AIAccessor extends L2Character.AIAccessor {
        protected AIAccessor() {
        }

        @Override
        public L2DoorInstance getActor() {
            return L2DoorInstance.this;
        }

        @Override
        public void moveTo(int x, int y, int z, int offset) {
        }

        @Override
        public void moveTo(int x, int y, int z) {
        }

        @Override
        public void stopMove(L2CharPosition pos) {
        }

        @Override
        public void doAttack(L2Character target) {
        }

        @Override
        public void doCast(L2Skill skill) {
        }
    }

    @Override
    public L2CharacterAI getAI() {
        if (_ai == null) {
            synchronized (this) {
                if (_ai == null) {
                    _ai = new L2DoorAI(new AIAccessor());
                }
            }
        }
        return _ai;
    }

    @Override
    public boolean hasAI() {
        return (_ai != null);
    }

    class CloseTask implements Runnable {
        @Override
        public void run() {
            try {
                onClose();
            } catch (Throwable e) {
                log.error( "", e);
            }
        }
    }

    /**
     * Manages the auto open and closing of a door.
     */
    class AutoOpenClose implements Runnable {
        @Override
        public void run() {
            try {
                String doorAction;

                if (getOpen() == 1) {
                    doorAction = "opened";
                    openMe();
                } else {
                    doorAction = "closed";
                    closeMe();
                }

                if (Config.DEBUG) {
                    log.info("Auto " + doorAction + " door ID " + _doorId + " (" + _name + ") for " + (_autoActionDelay / 60000) + " minute(s).");
                }
            } catch (Exception e) {
                log.warn("Could not auto open/close door ID " + _doorId + " (" + _name + ")");
            }
        }
    }

    /**
     * @param objectId
     * @param template
     * @param doorId
     * @param name
     * @param unlockable
     */
    public L2DoorInstance(int objectId, L2CharTemplate template, int doorId, String name, boolean unlockable) {
        super(objectId, template);
        getKnownList(); // init knownlist
        getStat(); // init stats
        getStatus(); // init status
        _doorId = doorId;
        _name = name;
        _unlockable = unlockable;
    }

    @Override
    public final DoorKnownList getKnownList() {
        if ((super.getKnownList() == null) || !(super.getKnownList() instanceof DoorKnownList)) {
            setKnownList(new DoorKnownList(this));
        }
        return (DoorKnownList) super.getKnownList();
    }

    @Override
    public final DoorStat getStat() {
        if ((super.getStat() == null) || !(super.getStat() instanceof DoorStat)) {
            setStat(new DoorStat(this));
        }
        return (DoorStat) super.getStat();
    }

    @Override
    public final DoorStatus getStatus() {
        if ((super.getStatus() == null) || !(super.getStatus() instanceof DoorStatus)) {
            setStatus(new DoorStatus(this));
        }
        return (DoorStatus) super.getStatus();
    }

    public final boolean isUnlockable() {
        return _unlockable;
    }

    @Override
    public final int getLevel() {
        return 1;
    }

    /**
     * @return Returns the doorId.
     */
    public int getDoorId() {
        return _doorId;
    }

    /**
     * @return Returns the open.
     */
    public int getOpen() {
        return _open;
    }

    /**
     * @param open The open to set.
     */
    public void setOpen(int open) {
        _open = open;
    }

    /**
     * Sets the delay in milliseconds for automatic opening/closing of this door instance. <BR>
     * <B>Note:</B> A value of -1 cancels the auto open/close task.
     *
     * @param actionDelay
     */
    public void setAutoActionDelay(int actionDelay) {
        if (_autoActionDelay == actionDelay) {
            return;
        }

        if (actionDelay > -1) {
            AutoOpenClose ao = new AutoOpenClose();
            ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(ao, actionDelay, actionDelay);
        } else {
            if (_autoActionTask != null) {
                _autoActionTask.cancel(false);
            }
        }

        _autoActionDelay = actionDelay;
    }

    public int getDamage() {
        int dmg = 6 - (int) Math.ceil((getCurrentHp() / getMaxHp()) * 6);
        if (dmg > 6) {
            return 6;
        }
        if (dmg < 0) {
            return 0;
        }
        return dmg;
    }

    public final Castle getCastle() {
        if (_castleIndex < 0) {
            _castleIndex = CastleManager.getInstance().getCastleIndex(this);
        }
        if (_castleIndex < 0) {
            return null;
        }
        return CastleManager.getInstance().getCastles().get(_castleIndex);
    }

    public void setClanHall(ClanHall clanhall) {
        _clanHall = clanhall;
    }

    public ClanHall getClanHall() {
        return _clanHall;
    }

    public boolean isEnemyOf(L2Character cha) {
        return true;
    }

    @Override
    public boolean isAutoAttackable(L2Character attacker) {
        if (isUnlockable()) {
            return true;
        }

        // Attackable during siege by attacker only
        return ((attacker != null) && (attacker instanceof L2PcInstance) && (getCastle() != null) && (getCastle().getCastleId() > 0) && getCastle().getSiege().getIsInProgress() && getCastle().getSiege().checkIsAttacker(((L2PcInstance) attacker).getClan()));
    }

    public boolean isAttackable(L2Character attacker) {
        return isAutoAttackable(attacker);
    }

    @Override
    public void updateAbnormalEffect() {
    }

    public int getDistanceToWatchObject(L2Object object) {
        if (!(object instanceof L2PcInstance)) {
            return 0;
        }
        return 2000;
    }

    /**
     * Return the distance after which the object must be remove from _knownObject according to the type of the object.<BR>
     * <BR>
     * <B><U> Values </U> :</B><BR>
     * <BR>
     * <li>object is a L2PcInstance : 4000</li> <li>object is not a L2PcInstance : 0</li><BR>
     * <BR>
     *
     * @param object
     * @return
     */
    public int getDistanceToForgetObject(L2Object object) {
        if (!(object instanceof L2PcInstance)) {
            return 0;
        }

        return 4000;
    }

    /**
     * Return null.<BR>
     * <BR>
     */
    @Override
    public L2ItemInstance getActiveWeaponInstance() {
        return null;
    }

    @Override
    public L2Weapon getActiveWeaponItem() {
        return null;
    }

    @Override
    public L2ItemInstance getSecondaryWeaponInstance() {
        return null;
    }

    @Override
    public L2Weapon getSecondaryWeaponItem() {
        return null;
    }

    @Override
    public void onAction(L2PcInstance player) {
        if (player == null) {
            return;
        }

        // Check if the L2PcInstance already target the L2NpcInstance
        if (this != player.getTarget()) {
            // Set the target of the L2PcInstance player
            player.setTarget(this);

            // Send a Server->Client packet MyTargetSelected to the L2PcInstance player
            MyTargetSelected my = new MyTargetSelected(getObjectId(), 0);
            player.sendPacket(my);

            // if (isAutoAttackable(player))
            // {
            DoorStatusUpdate su = new DoorStatusUpdate(this);
            player.sendPacket(su);
            // }

            // Send a Server->Client packet ValidateLocation to correct the L2NpcInstance position and heading on the client
            player.sendPacket(new ValidateLocation(this));
        } else {
            // MyTargetSelected my = new MyTargetSelected(getObjectId(), player.getLevel());
            // player.sendPacket(my);
            if (isAutoAttackable(player)) {
                if (Math.abs(player.getZ() - getZ()) < 400) // this max heigth difference might need some tweaking
                {
                    player.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, this);
                }
            } else if ((player.getClan() != null) && (getClanHall() != null) && (player.getClanId() == getClanHall().getOwnerId())) {
                if (!isInsideRadius(player, L2NpcInstance.INTERACTION_DISTANCE, false, false)) {
                    player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this);
                } else {
                    // need find serverpacket which ask open/close gate. now auto
                    // if (getOpen() == 1) player.sendPacket(new SystemMessage(1140));
                    // else player.sendPacket(new SystemMessage(1141));
                    if (getOpen() == 1) {
                        openMe();
                    } else {
                        closeMe();
                    }
                }
            }
        }
        // Send a Server->Client ActionFailed to the L2PcInstance in order to avoid that the client wait another packet
        player.sendPacket(new ActionFailed());
    }

    @Override
    public void onActionShift(L2GameClient client) {
        L2PcInstance player = client.getActiveChar();
        if (player == null) {
            return;
        }

        if (player.getAccessLevel() >= Config.GM_ACCESSLEVEL) {
            player.setTarget(this);
            MyTargetSelected my = new MyTargetSelected(getObjectId(), player.getLevel());
            player.sendPacket(my);

            if (isAutoAttackable(player)) {
                DoorStatusUpdate su = new DoorStatusUpdate(this);
                player.sendPacket(su);
            }

            NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
            StringBuilder html1 = new StringBuilder("<html><body><table border=0>");
            html1.append("<tr><td>S.Y.L. Says:</td></tr>");
            html1.append("<tr><td>Current HP  " + getCurrentHp() + "</td></tr>");
            html1.append("<tr><td>Max HP      " + getMaxHp() + "</td></tr>");

            html1.append("<tr><td>Object ID: " + getObjectId() + "</td></tr>");
            html1.append("<tr><td>Door ID:<br>" + getDoorId() + "</td></tr>");
            html1.append("<tr><td><br></td></tr>");

            html1.append("<tr><td>Class: " + getClass().getName() + "</td></tr>");
            html1.append("<tr><td><br></td></tr>");
            html1.append("</table>");

            html1.append("<table><tr>");
            html1.append("<td><button value=\"Open\" action=\"bypass -h admin_open " + getDoorId() + "\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
            html1.append("<td><button value=\"Close\" action=\"bypass -h admin_close " + getDoorId() + "\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
            html1.append("<td><button value=\"Kill\" action=\"bypass -h admin_kill\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
            html1.append("<td><button value=\"Delete\" action=\"bypass -h admin_delete\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
            html1.append("</tr></table></body></html>");

            html.setHtml(html1.toString());
            player.sendPacket(html);
        } else {
            // ATTACK the mob without moving?
        }

        player.sendPacket(new ActionFailed());
    }

    @Override
    public void broadcastStatusUpdate() {
        Collection<L2PcInstance> knownPlayers = getKnownList().getKnownPlayers().values();
        if ((knownPlayers == null) || knownPlayers.isEmpty()) {
            return;
        }

        DoorStatusUpdate su = new DoorStatusUpdate(this);
        for (L2PcInstance player : knownPlayers) {
            player.sendPacket(su);
        }
    }

    public void onOpen() {
        ThreadPoolManager.getInstance().scheduleGeneral(new CloseTask(), 60000);
    }

    public void onClose() {
        closeMe();
    }

    public final void closeMe() {
        setOpen(1);
        broadcastStatusUpdate();
    }

    public final void openMe() {
        setOpen(0);
        broadcastStatusUpdate();
    }

    @Override
    public String toString() {
        return "door " + _doorId;
    }

    public String getDoorName() {
        return _name;
    }

    public int getXMin() {
        return _rangeXMin;
    }

    public int getYMin() {
        return _rangeYMin;
    }

    public int getZMin() {
        return _rangeZMin;
    }

    public int getXMax() {
        return _rangeXMax;
    }

    public int getYMax() {
        return _rangeYMax;
    }

    public int getZMax() {
        return _rangeZMax;
    }

    public void setRange(int xMin, int yMin, int zMin, int xMax, int yMax, int zMax) {
        _rangeXMin = xMin;
        _rangeYMin = yMin;
        _rangeZMin = zMin;

        _rangeXMax = xMax;
        _rangeYMax = yMax;
        _rangeZMax = zMax;
    }

    public int getMapRegion() {
        return _mapRegion;
    }

    public void setMapRegion(int region) {
        _mapRegion = region;
    }

    public Collection<L2SiegeGuardInstance> getKnownSiegeGuards() {
        List<L2SiegeGuardInstance> result = new LinkedList<>();

        for (L2Object obj : getKnownList().getKnownObjects().values()) {
            if (obj instanceof L2SiegeGuardInstance) {
                result.add((L2SiegeGuardInstance) obj);
            }
        }

        return result;
    }
}
