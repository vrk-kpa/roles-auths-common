package fi.vm.kapa.rova.utils;

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
	
}
