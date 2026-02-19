package ca.bigmwaj.emapp.as.entity.platform;

import ca.bigmwaj.emapp.as.entity.common.AbstractBaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "PLATFORM_PRIVILEGE")
@Data
public class PrivilegeEntity extends AbstractBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", updatable = false)
    @EqualsAndHashCode.Include()
    private Short id;

    /**
     * ca.bigmwaj.emapp.as.lvo.platform.PrivilegeLvo
     * is used to define the type of privilege,
     * such as READ, WRITE, DELETE, etc.
     * It is an enumerated type that helps to categorize and manage different privileges within the system. Each privilege can be associated with specific permissions and roles, allowing for fine-grained access control and security management in the application.
     */
    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

    public Object getDefaultKey() {
        return id;
    }
}
