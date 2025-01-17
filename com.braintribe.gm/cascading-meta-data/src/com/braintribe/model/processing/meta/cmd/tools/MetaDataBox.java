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
package com.braintribe.model.processing.meta.cmd.tools;

import static com.braintribe.utils.lcd.CollectionTools2.newList;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.braintribe.model.processing.meta.oracle.QualifiedMetaData;
import com.braintribe.utils.lcd.CollectionTools2;

/**
 * 
 */
public class MetaDataBox {
	public static final MetaDataBox EMPTY_BOX = new MetaDataBox();

	public final List<QualifiedMetaData> normalMetaData;
	public final List<QualifiedMetaData> importantMetaData;

	private MetaDataBox() {
		this.normalMetaData = Collections.EMPTY_LIST;
		this.importantMetaData = Collections.EMPTY_LIST;
	}
	
	public MetaDataBox(List<QualifiedMetaData> normalMetaData, List<QualifiedMetaData> importantMetaData) {
		if (normalMetaData.isEmpty()) {
			this.normalMetaData = Collections.EMPTY_LIST;
		} else {
			this.normalMetaData = normalMetaData;
		}
		if (importantMetaData.isEmpty()) {
			this.importantMetaData = Collections.EMPTY_LIST;
		} else {
			this.importantMetaData = importantMetaData;
		}
	}

	public static MetaDataBox forNormalMdOnly(Stream<QualifiedMetaData> mds) {
		List<QualifiedMetaData> mdList = (List<QualifiedMetaData>) (List<?>) mds.collect(Collectors.toList());
		if (CollectionTools2.isEmpty(mdList)) {
			return EMPTY_BOX;
		}

		return new MetaDataBox(mdList, Collections.EMPTY_LIST);
	}

	public static MetaDataBox forPrioritizable(Stream<QualifiedMetaData> mds) {
		List<QualifiedMetaData> mdList = (List<QualifiedMetaData>) (List<?>) mds.collect(Collectors.toList());

		if (CollectionTools2.isEmpty(mdList)) {
			return EMPTY_BOX;
		}

		List<QualifiedMetaData> normalMd = newList();
		List<QualifiedMetaData> importantMd = newList();

		for (QualifiedMetaData qmd: mdList) {
			if (qmd.metaData().getImportant()) {
				importantMd.add(qmd);
			} else {
				normalMd.add(qmd);
			}
		}

		return new MetaDataBox(normalMd, importantMd);
	}

	public boolean isEmpty() {
		return normalMetaData.isEmpty() && importantMetaData.isEmpty();
	}

}
