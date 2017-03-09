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
package fi.vm.kapa.rova.localization;

import fi.vm.kapa.rova.rest.AbstractClient;

import javax.ws.rs.core.GenericType;
import java.util.List;

import static java.text.MessageFormat.format;

/**
 * Created by Juha Korkalainen on 25.8.2016.
 */

public class LocalizationClient extends AbstractClient {

    public LocalizationClient(String endPointUrl) {
        super(endPointUrl);
    }

    public List<Localization> getAllLocalizations(String lang) {
        return getGeneric(format("/rest/localization/all/{0}", lang), new GenericType<List<Localization>>() {});
    }

    public String getLocalization(String lang, String key) {
        return getGeneric(format("/rest/localization/{0}/{1}", lang, key), new GenericType<String>() {});
    }


    public String getLocalizationCustomized(String lang, String key, String[] customs) {
        StringBuilder cSb = new StringBuilder();
        if (customs != null) {
            for (String s : customs) {
                cSb.append(s);
                cSb.append("/");
            }
        }
        return getGeneric(format("/rest/localization/{0}/{1}/{2}", lang, key, cSb.toString()), new GenericType<String>() {});
    }

    @Override
    public String toString() {
        return "EngineDataProvider engine url: " + endPointUrl;
    }

}

