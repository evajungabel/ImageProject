


package hu.progmasters.moovsmart.service;

import hu.progmasters.moovsmart.config.CustomUserRole;
import hu.progmasters.moovsmart.domain.*;
import hu.progmasters.moovsmart.dto.*;
import hu.progmasters.moovsmart.exception.*;
import hu.progmasters.moovsmart.repository.AgentCommentRepository;
import hu.progmasters.moovsmart.repository.CustomUserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service
@Transactional
public class CustomUserService implements UserDetailsService {

    private final CustomUserRepository customUserRepository;
    private ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private ConfirmationTokenService confirmationTokenService;
    private EstateAgentService estateAgentService;
    private AgentCommentRepository agentCommentRepository;

    private SendingEmailService sendingEmailService;

    private PropertyService propertyService;

    private CustomUserEmailService customUserEmailService;

    @Autowired
    public CustomUserService(CustomUserRepository customUserRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder, ConfirmationTokenService confirmationTokenService, EstateAgentService estateAgentService, SendingEmailService sendingEmailService, CustomUserEmailService customUserEmailService, AgentCommentRepository agentCommentRepository) {
        this.customUserRepository = customUserRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.confirmationTokenService = confirmationTokenService;
        this.estateAgentService = estateAgentService;
        this.agentCommentRepository = agentCommentRepository;
        this.sendingEmailService = sendingEmailService;
        this.customUserEmailService = customUserEmailService;
    }


    @Autowired
    public CustomUserService setPropertyService(PropertyService propertyService) {
        this.propertyService = propertyService;
        return this;
    }


    public CustomUserInfo register(CustomUserForm customUserForm) {
        if (customUserRepository.findByEmail(customUserForm.getEmail()) != null) {
            throw new EmailAddressExistsException(customUserForm.getEmail());
        } else if (customUserRepository.findByUsername(customUserForm.getUsername()) != null) {
            throw new UsernameExistsException(customUserForm.getUsername());
        } else {
            ConfirmationToken confirmationToken = createConfirmationToken();
            CustomUser customUser = buildCustomUserForRegistration(customUserForm, confirmationToken);
            confirmationToken.setCustomUser(customUser);
            CustomUser savedUser = customUserRepository.save(customUser);
            isAgent(customUserForm, customUser);
            addToEmailList(customUserForm, customUser);
            CustomUserInfo customUserInfo = modelMapper.map(savedUser, CustomUserInfo.class);
            customUserInfo.setCustomUserRoles(customUser.getRoles());
            sendingActivationEmail(customUserForm.getName(), customUserForm.getEmail());
            deleteIfItIsNotActivated(customUser.getUsername());
            return customUserInfo;
        }
    }


    public CustomUser buildCustomUserForRegistration(CustomUserForm customUserForm, ConfirmationToken confirmationToken) {
        return new CustomUser().builder()
                .username(customUserForm.getUsername())
                .name(customUserForm.getName())
                .email(customUserForm.getEmail())
                .phoneNumber(customUserForm.getPhoneNumber())
                .password(passwordEncoder.encode(customUserForm.getPassword()))
                .roles(List.of(CustomUserRole.ROLE_USER))
                .enable(false)
                .hasNewsletter(customUserForm.getHasNewsletter())
                .activation(confirmationToken.getConfirmationToken())
                .confirmationToken(confirmationToken)
                .isAgent(customUserForm.getIsAgent())
                .build();
    }

    public int countByIsAdminTrue() {
        int count = 0;
        for (CustomUserInfo customUserInfo : getCustomUsers()) {
            for (CustomUserRole customUserRole : customUserInfo.getCustomUserRoles()) {
                if (customUserRole.equals(CustomUserRole.ROLE_ADMIN)) {
                    count = +1;
                }
            }
        }
        return count;
    }


    public void isAgent(CustomUserForm customUserForm, CustomUser customUser) {
        if (Boolean.TRUE.equals(customUserForm.getIsAgent())) {
            customUser.setRoles(List.of(CustomUserRole.ROLE_AGENT));
            estateAgentService.save(customUser);
        }
    }

    public void sendingActivationEmail(String name, String email) {
        String subject = "Felhasználói fiók aktivalása";
        String text = "Kedves " + name +
                "! \n \n Köszönjük, hogy regisztrált az oldalunkra! " +
                "\n \n Kérem, kattintson a linkre, hogy visszaigazolja a regisztrációját," +
                " amire 30 perce van! \n \n http://localhost:8080/api/customusers/activation/"
                + findCustomUserByEmail(email).getActivation();
        sendingEmailService.sendEmail(email, subject, text);
    }

    public void addToEmailList(CustomUserForm customUserForm, CustomUser customUser) {
        if (customUser.isHasNewsletter() && customUser.getCustomUserEmail() == null) {
            CustomUserEmail customUserEmail = CustomUserEmail.builder()
                    .email(customUserForm.getEmail())
                    .customUser(customUser)
                    .build();
            customUserEmailService.save(customUserEmail);
        }
    }


    public void deleteIfItIsNotActivated(String username) {

        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        Runnable task = () -> {
            CustomUser customUser = findCustomUserByUsername(username);
            if (!(customUser.isEnabled())) {
                customUserRepository.delete(customUser);
            }
        };

        scheduledExecutorService.schedule(task, 60, TimeUnit.SECONDS);

    }


    public ConfirmationToken createConfirmationToken() {
        ConfirmationToken confirmationToken = new ConfirmationToken();
        confirmationToken.setConfirmationToken(UUID.randomUUID().toString());
        confirmationToken.setCreatedDate(LocalDateTime.now());
        confirmationToken.setExpiredDate(LocalDateTime.now().plusMinutes(1));
        return confirmationTokenService.save(confirmationToken);
    }

    public String userActivation(String confirmationToken) {
        try {
            CustomUser customUser = customUserRepository.findByActivation(confirmationToken);
            if ((LocalDateTime.now()).isBefore(customUser.getConfirmationToken().getExpiredDate())) {
                customUser.setEnable(true);
                return "Activation is successful!";
            } else {
                customUserRepository.delete(customUser);
                return "The token is invalid or broken";
            }
        } catch (TokenCannotBeUsedException e) {
            throw new TokenCannotBeUsedException(confirmationToken);
        }
    }



    public CustomUserInfo login(String username, String email, String password) {
        if (username != null) {
            CustomUser customUser = findCustomUserByUsername(username);
            if (passwordEncoder.matches(password, customUser.getPassword())) {
                return modelMapper.map(customUser, CustomUserInfo.class);
            } else {
                throw new PasswordNotValidException(password);
            }
        }
        if (email != null) {
            CustomUser customUser = findCustomUserByEmail(email);
            if (passwordEncoder.matches(password, customUser.getPassword())) {
                return modelMapper.map(customUser, CustomUserInfo.class);
            } else {
                throw new PasswordNotValidException(password);
            }
        } throw new UsernameAndEmailAddressNotFoundException(username, email);
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        CustomUser customUser = findCustomUserByUsername(username);
        String[] roles = customUser.getRoles().stream()
                .map(Enum::toString)
                .toArray(String[]::new);

        if (customUser.isEnabled()) {
            return User
                    .withUsername(customUser.getUsername())
                    .authorities(AuthorityUtils.createAuthorityList(roles))
                    .password(customUser.getPassword())
                    .build();
        } else {
            throw new UsernameNotFoundExceptionImp(username);
        }
    }


    public List<CustomUserInfo> getCustomUsers() {
        List<CustomUser> customUsers = customUserRepository.findAll();
        List<CustomUserInfo> customUserInfos = customUsers.stream()
                .map(customUser -> {
                    CustomUserInfo customUserInfo = modelMapper.map(customUser, CustomUserInfo.class);
                    customUserInfo.setCustomUserRoles(customUser.getRoles());
                    return customUserInfo;
                })
                .collect(Collectors.toList());
        return customUserInfos;
    }

    public CustomUser findCustomUserByUsername(String username) {
        Optional<CustomUser> customUserOptional = Optional.ofNullable(customUserRepository.findByUsername(username));
        if (customUserOptional.isEmpty()) {
            throw new UsernameNotFoundExceptionImp(username);
        }
        return customUserOptional.get();
    }

    public CustomUser findCustomUserByEmail(String email) {
        Optional<CustomUser> customUserOptional = Optional.ofNullable(customUserRepository.findByEmail(email));
        if (customUserOptional.isEmpty()) {
            throw new EmailAddressNotFoundException(email);
        }
        return customUserOptional.get();
    }


    public CustomUserInfo getCustomUserDetails(String username) {
        CustomUser customUser = findCustomUserByUsername(username);
        CustomUserInfo customUserInfo = modelMapper.map(customUser, CustomUserInfo.class);
        customUserInfo.setCustomUserRoles(customUser.getRoles());
        return customUserInfo;
    }


    public String deleteSale(String username, Long pId) {
        CustomUser customUser = findCustomUserByUsername(username);
        List<Long> propertyIds = new ArrayList<>();
        for (Property property : customUser.getPropertyList()) {
            propertyIds.add(property.getId());
        }
            if (propertyIds.contains(pId)) {
                Property property = propertyService.findPropertyById(pId);
                property.setStatus(PropertyStatus.INACTIVE);
                property.setDateOfSale(LocalDateTime.now());
                if (customUser.getRoles().equals(List.of(CustomUserRole.ROLE_AGENT))) {
                    Integer sellPoint = customUser.getEstateAgent().getSellPoint();
                    customUser.getEstateAgent().setSellPoint(sellPoint + 1);
                    if (sellPoint >= 50 && sellPoint < 150) {
                        customUser.getEstateAgent().setRank(AgentRank.MEDIOR);
                    } else if (sellPoint >= 150) {
                        customUser.getEstateAgent().setRank(AgentRank.PROFESSIONAL);
                    } else {
                        customUser.getEstateAgent().setRank(AgentRank.RECRUIT);
                    }
                }
                return "Congratulate! You sold your property!";
            }
        return "There is no property with that id.";
    }


    public String deleteProperty(String username, Long pId) {
        CustomUser customUser = findCustomUserByUsername(username);
        for (Property property : customUser.getPropertyList()) {
            if (property.getId().equals(pId)) {
                property.setStatus(PropertyStatus.INACTIVE);
                property.setDateOfInactivation(LocalDateTime.now());
                return "You deleted your property!";
            }
        }
        return "There is no property with that id.";
    }


    public CustomUserInfo update(String username, CustomUserForm customUserForm) {
        CustomUser customUser = findCustomUserByUsername(username);
        if (customUserRepository.findByUsername(customUserForm.getUsername()) != null &&
                !(customUserForm.getUsername().equals(customUser.getUsername()))) {
            throw new UsernameExistsException(customUserForm.getUsername());
        } else if (customUserRepository.findByEmail(customUserForm.getEmail()) != null &&
                !(customUserForm.getEmail().equals(customUser.getEmail()))) {
            throw new EmailAddressExistsException(customUserForm.getEmail());
        } else {
            String emailOld = customUser.getEmail();
            String nameOld = customUser.getName();
            modelMapper.map(customUserForm, customUser);
            isAgent(customUserForm, customUser);
            if (Boolean.FALSE.equals(customUserForm.getIsAgent())) {
                customUser.setRoles(List.of(CustomUserRole.ROLE_USER));
            }
            addToEmailList(customUserForm, customUser);
            if (Boolean.FALSE.equals(customUserForm.getHasNewsletter())) {
                CustomUserEmail customUserEmail = customUser.getCustomUserEmail();
                customUser.setCustomUserEmail(null);
                if (customUserEmail != null) {
                    customUserEmailService.delete(customUserEmail);
                }
            }
            customUser.setPassword(passwordEncoder.encode(customUserForm.getPassword()));
            CustomUserInfo customUserInfo = modelMapper.map(customUser, CustomUserInfo.class);
            customUserInfo.setCustomUserRoles(customUser.getRoles());
            sendingEmailForUpdate(nameOld, emailOld);
            return customUserInfo;
        }
    }

    public void sendingEmailForUpdate(String name, String email) {
        String subject = "Felhasználói fiók adatainak megváltoztatása";
        String text = "Kedves " + name +
                "! \n \n Felhasználói fiókjának adatai megváltoztak! " +
                "Ha nem Ön tette, mielőbb lépjen kapcsolatba velünk!";
        sendingEmailService.sendEmail(email, subject, text);
    }


    public String makeInactive(String customUsername) {
        CustomUser customUser = findCustomUserByUsername(customUsername);
        deleteProperty(customUser.getUsername(), customUser.getCustomUserId());
        CustomUserEmail customUserEmail = customUserEmailService.findCustomUserEmailByEmail(customUser.getEmail());
        customUser.setCustomUserEmail(null);
        customUserEmailService.delete(customUserEmail);
        customUser.setHasNewsletter(false);
        customUser.setUsername(null);
        customUser.setName(null);
        customUser.setEmail(null);
        customUser.setPassword(null);
        customUser.setPhoneNumber(null);
        customUser.setRoles(null);
        customUser.setEnable(false);
        customUser.setActivation(null);
        customUser.setConfirmationToken(null);
        customUser.setDeleteDate(LocalDateTime.now());
        customUser.setDeleted(true);
        customUser.setCustomUserEmail(null);
        return "You deleted your profile!";
    }

    public AgentCommentInfo comment(CommentForm comment) {
        CustomUser agent = findCustomUserByUsername(comment.getAgentName());
        CustomUser user = findCustomUserByUsername(comment.getUserName());
        AgentComment agentComment = new AgentComment();
        agentComment.setComment(comment.getComment());
        agentComment.setCustomUsername(user.getUsername());
        agentComment.setEstateAgent(agent.getEstateAgent());
        agentCommentRepository.save(agentComment);
        return modelMapper.map(agentComment, AgentCommentInfo.class);
    }

    public EstateAgentInfo getAgentInfo(String userName) {
        CustomUser customUser = findCustomUserByUsername(userName);
        EstateAgent agent = customUser.getEstateAgent();
        List<AgentComment> comments = agent.getComments();
        List<AgentCommentInfo> commentInfos = new ArrayList<>();
        for (AgentComment comment : comments) {
            commentInfos.add(modelMapper.map(comment, AgentCommentInfo.class));
        }
        EstateAgentInfo info = modelMapper.map(agent, EstateAgentInfo.class);
        info.setCommentInfo(commentInfos);
        return info;
    }


    @Scheduled(cron = "0 0 6 * * ?")
    public void sendingNewsletter() {
        for (CustomUserEmail customUserEmail : customUserEmailService.getCustomUserEmails()) {
            String subject = "Hírlevél az újdonságokról!";
            String text = "Kedves " + findCustomUserByEmail(customUserEmail.getEmail()).getName() +
                    "! \n \n Ezennel küldjük a 24 óra alatt regisztrált új ingatlanokat!"
                    + "\n \n" + propertyService.getProperties24().toString()
                    + "\n \n Ha le szeretne íratkozni, kérem kattintson a következő linkre: "
                    + "http://localhost:8080/api/customusers/unsubscribenewsletter/"
                    + customUserEmail.getCustomUser().getConfirmationToken().getConfirmationToken();
            sendingEmailService.sendEmail(customUserEmail.getEmail(), subject, text);
        }
    }

    public String userUnsubscribeNewsletter(String confirmationToken) {
        try {
            CustomUser customUser = customUserRepository.findByActivation(confirmationToken);
            CustomUserEmail customUserEmail = customUserEmailService.findCustomUserEmailByEmail(customUser.getEmail());
            customUser.setCustomUserEmail(null);
            customUser.setHasNewsletter(false);
            customUserEmailService.delete(customUserEmail);
            return "Sikeresen leíratkozott a hírlevélről!";
        } catch (TokenCannotBeUsedException e) {
            throw new TokenCannotBeUsedException(confirmationToken);
        }
    }


    public CustomUserInfo registerAdmin(CustomUserFormAdmin customUserFormAdmin) {
        if (customUserRepository.findByEmail(customUserFormAdmin.getEmail()) != null) {
            throw new EmailAddressExistsException(customUserFormAdmin.getEmail());
        } else if (customUserRepository.findByUsername(customUserFormAdmin.getUsername()) != null) {
            throw new UsernameExistsException(customUserFormAdmin.getUsername());
        } else {
            ConfirmationToken confirmationToken = createConfirmationToken();
            CustomUser customUser = buildCustomUserForRegistration(modelMapper.map(customUserFormAdmin, CustomUserForm.class), confirmationToken);
            customUser.setRoles(List.of(CustomUserRole.ROLE_ADMIN, CustomUserRole.ROLE_USER));
            confirmationToken.setCustomUser(customUser);
            CustomUser savedUser = customUserRepository.save(customUser);
            addToEmailList(modelMapper.map(customUserFormAdmin, CustomUserForm.class), customUser);
            CustomUserInfo customUserInfo = modelMapper.map(savedUser, CustomUserInfo.class);
            customUserInfo.setCustomUserRoles(customUser.getRoles());
            sendingActivationEmail(customUserFormAdmin.getName(), customUserFormAdmin.getEmail());
            deleteIfItIsNotActivated(customUser.getUsername());
            return customUserInfo;
        }
    }

    public CustomUserInfo giveRoleAdmin(String username) {
        CustomUser customUser = findCustomUserByUsername(username);
        if (!customUser.getRoles().contains(CustomUserRole.ROLE_ADMIN)) {
            customUser.getRoles().add(CustomUserRole.ROLE_ADMIN);
            CustomUserInfo customUserInfo = modelMapper.map(customUser, CustomUserInfo.class);
            customUserInfo.setCustomUserRoles(customUser.getRoles());
            return customUserInfo;
        } else {
            throw new RoleAdminExistsException(username);
        }

    }

    public String deleteUser(String customUsername) {
        CustomUser customUser = findCustomUserByUsername(customUsername);
        customUserRepository.delete(customUser);
        return "Customer is deleted!";
    }
}


