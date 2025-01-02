package br.com.nemi.controller;

import br.com.nemi.domain.group.dto.CreateGroupRequestDTO;
import br.com.nemi.domain.group.dto.GroupDetailsResponseDTO;
import br.com.nemi.domain.participant.dto.CreateParticipantRequestDTO;
import br.com.nemi.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/groups")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @GetMapping
    public ResponseEntity<List<GroupDetailsResponseDTO>> getGroups() {
        List<GroupDetailsResponseDTO> response = this.groupService.getGroups();
        return ResponseEntity.ok().body(response);
    }

    @PostMapping
    public ResponseEntity<GroupDetailsResponseDTO> createGroup(
            @RequestBody CreateGroupRequestDTO request
    ) {
        GroupDetailsResponseDTO response = this.groupService.createGroup(request);
        return ResponseEntity.created(URI.create("")).body(response);
    }

    @PutMapping("/{groupId}/participants")
    public ResponseEntity<GroupDetailsResponseDTO> addParticipants(
            @PathVariable String groupId,
            @RequestBody List<CreateParticipantRequestDTO> request
    ) {
        GroupDetailsResponseDTO response = this.groupService.addParticipants(groupId, request);
        return ResponseEntity.created(URI.create("")).body(response);
    }
}
