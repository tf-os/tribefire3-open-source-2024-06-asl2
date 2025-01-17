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
package com.braintribe.coding;

import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.braintribe.cc.lcd.CodingMap;
import com.braintribe.cc.lcd.CodingSet;
import com.braintribe.cc.lcd.HashSupportWrapperCodec;

public class HashSupportCodecTest {
	private final Bean bean1 = new Bean("one");
	private final Bean bean2 = new Bean("two");
	/* private Bean bean3 = new Bean( "three"); private Bean bean4 = new Bean( "four"); private Bean bean5 = new Bean( "five"); */

	private final Bean dupBean1 = new Bean("one");

	private class HashSupportCodecImpl extends HashSupportWrapperCodec<Bean> {

		@Override
		protected int entityHashCode(final Bean e) {
			return e.getName().hashCode();
		}

		@Override
		protected boolean entityEquals(final Bean e1, final Bean e2) {
			return e1.getName().equals(e2.getName());
		}
	}

	@Test
	public void testSet() {
		final Set<Bean> set = CodingSet.createHashSetBased(new HashSupportCodecImpl());

		set.add(this.bean1);
		set.add(this.bean2);
		final int sizeBefore = set.size();
		set.add(this.dupBean1);
		final int sizeAfter = set.size();

		Assert.assertTrue("size has grown", sizeBefore == sizeAfter);

		Assert.assertTrue("cannot find bean by pointer", set.contains(this.bean1));
		Assert.assertTrue("cannot find bean by value", set.contains(this.dupBean1));
	}

	@Test
	public void testMap() {
		final Map<Bean, String> map = CodingMap.createHashMapBased(new HashSupportCodecImpl());
		map.put(this.bean1, "bean 1");
		map.put(this.bean2, "bean 2");

		Assert.assertTrue("cannot find bean by pointer", map.containsKey(this.bean1));
		Assert.assertTrue("cannot find bean by value", map.containsKey(this.dupBean1));

		final String value = map.get(this.bean1);
		final String value1 = map.get(this.dupBean1);
		Assert.assertTrue("values don't match", value.equalsIgnoreCase(value1));
		final int sizeBefore = map.size();
		map.put(this.dupBean1, "redefined");
		final int sizeAfter = map.size();
		Assert.assertTrue("map has grown at redefine", sizeBefore == sizeAfter);

		Assert.assertTrue("values are unchanged ", !value.equalsIgnoreCase(map.get(this.dupBean1)));
	}

}
