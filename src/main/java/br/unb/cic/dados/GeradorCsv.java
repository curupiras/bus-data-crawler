package br.unb.cic.dados;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import br.unb.cic.crawler.dominio.Arco;

@Component
public class GeradorCsv {

	private static final Logger logger = Logger.getLogger(GeradorCsv.class);

	public void gerar(Arco arco, List<Instancia> instancias) {

		logger.info("Inicio da criação do arquivo csv para o arco " + arco.getNome());

		PrintWriter pw = null;

		try {
			pw = new PrintWriter(new File("dados/" + arco.getNome() + ".csv"));
			StringBuilder sb = new StringBuilder();

			for (Instancia instancia : instancias) {
				sb.append(instancia.getTempoViagem());
				sb.append(',');
				sb.append(instancia.getPeriodoDoDia());
				sb.append(',');
				sb.append(instancia.getDiaDaSemana());
				sb.append(',');
				sb.append(instancia.getVelocidadeMedia());
				sb.append(',');
				sb.append(instancia.getTempoViagem1());
				sb.append(',');
				sb.append(instancia.getTempoViagem2());
				sb.append(',');
				sb.append(instancia.getTempoViagem3());
				sb.append(',');
				sb.append(instancia.getTempoViagem4());
				sb.append(',');
				sb.append(instancia.getTempoViagem5());
				sb.append(',');
				sb.append(instancia.getTempoViagem6());
				sb.append('\n');
			}

			pw.write(sb.toString());

		} catch (FileNotFoundException e) {
			logger.error("Erro ao criar csv para o arco " + arco.getNome(), e);
		} finally {
			pw.close();
		}

		logger.info("Fim da criação do arquivo csv para o arco " + arco.getNome());

	}

}
