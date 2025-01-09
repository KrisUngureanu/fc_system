package kz.tamur.guidesigner.service.cmd;

import org.tigris.gef.base.ModeCreateFigLine;
import org.tigris.gef.presentation.Fig;

import java.awt.event.MouseEvent;

import kz.tamur.guidesigner.service.fig.FigLineEdge;

/**
 * Created by IntelliJ IDEA.
 * User: Vale
 * Date: 21.03.2005
 * Time: 12:22:51
 * To change this template use File | Settings | File Templates.
 */
public class ModeCreateFigLineEdge extends ModeCreateFigLine{
    public Fig createNewItem(MouseEvent me, int snapX, int snapY) {
      return new FigLineEdge(snapX, snapY, snapX, snapY, true);
    }
}
