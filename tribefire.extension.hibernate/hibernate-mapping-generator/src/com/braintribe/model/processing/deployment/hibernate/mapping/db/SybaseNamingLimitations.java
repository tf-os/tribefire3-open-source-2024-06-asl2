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

public class SybaseNamingLimitations extends NamingLimitations {

	SybaseNamingLimitations(HbmXmlGenerationContext context) {
		super(context);
		setTableNameMaxLength(30);
		setColumnNameMaxLength(30);
		//setColumnNameIllegalLeadingCharsPattern("^_+");
		setupReservedWords();
	}

	/**
	 * @see <a href="http://infocenter.sybase.com/help/index.jsp?topic=/com.sybase.infocenter.dc38151.1510/html/iqrefbb/Alhakeywords.htm">Sybase Documentation</a>
	 */
	private void setupReservedWords() {
		registerReserved("active");
		registerReserved("add");
		registerReserved("all");
		registerReserved("algorithm");
		registerReserved("alter");
		registerReserved("and");
		registerReserved("any");
		registerReserved("append");
		registerReserved("as");
		registerReserved("asc");
		registerReserved("auto");
		registerReserved("backup");
		registerReserved("begin");
		registerReserved("between");
		registerReserved("bigint");
		registerReserved("binary");
		registerReserved("bit");
		registerReserved("bottom");
		registerReserved("break");
		registerReserved("by");
		registerReserved("calibrate");
		registerReserved("calibration");
		registerReserved("call");
		registerReserved("cancel");
		registerReserved("capability");
		registerReserved("cascade");
		registerReserved("case");
		registerReserved("cast");
		registerReserved("certificate");
		registerReserved("char");
		registerReserved("char_convert");
		registerReserved("character");
		registerReserved("check");
		registerReserved("checkpoint");
		registerReserved("checksum");
		registerReserved("clientport");
		registerReserved("close");
		registerReserved("columns");
		registerReserved("comment");
		registerReserved("commit");
		registerReserved("committed");
		registerReserved("comparisons");
		registerReserved("computes");
		registerReserved("conflict");
		registerReserved("connect");
		registerReserved("constraint");
		registerReserved("contains");
		registerReserved("continue");
		registerReserved("convert");
		registerReserved("create");
		registerReserved("cross");
		registerReserved("cube");
		registerReserved("current");
		registerReserved("current_timestamp");
		registerReserved("current_user");
		registerReserved("cursor");
		registerReserved("date");
		registerReserved("dbspace");
		registerReserved("dbspacename");
		registerReserved("deallocate");
		registerReserved("debug");
		registerReserved("dec");
		registerReserved("decimal");
		registerReserved("declare");
		registerReserved("decoupled");
		registerReserved("decrypted");
		registerReserved("default");
		registerReserved("delay");
		registerReserved("delete");
		registerReserved("deleting");
		registerReserved("density");
		registerReserved("desc");
		registerReserved("deterministic");
		registerReserved("disable");
		registerReserved("distinct");
		registerReserved("do");
		registerReserved("double");
		registerReserved("drop");
		registerReserved("dynamic");
		registerReserved("elements");
		registerReserved("else");
		registerReserved("elseif");
		registerReserved("enable");
		registerReserved("encapsulated");
		registerReserved("encrypted");
		registerReserved("end");
		registerReserved("endif");
		registerReserved("escape");
		registerReserved("except");
		registerReserved("exception");
		registerReserved("exclude");
		registerReserved("exec");
		registerReserved("execute");
		registerReserved("existing");
		registerReserved("exists");
		registerReserved("explicit");
		registerReserved("express");
		registerReserved("externlogin");
		registerReserved("fastfirstrow");
		registerReserved("fetch");
		registerReserved("first");
		registerReserved("float");
		registerReserved("following");
		registerReserved("for");
		registerReserved("force");
		registerReserved("foreign");
		registerReserved("forward");
		registerReserved("from");
		registerReserved("full");
		registerReserved("gb");
		registerReserved("goto");
		registerReserved("grant");
		registerReserved("group");
		registerReserved("grouping");
		registerReserved("having");
		registerReserved("hidden");
		registerReserved("history");
		registerReserved("holdlock");
		registerReserved("identified");
		registerReserved("if");
		registerReserved("in");
		registerReserved("inactive");
		registerReserved("index");
		registerReserved("index_lparen");
		registerReserved("inner");
		registerReserved("inout");
		registerReserved("input");
		registerReserved("insensitive");
		registerReserved("insert");
		registerReserved("inserting");
		registerReserved("install");
		registerReserved("instead");
		registerReserved("int");
		registerReserved("integer");
		registerReserved("integrated");
		registerReserved("intersect");
		registerReserved("into");
		registerReserved("iq");
		registerReserved("is");
		registerReserved("isolation");
		registerReserved("jdk");
		registerReserved("join");
		registerReserved("kb");
		registerReserved("key");
		registerReserved("lateral");
		registerReserved("left");
		registerReserved("like");
		registerReserved("lock");
		registerReserved("logging");
		registerReserved("login");
		registerReserved("long");
		registerReserved("mb");
		registerReserved("match");
		registerReserved("membership");
		registerReserved("message");
		registerReserved("mode");
		registerReserved("modify");
		registerReserved("namespace");
		registerReserved("natural");
		registerReserved("new");
		registerReserved("no");
		registerReserved("noholdlock");
		registerReserved("nolock");
		registerReserved("not");
		registerReserved("notify");
		registerReserved("null");
		registerReserved("numeric");
		registerReserved("of");
		registerReserved("off");
		registerReserved("on");
		registerReserved("open");
		registerReserved("optimization");
		registerReserved("option");
		registerReserved("options");
		registerReserved("or");
		registerReserved("order");
		registerReserved("others");
		registerReserved("out");
		registerReserved("outer");
		registerReserved("over");
		registerReserved("pages");
		registerReserved("paglock");
		registerReserved("partial");
		registerReserved("partition");
		registerReserved("passthrough");
		registerReserved("password");
		registerReserved("plan");
		registerReserved("preceding");
		registerReserved("precision");
		registerReserved("prepare");
		registerReserved("primary");
		registerReserved("print");
		registerReserved("privileges");
		registerReserved("proc");
		registerReserved("procedure");
		registerReserved("proxy");
		registerReserved("publication");
		registerReserved("raiserror");
		registerReserved("range");
		registerReserved("raw");
		registerReserved("readcommitted");
		registerReserved("readonly");
		registerReserved("readpast");
		registerReserved("readtext");
		registerReserved("readuncommitted");
		registerReserved("readwrite");
		registerReserved("real");
		registerReserved("recursive");
		registerReserved("reference");
		registerReserved("references");
		registerReserved("release");
		registerReserved("relocate");
		registerReserved("remote");
		registerReserved("remove");
		registerReserved("rename");
		registerReserved("reorganize");
		registerReserved("repeatable");
		registerReserved("repeatableread");
		registerReserved("reserve");
		registerReserved("resizing");
		registerReserved("resource");
		registerReserved("restore");
		registerReserved("restrict");
		registerReserved("return");
		registerReserved("revoke");
		registerReserved("right");
		registerReserved("rollback");
		registerReserved("rollup");
		registerReserved("root");
		registerReserved("row");
		registerReserved("rowlock");
		registerReserved("rows");
		registerReserved("save");
		registerReserved("savepoint");
		registerReserved("schedule");
		registerReserved("scroll");
		registerReserved("secure");
		registerReserved("select");
		registerReserved("sensitive");
		registerReserved("serializable");
		registerReserved("service");
		registerReserved("session");
		registerReserved("set");
		registerReserved("setuser");
		registerReserved("share");
		registerReserved("smallint");
		registerReserved("soapaction");
		registerReserved("some");
		registerReserved("space");
		registerReserved("sqlcode");
		registerReserved("sqlstate");
		registerReserved("start");
		registerReserved("stop");
		registerReserved("subtrans");
		registerReserved("subtransaction");
		registerReserved("synchronize");
		registerReserved("syntax_error");
		registerReserved("table");
		registerReserved("tablock");
		registerReserved("tablockx");
		registerReserved("tb");
		registerReserved("temporary");
		registerReserved("then");
		registerReserved("ties");
		registerReserved("time");
		registerReserved("timestamp");
		registerReserved("tinyint");
		registerReserved("to");
		registerReserved("top");
		registerReserved("tran");
		registerReserved("transaction");
		registerReserved("transactional");
		registerReserved("transfer");
		registerReserved("tries");
		registerReserved("trigger");
		registerReserved("truncate");
		registerReserved("tsequal");
		registerReserved("unbounded");
		registerReserved("uncommitted");
		registerReserved("union");
		registerReserved("unique");
		registerReserved("uniqueidentifier");
		registerReserved("unknown");
		registerReserved("unsigned");
		registerReserved("update");
		registerReserved("updating");
		registerReserved("updlock");
		registerReserved("url");
		registerReserved("user");
		registerReserved("utc");
		registerReserved("using");
		registerReserved("validate");
		registerReserved("values");
		registerReserved("varbinary");
		registerReserved("varchar");
		registerReserved("variable");
		registerReserved("varying");
		registerReserved("virtual");
		registerReserved("view");
		registerReserved("wait");
		registerReserved("waitfor");
		registerReserved("web");
		registerReserved("when");
		registerReserved("where");
		registerReserved("while");
		registerReserved("window");
		registerReserved("with");
		registerReserved("withauto");
		registerReserved("with_cube");
		registerReserved("with_lparen");
		registerReserved("with_rollup");
		registerReserved("within");
		registerReserved("word");
		registerReserved("work");
		registerReserved("writeserver");
		registerReserved("writetext");
		registerReserved("xlock");
		registerReserved("xml");
	}
}
