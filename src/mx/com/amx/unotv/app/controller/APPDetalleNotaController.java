package mx.com.amx.unotv.app.controller;

import javax.servlet.http.HttpServletResponse;

import mx.com.amx.unotv.app.bo.DetalleNotaAppBO;
import mx.com.amx.unotv.app.dto.response.RespuestaNoticiaDTO;
import mx.com.amx.unotv.app.vo.FriendlyURLVO;
import mx.com.amx.unotv.app.vo.IdNotaVO;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Component
@RequestMapping("appDetalleNoticiaController")
public class APPDetalleNotaController {
	
	private static final Logger log=Logger.getLogger(APPDetalleNotaController.class);
	private DetalleNotaAppBO detalleNotaAppBO;
	

	@RequestMapping(value={"obtieneDetalleNoticia"}, method={org.springframework.web.bind.annotation.RequestMethod.POST}, headers={"Accept=application/json"})
	@ResponseBody
	public RespuestaNoticiaDTO obtieneDetalleNoticia( @RequestBody  IdNotaVO idNotaVO, HttpServletResponse response ){
		RespuestaNoticiaDTO respuestaDTO = new RespuestaNoticiaDTO();
		log.info("obtieneDetalleNoticia [Controller]");
		log.info("idNoticia: "+idNotaVO.getId_nota());
		String msj="OK";
		String codigo="0";
		String causa_error="";
		int status_peticion=HttpServletResponse.SC_OK;
		try{
			respuestaDTO=detalleNotaAppBO.obtieneDetalleNoticia(idNotaVO.getId_nota());
		} catch (Exception e){
			log.error(" Error appNoticiaController [obtieneDetalleNoticia]:", e);
			respuestaDTO.setCodigo("-1");
			respuestaDTO.setMensaje(e.getMessage());
			respuestaDTO.setCausa_error(e.getCause() == null?"":e.getCause().toString());
			respuestaDTO.setNoticia(null);
			codigo="-1";
			msj=e.getMessage();
			causa_error=e.toString();
			status_peticion=HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
		}
		response.setHeader("codigo", codigo);
		response.setHeader("mensaje", msj);
		response.setHeader("causa_error", causa_error);
		response.setStatus(status_peticion);
		return respuestaDTO;
	}
	
	@RequestMapping(value={"obtieneDetalleNoticiaByFriendlyURL"}, method={org.springframework.web.bind.annotation.RequestMethod.POST}, headers={"Accept=application/json"})
	@ResponseBody
	public RespuestaNoticiaDTO obtieneDetalleNoticiaByFriendlyURL( @RequestBody  FriendlyURLVO friendlyURL, HttpServletResponse response ){
		RespuestaNoticiaDTO respuestaDTO = new RespuestaNoticiaDTO();
		log.info("obtieneDetalleNoticiaByFriendlyURL [Controller]");
		log.info("friendlyURL: "+friendlyURL.getFriendly_url());
		String msj="OK";
		String codigo="0";
		String causa_error="";
		int status_peticion=HttpServletResponse.SC_OK;
		try{
			respuestaDTO=detalleNotaAppBO.obtieneDetalleNoticiaByFriendlyURL(friendlyURL.getFriendly_url());
		} catch (Exception e){
			log.error(" Error appNoticiaController [obtieneDetalleNoticiaByFriendlyURL]:", e);
			respuestaDTO.setCodigo("-1");
			respuestaDTO.setMensaje(e.getMessage());
			respuestaDTO.setCausa_error(e.getCause() == null?"":e.getCause().toString());
			respuestaDTO.setNoticia(null);
			codigo="-1";
			msj=e.getMessage();
			causa_error=e.toString();
			status_peticion=HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
		}
		response.setHeader("codigo", codigo);
		response.setHeader("mensaje", msj);
		response.setHeader("causa_error", causa_error);
		response.setStatus(status_peticion);
		return respuestaDTO;
	}

	/**
	 * @return the detalleNotaAppBO
	 */
	public DetalleNotaAppBO getDetalleNotaAppBO() {
		return detalleNotaAppBO;
	}


	/**
	 * @param detalleNotaAppBO the detalleNotaAppBO to set
	 */
	@Autowired
	public void setDetalleNotaAppBO(DetalleNotaAppBO detalleNotaAppBO) {
		this.detalleNotaAppBO = detalleNotaAppBO;
	}
	
	
	
	
}
