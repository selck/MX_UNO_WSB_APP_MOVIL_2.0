package mx.com.amx.unotv.app.bo;

import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletResponse;

import mx.com.amx.unotv.app.bo.exception.MagazineAppBOException;
import mx.com.amx.unotv.app.dto.NoticiaListDTO;
import mx.com.amx.unotv.app.dto.response.RespuestaNoticiaListDTO;
import mx.com.amx.unotv.app.util.UtilMovil;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

@Component
@Qualifier("magazineAppBO")
public class MagazineAppBO {

	private static Logger log = Logger.getLogger(MagazineAppBO.class);
	private final Properties props = new Properties();
	
	String URL_WS_BASE = "";

	private RestTemplate restTemplate;
	HttpHeaders headers = new HttpHeaders();
	
	public MagazineAppBO() {
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
	
	public RespuestaNoticiaListDTO obtieneMagazine(  ) throws MagazineAppBOException{
		RespuestaNoticiaListDTO respuestaDTO = new RespuestaNoticiaListDTO();
		try  {
			String metodo = "appMagazineController/obtieneMagazine";
			String URL_WS = URL_WS_BASE + metodo;
			
			log.debug("URLWS= " + URL_WS);
			respuestaDTO=restTemplate.getForObject(URL_WS, RespuestaNoticiaListDTO.class);
			if(respuestaDTO!= null && respuestaDTO.getListaNotas().size()>0){
				List<NoticiaListDTO> listaNotas=respuestaDTO.getListaNotas();
				for (NoticiaListDTO noticiaListDTO : listaNotas) {
					noticiaListDTO.setFecha_publicacion(UtilMovil.getDateZoneTime(noticiaListDTO.getFecha_publicacion()));
				}
			}
		} catch ( Exception e ) {
			log.error("Exception en obtieneMagazine: " + e.getMessage() );
			throw new MagazineAppBOException(e.getMessage(), e);
		}		
		return respuestaDTO;
	}
	
}
