package kz.tamur.lang;

import java.security.SecureRandom;

/**
 * Created by IntelliJ IDEA.
 * Date: 08.06.2005
 * Time: 10:23:40
 *
 * @author berik
 */
public class MathOp {

    /**
     * Возвращает целое число, ближайшее к вещественному числу <code>n</code> (округляет <code>n</code>).
     *
     * @param n округляемое число
     * @return результат округления, целое
     */
    public Long round(Number n) {
        long res = Math.round(n.doubleValue());
        return new Long(res);
    }

    /**
     * Возвращает вещественное число, ближайшее к вещественному числу <code>n</code>.
     * Округляется дробная часть. Количество остающихся после запятой знаков определяется параметром <code>count</code>
     * Пример:
     * <pre>
     * round(3.4, 2) = 3.4
     * round(3.4851, 2) = 3.49
     * </pre>
     * @param n округляемое число
     * @param count количество знаков после запятой, которые необходимо оставить
     * @return результат округления, вещественное
     */
    public Double round(Number n, Number count) {
        double power = Math.pow((double) 10, count.doubleValue());
        long res = Math.round(n.doubleValue()*power);
        return ((double)res)/power;
    }
    
    /**
     * Возвращает ближайшее к числу <code>n</code> справа число с нулевой дробной частью (например от числа 3.4 результом будет 4.0).
     *
     * @param n округляемое число
     * @return результат округления
     */
    public Double ceil(Number n) {
        double res = Math.ceil(n.doubleValue());
        return res;
    }

    /**
     * Возвращает ближайшее к числу <code>n</code> слева число с нулевой дробной частью (например от числа 3.4 результом будет 3.0).
     *
     * @param n округляемое число
     * @return результат округления
     */
    public Double floor(Number n) {
        double res = Math.floor(n.doubleValue());
        return res;
    }

    /**
     * Генерация псевдослучайного числа размерностью(количеством цифр) <pre>n</pre>.
     *
     * @param n размерность числа
     * @return полученное число.
     */
    public static Long random(Number n) {
        double power = random();
        double res = Math.floor(n.doubleValue()*power);
        return (long)res;
    }
    
    public static double random() {
    	SecureRandom rnd = new SecureRandom();
    	byte sbs[] = new byte[32];
    	rnd.nextBytes(sbs);
    	
    	long l = 0;
        for (int i = 0; i < sbs.length; i++)
            l = (l << 4) + (sbs[i] & 0xFF);

        return ((double)(l & Long.MAX_VALUE)) / Long.MAX_VALUE;
    }
}
