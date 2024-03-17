package hu.progmasters.moovsmart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CustomUserFormAdmin {

    @NotBlank(message = "Name cannot be empty!")
    @Size(min = 3, max = 200, message = "Name must be between 3 and 200 characters!")
    private String name;

    @NotBlank(message = "Username cannot be empty!")
    @Size(min = 3, max = 200, message = "Name must be between 3 and 200 characters!")
    private String username;

    @NotNull(message = "Password cannot be empty!")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$")
    private String password;

    @NotNull(message = "Phone number cannot be empty!")
    @Size(min = 1, max = 200, message = "Phone number must be between 3 and 20 characters!")
    @Pattern(regexp = "^(\\+{0,})(\\d{0,})([(]{1}\\d{1,3}[)]{0,}){0,}(\\s?\\d+|\\+\\d{2,3}\\s{1}\\d+|\\d+){1}[\\s|-]?\\d+([\\s|-]?\\d+){1,2}(\\s){0,}$")
    private String phoneNumber;


    @NotNull(message = "E-mail cannot be empty!")
    @Size(min = 8, max = 200, message = "E-mail must be between 8 and 200 characters!")
    @Email
    @Pattern(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")
    private String email;

    @NotNull(message = "Being an agent cannot be empty!")
    private Boolean isAgent;

    @NotNull(message = "Choosing an option for sending newsletter cannot be empty!")
    private Boolean hasNewsletter;

    @NotBlank(message = "Wrong answer!")
    private String question;

}
