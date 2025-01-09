package kz.tamur.guidesigner.noteeditor;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnObject;

import kz.tamur.comps.Constants;
import kz.tamur.rt.Utils;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerFrame;
import kz.tamur.util.editor.OrTextPane;
import kz.tamur.util.Funcs;
import kz.tamur.util.LangItem;
import org.jdom.input.SAXBuilder;
import org.jdom.CDATA;
import org.jdom.Element;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.StringContent;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.ResourceBundle;
import java.util.Locale;

/**
 * Created by IntelliJ IDEA.
 * User: Кайржан
 * Date: 11.10.2005
 * Time: 10:35:07
 * To change this template use File | Settings | File Templates.
 */
public class NoteBrowser extends JSplitPane implements HyperlinkListener {
    private NotePageTree noteTree;
    private OrTextPane editor = new OrTextPane();
    private KrnObject obj;
    private String title;
    private NotePageNode lastNode;
    private long langId;
    private boolean isEditable = true;
    private PageNavigator navi = new PageNavigator();
    private boolean bool = true;
    private boolean initialized = false;
    ResourceBundle res;
    private NoteEditor ne_;

    public NoteBrowser(KrnObject krnObj, NoteEditor ne) {
        super();
        obj = krnObj;
        this.ne_ = ne;
        init(true);
    }

    public NoteBrowser(KrnObject krnObj, boolean isEditable, long langId) {
        super();
        obj = krnObj;
        this.langId = langId;
        this.isEditable = isEditable;
        this.title = getTitle(obj);
        //init(isEditable);
    }

    public void init() {
        if (!initialized)
            init(isEditable);
    }


    private void init(boolean isEditable) {
        initialized = true;
        editor.setEditable(isEditable);
        editor.setContentType("html/text; charset=Cp1251");
        editor.setEditorKit(new OrHTMLEditorKit());
        editor.setDocument(Utils.createHTMLDocument());
        Insets insets = new Insets(10, 10, 10, 10);
        editor.setMargin(insets);
        JScrollPane sp = new JScrollPane(editor);
        setRightComponent(sp);
        noteTree = new NotePageTree(new NotePageNode("Голова", null, false, 0), isEditable);
        setLeftComponent(new JScrollPane(noteTree));
        noteTree.setEditable(false);
        noteTree.setShowPopupEnabled((isEditable));
        setDividerSize(10);
        //setOneTouchExpandable(true);
        noteTree.addTreeSelectionListener(new NotePageListener());
        NotePageNode node = getNotePage(obj);
        lastNode = node;
        ((NotePageTree.NotePageTreeModel) noteTree.getModel()).setRoot(node);
        //title = node.getTitle();
        if (!isEditable) {
            editor.addHyperlinkListener(this);
            noteTree.setRootVisible(false);
            noteTree.setSelectionPath(getFirstNode());
            setDividerLocation(1.0);
        } else {
            editor.addFocusListener(new FocusListener() {
                public void focusGained(FocusEvent e) {
                    if (ne_.m_xStart >= 0 && ne_.m_xFinish >= 0)
                        if (editor.getCaretPosition() == ne_.m_xStart) {
                            editor.setCaretPosition(ne_.m_xFinish);
                            editor.moveCaretPosition(ne_.m_xStart);
                        } else
                            editor.select(ne_.m_xStart, ne_.m_xFinish);
                }

                public void focusLost(FocusEvent e) {
                    ne_.m_xStart = editor.getSelectionStart();
                    ne_.m_xFinish = editor.getSelectionEnd();
                }
            });
        }
    }

    private TreePath getFirstNode() {
        ArrayList list = new ArrayList();

        NotePageNode root = (NotePageNode) ((NotePageTree.NotePageTreeModel) noteTree.getModel()).getRoot();
        list.add(root);
        if (root.getChildCount() > 0)
            list.add(root.getFirstChild());
        return new TreePath(list.toArray());
    }

    public OrTextPane getEditor() {
        return editor;
    }

    public String getTitle() {
        return title;
    }

    public String getTitle(KrnObject obj) {
        try {
            long langId;
            if (isEditable)
                langId = DesignerFrame.instance().getInterfaceLang().id;
            else
                langId = this.langId;
            String[] titles = Kernel.instance().getStrings(obj,"title",langId,0);
            if (titles.length > 0) {
                title = titles[0];
            } else {
                LangItem li = LangItem.getById(langId);
                if (li != null) {
                    if ("KZ".equals(li.code)) {
                        res = ResourceBundle.getBundle(
                                Constants.NAME_RESOURCES, new Locale("kk"));
                    } else {
                        res = ResourceBundle.getBundle(
                                Constants.NAME_RESOURCES, new Locale("ru"));
                    }
                }
                title = res.getString("noname");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return title;
    }

    public NotePageNode getTree() {
        return (NotePageNode) noteTree.getModel().getRoot();
    }

    public KrnObject getKrnObject() {
        return obj;
    }

    public NotePageNode getRoot() {
        return (NotePageNode) noteTree.getModel().getRoot();
    }

    public void setSelection(TreePath path) {
        if (path != null)
            noteTree.setSelectionPath(path);
    }

    public void hyperlinkUpdate(HyperlinkEvent event) {
        if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            String url = event.getDescription();
            if (url.startsWith("jump")) {
                String pathStr = url.substring(5);
                String[] names = getNames(pathStr);
                TreePath path = findByName((TreeNode) noteTree.getModel().getRoot(), names);
                setSelection(path);
            } else if (url.startsWith("file")) {
                final Kernel krn = Kernel.instance();
                int objId = Integer.parseInt(url.substring(5));
                KrnObject obj = null;
                try {
                    obj = Utils.getObjectById(objId, 0);
                    if (obj != null) {
                        byte[] bytes = krn.getBlob(obj, "file", 0, 0, 0);
                        String[] filename = krn.getStrings(obj, "filename", 0, 0);
                        File file = Funcs.createTempFile("msdoc", ".doc");
                        FileOutputStream os = new FileOutputStream(file);
                        os.write(bytes);
                        os.close();
                        Runtime r = Runtime.getRuntime();
                        r.exec("cmd /c " + file.getAbsolutePath());
                    }
                } catch (Exception e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }

        }
    }

    public Component getNavigator() {
        if (navi == null)
            navi = new PageNavigator();
        return navi;
    }

    private class NotePageListener implements TreeSelectionListener {
        public void valueChanged(TreeSelectionEvent e) {
            NotePageNode node = (NotePageNode) noteTree.getSelectedNode();
            if(node==null) return;
            if (node.getContent() == null)
                node.setContent(Utils.createHTMLDocument());
            editor.setDocument(node.getContent());
            editor.setContentType("text/html");

            //editor.setText(node.getText());
            if (bool) {
            	TreePath path = noteTree.getSelectionPath();
            	if(path != null) {
            		navi.step(path.toString());
            	}
            }
        }
    }

    private NotePageNode getNotePage(KrnObject obj) {
        org.jdom.Element xml = null;
        NotePageNode note = null;
        try {
            final Kernel krn = Kernel.instance();
            long langId = 0;
            if (isEditable)
                langId = DesignerFrame.instance().getInterfaceLang().id;
            else
                langId = this.langId;
            String[] titles = krn.getStrings(obj,"title",langId,0);
            if (titles.length > 0) {
                title = titles[0];
            }
            byte[] data = krn.getBlob(obj, "content", 0, langId, 0);
            if (data.length > 0) {
                ByteArrayInputStream is = new ByteArrayInputStream(data);
                SAXBuilder b = new SAXBuilder();
                xml = b.build(is).getRootElement();
                note = getTree(xml);
                is.close();
            } else {
                note = new NotePageNode("Начало", null, false, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return note;
    }

    private NotePageNode getTree(org.jdom.Element e) {
        NotePageNode node = null;
        if (e.getName().equals("folder")) {
            String str = e.getAttribute("name").getValue();
            HTMLDocument htmlDoc = null;
            String html = "";
            if (e.getContentSize() > 0) {
                java.util.List list = e.getContent();
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i) instanceof CDATA) {
                        html = ((CDATA)list.get(i)).getText();
                        try {
                            if (!html.equals("")) {
                                StringContent strCont = new StringContent(html.length());
                                strCont.insertString(0, html);
                                htmlDoc = new HTMLDocument(Utils.getOrCSS());
                                HTMLEditorKit kit = new HTMLEditorKit();
                                ByteArrayInputStream is = new ByteArrayInputStream(html.getBytes());
                                kit.read(is, htmlDoc, 0);
                            }
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                        break;
                    }
                }
            }

            node = new NotePageNode(str, htmlDoc, false, 0);
            if (e.getChildren().size() > 0) {
                java.util.List list = e.getChildren();
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i) instanceof Element) {
                        node.add(getTree((org.jdom.Element) list.get(i)));
                    }
                }
                return node;
            }
        } else if (e.getName().equals("func")) {
            String title = e.getAttribute("name").getValue();
            String html = "";
            HTMLDocument htmlDoc = null;
            if (e.getContentSize() > 0) {
                java.util.List list = e.getContent();
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i) instanceof CDATA) {
                        html = ((CDATA)list.get(i)).getText();
                        try {
                            if (!html.equals("")) {
                                StringContent strCont = new StringContent(html.length());
                                strCont.insertString(0, html);
                                htmlDoc = new HTMLDocument(Utils.getOrCSS());
                                HTMLEditorKit kit = new HTMLEditorKit();
                                ByteArrayInputStream is = new ByteArrayInputStream(html.getBytes());
                                kit.read(is, htmlDoc, 0);
                            }
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                        break;
                    }
                }
            }
            node = new NotePageNode(title, htmlDoc, true, 0);
            return node;
        }
        return node;
    }

    private String[] getNames(String pathStr) {
        pathStr = pathStr.substring(1, pathStr.length() - 1);
        String[] names = pathStr.split(",");
        for (int i = 0; i < names.length; i++) {
            names[i] = names[i].trim();
        }
        return names;
    }

    public TreePath findByName(TreeNode root, String[] names) {
        return find2(new TreePath(root), names, 0, true);
    }

    private TreePath find2(TreePath parent, Object[] nodes, int depth, boolean byName) {
        TreeNode node = (TreeNode) parent.getLastPathComponent();
        Object o = node;
        if (byName) {
            o = o.toString();
        }
        if (o.equals(nodes[depth])) {
            if (depth == nodes.length - 1) {
                return parent;
            }
            if (node.getChildCount() >= 0) {
                for (Enumeration e = node.children(); e.hasMoreElements();) {
                    TreeNode n = (TreeNode) e.nextElement();
                    TreePath path = parent.pathByAddingChild(n);
                    TreePath result = find2(path, nodes, depth + 1, byName);
                    if (result != null) {
                        return result;
                    }
                }
            }
        }
        return null;
    }

    public class PageNavigator extends JPanel implements ActionListener {
        private JButton prev = new ButtonsFactory.DesignerToolButton("PageBack", "Назад");
        private JButton next = new JButton(kz.tamur.rt.Utils.getImageIcon("PageForward"));
        private ArrayList history = new ArrayList();
        private int index = 0;

        public PageNavigator() {
            setLayout(new FlowLayout(FlowLayout.LEFT));
            next.setToolTipText("Вперед");
            next.setBorder(null);
            next.setOpaque(false);
            add(prev);
            add(next);
            prev.setEnabled(false);
            next.setEnabled(false);
            next.addActionListener(this);
            prev.addActionListener(this);
        }


        public void actionPerformed(ActionEvent e) {
            Object obj = e.getSource();
            if (obj.equals(prev))
                stepBack();
            else if (obj.equals(next))
                stepNext();
        }

        public void stepBack() {
            if (index != 0) {
                index--;
                bool = false;
                loadHistory();
                bool = true;
                update();
            }
        }

        private void loadHistory() {
            String pathStr = (String) history.get(index);
            String[] names = getNames(pathStr);
            TreePath path = findByName((TreeNode) noteTree.getModel().getRoot(), names);
            setSelection(path);
        }

        public void stepNext() {
            if (index != history.size() - 1) {
                index++;
                bool = false;
                loadHistory();
                bool = true;
                update();
            }
        }

        public void step(String s) {
            history.add(s);
            index = history.size() - 1;
            update();
        }

        public void update() {
            if (history.size() > 1)
                prev.setEnabled(true);
            if (index < history.size() - 1)
                next.setEnabled(true);
            if (index == (history.size() -1))
                next.setEnabled(false);
            if (index == 1)
                prev.setEnabled(false);
        }
    }

}
