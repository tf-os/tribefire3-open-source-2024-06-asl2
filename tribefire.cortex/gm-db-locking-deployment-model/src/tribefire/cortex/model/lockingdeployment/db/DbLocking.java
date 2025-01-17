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
package tribefire.cortex.model.lockingdeployment.db;

import java.util.concurrent.locks.Lock;

import com.braintribe.model.deployment.database.pool.DatabaseConnectionPool;
import com.braintribe.model.generic.annotation.Initializer;
import com.braintribe.model.generic.annotation.meta.Mandatory;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.lockingdeployment.Locking;

/**
 * 
 */
public interface DbLocking extends Locking {

	EntityType<DbLocking> T = EntityTypes.T(DbLocking.class);

	@Mandatory
	DatabaseConnectionPool getDatabaseConnection();
	void setDatabaseConnection(DatabaseConnectionPool databaseConnection);

	@Initializer("true")
	boolean getAutoUpdateSchema();
	void setAutoUpdateSchema(boolean autoUpdateSchema);

	/** Describes the re-try interval to acquire a lock in case the first try wasn't successful. Relevant for the {@link Lock#tryLock(long, java.util.concurrent.TimeUnit)} */
	@Initializer("100")
	int getPollIntervalInMillis();
	void setPollIntervalInMillis(int pollIntervalInMillis);

	/**
	 * Time period after which a lock expires and is automatically unlocked.
	 * <p>
	 * Typical lock is unlock explicitly (in the <tt>finally</tt> block), this is only relevant if a node dies (JVM is terminated). This is not a
	 * regular case and thus the value can be higher, e.g. the default of 5 minutes.
	 * <p>
	 * While the lock is being held, it's expiration is updated automatically, with a shorter interval (half of this value).
	 */
	@Initializer("60")
	int getLockExpirationInSecs();
	void setLockExpirationInSecs(int lockExpirationInSecs);

	@Initializer("5000L")
	long getTopicExpirationInMillis();
	void setTopicExpirationInMillis(long topicExpirationInMillis);

}
