package kz.tamur.guidesigner.noteeditor;

import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTML;
import javax.swing.text.*;

/**
 * Created by IntelliJ IDEA.
 * User: Кайржан
 * Date: 12.11.2005
 * Time: 18:38:29
 * To change this template use File | Settings | File Templates.
 */
public class OrHTMLEditorKit extends HTMLEditorKit {

        public OrHTMLEditorKit() {
            super();
        }

        public ViewFactory getViewFactory() {
            return new MyFAc();    //To change body of overridden methods use File | Settings | File Templates.
        }

        public class MyFAc extends HTMLFactory {
            public MyFAc() {
                super();
            }
            public Document createDefaultDocument() {
                 StyleSheet styles = getStyleSheet();
                 HTMLDocument doc = new HTMLDocument(styles);
                 doc.setAsynchronousLoadPriority(4);
                 doc.setTokenThreshold(100);
                 return doc;
               }



            public View create(Element elem) {
                Object o = elem.getAttributes().getAttribute(StyleConstants.NameAttribute);
                if (o instanceof HTML.Tag) {
                    HTML.Tag kind = (HTML.Tag) o;
                    if (kind == HTML.Tag.IMG) {
                        return new OrHTMLImage(elem);
                    }

                }
                return super.create(elem);
            }
        }
    }
