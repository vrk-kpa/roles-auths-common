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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public final class HetuUtils {
    private static final String CHECKSUM_CHARACTERS = "0123456789ABCDEFHJKLMNPRSTUVWXY";
    private static volatile Map<Integer, Character> separators = new HashMap<Integer, Character>();
    private static volatile Map<Character, Integer> invertedSeparators = new HashMap<Character, Integer>();
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");


    static {
        separators.put(18, '+');
        separators.put(19, '-');
        separators.put(20, 'A');
//        separators.put(21, 'B');

        invertedSeparators.put('+', 18);
        invertedSeparators.put('-', 19);
        invertedSeparators.put('A', 20);
//        invertedSeparators.put('B', 21);

        DATE_FORMAT.setLenient(false);
    }

    private HetuUtils() {

    }


    /**
     * Checks if a given Finnish National Identification Number is valid.
     *
     * @param hetu a Finnish National Identification Number to be validated, such as "040789-5863"
     * @return true if the given hetu is totally formally valid
     */
    public static boolean isHetuValid(final String hetu) {
        if (StringUtils.isBlank(hetu)) {
            return false;
        }

        final String localHetu = hetu;

        return isHetuFormatValid(localHetu) && isBirthDateValid(localHetu) && isChecksumCharacterValid(localHetu);
    }

    /**
     * Checks if the format of a given Finnish National Identification Number is correct.
     * Number of characters and their types are validated against a regular expression.
     *
     * @param hetu a Finnish National Identification Number to be validated, such as "040789-5863"
     * @return true if the given hetu has correct format
     */
    public static boolean isHetuFormatValid(final String hetu) {
        return hetu.matches("\\d{6}[+-AB]\\d{3}[0123456789ABCDEFHJKLMNPRSTUVWXY]");
    }

    /**
     * Checks if the birthday part of a given Finnish National Identification Number is valid.
     * A java.text.SimpleDateFormat object is used in strict mode (lenient == false) for interpreting
     * the birthday part of the given hetu.
     *
     * @param hetu a Finnish National Identification Number to be validated, such as "040789-5863"
     * @return true if the given hetu has a valid birthday part
     */
    public static boolean isBirthDateValid(final String hetu)  {
        try {
            final String fullLengthBirthDate = String.format("%d%s-%s-%s",
                    invertedSeparators.get(hetu.charAt(6)),
                    StringUtils.substring(hetu, 4, 6),
                    StringUtils.substring(hetu, 2, 4),
                    StringUtils.substring(hetu, 0, 2));
            DATE_FORMAT.parse(fullLengthBirthDate);
            return true;
        } catch (final ParseException ignored) {
            return false;
        }
    }

    /**
     * Checks if the birthday part of a given Finnish National Identification Number is valid.
     *
     * @param hetu a Finnish National Identification Number to be validated, such as "040789-5863"
     * @return true if the given hetu has a valid birthday part
     */
    public static boolean isChecksumCharacterValid(final String hetu) {
        return getChecksumCharacter(hetu) == hetu.charAt(10);
    }

    /**
     * Checks if given Finnish National Identification Number is for male.
     *
     * @param hetu a Finnish National Identification Number to be checked, such as "040789-5863"
     * @return true if the given hetu formally belongs to a male citizen.
     */
    public static boolean isMaleHetu(final String hetu) {
        if (!isHetuValid(hetu)) {
            throw new IllegalArgumentException(String.format("Hetu [%s] is not valid", hetu));
        }

        return Integer.parseInt(StringUtils.substring(hetu, 9, 10)) % 2 != 0;
    }

    /**
     * Checks if given Finnish National Identification Number is for female.
     *
     * @param hetu a Finnish National Identification Number to be checked, such as "040789-5863"
     * @return true if the given hetu formally belongs to a female citizen.
     */
    public static boolean isFemaleHetu(final String hetu) {
        return !isMaleHetu(hetu);
    }

    /**
     * Masks the separator of a given hetu and characters thereafter with asterisks (*).
     *
     * @param hetu a Finnish National Identification Number to be masked, such as "040789-5863"
     * @return a masked hetu String or null if null was given
     */
    public static String maskHetu(final String hetu) {
        if (hetu == null)
            return null;

        if (hetu.length() > 6) {
            return maskHetuInternal(hetu, false);
        }

        return hetu;
    }

    /**
     * Masks the entire hetu with asterisks (*).
     *
     * @param hetu a Finnish National Identification Number to be masked, such as "040789-5863"
     * @return a masked hetu String or null if null was given
     */
    public static String maskHetuFull(final String hetu) {
        if (hetu == null)
            return null;

        return maskHetuInternal(hetu, true);
    }

    static Character getChecksumCharacter(final String partialHetu) {
        final long checkNumber = Long.parseLong(String.format("%s%s", StringUtils.substring(partialHetu, 0, 6),
                StringUtils.substring(partialHetu, 7, 10)));
        return CHECKSUM_CHARACTERS.charAt((int)(checkNumber % CHECKSUM_CHARACTERS.length()));
    }

    private static String maskHetuInternal(final String hetu, final boolean maskAllChars) {
        final StringBuilder masked = new StringBuilder(maskAllChars ? "" : hetu.substring(0,6));
        for (int i = (maskAllChars ? 0 : 6); i < hetu.length(); i++) {
            masked.append('*');
        }
        return masked.toString();
    }
}
