package ca.bigmwaj.emapp.as.dao.platform;

import ca.bigmwaj.emapp.as.dao.AbstractDao;
import ca.bigmwaj.emapp.as.dao.shared.QueryConfig;
import ca.bigmwaj.emapp.as.entity.platform.*;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface AccountDao extends AbstractDao<AccountEntity, Long> {

    default Class<AccountEntity> getEntityClass() {
        return AccountEntity.class;
    }

    @Override
    default String getQuery(String queryPart) {
        var root = AccountEntity.class.getSimpleName();
        var c = AccountContactEntity.class.getSimpleName();
        var cp = ContactPhoneEntity.class.getSimpleName();
        var ce = ContactEmailEntity.class.getSimpleName();
        var ca = ContactAddressEntity.class.getSimpleName();
        var query = new ArrayList<String>();

        query.add(String.format("select %s from %s %s", queryPart, root, QueryConfig.Q_ROOT));
        query.add(String.format("left outer join %s c on c.accountId = %s.id", c, QueryConfig.Q_ROOT));
        query.add(String.format("left outer join %s cp on c.contact = cp.contact", cp));
        query.add(String.format("left outer join %s ce on c.contact = ce.contact", ce));
        query.add(String.format("left outer join %s ca on c.contact = ca.contact", ca));

        return String.join(" ", query);
    }
}
