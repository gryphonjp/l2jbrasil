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
package com.l2jbr.gameserver.communitybbs.BB;

import com.l2jbr.commons.L2DatabaseFactory;
import com.l2jbr.gameserver.communitybbs.Manager.TopicBBSManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;


public class Topic
{
	
	private static Logger _log = LoggerFactory.getLogger(Topic.class.getName());
	public static final int MORMAL = 0;
	public static final int MEMO = 1;
	
	private final int _id;
	private final int _forumId;
	private final String _topicName;
	private final long _date;
	private final String _ownerName;
	private final int _ownerId;
	private final int _type;
	private final int _cReply;
	
	/**
	 * @param ct
	 * @param id
	 * @param fid
	 * @param name
	 * @param date
	 * @param oname
	 * @param oid
	 * @param type
	 * @param Creply
	 */
	public Topic(ConstructorType ct, int id, int fid, String name, long date, String oname, int oid, int type, int Creply)
	{
		_id = id;
		_forumId = fid;
		_topicName = name;
		_date = date;
		_ownerName = oname;
		_ownerId = oid;
		_type = type;
		_cReply = Creply;
		TopicBBSManager.getInstance().addTopic(this);
		
		if (ct == ConstructorType.CREATE)
		{
			
			insertindb();
		}
	}
	
	/**
	 *
	 */
	public void insertindb()
	{
		java.sql.Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("INSERT INTO topic (topic_id,topic_forum_id,topic_name,topic_date,topic_ownername,topic_ownerid,topic_type,topic_reply) values (?,?,?,?,?,?,?,?)");
			statement.setInt(1, _id);
			statement.setInt(2, _forumId);
			statement.setString(3, _topicName);
			statement.setLong(4, _date);
			statement.setString(5, _ownerName);
			statement.setInt(6, _ownerId);
			statement.setInt(7, _type);
			statement.setInt(8, _cReply);
			statement.execute();
			statement.close();
			
		}
		catch (Exception e)
		{
			_log.warn("error while saving new Topic to db " + e);
		}
		finally
		{
			try
			{
				con.close();
			}
			catch (Exception e)
			{
			}
		}
		
	}
	
	public enum ConstructorType
	{
		RESTORE,
		CREATE
	}
	
	/**
	 * @return
	 */
	public int getID()
	{
		return _id;
	}
	
	public int getForumID()
	{
		return _forumId;
	}
	
	/**
	 * @return
	 */
	public String getName()
	{
		// TODO Auto-generated method stub
		return _topicName;
	}
	
	public String getOwnerName()
	{
		// TODO Auto-generated method stub
		return _ownerName;
	}
	
	/**
	 * @param f
	 */
	public void deleteme(Forum f)
	{
		TopicBBSManager.getInstance().delTopic(this);
		f.rmTopicByID(getID());
		java.sql.Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("DELETE FROM topic WHERE topic_id=? AND topic_forum_id=?");
			statement.setInt(1, getID());
			statement.setInt(2, f.getID());
			statement.execute();
			statement.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				con.close();
			}
			catch (Exception e)
			{
			}
		}
	}
	
	/**
	 * @return
	 */
	public long getDate()
	{
		return _date;
	}
}