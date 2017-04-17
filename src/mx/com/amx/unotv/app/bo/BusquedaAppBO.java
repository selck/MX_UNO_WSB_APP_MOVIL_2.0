package mx.com.amx.unotv.app.bo;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import mx.com.amx.unotv.app.bo.exception.BusquedaAppBOException;
import mx.com.amx.unotv.app.dto.ParametrosBuscadorDTO;
import mx.com.amx.unotv.app.dto.ResultadoBusquedaDTO;
import mx.com.amx.unotv.app.dto.response.RespuestaResultadoBusquedaDTO;
import mx.com.amx.unotv.app.util.UtilMovil;
import mx.com.amx.unotv.app.vo.BusquedaVO;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@Component
@Qualifier("busquedaAppBO")
public class BusquedaAppBO {

	private static Logger log = Logger.getLogger(BusquedaAppBO.class);
	private final Properties props = new Properties();
	
	String URL_WS_BASE = "";

	private RestTemplate restTemplate;
	HttpHeaders headers = new HttpHeaders();

	
	public BusquedaAppBO() {
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
	
	public RespuestaResultadoBusquedaDTO obtieneResultadosBusqueda(BusquedaVO busquedaVO) throws BusquedaAppBOException{
		RespuestaResultadoBusquedaDTO respuestaDTO = new RespuestaResultadoBusquedaDTO();
		try  {
			ParametrosBuscadorDTO parametrosDTO=UtilMovil.obtenerPropiedadesBuscador("ambiente.resources.properties");
			
			List<ResultadoBusquedaDTO> listResultados=getNotasBuscadas(busquedaVO, parametrosDTO);
			String indexUsuario=busquedaVO.getRes_start() == null || busquedaVO.getRes_start().equals("")?"1":busquedaVO.getRes_start();
			int indice= (Integer.parseInt(indexUsuario) + Integer.parseInt(parametrosDTO.getNumResultadosBusqueda())) - 1;
			log.info("Indice: "+indice);
			respuestaDTO.setCausa_error("");
			respuestaDTO.setCodigo("0");
			respuestaDTO.setListaResultados(listResultados);
			respuestaDTO.setMensaje("OK");
			respuestaDTO.setRes_start(String.valueOf(indice));
			
		} catch ( Exception e ) {
			log.error("Exception en obtieneResultadosBusqueda: " + e.getMessage() );
			throw new BusquedaAppBOException(e.getMessage(), e);
		}		
		return respuestaDTO;
	}
	

	public static void main(String [] args){
		System.out.println("main");
		//String urlBuscador="http://buscador.unotv.com/s/search?q=donald canad�&site=unotv&access=p&client=unotv&output=xml_no_dtd&proxyreload=0&filter=0&sort=date:D:S:d1&num=15&start=0&getfields=*";
		String urlBuscador="http://buscador.unotv.com/s/search?q=$PALABRA_A_BUSCAR$&site=unotv&access=p&client=unotv&output=xml_no_dtd&proxyreload=0&filter=0&sort=date:D:S:d1&num=$NUM_RESULTADOS$&start=$PAGINA$&getfields=*";
		try {
			String palabra="Donald Trump";
			urlBuscador=urlBuscador.replace("$NUM_RESULTADOS$", "15").replace("$PAGINA$", "0");
			urlBuscador=urlBuscador.replace("$PALABRA_A_BUSCAR$", replaceURLEncoding(palabra));
			System.out.println(urlBuscador);
			URL url = new URL(urlBuscador);
			URLConnection urlCon = url.openConnection();
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(urlCon.getInputStream());
			doc.getDocumentElement().normalize();

			System.out.println("Root element :" +doc.getDocumentElement().getNodeName());
			NodeList nList = doc.getElementsByTagName("R");
			
			
			System.out.println("Recorremos las notas de la busqueda");
			System.out.println("Total de notas: "+nList.getLength());
			for (int temp = 0; temp < nList.getLength(); temp++) 
			{
				
				Node nNode = nList.item(temp);
				
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) nNode;
						
					System.out.println("U (URL Nota)    : "+eElement.getElementsByTagName("U").item(0).getTextContent());					
					NodeList nodeListMT =   eElement.getElementsByTagName("MT");
					//Obtenemos los metas de la nota:
					//log.info("== Metas de la nota: "+nodeListMT.getLength());
					//log.info("Num metas: "+nodeListMT.getLength());
					for (int i = 0; i < nodeListMT.getLength(); i++) {
						Node nNodeMeta = nodeListMT.item(i);																
						NamedNodeMap namedNodeMap = nNodeMeta.getAttributes();						 						
					}
				
				}
			}
		} catch (Exception e) {
			System.out.println("Error main: "+e);
			e.printStackTrace();
		}
	}
	private List<ResultadoBusquedaDTO> getNotasBuscadas(BusquedaVO busquedaVO, ParametrosBuscadorDTO parametrosDTO) throws BusquedaAppBOException{
		
		//String urlBuscador="http://AMXSVROUT01-1.tmx-internacional.net/s/search?q=epn&site=unotv&access=p&client=unotv&output=xml_no_dtd&proxyreload=1&getfields=*&num=50";
		String urlBuscador=parametrosDTO.getUrlBuscador().replace("$NUM_RESULTADOS$", parametrosDTO.getNumResultadosBusqueda()).replace("$PAGINA$", busquedaVO.getRes_start()== null || busquedaVO.getRes_start().equals("")?"1":busquedaVO.getRes_start());
		urlBuscador=urlBuscador.replace("$PALABRA_A_BUSCAR$", replaceURLEncoding(busquedaVO.getQ()));
		log.info("urlBuscador: "+urlBuscador);		
		
		List<ResultadoBusquedaDTO> list = new ArrayList<ResultadoBusquedaDTO>();
		//String base= parametrosDTO.getAmmbiente().equalsIgnoreCase("desarrollo")?"Keywords:$PALABRA$|":"nota_tags:$PALABRA$|";
		ResultadoBusquedaDTO resultadoBusquedaDTO = null;
		try {
			
			/*String palabras[] = tags.split("\\,");
			String query = "";
			
			for (String palabra : palabras) {
				palabra = replaceGSA(palabra.trim());
				query += base.replace("$PALABRA$", palabra);
			}
			urlServidor = urlBuscador + query;
			urlServidor = urlServidor.substring(0, urlServidor.length() - 1);
			log.info("URL GSA: " + urlServidor);*/
			
			URL url = new URL(urlBuscador);
			URLConnection urlCon = url.openConnection();
			if(parametrosDTO.getAmbiente().equalsIgnoreCase("produccion"))
				urlCon.setRequestProperty("X-Target", "buscador.unotv.com");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(urlCon.getInputStream());
			doc.getDocumentElement().normalize();

			log.info("Root element :" +doc.getDocumentElement().getNodeName());
			NodeList nList = doc.getElementsByTagName("R");
			
			
			log.info("Recorremos las notas de la busqueda");
			log.info("Total de notas: "+nList.getLength());
			//Recorremos las notas
			for (int temp = 0; temp < nList.getLength(); temp++) 
			{
				
				resultadoBusquedaDTO = new ResultadoBusquedaDTO();
				
				Node nNode = nList.item(temp);
				
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) nNode;
										
					//log.info("T (Titulo GSA)  : "+eElement.getElementsByTagName("T").item(0).getTextContent());
					log.info("U (URL Nota)    : "+eElement.getElementsByTagName("U").item(0).getTextContent());					
					NodeList nodeListMT =   eElement.getElementsByTagName("MT");
					resultadoBusquedaDTO.setUrl_nota(eElement.getElementsByTagName("U").item(0).getTextContent());
					//Obtenemos los metas de la nota:
					//log.info("== Metas de la nota: "+nodeListMT.getLength());
					//log.info("Num metas: "+nodeListMT.getLength());
					for (int i = 0; i < nodeListMT.getLength(); i++) {
						Node nNodeMeta = nodeListMT.item(i);																
						NamedNodeMap namedNodeMap = nNodeMeta.getAttributes();						 						
						resultadoBusquedaDTO = obtieneMeta(resultadoBusquedaDTO, namedNodeMap.getNamedItem("N").getTextContent().trim(), namedNodeMap.getNamedItem("V").getTextContent().trim());
					}

					list.add(resultadoBusquedaDTO);
				
				}
			}
		} catch (Exception e) {
			log.error("Error getNotasBuscadas: ", e);
			throw new BusquedaAppBOException(e.getMessage(), e);
		}
		return list;
	}
	private static String replaceURLEncoding(String cadena) {
		
		try {
			
			cadena = cadena.replaceAll(" ", "%20");

			cadena = cadena.replaceAll("á", "%C3%A1");
			cadena = cadena.replaceAll("é", "%C3%A9");
			cadena = cadena.replaceAll("í", "%C3%AD");
			cadena = cadena.replaceAll("ó", "%C3%B3");
			cadena = cadena.replaceAll("ú", "%C3%BA");
			cadena = cadena.replaceAll("ñ", "%C3%B1");
			cadena = cadena.replaceAll("ç", "%C3%A7");
			cadena = cadena.replaceAll("ü", "%C3%BC");
			
			cadena = cadena.replaceAll("Á", "%C3%81");
			cadena = cadena.replaceAll("É", "%C3%89");
			cadena = cadena.replaceAll("Í", "%C3%8D");
			cadena = cadena.replaceAll("Ó", "%C3%93");
			cadena = cadena.replaceAll("Ú", "%C3%9A");
			cadena = cadena.replaceAll("Ñ", "%C3%91");
			cadena = cadena.replaceAll("Ç", "%C3%87");
			cadena = cadena.replaceAll("Ü", "%C3%9C");
			
			cadena = cadena.replaceAll("°", "%C2%B0");
			cadena = cadena.replaceAll("“", "%E2%80%9C");        
	        cadena = cadena.replaceAll("”", "%E2%80%9D");
	        cadena = cadena.replaceAll("‘", "%E2%80%98");
	        cadena = cadena.replaceAll("’", "%E2%80%99");
	        cadena = cadena.replaceAll("¡", "%C2%A1");
	        cadena = cadena.replaceAll("¿", "%C2%BF");
	        cadena = cadena.replace("?", "%3F");

		} catch (Exception e) {
			log.error("Error replaceURLEncoding: " + e.getMessage());
			return "";
		}
		return cadena;
	}
	private static String replaceGSA5(String cadena) {
		
		try {
			
			cadena = cadena.replaceAll(" ", "%20");

			cadena = cadena.replaceAll("á", "%C3%A1");
			cadena = cadena.replaceAll("é", "%C3%A9");
			cadena = cadena.replaceAll("í", "%C3%AD");
			cadena = cadena.replaceAll("ó", "%C3%B3");
			cadena = cadena.replaceAll("ú", "%C3%BA");
			cadena = cadena.replaceAll("ñ", "%C3%B1");
			cadena = cadena.replaceAll("ç", "%C3%A7");
			cadena = cadena.replaceAll("ü", "%C3%BC");
			
			cadena = cadena.replaceAll("Á", "%C3%81");
			cadena = cadena.replaceAll("É", "%C3%89");
			cadena = cadena.replaceAll("Í", "%C3%8D");
			cadena = cadena.replaceAll("Ó", "%C3%93");
			cadena = cadena.replaceAll("Ú", "%C3%9A");
			cadena = cadena.replaceAll("Ñ", "%C3%91");
			cadena = cadena.replaceAll("Ç", "%C3%87");
			cadena = cadena.replaceAll("Ü", "%C3%9C");
			
			cadena = cadena.replaceAll("°", "%C2%B0");
			cadena = cadena.replaceAll("“", "%E2%80%9C");        
	        cadena = cadena.replaceAll("”", "%E2%80%9D");
	        cadena = cadena.replaceAll("‘", "%E2%80%98");
	        cadena = cadena.replaceAll("’", "%E2%80%99");
	        cadena = cadena.replaceAll("¡", "%C2%A1");
	        cadena = cadena.replaceAll("¿", "%C2%BF");
	        cadena = cadena.replace("?", "%3F");
			/*cadena = cadena.replaceAll(" ", "%2520");

			cadena = cadena.replaceAll("á", "%C3%A1");
			cadena = cadena.replaceAll("é", "%C3%A9");
			cadena = cadena.replaceAll("í", "%C3%AD");
			cadena = cadena.replaceAll("ó", "%C3%B3");
			cadena = cadena.replaceAll("ú", "%C3%BA");

			cadena = cadena.replaceAll("Á", "%C3%81");
			cadena = cadena.replaceAll("É", "%C3%89");
			cadena = cadena.replaceAll("Í", "%C3%8D");
			cadena = cadena.replaceAll("Ó", "%C3%93");
			cadena = cadena.replaceAll("Ú", "%C3%9A");*/

		} catch (Exception e) {
			log.error("Error replace GSA: " + e.getMessage());
			return "";
		}
		return cadena;
	}
	
	/**
	 * Metodo que coloca los tag
	 * */
	private ResultadoBusquedaDTO obtieneMeta(ResultadoBusquedaDTO resultadoBusquedaDTO, String N, String V)
	{
		//log.debug("Inicia obtieneMeta");
		try {
			
			if(V == null)
			{
				V="";
			}
			
			if(N.equals("nota_published_time"))
			{
				resultadoBusquedaDTO.setFecha_publicacion(V.trim());
			}
			
						
			if(N.equals("nota_tipo"))
			{
				resultadoBusquedaDTO.setId_tipo_nota(V.trim());
			}
			
			
			if(N.equals("nota_id"))
			{
				resultadoBusquedaDTO.setId_contenido(V.trim());
			}
			
			if(N.equals("nota_img"))
			{
				resultadoBusquedaDTO.setImagen_principal(V.trim());
				resultadoBusquedaDTO.setImagen_miniatura(V.trim().replace("Principal","Miniatura"));
			}
			
			if(N.equals("nota_categoria"))
			{
				resultadoBusquedaDTO.setId_categoria(V.trim());
				resultadoBusquedaDTO.setId_tag(V.trim());
			}
			
			if(N.equals("Description"))
			{
				resultadoBusquedaDTO.setDescripcion(V.trim());
			}
									
			if(N.equals("nota_titulo"))
			{
				resultadoBusquedaDTO.setTitulo(V.trim());
			}
			
			if(N.equals("nota_friendly_url"))
			{
				resultadoBusquedaDTO.setFriendly_url(V.trim());
			}
			/*if(N.equals("nota_categoria"))
			{
				resultadoBusquedaDTO.setId_tag(V.trim());
			}*/
			
			
		} catch (Exception e) {
			//log.error("Exception en obtieneMeta");
		}
		
		return resultadoBusquedaDTO;		
	}

}
