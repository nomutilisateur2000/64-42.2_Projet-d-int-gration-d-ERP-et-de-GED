package ch.hearc.ig.apiService.deserializer;

import ch.hearc.ig.apiService.annotation.QbField;
import ch.hearc.ig.business.Receipt;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ReceiptDeserializer extends JsonDeserializer<Receipt> {

    @Override
    public Receipt deserialize(JsonParser jsonParser, DeserializationContext ctxt)
            throws IOException, JacksonException {

        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        Receipt receipt = new Receipt();
        receipt.setId(node.get("ObjectID").asInt());

        for (JsonNode field : node.get("Fields")) {
            String code = field.get("Code").asText();
            String value = field.get("Value").asText();

            if (value.isEmpty()) continue;

            invokeSetter(receipt, code, value);
        }

        return receipt;
    }

    private void invokeSetter(Receipt receipt, String code, String value) {
        try {
            for (Method method : Receipt.class.getDeclaredMethods()) {
                QbField annotation = method.getAnnotation(QbField.class);
                if (annotation != null && annotation.value().equals(code)) {
                    Class<?> type = method.getParameterTypes()[0];

                    if (type == String.class) {
                        method.invoke(receipt, value);
                    } else if (type == Double.class) {
                        method.invoke(receipt, Double.parseDouble(value));
                    } else if (type == Date.class) {
                        method.invoke(receipt, new SimpleDateFormat("dd.MM.yyyy").parse(value));
                    }
                    return;
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur sur " + code + " : " + e.getMessage());
        }
    }
}
