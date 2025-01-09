package kz.tamur.guidesigner.replication;

import static kz.tamur.rt.Utils.createMenuItem;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

import kz.tamur.Or3Frame;
import kz.tamur.comps.Constants;
import kz.tamur.comps.ui.tabbedPane.OrBasicTabbedPane;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.DesignerModalFrame;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.guidesigner.bases.BaseTree;
import kz.tamur.guidesigner.search.GradientProgressBarUI;
import kz.tamur.guidesigner.users.Or3RightsNode;
import kz.tamur.or3.client.util.ClientUtils;
import kz.tamur.rt.MainFrame;
import kz.tamur.rt.Utils;
import kz.tamur.util.Funcs;

import com.cifs.or2.client.ImportResultListener;
import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.User;
import com.cifs.or2.kernel.DateValue;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.KrnVcsChange;
import com.cifs.or2.kernel.Time;
import com.cifs.or2.kernel.TimeValue;

public class ReplicationFrame extends OrBasicTabbedPane implements ActionListener, ImportResultListener {

    private JButton ImportBtn = new JButton("Импортировать");
    private JButton ExportBtn = new JButton("Экспортировать новые изменения");
    private JButton DbExportBtn = new JButton("Экспортировать БД в текстовый файл");
    private JButton refreshMethodsBtn = new JButton("Обновить создание всех методов");
    private JButton lastChangesInfoBtn = new JButton("Получить последние изменения");
    private JButton convertToSystemBtn = new JButton("Перевести базу в режим системной");
    private JButton convertToRealBtn = new JButton("Перевести базу в режим реальной");
    private JButton strBaseSysBtn = new JButton("Выбрать структуру баз");
    private JButton strBaseRealBtn = new JButton("Выбрать структуру баз");
    private JPanel utilBDPane=new JPanel();
    private Object value;
    private KrnObject lastSelectedBaseSys = null;
    private JTextField stringFieldBaseSys = new JTextField("");
    private KrnObject lastSelectedBaseReal = null;
    private JTextField stringFieldBaseReal = new JTextField("");
    private JTextField dir = new JTextField();
    private JTextField separator = new JTextField();
    private JPanel panelLastImportFile = new JPanel(new GridBagLayout());
    private JPanel panelLastExportFile = new JPanel(new GridBagLayout());
    private JTextArea areaScrOnBefAct = new JTextArea();
    private JTextArea areaScrOnAftAct = new JTextArea();
    private JTextArea areaInfo = new JTextArea();
    
    private String impFileNameString = "Последний импортированный файл: ";
    private JLabel impFileNameLabel = new JLabel(impFileNameString);
    
    private String impDateString = "Дата последнего репликационного импорта: ";
    private JLabel impDateLabel = new JLabel(impDateString);

    private String filesSourceString = "Источник репликационных файлов: ";
    private JLabel filesSourceLabel = new JLabel(filesSourceString);
    
    private JLabel impChangesLabel = new JLabel("Изменения последнего импорта:");
    
    private JTextArea impChangesInfoTA = new JTextArea();
    
    private String expDateString = "Дата последнего репликационного экспорта: ";
    private JLabel expDateLabel = new JLabel(expDateString);
    
    private JLabel expChangesLabel = new JLabel("Информация об изменениях, вошедших в последний экспорт:");
    
    private JTextArea expChangesInfoTA = new JTextArea();
    
    private JPopupMenu popupMenu = new JPopupMenu();
    private JMenuItem copyNameItem = createMenuItem("Копировать название репликационного файла");
    private JMenuItem copyPathItem = createMenuItem("Копировать путь к источнику файлов");
    
    private String allImportStartString = "Время старта всего импорта: ";
    private JLabel allImportStartLabel = Utils.createLabel(allImportStartString);
    private String allImportFinishString = "Время завершения всего импорта: ";
    private JLabel allImportFinishLabel = Utils.createLabel(allImportFinishString);
    private String importResultString = "Результат выполнения импорта: ";
    private JLabel importResultLabel = Utils.createLabel(importResultString);
    private String currentImportFileString = "Импортируемый файл: ";
    private JLabel currentImportFileLabel = Utils.createLabel(currentImportFileString);
    private JProgressBar filesProgressBar = new JProgressBar();
    
    private JLabel onBeforeImportInfoLabel = Utils.createLabel();
    private JLabel onAfterImportInfoLabel = Utils.createLabel();

    private String currentImportModelChangeString = "Обновление модели данных: ";
    private JLabel currentImportModelChangeLabel = Utils.createLabel(currentImportModelChangeString);

    private String currentImportDataChangeString = "Обновление данных: ";
    private JLabel currentImportDataChangeLabel = Utils.createLabel(currentImportDataChangeString);
    private boolean isAllowConvertDb = false;
    private String urlConnection = "";
    
    private SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    
    public ReplicationFrame() {
        super();
        Kernel krn=Kernel.instance();
        User user = krn.getUser();
        try {
			isAllowConvertDb = krn.isAllowConvertDb();
			urlConnection=krn.getUrlConnection();
		} catch (KrnException e1) {
			e1.printStackTrace();
		}
        boolean canImport = user.hasRight(Or3RightsNode.REPLICATION_IMPORT_RIGHT);
        boolean canExport = user.hasRight(Or3RightsNode.REPLICATION_EXPORT_RIGHT);
        boolean canExportText = user.hasRight(Or3RightsNode.REPLICATION_EXPORTTEXT_RIGHT);
        JPanel importPane=new JPanel();
        JPanel exportPane=new JPanel();
        JPanel exportBDPane=new JPanel();
        GridBagLayout importLayout = new GridBagLayout();
        importPane.setLayout(importLayout);        
        GridBagLayout exportLayout = new GridBagLayout();
        exportPane.setLayout(exportLayout);
        GridBagLayout exportBDLayout = new GridBagLayout();
        exportBDPane.setLayout(exportBDLayout);
        this.addTab(" И м п о р т ",importPane);
        this.addTab(" Э к с п о р т ",exportPane);
        this.addTab(" Э к с п о р т  Б Д ",exportBDPane);
        GridBagConstraints importBtnConstraints = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 1), 0, 0);        
        importLayout.setConstraints(ImportBtn, importBtnConstraints);
        importPane.add(ImportBtn);      
        exportPane.add(ExportBtn, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(30, 1, 30, 1), 0, 0));      
        Dimension d = new Dimension(200, 20);
        JLabel lbl1 = new JLabel("Каталог для экспорта: ");
        dir.setPreferredSize(d);
        dir.setMinimumSize(new Dimension(200, 20));
        JLabel lbl2 = new JLabel("Разделитель: ");
        separator.setPreferredSize(d);
        separator.setMinimumSize(new Dimension(200, 20));


        GridBagConstraints lbl1Constraints = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(1, 1, 1, 1), 0, 0);        
        exportBDLayout.setConstraints(lbl1, lbl1Constraints);
        exportBDPane.add(lbl1); 
        GridBagConstraints dirConstraints = new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1, 1, 1, 1), 0, 0);        
        exportBDLayout.setConstraints(dir, dirConstraints);
        exportBDPane.add(dir); 
        GridBagConstraints lbl2Constraints = new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(1, 1, 1, 1), 0, 0);        
        exportBDLayout.setConstraints(lbl2, lbl2Constraints);
        exportBDPane.add(lbl2); 
        GridBagConstraints separatorConstraints = new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1, 1, 1, 1), 0, 0);        
        exportBDLayout.setConstraints(separator, separatorConstraints);
        exportBDPane.add(separator); 
        GridBagConstraints DbExportBtnConstraints = new GridBagConstraints(0, 2, 2, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(1, 1, 1, 1), 0, 0);        
        exportBDLayout.setConstraints(DbExportBtn, DbExportBtnConstraints);
        exportBDPane.add(DbExportBtn); 

        if (!canExportText) {
            lbl1.setVisible(false);
            lbl2.setVisible(false);
            dir.setVisible(false);
            separator.setVisible(false);
            DbExportBtn.setVisible(false);
        }
        
        JLabel warningLabel = new JLabel("Важно! Если БД имеет подчиненные БД, то перед тем как экспортировать базу данных в текстовый файл необходимо сделать экспорт новых изменений.");
        GridBagConstraints warningConstraints = new GridBagConstraints(0, 4, 5, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0);        
        exportBDLayout.setConstraints(warningLabel, warningConstraints);
        exportBDPane.add(warningLabel);
        GridBagConstraints panelLastImportFileConstraints = new GridBagConstraints(0, 4, 5, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 1, 1, 1), 0, 0);
        importLayout.setConstraints(panelLastImportFile, panelLastImportFileConstraints);
        importPane.add(panelLastImportFile);
        
        popupMenu.add(copyNameItem);
        popupMenu.add(copyPathItem);
        copyNameItem.addActionListener(this);
        copyPathItem.addActionListener(this);
        
        impFileNameLabel.addMouseListener(new MouseAdapter() {
        	 @Override
        	 public void mouseClicked(MouseEvent e) {
        		 if (SwingUtilities.isRightMouseButton(e)) {
        			 getPopup(0).show(e.getComponent(), e.getX(), e.getY());
        		 }
        	 }
		});
        panelLastImportFile.add(impFileNameLabel, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE,  new Insets(0, 0, 5, 0), 0, 0));
        panelLastImportFile.add(impDateLabel, new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE,  new Insets(0, 0, 5, 0), 0, 0));
        filesSourceLabel.addMouseListener(new MouseAdapter() {
       	 @Override
       	 public void mouseClicked(MouseEvent e) {
       		 if (SwingUtilities.isRightMouseButton(e)) {
       			getPopup(1).show(e.getComponent(), e.getX(), e.getY());
       		 }
       	 }
		});
        panelLastImportFile.add(filesSourceLabel, new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE,  new Insets(0, 0, 5, 0), 0, 0));
        panelLastImportFile.add(impChangesLabel, new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE,  new Insets(0, 0, 5, 0), 0, 0));
        impChangesInfoTA.setEditable(false);
        impChangesInfoTA.setWrapStyleWord(true);
        impChangesInfoTA.setLineWrap(true);
        JScrollPane impChangesInfoSP = new JScrollPane(impChangesInfoTA);
        Utils.setAllSize(impChangesInfoSP, new Dimension(400, 100));
        panelLastImportFile.add(impChangesInfoSP, new GridBagConstraints(0, 4, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE,  new Insets(0, 0, 5, 0), 0, 0));
        
        loadLastImportInfo();
        
        allImportStartLabel.setVisible(false);
        panelLastImportFile.add(allImportStartLabel, new GridBagConstraints(0, 5, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE,  new Insets(0, 0, 5, 0), 0, 0));
        allImportFinishLabel.setVisible(false);
        panelLastImportFile.add(allImportFinishLabel, new GridBagConstraints(0, 6, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE,  new Insets(0, 0, 5, 0), 0, 0));
        importResultLabel.setVisible(false);
        panelLastImportFile.add(importResultLabel, new GridBagConstraints(0, 7, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE,  new Insets(0, 0, 5, 0), 0, 0));
        currentImportFileLabel.setVisible(false);
        panelLastImportFile.add(currentImportFileLabel, new GridBagConstraints(0, 8, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE,  new Insets(0, 0, 5, 0), 0, 0));
    	GradientProgressBarUI progressBarUI = new GradientProgressBarUI(Color.WHITE, Color.BLACK, Color.GRAY);
    	filesProgressBar.setUI(progressBarUI);
    	filesProgressBar.setBorder(null);
        filesProgressBar.setMinimum(0);
        filesProgressBar.setVisible(false);
        Utils.setAllSize(filesProgressBar, new Dimension(400, 10));
        panelLastImportFile.add(filesProgressBar, new GridBagConstraints(0, 9, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE,  new Insets(0, 0, 5, 0), 0, 0));
        
        onBeforeImportInfoLabel.setVisible(false);
        panelLastImportFile.add(onBeforeImportInfoLabel, new GridBagConstraints(0, 10, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE,  new Insets(0, 0, 5, 0), 0, 0));

        currentImportModelChangeLabel.setVisible(false);
        panelLastImportFile.add(currentImportModelChangeLabel, new GridBagConstraints(0, 11, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE,  new Insets(0, 0, 5, 0), 0, 0));

        currentImportDataChangeLabel.setVisible(false);
        panelLastImportFile.add(currentImportDataChangeLabel, new GridBagConstraints(0, 12, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE,  new Insets(0, 0, 5, 0), 0, 0));

        onAfterImportInfoLabel.setVisible(false);
        panelLastImportFile.add(onAfterImportInfoLabel, new GridBagConstraints(0, 13, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE,  new Insets(0, 0, 5, 0), 0, 0));

        areaInfo.setWrapStyleWord(true);
        areaInfo.setLineWrap(true);
        areaInfo.setBorder(new LineBorder(Color.black, 1, true));
        areaScrOnBefAct.setWrapStyleWord(true);
        areaScrOnBefAct.setLineWrap(true);
        areaScrOnBefAct.setBorder(new LineBorder(Color.black, 1, true));
        areaScrOnAftAct.setWrapStyleWord(true);
        areaScrOnAftAct.setLineWrap(true);
        areaScrOnAftAct.setBorder(new LineBorder(Color.black, 1, true));
        JLabel contentInfoLabel = new JLabel("Введите информацию о содержимом репликационного файла");
        exportPane.add(contentInfoLabel, new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(15, 15, 15, 15), 0, 0));      
        exportPane.add(lastChangesInfoBtn, new GridBagConstraints(0, 4, 1, 1, 0, 0, GridBagConstraints.NORTH, GridBagConstraints.NONE,  new Insets(5, 0, 5, 0), 0, 0));
        JScrollPane spInfo = new JScrollPane(areaInfo);
        spInfo.setPreferredSize(new Dimension(585, 150));
        exportPane.add(spInfo, new GridBagConstraints(0, 5, 1, 1, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(15, 15, 15, 15), 0, 0));
        JLabel formulaBefImpLabel = new JLabel("Формула, выполняемая перед импортом");
        exportPane.add(formulaBefImpLabel, new GridBagConstraints(1, 3, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(15, 15, 15, 15), 0, 0));
        JScrollPane spScrOnBefAct = new JScrollPane(areaScrOnBefAct);
        spScrOnBefAct.setPreferredSize(new Dimension(585, 150));
        exportPane.add(spScrOnBefAct, new GridBagConstraints(1, 5, 1, 1, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(15, 15, 15, 15), 0, 0));
        JLabel formulaAftImpLabel = new JLabel("Формула, выполняемая после импорта");
        exportPane.add(formulaAftImpLabel, new GridBagConstraints(2, 3, 1, 1, 0, 0, GridBagConstraints.SOUTH, GridBagConstraints.NONE, new Insets(15, 15, 15, 15), 0, 0));
        JScrollPane spScrOnAftAct = new JScrollPane(areaScrOnAftAct);
        spScrOnAftAct.setPreferredSize(new Dimension(585, 150));
        exportPane.add(spScrOnAftAct, new GridBagConstraints(2, 5, 1, 1, 1, 1, GridBagConstraints.SOUTH, GridBagConstraints.BOTH, new Insets(15, 15, 15, 15), 0, 0));
        
        GridBagConstraints panelLastEportFileConstraints = new GridBagConstraints(1, 6, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(1, 1, 30, 1), 0, 0);        
        exportLayout.setConstraints(panelLastExportFile, panelLastEportFileConstraints);
        exportPane.add(panelLastExportFile);     
        panelLastExportFile.add(expDateLabel, new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE,  new Insets(0, 0, 5, 0), 0, 0));
        panelLastExportFile.add(expChangesLabel, new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE,  new Insets(0, 0, 5, 0), 0, 0));
        expChangesInfoTA.setEditable(false);
        expChangesInfoTA.setWrapStyleWord(true);
        expChangesInfoTA.setLineWrap(true);
        JScrollPane expChangesInfoSP = new JScrollPane(expChangesInfoTA);
        Utils.setAllSize(expChangesInfoSP, new Dimension(600, 100));
        panelLastExportFile.add(expChangesInfoSP, new GridBagConstraints(0, 4, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE,  new Insets(2, 0, 0, 0), 0, 0));
        
        loadLastExportInfo();

        ImportBtn.addActionListener(this);
        ExportBtn.addActionListener(this);
        DbExportBtn.addActionListener(this);
        refreshMethodsBtn.addActionListener(this);
        lastChangesInfoBtn.addActionListener(this);
        ImportBtn.setVisible(canImport);
        ExportBtn.setVisible(canExport);
        
        setSelectedIndex(0);
        
        setOpaque(isOpaque);
        panelLastImportFile.setOpaque(isOpaque);
        panelLastExportFile.setOpaque(isOpaque);
        importPane.setOpaque(isOpaque);
        exportPane.setOpaque(isOpaque);
        exportBDPane.setOpaque(isOpaque);
        spInfo.setOpaque(isOpaque);
        spInfo.getViewport().setOpaque(isOpaque);
        spScrOnBefAct.setOpaque(isOpaque);
        spScrOnBefAct.getViewport().setOpaque(isOpaque);
        spScrOnAftAct.setOpaque(isOpaque);
        spScrOnAftAct.getViewport().setOpaque(isOpaque);
        if(isAllowConvertDb){
        	this.addTab(" П р е о б р а з о в а н и е  Б Д ",utilBDPane);
	        strBaseSysBtn.addActionListener(this);
	        strBaseRealBtn.addActionListener(this);
	        convertToSystemBtn.addActionListener(this);
	        convertToRealBtn.addActionListener(this);
	        stringFieldBaseSys.setEnabled(false);
	        stringFieldBaseReal.setEnabled(false);
	        GridBagLayout utilBDLayout = new GridBagLayout();
	        utilBDPane.setLayout(utilBDLayout);
	        JLabel cbl1 = new JLabel("Преобразовать базу из реальной в системную: ");
	        JLabel cbl2 = new JLabel("Преобразовать базу из системной в реальную: ");
	        GridBagConstraints cbl1Constraints = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(1, 1, 1, 1), 0, 0);        
	        utilBDLayout.setConstraints(cbl1, cbl1Constraints);
	        utilBDPane.add(cbl1); 
	        GridBagConstraints strBaseBtnConstraints = new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1, 1, 1, 1), 0, 0);        
	        utilBDLayout.setConstraints(strBaseSysBtn, strBaseBtnConstraints);
	        utilBDPane.add(strBaseSysBtn); 
	        GridBagConstraints stringFieldBaseConstraints = new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.RELATIVE, new Insets(1, 1, 1, 1), 0, 0);        
	        utilBDLayout.setConstraints(stringFieldBaseSys, stringFieldBaseConstraints);
	        utilBDPane.add(stringFieldBaseSys); 
	        GridBagConstraints convertToSystemBtnConstraints = new GridBagConstraints(3, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(1, 1, 1, 1), 0, 0);        
	        utilBDLayout.setConstraints(convertToSystemBtn, convertToSystemBtnConstraints);
	        utilBDPane.add(convertToSystemBtn); 
	        GridBagConstraints cbl2Constraints = new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(1, 1, 1, 1), 0, 0);        
	        utilBDLayout.setConstraints(cbl2, cbl2Constraints);
	        utilBDPane.add(cbl2); 
	        GridBagConstraints strBaseBtnConstraints_ = new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1, 1, 1, 1), 0, 0);        
	        utilBDLayout.setConstraints(strBaseRealBtn, strBaseBtnConstraints_);
	        utilBDPane.add(strBaseRealBtn); 
	        GridBagConstraints stringFieldBaseConstraints_ = new GridBagConstraints(2, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.RELATIVE, new Insets(1, 1, 1, 1), 0, 0);        
	        utilBDLayout.setConstraints(stringFieldBaseReal, stringFieldBaseConstraints_);
	        utilBDPane.add(stringFieldBaseReal); 
	        GridBagConstraints convertToRealBtnConstraints = new GridBagConstraints(3, 1, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(1, 1, 1, 1), 0, 0);        
	        utilBDLayout.setConstraints(convertToRealBtn, convertToRealBtnConstraints);
	        utilBDPane.add(convertToRealBtn); 
	        setStrBd();
	        utilBDPane.repaint();
        }
    }
    
    private JPopupMenu getPopup(int type) {
    	switch (type) {
	    	case 0:
	    		copyNameItem.setVisible(true);
	    		copyPathItem.setVisible(false);
	    		break;
	    	case 1:
	    		copyNameItem.setVisible(false);
	    		copyPathItem.setVisible(true);
	    		break;
    	}
        return popupMenu;
    }
    
    private void loadLastImportInfo() {
    	Kernel kernel = Kernel.instance();
        try {
        	KrnClass importCls = kernel.getClassByName("Import");
            KrnObject[] imps = kernel.getClassObjects(importCls, 0);
            Arrays.sort(imps,new Comparator<KrnObject>() {
                public int compare(KrnObject o1, KrnObject o2) {
                    return (o1.id < o2.id ? -1 : o1.id == o2.id ? 0 : 1);
                }
            });
            if(imps.length > 0) {
                String fileName = kernel.getStringsSingular(imps[imps.length - 1].id, kernel.getAttributeByName(importCls, "file_name").id, 0, false, false);
                impFileNameLabel.setText(impFileNameString + fileName);
                String lastImportDate = "";
                TimeValue[] timeValues = kernel.getTimeValues(new long[] {imps[imps.length - 1].id}, kernel.getAttributeByName(importCls, "importFinish"), 0);
                if (timeValues != null && timeValues.length > 0) {
                	com.cifs.or2.kernel.Time or3Time = timeValues[0].value;
                	GregorianCalendar calendar = new GregorianCalendar(or3Time.year, or3Time.month, or3Time.day, or3Time.hour, or3Time.min, or3Time.sec);
                	lastImportDate = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss").format(calendar.getTime());
                } else {
                    DateValue[] dateValues = kernel.getDateValues(new long[] {imps[imps.length - 1].id}, kernel.getAttributeByName(importCls, "date"), 0);
                    if (dateValues != null && dateValues.length > 0) {
                    	com.cifs.or2.kernel.Date or3Date = dateValues[0].value;
                    	GregorianCalendar calendar = new GregorianCalendar(or3Date.year, or3Date.month, or3Date.day);
                    	lastImportDate = new SimpleDateFormat("dd-MM-yyyy").format(calendar.getTime());
                    }
                }
                impDateLabel.setText(impDateString + lastImportDate);
                String[] changesInfo = kernel.getMemos(imps[imps.length - 1], kernel.getAttributeByName(importCls, "информация о содержимом файла"), 0, 0);
                impChangesInfoTA.setText((changesInfo != null && changesInfo.length > 0) ? changesInfo[0] : "");
            }
            String replicationDirectory = kernel.getReplicationDirectoryPath();
            filesSourceLabel.setText(filesSourceString + replicationDirectory);
        } catch (KrnException e) {
            e.printStackTrace();
        }
    }
    
    private void loadLastExportInfo() {
    	Kernel kernel = Kernel.instance();
        try {
        	KrnClass exportCls = kernel.getClassByName("Export");
            KrnObject[] exps = kernel.getClassObjects(exportCls, 0);
            Arrays.sort(exps,new Comparator<KrnObject>() {
                public int compare(KrnObject o1, KrnObject o2) {
                    return (o1.id < o2.id ? -1 : o1.id == o2.id ? 0 : 1);
                }
            });
            if(exps.length > 0) {
            	panelLastExportFile.setVisible(true);
                DateValue[] dateValues = kernel.getDateValues(new long[] {exps[exps.length - 1].id}, kernel.getAttributeByName(exportCls, "date"), 0);
                String lastExportDate = "";
                if (dateValues != null && dateValues.length > 0) {
                	com.cifs.or2.kernel.Date or3Date = dateValues[0].value;
                	GregorianCalendar calendar = new GregorianCalendar(or3Date.year, or3Date.month, or3Date.day);
                	SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                	lastExportDate = dateFormat.format(calendar.getTime());
                }
                expDateLabel.setText(expDateString + lastExportDate);
                String[] changesInfo = kernel.getMemos(exps[exps.length - 1], kernel.getAttributeByName(exportCls, "информация о содержимом файла"), 0, 0);
                expChangesInfoTA.setText((changesInfo != null && changesInfo.length > 0) ? changesInfo[0] : "");
            } else {
            	panelLastExportFile.setVisible(false);
            }
        } catch (KrnException e) {
            e.printStackTrace();
        }
    }

	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		Kernel krn = Kernel.instance();
		final JFrame frm = (JFrame) getTopLevelAncestor();
		if (src == ImportBtn) {
			try {
				//Проверка наличия активных потоков
				Map<Long,Long> activeFlows=krn.getActiveFlows();
				if(activeFlows.size()>0) {
					MessagesFactory.showMessageDialog(frm, MessagesFactory.ERROR_MESSAGE, "Необходимо дождаться завершения работы активных процессов!");
					return;
				}
				
				if (Constants.CHECK_DB_BLOCK) {
					//Проверка наличия блокировок на таблицах: t_attrs, t_classes, t_ids
					List<String> showDbLocks = krn.showDbLocks();
					if(showDbLocks.size()>0) {
						String msg = "В базе присутствуют следующие блокировки!";
						for(String showDbLock:showDbLocks) {
							msg+="\n"+showDbLock;
						}
						int result =MessagesFactory.showMessageDialog(frm, MessagesFactory.QUESTION_MESSAGE, msg);
						if(result!=3)
						return;
					}
				}
				
				//Проверка блокировки сервера
				if(krn.isServerBlocked()==0) {
					int result=MessagesFactory.showMessageDialog(frm, MessagesFactory.QUESTION_MESSAGE, "Необходимо заблокировать сервер перед импортом!\nЗаблокировать?");
					if(result==3) {
						if(krn.blockServer(false)==0) {
							MessagesFactory.showMessageDialog(frm, MessagesFactory.ERROR_MESSAGE, "Сервер заблокировать не удалось!");
							return;
						}
					}else
						return;
				}
			} catch (KrnException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			int res = MessagesFactory.showMessageDialog(frm, MessagesFactory.QUESTION_MESSAGE, "Запустить импорт?");
			if (res == 3) {
				allImportStartLabel.setText(allImportStartString + format.format(new Date()));
				allImportStartLabel.setVisible(true);
				allImportFinishLabel.setVisible(false);
				importResultLabel.setVisible(false);
				currentImportFileLabel.setText(currentImportFileString);
				currentImportFileLabel.setVisible(true);
				filesProgressBar.setValue(0);
				filesProgressBar.setVisible(true);
				krn.setChanges(this);
			}
		} else if (src == ExportBtn) {
			int res = MessagesFactory.showMessageDialog(frm, MessagesFactory.QUESTION_MESSAGE, "Запустить экспорт новых изменений?");
			if (res == 3) {
				try {
					Set<String> uids = new HashSet<String>();
			        Pattern p = Pattern.compile("[0-9]+\\.[0-9]+");  
			        Matcher m = p.matcher(Funcs.normalizeInput(areaScrOnBefAct.getText()));  
			        while (m.find( )) {
			        	uids.add(m.group(0));
			        }
			        m = p.matcher(Funcs.normalizeInput(areaScrOnAftAct.getText()));  
			        while (m.find( )) {
			        	uids.add(m.group(0));
			        }
					List<KrnVcsChange> changes = krn.getVcsChanges(Constants.VCS_NOT_FIXD, Constants.VCS_EXPORT, -1, -1);
		            List<UncommitedObjectsListItem> uncommitedObjects = new ArrayList<UncommitedObjectsListItem>();
					for (int i = 0; i < changes.size(); i++) {
						KrnVcsChange change = changes.get(i);
						if (change.cvsChangeObj != null && uids.contains(change.cvsChangeObj.uid)) {
							uncommitedObjects.add(new UncommitedObjectsListItem(change.cvsChangeObj));
						}
					}
					if (uncommitedObjects.size() > 0) {
						UncommitedObjectsList uncommitedObjectsList = new UncommitedObjectsList();
						uncommitedObjectsList.addToList(uncommitedObjects.toArray());
						DesignerModalFrame dialog = new DesignerModalFrame((Frame) this.getTopLevelAncestor(), "Внимание! Формулы содержат ссылки на объекты, по которым не выполнен commit.", uncommitedObjectsList);
						uncommitedObjectsList.setParent(dialog);
						dialog.setOkText("Игнорировать");
						dialog.setCancelBtnText("Отмена экспорта");
						dialog.setLocation(kz.tamur.comps.Utils.getCenterLocationPoint(dialog.getSize()));
						dialog.show();
						if (dialog.getResult() == 1) {
							return;
						}
					}
					int exportResult = krn.getChanges(1, areaInfo.getText(), areaScrOnBefAct.getText(), areaScrOnAftAct.getText());
				    // 0 - успешный экспорт,  1 - файл экспорта не создан, 2 - нет предыдущего экспорта, 3 - нет изменений
					String messageText;
					if (exportResult == 0) {
						messageText = "Экспорт данных успешно завершен!";
					} else if (exportResult == 1) {
						messageText = "Экспорт не выполнен. Не удалось создать файл экспорта!";
					} else if (exportResult == 2) {
						messageText = "Экспорт не выполнен. Предыдущий экспорт не найден!";
					} else if (exportResult == 3) {
						messageText = "Экспорт не выполнен. Отсутствуют изменения!";
					} else {
						messageText = "Экспорт не выполнен. Выполняется процесс репликации!";
					}
					MessagesFactory.showMessageDialog(frm, exportResult == 0 ? MessagesFactory.INFORMATION_MESSAGE : MessagesFactory.ERROR_MESSAGE, messageText);
				} catch (KrnException e1) {
					e1.printStackTrace();
					MessagesFactory.showMessageDialog(frm, MessagesFactory.ERROR_MESSAGE, "Ошибка при экспорте данных!");
				}
				loadLastExportInfo();
				areaInfo.setText("");//Очищаем поле
			}
		} else if (src == DbExportBtn) {
			int res = MessagesFactory.showMessageDialog(frm, MessagesFactory.QUESTION_MESSAGE, "Запустить экспорт базы данных?");
			if (res == 3) {
				krn.dbExport(dir.getText(), separator.getText());
				MessagesFactory.showMessageDialog(frm, MessagesFactory.INFORMATION_MESSAGE, "Экспорт данных завершен.");
			}
		} else if (src == refreshMethodsBtn) {
			int res = MessagesFactory.showMessageDialog(frm, MessagesFactory.QUESTION_MESSAGE, "Запустить обновление создания всех методов?");
			if (res == 3) {
				try {
					krn.refreshMethodsForReplication();
				} catch (KrnException ex) {
					ex.printStackTrace();
				}
				MessagesFactory.showMessageDialog(frm, MessagesFactory.INFORMATION_MESSAGE, "Обновление завершено.");
			}
		} else if (src == lastChangesInfoBtn) {
			frm.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			try {
				List<KrnVcsChange> changes_count = krn.getVcsChanges(Constants.VCS_FIXD, Constants.VCS_COUNT, -1, -1);
				long count=changes_count.get(0).count;
				if(count>1000) {
					int res = MessagesFactory.showMessageDialog(frm, MessagesFactory.QUESTION_MESSAGE, "Количество опсаний изменений равно '"+count+"' продолжить вывод?");
					if (res != 3) { 
						frm.setCursor(Cursor.getDefaultCursor());
						return;
					}
				}
				List<KrnVcsChange> changes = krn.getVcsChanges(Constants.VCS_FIXD, -1, -1, -1);
				Kernel kernel = Kernel.instance();
				KrnClass userCls = kernel.getClassByName("User");
				final KrnAttribute nameAttr = Kernel.instance().getAttributeByName(userCls, "name");
				final Map<Long, String> userNameById = new HashMap<Long, String>();
				Collections.sort(changes, new Comparator<KrnVcsChange>() {
					@Override
					public int compare(KrnVcsChange o1, KrnVcsChange o2) {
						try {
							String userName1;
							if (userNameById.containsKey(o1.user.id)) {
								userName1= userNameById.get(o1.user.id);
							} else {
								userName1 = Kernel.instance().getStringsSingular(o1.user.id, nameAttr.id, 0, false, false);
								userNameById.put(o1.user.id, userName1);
							}
							String userName2;
							if (userNameById.containsKey(o2.user.id)) {
								userName2 = userNameById.get(o2.user.id);
							} else {
								userName2 = Kernel.instance().getStringsSingular(o2.user.id, nameAttr.id, 0, false, false);
								userNameById.put(o2.user.id, userName2);
							}
							return userName1.compareTo(userName2);
						} catch (KrnException e) {
							System.out.println("Ошибка при сортировке списка последних изменений.");
							e.printStackTrace();
						}
						return 0;
					}
					
				});
				DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
				areaInfo.setText("");//Очищаем поле
				for (KrnVcsChange change: changes) {
					String userName = Kernel.instance().getStringsSingular(change.user.id, nameAttr.id, 0, false, false);
					areaInfo.append(" - " + change.toString() + "   " + df.format(change.dateChange) + "   " + userName + "   " + change.comment + "\n");
				}
			} catch (KrnException exception) {
				exception.printStackTrace();
			}
			frm.setCursor(Cursor.getDefaultCursor());
		} else if (src == copyNameItem) {
			String fileName = impFileNameLabel.getText();
			fileName = fileName.substring(impFileNameString.length());
            ClientUtils.setClipboard(fileName);
		} else if (src == copyPathItem) {
			String filePath = filesSourceLabel.getText();
			filePath = filePath.substring(filesSourceString.length());
            ClientUtils.setClipboard(filePath);
		}else if(src==strBaseSysBtn || src==strBaseRealBtn){
                final BaseTree tree = kz.tamur.comps.Utils.getBaseTree(
                        value, src==strBaseSysBtn?lastSelectedBaseSys:lastSelectedBaseReal);
                final JScrollPane scroller = new JScrollPane(tree);
                scroller.setPreferredSize(new Dimension(500, 600));
                scroller.setOpaque(isOpaque);
                scroller.getViewport().setOpaque(isOpaque);
                DesignerDialog dlg = new DesignerDialog(Or3Frame.instance(),
                        "Выберите структуру баз", scroller, true);
                dlg.show();
                int res = dlg.getResult();
                if (res != ButtonsFactory.BUTTON_NOACTION
                        && res == ButtonsFactory.BUTTON_OK) {
                    KrnObject obj = tree.getSelectedNode().getKrnObj();
                    if (obj != null) {
                        value = obj;
                        if(src==strBaseSysBtn){
                        	stringFieldBaseSys.setText(parseObjectToTitle(lastSelectedBaseSys = obj));
                        }else{
                        	stringFieldBaseReal.setText(parseObjectToTitle(lastSelectedBaseReal = obj));
                        }
                        utilBDPane.revalidate();
                    }
                } else if (dlg.getResult() == ButtonsFactory.BUTTON_CLEAR){
                    value = null;
                    if(src==strBaseSysBtn)
                    	stringFieldBaseSys.setText("");
                    else
                    	stringFieldBaseReal.setText("");
                }
		}else if(src==convertToSystemBtn){
			int ind0=urlConnection.indexOf("?");
			int ind1=0;
			int ind2=0;
			int ind3=0;
			String dbname="";
			if(ind0<0){
				ind1=urlConnection.indexOf(";");
				ind2=urlConnection.indexOf("databaseName");
				ind3=urlConnection.indexOf(";",ind2);
				dbname=urlConnection.substring(ind2,ind3);
			}
			int res = MessagesFactory.showMessageDialog(frm, MessagesFactory.QUESTION_MESSAGE, "Вы действительно хотите преобразовать базу:\n'"
					+urlConnection.substring(0, ind0>0?ind0:ind1)+(ind0>0?"":" "+dbname)
					+"'\n в системную?");
			if (res == ButtonsFactory.BUTTON_YES) {
				try {
					//установить в t_ids идентификатор системной структуры БД
					krn.setDbId("dbase_id", lastSelectedBaseSys.id);
					//создать объект в экспорте
					KrnObject expObj=krn.createObject(Kernel.SC_EXPORT,0);
					//записать в last
					long chLastId=krn.getLastId("t_changes");
					long cchLastId=krn.getLastId("t_changescls");
					krn.setLong(expObj.id,expObj.classId, "change_id", 0, chLastId, 0);
					krn.setLong(expObj.id,expObj.classId, "clschange_id", 0, cchLastId, 0);
					krn.setLong(expObj.id,expObj.classId, "prior_change_id", 0, chLastId, 0);
					krn.setLong(expObj.id,expObj.classId, "prior_clschange_id", 0, cchLastId, 0);
					krn.setLong(expObj.id,expObj.classId, "change_count", 0, 0, 0);
					krn.setLong(expObj.id,expObj.classId, "clschange_count", 0, 0, 0);
					krn.setObject(expObj.id,expObj.classId, "to_database", 0, lastSelectedBaseSys.id, 0,true);
					//очищаю импорт
					KrnObject[] objs=krn.getClassObjects(Kernel.SC_IMPORT, 0);
					for(KrnObject obj:objs){
						krn.deleteObject(obj, 0);
					}
					//заменяю все ссылки на системную структуру БД
					boolean resConvertLincs=krn.convertLinkForSysDb(lastSelectedBaseSys.id, lastSelectedBaseReal.id);
	                MessagesFactory.showMessageDialog(frm, MessagesFactory.INFORMATION_MESSAGE,resConvertLincs?
	                		"Замена ссылок на системную структуру БД прошла успешно!":
	                			"Ошибка при замене ссылок на системную структуру БД!");
				} catch (KrnException e1) {
					e1.printStackTrace();
				}
			}
		}else if(src==convertToRealBtn){
			int res = MessagesFactory.showMessageDialog(frm, MessagesFactory.QUESTION_MESSAGE, "Вы действительно хотите преобразовать базу:\n'"
					+urlConnection.substring(0, urlConnection.indexOf("?"))
					+"'\n в реальную?");
			if (res == ButtonsFactory.BUTTON_YES) {
				try {
					//проверяю совпадют ли значения последних идентификаторов t_changes,t_changescls
					//с соответствующими значениями в последнем экспорте
					KrnObject[] objs=krn.getClassObjects(Kernel.SC_EXPORT, 0);
			        KrnObject last = null;
			        KrnObject priorExp = null;
					long chLastId=krn.getLastId("t_changes");
					long cchLastId=krn.getLastId("t_changescls");
					boolean par_err=false;
			        //ищем экспорт с максимальным номером
		            for (KrnObject obj:objs) {
		            	if(last==null)
		            		last=obj;
		            	else if (obj.id > last.id) {
		                    last = obj;
		                }
		            }
		            if(last==null && (chLastId>0 || cchLastId>0))
		            		par_err=true;
		            else{
						long[] chIds=krn.getLongs(last, "change_id", 0);
						long[] cchIds=krn.getLongs(last, "clschange_id", 0);
						if((chIds.length>0 && chIds[0]<chLastId) || (cchIds.length>0 && cchIds[0]<cchLastId))
		            		par_err=true;
		            }
		            par_err=true;
		            if(par_err){
		    			Container cont = getTopLevelAncestor();
		                MessagesFactory.showMessageDialog(cont instanceof Dialog ? (Dialog) cont : (Frame) cont
		                		, MessagesFactory.INFORMATION_MESSAGE,"База не подходит для перевода в реальную.\nДля этого предварительно нужно произвести экспорт!");
		            }else{
						//установить в t_ids идентификатор реальной структуры БД
						krn.setDbId("dbase_id", lastSelectedBaseReal.id);
						//очищаю импорт и экспорт
						for(KrnObject obj:objs){
							krn.deleteObject(obj, 0);
						}
						objs=krn.getClassObjects(Kernel.SC_IMPORT, 0);
						for(KrnObject obj:objs){
							krn.deleteObject(obj, 0);
						}
		            }
					//
				} catch (KrnException e1) {
					e1.printStackTrace();
				}
			}
		}
			
	}

	class UncommitedObjectsList extends JPanel {

	    private JList list = new JList();
	    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
	    private DesignerModalFrame parent;
	    
	    public UncommitedObjectsList() {
	        super();
	        setLayout(new BorderLayout());
	        init();
	    }
	    
	    public void setParent(DesignerModalFrame dialog) {
	        parent = dialog;
	    }
	    
	    void init() {
	        setOpaque(isOpaque);
	        list.setBackground(Color.lightGray);
	        list.setCellRenderer(new UncommitedObjectsListCellRenderer());
	        JScrollPane scroll =  new JScrollPane(list);
	        list.setOpaque(isOpaque);
	        scroll.setOpaque(isOpaque);
	        scroll.getViewport().setOpaque(isOpaque);
	        add(scroll, BorderLayout.CENTER);
	        setPreferredSize(new Dimension(800, 400));
	    }

	    public int getListSize() {
	        return list.getModel().getSize();
	    }


	    public void addToList(Object[] items) {
	        list.setListData(items);
	    }

	    public JList getList() {
	        return list;
	    }
	    
	    public void setList(JList list) {
	        this.list = list;
	    }

	    class UncommitedObjectsListCellRenderer extends JLabel implements ListCellRenderer {

	    	private ImageIcon imOptional = kz.tamur.rt.Utils.getImageIcon("optional");

	        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
	            setFont(new Font("Dialog", Font.PLAIN, 12));
	            setForeground(Color.black);
	            setBackground(isSelected ? Color.white : Color.lightGray);
	            setOpaque(isSelected || isOpaque);
	            setText(value.toString());
	            setIcon(imOptional);
	            return this;
	        }
	    }
	}
	
	class UncommitedObjectsListItem extends Component {
	    
		private KrnObject object;
	    
	    public UncommitedObjectsListItem(KrnObject object) {
	        super();
	        this.object = object;
	    }
	    
	    public String toString() {
	        try {
				return Kernel.instance().getClass(object.classId).name + " - " + object.uid;
			} catch (KrnException e) {
				e.printStackTrace();
			}
	        return "Объект не определен";
	    }
	}
	
	public void replFilesProgress(int type, int filesCount, int currentFileNumber, String currentFileName, Time importTime) {
		System.out.println(currentFileNumber + "/" + filesCount + "type: " + type + ", currentFileName: " + currentFileName);
		if (type == 0) {
			currentImportFileLabel.setText(currentImportFileString + currentFileNumber + "/" + filesCount + "   " + currentFileName + "   Время старта " + format.format(kz.tamur.util.Funcs.convertToDate(importTime)));
	        onBeforeImportInfoLabel.setVisible(false);
			currentImportModelChangeLabel.setVisible(false);
			currentImportDataChangeLabel.setVisible(false);
	        onAfterImportInfoLabel.setVisible(false);
		} else {
			filesProgressBar.setMaximum(filesCount);
			filesProgressBar.setValue(currentFileNumber);
	    	loadLastImportInfo();
		}
	}
	
	public void replChangesProgress(int type, int currentChangeNumber, int changesCount, String changeType, String changeId) {
		System.out.println(currentChangeNumber + "/" + changesCount + "type: " + type + ", changeType: " + changeType + ", changeId: " + changeId);
		if (type == 0) {
			if ("ModelChange".equals(changeType)) {
				currentImportModelChangeLabel.setText(currentImportModelChangeString + currentChangeNumber + "/" + changesCount + "   id записи - " + changeId);
			} else {
				currentImportDataChangeLabel.setText(currentImportDataChangeString + currentChangeNumber + "/" + changesCount + "   uid объекта - " + changeId);
			}
		} else if (type == 2) {
			if ("ModelChange".equals(changeType)) {
				if (changesCount > 0) {
					currentImportModelChangeLabel.setText(currentImportModelChangeString);
					currentImportModelChangeLabel.setVisible(true);
				}
			} else {
				if (changesCount > 0) {
					currentImportDataChangeLabel.setText(currentImportDataChangeString);
					currentImportDataChangeLabel.setVisible(true);
				}
			}
		} else if (type == 3) {
			if ("ScriptOnBeforeAction".equals(changeType)) {
				if (currentChangeNumber == 0) {
					onBeforeImportInfoLabel.setText("Выполняется скрипт 'Перед импортом репликации'...");
					onBeforeImportInfoLabel.setVisible(true);
				} else {
					onBeforeImportInfoLabel.setText("Скрипт 'Перед импортом репликации' выполнен.");
				}
			} else if ("ScriptOnAfterAction".equals(changeType)) {
				if (currentChangeNumber == 0) {
					onAfterImportInfoLabel.setText("Выполняется скрипт 'После импорта репликации'...");
					onAfterImportInfoLabel.setVisible(true);
				} else {
					onAfterImportInfoLabel.setText("Скрипт 'После импорта репликации' выполнен.");
				}
			}
 		}
	}
	
	private void setStrBd(){
		lastSelectedBaseReal=lastSelectedBaseSys=Kernel.instance().getCurrentDb();
		String strBdTitle=parseObjectToTitle(lastSelectedBaseSys);
		stringFieldBaseSys.setText(strBdTitle);
		stringFieldBaseReal.setText(strBdTitle);
	}
	
	private String parseObjectToTitle(KrnObject obj){
		String res="";
		try {
			String[] strs = Kernel.instance().getStrings(obj, "наименование", 0, 0);
		    if (strs.length > 0) {
		        res = strs[0];
		    } else {
		        res = "Значение не присвоено";
		    }
		} catch (KrnException e) {
			e.printStackTrace();
		}
	    return res;
	}

	@Override
	public void importResult(int event, String result) {
		allImportFinishLabel.setText(allImportFinishString + format.format(new Date()));
		allImportFinishLabel.setVisible(true);
		MessagesFactory.showMessageDialog((JFrame) getTopLevelAncestor(), event == 0 ? MessagesFactory.INFORMATION_MESSAGE : MessagesFactory.ERROR_MESSAGE, result);
		importResultLabel.setText(importResultString + (event == 0 ? "Успешное выполнение импорта репликации." : "Ошибка при выполнении импорта репликации."));
		importResultLabel.setVisible(true);
		currentImportFileLabel.setVisible(false);
        filesProgressBar.setVisible(false);
        onBeforeImportInfoLabel.setVisible(false);
        currentImportModelChangeLabel.setVisible(false);
        currentImportDataChangeLabel.setVisible(false);
        onAfterImportInfoLabel.setVisible(false);
	}
}