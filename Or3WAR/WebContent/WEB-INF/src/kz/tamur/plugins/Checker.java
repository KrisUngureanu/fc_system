package kz.tamur.plugins;

import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cifs.or2.server.Session;

import kz.tamur.comps.Constants;
import kz.tamur.rt.orlang.AbstractClientPlugin;

/**
 * Created by IntelliJ IDEA.
 * User: Кайржан
 * Date: 01.07.2005
 * Time: 17:54:36
 * To change this template use File | Settings | File Templates.
 */
public class Checker extends AbstractClientPlugin {

	//Замена ошибочных латинских букв на русские
	private String err_en_str="ABCHPY";
	private char[] translit_err_en_ru="АВСНРУ".toCharArray();
	//Замена ошибочных русских букв на латинские
	private String err_ru_str="АВСНРУ";
	private char[] translit_err_ru_en="ABCHPY".toCharArray();
	//Замена казахских букв на русские
	private String translit_kz_str="ӘІҢҒҮҰҚӨҺ";
	private char[] translit_kz="АИНГУУКОХ".toCharArray();
	//Замена сочетаний латинских букв на русские
	private String[] en_m={"YO","JO","ZH","KH","TC","CH","SHC","SH","YU","JU","YA","JA"};
	private String[] translit_ru_str_m={"Ё","Ё","Ж","Х","Ц","Ч","Щ","Ш","Ю","Ю","Я","Я"};
	//Замена латинских букв на русские
	private String en_str="ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private char[] translit_en_ru="АБЦДЕФГХИЙКЛМНОПКРСТУВВХЫЗ".toCharArray();
    private static final boolean NEW_CHECK_NAME = "true".equals(System.getProperty("newCheckName"));
    /**
     * Проверяет валидность ОКПО (общий классификатор предприятий и организаций)
     * @param okpo ОКПО
     * @return true - если валидна, false - иначе
     */
	public boolean checkOKPO(String okpo) {
        try {
            if (okpo == null)
                return false;
            int len = okpo.length();
            if (len != 8 && len != 12)
                return false;
            int count = 8;
            int[] i = new int[count];
            for (int j = 0; j < count; j++) {
                i[j] = Character.digit(okpo.charAt(j), 10);
            }
            int sum = 0;
            for (int j = 0; j < count - 1; j++) {
                sum += i[j] * (j + 1);
            }
            sum %= 11;
            if (sum == 10) {
                sum = 0;
                for (int j = 0; j < count - 1; j++) {
                    sum += i[j] * (j + 3);
                }
                sum %= 11;
                if (sum == 10) {
                    sum = 0;
                }
            }
            return sum == i[count - 1];
        } catch (Exception e) {
            return false;
        }
    }
	/**
	 * Проверяет валидность РНН
	 * @param rnn РНН
	 * @return true - если валидна, false - иначе
	 */
    public boolean checkRNN(String rnn) {
        try {
            if (rnn == null)
                return false;
            boolean rightRNN = true;
            if (rnn.length() < 12)
              rightRNN = false;
            else if (rnn.length() > 12)
              rightRNN = false;
            else {
              int controlValue = Character.digit(rnn.charAt(11), 10);
              int coefficient = 10; int k = 0;
              while (coefficient >= 10) {
                k++; int sum = 0;
                if (k == 10) {
                  rightRNN = false;
                  break;
                }
                for (int i = 0, n = k; i < 11; i++, n++) {
                  if (n > 10) n = 1;
                  sum += Character.digit(rnn.charAt(i), 10) * n;
                }
                int r = sum / 11;
                coefficient = sum - r*11;
              }
              if (rightRNN)
                rightRNN = (coefficient == controlValue);
            }
            return rightRNN;
        } catch (Exception e) {
            return false;
        }
    }
    /**
     * Проверяет валидность РНН физ.лица
     * @param str РНН
     * @return true - если валидна, false - иначе
     */
    public boolean checkRNNFiz(String str) {
        if (str == null)
            return false;
        if (checkRNN(str)) {
            if (str.charAt(4)=='0')
                return false;
            else
                return true;
        } else
            return false;
    }
    /**
     * Проверяет валидность РНН
     * @param str РНН
     * @return true - если валидна, false - иначе
     */
    public boolean checkRNNUl(String str) {
        if (str == null)
            return false;
        if (checkRNN(str)) {
            if (str.charAt(4)=='0')
                return true;
            else
                return false;
        } else
            return false;
    }
    /**
     * Проверяет валидность ИН
     * @param str ИН
     * @return true - если валидна, false - иначе
     */
    public boolean checkIN(String str) {
        try {
            if (str == null)
                return false;
            if (str.length() != 12)
                return false;
            if (str.equals("000000000000"))
                return false;
            int checkNumber = (new Integer(str.substring(11, 12))).intValue();
            int controlSum = calculate_Checking_numeral(str);
            if (controlSum != -1) {
                if (checkNumber != controlSum)
                    return false;
            } else {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
        private int getControlSum(String code) { //TODO
            int a[] = getNumeric(code);
            //проверка А
            int number = a[0] + 2 * a[1] + 3 * a[2] + 4 * a[3] + 5 * a[4] + 6 * a[5] +
                    7 * a[6] + 8 * a[7] + 9 * a[8] + 10 * a[9] + 11 * a[10];
            number %= 11;
            //конец проверка А
            //если контрольное число равна 10 то идет проверка В
            //проверка В
            if(number == 10) {
                number = 3 * a[0] + 4 * a[1] + 5 * a[2] + 6 * a[3] + 7 * a[4] + 8 * a[5] +
                    9 * a[6] + 10 * a[7] + 11 * a[8] + 1 * a[9] + 2 * a[10];
                number %= 11;
            }

            //конец проверка В
            return number;
        }
        /**
         * Проверяет валидность БИН
         * @param str БИН
         * @return true - если валидна, false - иначе
         */
        public boolean checkBIN(String str) {
            try {
                if (str == null)
                    return false;
                if (str.length() != 12)
                    return false;
                int checkNumber = (new Integer(str.substring(11, 12))).intValue();
                int controlSum = getControlSum(str);
                if (controlSum != -1) {
                    if (checkNumber != controlSum)
                        return false;
                } else {
                    return false;
                }
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        private int[] getNumeric(String code) { //TODO
            int a[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            for (int i = 0; i < code.length(); i++) {
                a[i] = (new Integer(code.substring(i, i + 1))).intValue();
            }
            return a;
        }

        private int calculate_Checking_numeral(String code) { //TODO
            int a[] = getNumeric(code);
            int number = a[0] + 2 * a[1] + 3 * a[2] + 4 * a[3] + 5 * a[4] + 6 * a[5] +
                    7 * a[6] + 8 * a[7] + 9 * a[8] + 10 * a[9] + 11 * a[10];
            number %= 11;
            if(number == 10) {
                number = 3 * a[0] + 4 * a[1] + 5 * a[2] + 6 * a[3] + 7 * a[4] + 8 * a[5] +
                    9 * a[6] + 10 * a[7] + 11 * a[8] + a[9] + 2 * a[10];
                number %= 11;
            }
            return number;
        }
    
        public String convertName(String name) {
            // переводим в верхний регистр и разбиваем на слова
            String res = convertNameMU(name);
            String[] words = res.split("\\.");
            // каждое слово обрабатываем отдельно
//            ArrayList<Integer> l_ru = new ArrayList();
//            ArrayList<Integer> l_en = new ArrayList();
            res = "";
            for (String word : words) {
                if (!"".equals(res))
                    res += ".";
//                l_ru.clear();
//                l_en.clear();
                char[] chs = word.toCharArray();
                for (int i = 0; i < chs.length; i++) {
                    // все казахские на русские
                    int ind_kz = translit_kz_str.indexOf(chs[i]);
                    if (ind_kz > -1)
                        chs[i] = translit_kz[ind_kz];
                    // подсчет количества русских и латинских букв в слове
//                    int codeP = Character.codePointAt(chs, i);
//                    if (codeP >= 65 && codeP <= 90)
//                        l_en.add(i);// латинские буквы
//                    if (codeP >= 1040 && codeP <= 1071)
//                        l_ru.add(i);// русские буквы
//                }
//                if (l_ru.size() > 0 && l_en.size() > 0) {
//                    if (l_ru.size() >= l_en.size()) {
//                        if (l_en.size() > 0) {
//                            // ошибочные латинские буквы на русские
//                            for (int ind : l_en) {
//                                int ind_en = err_en_str.indexOf(chs[ind]);
//                                if (ind_en > -1)
//                                    chs[ind] = translit_err_en_ru[ind_en];
//                            }
//                        }
//
//                    } else {
//                        if (l_ru.size() > 0) {
//                            // ошибочные русские буквы на латинские
//                            for (int ind : l_ru) {
//                                int ind_en = err_ru_str.indexOf(chs[ind]);
//                                if (ind_en > -1)
//                                    chs[ind] = translit_err_ru_en[ind_en];
//                            }
//                        }
//                    }
                }
                word = new String(chs);
                // все латинские в русские по правилам транслитерации
                // двух и трехсимвольные
                for (int i = 0; i < en_m.length; i++) {
                    word = word.replaceAll(en_m[i], translit_ru_str_m[i]);
                }
                chs = word.toCharArray();
                // односимвольные
                for (int i = 0; i < chs.length; i++) {
                    int ind_en = en_str.indexOf(chs[i]);
                    if (ind_en > -1)
                        chs[i] = translit_en_ru[ind_en];
                }
                word = new String(chs);
                res += word;
            }
            return "." + res + ".";
        }
  
        public String convertNameMU(String name) {
            char[] chs = name.toCharArray();
            boolean pointAdded = false;
            StringBuilder res = new StringBuilder(chs.length);
            for (int i = 0; i < chs.length; i++) {
                char ch = chs[i];
                if (Character.isLetterOrDigit(ch)) {
                    res.append(Character.toUpperCase(ch));
                    pointAdded = false;
                } else if (!pointAdded) {
                    res.append('.');
                    pointAdded = true;
                }
            }
            return res.toString();
        }

    /**
     * Проверяет валидность почты
     * @param str адрес почты
     * @return true - если валидна, false - иначе
     */
    public boolean checkEmail(String str) {
        if (str == null) return false;
        if (str.trim().length() == 0) return false;
        Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
        Matcher m = p.matcher(str);
        return m.matches();
    }

    public Session getSession() {
        return null;
    }

    public void setSession(Session session) {}
    
    // Метод убирает все спецсимволы кроме пробела (пробел преобразуется в точку) и добавляет точки в конце и в начале названия
    public String convertNameWithSpace(String name) {
        name = name.trim().replaceAll(" +", " ");
        name = name.replaceAll("\"", "");
        char[] chs = name.toCharArray();
        boolean pointAdded = false;
        StringBuilder builder = new StringBuilder(chs.length);
        for (int i = 0; i < chs.length; i++) {
            char ch = chs[i];
            if (Character.isLetterOrDigit(ch)) {
                builder.append(Character.toUpperCase(ch));
                pointAdded = false;
            } else if ((i > 0 && i < chs.length - 1)
                    && (!Character.isLetterOrDigit(chs[i - 1]) || !Character.isLetterOrDigit(chs[i + 1]))) {
                pointAdded = false;
            } else if (!pointAdded && ch == ' ') {
                builder.append('.');
                pointAdded = true;
            }
        }
        String res = builder.toString();
        String[] words = res.split("\\.");
        // каждое слово обрабатываем отдельно
//        ArrayList<Integer> l_ru = new ArrayList();
//        ArrayList<Integer> l_en = new ArrayList();
        res = "";
        for (String word : words) {
            if (!"".equals(res))
                res += ".";
//            l_ru.clear();
//            l_en.clear();
            chs = word.toCharArray();
            for (int i = 0; i < chs.length; i++) {
                // все казахские на русские
                int ind_kz = translit_kz_str.indexOf(chs[i]);
                if (ind_kz > -1)
                    chs[i] = translit_kz[ind_kz];
                // подсчет количества русских и латинских букв в слове
//                int codeP = Character.codePointAt(chs, i);
//                if (codeP >= 65 && codeP <= 90)
//                    l_en.add(i);// латинские буквы
//                if (codeP >= 1040 && codeP <= 1071)
//                    l_ru.add(i);// русские буквы
//            }
//            if (l_ru.size() > 0 && l_en.size() > 0) {
//                if (l_ru.size() >= l_en.size()) {
//                    if (l_en.size() > 0) {
//                        // ошибочные латинские буквы на русские
//                        for (int ind : l_en) {
//                            int ind_en = err_en_str.indexOf(chs[ind]);
//                            if (ind_en > -1)
//                                chs[ind] = translit_err_en_ru[ind_en];
//                        }
//                    }
//
//                } else {
//                    if (l_ru.size() > 0) {
//                        // ошибочные русские буквы на латинские
//                        for (int ind : l_ru) {
//                            int ind_en = err_ru_str.indexOf(chs[ind]);
//                            if (ind_en > -1)
//                                chs[ind] = translit_err_ru_en[ind_en];
//                        }
//                    }
//                }
            }
            word = new String(chs);
            // все латинские в русские по правилам транслитерации
            // двух и трехсимвольные
            for (int i = 0; i < en_m.length; i++) {
                word = word.replaceAll(en_m[i], translit_ru_str_m[i]);
            }
            chs = word.toCharArray();
            // односимвольные
            for (int i = 0; i < chs.length; i++) {
                int ind_en = en_str.indexOf(chs[i]);
                if (ind_en > -1)
                    chs[i] = translit_en_ru[ind_en];
            }
            word = new String(chs);
            res += word;
        }
        return "." + res + ".";
    }

    // Метод убирает ОПФ, спецсимволы и сливает слова название в одно слово
    public String convertNameWithoutOPF(String name, String OPF) {
        if (OPF != null) {
            name = name.toUpperCase(Constants.OK).replace(OPF.toUpperCase(Constants.OK), "");
        }
        String res = convertNameMU(name);
        String[] words = res.split("\\.");
//        ArrayList<Integer> l_ru = new ArrayList();
//        ArrayList<Integer> l_en = new ArrayList();
        res = "";
        for (String word : words) {
//            l_ru.clear();
//            l_en.clear();
            char[] chs = word.toCharArray();
            for (int i = 0; i < chs.length; i++) {
                // Все казахские на русские
                int ind_kz = translit_kz_str.indexOf(chs[i]);
                if (ind_kz > -1)
                    chs[i] = translit_kz[ind_kz];
                // Подсчет количества русских и латинских букв в слове
//                int codeP = Character.codePointAt(chs, i);
//                if (codeP >= 65 && codeP <= 90)
//                    l_en.add(i);// латинские буквы
//                if (codeP >= 1040 && codeP <= 1071)
//                    l_ru.add(i);// русские буквы
//            }
//            if (l_ru.size() > 0 && l_en.size() > 0) {
//                if (l_ru.size() >= l_en.size()) {
//                    if (l_en.size() > 0) {
//                        // Ошибочные латинские буквы на русские
//                        for (int ind : l_en) {
//                            int ind_en = err_en_str.indexOf(chs[ind]);
//                            if (ind_en > -1)
//                                chs[ind] = translit_err_en_ru[ind_en];
//                        }
//                    }
//
//                } else {
//                    if (l_ru.size() > 0) {
//                        // Ошибочные русские буквы на латинские
//                        for (int ind : l_ru) {
//                            int ind_en = err_ru_str.indexOf(chs[ind]);
//                            if (ind_en > -1)
//                                chs[ind] = translit_err_ru_en[ind_en];
//                        }
//                    }
//                }
            }
            word = new String(chs);
            // Все латинские в русские по правилам транслитерации
            // Двух и трехсимвольные
            for (int i = 0; i < en_m.length; i++) {
                word = word.replaceAll(en_m[i], translit_ru_str_m[i]);
            }
            chs = word.toCharArray();
            // Односимвольные
            for (int i = 0; i < chs.length; i++) {
                int ind_en = en_str.indexOf(chs[i]);
                if (ind_en > -1)
                    chs[i] = translit_en_ru[ind_en];
            }
            word = new String(chs);
            res += word;
        }
        return res;
    }
}