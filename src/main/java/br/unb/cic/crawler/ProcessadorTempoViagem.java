package br.unb.cic.crawler;

import java.util.ArrayList;
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
import br.unb.cic.crawler.dominio.TempoViagem;
import br.unb.cic.crawler.dominio.TempoViagemRepository;
import br.unb.cic.geo.ProcessadorGeo;

@Component
public class ProcessadorTempoViagem {

	private static final Logger logger = Logger.getLogger(ProcessadorTempoViagem.class);
	private static final String LINHA_CIRCULAR = "0.031";
	private static final int TRINTA_MINUTOS = 60 * 30;

	private List<Arco> arcos;
	private List<No> nos;

	@Autowired
	private LocalizacaoRepository localizacaoRepository;

	@Autowired
	private ArcoRepository arcoRepository;

	@Autowired
	private NoRepository noRepository;

	@Autowired
	private TempoViagemRepository TempoViagemRepository;

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

		logger.info("Iniciando processamento de tempo de viagem");
		List<Integer> prefixos = localizacaoRepository.findDistinctPrefixoByLinha(LINHA_CIRCULAR);
		for (int prefixo : prefixos) {
			processarTempoViagem(prefixo);
		}
		logger.info("Finalizado processamento de tempo de viagem");

	}

	// TODO:Tratar casos em que há interrupção do envio de sinal (diferença de
	// tempo muito grande entre timestamp e outro.
	private void processarTempoViagem(int prefixo) {
		logger.info("Iniciando processamento para o prefixo " + prefixo);
		List<Localizacao> localizacoes = localizacaoRepository
				.findByPrefixoAndLinhaOnDistinctDataHoraOrderByDatahoraAsc(prefixo, LINHA_CIRCULAR);

		// Inicializa variáveis
		Localizacao localizacaoAnterior = null;
		double posicaoNoArcoAnterior = 0;
		Arco arcoAnterior = null;
		TempoViagem tempoViagem = null;

		if (localizacoes.size() < 2) {
			logger.warn("Há menos que duas localizações para o prefixo: " + prefixo);
			return;
		}

		for (Localizacao localizacao : localizacoes) {

			// List<Integer> lista = new ArrayList<>();
			// lista.add(211365);
			// lista.add(211420);
			// lista.add(211475);
			// lista.add(211530);
			// lista.add(211585);
			long id = localizacao.getId();
			if (id == 211365) {
				logger.info("teste");
			}

			// Arco mais proximo do ponto da localizacao
			Arco arcoMaisProximo = processadorGeo.arcoMaisProximo(arcos, localizacao);

			// Se a localizacao estiver muito longe continue.
			if (arcoMaisProximo == null) {
				continue;
			}

			// Trata o problema de superposição dos arcos 49 e 62
			if (arcoMaisProximo.getId() == 49 || arcoMaisProximo.getId() == 62) {
				long idAnterior = arcoAnterior.getId();
				if (idAnterior <= 49) {
					arcoMaisProximo = arcos.get(48);
				} else {
					arcoMaisProximo = arcos.get(61);
				}
			}

			Point pontoSobreArco = processadorGeo.getPontoSobreArco(arcoMaisProximo, localizacao);
			double posicaoNoArco = processadorGeo.getPosicaoNoArco(arcoMaisProximo, pontoSobreArco);

			if (tempoViagem == null) {

				if (localizacaoAnterior == null || arcoAnterior == arcoMaisProximo) {
					localizacaoAnterior = localizacao;
					posicaoNoArcoAnterior = posicaoNoArco;
					arcoAnterior = arcoMaisProximo;
					continue;
				}
			}

			// Trata Problema de o ônibus voltar para arco anterior
			if (arcoMaisProximo.getId() == arcoAnterior.getId() - 1) {
				// Inicializa variáveis
				localizacaoAnterior = null;
				posicaoNoArcoAnterior = 0;
				arcoAnterior = null;
				tempoViagem = null;
				continue;
			}

			long horaAnterior = localizacaoAnterior.getDataHora().getTime();
			long horaAtual = localizacao.getDataHora().getTime();
			long milisegundos = horaAtual - horaAnterior;
			int segundos = (int) milisegundos / 1000;

			if (segundos > TRINTA_MINUTOS) {
				// Inicializa variáveis
				localizacaoAnterior = null;
				posicaoNoArcoAnterior = 0;
				arcoAnterior = null;
				tempoViagem = null;
				continue;
			}

			if (arcoAnterior == arcoMaisProximo) {
				// Se arco igual da localizacao anterior, acumula o tempo
				tempoViagem.setTempo(tempoViagem.getTempo() + segundos);
			} else {

				List<Arco> arcosAtravessados = new ArrayList<Arco>();
				Arco arco = (Arco) arcoAnterior.getProximo().getProximo();

				while (arco != arcoMaisProximo) {
					arcosAtravessados.add(arco);
					arco = (Arco) arco.getProximo().getProximo();
				}

				// Caucula distancia percorrida no arco atual e o que falta dos
				// anteriores
				double distanciaArcoAnterior = (1 - posicaoNoArcoAnterior) * arcoAnterior.getTamanho();
				double distanciaArcosAtravessados = 0;
				for (Arco arcoAtravessado : arcosAtravessados) {
					distanciaArcosAtravessados = distanciaArcosAtravessados + arcoAtravessado.getTamanho();
				}
				double distanciaArcoAtual = posicaoNoArco * arcoMaisProximo.getTamanho();
				double distanciaTotal = distanciaArcoAnterior + distanciaArcosAtravessados + distanciaArcoAtual;

				// Estima o tempo gasto no arco anterior
				double tempoGastoArcoAnterior = (distanciaArcoAnterior / distanciaTotal) * segundos;

				if (tempoViagem != null) {
					// Acumula no TempoViagem o tempo gasto no arco anterior
					tempoViagem.setTempo(tempoViagem.getTempo() + tempoGastoArcoAnterior);

					// Salva o TempoViagem computado
					TempoViagemRepository.save(tempoViagem);
				}

				for (Arco arcoAtravessado : arcosAtravessados) {
					// Estima o tempo gasto no arco atravessado
					double tempoGastoArcoAtravessado = arcoAtravessado.getTamanho() / distanciaTotal * segundos;

					// Cria novo TempoViagem e salva
					tempoViagem = new TempoViagem();
					tempoViagem.setDataHora(localizacao.getDataHora());
					tempoViagem.setElementoGrafo(arcoAtravessado.getNome());
					tempoViagem.setOnibus(Integer.toString(localizacao.getPrefixo()));
					tempoViagem.setTempo(tempoGastoArcoAtravessado);
					TempoViagemRepository.save(tempoViagem);
				}

				// Cria novo TempoViagem e adiciona o tempo gasto no arco atual
				tempoViagem = new TempoViagem();
				double tempoGastoArcoAtual = (distanciaArcoAtual / distanciaTotal) * segundos;
				tempoViagem.setDataHora(localizacao.getDataHora());
				tempoViagem.setElementoGrafo(arcoMaisProximo.getNome());
				tempoViagem.setOnibus(Integer.toString(localizacao.getPrefixo()));
				tempoViagem.setTempo(tempoGastoArcoAtual);

			}

			// Armazena os dados da localizacao para a proxima iteracao
			localizacaoAnterior = localizacao;
			posicaoNoArcoAnterior = posicaoNoArco;
			arcoAnterior = arcoMaisProximo;
		}

	}

}
