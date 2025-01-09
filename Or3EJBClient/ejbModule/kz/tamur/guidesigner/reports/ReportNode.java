package kz.tamur.guidesigner.reports;

import static com.cifs.or2.client.Kernel.SC_REPORT_PRINTER;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.ClassNode;
import com.cifs.or2.client.util.AttrRequestBuilder;
import com.cifs.or2.kernel.*;

import kz.tamur.comps.*;
import kz.tamur.ods.Value;
import kz.tamur.rt.TreeUIDMap;
import kz.tamur.util.AbstractDesignerTreeNode;

import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import java.io.ByteArrayInputStream;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 13.10.2004
 * Time: 11:31:34
 */
public class ReportNode extends AbstractDesignerTreeNode {

    private OrGuiComponent comp;
    private boolean isModified = false;
    private OrFrame emptyFrame;
    private List<KrnObject> bases = new ArrayList<KrnObject>();
    private List<Object> oldBases = new ArrayList<Object>();
    private Kernel krn;
    private static KrnAttribute basesAttr  = Kernel.instance().getAttributeByNameTracing(SC_REPORT_PRINTER, "bases");
    private static KrnAttribute configAttr  = Kernel.instance().getAttributeByNameTracing(SC_REPORT_PRINTER, "config");

    public ReportNode(KrnObject uiObj, String title, OrGuiComponent comp, int index, OrFrame frame) {
        this(uiObj, title, comp, index, frame, null);
    }

    public ReportNode(KrnObject uiObj, String title, OrGuiComponent comp, int index, OrFrame frame, List<KrnObject> bases) {
    	nodeType = REPORT_NODE;
        krn = Kernel.instance();
        krnObj = uiObj;
        isLoaded = false;
        this.title = title;
        this.comp = comp;
        this.emptyFrame = frame;
        if (bases != null) {
            this.bases = bases;
            oldBases.clear();
            oldBases.addAll(bases);
            if (comp instanceof OrReportPrinter) {
                ((OrReportPrinter) comp).setReportNode(this);
            }
        }
    }

    public OrGuiComponent getOrGuiComponent() {
        return comp;
    }

    public boolean isLeaf() {
        return krnObj.classId == SC_REPORT_PRINTER.id;
    }

    public void setTitle(String newName) {
        title = newName;
    }

    public void rename(String newName) {
        title = newName;
        try {
            krn.setString(krnObj.id, krnObj.classId, "title", 0, emptyFrame.getInterfaceLang().id, title, 0);
        } catch (KrnException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            ClassNode cnode = krn.getClassNode(krnObj.classId);
            KrnAttribute basesAttr = cnode.getAttribute("bases");
            if (basesAttr != null) {
                if (oldBases != null && oldBases.size() > 0) {
                    krn.deleteValue(krnObj.id, krnObj.classId, "bases", oldBases, 0);
                }
                if (bases != null && bases.size() > 0) {
                    for (KrnObject base : bases) {
                        krn.setObject(krnObj.id, krnObj.classId, "bases", 0, base.id, 0, true);
                    }
                }
                oldBases.clear();
                oldBases.addAll(bases);
            } else {
                System.out.println("Attribute \"bases\" not found!");
            }

        } catch (KrnException e) {
            e.printStackTrace();
        }
    }

    protected void load() {
        if (!isLoaded) {
            isLoaded = true;
            if (!isLeaf()) {
                long[] oids = { krnObj.id };
                ObjectValue[] ovs = new ObjectValue[0];
                try {
                    ovs = krn.getObjectValues(oids, krnObj.classId, "children", 0);
                    long[] ids = new long[ovs.length];
                    for (int i = 0; i < ovs.length; i++) {
                        ids[i] = ovs[i].value.id;
                    }
                    
                    AttrRequestBuilder arb = new AttrRequestBuilder(SC_REPORT_PRINTER, krn) .add("title", emptyFrame.getInterfaceLang().id).add("bases").add("config");

                    final Map<Long, String> str_m = new HashMap<Long, String>();
                    final Map<Long, List<KrnObject>> bases_ = new HashMap<Long, List<KrnObject>>();
                    final Map<Long, byte[]> configs_ = new HashMap<Long, byte[]>();

                    List<Object[]> rows = krn.getObjects(ids, arb.build(), 0);
                    for (Object[] row : rows) {
                        KrnObject obj = (KrnObject) row[0];

                        if (row[2] != null) {
                            str_m.put(obj.id, (String) row[2]);
                        }
                        
                        int count = 2;
                        if (basesAttr != null && row[++count] != null) {
                            List<Value> vs = (List<Value>) row[count];
                            for (Value v : vs) {
                                KrnObject base = (KrnObject) v.value;
                                List<KrnObject> arr = bases_.get(obj.id);
                                if (arr == null) {
                                    arr = new ArrayList<KrnObject>();
                                    bases_.put(obj.id, arr);
                                }
                                arr.add(base);
                            }
                        }

                        if (configAttr != null && row[++count] != null) {
                            configs_.put(obj.id, (byte[]) row[count]);
                        }
                    }
                    
                    Arrays.sort(ovs, new Comparator<ObjectValue>() {
                        public int compare(ObjectValue ov1, ObjectValue ov2) {
                            if (ov1 == null) {
                                return -1;
                            } else if (ov2 == null) {
                                return 1;
                            } else {
                                String p1 = str_m.get(ov1.value.id);
                                String p2 = str_m.get(ov2.value.id);
                                if (p1 == null)
                                    return -1;
                                else if (p2 == null)
                                    return 1;
                                else
                                    return p1.compareTo(p2);
                            }
                        }
                    });

                    List<ReportNode> children = new ArrayList<ReportNode>();
                    for (int i = 0; i < ovs.length; i++) {
                        ObjectValue ov = ovs[i];
                        List<KrnObject> bases = bases_.get(ov.value.id);
                        String title = str_m.get(ov.value.id);
                        if (title == null || title.equals(""))
                            title = "Безымянный";

                        byte[] config = configs_.get(ov.value.id);
                        ReportNode childNode = new ReportNode(ov.value, title, loadComponent(config), ov.index, emptyFrame, bases);
                        TreeUIDMap.put(((KrnObject) ov.value).uid, childNode);
                        children.add(childNode);
                    }
                    addAllChildren(children);
                } catch (KrnException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private OrGuiComponent loadComponent(byte[] data) {
        Element xml = null;
        OrGuiComponent res = null;
        try {
            if (data != null && data.length > 0) {
                ByteArrayInputStream is = new ByteArrayInputStream(data);
                SAXBuilder b = new SAXBuilder();
                xml = b.build(is).getRootElement();
                is.close();
                res = Factories.instance().create(xml, Mode.DESIGN, emptyFrame);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public boolean isModified() {
        return isModified;
    }

    public void setModified(boolean modified) {
        isModified = modified;
    }

    public void setBases(KrnObject[] vals) {
        bases.clear();
        if (vals != null) {
            for (KrnObject val : vals) {
                bases.add(val);
            }
        }
    }

    public List<KrnObject> getBases() {
        return bases;
    }
}
