package hu.progmasters.moovsmart.controller;

import hu.progmasters.moovsmart.dto.*;
import hu.progmasters.moovsmart.service.CustomUserService;
import hu.progmasters.moovsmart.service.SendingEmailService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/customusers")
@Slf4j
public class CustomUserController {

    private CustomUserService customUserService;
    private SendingEmailService sendingEmailService;

    @Autowired
    public CustomUserController(CustomUserService customUserService, SendingEmailService sendingEmailService) {
        this.customUserService = customUserService;
        this.sendingEmailService = sendingEmailService;
    }


    @PostMapping("/registration")
    @Operation(summary = "Saving customer")
    @ApiResponse(responseCode = "201", description = "Customer is saved by themselves")
    public ResponseEntity<CustomUserInfo> register(@Valid @RequestBody CustomUserForm customUserForm) {
        log.info("Http request, POST /api/customusers/registration, body: " + customUserForm.toString());
        CustomUserInfo customUserInfo = customUserService.register(customUserForm);
        log.info("POST data from repository from /api/customusers/registration, body: " + customUserForm);
        return new ResponseEntity<>(customUserInfo, HttpStatus.CREATED);
    }

    @PostMapping("/login/me")
    @Operation(summary = "Login customer")
    @ApiResponse(responseCode = "201", description = "Customer is logged in")
    public ResponseEntity<CustomUserInfo> login(@RequestBody CustomUserLogInForm customUserLogInForm) {
        log.info("Http request, GET /api/customusers/login/me");
        CustomUserInfo loggedInUser = customUserService.login(customUserLogInForm.getUsername(), customUserLogInForm.getEmail(), customUserLogInForm.getPassword());
        log.info("GET data from repository from repository/api/customusers/login/me");
        return new ResponseEntity<>(loggedInUser, HttpStatus.OK);
    }

    @GetMapping("/activation/{confirmationToken}")
    @Operation(summary = "Activating confirmation token by costumer")
    @ApiResponse(responseCode = "200", description = "Confirmation token is activated by customer")
    public ResponseEntity<String> activation(@Valid @PathVariable("confirmationToken") String confirmationToken) {
        log.info("Http request, GET /api/customusers/activation/{confirmationToken, confirmation token: " + confirmationToken);
        String result = customUserService.userActivation(confirmationToken);
        log.info("GET successful activation from /api/customusers/activation/{confirmationToken}, confirmation token: " + confirmationToken);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/unsubscribenewsletter/{confirmationToken}")
    @Operation(summary = "Unsubscribing from newsletter by customer")
    @ApiResponse(responseCode = "200", description = "Customer unsubscribe from the newsletter")
    public ResponseEntity<String> unsubscribe(@PathVariable("confirmationToken") String confirmationToken) {
        log.info("Http request, GET /api/customusers/unsubscribenewsletter/{confirmationToken}, confirmationToken: " + confirmationToken);
        String result = customUserService.userUnsubscribeNewsletter(confirmationToken);
        log.info("GET successful unsubscribe from /api/customusers/unsubscribenewsletter/{confirmationToken}, confirmationToken: " + confirmationToken);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "Getting all customers by admin")
    @ApiResponse(responseCode = "200", description = "Getting list of customers by admin")
    @SecurityRequirement(name = "basicAuth")
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<List<CustomUserInfo>> getAllCustomers() {
        log.info("Http request, GET /api/customusers, list of all customers");
        List<CustomUserInfo> customerInfoList = customUserService.getCustomUsers();
        log.info("GET data from repository from /api/customusers, list of all customers");
        return new ResponseEntity<>(customerInfoList, HttpStatus.OK);
    }

    @GetMapping("/customuser")
    @Operation(summary = "Getting a customer with username by themselves")
    @ApiResponse(responseCode = "200", description = "Get a customer with username by a themselves")
    @SecurityRequirement(name = "basicAuth")
    @Secured({"ROLE_ADMIN", "ROLE_USER", "ROLE_AGENT"})
    public ResponseEntity<CustomUserInfo> getCustomUserDetails() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("Http request, GET /api/customusers/customuser, with username: " + userDetails.getUsername());
        CustomUserInfo customerInfoList = customUserService.getCustomUserDetails(userDetails.getUsername());
        log.info("GET data from repository from /api/customusers/customuser, with username: " + userDetails.getUsername());
        return new ResponseEntity<>(customerInfoList, HttpStatus.OK);
    }

    @GetMapping("/{username}")
    @Operation(summary = "Getting a customer with username by admin")
    @ApiResponse(responseCode = "200", description = "Get a customer with username by admin.")
    @SecurityRequirement(name = "basicAuth")
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<CustomUserInfo> getCustomUserDetails(@PathVariable("username") String username) {
        log.info("Http request, GET /api/customusers/{username}, with username: " + username);
        CustomUserInfo customerInfoList = customUserService.getCustomUserDetails(username);
        log.info("GET data from repository from /api/customusers/{username}, with username: " + username);
        return new ResponseEntity<>(customerInfoList, HttpStatus.OK);
    }


    @PutMapping()
    @Operation(summary = "Updating customer by themselves")
    @ApiResponse(responseCode = "200", description = "Customer is updated by themselves.")
    @SecurityRequirement(name = "basicAuth")
    @Secured({"ROLE_ADMIN", "ROLE_USER", "ROLE_AGENT"})
    public ResponseEntity<CustomUserInfo> update(@Valid @RequestBody CustomUserForm customUserForm) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("Http request, PUT /api/customusers, body: " + customUserForm +
                " with username: " + userDetails.getUsername());
        CustomUserInfo updated = customUserService.update(userDetails.getUsername(), customUserForm);
        log.info("PUT data from repository from /api/customusers, body: " + customUserForm +
                " with username: " + userDetails.getUsername());
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }


    @PutMapping("/{username}")
    @Operation(summary = "Updating customer by admin")
    @ApiResponse(responseCode = "200", description = "Customer is updated by admin.")
    @SecurityRequirement(name = "basicAuth")
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<CustomUserInfo> update(@PathVariable("username") String username,
                                                 @Valid @RequestBody CustomUserForm customUserForm) {
        log.info("Http request, PUT /api/customusers/{username}, body: " + customUserForm +
                " with username: " + username);
        CustomUserInfo updated = customUserService.update(username, customUserForm);
        log.info("PUT data from repository from /api/customusers/{username}, body: " + customUserForm +
                " with username: " + username);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }


    @DeleteMapping()
    @Operation(summary = "Deleting customer by themselves")
    @ApiResponse(responseCode = "200", description = "Customer is deleted by themselves.")
    @SecurityRequirement(name = "basicAuth")
    @Secured({"ROLE_ADMIN", "ROLE_USER", "ROLE_AGENT"})
    public ResponseEntity<String> deleteUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("Http request, DELETE /api/customusers with username: " + userDetails.getUsername());
        String message = customUserService.makeInactive(userDetails.getUsername());
        log.info("DELETE data from repository from /api/customusers with username: " + userDetails.getUsername());
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @DeleteMapping("/{customUsername}")
    @Operation(summary = "Deleting customer by admin")
    @ApiResponse(responseCode = "200", description = "Customer is deleted by admin.")
    @SecurityRequirement(name = "basicAuth")
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<String> deleteUser(@PathVariable("customUsername") String customUsername) {
        log.info("Http request, DELETE /api/customusers/{customUsername} with username: " + customUsername);
        String message = customUserService.makeInactive(customUsername);
        log.info("DELETE data from repository from /api/customusers/{customUsername} with username: " + customUsername);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }



    @DeleteMapping("/sale/{propertyId}")
    @Operation(summary = "Customer sales a property and it is deleted")
    @ApiResponse(responseCode = "200", description = "Property is sold and deleted by costumer.")
    @SecurityRequirement(name = "basicAuth")
    @Secured({"ROLE_ADMIN", "ROLE_USER", "ROLE_AGENT"})
    public ResponseEntity<String> deleteSale(@PathVariable("propertyId") Long pId) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("Http request, DELETE /api/customusers/sale/{propertyId} with username: " + userDetails.getUsername() + ", and {propertyId} with variable: " + pId);
        String message = customUserService.deleteSale(userDetails.getUsername(), pId);
        log.info("DELETE data from repository from /api/customusers/sale/{propertyId} with username: " + userDetails.getUsername() + ", and {propertyId} with variable: " + pId);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @DeleteMapping("/sale/{username}/{propertyId}")
    @Operation(summary = "Customer sales a property and it is deleted by admin")
    @ApiResponse(responseCode = "200", description = "Property is sold and deleted by admin.")
    @SecurityRequirement(name = "basicAuth")
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<String> deleteSale(@PathVariable("username") String username, @PathVariable("propertyId") Long pId) {
        log.info("Http request, DELETE /api/customusers/sale/{username}/{propertyId} with username: " + username + ", and {propertyId} with variable: " + pId);
        String message = customUserService.deleteSale(username, pId);
        log.info("DELETE data from repository from /api/customusers/sale/{username}/{propertyId} with username: " + username + ", and {propertyId} with variable: " + pId);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{propertyId}")
    @Operation(summary = "Customer deletes a property")
    @ApiResponse(responseCode = "200", description = "Property is deleted by costumer.")
    @SecurityRequirement(name = "basicAuth")
    @Secured({"ROLE_ADMIN", "ROLE_USER", "ROLE_AGENT"})
    public ResponseEntity<String> delete(@PathVariable("propertyId") Long pId) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("Http request, DELETE /api/customusers/delete/{propertyId} with username: " + userDetails.getUsername() + ", and {propertyId} with variable: " + pId);
        String message = customUserService.deleteProperty(userDetails.getUsername(), pId);
        log.info("DELETE data from repository from /api/customusers/delete/{propertyId} with username: " + userDetails.getUsername() + ", and {propertyId} with variable: " + pId);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @PostMapping("/comment")
    @Operation(summary = "Commenting of estate agent by customer")
    @ApiResponse(responseCode = "201", description = "Comment of estate agent is created by customer.")
    @SecurityRequirement(name = "basicAuth")
    @Secured({"ROLE_ADMIN", "ROLE_USER", "ROLE_AGENT"})
    public ResponseEntity<Void> comment(@Valid @RequestBody CommentForm comment) {
        log.info("Http request, POST /api/customusers/comment, body: " + comment.toString());
        customUserService.comment(comment);
        log.info("POST data from repository from /api/customusers/comment, body: " + comment);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/comment/{userName}")
    @Operation(summary = "Getting list of comments by anybody")
    @ApiResponse(responseCode = "200", description = "Comments are listed by anybody.")
    public ResponseEntity<EstateAgentInfo> listComments(@PathVariable("userName") String userName) {
        log.info("Http request, GET /api/customusers/comment/{userName} with username: " + userName);
        EstateAgentInfo estateAgentInfo = customUserService.getAgentInfo(userName);
        log.info("GET data from repository from /api/customusers/comment/{userName} with username: " + userName);
        return new ResponseEntity<>(estateAgentInfo, HttpStatus.OK);
    }

    @PostMapping("/register-admin")
    @Operation(summary = "Registering first admin")
    @ApiResponse(responseCode = "201", description = "First admin is saved")
    public ResponseEntity<String> registerAdmin(@Valid @RequestBody CustomUserFormAdmin customUserFormAdmin) {
        log.info("Http request, POST /api/customusers/register-admin, body: " + customUserFormAdmin.toString());
        if (customUserService.countByIsAdminTrue() == 0 && customUserFormAdmin.getQuestion().equals("tetőcserép")) {
            customUserService.registerAdmin(customUserFormAdmin);
            log.info("POST data from repository from /api/customusers/register-admin, body: " + customUserFormAdmin);
            return new ResponseEntity<>("Admin registration was successful!", HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>("The admin registration is closed!", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/roleadmin/{username}")
    @Operation(summary = "Giving ROLE_ADMIN by admin")
    @ApiResponse(responseCode = "200", description = "The ROLE_ADMIN was given by admin.")
    @SecurityRequirement(name = "basicAuth")
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<CustomUserInfo> giveRoleAdmin(@PathVariable("username") String username) {
        log.info("Http request, PUT /api/customusers/roleadmin/{username}");
        CustomUserInfo customUserInfo = customUserService.giveRoleAdmin(username);
        log.info("PUT data updated in repository from /api/customusers/roleadmin/{username}");
        return new ResponseEntity<>(customUserInfo, HttpStatus.OK);
    }


    @DeleteMapping("/total/{customUsername}")
    @Operation(summary = "Deleting customer by anybody")
    @ApiResponse(responseCode = "200", description = "Customer is deleted by anybody.")
    public ResponseEntity<String> deleteUserTotal(@PathVariable("customUsername") String customUsername) {
        log.info("Http request, DELETE /api/customusers/total/{customUsername} with username: " + customUsername);
        String message = customUserService.deleteUser(customUsername);
        log.info("DELETE data from repository from /api/customusers/total/{customUsername} with username: " + customUsername);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }
}


