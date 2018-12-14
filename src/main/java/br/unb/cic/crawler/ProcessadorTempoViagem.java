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

		List<Integer> prefixos = localizacaoRepository.findDistinctPrefixoByLinha(LINHA_CIRCULAR);
		for (int prefixo : prefixos) {
			processarTempoViagem(prefixo);
		}

	}

	// TODO:Tratar casos em que há interrupção do envio de sinal (diferença de
	// tempo muito grande entre timestamp e outro.
	private void processarTempoViagem(int prefixo) {
		logger.info("Iniciando processamento para o prefixo " + prefixo);
		List<Localizacao> localizacoes = localizacaoRepository
				.findByPrefixoAndLinhaOnDistinctDataHoraOrderByDatahoraAsc(prefixo, LINHA_CIRCULAR);
		Localizacao localizacaoAnterior = null;
		double posicaoInicial = 0;
		double posicaoNoArcoAnterior = 0;
		Arco arcoAnterior = null;
		TempoViagem tempoViagem = new TempoViagem();

		if (localizacoes.size() < 2) {
			logger.warn("Há menos que duas localizações para o prefixo: " + prefixo);
			return;
		}

		for (Localizacao localizacao : localizacoes) {

			// Arco mais proximo do ponto da localizacao
			Arco arcoMaisProximo = processadorGeo.arcoMaisProximo(arcos, localizacao);

			// Se a localizacao estiver muito longe de qualquer arco continue.
			if (arcoMaisProximo == null) {
				continue;
			}

			Point pontoSobreArco = processadorGeo.getPontoSobreArco(arcoMaisProximo, localizacao);
			double posicaoNoArco = processadorGeo.getPosicaoNoArco(arcoMaisProximo, pontoSobreArco);

			if (tempoViagem.getElementoGrafo() == null) {
				// Se primeira localizacao, armazena dados no TempoViagem
				tempoViagem.setDataHora(localizacao.getDataHora());
				tempoViagem.setElementoGrafo(arcoMaisProximo.getNome());
				tempoViagem.setOnibus(Integer.toString(localizacao.getPrefixo()));
				posicaoInicial = posicaoNoArco;
			} else if (tempoViagem.getElementoGrafo().equals(arcoMaisProximo.getNome())) {
				// Se arco igual da localizacao anterior, acumula o tempo gasto
				// no TempoViagem
				long horaInicial = tempoViagem.getDataHora().getTime();
				long horaFinal = localizacao.getDataHora().getTime();
				long milisegundos = horaFinal - horaInicial;
				int segundos = (int) milisegundos / 1000;
				tempoViagem.setTempo(segundos);
			} else {

				List<Arco> arcosAtravessados = new ArrayList<Arco>();
				Arco arco = (Arco) arcoAnterior.getProximo().getProximo();

				while (arco != arcoMaisProximo) {
					arcosAtravessados.add(arco);
					arco = (Arco) arco.getProximo().getProximo();
				}

				// Se arco diferente do arco anterior
				long horaAnterior = localizacaoAnterior.getDataHora().getTime();
				long horaAtual = localizacao.getDataHora().getTime();
				long milisegundos = horaAtual - horaAnterior;
				int segundos = (int) milisegundos / 1000;

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
				double tempoGastoArcoAnterior = distanciaArcoAnterior / distanciaTotal * segundos;

				// Acumula no TempoViagem o tempo gasto no arco anterior
				tempoViagem.setTempo(tempoViagem.getTempo() + tempoGastoArcoAnterior);
				if (posicaoInicial != 0) {
					// Se foi o primeiro arco computado estimar o tempo gasto
					// antes do inicio das medidas
					double tempoComputado = tempoViagem.getTempo();
					double tempoFaltante = (tempoComputado * posicaoInicial) / (1 - posicaoInicial);
					tempoViagem.setTempo(tempoComputado + tempoFaltante);
					posicaoInicial = 0;
				}
				// Salva o TempoViagem computado
				TempoViagemRepository.save(tempoViagem);

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
				double tempoGastoArcoAtual = (distanciaArcoAtual / (distanciaArcoAnterior + distanciaArcoAtual))
						* segundos;
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

		// Se for o último arco computado estimar o tempo que seria gasto para
		// atravessá-lo
		double tempoComputado = tempoViagem.getTempo();
		double tempoFaltante = (tempoComputado * (1 - posicaoInicial)) / (posicaoInicial);
		tempoViagem.setTempo(tempoComputado + tempoFaltante);
		TempoViagemRepository.save(tempoViagem);
		posicaoInicial = 0;

	}

}
