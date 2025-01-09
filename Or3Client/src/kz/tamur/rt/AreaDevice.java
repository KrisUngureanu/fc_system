package kz.tamur.rt;

/**
 * Класс для хранения положения окна и его размеров
 */
public  class AreaDevice {

    public int width;
    public int height;
    public int x;
    public int y;

    /**
     * Создание нового экземпляра класса
     * 
     * @param width
     *            ширина окна
     * @param height
     *            высота окна
     * @param x
     *            положение по X
     * @param y
     *            положение по Y
     */
    public AreaDevice(int width, int height, int x, int y) {
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
    }
}
