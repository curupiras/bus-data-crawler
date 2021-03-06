package br.unb.cic.crawler.dominio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ArcoRepository extends JpaRepository<Arco, Long> {

	Arco findById(long id);
	List<Arco> findAllByOrderByIdAsc();

}
