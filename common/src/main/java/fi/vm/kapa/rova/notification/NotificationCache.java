/**
 * The MIT License
 * Copyright (c) 2016 Population Register Centre
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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
