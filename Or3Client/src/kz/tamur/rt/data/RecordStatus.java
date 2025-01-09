package kz.tamur.rt.data;

public interface RecordStatus {
	int UPTODATE	= 0;
	int INSERTED	= 2;
	int MODIFIED	= 4;
	int CREATED		= 8;
	int DELETED		= 16;
	int TMPDELETED	= 32;
	int TMPINSERTED	= 64;
	int NaD			= 128;
}
