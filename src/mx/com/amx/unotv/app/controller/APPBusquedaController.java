package mx.com.amx.unotv.app.controller;

import javax.servlet.http.HttpServletResponse;

import mx.com.amx.unotv.app.bo.BusquedaAppBO;
import mx.com.amx.unotv.app.dto.response.RespuestaResultadoBusquedaDTO;
import mx.com.amx.unotv.app.vo.BusquedaVO;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Component
@RequestMapping("appBusquedaController")
public class APPBusquedaController {
	
	private Logger log=Logger.getLogger(APPMagazineController.class);
	public BusquedaAppBO busquedaAppBO=new BusquedaAppBO();
	
	@RequestMapping( value = "busquedaNotas" , method=RequestMethod.POST , headers="Accept=application/json; charset=utf-8", produces = "application/json; charset=utf-8" )
	@ResponseBody
	public RespuestaResultadoBusquedaDTO busquedaNotas ( @RequestHeader HttpHeaders headers, @RequestBody BusquedaVO busquedaVO, HttpServletResponse response) {
		log.info("busquedaNotas [Controller]");
		log.info("busquedaVO.getQ(): "+busquedaVO.getQ());
		RespuestaResultadoBusquedaDTO respuesta = new RespuestaResultadoBusquedaDTO();
		String msj="OK";
		String codigo="0";
		String causa_error="";
		int status_peticion=HttpServletResponse.SC_OK;
		try  {
			respuesta = busquedaAppBO.obtieneResultadosBusqueda(busquedaVO);
		}  catch ( Exception e ) {
			log.error(" Error en busquedaNotas [Controller]" + e.getMessage() );
			respuesta.setMensaje(e.getMessage());
			respuesta.setCodigo("-1");
			respuesta.setCausa_error(e.getCause() == null?"":e.getCause().toString());
			respuesta.setListaResultados(null);
			respuesta.setRes_start("");
			
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
	 * @return the busquedaAppBO
	 */
	public BusquedaAppBO getBusquedaAppBO() {
		return busquedaAppBO;
	}

	/**
	 * @param busquedaAppBO the busquedaAppBO to set
	 */
	@Autowired
	public void setBusquedaAppBO(BusquedaAppBO busquedaAppBO) {
		this.busquedaAppBO = busquedaAppBO;
	}
	
	
}
