package kz.tamur.guidesigner.noteeditor;

import kz.tamur.rt.Utils;
import kz.tamur.rt.MainFrame;
import kz.tamur.util.editor.OrTextPane;
import org.jdom.Element;
import org.jdom.CDATA;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.io.*;
import java.util.Enumeration;

/**
 * The Class HTMLPanelContent
 */
public class HTMLPanelContent extends JPanel {

    /** The html brow. */
    private HTMLBrowser htmlBrow = null;
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
    
    /**
     * Instantiates a new hTML panel content.
     */
    public HTMLPanelContent() {
        super();
        setOpaque(isOpaque);
        setFont(Utils.getDefaultFont());
        setBorder(null);
    }

    /**
     * Pack nodes.
     * 
     * @param node
     *            the node
     * @return the element
     */
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

    /**
     * New note browser.
     * 
     * @param ne
     *            the ne
     */
    public void newNoteBrowser(HTMLEditor ne) {
        htmlBrow = new HTMLBrowser(ne);
        super.add(htmlBrow);
    }

    public OrTextPane getEditor() {
        return (htmlBrow != null) ? htmlBrow.getEditor() : new OrTextPane();
    }
    
    public String getHTML() {
        return (htmlBrow != null) ? htmlBrow.getHTML() : "";
    }
    
    public void setHTML(String html) {
        if (htmlBrow != null) {
            htmlBrow.setHTML(html);
        }
    }
    /**
     * Encode doc.
     * 
     * @param html
     *            the html
     * @return the string
     */
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
            }
        }
        return value;
    }
}
