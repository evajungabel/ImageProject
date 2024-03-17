package hu.progmasters.moovsmart.dto;

import hu.progmasters.moovsmart.config.CustomUserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CustomUserInfo {
    private String name;
    private String username;
    private String email;
    private String phoneNumber;

    private Boolean enable;

    private Boolean hasNewsletter;
    private List<CustomUserRole> customUserRoles;

}
