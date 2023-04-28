package in.succinct.bpp.core.adaptor;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class TimeSensitiveCache {
    Map<Class<?>,Entry> cache = new HashMap<>();
    Duration ttl ;
    public TimeSensitiveCache(Duration ttl){
        this.ttl = ttl;
    }

    public <T> T put(Class<T> key, T value) {
        Entry entry = new Entry();
        entry.expiry = System.currentTimeMillis() + ttl.toMillis();
        entry.value = value;
        Entry oldValue = cache.put(key,entry);
        return oldValue == null ? null : (T)oldValue.value;
    }

    public void clear(){
        cache.clear();
    }


    public <T> T get(Class<T> key, ValueProvider<T> valueProvider){
        T value;
        long now = System.currentTimeMillis();

        Entry entry = cache.get(key);
        if (entry == null || now > entry.expiry){
            value = valueProvider.getValue();
            put(key,value);
        }else {
            value = (T)entry.value;
        }

        return value;
    }

    public static class Entry {
        Object value;
        long   expiry;

    }
    public interface ValueProvider<V>  {
        V getValue();
    }
}
