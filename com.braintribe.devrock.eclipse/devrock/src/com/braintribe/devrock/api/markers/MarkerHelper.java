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
package com.braintribe.devrock.api.markers;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;

import com.braintribe.devrock.plugin.DevrockPlugin;
import com.braintribe.devrock.plugin.DevrockPluginStatus;
import com.braintribe.logging.Logger;

/**
 * common/generic handler for markers.. currently only deletes all markers of a type from an IProject 
 * NOTE: even if project is marked as accessible, access may still lead to 'resource locked' exception, and 
 * note the CoreException as expected. Hence, the 'pure exception' is caught here.  
 * @author pit
 *
 */
public class MarkerHelper {
	private static Logger log = Logger.getLogger(MarkerHelper.class);
	public static final String MRK_MAIN = "com.braintribe.devrock.ac.marker.failed";
	
	/**
	 * remove all matching markers from the project
	 * @param project - the {@link IProject} 
	 * @param markerType - the type of the marker as {@link String} or null if all markers should be deleted
	 */
	public static void removeFailedResolutionMarkersFromProject( IProject project, String markerType) {
		if (!project.isAccessible() || project.isOpen())
			return;
		try {
			IMarker[] markers = project.findMarkers( markerType, false,  IResource.DEPTH_INFINITE);
			if (markers != null) {
				for (IMarker marker : markers) {
					marker.delete();
				}
			}
		} catch (Exception e) {
			String msg = "cannot remove markers of type [" + markerType + "] from project [" + project.getName() + "]";
			log.error( msg);
			DevrockPluginStatus status = new DevrockPluginStatus( msg, e);
			DevrockPlugin.instance().log(status);	
		}
	}
	
	/**
	 * remove all matching markers from the project
	 * @param project - the {@link IProject} 
	 * @param markerTypes - the types of the marker as an array of {@link String}
	 */
	public static void removeFailedResolutionMarkersFromProject( IProject project, String ... markerTypes ) {
		if (!project.isAccessible() || project.isOpen())
			return;
		Set<String> markerTypesToClear = null;
		if (markerTypes != null && markerTypes.length > 0) {
			markerTypesToClear = new HashSet<>( Arrays.asList( markerTypes));
		}
		try {
			IMarker[] markers = project.findMarkers( null, false,  IResource.DEPTH_INFINITE);
			if (markers != null) {
				for (IMarker marker : markers) {
					if (markerTypesToClear != null) {
						if (markerTypesToClear.contains( marker.getType())) {
							marker.delete();
						}
					}
					else {
						marker.delete();
					}
				}
			}
		} catch (Exception e) {
			String msg;		
			if (markerTypesToClear != null) {
				msg = "cannot remove all markers of types [" + markerTypesToClear.stream().collect( Collectors.joining(",")) + "] from project [" + project.getName() + "]";
			}
			else {
				msg = "cannot remove all markers from project [" + project.getName() + "]";
			}
			log.error( msg);
			DevrockPluginStatus status = new DevrockPluginStatus( msg, e);
			DevrockPlugin.instance().log(status);	
		}
	}

	
	/**
	 * ensures that the project get's a 'Container resolution failed' marker 
	 * @param project - the {@link IProject}
	 */
	public static void ensureFailedResolutionMarker( IProject project) {
		if (!project.isAccessible() || project.isOpen())
			return;
		// find main marker 
		IMarker[] markers = null;
		try {
			markers = project.findMarkers( MRK_MAIN, false,  IResource.DEPTH_INFINITE);
		} catch (Exception e1) {
			String msg = "cannot check for marker [" + MRK_MAIN + "] in project [" + project.getName() + "]";
			log.error( msg);
			DevrockPluginStatus status = new DevrockPluginStatus( msg, e1);
			DevrockPlugin.instance().log(status);
		}
		
		// find none's found, create one 
		try {
			if (markers == null || markers.length == 0) {
				IMarker marker = project.createMarker(MRK_MAIN);				
				marker.setAttribute(IMarker.MESSAGE, "Resolution failed - see container data for details");
		        marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
			}
		} catch (Exception e) {
			String msg = "cannot create marker [" + MRK_MAIN + "] in project [" + project.getName() + "]";
			log.error( msg);
			DevrockPluginStatus status = new DevrockPluginStatus( msg, e);
			DevrockPlugin.instance().log(status);
		}
	}
}
