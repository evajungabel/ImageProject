package hu.progmasters.moovsmart.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(name = "custom_user_email")
public class CustomUserEmail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "custom_user_email_id")
    private Long customUserEmailId;

    @Column(name = "e_mail")
    private String email;

    @OneToOne()
    @JoinColumn(name = "custom_user_id")
    private CustomUser customUser;


}
