package br.unb.cic.crawler;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import br.unb.cic.crawler.dominio.DtoFrota;
import br.unb.cic.crawler.dominio.Frota;

@Component
public class ClienteRest {

	@Autowired
	RestTemplate restTemplate;

	// @Autowired
	// LocalizacaoRepository repository;
	
	@Value("${crawler.url.piracicabana}")
	private String url;

	private static final Logger logger = Logger.getLogger(ClienteRest.class);

	@Scheduled(initialDelay = 0, fixedRate = 5000)
	public void scheduledTask() {
		
		DtoFrota dtoFrota = restTemplate.getForObject(url, DtoFrota.class);
		Frota frota = new Frota(dtoFrota);		
//		repository.save(dtoFrota.getFrota());
	}

}
