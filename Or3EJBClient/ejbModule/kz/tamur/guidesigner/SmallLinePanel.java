package kz.tamur.guidesigner;

import java.awt.*;
import java.awt.event.*;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import javax.swing.tree.TreePath;

import com.cifs.or2.util.MultiMap;

import kz.tamur.comps.Constants;
import kz.tamur.comps.OrHiperTree;
import kz.tamur.rt.MainFrame;

/**
 * Маленькая панелька для выполнения или поиска процесса
 * @author g009c1233
 * @since 2011/06/07
 * @version 0.1
 */
public class SmallLinePanel extends JPanel{
	ResourceBundle res = ResourceBundle.getBundle(Constants.NAME_RESOURCES, new Locale("ru"));
	public static final int SEARCH_SRV_IN_ORHIPERTREE = 0;
	public static final int RUN_SRV = 1;
	
	public static final int CONTAIN = 0;
	public static final int STARTS = 1;
	public static final int ENDS = 2;
	
	public OrHiperTree currHiperTree;
	
	private JComboBox box;
	private JTextComponent editor;
	private JTextField field;
	private JPopupMenu jpop;
	private TreePath[] comList;
	private JButton btn;
	private JButton btn2;
	private JCheckBox fol = new JCheckBox(res.getString("withFolders"));
	private ButtonGroup bg;
	private JRadioButton containR;
	private JRadioButton startsR;
	private JRadioButton endsR;
	private BadDocument bdoc;
	private JLabel label;
	
	private boolean firstTime = true;
	private int jumper = 0;
	private String[] list;
	
	private static SmallLinePanel slPanel;
	private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
	/**
	 * 
	 */
    public SmallLinePanel() {
        super(new GridBagLayout());
        if (fol != null) {
            fol.setOpaque(isOpaque);
        }
        if (containR != null) {
            containR.setOpaque(isOpaque);
        }
        if (startsR != null) {
            startsR.setOpaque(isOpaque);
        }
        if (endsR != null) {
            endsR.setOpaque(isOpaque);
        }
    }
	
	/**
	 * Запуск панели. Выбор по типу-mode.
	 * @param mode тип - 0-поиск, 1-выполнение
	 * @param hiperTree 
	 */
    public SmallLinePanel(int mode, OrHiperTree hiperTree, Locale local) {
        this();

        // Временное решение гемора с языком
        res = ResourceBundle.getBundle(Constants.NAME_RESOURCES, local);

        currHiperTree = hiperTree;
        switch (mode) {
        case SmallLinePanel.SEARCH_SRV_IN_ORHIPERTREE:
            setPreferredSize(new Dimension(360, 50));
            setMinimumSize(new Dimension(380, 50));
            initSearch();
            break;
        case SmallLinePanel.RUN_SRV:
            setPreferredSize(new Dimension(320, 25));
            setMinimumSize(new Dimension(320, 25));
            initRun();
            break;
        default:

            break;
        }
    }
	
	/**
	 * Инициализация панели запуска процесса.
	 */
	private void initRun(){
		JLabel textLabel = new JLabel(res.getString("pleaseInsertWord")+ ":");
		add(textLabel, new GridBagConstraints( 0, 0, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 5, 0, 0), 0, 0));
		box = new JComboBox();
		box.setEditable(true);
		box.requestFocusInWindow();
		editor = (JTextComponent) box.getEditor().getEditorComponent();
		bdoc = new BadDocument(box);
		editor.setDocument(bdoc);
		editor.setEditable(true);
	
		add(box, new GridBagConstraints( 0, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, GridBagConstraints.CENTER, GridBagConstraints.BOTH, Constants.INSETS_0, 0, 0));
		JButton btn = new JButton(res.getString("run"));
		add(btn, new GridBagConstraints( 1, 1, 1, 1, 1.5, GridBagConstraints.BOTH, GridBagConstraints.CENTER, GridBagConstraints.BOTH, Constants.INSETS_0, 0, 0));
		btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				runSrv();
			}
		});
		loadMap_Obj(false);
	}
	
	/**
	 * Инициализация панели поиска процесса
	 */
	private void initSearch(){
		JLabel textLabel = new JLabel(res.getString("pleaseInsertWord") + ":");
		add(textLabel, new GridBagConstraints( 0, 0, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(7, 5, 3, 0), 0, 0));
		label = new JLabel();
		add(label, new GridBagConstraints( 1, 0, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(7, 5, 0, 5), 0, 0));
		field = new JTextField();
		field.requestFocusInWindow();
		add(field, new GridBagConstraints( 0, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 5, 0, 1), 0, 0));
		btn = new JButton(res.getString("search"));
		add(btn, new GridBagConstraints( 1, 1, 1, 1, 0.7, GridBagConstraints.BOTH, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 3, 0, 5), 0, 0));
		btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				searchClicked();
			}
		});
		field.addKeyListener(new KeyListener(){
			public void keyPressed(KeyEvent e) {
				if(KeyEvent.getKeyText(e.getKeyCode()) == "Enter"){
					btn.doClick();
					e.setKeyCode(12);
				} else 
				firstTime = true;
			}
			public void keyReleased(KeyEvent e) {}
			public void keyTyped(KeyEvent e) {}
		});
		containR = new JRadioButton(res.getString("contain"));
		containR.setSelected(true);
		ItemListener iLis = new ItemListener(){
			public void itemStateChanged(ItemEvent e) {
				firstTime = true;
			}
		};
		containR.addItemListener(iLis);
		startsR = new JRadioButton(res.getString("startsWith"));
		startsR.addItemListener(iLis);
		endsR = new JRadioButton(res.getString("endsWith"));
		endsR.addItemListener(iLis);
		bg = new ButtonGroup();
		bg.add(containR);
		bg.add(startsR);
		bg.add(endsR);
		JPanel pan = new JPanel();
		pan.add(startsR);
		pan.add(endsR);
		pan.add(containR);
		fol.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				firstTime = true;
			}
		});
		pan.add(fol);
		add(pan, new GridBagConstraints( 0, 2, 3, 1, GridBagConstraints.EAST, GridBagConstraints.BOTH, GridBagConstraints.EAST, GridBagConstraints.EAST, Constants.INSETS_0, 0, 0));
	}
	
	/**
	 * Выполнение поиска
	 */
	private void searchClicked(){
		if(firstTime) {
			searchSrv(field.getText());
			if(comList!=null && comList.length >0){
				firstTime = false;
				currHiperTree.setActive(comList[0]);
				btn.setText(res.getString("next"));
				label.setText("1/"+comList.length);
			} else {
				btn.setText(res.getString("search"));
				label.setText(res.getString("not_found"));
			}
		} else {
			if(++jumper>=comList.length) {
				jumper = 0;
				firstTime = true;
				btn.setText("fine");
				searchClicked();
				return;
			}
			currHiperTree.setActive(comList[jumper]);
			btn.setText(res.getString("next"));
			label.setText((jumper+1) + "/"+ comList.length);
		}
	}
	
	/**
	 * Получение путей в дереве
	 * @param text слово которое искать
	 */
	private void searchSrv(String text){
		int mode = 0;
		if(containR.isSelected()) mode = SmallLinePanel.CONTAIN;
		else if(startsR.isSelected()) mode = SmallLinePanel.STARTS;
		else if(endsR.isSelected()) mode = SmallLinePanel.ENDS;
		comList = (TreePath[])currHiperTree.searchByName(text, fol.isSelected(), mode);
	}
	
	/**
	 * Загрузка хэшкарты
	 */
	public void loadMap_Obj(boolean fol) {
		//MultiMap mm = (MultiMap)currHiperTree.getMap_();
		//bdoc.map_ = mm;
		bdoc.setMap_((MultiMap)currHiperTree.getMap_());
		bdoc.setMap_Srv(currHiperTree.getMap_Obj(), false);
	}
	
	/**
	 * Запуск процесса
	 */
	public void runSrv(){
		bdoc.runSrv();
		this.getParent().getParent().getParent().getParent().setVisible(false);
	}
}
