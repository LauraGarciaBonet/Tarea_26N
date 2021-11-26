package com.formacionjava.springboot.apirest.models.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.formacionjava.springboot.apirest.models.dao.ClienteDao;
import com.formacionjava.springboot.apirest.models.entity.Cliente;
import com.formacionjava.springboot.apirest.models.entity.Region;

@Service
public class ClienteImpl implements ClienteService {
	
	//Nos evita instanciar una clase
	@Autowired
	private ClienteDao clienteDao;
	
	@Override
	@Transactional(readOnly = true)
	public List<Cliente> findAll(){
	
		return (List<Cliente>) clienteDao.findAll();
	}
	
	@Override
	@Transactional(readOnly = true)
	public Cliente findById(Long id){
	
		return clienteDao.findById(id).orElse(null);
	}
	

	@Override
	@Transactional
	public Cliente save(Cliente cliente) {
		return clienteDao.save(cliente);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		clienteDao.deleteById(id);
	}

	//transactional. no haya sobrecarga y reconocer el get
	@Override
	@Transactional(readOnly = true)
	public List<Region> findAllRegions() {
		return clienteDao.findAllRegions();
	}
	
	
	

}
