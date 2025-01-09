package kz.tamur.guidesigner.serviceControl;

import java.util.Enumeration;

import kz.tamur.util.AbstractDesignerTreeNode;
import kz.tamur.util.ServiceControlNode;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnObject;

/**
 * Узел дерева просмотра структуры объекта в системе.
 * 
 * @author Lebedev Sergey
 */
public class StructureViewNode extends AbstractDesignerTreeNode {
    /** Тип узла - Процесс. */
    private boolean isService = false;

    /** Тип узла - Интерфейс. */
    private boolean isInterface = false;

    /** Тип узла - Фильтр. */
    private boolean isFilter = false;

    /** Тип узла - Отчёт. */
    private boolean isReport = false;

    /**
     * Создание нового service control node.
     * 
     * @param nodeObj
     *            the node obj
     * @param title
     *            the title
     * @param langId
     *            the lang id
     */
    public StructureViewNode(KrnObject nodeObj, String title, long langId) {
        krnObj = nodeObj;
        isLoaded = false;
        this.title = title;
        this.langId = langId;
        long id = krnObj.classId;
        isService = id == Kernel.SC_PROCESS_DEF.id;
        isInterface = id == Kernel.SC_UI.id;
        isFilter = id == Kernel.SC_FILTER.id;
        isReport = id == Kernel.SC_REPORT_PRINTER.id;

    }

    /**
     * Получить title.
     * 
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Установить title.
     * 
     * @param title
     *            the new title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Проверяет, является ли узел отчётом.
     * 
     * @return <code>true</code> если узел - Отчёт
     */
    public boolean isReport() {
        return isReport;
    }

    /**
     * Проверяет, является ли узел процессом.
     * 
     * @return <code>true</code> если узел - Процесс
     */
    public boolean isService() {
        return isService;
    }

    /**
     * Проверяет, является ли узел интерфейсом.
     * 
     * @return <code>true</code> если узел - Интерфейс
     */
    public boolean isInterface() {
        return isInterface;
    }

    /**
     * Проверяет, является ли узел фильтром.
     * 
     * @return <code>true</code> если узел - Фильтр
     */
    public boolean isFilter() {
        return isFilter;
    }

    @Override
    protected void load() {
    }

    /**
     * Поиск значения узла среди загруженных элементов дерева.
     * 
     * @param obj
     *            the obj
     * @return true, в случае успеха
     */
    public boolean findLoadedChildValue(StructureViewNode obj) {
        if (obj != null) {
            if (this.equals(obj)) {
                if (getChildCount() != 0) {
                    Enumeration<StructureViewNode> childElements = children();
                    while (childElements.hasMoreElements()) {
                        StructureViewNode hn = childElements.nextElement();
                        if (hn.findLoadedChildValue(obj)) {
                            return true;
                        }
                    }
                }
            } else {
                return true;
            }
        }
        return false;
    }
}
