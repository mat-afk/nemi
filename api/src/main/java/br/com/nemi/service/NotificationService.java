package br.com.nemi.service;

import br.com.nemi.domain.result.dto.ResultMessageDTO;
import br.com.nemi.util.EmailClient;
import br.com.nemi.util.TwilioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private EmailClient emailClient;

    @Autowired
    private TwilioClient twilioClient;

    public void notify(ResultMessageDTO resultMessageDTO) {
        if (resultMessageDTO.email() != null) {
            String subject = "Descubra o seu amigo secreto no %s!".formatted(resultMessageDTO.drawTitle());
            String body = new File("templates/result.html").toString()
                    .formatted(resultMessageDTO.nickname(), resultMessageDTO.url(), resultMessageDTO.accessCode());

            emailClient.sendEmail(resultMessageDTO.email(), subject, body);
        }

        if (resultMessageDTO.phoneNumber() != null)
            twilioClient.sendMessage(resultMessageDTO.phoneNumber(), "");
    }

    public void notifyAll(List<ResultMessageDTO> resultMessageDTOs) {
        resultMessageDTOs.forEach(this::notify);
    }

}
