package com.formacionjava.springboot.apirest.models.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import com.formacionjava.springboot.apirest.models.entity.Cliente;
import com.formacionjava.springboot.apirest.models.entity.Region;


//CRUD - create, read, add, delete
//heredamos de la clase de librería CrudRepository, que incorpora muchos métodos
public interface ClienteDao extends CrudRepository<Cliente, Long> {

	// from region significa que quiero consultar desde region
	@Query("from Region")
	public List<Region>findAllRegions();
}
