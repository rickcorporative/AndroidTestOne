# Volt Stream Processing Quickstart

This project contains three example pipelines of varying complexity designed to gradually introduce the reader to the
Volt(SP) API.

Start with `RandomToConsolePipeline.java`. This class defines a simple pipeline that generates random strings and
prints them to the console. You will learn how to name, define, and run a pipeline. Additionally, you will learn how to
customize a pipeline at deployment time using environment variables.

To run the second pipeline, you will need a Kafka cluster. The `RandomToKafkaPipeline.java` replaces the console sink
with a Kafka topic.

The third and final pipeline will use the Kafka topic that the previous one writes to, transform that data, and insert
it into a VoltDB database.

## Working with the Example Pipelines

There are two steps required to run the examples:

1. Build the JAR file.
2. Deploy the pipelines to a Kubernetes environment using Helm.

Depending on the features of Volt(SP) being used, you might also need a `license.xml` file, a Kafka broker, and a VoltDB
cluster.

### 1. Building the JAR File

Java 17 is required to compile and build Volt(SP) applications.

```shell
mvn clean package
```

The result is a `target/demo-1.0-SNAPSHOT.jar` file that contains __all__ the example pipeline code. You can
use this file multiple times, with different `yaml` configuration files, to deploy different Volt(SP) pipelines in
Kubernetes clusters.

### 2. Deploying the Pipelines

There are three example pipelines and corresponding configuration files to deploy them:

1.`random-to-console-pipeline.yaml`
1.`random-to-kafka-pipeline.yaml`
1.`kafka-to-volt-pipeline.yaml`

First, define an environment variable that points to the Volt(SP) license. Please replace ... with the path to the
license file:

```shell
export MY_VOLT_LICENSE=...
```

The first pipeline is self-contained and does not need any other components to integrate with. Install it using the Helm
chart from `voltdb/volt-streams`:

```shell
helm install pipeline1 voltdb/volt-streams                              \
  --set-file streaming.licenseXMLFile="${MY_VOLT_LICENSE}"              \
  --set-file streaming.voltapps=target/demo-1.0-SNAPSHOT.jar   \
  --values src/main/resources/random-to-console-pipeline.yaml
```

The second pipeline requires a running Kafka cluster, while the third also needs a VoltDB cluster. You can set up the
required VoltDB schema by running the following SQL command:

```sql
FILE
src/main/resources/ddl.sql;
```

You will also need to create a Kafka topic called "greetings" (or have auto-creation of topics enabled).

Note that configuration YAML files have missing Kafka broker and VoltDB cluster addresses that you need to fill in.

```shell
helm install pipeline2 voltdb/volt-streams                               \
  --set-file streaming.licenseXMLFile="${MY_VOLT_LICENSE}"               \
  --set-file streaming.voltapps=target/demo-1.0-SNAPSHOT.jar    \
  --values src/main/resources/random-to-kafka-pipeline.yaml

helm install pipeline3 voltdb/volt-streams                               \
  --set-file streaming.licenseXMLFile="${MY_VOLT_LICENSE}"               \
  --set-file streaming.voltapps=target/demo-1.0-SNAPSHOT.jar    \
  --values src/main/resources/kafka-to-volt-pipeline.yaml
```

### 3. Monitoring the Pipelines

Volt(SP) includes a Management Console Helm chart that deploys a well-known OSS stack comprising Prometheus, Grafana,
Loki, and Pyroscope. Grafana comes pre-configured with dashboards dedicated to monitoring Volt(SP), VoltDB, and a
Kafka cluster. Together, they form a powerful stack to monitor the health, logs, and performance profiles of a
running streaming application.

You can deploy the Management Console while installing a pipeline by setting `management-console.enabled` to true. When
installing multiple pipelines in the same Kubernetes namespace, it is sufficient to enable the Management Console when
installing just one of them:

```shell
helm install pipeline1 voltdb/volt-streams                               \
  --set management-console.enabled=true                                  \
  --set-file streaming.licenseXMLFile=${MY_VOLT_LICENSE}                 \
  --set-file streaming.voltapps=target/demo-1.0-SNAPSHOT.jar    \
  --values src/main/resources/random-to-console-pipeline.yaml
```

#### 1. The Management Console

You can also install the Management Console from a stand-alone chart:

```shell
helm install mc voltdb/management-console 
```

Or with the flamegraph profiler enabled:

```shell
helm install mc voltdb/management-console --set pyroscope.enabled=true
```

#### 2. Capturing Logging

When installing a pipeline with the Management Console enabled, everything will be set up for you. If the Management
Console is installed as a stand-alone release, individual pipelines need to know the URLs of the Management Console
services to send data to them (let's assume it was installed under the `mc` release):

- Metrics are handled automatically by Prometheus. All pipelines installed in the same namespace as the Management Console
  are automatically monitored.
- Logs need to be sent to the Loki service, so each pipeline needs to set:
  ```shell
    --set monitoring.promtail.enabled=true                                  
    --set monitoring.promtail.lokiUrl=mc-management-console-loki:3100
  ```
- Profiles need to be sent to Pyroscope, so each pipeline needs to set:
  ```shell
        --set monitoring.profiler.enabled=true                                  \
        --set monitoring.profiler.pyroscopeUrl=http://mc-management-console-pyroscope:4040 \
  ```
