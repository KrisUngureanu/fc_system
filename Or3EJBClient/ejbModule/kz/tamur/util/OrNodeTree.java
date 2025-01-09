package kz.tamur.util;

import java.util.ArrayList;
import java.util.List;

import com.cifs.or2.kernel.KrnObject;

/**
 * The Class OrNodeTree.
 * Класс для построения деревьев
 * Реализует узел дерева
 * 
 * @author Sergey Lebedev
 */
public class OrNodeTree {

    /** Идентификатор узла */
    private long id;

    /** Указатель на предыдущий узел. Предок может быть только один! */
    private OrNodeTree previous;

    /** Список указателей на потомков узла */
    private List<OrNodeTree> nexts;

    /** Объект-Значение узла */
    private Object value;

    /** Был ли добавлен в ветку с узлом лист дерева? */
    private boolean isAddedLeaf = false;

    /** Является ли узел первоуровневым */
    private boolean isNodeMenu;

    /**
     * Конструктор узла
     * переменные инициализируются значениями по умолчанию
     */
    public OrNodeTree() {
        id = -1;
        previous = null;
        nexts = null;
        value = null;
        isNodeMenu = false;
    }

    /**
     * Создание нового or node tree.
     * 
     * @param id
     *            идентификатор узла
     * @param previous
     *            Указатель на предыдущий узел
     * @param nexts
     *            Список указателей на потомков узла
     * @param value
     *            Объект-Значение узла
     */
    public OrNodeTree(long id, OrNodeTree previous, List<OrNodeTree> nexts, Object value) {
        this(id, previous, nexts, value, false);
    }

    public OrNodeTree(long id, OrNodeTree previous, List<OrNodeTree> nexts, Object value, boolean isNodeMenu) {
        super();
        this.id = id;
        this.previous = previous;
        this.nexts = nexts;
        this.value = value;
        this.isNodeMenu = isNodeMenu;
    }

    /**
     * Получить идентификатор узла.
     * 
     * @return идентификатор узла
     */
    public long getId() {
        return id;
    }

    /**
     * Задать идентификатор узла
     * 
     * @param id
     *            новый идентификатор узла
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Получить предыдущий узел
     * 
     * @return предыдущий узел
     */
    public OrNodeTree getPrevious() {
        return previous;
    }

    /**
     * Задать предыдущий узел
     * 
     * @param previous
     *            новый
     */
    public void setPrevious(OrNodeTree previous) {
        this.previous = previous;
    }

    /**
     * Получить список потомков узла.
     * 
     * @return список потомков узла
     */
    public List<OrNodeTree> getNext() {
        return nexts;
    }

    /**
     * Установить список потомков узла.
     * 
     * @param nexts
     *            список потомков узла
     */
    public void setNext(List<OrNodeTree> nexts) {
        this.nexts = nexts;
    }

    /**
     * добавить потомка
     * 
     * @param child
     *            потомок OrNodeTree
     */
    public void addChild(OrNodeTree child) {
        if (nexts == null) {
            nexts = new ArrayList<OrNodeTree>();
        }
        nexts.add(child);
    }

    /**
     * Получить значение узла.
     * 
     * @return the value
     */
    public Object getValue() {
        return value;
    }

    /**
     * Установить value.
     * 
     * @param value
     *            the value to set
     */
    public void setValue(Object value) {
        this.value = value;
    }

    
    @Override
    public String toString() {
        return "OrNodeTree [id=" + id + " value=" + value + "]";
    }

    /**
     * Проверяет, является ли узел конечным(листом)
     * 
     * @return true, если узел - лист
     */
    public boolean isLeaf() {
        return nexts == null;
    }

    /**
     * Проверяет, является ли объект значением одного из прямых потомков узла.
     * 
     * @param value
     *            объект который ищется
     * @return true, найден
     */
    public boolean isInTheNexts(Object value) {
        if (nexts != null && !nexts.isEmpty()) {
            if (value instanceof KrnObject) {
                for (OrNodeTree obj : nexts) {
                    if (obj.value instanceof KrnObject) {
                        if (((KrnObject) obj.value).equals((KrnObject) value)) {
                            return true;
                        }
                    } else if (obj.value instanceof FilterObject) {
                        if (((FilterObject) obj.getValue()).getFilter().obj.equals((KrnObject) value)) {
                            return true;
                        }
                    }
                }
            } else {
                for (OrNodeTree obj : nexts) {
                    if (obj.value == value) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Был ли добавлен лист в ветку дерева?
     * 
     * @return <code>true</code> если да
     */
    public boolean isAddedLeaf() {
        return isAddedLeaf;
    }

    /**
     * Установить отметку о том что в ветку с узлом был добавлен лист.
     * 
     * @param isAddedLeaf
     *            флаг добавления листа
     */
    public void setAddedLeaf(boolean isAddedLeaf) {
        this.isAddedLeaf = isAddedLeaf;
    }

    /**
     * Является ли узел первоуровневым.
     * 
     * @return <code>true</code> если узел принадлежит первому уровню
     */
    public boolean isNodeMenu() {
        return isNodeMenu;
    }

    /**
     * Отметка узла как узла первого уровня
     * 
     * @param isNodeMenu
     *            флаг уровня узла
     */
    public void setNodeMenu(boolean isNodeMenu) {
        this.isNodeMenu = isNodeMenu;
    }
}
