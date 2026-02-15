package ca.bigmwaj.emapp.as.entity.platform;

import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode
public class GroupRolePK implements Serializable {

    private Short group;

    private Short role;

    public GroupRolePK(@Nonnull GroupRoleEntity entity) {
        super();
        this.role = entity.getRole().getId();
        this.group = entity.getGroup().getId();
    }
}
