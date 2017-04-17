package mx.com.amx.unotv.app.dto;

import java.io.Serializable;

public class ParametrosBuscadorDTO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String dominio;
	private String ambiente;
	private String urlBuscador;
	private String numResultadosBusqueda;
	
	/**
	 * @return the dominio
	 */
	public String getDominio() {
		return dominio;
	}
	/**
	 * @param dominio the dominio to set
	 */
	public void setDominio(String dominio) {
		this.dominio = dominio;
	}
	/**
	 * @return the ambiente
	 */
	public String getAmbiente() {
		return ambiente;
	}
	/**
	 * @param ammbiente the ambiente to set
	 */
	public void setAmbiente(String ambiente) {
		this.ambiente = ambiente;
	}
	/**
	 * @return the urlBuscador
	 */
	public String getUrlBuscador() {
		return urlBuscador;
	}
	/**
	 * @param urlBuscador the urlBuscador to set
	 */
	public void setUrlBuscador(String urlBuscador) {
		this.urlBuscador = urlBuscador;
	}
	/**
	 * @return the numResultadosBusqueda
	 */
	public String getNumResultadosBusqueda() {
		return numResultadosBusqueda;
	}
	/**
	 * @param numResultadosBusqueda the numResultadosBusqueda to set
	 */
	public void setNumResultadosBusqueda(String numResultadosBusqueda) {
		this.numResultadosBusqueda = numResultadosBusqueda;
	}
	
	
}
