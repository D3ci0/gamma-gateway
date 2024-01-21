package com.gamma.gateway.controller;

import com.gamma.gateway.model.dto.SignStoreResponseMessage;
import com.gamma.gateway.model.dto.email.EmailResponseMessage;
import com.gamma.gateway.service.GatewayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/v1")
public class GatewayController {
    private GatewayService gatewayService;

    @GetMapping("/emailsPreview/{tenantId}/{userId}")
    public EmailResponseMessage getEmailsWithAttachmentsByFilter(@PathVariable Long tenantId, @PathVariable Long userId,
                                                                 @RequestParam(required = false) List<String> attachmentType,
                                                                 @RequestParam(required = false) List<String> sender,
                                                                 @RequestParam LocalDateTime from,
                                                                 @RequestParam LocalDateTime to){
        return gatewayService.getEmailsWithAttachmentsByFilter(tenantId, userId, attachmentType, sender, from, to);

    }

    @GetMapping("/signStoreAttachment/{tenantId}/{userId}/{attachmentId}")
    public SignStoreResponseMessage signStoreEmailAttachment(@PathVariable Long tenantId, @PathVariable Long userId,
                                                             @PathVariable Long attachmentId){
        return gatewayService.signStoreAttachments(tenantId, userId, attachmentId);

    }

    @Autowired
    public void setGatewayService(GatewayService gatewayService) {
        this.gatewayService = gatewayService;
    }
}
