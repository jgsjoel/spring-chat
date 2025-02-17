package com.chat.backend.controllers.admin;

import com.chat.backend.dto.requests.AddToGroupRequest;
import com.chat.backend.services.ChannelService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/manage/groups")
public class ManageGroupsController {

    private ChannelService channelService;

    public ManageGroupsController(ChannelService channelService) {
        this.channelService = channelService;
    }

    //works
    @GetMapping("/users/{channel_token}")
    public ResponseEntity<?> getUsersForGroup(@PathVariable String channel_token) {
        return new ResponseEntity<>(channelService.getGroupUsers(channel_token),HttpStatus.OK);
    }

    //works
    @GetMapping("/all")
    public ResponseEntity<?> index() {
        return new ResponseEntity<>(channelService.getAllChannels(), HttpStatus.OK);
    }

    //works
    @GetMapping("/create/{name}")
    public ResponseEntity<?> addGroup(@PathVariable String name) {
        return new ResponseEntity<>(channelService.createNewChannel(name),HttpStatus.OK);
    }

    //works
    @PostMapping("/addRem")
    public ResponseEntity<?> addOrRemoveUser(@RequestBody AddToGroupRequest request) {
        Object obj = channelService.addOrRemoveUserFromGroup(request);
        if (obj == null){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }else{
            return new ResponseEntity<>(obj,HttpStatus.CREATED);
        }
    }

    //works
    @DeleteMapping("/delete/{token}")
    public ResponseEntity<?> deleteGroup(@PathVariable String token) {
        channelService.deleteChannel(token);
        return new ResponseEntity<>("Deleted Successfully!",HttpStatus.NO_CONTENT);
    }

}
