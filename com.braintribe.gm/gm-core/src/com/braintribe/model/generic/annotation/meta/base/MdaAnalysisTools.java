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
package com.braintribe.model.generic.annotation.meta.base;

import static com.braintribe.utils.lcd.CollectionTools2.first;
import static com.braintribe.utils.lcd.CollectionTools2.newList;
import static com.braintribe.utils.lcd.CollectionTools2.newMap;
import static com.braintribe.utils.lcd.StringTools.isEmpty;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Function;

import com.braintribe.logging.Logger;
import com.braintribe.model.generic.annotation.meta.api.MdaHandler;
import com.braintribe.model.generic.annotation.meta.api.RepeatableMdaHandler;
import com.braintribe.model.generic.annotation.meta.api.analysis.MdaAnalysisContext;
import com.braintribe.model.generic.annotation.meta.api.synthesis.AnnotationDescriptor;
import com.braintribe.model.generic.annotation.meta.api.synthesis.MdaSynthesisContext;
import com.braintribe.model.generic.annotation.meta.api.synthesis.RepeatedAnnotationDescriptor;
import com.braintribe.model.generic.annotation.meta.api.synthesis.SingleAnnotationDescriptor;
import com.braintribe.model.generic.i18n.LocalizedString;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.meta.data.MetaData;
import com.braintribe.utils.lcd.StringTools;

/**
 * @author peter.gazdik
 */
public class MdaAnalysisTools {

	private static final Logger log = Logger.getLogger(MdaAnalysisTools.class);

	public static <M extends MetaData> M newMd(MdaAnalysisContext context, EntityType<M> metaDataType, String globalId) {
		return buildMd(context, metaDataType, globalId, "");
	}

	public static <M extends MetaData> M newMd(MdaAnalysisContext context, EntityType<M> mdType, String globalId, int counter) {
		return buildMd(context, mdType, globalId, "_" + counter);
	}

	private static <M extends MetaData> M buildMd(MdaAnalysisContext context, EntityType<M> mdType, String globalId, String globalIdSuffix) {
		M result = mdType.create();
		if (!StringTools.isEmpty(globalId)) {
			result.setGlobalId(globalId);

		} else {
			String targetGlobalId = context.getTarget().getGlobalId();

			if (!StringTools.isEmpty(targetGlobalId))
				result.setGlobalId(naturalGlobalId(mdType, targetGlobalId, globalIdSuffix));
		}

		return result;
	}

	public static String naturalGlobalId(EntityType<? extends MetaData> mdType, String ownerGlobalId, String globalIdSuffix) {
		return mdType.getShortName() + ":" + ownerGlobalId + globalIdSuffix;
	}

	public static <M extends MetaData, A extends Annotation> List<M> toLsBasedMd( //
			MdaAnalysisContext context, //
			MdaHandler<A, M> handler, //
			Function<A, String> localeReseolver, //
			Function<A, String> textResolver, //
			Function<A, String> globalIdResolver, //
			BiConsumer<M, LocalizedString> lsSetter, //
			A... annos) {

		String globalId = null;
		Map<String, String> localizedValues = newMap();

		for (A anno : annos) {
			localizedValues.put(localeReseolver.apply(anno), textResolver.apply(anno));

			String currentGlobalId = globalIdResolver.apply(anno);
			checkGlobalId(globalId, currentGlobalId, handler.annotationClass());

			if (!StringTools.isEmpty(currentGlobalId))
				globalId = currentGlobalId;
		}

		M result = newMd(context, handler.metaDataType(), globalId);

		LocalizedString ls = LocalizedString.T.create("localized-description:" + result.getGlobalId());
		ls.setLocalizedValues(localizedValues);

		lsSetter.accept(result, ls);

		return Collections.singletonList(result);
	}

	private static void checkGlobalId(String globalId1, String globalId2, Class<? extends Annotation> annotationClass) {
		boolean twoDifferentValues = !isEmpty(globalId1) && !isEmpty(globalId2) && !globalId1.equals(globalId2);

		if (twoDifferentValues)
			log.warn("Different globalId configured for the same meta-data of type '" + annotationClass.getSimpleName() + "'. First: " + globalId1
					+ ", second: " + globalId2);
	}

	public static <M extends MetaData, A extends Annotation, RA extends Annotation> void buildLsBasedAnnotation(MdaSynthesisContext context, M md, //
			RepeatableMdaHandler<A, RA, M> handler, //
			Function<M, LocalizedString> lsResolver) {

		Map<String, String> localizedValues = lsResolver.apply(md).getLocalizedValues();

		List<SingleAnnotationDescriptor> list = newList();

		for (Entry<String, String> entry : localizedValues.entrySet()) {
			String locale = entry.getKey();
			String value = entry.getValue();

			SingleAnnotationDescriptor ad = context.newDescriptor(handler.annotationClass());
			ad.addAnnotationValue("locale", locale);
			ad.addAnnotationValue("value", value);

			list.add(ad);
		}

		AnnotationDescriptor descriptor = list.size() > 1 ? //
				new RepeatedAnnotationDescriptor(handler.aggregatorHandler().annotationClass(), list) : first(list);
		context.setCurrentDescriptor(descriptor);
	}

}
