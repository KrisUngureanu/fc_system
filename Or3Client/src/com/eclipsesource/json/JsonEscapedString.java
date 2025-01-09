package com.eclipsesource.json;

import java.io.IOException;

import org.apache.commons.lang.StringEscapeUtils;

@SuppressWarnings( "serial" ) // use default serial UID
public class JsonEscapedString extends JsonString {

	public JsonEscapedString(String string) {
		super(StringEscapeUtils.escapeHtml(string));
	}
	
	@Override
	protected void write(JsonWriter writer) throws IOException {
		writer.writeEscapedString(asString());
	}
}
