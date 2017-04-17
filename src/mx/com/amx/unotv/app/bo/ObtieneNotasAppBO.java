package mx.com.amx.unotv.app.bo;

import java.util.List;
import java.util.Properties;

import mx.com.amx.unotv.app.bo.exception.ObtieneNotasAppBOException;
import mx.com.amx.unotv.app.dto.NoticiaListDTO;
import mx.com.amx.unotv.app.dto.response.RespuestaNoticiaListDFPDTO;
import mx.com.amx.unotv.app.dto.response.RespuestaNoticiaListDTO;
import mx.com.amx.unotv.app.util.UtilMovil;
import mx.com.amx.unotv.app.vo.ParametrosNotasMenuVO;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

@Component
@Qualifier("obtieneNotasAppBO")
public class ObtieneNotasAppBO {

	private static Logger log = Logger.getLogger(ObtieneNotasAppBO.class);
	private final Properties props = new Properties();
	
	String URL_WS_BASE = "";

	private RestTemplate restTemplate;
	HttpHeaders headers = new HttpHeaders();
	
	public ObtieneNotasAppBO() {
		super();
		restTemplate = new RestTemplate();
		ClientHttpRequestFactory factory = restTemplate.getRequestFactory();
		
		if ( factory instanceof SimpleClientHttpRequestFactory) {
			((SimpleClientHttpRequestFactory) factory).setConnectTimeout( 35 * 1000 );
			((SimpleClientHttpRequestFactory) factory).setReadTimeout( 35 * 1000 );
		} else if ( factory instanceof HttpComponentsClientHttpRequestFactory) {
			((HttpComponentsClientHttpRequestFactory) factory).setReadTimeout( 35 * 1000);
			((HttpComponentsClientHttpRequestFactory) factory).setConnectTimeout( 35 * 1000);
		}
		
		restTemplate.setRequestFactory( factory );
		headers.setContentType(MediaType.APPLICATION_JSON);
	      
		try {
			props.load( this.getClass().getResourceAsStream( "/general.properties" ) );						
		} catch(Exception e) {
			log.error("[ConsumeWS:init]Error al iniciar y cargar arhivo de propiedades." + e.getMessage());
		}		
		URL_WS_BASE = props.getProperty(props.getProperty( "ambiente" )+".urlws");
	}
	
	@RequestMapping(value={"obtieneNotas"}, method={org.springframework.web.bind.annotation.RequestMethod.POST}, headers={"Accept=application/json"})
	@ResponseBody
	public RespuestaNoticiaListDTO obtieneNotas( @RequestBody String fecha ) throws ObtieneNotasAppBOException{
		RespuestaNoticiaListDTO respuestaDTO = new RespuestaNoticiaListDTO();
		log.info("obtieneNotas [Controller]");
		log.info("fecha: "+fecha);
		try  {
			String metodo = "appObtieneNotasController/obtieneNotas";
			String URL_WS = URL_WS_BASE + metodo;
			log.debug("URLWS= " + URL_WS);
			
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			
			HttpEntity<String> entity = new HttpEntity<String>( fecha, headers );
			respuestaDTO = restTemplate.postForObject( URL_WS, entity , RespuestaNoticiaListDTO.class);
			if(respuestaDTO!= null && respuestaDTO.getListaNotas().size()>0){
				List<NoticiaListDTO> listaNotas=respuestaDTO.getListaNotas();
				for (NoticiaListDTO noticiaListDTO : listaNotas) {
					noticiaListDTO.setFecha_publicacion(UtilMovil.getDateZoneTime(noticiaListDTO.getFecha_publicacion()));
				}
			}
			
		} catch ( Exception e ) {
			log.error("Exception en obtieneMagazine: " + e.getMessage() );
			throw new ObtieneNotasAppBOException(e.getMessage(), e);
		}		
		return respuestaDTO;
	}
	@RequestMapping(value={"obtieneNotasMenu"}, method={org.springframework.web.bind.annotation.RequestMethod.POST}, headers={"Accept=application/json"})
	@ResponseBody
	public RespuestaNoticiaListDFPDTO obtieneNotasMenu( @RequestBody ParametrosNotasMenuVO paMenuVO ) throws ObtieneNotasAppBOException{
		
		log.info("obtieneNotasMenu [Controller]");
		log.info("TipoMenu: "+paMenuVO.getTipo_menu());
		log.info("IdMenu: "+paMenuVO.getId_menu());
		log.info("fecha: "+paMenuVO.getFecha());
		RespuestaNoticiaListDFPDTO respuesta = new RespuestaNoticiaListDFPDTO();				
		
		try {		
		
            if(paMenuVO.getId_menu() == null || paMenuVO.getId_menu().trim().equals(""))
            	throw new ObtieneNotasAppBOException("id_menu esta vacio");
            else if(paMenuVO.getTipo_menu() == null || paMenuVO.getTipo_menu().trim().equals(""))
            	throw new ObtieneNotasAppBOException("tipo_menu esta vacio");

			//Verificamos que si es para una seccion o una categoria.
			if(paMenuVO.getTipo_menu().equalsIgnoreCase("seccion")){
				respuesta = obtieneNotasBySeccion(paMenuVO.getId_menu(), paMenuVO.getFecha());
				if(respuesta!= null && respuesta.getListaNotas().size()>0){
					List<NoticiaListDTO> listaNotas=respuesta.getListaNotas();
					for (NoticiaListDTO noticiaListDTO : listaNotas) {
						noticiaListDTO.setFecha_publicacion(UtilMovil.getDateZoneTime(noticiaListDTO.getFecha_publicacion()));
					}
					
				}
			}else{
				respuesta = obtieneNotasByCategoria(paMenuVO.getId_menu(), paMenuVO.getFecha());
				if(respuesta!= null && respuesta.getListaNotas().size()>0){
					List<NoticiaListDTO> listaNotas=respuesta.getListaNotas();
					for (NoticiaListDTO noticiaListDTO : listaNotas) {
						noticiaListDTO.setFecha_publicacion(UtilMovil.getDateZoneTime(noticiaListDTO.getFecha_publicacion()));
					}
				}
			}			
			return respuesta;
			
		}catch (ObtieneNotasAppBOException pe){
			log.error("PreferidosAppBOException en obtieneNotasMenu: " + pe.getMessage() );
			throw new ObtieneNotasAppBOException(pe.getMessage());
		}
	}
	

	private RespuestaNoticiaListDFPDTO obtieneNotasBySeccion(String seccion, String fecha) throws ObtieneNotasAppBOException
	{
		RespuestaNoticiaListDFPDTO respuesta =new RespuestaNoticiaListDFPDTO();
		try  {
			String metodo = "appObtieneNotasController/obtieneNotasBySeccion";
			String URL_WS = URL_WS_BASE + metodo;
			log.debug("URLWS= " + URL_WS);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			
			MultiValueMap<String, Object> parts;
			parts = new LinkedMultiValueMap<String, Object>();
			parts.add("seccion", seccion);
			parts.add("fecha", fecha);
			
			respuesta=restTemplate.postForObject(URL_WS, parts, RespuestaNoticiaListDFPDTO.class);

		}catch ( Exception e ) {
			log.error("Exception en obtieneNotasBySeccion: " + e.getMessage() );
			throw new ObtieneNotasAppBOException(e.getMessage(), e);
		}		
		return respuesta;
	}
	
	
	private RespuestaNoticiaListDFPDTO obtieneNotasByCategoria(String categoria, String fecha) throws ObtieneNotasAppBOException
	{
		RespuestaNoticiaListDFPDTO respuesta =new RespuestaNoticiaListDFPDTO();
		try  {
			String metodo = "appObtieneNotasController/obtieneNotasByCategoria";
			String URL_WS = URL_WS_BASE + metodo;
			log.debug("URLWS= " + URL_WS);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			
			MultiValueMap<String, Object> parts;
			parts = new LinkedMultiValueMap<String, Object>();
			parts.add("categoria", categoria);
			parts.add("fecha", fecha);
			
			respuesta=restTemplate.postForObject(URL_WS, parts, RespuestaNoticiaListDFPDTO.class);

		}catch ( Exception e ) {
			log.error("Exception en obtieneNotasByCategoria: " + e.getMessage() );
			throw new ObtieneNotasAppBOException(e.getMessage(), e);
		}		
		return respuesta;
	}
}
