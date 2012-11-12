package com.emc.xcelerators.community.dqlutils;

import com.documentum.fc.common.DfException;
import com.emc.xcelerators.community.dqlutils.params.QueryTemplate;
import com.emc.xcelerators.community.dqlutils.params.QueryTemplateWithOptions;
import com.emc.xcelerators.community.dqlutils.params.ResultSet;

public interface DQLUtils {

	public ResultSet read(QueryTemplate query) throws DfException;

	public ResultSet[] read(QueryTemplate[] query) throws DfException;

	public ResultSet apply(QueryTemplate query) throws DfException;

	public ResultSet[] apply(QueryTemplate[] query) throws DfException;

	public ResultSet exec(QueryTemplate dqlQuery) throws DfException;

	public ResultSet[] exec(QueryTemplate[] dqlQuery) throws DfException;

	public ResultSet query(QueryTemplateWithOptions query) throws DfException;

	public ResultSet[] query(QueryTemplateWithOptions[] query) throws DfException;

}
