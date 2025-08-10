package com.example.commonemail.service;

import com.example.commonnotification.dto.request.KafkaRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

@RequiredArgsConstructor
@Slf4j
public class MailSenderService {
    private final JavaMailSender javaMailSender;

    public void sendConfirmationMail(KafkaRequest request) {
        log.info("Send confirmation mail started for email: {}", request.getEmail());

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper;
        try {
            helper = new MimeMessageHelper(message, "utf-8");
            helper.setFrom(new InternetAddress("info@sonbuc.com", "Sonbuc"));
            helper.setTo(request.getEmail());
            helper.setSubject("Confirm account!");
            String confirmationLink = "http://localhost:5173/confirm/" + request.getToken();
            String emailContent = "<html><body><p>Please click the following link to confirm your account:</p>"
                    + "<a href=\"" + confirmationLink + "\">Confirm Account</a>"
                    + "</body></html>";
            helper.setText(emailContent, true);

            log.debug("Email content set with confirmation link: {}", confirmationLink);

        } catch (MessagingException e) {
            log.error("MessagingException while creating email for: {}", request.getEmail(), e);
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        try {
            javaMailSender.send(message);
            log.info("Confirmation mail sent successfully to: {}", request.getEmail());
        } catch (Exception e) {
            log.error("Failed to send confirmation mail to: {}", request.getEmail(), e);
            throw e;
        }
    }


    public void sendMail(String subject,String emailContent,String email) {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

        try {
            helper.setFrom(new InternetAddress("info@sonbuc.com", "Sonbuc"));
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(emailContent, true);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        javaMailSender.send(message);
    }

    public void sendEmailWithAttachment(String toEmail, String subject, String body, byte[] attachment, String attachmentName)
            throws MessagingException {

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.setText(body);

        // PDF dosyasını ek olarak ekleyin
        helper.addAttachment(attachmentName, new ByteArrayResource(attachment));

        javaMailSender.send(message);
    }

}
