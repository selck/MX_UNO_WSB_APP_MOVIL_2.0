package mx.com.amx.unotv.app.controller;

import javax.servlet.http.HttpServletResponse;

import mx.com.amx.unotv.app.bo.MagazineAppBO;
import mx.com.amx.unotv.app.dto.response.RespuestaNoticiaListDTO;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Component
@RequestMapping("appMagazineController")
public class APPMagazineController {
	
	private Logger log=Logger.getLogger(APPMagazineController.class);
	public MagazineAppBO magazineAppBO = new MagazineAppBO();
	
	
	@RequestMapping(value={"pruebaGetError"}, method={org.springframework.web.bind.annotation.RequestMethod.GET}, headers={"Accept=application/json"})
	@ResponseBody
	public RespuestaNoticiaListDTO pruebaGetError( HttpServletResponse response ){
		log.info("pruebaGetError [Controller]");
		RespuestaNoticiaListDTO respuestaDTO = new RespuestaNoticiaListDTO();
		String msj="OK";
		String codigo="0";
		String causa_error="";
		int status_peticion=HttpServletResponse.SC_OK;
		try {
			Integer.parseInt("abc");
		} catch (Exception e) {
			codigo="-1";
			msj=e.getMessage();
			causa_error=e.toString();
			status_peticion=HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
			respuestaDTO.setListaNotas(null);
			respuestaDTO.setMensaje(e.getMessage());
			respuestaDTO.setCodigo("-1");
			respuestaDTO.setCausa_error(e.getCause() == null?e.getMessage():e.getCause().toString());
			respuestaDTO.setListaNotas(null);
		}
		response.setHeader("codigo2", codigo);
		response.setHeader("mensaje2", msj);
		response.setHeader("causa_error2", causa_error);
		response.setStatus(status_peticion);
		return respuestaDTO;
	}
	
	@RequestMapping( value = "obtieneMagazine" , method=RequestMethod.GET , headers="Accept=application/json; charset=utf-8", produces = "application/json; charset=utf-8" )
	@ResponseBody
	public RespuestaNoticiaListDTO  obtieneMagazine (HttpServletResponse response ) {
		log.info("obtieneMagazine [Controller]");
		String msj="OK";
		String codigo="0";
		String causa_error="";
		int status_peticion=HttpServletResponse.SC_OK;
		RespuestaNoticiaListDTO respuesta = new RespuestaNoticiaListDTO();
		try  {
			respuesta = magazineAppBO.obtieneMagazine();
		}  catch ( Exception e ) {
			log.error(" Error en obtieneMagazine [Controller]" + e.getMessage() );
			respuesta.setMensaje(e.getMessage());
			respuesta.setCodigo("-1");
			respuesta.setCausa_error(e.getCause() == null?"":e.getCause().toString());
			respuesta.setListaNotas(null);
			codigo="-1";
			msj=e.getMessage();
			causa_error=e.toString();
			status_peticion=HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
		}
		response.setHeader("codigo", codigo);
		response.setHeader("mensaje", msj);
		response.setHeader("causa_error", causa_error);
		response.setStatus(status_peticion);
		return respuesta;								
	}
	
	/**
	 * @return the magazineAppBO
	 */
	public MagazineAppBO getMagazineAppBO() {
		return magazineAppBO;
	}

	/**
	 * @param magazineAppBO the magazineAppBO to set
	 */
	@Autowired
	public void setMagazineAppBO(MagazineAppBO magazineAppBO) {
		this.magazineAppBO = magazineAppBO;
	}
	
}
