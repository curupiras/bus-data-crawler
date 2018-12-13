package br.unb.cic.crawler.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "tempo_viagem_crawler")
public class TempoViagem {

	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private long id;

	public TempoViagem(Date dataHora, String elementoGrafo, double tempo, String onibus) {
		super();
		this.dataHora = dataHora;
		this.elementoGrafo = elementoGrafo;
		this.tempo = tempo;
		this.onibus = onibus;
	}

	public TempoViagem() {
	}

	@Column(name = "datahora")
	private Date dataHora;

	@Column(name = "elemento_grafo")
	private String elementoGrafo;

	@Column(name = "tempo")
	private double tempo;

	@Column(name = "onibus")
	private String onibus;

	@Column(name = "processado")
	private boolean processado;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getDataHora() {
		return dataHora;
	}

	public void setDataHora(Date dataHora) {
		this.dataHora = dataHora;
	}

	public String getElementoGrafo() {
		return elementoGrafo;
	}

	public void setElementoGrafo(String nome) {
		this.elementoGrafo = nome;
	}

	public double getTempo() {
		return tempo;
	}

	public void setTempo(double tempo) {
		this.tempo = tempo;
	}

	public String getOnibus() {
		return onibus;
	}

	public void setOnibus(String onibus) {
		this.onibus = onibus;
	}

	public boolean isProcessado() {
		return processado;
	}

	public void setProcessado(boolean processado) {
		this.processado = processado;
	}

}
