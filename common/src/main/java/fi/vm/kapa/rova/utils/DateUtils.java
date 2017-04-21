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
package fi.vm.kapa.rova.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtils {
    public static final TimeZone FINNISH_TIMEZONE = TimeZone.getTimeZone("Europe/Helsinki");

    private DateUtils() {

    }

    public static Date convertStartTime(Date date) {
        Calendar startCal = Calendar.getInstance();
        startCal.setTimeZone(FINNISH_TIMEZONE);
        startCal.setTime(date);
        startCal.set(Calendar.HOUR_OF_DAY, 0);
        startCal.set(Calendar.MINUTE, 0);
        startCal.set(Calendar.SECOND, 0);
        startCal.set(Calendar.MILLISECOND, 0);
        return startCal.getTime();
    }

    public static Date convertEndTime(Date date) {
        Calendar endCal = Calendar.getInstance();
        endCal.setTimeZone(FINNISH_TIMEZONE);
        endCal.setTime(date);
        endCal.set(Calendar.HOUR_OF_DAY, 23);
        endCal.set(Calendar.MINUTE, 59);
        endCal.set(Calendar.SECOND, 59);
        endCal.set(Calendar.MILLISECOND, 0);
        return endCal.getTime();
    }

    public static String format(Date date) {
        return date != null ? new SimpleDateFormat("yyyy-MM-dd HH.mm.ss").format(date) : null;
    }

    public static Date getCurrentFinnishDate() {
        Calendar currentMoment = Calendar.getInstance();
        currentMoment.setTimeZone(FINNISH_TIMEZONE);
        return currentMoment.getTime();
    }
}
