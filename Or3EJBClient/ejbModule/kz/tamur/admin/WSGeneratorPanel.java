package kz.tamur.admin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.io.FileUtils;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.ObjectValue;

import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.search.RoundedCornerBorder;
import kz.tamur.rt.MainFrame;
import kz.tamur.util.Funcs;

public class WSGeneratorPanel extends JPanel implements ActionListener {

    private JButton wsGeneratorBtn = ButtonsFactory.createToolButton("WSGenerator", ".png", "Генерирование WEB-сервисов");
    private JButton openBtn = ButtonsFactory.createToolButton("SelectWSDL", ".png", "Выбор WSDL-файла");
    private JButton startBtn = ButtonsFactory.createToolButton("StartGeneration", ".png", "Запуск генерирования сервиса");
    private JButton exampleRequestBtn = ButtonsFactory.createToolButton("ExampleXMLRequest", ".png", "Генерирование XML запроса");
    private JButton exampleResponseBtn = ButtonsFactory.createToolButton("ExampleXMLResponse", ".png", "Генерирование XML ответа");
    private JLabel propertiesLabel = kz.tamur.rt.Utils.createLabel("Генерирование WEB-сервисов");
    private JLabel selectedFileLabel = kz.tamur.rt.Utils.createLabel("Выбранный WSDL-файл:");
    private JLabel statusLabel = kz.tamur.rt.Utils.createLabel("Статус обработки: ");
    private JLabel existingServicesLabel = kz.tamur.rt.Utils.createLabel("Развернутые сервисы:");
    private JTextField packageNameTF = new JTextField();
    private JTextField methodNameTF = new JTextField();
    private JTextField xmlPathTF = new JTextField();
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
    private JPopupMenu settingsPopup = new JPopupMenu();
    private JScrollPane scrollPane;
    private JList existingServicesList = new JList();
    private File selectedWSDLFile = null;
    private Kernel kernel = Kernel.instance();
    private Font defaultFont = new Font("Calibri", Font.PLAIN, 10);

    public WSGeneratorPanel() {
        super(new BorderLayout());
        setOpaque(isOpaque);
        wsGeneratorBtn.addActionListener(this);
        openBtn.addActionListener(this);
        startBtn.addActionListener(this);
        exampleRequestBtn.addActionListener(this);
        exampleResponseBtn.addActionListener(this);
        setBorder(BorderFactory.createEmptyBorder());
        add(wsGeneratorBtn, BorderLayout.CENTER);
        startBtn.setEnabled(false);
        exampleRequestBtn.setEnabled(false);
        exampleResponseBtn.setEnabled(false);
        packageNameTF.setFont(defaultFont);
        methodNameTF.setFont(defaultFont);
        xmlPathTF.setFont(defaultFont);
        packageNameTF.setToolTipText("Наиманование пакета классов");
        methodNameTF.setToolTipText("Наиманование метода Or3");
        xmlPathTF.setToolTipText("Путь хранилища примера XML");
        initPopup();
    }

    private void initPopup() {
        settingsPopup.setBorder(new RoundedCornerBorder(Color.GRAY));
        propertiesLabel.setForeground(Color.BLUE);
        JPanel attributesPanel = new JPanel(new GridBagLayout());
        attributesPanel.add(propertiesLabel, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 5, 0), 0, 0));
        JPanel panel = new JPanel();
        panel.add(openBtn);
        panel.add(startBtn);
        panel.add(exampleRequestBtn);
        panel.add(exampleResponseBtn);
        attributesPanel.add(panel, new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        
        attributesPanel.add(packageNameTF, new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        attributesPanel.add(methodNameTF, new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        attributesPanel.add(xmlPathTF, new GridBagConstraints(0, 4, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

        attributesPanel.add(selectedFileLabel, new GridBagConstraints(0, 5, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        attributesPanel.add(statusLabel, new GridBagConstraints(0, 6, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        existingServicesLabel.setForeground(Color.BLUE);
        attributesPanel.add(existingServicesLabel, new GridBagConstraints(0, 7, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 0, 5, 0), 0, 0));
        scrollPane = new JScrollPane(existingServicesList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        existingServicesList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent arg0) {
                if (existingServicesList.getSelectedValue() != null) {
                    exampleRequestBtn.setEnabled(true);
                    exampleResponseBtn.setEnabled(true);
                } else {
                    exampleRequestBtn.setEnabled(false);
                    exampleResponseBtn.setEnabled(false);
                }
            }
        });
        existingServicesList.setBackground(settingsPopup.getBackground());
        existingServicesList.setCellRenderer(new ListCellRenderer() {
            DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();

            @Override
            public Component getListCellRendererComponent(JList arg0, Object arg1, int arg2, boolean arg3, boolean arg4) {
                JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(arg0, arg1, arg2, arg3, arg4);
                renderer.setText(" - " + renderer.getText());
                renderer.setForeground(Color.RED);
                renderer.setFont(kz.tamur.rt.Utils.getDefaultFont());
                return renderer;
            }
        });
        existingServicesList.setListData(getExistingServices().toArray());
        attributesPanel.add(scrollPane, new GridBagConstraints(0, 8, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        settingsPopup.add(attributesPanel);
    }
    
    private List<String> getExistingServices() {
        List<String> existingServices = new ArrayList<String>();
        try {
            KrnClass wsClass = kernel.getClassByName("WebService");
            if (wsClass != null) {
	            KrnObject[] wsObjects = kernel.getClassObjects(wsClass, 0);
	            for (int i = 0; i < wsObjects.length; i++) {
	                KrnObject wsObject = wsObjects[i];
	                KrnObject[] qnObjects =  kernel.getObjects(wsObject, "name", 0);
	                if (qnObjects != null && qnObjects.length > 0) {
	                    KrnObject qnObject = qnObjects[0];
	                    String[] lnStrings = kernel.getStrings(qnObject, "localName", 0, 0);
	                    if (lnStrings != null && lnStrings.length > 0) {
	                        existingServices.add(lnStrings[0]);
	                    }
	                }
	            }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return existingServices;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == wsGeneratorBtn) {
            statusLabel.setText("Статус обработки:");
            existingServicesList.setListData(getExistingServices().toArray());
            scrollPane.revalidate();
            scrollPane.repaint();
            settingsPopup.show(this, wsGeneratorBtn.getLocation().x, wsGeneratorBtn.getLocation().y + 35);
        } else if (source == startBtn) {
            try {
                String packageName = Funcs.sanitizeElementName(packageNameTF.getText());
                if (packageName.equals("")) {
                    statusLabel.setText("Статус обработки: Введите наименование пакета!");
                } else {
                    String methodName = Funcs.sanitizeElementName(methodNameTF.getText());
                    if (methodName.equals("")) {
                        statusLabel.setText("Статус обработки: Введите наименование метода Or3!");
                    } else {
                        byte[] wsdlFileInBytes = FileUtils.readFileToByteArray(selectedWSDLFile);
                        String statusMessage = Funcs.sanitizeElementName(Kernel.instance().generateWS(wsdlFileInBytes, selectedWSDLFile.getName(), packageName, methodName));
                        statusLabel.setText("Статус обработки: " + statusMessage);
                        existingServicesList.setListData(getExistingServices().toArray());
                        scrollPane.revalidate();
                        scrollPane.repaint();
                    }
                }
            } catch (IOException exception) {
                exception.printStackTrace();
                statusLabel.setText("Статус обработки: Ошибка при генерировании сервиса!");
            }
        } else if (source == openBtn) {
            statusLabel.setText("Статус:");
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Выбор WSDL-файлa");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("WSDL files", "wsdl"));
            int result = fileChooser.showDialog(null, "OK");                
            if (result == JFileChooser.APPROVE_OPTION) {
                selectedWSDLFile = fileChooser.getSelectedFile();
                selectedFileLabel.setText("Файл: " + selectedWSDLFile.getAbsolutePath());
                startBtn.setEnabled(true);
            } else {
                if (selectedWSDLFile == null) {
                    startBtn.setEnabled(false);
                }
            }
        } else if (source == exampleRequestBtn) {
            String xmlPath = Funcs.normalizeInput(xmlPathTF.getText());
            if (xmlPath.equals("")) {
                statusLabel.setText("Статус обработки: Введите путь для записи XML!");
            } else {
                String serviceName = (String) existingServicesList.getSelectedValue();
                try {
                    byte[] xmlFileInBytes = Kernel.instance().generateXML(serviceName, 0);
                    FileOutputStream fos = new FileOutputStream(serviceName + "_Request.xml");
                    fos.write(xmlFileInBytes);
                    fos.close();
                } catch (KrnException exception) {
                    exception.printStackTrace();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        } else if (source == exampleResponseBtn) {
            String xmlPath = Funcs.normalizeInput(xmlPathTF.getText());
            if (xmlPath.equals("")) {
                statusLabel.setText("Статус обработки: Введите путь для записи XML!");
            } else {
                String serviceName = (String) existingServicesList.getSelectedValue();
                try {
                    byte[] xmlFileInBytes = Kernel.instance().generateXML(serviceName, 1);
                    FileOutputStream fos = new FileOutputStream(serviceName + "_Response.xml");
                    fos.write(xmlFileInBytes);
                    fos.close();
                } catch (KrnException exception) {
                    exception.printStackTrace();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        }
    }
}
