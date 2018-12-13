package br.unb.cic.geo;

import java.util.List;

import org.springframework.stereotype.Component;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequences;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.operation.distance.DistanceOp;
import com.vividsolutions.jts.operation.overlay.snap.GeometrySnapper;
import com.vividsolutions.jts.operation.overlay.snap.LineStringSnapper;

import br.unb.cic.crawler.dominio.Arco;
import br.unb.cic.crawler.dominio.Localizacao;

@Component
public class ProcessadorGeo {
   
	public void encontrarArco(Point ponto){
		
	}
	
	public Arco arcoMaisProximo(List<Arco> arcos, Localizacao localizacao){
		Point ponto = localizacao.getGeoPto();
		Arco maisProximo = arcos.get(0);
		double menorDistancia = ponto.distance(maisProximo.getGeoLinha());
		
		for (Arco arco : arcos) {
			double distancia = ponto.distance(arco.getGeoLinha());
			if( distancia < menorDistancia){
				maisProximo = arco;
				menorDistancia = distancia; 
			}
		}
		
		return maisProximo;
	}

	public Point getPontoSobreArco(Arco arcoMaisProximo, Localizacao localizacao) {
		Geometry geometry = arcoMaisProximo.getGeoLinha();
		Point ponto = localizacao.getGeoPto();
		
		Coordinate[] coordinates = DistanceOp.nearestPoints(geometry, ponto);
		Coordinate coordinate = coordinates[0];
		
		GeometryFactory geometryFactory = new GeometryFactory(ponto.getPrecisionModel(), ponto.getSRID());
		return new Point(coordinate, ponto.getPrecisionModel(), ponto.getSRID());
		
	}

}


