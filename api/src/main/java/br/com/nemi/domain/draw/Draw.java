package br.com.nemi.domain.draw;

import br.com.nemi.domain.group.Group;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "draws")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Draw {

    @Id
    private String id;

    private String title;

    private String description;

    @Column(name = "base_price")
    private Double basePrice;

    @Column(name = "event_date")
    private LocalDate eventDate;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @Column(name = "drawn_at", nullable = false)
    private LocalDateTime drawnAt;
}
