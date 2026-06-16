package ee.gridshare.notify;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/** Sends SMS via Twilio. Gated by config — logs instead of sending when disabled. */
@Service
public class SmsService {

    private static final Logger log = LoggerFactory.getLogger(SmsService.class);

    private final boolean enabled;
    private final String fromNumber;

    public SmsService(
            @Value("${app.notifications.sms.enabled:false}") boolean enabled,
            @Value("${twilio.account-sid:}") String accountSid,
            @Value("${twilio.auth-token:}") String authToken,
            @Value("${twilio.from-number:}") String fromNumber) {
        this.fromNumber = fromNumber;
        this.enabled = enabled && !accountSid.isBlank() && !authToken.isBlank() && !fromNumber.isBlank();
        if (this.enabled) {
            Twilio.init(accountSid, authToken);
            log.info("SmsService: Twilio enabled (from {})", fromNumber);
        } else {
            log.info("SmsService: disabled — SMS will be logged, not sent");
        }
    }

    /** Best-effort send; never throws (a failed notification must not break the booking flow). */
    public void send(String to, String body) {
        if (to == null || to.isBlank()) {
            log.info("SMS skipped — recipient has no phone number set: {}", body);
            return;
        }
        if (!enabled) {
            log.info("[SMS disabled] → {} : {}", to, body);
            return;
        }
        try {
            Message.creator(new PhoneNumber(to), new PhoneNumber(fromNumber), body).create();
            log.info("SMS sent → {}", to);
        } catch (Exception e) {
            log.error("SMS send failed → {} : {}", to, e.getMessage());
        }
    }
}
