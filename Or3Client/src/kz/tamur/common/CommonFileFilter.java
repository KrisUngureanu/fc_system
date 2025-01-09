package kz.tamur.common;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.filechooser.FileFilter;

public class CommonFileFilter extends FileFilter {

	private List<String> filters;
	private String description;
	private String fullDescription;
	private boolean useExtensionsInDescription;

	public CommonFileFilter() {
		filters = null;
		description = null;
		fullDescription = null;
		useExtensionsInDescription = true;
		filters = new ArrayList<String>();
	}

	public CommonFileFilter(String extension) {
		this(extension, null);
	}

	public CommonFileFilter(String extension, String description) {
		this();
		if (extension != null) {
			addExtension(extension);
		}
		if (description != null) {
			setDescription(description);
		}
	}

	public CommonFileFilter(String filters[]) {
		this(filters, null);
	}

	public CommonFileFilter(String filters[], String description) {
		this();
		for (int i = 0; i < filters.length; i++) {
			addExtension(filters[i]);
		}

		if (description != null) {
			setDescription(description);
		}
	}

	public boolean accept(File f) {
		if (f != null) {
			if (f.isDirectory()) {
				return true;
			}
			String extension = getExtension(f);
			if (extension != null && filters.contains(extension)) {
				return true;
			}
		}
		return false;
	}

	public String getExtension(File f) {
		if (f != null) {
			String filename = f.getName();
			int i = filename.lastIndexOf('.');
			if (i > 0 && i < filename.length() - 1) {
				return filename.substring(i + 1).toLowerCase(Locale.ROOT);
			}
		}
		return null;
	}

	public void addExtension(String extension) {
		if (filters == null) {
			filters = new ArrayList<String>(1);
		}
		filters.add(extension.toLowerCase(Locale.ROOT));
		fullDescription = null;
	}

	public String getDescription() {
		if (fullDescription == null) {
			if (!isExtensionListInDescription()) {
				fullDescription = description != null ? (new StringBuilder())
						.append(description).append(" (").toString() : "(";
					
				StringBuilder temp = new StringBuilder();
				int i=0;
				for (String extension : filters) {
					if (i == 0) temp.append(", ");
					temp.append(extension);
					i++;
				}
				fullDescription = new StringBuilder(fullDescription).append(")").toString();
			}
		}
		return fullDescription;
	}

	public void setDescription(String description) {
		this.description = description;
		fullDescription = null;
	}

	public void setExtensionListInDescription(boolean b) {
		useExtensionsInDescription = b;
		fullDescription = null;
	}

	public boolean isExtensionListInDescription() {
		return useExtensionsInDescription;
	}
}