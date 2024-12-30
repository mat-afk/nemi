package br.com.nemi.repository;

import br.com.nemi.domain.group.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, String> {
}
