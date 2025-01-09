package kz.tamur.guidesigner;

import com.cifs.or2.client.gui.OrMultiLineToolTip;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 29.08.2005
 * Time: 12:00:21
 * To change this template use File | Settings | File Templates.
 */
public class SearchResultTable extends JTable {
    public JToolTip createToolTip() {
        return new OrMultiLineToolTip();
    }
}
