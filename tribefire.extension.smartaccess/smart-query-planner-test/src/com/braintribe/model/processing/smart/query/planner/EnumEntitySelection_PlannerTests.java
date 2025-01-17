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
package com.braintribe.model.processing.smart.query.planner;

import org.junit.Test;

import com.braintribe.model.accessdeployment.smart.meta.conversion.EnumToSimpleValue;
import com.braintribe.model.accessdeployment.smart.meta.conversion.EnumToString;
import com.braintribe.model.processing.query.smart.test.model.accessB.EnumEntityB;
import com.braintribe.model.processing.query.smart.test.model.smart.SmartEnumEntityB;
import com.braintribe.model.processing.smart.query.planner.base.AbstractSmartQueryPlannerTests;
import com.braintribe.model.query.SelectQuery;
import com.braintribe.model.queryplan.set.Projection;
import com.braintribe.model.smartqueryplan.value.ConvertedValue;

/**
 * 
 */
public class EnumEntitySelection_PlannerTests extends AbstractSmartQueryPlannerTests {

	@Test
	public void enumSelection_Identity() {
		// @formatter:off
		SelectQuery selectQuery = query()		
				.from(SmartEnumEntityB.T, "e")
				.select("e", "enumIdentity")
				.done();
		// @formatter:on

		runTest(selectQuery);

		// @formatter:off
		assertQueryPlan()
		.hasType(Projection.T)
			.whereProperty("operand")
				.isDelegateQuerySet("accessB")
				.whereDelegateQuery()
					.whereFroms(1).whereElementAt(0).isFrom(EnumEntityB.T)
					.whereSelection(1)
						.whereElementAt(0).castedToString().isPropertyOperand("enumIdentity")
				.endQuery()
				.whereProperty("scalarMappings").isListWithSize(1)
					.whereElementAt(0)
						.isScalarMappingAndValue(0).isTupleComponent_(0).close()
				.close()
			.close()
			.whereProperty("values").isListWithSize(1)
				.whereElementAt(0).hasType(ConvertedValue.T)
					.whereProperty("conversion")
					.hasType(EnumToString.T)
					.whereProperty("valueMappings").isNull()
		;
		// @formatter:on
	}

	@Test
	public void enumSelection_DelegateEnumConversion() {
		// @formatter:off
		SelectQuery selectQuery = query()		
				.from(SmartEnumEntityB.T, "e")
				.select("e", "enumAsDelegate")
				.done();
		// @formatter:on

		runTest(selectQuery);

		// @formatter:off
		assertQueryPlan()
		.hasType(Projection.T)
			.whereProperty("operand")
				.isDelegateQuerySet("accessB")
				.whereDelegateQuery()
					.whereFroms(1).whereElementAt(0).isFrom(EnumEntityB.T)
					.whereSelection(1)
						.whereElementAt(0).castedToString().isPropertyOperand("enumAsDelegate")
				.endQuery()
				.whereProperty("scalarMappings").isListWithSize(1)
					.whereElementAt(0)
						.isScalarMappingAndValue(0).isTupleComponent_(0).close()
				.close()
			.close()
			.whereProperty("values").isListWithSize(1)
				.whereElementAt(0).hasType(ConvertedValue.T)
					.whereProperty("conversion")
					.hasType(EnumToSimpleValue.T)
					.whereProperty("valueMappings").isNotNull()
		;
		// @formatter:on
	}

	@Test
	public void enumSelection_StringConversion() {
		// @formatter:off
		SelectQuery selectQuery = query()		
				.from(SmartEnumEntityB.T, "e")
				.select("e", "enumAsString")
				.done();
		// @formatter:on

		runTest(selectQuery);

		// @formatter:off
		assertQueryPlan()
		.hasType(Projection.T)
			.whereProperty("operand")
				.isDelegateQuerySet("accessB")
				.whereDelegateQuery()
					.whereFroms(1).whereElementAt(0).isFrom(EnumEntityB.T)
					.whereSelection(1)
						.whereElementAt(0).isPropertyOperand("enumAsString")
				.endQuery()
				.whereProperty("scalarMappings").isListWithSize(1)
					.whereElementAt(0)
						.isScalarMappingAndValue(0).isTupleComponent_(0).close()
				.close()
			.close()
			.whereProperty("values").isListWithSize(1)
				.whereElementAt(0).hasType(ConvertedValue.T)
					.whereProperty("conversion")
					.hasType(EnumToSimpleValue.T)
					.whereProperty("valueMappings").isNotNull()
		;
		// @formatter:on
	}

	@Test
	public void enumSelection_CustomConversion() {
		// @formatter:off
		SelectQuery selectQuery = query()		
				.from(SmartEnumEntityB.T, "e")
				.select("e", "enumCustomConverted")
				.done();
		// @formatter:on

		runTest(selectQuery);

		// @formatter:off
		assertQueryPlan()
		.hasType(Projection.T)
			.whereProperty("operand")
				.isDelegateQuerySet("accessB")
				.whereDelegateQuery()
					.whereFroms(1).whereElementAt(0).isFrom(EnumEntityB.T)
					.whereSelection(1)
						.whereElementAt(0).isPropertyOperand("enumCustomConverted")
				.endQuery()
				.whereProperty("scalarMappings").isListWithSize(1)
					.whereElementAt(0)
						.isScalarMappingAndValue(0).isTupleComponent_(0).close()
				.close()
			.close()
			.whereProperty("values").isListWithSize(1)
				.whereElementAt(0).hasType(ConvertedValue.T)
					.whereProperty("conversion")
					.hasType(EnumToSimpleValue.T)
					.whereProperty("valueMappings").isNotNull()
		;
		// @formatter:on
	}

	@Test
	public void enumSelection_StringConversion_Set() {
		// @formatter:off
		SelectQuery selectQuery = query()		
				.from(SmartEnumEntityB.T, "e")
				.select("e", "enumAsStringSet")
				.done();
		// @formatter:on

		runTest(selectQuery);

		// @formatter:off
		assertQueryPlan()
		.hasType(Projection.T)
			.whereProperty("operand")
				.isDelegateQuerySet("accessB")
				.whereDelegateQuery()
					.whereFroms(1).whereElementAt(0).isFrom(EnumEntityB.T)
					.whereSelection(1)
						.whereElementAt(0).isSourceOnlyPropertyOperand().whereSource().isJoin("enumAsStringSet")
				.endQuery()
				.whereProperty("scalarMappings").isListWithSize(1)
					.whereElementAt(0)
						.isScalarMappingAndValue(0).isTupleComponent_(0).close()
				.close()
			.close()
			.whereProperty("values").isListWithSize(1)
				.whereElementAt(0).hasType(ConvertedValue.T)
					.whereProperty("conversion")
					.hasType(EnumToSimpleValue.T)
					.whereProperty("valueMappings").isNotNull()
		;
		// @formatter:on
	}

	@Test
	public void enumSelection_StringConversion_MapValue() {
		// @formatter:off
		SelectQuery selectQuery = query()		
				.from(SmartEnumEntityB.T, "e")
				.select("e", "enumAsStringMap")
				.done();
		// @formatter:on

		runTest(selectQuery);

		// @formatter:off
		assertQueryPlan()
		.hasType(Projection.T)
			.whereProperty("operand")
				.isDelegateQuerySet("accessB")
				.whereDelegateQuery()
					.whereFroms(1).whereElementAt(0).isFrom(EnumEntityB.T)
					.whereSelection(1)
						.whereElementAt(0).isSourceOnlyPropertyOperand().whereSource().isJoin("enumAsStringMap")
				.endQuery()
				.whereProperty("scalarMappings").isListWithSize(1)
					.whereElementAt(0)
						.isScalarMappingAndValue(0).isTupleComponent_(0).close()
				.close()
			.close()
			.whereProperty("values").isListWithSize(1)
				.whereElementAt(0).hasType(ConvertedValue.T)
					.whereProperty("conversion")
					.hasType(EnumToSimpleValue.T)
					.whereProperty("valueMappings").isNotNull()
		;
		// @formatter:on
	}

	@Test
	public void enumSelection_StringConversion_MapKey() {
		// @formatter:off
		SelectQuery selectQuery = query()		
				.from(SmartEnumEntityB.T, "e")
					.join("e", "enumAsStringMap", "m")
				.select().mapKey("m")
				.done();
		// @formatter:on

		runTest(selectQuery);

		// @formatter:off
		assertQueryPlan()
		.hasType(Projection.T)
			.whereProperty("operand")
				.isDelegateQuerySet("accessB")
				.whereDelegateQuery()
					.whereFroms(1).whereElementAt(0).isFrom(EnumEntityB.T)
					.whereSelection(2)
						.whereElementAt(0).isSourceOnlyPropertyOperand().whereSource().isJoin("enumAsStringMap").close(2)
						.whereElementAt(1).isMapKeyOnJoin("enumAsStringMap").close()
				.endQuery()
				.whereProperty("scalarMappings").isListWithSize(2)
					.whereElementAt(0)
						.isScalarMappingAndValue(0).isTupleComponent_(0).close()
					.whereElementAt(1)
						.isScalarMappingAndValue(1).isTupleComponent_(1).close()
				.close()
			.close()
			.whereProperty("values").isListWithSize(1)
				.whereElementAt(0).hasType(ConvertedValue.T)
					.whereProperty("conversion")
					.hasType(EnumToSimpleValue.T)
					.whereProperty("valueMappings").isNotNull()
		;
		// @formatter:on
	}

	@Test
	public void enumSelection_StringConversion_MapKey_WhereValueIsEntity() {
		// @formatter:off
		SelectQuery selectQuery = query()		
				.from(SmartEnumEntityB.T, "e")
					.join("e", "enumAsStringMapKey", "m")
				.select().mapKey("m")
				.done();
		// @formatter:on

		runTest(selectQuery);

		// @formatter:off
		assertQueryPlan()
		.hasType(Projection.T)
			.whereProperty("operand")
				.isDelegateQuerySet("accessB")
				.whereDelegateQuery()
					.whereFroms(1).whereElementAt(0).isFrom(EnumEntityB.T)
					.whereSelection(1)
						.whereElementAt(0).isMapKeyOnJoin("enumAsStringMapKey").close().close()
				.endQuery()
				.whereProperty("scalarMappings").isListWithSize(1)
					.whereElementAt(0)
						.isScalarMappingAndValue(0).isTupleComponent_(0).close()
				.close()
			.close()
			.whereProperty("values").isListWithSize(1)
				.whereElementAt(0).hasType(ConvertedValue.T)
					.whereProperty("conversion")
					.hasType(EnumToSimpleValue.T)
					.whereProperty("valueMappings").isNotNull()
		;
		// @formatter:on
	}

	@Test
	public void enumSelection_Delegate_MapValue() {
		// @formatter:off
		SelectQuery selectQuery = query()		
				.from(SmartEnumEntityB.T, "e")
				.select("e", "enumAsDelegateMap")
				.done();
		// @formatter:on

		runTest(selectQuery);

		// @formatter:off
		assertQueryPlan()
		.hasType(Projection.T)
			.whereProperty("operand")
				.isDelegateQuerySet("accessB")
				.whereDelegateQuery()
					.whereFroms(1).whereElementAt(0).isFrom(EnumEntityB.T)
					.whereSelection(1)
						.whereElementAt(0).castedToString().isSourceOnlyPropertyOperand().whereSource().isJoin("enumAsDelegateMap")
				.endQuery()
				.whereProperty("scalarMappings").isListWithSize(1)
					.whereElementAt(0)
						.isScalarMappingAndValue(0).isTupleComponent_(0).close()
				.close()
			.close()
			.whereProperty("values").isListWithSize(1)
				.whereElementAt(0).hasType(ConvertedValue.T)
					.whereProperty("conversion")
					.hasType(EnumToSimpleValue.T)
					.whereProperty("valueMappings").isNotNull()
		;
		// @formatter:on
	}

	@Test
	public void enumSelection_Delegate_MapKey() {
		// @formatter:off
		SelectQuery selectQuery = query()		
				.from(SmartEnumEntityB.T, "e")
					.join("e", "enumAsDelegateMap", "m")
				.select().mapKey("m")
				.done();
		// @formatter:on

		runTest(selectQuery);

		// @formatter:off
		assertQueryPlan()
		.hasType(Projection.T)
			.whereProperty("operand")
				.isDelegateQuerySet("accessB")
				.whereDelegateQuery()
					.whereFroms(1).whereElementAt(0).isFrom(EnumEntityB.T)
					.whereSelection(2)
						.whereElementAt(0).castedToString().isSourceOnlyPropertyOperand().whereSource().isJoin("enumAsDelegateMap").close(3)
						.whereElementAt(1).castedToString().isMapKeyOnJoin("enumAsDelegateMap")
				.endQuery()
				.whereProperty("scalarMappings").isListWithSize(2)
					.whereElementAt(0)
						.isScalarMappingAndValue(0).isTupleComponent_(0).close()
					.whereElementAt(1)
						.isScalarMappingAndValue(1).isTupleComponent_(1).close()
				.close()
			.close()
			.whereProperty("values").isListWithSize(1)
				.whereElementAt(0).hasType(ConvertedValue.T)
					.whereProperty("conversion")
					.hasType(EnumToSimpleValue.T)
					.whereProperty("valueMappings").isNotNull()
		;
		// @formatter:on
	}

	@Test
	public void enumSelection_Delegate_MapKey_WhereValueIsEntity() {
		// @formatter:off
		SelectQuery selectQuery = query()		
				.from(SmartEnumEntityB.T, "e")
					.join("e", "enumAsDelegateMapKey", "m")
				.select().mapKey("m")
				.done();
		// @formatter:on

		runTest(selectQuery);

		// @formatter:off
		assertQueryPlan()
		.hasType(Projection.T)
			.whereProperty("operand")
				.isDelegateQuerySet("accessB")
				.whereDelegateQuery()
					.whereFroms(1).whereElementAt(0).isFrom(EnumEntityB.T)
					.whereSelection(1)
						.whereElementAt(0).castedToString().isMapKeyOnJoin("enumAsDelegateMapKey").close()
				.endQuery()
				.whereProperty("scalarMappings").isListWithSize(1)
					.whereElementAt(0)
						.isScalarMappingAndValue(0).isTupleComponent_(0).close()
				.close()
			.close()
			.whereProperty("values").isListWithSize(1)
				.whereElementAt(0).hasType(ConvertedValue.T)
					.whereProperty("conversion")
					.hasType(EnumToSimpleValue.T)
					.whereProperty("valueMappings").isNotNull()
		;
		// @formatter:on
	}
}
