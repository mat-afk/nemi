package br.com.nemi.service;

import br.com.nemi.domain.draw.Draw;
import br.com.nemi.domain.group.Group;
import br.com.nemi.exception.NotFoundException;
import br.com.nemi.repository.DrawRepository;
import br.com.nemi.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DrawService {

    @Autowired
    private DrawRepository drawRepository;

    @Autowired
    private GroupRepository groupRepository;

    public List<Draw> getDraws(String groupId) {
        Group group = this.groupRepository.findById(groupId).orElseThrow(
                () -> new NotFoundException("Group not found with id: " + groupId)
        );

        return this.drawRepository.findByGroup(group);
    }

    public Draw getDraw(String groupId, String drawId) {
        this.groupRepository.findById(groupId).orElseThrow(
                () -> new NotFoundException("Group not found with id: " + groupId)
        );

        return this.drawRepository.findById(drawId).orElseThrow(
                () -> new NotFoundException("Draw not found with id: " + drawId)
        );
    }

}
