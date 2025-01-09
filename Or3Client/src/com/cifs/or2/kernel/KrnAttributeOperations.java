package com.cifs.or2.kernel;

import java.util.List;

public interface KrnAttributeOperations {
    KrnClass getCls(KrnAttribute atter);
    KrnClass getType(KrnAttribute atter);
    List<KrnAttribute> getRevAttributes(KrnAttribute atter);
}
