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
package com.l2jbr.gameserver.model;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * This class ...
 *
 * @version $Revision$ $Date$
 */

public class DesireTable {
    public static final DesireType[] DEFAULT_DESIRES =
        {
            DesireType.FEAR,
            DesireType.DISLIKE,
            DesireType.HATE,
            DesireType.DAMAGE
        };

    public enum DesireType {
        FEAR,
        DISLIKE,
        HATE,
        DAMAGE;
    }

    class DesireValue {
        private float _value;

        DesireValue() {
            this(0f);
        }

        DesireValue(Float pValue) {
            _value = pValue;
        }

        public void addValue(float pValue) {
            _value += pValue;
        }

        public float getValue() {
            return _value;
        }
    }

    class Desires {
        private final Map<DesireType, DesireValue> _desireTable;

        public Desires(DesireType... desireList) {
            _desireTable = new LinkedHashMap<>();

            for (DesireType desire : desireList) {
                _desireTable.put(desire, new DesireValue());
            }
        }

        public DesireValue getDesireValue(DesireType type) {
            return _desireTable.get(type);
        }

        public void addValue(DesireType type, float value) {
            DesireValue temp = getDesireValue(type);
            if (temp != null) {
                temp.addValue(value);
            }
        }

        public void createDesire(DesireType type) {
            _desireTable.put(type, new DesireValue());
        }

        public void deleteDesire(DesireType type) {
            _desireTable.remove(type);
        }
    }

    private final Map<L2Object, Desires> _objectDesireTable;
    private final Desires _generalDesires;
    private final DesireType[] _desireTypes;

    public DesireTable(DesireType... desireList) {
        _desireTypes = desireList;
        _objectDesireTable = new LinkedHashMap<>();
        _generalDesires = new Desires(_desireTypes);
    }

    public float getDesireValue(DesireType type) {
        return _generalDesires.getDesireValue(type).getValue();
    }

    public float getDesireValue(L2Object object, DesireType type) {
        Desires desireList = _objectDesireTable.get(object);
        if (desireList == null) {
            return 0f;
        }
        return desireList.getDesireValue(type).getValue();
    }

    public void addDesireValue(DesireType type, float value) {
        _generalDesires.addValue(type, value);
    }

    public void addDesireValue(L2Object object, DesireType type, float value) {
        Desires desireList = _objectDesireTable.get(object);
        if (desireList != null) {
            desireList.addValue(type, value);
        }
    }

    public void createDesire(DesireType type) {
        _generalDesires.createDesire(type);
    }

    public void deleteDesire(DesireType type) {
        _generalDesires.deleteDesire(type);
    }

    public void createDesire(L2Object object, DesireType type) {
        Desires desireList = _objectDesireTable.get(object);
        if (desireList != null) {
            desireList.createDesire(type);
        }
    }

    public void deleteDesire(L2Object object, DesireType type) {
        Desires desireList = _objectDesireTable.get(object);
        if (desireList != null) {
            desireList.deleteDesire(type);
        }
    }

    public void addKnownObject(L2Object object) {
        if (object != null) {
            addKnownObject(object, DesireType.DISLIKE, DesireType.FEAR, DesireType.DAMAGE, DesireType.HATE);
        }
    }

    public void addKnownObject(L2Object object, DesireType... desireList) {
        if (object != null) {
            _objectDesireTable.put(object, new Desires(desireList));
        }
    }
}
