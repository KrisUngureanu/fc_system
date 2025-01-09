package com.cifs.or2.server.workflow.definition;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 15.08.2004
 * Time: 14:09:06
 * To change this template use File | Settings | File Templates.
 */
public interface ProcessBlock extends DefinitionObject {
    Collection getNodes();
    Collection getAttributes();
    Node getNode(String id);
    ActivityState createActivityState();
    Join createJoin();
    void addNode(Node node);
    void removeNode(Node node);
    ProcessBlock getParentBlock();
    Collection getChildBlocks();
    ConcurrentBlock createConcurrentBlock();
    void removeChildBlock(ProcessBlock block);
    void addChildBlock(ProcessBlock block);
}
