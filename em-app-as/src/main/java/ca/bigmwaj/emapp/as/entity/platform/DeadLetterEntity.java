package ca.bigmwaj.emapp.as.entity.platform;

import ca.bigmwaj.emapp.as.entity.common.AbstractStatusTrackingEntity;
import ca.bigmwaj.emapp.dm.lvo.platform.DeadLetterStatusLvo;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "PLATFORM_DEAD_LETTER")
@Data
public class DeadLetterEntity extends AbstractStatusTrackingEntity<DeadLetterStatusLvo> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", updatable = false)
    @EqualsAndHashCode.Include()
    private Long id;

    @Column(name = "EVENT_NAME", nullable = false, updatable = false)
    private String eventName;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private DeadLetterStatusLvo status;

    @Column(name = "MESSAGE", nullable = false)
    private String message;

    @Column(name = "ERROR_MESSAGE", nullable = false)
    private String errorMessage;

    public Object getDefaultKey() {
        return id;
    }
}
