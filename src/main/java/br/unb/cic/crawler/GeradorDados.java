package br.unb.cic.crawler;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.unb.cic.crawler.dominio.Arco;
import br.unb.cic.crawler.dominio.ArcoRepository;
import br.unb.cic.dados.GeradorCsv;
import br.unb.cic.dados.Instancia;
import br.unb.cic.dados.PreProcessadorDeDados;

@Component
public class GeradorDados {

	@Value("${crawler.quantidadeDeTemposDeViagemAnteriores}")
	private int quantidadeDeTemposDeViagemAnteriores;

	@Autowired
	private ArcoRepository arcoRepository;

	@Autowired
	private PreProcessadorDeDados preProcessadorDeDados;

	@Autowired
	private GeradorCsv geradorCsv;

	@Scheduled(initialDelay = 0, fixedRate = 500000)
	public void gerarDados() {

		List<Arco> arcos = arcoRepository.findAllByOrderByIdAsc();

		for (Arco arco : arcos) {
			List<Instancia> instancias = preProcessadorDeDados.prepararDados(arco,
					quantidadeDeTemposDeViagemAnteriores);
			geradorCsv.gerar(arco, instancias);
		}

	}

}
