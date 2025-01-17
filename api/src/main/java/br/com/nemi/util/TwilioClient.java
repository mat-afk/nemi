package br.com.nemi.util;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TwilioClient {

    @Value("${api.service.twilio.account-sid}")
    private String TWILIO_ACCOUNT_SID;

    @Value("${api.service.twilio.auth-token}")
    private String TWILIO_AUTH_TOKEN;

    public void sendMessage(String to, String message) {
        Twilio.init(TWILIO_ACCOUNT_SID, TWILIO_AUTH_TOKEN);

        String FROM = "whatsapp:+14155238886";
        Message.creator(
                new PhoneNumber("whatsapp:" + to),
                new PhoneNumber(FROM),
                message
        ).create();
    }
}
