package fi.vm.kapa.rova.localization;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * Created by Juha Korkalainen on 23.8.2016.
 */
public class Localization {

    private String key;
    private String lang;
    private String value;

    public Localization() {}


    /**
     * String representing localization csv file line
     * @param csvSourceLine
     */
    public Localization(String csvSourceLine) {
        String[] parts = csvSourceLine.split(";");
        this.key = parts[0];
        this.lang = parts[1];
        this.value = StringUtils.join(Arrays.copyOfRange(parts, 2, parts.length), ";");
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
