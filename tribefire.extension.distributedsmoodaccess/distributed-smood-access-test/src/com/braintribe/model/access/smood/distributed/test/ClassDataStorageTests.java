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
package com.braintribe.model.access.smood.distributed.test;

import java.io.InputStream;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.braintribe.model.access.IncrementalAccess;
import com.braintribe.model.access.smood.distributed.DistributedSmoodAccess;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.processing.query.fluent.EntityQueryBuilder;
import com.braintribe.model.processing.query.fluent.SelectQueryBuilder;
import com.braintribe.model.processing.session.impl.persistence.BasicPersistenceGmSession;
import com.braintribe.model.query.EntityQuery;
import com.braintribe.model.query.OrderingDirection;
import com.braintribe.model.query.SelectQuery;
import com.braintribe.model.smoodstorage.JavaClass;
import com.braintribe.model.smoodstorage.SmoodStorage;
import com.braintribe.testing.category.SpecialEnvironment;

@Category(SpecialEnvironment.class)
public class ClassDataStorageTests extends TestBase {

	@Test
	public void defineClassTest() throws Exception {

		try {
			testUtilities.clearTables();
			
			IncrementalAccess access = configuration.accessWithoutInitialData();
			DistributedSmoodAccess dsa = (DistributedSmoodAccess) access;
			
			Set<String> qualifiedNames = dsa.getQualifiedNamesOfStoredClasses();
			Assert.assertEquals(0, qualifiedNames.size());
			
			String qualifiedName = TestBase.class.getName();
			String classAsPath = qualifiedName.replace('.', '/') + ".class";
			InputStream inputStream = TestBase.class.getClassLoader().getResourceAsStream(classAsPath);
			dsa.storeClass(qualifiedName, inputStream, null);
			inputStream.close();
			
			qualifiedNames = dsa.getQualifiedNamesOfStoredClasses();
			Assert.assertEquals(1, qualifiedNames.size());
			String qualNameFromSet = qualifiedNames.iterator().next();
			Assert.assertEquals(qualifiedName, qualNameFromSet);
			
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	

	@Test
	public void defineClassTwiceTest() throws Exception {

		try {
			testUtilities.clearTables();
			
			IncrementalAccess access = configuration.accessWithoutInitialData();
			DistributedSmoodAccess dsa = (DistributedSmoodAccess) access;
			
			Set<String> qualifiedNames = dsa.getQualifiedNamesOfStoredClasses();
			Assert.assertEquals(0, qualifiedNames.size());
			
			// Insert 1st class
			
			String qualifiedName = TestBase.class.getName();
			String classAsPath = qualifiedName.replace('.', '/') + ".class";
			InputStream inputStream = TestBase.class.getClassLoader().getResourceAsStream(classAsPath);
			dsa.storeClass(qualifiedName, inputStream, null);
			inputStream.close();
			
			qualifiedNames = dsa.getQualifiedNamesOfStoredClasses();
			Assert.assertEquals(1, qualifiedNames.size());
			String qualNameFromSet = qualifiedNames.iterator().next();
			Assert.assertEquals(qualifiedName, qualNameFromSet);
			
			
			IncrementalAccess storage = testUtilities.getStorage();
			BasicPersistenceGmSession session = new BasicPersistenceGmSession();
			session.setIncrementalAccess(storage);
			EntityQuery query = EntityQueryBuilder.from(JavaClass.class).done();
			
			List<GenericEntity> javaClassList = session.query().entities(query).list();
			Assert.assertEquals(1, javaClassList.size());
			JavaClass javaClass = (JavaClass) javaClassList.get(0);
			Assert.assertEquals(0, javaClass.getSequenceNumber());
			
			// Insert same class again, MD5 should prevent from new entry in DB
			
			inputStream = TestBase.class.getClassLoader().getResourceAsStream(classAsPath);
			dsa.storeClass(qualifiedName, inputStream, null);
			inputStream.close();
			
			javaClassList = session.query().entities(query).list();
			Assert.assertEquals(1, javaClassList.size());
			javaClass = (JavaClass) javaClassList.get(0);
			Assert.assertEquals(0, javaClass.getSequenceNumber());
			
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	@Test
	public void defineClassTwiceNewVersionTest() throws Exception {

		try {
			testUtilities.clearTables();
			
			IncrementalAccess access = configuration.accessWithoutInitialData();
			DistributedSmoodAccess dsa = (DistributedSmoodAccess) access;
			
			Set<String> qualifiedNames = dsa.getQualifiedNamesOfStoredClasses();
			Assert.assertEquals(0, qualifiedNames.size());
			
			// Insert 1st class
			
			String qualifiedName = TestBase.class.getName();
			String classAsPath = qualifiedName.replace('.', '/') + ".class";
			InputStream inputStream = TestBase.class.getClassLoader().getResourceAsStream(classAsPath);
			dsa.storeClass(qualifiedName, inputStream, null);
			inputStream.close();
			
			qualifiedNames = dsa.getQualifiedNamesOfStoredClasses();
			Assert.assertEquals(1, qualifiedNames.size());
			String qualNameFromSet = qualifiedNames.iterator().next();
			Assert.assertEquals(qualifiedName, qualNameFromSet);
			
			
			IncrementalAccess storage = testUtilities.getStorage();
			BasicPersistenceGmSession session = new BasicPersistenceGmSession();
			session.setIncrementalAccess(storage);
			SelectQuery query = new SelectQueryBuilder()
				.from(SmoodStorage.class, "ss")
				.join("ss", SmoodStorage.classDependencies, "cd")
				.select("cd")
				.where()
					.property("cd", JavaClass.qualifiedName).eq(qualifiedName)
				.orderBy().property("cd", JavaClass.sequenceNumber).orderingDirection(OrderingDirection.descending)
				.tc()
					.pattern().entity(JavaClass.class).property(JavaClass.classData).close()
				.done();
			List<JavaClass> javaClassList = session.query().select(query).list();
			
			
			Assert.assertEquals(1, javaClassList.size());
			JavaClass javaClass = javaClassList.get(0);
			Assert.assertEquals(0, javaClass.getSequenceNumber());
			
			// Insert same class again, should create new entry in DB
			
			classAsPath = ClassDataStorageTests.class.getName().replace('.', '/') + ".class";
			inputStream = TestBase.class.getClassLoader().getResourceAsStream(classAsPath);
			dsa.storeClass(qualifiedName, inputStream, null);
			inputStream.close();
			
			javaClassList = session.query().select(query).list();
			Assert.assertEquals(1, javaClassList.size());
			javaClass = javaClassList.get(0);
			Assert.assertEquals(1, javaClass.getSequenceNumber());
			
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	@Test
	public void defineTwoClassesTest() throws Exception {

		try {
			testUtilities.clearTables();
			
			IncrementalAccess access = configuration.accessWithoutInitialData();
			DistributedSmoodAccess dsa = (DistributedSmoodAccess) access;
			
			Set<String> qualifiedNames = dsa.getQualifiedNamesOfStoredClasses();
			Assert.assertEquals(0, qualifiedNames.size());
			
			// Insert 1st class
			
			String qualifiedName = TestBase.class.getName();
			String classAsPath = qualifiedName.replace('.', '/') + ".class";
			InputStream inputStream = TestBase.class.getClassLoader().getResourceAsStream(classAsPath);
			dsa.storeClass(qualifiedName, inputStream, null);
			inputStream.close();
			
			qualifiedNames = dsa.getQualifiedNamesOfStoredClasses();
			Assert.assertEquals(1, qualifiedNames.size());
			String qualNameFromSet = qualifiedNames.iterator().next();
			Assert.assertEquals(qualifiedName, qualNameFromSet);
			
			
			IncrementalAccess storage = testUtilities.getStorage();
			BasicPersistenceGmSession session = new BasicPersistenceGmSession();
			session.setIncrementalAccess(storage);
			SelectQuery query = new SelectQueryBuilder()
				.from(SmoodStorage.class, "ss")
				.join("ss", SmoodStorage.classDependencies, "cd")
				.select("cd")
				.where()
					.property("cd", JavaClass.qualifiedName).eq(qualifiedName)
				.orderBy().property("cd", JavaClass.sequenceNumber).orderingDirection(OrderingDirection.descending)
				.tc()
					.pattern().entity(JavaClass.class).property(JavaClass.classData).close()
				.done();
			List<JavaClass> javaClassList = session.query().select(query).list();
			
			
			Assert.assertEquals(1, javaClassList.size());
			JavaClass javaClass = javaClassList.get(0);
			Assert.assertEquals(0, javaClass.getSequenceNumber());
			
			// Insert second class
			
			String qualifiedName2 = ClassDataStorageTests.class.getName();
			String classAsPath2 = qualifiedName2.replace('.', '/') + ".class";
			InputStream inputStream2 = ClassDataStorageTests.class.getClassLoader().getResourceAsStream(classAsPath2);
			dsa.storeClass(qualifiedName2, inputStream2, null);
			inputStream2.close();
			
			query = new SelectQueryBuilder()
				.from(SmoodStorage.class, "ss")
				.join("ss", SmoodStorage.classDependencies, "cd")
				.select("cd")
				.orderBy().property("cd", JavaClass.sequenceNumber).orderingDirection(OrderingDirection.descending)
				.tc()
					.pattern().entity(JavaClass.class).property(JavaClass.classData).close()
				.done();
			javaClassList = session.query().select(query).list();
			
			Assert.assertEquals(2, javaClassList.size());
			for (JavaClass jc : javaClassList) {
				Assert.assertEquals(0, jc.getSequenceNumber());
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
}
