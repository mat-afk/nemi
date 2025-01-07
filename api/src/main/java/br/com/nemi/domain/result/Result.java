package br.com.nemi.domain.result;

import br.com.nemi.domain.draw.Draw;
import br.com.nemi.domain.participant.Participant;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "results")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Result {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "access_code", nullable = false)
    private String accessCode;

    @ManyToOne
    @JoinColumn(name = "draw_id", nullable = false)
    private Draw draw;

    @ManyToOne
    @JoinColumn(name = "giver_id", nullable = false)
    private Participant giver;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private Participant receiver;

}
