package com.cifs.or2.server.exchange.transport.gdms3;

public class Gdms3Message {

	public static final int MN_NONE = 0;
	public static final int MN_SENT = 1;
	public static final int MN_DELIVERED = 2;
	public static final int MN_ALL = 3;


	public String msgUUID;
	public byte msgPriority;
	public int shouldNotify;
	public String docVer;
	public String docType;
	public String docId;
	public boolean signed;
	public boolean crypted;
	public byte compressed;
	public Gdms3ParticipantInfo sender;
	public Gdms3ParticipantInfo recipient;

	public byte[] binaryData;
	public String textData;
}
