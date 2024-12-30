package br.com.nemi.domain.group;

import br.com.nemi.domain.participant.Participant;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "draw_groups")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Group {

    @Id
    @Column(nullable = false)
    private String id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private Participant owner;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
