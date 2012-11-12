package com.emc.xcelerators.community.dqlutils.params;

import java.util.Arrays;

public class QueryTemplateWithOptions extends QueryTemplate {

	private String	option;

	public void setOption(final String option) {
		this.option = option;
	}

	public String getOption() {
		return option;
	}

	@Override
	public String toString() {
		final StringBuilder buf = new StringBuilder();
		buf.append(this.getClass().getSimpleName());
		buf.append("[template=");
		buf.append(getTemplate());
		buf.append(", params=");
		buf.append(Arrays.toString(getParams()));
		buf.append(", option=");
		buf.append(option);
		buf.append("]");
		return buf.toString();
	}
}
