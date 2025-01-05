package br.com.nemi.controller;

import br.com.nemi.domain.group.dto.CreateGroupRequestDTO;
import br.com.nemi.domain.group.dto.GroupDetailsDTO;
import br.com.nemi.domain.participant.dto.AddParticipantInGroupRequestDTO;
import br.com.nemi.domain.participant.dto.ParticipantMembershipDetailsDTO;
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
    public ResponseEntity<List<GroupDetailsDTO>> getGroups() {
        List<GroupDetailsDTO> response = this.groupService.getGroups();
        return ResponseEntity.ok().body(response);
    }

    @PostMapping
    public ResponseEntity<GroupDetailsDTO> createGroup(
            @RequestBody CreateGroupRequestDTO request
    ) {
        GroupDetailsDTO response = this.groupService.createGroup(request);
        return ResponseEntity.created(URI.create("")).body(response);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable String id) {
        this.groupService.deleteGroup(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{groupId}/participants")
    public ResponseEntity<List<ParticipantMembershipDetailsDTO>> addParticipants(
            @PathVariable String groupId,
            @RequestBody List<AddParticipantInGroupRequestDTO> request
    ) {
        List<ParticipantMembershipDetailsDTO> response = this.groupService.addParticipants(groupId, request);
        return ResponseEntity.created(URI.create("")).body(response);
    }

    @PutMapping("/{groupId}/participants/{participantId}")
    public ResponseEntity<ParticipantMembershipDetailsDTO> updateParticipant(
            @PathVariable String groupId,
            @PathVariable String participantId,
            @RequestBody AddParticipantInGroupRequestDTO request
    ) {
        ParticipantMembershipDetailsDTO response = this.groupService.updateParticipant(groupId, participantId, request);

        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/{groupId}/participants/{participantId}")
    public ResponseEntity<Void> removeParticipant(
            @PathVariable String groupId,
            @PathVariable String participantId
    ) {
        this.groupService.removeParticipant(groupId, participantId);

        return ResponseEntity.ok().build();
    }

}
