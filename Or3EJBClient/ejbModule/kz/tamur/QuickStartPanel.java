package kz.tamur;

import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.users.Or3RightsNode;
import kz.tamur.rt.MainFrame;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import com.cifs.or2.client.User;
import com.cifs.or2.util.CursorToolkit;

/**
 * Created by IntelliJ IDEA.
 * User: Администратор
 * Date: 12.11.2004
 * Time: 12:08:11
 * To change this template use File | Settings | File Templates.
 */
public class QuickStartPanel extends JPanel implements ActionListener {

    private JButton newServiceBtn = ButtonsFactory.createToolButton("CreateServiceBig", "Открыть процессы", true);
    private JButton openFiltersBtn = ButtonsFactory.createToolButton("FiltersBig", "Открыть фильтры", true);
    private JButton newInterfaceBtn = ButtonsFactory.createToolButton("CreateIfcBig", "Открыть интерфейс", true);
    private JButton baseBtn = ButtonsFactory.createToolButton("BaseBig", "Структура баз данных", true);
    private JButton openClassesBtn = ButtonsFactory.createToolButton("ClassesBig", "Открыть классы", true);
    private JButton openUsersBtn = ButtonsFactory.createToolButton("UsersBig", "Открыть управление пользователями", true);
    private JButton openReportsBtn = ButtonsFactory.createToolButton("ReportsBig", "Открыть отчёты", true);
    private JButton helpBtn = ButtonsFactory.createToolButton("HyperBig", "Открыть гиперменю", true);
    private JButton scheduleBtn = ButtonsFactory.createToolButton("SchedBig", "Открыть планировщик", true);
    private JButton boxBtn = ButtonsFactory.createToolButton("BoxBig", "Открыть пункты обмена", true);
    private JButton funcBtn = ButtonsFactory.createToolButton("FuncBig", "Общие функции", true);
    private JButton replBtn = ButtonsFactory.createToolButton("ReplBig", "Репликация баз данных", true);
    private JButton rightsBtn = ButtonsFactory.createToolButton("RightsBig", "Открыть права доступа", true);
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
    
    private Or3Frame mainFrm;

    public QuickStartPanel(Or3Frame frm) {
        super(new BorderLayout());
        mainFrm = frm;
        initButtons();
        JPanel p = new JPanel(new GridLayout(3, 4, 20, 20));
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        p.add(newServiceBtn);
        p.add(openClassesBtn);
        p.add(newInterfaceBtn);
        p.add(openFiltersBtn);
        p.add(openUsersBtn);
        p.add(openReportsBtn);
        p.add(helpBtn);
        p.add(baseBtn);
        p.add(scheduleBtn);
        p.add(boxBtn);
        p.add(funcBtn);
        p.add(replBtn);
        p.add(rightsBtn);
        p.setOpaque(isOpaque);
        add(p, BorderLayout.CENTER);
        for (int i = 0; i < p.getComponentCount(); i++) {
            Component c = p.getComponent(i);
            if (c instanceof JButton) {
                ((JButton)c).addActionListener(this);
            }
        }
    }

    private void initButtons() {
        newServiceBtn.setPreferredSize(new Dimension(72, 72));
        newServiceBtn.setMaximumSize(new Dimension(72, 72));
        newServiceBtn.setMinimumSize(new Dimension(72, 72));

        openFiltersBtn.setPreferredSize(new Dimension(72, 72));
        openFiltersBtn.setMaximumSize(new Dimension(72, 72));
        openFiltersBtn.setMinimumSize(new Dimension(72, 72));

        newInterfaceBtn.setPreferredSize(new Dimension(72, 72));
        newInterfaceBtn.setMaximumSize(new Dimension(72, 72));
        newInterfaceBtn.setMinimumSize(new Dimension(72, 72));

        baseBtn.setPreferredSize(new Dimension(72, 72));
        baseBtn.setMaximumSize(new Dimension(72, 72));
        baseBtn.setMinimumSize(new Dimension(72, 72));

        openClassesBtn.setPreferredSize(new Dimension(72, 72));
        openClassesBtn.setMaximumSize(new Dimension(72, 72));
        openClassesBtn.setMinimumSize(new Dimension(72, 72));

        openUsersBtn.setPreferredSize(new Dimension(72, 72));
        openUsersBtn.setMaximumSize(new Dimension(72, 72));
        openUsersBtn.setMinimumSize(new Dimension(72, 72));

        openReportsBtn.setPreferredSize(new Dimension(72, 72));
        openReportsBtn.setMaximumSize(new Dimension(72, 72));
        openReportsBtn.setMinimumSize(new Dimension(72, 72));

        helpBtn.setPreferredSize(new Dimension(72, 72));
        helpBtn.setMaximumSize(new Dimension(72, 72));
        helpBtn.setMinimumSize(new Dimension(72, 72));
    }

    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        DesignerDialog parent = (DesignerDialog)getTopLevelAncestor();
        CursorToolkit.startWaitCursor(this);
        if (src == newServiceBtn) {
            mainFrm.quickStartService(true);
        } else if (src == newInterfaceBtn) {
            mainFrm.quickStartIfc(true);
        } else if (src == openClassesBtn) {
            mainFrm.quickStartClasses();
        } else if (src == openFiltersBtn) {
            mainFrm.quickStartFilters(true);
        } else if (src == openUsersBtn) {
            mainFrm.quickStartUsers();
        } else if (src == openReportsBtn) {
            mainFrm.quickStartReports();
        } else if (src == helpBtn) {
            mainFrm.quickStartHypers();
        } else if (src == baseBtn) {
            mainFrm.quickStartBase();
        } else if (src == boxBtn) {
            mainFrm.quickStartBoxes();
        } else if (src == scheduleBtn) {
            mainFrm.quickStartScheduler();
        } else if (src == funcBtn) {
            mainFrm.quickStartXmlFrame();
        } else if (src == replBtn) {
            mainFrm.quickStartReplFrame();
        } else if (src == rightsBtn) {
            mainFrm.quickStartUserRights();
        }
        parent.dispose();
        CursorToolkit.stopWaitCursor(this);
    }

    public void disableDeveloperButtons() {
        newServiceBtn.setEnabled(false);
        openFiltersBtn.setEnabled(false);
        newInterfaceBtn.setEnabled(false);
        baseBtn.setEnabled(false);
        openClassesBtn.setEnabled(false);
        openReportsBtn.setEnabled(false);
        helpBtn.setEnabled(false);
        funcBtn.setEnabled(false);
        rightsBtn.setEnabled(false);
    }

    public void applyViewRights(User user) {
        boolean res = user.hasRight(Or3RightsNode.PROCESS_VIEW_RIGHT);
        newServiceBtn.setEnabled(res);

        res = user.hasRight(Or3RightsNode.CLASSES_VIEW_RIGHT);
        openClassesBtn.setEnabled(res);

        res = user.hasRight(Or3RightsNode.INTERFACE_VIEW_RIGHT);
        newInterfaceBtn.setEnabled(res);

        res = user.hasRight(Or3RightsNode.BASES_VIEW_RIGHT);
        baseBtn.setEnabled(res);

        res = user.hasRight(Or3RightsNode.BOXES_VIEW_RIGHT);
        boxBtn.setEnabled(res);

        res = user.hasRight(Or3RightsNode.FUNCS_VIEW_RIGHT);
        funcBtn.setEnabled(res);

        res = user.hasRight(Or3RightsNode.FILTERS_VIEW_RIGHT);
        openFiltersBtn.setEnabled(res);

        res = user.hasRight(Or3RightsNode.USERS_VIEW_RIGHT);
        openUsersBtn.setEnabled(res);

        res = user.hasRight(Or3RightsNode.REPORTS_VIEW_RIGHT);
        openReportsBtn.setEnabled(res);

        res = user.hasRight(Or3RightsNode.MENU_VIEW_RIGHT);
        helpBtn.setEnabled(res);

        res = user.hasRight(Or3RightsNode.TASKS_VIEW_RIGHT);
        scheduleBtn.setEnabled(res);

        res = user.hasRight(Or3RightsNode.USER_RIGHT_VIEW_RIGHT);
        rightsBtn.setEnabled(res);

        res = user.hasRight(Or3RightsNode.REPLICATION_VIEW_RIGHT);
        replBtn.setEnabled(res);        
    }
}
