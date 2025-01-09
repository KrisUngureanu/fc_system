package com.cifs.or2.server.workflow.definition;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 15.08.2004
 * Time: 14:15:36
 * To change this template use File | Settings | File Templates.
 */
public interface ConcurrentBlock extends ProcessBlock {
  Fork getFork();
  Join getJoin();
    Fork createFork();
}
