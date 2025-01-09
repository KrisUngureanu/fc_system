package kz.tamur.web.component;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.AbstractButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import kz.tamur.comps.Constants;
import kz.tamur.comps.Filter;
import kz.tamur.comps.FilterMenuItem;
import kz.tamur.comps.Mode;
import kz.tamur.comps.OrGuiComponent;
import kz.tamur.comps.PropertyValue;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.rt.ConfigObject;
import kz.tamur.rt.Utils;
import kz.tamur.rt.adapters.TableAdapter;
import kz.tamur.rt.adapters.TableAdapter.ProcessRecordAction;
import kz.tamur.util.FilterObject;
import kz.tamur.web.common.CommonHelper;
import kz.tamur.web.common.JSONComponent;
import kz.tamur.web.common.LangHelper;
import kz.tamur.web.common.webgui.WebButton;
import kz.tamur.web.common.webgui.WebButtonGroup;
import kz.tamur.web.common.webgui.WebComponent;
import kz.tamur.web.common.webgui.WebLabel;
import kz.tamur.web.common.webgui.WebMenuItem;
import kz.tamur.web.common.webgui.WebPopupMenu;
import kz.tamur.web.common.webgui.WebSpinner;
import kz.tamur.web.common.webgui.WebToolbar;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.StringValue;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 04.08.2004
 * Time: 9:56:23
 * To change this template use File | Settings | File Templates.
 */
public class OrWebTableNavigator extends WebComponent implements JSONComponent, PropertyChangeListener {

    ResourceBundle res = CommonHelper.RESOURCE_RU;

    private WebButton fastRepBtn = createButton("fastRepNavi", res.getString("fastRep"));
    private WebButton consalBtn = createButton("consalNavi", res.getString("consal"));
    private WebButton addBtn = createButton("addNavi", res.getString("add"));
    private WebButton delBtn = createButton("delNavi", res.getString("delete"));
    private WebButton findBtn = createButton("findNavi", res.getString("find"));
    private WebButton filterBtn = createButton("filterNavi", res.getString("filter"), true);
    private WebButton copyRowsBtn = createButton("copyRowsNavi", res.getString("copyRows"));
    private WebButton yesManBtn = createButton("goRight", res.getString("goDown"));
    private WebButton upBtn = createButton("moveUp", res.getString("moveUp"));
    private WebButton downBtn = createButton("moveDown", res.getString("moveDown"));
    private WebButton firstPage = createButton("firstPage", res.getString("firstPage"));
    private WebButton lastPage = createButton("lastPage", res.getString("lastPage"));
    private WebButton nextPage = createButton("nextPage", res.getString("nextPage"));
    private WebButton backPage = createButton("backPage", res.getString("backPage"));
    private WebButton showDelBtn = createButton("showDel", res.getString("showDeleted"));
    
    private List<WebButton> actBtn = new ArrayList<WebButton>();
    private WebToolbar toolBar = new WebToolbar(null, Mode.RUNTIME, null, null);
    private WebToolbar naviPane = new WebToolbar(GridBagConstraints.EAST, null, Mode.RUNTIME, null, null);

    private WebSpinner setterCountRowPage;

    private WebLabel counterLabel = new WebLabel("", null, Mode.RUNTIME, null, null);
    private WebLabel infoPage = new WebLabel("__ страница из __", null, Mode.RUNTIME, null, null);
    /** The message label. */
    private WebLabel messageLabel = new WebLabel("", null, Mode.RUNTIME, null, null);

    private OrWebTable table;

    private int selRowIdx;
    private int rowCount;

    private TableAdapter tableAdapter;

    private Map filters;
    /** The filters obj. */
    List<FilterObject> filtersObj;
    Map<String, Boolean> activFilters;
    private List<WebButton> allButtons;
    private List<WebButton> processButtons = null;

    private boolean textChanged = false;
    private boolean enableChanged = false;

    private int iProc = 0;
    boolean[] indxBtn;

    private Kernel krn;

    /** The flr cls. */
    private KrnClass flrCls;

    private WebPopupMenu menu = null;
    /** Группа переключателей, объединяющая пункты меню выбора фильтра */
    WebButtonGroup itemGroup = new WebButtonGroup();

    /** Список Созданных пунктнов меню для фильтров, необходим для удобной их дезактивации */
    List<Object> itemFilter = new ArrayList<Object>();

    /** Элемент меню (для временного хранения) */
    final OrRadioButtonMenuItemFilter hideItem = new OrRadioButtonMenuItemFilter("", null);
    /** индекс разделителя. Разделители начинают нумероваться с единицы! */
    private int sepIndx = 0;
    /** Пункт меню для отмены действия фильтров */
    final OrFilterMenuItem itemCancel = new OrFilterMenuItem(res.getString("resetData"));

    /* метка для промежуточного хранения компонента */
    private WebLabel separator;
    private final Dimension btnSize = new Dimension(32, 32);
    private final Dimension btnPadding = new Dimension(0, 0);

    private boolean multi = false;

    private static String imgPath;

    public OrWebTableNavigator(OrWebTable table) {
    	super(null, Mode.RUNTIME, table.getFrame(), null);
        this.table = table;
        this.krn = table.getFrame().getKernel();
        imgPath = "../images/foto/";
        setterCountRowPage = new WebSpinner(this, 100, 10, 10, 50, null, Mode.RUNTIME, null, null);
        init();
    }

    public OrWebTable getTable() {
        return table;
    }

    public void setTableAdapter(TableAdapter tableAdapter) {
        this.tableAdapter = tableAdapter;
    }

    void init() {
        try {
            flrCls = krn.getClassByName("Filter");
        } catch (KrnException e) {
        	log.error(e, e);
        }

        PropertyValue pv = table.getPropertyValue(table.getProperties().getChild("view").getChild("navi").getChild("buttons")
                .getChild("naviPane"));
        if (!pv.booleanValue() || ConfigObject.instance(krn).getProperty(table.uuid, "countRowPage") == null) {
            pv = table.getPropertyValue(table.getProperties().getChild("pov").getChild("maxObjectCount"));
            setCountRowPage(pv.isNull() ? 50 : pv.intValue());
        } else {
            ConfigObject.instance(krn).getProperty(table.uuid, "countRowPage");
            setCountRowPage(Integer.parseInt(ConfigObject.instance(krn).getProperty(table.uuid, "countRowPage")));
        }

        counterLabel.setFocusable(false);
        counterLabel.setName("counter" + table.getId());
        messageLabel.setForeground(Color.red);
        allButtons = new ArrayList<WebButton>();
        allButtons.add(addBtn);
        allButtons.add(delBtn);
        allButtons.add(copyRowsBtn);
        allButtons.add(findBtn);
        allButtons.add(filterBtn);
        allButtons.add(fastRepBtn);
        allButtons.add(consalBtn);
        allButtons.add(yesManBtn);
        allButtons.add(upBtn);
        allButtons.add(downBtn);
        allButtons.add(firstPage);
        allButtons.add(backPage);
        allButtons.add(nextPage);
        allButtons.add(lastPage);
        allButtons.add(showDelBtn);

        // добавить кнопку "добавить строку"
        toolBar.add(addBtn);
        addNaviSeparator();
        // добавить кнопку "удалить строку"
        toolBar.add(delBtn);
        addNaviSeparator();
        // добавить кнопку "копировать строки"
        toolBar.add(copyRowsBtn);
        addNaviSeparator();
        // добавить кнопку "поиск"
        toolBar.add(findBtn);
        addNaviSeparator();
        // добавить кнопку "фильтры"
        toolBar.add(filterBtn);
        addNaviSeparator();
        // добавить кнопку "быстрый отчет"
        toolBar.add(fastRepBtn);
        addNaviSeparator();
        // добавить кнопку "объединить записи"
        toolBar.add(consalBtn);
        addNaviSeparator();
        // добавить кнопку "направление перехода"
        toolBar.add(yesManBtn);
        addNaviSeparator();
        // добавить кнопку "строку вверх"
        toolBar.add(upBtn);
        addNaviSeparator();
        // добавить кнопку "строку вниз"
        toolBar.add(downBtn);
        addNaviSeparator();

        toolBar.add(showDelBtn);
        showDelBtn.setIconPath("showDel.png");
        addNaviSeparator();

        naviPane.add(firstPage);
        naviPane.add(backPage);
        naviPane.add(infoPage);
        naviPane.add(nextPage);
        naviPane.add(lastPage);
        naviPane.add(setterCountRowPage);

        toolBar.add(naviPane);

        WebToolbar cbar = new WebToolbar(GridBagConstraints.EAST, null, Mode.RUNTIME, table.getFrame(), null);
        cbar.add(counterLabel);
        toolBar.add(cbar);

        setButtonsSize();
        setButtonsPadding();
        setCounterText();

        addBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // tableAdapter.addNewRow();
                table.addRow();
            }
        });
        addBtn.setId(table.getId() + "_add");
        addBtn.setActionId(table.getId() + "_add");
        ((WebFrame) table.getFrame()).getSession().addAction(addBtn);

        delBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tableAdapter.deleteRow();
            }
        });
        delBtn.setId(table.getId() + "_del");
        delBtn.setActionId(table.getId() + "_del");
        ((WebFrame) table.getFrame()).getSession().addAction(delBtn);

        showDelBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // tableAdapter.addNewRow();
                tableAdapter.showDeleted();
            }
        });
        showDelBtn.setId(table.getId() + "_showDel");
        showDelBtn.setActionId(table.getId() + "_showDel");
        ((WebFrame) table.getFrame()).getSession().addAction(showDelBtn);

        // добавить обработку нажатия на кнопку сброса фильтров
        itemCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                // отключение фильтров
                tableAdapter.cancelFilterAction();
                // чистка массива
                for (FilterObject filter : filtersObj) {
                    filter.setEnabled(false);
                }
                // выключение чекбоксов в меню
                for (Object item : itemFilter) {
                    if (item instanceof OrCheckBoxMenuItemFilter) {
                        ((OrCheckBoxMenuItemFilter) item).setSelected(false);
                    } else if (item instanceof OrRadioButtonMenuItemFilter) {
                        // включить скрытый элемент
                        hideItem.setSelected(true);
                        break;
                    }

                }
            }
        });
        fastRepBtn.setId(table.getId() + "_fastRep");
        fastRepBtn.setActionId(table.getId() + "_fastRep");
        consalBtn.setId(table.getId() + "_consal");
        consalBtn.setActionId(table.getId() + "_consal");
        findBtn.setId(table.getId() + "_find");
        findBtn.setActionId(table.getId() + "_find");
        filterBtn.setId(table.getId() + "_filter");
        filterBtn.setActionId(table.getId() + "_filter");
        copyRowsBtn.setId(table.getId() + "_copyRows");
        copyRowsBtn.setActionId(table.getId() + "_copyRows");
        yesManBtn.setId(table.getId() + "_yesMan");
        yesManBtn.setActionId(table.getId() + "_yesMan");
        upBtn.setId(table.getId() + "_moveUp");
        upBtn.setActionId(table.getId() + "_moveUp");
        downBtn.setId(table.getId() + "_moveDown");
        downBtn.setActionId(table.getId() + "_moveDown");
        firstPage.setId(table.getId() + "_firstPage");
        firstPage.setActionId(table.getId() + "_firstPage");
        lastPage.setId(table.getId() + "_lastPage");
        lastPage.setActionId(table.getId() + "_lastPage");
        nextPage.setId(table.getId() + "_nextPage");
        nextPage.setActionId(table.getId() + "_nextPage");
        backPage.setId(table.getId() + "_backPage");
        backPage.setActionId(table.getId() + "_backPage");
    }

    public void setButtonsVisible(PropertyNode propNode, boolean isVisible) {
        String propName = propNode.getName();
        if ("fastRepBtn".equals(propName)) {
            fastRepBtn.setVisible(isVisible);
            indxBtn[5] = isVisible;
        } else if ("consalBtn".equals(propName)) {
            consalBtn.setVisible(isVisible);
            indxBtn[6] = isVisible;
        } else if ("addBtn".equals(propName)) {
            addBtn.setVisible(isVisible);
            indxBtn[0] = isVisible;
        } else if ("delBtn".equals(propName)) {
            delBtn.setVisible(isVisible);
            indxBtn[1] = isVisible;
        } else if ("findBtn".equals(propName)) {
            findBtn.setVisible(false);
            indxBtn[3] = false;
        } else if ("copyRowsBtn".equals(propName)) {
            copyRowsBtn.setVisible(isVisible);
            indxBtn[2] = isVisible;
        } else if ("yesManBtn".equals(propName)) {
            yesManBtn.setVisible(isVisible);
            indxBtn[7] = isVisible;
        } else if ("filterBtn".equals(propName)) {
            filterBtn.setVisible(isVisible);
            indxBtn[4] = isVisible;
        } else if ("downBtn".equals(propName)) {
            downBtn.setVisible(isVisible);
            indxBtn[9] = isVisible;
        } else if ("upBtn".equals(propName)) {
            upBtn.setVisible(isVisible);
            indxBtn[8] = isVisible;
        } else if ("showDelBtn".equals(propName)) {
            showDelBtn.setVisible(isVisible);
            indxBtn[10] = isVisible;
        } else if ("naviPane".equals(propName)) {
            naviPane.setVisible(isVisible);
            indxBtn[11] = isVisible;
            counterLabel.setVisible(!isVisible);
        }
    }
    
    public void setButtonsTextInfo(PropertyNode propNode, String textInfo, String textInfoUID) {
        String propName = propNode.getName();
        if ("fastRepBtnProp".equals(propName)) {
            fastRepBtn.setTextInfo(textInfo);
            fastRepBtn.setTextInfoUID(textInfoUID);
        } else if ("consalBtnProp".equals(propName)) {
            consalBtn.setTextInfo(textInfo);
            consalBtn.setTextInfoUID(textInfoUID);
        } else if ("addBtnProp".equals(propName)) {
            addBtn.setTextInfo(textInfo);
            addBtn.setTextInfoUID(textInfoUID);
        } else if ("delBtnProp".equals(propName)) {
            delBtn.setTextInfo(textInfo);
            delBtn.setTextInfoUID(textInfoUID);
        } else if ("findBtnProp".equals(propName)) {
            findBtn.setTextInfo(textInfo);
            findBtn.setTextInfoUID(textInfoUID);
        } else if ("copyRowsBtnProp".equals(propName)) {
            copyRowsBtn.setTextInfo(textInfo);
            copyRowsBtn.setTextInfoUID(textInfoUID);
        } else if ("yesManBtnProp".equals(propName)) {
            yesManBtn.setTextInfo(textInfo);
            yesManBtn.setTextInfoUID(textInfoUID);
        } else if ("filterBtnProp".equals(propName)) {
            filterBtn.setTextInfo(textInfo);
            filterBtn.setTextInfoUID(textInfoUID);
        } else if ("downBtnProp".equals(propName)) {
            downBtn.setTextInfo(textInfo);
            downBtn.setTextInfoUID(textInfoUID);
        } else if ("upBtnProp".equals(propName)) {
            upBtn.setTextInfo(textInfo);
            upBtn.setTextInfoUID(textInfoUID);
        } else if ("showDelBtnProp".equals(propName)) {
        	showDelBtn.setTextInfo(textInfo);
        	showDelBtn.setTextInfoUID(textInfoUID);
        }
    }
    
    public void setButtonsIcon(PropertyNode propNode, byte[] iconBytes) {
        String propName = propNode.getName();
        if ("fastRepBtnProp".equals(propName)) {
            fastRepBtn.setIcon(iconBytes);
        } else if ("consalBtnProp".equals(propName)) {
            consalBtn.setIcon(iconBytes);
        } else if ("addBtnProp".equals(propName)) {
            addBtn.setIcon(iconBytes);
        } else if ("delBtnProp".equals(propName)) {
            delBtn.setIcon(iconBytes);
        } else if ("findBtnProp".equals(propName)) {
            findBtn.setIcon(iconBytes);
        } else if ("copyRowsBtnProp".equals(propName)) {
            copyRowsBtn.setIcon(iconBytes);
        } else if ("yesManBtnProp".equals(propName)) {
            yesManBtn.setIcon(iconBytes);
        } else if ("filterBtnProp".equals(propName)) {
            filterBtn.setIcon(iconBytes);
        } else if ("downBtnProp".equals(propName)) {
            downBtn.setIcon(iconBytes);
        } else if ("upBtnProp".equals(propName)) {
            upBtn.setIcon(iconBytes);
        } else if ("showDelBtnProp".equals(propName)) {
        	showDelBtn.setIcon(iconBytes);
        }
    }

    public WebButton getButtonByName(String name) {
        WebButton res = null;
        if ("fastRepBtn".equals(name)) {
            res = fastRepBtn;
        } else if ("consalBtn".equals(name)) {
            res = consalBtn;
        } else if ("addBtn".equals(name)) {
            res = addBtn;
        } else if ("delBtn".equals(name)) {
            res = delBtn;
        } else if ("findBtn".equals(name)) {
            res = findBtn;
        } else if ("copyRowsBtn".equals(name)) {
            res = copyRowsBtn;
        } else if ("yesManBtn".equals(name)) {
            res = yesManBtn;
        } else if ("filterBtn".equals(name)) {
            res = filterBtn;
        } else if ("downBtn".equals(name)) {
            res = downBtn;
        } else if ("upBtn".equals(name)) {
            res = upBtn;
        } else if ("showDelBtn".equals(name)) {
            res = showDelBtn;
        }
        return res;
    }

    public void actionPerformed(String btnName) {
        WebButton src = getButtonByName(btnName);
        if (tableAdapter != null) {
            if (src == addBtn) {
                tableAdapter.addNewRow();
            } else if (src == delBtn) {
                table.deleteRow();
            } else if (src == findBtn) {
            } else if (src == copyRowsBtn) {
                table.copyRows();
            } else if (src == yesManBtn) {
                table.yesMan();
            } else if (src == showDelBtn) {
            	boolean b = tableAdapter.showDeleted();
            	if (b)
            		showDelBtn.setIconPath("showDel.png");
            	else
            		showDelBtn.setIconPath("showDelUn.png");

            } else if (src == filterBtn) {
            }
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if ("rowSelected".equals(evt.getPropertyName())
                && ((Integer) evt.getOldValue()).intValue() != ((Integer) evt.getNewValue()).intValue()) {
            selRowIdx = (Integer) evt.getNewValue() + 1;
            setCounterText();
        }
        if ("rowCont".equals(evt.getPropertyName())
                && ((Integer) evt.getOldValue()).intValue() != ((Integer) evt.getNewValue()).intValue()) {
            rowCount = (Integer) evt.getNewValue() + 1;
            setCounterText();
        }
    }

    private void setCounterText() {
        if (rowCount == 0) {
            selRowIdx = 0;
        }  textChanged = true;
        counterLabel.setText(selRowIdx + " / " + rowCount);// + " ");
    }

    public void initFilterPopupMenu(List<Filter> items) {
        filters = new HashMap();
        if (items != null) {
            if (items.size() > 0) {
                for (int i = 0; i < items.size(); i++) {
                    Filter item = items.get(i);
                    filters.put(new Long(item.obj.id), item);
                }
            }
        }
    }

    private void setButtonsSize() {
        int count = allButtons.size();
        for (int i = 0; i < count; i++) {
            kz.tamur.comps.Utils.setAllSize(allButtons.get(i), btnSize);
        }
    }

    /**
     * Задать внутренние отступы кнопок
     */
    private void setButtonsPadding() {
        int count = allButtons.size();
        for (int i = 0; i < count; i++) {
            allButtons.get(i).setPadding(btnPadding);
        }
    }

    public void setEnabled(boolean enable, boolean delBtnAlso) {
        int count = allButtons.size();
        for (int i = 0; i < count; i++) {
            WebButton btn = allButtons.get(i);
            if (btn != findBtn && btn != filterBtn && actBtn.indexOf(btn) == -1) {
            	if (btn instanceof OrGuiComponent)
            		btn.setEnabled(enable);
            	else if (btn.getActionId() != null && (delBtnAlso || btn != delBtn)) {
            		btn.setEnabled(enable);
                    table.sendChangeProperty("ne", new JsonArray().add(new JsonObject().add("actionId", btn.getActionId()).add("e", enable ? "1" : "0")));
            	}
            }
        }
    }

    public void setDelEnabled(boolean enable) {
        if (delBtn != null) {
            delBtn.setEnabled(enable);
        }
    }

    public boolean isDelEnabled() {
        if (delBtn != null) {
            return delBtn.isEnabled();
        }
        return false;
    }
    
    private void changeTitles(ResourceBundle res) {
        fastRepBtn.setToolTipText(res.getString("fastRep"));
        consalBtn.setToolTipText(res.getString("consal"));
        addBtn.setToolTipText(res.getString("add"));
        delBtn.setToolTipText(res.getString("delete"));
        findBtn.setToolTipText(res.getString("find"));
        filterBtn.setToolTipText(res.getString("filter"));
        copyRowsBtn.setToolTipText(res.getString("copyRows"));
        yesManBtn.setToolTipText(res.getString("goDown"));
        downBtn.setToolTipText(res.getString("moveDown"));
        upBtn.setToolTipText(res.getString("moveUp"));
        showDelBtn.setToolTipText(res.getString("showDeleted"));
    }
    
    private void changeTextInfo() {
    	List<WebButton> buttons = Arrays.asList(fastRepBtn, consalBtn, addBtn, delBtn, findBtn, filterBtn, copyRowsBtn, yesManBtn, downBtn, upBtn, showDelBtn);
    	for (int i = 0; i < buttons.size(); i++) {
    		String textInfoUID = buttons.get(i).getTextInfoUID();
        	if (textInfoUID != null) {
        		String textInfo = frame.getString(textInfoUID);
        		if (textInfo != null && textInfo.length() > 0) {
        			buttons.get(i).changeTextInfo(textInfo);
        		}
        	}

    	}
    }

    public void setInterfaseLangId(long langId) {
        LangHelper.WebLangItem li = LangHelper.getLangById(langId, ((WebFrame) table.getFrame()).getSession().getConfigNumber());
        if (li != null) {
            res = "KZ".equals(li.code) ? CommonHelper.RESOURCE_KZ : CommonHelper.RESOURCE_RU;
            changeTitles(res);
            changeTextInfo();
        }

        if (filters.size() > 0) {
            long[] ids = new long[filters.values().size()];
            int i = 0;
            for (Iterator it = filters.values().iterator(); it.hasNext();) {
                ids[i++] = ((Filter) it.next()).obj.id;
            }
            try {
                Kernel krn = ((WebFrame) table.getFrame()).getSession().getKernel();
                KrnClass cls = krn.getClassByName("Filter");
                StringValue[] sv = krn.getStringValues(ids, cls.id, "title", langId, false, 0);
                for (i = 0; i < sv.length; ++i) {
                    if (sv[i].index == 0) {
                        Filter f = (Filter) filters.get(sv[i].objectId);
                        f.setTitle(sv[i].value, langId);
                    }
                }
            } catch (KrnException e) {
            	log.error(e, e);
            }
        }
    }

    protected static WebButton createButton(String icon, String toolTip) {
        return new WebButton(Constants.REL_PATH_IMG + icon + ".gif", toolTip, false, null, null, null, Mode.RUNTIME, null, null);
    }

    private WebButton createButton(String icon, String toolTip, boolean dropDown) {
        return new WebButton(Constants.REL_PATH_IMG + icon + ".gif", toolTip, dropDown, null, null, null, Mode.RUNTIME, null, null);
    }
    
    public JsonObject getToolbarHTML2(WebToolbar bar) {
        JsonObject tBar = new JsonObject();

        JsonObject style_ = new JsonObject();
        if (bar.getAlignment() == GridBagConstraints.EAST) {
            style_.add("float", "right");
        }

        tBar.add("st", style_);

        JsonArray buttons = new JsonArray();
        int index = 0;
        String nameC = "";
        String nameH = "";
        String idHideItem = null;
        int buttonNumber = 0;
        for (WebComponent comp : bar.getListChildren()) {
            if (comp instanceof WebButton) {
                WebButton b = (WebButton) comp;
                if (b.isVisible()) {
                    if (b == filterBtn) {
                        JsonObject filterBtn = new JsonObject();
                        filterBtn.add("button", b.putJSON(false));

                        if (menu != null) {
                            JsonObject menuJSON = new JsonObject();
                            JsonArray items = new JsonArray();
                            WebComponent[] children = menu.getChildren();
                            for (WebComponent c : children) {
                                nameC = table.getId() + "_C" + (++index);
                                nameH = table.getId() + "_H" + index;
                                if (c.equals(itemCancel)) {
                                    OrFilterMenuItem m = (OrFilterMenuItem) c;
                                    JsonObject item = m.putJSON( false);
                                                                        m.setId(nameH);
                                    m.setActionId(nameH);
                                    ((WebFrame) table.getAdapter().getFrame()).getSession().addAction(m);
                                    item.add("text", m.getText());
                                    items.add(item);
                                } else if (c instanceof OrCheckBoxMenuItemFilter) {
                                    OrCheckBoxMenuItemFilter m = (OrCheckBoxMenuItemFilter) c;
                                    JsonObject item = m.putJSON( false);
                                    m.setId(nameC);
                                    m.setChangeId(nameC);
                                    item.add("uuidA", nameH);
                                    item.add("name", table.getId() + "_ChF");
                                    item.add("text", m.getText());
                                    ((WebFrame) table.getAdapter().getFrame()).getSession().addChange(m);
                                    items.add(item);
                                } else if (c instanceof OrRadioButtonMenuItemFilter) {
                                    OrRadioButtonMenuItemFilter m = (OrRadioButtonMenuItemFilter) c;
                                    JsonObject item = m.putJSON( false);
                                    if (!m.isVisible()) {
                                        nameC = table.getId() + "_CHide";
                                        idHideItem = nameC;
                                    }
                                    m.setId(nameC);
                                    m.setChangeId(nameC);
                                    item.add("uuidA", nameH);
                                    item.add("text", m.getText());
                                    ((WebFrame) table.getAdapter().getFrame()).getSession().addChange(m);
                                    items.add(item);
                                } else if (c instanceof OrFilterMenuItem) {
                                	OrFilterMenuItem m = (OrFilterMenuItem) c;
                                    JsonObject item = m.putJSON( false);
                                    m.setId(nameH);
                                    m.setActionId(nameH);
                                    ((WebFrame) table.getAdapter().getFrame()).getSession().addAction(m);
                                    item.add("text", m.getText());
                                    items.add(item);
                                }
                            }
                            menuJSON.add("items", items);
                            filterBtn.add("menu", menuJSON);
                        }
                        buttons.add(filterBtn.add("index", buttonNumber++));
                    } else {
                        buttons.add(b.putJSON(false).add("index", buttonNumber++));
                    }
                }
            } else if (comp instanceof WebLabel || comp instanceof WebSpinner) {
                buttons.add(comp.putJSON( false).add("index", buttonNumber++));
            }
        }
        tBar.add("buttons", buttons);
        int i = 0;
        for (WebComponent comp : bar.getListChildren()) {
            if (comp instanceof WebToolbar && !(comp instanceof WebSpinner)) {
                if (((WebToolbar) comp).isVisible()) {
                    i++;
                    tBar.add("toolBar_" + i, getToolbarHTML2((WebToolbar) comp));
                }
            }

        }
        return tBar;
    }

    public void setSelectedRow(int row) {
        selRowIdx = row;
        setCounterText();
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
        setCounterText();
    }

    public void setMessage(String msg) {
        messageLabel.setText(msg);
    }

    /**
     * Установить значение количества строк на страницу вывода информации.
     * 
     * @param value
     *            новое количество строк
     */
    public void setCountRowPage(int value) {
        setterCountRowPage.setValue(value);
    }

    public void saveCountRowPage(int value) {
        ConfigObject.instance(krn).saveProperty(table.uuid, "countRowPage", value + "");
    }

    /**
     * Adds the action.
     * 
     * @param action
     *            the action
     */
    public void addAction(ProcessRecordAction action) {
        if (processButtons == null) {
            processButtons = new ArrayList<WebButton>();
        }
     //   WebButton button = new WebButton(WebController.PATH_IMG + action.getIcon(), action.getName(), action.getId());
        WebButton button = null;
        button = new WebButton(imgPath + action.getIcon(), action.getName(), action.getId(), null, Mode.RUNTIME, frame, null);
        button.setEnabled(action.isEnabled());
        button.setOpaque(false);
        button.setToolTipText(action.getName());
        kz.tamur.comps.Utils.setAllSize(button, btnSize);
        button.setPadding(btnPadding);
        toolBar.add(button);
        actBtn.add(button);
        processButtons.add(button);
        addNaviSeparator();
        indxBtn[table.countBtn + iProc++] = true;

    }

    /**
     * Создаёт PopUp меню выбора и установки фильтров Если объект существует, то
     * повторной инициализации не происходит.
     * 
     * @param multi
     *            мультивыбор?
     */
    protected void initMenu(boolean multi) {
        this.multi = multi;
        if (menu == null && filtersObj != null) { // если объекта не существует
            // запомнить количество фильтров
            final int count = filtersObj.size();
            // меню
            menu = new WebPopupMenu(null, mode, frame, null);
            menu.setFont(Utils.getDefaultFont());

            for (FilterObject filterObj : filtersObj) {
                if (multi) {
                    OrCheckBoxMenuItemFilter item = new OrCheckBoxMenuItemFilter(filterObj.getTitle(), filterObj);
                    menu.add(item);
                    itemFilter.add(item);
                } else {
                    OrRadioButtonMenuItemFilter itemRadio = new OrRadioButtonMenuItemFilter(filterObj.getTitle(), filterObj);
                    menu.add(itemRadio);
                    // добавить пункт в группу
                    itemGroup.add(itemRadio);
                    // добавить пункт в список для удобного доступа
                    itemFilter.add(itemRadio);
                }
            }

            if (!multi) {
                // добавить скрытый компоенент, который используется при необходимости убрать выделение во всех радиобаттонов
                menu.add(hideItem);
                hideItem.setVisible(false);
                itemGroup.add(hideItem);
            }
            // добавить в меню разделитель
            // добавить в меню элемент
            menu.add(itemCancel);
        }
    }

    /**
     * Inits the filter popup menu.
     * 
     * @param items
     *            the items
     */
    public void initFilterPopupMenu(FilterMenuItem[] items) {
        filters = new HashMap();
        if (items != null) {
            if (items.length > 0) {
                ArrayList<FilterMenuItem> arr = new ArrayList<FilterMenuItem>();
                for (int i = 0; i < items.length; i++)
                    arr.add(items[i]);

                java.util.Collections.sort(arr);

                // задание элементов меню (по количеству фильтров)
                // собрать список Фильтров-объектов
                filtersObj = new ArrayList<FilterObject>();

                for (int i = 0; i < arr.size(); i++) {
                    filtersObj.add(new FilterObject(arr.get(i).getText(), arr.get(i).filter));
                    items[i] = arr.get(i);
                }
            }
        }
    }

    /**
     * Добавление на панель инструментов визуального разделителя для обращения к
     * разделителю ему задаётся свойство NAME
     * 
     * имя задаётся по шаблону: <i>separator</i>+<i>$</i> <i>$</i> - глобальный
     * счётчик разделителей для задания уникальности имён.
     */
    private void addNaviSeparator() {
      //  toolBar.add(separator = new WebLabel());
        //separator.setVisible(false);
        //separator.setIconName("separator.png");
        //separator.setName("separator" + (++sepIndx));
    }

    class OrCheckBoxMenuItemFilter extends WebMenuItem {

        private FilterObject filter;
        private long filterId;

        public OrCheckBoxMenuItemFilter(String title, FilterObject filter) {
            super(title, title, null, Mode.RUNTIME, OrWebTableNavigator.this.frame, null);
            this.filter = filter;
            this.filterId = (filter != null) ? filter.getFilter().obj.id : 0;
            setFont(Utils.getDefaultFont());
            setOpaque(true);
            addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    AbstractButton aButton = (AbstractButton) event.getSource();
                    ((OrCheckBoxMenuItemFilter) event.getSource()).getFilter().setEnabled(aButton.getModel().isSelected());
                    // сформировать список активных фильтров
                    List<Filter> filters = new ArrayList<Filter>();
                    tableAdapter.cancelFilterAction();
                    for (FilterObject filter : filtersObj) {
                        if (filter.isEnabled()) {
                            filters.add(filter.getFilter());
                        }
                    }
                    try {
                        // активировать фильтры
                        tableAdapter.applyFilters(filters);
                    } catch (KrnException e) {
                    	log.error(e, e);
                    }
                }
            });
            addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    ((OrCheckBoxMenuItemFilter) e.getSource()).getFilter().setEnabled("1".equals(changeValue));
                    // сформировать список активных фильтров
                    List<Filter> filters = new ArrayList<Filter>();
                    tableAdapter.cancelFilterAction();
                    for (FilterObject filter : filtersObj) {
                        if (filter.isEnabled()) {
                            filters.add(filter.getFilter());
                        }
                    }
                    try {
                        // активировать фильтры
                        tableAdapter.applyFilters(filters);
                    } catch (KrnException e1) {
                    	log.error(e1, e1);
                    }
                }
            });
        }

        public long getFilterId() {
            return filterId;
        }

        /**
         * Получить фильтр-объект
         * 
         * @return
         */
        public FilterObject getFilter() {
            return filter;
        }

        /**
         * Задать фильтр-объект
         * 
         * @param filter
         */
        public void setFilter(FilterObject filter) {
            this.filter = filter;
        }
    }

    /**
     * Пункт меню - переключатель с хранением фильтра ему принадлежащим
     * 
     * @author Sergey Lebedev
     * 
     */
    class OrRadioButtonMenuItemFilter extends WebMenuItem {

        private FilterObject filter;
        private long filterId;

        public OrRadioButtonMenuItemFilter(String title, FilterObject filter) {
            super(title, title, null, Mode.RUNTIME, OrWebTableNavigator.this.frame, null);
            this.filter = filter;
            this.filterId = (filter != null) ? filter.getFilter().obj.id : 0;
            setFont(Utils.getDefaultFont());
            setOpaque(true);
            addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    AbstractButton aButton = (AbstractButton) event.getSource();
                    if (aButton.getModel().isSelected()) {
                        // сформировать список активных фильтров
                        List<Filter> filters = new ArrayList<Filter>();
                        filters.add(((OrRadioButtonMenuItemFilter) event.getSource()).getFilter().getFilter());
                        try {
                            // активировать фильтр
                            tableAdapter.applyFilters(filters);
                        } catch (KrnException e) {
                        	log.error(e, e);
                        }
                    }
                }
            });
            addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    if ("1".equals(changeValue)) {
                        // сформировать список активных фильтров
                        List<Filter> filters = new ArrayList<Filter>();
                        filters.add(((OrRadioButtonMenuItemFilter) e.getSource()).getFilter().getFilter());
                        try {
                            // активировать фильтр
                            tableAdapter.applyFilters(filters);
                        } catch (KrnException e1) {
                        	log.error(e1, e1);
                        }
                    }
                }
            });
        }

        /**
         * Получить фильтр-объект
         * 
         * @return
         */
        public FilterObject getFilter() {
            return filter;
        }

        /**
         * Задать фильтр-объект
         * 
         * @param filter
         */
        public void setFilter(FilterObject filter) {
            this.filter = filter;
        }

        public long getFilterId() {
            return filterId;
        }
    }

    class OrFilterMenuItem extends WebMenuItem {

        /**
         * Создание нового пункта меню.
         * 
         * @param s
         *            заголовок пункта меню
         */
        public OrFilterMenuItem(String s) {
            super(s, s, null, Mode.RUNTIME, OrWebTableNavigator.this.frame, null);
            setFont(Utils.getDefaultFont());
            setOpaque(true);
        }

        public void setText(String text) {
            final int beg = text.indexOf('&');
            if (beg > -1) {
                final char m = text.charAt(beg + 1);
                super.setText(text.substring(0, beg) + text.substring(beg + 2));
            } else {
                super.setText(text);
            }
        }
    }

    public void setSeparator(int[] index, boolean[] indxBtn) {
        // защита от ненужного срабатывания
        if (indxBtn == null || indxBtn.length == 0) {
            return;
        }

        // очистка
        if (index == null || index.length == 0) {
            WebComponent[] cList = toolBar.getComponents();
            for (int i = 0; i < cList.length; ++i) {
                if (cList[i] instanceof WebLabel) {
                    if (((WebLabel) cList[i]).getName().contains("separator")) {
                        ((WebLabel) cList[i]).setVisible(false);
                    }
                }
            }
            return;
        }

        /**
         * конечный массив индексов разделителей для активации именно в этом
         * массиве окажутся реальные индексы разделителей по которым можно
         * отследить существующий объект
         */
        int[] indxSeparator = new int[index.length];

        // номер разделителя в массиве
        int iS = 0;
        // просчёт реальный индексов разделителей
        for (int i = 0; i < index.length; ++i) {
            /* так как разделителей может быть указано больше чем поместиться на панели, необходима эта проверка */
            if (iS == indxSeparator.length) {
                break;
            }
            /* index[i] - позиция кнопки после которой нужно активировать разделитель просчитать индекс разделителя для нужной кнопки */
            int j;
            int k = 0;
            for (j = 0; j < indxBtn.length; ++j) {
                // если кнопка выводиться на панель
                if (indxBtn[j]) {
                    // увеличить индекс выводимой кнопки и сравнить его с
                    // позицией разделителя
                    if (++k == index[i]) {
                        k = -1;
                        break;
                    }
                }
            }
            // если был сделан принудительный выход из цикла, записать индекс
            // выхода из цикла как индекс сепаратора +1
            indxSeparator[iS] = k == -1 ? j + 1 : -1;
            ++iS;
        }

        if (index == null || index.length == 0) {
            WebComponent[] cList = toolBar.getComponents();
            for (int i = 0; i < cList.length; ++i) {
                if (cList[i] instanceof WebLabel) {
                    if (((WebLabel) cList[i]).getName().contains("separator")) {
                        ((WebLabel) cList[i]).setVisible(false);
                    }
                }
            }
            return;
        }

        // активация разделителей
        if (indxSeparator.length != 0) {
            WebComponent[] cList = toolBar.getComponents();
            // очистка
            for (int i = 0; i < cList.length; ++i) {
                if (cList[i] instanceof WebLabel) {
                    if (((WebLabel) cList[i]).getName().contains("separator")) {
                        ((WebLabel) cList[i]).setVisible(false);
                    }
                }
            }
            // прорисовать разделители
            for (int i = 0; i < cList.length; ++i) {
                // если сомпонент - метка
                if (cList[i] instanceof WebLabel) {
                    // сравнивнить свойство NAME с предполагаемым именем
                    // выводимого раделителя
                    for (int j = 0; j < indxSeparator.length; ++j) {
                        // если имена совпадают - вывести на панель
                        if (indxSeparator[j] != -1 && ((WebLabel) cList[i]).getName().equals("separator" + indxSeparator[j])) {
                            ((WebLabel) cList[i]).setVisible(true);
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * @return the processButtons
     */
    public List<WebButton> getProcessButtons() {
        return processButtons;
    }

    public JsonObject putJSON(boolean isSend) {
        JsonObject obj = addJSON("h" + table.getId());
        JsonObject property = new JsonObject();
        property.add("toolBar", getToolbarHTML2(toolBar));
        property.add("multi", multi);
        if (property.size() > 0) {
            obj.add("pr", property);
        }
        sendChange(obj, isSend);
        return obj;
    }
}
