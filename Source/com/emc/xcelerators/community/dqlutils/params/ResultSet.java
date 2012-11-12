package com.emc.xcelerators.community.dqlutils.params;

import java.util.Arrays;
import java.util.Collection;

public class ResultSet {

	private Row[]	row;

	public ResultSet() {
		setRow(new Row[0]);
	}

	public ResultSet(final Collection<Row> result) {
		setRow(result.toArray(new Row[result.size()]));
	}

	public void setRow(final Row[] row) {
		this.row = row;
	}

	public Row[] getRow() {
		return row;
	}

	@Override
	public String toString() {
		final StringBuilder buf = new StringBuilder();
		buf.append(this.getClass().getSimpleName());
		buf.append("[rows = ");
		buf.append(Arrays.toString(row));
		buf.append("]");
		return buf.toString();
	}
}
