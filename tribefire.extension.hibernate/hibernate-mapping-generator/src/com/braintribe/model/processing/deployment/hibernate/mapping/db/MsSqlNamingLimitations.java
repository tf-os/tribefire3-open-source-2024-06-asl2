// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
// 
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// 
//     http://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// ============================================================================
package com.braintribe.model.processing.deployment.hibernate.mapping.db;

import com.braintribe.model.processing.deployment.hibernate.mapping.HbmXmlGenerationContext;

public class MsSqlNamingLimitations extends NamingLimitations {
	
	MsSqlNamingLimitations(HbmXmlGenerationContext context) {
		super(context);
		setTableNameMaxLength(128);
		setColumnNameMaxLength(128);
		//setColumnNameIllegalLeadingCharsPattern("^_+");
		setupReservedWords();
	}

	/**
	 * @see <a href="http://technet.microsoft.com/en-us/library/ms189822.aspx">SQL Server Documentation</a>
	 */
	private void setupReservedWords() {
		registerReserved("add");
		registerReserved("all");
		registerReserved("alter");
		registerReserved("and");
		registerReserved("any");
		registerReserved("as");
		registerReserved("asc");
		registerReserved("authorization");
		registerReserved("backup");
		registerReserved("begin");
		registerReserved("between");
		registerReserved("break");
		registerReserved("browse");
		registerReserved("bulk");
		registerReserved("by");
		registerReserved("cascade");
		registerReserved("case");
		registerReserved("check");
		registerReserved("checkpoint");
		registerReserved("close");
		registerReserved("clustered");
		registerReserved("coalesce");
		registerReserved("collate");
		registerReserved("column");
		registerReserved("commit");
		registerReserved("compute");
		registerReserved("constraint");
		registerReserved("contains");
		registerReserved("containstable");
		registerReserved("continue");
		registerReserved("convert");
		registerReserved("create");
		registerReserved("cross");
		registerReserved("current");
		registerReserved("current_date");
		registerReserved("current_time");
		registerReserved("current_timestamp");
		registerReserved("current_user");
		registerReserved("cursor");
		registerReserved("database");
		registerReserved("dbcc");
		registerReserved("deallocate");
		registerReserved("declare");
		registerReserved("default");
		registerReserved("delete");
		registerReserved("deny");
		registerReserved("desc");
		registerReserved("disk");
		registerReserved("distinct");
		registerReserved("distributed");
		registerReserved("double");
		registerReserved("drop");
		registerReserved("dump");
		registerReserved("else");
		registerReserved("end");
		registerReserved("errlvl");
		registerReserved("escape");
		registerReserved("except");
		registerReserved("exec");
		registerReserved("execute");
		registerReserved("exists");
		registerReserved("exit");
		registerReserved("external");
		registerReserved("fetch");
		registerReserved("file");
		registerReserved("fillfactor");
		registerReserved("for");
		registerReserved("foreign");
		registerReserved("freetext");
		registerReserved("freetexttable");
		registerReserved("from");
		registerReserved("full");
		registerReserved("function");
		registerReserved("goto");
		registerReserved("grant");
		registerReserved("group");
		registerReserved("having");
		registerReserved("holdlock");
		registerReserved("identity");
		registerReserved("identity_insert");
		registerReserved("identitycol");
		registerReserved("if");
		registerReserved("in");
		registerReserved("index");
		registerReserved("inner");
		registerReserved("insert");
		registerReserved("intersect");
		registerReserved("into");
		registerReserved("is");
		registerReserved("join");
		registerReserved("key");
		registerReserved("kill");
		registerReserved("left");
		registerReserved("like");
		registerReserved("lineno");
		registerReserved("load");
		registerReserved("merge");
		registerReserved("national");
		registerReserved("nocheck");
		registerReserved("nonclustered");
		registerReserved("not");
		registerReserved("null");
		registerReserved("nullif");
		registerReserved("of");
		registerReserved("off");
		registerReserved("offsets");
		registerReserved("on");
		registerReserved("open");
		registerReserved("opendatasource");
		registerReserved("openquery");
		registerReserved("openrowset");
		registerReserved("openxml");
		registerReserved("option");
		registerReserved("or");
		registerReserved("order");
		registerReserved("outer");
		registerReserved("over");
		registerReserved("percent");
		registerReserved("pivot");
		registerReserved("plan");
		registerReserved("precision");
		registerReserved("primary");
		registerReserved("print");
		registerReserved("proc");
		registerReserved("procedure");
		registerReserved("public");
		registerReserved("raiserror");
		registerReserved("read");
		registerReserved("readtext");
		registerReserved("reconfigure");
		registerReserved("references");
		registerReserved("replication");
		registerReserved("restore");
		registerReserved("restrict");
		registerReserved("return");
		registerReserved("revert");
		registerReserved("revoke");
		registerReserved("right");
		registerReserved("rollback");
		registerReserved("rowcount");
		registerReserved("rowguidcol");
		registerReserved("rule");
		registerReserved("save");
		registerReserved("schema");
		registerReserved("securityaudit");
		registerReserved("select");
		registerReserved("semantickeyphrasetable");
		registerReserved("semanticsimilaritydetailstable");
		registerReserved("semanticsimilaritytable");
		registerReserved("session_user");
		registerReserved("set");
		registerReserved("setuser");
		registerReserved("shutdown");
		registerReserved("some");
		registerReserved("statistics");
		registerReserved("system_user");
		registerReserved("table");
		registerReserved("tablesample");
		registerReserved("textsize");
		registerReserved("then");
		registerReserved("to");
		registerReserved("top");
		registerReserved("tran");
		registerReserved("transaction");
		registerReserved("trigger");
		registerReserved("truncate");
		registerReserved("try_convert");
		registerReserved("tsequal");
		registerReserved("union");
		registerReserved("unique");
		registerReserved("unpivot");
		registerReserved("update");
		registerReserved("updatetext");
		registerReserved("use");
		registerReserved("user");
		registerReserved("values");
		registerReserved("varying");
		registerReserved("view");
		registerReserved("waitfor");
		registerReserved("when");
		registerReserved("where");
		registerReserved("while");
		registerReserved("with");
		registerReserved("within group");
		registerReserved("writetext");
	}
}
