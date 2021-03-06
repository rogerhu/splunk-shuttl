// Copyright (C) 2011 Splunk Inc.
//
// Splunk Inc. licenses this file
// to you under the Apache License, Version 2.0 (the
// License); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an AS IS BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.splunk.shuttl.testutil;

import java.io.File;
import java.io.IOException;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

/**
 * HadoopFileSystemPutter puts a file on a hadoop {@link FileSystem} in a safe
 * place. <br>
 * It uses the {@link SafePathCreator} to get a unique, readable and writable
 * directory on Hadoop. <br>
 * Use convenience method {@link HadoopFileSystemPutter#create(FileSystem)} to
 * instanciate this class with whatever filesystem you'd like to use.
 * 
 * @author periksson
 * 
 */
public class HadoopFileSystemPutter {

	public static class LocalFileNotFound extends RuntimeException {
		private static final long serialVersionUID = 1L;
	}

	private final FileSystem fileSystem;

	public HadoopFileSystemPutter(FileSystem fileSystem) {
		this.fileSystem = fileSystem;
	}

	public void putFile(File source) {
		if (!source.exists())
			throw new LocalFileNotFound();
		else
			putFileOnHadoopFileSystemHandlingIOExceptions(source);
	}

	private void putFileOnHadoopFileSystemHandlingIOExceptions(File src) {
		try {
			fileSystem.copyFromLocalFile(new Path(src.getPath()),
					getSafePathOnFileSystemForFile(src));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private Path getSafePathOnFileSystemForFile(File src) {
		Path safeDirectory = getSafePathForClassPuttingFile();
		return new Path(safeDirectory, src.getName());
	}

	private Path getSafePathForClassPuttingFile() {
		Class<?> callerToThisMethod = MethodCallerHelper.getCallerToMyMethod();
		Path safeDirectory = TUtilsPath.getSafeDirectory(fileSystem,
				callerToThisMethod);
		return safeDirectory;
	}

	public boolean isFileCopiedToFileSystem(File file) {
		try {
			return fileSystem.exists(getSafePathOnFileSystemForFile(file));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public Path getPathOfMyFiles() {
		return getSafePathForClassPuttingFile();
	}

	public void deleteMyFiles() {
		deletePathOnHadoopFileSystemHandlingIOException(getPathOfMyFiles());
	}

	private void deletePathOnHadoopFileSystemHandlingIOException(Path filesDir) {
		try {
			fileSystem.delete(filesDir, true);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public Path getPathForFile(File file) {
		return getSafePathOnFileSystemForFile(file);
	}

	public static HadoopFileSystemPutter create(FileSystem fileSystem) {
		return new HadoopFileSystemPutter(fileSystem);
	}

	public Path getPathForFileName(String fileName) {
		return getPathForFile(new File(fileName));
	}
}
