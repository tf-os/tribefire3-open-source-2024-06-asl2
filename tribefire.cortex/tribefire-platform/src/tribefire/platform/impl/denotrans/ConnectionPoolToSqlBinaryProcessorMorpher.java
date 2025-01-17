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
package tribefire.platform.impl.denotrans;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.model.deployment.database.pool.DatabaseConnectionPool;
import com.braintribe.model.deployment.resource.sql.SqlBinaryProcessor;

import tribefire.module.api.DenotationTransformationContext;
import tribefire.module.api.SimpleDenotationMorpher;

/**
 * @author peter.gazdik
 */
public class ConnectionPoolToSqlBinaryProcessorMorpher extends SimpleDenotationMorpher<DatabaseConnectionPool, SqlBinaryProcessor> {

	public ConnectionPoolToSqlBinaryProcessorMorpher() {
		super(DatabaseConnectionPool.T, SqlBinaryProcessor.T);
	}

	@Override
	public Maybe<SqlBinaryProcessor> morph(DenotationTransformationContext context, DatabaseConnectionPool denotation) {
		SqlBinaryProcessor processor = context.create(SqlBinaryProcessor.T);
		processor.setConnectionPool(denotation);

		return Maybe.complete(processor);
	}

}
