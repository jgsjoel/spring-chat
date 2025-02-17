package com.chat.backend.services;

import com.chat.backend.dto.requests.UserRequest;
import com.chat.backend.entities.User;
import com.chat.backend.exceptions.EntityNotFoundException;
import com.chat.backend.repositories.RoleRepo;
import com.chat.backend.repositories.UserRepo;
import com.chat.backend.util.AuthUserUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserService {

    private UserRepo userRepo;

    private final BCryptPasswordEncoder passwordEncoder;

    private AuthUserUtil authUserUtil;

    private RoleRepo roleRepo;

    UserService(
            UserRepo userRepo,
            RoleRepo roleRepo,
            BCryptPasswordEncoder passwordEncoder,
            AuthUserUtil authUserUtil
            ) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.authUserUtil = authUserUtil;
        this.roleRepo = roleRepo;
    }

    public List<Map<String,Object>> getAllUsers(){
        List<Map<String,Object>> list = new ArrayList<>();
        for(User user : userRepo.findAllExCurrent(authUserUtil.getAuthenticatedUser())){
            Map<String,Object> map = new HashMap<>();
            map.put("id", user.getId());
            map.put("username", user.getUserfName());
            map.put("email", user.getEmail());
            map.put("active", user.isActive());
            list.add(map);
        }
        return list;
    }

    public Optional<User> getUser(String email){
        return userRepo.findByEmail(email);
    }

    public User getUserByEmail(String email){
        Optional<User> userOpt = userRepo.findByEmail(email);
        if (!userOpt.isPresent()){
            throw new EntityNotFoundException("User not found");
        }
        return userOpt.get();
    }

    public User getUserByToken(String token){
        Optional<User> userOpt = userRepo.findByToken(token);
        if (!userOpt.isPresent()){
            throw new EntityNotFoundException("User not found");
        }
        return userOpt.get();
    }

    public Map<String,Object> saveUser(UserRequest request){
        Optional<User> userOptional = getUser(request.email());

        if(userOptional.isPresent()){
            throw new EntityNotFoundException("User With Same Email Exist");
        }
            User newUser = new User();
            newUser.setEmail(request.email());
            newUser.setUserfName(request.user_name());
            newUser.setPassword(passwordEncoder.encode(request.password()));
            newUser.setActive(true);
            newUser.setToken("us-"+UUID.randomUUID().toString());
            newUser.setRole(roleRepo.findRolesByRole("ROLE_USER").get());

            User savedUser = userRepo.save(newUser);
            Map<String,Object> map = new HashMap<>();
            map.put("id", savedUser.getId());
            map.put("username", savedUser.getUserfName());
            map.put("email", savedUser.getEmail());
            map.put("active", savedUser.isActive());
            return map;
    }

    public Map<String,Object> updateUserInfo(String email, UserRequest request){
        User existingUser = getUserByEmail(email);

        if(!getUser(request.email()).isPresent()){

            existingUser.setUserfName(request.user_name());
            existingUser.setEmail(request.email());
            existingUser.setPassword(passwordEncoder.encode(request.password()));

            User savedUser = userRepo.save(existingUser);
            Map<String,Object> map = new HashMap<>();
            map.put("id", savedUser.getId());
            map.put("username", savedUser.getUserfName());
            map.put("email", savedUser.getEmail());
            map.put("active", savedUser.isActive());
            return map;

        }else{
            throw new EntityNotFoundException("User With Same Email Exist");
        }
    }

    public void userStatusUpdate(String  email){
        Optional<User> existingUser = getUser(email);

        if(!existingUser.isPresent()){
            throw new EntityNotFoundException("User Not Found");
        }
        User user = existingUser.get();

        user.setActive(!user.isActive());
        userRepo.save(user);
    }

    public void updateLastSeen(String userToken){
        User user = getUserByToken(userToken);
        user.setLast_seen(LocalDateTime.now());
        userRepo.save(user);
    }

}
