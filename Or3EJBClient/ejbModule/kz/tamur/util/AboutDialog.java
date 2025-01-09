package kz.tamur.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.jar.*;

import kz.tamur.rt.Utils;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.KrnException;

/**
 * Created by IntelliJ IDEA.
 * User: Администратор
 * Date: 01.03.2007
 * Time: 16:52:50
 * To change this template use File | Settings | File Templates.
 */
public class AboutDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private ImageIcon icon;
	private long modeId;
    private long versionId;
    private long dbaseId;
    private String versionIdBaseInfoString = "Версия базы данных платформы OR3: ";
    private JLabel versionIdBaseInfoLabel = Utils.createLabel(versionIdBaseInfoString);
    private String dbaseIdBaseInfoString = "ID базы данных: ";
    private JLabel dbaseIdBaseInfoLabel = Utils.createLabel(dbaseIdBaseInfoString);
    private String modeIdBaseInfoString = "Режим редактирования конструкторов: ";
    private JLabel modeIdBaseInfoLabel = Utils.createLabel(modeIdBaseInfoString);
	
    public AboutDialog(Frame owner, ImageIcon icon) throws HeadlessException {
        super(owner);
        this.icon = icon;
        setUndecorated(true);
        init();
    }

    private void init() {
        JPanel p = new JPanel(new BorderLayout());
        JLabel lab = new JLabel();
        if (icon != null) {
            lab = new JLabel(icon);
        }
        lab.setLayout(new GridBagLayout());
        final JEditorPane text = new JEditorPane();
        text.setOpaque(false);
/*
        try {
            if (Utils.getAboutURL() != null)
                text.setPage(Utils.getAboutURL());
        } catch (IOException e) {
            e.printStackTrace();
        }
*/
        text.setPreferredSize(new Dimension(200, 20));
        text.setEditable(false);
        text.setText("www.tamur.kz");
/*
        //@todo Переделать
        text.addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    Runtime runtime = Runtime.getRuntime();
                    try {
                        //Process process =
                        runtime.exec("C:/Program Files/Internet Explorer/iexplore.exe " +
                                e.getURL().toString());
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
*/
/*
        lab.add(new JLabel(" 123"), new GridBagConstraints(0, 0, 2, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(2, 2, 45, 2), 0, 0));
        lab.add(text, new GridBagConstraints(1, 1, 1, 2, 0, 1,
                GridBagConstraints.SOUTH, GridBagConstraints.VERTICAL,
                new Insets(15, 2, 2, 2), 0, 0));
                */
        String buildNumber = "0";
        long replicationNumber = 0;

        KrnObject[] objs = null;
        Kernel krn = Kernel.instance();
        try {
	        objs = krn.getClassObjects(
	                krn.getClassByName("Import"),new long[0], 0);
		} catch (KrnException e) {
			e.printStackTrace();
		}
        KrnObject last = null;
        KrnObject priorExp = null;
        if (objs.length > 0) {
            last = objs[0];
            for (int i = 0; i < objs.length; i++) {
                if (objs[i].id > last.id) {
                    last = objs[i];
                }
            }
        }
        if (last != null) {
        	try {
	        	replicationNumber = (long) krn.getLongsSingular(
	            	last, krn.getAttributeByName(krn.getClassByName("Import"), "exp_id"), true);
        	} catch (KrnException e) {
        		e.printStackTrace();
        	}
        }
         
        try {
        	String pathToThisClass = getClass().getResource("/kz/tamur/util/AboutDialog.class").toString();
        	int pos = pathToThisClass.lastIndexOf("!");
        	if (pos != -1) {
        		String manifestPath = pathToThisClass.substring(0, pos + 1) + "/META-INF/MANIFEST.MF";
        		Manifest m = new Manifest(new java.net.URL(manifestPath).openStream());
        		System.out.println(manifestPath);
        		m.write(System.out);
        		Map<String, Attributes> entries = m.getEntries();
        		for (String entryName : entries.keySet()) {
        			System.out.println("Entry: " + entryName);
        			Attributes attrs = entries.get(entryName);
        			Set<Entry<Object, Object>> aentries = attrs.entrySet();
        			for (Entry<Object, Object> e : aentries) {
            			System.out.println("Attribute: " + e);
        			}
        		}
    	        Attributes attrs = m.getMainAttributes();
    	        String version = attrs.getValue("Implementation-Version");
    	        
    	        if (version != null) {
    	        	buildNumber = version.replace("b", "").replace("-", ".");
    	        }
    	        else
    	        	buildNumber = "no version in or3.jar";
        	}
        } catch(IOException e) {
        	e.printStackTrace();
        }
        
        JPanel pp = new JPanel();
        JPanel pp2 = new JPanel(new GridBagLayout());
        pp.add(new JLabel("Версия " + buildNumber + "." + replicationNumber + "                       "), BorderLayout.EAST);
        try {
        	modeId = krn.getId("mode");
        	versionId = krn.getId("version");
        	dbaseId = krn.getId("dbase_id");
        } catch (KrnException e1) {
			e1.printStackTrace();
		}
        Font font = new Font("Dialog", Font.BOLD, 11);
        versionIdBaseInfoLabel.setText(versionIdBaseInfoString + versionId);
        versionIdBaseInfoLabel.setFont(font);
        versionIdBaseInfoLabel.setForeground(Color.black);
        dbaseIdBaseInfoLabel.setText(dbaseIdBaseInfoString + dbaseId);
        dbaseIdBaseInfoLabel.setFont(font);
        dbaseIdBaseInfoLabel.setForeground(Color.black);
	    modeIdBaseInfoLabel.setText(modeIdBaseInfoString + ((modeId == 1) ? "Вкл" : "Выкл"));
	    modeIdBaseInfoLabel.setFont(font);
	    modeIdBaseInfoLabel.setForeground(Color.black);
        pp2.add(versionIdBaseInfoLabel, new GridBagConstraints(0, 0, 1, 1, 0, 1,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(40, 0, 0, 0), 0, 0));
        pp2.add(dbaseIdBaseInfoLabel, new GridBagConstraints(0, 1, 1, 1, 0, 1,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 10));
        pp2.add(modeIdBaseInfoLabel, new GridBagConstraints(0, 2, 1, 1, 0, 1,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));
        pp.add(text, BorderLayout.WEST);
        pp.setOpaque(false);
        pp2.setOpaque(false);
        lab.add(pp, new GridBagConstraints(1, 1, 1, 2, 0, 1,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL,
                new Insets(0, 2, 2, 2), 0, 1));
        lab.add(pp2, new GridBagConstraints(1, 3, 1, 3, 0, 1,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));
        p.add(lab, BorderLayout.CENTER);

        //p.add(lab, BorderLayout.CENTER);
        //p.add(text, BorderLayout.SOUTH);
        
        setContentPane(p);
        pack();
        addWindowFocusListener(new WindowFocusAdapter(this));
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    dispose();
                }
            }
        });
        text.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    dispose();
                }
            }
        });
    }

    class WindowFocusAdapter implements WindowFocusListener {
        private JDialog wnd;

        public WindowFocusAdapter(JDialog wnd) {
            this.wnd = wnd;
        }

        public void windowGainedFocus(WindowEvent e) {
        }

        public void windowLostFocus(WindowEvent e) {
            if (wnd != null && wnd.isShowing())
                wnd.dispose();
            wnd = null;
        }
    }
}