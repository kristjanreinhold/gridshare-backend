package ee.gridshare.notify;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/** Sends email via SendGrid. Gated by config — logs instead of sending when disabled. */
@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final boolean enabled;
    private final String apiKey;
    private final String fromEmail;

    public EmailService(
            @Value("${app.notifications.email.enabled:false}") boolean enabled,
            @Value("${sendgrid.api-key:}") String apiKey,
            @Value("${app.notifications.email.from:no-reply@gridshare.ee}") String fromEmail) {
        this.apiKey = apiKey;
        this.fromEmail = fromEmail;
        this.enabled = enabled && !apiKey.isBlank();
        log.info("EmailService: {}", this.enabled ? "SendGrid enabled (from " + fromEmail + ")" : "disabled — email logged");
    }

    public void send(String to, String subject, String body) {
        if (to == null || to.isBlank()) return;
        if (!enabled) {
            log.info("[EMAIL disabled] → {} | {} | {}", to, subject, body);
            return;
        }
        try {
            Mail mail = new Mail(new Email(fromEmail), subject, new Email(to), new Content("text/plain", body));
            SendGrid sg = new SendGrid(apiKey);
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            sg.api(request);
            log.info("Email sent → {}", to);
        } catch (Exception e) {
            log.error("Email send failed → {} : {}", to, e.getMessage());
        }
    }
}
