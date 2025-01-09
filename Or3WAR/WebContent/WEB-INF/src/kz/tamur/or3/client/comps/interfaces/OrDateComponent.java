package kz.tamur.or3.client.comps.interfaces;

import kz.tamur.util.CopyButton;

/**
 * Created by IntelliJ IDEA.
 * User: Администратор
 * Date: 18.07.2006
 * Time: 19:12:44
 * To change this template use File | Settings | File Templates.
 */
public interface OrDateComponent extends OrTextComponent {
	// TODO Перенести в OrGuiComponent после перевода всех
	// компонентов на новый вариант
    int getDateFormat();

    String getCopyRefPath();

    CopyButton getCopyBtn();
}