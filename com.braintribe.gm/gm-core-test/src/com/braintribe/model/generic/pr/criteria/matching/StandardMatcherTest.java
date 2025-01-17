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
package com.braintribe.model.generic.pr.criteria.matching;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.braintribe.common.attribute.common.UserInfo;
import com.braintribe.common.attribute.common.UserInfoAttribute;
import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.pr.criteria.AclCriterion;
import com.braintribe.model.generic.pr.criteria.EntityCriterion;
import com.braintribe.model.generic.pr.criteria.JokerCriterion;
import com.braintribe.model.generic.pr.criteria.RootCriterion;
import com.braintribe.model.generic.pr.criteria.TraversingCriterion;
import com.braintribe.model.generic.reflection.AbstractProperty;
import com.braintribe.model.generic.reflection.StandardTraversingContext;
import com.braintribe.model.generic.reflection.cloning.model.AclTcEntity;
import com.braintribe.model.generic.reflection.cloning.model.City;
import com.braintribe.utils.collection.impl.AttributeContexts;

/**
 * Tests for {@link StandardMatcher}
 * 
 * @author peter.gazdik
 */
public class StandardMatcherTest {

	private final StandardMatcher matcher = newMatcher();

	private StandardMatcher newMatcher() {
		StandardMatcher result = new StandardMatcher();
		result.setCheckOnlyProperties(false);
		return result;
	}

	private final StandardTraversingContext context = new StandardTraversingContext();

	@Test
	public void testJoker() throws Exception {
		setMatcherTc(JokerCriterion.T.create());

		pushRoot("string");

		assertMatches(true);
	}

	@Test
	public void testAcl_NonAclEntity() throws Exception {
		setMatcherTc(aclWriteCriterion());

		City nonAcl = City.T.create();

		pushRoot(nonAcl);
		assertMatches(false);

		pushEntity(nonAcl);
		assertMatches(false);
	}

	@Test
	public void testAcl_NoContext() throws Exception {
		setMatcherTc(aclWriteCriterion());

		AclTcEntity aclEntity = AclTcEntity.T.create();
		aclEntity.setOwner("AclOwner");

		pushRoot(aclEntity);
		assertMatches(false);

		pushEntity(aclEntity);
		assertMatches(false);
	}

	@Test
	public void testAcl_Matches() throws Exception {
		setMatcherTc(aclWriteCriterion());

		AclTcEntity aclEntity = AclTcEntity.T.create();
		aclEntity.setOwner("AclOwner");

		UserInfo ui = UserInfo.of("AclOwner", null);
		AttributeContexts.push(AttributeContexts.derivePeek().set(UserInfoAttribute.class, ui).build());

		pushRoot(aclEntity);
		assertMatches(false);

		pushEntity(aclEntity);
		assertMatches(true); // HERE WE FINALLY MATCH

		pushProperty("name");
		assertMatches(false); // we don't match properties, even if on matching ACL entity
	}

	private AclCriterion aclWriteCriterion() {
		AclCriterion acl = AclCriterion.T.create();
		acl.setOperation("write");
		return acl;
	}

	private void setMatcherTc(TraversingCriterion tc) {
		matcher.setCriterion(tc);
	}

	private void pushRoot(Object o) {
		RootCriterion rootCriterion = RootCriterion.T.createPlain();
		rootCriterion.setTypeSignature(GMF.getTypeReflection().getType(o).getTypeSignature());
		context.pushTraversingCriterion(rootCriterion, o);

	}

	private void pushEntity(GenericEntity entity) {
		EntityCriterion ec = EntityCriterion.T.createPlainRaw();
		ec.setTypeSignature(entity.entityType().getTypeSignature());

		context.pushTraversingCriterion(ec, entity);
	}

	private void pushProperty(String propertyName) {
		GenericEntity entity = (GenericEntity) context.getObjectStack().peek();
		AbstractProperty p = (AbstractProperty) entity.entityType().getProperty(propertyName);

		context.pushTraversingCriterion(p.acquireCriterion(), p.get(entity));
	}

	private void assertMatches(boolean expected) {
		assertThat(matcher.matches(context)).isEqualTo(expected);
	}

}
