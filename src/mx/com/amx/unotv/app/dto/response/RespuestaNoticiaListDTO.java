package mx.com.amx.unotv.app.dto.response;

import java.util.List;

import mx.com.amx.unotv.app.dto.NoticiaListDTO;

public class RespuestaNoticiaListDTO extends RespuestaDTO {

	List<NoticiaListDTO> listaNotas;

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
