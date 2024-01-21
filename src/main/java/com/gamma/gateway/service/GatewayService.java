package com.gamma.gateway.service;

import com.gamma.gateway.configuration.RestServicesConfig;
import com.gamma.gateway.model.dto.SignStoreResponseMessage;
import com.gamma.gateway.model.dto.email.EmailResponseMessage;
import com.gamma.gateway.model.dto.email.Result;
import com.gamma.gateway.model.dto.store.StoreFileResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class GatewayService {
    private static final Logger logger = LoggerFactory.getLogger(GatewayService.class);
    private RestTemplate restTemplate;
    private RestServicesConfig restServicesConfig;

    public EmailResponseMessage getEmailsWithAttachmentsByFilter(Long tenantId, Long userId, List<String> attachmentType,
                                                                 List<String> sender, LocalDateTime from, LocalDateTime to){
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

    public SignStoreResponseMessage signStoreAttachments(Long tenantId, Long userId, Long attachmentId){
        SignStoreResponseMessage response = new SignStoreResponseMessage();
        try{
            //Get the attachment from email service
            Resource attachmentResource = getEmailAttachment(tenantId, userId, attachmentId);
            //Request attachment signature to signature service
            Resource signedResource = signAttachment(attachmentResource);
            //Store both the original attachment and the signature
            storeFiles(attachmentResource, signedResource);

            response.setResultCode("0");
        }catch (Exception e){
            logger.error("Exception while processing attachment: ", e);
            response.setResultCode("-1");
        }

        return response;
    }

    private Resource getEmailAttachment(Long tenantId, Long userId, Long attachmentId){
        String emailAttachmentServiceUrl = restServicesConfig.getEmail().get("get-emails-attachment");

        String urlTemplate = UriComponentsBuilder.fromHttpUrl(emailAttachmentServiceUrl)
                .path("/{tenantId}/{userId}/{attachmentId}")
                .encode()
                .build(tenantId, userId, attachmentId)
                .toString();

        logger.info("Requesting email attachment: {}", urlTemplate);

        Resource response = restTemplate.getForObject(urlTemplate, Resource.class);

        if(response == null){
            throw new RuntimeException("Attachment not found");
        }

        logger.info("Attachment with name {} retrieved", response.getFilename());

        return response;
    }

    private Resource signAttachment(Resource attachment){
        String signatureServiceUrl = restServicesConfig.getSignature().get("signature-service");
        logger.info("Requesting email attachment signature: {}", signatureServiceUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", attachment);

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

        Resource response = restTemplate.postForObject(signatureServiceUrl, request, Resource.class);

        if(response == null){
            throw new RuntimeException("Signed attachment not found");
        }

        logger.info("Attachment signed {}", response.getFilename());

        return response;
    }

    private void storeFiles(Resource attachment, Resource signedAttachment){
        String storeServiceUrl = restServicesConfig.getStore().get("store-service");
        logger.info("Storing original and signed email attachment: {}", storeServiceUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("files", attachment);
        body.add("files", signedAttachment);

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

        StoreFileResponseMessage response = restTemplate.postForObject(storeServiceUrl, request, StoreFileResponseMessage.class);

        if(response == null || !response.getResultCode().equals("0")){
            throw new RuntimeException("Store service didn't return success message");
        }

        logger.info("Attachments stored successfully");
    }

    @Autowired
    public void setRestServicesConfig(RestServicesConfig restServicesConfig) {
        this.restServicesConfig = restServicesConfig;
    }

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
}
