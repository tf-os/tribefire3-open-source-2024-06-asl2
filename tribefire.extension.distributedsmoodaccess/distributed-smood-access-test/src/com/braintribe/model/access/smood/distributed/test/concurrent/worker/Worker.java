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
package com.braintribe.model.access.smood.distributed.test.concurrent.worker;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import com.braintribe.model.access.IncrementalAccess;
import com.braintribe.model.access.smood.distributed.test.concurrent.entities.MultiExtensionType;
import com.braintribe.model.access.smood.distributed.test.concurrent.entities.SingleExtensionType;
import com.braintribe.model.access.smood.distributed.test.wire.contract.DistributedSmoodAccessTestContract;
import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.meta.GmEntityType;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.processing.itw.synthesis.gm.GenericModelTypeSynthesis;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;
import com.braintribe.model.user.User;
import com.braintribe.model.util.meta.NewMetaModelGeneration;
import com.braintribe.utils.FileTools;


public class Worker implements Callable<Void> {

	protected String id = "";
	protected int iterations = 0;
	protected DistributedSmoodAccessTestContract space = null;

	protected IncrementalAccess access = null;
	protected PersistenceGmSession session = null;

	protected int accessRenewInterval = 5;

	public Worker(int iterations, DistributedSmoodAccessTestContract space) {
		this.iterations = iterations;
		this.space = space;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public Void call() throws Exception {

		File tempFolder = null;
		try {

			GenericModelTypeSynthesis gmTypeSynthesis = GenericModelTypeSynthesis.newInstance();
			tempFolder = FileTools.createNewTempDir(UUID.randomUUID().toString());
			gmTypeSynthesis.setClassOutputFolder(tempFolder);

			for (int i=0; i<this.iterations; ++i) {
				PersistenceGmSession localSession = this.getSession(i);
				User newUser = localSession.create(User.T);
				newUser.setFirstName(""+id+"."+i);
				newUser.setLastName("Mustermann");
				localSession.commit();
			}

			List<EntityType<?>> types = Arrays.asList(SingleExtensionType.T, MultiExtensionType.T);
			GmMetaModel metaModel = new NewMetaModelGeneration().buildMetaModel("gm:WorkerModel", types);

			String newSingleTypeSig = null;
			String newMultiTypeSig = null;
			String singleName = SingleExtensionType.class.getName();
			String multiName = MultiExtensionType.class.getName();
			for (GmEntityType type : metaModel.entityTypes().collect(Collectors.toList())) {
				String typeSig = type.getTypeSignature();
				if (typeSig.startsWith(singleName)) {
					newSingleTypeSig = typeSig + "_runtime";
					type.setTypeSignature(newSingleTypeSig);
				} else if (typeSig.startsWith(multiName)) {
					newMultiTypeSig = typeSig + "_runtime_"+this.id;
					type.setTypeSignature(newMultiTypeSig);
				}
			}
			gmTypeSynthesis.ensureModelTypes(metaModel);

			File[] files = tempFolder.listFiles();
			for (File f : files) {
				if (f.isFile()) {
					String name = f.getName();
					InputStream inputStream = new FileInputStream(f);
					this.space.utils().getClassDataStorage().storeClass(name, inputStream, null);
					inputStream.close();
				}
			}

			EntityType<GenericEntity> newSingleType = GMF.getTypeReflection().getEntityType(newSingleTypeSig);
			GenericEntity geSingle = session.create(newSingleType);
			GMF.getTypeReflection().getEntityType(newSingleTypeSig).getProperty("name").set(geSingle, this.id);
			
			EntityType<GenericEntity> newMultiType = GMF.getTypeReflection().getEntityType(newMultiTypeSig);
			GenericEntity geMulti = session.create(newMultiType);
			GMF.getTypeReflection().getEntityType(newMultiTypeSig).getProperty("name").set(geMulti, this.id);
			
			session.commit();
			
			System.out.println("Finished after "+this.iterations+" iterations");

		} catch(Exception e) {
			System.out.println("Error in Worker "+this.id);
			e.printStackTrace(System.out);
		} finally {
			FileTools.deleteDirectoryRecursively(tempFolder);
		}
		System.out.flush();

		return null;
	}

	protected PersistenceGmSession getSession(int iteration) throws Exception {

		if ((iteration % this.accessRenewInterval) == 0) {
			this.session = null;
			this.access = null;
		}

		if (this.access == null) {
			this.access = space.concurrentAccess();
		}
		if (session == null) {
			this.session = space.utils().openSession(this.access);
		}
		return this.session;

	}

}
