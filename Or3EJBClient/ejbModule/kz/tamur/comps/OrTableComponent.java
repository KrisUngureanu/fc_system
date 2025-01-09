package kz.tamur.comps;

public interface OrTableComponent extends OrGuiContainer {
	OrColumnComponent getColumnAt(int col);
    int getRowCount();
    int getColumnCount();
}
