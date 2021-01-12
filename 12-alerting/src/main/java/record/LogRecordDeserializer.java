package record;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class LogRecordDeserializer implements Deserializer<LogRecord> {
    private static final Logger LOG = LogManager.getLogger(LogRecordDeserializer.class);

    @Override
    public void configure(Map map, boolean b) {
        // Nothing to do
    }

    @Override
    public LogRecord deserialize(String s, byte[] bytes) {
        ObjectMapper mapper = new ObjectMapper();
        LogRecord obj = null;

        try {
            obj = mapper.readValue(bytes, LogRecord.class);
        } catch (Exception e) {
            LOG.error("Failed to deserialize LogRecord", e);
        }

        return obj;
    }

    @Override
    public void close() {
        // Nothing to close
    }
}
