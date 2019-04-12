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
package com.l2jbr.gameserver.clientpackets;

import com.l2jbr.commons.Config;
import com.l2jbr.gameserver.ai.CtrlIntention;
import com.l2jbr.gameserver.datatables.SkillTable;
import com.l2jbr.gameserver.instancemanager.CastleManager;
import com.l2jbr.gameserver.model.*;
import com.l2jbr.gameserver.model.actor.instance.*;
import com.l2jbr.gameserver.network.SystemMessageId;
import com.l2jbr.gameserver.serverpackets.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


/**
 * This class ...
 * @version $Revision: 1.11.2.7.2.9 $ $Date: 2005/04/06 16:13:48 $
 */
public final class RequestActionUse extends L2GameClientPacket
{
	private static final String _C__45_REQUESTACTIONUSE = "[C] 45 RequestActionUse";
	private static Logger _log = LoggerFactory.getLogger(RequestActionUse.class.getName());
	
	private int _actionId;
	private boolean _ctrlPressed;
	private boolean _shiftPressed;
	
	@Override
	protected void readImpl()
	{
		_actionId = readD();
		_ctrlPressed = (readD() == 1);
		_shiftPressed = (readC() == 1);
	}
	
	@Override
	protected void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		
		if (activeChar == null)
		{
			return;
		}
		
		if (Config.DEBUG)
		{
			_log.debug(activeChar.getName() + " request Action use: id " + _actionId + " 2:" + _ctrlPressed + " 3:" + _shiftPressed);
		}
		
		// dont do anything if player is dead
		if (activeChar.isAlikeDead())
		{
			getClient().sendPacket(new ActionFailed());
			return;
		}
		
		// don't do anything if player is confused
		if (activeChar.isOutOfControl())
		{
			getClient().sendPacket(new ActionFailed());
			return;
		}
		
		L2Summon pet = activeChar.getPet();
		L2Object target = activeChar.getTarget();
		
		if (Config.DEBUG)
		{
			_log.info("Requested Action ID: " + String.valueOf(_actionId));
		}
		
		switch (_actionId)
		{
			case 0:
				if (activeChar.getMountType() != 0)
				{
					break;
				}
				
				if ((target != null) && !activeChar.isSitting() && (target instanceof L2StaticObjectInstance) && (((L2StaticObjectInstance) target).getType() == 1) && (CastleManager.getInstance().getCastle(target) != null) && activeChar.isInsideRadius(target, L2StaticObjectInstance.INTERACTION_DISTANCE, false, false))
				{
					ChairSit cs = new ChairSit(activeChar, ((L2StaticObjectInstance) target).getStaticObjectId());
					activeChar.sendPacket(cs);
					activeChar.sitDown();
					activeChar.broadcastPacket(cs);
					break;
				}
				
				if (activeChar.isSitting())
				{
					activeChar.standUp();
				}
				else
				{
					activeChar.sitDown();
				}
				
				if (Config.DEBUG)
				{
					_log.debug("new wait type: " + (activeChar.isSitting() ? "SITTING" : "STANDING"));
				}
				
				break;
			case 1:
				if (activeChar.isRunning())
				{
					activeChar.setWalking();
				}
				else
				{
					activeChar.setRunning();
				}
				
				if (Config.DEBUG)
				{
					_log.debug("new move type: " + (activeChar.isRunning() ? "RUNNING" : "WALKIN"));
				}
				break;
			case 15:
			case 21: // pet follow/stop
				if ((pet != null) && !pet.isMovementDisabled() && !activeChar.isBetrayed())
				{
					pet.setFollowStatus(!pet.getFollowStatus());
				}
				
				break;
			case 16:
			case 22: // pet attack
				if ((target != null) && (pet != null) && (pet != target) && !pet.isAttackingDisabled() && !activeChar.isBetrayed())
				{
					if (activeChar.isInOlympiadMode() && !activeChar.isOlympiadStart())
					{
						// if L2PcInstance is in Olympia and the match isn't already start, send a Server->Client packet ActionFailed
						activeChar.sendPacket(new ActionFailed());
						return;
					}
					
					if ((activeChar.getAccessLevel() < Config.GM_PEACEATTACK) && activeChar.isInsidePeaceZone(pet, target))
					{
						activeChar.sendPacket(new SystemMessage(SystemMessageId.TARGET_IN_PEACEZONE));
						return;
					}
					
					if (target.isAutoAttackable(activeChar) || _ctrlPressed)
					{
						if (target instanceof L2DoorInstance)
						{
							if (((L2DoorInstance) target).isAttackable(activeChar) && (pet.getNpcId() != L2SiegeSummonInstance.SWOOP_CANNON_ID))
							{
								pet.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
							}
						}
						// siege golem AI doesn't support attacking other than doors at the moment
						else if (pet.getNpcId() != L2SiegeSummonInstance.SIEGE_GOLEM_ID)
						{
							pet.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
						}
					}
				}
				break;
			case 17:
			case 23: // pet - cancel action
				if ((pet != null) && !pet.isMovementDisabled() && !activeChar.isBetrayed())
				{
					pet.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE, null);
				}
				
				break;
			case 19: // pet unsummon
				if ((pet != null) && !activeChar.isBetrayed())
				{
					// returns pet to control item
					if (pet.isDead())
					{
						activeChar.sendPacket(new SystemMessage(SystemMessageId.DEAD_PET_CANNOT_BE_RETURNED));
					}
					else if (pet.isAttackingNow() || pet.isRooted())
					{
						activeChar.sendPacket(new SystemMessage(SystemMessageId.PET_CANNOT_SENT_BACK_DURING_BATTLE));
					}
					else
					{
						// if it is a pet and not a summon
						if (pet instanceof L2PetInstance)
						{
							L2PetInstance petInst = (L2PetInstance) pet;
							
							// if the pet is more than 40% fed
							if (petInst.getCurrentFed() > (petInst.getMaxFed() * 0.40))
							{
								pet.unSummon(activeChar);
							}
							else
							{
								activeChar.sendPacket(new SystemMessage(SystemMessageId.YOU_CANNOT_RESTORE_HUNGRY_PETS));
							}
						}
					}
				}
				break;
			case 38: // pet mount
				// mount
				if ((pet != null) && pet.isMountable() && !activeChar.isMounted() && !activeChar.isBetrayed())
				{
					if (activeChar.isDead())
					{
						// A strider cannot be ridden when dead
						SystemMessage msg = new SystemMessage(SystemMessageId.STRIDER_CANT_BE_RIDDEN_WHILE_DEAD);
						activeChar.sendPacket(msg);
						msg = null;
					}
					else if (pet.isDead())
					{
						// A dead strider cannot be ridden.
						SystemMessage msg = new SystemMessage(SystemMessageId.DEAD_STRIDER_CANT_BE_RIDDEN);
						activeChar.sendPacket(msg);
						msg = null;
					}
					else if (pet.isInCombat() || pet.isRooted())
					{
						// A strider in battle cannot be ridden
						SystemMessage msg = new SystemMessage(SystemMessageId.STRIDER_IN_BATLLE_CANT_BE_RIDDEN);
						activeChar.sendPacket(msg);
						msg = null;
					}
					else if (activeChar.isInCombat())
					{
						// A strider cannot be ridden while in battle
						SystemMessage msg = new SystemMessage(SystemMessageId.STRIDER_CANT_BE_RIDDEN_WHILE_IN_BATTLE);
						activeChar.sendPacket(msg);
						msg = null;
					}
					else if (activeChar.isSitting() || activeChar.isMoving())
					{
						// A strider can be ridden only when standing
						SystemMessage msg = new SystemMessage(SystemMessageId.STRIDER_CAN_BE_RIDDEN_ONLY_WHILE_STANDING);
						activeChar.sendPacket(msg);
						msg = null;
					}
					else if (activeChar.isFishing())
					{
						// You can't mount, dismount, break and drop items while fishing
						SystemMessage msg = new SystemMessage(SystemMessageId.CANNOT_DO_WHILE_FISHING_2);
						activeChar.sendPacket(msg);
						msg = null;
					}
					else if (activeChar.isCursedWeaponEquiped())
					{
						// You can't mount, dismount, break and drop items while weilding a cursed weapon
						SystemMessage msg = new SystemMessage(SystemMessageId.STRIDER_CANT_BE_RIDDEN_WHILE_IN_BATTLE);
						activeChar.sendPacket(msg);
					}
					else if (!pet.isDead() && !activeChar.isMounted())
					{
						if (!activeChar.disarmWeapons())
						{
							return;
						}
						Ride mount = new Ride(activeChar.getObjectId(), Ride.ACTION_MOUNT, pet.getTemplate().npcId);
						activeChar.broadcastPacket(mount);
						activeChar.setMountType(mount.getMountType());
						activeChar.setMountObjectID(pet.getControlItemId());
						pet.unSummon(activeChar);
					}
				}
				else if (activeChar.isRentedPet())
				{
					activeChar.stopRentPet();
				}
				else if (activeChar.isMounted())
				{
					if (activeChar.setMountType(0))
					{
						if (activeChar.isFlying())
						{
							activeChar.removeSkill(SkillTable.getInstance().getInfo(4289, 1));
						}
						Ride dismount = new Ride(activeChar.getObjectId(), Ride.ACTION_DISMOUNT, 0);
						activeChar.broadcastPacket(dismount);
						activeChar.setMountObjectID(0);
					}
				}
				break;
			case 32: // Wild Hog Cannon - Mode Change
				useSkill(4230);
				break;
			case 36: // Soulless - Toxic Smoke
				useSkill(4259);
				break;
			case 37:
				if (activeChar.isAlikeDead())
				{
					getClient().sendPacket(new ActionFailed());
					return;
				}
				if (activeChar.getPrivateStoreType() != 0)
				{
					activeChar.setPrivateStoreType(L2PcInstance.STORE_PRIVATE_NONE);
					activeChar.broadcastUserInfo();
				}
				if (activeChar.isSitting())
				{
					activeChar.standUp();
				}
				
				if (activeChar.getCreateList() == null)
				{
					activeChar.setCreateList(new L2ManufactureList());
				}
				
				activeChar.sendPacket(new RecipeShopManageList(activeChar, true));
				break;
			case 39: // Soulless - Parasite Burst
				useSkill(4138);
				break;
			case 41: // Wild Hog Cannon - Attack
				useSkill(4230);
				break;
			case 42: // Kai the Cat - Self Damage Shield
				useSkill(4378, activeChar);
				break;
			case 43: // Unicorn Merrow - Hydro Screw
				useSkill(4137);
				break;
			case 44: // Big Boom - Boom Attack
				useSkill(4139);
				break;
			case 45: // Unicorn Boxer - Master Recharge
				useSkill(4025, activeChar);
				break;
			case 46: // Mew the Cat - Mega Storm Strike
				useSkill(4261);
				break;
			case 47: // Silhouette - Steal Blood
				useSkill(4260);
				break;
			case 48: // Mechanic Golem - Mech. Cannon
				useSkill(4068);
				break;
			case 51:
				// Player shouldn't be able to set stores if he/she is alike dead (dead or fake death)
				if (activeChar.isAlikeDead())
				{
					getClient().sendPacket(new ActionFailed());
					return;
				}
				if (activeChar.getPrivateStoreType() != 0)
				{
					activeChar.setPrivateStoreType(L2PcInstance.STORE_PRIVATE_NONE);
					activeChar.broadcastUserInfo();
				}
				if (activeChar.isSitting())
				{
					activeChar.standUp();
				}
				
				if (activeChar.getCreateList() == null)
				{
					activeChar.setCreateList(new L2ManufactureList());
				}
				
				activeChar.sendPacket(new RecipeShopManageList(activeChar, false));
				break;
			case 52: // unsummon
				if ((pet != null) && (pet instanceof L2SummonInstance))
				{
					pet.unSummon(activeChar);
				}
				break;
			case 53: // move to target
				if ((target != null) && (pet != null) && (pet != target) && !pet.isMovementDisabled())
				{
					pet.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(target.getX(), target.getY(), target.getZ(), 0));
				}
				break;
			case 54: // move to target hatch/strider
				if ((target != null) && (pet != null) && (pet != target) && !pet.isMovementDisabled())
				{
					pet.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(target.getX(), target.getY(), target.getZ(), 0));
				}
				break;
			case 96: // Quit Party Command Channel
				_log.info("98 Accessed");
				break;
			case 97: // Request Party Command Channel Info
				// if (!PartyCommandManager.getInstance().isPlayerInChannel(activeChar))
				// return;
				_log.info("97 Accessed");
				// PartyCommandManager.getInstance().getActiveChannelInfo(activeChar);
				break;
			case 1000: // Siege Golem - Siege Hammer
				if (target instanceof L2DoorInstance)
				{
					useSkill(4079);
				}
				break;
			case 1001:
				break;
			case 1003: // Wind Hatchling/Strider - Wild Stun
				useSkill(4710); // TODO use correct skill lvl based on pet lvl
				break;
			case 1004: // Wind Hatchling/Strider - Wild Defense
				useSkill(4711, activeChar); // TODO use correct skill lvl based on pet lvl
				break;
			case 1005: // Star Hatchling/Strider - Bright Burst
				useSkill(4712); // TODO use correct skill lvl based on pet lvl
				break;
			case 1006: // Star Hatchling/Strider - Bright Heal
				useSkill(4713, activeChar); // TODO use correct skill lvl based on pet lvl
				break;
			case 1007: // Cat Queen - Blessing of Queen
				useSkill(4699, activeChar);
				break;
			case 1008: // Cat Queen - Gift of Queen
				useSkill(4700, activeChar);
				break;
			case 1009: // Cat Queen - Cure of Queen
				useSkill(4701);
				break;
			case 1010: // Unicorn Seraphim - Blessing of Seraphim
				useSkill(4702, activeChar);
				break;
			case 1011: // Unicorn Seraphim - Gift of Seraphim
				useSkill(4703, activeChar);
				break;
			case 1012: // Unicorn Seraphim - Cure of Seraphim
				useSkill(4704);
				break;
			case 1013: // Nightshade - Curse of Shade
				useSkill(4705);
				break;
			case 1014: // Nightshade - Mass Curse of Shade
				useSkill(4706, activeChar);
				break;
			case 1015: // Nightshade - Shade Sacrifice
				useSkill(4707);
				break;
			case 1016: // Cursed Man - Cursed Blow
				useSkill(4709);
				break;
			case 1017: // Cursed Man - Cursed Strike/Stun
				useSkill(4708);
				break;
			case 1031: // Feline King - Slash
				useSkill(5135);
				break;
			case 1032: // Feline King - Spinning Slash
				useSkill(5136);
				break;
			case 1033: // Feline King - Grip of the Cat
				useSkill(5137);
				break;
			case 1034: // Magnus the Unicorn - Whiplash
				useSkill(5138);
				break;
			case 1035: // Magnus the Unicorn - Tridal Wave
				useSkill(5139);
				break;
			case 1036: // Spectral Lord - Corpse Kaboom
				useSkill(5142);
				break;
			case 1037: // Spectral Lord - Dicing Death
				useSkill(5141);
				break;
			case 1038: // Spectral Lord - Force Curse
				useSkill(5140);
				break;
			case 1039: // Swoop Cannon - Cannon Fodder
				if (!(target instanceof L2DoorInstance))
				{
					useSkill(5110);
				}
				break;
			case 1040: // Swoop Cannon - Big Bang
				if (!(target instanceof L2DoorInstance))
				{
					useSkill(5111);
				}
				break;
			default:
				_log.warn(activeChar.getName() + ": unhandled action type " + _actionId);
		}
	}
	
	/*
	 * Cast a skill for active pet/servitor. Target is specified as a parameter but can be overwrited or ignored depending on skill type.
	 */
	private void useSkill(int skillId, L2Object target)
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		L2Summon activeSummon = activeChar.getPet();
		
		if (activeChar.getPrivateStoreType() != 0)
		{
			activeChar.sendMessage("Cannot use skills while trading");
			return;
		}
		
		if ((activeSummon != null) && !activeChar.isBetrayed())
		{
			Map<Integer, L2Skill> _skills = activeSummon.getTemplate().getSkills();
			
			if (_skills == null)
			{
				return;
			}
			
			if (_skills.size() == 0)
			{
				activeChar.sendPacket(new SystemMessage(SystemMessageId.SKILL_NOT_AVAILABLE));
				return;
			}
			
			L2Skill skill = _skills.get(skillId);
			
			if (skill == null)
			{
				if (Config.DEBUG)
				{
					_log.warn("Skill " + skillId + " missing from npcskills.sql for a summon id " + activeSummon.getNpcId());
				}
				return;
			}
			
			activeSummon.setTarget(target);
			activeSummon.useMagic(skill, _ctrlPressed, _shiftPressed);
		}
	}
	
	/*
	 * Cast a skill for active pet/servitor. Target is retrieved from owner' target, then validated by overloaded method useSkill(int, L2Character).
	 */
	private void useSkill(int skillId)
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		useSkill(skillId, activeChar.getTarget());
	}
	
	@Override
	public String getType()
	{
		return _C__45_REQUESTACTIONUSE;
	}
}
