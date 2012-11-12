package com.emc.xcelerators.community.dqlutils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.DfSingleDocbaseModule;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.IDfValue;
import com.documentum.fc.impl.util.StringUtil;
import com.emc.xcelerators.community.dqlutils.params.QueryTemplate;
import com.emc.xcelerators.community.dqlutils.params.QueryTemplateWithOptions;
import com.emc.xcelerators.community.dqlutils.params.ResultSet;
import com.emc.xcelerators.community.dqlutils.params.Row;
import com.emc.xcelerators.community.dqlutils.params.TemplateParam;
import com.emc.xcelerators.utils.text.STTCompiler;
import com.emc.xcelerators.utils.text.TextTemplate;

public class DQLUtilsImpl extends DfSingleDocbaseModule implements DQLUtils {

	final private static Map<String, Integer>	QUERY_OPTIONS				= makeQueryOptions();

	final private static String					LOG							= DQLUtilsImpl.class.getCanonicalName();

	private static final String					DEBUG_DQL					= "DQL: {0}";

	private static final String					ERR_NO_QUERY				= "No query specified, query template was {0}.";
	private static final String					WARN_NO_QUERY				= "The query template {0} yielded no query (\"{0}\"), activity will fail.";

	private static final String					DEBUG_RESULT_SET			= "Query {0} yielded result set {1}.";

	private static final String					WARN_INVALID_QUERY_OPTION	= "Invalid Query Option {0} valid values are {1}.";
	private static String						DEBUG_QUERY					= "DQL Query: {0}.";
	private static String						DEBUG_READ					= "DQL Read query: {0}.";
	private static String						DEBUG_APPLY					= "DQL Apply query: {0}.";
	private static String						DEBUG_EXEC					= "DQL Exec query: {0}.";
	
	public String getVersion() {
		return "1.0";
	}

	public ResultSet read(final QueryTemplate query) throws DfException {
		return read(new QueryTemplate[] { query })[0];
	}

	public ResultSet[] read(final QueryTemplate[] query) throws DfException {
		d(DEBUG_READ, (Object) query);
		return query(query, makeArray(query.length, IDfQuery.DF_READ_QUERY));
	}

	public ResultSet apply(final QueryTemplate query) throws DfException {
		return apply(new QueryTemplate[] { query })[0];
	}

	public ResultSet[] apply(final QueryTemplate[] query) throws DfException {
		d(DEBUG_APPLY, (Object) query);
		return query(query, makeArray(query.length, IDfQuery.DF_APPLY));
	}

	public ResultSet exec(final QueryTemplate query) throws DfException {
		return exec(new QueryTemplate[] { query })[0];
	}

	public ResultSet[] exec(final QueryTemplate[] query) throws DfException {
		d(DEBUG_EXEC, (Object) query);
		return query(query, makeArray(query.length, IDfQuery.DF_EXEC_QUERY));
	}

	public ResultSet query(final QueryTemplateWithOptions query) throws DfException {
		return query(new QueryTemplateWithOptions[] { query })[0];
	}

	public ResultSet[] query(final QueryTemplateWithOptions[] query) throws DfException {
		d(DEBUG_QUERY, (Object) query);
		final int[] options = new int[query.length];
		for (int i = 0; i < query.length; ++i) {
			options[i] = asQueryOption(query[i].getOption());
		}
		return query(query, options);
	}

	protected ResultSet[] query(final QueryTemplate[] queries, final int[] option) throws DfException {
		final int len = queries.length;
		final ResultSet[] rset = new ResultSet[len];
		final IDfSession session = getSession();
		try {
			for (int i = 0; i < len; ++i) {
				rset[i] = query(session, queries[i], option[i]);
			}
		} finally {
			if (session != null) {
				releaseSession(session);
			}
		}
		return rset;
	}

	private ResultSet query(final IDfSession session, final QueryTemplate query, final int queryOption) throws DfException {
		final IDfQuery dqlQuery = asDQLQuery(query);
		IDfCollection col = null;
		try {
			col = dqlQuery.execute(session, queryOption);
			final ArrayList<Row> result = new ArrayList<Row>();
			final int attrCount = col.getAttrCount();
			while (col.next()) {
				final String[] colsInRow = new String[attrCount];
				for (int i = 0; i < attrCount; i++) {
					final IDfValue value = col.getValueAt(i);
					if (value != null) {
						colsInRow[i] = value.asString();
					}
				}
				result.add(new Row(colsInRow));
			}
			final ResultSet resultSet = new ResultSet(result);
			d(DEBUG_RESULT_SET, dqlQuery.getDQL(), resultSet);
			return resultSet;
		} finally {
			if (col != null) {
				col.close();
			}
		}
	}

	public String getDqlFromTemplate(final QueryTemplate template) {
		final Map<String, String> paramValues = getParamValues(template.getParams());
		final TextTemplate textTemplate = STTCompiler.compile(template.getTemplate());
		final String dql = textTemplate.apply(paramValues);
		return dql;
	}

	private Map<String, String> getParamValues(final TemplateParam[] params) {
		final int len = params != null ? params.length : 0;
		final Map<String, String> paramValues = new HashMap<String, String>();
		for (int i = 0; i < len; ++i) {
			int numValues = params[i].getValue().length;
			if (numValues == 1) {
				paramValues.put(String.valueOf(i), getParamValue(params[i], params[i].isQuote(), 0));
			} else if (numValues > 1) {
				if (params[i].isConcat()) {
					final StringBuilder buf = new StringBuilder();
					for (int j = 0; j < params[i].getValue().length; ++j) {
						if(buf.length() > 0) {
							buf.append(params[i].getSeparator());
						}
						getParamValue(params[i], false, j, buf);
					}
					if(params[i].isQuote()) {
						paramValues.put(String.valueOf(i), "'" + buf.toString() + "'");
					} else {
						paramValues.put(String.valueOf(i), buf.toString());						
					}
				} else {
					final StringBuilder buf = new StringBuilder();
					for (int j = 0; j < params[i].getValue().length; ++j) {
						if (buf.length() > 0) {
							buf.append(",");
						}
						getParamValue(params[i], params[i].isQuote(), j, buf);
					}
					paramValues.put(String.valueOf(i), buf.toString());
				}
			}
		}
		return paramValues;
	}
	
	private String getParamValue(final TemplateParam param, boolean quote, final int i) {
		final StringBuilder buf = new StringBuilder();
		getParamValue(param, quote, i, buf);
		return buf.toString();
	}

	private void getParamValue(final TemplateParam param, boolean quote, final int i, final StringBuilder buf) {
		if (quote) {
			buf.append("'");
		}
		if (param.isEscape()) {
			buf.append(escape(param.getValue()[i]));
		} else {
			buf.append(param.getValue()[i]);
		}
		if (quote) {
			buf.append("'");
		}
	}

	private String escape(String string) {
		return string.replaceAll("'", "''");
	}

	final protected static void d(final String msgTemplate, final Object... params) {
		if (DfLogger.isDebugEnabled(LOG)) {
			DfLogger.debug(LOG, msgTemplate, params, null);
		}
	}

	final protected static void w(final String msgTemplate, final Object... params) {
		if (DfLogger.isWarnEnabled(LOG)) {
			DfLogger.warn(LOG, msgTemplate, params, null);
		}
	}

	private int asQueryOption(final String option) {
		final Integer result = QUERY_OPTIONS.get(option.toUpperCase());
		if (result == null) {
			w(WARN_INVALID_QUERY_OPTION, option, QUERY_OPTIONS.keySet());
			throw new IllegalArgumentException("Invalid Query Option " + option + " valid values are " + QUERY_OPTIONS.keySet());
		}
		return result;
	}

	private static Map<String, Integer> makeQueryOptions() {
		final Map<String, Integer> options = new HashMap<String, Integer>();
		options.put("APPLY", IDfQuery.DF_APPLY);
		options.put("READ", IDfQuery.DF_READ_QUERY);
		options.put("EXEC", IDfQuery.DF_EXEC_QUERY);
		options.put("DF_APPLY", IDfQuery.DF_APPLY);
		options.put("DF_READ_QUERY", IDfQuery.DF_READ_QUERY);
		options.put("DF_EXEC_QUERY", IDfQuery.DF_EXEC_QUERY);
		options.put("READ_QUERY", IDfQuery.DF_READ_QUERY);
		options.put("EXEC_QUERY", IDfQuery.DF_EXEC_QUERY);
		return options;
	}

	private int[] makeArray(final int length, final int value) {
		final int[] array = new int[length];
		Arrays.fill(array, value);
		return array;
	}

	private IDfQuery asDQLQuery(final QueryTemplate query) {
		final String dql = query == null ? null : getDqlFromTemplate(query);
		if (StringUtil.isEmptyOrNull(dql)) {
			w(WARN_NO_QUERY, query, dql);
			throw new IllegalStateException(ERR_NO_QUERY);
		}
		d(DEBUG_DQL, dql);
		return new DfQuery(dql);
	}

}
