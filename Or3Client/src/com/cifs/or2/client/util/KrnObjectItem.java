package com.cifs.or2.client.util;

import com.cifs.or2.kernel.KrnObject;


public class KrnObjectItem implements Comparable
{
	public KrnObject obj;
	public String title;

	public KrnObjectItem(KrnObject obj, String title) {
		this.obj = obj;
		this.title = title;
	}

	public boolean equals(Object o) {
		if (o instanceof KrnObjectItem) {
			KrnObject krnObj = ((KrnObjectItem) o).obj;
			if (krnObj != null && obj != null)
				return (obj.id == krnObj.id);
		}
		return false;
	}

	public String toString() {
		return title;
	}

	public int compareTo(Object o) {
		return title.compareTo(((KrnObjectItem) o).title);
	}
}
