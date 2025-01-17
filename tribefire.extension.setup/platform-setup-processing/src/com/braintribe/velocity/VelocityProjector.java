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
package com.braintribe.velocity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Stack;
import java.util.stream.Stream;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.app.event.EventCartridge;

import com.braintribe.exception.Exceptions;
import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.utils.lcd.StringTools;
import com.braintribe.utils.paths.PathCollectors;
import com.braintribe.utils.paths.UniversalPath;

public class VelocityProjector implements Runnable {
	private static final String TEMPLATE_EXTENSION = ".vm";
	private File sourceFolder;
	private File targetFolder;
	private GenericEntity request;
	private VelocityEngine velocityEngine;

	public VelocityProjector(File sourceFolder, File targetFolder, GenericEntity request) {
		super();
		this.sourceFolder = sourceFolder;
		this.targetFolder = targetFolder;
		this.request = request;

		Properties properties = new Properties();

		properties.setProperty("resource.default_encoding", "UTF-8");
		properties.setProperty("resource.loaders", "file");
		properties.setProperty("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.FileResourceLoader");
		properties.setProperty("file.resource.loader.path", sourceFolder.getAbsolutePath());
		properties.setProperty("file.resource.loader.cache", "true");
		properties.setProperty("runtime.strict_mode.escape", "true");

		velocityEngine = new VelocityEngine();
		velocityEngine.init(properties);
	}

	private Map<String, File> collectProjections() {
		Map<String, File> mappings = new LinkedHashMap<>();

		collectProjections(sourceFolder, new Stack<>(), mappings);

		return mappings;
	}

	private void collectProjections(File folder, Stack<String> path, Map<String, File> mappings) {
		for (File file : folder.listFiles()) {
			if (file.isDirectory()) {
				path.push(file.getName());
				try {
					collectProjections(file, path, mappings);
				} finally {
					path.pop();
				}
			} else {
				if (file.getName().endsWith(TEMPLATE_EXTENSION)) {
					String templateName = file.getName();
					String projectedName = templateName.substring(0, templateName.length() - TEMPLATE_EXTENSION.length());

					String relativeSourcePath = Stream.concat(path.stream(), Stream.of(templateName)).collect(PathCollectors.slashPath);
					String relativeTargetPath = Stream.concat(path.stream(), Stream.of(projectedName)).collect(PathCollectors.filePath);

					File targetFile = new File(targetFolder, relativeTargetPath);
					mappings.put(relativeSourcePath, targetFile);
				}
			}
		}
	}

	@Override
	public void run() {
		Map<String, File> projections = collectProjections();

		for (Map.Entry<String, File> entry : projections.entrySet()) {
			VelocityContext context = new VelocityContext();
			context.put("request", request);
			context.put("tools", new TemplateTools());
			String templatePath = entry.getKey();
			File targetFile = entry.getValue();

			EventCartridge eventCartridge = new EventCartridge();
			EscapeReferenceInserter ev = new EscapeReferenceInserter();
			ev.setTemplatePath(templatePath);
			eventCartridge.addReferenceInsertionEventHandler(ev);

			context.attachEventCartridge(eventCartridge);

			try (Writer writer = new OutputStreamWriter(new FileOutputStream(targetFile))) {
				velocityEngine.mergeTemplate(templatePath, "UTF-8", context, writer);
			} catch (Exception e) {
				throw Exceptions.unchecked(e, "Error while evaluating projection template: " + templatePath);
			}
		}
	}

	public static class TemplateTools {
		public boolean isInstanceOf(Object instance, String typeSignature) {
			return GMF.getTypeReflection().getType(typeSignature).isInstance(instance);
		}

		public String join(Iterable<?> iterable, String separator) {
			return StringTools.join(separator, iterable.iterator());
		}

		public String resolveRelativePath(String basePath, String path) {
			if (new File(path).isAbsolute()) {
				return path;
			} else {
				return UniversalPath.start(basePath).push(path).toSlashPath();
			}
		}

		public boolean isRelativePath(String path) {
			return !isAbsolutePath(path);
		}

		public boolean isAbsolutePath(String path) {
			return new File(path).isAbsolute();
		}
	}
}
