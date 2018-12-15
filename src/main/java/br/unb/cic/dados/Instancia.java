package br.unb.cic.dados;

import java.util.List;

public class Instancia {

	private double tempoViagem;

	private double periodoDoDia;

	private double diaDaSemana;

	private double tempoViagem1;

	private double tempoViagem2;

	private double tempoViagem3;

	private double tempoViagem4;

	private double tempoViagem5;

	private double tempoViagem6;

	private double velocidadeMedia;

	public double getTempoViagem() {
		return tempoViagem;
	}

	public void setTempoViagem(double tempoViagem) {
		this.tempoViagem = tempoViagem;
	}

	public double getPeriodoDoDia() {
		return periodoDoDia;
	}

	public void setPeriodoDoDia(double periodoDoDia) {
		this.periodoDoDia = periodoDoDia;
	}

	public double getDiaDaSemana() {
		return diaDaSemana;
	}

	public void setDiaDaSemana(double diaDaSemana) {
		this.diaDaSemana = diaDaSemana;
	}

	public double getTempoViagem1() {
		return tempoViagem1;
	}

	public void setTempoViagem1(double tempoViagem1) {
		this.tempoViagem1 = tempoViagem1;
	}

	public double getTempoViagem2() {
		return tempoViagem2;
	}

	public void setTempoViagem2(double tempoViagem2) {
		this.tempoViagem2 = tempoViagem2;
	}

	public double getTempoViagem3() {
		return tempoViagem3;
	}

	public void setTempoViagem3(double tempoViagem3) {
		this.tempoViagem3 = tempoViagem3;
	}

	public double getTempoViagem4() {
		return tempoViagem4;
	}

	public void setTempoViagem4(double tempoViagem4) {
		this.tempoViagem4 = tempoViagem4;
	}

	public double getTempoViagem5() {
		return tempoViagem5;
	}

	public void setTempoViagem5(double tempoViagem5) {
		this.tempoViagem5 = tempoViagem5;
	}

	public double getTempoViagem6() {
		return tempoViagem6;
	}

	public void setTempoViagem6(double tempoViagem6) {
		this.tempoViagem6 = tempoViagem6;
	}

	public double getVelocidadeMedia() {
		return velocidadeMedia;
	}

	public void setVelocidadeMedia(double velocidadeMedia) {
		this.velocidadeMedia = velocidadeMedia;
	}

	public void setTemposDeViagemAnteriores(List<Double> temposDeViagemAnteriores) {
		for (int i = 0; i < temposDeViagemAnteriores.size(); i++) {
			if (i == 0) {
				setTempoViagem1(temposDeViagemAnteriores.get(i));
			} else if (i == 1) {
				setTempoViagem2(temposDeViagemAnteriores.get(i));
			} else if (i == 2) {
				setTempoViagem3(temposDeViagemAnteriores.get(i));
			} else if (i == 3) {
				setTempoViagem4(temposDeViagemAnteriores.get(i));
			} else if (i == 4) {
				setTempoViagem5(temposDeViagemAnteriores.get(i));
			} else if (i == 5) {
				setTempoViagem6(temposDeViagemAnteriores.get(i));
			}
		}

	}

}
