package br.unb.cic.crawler.dominio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LocalizacaoRepository extends JpaRepository<Localizacao, Long> {

	Localizacao findById(long id);

	List<Localizacao> findByLinhaOrderByPrefixoDesc(String linha);

	List<Localizacao> findByPrefixoOrderByDataHoraAsc(int prefixo);

	@Query(value = "select distinct on (datahora) * from localizacao_crawler where prefixo = ?1 order by datahora asc;", nativeQuery = true)
	public List<Localizacao> findByPrefixoOnDistinctDataHoraOrderByDatahoraAsc(int prefixo);

	@Query("select distinct prefixo from Localizacao where linha = ?1")
	List<Integer> findDistinctPrefixoByLinha(String Linha);

}