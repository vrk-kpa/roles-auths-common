
package fi.vm.kapa.rova.ui;

public enum Channel {

    ADMIN_UI("ADMIN-UI"),
    WEB_API("WEB-API"),
    VARE_UI("VARE-UI");

    private String name;

    Channel(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}

