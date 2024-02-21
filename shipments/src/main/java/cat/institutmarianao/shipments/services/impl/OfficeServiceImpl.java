package cat.institutmarianao.shipments.services.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import cat.institutmarianao.shipments.model.Office;
import cat.institutmarianao.shipments.services.OfficeService;

@Service
@PropertySource("classpath:application.properties")
public class OfficeServiceImpl implements OfficeService {

	@Value("${webService.host}")
	private String webServiceHost;

	@Value("${webService.port}")
	private String webServicePort;

	@Autowired
	private RestTemplate restTemplate;

	@Override
	public Office getById(Long id) {
		final String baseUri = webServiceHost + ":" + webServicePort + "/office/get/by/{id}";

		UriComponentsBuilder uriTemplate = UriComponentsBuilder.fromHttpUrl(baseUri);

		Map<String, Long> uriVariables = new HashMap<>();
		uriVariables.put("id", id);

		Office response = restTemplate.getForObject(uriTemplate.buildAndExpand(uriVariables).toUriString(),
				Office.class);
		return response;
	}

	@Override
	public List<Office> getAllOffices() {
		final String uri = webServiceHost + ":" + webServicePort + "/offices/find/all";

		ResponseEntity<Office[]> response = restTemplate.getForEntity(uri, Office[].class);
		return Arrays.asList(response.getBody());
	}
}
