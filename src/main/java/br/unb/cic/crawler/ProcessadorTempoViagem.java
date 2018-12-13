package br.unb.cic.crawler;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.vividsolutions.jts.geom.Point;

import br.unb.cic.crawler.dominio.Arco;
import br.unb.cic.crawler.dominio.ArcoRepository;
import br.unb.cic.crawler.dominio.Localizacao;
import br.unb.cic.crawler.dominio.LocalizacaoRepository;
import br.unb.cic.crawler.dominio.No;
import br.unb.cic.crawler.dominio.NoRepository;
import br.unb.cic.geo.ProcessadorGeo;

@Component
public class ProcessadorTempoViagem {

	private static final Logger logger = Logger.getLogger(ProcessadorTempoViagem.class);
	private static final String LINHA_CIRCULAR = "0.031";
	
	private List<Arco> arcos;
	private List<No> nos;

	@Autowired
	private LocalizacaoRepository localizacaoRepository;
	
	@Autowired
	private ArcoRepository arcoRepository;
	
	@Autowired
	private NoRepository noRepository;
	
	@Autowired
	private ProcessadorGeo processadorGeo;
	
	@PostConstruct
    public void init() {
		this.arcos = arcoRepository.findAllByOrderByIdAsc();
		this.nos = noRepository.findAllByOrderByIdAsc();

		for (int i = 0; i < nos.size(); i++) {
			nos.get(i).setProximo(arcos.get(i));
		}

		for (int i = 0; i < arcos.size(); i++) {
			arcos.get(i).setAnterior(nos.get(i));
		}

		for (int i = 1; i < nos.size(); i++) {
			nos.get(i).setAnterior(arcos.get(i - 1));
		}

		for (int i = 0; i < arcos.size() - 1; i++) {
			arcos.get(i).setProximo(nos.get(i + 1));
		}

		arcos.get(arcos.size() - 1).setProximo(nos.get(0));
		nos.get(0).setAnterior(arcos.get(arcos.size() - 1));
    }

	@Scheduled(initialDelay = 0, fixedRate = 500000)
	public void processarTempoViagem() {

		List<Integer> prefixos = localizacaoRepository.findDistinctPrefixoByLinha(LINHA_CIRCULAR);
		for (int prefixo : prefixos) {
			processarTempoViagem(prefixo);
		}

	}

	private void processarTempoViagem(int prefixo) {
		List<Localizacao> localizacoes = localizacaoRepository.findByPrefixoOnDistinctDataHoraOrderByDatahoraAsc(prefixo);
		
		for (Localizacao localizacao : localizacoes) {
			
			Arco arcoMaisProximo = processadorGeo.arcoMaisProximo(arcos, localizacao);
			Point pontoSobreArco = processadorGeo.getPontoSobreArco(arcoMaisProximo, localizacao);
			
			logger.info(pontoSobreArco);
		}
		
		
		
	}

}
