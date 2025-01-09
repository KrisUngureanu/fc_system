package kz.tamur.guidesigner;

import java.awt.Rectangle;

import com.cifs.or2.kernel.KrnObject;

import kz.tamur.guidesigner.service.ServiceNode;
import kz.tamur.guidesigner.service.ui.StateNode;
import kz.tamur.guidesigner.service.ui.TransitionEdge;

public interface ProcessFrameTemplate {

	long getRusLang();

	void setProcessModified(boolean b);

	void addEdge(TransitionEdge edge, KrnObject obj);
	void removeEdge(TransitionEdge edge, KrnObject obj);
	void addNode(StateNode node, KrnObject obj);
	void removeNode(StateNode node, KrnObject obj);

	void setSelectionMode();

	void changeNodeLocation(KrnObject obj, StateNode owner,
			Rectangle boundsOld, Rectangle boundsNew);

	boolean getUndoRedoCall(KrnObject obj);

	ServiceNode findServiceNode(KrnObject obj);

	void open(KrnObject process, String title, KrnObject nodeObj);
}
