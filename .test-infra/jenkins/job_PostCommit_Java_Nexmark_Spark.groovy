/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import common_job_properties
import NexmarkBigqueryProperties
import NoPhraseTriggeringPostCommitBuilder

// This job runs the suite of ValidatesRunner tests against the Spark runner.
NoPhraseTriggeringPostCommitBuilder.postCommitJob('beam_PostCommit_Java_Nexmark_Spark',
        'Spark Runner Nexmark Tests', this) {
  description('Runs the Nexmark suite on the Spark runner.')

  // Execute concurrent builds if necessary.
  concurrentBuild()

  // Set common parameters.
  common_job_properties.setTopLevelMainJobProperties(delegate, 'master', 240)

  // Gradle goals for this job.
  steps {
    shell('echo *** RUN NEXMARK IN BATCH MODE USING SPARK RUNNER ***')
    gradle {
      rootBuildScriptDir(common_job_properties.checkoutDir)
      tasks(':beam-sdks-java-nexmark:run')
      common_job_properties.setGradleSwitches(delegate)
      switches('-Pnexmark.runner=":beam-runners-spark"' +
              ' -Pnexmark.args="' +
              [NexmarkBigqueryProperties.nexmarkBigQueryArgs,
              '--runner=SparkRunner',
              '--streaming=false',
              '--suite=SMOKE',
              '--streamTimeout=60' ,
              '--manageResources=false',
              '--monitorJobs=true'].join(' '))
    }
  }
}
