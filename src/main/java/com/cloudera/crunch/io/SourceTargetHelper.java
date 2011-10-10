/**
 * Copyright (c) 2011, Cloudera, Inc. All Rights Reserved.
 *
 * Cloudera, Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"). You may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the
 * License.
 */
package com.cloudera.crunch.io;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.InputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.OutputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.CrunchMultipleOutputs;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import com.cloudera.crunch.impl.mr.run.CrunchInputs;
import com.cloudera.crunch.type.DataBridge;

/**
 * Functions for configuring the inputs/outputs of MapReduce jobs.
 *
 */
public class SourceTargetHelper {
  public static void configureSource(Job job, int sourceId,
      Class<? extends InputFormat> inputFormatClass, Path path) throws IOException {
    if (sourceId == -1) {
      FileInputFormat.addInputPath(job, path);
      job.setInputFormatClass(inputFormatClass);
    } else {
      CrunchInputs.addInputPath(job, path, inputFormatClass, sourceId);
    }
  }
  
  public static void configureTarget(Job job, Class<? extends OutputFormat> outputFormatClass,
      DataBridge handler, Path path, String name) {
    FileOutputFormat.setOutputPath(job, path);
    if (name == null) {
      job.setOutputFormatClass(outputFormatClass);
      job.setOutputKeyClass(handler.getKeyClass());
      job.setOutputValueClass(handler.getValueClass());
    } else {
      CrunchMultipleOutputs.addNamedOutput(job, name, outputFormatClass,
          handler.getKeyClass(), handler.getValueClass());
    }
  }
  
  public static long getPathSize(Configuration conf, Path path) throws IOException {
    FileSystem fs = FileSystem.get(conf);
    long size = 0;
    for (FileStatus status : fs.listStatus(path)) {
      size += status.getLen();
    }
    return size;
  }
}