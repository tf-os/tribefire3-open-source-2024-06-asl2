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
package com.braintribe.gwt.customization.client.ioc;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.braintribe.gwt.ioc.gme.client.IocExtensionPoints;
import com.braintribe.gwt.ioc.gme.client.Runtime;
import com.braintribe.model.generic.i18n.LocalizedString;
import com.braintribe.model.workbench.HyperlinkAction;
import com.braintribe.provider.SingletonBeanProvider;

public class IocExtensions extends IocExtensionPoints {

	@Override
	public Supplier<List<HyperlinkAction>> topBannerLinks() {
		return topBannerLinks;
	}

	public static Supplier<List<HyperlinkAction>> topBannerLinks = new SingletonBeanProvider<List<HyperlinkAction>>() {
		@Override
		public List<HyperlinkAction> create() throws Exception {
			List<HyperlinkAction> bean = publish(new ArrayList<HyperlinkAction>());
			bean.add(explorerLink.get());
			bean.add(servicesLink.get());
			bean.add(documentationLink.get());
			return bean;
		}
	};

	public static Supplier<HyperlinkAction> explorerLink = new SingletonBeanProvider<HyperlinkAction>() {
		@Override
		public HyperlinkAction create() throws Exception {
			HyperlinkAction bean = publish(HyperlinkAction.T.create());

			bean.setDisplayName(wrapWithLocalizedString(LocalizedText.INSTANCE.tribefireExplorer()));
			bean.setTarget("_blank");
			bean.setUrl(Runtime.tribefireExplorerUrl.get());

			return bean;
		}
	};

	public static Supplier<HyperlinkAction> servicesLink = new SingletonBeanProvider<HyperlinkAction>() {
		@Override
		public HyperlinkAction create() throws Exception {
			HyperlinkAction bean = publish(HyperlinkAction.T.create());

			bean.setDisplayName(wrapWithLocalizedString(LocalizedText.INSTANCE.tribefireServices()));
			bean.setTarget("_blank");
			bean.setUrl(Runtime.tribefireServicesUrl.get());

			return bean;
		}
	};

	public static Supplier<HyperlinkAction> documentationLink = new SingletonBeanProvider<HyperlinkAction>() {
		@Override
		public HyperlinkAction create() throws Exception {
			HyperlinkAction bean = publish(HyperlinkAction.T.create());

			bean.setDisplayName(wrapWithLocalizedString(LocalizedText.INSTANCE.tribefireDocumentation()));
			bean.setTarget("_blank");
			bean.setUrl(Runtime.tribefireDocumentationUrl.get());

			return bean;
		}
	};	
	
	private static LocalizedString wrapWithLocalizedString(String value) {
		LocalizedString result = LocalizedString.T.create();
		result.getLocalizedValues().put("default", value);
		return result;
	}

}
