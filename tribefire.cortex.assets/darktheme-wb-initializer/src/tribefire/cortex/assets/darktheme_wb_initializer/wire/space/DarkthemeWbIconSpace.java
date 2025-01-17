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
package tribefire.cortex.assets.darktheme_wb_initializer.wire.space;

import static com.braintribe.wire.api.util.Sets.set;

import com.braintribe.model.resource.AdaptiveIcon;
import com.braintribe.model.resource.Icon;
import com.braintribe.model.resource.SimpleIcon;
import com.braintribe.wire.api.annotation.Import;
import com.braintribe.wire.api.annotation.Managed;

import tribefire.cortex.assets.darktheme_wb_initializer.wire.contract.DarkthemeWbIconContract;
import tribefire.cortex.assets.darktheme_wb_initializer.wire.contract.DarkthemeWbResourceContract;
import tribefire.cortex.initializer.support.wire.space.AbstractInitializerSpace;

@Managed
public class DarkthemeWbIconSpace extends AbstractInitializerSpace implements DarkthemeWbIconContract {
	
	@Import
	DarkthemeWbResourceContract resources;
	
	@Managed
	@Override
	public AdaptiveIcon run() {
		AdaptiveIcon bean = create(AdaptiveIcon.T);

		bean.setName("Run Icon");
		bean.setRepresentations(set(
				resources.run16Png(),
				resources.run32Png()
				));
		
		return bean;
	}

	@Managed
	@Override
	public SimpleIcon logo() {
		SimpleIcon bean = create(SimpleIcon.T);
		
		bean.setName("Logo Icon");
		bean.setImage(resources.logoPng());
		
		return bean;
	}

	@Managed
	@Override
	public Icon home() {
		AdaptiveIcon bean = create(AdaptiveIcon.T);

		bean.setName("Home Icon");
		bean.setRepresentations(set(
				resources.home16Png(),
				resources.home32Png(),
				resources.home64Png()
				));
		
		return bean;
	}

	@Managed
	@Override
	public Icon changes() {
		AdaptiveIcon bean = create(AdaptiveIcon.T);

		bean.setName("Changes Icon");
		bean.setRepresentations(set(
				resources.changes16Png(),
				resources.changes32Png(),
				resources.changes64Png()
				));
		
		return bean;
	}
	
	@Managed
	@Override
	public Icon clipboard() {
		AdaptiveIcon bean = create(AdaptiveIcon.T);

		bean.setName("Clipboard Icon");
		bean.setRepresentations(set(
				resources.clipboard16Png(),
				resources.clipboard32Png(),
				resources.clipboard64Png()
				));
		
		return bean;
	}
	
	@Managed
	@Override
	public Icon notification() {
		AdaptiveIcon bean = create(AdaptiveIcon.T);

		bean.setName("Notification Icon");
		bean.setRepresentations(set(
				resources.notification16Png(),
				resources.notification32Png(),
				resources.notification64Png()
				));
		
		return bean;
	}

	@Managed
	@Override
	public Icon magnifier() {
		SimpleIcon bean = create(SimpleIcon.T);
		
		bean.setName("Magnifier Icon");
		bean.setImage(resources.magnifier16Png());
		
		return bean;
	}
	
	@Managed
	@Override
	public Icon newIcon() {
		
		AdaptiveIcon bean = create(AdaptiveIcon.T);

		bean.setName("New Icon");
		bean.setRepresentations(set(
				resources.new16Png(),
				resources.new32Png()
				));
		
		return bean;
	}
	
	@Managed
	@Override
	public Icon upload() {
		SimpleIcon bean = create(SimpleIcon.T);
		
		bean.setName("Upload Icon");
		bean.setImage(resources.upload32Png());
		
		return bean;
	}
	
	@Managed
	@Override
	public Icon undo() {
		SimpleIcon bean = create(SimpleIcon.T);
		
		bean.setName("Undo Icon");
		bean.setImage(resources.undo32Png());
		
		return bean;
	}
	
	@Managed
	@Override
	public Icon redo() {
		SimpleIcon bean = create(SimpleIcon.T);
		
		bean.setName("Redo Icon");
		bean.setImage(resources.redo32Png());
		
		return bean;
	}
	
	@Managed
	@Override
	public Icon commit() {
		SimpleIcon bean = create(SimpleIcon.T);
		
		bean.setName("Commit Icon");
		bean.setImage(resources.commit32Png());
		
		return bean;
	}
	
}
