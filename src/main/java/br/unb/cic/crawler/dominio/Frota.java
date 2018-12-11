package br.unb.cic.crawler.dominio;

import java.util.ArrayList;
import java.util.List;

public class Frota {

	private List<Localizacao> localizacoes;

	public Frota(DtoFrota dtoFrota) {
		this.localizacoes = new ArrayList<Localizacao>();
		List<List<String>> dados = dtoFrota.getDados();
		for (List<String> dadosLocalizacao : dados) {
			Localizacao localizacao = new Localizacao();
			localizacao.setPrefixo(Integer.parseInt(dadosLocalizacao.get(0)));
			localizacao.setDataHora(dadosLocalizacao.get(1));
			localizacao.setGpsLatitude(dadosLocalizacao.get(2));
			localizacao.setGpsLongitude(dadosLocalizacao.get(3));
			localizacao.setGpsDirecao(dadosLocalizacao.get(4));
			localizacao.setLinha(dadosLocalizacao.get(5));
			localizacao.setGtfsLinha(dadosLocalizacao.get(6));
			localizacao.setGtfsSentido(Boolean.parseBoolean(dadosLocalizacao.get(7)));
			localizacao.setVelocidade(Float.parseFloat(dadosLocalizacao.get(8)));
			this.localizacoes.add(localizacao);
		}
	}

	public List<Localizacao> getFrota() {
		return this.localizacoes;
	}

	public void setFrota(List<Localizacao> frota) {
		this.localizacoes = frota;
	}

}
