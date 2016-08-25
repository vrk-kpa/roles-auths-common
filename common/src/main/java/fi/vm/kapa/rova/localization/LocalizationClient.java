package fi.vm.kapa.rova.localization;

import fi.vm.kapa.rova.rest.AbstractClient;
import org.springframework.stereotype.Component;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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

    public List<Localization> getLocalization(String lang, String key) {
        return getGeneric(format("/rest/localization/{0}/{1}", lang, key), new GenericType<List<Localization>>() {});
    }


    @Override
    public String toString() {
        return "EngineDataProvider engine url: " + endPointUrl;
    }

}

