package br.unb.cic.dados;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import br.unb.cic.crawler.dominio.ElementoGrafo;
import br.unb.cic.crawler.dominio.Localizacao;
import br.unb.cic.crawler.dominio.LocalizacaoRepository;
import br.unb.cic.crawler.dominio.TempoViagem;
import br.unb.cic.crawler.dominio.TempoViagemRepository;
import br.unb.cic.util.Util;

@Component
public class PreProcessadorDeDados {

	private static final double HORAS_PARA_SEGUNDOS = 3600;
	private static final double MINUTOS_PARA_SEGUNDOS = 60;

	@Value("${crawler.quantidadeDeAmostrasParaCalculoDaVelocidadeMedia}")
	private int quantidadeDeAmostrasParaCalculoDaVelocidadeMedia;

	@Autowired
	private TempoViagemRepository tempoViagemRepository;

	@Autowired
	private LocalizacaoRepository localizacaoRepository;

	@Autowired
	private Util util;

	public List<Instancia> prepararDados(ElementoGrafo elementoGrafo, int quantidadeDeTemposDeViagemAnteriores) {
		List<Instancia> instancias = new ArrayList<>();
		List<TempoViagem> temposDeViagem = tempoViagemRepository
				.findByElementoGrafoOrderByDataHoraDesc(elementoGrafo.getNome());

		for (int i = 0; i < temposDeViagem.size(); i++) {
			TempoViagem tempoDeViagem = temposDeViagem.get(i);

			double tempo = tempoDeViagem.getTempo();
			double periodoDoDia = getPeriodoDoDia(tempoDeViagem);
			int diaDaSemana = getDiaDaSemana(tempoDeViagem);
			List<Double> temposDeViagemAnteriores = getTemposDeViagemAnteriores(temposDeViagem, i,
					quantidadeDeTemposDeViagemAnteriores);
			double velocidadeMedia = getVelocidadeMedia(tempoDeViagem);

			Instancia instancia = new Instancia();
			instancia.setTempoViagem(tempo);
			instancia.setPeriodoDoDia(periodoDoDia);
			instancia.setDiaDaSemana(diaDaSemana);
			instancia.setTemposDeViagemAnteriores(temposDeViagemAnteriores);
			instancia.setVelocidadeMedia(velocidadeMedia);

			instancias.add(instancia);
		}

		return instancias;

	}

	private double getVelocidadeMedia(TempoViagem tempoDeViagem) {
		double velocidadeMedia;
		List<Localizacao> localizacoesAnteriores = getLocalizacoesAnteriores(
				Integer.parseInt(tempoDeViagem.getOnibus()), tempoDeViagem.getDataHora());

		velocidadeMedia = util.calculaMedia(localizacoesAnteriores);
		if (velocidadeMedia == 0) {
			velocidadeMedia = getUltimaLocalizacao(Integer.parseInt(tempoDeViagem.getOnibus()),
					tempoDeViagem.getDataHora()).getVelocidade();
		}
		return velocidadeMedia;
	}

	private List<Localizacao> getLocalizacoesAnteriores(int prefixo, Date dataHora) {

		if (quantidadeDeAmostrasParaCalculoDaVelocidadeMedia == 6) {
			return localizacaoRepository.findTop6ByPrefixoAndDataHoraLessThanOrderByDataHoraDesc(prefixo, dataHora);
		} else if (quantidadeDeAmostrasParaCalculoDaVelocidadeMedia == 5) {
			return localizacaoRepository.findTop5ByPrefixoAndDataHoraLessThanOrderByDataHoraDesc(prefixo, dataHora);
		} else if (quantidadeDeAmostrasParaCalculoDaVelocidadeMedia == 4) {
			return localizacaoRepository.findTop4ByPrefixoAndDataHoraLessThanOrderByDataHoraDesc(prefixo, dataHora);
		} else if (quantidadeDeAmostrasParaCalculoDaVelocidadeMedia == 3) {
			return localizacaoRepository.findTop3ByPrefixoAndDataHoraLessThanOrderByDataHoraDesc(prefixo, dataHora);
		} else if (quantidadeDeAmostrasParaCalculoDaVelocidadeMedia == 2) {
			return localizacaoRepository.findTop2ByPrefixoAndDataHoraLessThanOrderByDataHoraDesc(prefixo, dataHora);
		} else {
			return localizacaoRepository.findTop1ByPrefixoAndDataHoraLessThanOrderByDataHoraDesc(prefixo, dataHora);
		}
	}

	private Localizacao getUltimaLocalizacao(int onibus, Date data) {
		return localizacaoRepository.findTop1ByPrefixoAndDataHoraLessThanEqualOrderByDataHoraDesc(onibus, data);
	}

	private List<Double> getTemposDeViagemAnteriores(List<TempoViagem> temposDeViagem, int i,
			int quantidadeDeTemposDeViagemAnteriores) {
		List<Double> temposDeViagemAnteriores = new ArrayList<>();

		for (int j = 1; j <= quantidadeDeTemposDeViagemAnteriores; j++) {
			if (i + j == temposDeViagem.size()) {
				break;
			}
			temposDeViagemAnteriores.add((double) temposDeViagem.get(i + j).getTempo());
		}

		return temposDeViagemAnteriores;
	}

	private int getDiaDaSemana(TempoViagem tempoDeViagem) {
		Date dataHora = tempoDeViagem.getDataHora();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dataHora);

		return calendar.get(Calendar.DAY_OF_WEEK);
	}

	private double getPeriodoDoDia(TempoViagem tempoViagem) {
		Date dataHora = tempoViagem.getDataHora();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dataHora);

		double horas = calendar.get(Calendar.HOUR_OF_DAY);
		double minutos = calendar.get(Calendar.MINUTE);
		double segundos = calendar.get(Calendar.SECOND);

		return (horas * HORAS_PARA_SEGUNDOS + minutos * MINUTOS_PARA_SEGUNDOS + segundos) / (HORAS_PARA_SEGUNDOS);
	}

}
