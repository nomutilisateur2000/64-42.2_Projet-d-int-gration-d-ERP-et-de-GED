package ch.hearc.ig.apiService.deserializer;

import ch.hearc.ig.business.AttachementFile;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public class FileDeserializer extends JsonDeserializer<AttachementFile> {

    @Override
    public AttachementFile deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException, JacksonException {

        JsonNode root = p.getCodec().readTree(p);

        AttachementFile attachementFile = new AttachementFile();

        JsonNode fileNameNode = root.get("FileName");
        if (fileNameNode != null && !fileNameNode.isNull()) {
            attachementFile.setName(fileNameNode.asText());
        }

        JsonNode fileNode = root.get("File");
        if (fileNode != null && !fileNode.isNull()) {
            attachementFile.setFile(fileNode.asText());
        }

        return attachementFile;
    }
}