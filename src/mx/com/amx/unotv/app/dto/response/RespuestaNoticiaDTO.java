package mx.com.amx.unotv.app.dto.response;

import mx.com.amx.unotv.app.dto.NoticiaDTO;

public class RespuestaNoticiaDTO extends RespuestaDTO {
	
	NoticiaDTO noticia;

	/**
	 * @return the noticia
	 */
	public NoticiaDTO getNoticia() {
		return noticia;
	}

	/**
	 * @param noticia the noticia to set
	 */
	public void setNoticia(NoticiaDTO noticia) {
		this.noticia = noticia;
	}
	
	
	
}
