package com.testchallenge.server;

import java.io.ObjectInputFilter;

final class SerializationFilters {

    private static final long MAX_ARRAY_LENGTH = 5L * 1024L * 1024L;
    private static final long MAX_DEPTH = 20L;
    private static final long MAX_REFERENCES = 20_000L;
    private static final long MAX_STREAM_BYTES = 10L * 1024L * 1024L;

    private SerializationFilters() {
    }

    static ObjectInputFilter messagesOnly() {
        return info -> {
            if (info.depth() > MAX_DEPTH
                    || info.references() > MAX_REFERENCES
                    || info.streamBytes() > MAX_STREAM_BYTES
                    || info.arrayLength() > MAX_ARRAY_LENGTH) {
                return ObjectInputFilter.Status.REJECTED;
            }

            Class<?> serialClass = info.serialClass();
            if (serialClass == null) {
                return ObjectInputFilter.Status.UNDECIDED;
            }

            boolean arrayClass = serialClass.isArray();
            while (serialClass.isArray()) {
                serialClass = serialClass.getComponentType();
            }

            if (serialClass.isPrimitive()) {
                return ObjectInputFilter.Status.ALLOWED;
            }

            if (arrayClass && serialClass == Object.class) {
                return ObjectInputFilter.Status.ALLOWED;
            }

            if (serialClass == Enum.class) {
                return ObjectInputFilter.Status.ALLOWED;
            }

            if (serialClass == Number.class) {
                return ObjectInputFilter.Status.ALLOWED;
            }

            String className = serialClass.getName();
            if (className.startsWith("com.testchallenge.model.")
                    || className.equals("java.lang.String")
                    || className.equals("java.lang.Integer")
                    || className.equals("java.lang.Long")
                    || className.equals("java.lang.Boolean")
                    || className.equals("java.util.ArrayList")
                    || className.equals("java.util.Arrays$ArrayList")
                    || className.equals("java.util.Date")
                    || className.equals("java.util.HashMap")
                    || className.equals("java.util.Map$Entry")) {
                return ObjectInputFilter.Status.ALLOWED;
            }

            return ObjectInputFilter.Status.REJECTED;
        };
    }
}
