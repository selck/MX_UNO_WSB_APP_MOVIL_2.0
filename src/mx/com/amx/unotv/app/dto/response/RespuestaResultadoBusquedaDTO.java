package mx.com.amx.unotv.app.dto.response;

import java.io.Serializable;
import java.util.List;

import mx.com.amx.unotv.app.dto.ResultadoBusquedaDTO;


public class RespuestaResultadoBusquedaDTO extends RespuestaDTO implements Serializable {
	
private static final long serialVersionUID = 1L;
	
	private List<ResultadoBusquedaDTO> listaResultados;
	private String res_start;

	/**
	 * @return the listaResultados
	 */
	public List<ResultadoBusquedaDTO> getListaResultados() {
		return listaResultados;
	}

	/**
	 * @param listaResultados the listaResultados to set
	 */
	public void setListaResultados(List<ResultadoBusquedaDTO> listaResultados) {
		this.listaResultados = listaResultados;
	}

	/**
	 * @return the res_start
	 */
	public String getRes_start() {
		return res_start;
	}

	/**
	 * @param res_start the res_start to set
	 */
	public void setRes_start(String res_start) {
		this.res_start = res_start;
	}
	
	

}
