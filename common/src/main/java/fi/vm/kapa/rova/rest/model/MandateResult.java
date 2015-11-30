package fi.vm.kapa.rova.rest.model;

public class MandateResult {
    boolean created;
    long mandateId;

    public boolean isCreated() {
        return created;
    }

    public void setCreated(boolean created) {
        this.created = created;
    }

    public long getMandateId() {
        return mandateId;
    }

    public void setMandateId(long mandateId) {
        this.mandateId = mandateId;
    }
}
