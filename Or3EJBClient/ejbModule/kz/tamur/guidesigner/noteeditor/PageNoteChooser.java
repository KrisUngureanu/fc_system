package kz.tamur.guidesigner.noteeditor;

import kz.tamur.rt.Utils;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Кайржан
 * Date: 02.11.2005
 * Time: 19:10:16
 * To change this template use File | Settings | File Templates.
 */
public class PageNoteChooser extends JPanel{
    private NotePageTree noteTree;
    private JTextField tf = new JTextField();
    public PageNoteChooser(NotePageNode tree) {
        super(new BorderLayout());
        noteTree = new NotePageTree(tree, false);
        //((NotePageTree.NotePageTreeModel)noteTree.getModel()).setRoot(tree);
        JScrollPane scroll = new JScrollPane(noteTree);
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        JLabel label = new JLabel("Текст ссылки: ");
        label.setFont( Utils.getDefaultFont());
        topPanel.add(label, BorderLayout.WEST);
        topPanel.add(tf, BorderLayout.CENTER);
        tf.setFont(Utils.getDefaultFont());
        add(topPanel, BorderLayout.NORTH );
        add(scroll, BorderLayout.CENTER);

    }

    public NotePageNode getSelectedNode() {
        return (NotePageNode) noteTree.getSelectedNode();
    }

    public TreePath getSelectedPath() {
        return noteTree.getSelectionPath();
    }

    public String getTitle() {
        return tf.getText(); 
    }

}

