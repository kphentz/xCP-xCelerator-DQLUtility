package com.emc.xcelerators.community.dqlutils.params;

import java.util.Arrays;

public class QueryTemplate {

	private String			template;
	private TemplateParam[]	params;

	public QueryTemplate() {
	}

	public void setTemplate(final String template) {
		this.template = template;
	}

	public String getTemplate() {
		return template;
	}

	public void setParams(final TemplateParam[] params) {
		this.params = params;
	}

	public TemplateParam[] getParams() {
		return params;
	}

	@Override
	public String toString() {
		final StringBuilder buf = new StringBuilder();
		buf.append(this.getClass().getSimpleName());
		buf.append("[template=");
		buf.append(template);
		buf.append(", params=");
		buf.append(Arrays.toString(params));
		buf.append("]");
		return buf.toString();
	}
}
