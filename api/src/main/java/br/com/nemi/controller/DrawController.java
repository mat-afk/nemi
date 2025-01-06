package br.com.nemi.controller;

import br.com.nemi.domain.draw.Draw;
import br.com.nemi.service.DrawService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("{drawId}")
    public ResponseEntity<Draw> getDraw(
            @PathVariable String groupId,
            @PathVariable String drawId
    ) {
        Draw draw = this.drawService.getDraw(groupId, drawId);
        return ResponseEntity.ok().body(draw);
    }

}
