package hu.progmasters.moovsmart.repository;

import hu.progmasters.moovsmart.domain.CustomUserEmail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomUserEmailRepository  extends JpaRepository<CustomUserEmail, Long> {
    CustomUserEmail findCustomUserEmailByEmail(String email);
}
