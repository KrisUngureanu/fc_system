package kz.tamur.rt.adapters;

import java.util.EventListener;
import java.util.List;
import java.util.Stack;

import kz.tamur.util.Pair;
import kz.tamur.util.ReqMsgsList;

public interface OrRefListener extends EventListener {
    public void valueChanged(OrRefEvent e);

    public void changesCommitted(OrRefEvent e);

    public void changesRollbacked(OrRefEvent e);

    public void pathChanged(OrRefEvent e);

    public void checkReqGroups(OrRef ref, List<ReqMsgsList.MsgListItem> errMsgs, List<ReqMsgsList.MsgListItem> reqMsgs, Stack<Pair> locs);

    public void clear();

    public void stateChanged(OrRefEvent e);
}
