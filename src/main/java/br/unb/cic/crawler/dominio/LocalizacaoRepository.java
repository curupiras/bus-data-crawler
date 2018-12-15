package br.unb.cic.crawler.dominio;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LocalizacaoRepository extends JpaRepository<Localizacao, Long> {

	Localizacao findById(long id);

	List<Localizacao> findByLinhaOrderByPrefixoDesc(String linha);

	List<Localizacao> findByPrefixoOrderByDataHoraAsc(int prefixo);

	List<Localizacao> findTop6ByPrefixoAndDataHoraLessThanOrderByDataHoraDesc(int prefixo, Date dataHora);

	List<Localizacao> findTop5ByPrefixoAndDataHoraLessThanOrderByDataHoraDesc(int prefixo, Date dataHora);

	List<Localizacao> findTop4ByPrefixoAndDataHoraLessThanOrderByDataHoraDesc(int prefixo, Date dataHora);

	List<Localizacao> findTop3ByPrefixoAndDataHoraLessThanOrderByDataHoraDesc(int prefixo, Date dataHora);

	List<Localizacao> findTop2ByPrefixoAndDataHoraLessThanOrderByDataHoraDesc(int prefixo, Date dataHora);

	List<Localizacao> findTop1ByPrefixoAndDataHoraLessThanOrderByDataHoraDesc(int prefixo, Date dataHora);

	Localizacao findTop1ByPrefixoAndDataHoraLessThanEqualOrderByDataHoraDesc(int prefixo, Date dataHora);

	@Query(value = "select distinct on (datahora) * from localizacao_crawler where prefixo = ?1 and linha = ?2 order by datahora asc;", nativeQuery = true)
	public List<Localizacao> findByPrefixoAndLinhaOnDistinctDataHoraOrderByDatahoraAsc(int prefixo, String linha);

	@Query("select distinct prefixo from Localizacao where linha = ?1")
	List<Integer> findDistinctPrefixoByLinha(String Linha);

}