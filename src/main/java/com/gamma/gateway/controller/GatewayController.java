package com.gamma.gateway.controller;

import com.gamma.gateway.configuration.RestServicesConfig;
import com.gamma.gateway.model.dto.EmailResponseMessage;
import com.gamma.gateway.model.dto.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/v1")
public class GatewayController {
    private static final Logger logger = LoggerFactory.getLogger(GatewayController.class);
    private RestTemplate restTemplate;
    private RestServicesConfig restServicesConfig;

    @GetMapping("/emailsPreview/{tenantId}/{userId}")
    public EmailResponseMessage getEmailsWithAttachmentsByFilter(@PathVariable Long tenantId, @PathVariable Long userId,
                                                                 @RequestParam(required = false) List<String> attachmentType,
                                                                 @RequestParam(required = false) List<String> sender,
                                                                 @RequestParam LocalDateTime from,
                                                                 @RequestParam LocalDateTime to){
        EmailResponseMessage emailResponseMessage;
        logger.info("Request sent by user {} of tenant {}", userId, tenantId);
        logger.info("Request filtering criteria: from {}, to {}, attachment type {}, sender {}", from, to, attachmentType, sender);

        try {
            //Build the request URL
            String urlTemplate = UriComponentsBuilder.fromHttpUrl(restServicesConfig.getEmail().get("get-emails-preview"))
                    .path("/{tenantId}/{userId}")
                    .queryParam("from", from)
                    .queryParam("to", to)
                    .queryParam("attachmentType", attachmentType)
                    .queryParam("sender", sender)
                    .encode()
                    .build(tenantId, userId)
                    .toString();

            logger.info("Requesting resource to URL: {}", urlTemplate);

            emailResponseMessage = restTemplate.getForObject(urlTemplate,
                    EmailResponseMessage.class);
        } catch (Exception e){
          logger.error("Exception while requesting emails: ", e);
          emailResponseMessage = new EmailResponseMessage();
          emailResponseMessage.setResult(new Result("-1", "GENERIC_ERROR"));
        }

        logger.info("Response payload: {}", emailResponseMessage);

        return emailResponseMessage;

    }

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Autowired
    public void setRestServicesConfig(RestServicesConfig restServicesConfig) {
        this.restServicesConfig = restServicesConfig;
    }
}
