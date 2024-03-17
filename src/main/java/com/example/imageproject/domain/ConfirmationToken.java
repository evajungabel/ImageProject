package hu.progmasters.moovsmart.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Table(name = "confirmation_token")
public class ConfirmationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "tokenId")
    private Long tokenId;

    @Column(name = "confirmation_token")
    private String confirmationToken;

    @Column(name = "createdDate")
    private LocalDateTime createdDate;

    @Column(name = "expiredDate")
    private LocalDateTime expiredDate;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "custom_user_id")
    private CustomUser customUser;
}
