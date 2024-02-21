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

import cat.institutmarianao.shipments.model.Company;
import cat.institutmarianao.shipments.services.CompanyService;

@Service
@PropertySource("classpath:application.properties")
public class CompanyServiceImpl implements CompanyService {

	@Value("${webService.host}")
	private String webServiceHost;

	@Value("${webService.port}")
	private String webServicePort;

	@Autowired
	private RestTemplate restTemplate;

	@Override
	public List<Company> getAllCompanies() {
		final String uri = webServiceHost + ":" + webServicePort + "/companies/find/all";

		ResponseEntity<Company[]> response = restTemplate.getForEntity(uri, Company[].class);
		return Arrays.asList(response.getBody());
	}

	@Override
	public Company getById(Long id) {
		final String baseUri = webServiceHost + ":" + webServicePort + "/company/get/by/{id}";

		UriComponentsBuilder uriTemplate = UriComponentsBuilder.fromHttpUrl(baseUri);

		Map<String, Long> uriVariables = new HashMap<>();
		uriVariables.put("id", id);

		Company response = restTemplate.getForObject(uriTemplate.buildAndExpand(uriVariables).toUriString(),
				Company.class);
		return response;
	}
}
