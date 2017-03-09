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
package fi.vm.kapa.rova.spring.profiles;

public final class SpringProfiles {
    public static final String PROD = "default";
    public static final String TEST = "test";
    public static final String ITEST = "itest";
    public static final String DEV = "dev";
    public static final String QA = "qa";
    public static final String JWT = "jwt";
    // public static final String PROD = "prod"; // Tällainenkin oli käytössä, korvataan defaultilla


    // negations
    public static final String NOT_PROD = "!default";
    public static final String NOT_TEST = "!test";
    public static final String NOT_ITEST = "!itest";
    public static final String NOT_DEV = "!dev";
    public static final String NOT_QA = "!qa";
    public static final String NOT_JWT = "!jwt";
    // public static final String NOT PROD = "!prod";

    private SpringProfiles() {
    }
}
