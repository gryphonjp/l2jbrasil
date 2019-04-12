# contributed by kerberos_20 to the Official L2J Datapack Project.
# Visit http://forum.l2jdp.com for more details.
import sys
from com.l2jbr.gameserver.model.quest import State
from com.l2jbr.gameserver.model.quest import QuestState
from com.l2jbr.gameserver.model.quest.jython import QuestJython as JQuest

qn = "6111_ElrokiTeleporters"

class Quest (JQuest) :

 def __init__(self,id,name,descr): 
   JQuest.__init__(self,id,name,descr)
 
 def onTalk (self,npc,player):
    npcId = npc.getNpcId()
    if npcId == 32111 :
        player.teleToLocation(4990,-1879,-3178)
    if npcId == 32112 :
        player.teleToLocation(7557,-5513,-3221)
    return

QUEST       = Quest(6111, qn, "Teleports")
CREATED     = State('Start', QUEST)

QUEST.setInitialState(CREATED)

QUEST.addStartNpc(32111)
QUEST.addTalkId(32111)
QUEST.addStartNpc(32112)
QUEST.addTalkId(32112)