package com.bank.integra.services.person;

import com.bank.integra.dao.RolesRepository;
import com.bank.integra.dao.UserDetailsRepository;
import com.bank.integra.dao.UserRepository;
import com.bank.integra.entities.details.UserDetails;
import com.bank.integra.entities.person.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    public UserService() {

    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDetailsRepository userDetailsRepository;

    @Autowired
    private RolesRepository rolesRepository;

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

    public Boolean existsByUserId(Integer id) {
        return userDetailsRepository.existsByUserId(id);
    }
}
