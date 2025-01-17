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
package com.braintribe.doc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import com.braintribe.doc.lunr.Builder;
import com.braintribe.doc.lunr.Lunr;
import com.braintribe.utils.stream.KeepAliveDelegateOutputStream;
import com.fasterxml.jackson.databind.ObjectMapper;

public class LunrJsIndexJava implements LunrJsIndex {
	private final Builder<MarkdownFile> builder;
	private final ExecutorService sequentialExecutor = Executors.newSingleThreadExecutor(r -> {
		Thread thread = new Thread(r);
		thread.setPriority(Thread.MAX_PRIORITY);
		return thread;
	});
	private final Collection<Future<?>> taskFutures = new ArrayList<>();

	public LunrJsIndexJava() {
		builder = new Builder<MarkdownFile>(md -> md.getDocRelativeHtmlFileLocation().toSlashPath());
		
		builder.pipeline.add(Lunr.trimmer, Lunr.stopWordFilter, Lunr.stemmer);
		builder.searchPipeline.add(Lunr.stemmer);
		
		builder.field("body", MarkdownFile::unloadContentText, null);
		builder.field("title", MarkdownFile::getTitle, null);
		builder.field("headings", m -> String.join(" ", m.getHeadings()), null);
		builder.field("boosted", m -> String.join(" ", m.getBoostedSearchTerms()), null);
		builder.field("tags", m -> m.getTags().stream().map(Tag::getName).collect(Collectors.joining(" ")), null);
		
	}

	@Override
	public void serialize(File serializedIndexFile) throws IOException {
		MarkdownCompiler.lunrTimer.start("await");
		int timeoutInSeconds = 20;
		try {
			for (Future<?> future: taskFutures) {
				future.get();
			}
		} catch (InterruptedException | ExecutionException e) {
			throw new IllegalStateException("Error while waiting index for generation to complete. Either thread was interupted or timeout of " 
					+ timeoutInSeconds + "s was exceeded.", e);
		}
		sequentialExecutor.shutdownNow();
		
		MarkdownCompiler.lunrTimer.stop("await");
		MarkdownCompiler.lunrTimer.start("build");
		
		Map<String, Object> json = builder.build().toJSON();
		
		try (FileOutputStream out = new FileOutputStream(serializedIndexFile)){
			out.write("serializedIndex = ".getBytes());

			// TODO find a way to write the JSON without this monstrous library (jackson-databind)
			ObjectMapper mapper = new ObjectMapper();
			mapper.writerWithDefaultPrettyPrinter()
			  .writeValue(new KeepAliveDelegateOutputStream(out), json);
		} catch (Exception e) {
			e.printStackTrace();
		}
		MarkdownCompiler.lunrTimer.stop("build");
		
	}

	@Override
	public void add(MarkdownFile markdownFile) {
		Future<?> future = sequentialExecutor.submit(() -> {
			builder.add(markdownFile, null);
		});
		
		taskFutures.add(future);
	}

	@Override
	public void close() {
		// No resources to free
	}
	
}
