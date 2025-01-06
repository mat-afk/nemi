package br.com.nemi.repository;

import br.com.nemi.domain.draw.Draw;
import br.com.nemi.domain.group.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DrawRepository extends JpaRepository<Draw, String> {

    List<Draw> findByGroup(Group group);
}
