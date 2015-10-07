package fi.vm.kapa.rova.jpa.model;

import javax.persistence.*;
import java.util.Date;

@MappedSuperclass
public abstract class ModelBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "created")
    private Date created = new Date();

    @Column(name = "modified")
    private Date modified = new Date();

    @Column(name = "modified_by")
    private String modifiedBy;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    @Override
    public String toString() {
        return "ModelBase [id=" + id + ", created=" + created + ", modified="
                + modified + ", modifiedBy=" + modifiedBy + "]";
    }

}
