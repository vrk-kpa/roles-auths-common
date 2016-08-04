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

import static org.junit.Assert.*;

import org.junit.Test;

public class HetuUtilsTest {

    // isHetuValid tests (will test also isHetuFormatValid(), 
    // isBirthDateValid() and isChecksumCharacterValid() methods too)

    @Test
    public void hetuValidPassed1800Test() {
        String testHetu = "130192+614R"; // valid hetu with + separator

        assertTrue(HetuUtils.isHetuValid(testHetu));
    }

    @Test
    public void hetuValidPassed1900Test() {
        String testHetu = "060887-298C"; // valid hetu with - separator

        assertTrue(HetuUtils.isHetuValid(testHetu));
    }

    @Test
    public void hetuValidPassed2000Test() {
        String testHetu = "180242A273V"; // valid hetu with A separator

        assertTrue(HetuUtils.isHetuValid(testHetu));
    }

    @Test
    public void hetuValidFailed2000Test() {
        String testHetu = "180242a273V"; // invalid hetu with a separator

        assertFalse(HetuUtils.isHetuValid(testHetu));
    }

    /* not valid yet
    @Test
    public void hetuValidPassed2100Test() {
        String testHetu = "280609B1236"; // valid hetu with B separator

        assertTrue(HetuUtils.isHetuValid(testHetu));
    }
    */

    @Test
    public void hetuValidPassedLeapDayTest1() {
        String testHetu = "290216A123D"; // valid hetu with leap day of 2016

        assertTrue(HetuUtils.isHetuValid(testHetu));
    }

    @Test
    public void hetuValidPassedLeapDayTest2() {
        String testHetu = "290200A1239"; // valid hetu with leap day of 2000 (most complex leap day check)

        assertTrue(HetuUtils.isHetuValid(testHetu));
    }

    @Test
    public void hetuValidFailedLeapDayTest1() {
        String testHetu = "290200-123D"; // invalid hetu with leap day of 1900 (wasn't leap year)

        assertFalse(HetuUtils.isHetuValid(testHetu));
    }

    @Test
    public void hetuValidFailedLeapDayTest2() {
        String testHetu = "300216A1232"; // invalid hetu with day exceeding leap day of 2016
//        System.out.println(HetuUtils.getChecksumCharacter(testHetu.substring(0, testHetu.length()-1)));
        assertFalse(HetuUtils.isHetuValid(testHetu));
    }

    @Test
    public void hetuValidFailedDateFormatTest1() {
        String testHetu = "06088-298C"; // invalid date format (too few chars in date)

        assertFalse(HetuUtils.isHetuValid(testHetu));
    }

    @Test
    public void hetuValidFailedDateFormatTest2() {
        String testHetu = "0608877-298C"; // invalid date format (too many chars in date)
        
        assertFalse(HetuUtils.isHetuValid(testHetu));
    }

    @Test
    public void hetuValidFailedDateFormatTest3() {
        String testHetu = "320187-298H"; // invalid date format (day exceeds month length)

        assertFalse(HetuUtils.isHetuValid(testHetu));
    }

    @Test
    public void hetuValidFailedSeparatorTest() {
        String testHetu = "060887X298C"; // unknown separator (X)
        
        assertFalse(HetuUtils.isHetuValid(testHetu));
    }

    @Test
    public void hetuValidFailedChecksumTest() {
        String testHetu = "060887-298D"; // invalid checksum character (D)
        
        assertFalse(HetuUtils.isHetuValid(testHetu));
    }

    @Test
    public void hetuValidFailedSpacesTest() {
        String testHetu = " 060887-298C "; // valid hetu but extra spaces
        
        assertFalse(HetuUtils.isHetuValid(testHetu));
    }

    @Test
    public void hetuValidFailedCharacterTest() {
        String testHetu = " 06O887-298C "; // invalid hetu with uppercase O instead of digit 0 
        
        assertFalse(HetuUtils.isHetuValid(testHetu));
    }

    @Test
    public void hetuValidFailedCaseTest() {
        String testHetu = " 060887-298c "; // invalid hetu wrong case c
        
        assertFalse(HetuUtils.isHetuValid(testHetu));
    }


    // isMaleHetu & isFemaleHetu tests

    @Test
    public void maleHetuPassedTest() {
        String testHetu = "060887-299D"; // valid male hetu
        
        assertTrue(HetuUtils.isMaleHetu(testHetu));
    }

    @Test
    public void maleHetuFailedTest() {
        String testHetu = "060887-298C"; // valid female hetu
        
        assertFalse(HetuUtils.isMaleHetu(testHetu));
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void maleHetuExceptionTest() {
        String testHetu = "060887-299C"; // invalid male hetu
        
        HetuUtils.isMaleHetu(testHetu);
    }

    @Test
    public void femaleHetuPassedTest() {
        String testHetu = "060887-298C"; // valid female hetu
        
        assertTrue(HetuUtils.isFemaleHetu(testHetu));
    }

    @Test
    public void femaleHetuFailedTest() {
        String testHetu = "060887-299D"; // valid male hetu
        
        assertFalse(HetuUtils.isFemaleHetu(testHetu));
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void femaleHetuExceptionTest() {
        String testHetu = "060887-298B"; // invalid female hetu
        
        HetuUtils.isFemaleHetu(testHetu);
    }


    // maskHetu tests

    @Test
    public void maskHetuPassedTest() {
        String testHetu = "060887-299D"; // valid male hetu
        
        assertEquals("060887*****", HetuUtils.maskHetu(testHetu));
    }

    @Test
    public void maskHetuFullPassedTest() {
        String testHetu = "060887-299D"; // valid male hetu
        
        assertEquals("***********", HetuUtils.maskHetuFull(testHetu));
    }

    @Test
    public void maskHetuNullTest() {
        assertNull(HetuUtils.maskHetu(null));
    }

    @Test
    public void maskHetuInvalidPassedTest1() {
        String testHetu = "060887-299"; // invalid male hetu (cheksum missing)
        
        assertEquals("060887****", HetuUtils.maskHetu(testHetu));
    }

    @Test
    public void maskHetuInvalidPassedTest2() {
        String testHetu = "06088-299D"; // invalid male hetu (too short date)
        
        assertEquals("06088-****", HetuUtils.maskHetu(testHetu));
    }
}

