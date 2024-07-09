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

# Run mxd performance tests

# Constants
LOG_MESSAGE="Test Completed"
POD_NAME="mxd-performance-test"
GENERATED_OUTPUT_FILE="/opt/apache-jmeter-5.5/mxd-performance-evaluation/output.tar"
GENERATED_OUTPUT_SLIM_FILE="/opt/apache-jmeter-5.5/mxd-performance-evaluation/output_slim.tar"
GENERATED_OUTPUT_JTL_FILE="/opt/apache-jmeter-5.5/mxd-performance-evaluation/output_jtl.tar.gz"
CUSTOM_PROPERTIES="custom_experiment.properties"
TERRAFORM_LOGFILE="terraform_logs_$(date +%d-%m-%YT%H-%M-%S).logs"
TEST_CONFIGURATION_FILE_EXTENSION=".properties"

#defaults
path_to_test_configuration="test-configurations/small_experiment.properties"
terraform_dir="$(dirname "$0")/.."
test_pod_context="kind-mxd"
test_environment_context="kind-mxd"
is_debug=true


#Override defaults with provided command line arguments
while getopts "f:t:x:y:d" opt; do
    case $opt in
        f) path_to_test_configuration=$OPTARG;;
        t) terraform_dir=$OPTARG;;
        x) test_pod_context=$OPTARG;;
        y) test_environment_context=$OPTARG;;
        d) is_debug=$OPTARG;;
        \?) cat help.txt; exit 1;;
    esac
done


# Prints an info message
print_info_log() {
  echo -e "$(date +%d-%m-%Y) $(date +%H:%M:%S) \033[32m INFO  \033[0m $@"
}

# Prints a debug message
print_debug_log() {
  read IN
  if [[ $is_debug == true ]]; then
    echo -e "$(date +%d-%m-%Y) $(date +%H:%M:%S) \033[33m DEBUG \033[0m $IN"
  fi
}

# Prints an error message
print_error_log() {
  echo -e "$(date +%d-%m-%Y) $(date +%H:%M:%S) \033[31m ERROR \033[0m $@"
}

# Prints error messages and exits with error code
print_error_log_and_exit() {
  echo -e "$(date +%d-%m-%Y) $(date +%H:%M:%S) \033[31m ERROR \033[0m $@"
  cleanup_test_environment
  exit 1
}


#######################################
# Sets up test environment
# Globals:
#   CUSTOM_PROPERTIES
#   TERRAFORM_LOGFILE
#   POD_NAME
#   terraform_dir
#   test_pod_context
#   test_environment_context
# Arguments:
#   experiment_file - .properties file used as test configuration
# Outputs:
#   Creates all the needed pods for running performance tests
#######################################
setup_test_environment() {
  local experiment_file=$1

  print_info_log "Adding ${experiment_file} on pod using custom-property configmap"
  kubectl create configmap custom-property \
    --from-file="run_experiment.sh=mxd-performance-evaluation/run_experiment.sh" \
    --from-file="setup.jmx=mxd-performance-evaluation/setup.jmx" \
    --from-file="measurement_interval.jmx=mxd-performance-evaluation/measurement_interval.jmx" \
    --from-file="${CUSTOM_PROPERTIES}=${experiment_file}" \
    --context="${test_pod_context}" \
    | print_debug_log \
    || print_error_log_and_exit "Failed to create configmap with name custom-property"

  print_info_log "Init terraform"
  terraform -chdir="${terraform_dir}" init \
    >> "${TERRAFORM_LOGFILE}" \
    || print_error_log_and_exit "Failed to initialize Terraform"

  print_info_log "Create namespace monitoring"
  kubectl create namespace monitoring --context="${test_environment_context}" \
    | print_debug_log \
    || print_error_log_and_exit "Failed to create namespace monitoring"

  print_info_log "Deploy Prometheus"
  kubectl apply -f prometheus --context="${test_environment_context}" \
    || print_error_log_and_exit "Failed to deploy Prometheus"

  print_info_log "Apply terraform"
  terraform -chdir="${terraform_dir}" apply -auto-approve \
    >> "${TERRAFORM_LOGFILE}" \
    || print_error_log_and_exit "Failed to apply Terraform"

  print_info_log "Create performance tests pod"
  kubectl apply -f performance-test.yaml --context="${test_pod_context}" \
    | print_debug_log \
    || print_error_log_and_exit "Failed to deploy Prometheus"

  print_info_log "Waiting for test pod ready state"
  kubectl wait --for=condition=ready "pod/${POD_NAME}" --context="${test_pod_context}" \
    | print_debug_log \
    || print_error_log_and_exit "Test pod failed to reach ready state"
}

#######################################
# Copies test result files to local machine
# Globals:
#   POD_NAME
#   LOG_MESSAGE
#   GENERATED_OUTPUT_FILE
#   GENERATED_OUTPUT_SLIM_FILE
#   test_pod_context
# Arguments:
#   experiment_file - .properties file used as test configuration
# Outputs:
#   It's copying the 2 .tar files containing the test results
#   form the test pod in the local /test-configurations folder
#######################################
copy_test_result_files() {
  local experiment_file=$1
  local logs

  #Save connector's image in metadata.txt
  local connector_version
  connector_version=$(kubectl get deploy alice-tractusx-connector-controlplane -o jsonpath="{..image}")
  kubectl exec -it --namespace=default mxd-performance-test -- \
    bash -c "echo CONNECTOR_VERSION=${connector_version}>>mxd-performance-evaluation/output/metadata.txt"

  print_info_log "Waiting for the tests to finish ..."
  while true; do
    logs=$(kubectl logs --tail=5 "${POD_NAME}" --context="${test_pod_context}" 2>/dev/null)

    if echo "${logs}" | grep -q "${LOG_MESSAGE}"; then

      print_info_log "Log message found in the logs."

      kubectl cp --retries=-1 "${POD_NAME}:${GENERATED_OUTPUT_FILE}" \
        "${experiment_file}.tar" --context="${test_pod_context}"
      print_info_log "Test Report downloaded with name output_${experiment_file}.tar"

      kubectl cp --retries=-1 "${POD_NAME}:${GENERATED_OUTPUT_SLIM_FILE}" \
        "${experiment_file}_slim.tar" --context="${test_pod_context}"
      print_info_log "Test Report downloaded with name output_${experiment_file}_slim.tar"

      kubectl cp --retries=-1 "${POD_NAME}:${GENERATED_OUTPUT_JTL_FILE}" \
        "${experiment_file}_jtl.tar.gz" --context="${test_pod_context}"
      print_info_log "Test Report downloaded with name output_${experiment_file}_jtl.tar.gz"

      break
    else
      echo "Waiting for log message" | print_debug_log
      sleep 5
    fi
  done
}

#######################################
# Cleans up test environment
# Globals:
#   terraform_dir
#   test_pod_context
#   test_environment_context
#   POD_NAME
#   TERRAFORM_LOGFILE
# Arguments: -
# Outputs:
#   Deletes all the pods and config maps and leaves a clean cluster
#######################################
cleanup_test_environment() {

  print_info_log "Destroying terraform"
  terraform -chdir="${terraform_dir}" destroy -auto-approve \
    >> "${TERRAFORM_LOGFILE}" \
    || print_error_log "Failed to destroy Terraform" \

  print_info_log "Destroying test pod"
  kubectl delete pod "${POD_NAME}" --context="${test_pod_context}" \
    | print_debug_log \
    || print_error_log "Failed to delete test pod"

  print_info_log "Destroying configmap"
  kubectl delete configmap custom-property --context="${test_pod_context}" \
    | print_debug_log \
    || print_error_log "Failed to delete configmap custom-property"

  print_info_log "Destroying Prometheus"
  kubectl delete -f prometheus --context="${test_environment_context}" \
    | print_debug_log \
    || print_error_log "Failed to delete Prometheus"

  print_info_log "Deleting namespace monitoring"
  kubectl delete namespace monitoring --context="${test_environment_context}" \
    | print_debug_log \
    || print_error_log "Failed to delete namespace monitoring"

  print_info_log "Waiting for the test pod to be deleted"
  kubectl wait --for=delete "pod/${POD_NAME}" --context="${test_pod_context}" \
    | print_debug_log \
    || print_error_log "Failed to delete test pod"
}

#######################################
# Starts test for a single file
# Globals: -
# Arguments:
#   experiment_file - .properties file used as test configuration
# Outputs:
#   Creates all the pods, runs tests, copies results and cleans afterwards
#######################################
start_test_for_a_single_file() {
  local experiment_file=$1

  print_info_log Start experiment for file $experiment_file

  setup_test_environment "$experiment_file"
  copy_test_result_files "$experiment_file"
  cleanup_test_environment
}

#######################################
# Starts test for all files in the provided directory
# Globals:
#   TEST_CONFIGURATION_FILE_EXTENSION
# Arguments: -
# Outputs: -
#######################################
start_test_for_all_files_in_directory() {
  # Use an array to store the all properties file in provided folder
  local files_array=()

  # Populate the array with all files in provided folder
  while IFS= read -r -d '' file; do
      files_array+=("$file")
  done < <(find "${path_to_test_configuration}" -type f -name "*${TEST_CONFIGURATION_FILE_EXTENSION}" -print0)

  # Print files in provided folder
  for file in "${files_array[@]}"; do
      print_info_log "Detected following properties file: $file"
  done

  # Start test for one file at a time
  for file in "${files_array[@]}"; do
      start_test_for_a_single_file "$file"
  done
}

#######################################
# Run test for the provided file or folder
# Globals:
#   path_to_test_configuration
# Arguments: -
# Outputs:
# Runs a single file or all the files in the folder
#######################################
run_test_for_provided_file_or_folder() {
  if [ -e "$path_to_test_configuration" ]; then
      if [ -f "$path_to_test_configuration" ]; then
          print_info_log "${path_to_test_configuration} is a file."
          start_test_for_a_single_file "$path_to_test_configuration"
      elif [ -d "$path_to_test_configuration" ]; then
          print_info_log "${path_to_test_configuration} is a directory."
          start_test_for_all_files_in_directory
      else
          print_info_log "${path_to_test_configuration} exists but is neither a file nor a directory."
      fi
  else
      print_info_log "${path_to_test_configuration} does not exist."
  fi
}

# Cleans up resources and exit
cleanup_and_exit() {
  cleanup_test_environment
  exit 0
}

# Execute cleanup_test_environment when CONTROL+C is invoked
trap cleanup_and_exit INT

# Script entry point
run_test_for_provided_file_or_folder
