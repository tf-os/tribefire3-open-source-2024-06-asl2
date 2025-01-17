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
package com.braintribe.setup.tools;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import com.braintribe.exception.Exceptions;
import com.braintribe.model.asset.natures.PlatformAssetNature;
import com.braintribe.model.generic.reflection.GenericModelException;

/**
 * @author peter.gazdik
 */
public enum GmNature {

	/** GM model */
	model,

	/** GM API artifact - it must always be promoted to main classpath */
	api,

	/** standard jar, has nothing to do with platform-library, which is a platform as {@link PlatformAssetNature} */
	library,

	/**
	 * This can only be a dependency of the platform. If it occurs anywhere else, it is considered an error. Typical platformOnly artifact is
	 * <tt>tribefire-platform-commons</tt>.
	 */
	platformOnly;

	/**
	 * This is the name of the property used in artifact's pom.xml. The value used is the name of the enum constant, e.g.:
	 * {@code <gmNature>platformOnly</gmNature>}
	 */
	public static final String mavenPropertyName = "gmNature";

	/**
	 * Returns parsed Gm-Nature from the manifest file inside the jar denoted by given file name, or {@link #library} if that is not possible.<br>
	 * 
	 * Equivalent to creating a {@link JarFile} for given fileName and calling {@link #tryFromJarFile(JarFile)}
	 */
	public static GmNature fromJarFileName(String jarFileName) {
		try {
			return tryFromJarFileName(jarFileName);

		} catch (IOException e) {
			throw Exceptions.uncheckedAndContextualize(e, "Error while processing JarFile: " + jarFileName, GenericModelException::new);
		}
	}

	private static GmNature tryFromJarFileName(String jarFileName) throws IOException {
		try (JarFile jarFile = new JarFile(jarFileName, false)) {
			return tryFromJarFile(jarFile);
		}
	}

	/**
	 * Returns the parsed Gm-Nature attribute from the manifest file inside given {@link JarFile}. If any of the necessary things is not available,
	 * this method returns {@link #library}.
	 */
	private static GmNature tryFromJarFile(JarFile jarFile) throws IOException {
		Objects.requireNonNull(jarFile, "jarFile cannot be null");

		GmNature result = fromManifestOf(jarFile);

		return result != library ? result : (isModel(jarFile) ? model : library);
	}

	private static GmNature fromManifestOf(JarFile jarFile) throws IOException {
		return Optional.ofNullable(jarFile.getManifest()) //
				.map(Manifest::getMainAttributes) //
				.map(attrs -> attrs.getValue("Gm-Nature")) //
				.map(GmNature::valueOfSafe) //
				.orElse(library);
	}

	private static boolean isModel(JarFile jarFile) {
		return jarFile.getEntry("model-declaration.xml") != null;
	}

	/**
	 * Returns the same result as the {@link #valueOf(String)} as long as it would not throw an exception. In such case this method returns
	 * {@link #library}.
	 */
	public static GmNature valueOfSafe(String name) {
		try {
			return GmNature.valueOf(name);
		} catch (Exception e) {
			return library;
		}
	}

}
