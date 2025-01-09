package kz.tamur.admin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.search.RoundedCornerBorder;
import kz.tamur.rt.Utils;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.User;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;

public class FavoritesClassesPanel extends JPanel implements ActionListener {

	private JButton favoritesClassesBtn = ButtonsFactory.createToolButton("FavouritesClassesIcon", ".png", "Избранные классы");
	private JButton deleteClassBtn = ButtonsFactory.createToolButton("DeleteClassFromFavoriteIcon", ".png", "Удалить из списка");
	private JPopupMenu favoritePopup = new JPopupMenu();
	private JList classesList = new JList();
	private JScrollPane classesListSP = new JScrollPane(classesList);
	private Dimension listDimension = new Dimension(200, 200);
	private Kernel kernel = Kernel.instance();
	private User user = kernel.getUser();
	private Map<Long, KrnClass> classesMap = new HashMap<Long, KrnClass>();
	private ClassBrowser classBrowser;
	
	public FavoritesClassesPanel(ClassBrowser classBrowser) {
		super(new BorderLayout());
		this.classBrowser = classBrowser;
		favoritesClassesBtn.addActionListener(this);
		add(favoritesClassesBtn, BorderLayout.CENTER);
		initPopup();
	}
	
	private void initPopup() {
		favoritePopup.setBorder(new RoundedCornerBorder(Color.GRAY));
		favoritePopup.setLayout(new GridBagLayout());

		deleteClassBtn.setEnabled(false);
		deleteClassBtn.addActionListener(this);
		favoritePopup.add(deleteClassBtn, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		
		classesList.addMouseListener(new MouseListener() {
			public void mouseReleased(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2 && classesList.getSelectedIndex() > -1) {
					try {
						KrnClass selectedClass = kernel.getClassByName(String.valueOf(classesList.getSelectedValue()));
						classBrowser.classTree.setSelectedPath(Kernel.instance().getClassNode(selectedClass.id));
					} catch (KrnException exception) {
						exception.printStackTrace();
					}
				}
			}
		});
		
		classesList.setFont(new Font("Arial", Font.PLAIN, 12));
		classesList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		classesList.setBorder(BorderFactory.createEmptyBorder());
		classesList.setBackground(Utils.getLightGraySysColor());
		
		classesList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (classesList.getSelectedIndex() > -1) {
					deleteClassBtn.setEnabled(true);
				} else {
					deleteClassBtn.setEnabled(false);
				}
			}
		});
		
		classesListSP.setBorder(BorderFactory.createEmptyBorder());
		classesListSP.setMinimumSize(listDimension);
		classesListSP.setMaximumSize(listDimension);
		classesListSP.setPreferredSize(listDimension);

		updateList();
		
		favoritePopup.add(classesListSP, new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
	}
	
	public void updateList() {
		classesMap.clear();
		List<KrnClass> classes = loadFavoriteClasses();
		DefaultListModel listModel = new DefaultListModel();
		for (int i = 0; i < classes.size(); i++) {
			listModel.addElement(classes.get(i).name);
			classesMap.put(classes.get(i).id, classes.get(i));
		}
		classesList.setModel(listModel);
	}
	
    private List<KrnClass> loadFavoriteClasses() {
        ArrayList<KrnClass> classes = new ArrayList<KrnClass>();
        try {
            long[] classesID = kernel.getLongs(user.object, "favoritesClasses", 0);
            for (int i = 0; i < classesID.length; i++) {
                classes.add(kernel.getClass(classesID[i]));
            }

        } catch (KrnException exception) {
            exception.printStackTrace();
        }
        return classes;
    }
	
	public boolean isContain(long classID) {
		return classesMap.containsKey(classID);
	}
	
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == favoritesClassesBtn) {
			favoritePopup.show(this, favoritesClassesBtn.getLocation().x, favoritesClassesBtn.getLocation().y + 35);
		} else if (source == deleteClassBtn) {
			int index = classesList.getSelectedIndex();
			try {
				kernel.deleteValue(user.getObject().id, kernel.getClassByName("User").id, "favoritesClasses", new int[] {index}, 0);
			} catch (KrnException exception) {
				exception.printStackTrace();
			}
			updateList();
		}
	}

}
