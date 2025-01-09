package kz.tamur.guidesigner;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import kz.tamur.comps.Constants;
import kz.tamur.comps.EmptyPlace;
import kz.tamur.comps.OrAccordion;
import kz.tamur.comps.OrAnalyticPanel;
import kz.tamur.comps.OrBarcode;
import kz.tamur.comps.OrButton;
import kz.tamur.comps.OrCheckBox;
import kz.tamur.comps.OrCheckColumn;
import kz.tamur.comps.OrCollapsiblePanel;
import kz.tamur.comps.OrColumnComponent;
import kz.tamur.comps.OrComboBox;
import kz.tamur.comps.OrComboColumn;
import kz.tamur.comps.OrDateColumn;
import kz.tamur.comps.OrDateField;
import kz.tamur.comps.OrDocField;
import kz.tamur.comps.OrDocFieldColumn;
import kz.tamur.comps.OrFloatColumn;
import kz.tamur.comps.OrFloatField;
import kz.tamur.comps.OrGISPanel;
import kz.tamur.comps.OrGuiComponent;
import kz.tamur.comps.OrGuiContainer;
import kz.tamur.comps.OrHyperLabel;
import kz.tamur.comps.OrHyperPopup;
import kz.tamur.comps.OrImage;
import kz.tamur.comps.OrImagePanel;
import kz.tamur.comps.OrIntColumn;
import kz.tamur.comps.OrIntField;
import kz.tamur.comps.OrLabel;
import kz.tamur.comps.OrLayoutPane;
import kz.tamur.comps.OrMemoColumn;
import kz.tamur.comps.OrMemoField;
import kz.tamur.comps.OrRichTextEditor;
import kz.tamur.comps.OrNote;
import kz.tamur.comps.OrPanel;
import kz.tamur.comps.OrPasswordField;
import kz.tamur.comps.OrPopUpPanel;
import kz.tamur.comps.OrPopupColumn;
import kz.tamur.comps.OrRadioBox;
import kz.tamur.comps.OrScrollPane;
import kz.tamur.comps.OrSplitPane;
import kz.tamur.comps.OrTabbedPane;
import kz.tamur.comps.OrTable;
import kz.tamur.comps.OrTableColumn;
import kz.tamur.comps.OrTextColumn;
import kz.tamur.comps.OrTextField;
import kz.tamur.comps.OrTreeControl2;
import kz.tamur.comps.OrTreeCtrl;
import kz.tamur.comps.OrTreeField;
import kz.tamur.comps.OrTreeTable;
import kz.tamur.comps.OrTreeTable2;
import kz.tamur.comps.PropertyValue;
import kz.tamur.comps.Spacer;
import kz.tamur.comps.models.BarcodePropertyRoot;
import kz.tamur.comps.models.ImagePropertyRoot;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.rt.Utils;
import kz.tamur.util.Funcs;
import kz.tamur.util.MapMap;
import kz.tamur.util.Pair;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnObject;

public class OrHtmlGenerator {
    private OrPanel orPanel;
    private final static String EOL = Constants.EOL;
    private Document messagesXml;
    private static Pattern pattern = Pattern.compile("^-?\\d+$");
    private KrnObject uiObj = null;
    
    private List<String> allowedExtensions = new ArrayList<String>(Arrays.asList("doc", "docx", "xls", "xlsx", "ppt", "pptx", "html", "xml", "txt", "pdf", "png", "jpg", "gif", "zip"));

    private final static boolean canHideCalendar = "true".equals(System.getProperty("canHideCalendar"));
    
    private final static boolean isRnDB = "true".equals(System.getProperty("isRnDB")) || "true".equals(System.getProperty("isEGKNDB"));

    public OrHtmlGenerator(OrPanel panel, KrnObject uiObj) {
        orPanel = panel;
        this.uiObj = uiObj;
    }

    private String parseTitle(String title) {
        return title == null ? null : title.replaceAll("@", "<br/>");
    }

    /**
     * Генерация html Документа для отдельного интерфейса в OR3
     */
    public StringBuilder generateHtml(Document messagesXml) {
        this.messagesXml = messagesXml;
        StringBuilder htmlString = new StringBuilder();
        toHtml(orPanel, htmlString, true);
        htmlString.append("<input type='file' style='display:none;' id='upload'/>");
        this.messagesXml = null;
        return htmlString;
    }

    public void toHtml(OrPanel pan, StringBuilder sb) {
    	toHtml(pan, sb, false);
    }
    
    /**
     * Формирование html для OrPanel
     */
    public void toHtml(OrPanel pan, StringBuilder sb, boolean mainPanel) {
        SoftReference<List<SubComponent>> subElements = new SoftReference<List<SubComponent>>( new ArrayList<SubComponent>());
        GridBagConstraints bgc;
        int width = 0;
        int height = 0;
        boolean isCellContains = false; // содержит ли ячейка компонент
        boolean isTabPanel = false;
        String tabUID = "";
        if (pan.getParent() instanceof OrTabbedPane) {
            isTabPanel = true;
            tabUID = ((OrTabbedPane) pan.getParent()).getUUID();
        }
        boolean fillX = false;
        boolean fillY = false;
        if (pan.getConstraints().fill == GridBagConstraints.HORIZONTAL) {
            fillX = true;
        } else if (pan.getConstraints().fill == GridBagConstraints.VERTICAL) {
        	fillY = true;
        } else if (pan.getConstraints().fill == GridBagConstraints.BOTH) {
        	fillX = fillY = true;
        }
        
        boolean isFieldset = false;
        int count = pan.getComponentCount();
        pan.isWebWisible=false;//панель видима если хоть однин копонент на ней останется
        for (int k = 0; k < count; k++) {
            if (pan.getComponent(k) instanceof OrGuiComponent) {
                bgc = ((OrGuiComponent) pan.getComponent(k)).getConstraints();
                width = Math.max(width, bgc.gridx);
                height = Math.max(height, bgc.gridy);
                subElements.get().add(new SubComponent((OrGuiComponent) pan.getComponent(k), bgc.gridx, bgc.gridy, bgc.weightx, bgc.weighty, bgc.gridwidth, bgc.gridheight));
                if(!((OrGuiComponent) pan.getComponent(k)).isShowOnTopPan())
                    pan.isWebWisible=true;
            }
        }

        Border border = pan.getBorder();

        if (border != null) {

            sb.append("<fieldset");
            StringBuilder temp = new StringBuilder();
            if (!fillX) {
                temp.append("display:inline-block;");
            }
            addConstraints(pan, temp);
            if (temp.length() > 0) {
                sb.append(" style=\"").append(temp).append("\"");
            }

            sb.append(" id='").append(pan.getUUID()).append("'>");
            isFieldset = true;
            String textUID = pan.getBorderTitleUID();
            String borderTitle = getMessagesByUID(textUID);
            if (borderTitle != null && !borderTitle.isEmpty()) {
                Border bType = pan.getBorderType();
                int pos = Constants.CENTER_ALIGNMENT;
                String align = "center";
                StringBuilder style = new StringBuilder();

                if (bType instanceof TitledBorder) {
                    pos = ((TitledBorder) bType).getTitleJustification();
                    if (pos == TitledBorder.LEFT) {
                        align = "left";
                    } else if (pos == TitledBorder.RIGHT)
                        align = "right";
                    Utils.getCSS(((TitledBorder) bType).getTitleFont(), style);
                    Utils.getCSS(((TitledBorder) bType).getTitleColor(),style);
                }
                sb.append("<legend class='legend_").append(pos).append("'");
				if (style.length() > 0) {
					sb.append(" style=\"").append(style).append("\"");
				}
				sb.append(" align='").append(align).append("'>").append(borderTitle).append("</legend>");
            }
            sb.append("<div class='fielsetContent'>");
        }

        String textUID = pan.getXml().getChildText("title");
    	String text = getMessagesByUID(textUID);
    	if (text.isEmpty()) {
    	    text = pan.getTitle(); // потом убрать
    	}
    	text = parseTitle(text);

    	if (subElements.get().size() > 1) {
        	sb.append("<div");
			StringBuilder temp = new StringBuilder();
			addConstraints(pan, temp);
			if (!(pan.getParent() instanceof OrLayoutPane)) {
				if(pan.isWebWisible)
					addPrefSizeComp(pan, temp, true, true);
				else{
					//если панель невидима то устанавливаем ее высоту равную нулю
        			addPrefSizeCell(pan, temp, true, false, true);
        			temp.append("height:0px;");
				}
			}
			else {
                temp.append("width:100%;height:100%;");
			}

			if (temp.length() > 0) {
				sb.append(" style=\"").append(temp).append("\"");
			}
        	sb.append(">");
            if (isTabPanel) {
                sb.append("<table class=\"ortable orpanel tabContent\" tabid=\"").append(tabUID).append("\"");
            } else {
                if (mainPanel) {
                    sb.append("<table class=\"ortable orpanel mainPanel\"");
                    sb.append(" data-uid=\"").append(this.uiObj.getUID()).append("\"");
                	sb.append(" data-uiTitle=\"").append(text).append("\"");
                	sb.append(" uiTitleAlign=\"").append(orPanel.getTitleAlign()).append("\"");
                } else {
                    sb.append("<table class=\"ortable orpanel\"");
                }
            }

			temp = new StringBuilder();

        	if (fillX)
                temp.append("width:100%;");
            if (fillY)
                temp.append("height:100%;");
            addPosition(pan, sb, temp);
            addHPositionInTab(pan, temp);

            if (pan.getWebNameBg() != null) {
                temp.append("background:url('../images/foto/").append(pan.getWebNameBg()).append("') no-repeat scroll");
                temp.append(pan.getPositionPict() == GridBagConstraints.CENTER ? " center" : " 0 0"); // по центру или левый верхний угол
                temp.append(" transparent;background-size:contain;");
            }
        	if (pan.getBackground() != null && !pan.getBackground().equals(UIManager.getColor("Panel.background")))
                temp.append("background-color:").append(Utils.colorToString(pan.getBackground())).append(";");
			if (temp.length() > 0) {
				sb.append(" style=\"").append(temp).append("\"");
			}

			if (!isFieldset)
            	sb.append(" id='").append(pan.getUUID()).append("'");
            sb.append(">").append(EOL);
            MapMap<Integer, Integer, Boolean> exclude = new MapMap<Integer, Integer, Boolean>();

            for (int i = 0; i <= height; i++) {
                sb.append("<tr>").append(EOL);
                for (int j = 0; j <= width; j++) {
                    isCellContains = false;
                    for (int k = 0; k < subElements.get().size(); k++) {
                        if (subElements.get().get(k).posX == j && subElements.get().get(k).posY == i) {

                            sb.append("<td");

                            if (subElements.get().get(k).gridWidth > 1) {
                                sb.append(" colspan='").append(subElements.get().get(k).gridWidth).append("'");
                                j += subElements.get().get(k).gridWidth - 1;
                            }
                            if (subElements.get().get(k).gridHeight > 1) {
                                sb.append(" rowspan='").append(subElements.get().get(k).gridHeight).append("'");

                                for (int m = 1; m < subElements.get().get(k).gridHeight; m++) {
                                    for (int l = 0; l < subElements.get().get(k).gridWidth; l++) {
                                        exclude.put(i + m, j + l, true);
                                    }
                                }
                            }

/*
 * 							Padding пока не используется - используется margin в самом компоненте
 * 
 *                          StringBuilder temp = new StringBuilder();
                            Insets ins = subElements.get().get(k).component.getConstraints().insets;
                            if (ins != null) {
                                temp.append(" padding: ").append(ins.top).append("px ");
                                temp.append(ins.right).append("px ");
                                temp.append(ins.bottom).append("px ");
                                temp.append(ins.left).append("px;");
                            }
*/
                            StringBuilder sub = new StringBuilder();
                            toHtml(subElements.get().get(k).component, sub);

                			temp = new StringBuilder();
                            addPosition(subElements.get().get(k).component, sb, temp);
                            //Если сам компонент или все его компоненты переносятся на тулбар то
                            // место которое он занимает не должно отображаться на основной панели,
                            //для этого его высоту ставим равной нулю  
                            OrGuiComponent com = subElements.get().get(k).component;
                            if((com instanceof OrPanel	&& !((OrPanel)com).isWebWisible) || com.isShowOnTopPan()){
                    			addPrefSizeCell(com, temp, true, false, true);
                    			temp.append("height:0px;");
                            } else if (com instanceof OrDateField) {
                            	addPrefSizeCell(com, temp, true, true, false);
                            	addTdConstraints(com, temp);
                            } else {
                            	addPrefSizeCell(com, temp, true, true, true);
                            }
                			
                            if (temp.length() > 0)
                            	sb.append(" style=\"").append(temp).append("\"");

                            sb.append(">");
                            if (sub.toString().length() > 0)
                                sb.append(sub.toString().trim());

                            sb.append("</td>");
                            isCellContains = true;
                            // }
                        }
                    }
                    if (isCellContains == false) {
                        Object obj = exclude.get(i, j);
                        if (obj == null) {
                            sb.append("<td></td>");
                        }
                    }
                }
                sb.append("</tr>").append(EOL);
            }
            sb.append("</table>").append(EOL);
            sb.append("</div>");
        } else if (subElements.get().size() == 1) {
            if (mainPanel) {
                sb.append("<div class='ortable orpanel mainPanel'");
                sb.append(" data-uid=\"").append(this.uiObj.getUID()).append("\"");
            	sb.append(" data-uiTitle=\"").append(text != null ? text : "").append("\"");
            	sb.append(" uiTitleAlign=\"").append(orPanel.getTitleAlign()).append("\"");
            } else {
                sb.append("<div class='ortable orpanel'");
            }
                
            OrGuiComponent comp = subElements.get().get(0).component;
            
            StringBuilder temp = new StringBuilder();
			if (!(pan.getParent() instanceof OrLayoutPane)) {
				addPrefSizeCell(comp, temp, true, true, true);
//				if(pan.isWebWisible)
//					addPrefSizeComp(pan, temp, true, true);
//				else{
//					//если панель невидима то устанавливаем ее высоту равную нулю
//        			addPrefSizeCell(comp, temp, true, false, true);
//        			temp.append("height:0px;");
//				}
			} else {
                temp.append("width:100%;height:100%;");
			}
			addPosition(pan, sb, temp);
			addConstraints(pan, temp);

			if (comp instanceof OrDateField)
            	addTdConstraints(comp, temp);

			if (pan.getBackground() != null && !pan.getBackground().equals(UIManager.getColor("Panel.background")))
                temp.append("background-color:").append(Utils.colorToString(pan.getBackground())).append(";");

            if (temp.length() > 0)
            	sb.append(" style=\"").append(temp).append("\"");
            
            sb.append(" id=\"").append(pan.getUUID()).append("\">");
            toHtml(comp, sb);
            sb.append("</div>");
        }
        if (border != null) {
            sb.append("</div></fieldset>");
        }
    }

    /**
     * Формирование html для OrGISPanel
     */
    public void toHtml(OrGISPanel orGISPanel, StringBuilder sb) {
        sb.append("<div style=\"display:block;");
        StringBuilder temp = new StringBuilder();
		addConstraints((OrGuiComponent) orGISPanel, temp);
        boolean fillX = false;
        boolean fillY = false;
        if (orGISPanel.getConstraints().fill == GridBagConstraints.HORIZONTAL) {
        	fillX = true;
        } else if (orGISPanel.getConstraints().fill == GridBagConstraints.VERTICAL) {
        	fillY = true;
        } else if (orGISPanel.getConstraints().fill == GridBagConstraints.BOTH) {
        	fillX = fillY = true;
        }
        
        addPrefSize(orGISPanel, temp, fillX, fillY, true);
		addMinSize((OrGuiComponent) orGISPanel, temp);
		addMaxSize((OrGuiComponent) orGISPanel, temp);

        sb.append(temp.toString() + "\">");
                
        sb.append("<div class=\"or3-gis-map\" id=\"").append(orGISPanel.getUUID()).append("\" style=\"" + temp.toString() + "\"></div>");
        sb.append("</div>");
    }
    
    public void toHtml(OrAnalyticPanel orAnalyticPanel, StringBuilder sb) {
    	String uuid = orAnalyticPanel.getUUID();
    	sb.append("<div style=\"display:block;");
        StringBuilder temp = new StringBuilder();
		addConstraints((OrGuiComponent) orAnalyticPanel, temp);
        boolean fillX = false;
        boolean fillY = false;
        if (orAnalyticPanel.getConstraints().fill == GridBagConstraints.HORIZONTAL) {
        	fillX = true;
        } else if (orAnalyticPanel.getConstraints().fill == GridBagConstraints.VERTICAL) {
        	fillY = true;
        } else if (orAnalyticPanel.getConstraints().fill == GridBagConstraints.BOTH) {
        	fillX = fillY = true;
        }
        
        addPrefSize(orAnalyticPanel, temp, fillX, fillY, true);
		addMinSize((OrGuiComponent) orAnalyticPanel, temp);
		addMaxSize((OrGuiComponent) orAnalyticPanel, temp);

        sb.append(temp.toString()).append("\">");
        
        sb.append("<table class='full-size'><tr><td width='2%'>");
        	sb.append("<div class='full-height' class='accordion-v' id='acc_").append(uuid).append("' multi='0'>");
        		sb.append("<table class='analytic-acc'><tbody class='full-height'><tr>");
        			sb.append("<td class='bcg-coll-pan analytic-coll-pan'><div>");
        				sb.append("<span class='ttl-coll-pan-v arr-parent' id='t0acc_").append(uuid).append("'>");
        					sb.append("<span class='acc-arrow arrow-right'></span><span class='acc-arrow arrow-right'></span>");
        			sb.append("</span></div></td><td class='full-height'>");
        				sb.append("<div class='accord cnt-coll-pan-v analytic-cnt-coll-pan' id='cnt0acc_").append(uuid).append("'>");
        					
        					sb.append("<div class='analytic-dimension-title'><p class='analytic-title-text'>ВЫБЕРИТЕ ТИП ДИАГРАММЫ</p><span id='hide_btn_");
        					sb.append(uuid).append("' class='arrow-width'><span class='acc-arrow arrow-left'></span><span class='acc-arrow arrow-left'></span></span></div><hr>");
        					sb.append("<input id='type_").append(uuid).append("' class='easyui-combobox inherit-width'><hr>");
        					sb.append("<div class='analytic-dimension'><p class='analytic-title-text'>ВЫБЕРИТЕ ФИЛЬТР</p></div>");
        					sb.append("<ul id='filter_").append(uuid).append("' class='easyui-combotree inherit-width' data-options='multiple:true'></ul><hr>");
        					
        					sb.append("<div class='analytic-dimension-title'><p class='analytic-title-text'>ВЫБЕРИТЕ ИЗМЕРИТЕЛИ</p></div><hr>");
        					sb.append("<div class='dimension'><p class='dimension-text'>Измерители X</p><div>")
        						.append("<button title='Снять все' id='clear_dim_x_").append(uuid).append("'><div class='icon-clearAll'></div></button>")
        						.append("<button title='Выбрать корневые' id='select_dim_x_").append(uuid).append("'><div class='icon-selectAll'></div></button></div>")
        						.append("</div><input id='xaxes_").append(uuid).append("' class='easyui-combobox inherit-width'><hr>");
        					sb.append("<ul id='x_").append(uuid).append("' class='easyui-tree analytic-tree' data-options='multiple:true'></ul><hr>");
        					sb.append("<div class='dimension'><p class='dimension-text'>Измерители Y</p><div>")
        						.append("<button title='Снять все' id='clear_dim_y_").append(uuid).append("'><div class='icon-clearAll'></div></button>")
        						.append("<button title='Выбрать корневые' id='select_dim_y_").append(uuid).append("'><div class='icon-selectAll'></div></button></div>")
        						.append("</div><input id='yaxes_").append(uuid).append("' class='easyui-combobox inherit-width'><hr>");
        					sb.append("<ul id='y_").append(uuid).append("' class='easyui-tree analytic-tree' data-options='multiple:true'></ul><hr>");
        					sb.append("<a id='btn_").append(uuid).append("' class='easyui-linkbutton analytic-apply'></a><a id='tgl_leg_").append(uuid).append("' class='easyui-linkbutton analytic-apply show-hide'></a><hr>");
        				sb.append("</div>");
        			sb.append("</td>");
        		sb.append("</tr></tbody></table>");
        	sb.append("</div>");
        sb.append("</td><td>");
        	sb.append("<p id='axes_title_").append(uuid).append("' class='axes-title'></p>");
        	sb.append("<div class=\"or3-analytic\" id=\"").append(uuid).append("\" style=\"").append(temp.toString()).append("\"></div>");
        sb.append("</tr></table>");

        sb.append("</div>");
    }
    
    public void toHtml(OrImagePanel panel, StringBuilder sb) {
		if (panel.getUUID().equals(panel.getImageUID())) {
	        StringBuilder temp = new StringBuilder();
			addConstraints((OrGuiComponent) panel, temp);
	        boolean fillX = false;
	        boolean fillY = false;
	        if (panel.getConstraints().fill == GridBagConstraints.HORIZONTAL) {
	        	fillX = true;
	        } else if (panel.getConstraints().fill == GridBagConstraints.VERTICAL) {
	        	fillY = true;
	        } else if (panel.getConstraints().fill == GridBagConstraints.BOTH) {
	        	fillX = fillY = true;
	        }
	        
	        addPrefSize(panel, temp, fillX, fillY, true);
			addMinSize((OrGuiComponent) panel, temp);
			addMaxSize((OrGuiComponent) panel, temp);
			
	        sb.append("<div class=\"or3-img-panel").append(panel.getOrientation() == 2 ? "-vertical" : "").append("\" id=\"").append(panel.getUUID()).append("\" style=\"position:relative;" + temp.toString() + "\"></div>");
	        sb.append("</div>");
		} else {
	        sb.append("<div class=\"easyui-panel\" style=\"display:block;");
	        StringBuilder temp = new StringBuilder();
			addConstraints((OrGuiComponent) panel, temp);
	        boolean fillX = false;
	        boolean fillY = false;
	        if (panel.getConstraints().fill == GridBagConstraints.HORIZONTAL) {
	        	fillX = true;
	        } else if (panel.getConstraints().fill == GridBagConstraints.VERTICAL) {
	        	fillY = true;
	        } else if (panel.getConstraints().fill == GridBagConstraints.BOTH) {
	        	fillX = fillY = true;
	        }
	        
	        addPrefSize(panel, temp, fillX, fillY, true);
			addMinSize((OrGuiComponent) panel, temp);
			addMaxSize((OrGuiComponent) panel, temp);
			
	        sb.append(temp.toString() + "\" data-options=\"fit:true\">");
	                
	        sb.append("<div class=\"or3-img-panel").append(panel.getOrientation() == 2 ? "-vertical" : "").append("\" id=\"").append(panel.getUUID()).append("\" style=\"width:100%;height:100%;\"></div>");
	        sb.append("</div>");
		}
    }

    private void addConstraints(OrGuiComponent orComp, StringBuilder b) {
        GridBagConstraints constraints = orComp.getConstraints();
        if (constraints != null) {
        	if (Constants.IS_UL_PROJECT) {
                b.append("margin-left:").append(constraints.insets.left).append("px;");
        	} else {
	            if (constraints.insets.left > 1) {
	                b.append("margin-left:").append(constraints.insets.left).append("px;");
	            }
        	}
            if (constraints.insets.bottom > 1) {
                b.append("margin-bottom:").append(constraints.insets.bottom).append("px;");
            }
            if (constraints.insets.right > 0) {
                b.append("margin-right:").append(constraints.insets.right).append("px;");
            }
            if (constraints.insets.top > 0 || constraints.insets.top < 0) {
                b.append("margin-top:").append(constraints.insets.top).append("px;");
            }
        }
        constraints = null;
    }

    private void addTdConstraints(OrGuiComponent orComp, StringBuilder b) {
        GridBagConstraints constraints = orComp.getConstraints();
        if (constraints != null) {
            if (constraints.insets.left > 1) {
                b.append("padding-left:").append(constraints.insets.left).append("px;");
            }
            if (constraints.insets.bottom > 1) {
                b.append("padding-bottom:").append(constraints.insets.bottom).append("px;");
            }
            if (constraints.insets.right > 0) {
                b.append("padding-right:").append(constraints.insets.right).append("px;");
            }
            if (constraints.insets.top > 0 || constraints.insets.top < 0) {
                b.append("padding-top:").append(constraints.insets.top).append("px;");
            }
        }
        constraints = null;
    }

    private void addSize(OrGuiComponent orComp, StringBuilder b) {
        GridBagConstraints constraints = orComp.getConstraints();
        if (constraints != null) {
            if (constraints.weighty > 0) {
                b.append("height:").append(constraints.weighty).append("px;");
            }
            if (constraints.weightx > 0) {
                b.append("width:").append(constraints.weightx).append("px;");
            }
        }
        constraints = null;
    }

    private void addFontStyle(OrGuiComponent orComp, StringBuilder b) {
        PropertyNode pn = orComp.getProperties().getChild("view");
        if (pn != null) {
            PropertyValue pv = orComp.getPropertyValue(pn.getChild("font").getChild("fontG"));
            if (!pv.isNull()) {
                Utils.getCSS(pv.fontValue(),b);
            }
        }
        pn = null;
    }

    private void addMinSize(OrGuiComponent orComp, StringBuilder b) {
        Dimension minSize = orComp.getMinSize();
        if (minSize != null) {
            if (minSize.width > 0) {
                b.append("min-width:").append(minSize.width).append("px;");
            }
            if (minSize.height > 0) {
                b.append("min-height:").append(minSize.height).append("px;");
            }
        }
        minSize = null;
    }

    private void addMaxSize(OrGuiComponent orComp, StringBuilder b) {
        Dimension maxSize = orComp.getMaxSize();
        if (maxSize != null) {
            if (maxSize.width > 0) {
                b.append("max-width:").append(maxSize.width).append("px;");
            }
            if (maxSize.height > 0) {
                b.append("max-height:").append(maxSize.height).append("px;");
            }
        }
        maxSize = null;
    }

    private void addFill(OrGuiComponent c, StringBuilder b) {
        boolean fillX = false;
        boolean fillY = false;
        if (c.getConstraints().fill == GridBagConstraints.HORIZONTAL) {
            fillX = true;
        } else if (c.getConstraints().fill == GridBagConstraints.VERTICAL) {
        	fillY = true;
        } else if (c.getConstraints().fill == GridBagConstraints.BOTH) {
        	fillX = fillY = true;
        }

    	if (fillX)
            b.append("width:100%;");
        if (fillY)
            b.append("height:100%;");
    }

    private void addPrefSize(OrGuiComponent comp, StringBuilder b, boolean autowidth, boolean autoheight, boolean addHeight) {
        Dimension size = comp.getPrefSize();
        if (autowidth && (comp instanceof OrMemoField || comp instanceof OrRichTextEditor))
            b.append("width:99%;");
        else if (autowidth && comp instanceof OrGISPanel)
            b.append("width:98%;");
        else if (autowidth)
            b.append("width:100%;");
        else if (size != null && size.width > 0)
            b.append("width:").append(size.width).append("px;");

        if (addHeight) {
            if (autowidth && comp instanceof OrGISPanel)
                b.append("height:98%;");
            else if (autoheight)
	            b.append("height:100%;");
	        else if (addHeight && size != null && size.height > 0)
	            b.append("height:").append(size.height).append("px;");
        }
    }

    // Размеры ячейки, окружающей компонент
    // Если задан вес, то размеры в процентах,
    // если задана величина (высота, ширина), то размеры в пикселах с учетом отступов
    private void addPrefSizeCell(OrGuiComponent c, StringBuilder b, boolean addWidth, boolean addHeight, boolean addInsets) {
        GridBagConstraints cs = c.getConstraints();
        Dimension size = c.getPrefSize();
        
        if (addWidth) {
            double wx = c.getConstraints().weightx;
	        if (wx > 0 && (c instanceof OrMemoField || c instanceof OrRichTextEditor))
	            b.append("width:").append(wx*100 > 99 ? 99 : (int)(wx*100)).append("%;");
	        else if (wx > 0)
	            b.append("width:").append(wx*100 > 100 ? 100 : (int)(wx*100)).append("%;");
	        else if (size != null && size.width > 0) {
	            b.append("width:").append(size.width + ((addInsets) ? (cs.insets.left + cs.insets.right) : 0)).append("px;");
	        }
        }
        if (addHeight) {
            double wy = c.getConstraints().weighty;
	        if (wy > 0)
	            b.append("height:").append(wy*100 > 100 ? 100 : (int)(wy*100)).append("%;");
	        else if (size != null && size.height > 0)
	            b.append("height:").append(size.height + ((addInsets) ? (cs.insets.top + cs.insets.bottom) : 0)).append("px;");
        }
    }

    private void addPrefSizeComp(OrGuiComponent c, StringBuilder b, boolean addWidth, boolean addHeight) {
    	addPrefSizeComp(c, b, addWidth, addHeight, -1, -1);
    }
    
    // Размеры самого компонента в ячейке
    // Если заполнения нет и задан вес, то размеры в процентах,
    // если есть заполнение и задан вес, то 100%,
    // если задана величина (высота, ширина), то размеры в пикселах
    private void addPrefSizeComp(OrGuiComponent c, StringBuilder b, boolean addWidth, boolean addHeight, int defWidth, int defHeight) {
        boolean fillX = false;
        boolean fillY = false;
        if (c.getConstraints().fill == GridBagConstraints.HORIZONTAL) {
            fillX = true;
        } else if (c.getConstraints().fill == GridBagConstraints.VERTICAL) {
        	fillY = true;
        } else if (c.getConstraints().fill == GridBagConstraints.BOTH) {
        	fillX = fillY = true;
        }
        Dimension size = c.getPrefSize();
        double wx = c.getConstraints().weightx;
        double wy = c.getConstraints().weighty;

        if (addWidth || (wx == 0 && size != null && size.width > 0)) {
        	if (wx > 0) {
        		b.append("width:");
        		int margin = c.getConstraints().insets.left + c.getConstraints().insets.right;
        		if (margin > 3)
    	            b.append("calc(");
        		
    	        if (!fillX && (c instanceof OrMemoField || c instanceof OrRichTextEditor))
    	            b.append(wx*100 > 99 ? 99 : (int)(wx*100)).append("%");
    	        else if (!fillX)
    	            b.append(wx*100 > 100 ? 100 : (int)(wx*100)).append("%");
    	        else if (fillX && (c instanceof OrMemoField || c instanceof OrRichTextEditor))
    	            b.append("99%");
    	        else if (fillX)
    	            b.append("100%");

        		if (margin > 3) {
    	            b.append(" - ").append(margin).append("px);");
        		} else
        			b.append(";");
        	}
	        else if (size != null && size.width > 0)
	            b.append("width:").append(size.width).append("px;");
	        else if (defWidth > -1)
	            b.append("width:").append(defWidth).append("px;");
        }
        if (addHeight) {
        	if (wy > 0) {
        		b.append("height:");
        		int margin = c.getConstraints().insets.top + c.getConstraints().insets.bottom;
        		if (margin > 3)
    	            b.append("calc(");

        		if (!fillY)
		            b.append(wy*100 > 100 ? 100 : (int)(wy*100)).append("%");
		        else if (fillY)
		            b.append("100%");

        		if (margin > 3) {
    	            b.append(" - ").append(margin).append("px);");
        		} else
        			b.append(";");
        	}
        	else if (size != null && size.height > 0)
        		b.append("height:").append(size.height).append("px;");
	        else if (defHeight > -1)
	            b.append("height:").append(defHeight).append("px;");
        }
    }

    private void addPosition(OrGuiComponent orComp, StringBuilder b, StringBuilder style) {
        GridBagConstraints constraints = orComp.getConstraints();
        String align = null;
        String valignStyle = null;

        if (constraints != null) {
            switch (constraints.anchor) {
	            case GridBagConstraints.NORTH: {
	            	valignStyle = "top";
	                align = "center";
	                break;
	            }
	            case GridBagConstraints.NORTHEAST: {
	            	valignStyle = "top";
	                align = "right";
	                break;
	            }
	            case GridBagConstraints.EAST: {
	                valignStyle = "middle";
	                align = "right";
	                break;
	            }
	            case GridBagConstraints.WEST: {
	                valignStyle = "middle";
	                align = "left";
	                break;
	            }
	            case GridBagConstraints.SOUTHEAST: {
	                valignStyle = "bottom";
	                align = "right";
	                break;
	            }
	            case GridBagConstraints.SOUTH: {
	                valignStyle = "bottom";
	                align = "center";
	                break;
	            }
	            case GridBagConstraints.SOUTHWEST: {
	                valignStyle = "bottom";
	                align = "left";
	                break;
	            }
	            case GridBagConstraints.NORTHWEST: {
	                valignStyle = "top";
	                align = "left";
	                break;
	            }
	            case GridBagConstraints.CENTER: {
	                align = "center";
	                valignStyle = "middle";
	                break;
	            }
            }
        }

        if (align != null) {
            b.append(" align='").append(align).append("'");
        }
        if (valignStyle != null) {
            style.append("vertical-align:").append(valignStyle).append(";");
        }
    }

    private void addVPositionInTab(OrGuiComponent orComp, StringBuilder style) {
        style.append("display:table-cell;");
        GridBagConstraints constraints = orComp.getConstraints();

        if (constraints != null) {
            boolean fillY = constraints.fill == GridBagConstraints.VERTICAL || constraints.fill == GridBagConstraints.BOTH;
            
            if (!fillY) {
                String valignStyle = null;
	            switch (constraints.anchor) {
		            case GridBagConstraints.EAST: {
		                valignStyle = "middle";
		                break;
		            }
		            case GridBagConstraints.WEST: {
		                valignStyle = "middle";
		                break;
		            }
		            case GridBagConstraints.SOUTHEAST: {
		                valignStyle = "bottom";
		                break;
		            }
		            case GridBagConstraints.SOUTH: {
		                valignStyle = "bottom";
		                break;
		            }
		            case GridBagConstraints.SOUTHWEST: {
		                valignStyle = "bottom";
		                break;
		            }
		            case GridBagConstraints.CENTER: {
		                valignStyle = "middle";
		                break;
		            }
	            }
		        if (valignStyle != null) {
		            style.append("vertical-align:").append(valignStyle).append(";");
		        }
            }
        }
    }

    private void addHPositionInTab(OrGuiComponent orComp, StringBuilder style) {
        GridBagConstraints constraints = orComp.getConstraints();

        if (constraints != null) {
            boolean fillX = constraints.fill == GridBagConstraints.HORIZONTAL || constraints.fill == GridBagConstraints.BOTH;
            
            if (!fillX) {
                String margin = null;
	            switch (constraints.anchor) {
		            case GridBagConstraints.NORTH: {
		            	margin = "margin-left:auto;margin-right:auto";
		                break;
		            }
		            case GridBagConstraints.NORTHEAST: {
		            	margin = "margin-left:auto;";
		                break;
		            }
		            case GridBagConstraints.EAST: {
		            	margin = "margin-left:auto;";
		                break;
		            }
		            case GridBagConstraints.SOUTHEAST: {
		            	margin = "margin-left:auto;";
		                break;
		            }
		            case GridBagConstraints.SOUTH: {
		            	margin = "margin-left:auto;margin-right:auto";
		                break;
		            }
		            case GridBagConstraints.CENTER: {
		            	margin = "margin-left:auto;margin-right:auto";
		                break;
		            }
	            }
	            if (margin != null) {
	                style.append(margin);
	            }
            }
        }
    }

    /**
	 * Возвращает текст по uid, из xml в атрибуте strings класса ui
	 * 
	 * @return
	 */
    private String getMessagesByUID(String msgUID) {
        if (msgUID != null && !msgUID.isEmpty()) {
            Matcher matcher = pattern.matcher(msgUID);
            if (matcher.matches() && messagesXml != null) {
                NodeList nodeList = messagesXml.getChildNodes().item(0).getChildNodes();
                int l = nodeList.getLength();
                for (int i = 0; i < l; i++) {
                    if (msgUID.equals(nodeList.item(i).getAttributes().item(0).getTextContent())) {
                        return nodeList.item(i).getTextContent();
                    }
                }
            }
        }
        return "";
    }

    private String getBytesByUID(String msgUID) {
        if (msgUID != null && !msgUID.isEmpty()) {
            Matcher matcher = pattern.matcher(msgUID);
            if (matcher.matches() && messagesXml != null) {
                NodeList nodeList = messagesXml.getChildNodes().item(0).getChildNodes();
                int l = nodeList.getLength();
                for (int i = 0; i < l; i++) {
                    if (msgUID.equals(nodeList.item(i).getAttributes().item(0).getTextContent())) {
                    	Node msgNode = nodeList.item(i);
                    	NodeList msgNodeChildren = msgNode.getChildNodes();
                    	if (msgNodeChildren.getLength() > 0) {
                            String s = msgNode.getTextContent();
                            SAXBuilder builder = new SAXBuilder();
                            InputStream is = new ByteArrayInputStream(s.getBytes());
                            try {
                                Element var_doc = builder.build(is).getRootElement();
                                if (var_doc.getName().equals("html")) {
                                    XMLOutputter outp = new XMLOutputter();

                                    outp.setFormat(Format.getCompactFormat());
                                    StringWriter sw = new StringWriter();
                                    outp.output(var_doc.getChild("body").getContent(), sw);
                                    StringBuffer sb = sw.getBuffer();
                                    return sb.toString();
                                }
                            } catch (JDOMException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return null;
	                    } else {
	                    	return null;
	                    }
                    }
                }
            }
        }
        return null;
    }

    public void toHtml(OrGuiComponent comp, StringBuilder sb) {
        if (comp instanceof OrPanel) {
            toHtml((OrPanel) comp, sb);
        } else if (comp instanceof OrGISPanel) {
            toHtml((OrGISPanel) comp, sb);
        } else if (comp instanceof OrAnalyticPanel) {
            toHtml((OrAnalyticPanel) comp, sb);
        } else if (comp instanceof OrImagePanel) {
            toHtml((OrImagePanel) comp, sb);
        } else if (comp instanceof OrLabel) {
            toHtml((OrLabel) comp, sb);
        } else if (comp instanceof OrTextField) {
            toHtml((OrTextField) comp, sb);
        } else if (comp instanceof Spacer) {
            toHtml((Spacer) comp, sb);
        } else if (comp instanceof OrButton) {
            toHtml((OrButton) comp, sb);
        } else if (comp instanceof OrHyperLabel) {
            toHtml((OrHyperLabel) comp, sb);
        } else if (comp instanceof OrDocField) {
            toHtml((OrDocField) comp, sb);
        } else if (comp instanceof OrCheckBox) {
            toHtml((OrCheckBox) comp, sb);
        } else if (comp instanceof OrRadioBox) {
            toHtml((OrRadioBox) comp, sb);
        } else if (comp instanceof OrHyperPopup) {
            toHtml((OrHyperPopup) comp, sb);
        } else if (comp instanceof OrComboBox) {
            toHtml((OrComboBox) comp, sb);
        } else if (comp instanceof OrScrollPane) {
            toHtml((OrScrollPane) comp, sb);
        } else if (comp instanceof OrIntField) {
            toHtml((OrIntField) comp, sb);
        } else if (comp instanceof OrTreeTable2) {
            toHtml((OrTreeTable2) comp, sb);
        } else if (comp instanceof OrTreeTable) {
            toHtml((OrTreeTable) comp, sb);
        } else if (comp instanceof OrTable) {
            toHtml((OrTable) comp, sb);
        }else if (comp instanceof OrTreeField) {
            toHtml((OrTreeField) comp, sb);
        } else if (comp instanceof OrDateField) {
            toHtml((OrDateField) comp, sb);
        } else if (comp.getClass() == OrMemoField.class) {
            toHtml((OrMemoField) comp, sb);
        } else if (comp.getClass() == OrRichTextEditor.class) {
            toHtml((OrRichTextEditor) comp, sb);
        } else if (comp instanceof OrTabbedPane) {
            toHtml((OrTabbedPane) comp, sb);
        } else if (comp instanceof OrFloatField) {
            toHtml((OrFloatField) comp, sb);
        } else if (comp instanceof OrTreeCtrl) {
            toHtml((OrTreeCtrl) comp, sb);
        } else if (comp instanceof OrTreeControl2) {
            toHtml((OrTreeControl2) comp, sb);
        } else if (comp instanceof OrPopUpPanel) {
            toHtml((OrPopUpPanel) comp, sb);
        }  else if (comp instanceof OrCollapsiblePanel) {
            toHtml((OrCollapsiblePanel) comp, sb);
        } else if (comp instanceof OrAccordion) {
            toHtml((OrAccordion) comp, sb);
        } else if (comp instanceof OrSplitPane) {
            toHtml((OrSplitPane) comp, sb);
        } else if (comp instanceof OrImage) {
            toHtml((OrImage) comp, sb);
        } else if (comp instanceof OrPasswordField) {
            toHtml((OrPasswordField) comp, sb);
        } else if (comp instanceof OrNote) {
            toHtml((OrNote) comp, sb);
        } else if (comp instanceof OrBarcode) {
        	toHtml((OrBarcode) comp, sb);
        } else if (comp instanceof OrLayoutPane) {
        	toHtml((OrLayoutPane) comp, sb);
        } else if (comp != null) {
            System.out.println("Не выполненн метод toHTML для класса "+comp.getClass().getName()+";UUID="+comp.getUUID()+";pUUID="+comp.getGuiParent().getUUID());
            sb.append("<font color=\"red\">").append(comp.getClass().getName()).append("</font>").append(EOL);
        } else {
            System.out.println("Не выполненн метод toHTML обнаружен нулевой компонент");
            sb.append("<font color=\"red\">NULL</font>").append(EOL);
        }
    }
    
	/*
	 * Ниже методы toHtml() для каждого компонета, который реализует
	 * OrGuiComponent
	 */

    public void toHtml(OrImage orImage, StringBuilder sb) {
    	PropertyValue pv = orImage.getPropertyValue(new ImagePropertyRoot().getChild("ref")
				.getChild("data"));
    	
    	boolean hasRef = (!pv.isNull() && pv.stringValue() != null && pv.stringValue().length() > 0);

    	String tooltipUID = orImage.getXml().getChildText("toolTip");
        String tooltip = getBytesByUID(tooltipUID);
        
        if(tooltip == null)
    		sb.append("<img");
    	else {
    		sb.append("<div style=\"display:none\">");
    		sb.append("<div id=\"or").append(orImage.getUUID()).append("\">");
    		sb.append(tooltip);
    		sb.append("</div>");
    		sb.append("</div>");
    		
    		sb.append("<img tooltip='1'");
    	}
		String base64 = orImage.getBase64Icon();
		String className = "or3-image"; 
		if (base64 != null) {
		    sb.append(" src=\"data:image/png;base64,").append(base64).append("\"");
		    className += " staticImg";
		}
		else
		    sb.append(" src=''");
		sb.append(" class='"+ className +"'");

		StringBuilder temp = new StringBuilder();
		addPrefSize(orImage, temp, false, false, true);
		if (temp.length() > 0) {
			sb.append(" style=\"").append(temp).append("\"");
		}

		sb.append(" id='" ).append( orImage.getUUID() ).append( "'");
	
		if (orImage.getMaxDataSize() != 0) {
		    sb.append(" maxDataSize=").append(orImage.getMaxDataSize() );
		}
		sb.append(" />").append(EOL);
		if (hasRef) {
			sb.append("<div id=\"mm").append(orImage.getUUID()).append("\" class=\"easyui-menu\" style=\"width:120px;\">");
			sb.append("<div onclick=\"javascript:uploadImage('").append(orImage.getUUID()).append("')\">Загрузить фото</div>");
			sb.append("</div>").append(EOL);
		}
    }

    public void toHtml(OrHyperLabel orHyperLabel, StringBuilder sb) {
        String s = "";
        String tooltipUID = orHyperLabel.getXml().getChildText("toolTip");
        String tooltip = getBytesByUID(tooltipUID);
        
        if (tooltip != null) {
            sb.append("<div style=\"display:none\">");
            sb.append("<div id=\"or").append(orHyperLabel.getUUID()).append("\">");
            sb.append(tooltip);
            sb.append("</div>");
            sb.append("</div>");
            s = " tooltip='1'";
        }

        StringBuilder sbI = new StringBuilder();
        if (orHyperLabel.isVisibleArrow()) {
            String icon = orHyperLabel.getWebNameIcon();
            sbI.append("<img class='or3-btn-icon' src='");
            if ("VSlider.gif".equals(icon)) {
                sbI.append("media/img/").append(icon);
            } else {
                sbI.append("../images/foto/").append(icon);
            }
            sbI.append("'/>");
        }

        sb.append("<a href='' class='hyper'").append(s);
        sb.append(" id='").append(orHyperLabel.getUUID()).append("'");
        StringBuilder temp = new StringBuilder();
        addConstraints(orHyperLabel, temp);
        if (temp.length() > 0) {
            sb.append(" style=\"").append(temp).append("\"");
        }
        if (orHyperLabel.isShowOnTopPan()) {
        	sb.append(" onTop='").append(orHyperLabel.getPositionOnTopPan()).append("'");
        }
        sb.append(">");
        String textUID = orHyperLabel.getXml().getChildText("title");
        String title = getMessagesByUID(textUID);
        if (title.isEmpty()) {
            title = orHyperLabel.getText();
        }
        title = parseTitle(title);
        if (orHyperLabel.getPosIcon() == GridBagConstraints.WEST || orHyperLabel.getPosIcon() == GridBagConstraints.NORTH){
        	if (sbI.length() > 0) {
                sb.append(sbI);
            }
        }
        if (orHyperLabel.getPosIcon() == GridBagConstraints.NORTH) {
        	sb.append("<br>");
        }
        sb.append("<span class='btn-label'");
        addFontStyle(orHyperLabel, temp);
        Utils.getCSS(orHyperLabel.getForeground(), temp);
		if (temp.length() > 0) {
			sb.append(" style=\"").append(temp).append("\"");
		}
        sb.append(">");
        sb.append(title).append("</span>");
        if (orHyperLabel.getPosIcon() == GridBagConstraints.SOUTH) {
        	sb.append("<br>");
        }
        if (orHyperLabel.getPosIcon() == GridBagConstraints.EAST || orHyperLabel.getPosIcon() == GridBagConstraints.SOUTH){
        	if (sbI.length() > 0) {
                sb.append(sbI);
            }
        }
        sb.append("</a>").append(EOL);
        sb.append(EOL);
    }

    public void toHtml(OrDocField orDocField, StringBuilder sb) {
        String blockClass = orDocField.getConstraints().fill == GridBagConstraints.HORIZONTAL ? " block" : "";
        
        String tooltipUID = orDocField.getXml().getChildText("toolTip");
        String tooltip = getBytesByUID(tooltipUID);
        
        boolean isTT = tooltip != null;
        PropertyValue pv = orDocField.getPropertyValue(orDocField.getProperties().getChild("view").getChild("opaque"));
        boolean isAhref = !(pv.isNull() || pv.booleanValue());
        String title = isAhref ? Utils.delHTML(orDocField.getText()) : orDocField.getText();
        if (isTT) {
            sb.append("<div style='display:none'>");
            sb.append("<div id='or").append(orDocField.getUUID()).append("'>");
            sb.append(tooltip);
            sb.append("</div></div>");
        }
        String iconTag = "<img class='or3-btn-icon' src='media/img/DocField.gif' />";
        if (orDocField.getDocAction() == Constants.DOC_UPDATE || orDocField.getDocAction() == Constants.DOC_UPDATE_VIEW) {
        	if (orDocField.isShowUploaded()) {
	            sb.append("<div style=\"width:100%;word-wrap:break-word;\" class=\"easyui-panel\" id='uploaded").append(orDocField.getUUID()).append("'");
	            sb.append(" data-options=\"border:false,href:'../main?uploadedData=").append(orDocField.getUUID()).append("'\">");
	            sb.append("</div>");
        	}
            sb.append("<span class='").append(isAhref ? "" : "btn ").append("btn-small fileinput-button trBtn").append(blockClass).append("'");
            sb.append(" style=\"display: inline-flex;align-items: center;justify-content: center;"); 
            addPrefSize(orDocField, sb, false, false, true);
            addConstraints(orDocField, sb);
            sb.append("\">");
            String icon = orDocField.getBase64Icon();
            if (icon != null) {
                sb.append("<img class='or3-btn-icon' src='data:image/png;base64,").append(icon).append("' />");
            } else {
                sb.append("<i class='fam-attach'></i>");
            }
            if (isAhref) {
                sb.append("<a class='docField' href='#'>"); 
             }
            sb.append("<span class='btn-label'");

            StringBuilder temp = new StringBuilder();
            addFontStyle(orDocField, temp);
	        Utils.getCSS(orDocField.getForeground(), temp);
    		if (temp.length() > 0) {
    			sb.append(" style=\"").append(temp).append("\"");
    		}
    		
            sb.append(">");
            sb.append(title);
            sb.append(isAhref ? "</span></a>":"</span>");
            sb.append("<input id='").append(orDocField.getUUID()).append(isTT ? "' tooltip='1'" : "'");
            sb.append(" type='file' name='file' data-url=\"../main?uid=").append(orDocField.getUUID()).append("\"");
            
            String extensionsProperty = orDocField.getExtensions();
            if (extensionsProperty != null) {
	            String[] extensions = extensionsProperty.trim().split(",");
	            boolean isAdded = false;
	            for (int i = 0; i < extensions.length; i++) {
	            	String extension = extensions[i].trim();
	            	if (allowedExtensions.contains(extension)) {
	            		if (!isAdded) {
	            			sb.append(" accept='");
	            			isAdded = true;
	            		} else {
	            			sb.append(", ");
	            		}
	            		sb.append("." + extension);
	            	}
	            }
	            if (isAdded) {
	            	sb.append("'");
	            }
            }
            
            if(orDocField.isMultipleFile()) {
                sb.append(" multiple='1'");
            }

            if(orDocField.isShowOnTopPan()) {
                sb.append(" onTop='").append(orDocField.getPositionOnTopPan()).append("'");
            }
            sb.append(" class='or3-file-upload'/></span>");
        } else if (orDocField.getDocAction() == Constants.DOC_VIEW || orDocField.getDocAction() == Constants.DOC_EDIT || orDocField.getDocAction() == Constants.DOC_PRINT) {
            sb.append(isAhref ? "<a class='or3-btn view-file trBtn" : "<button class='btn or3-btn view-file");
            sb.append(blockClass).append("'");
            sb.append(" id='").append(orDocField.getUUID()).append("'");
            if(orDocField.isShowOnTopPan()) {
                sb.append(" onTop='").append(orDocField.getPositionOnTopPan()).append("'");
            }
            
            StringBuilder temp = new StringBuilder();
            addPrefSize(orDocField, temp, false, false, true);
            addConstraints(orDocField, temp);
    		if (temp.length() > 0) {
    			sb.append(" style=\"display: inline-flex;align-items: center;justify-content: center;").append(temp).append("\"");
    		}
            sb.append(">").append(EOL);
            
            String icon = orDocField.getBase64Icon();
            if (icon != null) {
                sb.append("<img class='or3-btn-icon' src='data:image/png;base64,").append(icon).append("' />");
            } else {
                sb.append(iconTag);
            }

            sb.append("<span class='btn-label'");

            temp = new StringBuilder();
    		addFontStyle(orDocField, temp);
	        Utils.getCSS(orDocField.getForeground(), temp);
    		if (temp.length() > 0) {
    			sb.append(" style=\"").append(temp).append("\"");
    		}

            sb.append(">");
            sb.append(title).append("</span>");
            sb.append(isAhref ? "</a>" : "</button>").append(EOL);
        }
    }

	public void toHtml(OrPasswordField orPasswordField, StringBuilder sb) {
		StringBuilder temp = new StringBuilder();

		addConstraints((OrGuiComponent) orPasswordField, temp);
		addSize((OrGuiComponent) orPasswordField, temp);
		addMinSize((OrGuiComponent) orPasswordField, temp);
		addMaxSize((OrGuiComponent) orPasswordField, temp);

		String tooltipUID = orPasswordField.getXml().getChildText("toolTip");
        String tooltip = getBytesByUID(tooltipUID);
        
        if(tooltip == null)
			sb.append("<input type=\"password\" ");
		else {
			sb.append("<div style=\"display:none\">");
			sb.append("<div id=\"or").append(orPasswordField.getUUID()).append("\"");
			sb.append(" >");
			sb.append(tooltip);
			sb.append("</div>");
			sb.append("</div>");
			
			sb.append("<input type=\"password\" tooltip='1' ");
		}
		sb.append(" id=\"").append(orPasswordField.getUUID()).append("\"");
		sb.append(" />");
	}

    public void toHtml(OrCheckBox orCheckBox, StringBuilder sb) {
        sb.append("<p>");
        sb.append("<input type=\"checkbox\"");

        String tooltipUID = orCheckBox.getXml().getChildText("toolTip");
        String tooltip = getBytesByUID(tooltipUID);
        
        if (tooltip != null) {
            sb.append(" tooltip='1'");
        }
        StringBuilder tmp = new StringBuilder();
        addConstraints(orCheckBox, tmp);
        if (tmp.length() > 0) {
            sb.append(" style=\"").append(tmp).append("\"");
        }
        sb.append(" value=\"\"");
        sb.append(" id=\"").append(orCheckBox.getUUID()).append("\"");
        sb.append(" />").append(orCheckBox.getText());
        sb.append("</p>");
        if (tooltip != null) {
            sb.append("<div style=\"display:none\">");
            sb.append("<div id=\"or").append(orCheckBox.getUUID()).append("\">");
            sb.append(tooltip);
            sb.append("</div>");
            sb.append("</div>");
        }
    }

    public void toHtml(OrRadioBox orRadioBox, StringBuilder sb) {
        String textUID = orRadioBox.getBorderTitleUID();
        String title = textUID != null ? getMessagesByUID(textUID) : "";
        int colCount = orRadioBox.getColumncount();
        if (colCount < 1) {
            colCount = orRadioBox.getComponentCount();
        }
        if (title != null && !title.isEmpty()) {
            sb.append("<fieldset style='display: inline;'><legend align='center'>").append(title).append("</legend>");
        }
        sb.append("<table type='radio' id=\"").append(orRadioBox.getUUID()).append("\" count='").append(colCount).append("'");
        
        String tooltipUID = orRadioBox.getXml().getChildText("toolTip");
        String tooltip = getBytesByUID(tooltipUID);
        if (tooltip == null) {
            sb.append(" tooltip='1'");
        }
        StringBuilder tmp = new StringBuilder();
        addConstraints(orRadioBox, tmp);
        if (tmp.length() > 0) {
            sb.append(" style=\"").append(tmp).append("\"");
        }
        sb.append(">");
        sb.append("</table>");

        if (title != null && !title.isEmpty()) {
            sb.append("</fieldset>");
        }
        if (tooltip != null) {
            sb.append("<div style='display:none'>");
            sb.append("<div id=\"or").append(orRadioBox.getUUID()).append("\">");
            sb.append(tooltip);
            sb.append("</div></div>");
        }
    }

    public void toHtml(OrHyperPopup comp, StringBuilder sb) {
        String textUID = comp.getXml().getChildText("title");
        String title = getMessagesByUID(textUID);

        if (title.equals(""))
            title = comp.getText(); // временно. потом убрать
        boolean isAhref = false;
        boolean showIcon = true;
        String textAlign = "left";
        title = parseTitle(title);
        PropertyNode pn = comp.getProperties().getChild("view");
        if (pn != null) {
            PropertyValue pv = comp.getPropertyValue(pn.getChild("opaque"));
            if (!pv.isNull()) {
                isAhref = !pv.booleanValue();
            }

            pv = comp.getPropertyValue(pn.getChild("showIcon"));
            if (!pv.isNull()) {
                showIcon = pv.booleanValue();
            }
            pv = comp.getPropertyValue(pn.getChild("alignmentText"));
            if (!pv.isNull()) {
                if (pv.intValue() == SwingConstants.RIGHT)
                    textAlign = "right";
                else if (pv.intValue() == SwingConstants.CENTER)
                    textAlign = "center";
            }

        }
        if (comp.isClearBtnExists()) {
            sb.append("<div style='display:inline-flex;width:100%;'>");
        }
        String tooltipUID = comp.getXml().getChildText("toolTip");
        String tooltip = getBytesByUID(tooltipUID);
        if (tooltip == null) {
            if (isAhref)
                sb.append("<a class='popup");
            else
                sb.append("<button class='popup btn");
        } else {
            sb.append("<div style=\"display:none\">");
            sb.append("<div id=\"or").append(comp.getUUID()).append("\"");
            sb.append(" >");
            sb.append(tooltip);
            sb.append("</div>");
            sb.append("</div>");

            if (isAhref)
                sb.append("<a tooltip='1' class='popup");
            else
                sb.append("<button tooltip='1' class='popup btn");
        }
        boolean fill = false;
        if (comp.getConstraints().fill == GridBagConstraints.HORIZONTAL) {
            sb.append(" block");
            fill = true;
        }

        sb.append("' posIcon='").append(comp.getPosIcon()).append("'");// Cлева - 17, Cправа - 13, Сверху - 11, Снизу - 15
        if(comp.isShowOnTopPan()) {
            sb.append(" onTop='").append(comp.getPositionOnTopPan()).append("'");
        }
        sb.append(" id='").append(comp.getUUID()).append("'");
        sb.append(" style=\"");
        if (!fill && !comp.isClearBtnExists()) {
            addPrefSize(comp, sb, false, false, true);
        } else {
        	sb.append("width:100%;height:100%;");
        }
        addConstraints(comp, sb);
        sb.append(" text-align:" + textAlign + ";");
        //if(comp.isShowOnTopPan()) {
        //    sb.append(" display:inline;");
        //}
        sb.append("\">");

        String iconTag = null;
        if (showIcon) {
            String icon = comp.getBase64Icon();
            if (icon != null) {
                iconTag = "<img class='or3-btn-icon' src='data:image/png;base64," + icon + "' />";
            } else {
                iconTag = "<img class='or3-btn-icon' src='media/img/VSlider.gif' />";
            }
        }
        
        if (iconTag != null && comp.getPosIcon() == 17) { // слева
            sb.append(iconTag);
        }
        sb.append("<span class='btn-label'");
        StringBuilder temp = new StringBuilder();
		addFontStyle(comp, temp);
		Utils.getCSS(comp.getForeground(), temp);
		
        pn = comp.getProperties().getChild("pov").getChild("wrapStyleWord");
        PropertyValue pv = comp.getPropertyValue(pn);
        if (!pv.isNull() && pv.booleanValue()) {
        	temp.append("white-space: normal;");
        }

		if (temp.length() > 0) {
			sb.append(" style=\"").append(temp).append("\"");
		}
        sb.append(">");
        sb.append(title + "</span>");
        if (iconTag != null && comp.getPosIcon() == 13) { // справа
            sb.append(iconTag);
        }
        if (isAhref)
            sb.append("</a>");
        else
            sb.append("</button>");
        if (comp.isClearBtnExists()) {
            sb.append("<img  id='clr").append(comp.getUUID()).append("' class='or3-btn-icon clean-btn btn' src='media/img/DeleteValue.png' />");
            sb.append("</div>");
        }
    }

    public void toHtml(OrTreeField comp, StringBuilder sb) {
        String titleUid = comp.getXml().getChildText("title");
        String title = getMessagesByUID(titleUid);
        if (title.equals(""))
            title = comp.getText();
        
        String tooltipUID = comp.getXml().getChildText("toolTip");
        String tooltip = getBytesByUID(tooltipUID);
        if (tooltip == null) {
            if (comp.isClearBtnExists()) {
                sb.append("<div style='display:inline-flex;width:100%;'>");
            } 
            if (comp.isPrefWidth()) {
            	sb.append("<a href='#' class='treeField btn btn-small' style='white-space: pre-wrap;'");}
            else {
            	sb.append("<a href='#' class='treeField btn btn-small'");
            }
        }else {
            sb.append("<div style=\"display:none\">");
            sb.append("<div id=\"or").append(comp.getUUID()).append("\">");
            sb.append(tooltip);
            sb.append("</div>");
            sb.append("</div>");
            if (comp.isClearBtnExists()) {
                sb.append("<div style='display: table;'>");
            }
            sb.append("<a href='#' class='treeField btn btn-small' tooltip='1'");
        }
        if (comp.isShowOnTopPan()) {
            sb.append(" onTop='").append(comp.getPositionOnTopPan()).append("'");
        }
        StringBuilder tmp = new StringBuilder();
        addConstraints(comp, tmp);
        if (tmp.length() > 0) {
            sb.append(" style=\"").append(tmp).append("\"");
        }
        sb.append(" viewType='").append(comp.getOrTree().getViewType());
        sb.append("' sortType='").append(comp.getOrTree().getSortType());
        sb.append("' id='").append(comp.getUUID()).append("'>");
        sb.append("<i class='fam-table-relationship'></i> ");
        sb.append("<span class='btn-label'></span>");
        sb.append("</a>");
        if (comp.isClearBtnExists()) {        	
            sb.append("<img  id='clr").append(comp.getUUID()).append("' class='or3-btn-icon clean-btn btn' style='height:100%;' src='media/img/DeleteValue.png'/>");
            sb.append("</div>");
        }
    }

    public void toHtml(OrTreeCtrl orTree, StringBuilder sb) {
        Dimension prefSize = orTree.getPrefSize();
        int width = prefSize == null || prefSize.getWidth() < 10 ? 300 : (int) prefSize.getWidth();
        int height = prefSize == null || prefSize.getHeight() < 10 ? 100 : (int) prefSize.getHeight();

        StringBuilder strW = new StringBuilder("width:").append(width).append( "px; ");
        StringBuilder strH = new StringBuilder("height:").append(height).append("px; ");
        String tt = "";
        String tooltipUID = orTree.getXml().getChildText("toolTip");
        String tooltip = getBytesByUID(tooltipUID);
        if (tooltip != null) {
            sb.append("<div style=\"display:none\"><div id=\"or").append(orTree.getUUID()).append("\" >");
            sb.append(tooltip);
            sb.append("</div></div>");
            tt = "tooltip='1'";
        }

        if (orTree.isShowSearchLine()) {
        	sb.append("<input id='_").append(orTree.getUUID()).append("' class=\"tree-search\" placeholder=\"Поиск\" style=\"width:100%;height:20px\"/>");
        }
        sb.append("<div style='").append(strW).append(strH).append("overflow:auto; text-align:left;'>");
        sb.append("<ul class='easyui-tree'").append(tt).append(" id='").append(orTree.getUUID()).append("'");
        sb.append(" viewType='").append(orTree.getViewType());
        sb.append("' sortType='").append(orTree.getSortType());
        sb.append("' selectFolder='").append(orTree.isFolderSelect());
        sb.append("' data-options='checkbox:").append(orTree.isMultiSelection()).append(",cascadeCheck:false,url:\"../main?treeData=")
                .append(orTree.getUUID()).append("\"'>");
        sb.append("</ul>");
        sb.append("</div>");
    }

    public void toHtml(OrTreeControl2 orTree, StringBuilder sb) {
        Dimension prefSize = orTree.getPrefSize();
        int width = prefSize == null || prefSize.getWidth() < 10 ? 300 : (int) prefSize.getWidth();
        int height = prefSize == null || prefSize.getHeight() < 10 ? 100 : (int) prefSize.getHeight();

        StringBuilder strW = new StringBuilder("width:").append(width).append( "px; ");
        StringBuilder strH = new StringBuilder("height:").append(height).append("px; ");
        
        if (orTree.isShowSearchLine()) {
        	sb.append("<input id='_").append(orTree.getUUID()).append("' class=\"tree-search\" placeholder=\"Поиск\" style=\"width:100%;height:20px\"/>");
        }
        sb.append("<div style='").append(strW).append(strH).append("overflow:auto; text-align:left;'>");
        sb.append("<ul class='easyui-tree' id='").append(orTree.getUUID()).append("'");
        sb.append(" viewType='").append(orTree.getViewType());
        sb.append("' sortType='").append(orTree.getSortType());
        sb.append("' selectFolder='").append(orTree.isFolderSelect());
        sb.append("' data-options='checkbox:").append(orTree.isMultiSelection()).append(",cascadeCheck:false,url:\"../main?treeData=")
                .append(orTree.getUUID()).append("\"'>");
        sb.append("</ul>");
        sb.append("</div>");
    }

    public void toHtml(OrTable orTable, StringBuilder sb) {
        OrPanel addPanel = orTable.getAddPan();
        if (addPanel != null) {
            sb.append("<div class='or3-popup-panel'>");
            toHtml(addPanel, sb);
            sb.append("</div>");
            sb.append("<div id=\"emt").append(orTable.getUUID()).append("\" class='datagrid-toolbar'><table cellspacing='0' cellpadding='0'><tbody><tr><td width='100%'></td></tr></tbody></table></div>");
        }
        sb.append("<div");

        StringBuilder tmp = new StringBuilder();
        addPrefSizeComp(orTable, tmp, false, true, -1, 350);
        addConstraints(orTable, tmp);
        if (tmp.length() > 0) {
            sb.append(" style=\"").append(tmp).append("\"");
        }
        sb.append(">");

        if (orTable.isShowSearchLine()) {
            sb.append("<div>");
        	sb.append("<input id='_").append(orTable.getUUID()).append("' class=\"table-search\" placeholder=\"Поиск\" style=\"width:100%;height:20px\"/>");
            sb.append("</div>");
            sb.append("<div style=\"width:100%;height:calc(100% - 20px);\">");
        }

        StringBuilder sbTitle = new StringBuilder();
        PropertyNode pn = orTable.getProperties().getChild("title1");
        if (pn != null) {
            PropertyValue pv = orTable.getPropertyValue(pn.getChild("font"));
            if (!pv.isNull()) {
                Utils.getCSS(pv.fontValue(),sbTitle);
            }
            pv = orTable.getPropertyValue(pn.getChild("fontColorCol"));
            if (!pv.isNull()) {
                Utils.getCSS(pv.colorValue(),sbTitle);
            }
        }
        String tableTitle = null;
        String titleUID = ((OrGuiComponent) orTable).getXml().getChildText("title");
        tableTitle = getMessagesByUID(titleUID);
        String columnName = "";
        sb.append("<table class=\"easyui-datagrid ordatatable\"");
        if (tableTitle != null && orTable.isShowTitle()) {
        	if (sbTitle.length() > 0)
        		sb.append(" title=\"<div style='").append(sbTitle).append("'>").append(tableTitle).append("</div>\"");
        	else
        		sb.append(" title=\"").append(tableTitle).append("\"");
        }
        sb.append(" id=\"").append(orTable.getUUID()).append("\"");

        boolean multiSelection = false;
        PropertyValue pv = orTable.getPropertyValue(orTable.getProperties().getChild("pov").getChild("multiselection"));
        multiSelection = pv.booleanValue();
        
        boolean rownumbers = orTable.isVisibleRowHeader();
        sb.append(" data-options=\"multiSort:true,striped:true,fit:true,singleSelect:").append(!multiSelection).append(",rownumbers:").append(rownumbers);
        if (orTable.getPageSize() > 0) {
            sb.append(",pageSize:").append(orTable.getPageSize());
        }
        if (orTable.isFitColumns())
            sb.append(",fitColumns:true, scrollbarSize:0");
        if (!orTable.isShowHeader())
            sb.append(",showHeader:false,border:false");
        if (!orTable.isRowNowrap())
            sb.append(",nowrap:false");
        sb.append(",collapsible:false,url:'../main?tableData=").append(orTable.getUUID()).append("'\"");
       
        if (orTable.isShowPaging()){
            sb.append(" pagination=\"true\"");
        }
        String pageList = orTable.getPageList().replaceAll(" ", "");
        if (pageList.length() > 0) {
            sb.append(" pageList=\"[").append(pageList).append("]\"");
        }

        if (orTable.isCanSort()) {
            SortedMap<Integer, OrTableColumn> sortedColumns = new TreeMap<Integer, OrTableColumn>();
            int count = orTable.getJTable().getColumnCount();
            for (int i = 0; i < count; i++) {
                OrTableColumn col = (OrTableColumn) orTable.getColumnAt(i);
                if (col != null && col.isSort() && col.isCanSort()) {
                    sortedColumns.put(col.getSortingIndex(), col);
                }
            }
            if (sortedColumns.size() > 0) {
                StringBuilder sortName = new StringBuilder();
                StringBuilder sortOrder = new StringBuilder();
                for (Iterator<Integer> it = sortedColumns.keySet().iterator(); it.hasNext();) {
                    OrTableColumn col = sortedColumns.get(it.next());
                    sortName.append(col.getUUID()).append(",");
                    sortOrder.append(col.getDirection() == Constants.SORT_ASCENDING ? "asc" : "desc").append(",");
                }
                sortName = sortName.deleteCharAt(sortName.length() - 1);
                sortOrder = sortOrder.deleteCharAt(sortOrder.length() - 1);
                sb.append(" sortName=\"").append(sortName).append("\"");
                sb.append(" sortOrder=\"").append(sortOrder).append("\"");
            }
        }
        sb.append(">");

        sb.append("<thead>").append(EOL);
        sb.append("<tr>").append(EOL);
        /*if (multiSelection) {
	        sb.append("<th data-options=\"field:'ck',title:'',checkbox:true\"></th>").append(EOL);
        }*/
        String uuid;
        String wrap = !orTable.isRowNowrap()? "true" : "false";
        for (int i = 0; i < orTable.getJTable().getColumnCount(); i++) {
        	StringBuilder temp = new StringBuilder();
            columnName = "";
            OrColumnComponent col = orTable.getColumnAt(i);
            if (col==null) {
                continue;
            }
            uuid = col.getUUID();
            int rot = ((OrTableColumn) col).getRotation();
            pv = col.getPropertyValue(col.getProperties().getChild("header").getChild("text"));
            if (!pv.isNull()) {
            	Pair<String, Object> p = pv.resourceStringValue();
                String titleUid = p.first;
                columnName = getMessagesByUID(titleUid);
            }
            
            pv = col.getPropertyValue(col.getProperties().getChild("header").getChild("font"));
            if (!pv.isNull()) {
            	Utils.getCSS(pv.fontValue(),temp);
            }
            
            pv = col.getPropertyValue(col.getProperties().getChild("header").getChild("fontColorCol"));
            if (!pv.isNull()) {
            	Utils.getCSS(pv.colorValue(),temp);
            }
            
            pv = col.getPropertyValue(col.getProperties().getChild("header").getChild("backgroundColorCol"));
            if (!pv.isNull()) {
            	temp.append("background-color:").append(Utils.colorToString(pv.colorValue())).append(";");
            }

            pv = col.getPropertyValue(col.getProperties().getChild("width").getChild("pref"));

            int width = 0;
            if (!pv.isNull() && pv.intValue() != 0) {
                width = pv.intValue();
            } else {
                width = Constants.DEFAULT_PREF_WIDTH;
            }

            String align = "";

            if (col.getProperties().getChild("view").getChild("alignmentText") != null) {
                pv = col.getPropertyValue(col.getProperties().getChild("view").getChild("alignmentText"));
                if (!pv.isNull()) {
                    int alignVal = pv.intValue();
                    if (alignVal == JTextField.RIGHT)
                        align = "align:'right',";
                    else if (alignVal == JTextField.CENTER)
                        align = "align:'center',";
                }
            }

            if (columnName == null || columnName.isEmpty()) {
                columnName = orTable.getJTable().getColumnName(i);
            }
            
            StringBuilder editor = new StringBuilder();

            if (col instanceof OrIntColumn) {
            	OrGuiComponent comp =  ((OrTableColumn)col).getEditor();

                pn = comp.getProperties().getChild("constraints");
                pv = comp.getPropertyValue(pn.getChild("charsNumber"));
                int limit = 0;
                String include = null, exclude = null;
                if (!pv.isNull()) {
                    limit = pv.intValue();
                }
                pv = comp.getPropertyValue(pn.getChild("exclude"));
                if (!pv.isNull()) {
                	exclude = pv.stringValue();
                }
                pv = comp.getPropertyValue(pn.getChild("include"));
                if (!pv.isNull()) {
                	include = pv.stringValue();
                }
                if (include != null || exclude != null || limit > 0) {
                	editor.append("editor:{type:'intfield',options:{maxlength:").append(limit)
                		  .append(",include:'").append(include != null ? include : "")
                		  .append("',exclude:'").append(exclude != null ? exclude : "")
                		  .append("'}}");
                } else {
                	editor.append("editor:'numberbox'");
                }
            } else if (col instanceof OrFloatColumn) {
            	OrFloatField orField =  (OrFloatField) ((OrFloatColumn) col).getEditor();
            	pn = orField.getProperties().getChild("constraints").getChild("formatPattern");
                pv = orField.getPropertyValue(pn);
                String pattern = "";
                if (!pv.isNull()) {
                    pattern = pv.stringValue();
                } else {
                    pattern = pn.getDefaultValue().toString();
                }
                int precision = pattern.length() - pattern.indexOf('.') - 1;

                editor.append("editor:{type:'numberbox',options:{precision:" + precision + ",decimalSeparator:',',groupSeparator:''}}");
            } else if (col instanceof OrDateColumn) {
                OrDateField dateEditor = (OrDateField) ((OrDateColumn) col).getEditor();
                int format = dateEditor.getDateFormat();
                boolean showCalendar = dateEditor.isShowCalendar();
                if (format == Constants.DD_MM_YYYY)
                    editor.append(align).append("halign:'center',").append("editor:'datebox'");
                else if (format == Constants.HH_MM)
                    editor.append(align).append("halign:'center',").append("editor:'hhmmEditor'");
                else if (format == Constants.DD_MM_YYYY_HH_MM && showCalendar)
                    editor.append(align).append("halign:'center',").append("editor:'datetimebox'");
                else if (format == Constants.DD_MM_YYYY_HH_MM)
                    editor.append(align).append("halign:'center',").append("editor:'datehhmmEditor'");
                else if (format == Constants.DD_MM_YYYY_HH_MM_SS && showCalendar)
                    editor.append(align).append("halign:'center',").append("editor:{type:'datetimebox',options:{showSeconds:true}}");
                else if (format == Constants.DD_MM_YYYY_HH_MM_SS)
                    editor.append(align).append("halign:'center',").append("editor:'datehhmmssEditor'");
            } else if (col instanceof OrTextColumn) {
                editor.append(align).append("halign:'center',").append("editor:'text'");
            } else if (col instanceof OrMemoColumn) {
            	if (isRnDB) {
                	editor.append("formatter:memoFormat,");
                }
                editor.append("editor:'textarea'");
            } else if (col instanceof OrCheckColumn) {
                editor.append("align:'center',formatter:checkboxFormat,editor:{type:'checkbox',options:{on:'x',off:''}}");
            } else if (col instanceof OrComboColumn) {
                editor.append("formatter:function(value,row){return comboFormat(row, '").append(uuid).append("-title' , '").append(wrap).append("');},")
                        .append("editor:{type:'combobox',options:{novalidate:true,valueField:'" + uuid + "',textField:'")
                        .append(uuid).append("-title',").append("url:'../main?comboData=")
                        .append(uuid).append("'}}");
            } else if (col instanceof OrPopupColumn) {
                editor.append("formatter:hyperPopup");
            } else if (col instanceof OrDocFieldColumn) {
                if (((OrDocField) ((OrDocFieldColumn) col).getEditor()).getDocAction() == Constants.DOC_UPDATE)
                    editor.append("editor:{type:'file', options:{uid:'").append(uuid).append("'}}");
            }

            columnName = parseTitle(columnName);
            sb.append("<th data-options=\"field:'").append(uuid).append("',width:").append(width);
            sb.append(",styler:columnStyler");

            boolean sortable = ((OrTableColumn) col).isCanSort();
            if (sortable) {
                sb.append(",sortable:true");
            }
            if (editor != null) {
                sb.append(",").append(editor);
            }
            sb.append("\">");

            String tooltipUID = col.getXml().getChildText("toolTip");
            String tooltip = getBytesByUID(tooltipUID);
            boolean isAddTooltip = tooltip != null && !tooltip.isEmpty();
            if (isAddTooltip) {
            	sb.append("<div>");
            }
            if (rot == Constants.DONT_ROTATE) {
            	if (temp.length() > 0)  
                	sb.append("<div style=\"").append(temp).append("\">").append(columnName).append("</div>");
            	else 
            		sb.append(columnName);
            } else {
                sb.append(rot == Constants.ROTATE_RIGHT ? "<div class='vertical90'" : "<div class='vertical-90'");
                if (temp.length() > 0)  
                	sb.append(" style=\"").append(temp).append("\">");
                else 
                	sb.append(">");
                sb.append(columnName).append("</div>");
            }
            if (isAddTooltip) {
            	sb.append("<span class='tableColumnTooltipText'>" + tooltip+"</span>");            	
            	sb.append("</div>");
            }
            sb.append("</th>").append(EOL);
        }
        if (orTable.isDeleteRowColumn()) {
	        sb.append("<th data-options=\"field:'rowDeleter',width:22,formatter:deleteRowFmt\"></th>").append(EOL);
        }
        
        sb.append("</tr>").append(EOL);
        sb.append("</thead>").append(EOL);
        sb.append("</table>").append(EOL);
        
        if (orTable.isShowSearchLine()) {
            sb.append("</div>");
        }

        sb.append("</div>");
    }

    public void toHtml(OrTreeTable orTable, StringBuilder sb) {
        OrPanel addPanel = orTable.getAddPan();
        if (addPanel != null) {
            sb.append("<div class='or3-popup-panel'>");
            toHtml(addPanel, sb);
            sb.append("</div>");
            sb.append("<div id=\"emt").append(orTable.getUUID()).append("\" class='datagrid-toolbar'><table cellspacing='0' cellpadding='0'><tbody><tr><td></td></tr></tbody></table></div>");
        }
        String columnName = "ColumnName";

        sb.append("<div");

        StringBuilder tmp = new StringBuilder();
        addPrefSizeComp(orTable, tmp, false, true, -1, 350);
        addConstraints(orTable, tmp);
        if (tmp.length() > 0) {
            sb.append(" style=\"").append(tmp).append("\"");
        }
        sb.append(">");

        if (orTable.isShowSearchLine()) {
            sb.append("<div>");
        	sb.append("<input id='_").append(orTable.getUUID()).append("' class=\"treetable-search\" placeholder=\"Поиск\" style=\"width:100%;height:20px\"/>");
            sb.append("</div>");
            sb.append("<div style=\"width:100%;height:auto;\">");
        }

        StringBuilder sbTitle = new StringBuilder();
        
        PropertyNode pn = orTable.getProperties().getChild("title1");
        if (pn != null) {
            PropertyValue pv = orTable.getPropertyValue(pn.getChild("font"));
            if (!pv.isNull()) {
                Utils.getCSS(pv.fontValue(),sbTitle);
            }
            pv = orTable.getPropertyValue(pn.getChild("fontColorCol"));
            if (!pv.isNull()) {
                Utils.getCSS(pv.colorValue(),sbTitle);
            }
        }
        
        String tableTitle = null;
        String titleUID = orTable.getXml().getChildText("title");
        tableTitle = getMessagesByUID(titleUID);

        sb.append("<table class=\"easyui-treegrid old-tree\"");
        if (tableTitle != null && orTable.isShowTitle()) {
        	if (sbTitle.length() > 0)
        		sb.append(" title=\"<div style='").append(sbTitle).append("'>").append(tableTitle).append("</div>\"");
        	else
        		sb.append(" title=\"").append(tableTitle).append("\"");
        }
        if (orTable.getOrTree() != null) {
            sb.append(" viewType='").append(orTable.getViewType()).append("'");
        }
        sb.append(" id=\"").append(orTable.getUUID()).append("\"");
        boolean multiSelection = false;
        PropertyValue pv = orTable.getPropertyValue(orTable.getProperties().getChild("pov").getChild("multiselection"));
        if (!pv.isNull()) {
            multiSelection = pv.booleanValue();
        }

        sb.append(" data-options=\"striped:true,fit:true,singleSelect:").append(!multiSelection).append(",url:'../main?tableData=").append(orTable.getUUID())
                .append("',rownumbers:true,idField:'id',treeField:'name'");
        if (orTable.isFitColumns())
            sb.append(",fitColumns:true, scrollbarSize:0");
        sb.append("\">");
        sb.append(EOL);
        sb.append("<thead>").append(EOL);
        sb.append("<tr>").append(EOL);

        sb.append("<th field='name' width=\"").append(orTable.getTreeWidth()).append("\"");

        boolean sortable = orTable.isCanSort();
        if (sortable) {
            sb.append(" data-options=\"sortable:true\"");
        }
        sb.append(">");
        
        StringBuilder sbTitle1 = new StringBuilder();
        
        pn = orTable.getProperties().getChild("treeTitle1");
        if (pn != null) {
            pv = orTable.getPropertyValue(pn.getChild("font"));
            if (!pv.isNull()) {
                Utils.getCSS(pv.fontValue(),sbTitle1);
            }
            pv = orTable.getPropertyValue(pn.getChild("fontColorCol"));
            if (!pv.isNull()) {
                Utils.getCSS(pv.colorValue(),sbTitle1);
            }
        }
        String treeTableTitle = null;
        String treeTitleUID = orTable.getXml().getChildText("treeTitle");
        treeTableTitle = getMessagesByUID(treeTitleUID);
        if(treeTableTitle != null) {
	        if (sbTitle1.length() > 0)  
	        	sb.append("<div style=\"").append(sbTitle1).append("\">").append(treeTableTitle).append("</div>");
	    	else 
	    		sb.append(treeTableTitle);
        }
        sb.append("</th>").append(EOL);

        for (int i = 1; i < orTable.getJTable().getColumnCount(); i++) {
        	StringBuilder temp = new StringBuilder();
            columnName = "";
            OrColumnComponent col = orTable.getColumnAt(i);
            int rot = ((OrTableColumn) col).getRotation();
            pv = col.getPropertyValue(col.getProperties().getChild("header").getChild("text"));
            if (!pv.isNull()) {
            	Pair<String, Object> p = pv.resourceStringValue();
                String titleUid = p.first;
                columnName = getMessagesByUID(titleUid);
            }
            
            pv = col.getPropertyValue(col.getProperties().getChild("header").getChild("font"));
            if (!pv.isNull()) {
            	Utils.getCSS(pv.fontValue(),temp);
            }
            
            pv = col.getPropertyValue(col.getProperties().getChild("header").getChild("fontColorCol"));
            if (!pv.isNull()) {
            	Utils.getCSS(pv.colorValue(),temp);
            }
            
            pv = col.getPropertyValue(col.getProperties().getChild("header").getChild("backgroundColorCol"));
            if (!pv.isNull()) {
            	temp.append("background-color:").append(Utils.colorToString(pv.colorValue())).append(";");
            }

            pv = col.getPropertyValue(col.getProperties().getChild("width").getChild("pref"));
            int width = 0;
            if (!pv.isNull() && pv.intValue() != 0) {
                width = pv.intValue();
            } else {
                width = Constants.DEFAULT_PREF_WIDTH;
            }

            String align = "";

            if (col.getProperties().getChild("view").getChild("alignmentText") != null) {
                pv = col.getPropertyValue(col.getProperties().getChild("view").getChild("alignmentText"));
                if (!pv.isNull()) {
                    int alignVal = pv.intValue();
                    if (alignVal == JTextField.RIGHT)
                        align = "align:'right',";
                    else if (alignVal == JTextField.CENTER)
                        align = "align:'center',";
                }
            }
            
            if (columnName.equals("")) {
                columnName = "ColumnName";
            }
            columnName = parseTitle(columnName);

            String tooltipUID = col.getXml().getChildText("toolTip");
            String tooltip = getBytesByUID(tooltipUID);
            
            if(tooltip == null || tooltip.isEmpty())
            	sb.append("<th data-options=\"field:'");
            else {
    			sb.append("<div style=\"display:none\">");
    			sb.append("<div id=\"or" + col.getUUID() +"\">");
    			sb.append(tooltip);
    			sb.append("</div>");
    			sb.append("</div>");
    			sb.append("<th id=\"" + col.getUUID() +"\" tooltip=\"1\" data-options=\"field:'");
    		}
            
            StringBuilder editor = new StringBuilder();
            if (col instanceof OrIntColumn) {
            	editor.append("editor:'numberbox'");
            } else if (col instanceof OrFloatColumn) {
            	OrFloatField orField =  (OrFloatField) ((OrFloatColumn) col).getEditor();
            	pn = orField.getProperties().getChild("constraints").getChild("formatPattern");
                pv = orField.getPropertyValue(pn);
                String pattern = "";
                if (!pv.isNull()) {
                    pattern = pv.stringValue();
                } else {
                    pattern = pn.getDefaultValue().toString();
                }
                int precision = pattern.length() - pattern.indexOf('.') - 1;

                editor.append("editor:{type:'numberbox',options:{precision:" + precision + ",decimalSeparator:',',groupSeparator:''}}");
            } else if (col instanceof OrDateColumn) {
                OrDateField dateEditor = (OrDateField) ((OrDateColumn) col).getEditor();
                int format = dateEditor.getDateFormat();
                if (format == Constants.DD_MM_YYYY)
                    editor.append(align).append("halign:'center',").append("editor:'datebox'");
                else if (format == Constants.HH_MM)
                    editor.append(align).append("halign:'center',").append("editor:'hhmmEditor'");
                else if (format == Constants.DD_MM_YYYY_HH_MM)
                    editor.append(align).append("halign:'center',").append("editor:'datehhmmEditor'");
            } else if (col instanceof OrTextColumn) {
                editor.append(align).append("halign:'center',").append("editor:'text'");
            } else if (col instanceof OrMemoColumn) {
                editor.append("editor:'textarea'");
            } else if (col instanceof OrCheckColumn) {
                editor.append("align:'center',formatter:checkboxFormat,editor:{type:'checkbox',options:{on:'x',off:''}}");
            } else if (col instanceof OrComboColumn) {
                editor.append("formatter:function(value,row){return row['")
                        .append(col.getUUID()).append("-title'];},editor:{type:'combobox',options:{valueField:'").append(col.getUUID())
                        .append("',textField:'").append(col.getUUID()).append("-title',url:'../main?comboData=").append(col.getUUID()).append("'}}");
            } else if (col instanceof OrPopupColumn) {
                editor.append("formatter:hyperPopup");
            } else if (col instanceof OrDocFieldColumn) {
            	 if (((OrDocField) ((OrDocFieldColumn) col).getEditor()).getDocAction() == Constants.DOC_UPDATE)
                     editor.append("editor:{type:'file', options:{uid:'").append(col.getUUID()).append("'}}");
//            	editor.append("editor:{type:'file', options:{valueField:'").append(col.getUUID()).append("'}}");
            }
            sb.append(col.getUUID()).append("',width:").append(width);

            sortable = ((OrTableColumn) col).isCanSort();
            if (sortable) {
                sb.append(",sortable:true");
            }
            if (editor.length() > 0) {
                sb.append(",").append(editor);
            }
            sb.append("\">");
            if (rot == Constants.DONT_ROTATE) {
            	if (temp.length() > 0)  
                	sb.append("<div style=\"").append(temp).append("\">").append(columnName).append("</div>");
            	else 
            		sb.append(columnName);
            } else {
                sb.append(rot == Constants.ROTATE_RIGHT ? "<div class='vertical90'" : "<div class='vertical-90'");
                if (temp.length() > 0)  
                	sb.append(" style=\"").append(temp).append("\">");
                else 
                	sb.append(">");
                sb.append(columnName).append("</div>");
            }
            sb.append("</th>").append(EOL);
        }
        sb.append("</tr>").append(EOL);
        sb.append("</thead>").append(EOL);
        sb.append("</table>").append(EOL);
        if (orTable.isShowSearchLine()) {
            sb.append("</div>");
        }
        sb.append("</div>");
    }

    public void toHtml(OrTreeTable2 orTable, StringBuilder sb) {
        OrPanel addPanel = orTable.getAddPan();
        if (addPanel != null) {
            sb.append("<div class='or3-popup-panel'>");
            toHtml(addPanel, sb);
            sb.append("</div>");
            sb.append("<div id=\"emt").append(orTable.getUUID()).append("\" class='datagrid-toolbar'><table cellspacing='0' cellpadding='0'><tbody><tr><td></td></tr></tbody></table></div>");
        }
        String columnName = "ColumnName";

        boolean fill = false;
        if (orTable.getConstraints().fill == GridBagConstraints.HORIZONTAL) {
            fill = true;
        }
        sb.append("<div");
        sb.append(" style=\"");
        
        int height = 0;
        if (!fill) {
            Dimension prefSize = orTable.getPrefSize();
            if (prefSize != null) {
                if (prefSize.width > 0) {
                    sb.append("width:").append(prefSize.width).append("px;");
                }
                if (prefSize.height > 0) {
                	height = prefSize.height;
                    sb.append("height:").append(prefSize.height).append("px;");
                } else {
                	height = 350;
                    sb.append("height:350px;");
                }
            } else {
            	height = 350;
                sb.append("height:350px;");
            }
        } else {
            sb.append("width:100%;");
            Dimension prefSize = orTable.getPrefSize();
            if (prefSize != null) {
                if (prefSize.height > 0) {
                    sb.append("height:").append(prefSize.height).append("px;");
                	height = prefSize.height;
                } else {
                    sb.append("height:350px;");
                	height = 350;
                }
            } else {
            	height = 350;
                sb.append("height:350px;");
            }
        }

        addConstraints(orTable, sb);

        sb.append("\">");

        if (orTable.isShowSearchLine()) {
            sb.append("<div>");
        	sb.append("<input id='_").append(orTable.getUUID()).append("' class=\"treetable-search\" placeholder=\"Поиск\" style=\"width:100%;height:20px\"/>");
            sb.append("</div>");
            sb.append("<div style=\"width:100%;height:").append(height - 20).append("px;\">");
        }

        StringBuilder sbTitle = new StringBuilder();
        
        PropertyNode pn = orTable.getProperties().getChild("title1");
        if (pn != null) {
            PropertyValue pv = orTable.getPropertyValue(pn.getChild("font"));
            if (!pv.isNull()) {
                Utils.getCSS(pv.fontValue(),sbTitle);
            }
            pv = orTable.getPropertyValue(pn.getChild("fontColorCol"));
            if (!pv.isNull()) {
                Utils.getCSS(pv.colorValue(),sbTitle);
            }
        }
        
        String tableTitle = null;
        String titleUID = orTable.getXml().getChildText("title");
        tableTitle = getMessagesByUID(titleUID);

        sb.append("<table class=\"easyui-treegrid\"");
        if (tableTitle != null && orTable.isShowTitle()) {
        	if (sbTitle.length() > 0)
        		sb.append(" title=\"<div style='").append(sbTitle).append("'>").append(tableTitle).append("</div>\"");
        	else
        		sb.append(" title=\"").append(tableTitle).append("\"");
        }
        if (orTable.getTree() != null) {
            sb.append(" viewType='").append(orTable.getViewType()).append("'");
        }
        sb.append(" id=\"").append(orTable.getUUID()).append("\"");
        boolean multiSelection = false;
        PropertyValue pv = orTable.getPropertyValue(orTable.getProperties().getChild("pov").getChild("multiselection"));
        if (!pv.isNull()) {
            multiSelection = pv.booleanValue();
        }

        sb.append(" data-options=\"striped:true,fit:true,singleSelect:").append(!multiSelection);
        if(multiSelection) {
        	sb.append(",checkbox:true, onlyLeafCheck:true"); // , onlyLeafCheck:true
        }
        sb.append(",url:'../main?tableData=").append(orTable.getUUID())
                .append("',rownumbers:true,idField:'id',treeField:'name'");
        if (orTable.isFitColumns()) {
            sb.append(",fitColumns:true, scrollbarSize:0");
        }
        if (!orTable.isRowNowrap()) {
            sb.append(",nowrap:false");
        }
        sb.append("\">");
        sb.append(EOL);
        sb.append("<thead>").append(EOL);
        sb.append("<tr>").append(EOL);

        sb.append("<th field='name' width=\"").append(orTable.getTreeWidth()).append("\"");

        boolean sortable = orTable.isCanSort();
        if (sortable) {
            sb.append(" data-options=\"sortable:true\"");
        }
        sb.append(">");
        
        StringBuilder sbTitle1 = new StringBuilder();
        
        pn = orTable.getProperties().getChild("treeTitle1");
        if (pn != null) {
            pv = orTable.getPropertyValue(pn.getChild("font"));
            if (!pv.isNull()) {
                Utils.getCSS(pv.fontValue(),sbTitle1);
            }
            pv = orTable.getPropertyValue(pn.getChild("fontColorCol"));
            if (!pv.isNull()) {
                Utils.getCSS(pv.colorValue(),sbTitle1);
            }
        }
        String treeTableTitle = null;
        String treeTitleUID = orTable.getXml().getChildText("treeTitle");
        treeTableTitle = getMessagesByUID(treeTitleUID);
        if(treeTableTitle != null) {
	        if (sbTitle1.length() > 0)  
	        	sb.append("<div style=\"").append(sbTitle1).append("\">").append(treeTableTitle).append("</div>");
	    	else 
	    		sb.append(treeTableTitle);
        }
        sb.append("</th>").append(EOL);

        for (int i = 1; i < orTable.getJTable().getColumnCount(); i++) {
        	StringBuilder temp = new StringBuilder();
            columnName = "";
            OrColumnComponent col = orTable.getColumnAt(i);
            int rot = ((OrTableColumn) col).getRotation();
            pv = col.getPropertyValue(col.getProperties().getChild("header").getChild("text"));
            if (!pv.isNull()) {
            	Pair<String, Object> p = pv.resourceStringValue();
                String titleUid = p.first;
                columnName = getMessagesByUID(titleUid);
            }
            
            pv = col.getPropertyValue(col.getProperties().getChild("header").getChild("font"));
            if (!pv.isNull()) {
            	Utils.getCSS(pv.fontValue(),temp);
            }
            
            pv = col.getPropertyValue(col.getProperties().getChild("header").getChild("fontColorCol"));
            if (!pv.isNull()) {
            	Utils.getCSS(pv.colorValue(),temp);
            }
            
            pv = col.getPropertyValue(col.getProperties().getChild("header").getChild("backgroundColorCol"));
            if (!pv.isNull()) {
            	temp.append("background-color:").append(Utils.colorToString(pv.colorValue())).append(";");
            }

            pv = col.getPropertyValue(col.getProperties().getChild("width").getChild("pref"));
            int width = 0;
            if (!pv.isNull() && pv.intValue() != 0) {
                width = pv.intValue();
            } else {
                width = Constants.DEFAULT_PREF_WIDTH;
            }

            String align = "";

            if (col.getProperties().getChild("view").getChild("alignmentText") != null) {
                pv = col.getPropertyValue(col.getProperties().getChild("view").getChild("alignmentText"));
                if (!pv.isNull()) {
                    int alignVal = pv.intValue();
                    if (alignVal == JTextField.RIGHT)
                        align = "align:'right',";
                    else if (alignVal == JTextField.CENTER)
                        align = "align:'center',";
                }
            }
            
            if (columnName.equals("")) {
                columnName = "ColumnName";
            }
            columnName = parseTitle(columnName);

            String tooltipUID = col.getXml().getChildText("toolTip");
            String tooltip = getBytesByUID(tooltipUID);
            if(tooltip == null || tooltip.isEmpty())
            	sb.append("<th data-options=\"field:'");
            else {
    			sb.append("<div style=\"display:none\">");
    			sb.append("<div id=\"or" + col.getUUID() +"\">");
    			sb.append(tooltip);
    			sb.append("</div>");
    			sb.append("</div>");
    			sb.append("<th id=\"" + col.getUUID() +"\" tooltip=\"1\" data-options=\"field:'");
    		}
            
            StringBuilder editor = new StringBuilder();
            if (col instanceof OrIntColumn) {
            	editor.append("editor:'numberbox'");
            } else if (col instanceof OrFloatColumn) {
            	OrFloatField orField =  (OrFloatField) ((OrFloatColumn) col).getEditor();
            	pn = orField.getProperties().getChild("constraints").getChild("formatPattern");
                pv = orField.getPropertyValue(pn);
                String pattern = "";
                if (!pv.isNull()) {
                    pattern = pv.stringValue();
                } else {
                    pattern = pn.getDefaultValue().toString();
                }
                int precision = pattern.length() - pattern.indexOf('.') - 1;

                editor.append("editor:{type:'numberbox',options:{precision:" + precision + ",decimalSeparator:',',groupSeparator:''}}");
            } else if (col instanceof OrDateColumn) {
                OrDateField dateEditor = (OrDateField) ((OrDateColumn) col).getEditor();
                int format = dateEditor.getDateFormat();
                if (format == Constants.DD_MM_YYYY)
                    editor.append(align).append("halign:'center',").append("editor:'datebox'");
                else if (format == Constants.HH_MM)
                    editor.append(align).append("halign:'center',").append("editor:'hhmmEditor'");
                else if (format == Constants.DD_MM_YYYY_HH_MM)
                    editor.append(align).append("halign:'center',").append("editor:'datehhmmEditor'");
            } else if (col instanceof OrTextColumn) {
            	pv = col.getPropertyValue(col.getProperties().getChild("constraints").getChild("upperAllChar"));
            	boolean upperAllChars = false;
	            if (!pv.isNull()) {
	            	upperAllChars = pv.booleanValue();
	            }
	            pv = col.getPropertyValue(col.getProperties().getChild("constraints").getChild("upperCase"));
            	boolean upperCase = false;
	            if (!pv.isNull()) {
	            	upperCase = pv.booleanValue();
	            }
                editor.append(align).append("halign:'center',").append("editor:{type:'text',options:{upperAllChars:" + upperAllChars + ",upperCase:" + upperCase + "}}");
            } else if (col instanceof OrMemoColumn) {
                editor.append("editor:'textarea'");
            } else if (col instanceof OrCheckColumn) {
                editor.append("align:'center',formatter:checkboxFormat,editor:{type:'checkbox',options:{on:'x',off:''}}");
            } else if (col instanceof OrComboColumn) {
                editor.append("formatter:function(value,row){return row['")
                        .append(col.getUUID()).append("-title'];},editor:{type:'combobox',options:{valueField:'").append(col.getUUID())
                        .append("',textField:'").append(col.getUUID()).append("-title',url:'../main?comboData=").append(col.getUUID()).append("'}}");
            } else if (col instanceof OrPopupColumn) {
                editor.append("formatter:hyperPopup");
            } else if (col instanceof OrDocFieldColumn) {
            	 if (((OrDocField) ((OrDocFieldColumn) col).getEditor()).getDocAction() == Constants.DOC_UPDATE)
                     editor.append("editor:{type:'file', options:{uid:'").append(col.getUUID()).append("'}}");
//            	editor.append("editor:{type:'file', options:{valueField:'").append(col.getUUID()).append("'}}");
            }
            sb.append(col.getUUID()).append("',width:").append(width);

            sortable = ((OrTableColumn) col).isCanSort();
            if (sortable) {
                sb.append(",sortable:true");
            }
            if (editor.length() > 0) {
                sb.append(",").append(editor);
            }
            sb.append("\">");
            if (rot == Constants.DONT_ROTATE) {
            	if (temp.length() > 0)  
                	sb.append("<div style=\"").append(temp).append("\">").append(columnName).append("</div>");
            	else 
            		sb.append(columnName);
            } else {
                sb.append(rot == Constants.ROTATE_RIGHT ? "<div class='vertical90'" : "<div class='vertical-90'");
                if (temp.length() > 0)  
                	sb.append(" style=\"").append(temp).append("\">");
                else 
                	sb.append(">");
                sb.append(columnName).append("</div>");
            }
            sb.append("</th>").append(EOL);
        }
        sb.append("</tr>").append(EOL);
        sb.append("</thead>").append(EOL);
        sb.append("</table>").append(EOL);
        if (orTable.isShowSearchLine()) {
            sb.append("</div>");
        }
        sb.append("</div>");
    }

    public void toHtml(OrPopUpPanel comp, StringBuilder sb) {
        OrPanel orPane = (OrPanel) comp.getMainPanel();
        String textUID = comp.getXml().getChildText("title");
        String text = getMessagesByUID(textUID);
        boolean isAhref = false;
        boolean isShowAsMenu=false;
        String textAlign = "left";
        
        PropertyValue pv = comp.getPropertyValue(comp.getProperties().getChild("view").getChild("opaque"));
        if (!pv.isNull()) {
            isAhref = !pv.booleanValue();
        }
        pv = comp.getPropertyValue(comp.getProperties().getChild("view").getChild("alignmentText"));
    	if (!pv.isNull()) {
            switch (pv.intValue()) {
            case SwingConstants.RIGHT:
                textAlign = "right";
                break;
            case SwingConstants.CENTER:
                textAlign = "center";
                break;
            }
        }
        
        pv = comp.getPropertyValue(comp.getProperties().getChild("pov").getChild("isShowAsMenu"));
        if (!pv.isNull()) {
        	isShowAsMenu = pv.booleanValue();
        }
        
        String tooltipUID = comp.getXml().getChildText("toolTip");
        String tooltip = getBytesByUID(tooltipUID);
        String tt = tooltip == null ? "" : " tooltip='1'";

        if (!tt.isEmpty()) {
            sb.append("<div style=\"display:none\">");
            sb.append("<div id=\"or").append(comp.getUUID()).append("\">");
            sb.append(tooltip);
            sb.append("</div></div>");
        }
        
        sb.append("<a class='").append(isAhref?"or3-btn trBtn":"btn").append(isShowAsMenu?" asMenu popUpPan' rel=\"#pop":" popUpPan' rel=\"#pop").append(comp.getUUID()).append("\" id=\"").append(comp.getUUID()).append("\"");
        if(comp.isSubPanel()) {
            sb.append(" sub='1'");
        } else if(comp.isShowOnTopPan()) {
            sb.append(" onTop='").append(comp.getPositionOnTopPan()).append("'");
        }
        
        StringBuilder tmp = new StringBuilder();
        boolean fillX = false;
        boolean fillY = false;
        if (comp.getConstraints().fill == GridBagConstraints.HORIZONTAL) {
        	fillX = true;
        } else if (comp.getConstraints().fill == GridBagConstraints.VERTICAL) {
        	fillY = true;
        } else if (comp.getConstraints().fill == GridBagConstraints.BOTH) {
        	fillX = fillY == true;
        }
        
        addPrefSize(comp, tmp, fillX, fillY, true);
        addConstraints(comp, tmp);
        addFontStyle(comp, tmp);
        Utils.getCSS(comp.getForeground(), tmp);
        tmp.append("text-align:").append(textAlign).append(";");
        
        if (tmp.length() > 0) {
            sb.append(" style=\"").append(tmp).append("\"");
        }
        sb.append(" title=\"").append(Funcs.xmlQuote2(comp.getToolTipText())).append("\"");
        sb.append(tt);
        sb.append(" hide='").append(comp.isHideAfterClick()?1:0).append("'>");
        
        String icon = comp.getWebNameIcon(); 
        if (icon != null) {
            sb.append("<img class='or3-btn-icon' src='../images/foto/").append(icon).append("'/>");
        }
        sb.append(text).append("<img class='or3-btn-icon' src='media/img/arrow_down.png'/></a>");
        sb.append("<div id='pop").append(comp.getUUID()).append("' class='popUpPanContent'>");
        toHtml(orPane, sb);
        sb.append("</div>");
}
    
    public void toHtml(OrTabbedPane orTabbedPane, StringBuilder sb) {
        String tabTitle;
        PropertyValue pv;
        int count = orTabbedPane.getComponentCount();

        if (count > 0) {
            sb.append("<div class=\"tamur-tabs easyui-panel\"");
            StringBuilder temp = new StringBuilder();
    		addPrefSizeComp(orTabbedPane, temp, true, true);
    		if (temp.length() > 0) {
    			sb.append(" style=\"").append(temp).append("\"");
    		}
            if (orTabbedPane.getConstraints().fill == GridBagConstraints.BOTH
            		&& orTabbedPane.getConstraints().weightx > 0
            		&& orTabbedPane.getConstraints().weighty > 0) {
                sb.append(" data-options=\"fit:true\"");
            }

            sb.append(">");
            sb.append("<div class=\"easyui-tabs\"");
            sb.append(" data-options=\"fit:true\"");
            sb.append(" id=\"").append(orTabbedPane.getUUID()).append("\"");
            int tptp = orTabbedPane.getTabPlacement();
            if (tptp != JTabbedPane.TOP) {
                sb.append(" tabPosition=\"")
                        .append(tptp == JTabbedPane.BOTTOM ? "bottom" : tptp == JTabbedPane.LEFT ? "left" : "right")
                        .append("\"");
            }
            sb.append(">");

            Map<Integer, Component> comps = new TreeMap<Integer, Component>();
            for (int i = 0; i < count; i++) {
            	Component comp = orTabbedPane.getComponent(i);
            	int index = orTabbedPane.indexOfComponent(comp);
            	comps.put(index, comp);
            }
            
            for (int i = 0; i < count; i++) {
            	Component comp = comps.get(i);
                if (comp instanceof OrGuiContainer) {
                    String titleUID = ((OrGuiComponent) comp).getXml().getChildText("title");
                    tabTitle = getMessagesByUID(titleUID);

                    // --потом убрать
                    if (tabTitle.equals("")) {
                        pv = null;
                        pv = ((OrGuiComponent) comp).getPropertyValue(((OrGuiComponent) comp).getProperties().getChild(
                                "title"));
                        if (!pv.isNull())
                            tabTitle = pv.toString();
                    }
                    // -------
                    if (tabTitle.equals("")) {
                        tabTitle = "Вкладка № " + i;
                    }
                    
                    sb.append("<div title=\"").append(tabTitle).append("\" tabIndex=\"").append(i).append("\"");
                    
                    temp = new StringBuilder();
                    addVPositionInTab((OrGuiComponent) comp, temp);
                    if (comp instanceof OrPanel) {
                    	OrPanel pan = (OrPanel)comp;
                    	if (pan.getBackground() != null && (!Constants.removeDefaultColor || !pan.getBackground().equals(UIManager.getColor("Panel.background"))))
                    		temp.append("background-color:").append(Utils.colorToString(((OrPanel)comp).getBackground())).append(";");
                    }
            		if (temp.length() > 0) {
            			sb.append(" style=\"").append(temp).append("\"");
            		}
                    sb.append(">");
                    sb.append("<div style=\"height: 100%; width: 100%; overflow: auto;\">");

            		toHtml((OrGuiComponent) comp, sb);
                    sb.append("</div>");
                    sb.append("</div>");
                }
            }
            sb.append("</div>");
            sb.append("</div>");
        }
    }

	public void toHtml(OrSplitPane orSplitPane, StringBuilder sb) {

		sb.append("<table  class=\"ortable\"");
		sb.append(" id=\"" + orSplitPane.getUUID() + "\"");

		sb.append(" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" height=\"100%\"");

		// StringBuilder temp = new StringBuilder(256);
		// addSize(orSplitPane, temp);
		// addConstraints(orSplitPane, temp);
		// if (temp.length() > 0) {
		// sb.append(" style=\"").append(temp).append("\" ");
		// }

		sb.append(">");
		if (orSplitPane.getOrientation() == Constants.VERTICAL) {
			sb.append("<tr>");
			sb.append("<td>");
			sb.append("<div style='width:100%;height:100%;'>");

			if (orSplitPane.getLeftComponent() != null
					&& orSplitPane.getLeftComponent().isVisible()) {
			    toHtml((OrGuiComponent) orSplitPane.getLeftComponent(), sb);
			}

			sb.append("</div>");
			sb.append("</td>");
			sb.append("<td style='cursor:w-resize; width: 1px;'>");
			sb.append("</td>");
			sb.append("<td>");
			sb.append("<div style='width:100%;height:100%;'>");

			if (orSplitPane.getRightComponent() != null
					&& orSplitPane.getRightComponent().isVisible()) {
			    toHtml((OrGuiComponent) orSplitPane.getRightComponent(), sb);
			}

			sb.append("</div>");
			sb.append("</td>");
			sb.append("</tr>");
		} else {
			sb.append("<tr>");
			sb.append("<td>");
			sb.append("<div style='width:100%;height:100%;'>");

			if (orSplitPane.getLeftComponent() != null
					&& orSplitPane.getLeftComponent().isVisible()) {
			    toHtml((OrGuiComponent) orSplitPane.getLeftComponent(), sb);
			}

			sb.append("</div>");
			sb.append("</td>");
			sb.append("</tr>");
			sb.append("<tr>");
			sb.append("<td style='cursor:n-resize; height: 0px;'>");
			sb.append("</td>");
			sb.append("</tr>");
			sb.append("<tr>");
			sb.append("<td>");
			sb.append("<div style='width:100%;height:100%;'>");

			if (orSplitPane.getRightComponent() != null
					&& orSplitPane.getRightComponent().isVisible()) {
			    toHtml((OrGuiComponent) orSplitPane.getRightComponent(), sb);
			}

			sb.append("</div>");
			sb.append("</td>");
			sb.append("</tr>");
		}
		sb.append("</table>");

	}

	public void toHtml(OrIntField comp, StringBuilder sb) {
        String tt = "";
        
        String tooltipUID = comp.getXml().getChildText("toolTip");
        String tooltip = getBytesByUID(tooltipUID);
        if (tooltip != null) {
            sb.append("<div style=\"display:none\">");
            sb.append("<div id=\"or").append(comp.getUUID()).append("\">");
            sb.append(tooltip);
            sb.append("</div></div>");
            tt = " tooltip='1'";
        }

        StringBuilder temp = new StringBuilder();
		addPrefSize(comp, temp, false, false, false);
		addConstraints(comp, temp);
		
		sb.append("<input class='fcs easyui-numberbox' type='text'").append(tt);
		if (temp.length() > 0) {
			sb.append(" style=\"").append(temp).append("\"");
		}
		sb.append(" id=\"").append(comp.getUUID()).append("\"");
		
        PropertyNode pn = comp.getProperties().getChild("constraints");
        PropertyValue pv = comp.getPropertyValue(pn.getChild("charsNumber"));
        int limit = 0;
        String include = null, exclude = null;
        if (!pv.isNull()) {
            limit = pv.intValue();
            if (limit > 0)
                sb.append(" maxlength='").append(limit).append("'");
        }
        pv = comp.getPropertyValue(pn.getChild("exclude"));
        if (!pv.isNull()) {
            sb.append(" exclude='").append(pv.stringValue()).append("' ");
        	exclude = pv.stringValue();
        }
        pv = comp.getPropertyValue(pn.getChild("include"));
        if (!pv.isNull()) {
            sb.append(" include='").append(pv.stringValue()).append("' ");
        	include = pv.stringValue();
        }
        if (include != null || exclude != null) {
    		sb.append(" data-options=\"filter:validChars\"");
        }
		sb.append("/>");
	}

    public void toHtml(OrFloatField orField, StringBuilder sb) {
    	PropertyNode pn = orField.getProperties().getChild("constraints").getChild("formatPattern");
        PropertyValue pv = orField.getPropertyValue(pn);
        String pattern = "";
        if (!pv.isNull()) {
            pattern = pv.stringValue();
        } else {
            pattern = pn.getDefaultValue().toString();
        }
        int precision = pattern.length() - pattern.indexOf('.') - 1;
        
        pn = orField.getProperties().getChild("constraints").getChild("charsNumber");
        pv = orField.getPropertyValue(pn);
        int limit = 0;
        if (!pv.isNull()) {
            limit = pv.intValue();
        }
        
        StringBuilder temp = new StringBuilder();
        addPrefSize(orField, temp, false, false, false);
        addConstraints(orField, temp);

        sb.append("<input");
        String tooltipUID = orField.getXml().getChildText("toolTip");
        String tooltip = getBytesByUID(tooltipUID);
        if (tooltip != null) {
            sb.append(" tooltip='1'");
        }
		if (temp.length() > 0) {
			sb.append(" style=\"").append(temp).append("\"");
		}
        sb.append(" id=\"").append(orField.getUUID()).append("\"");
        sb.append(" class='fcs easyui-numberbox' " + (limit > 0 ? "maxlength=" + limit + " " : "") + "data-options=\"precision:" + precision + ",decimalSeparator:',',groupSeparator:''\" />");
        if (tooltip != null) {
            sb.append("<div style=\"display:none\">");
            sb.append("<div id=\"or").append(orField.getUUID()).append("\">");
            sb.append(tooltip);
            sb.append("</div></div>");
        }
    }

    public void toHtml(OrDateField orDateField, StringBuilder sb) {
    	String tooltipUID = orDateField.getXml().getChildText("toolTip");
        String tooltip = getBytesByUID(tooltipUID);
        
        if (tooltip == null)
            sb.append("<input");
        else {
            sb.append("<div style=\"display:none\">");
            sb.append("<div id=\"or").append(orDateField.getUUID()).append("\">");
            sb.append(tooltip);
            sb.append("</div>");
            sb.append("</div>");

            sb.append("<input tooltip='1'");
        }
        sb.append(" id=\"").append(orDateField.getUUID()).append("\"");
        
        StringBuilder tmp = new StringBuilder();
        	
        addPrefSize(orDateField, tmp, false, false, true);
        //addConstraints(orDateField, tmp);
        if (tmp.length() > 0) {
            sb.append(" style=\"").append(tmp).append("\"");
        }
        
        StringBuilder opts = new StringBuilder();
        int format = orDateField.getDateFormat();
        if (format == Constants.DD_MM_YYYY)
            sb.append(" class=\"easyui-datebox\"");
        else if (format == Constants.HH_MM)
            sb.append(" class=\"format_hh_mm\"");
        else if (format == Constants.DD_MM_YYYY_HH_MM) {
            sb.append(" class=\"easyui-datetimebox minutes\"");
        	opts.append("showSeconds:false");
        }
        else if (format == Constants.DD_MM_YYYY_HH_MM_SS)
            sb.append(" class=\"easyui-datetimebox seconds\"");

        if (opts.length() > 0) {
            sb.append(" data-options=\"").append(opts).append("\"");
        }
        
        if (canHideCalendar && !orDateField.isShowCalendar()) {
        	sb.append(" hidePanel=\"true\"");
        }
        
        sb.append("/>");
    }

	public void toHtml(OrComboBox cb, StringBuilder sb) {
        PropertyValue pv = cb.getPropertyValue(cb.getProperties().getChild("view").getChild("appearance"));
        int appearance = Constants.VIEW_SIMPLE_COMBO;
        if (!pv.isNull()) {
        	appearance = pv.enumValue();
        }
        pv = cb.getPropertyValue(cb.getProperties().getChild("view").getChild("comboSearch"));
        boolean comboSearch = true;
        if(!pv.isNull()) {
        	comboSearch = !pv.booleanValue();
        }
        boolean fillX = false;
        boolean fillY = false;
        boolean isRNDB = Kernel.instance().isRNDB();

        if (isRNDB) {
	        if (cb.getConstraints().fill == GridBagConstraints.HORIZONTAL) {
		        fillX = true;
		    } else if (cb.getConstraints().fill == GridBagConstraints.VERTICAL) {
		    	fillY = true;
		    } else if (cb.getConstraints().fill == GridBagConstraints.BOTH) {
		    	fillX = fillY = true;
		    }
        }
        String tooltipUID = cb.getXml().getChildText("toolTip");
        String tooltip = getBytesByUID(tooltipUID);
        
        if (appearance == Constants.VIEW_SIMPLE_COMBO) {
            if (tooltip == null)
    			sb.append("<input");
    		else {
    			sb.append("<div style=\"display:none\">");
    			sb.append("<div id=\"or").append(cb.getUUID()).append("\">");
    			sb.append(tooltip);
    			sb.append("</div></div>");
    			
    			sb.append("<input tooltip='1'");
    		}
            StringBuilder temp = new StringBuilder();
    		addPrefSize(cb, temp, fillX, fillY, true);
    		if (temp.length() > 0) {
    			sb.append(" style='").append(temp).append("'");
    		}
    		sb.append(" class='easyui-combobox' id=\"").append(cb.getUUID()).append("\"");
    		sb.append(" data-options=\"novalidate:true,valueField:'").append(cb.getUUID()).append("',textField:'").append(cb.getUUID()).append("-title',url:'../main?comboData=").append(cb.getUUID())
    		.append("',toUpperCase:").append(comboSearch).append("\"");
    		sb.append("/>");
        } else if(appearance == Constants.VIEW_SOLID_LIST) {
            if (tooltip == null)
    			sb.append("<input");
    		else {
    			sb.append("<div style=\"display:none\">");
    			sb.append("<div id=\"or").append(cb.getUUID()).append("\">");
    			sb.append(tooltip);
    			sb.append("</div></div>");
    			
    			sb.append("<input tooltip='1'");
    		}
            StringBuilder temp = new StringBuilder();
    		addPrefSize(cb, temp, fillX, fillY, true);
    		if (temp.length() > 0) {
    			sb.append(" style='").append(temp).append("'");
    		}
    		sb.append(" class='easyui-combobox solid-list' id=\"").append(cb.getUUID()).append("\"");
    		sb.append(" data-options=\"novalidate:true,valueField:'").append(cb.getUUID()).append("',textField:'").append(cb.getUUID()).append("-title',url:'../main?comboData=").append(cb.getUUID())
    		.append("',toUpperCase:").append(comboSearch).append("\"");
    		sb.append("/>");
        } else if(appearance == Constants.VIEW_LIST) {
            sb.append("<div class=\"easyui-datalist\"");
            sb.append(" id=\"").append(cb.getUUID()).append("\"");
            sb.append(" data-options=\"fit:true,singleSelect:false,checkbox:true,lines:false,url:'../main?comboData=").append(cb.getUUID()).append("'\"");
            StringBuilder temp = new StringBuilder();
    		addPrefSize(cb, temp, fillX, fillY, true);
    		addFontStyle(cb, temp);
    		addConstraints(cb, temp);
    		if (temp.length() > 0) {
    			sb.append(" style=\"").append(temp).append("\"");
    		}
    		sb.append("></div>");
        } else if (appearance == Constants.VIEW_CHECKBOX_LIST) {
            sb.append("<div class=\"checklist\"");
            sb.append(" id=\"").append(cb.getUUID()).append("\"");
            sb.append(" url=\"../main?comboData=").append(cb.getUUID()).append("\"");
            StringBuilder temp = new StringBuilder();
    		addPrefSize(cb, temp, isRNDB ? fillX : true, isRNDB ? fillY : true, true);
    		addFontStyle(cb, temp);
    		addConstraints(cb, temp);
    		if (temp.length() > 0) {
    			sb.append(" style=\"").append(temp).append("\"");
    		}
    		sb.append("></div>");
        }
	}

    public void toHtml(OrMemoField orMemoField, StringBuilder sb) {
        StringBuilder tmp = new StringBuilder();
        addConstraints(orMemoField, tmp);

        boolean fillX = false;
        boolean fillY = false;
        if (orMemoField.getConstraints().fill == GridBagConstraints.HORIZONTAL) {
            fillX = true;
        } else if (orMemoField.getConstraints().fill == GridBagConstraints.VERTICAL) {
        	fillY = true;
        } else if (orMemoField.getConstraints().fill == GridBagConstraints.BOTH) {
        	fillX = fillY = true;
        }
        addPrefSize(orMemoField, tmp, fillX, fillY, true);
        addFontStyle(orMemoField, tmp);
        Utils.getCSS(orMemoField.getForeground(), tmp);
        sb.append("<textarea class='fcs' ");
        String tooltipUID = orMemoField.getXml().getChildText("toolTip");
        String tooltip = getBytesByUID(tooltipUID);
        
        if (tooltip != null) {
            sb.append(" tooltip='1'");
        }
        if (tmp.length() > 0) {
            sb.append(" style=\"").append(tmp).append("\"");
        }
        sb.append(" id=\"").append(orMemoField.getUUID()).append("\"");
        if (orMemoField.isWysiwyg()) {
            sb.append(" wysiwyg=1");
        }
        PropertyValue pv = orMemoField.getPropertyValue(orMemoField.getProperties().getChild("pov").getChild("activity").getChild("nocopy"));
        if(!pv.isNull()){
        	sb.append(" oncopy='return false'");
        	sb.append(" ondragstart='return false'");
        }
        sb.append("></textarea>");
        if (tooltip != null) {
            sb.append("<div style=\"display:none\">");
            sb.append("<div id=\"or").append(orMemoField.getUUID()).append("\">");
            sb.append(tooltip);
            sb.append("</div>");
            sb.append("</div>");
        }
    }
    public void toHtml(OrRichTextEditor orRichTextEditor, StringBuilder sb) {
        StringBuilder tmp = new StringBuilder();
        addConstraints(orRichTextEditor, tmp);

        boolean fillX = false;
        boolean fillY = false;
        if (orRichTextEditor.getConstraints().fill == GridBagConstraints.HORIZONTAL) {
            fillX = true;
        } else if (orRichTextEditor.getConstraints().fill == GridBagConstraints.VERTICAL) {
        	fillY = true;
        } else if (orRichTextEditor.getConstraints().fill == GridBagConstraints.BOTH) {
        	fillX = fillY = true;
        }
        addPrefSize(orRichTextEditor, tmp, fillX, fillY, true);
        addFontStyle(orRichTextEditor, tmp);
        Utils.getCSS(orRichTextEditor.getForeground(), tmp);
        sb.append("<textarea class='tinyMCE' ");
        String tooltipUID = orRichTextEditor.getXml().getChildText("toolTip");
        String tooltip = getBytesByUID(tooltipUID);
        
        if (tooltip != null) {
            sb.append(" tooltip='1'");
        }
        if (tmp.length() > 0) {
            sb.append(" style=\"").append(tmp).append("\"");
        }
        sb.append(" id=\"").append(orRichTextEditor.getUUID()).append("\"");
        PropertyValue pv = orRichTextEditor.getPropertyValue(orRichTextEditor.getProperties().getChild("pov").getChild("activity").getChild("nocopy"));
        if(!pv.isNull()){
        	sb.append(" oncopy='return false'");
        	sb.append(" ondragstart='return false'");
        }
        sb.append("></textarea>");

        if (tooltip != null) {
            sb.append("<div style=\"display:none\">");
            sb.append("<div id=\"or").append(orRichTextEditor.getUUID()).append("\">");
            sb.append(tooltip);
            sb.append("</div>");
            sb.append("</div>");
        }
    }
	public void toHtml(OrScrollPane orScrollPane, StringBuilder sb) {
		Component[] orComps = orScrollPane.getOrComponents();

		sb.append("<div class=\"or3-scroll-panel\"");
		sb.append(" id=\"").append(orScrollPane.getUUID()).append("\"");
		sb.append("style=\"");
		switch(orScrollPane.getScrollPolicy()) {
        case 0:
        	sb.append("overflow:scroll;");
            break;
        case 1:
        	sb.append("overflow-y:hidden;");
            break;
        case 2:
        	sb.append("overflow-x:hidden;");
            break;
        case 3:
        	sb.append("overflow:auto;");
            break;
		}
		sb.append("\"");
		sb.append(">");

		for (int i = 0; i < orComps.length; i++) {
			if (orComps[i] instanceof OrGuiComponent) {
			    toHtml((OrGuiComponent) orComps[i], sb);
				break;
			}
		}

		sb.append("</div>").append(EOL);
	}

    public void toHtml(OrButton comp, StringBuilder sb) {
        String textUID = comp.getXml().getChildText("title");
        String text = getMessagesByUID(textUID);
        if (text.equals("")) {
            text = comp.getTitle();
        }
        text = parseTitle(text);

        boolean isAhref = false;
        String textAlign = "left";

        PropertyNode pn = comp.getProperties().getChild("view");
        if (pn != null) {
            PropertyValue pv = comp.getPropertyValue(pn.getChild("opaque"));
            if (!pv.isNull()) {
                isAhref = !pv.booleanValue();
            }

            pv = comp.getPropertyValue(pn.getChild("alignmentText"));
            if (!pv.isNull()) {
                switch (pv.intValue()) {
                case SwingConstants.RIGHT:
                    textAlign = "right";
                    break;
                case SwingConstants.CENTER:
                    textAlign = "center";
                    break;
                }
            }
        }

        String tooltipUID = comp.getXml().getChildText("toolTip");
        String tooltip = getBytesByUID(tooltipUID);
        
        String tt = tooltip == null ? "" : " tooltip='1'";

        if (!tt.isEmpty()) {
            sb.append("<div style=\"display:none\">");
            sb.append("<div id=\"or").append(comp.getUUID()).append("\">");
            sb.append(tooltip);
            sb.append("</div></div>");
        }

        sb.append(isAhref ? "<a href='#' class='or3-btn trBtn" : "<button class='btn or3-btn");

        boolean fill = false;
        if (comp.getConstraints().fill == GridBagConstraints.HORIZONTAL) {
            sb.append(" block");
            fill = true;
        }

        sb.append("' id='").append(comp.getUUID()).append("'").append(tt);
        if(comp.isShowOnTopPan()) {
            sb.append(" onTop='").append(comp.getPositionOnTopPan()).append("'");
        }
        sb.append(" style=\"");
        sb.append("text-align:").append(textAlign).append(";");
        if (!fill) {
            addPrefSize(comp, sb, false, false, true);
        }
        
        addConstraints(comp, sb);
        sb.append("\">").append(EOL);
        String iconTag = null;
        String icon = comp.getBase64Icon();
            if (icon != null) {
                iconTag = "<img class='or3-btn-icon' src='data:image/png;base64," + icon + "' />";
            }
    
        if (comp.getPosIcon() == GridBagConstraints.WEST || comp.getPosIcon() == GridBagConstraints.NORTH){
        	if (iconTag != null) {
                sb.append(iconTag);
            }
        }
        if (comp.getPosIcon() == GridBagConstraints.NORTH) {
        	sb.append("<br>");
        }
        sb.append("<span class='btn-label'");
        StringBuilder temp = new StringBuilder();
        addFontStyle(comp, temp);
        Utils.getCSS(comp.getForeground(), temp);
        
		if (temp.length() > 0) {
			sb.append(" style=\"").append(temp).append("\"");
		}
		
        sb.append(">");
        sb.append(text != null ? text : "").append("</span>");
        if (comp.getPosIcon() == GridBagConstraints.SOUTH) {
        	sb.append("<br>");
        }
        if (comp.getPosIcon() == GridBagConstraints.EAST || comp.getPosIcon() == GridBagConstraints.SOUTH){
        	if (iconTag != null) {
                sb.append(iconTag);
            }
        }
        sb.append(isAhref ? "</a>" : "</button>").append(EOL);
    }

    public void toHtml(OrLabel comp, StringBuilder sb) {
        String textUID = comp.getXml().getChildText("title");
        String text = getMessagesByUID(textUID);
        if (text.isEmpty()) {
            text = comp.getText();
        }
        if (!text.isEmpty()) {
            StringBuilder temp = new StringBuilder();
            addPrefSize(comp, temp, false, false, true);
            addConstraints(comp, temp);
            addFontStyle(comp, temp);
            Utils.getCSS(comp.getForeground(), temp);
            
            Dimension size = comp.getPrefSize();
            if (size != null && size.width > 0){
            	int align = comp.getHorizontalAlignment();
            	switch (align) {
            		case SwingConstants.RIGHT:
            			temp.append("text-align:right;");
		                break;
		            case SwingConstants.CENTER:
		            	temp.append("text-align:center;");
		                break;
		            }
            	temp.append("white-space:nowrap; overflow:hidden; text-overflow: ellipsis;");
            }

            sb.append("<span class=\"block\"");
            sb.append(" id=\"").append(comp.getUUID()).append("\"");
            if (temp != null && temp.length() > 0) {
    			sb.append(" style=\"").append(temp).append("\"");
            }
            sb.append(">").append(EOL);
            sb.append(parseTitle(text));
            sb.append("</span>").append(EOL);
        }
    }
    
    public void toHtml(OrBarcode orBarcode, StringBuilder sb) {
    	PropertyValue pv = orBarcode.getPropertyValue(new BarcodePropertyRoot().getChild("ref")
				.getChild("data"));
    	
    	boolean hasRef = (!pv.isNull() && pv.stringValue() != null && pv.stringValue().length() > 0);

    	String tooltipUID = orBarcode.getXml().getChildText("toolTip");
        String tooltip = getBytesByUID(tooltipUID);
        
    	if(tooltip == null)
    		sb.append("<img");
    	else {
    		sb.append("<div style=\"display:none\">");
    		sb.append("<div id=\"or").append(orBarcode.getUUID()).append("\">");
    		sb.append(tooltip);
    		sb.append("</div>");
    		sb.append("</div>");
    		
    		sb.append("<img tooltip='1'");
    	}
		String base64 = orBarcode.getBase64Icon();
		String className = "or3-barcodeimage"; // было or3-image
		if (base64 != null) {
		    sb.append(" src=\"data:image/png;base64,").append(base64).append("\"");
		    //className += " staticImg";
		}
		else
		    sb.append(" src=''");
		sb.append(" class='"+ className +"'");

        StringBuilder temp = new StringBuilder();
		addPrefSize(orBarcode, temp, false, false, true);
		if (temp != null && temp.length() > 0) {
			sb.append(" style=\"").append(temp).append("\"");
        }

		sb.append(" id='" ).append( orBarcode.getUUID() ).append( "'");
	
		if (orBarcode.getMaxDataSize() != 0) {
		    sb.append(" maxDataSize=").append(orBarcode.getMaxDataSize() );
		}
		sb.append(" />").append(EOL);
/*		if (hasRef) {
			sb.append("<div id=\"mm").append(orBarcode.getUUID()).append("\" class=\"easyui-menu\" style=\"width:120px;\">");
			sb.append("<div onclick=\"javascript:uploadImage('").append(orBarcode.getUUID()).append("')\">Загрузить фото</div>");
			sb.append("</div>").append(EOL);
		}
*/    }
    

    public void toHtml(Spacer spacer, StringBuilder sb) {
        StringBuilder temp = new StringBuilder();
        addMaxSize(spacer, temp);
        sb.append("<span");
        if (temp.length() > 0) {
            sb.append(" style=\"").append(temp).append("\"");
        }
        sb.append("></span>").append(EOL);
    }

    public void toHtml(OrNote note, StringBuilder sb) {
        String textUID = note.getXml().getChildText("title");
        String text = getMessagesByUID(textUID);
        if (text.equals("")) {
            text = note.getTitle();
        }
        text = parseTitle(text);

        boolean isAhref = false;
        String textAlign = "left";

        PropertyNode pn = note.getProperties().getChild("view");
        if (pn != null) {
            PropertyValue pv = note.getPropertyValue(pn.getChild("opaque"));
            if (!pv.isNull()) {
                isAhref = !pv.booleanValue();
            }

            pv = note.getPropertyValue(pn.getChild("alignmentText"));
            if (!pv.isNull()) {
                switch (pv.intValue()) {
                case SwingConstants.RIGHT:
                    textAlign = "right";
                    break;
                case SwingConstants.CENTER:
                    textAlign = "center";
                    break;
                }
            }
        }

        sb.append(isAhref ? "<a href='#' class='or3-note trBtn" : "<button class='btn or3-note");

        boolean fill = false;
        if (note.getConstraints().fill == GridBagConstraints.HORIZONTAL) {
            sb.append(" block");
            fill = true;
        }

        sb.append("' id='").append(note.getUUID()).append("'");
        if (note.isShowOnTopPan()) {
            sb.append(" onTop='").append(note.getPositionOnTopPan()).append("'");
        }
        sb.append(" style=\"");
        if (!fill) {
            addPrefSize(note, sb, false, false, true);
        }
        sb.append("text-align:").append(textAlign).append(";");
        addConstraints(note, sb);

        sb.append("\">").append(EOL);
        String icon = note.getBase64Icon();
        sb.append("<img class='or3-btn-icon' src='");
        
        if (icon == null) {
            sb.append("media/img/") .append(note.getWebNameIcon());
        } else {
            sb.append("data:image/png;base64,").append(icon);
        }
        sb.append("'/>");
        sb.append("<span class='btn-label'");

        StringBuilder temp = new StringBuilder();
        addFontStyle(note, temp);
		if (temp != null && temp.length() > 0) {
			sb.append(" style=\"").append(temp).append("\"");
        }
        sb.append(">");
        sb.append(text).append("</span>");
        sb.append(isAhref ? "</a>" : "</button>").append(EOL);
    }

    public void toHtml(OrTextField comp, StringBuilder sb) {
        String tt = "";
        String tooltipUID = comp.getXml().getChildText("toolTip");
        String tooltip = getBytesByUID(tooltipUID);
        if (tooltip != null) {
            sb.append("<div style=\"display:none\">");
            sb.append("<div id=\"or").append(comp.getUUID()).append("\">");
            sb.append(tooltip);
            sb.append("</div></div>");
            tt = " tooltip='1'";
        }
        if(comp.isEmailType()) {
        	sb.append("<input class='fcs' type=\"email\" multiple");
        } else {
        	sb.append("<input class='fcs' type=\"text\"");
        }

        sb.append(tt);

        sb.append(" id=\"").append(comp.getUUID()).append("\"");
        
        PropertyNode pn = comp.getProperties().getChild("pov").getChild("activity").getChild("editable");
        PropertyValue pv;
        if (pn != null) {
        	pv = comp.getPropertyValue(pn);
        	if (!pv.isNull() && pv.booleanValue()) {
        		pn = comp.getProperties().getChild("view").getChild("showAllText");
        		if (pn != null) {
                	pv = comp.getPropertyValue(pn);
                	if (!pv.isNull() && pv.booleanValue()) {
                		sb.append(" showAllText=\"true\"");
                	}
        		}
        	}
        }
        
        pn = comp.getProperties().getChild("constraints");
        pv = comp.getPropertyValue(pn.getChild("charsNumber"));
        if (!pv.isNull()) {
            int limit = pv.intValue();
            if (limit > 0) {
                sb.append(" maxlength='").append(limit).append("' ");
            }
        }
        boolean flc = false;
        pv = comp.getPropertyValue(pn.getChild("exclude"));
        if (!pv.isNull()) {
            sb.append(" exclude='").append(pv.stringValue()).append("' ");
            flc = true;
        }
        pv = comp.getPropertyValue(pn.getChild("include"));
        if (!pv.isNull()) {
            sb.append(" include='").append(pv.stringValue()).append("' ");
            flc = true;
        }
        
        if (flc) {
            sb.append(" onkeypress='return validChars(this, event);'");
        }
        
        boolean upperAllChars = false;
        pv = comp.getPropertyValue(pn.getChild("upperAllChar"));
        if (!pv.isNull()) {
        	upperAllChars = pv.booleanValue();
        }
        
        boolean upperFirstChar = false;
        pv = comp.getPropertyValue(pn.getChild("upperCase"));
        if (!pv.isNull()) {
        	upperFirstChar = pv.booleanValue();
        }
        
        if (upperAllChars || upperFirstChar) {
        	sb.append(" onkeyup='changeToUpperCase(this, event, " + upperAllChars + ", " + upperFirstChar + ")'");
        }
        
        pv = comp.getPropertyValue(comp.getProperties().getChild("pov").getChild("activity").getChild("nocopy"));
        if(!pv.isNull()){
        	sb.append(" oncopy='return false'");
        	sb.append(" ondragstart='return false'");
        }
        
        StringBuilder temp = new StringBuilder();
        
        boolean fillX = false;
        boolean fillY = false;
		if (Kernel.instance().isRNDB()) {
		    if (comp.getConstraints().fill == GridBagConstraints.HORIZONTAL) {
		        fillX = true;
		    } else if (comp.getConstraints().fill == GridBagConstraints.VERTICAL) {
		    	fillY = true;
		    } else if (comp.getConstraints().fill == GridBagConstraints.BOTH) {
		    	fillX = fillY = true;
		    }
		}
        
        addPrefSize(comp, temp, fillX, fillY, true);
        addFontStyle(comp, temp);
        addConstraints(comp, temp);
        Utils.getCSS(comp.getForeground(),temp);
        
//        temp.append("border-radius: 5px 5px 5px 5px;");

        pv = comp.getPropertyValue(comp.getProperties().getChild("view").getChild("alignmentText"));
        if (!pv.isNull()) {
            if (pv.intValue() == SwingConstants.RIGHT)
                temp.append("text-align:right;");
            else if (pv.intValue() == SwingConstants.CENTER)
                temp.append("text-align:center;");
        }
		if (temp != null && temp.length() > 0) {
			sb.append(" style=\"").append(temp).append("\"");
        }
        sb.append("/>").append(EOL);
    }
    
    public void toHtml(OrCollapsiblePanel comp, StringBuilder sb) {
        String uuid = comp.getUUID();
        StringBuilder styleTitle = new StringBuilder();
        StringBuilder title = new StringBuilder();
        StringBuilder content = new StringBuilder();
        StringBuilder icon = new StringBuilder();
        int tat = comp.getTitleAlignmentText();
        Color tfc = comp.getTitleFontColor();
        Font tfg = comp.getTitleFontG();
        int tpp = comp.getTitlePanePostion();
        String name = comp.getWebNameIcon();
        sb.append("<div class='coll-pan").append(tpp == SwingConstants.LEFT || tpp == SwingConstants.RIGHT ? "-v" : "").append("' id='").append(uuid).append("'");

        StringBuilder tmp = new StringBuilder();
        addFill(comp, tmp);
        addConstraints(comp, tmp);

		if (tmp.length() > 0) {
			sb.append(" style=\"").append(tmp).append("\"");
        }
		sb.append(">");

        if (name!=null) {
            icon.append("<img class='or3-btn-icon' src='../images/foto/").append(name).append("'/>");
        }
        Utils.getCSS(tfc, styleTitle);
        switch (tat) {
        case SwingConstants.CENTER:
            styleTitle.append("text-align:center;");
            break;
        case SwingConstants.LEFT:
            styleTitle.append("text-align:left;");
            break;
        case SwingConstants.RIGHT:
            styleTitle.append("text-align:right;");
            break;
        }
        Utils.getCSS(tfg,styleTitle);
        title.append("<span class='bcg-coll-pan ttl-coll-pan").append(tpp == SwingConstants.LEFT?"-v vertical-90": tpp == SwingConstants.RIGHT ? "-v vertical90" : "");
        title.append("' id='t").append(uuid).append("' ");
        title.append("style=\"").append(styleTitle).append("\">").append(icon).append(comp.getTitle()).append("</span>");
        
        content.append("<div class='cnt-coll-pan").append(tpp == SwingConstants.LEFT || tpp == SwingConstants.RIGHT ? "-v" : "").append("' id='cnt").append(uuid).append("'>");
        toHtml(comp.getContent(), content);
        content.append("</div>");

        switch (tpp) {
        case SwingConstants.TOP:
            sb.append(title).append(content);
            break;
        case SwingConstants.BOTTOM:
            sb.append(content).append(title);
            break;
        case SwingConstants.LEFT:
            sb.append("<table><tr><td><div>");
            sb.append(title).append("</div></td><td>").append(content);
            sb.append("</td></tr></table>");
            break;
        case SwingConstants.RIGHT:
            sb.append("<table><tr><td>");
            sb.append(content).append("</td><td><div>").append(title);
            sb.append("</div></td></tr></table>");
            break;
        }
        sb.append("</div>").append(EOL);
    }

    public void toHtml(OrAccordion comp, StringBuilder sb) {
        String uuid = comp.getUUID();
        StringBuilder styleTitle;
        StringBuilder title;
        StringBuilder content;
        StringBuilder icon;
        int tat;
        Color tfc;
        Font tfg;
        int tpp;
        String name;
        boolean isH = comp.getOrientation() == SwingConstants.HORIZONTAL;
        sb.append("<div class='accordion").append(isH ? "-v' id='" : "' id='").append(uuid).append("' multi='").append(comp.isMultiplySelectionAllowed() ? 1 : 0).append("'");
        
        StringBuilder tmp = new StringBuilder();
        addFill(comp, tmp);
        addConstraints(comp, tmp);

		if (tmp.length() > 0) {
			sb.append(" style=\"").append(tmp).append("\"");
        }
		sb.append(">");

        List<OrPanel> panels = comp.getContent();
        int i = 0;
        if (isH) {
            sb.append("<table><tr>");
        }
        for (OrPanel panel : panels) {
            styleTitle = new StringBuilder();
            title = new StringBuilder();
            content = new StringBuilder();
            icon = new StringBuilder();
            tat = comp.getPanelAt(i).getTitleAlignmentText();
            tfc = comp.getPanelAt(i).getTitleFontColor();
            tfg = comp.getPanelAt(i).getTitleFontG();
            tpp = comp.getPanelAt(i).getTitlePanePostion();
            name = comp.getWebNameIcons().get(i);

            if (name != null) {
                icon.append("<img class='or3-btn-icon' src='../images/foto/").append(name)
                        .append("'/>");
            }
            Utils.getCSS(tfc, styleTitle);
            if (tat != -1) {
                switch (tat) {
                case SwingConstants.CENTER:
                    styleTitle.append(" text-align:center");
                    break;
                case SwingConstants.LEFT:
                    styleTitle.append(" text-align:left;");
                    break;
                case SwingConstants.RIGHT:
                    styleTitle.append(" text-align:right;");
                    break;
                }
            }
            Utils.getCSS(tfg,styleTitle);
            title.append("<span class='ttl-coll-pan").append(isH ? "-v vertical-90" : " bcg-coll-pan").append("' id='t").append(i).append(uuid).append("' ");
            String titleUID = ((OrGuiComponent) comp).getXml().getChild("titleN").getChildText("title_" + i);
            String title_n = getMessagesByUID(titleUID);
            title.append("style=\"").append(styleTitle).append("\" onmouseover=\"this.style.textDecoration='underline'; this.style.cursor='pointer'\" onmouseout=\"this.style.textDecoration='none'\">").append(icon).append(title_n != null ? title_n : "").append("</span>");
            content.append("<div class='accord cnt-coll-pan").append(isH ? "-v" : "").append("' id='cnt").append(i).append(uuid).append("'>");
            toHtml(panel, content);
            content.append("</div>");

            switch (tpp) {
            case SwingConstants.TOP:
                sb.append(title).append(content);
                break;
            case SwingConstants.BOTTOM:
                sb.append(content).append(title);
                break;
            case SwingConstants.LEFT:
                sb.append("<td class='bcg-coll-pan'><div>");
                sb.append(title).append("</div></td><td>").append(content);
                sb.append("</td>");
                break;
            case SwingConstants.RIGHT:
                sb.append("<td>");
                sb.append(content).append("</td><td class='bcg-coll-pan'><div>").append(title);
                sb.append("</div></td>");
                break;
            }
            ++i;
        }
        if (isH) {
            sb.append("</tr></table>");
        }
        sb.append("</div>").append(EOL);
    }

    public void toHtml(OrLayoutPane pane, StringBuilder sb) {
        String tabTitle;
        PropertyValue pv;
        Component[] comps = pane.getComponents();

        if (comps != null) {
            sb.append("<div class=\"easyui-layout\" id=\"").append(pane.getUUID()).append("\"");

            StringBuilder temp = new StringBuilder();
    		addFill(pane, temp);
    		
    		if (temp.length() > 0) {
    			sb.append(" style=\"").append(temp).append("\"");
    		}
            sb.append(">");
            
            for (int i = 0; i < comps.length; i++) {
                if (comps[i] instanceof OrGuiComponent && !(comps[i] instanceof EmptyPlace)) {
                	OrGuiComponent guiComp = (OrGuiComponent) comps[i];
                    String titleUID = guiComp.getXml().getChildText("title");
                    tabTitle = getMessagesByUID(titleUID);

                    if (tabTitle.equals("")) {
                        pv = guiComp.getPropertyValue(guiComp.getProperties().getChild("title"));
                        if (!pv.isNull())
                            tabTitle = pv.toString();
                    }
                    String pos = (String) ((BorderLayout)pane.getLayout()).getConstraints(comps[i]);
                    if (tabTitle.equals("")) {
                        tabTitle = pos;
                    }
                    String region = BorderLayout.PAGE_START.equals(pos) ? "north"
                    				: BorderLayout.LINE_END.equals(pos) ? "east"
                    				: BorderLayout.PAGE_END.equals(pos) ? "south"
                    				: BorderLayout.LINE_START.equals(pos) ? "west" : "center";
                    
                    boolean showHeader = false;
                    boolean collapsible = false;
                    boolean refreshable = false;
                    boolean expandable = false;
                    if (guiComp instanceof OrPanel) {
                        pv = guiComp.getPropertyValue(guiComp.getProperties().getChild("view").getChild("showHeader"));
                        if (!pv.isNull())
                        	showHeader = pv.booleanValue();
                        pv = guiComp.getPropertyValue(guiComp.getProperties().getChild("view").getChild("collapsible"));
                        if (!pv.isNull())
                        	collapsible = pv.booleanValue();
                        pv = guiComp.getPropertyValue(guiComp.getProperties().getChild("view").getChild("refreshable"));
                        if (!pv.isNull())
                        	refreshable = pv.booleanValue();
                        pv = guiComp.getPropertyValue(guiComp.getProperties().getChild("view").getChild("expandable"));
                        if (!pv.isNull())
                        	expandable = pv.booleanValue();
                    }

                    sb.append("<div data-options=\"region:'").append(region).append("',split:true")
                    	.append(",collapsible:").append(collapsible)
                    	.append(",maximizable:").append(expandable).append("\"");
                    
                    if (showHeader && tabTitle.length() > 0)
            			sb.append(" title=\"").append(tabTitle).append("\"");
                    	
                    temp = new StringBuilder();
            		addPrefSizeCell(guiComp, temp, true, true, true);
            		if (temp.length() > 0) {
            			sb.append(" style=\"").append(temp).append("\"");
            		}
                    sb.append(">");
                    toHtml(guiComp, sb);
                    sb.append("</div>");
                }
            }
            sb.append("</div>");
        }
    }

    public class SubComponent {
        private String componentName;
        private OrGuiComponent component;
        private int posX;
        private int posY;
        private double weightX;
        private double weightY;
        private int gridWidth;
        private int gridHeight;

        public SubComponent(OrGuiComponent comp, int x, int y, double weightX, double weightY, int gridWidth, int gridHeight) {
            component = comp;
            posX = x;
            posY = y;
            this.weightX = weightX;
            this.weightY = weightY;
            this.gridWidth = gridWidth;
            this.gridHeight = gridHeight;
            componentName = comp.getClass().getName();
        }
    }
}