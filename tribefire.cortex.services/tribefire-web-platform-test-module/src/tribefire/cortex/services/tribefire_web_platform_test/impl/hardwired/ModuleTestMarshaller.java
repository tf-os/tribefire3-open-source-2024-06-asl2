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
package tribefire.cortex.services.tribefire_web_platform_test.impl.hardwired;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import com.braintribe.codec.marshaller.api.GmDeserializationOptions;
import com.braintribe.codec.marshaller.api.GmSerializationOptions;
import com.braintribe.codec.marshaller.api.MarshallException;
import com.braintribe.codec.marshaller.api.Marshaller;
import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.tools.GmValueCodec;
import com.braintribe.utils.IOTools;

/**
 * @author peter.gazdik
 */
public class ModuleTestMarshaller implements Marshaller {

	@Override
	public Object unmarshall(InputStream in, GmDeserializationOptions options) throws MarshallException {
		return unmarshall(in);
	}

	@Override
	public Object unmarshall(InputStream in) throws MarshallException {
		try {
			String s = IOTools.slurp(in, StandardCharsets.UTF_8.name());
			return GmValueCodec.objectFromGmString(s);

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void marshall(OutputStream out, Object value, GmSerializationOptions options) throws MarshallException {
		marshall(out, value);
	}

	@Override
	public void marshall(OutputStream out, Object value) throws MarshallException {
		GenericModelType type = GMF.getTypeReflection().getType(value);
		if (!type.isSimple())
			throw new UnsupportedOperationException("Test error. Only simple values should be used, not: " + value);

		try {
			out.write(GmValueCodec.objectToGmString(value).getBytes());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
