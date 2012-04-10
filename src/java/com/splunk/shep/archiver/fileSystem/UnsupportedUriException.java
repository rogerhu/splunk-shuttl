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
package com.splunk.shep.archiver.fileSystem;

import java.net.URI;

/**
 * Thrown when method does not support the uri.</br> See
 * {@link ArchiveFileSystemFactory#getForUriToTmpDir(URI)}
 */
public class UnsupportedUriException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UnsupportedUriException(String message) {
	super(message);
    }

}