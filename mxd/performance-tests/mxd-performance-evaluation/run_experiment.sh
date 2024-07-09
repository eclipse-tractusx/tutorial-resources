#!/bin/bash
#
#  Copyright (c) 2024 Contributors to the Eclipse Foundation
#
#  See the NOTICE file(s) distributed with this work for additional
#  information regarding copyright ownership.
#
#  This program and the accompanying materials are made available under the
#  terms of the Apache License, Version 2.0 which is available at
#  https://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
#  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
#  License for the specific language governing permissions and limitations
#  under the License.
#
#  SPDX-License-Identifier: Apache-2.0
#

experiment_property="mount/custom_experiment.properties"

if [ -z "$JMETER_HOME" ]; then
  echo "JMETER_HOME is not defined - please define it."
  exit 1
fi
echo "Jmeter found at path $JMETER_HOME"

jmeter_binary="$JMETER_HOME/bin/jmeter"

rm -rf output
mkdir -p output/dashboard

rm -rf output_slim
mkdir -p output_slim/dashboard

rm -rf output_jtl
mkdir -p output_jtl

echo "*** Property: $experiment_property Start ***"
cat $experiment_property
echo "*** Property: $experiment_property End ***"

echo "Copying custom_experiment.properties to output folder with new name metadata.txt"
cp $experiment_property output/metadata.txt

echo "*** Performance Test Start ***"
if [ -z "$jmeter_script" ]; then
  echo "Executing all scripts in order"
  echo "Executing setup.jmx"
  $jmeter_binary -n -t mount/setup.jmx -l output_jtl/setup.jtl -q $experiment_property -j "output/setup_jmeter.log"
  echo "Executing measurement_interval.jmx"
  $jmeter_binary -n -t mount/measurement_interval.jmx -l output_jtl/measurement_interval.jtl -q $experiment_property \
    -j "output/measurement_interval_jmeter.log" -e -o output/dashboard
fi
echo "*** Performance Test End ***"

echo "Creating tar file with output"
tar -cf output.tar output

echo "Copying custom_experiment.properties to output_slim folder with new name metadata.txt"
cp $experiment_property output_slim/metadata.txt
echo "Copying output/dashboard/statistics.json to output_slim/dashboard/statistics.json folder"
cp output/dashboard/statistics.json output_slim/dashboard/statistics.json
echo "Creating tar file with output"
tar -cf output_slim.tar output_slim

tar -czf output_jtl.tar.gz output_jtl

echo "*** Test Completed..., Sleeping now ***"
while true; do sleep 10000; done
