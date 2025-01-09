package kz.tamur.rt;

import java.util.ListResourceBundle;

public class RuntimeResourcesSrv_ru extends ListResourceBundle {

    public static final Object[][] contents = {
          {"notCompleteMessage", "Не заполнены необходимые поля!"},
          {"passNotEqualsMessage", "Пароли не совпадают!"},
          {"messPassIdent","Пароли идентичны!"},
          {"oldPassInvalidMessage", "Старый пароль введён неверно!"},
          {"messMinPeriodPass","Пароль разрешено менять не чаще чем раз в X суток!"},
          {"validPwdMinLogin","Имя пользователя не может быть меньше X символов!"},
          {"validPwdMaxLogin","Имя пользователя не может быть больше X символов!"},
          {"validPwdmMinPass","Пароль не может быть меньше X символов!"},
          {"validPwdMinPassAdm","Для администратора пароль не может быть меньше X символов!"},
          {"validPwdmMaxPass","Пароль не может быть больше X символов!"},
          {"validPwdNoNumb","В пароле должны присутствовать цифры!"},
          {"validPwdNoAllNumb","В пароле не должны явно преобладать цифры!"},
          {"validPwdNoSymb","В пароле должны присутствовать буквы!"},
          {"validPwdNoReg","В пароле должны присутствовать буквы в различном регистре!"},
          {"validPwdNoSpec","В пароле должны присутствовать специальные символы!"},
          {"validPwdNotName","В пароле запрещено использовать имена!"},
          {"validPwdNotSurn","В пароле запрещено использовать фамилии!"},
          {"validPwdNotTel","В пароле запрещено использовать номера телефонов!"},
          {"validPwdNotWord","В пароле запрещено использовать частоупотребляемые слова!"},
          {"validPwdNotKeyboard","В пароле запрещено использовать клавиатурные выражения!"},
          {"validPwdNotLogin","В пароле не должен употребляться логин пользователя!"},
          {"validPwdNotRep","В пароле запрещено повторение первых трёх символов!"},    
          {"validPwdNotRepAnyMoreTwo","В пароле запрещено повторение последовательности из более 2-х одинаковых символов подряд"},
          {"messPassDupl","Пароль не должен повторять X предыдущих паролей!"},
          {"validPwdNotIdentificationData","В пароле запрещено использовать собственные идентификационные данные!"},
    };

    protected Object[][] getContents() {
        return contents;
    }
}