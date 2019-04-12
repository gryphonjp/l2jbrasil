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
package com.l2jbr.gameserver.skills.conditions;

import com.l2jbr.gameserver.model.actor.instance.L2NpcInstance;
import com.l2jbr.gameserver.skills.Env;

import java.util.List;


/**
 * @author nBd
 */

public class ConditionTargetRaceId extends Condition {
    private final List<Integer> _raceIds;

    public ConditionTargetRaceId(List<Integer> raceId) {
        _raceIds = raceId;
    }

    @Override
    public boolean testImpl(Env env) {
        if (!(env.target instanceof L2NpcInstance)) {
            return false;
        }

        return (_raceIds.contains(((L2NpcInstance) env.target).getTemplate().race.ordinal()));
    }
}
