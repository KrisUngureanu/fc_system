package kz.tamur.web.component;

import kz.tamur.rt.adapters.ColumnAdapter;
import java.util.List;


public class OrWebTableFooter {

    private List<ColumnAdapter> columns;
/*
    private JMenuItem noMenuItem = createMenuItem("�����������");
    private JMenuItem sumMenuItem = createMenuItem("�����");
    private JMenuItem averageMenuItem = createMenuItem("�������");
    private JMenuItem maxMenuItem = createMenuItem("������������");
    private JMenuItem minMenuItem = createMenuItem("�����������");
    private JMenuItem trueMenuItem = createMenuItem("���-�� �������", "trueCount");
    private JMenuItem countMenuItem = createMenuItem("����������");
*/
    private ColumnAdapter lastSelectedColumn = null;

    public OrWebTableFooter(List<ColumnAdapter> columns) {
        this.columns = columns;
        init();
    }

    private void init() {
    }
}
