package br.unb.cic.geo;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.linearref.LinearLocation;
import com.vividsolutions.jts.linearref.LocationIndexedLine;
import com.vividsolutions.jts.operation.distance.DistanceOp;

import br.unb.cic.crawler.dominio.Arco;
import br.unb.cic.crawler.dominio.Localizacao;

@Component
public class ProcessadorGeo {

	private static final Logger logger = Logger.getLogger(ProcessadorGeo.class);

	private static final double MAXIMA_DISTANCIA = 0.003;

	public void encontrarArco(Point ponto) {

	}

	public Arco arcoMaisProximo(List<Arco> arcos, Localizacao localizacao) {
		Point ponto = localizacao.getGeoPto();
		Arco maisProximo = arcos.get(0);
		double menorDistancia = ponto.distance(maisProximo.getGeoLinha());

		for (Arco arco : arcos) {
			double distancia = ponto.distance(arco.getGeoLinha());
			if (distancia < menorDistancia) {
				maisProximo = arco;
				menorDistancia = distancia;
			}
		}

		if (menorDistancia > MAXIMA_DISTANCIA) {
			logger.debug("Localização " + localizacao.getId() + " muito distante da linha: " + menorDistancia);
			return null;
		}

		return maisProximo;
	}

	public Point getPontoSobreArco(Arco arcoMaisProximo, Localizacao localizacao) {
		Geometry geometry = arcoMaisProximo.getGeoLinha();
		Point ponto = localizacao.getGeoPto();

		Coordinate[] coordinates = DistanceOp.nearestPoints(geometry, ponto);
		Coordinate coordinate = coordinates[0];

		return new Point(coordinate, ponto.getPrecisionModel(), ponto.getSRID());

	}

	public double getPosicaoNoArco(Arco arcoMaisProximo, Point pontoSobreArco) {
	    LocationIndexedLine line = new LocationIndexedLine(arcoMaisProximo.getGeoLinha());
	    LinearLocation l = line.indexOf(pontoSobreArco.getCoordinate());
	    return l.getSegmentFraction();
	}

}
