package br.com.nemi.controller;

import br.com.nemi.domain.group.Group;
import br.com.nemi.domain.group.dto.CreateGroupRequestDTO;
import br.com.nemi.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/groups")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @PostMapping
    public ResponseEntity<Group> createGroup(@RequestBody CreateGroupRequestDTO request) {
        Group response = this.groupService.createGroup(request);
        return ResponseEntity.created(URI.create("")).body(response);
    }
}
