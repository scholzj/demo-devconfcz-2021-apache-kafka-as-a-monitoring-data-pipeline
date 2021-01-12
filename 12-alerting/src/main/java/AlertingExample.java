import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import record.LogRecord;
import record.LogRecordDeserializer;
import record.LogRecordSerializer;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;

public class AlertingExample {
    private static final Logger log = LogManager.getLogger(AlertingExample.class);
    private static final Serde<LogRecord> logRecordSerde = Serdes.serdeFrom(new LogRecordSerializer(), new LogRecordDeserializer());

    public static void main(String[] args) {
        AlertingConfig config = AlertingConfig.fromEnv();

        log.info(AlertingConfig.class.getName() + ": {}",  config.toString());

        Properties props = AlertingConfig.createProperties(config);

        StreamsBuilder builder = new StreamsBuilder();

        builder.stream(config.getSourceTopic(), Consumed.with(Serdes.String(), logRecordSerde))
                .filter((s, logRecord) -> {
                    if (logRecord != null
                            && logRecord.getKubernetes() != null
                            && logRecord.getKubernetes().getPodName() != null
                            && logRecord.getKubernetes().getPodName().contains("-kafka-")
                            && logRecord.getMessage() != null
                            && logRecord.getMessage().contains("kafka.authorizer.logger")
                            && logRecord.getMessage().contains("Denied Operation"))  {
                        return true;
                    }

                    return false;
                })
                .mapValues(logRecord -> {
                    StringWriter stringWriter = new StringWriter();

                    log.info("Found Kafka Authorization error to alert about ...");

                    try (PrintWriter writer = new PrintWriter(stringWriter)) {
                        writer.println("**Found Kafka Authorization error** in pod " + logRecord.getKubernetes().getPodName() + ":");
                        writer.println("\t" + logRecord.getMessage());

                        return stringWriter.toString();
                    }
                })
                .to(config.getTargetTopic(), Produced.with(Serdes.String(), Serdes.String()));

        new KafkaStreams(builder.build(), props).start();
    }
}
