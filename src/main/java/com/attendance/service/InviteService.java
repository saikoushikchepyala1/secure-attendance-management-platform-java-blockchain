package com.attendance.service;

import com.attendance.model.InviteToken;
import com.attendance.repository.InviteTokenRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class InviteService {

    @Autowired
    private InviteTokenRepository inviteTokenRepository;

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public void sendInvite(String email) throws MessagingException {
        String token = UUID.randomUUID().toString();
        LocalDateTime expiry = LocalDateTime.now().plusDays(2);

        InviteToken invite = new InviteToken(email, token, expiry);
        inviteTokenRepository.save(invite);

        String link = frontendUrl + "/register.html?token=" + token;
        sendEmail(email, link);
    }

    public Optional<InviteToken> validateToken(String token) {
        return inviteTokenRepository.findByToken(token)
                .filter(t -> !t.isUsed() && t.getExpiryDate().isAfter(LocalDateTime.now()));
    }

    public void markUsed(InviteToken token) {
        token.setUsed(true);
        inviteTokenRepository.save(token);
    }

    private void sendEmail(String to, String link) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject("Your Registration Link");
        helper.setText("<p>Click the link to register: <a href=\"" + link + "\">Register Here</a></p>", true);
        javaMailSender.send(message);
    }
}
