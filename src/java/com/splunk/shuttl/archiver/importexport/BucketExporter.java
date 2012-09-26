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

package com.splunk.shuttl.archiver.importexport;

import static com.splunk.shuttl.archiver.LogFormatter.*;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.splunk.shuttl.archiver.archive.BucketFormat;
import com.splunk.shuttl.archiver.archive.UnknownBucketFormatException;
import com.splunk.shuttl.archiver.importexport.csv.CsvExporter;
import com.splunk.shuttl.archiver.model.Bucket;

/**
 * For exporting a {@link Bucket} to {@link BucketFormat#CSV}, since it's
 * currently the only other format to export to.
 */
public class BucketExporter {

	public class UnknownFormatChangerToFormatException extends RuntimeException {

		private static final long serialVersionUID = 1L;

	}

	private final static Logger logger = Logger.getLogger(BucketExporter.class);
	private Map<BucketFormat, BucketFormatChanger> formatChangers;

	/**
	 * @param bucketToCsvFileExporter
	 *          for exporting the bucket to a .csv file.
	 * @param csvBucketCreator
	 *          for creating a {@link Bucket} from the .csv file.
	 */
	public BucketExporter(Map<BucketFormat, BucketFormatChanger> formatChangers) {
		this.formatChangers = formatChangers;
	}

	/**
	 * @return a new {@link Bucket} in the new format.
	 */
	public Bucket exportBucket(Bucket bucket, BucketFormat newFormat) {
		if (newFormat.equals(BucketFormat.UNKNOWN))
			logAndThrowUnknownFormatException(bucket, newFormat);

		if (!isSameFormat(bucket, newFormat))
			return getBucketInNewFormat(bucket, newFormat);
		else
			return bucket;
	}

	private void logAndThrowUnknownFormatException(Bucket bucket,
			BucketFormat newFormat) {
		logger.debug(warn("Attempted to export bucket to newFormat",
				"Bucket was in an Unknown format", "Throwing exception", "bucket",
				bucket, "new_format", newFormat));
		throw new UnknownBucketFormatException();
	}

	private boolean isSameFormat(Bucket bucket, BucketFormat newFormat) {
		return bucket.getFormat().equals(newFormat);
	}

	private Bucket getBucketInNewFormat(Bucket bucket, BucketFormat newFormat) {
		if (bucket.getFormat().equals(BucketFormat.SPLUNK_BUCKET))
			return changeFormatFromSplunkBucketToFormat(bucket, newFormat);
		else
			throw new UnsupportedOperationException("Can only ");
	}

	private Bucket changeFormatFromSplunkBucketToFormat(Bucket bucket,
			BucketFormat newFormat) {
		if (formatChangers.containsKey(newFormat))
			return formatChangers.get(newFormat).changeFormat(bucket);
		else
			throw new UnknownFormatChangerToFormatException();
	}

	/**
	 * @return an instance of the {@link BucketExporter}
	 */
	public static BucketExporter create(CsvExporter csvExporter) {
		Map<BucketFormat, BucketFormatChanger> formatChangers = new HashMap<BucketFormat, BucketFormatChanger>();
		formatChangers.put(BucketFormat.CSV, csvExporter);

		return new BucketExporter(formatChangers);
	}

}
