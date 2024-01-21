package com.gamma.gateway;

import com.gamma.gateway.configuration.RestServicesConfig;
import com.gamma.gateway.controller.GatewayController;
import com.gamma.gateway.model.dto.EmailResponseMessage;
import com.gamma.gateway.model.dto.Result;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.Assert;
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
	private GatewayController gatewayController;
	@Mock
	private RestServicesConfig restServicesConfig;

	@ParameterizedTest
	@CsvSource({"1,1,2024-01-01T19:00:00.000,2024-01-20T14:47:55.892,prova.pdf,prova",
			"1,1,2024-01-01T19:05:00.000,2024-01-20T14:47:55.892,prova.pdf,prova"})
	void getEmailsWithAttachmentsByFilterTest(Long tenantId, Long userId, LocalDateTime from, LocalDateTime to,
											  String attachmentType, String sender) {
		String urlTemplate = UriComponentsBuilder.fromHttpUrl("http://localhost:8081/v1/emailsPreview")
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
		mockConfig.put("get-emails-preview", "http://localhost:8081/v1/emailsPreview");

		Mockito.when(restTemplate.getForObject(urlTemplate, EmailResponseMessage.class))
				.thenReturn(mock);
		Mockito.when(restServicesConfig.getEmail())
				.thenReturn(mockConfig);

		EmailResponseMessage emailResponseMessage = gatewayController.getEmailsWithAttachmentsByFilter(tenantId, userId,
				Collections.singletonList(attachmentType), Collections.singletonList(sender), from, to);

		Assert.isTrue(mock.getResult().getResultCode().equals(emailResponseMessage.getResult().getResultCode()),
				"Expected result code differs from actual result code");
	}

}
