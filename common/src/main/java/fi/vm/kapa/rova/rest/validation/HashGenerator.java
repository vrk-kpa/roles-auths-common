package fi.vm.kapa.rova.rest.validation;

import java.io.IOException;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class HashGenerator {

    private final static String HMAC_ALGORITHM = "HmacSHA256";

    /**
     * Generates hash Using HmacSHA256 algorithm. Encodes hash in Base64 coding.
     * 
     * @param data
     *            data to be hashed
     * @param key
     *            the key to be used in hashing
     * @return hash in Base64
     * @throws IOException
     *             thrown if key or data is incorrect
     */
    public static String hash(String data, String key) throws IOException {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), HMAC_ALGORITHM);
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(data.getBytes());
            String result = new String(Base64.getEncoder().encode(rawHmac));
            return result;
        } catch (Exception e) {

        }
        throw new IOException("Cannot create hash");
    }

}
