package hu.progmasters.moovsmart.domain;

import hu.progmasters.moovsmart.config.CustomUserRole;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(name = "custom_user")
public class CustomUser implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "custom_user_id")
    private Long customUserId;

    @Column(name = "username", unique = true)
    private String username;

    @Column(name = "name")
    private String name;

    @Column(name = "password")
    private String password;

    @Column(name = "e_mail")
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "enable")
    private boolean enable;

    @Column(name = "account_non_locked")
    private boolean accountNonLocked;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @Column(name = "delete_date")
    private LocalDateTime deleteDate;

    @Column(name = "activation")
    private String activation;

    @Column(name = "is_agent")
    private boolean isAgent;

    @Column(name = "has_newsletter")
    private boolean hasNewsletter;

    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @JoinTable(name = "custom_user_role")
    private List<CustomUserRole> roles;

    @OneToMany(mappedBy = "customUser", cascade = CascadeType.ALL)
    private List<Property> propertyList;

    @OneToOne(mappedBy = "customUser", cascade = CascadeType.ALL)
    private ConfirmationToken confirmationToken;

    @OneToOne(mappedBy = "customUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private EstateAgent estateAgent;

    @OneToMany(mappedBy = "customUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CustomUserImageURL> customUserImageURLs;


    @OneToOne(mappedBy = "customUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private CustomUserEmail customUserEmail;

    @OneToMany(mappedBy = "customUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CustomUserGame> customUserGames;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new HashSet<>();
        Set<CustomUserRole> roles1 = (Set<CustomUserRole>) getRoles();
        for (CustomUserRole role : roles1) {
            authorities.add(new SimpleGrantedAuthority(role.getRole()));
        }
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enable;
    }


}
