package com.prototype.crm.service.imp;

import com.prototype.crm.model.dto.request.UserReqDTO;
import com.prototype.crm.model.dto.response.UserRespDTO;
import com.prototype.crm.model.entity.Users;
import com.prototype.crm.repository.UserRepository;
import com.prototype.crm.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserRespDTO addUser(UserReqDTO users) {

        if (userRepository.existsByUsername(users.getUsername())) {
            throw new RuntimeException("Users already exists.");
        }
        if (userRepository.existsByEmail(users.getEmail())) {
            throw new RuntimeException("Email already exists.");
        }

        Users userEntity = new Users();
        userEntity.setEmail(users.getEmail());
        userEntity.setUsername(users.getUsername());

        String encodedPassword = passwordEncoder.encode(users.getPassword());
        userEntity.setPassword(encodedPassword);

        userRepository.save(userEntity);

        return new UserRespDTO(
            userEntity.getId(),
            userEntity.getEmail(),
            userEntity.getUsername()
        );
    }
}
