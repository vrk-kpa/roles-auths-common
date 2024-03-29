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
package fi.vm.kapa.rova.rest.exception;

public enum ExceptionType implements ErrorCode {

    MISSING_PARAMETER(101),
    MATCHING_SERVICE_NOT_FOUND(102),
    DUPLICATE_SERVICE_IDENTIFIER(103),
    USER_UNKNOWN(104),
    DUPLICATE_RULESET_TYPE(105),
    NOT_AUTHORIZED(106),
    MATCHING_RULESET_NOT_FOUND(107),
    ILLEGAL_RULE_CONFIG(108),
    DUPLICATE_USER_IDENTITY(109),
    INVALID_WEB_API_REDIRECT_URL(110),
    SAVING_KATSO_ENTITYID_NOT_ALLOWED(111),
    OTHER_EXCEPTION(199);

    int number;

    ExceptionType(int number) {
        this.number = number;
    }

    @Override
    public int getCodeNumber() {
        return number;
    }
}
