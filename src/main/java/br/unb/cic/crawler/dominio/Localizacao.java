package br.unb.cic.crawler.dominio;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.annotations.GenericGenerator;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

@Entity
@Table(name = "localizacao_crawler")
public class Localizacao {

	private static final Log logger = LogFactory.getLog(Localizacao.class);

	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private long id;

	@Column(name = "prefixo")
	private int prefixo;

	@Column(name = "datahora")
	private Date dataHora;

	@Column(name = "gps_latitude")
	private String gpsLatitude;

	@Column(name = "gps_longitude")
	private String gpsLongitude;

	@Column(name = "gps_direcao")
	private String gpsDirecao;

	@Column(name = "geo_pto")
	private Point geoPto;

	@Column(name = "linha")
	private String linha;

	@Column(name = "gtfs_linha")
	private String gtfsLinha;

	@Column(name = "gtfs_sentido")
	private boolean gtfsSentido;

	@Column(name = "velocidade")
	private float velocidade;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getPrefixo() {
		return prefixo;
	}

	public void setPrefixo(int prefixo) {
		this.prefixo = prefixo;
	}

	public Date getDataHora() {
		return dataHora;
	}

	public void setDataHora(Object dataHora) {

		if (dataHora.getClass().equals(String.class)) {
			DateFormat fmt = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
			try {
				this.dataHora = new Date(fmt.parse((String) dataHora).getTime());
			} catch (ParseException e) {
				logger.error("Erro ao tentar converter data.");
			}
		} else {
			this.dataHora = (Date) dataHora;
		}
	}

	public String getGpsLatitude() {
		return gpsLatitude;
	}

	public void setGpsLatitude(String gpsLatitude) {
		this.gpsLatitude = gpsLatitude;

		if (this.gpsLongitude != null && geoPto == null) {
			double longitude = Double.parseDouble(this.gpsLongitude.replaceAll(",", "."));
			double latitude = Double.parseDouble(this.gpsLatitude.replaceAll(",", "."));
			GeometryFactory geometryFactory = new GeometryFactory();
			this.geoPto = geometryFactory.createPoint(new Coordinate(longitude, latitude));
		}
	}

	public String getGpsLongitude() {
		return gpsLongitude;
	}

	public void setGpsLongitude(String gpsLongitude) {
		this.gpsLongitude = gpsLongitude;

		if (this.gpsLatitude != null && geoPto == null) {
			double longitude = Double.parseDouble(this.gpsLongitude.replaceAll(",", "."));
			double latitude = Double.parseDouble(this.gpsLatitude.replaceAll(",", "."));
			GeometryFactory geometryFactory = new GeometryFactory();
			this.geoPto = geometryFactory.createPoint(new Coordinate(longitude, latitude));
		}
	}

	public String getGpsDirecao() {
		return gpsDirecao;
	}

	public void setGpsDirecao(String gpsDirecao) {
		this.gpsDirecao = gpsDirecao;
	}

	public Point getGeoPto() {
		return geoPto;
	}

	public void setGeoPto(Point geoPto) {
		this.geoPto = geoPto;
	}

	public String getLinha() {
		return linha;
	}

	public void setLinha(String linha) {
		this.linha = linha;
	}

	public String getGtfsLinha() {
		return gtfsLinha;
	}

	public void setGtfsLinha(String gtfsLinha) {
		this.gtfsLinha = gtfsLinha;
	}

	public boolean isGtfsSentido() {
		return gtfsSentido;
	}

	public void setGtfsSentido(boolean gtfsSentido) {
		this.gtfsSentido = gtfsSentido;
	}

	public double getVelocidade() {
		return velocidade;
	}

	public void setVelocidade(float velocidade) {
		this.velocidade = velocidade;
	}

}
