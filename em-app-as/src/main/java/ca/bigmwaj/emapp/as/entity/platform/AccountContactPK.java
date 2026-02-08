package ca.bigmwaj.emapp.as.entity.platform;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Data
public class AccountContactPK implements Serializable {

    private Long account;

    private Long contact;

    public AccountContactPK() {
        super();
    }

    public AccountContactPK(Long account, Long contact) {
        super();
        this.contact = contact;
        this.account = account;
    }

    public AccountContactPK(AccountContactEntity entity) {
        super();
        this.contact = entity.getContact().getId();
        this.account = entity.getAccount().getId();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AccountContactPK pk)) {
            return false;
        }
        return Objects.equals(contact, pk.contact) && Objects.equals(account, pk.account);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contact, account);
    }
}
