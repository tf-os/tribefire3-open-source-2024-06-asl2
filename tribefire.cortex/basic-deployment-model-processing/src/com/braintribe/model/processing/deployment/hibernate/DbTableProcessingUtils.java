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
package com.braintribe.model.processing.deployment.hibernate;

import static com.braintribe.model.generic.reflection.SimpleTypes.TYPE_BOOLEAN;
import static com.braintribe.model.generic.reflection.SimpleTypes.TYPE_DATE;
import static com.braintribe.model.generic.reflection.SimpleTypes.TYPE_DECIMAL;
import static com.braintribe.model.generic.reflection.SimpleTypes.TYPE_DOUBLE;
import static com.braintribe.model.generic.reflection.SimpleTypes.TYPE_FLOAT;
import static com.braintribe.model.generic.reflection.SimpleTypes.TYPE_INTEGER;
import static com.braintribe.model.generic.reflection.SimpleTypes.TYPE_LONG;
import static com.braintribe.model.generic.reflection.SimpleTypes.TYPE_STRING;

import java.sql.Types;

import com.braintribe.model.dbs.DbColumn;
import com.braintribe.model.dbs.DbTable;
import com.braintribe.model.generic.reflection.SimpleType;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.utils.StringTools;

public class DbTableProcessingUtils {

	static SimpleType getGmTypeFromSqlType(int type, String typeName) {
		switch (type) {
			case Types.BIT:
			case Types.BOOLEAN:
				return TYPE_BOOLEAN;

			case Types.TINYINT:
			case Types.SMALLINT:
			case Types.INTEGER:
				return TYPE_INTEGER;

			case Types.BIGINT:
				return TYPE_LONG;

			case Types.FLOAT:
				return TYPE_FLOAT;

			case Types.REAL:
			case Types.DOUBLE:
			case Types.NUMERIC:
				return TYPE_DOUBLE;

			case Types.DECIMAL:
				return TYPE_DECIMAL;

			case Types.DATE:
			case Types.TIME:
			case Types.TIMESTAMP:
				return TYPE_DATE;

			case Types.CHAR:
			case Types.VARCHAR:
			case Types.LONGVARCHAR:
			case Types.NCHAR:
			case Types.NVARCHAR:
			case Types.LONGNVARCHAR:
				return TYPE_STRING;

			case Types.OTHER:
				return getGmTypeFromTypeNameIfPossible(typeName);

			case Types.BINARY:
			case Types.VARBINARY:
			case Types.LONGVARBINARY:
			case Types.NULL:
			case Types.JAVA_OBJECT:
			case Types.DISTINCT:
			case Types.STRUCT:
			case Types.ARRAY:
			case Types.BLOB:
			case Types.CLOB:
			case Types.REF:
			case Types.DATALINK:
			case Types.ROWID:
			case Types.NCLOB:
			case Types.SQLXML:
				return null;
		}

		return null;
	}

	static SimpleType getGmTypeFromTypeNameIfPossible(String typeName) {
		typeName = normalizeTypeName(typeName);

		switch (typeName) {
			case "timestamp":
				return TYPE_DATE;

			case "varchar2":
			case "nvarchar2":
				return TYPE_STRING;
		}

		return null;
	}

	private static String normalizeTypeName(String typeName) {
		if (typeName == null) {
			return "";
		}

		int pos = typeName.indexOf('(');
		if (pos > 0) {
			// for cases like TIMESTAMP(4)
			typeName = typeName.substring(0, pos);
		}

		return typeName.toLowerCase();
	}

	public static String getEntitySignatureFrom(GmMetaModel metaModel, DbTable dbTable) {
		String groupId = extractGroupId(metaModel);
		String packageName = groupId + ".";

		String schema = null;
		if (dbTable.getSchema() != null)
			schema = dbTable.getSchema().replace(" ", "");

		if (schema != null) {
			packageName += "$" + schema + ".";
		}

		return packageName + camelCase(dbTable.getName(), true);
	}

	public static String getPropertyName(DbColumn dbColumn) {
		String columnName = dbColumn.getName();

		if (dbColumn.getReferencedTable() != null && columnName.toLowerCase().endsWith("_id")) {
			columnName = columnName.substring(0, columnName.length() - 3);
		}

		columnName = ensueUsingJavaLettersOnly(columnName);
		columnName = camelCase(columnName, false);
		columnName = escapeIfStartsWithNumber(columnName);
		
		return columnName;
	}

	private static String ensueUsingJavaLettersOnly(String s) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			char newC = Character.isJavaIdentifierPart(c) ? c : ' ';
			sb.append(newC);
		}

		return sb.toString();
	}

	private static String escapeIfStartsWithNumber(String s) {
		char firstChar = s.charAt(0);
		return (firstChar >= '0' && firstChar <= '9') || (firstChar == '_') ? ("_" + s) : s;
	}

	private static String camelCase(String s, boolean capitalizeFirst) {
		String[] strings = s.split("_|\\s");

		for (int i = 0; i < strings.length; i++) {
			if (capitalizeFirst || (i > 0)) {
				strings[i] = StringTools.capitalize(strings[i]);

			} else if (!capitalizeFirst && i == 0) {
				strings[i] = uncapitalizeInitialCapitalSequence(strings[i]);
			}
		}

		return StringTools.join("", strings);
	}

	private static String uncapitalizeInitialCapitalSequence(String s) {
		int len = s.length();

		if (len < 2) {
			return s.toLowerCase();
		}

		int i = 1;
		while (i < len && Character.isUpperCase(s.charAt(i))) {
			i++;
		}

		return s.substring(0, i).toLowerCase() + s.substring(i);
	}

	private static final String DEFAULT_GROUP_ID = "com.braintribe";

	private static String extractGroupId(GmMetaModel metaModel) {
		String name = metaModel.getName();
		if (name == null) {
			return DEFAULT_GROUP_ID;
		}
		
		int index = name.lastIndexOf(':');
		
		String groupId = index != -1? name.substring(0, index): name;

		return groupId;
	}

}
