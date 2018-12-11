package br.unb.cic.crawler.dominio;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DtoFrota {

	List<String> campos;

	List<List<String>> dados;

	@JsonProperty("Campos")
	public List<String> getCampos() {
		return this.campos;
	}

	public void setCampos(List<String> campos) {
		this.campos = campos;
	}

	@JsonProperty("Dados")
	public List<List<String>> getDados() {
		return this.dados;
	}

	public void setDados(List<List<String>> dados) {
		this.dados = dados;
	}
}
