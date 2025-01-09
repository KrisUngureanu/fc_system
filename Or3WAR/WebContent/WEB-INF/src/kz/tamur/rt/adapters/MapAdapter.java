package kz.tamur.rt.adapters;

import kz.tamur.comps.*;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.rt.orlang.ClientOrLang;
import kz.tamur.rt.InterfaceManagerFactory;
import kz.tamur.lang.OrLang;
import kz.tamur.lang.parser.ASTStart;
import kz.tamur.or3.client.comps.interfaces.OrMapComponent;

import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.client.Kernel;
import com.cifs.or2.util.expr.Editor;

/**
 * Created by IntelliJ IDEA.
 * User: erik-b
 * Date: 27.03.2009
 * Time: 10:05:36
 * To change this template use File | Settings | File Templates.
 */
public class MapAdapter extends ComponentAdapter {

    private DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
    private OrMapComponent map;
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

    private int count = 0, maxCount = 0;

    public MapAdapter(OrFrame frame, OrMapComponent map, boolean isEditor)
            throws KrnException {
        super(frame, map, isEditor);

        Kernel krn = frame.getKernel();

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
        //IndexPath
        PropertyNode prop = proot.getChild("ref").getChild("indexRef");
        pv = map.getPropertyValue(prop);
        String path = null;
        if (!pv.isNull()) {
            path = pv.stringValue(frame.getKernel());
            indexRef = OrRef.createRef(path, true, Mode.RUNTIME, frame.getRefs(),
                    frame.getTransactionIsolation(), frame);
            indexRef.addOrRefListener(this);
            maxCount++;
        }

        prop = proot.getChild("ref").getChild("colorRef");
        pv = map.getPropertyValue(prop);
        path = null;
        if (!pv.isNull()) {
            path = pv.stringValue(frame.getKernel());
            colorRef = OrRef.createRef(path, true, Mode.RUNTIME, frame.getRefs(),
                    frame.getTransactionIsolation(), frame);
            colorRef.addOrRefListener(this);
            maxCount++;
        }

        prop = proot.getChild("ref").getChild("valuePath");
        pv = map.getPropertyValue(prop);
        path = null;
        if (!pv.isNull()) {
            path = pv.stringValue(frame.getKernel());
            valueRef = OrRef.createRef(path, true, Mode.RUNTIME, frame.getRefs(),
                    frame.getTransactionIsolation(), frame);
            valueRef.addOrRefListener(this);
            maxCount++;
        }
        //TitlePath
        prop = proot.getChild("ref").getChild("titlePath");
        pv = map.getPropertyValue(prop);
        path = null;
        if (!pv.isNull()) {
            path = pv.stringValue(frame.getKernel());
            titleRef = OrRef.createRef(path, true, Mode.RUNTIME, frame.getRefs(),
                        frame.getTransactionIsolation(), frame);
            titleRef.addOrRefListener(this);
            maxCount++;
        }
        //содержимое
        PropertyNode rprop = proot.getChild("ref").getChild("content");
        pv = map.getPropertyValue(rprop);
        if (!pv.isNull()) {
            contentPath = pv.stringValue(frame.getKernel());
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
            expr = pv.stringValue(frame.getKernel());
        }
        if (expr != null && expr.length() > 0) {
            afterTemplate = OrLang.createStaticTemplate(expr, log);
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
            expr = pv.stringValue(frame.getKernel());
        }
        if (expr != null && expr.length() > 0) {
            beforeModificationTemplate = OrLang.createStaticTemplate(expr, log);
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
            dynIfcExpr = pv.stringValue(frame.getKernel());
        }
        if (dynIfcExpr != null && dynIfcExpr.length() > 0) {
            dynamicIfcExprTemplate = OrLang.createStaticTemplate(dynIfcExpr, log);
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

        pv = map.getPropertyValue(proot.getChild("pov").getChild("dynamicIfc"));
        if (!pv.isNull()) {
            try {
                propertyName = "Свойство: Динамический интерфейс";
                dynIfcRef = OrRef.createRef(pv.stringValue(frame.getKernel()), false, Mode.RUNTIME, frame.getRefs(),
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
            if (ref != null) {
                if (ref == indexRef || ref == titleRef || ref == valueRef || ref == colorRef) {
                    selfChange = true;
                    map.refresh();
                    selfChange = false;
                    count = 0;
                }
            }
        }
    }

    public OrFrame getPopupFrame() {
        try {
            kz.tamur.rt.InterfaceManager mgr = frame.getInterfaceManager();

            if (mgr != null) {
                long tr_id = mgr.getCash().getTransactionId();
                KrnObject[] objs = null;
                if (dataRef != null) {
                    OrRef.Item item = dataRef.getItem(langId);
                    if (item != null && item.getCurrent() != null)
                        objs = new KrnObject[]{(KrnObject) item.getCurrent()};
                }

                OrFrame frm = null;
                if (_ifc != null) {
                    frm = mgr.getInterfacePanel(_ifc, objs, tr_id,
                            frame.getEvaluationMode(), (cash & 0x01) > 0, false, true);
                } else if (dynIfcRef != null) {
                    OrRef.Item item  = dynIfcRef.getItem(langId);
                    dynIfc = (KrnObject) ((item != null) ? item.getCurrent() : null);
                    if (dynIfc != null) {
                        frm = mgr.getInterfacePanel(dynIfc, objs, tr_id,
                                frame.getEvaluationMode(), (cash & 0x01) > 0, false, true);
                    }
                } else if (dynamicIfcExprTemplate != null) {
                    ClientOrLang orlang = new ClientOrLang(frame);
                    Map<String, Object> vc = new HashMap<String, Object>();
                    boolean calcOwner = OrCalcRef.setCalculations();
                    try {
                        orlang.evaluate(dynamicIfcExprTemplate, vc, this, new Stack<String>());
                        Object res = vc.get("RETURN");
                        if (res != null && res instanceof KrnObject) {
                            dynIfc = (KrnObject)res;
                            frm = mgr.getInterfacePanel(dynIfc, objs, tr_id,
                                    frame.getEvaluationMode(), (cash & 0x01) > 0, false, true);
                        }
                    } catch (Exception ex) {
                        Util.showErrorMessage(map, ex.getMessage(),
                                "Динамический интерфейс (Выражение)");
                    	log.error("Ошибка при выполнении формулы 'Динамический интерфейс (Выражение)' компонента '" + (comp != null ? comp.getClass().getName() : "") + "', uuid: " + getUUID());
                        log.error(ex, ex);
        	        } finally {
        				if (calcOwner)
        					OrCalcRef.makeCalculations();
                    }
                }
                if (frm == null) {
                    return null;
                }
                frm.getRef().fireValueChangedEvent(-1, this, 0);

                return frm;
            }
        } catch (KrnException e1) {
            e1.printStackTrace();
        }
        return null;
    }

    public void okPressed(OrFrame frm) {
        kz.tamur.rt.InterfaceManager mgr = InterfaceManagerFactory.instance().getManager();
        if ((cash & 0x01) > 0)
            frm.getRef().fireValueChangedEvent(-1, this, 0);
        mgr.releaseInterface(true);
    }
}
