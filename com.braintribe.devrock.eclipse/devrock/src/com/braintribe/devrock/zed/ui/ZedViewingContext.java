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
package com.braintribe.devrock.zed.ui;

import java.util.Map;

import org.eclipse.core.resources.IProject;

import com.braintribe.devrock.zed.api.context.ZedAnalyzerContext;
import com.braintribe.devrock.zed.forensics.fingerprint.register.RatingRegistry;
import com.braintribe.gm.model.reason.Reason;
import com.braintribe.zarathud.model.data.Artifact;
import com.braintribe.zarathud.model.forensics.ClasspathForensicsResult;
import com.braintribe.zarathud.model.forensics.DependencyForensicsResult;
import com.braintribe.zarathud.model.forensics.FingerPrint;
import com.braintribe.zarathud.model.forensics.ForensicsRating;
import com.braintribe.zarathud.model.forensics.ModelForensicsResult;
import com.braintribe.zarathud.model.forensics.ModuleForensicsResult;

/**
 * a context to be passed to the viewer / transposer
 * @author pit
 *
 */
public class ZedViewingContext {
	private Artifact artifact;
	private DependencyForensicsResult dependencyForensicsResult;
	private ClasspathForensicsResult classpathForensicsResult;
	private ModelForensicsResult modelForensicsResult;
	private ModuleForensicsResult moduleForensicsResult;
	private ForensicsRating worstRating;
	private Map<FingerPrint, ForensicsRating> issues;
	private RatingRegistry ratingRegistry;
	private ZedAnalyzerContext analyzerContext;
	private Reason analyzerReturnReason;
	private IProject project;
	
	/**
	 * @return - the {@link Artifact} that has been analyzed
	 */
	public Artifact getArtifact() {
		return artifact;
	}
	public void setArtifact(Artifact artifact) {
		this.artifact = artifact;
	}
	/**
	 * @return - the {@link DependencyForensicsResult} as collected from analyzing the dependencies
	 */
	public DependencyForensicsResult getDependencyForensicsResult() {
		return dependencyForensicsResult;
	}
	public void setDependencyForensicsResult(DependencyForensicsResult dependencyForensicsResult) {
		this.dependencyForensicsResult = dependencyForensicsResult;
	}
	/**
	 * @return - the {@link ClasspathForensicsResult} as collected from analyzing the classpath
	 */
	public ClasspathForensicsResult getClasspathForensicsResult() {
		return classpathForensicsResult;
	}
	public void setClasspathForensicsResult(ClasspathForensicsResult classpathForensicsResult) {
		this.classpathForensicsResult = classpathForensicsResult;
	}
	/**
	 * @return - the {@link ModelForensicsResult} as collected (if the artifact is a model)
	 */
	public ModelForensicsResult getModelForensicsResult() {
		return modelForensicsResult;
	}
	public void setModelForensicsResult(ModelForensicsResult modelForensicsResult) {
		this.modelForensicsResult = modelForensicsResult;
	}
	/**
	 * @return - the {@link ModuleForensicsResult 
	 */
	public ModuleForensicsResult getModuleForensicsResult() {
		return moduleForensicsResult;
	}
	public void setModuleForensicsResult(ModuleForensicsResult moduleForensicsResult) {
		this.moduleForensicsResult = moduleForensicsResult;
	}
	/**
	 * @return - the worst {@link ForensicsRating}
	 */
	public ForensicsRating getWorstRating() {
		return worstRating;
	}
	public void setWorstRating(ForensicsRating worstRating) {
		this.worstRating = worstRating;
	}
	/**
	 * @return a {@link Map} of {@link FingerPrint} to {@link ForensicsRating}
	 */
	public Map<FingerPrint, ForensicsRating> getIssues() {
		return issues;
	}
	public void setIssues(Map<FingerPrint, ForensicsRating> issues) {
		this.issues = issues;
	}
	
	/**
	 * @return - the {@link RatingRegistry} as created during the run
	 */
	public RatingRegistry getRatingRegistry() {
		return ratingRegistry;
	}
	public void setRatingRegistry(RatingRegistry ratingRegistry) {
		this.ratingRegistry = ratingRegistry;
	}
	
	/**
	 * @return - the {@link ZedAnalyzerContext} as built during the run
	 */
	public ZedAnalyzerContext getAnalyzerContext() {
		return analyzerContext;
	}
	public void setAnalyzerContext(ZedAnalyzerContext analyzerContext) {
		this.analyzerContext = analyzerContext;
	}
	
	/**
	 * @return - the main reason the analyzer returned (if any)
	 */
	public Reason getAnalyzerReturnReason() {
		return analyzerReturnReason;
	}
	public void setAnalyzerReturnReason(Reason analyzerReturnReason) {
		this.analyzerReturnReason = analyzerReturnReason;
	}
	
	/**
	 * @return - the {@link IProject} that is associated, may be null (if viewing a remote artifact)
	 */
	public IProject getProject() {
		return project;
	}
	public void setProject(IProject project) {
		this.project = project;
	}
	
	
	
	

	
	
	
}
