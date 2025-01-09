package kz.tamur.admin;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.rt.MainFrame;
import kz.tamur.rt.Utils;
import kz.tamur.util.Funcs;

import org.apache.commons.io.FileUtils;

import com.cifs.or2.client.Kernel;

public class DownloadFilePanel extends JPanel implements ActionListener {
	
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
    private JLabel sourceLabel = Utils.createLabel("Путь исходного файла");
    private JLabel destinationLabel = Utils.createLabel("Путь файла назначения");
    private JTextField sourceTF = Utils.createDesignerTextField();
    private JTextField destinationTF = Utils.createDesignerTextField();
    private JButton downloadFileBtn = ButtonsFactory.createToolButton("DownloadFile.png", "Скачать файл");
    private JLabel statusLabel = Utils.createLabel();

	public DownloadFilePanel() {
        super(new GridBagLayout());
        Utils.setAllSize(this, new Dimension(400, 65));
        setOpaque(isOpaque);
        statusLabel.setOpaque(isOpaque);
        sourceTF.setToolTipText("Путь к файлу на сервере");
        destinationTF.setToolTipText("Путь к загруженному файлу");
        
        add(sourceLabel, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2, 2, 2, 0), 0, 0));
        add(sourceTF, new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        add(destinationLabel, new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2, 2, 2, 0), 0, 0));
        add(destinationTF, new GridBagConstraints(1, 1, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));

        add(downloadFileBtn, new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        add(statusLabel, new GridBagConstraints(1, 2, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        downloadFileBtn.addActionListener(this);
		check();
	}
	
	private boolean check() {
		if (sourceTF.getText().trim().length() == 0 || destinationTF.getText().trim().length() == 0) {
			return false;
		} else {
			return true;
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		if (src == downloadFileBtn) {
			if (!check()) {
				statusLabel.setText("Поля не заполнены!");
				return;
			}
			String source = sourceTF.getText();
			String destination = Funcs.validate(Funcs.normalizeInput(destinationTF.getText()));
			try {
				List<Object> result = Kernel.instance().downloadFile(source);
				int status = (Integer) result.get(0);
				if (status == 1) {
					byte[] bytes = (byte[]) result.get(1);
					
					if (destination.matches(".+")) {
						File destinationFile = Funcs.getCanonicalFile(destination);
						if (!destinationFile.exists()) {
							destinationFile.createNewFile();
						}
						FileUtils.writeByteArrayToFile(destinationFile, bytes);
						statusLabel.setText("Файл успешно загружен!");
					}
				} else if (status == 2) {
					statusLabel.setText("Указанный файл не найден!");
				} else {
					statusLabel.setText("Ошибка при загрузке файла!");
				}
			} catch(Exception exception) {
				statusLabel.setText("Ошибка при загрузке файла!");
				exception.printStackTrace();
			}
		}
	}
}