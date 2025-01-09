package kz.tamur.rt.adapters;

import kz.tamur.comps.*;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.rt.orlang.ClientOrLang;
import kz.tamur.rt.InterfaceManagerFactory;
import kz.tamur.lang.OrLang;
import kz.tamur.lang.parser.ASTStart;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.ButtonsFactory;

import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.DecimalFormat;
import java.awt.*;

import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.client.Kernel;
import com.cifs.or2.util.expr.Editor;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: erik-b
 * Date: 27.03.2009
 * Time: 10:05:36
 * To change this template use File | Settings | File Templates.
 */
public class MapAdapter extends ComponentAdapter {

    private DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
    private DecimalFormat dformat = null;
    private OrMap map;
    private boolean selfChange = false;
    private KrnObject _ifc, dynIfc;
    private OrRef dynIfcRef;
    private String _ifcTitle, contentPath;
    private OrRef contentRef, selectedRef;
    private OrRef autoCreateRef;
    private boolean hasClearBtn = false;
    private int refreshMode;
    private int cash;
    private ASTStart afterTemplate, beforTemplate,
            beforeModificationTemplate, dynamicIfcExprTemplate;
    private String selectedRefPath;
    private OrRef titleRef;
    private OrRef indexRef;
    private OrRef colorRef;
    private OrRef valueRef;
    private int[] selRows;
    private int actionFlag;
    private boolean copyFlag;
    private boolean ifcLock = false;
    private ImageIcon hpcImage = kz.tamur.rt.Utils.getImageIcon("HyperPopCol");
	private boolean showIcon = true;

    public MapAdapter(OrFrame frame, OrMap map, boolean isEditor)
            throws KrnException {
        super(frame, map, isEditor);

        Kernel krn = Kernel.instance();

        PropertyNode proot = map.getProperties();
        PropertyValue pv = map.getPropertyValue(
                proot.getChild("ref").getChild("refreshMode"));
        if (!pv.isNull()) {
            refreshMode = pv.intValue();
        }
        PropertyNode pn = proot.getChild("pov");
        pv = map.getPropertyValue(pn.getChild("cashFlag"));
        if (!pv.isNull()) {
            cash = pv.intValue();
        }
        //TitlePath
        PropertyNode prop = proot.getChild("ref").getChild("titlePath");
        pv = map.getPropertyValue(prop);
        String path = null;
        if (!pv.isNull()) {
            path = pv.stringValue();
            titleRef = OrRef.createRef(path, true, Mode.RUNTIME, frame.getRefs(),
                        frame.getTransactionIsolation(), frame);
            titleRef.addOrRefListener(this);
        }
        //IndexPath
        prop = proot.getChild("ref").getChild("indexRef");
        pv = map.getPropertyValue(prop);
        path = null;
        if (!pv.isNull()) {
            path = pv.stringValue();
            indexRef = OrRef.createRef(path, true, Mode.RUNTIME, frame.getRefs(),
                    frame.getTransactionIsolation(), frame);
            indexRef.addOrRefListener(this);
        }

        prop = proot.getChild("ref").getChild("colorRef");
        pv = map.getPropertyValue(prop);
        path = null;
        if (!pv.isNull()) {
            path = pv.stringValue();
            colorRef = OrRef.createRef(path, true, Mode.RUNTIME, frame.getRefs(),
                    frame.getTransactionIsolation(), frame);
            colorRef.addOrRefListener(this);
        }

        prop = proot.getChild("ref").getChild("valuePath");
        pv = map.getPropertyValue(prop);
        path = null;
        if (!pv.isNull()) {
            path = pv.stringValue();
            valueRef = OrRef.createRef(path, true, Mode.RUNTIME, frame.getRefs(),
                    frame.getTransactionIsolation(), frame);
            valueRef.addOrRefListener(this);
        }
        //содержимое
        PropertyNode rprop = proot.getChild("ref").getChild("content");
        pv = map.getPropertyValue(rprop);
        if (!pv.isNull()) {
            contentPath = pv.stringValue();
            try {
                if (!contentPath.equals("")) {
                    long contentFilterId = 0;
                    pv = map.getPropertyValue(proot.getChild("ref").getChild("contentFilter"));
                    if (!pv.isNull()) {
                        contentFilterId = pv.filterValue().getObjId();
                    }

                    if (refreshMode == Constants.RM_DIRECTLY) {
                        contentRef = OrRef.createRef(contentPath, false, Mode.RUNTIME, frame.getRefs(),
                                frame.getTransactionIsolation(), frame);
                    } else {
                        contentRef = OrRef.createContentRef(contentPath, contentFilterId, refreshMode, Mode.RUNTIME,
                                 frame.getTransactionIsolation(), true, frame);
                    }

                    if (contentFilterId > 0)
                        contentRef.setDefaultFilter(contentFilterId);

                    contentRef.addOrRefListener(this);
                }
            } catch(Exception ex) {
                Util.showErrorMessage(map, ex.getMessage(), "Содержимое");
            }
        }

        pv = map.getPropertyValue(proot.getChild("pov").getChild("act").getChild("callDialog"));
        if (!pv.isNull()) {
            KrnObject oo = new KrnObject(Long.parseLong(pv.getKrnObjectId()), "", krn.getClassByName(pv.getKrnClassName()).id);
            _ifc = oo;
            _ifcTitle = pv.getTitle();
        }
        this.map = map;

        pv = map.getPropertyValue(proot.getChild("pov").getChild("act").getChild("actionJobAfter"));
        String expr = null;
        if (!pv.isNull()) {
            expr = pv.stringValue();
        }
        if (expr != null && expr.length() > 0) {
            afterTemplate = OrLang.createStaticTemplate(expr);
            try {
                Editor e = new Editor(expr);
                ArrayList<String> paths = e.getRefPaths();
                for (int j = 0; j < paths.size(); ++j) {
                    path = paths.get(j);
                    OrRef.createRef(path, false, Mode.RUNTIME, frame.getRefs(),
                            OrRef.TR_CLEAR, frame);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        pv = map.getPropertyValue(proot.getChild("pov").getChild("act").getChild("actionJobBefore"));
        expr = null;
        if (!pv.isNull()) {
            expr = pv.stringValue();
        }
        if (expr != null && expr.length() > 0) {
            beforeModificationTemplate = OrLang.createStaticTemplate(expr);
            try {
                Editor e = new Editor(expr);
                ArrayList<String> paths = e.getRefPaths();
                for (int j = 0; j < paths.size(); ++j) {
                    path = paths.get(j);
                    OrRef.createRef(path, false, Mode.RUNTIME, frame.getRefs(),
                            OrRef.TR_CLEAR, frame);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        pv = map.getPropertyValue(proot.getChild("pov").getChild("dynamicIfcExpr"));
        String dynIfcExpr = null;
        if (!pv.isNull()) {
            dynIfcExpr = pv.stringValue();
        }
        if (dynIfcExpr != null && dynIfcExpr.length() > 0) {
            dynamicIfcExprTemplate = OrLang.createStaticTemplate(dynIfcExpr);
            try {
                Editor e = new Editor(dynIfcExpr);
                ArrayList<String> paths = e.getRefPaths();
                for (int j = 0; j < paths.size(); ++j) {
                    path = paths.get(j);
                    OrRef.createRef(path, false, Mode.RUNTIME, frame.getRefs(),
                            OrRef.TR_CLEAR, frame);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        pn = map.getProperties().getChild("pov").getChild("ifcLock");
        pv = map.getPropertyValue(pn);
        if (!pv.isNull()) {
            ifcLock = pv.booleanValue();
        }
        pv = map.getPropertyValue(proot.getChild("pov").getChild("dynamicIfc"));
        if (!pv.isNull()) {
            try {
                propertyName = "Свойство: Динамический интерфейс";
                dynIfcRef = OrRef.createRef(pv.stringValue(), false, Mode.RUNTIME, frame.getRefs(),
                        frame.getTransactionIsolation(), frame);
                dynIfcRef.addOrRefListener(this);
            } catch (Exception e) {
                if (e instanceof RuntimeException) {
                    showErrorNessage(e.getMessage());
                }
                e.printStackTrace();
            }
        }
        this.map.setXml(null);
        //this.hpopup.setBackground(Color.red);
    }

    public void clear() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public OrRef getIndexRef() {
        return indexRef;
    }

    public OrRef getTitleRef() {
        return titleRef;
    }

    public OrRef getColorRef() {
        return colorRef;
    }

    public OrRef getValueRef() {
        return valueRef;
    }

    // OrRefListener
    public void valueChanged(OrRefEvent e) {
        super.valueChanged(e);
        if (e.getOriginator() != this && !selfChange) {
            OrRef ref = e.getRef();
            if (ref != null && ref == titleRef) {
                selfChange = true;
                map.repaint();
                selfChange = false;
            }
        }
    }

    public void actionPerformed(int selectedIndex) {
        try {
            kz.tamur.rt.InterfaceManager mgr = InterfaceManagerFactory.instance().getManager();

            if (mgr != null) {
                long tr_id = mgr.getCash().getTransactionId();
                KrnObject[] objs = null;
                if (dataRef != null) {
                    dataRef.absolute(selectedIndex, this);
                    OrRef.Item item = dataRef.getItem(langId);
                    if (item != null && item.getCurrent() != null)
                        objs = new KrnObject[]{(KrnObject) item.getCurrent()};
                }

                UIFrame frm = null;
                if (_ifc != null) {
                    frm = mgr.getInterfacePanel(_ifc, objs, tr_id,
                            frame.getEvaluationMode(), (cash & 0x01) > 0, false);
                } else if (dynIfcRef != null) {
                    OrRef.Item item  = dynIfcRef.getItem(langId);
                    dynIfc = (KrnObject) ((item != null) ? item.getCurrent() : null);
                    if (dynIfc != null) {
                        frm = mgr.getInterfacePanel(dynIfc, objs, tr_id,
                                frame.getEvaluationMode(), (cash & 0x01) > 0, false);
                    }
                } else if (dynamicIfcExprTemplate != null) {
                    ClientOrLang orlang = new ClientOrLang(frame);
                    Map vc = new HashMap();
                    try {
                        orlang.evaluate(dynamicIfcExprTemplate, vc, this, new Stack<String>());
                        Object res = vc.get("RETURN");
                        if (res != null && res instanceof KrnObject) {
                            dynIfc = (KrnObject)res;
                            frm = mgr.getInterfacePanel(dynIfc, objs, tr_id,
                                    frame.getEvaluationMode(), (cash & 0x01) > 0, false);
                        }
                    } catch (Exception ex) {
                        Util.showErrorMessage(map, ex.getMessage(),
                                "Динамический интерфейс (Выражение)");
                    }
                }
                if (frm == null) {
                    MessagesFactory.showMessageDialog(map.getTopLevelAncestor(), MessagesFactory.ERROR_MESSAGE, "Не задан интерфейс обработки!");
                    return;
                }
                frm.getRef().fireValueChangedEvent(-1, this, 0);

                int mode = frm.getEvaluationMode();
                PanelAdapter pa = frm.getPanelAdapter();
                OrPanel p = (OrPanel)frm.getPanel();
                String title = p.getTitle();
                DesignerDialog dlg = Utils.getDesignerDialog(map.getTopLevelAncestor(),
                        title, p, false);
                dlg.setLanguage(frm.getInterfaceLang().id);
                dlg.setInitiator(map);
                //frm.getRef().absolute(index, hpopup);
                boolean ifcEnabled = !ifcLock;
                if (ifcEnabled) {
                    ifcEnabled = !(mode == kz.tamur.rt.InterfaceManager.ARCH_RO_MODE);
                }
                if (ifcEnabled) {
                    ifcEnabled = !(mode == kz.tamur.rt.InterfaceManager.READONLY_MODE);
                }
                pa.setEnabled(ifcEnabled);
                dlg.setFirstRow(frm);
                Dimension pSize = p.getPrefSize();
                dlg.setSize(pSize != null ? pSize : new Dimension(800, 600));
                dlg.setLocation(Utils.getCenterLocationPoint(dlg.getSize()));
                dlg.show();
                if (dlg.isOK()) {
                        // Получаем спсок выбранных объектов
                    if ((cash & 0x01) > 0)
                        frm.getRef().fireValueChangedEvent(-1, this, 0);
                    mgr.releaseInterface(true);

                }  else if (dlg.getResult() == ButtonsFactory.BUTTON_CLEAR) {
                    mgr.releaseInterface(false);
                } else if (dlg.getResult() == ButtonsFactory.BUTTON_CANCEL) {
                    mgr.releaseInterface(false);
                }
            }
        } catch (KrnException e1) {
            e1.printStackTrace();
        }

    }
}
