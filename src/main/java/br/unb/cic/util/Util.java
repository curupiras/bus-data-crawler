package br.unb.cic.util;

import java.util.List;

import org.springframework.stereotype.Component;

import br.unb.cic.crawler.dominio.Localizacao;

@Component
public class Util {

	public double calculaMedia(List<Localizacao> lista) {
		double sum = 0;
		if (!lista.isEmpty()) {
			for (Localizacao item : lista) {
				sum += item.getVelocidade();
			}
			return sum / lista.size();
		}
		return sum;
	}
}
