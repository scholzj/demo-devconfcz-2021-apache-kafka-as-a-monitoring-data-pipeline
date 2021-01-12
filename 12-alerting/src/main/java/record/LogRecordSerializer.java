package record;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class LogRecordSerializer implements Serializer<LogRecord> {
    private static final Logger LOG = LogManager.getLogger(LogRecordSerializer.class);

    @Override
    public void configure(Map map, boolean b) {
        // Nothing to configure
    }

    @Override
    public byte[] serialize(String s, LogRecord o) {
        byte[] retVal = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            retVal = objectMapper.writeValueAsBytes(o);
        } catch (Exception e) {
            LOG.error("Failed to serialize LogRecord", e);
        }
        return retVal;
    }

    @Override
    public void close() {
        // Nothing to do
    }
}
