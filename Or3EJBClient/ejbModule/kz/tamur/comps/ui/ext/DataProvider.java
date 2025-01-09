package kz.tamur.comps.ui.ext;

/**
 * The Interface DataProvider.
 *
 * @param <T> the generic type
 * @author Lebedev Sergey
 */
public interface DataProvider<T> {
    
    /**
     * Provide.
     *
     * @return t
     */
    public T provide();
}