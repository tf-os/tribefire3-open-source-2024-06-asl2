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
package com.braintribe.tribefire.jinni.support;

import static com.braintribe.console.ConsoleOutputs.brightYellow;
import static com.braintribe.console.ConsoleOutputs.println;

import java.io.File;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.braintribe.utils.FileTools;

public class PackagedSolution {

	private static final String FILENAME_PACKAGED_SOLUTIONS = "packaged-solutions.txt";

	public String groupId;
	public String artifactId;
	public String version;

	public PackagedSolution(String groupId, String artifactId, String version) {
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.version = version;
	}

	public static Set<PackagedSolution> readSolutionsFrom(File installationDir) {

		if (installationDir == null) {
			println(brightYellow("Missing information about packaged libraries"));
			return null;
		}

		File file = new File(installationDir, FILENAME_PACKAGED_SOLUTIONS);

		if (!file.exists()) {
			println(brightYellow("Missing information about packaged libraries"));
			return null;
		}

		String text = FileTools.read(file).asString();

		Pattern pattern = Pattern.compile("(.*):(.*)#(.*)");

		String solutions[] = text.split("\\n");

		Comparator<PackagedSolution> comparator = Comparator //
				.comparing((PackagedSolution s) -> s.groupId) //
				.thenComparing((PackagedSolution s) -> s.artifactId);

		Set<PackagedSolution> packagedSolutions = new TreeSet<>(comparator);

		for (String solution : solutions) {
			Matcher matcher = pattern.matcher(solution);
			if (matcher.matches()) {
				PackagedSolution packagedSolution = new PackagedSolution(matcher.group(1), matcher.group(2), matcher.group(3));
				packagedSolutions.add(packagedSolution);
			}
		}

		return packagedSolutions;
	}

}