package kz.tamur.or3.client.comps.interfaces;

import kz.tamur.lang.parser.ASTStart;
import kz.tamur.comps.OrGuiContainer;

/**
 * Created by IntelliJ IDEA.
 * User: Администратор
 * Date: 14.07.2006
 * Time: 17:04:39
 * To change this template use File | Settings | File Templates.
 */
public interface OrPanelComponent extends OrGuiContainer {
    ASTStart getAfterOpenTemplate();

    ASTStart getBeforeOpenTemplate();

    ASTStart getBeforeCloseTemplate();

    ASTStart getAfterCloseTemplate();

    ASTStart getCreateXmlTemplate();
    
    boolean isPanelEnabled();
    
    String getIconName();

}
