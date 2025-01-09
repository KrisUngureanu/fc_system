package kz.tamur.rt;

import kz.tamur.comps.*;
import kz.tamur.util.DescriptionSupport;
import kz.tamur.rt.adapters.UIFrame;

import javax.swing.*;
import javax.swing.tree.TreePath;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.text.*;
import javax.swing.text.rtf.RTFEditorKit;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;
import java.awt.*;
import java.util.StringTokenizer;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 19.04.2005
 * Time: 11:12:47
 * To change this template use File | Settings | File Templates.
 */
public class RuntimeController implements AWTEventListener {

    private JPopupMenu popup = new JPopupMenu();
    StyledDocument doc = new DefaultStyledDocument();
    private JTextPane text = new JTextPane(doc);
    private OrFrame frame;
    private boolean isShortDescrMode = false;
    private int mode;
    private RTFEditorKit editorKit;

    public RuntimeController() {
        editorKit = new RTFEditorKit();
        String desc = System.getProperty("desc");
        if (desc != null) {
            isShortDescrMode = "1".equals(desc);
        } else {
            isShortDescrMode = "0".equals(desc);
        }
        text.setMaximumSize(new Dimension(400, 400));
        text.setMinimumSize(new Dimension(400, 100));
        popup.setMaximumSize(new Dimension(500, 400));
        text.setEditable(false);
        text.setOpaque(false);
        JScrollPane sp = new JScrollPane(text);
        sp.setBorder(null);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        //JLabel lb = new JLabel(kz.tamur.rt.Utils.getImageIcon("backHelp"));
        ///lb.setLayout(new BorderLayout());
        //lb.add(sp, BorderLayout.CENTER);
        popup.setBorder(BorderFactory.createLineBorder(
                Utils.getDarkShadowSysColor()));
        popup.setBackground(Utils.getLightYellowColor());
        popup.add(sp);
    }

    public void setCurrentMode(int mode) {
        this.mode = mode;
    }

    public void eventDispatched(AWTEvent event) {
        switch (event.getID()) {
            case MouseEvent.MOUSE_PRESSED :
                mousePressed((MouseEvent)event);
                break;
        }
    }

    private void mousePressed(MouseEvent e) {
        if (mode == MainFrame.HELP) {
            Object src = e.getSource();
            if (src instanceof Descriptionable) {
                Descriptionable b = (Descriptionable)src;
                String desc = b.getDesc();
                if (desc != null && desc.length() > 0 &&
                        b instanceof Component) {
                    StringTokenizer st = new StringTokenizer(desc, "\n");
                    int ct = st.countTokens();
                    text.setPreferredSize(new Dimension(500, ((ct==0)?20:ct*20) + 5));
                    popup.setPreferredSize(new Dimension(500, ((ct==0)?20:ct*20) + 5));
                    text.setText(desc);
                    popup.show((Component)b, e.getX(), e.getY());
                }
            } else {
                if (src instanceof Component && !(src instanceof OrGuiComponent) &&
                    !(src instanceof ProcessTree) && !(src instanceof JTableHeader)) {
                    src = getOrGuiParent((Component)src);
                }
                if (src instanceof ProcessTree) {
                    ProcessTree pTree = (ProcessTree)src;
    /*
                    ProcessNode pNode =
                            (ProcessNode)pTree.getSelectionPath().getLastPathComponent();
    */
                    TreePath path = pTree.getPathForLocation(e.getX(), e.getY());
                    if (path != null) {
                        ProcessNode pNode =
                                (ProcessNode)path.getLastPathComponent();
                        if (pNode != null) {
                            if (pNode.isLeaf()) {
                                String desc = pNode.getDesc();
                                if (desc != null && desc.length() > 0) {
                                    StringTokenizer st = new StringTokenizer(desc, "\n");
                                    int ct = st.countTokens();
                                    text.setPreferredSize(new Dimension(500, ((ct==0)?20:ct*20) + 5));
                                    popup.setPreferredSize(new Dimension(500, ((ct==0)?20:ct*20) + 5));
                                    text.setText(desc);
                                    popup.show(pTree, e.getX(), e.getY());
                                }
                            }
                        }
                    }
                } else if (src instanceof JTableHeader && !(src instanceof OrTableFooter)) {
                    JTableHeader h = (JTableHeader)src;
                    TableModel tm = h.getTable().getModel();
                    if (tm instanceof OrTableModel) {
                        TableColumnModel columnModel = h.getColumnModel();
                        int viewIndex = columnModel.getColumnIndexAtX(e.getX());
                        if (viewIndex != -1) {
                            OrTableColumn tc = ((OrTableModel)tm).getColumn(viewIndex);
                            if (tc != null) {
                                byte[] b = tc.getDescription();
                                if (b != null && b.length > 0) {
                                    String s = new String(b);
                                    int ct = -1;
                                    int beg = 0;
                                    while (beg > -1) {
                                        beg = s.indexOf("\\par", beg + 1);
                                        ct++;
                                    }
                                    doc = new DefaultStyledDocument();
                                    ByteArrayInputStream is = new ByteArrayInputStream(b);
                                    try {
                                        editorKit.read(is, doc, 0);
                                    } catch (IOException e1) {
                                        e1.printStackTrace();
                                    } catch (BadLocationException e1) {
                                        e1.printStackTrace();
                                    }

                                    text.setDocument(doc);
                                    text.setPreferredSize(new Dimension(500, ((ct==0)?20:ct*20) + 5));
                                    popup.setPreferredSize(new Dimension(500, ((ct==0)?20:ct*20) + 5));
                                    popup.show((Component)src, e.getX(), e.getY());
                                } else {
                                    //text.setPreferredSize(new Dimension(150, 30));
                                    //popup.setPreferredSize(new Dimension(150, 30));
                                    //text.setText(frame.getResourceBundle().getString("nocomment"));
                                }
                            }
                        }
                    }
                } else if (src instanceof OrGuiComponent) {
                    OrGuiComponent comp = (OrGuiComponent)src;
                    OrGuiContainer cont = ((UIFrame)frame).getPanel();
                    if (!(comp instanceof OrLabel) && !isShortDescrMode) {
                        byte[] b = comp.getDescription();
                        if (b != null && b.length > 0) {
                            String s = new String(b);
                            int ct = -1;
                            int beg = 0;
                            while (beg > -1) {
                                beg = s.indexOf("\\par", beg + 1);
                                ct++;
                            }

                            doc = new DefaultStyledDocument();
                            ByteArrayInputStream is = new ByteArrayInputStream(b);
                            try {
                                editorKit.read(is, doc, 0);
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            } catch (BadLocationException e1) {
                                e1.printStackTrace();
                            }

                            text.setDocument(doc);
                            text.setPreferredSize(new Dimension(500, ((ct==0)?20:ct*20) + 10));
                            popup.setPreferredSize(new Dimension(500, ((ct==0)?20:ct*20) + 10));
                            if (comp instanceof OrHyperPopup) {
                                ((OrHyperPopup)comp).setHelpClick(true);
                            } else if (comp instanceof OrButton) {
                                ((OrButton)comp).setHelpClick(true);
                            } else if (comp instanceof OrHyperLabel) {
                                ((OrHyperLabel)comp).setHelpClick(true);
                            } else if (src instanceof OrTreeField) {
                                ((OrTreeField)src).setHelpClick(true);
                            } else if (src instanceof OrDocField) {
                                ((OrDocField)src).setHelpClick(true);
                            } else if (src instanceof OrComboBox) {
                                ((OrComboBox)src).setHelpClick(true);
                            }
                            popup.show((Component)comp, e.getX(), e.getY());
                        } else {
                            //text.setPreferredSize(new Dimension(150, 30));
                            //popup.setPreferredSize(new Dimension(150, 30));
                            //text.setText(frame.getResourceBundle().getString("nocomment"));
                        }
                    } else if (cont instanceof DescriptionSupport) {
                        byte[] b = ((DescriptionSupport)cont).getDescription();
                        if (b != null && b.length > 0) {
                            String s = new String(b);
                            int ct = -1;
                            int beg = 0;
                            while (beg > -1) {
                                beg = s.indexOf("\\par", beg + 1);
                                ct++;
                            }

                            doc = new DefaultStyledDocument();
                            ByteArrayInputStream is = new ByteArrayInputStream(b);
                            try {
                                editorKit.read(is, doc, 0);
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            } catch (BadLocationException e1) {
                                e1.printStackTrace();
                            }

                            text.setDocument(doc);
                            text.setPreferredSize(new Dimension(500, ((ct==0)?20:ct*20) + 5));
                            popup.setPreferredSize(new Dimension(500, ((ct==0)?20:ct*20) + 5));
                            popup.show((Component)comp, e.getX(), e.getY());
                        } else {
                            //text.setPreferredSize(new Dimension(150, 30));
                            //popup.setPreferredSize(new Dimension(150, 30));
                            //text.setText(frame.getResourceBundle().getString("nocomment"));
                        }
                    }
                }
            }
            ((MainFrame) InterfaceManagerFactory.instance().getManager()).changeHelpMode();
            e.consume();
        }
    }

    private Component getOrGuiParent(Component c) {
        if (c != null) {
            Component parent = c.getParent();
            if (parent instanceof OrGuiComponent) {
                return parent;
            } else {
                return getOrGuiParent(parent);
            }
        } else {
            return null;
        }
    }

    public void setFrame(OrFrame frame) {
        this.frame = frame;
    }
}
