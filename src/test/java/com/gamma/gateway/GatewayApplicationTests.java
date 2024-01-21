package com.gamma.gateway;

import com.gamma.gateway.configuration.RestServicesConfig;
import com.gamma.gateway.model.dto.SignStoreResponseMessage;
import com.gamma.gateway.model.dto.email.EmailResponseMessage;
import com.gamma.gateway.model.dto.email.Result;
import com.gamma.gateway.model.dto.store.StoreFileResponseMessage;
import com.gamma.gateway.service.GatewayService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class GatewayApplicationTests {
	@Mock
	private RestTemplate restTemplate;
	@InjectMocks
	private GatewayService gatewayService;
	@Mock
	private RestServicesConfig restServicesConfig;

	@ParameterizedTest
	@CsvSource({"1,1,2024-01-01T19:00:00.000,2024-01-20T14:47:55.892,prova.pdf,prova",
			"1,1,2024-01-01T19:05:00.000,2024-01-20T14:47:55.892,prova.pdf,prova"})
	void getEmailsWithAttachmentsByFilterTest(Long tenantId, Long userId, LocalDateTime from, LocalDateTime to,
											  String attachmentType, String sender) {
		String urlTemplate = UriComponentsBuilder.fromHttpUrl("http://localhost:8081/v1/emails")
				.path("/{tenantId}/{userId}")
				.queryParam("from", from)
				.queryParam("to", to)
				.queryParam("attachmentType", attachmentType)
				.queryParam("sender", sender)
				.encode()
				.build(tenantId, userId)
				.toString();

		//Mock response object
		EmailResponseMessage mock = new EmailResponseMessage();
		mock.setResult(new Result("0", "SUCCESS"));
		//Mock utility classes needed by the rest controller
		Map<String, String> mockConfig = new HashMap<>();
		mockConfig.put("get-emails-preview", "http://localhost:8081/v1/emails");

		Mockito.when(restTemplate.getForObject(urlTemplate, EmailResponseMessage.class))
				.thenReturn(mock);
		Mockito.when(restServicesConfig.getEmail())
				.thenReturn(mockConfig);

		EmailResponseMessage emailResponseMessage = gatewayService.getEmailsWithAttachmentsByFilter(tenantId, userId,
				Collections.singletonList(attachmentType), Collections.singletonList(sender), from, to);

		Assert.isTrue(mock.getResult().getResultCode().equals(emailResponseMessage.getResult().getResultCode()),
				"Expected result code differs from actual result code");
	}

	@ParameterizedTest
	@CsvSource({"1,1,1"})
	void getEmailsWithAttachmentsByFilterTest(Long tenantId, Long userId, Long attachmentId) {
		//Mock resource
		Resource mockResource = new ByteArrayResource("MOCK".getBytes());
		//Mock storage service response message
		StoreFileResponseMessage mockStoreResponse = new StoreFileResponseMessage("0");
		//Mock utility classes needed by the rest controller
		Map<String, String> mockConfig = new HashMap<>();
		mockConfig.put("get-emails-attachment", "http://localhost:8080/v1/attachment");
		mockConfig.put("signature-service", "http://localhost:8082/v1/sign");
		mockConfig.put("store-service", "http://localhost:8083/v1/store");
		//Mock signature request
		HttpHeaders siggnatureHeaders = new HttpHeaders();
		siggnatureHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
		MultiValueMap<String, Object> signatureBody = new LinkedMultiValueMap<>();
		signatureBody.add("file", mockResource);
		HttpEntity<MultiValueMap<String, Object>> signatureRequest = new HttpEntity<>(signatureBody, siggnatureHeaders);
		//Mock store request
		HttpHeaders storeHeaders = new HttpHeaders();
		storeHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
		MultiValueMap<String, Object> storeBody = new LinkedMultiValueMap<>();
		storeBody.add("files", mockResource);
		storeBody.add("files", mockResource);
		HttpEntity<MultiValueMap<String, Object>> storeRequest = new HttpEntity<>(storeBody, storeHeaders);

		Mockito.when(restServicesConfig.getEmail())
				.thenReturn(mockConfig);
		Mockito.when(restServicesConfig.getStore())
				.thenReturn(mockConfig);
		Mockito.when(restServicesConfig.getSignature())
				.thenReturn(mockConfig);

		Mockito.when(restTemplate.getForObject("http://localhost:8080/v1/attachment"+"/"+tenantId+"/"+userId+"/"+attachmentId, Resource.class))
				.thenReturn(mockResource);
		Mockito.when(restTemplate.postForObject("http://localhost:8082/v1/sign", signatureRequest, Resource.class))
				.thenReturn(mockResource);
		Mockito.when(restTemplate.postForObject("http://localhost:8083/v1/store", storeRequest, StoreFileResponseMessage.class))
				.thenReturn(mockStoreResponse);

		SignStoreResponseMessage emailResponseMessage = gatewayService.signStoreAttachments(tenantId, userId, attachmentId);
	}

}
