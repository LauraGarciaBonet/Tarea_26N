package com.formacionjava.springboot.apirest.models.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

//Añadimos tabla clientes
@Entity
@Table(name = "clientes")
//prepara los atributos y métodos para que no tenga problemas en convertir esto en propiedades de base de datos
public class Cliente implements Serializable {
	
	//Indicamos que la variable inferior será un ID
	@Id
	//generamos valor AI, PK, etc
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private long id;
	
	//Al aplicar el column nullable se indica que es un parámetro obligatorio.
	@Column(nullable=false)
	private String nombre;
	private String apellido;
	
	//Al incluir el parámetro unique le indicamos que tiene que ser unico, no se puede repetir.
	@Column(nullable=false, unique=true)
	private String email;
	private int telefono;
	
	//Nombrar columna de mi tabla y escoger tipo de dato
	@Column(name="created_at")
	@Temporal(TemporalType.DATE)
	private Date createdAt;
	
	private String imagen;
	
	//el fetch es una subconsulta a otra tabla. hay especificar como se hace y solo a traves del metodo que se le indique
	//la anotacion json.. se usa solo cuando el fetch es de tipi LAZY
	//notnull sirve para especificar que no puede estar vacío, como el nullable, y envia un msj
	@NotNull(message="No puede estar vacio")
	@ManyToOne(fetch= FetchType.LAZY)
	@JoinColumn(name="region_id")
	@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
	private Region region;
	
	
	public Region getRegion() {
		return region;
	}
	public void setRegion(Region region) {
		this.region = region;
	}
	public String getImagen() {
		return imagen;
	}
	public void setImagen(String imagen) {
		this.imagen = imagen;
	}
	
	@PrePersist
	public void prePersist() {
		createdAt = new Date();
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getApellido() {
		return apellido;
	}
	public void setApellido(String apellido) {
		this.apellido = apellido;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public int getTelefono() {
		return telefono;
	}
	public void setTelefono(int telefono) {
		this.telefono = telefono;
	}
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
}