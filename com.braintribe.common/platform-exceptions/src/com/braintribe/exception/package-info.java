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
/**

<h2>Important Unchecked Exceptions</h2>

<ul>
<li>{@link java.lang.NullPointerException}
<li>{@link java.lang.IllegalArgumentException}
<li>{@link java.lang.IllegalStateException}
<li>{@link java.lang.UnsupportedOperationException}
<li>{@link java.util.NoSuchElementException}
<li>{@link java.io.UncheckedIOException}
<li>{@link com.braintribe.exception.CommunicationException}
<li>{@link com.braintribe.exception.AuthorizationException}
<li>{@link com.braintribe.exception.CanceledException}
<li>{@link com.braintribe.exception.GenericServiceException}

java.lang.IllegalArgumentException: 4056: you gave me wrong arguments
</ul>

<h2>Important Checked Exceptions</h2>
<ul>
<li>{@link java.lang.InterruptedException}
</ul>

<p>
The commonly used strategy of exception handling has the disadvantage that it always wraps exceptions in order to add contextual information from
higher call sites. It also often forces to wrap in order to pass on checked exceptions that are not announced by the throws clause of the catching method.
Such repeated wrappings lead to obscure stacktraces which treat the important root cause with less priority than all its wrappings. This is especially
annoying when trying to react on specific exceptions because they could be hidden by wrappings and therefore require extra scanning in the chain of causes.
Also the rendering confuses with truncated stack frames in order to avoid the inherent redundancy. 
This redundancy is not only useless but also costly as each new constructed wrapper exception involves a determination of the stack
frames which is an expensive reflective operation.   

<p>
In order to solve all the issues that are made up by the common strategy we introduce a new strategy that is supported by this class.

<p>
The new strategy avoids unnecessary wrappings by using dynamically acquired suppressed exceptions to store additional contextual information
from higher call sites in method {@link com.braintribe.exception.Exceptions#contextualize(Throwable, String)}. Wrapping exceptions should only be done if really needed in case
of undeclared checked exceptions by using one of the following methods:
<ul>
 <li>{@link com.braintribe.exception.Exceptions#unchecked(Throwable, String)}
 <li>{@link com.braintribe.exception.Exceptions#unchecked(Throwable, String, java.util.function.BiFunction)}
 <li>{@link com.braintribe.exception.Exceptions#uncheckedAndContextualize(Throwable, String, java.util.function.Function)}
</ul>

<p>
This class offers stringification of exceptions in a different way than {@link java.lang.Throwable#printStackTrace()}. It only renders the root cause
and its stack frames and joins contextual information from wrappers and informations from suppressed exceptions on those:
<ul>
 <li>{@link com.braintribe.exception.Exceptions#stringify(Throwable)}
 <li>{@link com.braintribe.exception.Exceptions#stringify(Throwable, java.lang.Appendable)}
</ul>

<h2>Examples</h2>
<pre>
</pre>

 */
package com.braintribe.exception;
