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
package tribefire.extension.js.core.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.braintribe.build.artifact.representations.artifact.pom.ArtifactPomReader;
import com.braintribe.build.artifact.retrieval.multi.resolving.DependencyResolver;
import com.braintribe.build.artifact.retrieval.multi.resolving.ResolvingException;
import com.braintribe.build.artifact.retrieval.multi.resolving.listener.DependencyResolverNotificationListener;
import com.braintribe.cfg.Configurable;
import com.braintribe.cfg.Required;
import com.braintribe.model.artifact.Dependency;
import com.braintribe.model.artifact.Identification;
import com.braintribe.model.artifact.Part;
import com.braintribe.model.artifact.Solution;
import com.braintribe.model.artifact.processing.artifact.ArtifactProcessor;
import com.braintribe.model.artifact.processing.part.PartTupleProcessor;
import com.braintribe.model.artifact.processing.version.VersionProcessor;
import com.braintribe.model.artifact.version.Version;
import com.braintribe.utils.paths.UniversalPath;

/**
 * a {@link DependencyResolver} that works with a project file system (poms are pom.xml rather than .pom)
 * @author pit
 *
 */
public class BasicDependencyResolver implements DependencyResolver {
	
	private DependencyResolver delegate;
	private ArtifactPomReader pomReader;
	private File workingDirectory;

	@Required @Configurable
	public void setDelegate(DependencyResolver delegate) {
		this.delegate = delegate;
	}
	@Required @Configurable
	public void setWorkingDirectory(File workingDirectory) {
		this.workingDirectory = workingDirectory;
	}
	@Required @Configurable
	public void setPomReader(ArtifactPomReader pomReader) {
		this.pomReader = pomReader;
	}

	@Override
	public void addListener(DependencyResolverNotificationListener listener) {}

	@Override
	public void removeListener(DependencyResolverNotificationListener listener) {}

	@Override
	public Part resolvePom(String walkScopeId, Identification id, Version version) throws ResolvingException {
		File artifactDirectory = UniversalPath.from(workingDirectory).push(id.getArtifactId()).toFile();
		if (!artifactDirectory.exists()) {
			return null;
		}
		File pom = new File( artifactDirectory,"pom.xml");
		if (!pom.exists()) {
			return null;
		}
		
		Part part = Part.T.create();
		ArtifactProcessor.transferIdentification(part, id);
		part.setVersion(version);
		part.setLocation( pom.getAbsolutePath());
		part.setType( PartTupleProcessor.createPomPartTuple());
		
		return part;
	}

	@Override
	public Part resolvePomPart(String walkScopeId, Part pomPart) throws ResolvingException {
		
		File artifactDirectory = UniversalPath.from(workingDirectory).push( pomPart.getArtifactId()).toFile();
		if (!artifactDirectory.exists()) {
			return delegate.resolvePomPart(walkScopeId, pomPart);
		}
		File pom = new File( artifactDirectory,"pom.xml");
		if (!pom.exists()) {
			return delegate.resolvePomPart(walkScopeId, pomPart);
		}
		pomPart.setLocation( pom.getAbsolutePath());
		pomPart.setType( PartTupleProcessor.createPomPartTuple());		
		return pomPart;
	}

	@Override
	public Set<Solution> resolveTopDependency(String walkScopeId, Dependency dependency) throws ResolvingException {
		Set<Solution> matching = resolveMatchingDependency(walkScopeId, dependency);
		if (matching == null || matching.size() == 0) {
			return Collections.emptySet();
		}
		List<Solution> list = new ArrayList<>( matching);
		
		list.sort( new Comparator<Solution>() {
			@Override
			public int compare(Solution o1, Solution o2) {			
				return VersionProcessor.compare( o1.getVersion(), o2.getVersion());
			}			
		});
		
		return Collections.singleton(  list.get( list.size()-1));		
	}

	@Override
	public Set<Solution> resolveMatchingDependency(String walkScopeId, Dependency dependency) throws ResolvingException {
		File artifactDirectory = UniversalPath.from(workingDirectory).push( dependency.getArtifactId()).toFile();
		if (!artifactDirectory.exists()) {
			return delegate.resolveMatchingDependency(walkScopeId, dependency);
		}
		File pom = new File( artifactDirectory, "pom.xml");
		if (!pom.exists()) {
			return delegate.resolveMatchingDependency(walkScopeId, dependency);
		}
		
		Set<Solution> result = new HashSet<>();
		Solution solution = pomReader.readPom(walkScopeId, pom);
		if (solution != null) {
			result.add( solution);
		}
			
		// merge all solutions from the delegate
		Set<Solution> fromDelegate = delegate.resolveMatchingDependency(walkScopeId, dependency);
		
		if (fromDelegate != null && fromDelegate.size() != 0 && result.isEmpty()) {
			result.addAll( fromDelegate);
		}
		return result;		
	}
	@Override
	public Set<Solution> resolveDependency(String walkScopeId, Dependency dependency) throws ResolvingException {
		return resolveTopDependency(walkScopeId, dependency);
	}

	
	
}
