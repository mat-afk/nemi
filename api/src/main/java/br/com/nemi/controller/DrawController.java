package br.com.nemi.controller;

import br.com.nemi.domain.draw.Draw;
import br.com.nemi.domain.draw.dto.CreateDrawRequestDTO;
import br.com.nemi.service.DrawService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/groups/{groupId}/draws")
public class DrawController {

    @Autowired
    private DrawService drawService;

    @GetMapping
    public ResponseEntity<List<Draw>> getDraws(@PathVariable String groupId) {
        List<Draw> response = this.drawService.getDraws(groupId);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/{drawId}")
    public ResponseEntity<Draw> getDraw(
            @PathVariable String groupId,
            @PathVariable String drawId
    ) {
        Draw response = this.drawService.getDraw(groupId, drawId);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping
    public ResponseEntity<Draw> createDraw(
            @PathVariable String groupId,
            @RequestBody CreateDrawRequestDTO request
    ) {
        Draw response = this.drawService.createDraw(groupId, request);
        return ResponseEntity.created(URI.create("")).body(response);
    }

    @PutMapping("/{drawId}")
    public ResponseEntity<Draw> updateDraw(
            @PathVariable String groupId,
            @PathVariable String drawId,
            @RequestBody CreateDrawRequestDTO request
    ) {
        Draw response = this.drawService.updateDraw(groupId, drawId, request);
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/{drawId}/retry")
    public ResponseEntity<Void> retry(
            @PathVariable String groupId,
            @PathVariable String drawId
    ) {
        this.drawService.retry(groupId, drawId);
        return ResponseEntity.ok().build();
    }

}
