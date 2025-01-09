package kz.tamur.admin;

import static kz.tamur.comps.Utils.getCenterLocationPoint;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.Document;
import javax.swing.text.Utilities;

import com.cifs.or2.client.ClassNode;
import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnMethod;
import com.cifs.or2.kernel.KrnObject;

import kz.tamur.Or3Frame;
import kz.tamur.comps.Utils;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.DesignerFrame;
import kz.tamur.guidesigner.EmptyFrame;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.guidesigner.filters.InputField;
import kz.tamur.or3.client.props.Expression;
import kz.tamur.or3.client.props.inspector.ExprEditorDelegate;
import kz.tamur.rt.orlang.ClientOrLang;
import kz.tamur.util.DateField;
import kz.tamur.util.ExpressionEditor;
import kz.tamur.util.UIDChooser;

public class MethodSplitPane extends  JSplitPane{
	JScrollPane resultMethodPanel = new JScrollPane();
    JTextPane resultMethodText = new JTextPane();
    JPanel inputMethodPanel = new JPanel();
    GridBagConstraints gbc = new GridBagConstraints();
    HashMap<String,Object> mapValues = new HashMap<String,Object>();
    List<String> paramNames = new ArrayList<String>();
    private ExecutorService executor;
    private Icon cancelIcon = kz.tamur.rt.Utils.getImageIconExt("cancel",".png");
    private Icon UIDIcon = kz.tamur.rt.Utils.getImageIconExt("UID1",".gif");
   public void init() {
	   setOrientation(JSplitPane.VERTICAL_SPLIT);
	   setMinimumSize(new Dimension(120, 600));
	   setPreferredSize(new Dimension(300, 600));
	   setResizeWeight(0.4);
	   resultMethodText.setEditable(false);
       resultMethodPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
       resultMethodPanel.getViewport().add(resultMethodText, null);
       setTopComponent(resultMethodPanel);
       setBottomComponent(inputMethodPanel);
    	
    }
   public void clear() {
       paramNames.clear();
       inputMethodPanel.removeAll();
       inputMethodPanel.revalidate();
       inputMethodPanel.repaint();
       resultMethodText.setText("");
   }

    public void populateInputPanel(final KrnMethod method) {
    	clear();
        final GridBagLayout layout = new GridBagLayout();
        inputMethodPanel.setLayout(layout);
        gbc.fill = GridBagConstraints.HORIZONTAL;
		try {
	        String expr = Kernel.instance().getMethodExpression(method.uid);
	        int i=0;
	        int j=0;
	        while(true) {
	        	if((j=expr.indexOf("$ARGS["+i+"]"))>0) {
	        		int endIndex=expr.substring(0,j).lastIndexOf("=");
	        		int beginIndex=expr.substring(0,endIndex).lastIndexOf("$");
	        		String paramName=expr.substring(beginIndex+1,endIndex).trim();
		            paramNames.add(paramName);
	        			i++;
	        	}else 
	        		break;
	        }
	        mapValues.clear();
	        int y;
	        for(y=0;y<i;y++)
	        {
	            createInputRow(y);
	        }
	        JButton prodolzhit = new JButton("Выполнить");
	        gbc.gridx = 2;
	        gbc.gridy = y;
	        inputMethodPanel.add(prodolzhit,gbc);
	        prodolzhit.addActionListener(new ActionListener() {//все значения введеные юзером передать в метод
	            public void actionPerformed(ActionEvent e)
	            { 
	                changeResultsPanel(method);
	                return;
	            }
	        }); 
		} catch (KrnException e1) {
			e1.printStackTrace();
		}
    }
	private void createInputRow(int y) {
		final String paramName=paramNames.get(y);
		JLabel label = new JLabel(paramName);
		gbc.gridx = 0;
		gbc.gridy = y;
		inputMethodPanel.add(label, gbc);
		gbc.gridx = 2;
		gbc.gridy = y;
		final JTextField field = new JTextField(15);
	    final InputField inputField = new InputField(field);
		JButton objBtn = ButtonsFactory.createToolButton("UID1", "Выбор объекта");
	    JButton exprBtn = ButtonsFactory.createToolButton("fx", "Формула");
	    final JCheckBox isDigit = new JCheckBox();
	    isDigit.setToolTipText("Числовое/строковое значение");
	    JButton clearBtn = ButtonsFactory.createToolButton("cancel.png", "Очистить параметр");
		field.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(isDigit.isSelected())
					try {
						mapValues.put(paramName, Long.parseLong(field.getText()));
					}catch(NumberFormatException nfe) {
						isDigit.setSelected(false);
						System.out.println(nfe.getMessage());
					}
				else
					mapValues.put(paramName, field.getText());
			}
		});
	    field.addFocusListener(new FocusListener() {
	        public void focusGained(FocusEvent e) {
	        }

	        public void focusLost(FocusEvent e) {
	        	if(field.isEditable()) {
					if(isDigit.isSelected())
						try {
							mapValues.put(paramName, Long.parseLong(field.getText()));
						}catch(NumberFormatException nfe) {
							isDigit.setSelected(false);
							System.out.println(nfe.getMessage());
						}
					else
						mapValues.put(paramName, field.getText());
	        	}
	        }

	      });
		inputMethodPanel.add(field, gbc);
		gbc.gridx = 3;
		gbc.gridy = y;
		objBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
	                String path = DesignerFrame.path_expr;
	                ClassNode cnode = getClassNode(path);
	                ClassBrowser cb = new ClassBrowser(cnode, true);
	                if (path != null && !"".equals(path))
	                    cb.setSelectedPath(path);
	                DesignerDialog dialog;
	                if (getTopLevelAncestor() instanceof JFrame) {
	                	dialog = new DesignerDialog((Frame) getTopLevelAncestor(), "Выберите путь", cb);
	                } else {
	                	dialog = new DesignerDialog((Dialog) getTopLevelAncestor(), "Выберите путь", cb);
	                }
	                dialog.setSize(new Dimension(800, 600));
	                dialog.setLocation(getCenterLocationPoint(800, 600));
	                dialog.show();
	                int res = dialog.getResult();
	                if (res != ButtonsFactory.BUTTON_NOACTION && res == ButtonsFactory.BUTTON_OK) {
	                    String spath = cb.getSelectedPath();
	                    if (spath.length() > 0) {
	                        DesignerFrame.path_expr = spath;
	                    }
	                    UIDChooser uidList = new UIDChooser(spath, true);
	                    if (getTopLevelAncestor() instanceof JFrame) {
	                    	dialog = new DesignerDialog((Frame) getTopLevelAncestor(), "Выберите значение", uidList);
	                    } else {
	                    	dialog = new DesignerDialog((Dialog) getTopLevelAncestor(), "Выберите значение", uidList);
	                    }
	                	dialog.setOkEnabled(false);
	                    dialog.show();
	                    dialog.setSize(new Dimension(700, 500));
	                    dialog.setLocation(getCenterLocationPoint(700, 500));
	                    res = dialog.getResult();
	                    if (res != ButtonsFactory.BUTTON_NOACTION && res == ButtonsFactory.BUTTON_OK) {
	                    	if (uidList.getValuesList().getSelectedValue() != null) {
	                    		String uid=uidList.getStringUID();
	                    		field.setText(uidList.getSelectedUID()[0]);
	                    	    field.setToolTipText(uidList.getSelectedUID()[0]);
	                    		KrnObject obj=Kernel.instance().getObjectByUid(uid, 0);
	                    		mapValues.put(paramName, obj);
	                    		field.setEditable(false);
	                    	}
	                    }
	                }
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});

        inputMethodPanel.add(objBtn, gbc);
		gbc.gridx = 4;
		gbc.gridy = y;
	    exprBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
	               ExpressionEditor exprEditor = new ExpressionEditor(inputField.value != null ? inputField.value.text : "", this);
	               exprEditor.setServiceFrm(Or3Frame.instance().getServiceFrame());
	               
	               DesignerDialog dlg = new DesignerDialog(Or3Frame.instance(), "Выражение", exprEditor);
	                dlg.setSize(new Dimension(800, 600));
	               dlg.setLocation(Utils.getCenterLocationPoint(dlg.getSize()));
	               dlg.show();
	               if (dlg.isOK()) {
	            	   inputField.value= new Expression(exprEditor.getExpression());
		    			ClientOrLang lng = new ClientOrLang(null);
		    			Map vars=new HashMap();
		    			try {
		    				lng.evaluate(exprEditor.getExpression(), vars, null, true, new Stack<String>(),-1);
		    			}catch(Exception ex) {
		    				ex.printStackTrace();
		    			}
		    			Object obj=vars.get("RETURN");
		    			if(obj!=null) {
	                		mapValues.put(paramName, obj);
	                		field.setEditable(false);
	                		field.setText("Формула");
	                		field.setToolTipText(exprEditor.getExpression());
	                	}
	               }
			}
		});
		inputMethodPanel.add(exprBtn, gbc);
		gbc.gridx = 5;
		gbc.gridy = y;
	    isDigit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
					if(field.isEditable() && isDigit.isSelected()) {
						try {
							mapValues.put(paramName, Long.parseLong(field.getText()));
						}catch(NumberFormatException nfe) {
							isDigit.setSelected(false);
							System.out.println(nfe.getMessage());
						}
					}else if(field.isEditable()) {
						mapValues.put(paramName, field.getText());
					}
				
			}
		});
		inputMethodPanel.add(isDigit, gbc);
		gbc.gridx = 6;
		gbc.gridy = y;
	    clearBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
                		mapValues.remove(paramName);
                		field.setText(null);
                		field.setToolTipText(null);
                		field.setEditable(true);
			}
		});
		inputMethodPanel.add(clearBtn, gbc);
	}
   private void changeResultsPanel(KrnMethod method) {
        Kernel krn  = Kernel.instance();
        List<Object> args=new ArrayList<>();
        try {
        	KrnClass cls_m=krn.getClassById(method.classId);
        	for(String paramName: paramNames){
                Object paramValue = mapValues.get(paramName);
                args.add(paramValue);
            }
        	Object res=krn.executeMethod(cls_m, cls_m, method.name, args, 0);
        	resultMethodText.setText("Метод:"+method.name
        			+"\n"+"Параметры:"+args
        			+"\n"+"Результат выполнения:"
        			+"\n"+(res!=null?res.toString():""+res));
		} catch (KrnException e) {
			resultMethodText.setText(e.getMessage() +"\n"+"Метод:"+method.name+"\n"+"Параметры:"+args);
		}
    }
   private class InputField {

	    JTextField field = new JTextField();
	    String paramName;
	    Expression value;
	    public  InputField(JTextField field)
	    {
	        this.field = field;
	    }
	    void setText(String text){
	    	field.setText(text);
	    	}
	    String getText()
	    {
	        return field.getText();
	    }
	    public int getDateFormat()
	    {
	        if(field instanceof DateField)
	        { 
	           return ((DateField)field).getDateFormat();
	        }
	        return -1;
	    }
   }
   private ClassNode getClassNode(String path) {
       ClassNode cls = null;
       final Kernel krn = Kernel.instance();
       String s = "";
       try {
           if ("".equals(path)) {
               cls = krn.getClassNodeByName("Объект");
           } else {
               try {
                   s = getClassNameFromPath(path);
                   cls = krn.getClassNodeByName(s);
               } catch (KrnException e) {
                   MessagesFactory.showMessageDialog(Or3Frame.instance(),
                           MessagesFactory.ERROR_MESSAGE, "\"" + s +
                           "\" - ошибочное имя класса!");
               }
           }
       } catch (KrnException e) {
           e.printStackTrace();
       }

       return cls;
   }

   private String getClassNameFromPath(String path) {
       StringTokenizer st = new StringTokenizer(path.toString(), ".");
       String s = st.nextToken();
       return s;
   }
}
