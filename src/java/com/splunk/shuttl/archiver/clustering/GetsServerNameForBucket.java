// Copyright (C) 2011 Splunk Inc.
//
// Splunk Inc. licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.splunk.shuttl.archiver.clustering;

import com.splunk.shuttl.archiver.archive.ArchiveConfiguration;
import com.splunk.shuttl.archiver.model.Bucket;

/**
 * Gets the original server name from where the replicated bucket was indexed
 * at. <br/>
 * Since Splunk 5.0, the buckets can be replicated between indexers. We must
 * prevent these buckets to get archived to different locations, and to do that,
 * we get the original indexers configured server name.
 */
public class GetsServerNameForBucket {

	private final ClusterMaster clusterMaster;
	private final CallsRemoteIndexer remoteIndexer;
	private final ArchiveConfiguration config;

	public GetsServerNameForBucket(ClusterMaster clusterMaster,
			CallsRemoteIndexer remoteIndexer, ArchiveConfiguration config) {
		this.clusterMaster = clusterMaster;
		this.remoteIndexer = remoteIndexer;
		this.config = config;
	}

	public String getServerName(Bucket bucket) {
		if (bucket.isReplicatedBucket())
			return getServerNameForReplicatedBucket(bucket);
		else
			return config.getServerName();
	}

	private String getServerNameForReplicatedBucket(Bucket bucket) {
		IndexerInfo indexerInfo = clusterMaster.indexerForGuid(bucket.getGuid());
		return remoteIndexer.getShuttlConfiguredServerName(indexerInfo);
	}

	public static GetsServerNameForBucket create(ArchiveConfiguration config) {
		return new GetsServerNameForBucket(ClusterMaster.create(),
				CallsRemoteIndexer.create(), config);
	}

}
