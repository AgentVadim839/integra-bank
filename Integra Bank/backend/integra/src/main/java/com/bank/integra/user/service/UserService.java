package com.bank.integra.user.service;

import com.bank.integra.general.repository.RolesRepository;
import com.bank.integra.user.repository.UserDetailsRepository;
import com.bank.integra.user.repository.UserRepository;
import com.bank.integra.user.model.UserDetails;
import com.bank.integra.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserDetailsRepository userDetailsRepository;
    private final RolesRepository rolesRepository;

    public UserService(UserRepository userRepository, UserDetailsRepository userDetailsRepository, RolesRepository rolesRepository) {
        this.userRepository = userRepository;
        this.userDetailsRepository = userDetailsRepository;
        this.rolesRepository = rolesRepository;
    }

    @Transactional
    public User createUser(User user, UserDetails userDetails) {
        user.setUserDetails(userDetails);
        User savedUser = userRepository.save(user);
        return savedUser;
    }

    public User getUserById(Integer id) {
        return userRepository.findById(id).orElse(null);
    }

    public UserDetails getUserDetailsByUserId(Integer id) {
        return userDetailsRepository.findById(id).orElse(null);
    }

    @Transactional
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public UserDetails updateUserDetails (UserDetails userDetails) {
        return userDetailsRepository.save(userDetails);
    }

    @Transactional
    public void deleteUserById(Integer id) {
        userRepository.deleteById(id);
    }

    @Transactional
    public void deleteUserDetailsById(Integer id) {
        userDetailsRepository.deleteById(id);
    }

    public List<User> getAllUsers() {
        return userRepository.findByDtype("USER");
    }

    public Boolean existsByEmail(String email) {
        return userDetailsRepository.existsByEmail(email);
    }

    public UserDetails getUserDetailsByEmail(String email) {
        UserDetails details = userDetailsRepository.findUserDetailsByEmail(email);
        return details;
    }


    public Boolean existsByUserId(Integer id) {
        return userDetailsRepository.existsByUserId(id);
    }
}
