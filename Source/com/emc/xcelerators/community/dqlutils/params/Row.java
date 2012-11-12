package com.emc.xcelerators.community.dqlutils.params;

import java.util.Arrays;

public class Row {

	private String[]	columns;

	public Row() {
	}

	public Row(final String[] column) {
		this.columns = column;
	}

	public String[] getColumns() {
		return columns;
	}

	public void setColumns(final String[] columns) {
		this.columns = columns;
	}

	@Override
	public String toString() {
		final StringBuilder buf = new StringBuilder();
		buf.append(this.getClass().getSimpleName());
		buf.append("[columns = ");
		buf.append(Arrays.toString(columns));
		buf.append("]");
		return buf.toString();
	}
}
