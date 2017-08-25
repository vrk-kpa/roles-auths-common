package fi.vm.kapa.rova.ontology;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

/**
 * Describes a set of Katso roles that together (using logical AND condition) PROVIDE a mandate that is described by the
 * associated ontology Concept.
 *
 * Note that the Katso roles are not a requirement for the existence of the mandate, as the mandate can be acquired or allocated
 * also by other means than Katso roles. That is why it is called a provision, not a requirement.
 */
public class KatsoRolesMandateProvision {

    private final Set<String> katsoRoles;

    @JsonCreator
    public KatsoRolesMandateProvision(@JsonProperty("katsoRoles") Set<String> roles) {
        katsoRoles = roles;
    }

    public Set<String> getKatsoRoles() { return katsoRoles; }
}
