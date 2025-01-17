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
package com.braintribe.gwt.customizationui.client.packaging;

import java.util.function.Supplier;

import com.braintribe.gwt.action.client.Action;
import com.braintribe.gwt.action.client.TriggerInfo;
import com.braintribe.gwt.async.client.Future;
import com.braintribe.gwt.customizationui.client.packaging.resources.PackagingResources;
import com.braintribe.gwt.gxt.gxtresources.text.LocalizedText;
import com.braintribe.gwt.ioc.client.Configurable;
import com.braintribe.gwt.logging.client.Logger;
import com.braintribe.gwt.logging.ui.gxt.client.GxtErrorDialog;
import com.braintribe.model.packaging.Artifact;
import com.braintribe.model.packaging.Packaging;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.form.TextArea;

public class ShowPackagingInfoAction extends Action implements Supplier<String> {
	private static final Logger logger = new Logger(ShowPackagingInfoAction.class);
	
	private Window packagingWindow;
	private TextArea packagingInfo;
	private String packagingInfoString;
	private Supplier<String> versionStringProvider;
	private Supplier<Future<Packaging>> packagingProvider;
	
	public ShowPackagingInfoAction(Supplier<Future<Packaging>> packagingProvider) {
		this.packagingProvider = packagingProvider;
		setName(LocalizedText.INSTANCE.packagingInfo());
		setIcon(PackagingResources.INSTANCE.info());
		setTooltip(LocalizedText.INSTANCE.packagingInfoDescription());
		GxtErrorDialog.setPackagingInfoProvider(this);
		
		getPackagingInfoString().onError(e -> {
			packagingInfoString = null;
			logger.error("Packaging Info not available.", e);
		});
	}

	@Override
	public void perform(TriggerInfo triggerInfo) {
		getPackagingWindow().show();
	}
	
	@Override
	public String get() throws RuntimeException {
		return packagingInfoString;
	}
	
	@Configurable
	public void setVersionStringProvider(Supplier<String> versionStringProvider) {
		this.versionStringProvider = versionStringProvider;
	}
	
	private Window getPackagingWindow() {
		if (packagingWindow == null) {
			packagingWindow = new Window();
			packagingWindow.setBorders(false);
			packagingWindow.setBodyBorder(false);
			packagingWindow.setHeading(LocalizedText.INSTANCE.packagingInfo());
			packagingWindow.setSize("400px", "300px");
			
			packagingInfo = new TextArea();
			packagingInfo.setReadOnly(true);
			
			ContentPanel formPanel = new ContentPanel();
			formPanel.setHeaderVisible(false);
			formPanel.setBodyBorder(false);
			formPanel.setBorders(false);
			formPanel.setBodyStyle("background: none; padding: 15px");
			formPanel.add(packagingInfo);
			
			packagingWindow.add(formPanel);
		}
		
		packagingInfo.setValue(packagingInfoString != null ? packagingInfoString : LocalizedText.INSTANCE.noPackagingInfo());
		return packagingWindow;
	}
	
	private String getVersionString() {
		String versionString = "";
		if (versionStringProvider != null)
			versionString = versionStringProvider.get();
		
		return versionString;
	}
	
	private Future<Packaging> getPackaging() throws RuntimeException {
		return packagingProvider.get();
	}
	
	private Future<String> getPackagingInfoString() {
		final Future<String> future = new Future<>();
		if (packagingInfoString != null) {
			future.onSuccess(packagingInfoString);
			return future;
		}
		
		getPackaging() //
				.andThen(packaging -> {
					StringBuilder builder = new StringBuilder();

					String versionString = getVersionString();
					if (!versionString.isEmpty())
						builder.append(versionString).append("\n\n");

					if (packaging == null) {
						packagingInfoString = null;
						setHidden(true);
					} else {
						Artifact terminalArtifact = packaging.getTerminalArtifact();

						builder.append(LocalizedText.INSTANCE.packagingInfoFor(
								terminalArtifact.getGroupId() + ":" + terminalArtifact.getArtifactId() + "-" + terminalArtifact.getVersion()));

						if (packaging.getDependencies() != null) {
							builder.append("\n\n").append(LocalizedText.INSTANCE.dependencies()).append("\n");
							for (Artifact artifact : packaging.getDependencies()) {
								builder.append("\n");
								builder.append(artifact.getGroupId() + ":" + artifact.getArtifactId() + "-" + artifact.getVersion());
							}
						}
						packagingInfoString = builder.toString();
					}

					future.onSuccess(packagingInfoString);
				}).onError(e -> {
					future.onFailure(e);
					setHidden(true);
				});
		
		return future;
	}

}
