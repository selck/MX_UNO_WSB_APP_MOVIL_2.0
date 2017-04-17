package mx.com.amx.unotv.app.dto.response;

import java.util.List;

import mx.com.amx.unotv.app.dto.NoticiaListDTO;

public class RespuestaNoticiaListDFPDTO extends RespuestaDTO {
	
	private String ruta_dfp;  
	private List<NoticiaListDTO> listaNotas;
	
	/**
	 * @return the ruta_dfp
	 */
	public String getRuta_dfp() {
		return ruta_dfp;
	}
	/**
	 * @param ruta_dfp the ruta_dfp to set
	 */
	public void setRuta_dfp(String ruta_dfp) {
		this.ruta_dfp = ruta_dfp;
	}
	/**
	 * @return the listaNotas
	 */
	public List<NoticiaListDTO> getListaNotas() {
		return listaNotas;
	}
	/**
	 * @param listaNotas the listaNotas to set
	 */
	public void setListaNotas(List<NoticiaListDTO> listaNotas) {
		this.listaNotas = listaNotas;
	}
	
	
}
