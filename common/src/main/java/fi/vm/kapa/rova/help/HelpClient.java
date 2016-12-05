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
package fi.vm.kapa.rova.help;

import fi.vm.kapa.rova.logging.Logger;
import fi.vm.kapa.rova.rest.AbstractClient;
import fi.vm.kapa.rova.ui.Channel;

import javax.ws.rs.core.GenericType;
import java.util.List;

import static java.text.MessageFormat.format;

public class HelpClient extends AbstractClient {

    private static final Logger LOG = Logger.getLogger(HelpClient.class);

    public HelpClient(String endPointUrl) {
        super(endPointUrl);
    }

    public String getHelp(String lang, Channel channel, String docName) {
        String resource = format("/rest/help/{0}/{1}/{2}", lang, channel, docName);
        LOG.debug("Loading help resource: " + resource);
        return getGeneric(resource, new GenericType<String>() {});
    }

    @Override
    public String toString() {
        return "EngineDataProvider engine url: " + endPointUrl;
    }

}

