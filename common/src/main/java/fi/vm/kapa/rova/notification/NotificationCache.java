package fi.vm.kapa.rova.notification;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import fi.vm.kapa.rova.logging.Logger;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public abstract class NotificationCache<N> {
    private static final Logger LOG = Logger.getLogger(NotificationCache.class);

    private final LoadingCache<String, List<N>> cache;

    public NotificationCache(long cacheExpirationInMinutes) {
        CacheLoader<String, List<N>> loader = new CacheLoader<String, List<N>>() {
            @Override
            public List<N> load(String channel) throws Exception {
                LOG.info("Refreshed notifications for channel: <" + channel + ">");
                return loadNotifications(channel);
            }
        };
        cache = CacheBuilder.newBuilder().expireAfterWrite(cacheExpirationInMinutes, TimeUnit.MINUTES).build(loader);
    }

    protected abstract List<N> loadNotifications(String channel);

    public List<N> getNotificationForChannel(String channel) throws ExecutionException {
        return cache.get(channel);
    }

    public void invalidate(String channel) {
        cache.invalidate(channel);
    }

    public void invalidateAll() {
        cache.invalidateAll();
    }

}
