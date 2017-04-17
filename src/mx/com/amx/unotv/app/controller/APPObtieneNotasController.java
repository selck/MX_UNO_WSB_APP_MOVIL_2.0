package mx.com.amx.unotv.app.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.servlet.http.HttpServletResponse;

import mx.com.amx.unotv.app.bo.ObtieneNotasAppBO;
import mx.com.amx.unotv.app.dto.response.RespuestaNoticiaListDFPDTO;
import mx.com.amx.unotv.app.dto.response.RespuestaNoticiaListDTO;
import mx.com.amx.unotv.app.vo.FechaNotaVO;
import mx.com.amx.unotv.app.vo.ParametrosNotasMenuVO;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Component
@RequestMapping("appObtieneNotasController")
public class APPObtieneNotasController {
	
	private static final Logger log=Logger.getLogger(APPObtieneNotasController.class);
	private ObtieneNotasAppBO obtieneNotasAppBO;
	
	@RequestMapping( value = "obtieneNotas" , method=RequestMethod.POST , headers="Accept=application/json; charset=utf-8", produces = "application/json; charset=utf-8" )
	@ResponseBody
	public RespuestaNoticiaListDTO  obtieneNotas (@RequestBody FechaNotaVO fechaNotaVO, HttpServletResponse response ) {
		log.info("obtieneNotas [Controller]");
		String msj="OK";
		String codigo="0";
		String causa_error="";
		int status_peticion=HttpServletResponse.SC_OK;
		RespuestaNoticiaListDTO respuesta = new RespuestaNoticiaListDTO();
		try  {
			String fecha=fechaNotaVO.getFecha() == null ?"":fechaNotaVO.getFecha();
			if(!fecha.equals("")){
				TimeZone tz = TimeZone.getTimeZone("CST");
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
				df.setTimeZone(tz);
				Date date = df.parse(fecha);
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				fecha=formatter.format(date);
			}
			respuesta = obtieneNotasAppBO.obtieneNotas(fecha);
		}  catch ( Exception e ) {
			log.error(" Error en obtieneNotas [Controller]" + e.getMessage() );
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
	
	@RequestMapping( value = "obtieneNotasMenu" , method=RequestMethod.POST , headers="Accept=application/json; charset=utf-8", produces = "application/json; charset=utf-8" )
	@ResponseBody
	public RespuestaNoticiaListDFPDTO  obtieneNotasMenu (@RequestBody ParametrosNotasMenuVO paMenuVO, HttpServletResponse response ) {
		log.info("obtieneNotasMenu [Controller]");
		String msj="OK";
		String codigo="0";
		String causa_error="";
		int status_peticion=HttpServletResponse.SC_OK;
		RespuestaNoticiaListDFPDTO respuesta = new RespuestaNoticiaListDFPDTO();
		try  {
			String fecha=paMenuVO.getFecha() == null ?"":paMenuVO.getFecha();
			if(!fecha.equals("")){
				TimeZone tz = TimeZone.getTimeZone("CST");
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
				df.setTimeZone(tz);
				Date date = df.parse(fecha);
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				paMenuVO.setFecha(formatter.format(date));
			}
			respuesta = obtieneNotasAppBO.obtieneNotasMenu(paMenuVO);
		}  catch ( Exception e ) {
			log.error(" Error en obtieneNotasMenu [Controller]" + e.getMessage() );
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
	 * @return the obtieneNotasAppBO
	 */
	public ObtieneNotasAppBO getObtieneNotasAppBO() {
		return obtieneNotasAppBO;
	}
	/**
	 * @param obtieneNotasAppBO the obtieneNotasAppBO to set
	 */
	@Autowired
	public void setObtieneNotasAppBO(ObtieneNotasAppBO obtieneNotasAppBO) {
		this.obtieneNotasAppBO = obtieneNotasAppBO;
	}
	
	
}
