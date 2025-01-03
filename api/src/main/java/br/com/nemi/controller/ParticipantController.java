package br.com.nemi.controller;

import br.com.nemi.domain.participant.Participant;
import br.com.nemi.service.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/participants")
public class ParticipantController {

    @Autowired
    private ParticipantService participantService;

    @GetMapping
    public ResponseEntity<List<Participant>> getParticipants() {
        List<Participant> response = this.participantService.getParticipants();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Participant> getParticipant(@PathVariable String id) {
        Participant response = this.participantService.getParticipant(id);
        return ResponseEntity.ok().body(response);
    }
}
