# Tractus-X EDC Performance Test

## 1. Prerequisites
- Download and Install  [Apache JMeter](https://jmeter.apache.org/download_jmeter.cgi).
- Add JMETER_HOME/bin to the environment path. [Optional]
- Deploy Tractusx EDC connectors. If you don't have, you can deploy MXD.

## 2. How to run
- Update connectors properties in the file [connector.properties](connector.properties).
- Run Following command.
```shell
jmeter -n -t MXD_Performance_Test.jmx -l report.jtl -q connector.properties -e -o report
```

> It will create a raw `report.jtl` file which will have all the results. It will also generate a nice html report in `report` directory at the end of the performance test.

## 3. Update the JMeter Script
- Open the script:
```shell
jmeter -t MXD_Performance_Test.jmx
```
- JMeter window will open like this.
![apache-jmeter-ui.png](../assets/apache-jmeter-ui.png)
- Update the components as per your requirements and save it.

## Properties Configuration
JMeter Script can be configured via changing properties in the file  [connector.properties](connector.properties)

Following table explains the properties in details.

| Property                   | Default Value                             | Detail                                                                                             |
|----------------------------|-------------------------------------------|----------------------------------------------------------------------------------------------------|
| PROVIDER_MANAGEMENT_URL    | http://localhost/alice/management         | Provider Data Management URL                                                                       |
| CONSUMER_MANAGEMENT_URL    | http://localhost/bob/management           | Consumer Data Management URL                                                                       |
| PROVIDER_PROTOCOL_URL      | http://alice-controlplane:8084/api/v1/dsp | Provider DSP Protocol URL                                                                          |
| CONSUMER_PROTOCOL_URL      | http://bob-controlplane:8084/api/v1/dsp   | Consumer DSP Protocol URL                                                                          |
| PROVIDER_ID                | BPNL000000000001                          | Provider BPN Number                                                                                |
| CONSUMER_ID                | BPNL000000000002                          | Consumer BPN Number                                                                                |
| BACKEND_SERVICE            | http://backend:8080                       | Consumer Backend Service Url to receive assets data                                                |
| EDC_NAMESPACE              | https://w3id.org/edc/v0.0.1/ns/           | EDC Namespace                                                                                      |
| PROVIDER_API_KEY           | password                                  | Provider Data Management API Key                                                                   |
| CONSUMER_API_KEY           | password                                  | Consumer Data Management API Key                                                                   |
| MAX_NEGOTIATION_POLL_COUNT | 10                                        | Maximum no of time to poll for negotiation state                                                   |
| MAX_TRANSFER_POLL_COUNT    | 10                                        | Maximum no of time to poll for transfer state                                                      |
| LOOP_COUNT                 | 10                                        | No of time to run the test. This can be increased to a significantly large number like 1M, 5M etc. |


## 5. Resources
- [JMeter Getting Started](https://jmeter.apache.org/usermanual/get-started.html)
- [JMeter Component Reference](https://jmeter.apache.org/usermanual/component_reference.html)
- [JMeter Functions Reference](https://jmeter.apache.org/usermanual/functions.html)
- [JMeter Generating Report](https://jmeter.apache.org/usermanual/generating-dashboard.html)
