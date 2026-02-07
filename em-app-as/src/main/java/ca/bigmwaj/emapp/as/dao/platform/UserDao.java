package ca.bigmwaj.emapp.as.dao.platform;

import ca.bigmwaj.emapp.as.dao.AbstractDao;
import ca.bigmwaj.emapp.as.dao.shared.QueryConfig;
import ca.bigmwaj.emapp.as.entity.platform.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Optional;

@Repository
public interface UserDao extends AbstractDao<UserEntity, Long> {

    default Class<UserEntity> getEntityClass() {
        return UserEntity.class;
    }

    @Override
    default String getQuery(String queryPart){
        var root = UserEntity.class.getSimpleName();
        var c = ContactEntity.class.getSimpleName();
        var cp = ContactPhoneEntity.class.getSimpleName();
        var ce = ContactEmailEntity.class.getSimpleName();
        var ca = ContactAddressEntity.class.getSimpleName();
        var query = new ArrayList<String>();

        query.add(String.format("select %s from %s %s", queryPart, root, QueryConfig.Q_ROOT));
        query.add(String.format("left join %s c on %s.contact = c", c, QueryConfig.Q_ROOT));
        query.add(String.format("left outer join %s cp on c = cp.contact", cp));
        query.add(String.format("left outer join %s ce on c = ce.contact", ce));
        query.add(String.format("left outer join %s ca on c = ca.contact", ca));

        return String.join(" ", query);
    }

    Optional<UserEntity> findByUsername(String username);

    @Query("select u from UserEntity u " +
            "join u.contact c " +
            "join ContactEmailEntity ce on c = ce.contact " +
            "where lower(ce.email) = lower(:email)")
    Optional<UserEntity> findByEmail(String email);
}
