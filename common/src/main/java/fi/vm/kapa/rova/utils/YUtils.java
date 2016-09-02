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

import org.apache.commons.lang3.StringUtils;

public final class YUtils {
    private static int[] factors = {7, 9, 10, 5, 8, 4, 2}; // factors for checksum calculation

    private YUtils() {
    }

    /**
     * Checks if a given Finnish Business ID is valid.
     *
     * @param ytunnus a Finnish Business ID to be validated, such as "0737546-2"
     * @return
     */
    public static boolean isYTunnusValid(final String ytunnus) {
        if (StringUtils.isBlank(ytunnus)) {
            return false;
        }

        return isYFormatValid(ytunnus) && isChecksumCharacterValid(ytunnus);
    }

    public static boolean isYFormatValid(final String ytunnus) {
        return ytunnus.matches("\\d{6,7}[-]\\d{1}");
    }

    public static boolean isChecksumCharacterValid(final String ytunnus) {
        int nbrCount = ytunnus.length() - 2;
        int factorSum = 0;
        for (int i = 0; i < nbrCount; i++) {
            factorSum += factors[i] * Character.getNumericValue(ytunnus.charAt(i));
        }
        
        int modulus = factorSum % 11;
        if (modulus == 1) {
            return false;
        } else if (modulus == 0){
            return 0 == Character.getNumericValue(ytunnus.charAt(ytunnus.length() - 1));
        } else {
            return (11 - modulus) == Character.getNumericValue(ytunnus.charAt(ytunnus.length() - 1));
        }
    }

    /* roles-auths-common/common/target/classes$ java fi.vm.kapa.rova.utils.YUtils */
    @SuppressWarnings("UseOfSystemOutOrSystemErr")
    public static void main(String[] args) {
        String testYtunnus = "1572860-0"; // ok
        String testYtunnus2 = "0737546-2"; // ok
        String testYtunnus3 = "073754-3"; // ok, 6 digits
        String testYtunnus4 = "0737546-3"; // nok, wrong checksum
        String testYtunnus5 = "07375468-3"; // nok, wrong format
        System.out.println(testYtunnus +" is "+ (isYTunnusValid(testYtunnus) ? "valid" : "not valid") );
        System.out.println(testYtunnus4 +" is "+ (isYTunnusValid(testYtunnus4) ? "valid" : "not valid") );
    }
}
