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
package com.braintribe.cartridge.common.processing;

import static com.braintribe.utils.lcd.CollectionTools2.isEmpty;
import static java.util.Collections.emptyList;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.braintribe.logging.Logger;
import com.braintribe.model.accessdeployment.orm.meta.NativeOrm;
import com.braintribe.model.meta.data.HasMetaData;
import com.braintribe.model.meta.data.MetaData;
import com.braintribe.model.processing.meta.cmd.CmdResolver;
import com.braintribe.model.resource.Resource;
import com.braintribe.utils.lcd.NullSafe;

/**
 * <p>
 * Offers Object-Relational Mapping related convenience methods.
 */
public class ObjectRelationalMappings {

	private static final Logger log = Logger.getLogger(ObjectRelationalMappings.class);

	/**
	 * <p>
	 * Applies to the given {@code target} the native ORM sources (see {@link NativeOrm}) resolved based on the provided
	 * {@link HasMetaData denotation} and {@link CmdResolver metadataResolver}.
	 * 
	 * <p>
	 * When no native ORM is resolved, an optional user-provided {@code fallbackSupplier} function is called.
	 * 
	 * @param target
	 *            A {@link Consumer} to which the resolved native ORM {@link InputStream} will be provided.
	 * @param denotation
	 *            The {@link HasMetaData} denotation from which instance-level native ORM metadata is to be
	 *            resolved. Can be {@code null} is a {@code CmdResolver metadataResolver} given.
	 * @param cmdResolver
	 *            The {@link CmdResolver} from which type-level native ORM metadata is to be resolved. Can be
	 *            {@code null} is a {@code CmdResolver denotation} given.
	 * @param fallbackSupplier
	 *            An optional procedure to be called if no native ORM metadata is resolved.
	 */
	public static void applyMappings(Consumer<List<Supplier<InputStream>>> target, HasMetaData denotation, CmdResolver cmdResolver,
			Runnable fallbackSupplier) {

		NullSafe.nonNull(target, "target");

		if (denotation == null && cmdResolver == null)
			throw new IllegalArgumentException("Either the denotation or the metadataResolver must be provided");

		List<NativeOrm> nativeOrm = nativeOrms(denotation, cmdResolver);

		if (nativeOrm !=null && !nativeOrm.isEmpty()) {

			List<Supplier<InputStream>> streamSuppliers = new ArrayList<>();

			for (NativeOrm orm : nativeOrm) {

				Set<Resource> resources = orm.resources();

				if (resources == null || resources.isEmpty()) {
					throw new IllegalStateException("The metadata provided no resources: " + orm);
				}

				for (Resource resource : resources) {
					streamSuppliers.add(() -> resource.openStream());
				}

			}

			target.accept(streamSuppliers);

		} else if (fallbackSupplier != null) {
			fallbackSupplier.run();

		}

	}

	public static List<NativeOrm> nativeOrms(HasMetaData denotation, CmdResolver cmdResolver) {
		if (denotation != null) {
			Set<MetaData> mds = denotation.getMetaData();

			if (!isEmpty(mds)) {
				List<NativeOrm> result = mds.stream().filter(m -> m instanceof NativeOrm).map(p -> (NativeOrm) p).collect(Collectors.toList());
				if (!result.isEmpty()) {
					log.info("Resolved " + result.size() + " " + NativeOrm.T.getShortName() + " instance(s) from denotation instance: " + denotation);
					return result;
				} 

				log.info("No " + NativeOrm.T.getShortName() + " instance was resolved from denotation instance: " + denotation);
			}
		}

		if (cmdResolver == null)
			return emptyList();

		List<NativeOrm> result = cmdResolver.getMetaData().meta(NativeOrm.T).list();

		if (!result.isEmpty())
			log.info("Resolved " + result.size() + " " + NativeOrm.T.getShortName() + " instance(s) with the given resolver: " + cmdResolver);
		else
			log.info("No " + NativeOrm.T.getShortName() + " instance was resolved with the given resolver: " + cmdResolver);
		return result;

	}

	/**
	 * <p>
	 * Applies to the given {@code target} the native ORM sources (see {@link NativeOrm}) resolved based on the provided
	 * {@link HasMetaData denotation}.
	 * 
	 * <p>
	 * When no native ORM is resolved, an optional user-provided {@code fallbackSupplier} function is called.
	 * 
	 * @param target
	 *            A {@link Consumer} to which the resolved native ORM {@link InputStream} will be provided.
	 * @param denotation
	 *            The {@link HasMetaData} denotation from which instance-level native ORM metadata is to be
	 *            resolved.
	 * @param fallbackSupplier
	 *            An optional procedure to be called if no native ORM metadata is resolved.
	 */
	public static void applyMappings(Consumer<List<Supplier<InputStream>>> target, HasMetaData denotation, Runnable fallbackSupplier) {
		applyMappings(target, denotation, null, fallbackSupplier);
	}

}
