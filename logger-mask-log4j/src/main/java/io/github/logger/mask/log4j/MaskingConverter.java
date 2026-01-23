//package io.github.logger.mask.log4j;
//
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.apache.logging.log4j.core.LogEvent;
//import org.apache.logging.log4j.core.config.plugins.Plugin;
//import org.apache.logging.log4j.core.pattern.ConverterKeys;
//import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;
//import org.apache.logging.log4j.core.pattern.PatternConverter;
//import org.apache.logging.log4j.message.Message;
//
//import java.text.MessageFormat;
//
//@Plugin(name = "MaskingConverter", category = PatternConverter.CATEGORY)
//@ConverterKeys({"mask", "m"})
//public class MaskingConverter extends LogEventPatternConverter {
//
//    private static final Logger LOGGER = LogManager.getLogger(MaskingConverter.class);
//
//    protected MaskingConverter(String name, String style) {
//        super(name, style);
//    }
//
//    @Override
//    public void format(LogEvent event, StringBuilder toAppendTo) {
//        try {
//            Message message = event.getMessage();
//            if (message == null) {
//                toAppendTo.append("");
//                return;
//            }
//
//            String formattedMessage = message.getFormattedMessage();
//            Object[] parameters = message.getParameters();
//
//            if (parameters == null || parameters.length == 0) {
//                toAppendTo.append(formattedMessage);
//                return;
//            }
//
//            // 处理参数脱敏
//            Object[] maskedParameters = new Object[parameters.length];
//            for (int i = 0; i < parameters.length; i++) {
//                maskedParameters[i] = processParameter(parameters[i]);
//            }
//
//            // 重新格式化消息
//            String maskedMessage = MessageFormat.format(formattedMessage, maskedParameters);
//            toAppendTo.append(maskedMessage);
//
//        } catch (Exception e) {
//            LOGGER.error("Failed to mask log message", e);
//            toAppendTo.append(event.getMessage().getFormattedMessage());
//        }
//    }
//
//    private Object processParameter(Object param) {
//        if (param == null) {
//            return null;
//        }
//
//        // 处理JSON字符串
//        if (param instanceof String) {
//            String strParam = (String) param;
//            if (isJson(strParam)) {
//                try {
//                    JsonNode jsonNode = OBJECT_MAPPER.readTree(strParam);
//                    JsonNode maskedNode = maskJsonNode(jsonNode);
//                    return maskedNode.toString();
//                } catch (Exception e) {
//                    LOGGER.debug("Failed to parse JSON string", e);
//                    return strParam;
//                }
//            }
//        }
//
//        // 处理对象
//        if (hasMaskedFields(param)) {
//            return MaskAnnotationProcessor.processMasking(param);
//        }
//
//        // 处理集合
//        if (param instanceof Collection<?>) {
//            Collection<?> collection = (Collection<?>) param;
//            List<Object> maskedList = new ArrayList<>();
//            for (Object item : collection) {
//                maskedList.add(processParameter(item));
//            }
//            return maskedList;
//        }
//
//        // 处理数组
//        if (param.getClass().isArray()) {
//            int length = Array.getLength(param);
//            Object[] array = new Object[length];
//            for (int i = 0; i < length; i++) {
//                array[i] = processParameter(Array.get(param, i));
//            }
//            return array;
//        }
//
//        return param;
//    }
//
//    private boolean isJson(String str) {
//        str = str.trim();
//        return str.startsWith("{") && str.endsWith("}") || str.startsWith("[") && str.endsWith("]");
//    }
//
//    private JsonNode maskJsonNode(JsonNode node) {
//        if (node == null || node.isNull()) {
//            return node;
//        }
//
//        if (node.isObject()) {
//            ObjectNode objectNode = (ObjectNode) node;
//            Iterator<Map.Entry<String, JsonNode>> fields = objectNode.fields();
//
//            while (fields.hasNext()) {
//                Map.Entry<String, JsonNode> field = fields.next();
//                JsonNode fieldValue = field.getValue();
//
//                if (fieldValue.isTextual()) {
//                    // 这里可以根据字段名或值进行脱敏判断
//                    String fieldName = field.getKey();
//                    String value = fieldValue.asText();
//
//                    // 简单的字段名匹配脱敏
//                    if (fieldName.contains("phone") || fieldName.contains("mobile") ||
//                        fieldName.contains("email") || fieldName.contains("idCard")) {
//                        objectNode.put(field.getKey(), "***");
//                    }
//                } else if (fieldValue.isObject() || fieldValue.isArray()) {
//                    maskJsonNode(fieldValue);
//                }
//            }
//        } else if (node.isArray()) {
//            ArrayNode arrayNode = (ArrayNode) node;
//            for (int i = 0; i < arrayNode.size(); i++) {
//                maskJsonNode(arrayNode.get(i));
//            }
//        }
//
//        return node;
//    }
//
//    private boolean hasMaskedFields(Object obj) {
//        if (obj == null) {
//            return false;
//        }
//
//        Class<?> clazz = obj.getClass();
//        for (Field field : getAllFields(clazz)) {
//            if (field.isAnnotationPresent(Mask.class)) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    private List<Field> getAllFields(Class<?> clazz) {
//        List<Field> fields = new ArrayList<>();
//        while (clazz != null) {
//            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
//            clazz = clazz.getSuperclass();
//        }
//        return fields;
//    }
//
//    public static MaskingConverter newInstance(Configuration config, String[] options) {
//        return new MaskingConverter("mask", ThreadContext.get("style"));
//    }
//}