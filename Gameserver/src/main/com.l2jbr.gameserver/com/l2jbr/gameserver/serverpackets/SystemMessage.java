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
package com.l2jbr.gameserver.serverpackets;

import com.l2jbr.gameserver.network.SystemMessageId;

import java.util.Vector;


/**
 * This class ...
 * @version $Revision: 1.18.2.5.2.8 $ $Date: 2005/04/05 19:41:08 $
 */
public class SystemMessage extends L2GameServerPacket
{
	// d d (d S/d d/d dd)
	// |--------------> 0 - String 1-number 2-textref npcname (1000000-1002655) 3-textref itemname 4-textref skills 5-??
	private static final int TYPE_ZONE_NAME = 7;
	private static final int TYPE_SKILL_NAME = 4;
	private static final int TYPE_ITEM_NAME = 3;
	private static final int TYPE_NPC_NAME = 2;
	private static final int TYPE_NUMBER = 1;
	private static final int TYPE_TEXT = 0;
	private static final String _S__7A_SYSTEMMESSAGE = "[S] 64 SystemMessage";
	private final int _messageId;
	private final Vector<Integer> _types = new Vector<>();
	private final Vector<Object> _values = new Vector<>();
	private int _skillLvL = 1;
	
	public SystemMessage(SystemMessageId messageId)
	{
		_messageId = messageId.getId();
	}
	
	@Deprecated
	public SystemMessage(int messageId)
	{
		_messageId = messageId;
	}
	
	public static SystemMessage sendString(String msg)
	{
		SystemMessage sm = new SystemMessage(SystemMessageId.S1_S2);
		sm.addString(msg);
		
		return sm;
	}
	
	public SystemMessage addString(String text)
	{
		_types.add(TYPE_TEXT);
		_values.add(text);
		
		return this;
	}
	
	public SystemMessage addNumber(int number)
	{
		_types.add(TYPE_NUMBER);
		_values.add(number);
		return this;
	}
	
	public SystemMessage addNpcName(int id)
	{
		_types.add(TYPE_NPC_NAME);
		_values.add(1000000 + id);
		
		return this;
	}
	
	public SystemMessage addItemName(int id)
	{
		_types.add(TYPE_ITEM_NAME);
		_values.add(id);
		
		return this;
	}
	
	public SystemMessage addZoneName(int x, int y, int z)
	{
		_types.add(TYPE_ZONE_NAME);
		int[] coord =
		{
			x,
			y,
			z
		};
		_values.add(coord);
		
		return this;
	}
	
	public SystemMessage addSkillName(int id)
	{
		return addSkillName(id, 1);
	}
	
	public SystemMessage addSkillName(int id, int lvl)
	{
		_types.add(TYPE_SKILL_NAME);
		_values.add(id);
		_skillLvL = lvl;
		
		return this;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0x64);
		
		writeD(_messageId);
		writeD(_types.size());
		
		for (int i = 0; i < _types.size(); i++)
		{
			int t = _types.get(i);
			
			writeD(t);
			
			switch (t)
			{
				case TYPE_TEXT:
				{
					writeS((String) _values.get(i));
					break;
				}
				case TYPE_NUMBER:
				case TYPE_NPC_NAME:
				case TYPE_ITEM_NAME:
				{
					int t1 = (Integer) _values.get(i);
					writeD(t1);
					break;
				}
				case TYPE_SKILL_NAME:
				{
					int t1 = (Integer) _values.get(i);
					writeD(t1); // Skill Id
					writeD(_skillLvL); // Skill lvl
					break;
				}
				case TYPE_ZONE_NAME:
				{
					int t1 = ((int[]) _values.get(i))[0];
					int t2 = ((int[]) _values.get(i))[1];
					int t3 = ((int[]) _values.get(i))[2];
					writeD(t1);
					writeD(t2);
					writeD(t3);
					break;
				}
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.l2jbr.gameserver.serverpackets.ServerBasePacket#getType()
	 */
	@Override
	public String getType()
	{
		return _S__7A_SYSTEMMESSAGE;
	}
	
	public int getMessageID()
	{
		return _messageId;
	}
}
