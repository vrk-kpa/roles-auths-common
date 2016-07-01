package fi.vm.kapa.rova.notification;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import fi.vm.kapa.rova.logging.Logger;
import fi.vm.kapa.rova.ui.Channel;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public abstract class NotificationCache<N> {
    private static final Logger LOG = Logger.getLogger(NotificationCache.class);

    private final LoadingCache<Channel, List<N>> cache;

    public NotificationCache(long cacheExpirationInMinutes) {
        CacheLoader<Channel, List<N>> loader = new CacheLoader<Channel, List<N>>() {
            @Override
            public List<N> load(Channel channel) throws Exception {
                LOG.info("Refreshed notifications for channel: <" + channel.getName() + ">");
                return loadNotifications(channel);
            }
        };
        cache = CacheBuilder.newBuilder().expireAfterWrite(cacheExpirationInMinutes, TimeUnit.MINUTES).build(loader);
    }

    protected abstract List<N> loadNotifications(Channel channel);

    public List<N> getNotificationForChannel(Channel channel) throws ExecutionException {
        return cache.get(channel);
    }

    public void invalidate(Channel channel) {
        cache.invalidate(channel);
    }

    public void invalidateAll() {
        cache.invalidateAll();
    }

}
