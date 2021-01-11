# DevConf.CZ 2021: Apache Kafka as a Monitoring Data Pipeline

This repository container the demo for my talk at the [DevConf.CZ](https://www.devconf.info/cz/) conference in 2021 about Apache Kafka as a Monitoring Data Pipeline.

## Prerequisites

### Namespace

This demo expects to be run in the namespace `myproject`.
Before running the demo, create it and set it as the default namespace.

### Strimzi Kafka Operator

To deploy and run Kafka, we will use Strimzi operator.
Before we deploy Kafka, we have to install the operator:

```
kubectl apply -f 00-strimzi/
```

### Kafka cluster

Next, we have to deploy the Kafka cluster.
The configuration of the deployment is in [`01-kafka.yaml`](./01-kafka.yaml) which has to be applied.

```
kubectl apply -f 01-kafka.yaml
```

### Kafka Connect

One of the easiest way to get messages out of Kafka is to use Kafka Connect and different Connectors.
So next we will deploy Kafka Connect with additional connectors - ElasticSearch Connector, AWS S3 connector and Slack connector.
We will use these later to get the data from Kafka.
The definition is in [`02-kafka-connect.yaml`](./02-kafka-connect.yaml).

```
kubectl apply -f 02-kafka-connect.yaml
```

### Secrets

#### Docker secret

Create Docker credentials secret names `kafkaconnectbuild-pull-secret`.
It should be secret of type `kubernetes.io/dockerconfigjson` and contain credentials to some Container registry account which will be used for the Kafka Connect build.
This secret is referenced in [`04-kafka-connect.yaml`](./04-kafka-connect.yaml).
You might need to also edit the container image used in [`04-kafka-connect.yaml`](./04-kafka-connect.yaml) to match your own container registry.

## Demo

### Logs topic

First we have to create a topic where the logs will be sent.
The definition for the Strimzi operator is in [`03-fluent-bit-logs-topic.yaml`](./03-fluent-bit-logs-topic.yaml).
Create it with `kubectl apply`.

```
kubectl apply -f 03-fluent-bit-logs-topic.yaml
```

### Fluent Bit

Next we have to deploy Fluent-bit and configure it to send the logs to Kafka.
We have to create all the different RBAC resource and a config map with configuration.
The FLuent-bit it self runs as DeamonSet to collect the logs from all nodes.
The whole Fluent Bit deployment is in [`04-fluentbit.yaml`](./04-fluentbit.yaml)

```
kubectl apply -f 04-fluentbit.yaml
```

### Check log messages in Kafka

Get the address of the Kafka broker from the status in `kubectl get kafka -o yaml`
And use it with Kafkacat to see the messages from Fluent Bit.

```
kafkacat -C -b 192.168.1.222:9094 -t fluent-bit-logs -f '%s\n\n' -o end
```

We should see the messages in JSON format being received.

### Deploy ElasticSearch and Kibana

Next we deploy ElasticSearch to use for looking through the logs.
The YAMLs for both are in [`05-elasticsearch.yaml`](./05-elasticsearch.yaml) and [`06-kibana.yaml`](./06-kibana.yaml).

```
kubectl apply -f 05-elasticsearch.yaml
kubectl apply -f 06-kibana.yaml
```

### Push data to ElasticSearch

Now with everything running, we can use the Camel Elastic Search connector to push the logs to ElasticSearch.
We can deploy it using the [`07-elasticsearch-connector.yaml`](./07-elasticsearch-connector.yaml).

```
kubectl apply -f 07-elasticsearch-connector.yaml
```

After it is deployed and running, we can go to Kibana, create the index and check the data.