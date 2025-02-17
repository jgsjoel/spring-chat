package com.chat.backend.controllers.admin;

import com.chat.backend.dto.requests.UserRequest;
import com.chat.backend.dto.requests.groups.Create;
import com.chat.backend.dto.requests.groups.Update;
import com.chat.backend.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/manage")
public class ManageUsersController {

    private UserService userService;

    public ManageUsersController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public ResponseEntity<?> geAll(){
        return new ResponseEntity<>(userService.getAllUsers(),HttpStatus.OK);
    }

    //works
    @GetMapping("/status/{email}")
    public ResponseEntity<?> updateUserStatus(@PathVariable(required = true) String email) {
        System.out.println(email);
        userService.userStatusUpdate(email);
        return new ResponseEntity<>("User Updated", HttpStatus.OK);
    }

    //works
    @PostMapping("/invite")
    public ResponseEntity<?> inviteUser(@RequestBody @Validated(Create.class) UserRequest request) {
        return new ResponseEntity<>(userService.saveUser(request), HttpStatus.CREATED);
    }

    //works
    @PutMapping("/update/{email}")
    public ResponseEntity<?> updateUserEmailAndPassword(
            @PathVariable(required = true) String email,
            @RequestBody @Validated(Update.class) UserRequest request
    ){
        Map<String,Object> user = null;
        if(!email.equals(request.email())){
            user = userService.updateUserInfo(email,request);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

}
