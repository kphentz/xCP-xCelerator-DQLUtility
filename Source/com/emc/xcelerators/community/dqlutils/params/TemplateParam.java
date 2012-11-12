package com.emc.xcelerators.community.dqlutils.params;

import java.util.Arrays;

public class TemplateParam {

	private String[]	value;
	private boolean		escape		= true;
	private boolean		quote		= true;
	private boolean		concat		= false;
	private String		separator	= "";

	public TemplateParam() {
	}

	public void setValue(final String[] value) {
		this.value = value;
	}

	public String[] getValue() {
		return value;
	}

	public boolean isEscape() {
		return escape;
	}

	public void setEscape(boolean escape) {
		this.escape = escape;
	}

	public boolean isQuote() {
		return quote;
	}

	public void setQuote(boolean quote) {
		this.quote = quote;
	}

	@Override
	public String toString() {
		final StringBuilder buf = new StringBuilder();
		buf.append(this.getClass().getSimpleName());
		buf.append("[escape=");
		buf.append(escape);
		buf.append(", quote=");
		buf.append(quote);
		buf.append(", concat=");
		buf.append(concat);
		buf.append(", separator=");
		buf.append(getSeparator());
		buf.append(", value = ");
		buf.append(Arrays.toString(value));
		buf.append("]");
		return buf.toString();
	}

	public void setConcat(boolean concat) {
		this.concat = concat;
	}

	public boolean isConcat() {
		return concat;
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}

	public String getSeparator() {
		return separator;
	}
}
