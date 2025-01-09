package kz.tamur.guidesigner.noteeditor;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnObject;
import kz.tamur.rt.Utils;
import kz.tamur.util.editor.OrTextPane;
import kz.tamur.comps.ui.tabbedPane.OrBasicTabbedPane;
import kz.tamur.guidesigner.DesignerFrame;
import org.jdom.Element;
import org.jdom.CDATA;
import org.jdom.output.XMLOutputter;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.Enumeration;
import static kz.tamur.rt.Utils.createMenuItem;
/**
 * Created by IntelliJ IDEA.
 * User: Кайржан
 * Date: 10.10.2005
 * Time: 10:04:02
 */
public class NoteTabbedContent extends OrBasicTabbedPane {

    private JPopupMenu pm = new JPopupMenu();
    private JMenuItem miSave = createMenuItem("Сохранить");
    private JMenuItem miClose = createMenuItem("Закрыть");
    private JMenuItem miDelete = createMenuItem("Удалить");
    
    public NoteTabbedContent() {
        super();
        setFont(Utils.getDefaultFont());
        setBorder(null);
        miSave.setIcon(kz.tamur.rt.Utils.getImageIcon("Save"));
        miSave.setEnabled(false);
        miSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveCurrent();
                /*setAllSaveEnabled();*/
            }
        });
        miClose.setEnabled(false);
        miClose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                removeTabAt(getSelectedIndex());
            }
        });
        miDelete.setEnabled(false);
        miDelete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteCurrent();
            }
        });
        pm.add(miSave);
        pm.add(miClose);
        pm.addSeparator();
        pm.add(miDelete);
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                showPopup(e);
            }

            public void mouseReleased(MouseEvent e) {
                showPopup(e);
            }

            private void showPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    /*if (isTabModified(getTitleAt(getSelectedIndex()))) {

                    } else {
                        miSave.setEnabled(false);
                    }*/
                    miSave.setEnabled(true);
                    miClose.setEnabled(true);
                    miDelete.setEnabled(true);
                    pm.show(NoteTabbedContent.this, e.getX(), e.getY());
                }
            }
        });
    }
    
    protected void deleteCurrent() {
        NoteBrowser br = (NoteBrowser) getSelectedComponent();
        if (br != null) {
            NotePageNode node = br.getRoot();
        }
    }

    protected void saveCurrent() {
        NoteBrowser br = (NoteBrowser) getSelectedComponent();
        if (br != null) {
            NotePageNode node = br.getRoot();
            Element element = packNodes(node);
            saveToBase(element, br.getKrnObject());
        }
    }

    private void saveToBase(Element element, KrnObject obj) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            XMLOutputter out = new XMLOutputter();
            out.getFormat().setEncoding("UTF-8");
            out.output(element, os);
            os.close();
            Kernel krn = Kernel.instance();
            krn.setAutoCommit(false);
            long langId = DesignerFrame.instance().getInterfaceLang().id;
            krn.setBlob(obj.id, obj.classId, "content", 0, os.toByteArray(), langId, 0);
            krn.setAutoCommit(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Element packNodes(NotePageNode node) {
        Element element = null;
        if (!node.isLeaf()) {
            element = new Element("folder");
            element.setAttribute("name", node.toString());
            HTMLDocument html = node.getContent();
            String value = encodeDoc(html);
            CDATA cdata = new CDATA(value);
            element.addContent(cdata);
            if (node.getChildCount() >= 0) {
                for (Enumeration e = node.children(); e.hasMoreElements();) {
                    NotePageNode n = (NotePageNode) e.nextElement();
                    element.addContent(packNodes(n));
                }
            }
        } else if (node.isLeaf()) {
            element = new Element("func");
            element.setAttribute("name", node.toString());
            HTMLDocument html = node.getContent();
            String value = encodeDoc(html);
            CDATA cdata = new CDATA(value);
            element.addContent(cdata);
            return element;
        }
        return element;
    }

    public void addNoteTab(NoteNode node, NoteEditor ne) {
        NoteBrowser nb = new NoteBrowser(node.getKrnObj(), ne);
        super.addTab(node.getTitle(), nb);
        setSelectedIndex(getComponentCount() - 1);
    }

    public OrTextPane getEditor() {
        NoteBrowser br = (NoteBrowser) getSelectedComponent();
        if (br != null)
            return br.getEditor();
        else
            return new OrTextPane();
    }

    public NotePageNode getActiveRoot() {
        NoteBrowser br = (NoteBrowser) getSelectedComponent();
        return br.getTree();
    }

    private String encodeDoc(HTMLDocument html) {
        String value = "";
            if (html != null) {
            try {
                String tmp = html.getText(0, html.getLength());
                if (!"".equals(tmp)) {
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    HTMLEditorKit kit = new HTMLEditorKit();
                    kit.write(os, html, 0, html.getLength());
                    os.close();
                    value = os.toString();
                }
            } catch (BadLocationException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }}
        return value;
    }
}
