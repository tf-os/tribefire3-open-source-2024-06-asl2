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
package com.braintribe.model.processing.deployment.hibernate.data;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * 
 */

public interface Person extends GenericEntity {

	EntityType<Person> T = EntityTypes.T(Person.class);

	// @formatter:off
	String getName();
	void setName(String name);

	List<String> getStrList();
	void setStrList(List<String> strList);

	Set<String> getStrSet();
	void setStrSet(Set<String> strSet);

	List<Car> getCarList();
	void setCarList(List<Car> carList);

	Set<Car> getCarSet();
	void setCarSet(Set<Car> carSet);

	List<Person> getPersonList();
	void setPersonList(List<Person> personList);

	Set<Person> getPersonSet();
	void setPersonSet(Set<Person> personSet);

	// ##########################################################################################
	// ## . . . . . . . . . . . . . . Other Simple Types . . . . . . . . . . . . . . . . . . . ##
	// ##########################################################################################

	List<Integer> getIntList();
	void setIntList(List<Integer> intList);

	List<Long> getLongList();
	void setLongList(List<Long> longList);

	List<Float> getFloatList();
	void setFloatList(List<Float> floatList);

	List<Double> getDoubleList();
	void setDoubleList(List<Double> doubleList);

	List<Date> getDateList();
	void setDateList(List<Date> dateList);
	// @formatter:on

}
