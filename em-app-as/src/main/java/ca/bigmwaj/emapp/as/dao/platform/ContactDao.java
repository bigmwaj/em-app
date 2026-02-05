package ca.bigmwaj.emapp.as.dao.platform;

import ca.bigmwaj.emapp.as.dao.AbstractDao;
import ca.bigmwaj.emapp.as.dao.shared.QueryConfig;
import ca.bigmwaj.emapp.as.entity.platform.ContactAddressEntity;
import ca.bigmwaj.emapp.as.entity.platform.ContactEmailEntity;
import ca.bigmwaj.emapp.as.entity.platform.ContactEntity;
import ca.bigmwaj.emapp.as.entity.platform.ContactPhoneEntity;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface ContactDao extends AbstractDao<ContactEntity, Long> {

    default Class<ContactEntity> getEntityClass() {
        return ContactEntity.class;
    }

    @Override
    default String getQuery(String queryPart){
        var root = ContactEntity.class.getSimpleName();
        var cp = ContactPhoneEntity.class.getSimpleName();
        var ce = ContactEmailEntity.class.getSimpleName();
        var ca = ContactAddressEntity.class.getSimpleName();
        var query = new ArrayList<String>();

        query.add(String.format("select %s from %s %s", queryPart, root, QueryConfig.Q_ROOT));
        query.add(String.format("left outer join %s cp on %s = cp.contact", cp, QueryConfig.Q_ROOT));
        query.add(String.format("left outer join %s ce on %s = ce.contact", ce, QueryConfig.Q_ROOT));
        query.add(String.format("left outer join %s ca on %s = ca.contact", ca, QueryConfig.Q_ROOT));

        return String.join(" ", query);
    }
}
