package br.com.nemi.service;

import br.com.nemi.domain.participant.Participant;
import br.com.nemi.util.TwilioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private TwilioClient twilioClient;

    public void notify(Participant participant) {
        if (participant.getPhoneNumber() != null)
            twilioClient.sendMessage(participant.getPhoneNumber());
    }

    public void notifyAll(List<Participant> participants) {
        participants.forEach(this::notify);
    }

}
