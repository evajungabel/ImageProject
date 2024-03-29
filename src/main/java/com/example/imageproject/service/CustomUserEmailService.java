package com.example.imageproject.service;

import com.example.imageproject.domain.CustomUserEmail;
import com.example.imageproject.exception.CustomUserEmailNotFoundException;
import com.example.imageproject.repository.CustomUserEmailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CustomUserEmailService {

    private CustomUserEmailRepository customUserEmailRepository;

    @Autowired
    public CustomUserEmailService(CustomUserEmailRepository customUserEmailRepository) {
        this.customUserEmailRepository = customUserEmailRepository;
    }

    public CustomUserEmail save(CustomUserEmail customUserEmail) {
        customUserEmailRepository.save(customUserEmail);
        return customUserEmail;
    }


    public CustomUserEmail findCustomUserEmailByEmail(String email) {
        Optional<CustomUserEmail> customUserOptionalEmail = Optional.ofNullable(customUserEmailRepository.findCustomUserEmailByEmail(email));
        if (customUserOptionalEmail.isEmpty()) {
            throw new CustomUserEmailNotFoundException(email);
        }
        return customUserOptionalEmail.get();
    }

    public void delete(CustomUserEmail customUserEmail) {
        customUserEmailRepository.delete(customUserEmail);
    }

    public List<CustomUserEmail> getCustomUserEmails() {
        return customUserEmailRepository.findAll();
    }
}
