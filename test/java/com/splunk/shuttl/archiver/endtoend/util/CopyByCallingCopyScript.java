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
package com.splunk.shuttl.archiver.endtoend.util;

import static com.splunk.shuttl.testutil.TUtilsFile.*;
import static java.util.Arrays.*;
import static org.testng.Assert.*;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.splunk.shuttl.archiver.endtoend.CopyWithoutDeletionEndToEndTest;
import com.splunk.shuttl.archiver.endtoend.CopyWithoutDeletionEndToEndTest.CopiesBucket;
import com.splunk.shuttl.archiver.importexport.ShellExecutor;
import com.splunk.shuttl.archiver.model.LocalBucket;
import com.splunk.shuttl.testutil.TUtilsBucket;
import com.splunk.shuttl.testutil.TUtilsTestNG;

/**
 * Used for testing {@link CopyWithoutDeletionEndToEndTest}
 */
public class CopyByCallingCopyScript implements CopiesBucket {

	private String splunkHome;

	public CopyByCallingCopyScript(String splunkHome) {
		this.splunkHome = splunkHome;
	}

	@Override
	public void copyBucket(LocalBucket bucket) {
		File copyScript = getCopyScript();
		File directoryToMoveBucketTo = createDirectory();

		executeCopyScript(bucket, copyScript, directoryToMoveBucketTo);
		assertThatTheOriginalBucketWasMovedByTheScript(bucket,
				directoryToMoveBucketTo);

		moveOriginalBucketBackToItsFirstLocation(directoryToMoveBucketTo, bucket);
	}

	private void executeCopyScript(LocalBucket bucket, File copyScript,
			File directoryToMoveBucketTo) {
		ShellExecutor shellExecutor = ShellExecutor.getInstance();
		Map<String, String> env = new HashMap<String, String>();
		env.put("SPLUNK_HOME", splunkHome);
		List<String> command = createCommand(bucket, copyScript,
				directoryToMoveBucketTo);
		int exit = shellExecutor.executeCommand(env, command);
		System.out.println(shellExecutor.getStdErr());
		assertEquals(exit, 0);
	}

	private List<String> createCommand(LocalBucket bucket, File copyScript,
			File directoryToMoveBucketTo) {
		String scriptPath = copyScript.getAbsolutePath();
		String bucketPath = bucket.getDirectory().getAbsolutePath();
		String dirPath = directoryToMoveBucketTo.getAbsolutePath();
		return asList(scriptPath, bucketPath, dirPath);
	}

	private File getCopyScript() {
		String copyScriptPath = splunkHome + "/etc/apps/shuttl/bin/copyBucket.sh";
		File copyScript = new File(copyScriptPath);
		assertTrue(copyScript.exists());
		assertTrue(copyScript.canExecute());
		return copyScript;
	}

	private void assertThatTheOriginalBucketWasMovedByTheScript(
			LocalBucket bucket, File directoryToMoveBucketTo) {
		assertFalse(bucket.getDirectory().exists());
		File[] filesInNewDir = directoryToMoveBucketTo.listFiles();
		assertEquals(filesInNewDir.length, 1);
		File movedBucket = filesInNewDir[0];
		File aRealBucket = TUtilsBucket.createRealBucket().getDirectory();
		TUtilsTestNG.assertDirectoriesAreCopies(movedBucket, aRealBucket);
	}

	private void moveOriginalBucketBackToItsFirstLocation(
			File directoryToMoveBucketTo, LocalBucket bucket) {
		directoryToMoveBucketTo.renameTo(bucket.getDirectory());
	}
}
