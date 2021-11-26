package com.formacionjava.springboot.apirest.controllers;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.formacionjava.springboot.apirest.models.entity.Cliente;
import com.formacionjava.springboot.apirest.models.entity.Region;
import com.formacionjava.springboot.apirest.models.service.ClienteService;

import io.swagger.annotations.Api;

//CREAMOS EL CONTROLADOR

//De qué manera lo mostraremos (especificar url)

@RestController
@Api(tags = "Documentation Api SpringBoot")
//url enrutador o cabecera 
@RequestMapping("/api")
public class ClienteRestController {
	
	@Autowired
	private ClienteService clienteService;
	
	//Petición GET
	@GetMapping("/clientes")
	public List<Cliente> index() {
		return clienteService.findAll();
	}
	
	/*//petición GET2 (cliente específico por id)
	@GetMapping("/clientes/{id}")
	public Cliente show(@PathVariable Long id) {
		return clienteService.findById(id);
	}*/
	
	@GetMapping("/clientes/{id}")
	public ResponseEntity<?>show(@PathVariable Long id){
		Cliente cliente= null;
		Map<String,Object> response= new HashMap<>();
		
		//El try esta para escuchar mas errores del servidor o bbdd
		try {
			cliente= clienteService.findById(id);
		} catch (DataAccessException e) {
			response.put("mensaje","Error al realizar consulta en base de datos");
			response.put("error", e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if(cliente==null) {
			response.put("mensaje", "El cliente ID: ".concat(id.toString().concat(" no existe en la base de datos")));
			return new ResponseEntity<Map<String, Object>>(response,HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Cliente>(cliente,HttpStatus.OK);
	}
	
	@GetMapping("/uploads/img/{nombreFoto:.+}")
	public ResponseEntity<Resource> verFoto(@PathVariable String nombreFoto){
		Path rutaArchivo= Paths.get("uploads").resolve(nombreFoto).toAbsolutePath();
		
		Resource recurso = null;
		
		try {
			recurso=new UrlResource(rutaArchivo.toUri());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		if(!recurso.exists() && !recurso.isReadable()) {
			throw new RuntimeException("Error no se puede cargar la imagen " + nombreFoto);
		}
		
		HttpHeaders cabecera = new HttpHeaders();
		cabecera.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\""+recurso.getFilename()+"\"");
		return new ResponseEntity<Resource>(recurso, cabecera, HttpStatus.OK);
	}
	
	//POST
	/*@PostMapping("/clientes")
	//recibir respuesta 
	@ResponseStatus(HttpStatus.CREATED)
	public Cliente create(@RequestBody Cliente cliente) {
		return clienteService.save(cliente);
	}*/
	
	@PostMapping("/clientes")
	public ResponseEntity<?>create(@RequestBody Cliente cliente){
		Cliente clienteNew=null;
		Map<String, Object> response= new HashMap<>();
		
		try {
			clienteNew= clienteService.save(cliente);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar insert en base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje","El cliente ha sido creado con éxito!");
		response.put("cliente", clienteNew);
		return new ResponseEntity<Map<String, Object>>(response,HttpStatus.CREATED);
	}
	
	@PostMapping("/clientes/upload")
	public ResponseEntity<?>upload(@RequestParam("archivo") MultipartFile archivo, @RequestParam("id") Long id){
		Map<String, Object> response= new HashMap<>();
		
		Cliente cliente= clienteService.findById(id);
		
		if(!archivo.isEmpty()) {
			String nombreArchivo= UUID.randomUUID().toString()+"_"+archivo.getOriginalFilename().replace(" ","");
			Path rutaArchivo= Paths.get("uploads").resolve(nombreArchivo).toAbsolutePath();
			
			try {
				Files.copy(archivo.getInputStream(),rutaArchivo);
			} catch (IOException e) {
				//Todo Auto-generated catch block
				response.put("mensaje","Error al subir la imagen del cliente");
				response.put("error", e.getMessage().concat(": ").concat(e.getCause().getMessage()));
				return new ResponseEntity<Map<String, Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
			}
			
			String nombreFotoAnterior= cliente.getImagen();
			
			if(nombreFotoAnterior !=null && nombreFotoAnterior.length()>0) {
			Path rutaFotoAnterior= Paths.get("uploads").resolve(nombreFotoAnterior).toAbsolutePath();
			File archivoFotoAnterior= rutaFotoAnterior.toFile();
			
			if(archivoFotoAnterior.exists() && archivoFotoAnterior.canRead()) {
				archivoFotoAnterior.delete();
			}
		}
			cliente.setImagen(nombreArchivo);
			
			clienteService.save(cliente);
			
			response.put("cliente", cliente);
			response.put("mensaje","Has subido correctamente la imagen: " + nombreArchivo);
		}
		return new ResponseEntity<Map<String, Object>>(response,HttpStatus.CREATED);
	}
	
	//PUT
	/*@PutMapping("/clientes/{id}")
	@ResponseStatus(HttpStatus.CREATED)
	public Cliente create(@RequestBody Cliente cliente, @PathVariable Long id) {
		Cliente clienteUpdate = clienteService.findById(id);
		
		clienteUpdate.setApellido(cliente.getApellido());
		clienteUpdate.setNombre(cliente.getNombre());
		clienteUpdate.setEmail(cliente.getEmail());
		
		return clienteService.save(clienteUpdate);
		
	}*/
	
	@PutMapping("/clientes/{id}")
	public ResponseEntity<?> update(@RequestBody Cliente cliente, @PathVariable Long id){
		Cliente clienteActual=clienteService.findById(id);
		
		Cliente clienteUpdate=null;
		Map<String,Object> response= new HashMap<>();
		
		if(clienteActual==null) {
			response.put("mensaje","Error: no se pudo editar, el cliente ID: ".concat(id.toString().concat("no existe el id en la base de datos")));
			return new ResponseEntity<Map<String, Object>>(response,HttpStatus.NOT_FOUND);
		}
		
		try {
			clienteActual.setApellido(cliente.getApellido());
			clienteActual.setNombre(cliente.getNombre());
			clienteActual.setEmail(cliente.getEmail());
			clienteActual.setTelefono(cliente.getTelefono());
			clienteActual.setRegion(cliente.getRegion());
			if(cliente.getCreatedAt()!= null) {
				clienteActual.setCreatedAt(cliente.getCreatedAt());
			}else {
				clienteActual.setCreatedAt(clienteActual.getCreatedAt());
			}
			
			clienteUpdate=clienteService.save(clienteActual);
		} catch (DataAccessException e){
			response.put("mensaje","Error al actualizar el cliente en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje","El cliente ha sido creado con éxito!");
		response.put("cliente", clienteUpdate);
		return new ResponseEntity<Map<String, Object>>(response,HttpStatus.CREATED);
	}
	
	/*@DeleteMapping("clientes/{id}")
	public void delete(@PathVariable Long id) {
		clienteService.delete(id);
	}*/
	
	@DeleteMapping("clientes/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id){
		Map<String,Object> response= new HashMap<>();
		
		try {
			clienteService.delete(id);
		} catch (DataAccessException e) {
			response.put("mensaje","Error al eliminar el cliente en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "El cliente ha sido eliminado con éxito");
		
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
	
	@GetMapping("/clientes/regiones")
	public List<Region> listarRegiones(){
		return clienteService.findAllRegions();
	}
}
//comentario de prueba para ver los cambios en el repo subido a github
/*@ApiOperation(value=descricion,
notes=kfkfkfff descripcion mas larga)
ApiParams
Hay una version superior de Swagger en la que la etiqueta ApiOpertion se llama solo Operation.
*/
