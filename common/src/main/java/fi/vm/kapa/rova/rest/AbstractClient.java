package fi.vm.kapa.rova.rest;

import fi.vm.kapa.rova.logging.Logger;
import fi.vm.kapa.rova.logging.LoggingClientRequestFilter;
import fi.vm.kapa.rova.rest.exception.SystemException;
import org.apache.commons.lang3.Validate;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

import static fi.vm.kapa.rova.rest.exception.ExceptionType.*;
import static fi.vm.kapa.rova.rest.exception.ExceptionType.OTHER_EXCEPTION;
import static fi.vm.kapa.rova.rest.exception.SystemException.Key.DESCRIPTION;

/**
 * Created by Juha Korkalainen on 25.8.2016.
 */
public class AbstractClient {

    protected String endPointUrl;

    public AbstractClient(String endPointUrl) {
        this.endPointUrl = endPointUrl;
    }

    private static final Logger LOG = Logger.getLogger(AbstractClient.class);

    protected String getEnduser() {
        String enduser = null;
        if (SecurityContextHolder.getContext() != null && SecurityContextHolder.getContext().getAuthentication() != null) {
            enduser = SecurityContextHolder.getContext().getAuthentication().getName();
        }
        return enduser;
    }

    protected Client getClient() {
        ClientConfig clientConfig = new ClientConfig();
        Client client = ClientBuilder.newClient(clientConfig);
        client.register(new LoggingClientRequestFilter());
        client.register(JacksonFeature.class);
        return client;
    }

    protected Map<String, Object> queryParams(String... params) {
        Validate.noNullElements(params);
        Validate.isTrue(params.length % 2 == 0, "Params length not even, can't form key-value pairs");
        Map<String, Object> paramMap = new HashMap<>(params.length / 2);
        for (int i = 0; i < params.length; i = i + 2) {
            paramMap.put(params[i], params[i+1]);
        }
        return paramMap;
    }

    protected <T> T getGeneric(String url, Map<String, Object> params, GenericType<T> returnObject) {
        return getResponse(url, params).readEntity(returnObject);
    }

    protected <T> T getGeneric(String url, GenericType<T> returnObject) {
        return getResponse(url, null).readEntity(returnObject);
    }

    protected <T> T getPlain(String url, Map<String, Object> params, Class<T> clazz) {
        return getResponse(url, params).readEntity(clazz);
    }

    protected <T> T getPlain(String url, Class<T> clazz) {
        return getResponse(url, null).readEntity(clazz);
    }

    private Response getResponse(String url, Map<String, Object> params) {
        WebTarget webTarget = getClient().target(endPointUrl + url);
        if (params != null) {
            for (Map.Entry<String, Object> param : params.entrySet()) {
                webTarget = webTarget.queryParam(param.getKey(), param.getValue());
            }
        }
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
        Response response = invocationBuilder.get();
        if (response.getStatus() != 200) {
            handleError(response);
        }
        return response;
    }

    protected void handleError(Response response) {
        if (!response.hasEntity())  {
            throw new SystemException(OTHER_EXCEPTION).set(DESCRIPTION, SystemException.MSG_FAIL);
        }

        try {
            HashMap<String, Object> errorMap = response.readEntity(new GenericType<HashMap<String, Object>>() {});
            int errorCode = (Integer) errorMap.get("errorCode");

            switch (errorCode) {
                case 101:
                    throw new SystemException(MISSING_PARAMETER).set(DESCRIPTION, "puuttuva.parametri");

                case 102:
                    throw new SystemException(MATCHING_SERVICE_NOT_FOUND).set(DESCRIPTION, "palvelua.ei.loytynyt");

                case 103:
                    throw new SystemException(DUPLICATE_SERVICE_IDENTIFIER).set(DESCRIPTION, "samanniminen.palvelu.on.jo.kannassa");

                case 108:
                    throw new SystemException(ILLEGAL_RULE_CONFIG).set(DESCRIPTION, "viallinen.saantokonfiguraatio");

                case 109:
                    throw new SystemException(DUPLICATE_USER_IDENTITY).set(DESCRIPTION, "identiteetti.on.jo.lisatty");

                default:
                    throw new SystemException(OTHER_EXCEPTION).set(DESCRIPTION, SystemException.MSG_FAIL);
            }
        } catch (ClassCastException e) {
            LOG.error("Exception occured while handling error",e);
            throw new SystemException(OTHER_EXCEPTION).set(DESCRIPTION, SystemException.MSG_FAIL);
        } catch (ProcessingException e) {
            LOG.error("Exception occured while handling error",e);
            throw new SystemException(OTHER_EXCEPTION).set(DESCRIPTION, SystemException.MSG_FAIL);
        } catch (IllegalStateException e) {
            LOG.error("Exception occured while handling error",e);
            throw new SystemException(OTHER_EXCEPTION).set(DESCRIPTION, SystemException.MSG_FAIL);
        }  catch (NullPointerException e) {
            LOG.error("Exception occured while handling error",e);
            throw new SystemException(OTHER_EXCEPTION).set(DESCRIPTION, SystemException.MSG_FAIL);
        }
    }
}