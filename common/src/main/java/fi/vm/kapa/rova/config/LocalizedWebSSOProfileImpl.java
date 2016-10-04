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
package fi.vm.kapa.rova.config;

import org.opensaml.common.SAMLException;
import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml2.common.Extensions;
import org.opensaml.saml2.common.impl.ExtensionsBuilder;
import org.opensaml.saml2.core.AuthnRequest;
import org.opensaml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml2.metadata.SingleSignOnService;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.opensaml.xml.schema.XSAny;
import org.opensaml.xml.schema.impl.XSAnyBuilder;
import org.springframework.security.saml.context.SAMLMessageContext;
import org.springframework.security.saml.websso.WebSSOProfileImpl;
import org.springframework.security.saml.websso.WebSSOProfileOptions;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * Sends authentication request possibly with extension that selects language for identification process.
 * The language that is used when the user is first identified for the session is also used in single logout process.
 * Created by mtom on 6/27/16.
 */
public class LocalizedWebSSOProfileImpl extends WebSSOProfileImpl {

    /**
     * {@value}
     */
    private static final String PARAM_LANG = "lang";
    private static final String NS_VETUMA = "urn:vetuma:SAML:2.0:extensions";
    private static final String EL_VETUMA = "vetuma";
    private static final String EL_LG = "LG";
    private static final List<String> supportedLanguages = Arrays.asList("fi", "sv", "en");

    @Override
    protected AuthnRequest getAuthnRequest(SAMLMessageContext context, WebSSOProfileOptions options, AssertionConsumerService assertionConsumer,
                                           SingleSignOnService bindingService) throws SAMLException, MetadataProviderException {
        AuthnRequest authnRequest = super.getAuthnRequest(context, options, assertionConsumer, bindingService);
        String lang = getSelectedLanguage();
        if (lang != null) {
            authnRequest.setExtensions(buildLanguageExtension(lang));
        }
        return authnRequest;
    }

    private String getSelectedLanguage() {
        String lang = null;
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String param = request.getParameter(PARAM_LANG);
            if (param != null && supportedLanguages.contains(param)) {
                lang = param;
            }
        }
        return lang;
    }

    private Extensions buildLanguageExtension(String lang) {
        Extensions extensions = new ExtensionsBuilder().buildObject(
                SAMLConstants.SAML20P_NS, Extensions.LOCAL_NAME, SAMLConstants.SAML20P_PREFIX);
        XSAnyBuilder builder = new XSAnyBuilder();
        XSAny vetuma = builder.buildObject(NS_VETUMA, EL_VETUMA, null);
        XSAny lg = builder.buildObject(NS_VETUMA, EL_LG, null);
        lg.setTextContent(lang);
        vetuma.getUnknownXMLObjects().add(lg);
        extensions.getUnknownXMLObjects().add(vetuma);
        return extensions;
    }

}
