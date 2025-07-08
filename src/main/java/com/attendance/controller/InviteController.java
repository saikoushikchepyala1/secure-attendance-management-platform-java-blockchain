package com.attendance.controller;

import com.attendance.service.InviteService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/invite")
public class InviteController {

    @Autowired
    private InviteService inviteService;

    @PostMapping("/send")
    public String sendInvite(@RequestParam String email) {

        try {
            inviteService.sendInvite(email);
            return "Invite sent successfully to: " + email;
        } catch (MessagingException e) {
            return "Failed to send email.";
        }
    }
}
