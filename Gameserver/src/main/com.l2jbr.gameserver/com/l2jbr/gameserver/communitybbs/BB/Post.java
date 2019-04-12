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
import com.l2jbr.gameserver.communitybbs.Manager.PostBBSManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;


/**
 * @author Maktakien
 */
public class Post {
    private static Logger _log = LoggerFactory.getLogger(Post.class.getName());

    public class CPost {
        public int postId;
        public String postOwner;
        public int postOwnerId;
        public long postDate;
        public int postTopicId;
        public int postForumId;
        public String postTxt;
    }

    private final List<CPost> _post;

    /**
     * @param _PostOwner
     * @param _PostOwnerID
     * @param date
     * @param tid
     * @param _PostForumID
     * @param txt
     */
    public Post(String _PostOwner, int _PostOwnerID, long date, int tid, int _PostForumID, String txt) {
        _post = new LinkedList<>();
        CPost cp = new CPost();
        cp.postId = 0;
        cp.postOwner = _PostOwner;
        cp.postOwnerId = _PostOwnerID;
        cp.postDate = date;
        cp.postTopicId = tid;
        cp.postForumId = _PostForumID;
        cp.postTxt = txt;
        _post.add(cp);
        insertindb(cp);

    }

    public void insertindb(CPost cp) {
        java.sql.Connection con = null;
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = con.prepareStatement("INSERT INTO posts (post_id,post_owner_name,post_ownerid,post_date,post_topic_id,post_forum_id,post_txt) values (?,?,?,?,?,?,?)");
            statement.setInt(1, cp.postId);
            statement.setString(2, cp.postOwner);
            statement.setInt(3, cp.postOwnerId);
            statement.setLong(4, cp.postDate);
            statement.setInt(5, cp.postTopicId);
            statement.setInt(6, cp.postForumId);
            statement.setString(7, cp.postTxt);
            statement.execute();
            statement.close();
        } catch (Exception e) {
            _log.warn("error while saving new Post to db " + e);
        } finally {
            try {
                con.close();
            } catch (Exception e) {
            }
        }

    }

    public Post(Topic t) {
        _post = new LinkedList<>();
        load(t);
    }

    public CPost getCPost(int id) {
        int i = 0;
        for (CPost cp : _post) {
            if (i == id) {
                return cp;
            }
            i++;
        }
        return null;
    }

    public void deleteme(Topic t) {
        PostBBSManager.getInstance().delPostByTopic(t);
        java.sql.Connection con = null;
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = con.prepareStatement("DELETE FROM posts WHERE post_forum_id=? AND post_topic_id=?");
            statement.setInt(1, t.getForumID());
            statement.setInt(2, t.getID());
            statement.execute();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (Exception e) {
            }
        }
    }

    /**
     * @param t
     */
    private void load(Topic t) {
        java.sql.Connection con = null;
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = con.prepareStatement("SELECT * FROM posts WHERE post_forum_id=? AND post_topic_id=? ORDER BY post_id ASC");
            statement.setInt(1, t.getForumID());
            statement.setInt(2, t.getID());
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                CPost cp = new CPost();
                cp.postId = Integer.parseInt(result.getString("post_id"));
                cp.postOwner = result.getString("post_owner_name");
                cp.postOwnerId = Integer.parseInt(result.getString("post_ownerid"));
                cp.postDate = Long.parseLong(result.getString("post_date"));
                cp.postTopicId = Integer.parseInt(result.getString("post_topic_id"));
                cp.postForumId = Integer.parseInt(result.getString("post_forum_id"));
                cp.postTxt = result.getString("post_txt");
                _post.add(cp);
            }
            result.close();
            statement.close();
        } catch (Exception e) {
            _log.warn("data error on Post " + t.getForumID() + "/" + t.getID() + " : " + e);
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (Exception e) {
            }
        }
    }

    /**
     * @param i
     */
    public void updatetxt(int i) {
        java.sql.Connection con = null;
        try {
            CPost cp = getCPost(i);
            con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = con.prepareStatement("UPDATE posts SET post_txt=? WHERE post_id=? AND post_topic_id=? AND post_forum_id=?");
            statement.setString(1, cp.postTxt);
            statement.setInt(2, cp.postId);
            statement.setInt(3, cp.postTopicId);
            statement.setInt(4, cp.postForumId);
            statement.execute();
            statement.close();
        } catch (Exception e) {
            _log.warn("error while saving new Post to db " + e);
        } finally {
            try {
                con.close();
            } catch (Exception e) {
            }
        }

    }
    /**
     *
     */

}
