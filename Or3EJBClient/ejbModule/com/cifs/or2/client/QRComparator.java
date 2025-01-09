package com.cifs.or2.client;

import java.util.Comparator;

/**
 * Created by IntelliJ IDEA.
 * User: Администратор
 * Date: 20.02.2004
 * Time: 16:03:06
 * To change this template use Options | File Templates.
 */
public class QRComparator implements Comparator {
        int direction;  // 1 - сортировка по возрастанию, 2 - по убыванию
        int sortColumn;

        public QRComparator(int direction, int col) {
            this.direction = direction;
            this.sortColumn = col;
        }

        public void changeDirection() {
            direction *= -1;
        }

        public int compare(Object o, Object o1) {
            if (o == null && o1 != null) return -direction;
            if (o1 == null && o != null) return direction;
            if (o instanceof QRAttr && o1 instanceof QRAttr) {
                QRAttr q1 = (QRAttr) o;
                QRAttr q2 = (QRAttr) o1;
                if (sortColumn == 0) {
                    if (q1.getName() != null)
                        return q1.getName().compareTo(q2.getName()) * direction;
                    else
                        return -direction;
                } else if (sortColumn == 1) {
                    if (q1.getPath() != null)
                        return q1.getPath().compareTo(q2.getPath()) * direction;
                    else
                        return -direction;
                }

            }
            return 0;
        }
    }