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

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

public class DateUtilsTest {
    
    @Test
    public void testIsCurrentDay_beginningOfSameDay() {
        Date today = new Date();
        today.setHours(0);
        today.setMinutes(0);
        today.setSeconds(0);
        
        assertTrue(DateUtils.isCurrentDay(today));
    }
    
    @Test
    public void testIsCurrentDay_endOfSameDay() {
        Date today = new Date();
        today.setHours(23);
        today.setMinutes(59);
        today.setSeconds(59);
        
        assertTrue(DateUtils.isCurrentDay(today));
    }
    
    @Test
    public void testIsCurrentDay_previousDay() {
        Date yesterday = new Date();
        yesterday.setDate(yesterday.getDate() - 1);
        yesterday.setHours(23);
        yesterday.setMinutes(59);
        yesterday.setSeconds(59);
        
        assertFalse(DateUtils.isCurrentDay(yesterday));
    }
    
    @Test
    public void testIsCurrentDay_previousDaySameTime() {
        Date yesterday = new Date();
        yesterday.setDate(yesterday.getDate() - 1);
        
        assertFalse(DateUtils.isCurrentDay(yesterday));
    }
    
    @Test
    public void testIsCurrentDay_previousYear() {
        Date lastYear = new Date();
        lastYear.setYear(lastYear.getYear() - 1);
        
        assertFalse(DateUtils.isCurrentDay(lastYear));
    }
    
    @Test
    public void testIsCurrentDay_nextDay() {
        Date tomorrow = new Date();
        tomorrow.setDate(tomorrow.getDate() + 1);
        tomorrow.setHours(00);
        tomorrow.setMinutes(00);
        tomorrow.setSeconds(00);
        
        assertFalse(DateUtils.isCurrentDay(tomorrow));
    }
    
    @Test
    public void testIsCurrentDay_nextDaySameTime() {
        Date tomorrow = new Date();
        tomorrow.setDate(tomorrow.getDate() + 1);
        
        assertFalse(DateUtils.isCurrentDay(tomorrow));
    }
    
    @Test
    public void testIsCurrentDay_nextYear() {
        Date nextYear = new Date();
        nextYear.setYear(nextYear.getYear() + 1);
        
        assertFalse(DateUtils.isCurrentDay(nextYear));
    }

}
