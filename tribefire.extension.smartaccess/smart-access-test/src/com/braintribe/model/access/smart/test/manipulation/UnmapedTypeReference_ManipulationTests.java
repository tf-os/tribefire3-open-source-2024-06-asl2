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

import static com.braintribe.utils.lcd.CollectionTools2.first;
import static com.braintribe.utils.lcd.CollectionTools2.isEmpty;
import static com.braintribe.utils.lcd.CollectionTools2.newSet;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.braintribe.model.processing.query.smart.test.model.accessA.special.ReaderA;
import com.braintribe.model.processing.query.smart.test.model.accessA.special.ReaderBookLink;
import com.braintribe.model.processing.query.smart.test.model.accessA.special.ReaderBookSetLink;
import com.braintribe.model.processing.query.smart.test.model.smart.special.SmartBookB;
import com.braintribe.model.processing.query.smart.test.model.smart.special.SmartPublicationB;
import com.braintribe.model.processing.query.smart.test.model.smart.special.SmartReaderA;
import com.braintribe.utils.junit.assertions.BtAssertions;

/**
 *  
 */
public class UnmapedTypeReference_ManipulationTests extends AbstractManipulationsTests {

	private SmartBookB b;
	private SmartReaderA sr;

	@Before
	public void prepareData() {
		b = newSmartBookB();
		b.setTitle("titleA");

		sr = newSmartReaderA();
		sr.setName("r");
		commit();
	}

	// KPA

	@Test
	public void changeEntityValue() throws Exception {
		sr.setFavoritePublication(b);
		commit();

		ReaderA r = readerAByName("r");
		BtAssertions.assertThat(r.getFavoritePublicationTitle()).isEqualTo(b.getTitle());
	}

	// IKPA

	@Test
	public void changeEntityValue_Ikpa() throws Exception {
		b.setFavoriteReader(sr);
		commit();

		 ReaderA r = readerAByName("r");
		 BtAssertions.assertThat(r.getIkpaPublicationTitle()).isEqualTo(b.getTitle());
	}

	// LPA - Entity

	@Test
	public void changeLpaEntityValue() throws Exception {
		sr.setFavoritePublicationLink(b);
		commit();

		BtAssertions.assertThat(loadFavoritePublicationTitleFor(sr)).isEqualTo(b.getTitle());
	}

	private String loadFavoritePublicationTitleFor(SmartReaderA sr) {
		List<ReaderBookLink> list = listAllByProperty(ReaderBookLink.class, "readerName", sr.getName(), smoodA);
		if (list.size() > 1) {
			Assert.fail("Cannot have more than 1 value for PerstonItemLink. This link represents an entity property.");
		}

		return isEmpty(list) ? null : first(list).getPublicationTitle();
	}

	// LPA - Collection

	@Test
	public void addLpaSetValue() throws Exception {
		sr.setFavoritePublicationLinks(new HashSet<SmartPublicationB>());
		sr.getFavoritePublicationLinks().add(b);
		commit();

		BtAssertions.assertThat(loadFavoritePublicationTitlesFor(sr)).containsOnly(b.getTitle());
	}

	private Set<String> loadFavoritePublicationTitlesFor(SmartReaderA sr) {
		List<ReaderBookSetLink> list = listAllByProperty(ReaderBookSetLink.class, "readerName", sr.getName(), smoodA);

		Set<String> result = newSet();
		for (ReaderBookSetLink rbsl: list) {
			result.add(rbsl.getPublicationTitle());
		}

		return result;
	}

	protected ReaderA readerAByName(String name) {
		return selectByProperty(ReaderA.class, "name", name, smoodA);
	}

}
