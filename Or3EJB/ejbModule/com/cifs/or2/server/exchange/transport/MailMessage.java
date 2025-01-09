package com.cifs.or2.server.exchange.transport;


/**
 * Created by IntelliJ IDEA.
 * User: Valeri
 * Date: 05.07.2007
 * Time: 12:02:08
 * To change this template use File | Settings | File Templates.
 */
public class MailMessage {
    public String subject="";
    public String text="";
    public String from="";
    public String[] to;
    public String[] copyto;
    public String msgId;
    public String[] ids;
    public byte[][] attach;
}
