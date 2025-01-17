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
package com.braintribe.utils.paths;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EmptyStackException;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.Collector;

public interface PathList extends List<String> {

	static PathList create() {
		return new PathListImpl();
	}

	default PathList pushFilePath(String elements) {
		return push(elements, File.separator);
	}
	default PathList pushSlashPath(String elements) {
		return push(elements, "/");
	}
	default PathList pushBackSlashPath(String elements) {
		return push(elements, "\\");
	}
	default PathList pushDottedPath(String elements) {
		return push(elements, ".");
	}

	PathList push(String elements, String delimiter);

	PathList push(Iterable<String> elements);

	PathList push(String element);

	PathList pop();
	PathList popN(int n);

	PathList copy();

	default PathList getParent() {
		return copy().pop();
	}
	default PathList getNthParent(int n) {
		return copy().popN(n);
	}
	default PathList getChild(String element) {
		return copy().push(element);
	}
	default PathList getSibling(String element) {
		return copy().pop().push(element);
	}

	String toFilePath();
	String toDottedPath();
	String toSlashPath();
	String toBackslashPath();
	String toPath(char delimiter);

	<T> String toPath(Collector<String, T, String> collector);

	File toFile();
	Path toPath();
	URL toUrl();
}

class PathListImpl extends ArrayList<String> implements PathList {

	private static final long serialVersionUID = -6368183726709182418L;

	public PathListImpl() {

	}

	public PathListImpl(Collection<? extends String> c) {
		super(c);
	}

	@Override
	public PathList push(String elements, String delimiter) {
		StringTokenizer tokenizer = new StringTokenizer(elements, delimiter);

		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			add(token);
		}

		return this;
	}

	@Override
	public PathList push(Iterable<String> elements) {
		for (String element : elements) {
			push(element);
		}
		return this;
	}

	@Override
	public PathList push(String element) {
		add(element);
		return this;
	}

	@Override
	public PathList pop() throws EmptyStackException {
		if (isEmpty()) {
			throw new EmptyStackException();
		}
		remove(size() - 1);
		return this;
	}

	@Override
	public PathList popN(int n) {
		for (int i = 0; i < n; i++) {
			pop();
		}
		return this;
	}

	@Override
	public PathList copy() {
		return new PathListImpl(this);
	}

	@Override
	public String toFilePath() {
		return toPath(PathCollectors.filePath);
	}

	@Override
	public String toDottedPath() {
		return toPath(PathCollectors.dottedPath);
	}

	@Override
	public String toSlashPath() {
		return toPath(PathCollectors.slashPath);
	}

	@Override
	public String toBackslashPath() {
		return toPath(PathCollectors.backslashPath);
	}

	@Override
	public String toPath(char delimiter) {
		return toPath(new PathCollector(delimiter));
	}

	@Override
	public <T> String toPath(Collector<String, T, String> collector) {
		return stream().collect(collector);
	}

	@Override
	public File toFile() {
		return new File(toFilePath());
	}

	@Override
	public Path toPath() {
		return Paths.get(toFilePath());
	}

	@Override
	public String toString() {
		return toSlashPath();
	}

	@Override
	public URL toUrl() {
		String path = toSlashPath();
		try {
			return new URL(path);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Error while building URL from path: " + path, e);
		}
	}
}
