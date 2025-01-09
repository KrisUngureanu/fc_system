package com.cifs.or2.kernel;

import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.util.ArrayList;

import kz.tamur.ods.Value;

public class Or3ObjectInputStream extends ObjectInputStream {

	public Or3ObjectInputStream(InputStream inputStream) throws IOException {
		super(inputStream);
	}

	@Override
	protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
/*		if (!(
				desc.getName().equals(DataChanges.class.getName()) 
				|| desc.getName().equals(ArrayList.class.getName()) 
				|| desc.getName().equals("java.util.Collections$EmptyList") 
				|| desc.getName().equals(Object[].class.getName()) 
				|| desc.getName().equals(KrnObject[].class.getName()) 
				|| desc.getName().equals(Object.class.getName()) 
				|| desc.getName().equals(String.class.getName()) 
				|| desc.getName().equals(Number.class.getName()) 
				|| desc.getName().equals(Long.class.getName()) 
				|| desc.getName().equals(Integer.class.getName()) 
				|| desc.getName().equals(Boolean.class.getName()) 
				|| desc.getName().equals(Double.class.getName()) 
				|| desc.getName().equals(KrnObject.class.getName()) 
				|| desc.getName().equals(byte[].class.getName()) 
				|| desc.getName().equals(com.cifs.or2.kernel.Date.class.getName()) 
				|| desc.getName().equals(com.cifs.or2.kernel.Time.class.getName())
				
				|| desc.getName().equals(ModelChanges.class.getName()) 
				|| desc.getName().equals(ModelChange.class.getName()) 

				|| desc.getName().equals(QueryResult.class.getName()) 
				|| desc.getName().equals(Value.class.getName()) 

				|| desc.getName().equals(ReportData.class.getName()) 
				|| desc.getName().startsWith("[Lorg.jdom.") 
				|| desc.getName().startsWith("org.jdom.") 
			)) {
			throw new InvalidClassException("Unrecognized Class", desc.getName());
		}
*/		return super.resolveClass(desc);
	}
}