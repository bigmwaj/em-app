package ca.bigmwaj.emapp.as.dao.platform;

import ca.bigmwaj.emapp.as.dao.AbstractDao;
import ca.bigmwaj.emapp.as.dao.shared.QueryConfig;
import ca.bigmwaj.emapp.as.entity.platform.*;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface AccountDao extends AbstractDao<AccountEntity, Short> {

    default Class<AccountEntity> getEntityClass() {
        return AccountEntity.class;
    }

    @Override
    default String getQuery(String queryPart) {
        var root = AccountEntity.class.getSimpleName();
        var ac = AccountContactEntity.class.getSimpleName();
        var c = ContactEntity.class.getSimpleName();
        var cp = ContactPhoneEntity.class.getSimpleName();
        var ce = ContactEmailEntity.class.getSimpleName();
        var ca = ContactAddressEntity.class.getSimpleName();
        var query = new ArrayList<String>();

        query.add(String.format("select distinct %s from %s %s", queryPart, root, QueryConfig.Q_ROOT));
        query.add(String.format("left outer join %s ac on ac.account = %s", ac, QueryConfig.Q_ROOT));
        query.add(String.format("left outer join %s c on c = ac.contact", c));
        query.add(String.format("left outer join %s cp on c = cp.contact", cp));
        query.add(String.format("left outer join %s ce on c = ce.contact", ce));
        query.add(String.format("left outer join %s ca on c = ca.contact", ca));

        return String.join(" ", query);
    }
}
