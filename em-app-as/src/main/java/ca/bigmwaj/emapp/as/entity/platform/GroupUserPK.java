package ca.bigmwaj.emapp.as.entity.platform;

import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode
public class GroupUserPK implements Serializable {

    private Short group;

    private Short user;

    public GroupUserPK(@Nonnull GroupUserEntity entity) {
        super();
        this.user = entity.getUser().getId();
        this.group = entity.getGroup().getId();
    }
}
