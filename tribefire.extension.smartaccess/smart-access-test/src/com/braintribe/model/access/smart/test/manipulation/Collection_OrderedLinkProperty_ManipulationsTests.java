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
package com.braintribe.model.access.smart.test.manipulation;

import static com.braintribe.utils.lcd.CollectionTools2.asList;
import static com.braintribe.utils.lcd.CollectionTools2.newList;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.processing.query.fluent.SelectQueryBuilder;
import com.braintribe.model.processing.query.smart.test.model.accessB.PersonItemOrderedLink;
import com.braintribe.model.processing.query.smart.test.model.smart.SmartItem;
import com.braintribe.model.processing.query.smart.test.model.smart.SmartPersonA;
import com.braintribe.model.query.SelectQuery;
import com.braintribe.utils.junit.assertions.BtAssertions;

public class Collection_OrderedLinkProperty_ManipulationsTests extends AbstractManipulationsTests {

	private SmartPersonA sp;
	private SmartItem si1, si2, si3, si4;
	private final List<String> itemNames = newList();

	@Before
	public void prepareData() throws Exception {
		sp = newSmartPersonA();
		sp.setNameA("sp");

		si1 = newSmartItem();
		si1.setNameB("si1");
		si2 = newSmartItem();
		si2.setNameB("si2");
		si3 = newSmartItem();
		si3.setNameB("si3");
		si4 = newSmartItem();
		si4.setNameB("si4");

		commit();

		BtAssertions.assertThat(countPersonA()).isEqualTo(1);
		BtAssertions.assertThat(countItemB()).isEqualTo(4);
	}

	// ####################################
	// ## . . . . Change Value . . . . . ##
	// ####################################

	@Test
	public void changeEmptyListValue() throws Exception {
		sp.setOrderedLinkItems(asList(si1, si2, si1));
		commit();

		loadItemNames();

		BtAssertions.assertThat(itemNames).containsExactly("si1", "si2", "si1");
	}

	@Test
	public void changeNonEmptySetValue() throws Exception {
		sp.setOrderedLinkItems(asList(si1, si2, si3));
		commit();

		sp.setOrderedLinkItems(asList(si3, si4));
		commit();

		loadItemNames();

		BtAssertions.assertThat(itemNames).isNotEmpty().hasSize(2).containsOnly("si3", "si4");
	}

	// ####################################
	// ## . . . . . . Insert . . . . . . ##
	// ####################################

	@Test
	public void insert() throws Exception {
		sp.setOrderedLinkItems(new ArrayList<SmartItem>());
		sp.getOrderedLinkItems().add(si1);
		commit();

		loadItemNames();

		BtAssertions.assertThat(itemNames).isNotEmpty().hasSize(1).containsOnly("si1");
	}

	@Test
	public void insertOnPosition() throws Exception {
		sp.setOrderedLinkItems(new ArrayList<SmartItem>(asList(si1, si1)));
		commit();
		
		sp.getOrderedLinkItems().add(1, si2);
		commit();
		
		loadItemNames();
		
		BtAssertions.assertThat(itemNames).containsExactly("si1", "si2", "si1");
	}
	
	@Test
	public void bulkInsert() throws Exception {
		sp.setOrderedLinkItems(new ArrayList<SmartItem>());
		sp.getOrderedLinkItems().addAll(asList(si1, si2, si3));
		commit();

		loadItemNames();

		BtAssertions.assertThat(itemNames).isNotEmpty().hasSize(3).containsOnly("si1", "si2", "si3");
	}

	// ####################################
	// ## . . . . . . Remove . . . . . . ##
	// ####################################

	@Test
	public void remove() throws Exception {
		insert();

		sp.getOrderedLinkItems().remove(si1);
		commit();

		loadItemNames();

		BtAssertions.assertThat(itemNames).isEmpty();
	}

	@Test
	public void bulkRemove() throws Exception {
		bulkInsert();

		sp.getOrderedLinkItems().removeAll(asList(si2, si3));
		commit();

		loadItemNames();

		BtAssertions.assertThat(itemNames).hasSize(1).containsOnly("si1");
	}

	@Test
	public void clear() throws Exception {
		bulkInsert();

		sp.getOrderedLinkItems().clear();
		commit();

		loadItemNames();

		BtAssertions.assertThat(itemNames).isEmpty();
	}

	private void loadItemNames() {
		itemNames.clear();

		for (PersonItemOrderedLink link: listAllByProperty(PersonItemOrderedLink.class, "personName", sp.getNameA(), "itemIndex")) {
			itemNames.add(link.getItemName());
		}
	}

	private <T extends GenericEntity> List<T> listAllByProperty(Class<T> clazz, String propertyName, Object propertyValue,
			String indexColumn) {

		SelectQuery query = new SelectQueryBuilder().from(clazz, "e").select("e").where().property("e", propertyName).eq(propertyValue)
				.orderBy().property("e", indexColumn).done();

		try {
			return cast(smoodB.query(query).getResults());

		} catch (Exception e) {
			throw new RuntimeException("Query evaluation failed for: " + smoodB, e);
		}
	}
}
