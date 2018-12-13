package br.unb.cic.crawler.dominio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NoRepository extends JpaRepository<No, Long> {

	No findById(long id);

	List<No> findAllByOrderByIdAsc();

}
