package kz.tamur.admin.clsbrow;

import com.cifs.or2.util.MultiMap;
import com.cifs.or2.kernel.KrnException;

public interface PropertyField {
    public boolean isModified();
    public void save(MultiMap deletions) throws KrnException;
    public void restore();
    public String toString();
    public void deleteValue();
    public void doClickSelBtn();
}