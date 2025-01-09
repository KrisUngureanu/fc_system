package kz.tamur.lang;

import com.cifs.or2.kernel.KrnMethod;

/**
 * Created by IntelliJ IDEA.
 * User: Valeri
 * Date: 17.12.2006
 * Time: 15:19:41
 * To change this template use File | Settings | File Templates.
 */
    public abstract class MethodWrp {

        protected KrnMethod mtd;

        protected MethodWrp(KrnMethod mtd) {
            this.mtd = mtd;
        }

        public KrnMethod getKrnMethod() {
            return mtd;
        }

        public boolean isClassMethod() {
            return mtd.isClassMethod;
        }

        public String getId() {
            return mtd.uid;
        }

        public String getName() {
            return mtd.name;
        }
}
