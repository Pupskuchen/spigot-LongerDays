package net.pupskuchen.pluginconfig.serialization;

import java.util.Map;

public abstract interface Serializer {
    public static interface MapSerialize<T> {
        public Map<String, Object> serialize(final T unserialized);
    }

    public static interface MapSerializer<T> extends MapSerialize<T> {
        public T deserialize(final Map<String, Object> serialized);
    }

    public static interface MapSerializerWithType<T> extends MapSerialize<T> {
        public T deserialize(final Map<String, Object> serialized, final Class<T> type);
    }
}
