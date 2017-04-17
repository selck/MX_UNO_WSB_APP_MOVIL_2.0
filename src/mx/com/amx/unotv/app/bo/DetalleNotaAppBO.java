package mx.com.amx.unotv.app.bo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.Scanner;

import mx.com.amx.unotv.app.bo.exception.DetalleNotaAppBOException;
import mx.com.amx.unotv.app.dto.NoticiaDTO;
import mx.com.amx.unotv.app.dto.ParametrosDTO;
import mx.com.amx.unotv.app.dto.RedSocialEmbedPost;
import mx.com.amx.unotv.app.dto.response.RespuestaNoticiaDTO;
import mx.com.amx.unotv.app.util.UtilMovil;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TimeZone;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Qualifier("detalleNotaAppBO")
public class DetalleNotaAppBO {

	private static Logger logger = Logger.getLogger(DetalleNotaAppBO.class);
	private final Properties props = new Properties();
	
	String URL_WS_BASE = "";

	private RestTemplate restTemplate;
	HttpHeaders headers = new HttpHeaders();
	
	public DetalleNotaAppBO() {
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
			logger.error("[ConsumeWS:init]Error al iniciar y cargar arhivo de propiedades." + e.getMessage());
		}		
		URL_WS_BASE = props.getProperty(props.getProperty( "ambiente" )+".urlws");
	}
	

	
	public RespuestaNoticiaDTO obtieneDetalleNoticia( final String idNoticia ) throws DetalleNotaAppBOException{
		RespuestaNoticiaDTO respuestaDTO = new RespuestaNoticiaDTO();
		try  {
			String metodo = "appDetalleNoticiaController/obtieneDetalleNoticia";
			String URL_WS = URL_WS_BASE + metodo;
			logger.debug("URLWS= " + URL_WS);
			
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			
			HttpEntity<String> entity = new HttpEntity<String>( idNoticia, headers );
			respuestaDTO = restTemplate.postForObject( URL_WS, entity , RespuestaNoticiaDTO.class);
			
			if(respuestaDTO.getCodigo().equals("0") && respuestaDTO.getMensaje().equals("OK")){
				
				ParametrosDTO parametrosDTO=UtilMovil.obtenerPropiedades("ambiente.resources.properties");
				NoticiaDTO nota=respuestaDTO.getNoticia();
				nota.setContenido_nota(getEmbedPost(nota.getContenido_nota()));
				//nota.setContenido_nota(nota.getContenido_nota());
				String video_player=nota.getId_video_player();
				if(video_player.contains("?")){
					video_player=video_player.substring(0,video_player.indexOf('?'));
				}
				
				if(nota.getId_categoria().equalsIgnoreCase("deportes")){
					nota.setId_video_pcode((nota.getId_tipo_nota().equalsIgnoreCase("video")|| nota.getId_tipo_nota().equalsIgnoreCase("multimedia"))?parametrosDTO.getPcodeDeportes():"");
					nota.setAdSetCode(parametrosDTO.getAdsetcodeDeportes());
					nota.setFecha_publicacion(UtilMovil.getDateZoneTime(nota.getFecha_publicacion()));
					nota.setId_video_player(video_player);
				}else{
					nota.setId_video_pcode((nota.getId_tipo_nota().equalsIgnoreCase("video")|| nota.getId_tipo_nota().equalsIgnoreCase("multimedia"))?parametrosDTO.getPcodeNoticias():"");
					nota.setAdSetCode(parametrosDTO.getAdsetcodeNoticias());
					nota.setFecha_publicacion(UtilMovil.getDateZoneTime(nota.getFecha_publicacion()));
					nota.setId_video_player(video_player);
				}
				
				respuestaDTO.setNoticia(nota);
			}else{
				throw new DetalleNotaAppBOException(respuestaDTO.getCausa_error());
			}
			
			
		} catch ( Exception e ) {
			/*logger.error("e: "+e.getMessage());
			logger.error("e: "+e.getCause());
			logger.error("e: "+e.getLocalizedMessage());
			logger.error("Exception en obtieneDetalleNoticia: " + e.getMessage() );*/
			throw new DetalleNotaAppBOException(e.getMessage(), e);
		}		
		return respuestaDTO;
	}
	
	public RespuestaNoticiaDTO obtieneDetalleNoticiaByFriendlyURL( final String friendlyURL ) throws DetalleNotaAppBOException{
		RespuestaNoticiaDTO respuestaDTO = new RespuestaNoticiaDTO();
		try  {
			String metodo = "appDetalleNoticiaController/obtieneDetalleNoticiaByFriendlyURL";
			String URL_WS = URL_WS_BASE + metodo;
			logger.debug("URLWS= " + URL_WS);
			
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			
			HttpEntity<String> entity = new HttpEntity<String>( friendlyURL, headers );
			respuestaDTO = restTemplate.postForObject( URL_WS, entity , RespuestaNoticiaDTO.class);
			
			if(respuestaDTO.getCodigo().equals("0") && respuestaDTO.getMensaje().equals("OK")){
				
				ParametrosDTO parametrosDTO=UtilMovil.obtenerPropiedades("ambiente.resources.properties");
				NoticiaDTO nota=respuestaDTO.getNoticia();
				nota.setContenido_nota(getEmbedPost(nota.getContenido_nota()));
				//nota.setContenido_nota(nota.getContenido_nota());
				String video_player=nota.getId_video_player();
				if(video_player.contains("?")){
					video_player=video_player.substring(0,video_player.indexOf('?'));
				}
				if(nota.getId_categoria().equalsIgnoreCase("deportes")){
					nota.setId_video_pcode((nota.getId_tipo_nota().equalsIgnoreCase("video")|| nota.getId_tipo_nota().equalsIgnoreCase("multimedia"))?parametrosDTO.getPcodeDeportes():"");
					nota.setAdSetCode(parametrosDTO.getAdsetcodeDeportes());
					nota.setFecha_publicacion(UtilMovil.getDateZoneTime(nota.getFecha_publicacion()));
					nota.setId_video_player(video_player);
				}else{
					nota.setId_video_pcode((nota.getId_tipo_nota().equalsIgnoreCase("video")|| nota.getId_tipo_nota().equalsIgnoreCase("multimedia"))?parametrosDTO.getPcodeNoticias():"");
					nota.setAdSetCode(parametrosDTO.getAdsetcodeNoticias());
					nota.setFecha_publicacion(UtilMovil.getDateZoneTime(nota.getFecha_publicacion()));
					nota.setId_video_player(video_player);
				}
				respuestaDTO.setNoticia(nota);
			}else{
				throw new DetalleNotaAppBOException(respuestaDTO.getCausa_error());
			}
			
			
		} catch ( Exception e ) {
			/*logger.error("e: "+e.getMessage());
			logger.error("e: "+e.getCause());
			logger.error("e: "+e.getLocalizedMessage());*/
			logger.error("Exception en obtieneDetalleNoticiaByFriendlyURL: " + e.getMessage() );
			throw new DetalleNotaAppBOException(e.getMessage(), e);
		}		
		return respuestaDTO;
	}

	private static String devuelveCadenasPost(String id_red_social, String rtfContenido){
		String url="", cadenaAReemplazar="", salida="";
		try {
			cadenaAReemplazar=rtfContenido.substring(rtfContenido.indexOf("["+id_red_social+"="), rtfContenido.indexOf("="+id_red_social+"]"))+"="+id_red_social+"]";
			url=cadenaAReemplazar.replace("["+id_red_social+"=", "").replace("="+id_red_social+"]", "");
			salida=cadenaAReemplazar+"|"+url;
		} catch (Exception e) {
			logger.error("Error devuelveCadenasPost: ",e);
			return "|";
		}
		return salida;
	}
	private static String getEmbedPost(String RTFContenido){
		try {
			
			
			String ini="<p dir=\"ltr\" style=\"text-align: justify;\">";
			String ini2="<p dir=\"ltr\">";
			String fin="</p>";
			
			RTFContenido=limpiaRedSocial(ini, fin, "instagram", RTFContenido);
			RTFContenido=limpiaRedSocial(ini, fin, "twitter", RTFContenido);
			RTFContenido=limpiaRedSocial(ini, fin, "facebook", RTFContenido);
			RTFContenido=limpiaRedSocial(ini, fin, "giphy", RTFContenido);
			
			RTFContenido=limpiaRedSocial(ini2, fin, "instagram", RTFContenido);
			RTFContenido=limpiaRedSocial(ini2, fin, "twitter", RTFContenido);
			RTFContenido=limpiaRedSocial(ini2, fin, "facebook", RTFContenido);
			RTFContenido=limpiaRedSocial(ini2, fin, "giphy", RTFContenido);
			
			String rtfContenido=RTFContenido;
			
			String url, cadenaAReemplazar;
			StringBuffer embedCode;
			HashMap<String,ArrayList<RedSocialEmbedPost>> MapAReemplazar = new HashMap<String,ArrayList<RedSocialEmbedPost>>();
			int num_post_embebidos;
			int contador;
			if(rtfContenido.contains("[instagram")){
				logger.info("Embed Code instagram");
				ArrayList<RedSocialEmbedPost> listRedSocialEmbedInstagram=new ArrayList<RedSocialEmbedPost>();
				num_post_embebidos=rtfContenido.split("\\[instagram=").length-1;
				contador=1;
				do{
					RedSocialEmbedPost embebedPost=new RedSocialEmbedPost();
					String cadenas=devuelveCadenasPost("instagram", rtfContenido);
					cadenaAReemplazar=cadenas.split("\\|")[0];
					url=cadenas.split("\\|")[1];
					rtfContenido=rtfContenido.replace(cadenaAReemplazar, "");
					embedCode=new StringBuffer();
					embedCode.append("<embed-instagram data-insta=\""+url+"\"></embed-instagram>\n");

					embebedPost.setCadena_que_sera_reemplazada(cadenaAReemplazar);
					embebedPost.setRed_social("instagram");
					embebedPost.setCodigo_embebido(embedCode.toString());
					
					listRedSocialEmbedInstagram.add(embebedPost);
					contador ++;
				}while(contador <= num_post_embebidos);
				
				MapAReemplazar.put("instagram", listRedSocialEmbedInstagram);
			}
			if(rtfContenido.contains("[twitter")){
				logger.info("Embed Code twitter");
				ArrayList<RedSocialEmbedPost> listRedSocialEmbedTwitter=new ArrayList<RedSocialEmbedPost>();
				num_post_embebidos=rtfContenido.split("\\[twitter=").length-1;
				contador=1;
				do{
					RedSocialEmbedPost embebedPost=new RedSocialEmbedPost();
					String cadenas=devuelveCadenasPost("twitter", rtfContenido);
					cadenaAReemplazar=cadenas.split("\\|")[0];
					url=cadenas.split("\\|")[1];
					rtfContenido=rtfContenido.replace(cadenaAReemplazar, "");
					embedCode=new StringBuffer();
							
					embedCode.append(" <embed-tweet data-twett=\""+url+"\"></embed-tweet> \n");

					embebedPost.setCadena_que_sera_reemplazada(cadenaAReemplazar);
					embebedPost.setRed_social("twitter");
					embebedPost.setCodigo_embebido(embedCode.toString());
					
					listRedSocialEmbedTwitter.add(embebedPost);
					contador ++;
				}while(contador <= num_post_embebidos);
				
				MapAReemplazar.put("twitter", listRedSocialEmbedTwitter);
			
			}
			if(rtfContenido.contains("[facebook")){
				logger.info("Embed Code facebook");
				ArrayList<RedSocialEmbedPost> listRedSocialEmbedFacebook=new ArrayList<RedSocialEmbedPost>();
				num_post_embebidos=rtfContenido.split("\\[facebook=").length-1;
				contador=1;
				do{
					RedSocialEmbedPost embebedPost=new RedSocialEmbedPost();
					String cadenas=devuelveCadenasPost("facebook", rtfContenido);
					cadenaAReemplazar=cadenas.split("\\|")[0];
					url=cadenas.split("\\|")[1];
					rtfContenido=rtfContenido.replace(cadenaAReemplazar, "");
					embedCode=new StringBuffer();
										
					embedCode.append(" <embed-fb data-fb=\""+url+"\"></embed-fb> \n");
					
					embebedPost.setCadena_que_sera_reemplazada(cadenaAReemplazar);
					embebedPost.setRed_social("facebook");
					embebedPost.setCodigo_embebido(embedCode.toString());
					
					listRedSocialEmbedFacebook.add(embebedPost);
					contador++;;
				}while(contador <= num_post_embebidos);
				
				MapAReemplazar.put("facebook", listRedSocialEmbedFacebook);
			}
			if(rtfContenido.contains("[giphy")){
				logger.info("Embed Code giphy");
				ArrayList<RedSocialEmbedPost> listRedSocialEmbedGiphy=new ArrayList<RedSocialEmbedPost>();
				num_post_embebidos=rtfContenido.split("\\[giphy=").length-1;
				contador=1;
				do{
					RedSocialEmbedPost embebedPost=new RedSocialEmbedPost();
					String cadenas=devuelveCadenasPost("giphy", rtfContenido);
					//cadenas giphy: [giphy=http://giphy.com/gifs/sassy-batman-ZuM7gif8TCvqU,http://i.giphy.com/rgg2PJ6VJTyPC.gif=giphy]|http://giphy.com/gifs/sassy-batman-ZuM7gif8TCvqU,http://i.giphy.com/rgg2PJ6VJTyPC.gif
					//cadenas giphy: [giphy=http://giphy.com/gifs/superman-funny-wdh1SvEn0E06I,http://i.giphy.com/wdh1SvEn0E06I.gif=giphy]|http://giphy.com/gifs/superman-funny-wdh1SvEn0E06I,http://i.giphy.com/wdh1SvEn0E06I.gif

					cadenaAReemplazar=cadenas.split("\\|")[0];
					url=cadenas.split("\\|")[1];
					rtfContenido=rtfContenido.replace(cadenaAReemplazar, "");
					embedCode=new StringBuffer();
					
					embedCode.append(" <embed-giphy data-giphy=\""+url.split("\\,")[1]+"\"></embed-giphy> \n");
					
					embebedPost.setCadena_que_sera_reemplazada(cadenaAReemplazar);
					embebedPost.setRed_social("giphy");
					embebedPost.setCodigo_embebido(embedCode.toString());
					
					listRedSocialEmbedGiphy.add(embebedPost);
					contador ++;
				}while(contador <= num_post_embebidos);
				
				MapAReemplazar.put("giphy", listRedSocialEmbedGiphy);
			}
			
			
			if(!MapAReemplazar.isEmpty()){
				Iterator<String> iterator_red_social = MapAReemplazar.keySet().iterator();
				String red_social="", codigo_embebido="", cadena_que_sera_reemplazada="";
				while(iterator_red_social.hasNext()){
					red_social = iterator_red_social.next();
			        if(red_social.equalsIgnoreCase("twitter") || red_social.equalsIgnoreCase("facebook") || red_social.equalsIgnoreCase("instagram") 
			        		|| red_social.equalsIgnoreCase("giphy")){
			        	ArrayList<RedSocialEmbedPost> listEmbebidos=MapAReemplazar.get(red_social);
			        	for (RedSocialEmbedPost redSocialEmbedPost : listEmbebidos) {
				        	cadena_que_sera_reemplazada=redSocialEmbedPost.getCadena_que_sera_reemplazada();
				        	codigo_embebido=redSocialEmbedPost.getCodigo_embebido();
				        	RTFContenido=RTFContenido.replace(cadena_que_sera_reemplazada, codigo_embebido);
						}
			        	
			        }
			    } 
			}
			try {
				
				RTFContenido = RTFContenido.replace("href=","data-href=");
			} catch (Exception e) {
				logger.error("Error al sustituir styles");
			}
			
			/*try {
				String listStyles[]=StringUtils.substringsBetween(RTFContenido,"style=\"","\"");
				for (String style : listStyles) {
					RTFContenido = RTFContenido.replace(style,"");
				}
				RTFContenido = RTFContenido.replace("style=\"\"","");
				
			} catch (Exception e) {
				logger.error("Error al sustituir styles");
			}*/
			
			
			return RTFContenido;
		} catch (Exception e) {
			logger.error("Error getEmbedPost: ",e);
			return RTFContenido;
		}
	}
	private static String limpiaRedSocial(String inicioBusqueda, String finBusqueda, String id_red_social, String rtfContenido){
		/*String ini="<p dir=\"ltr\" style=\"text-align: justify;\">";
		String ini2="<p dir=\"ltr\">";
		String fin="</p>";*/
		try {
			String [] arrayBetween=StringUtils.substringsBetween(rtfContenido, inicioBusqueda, finBusqueda);
			//String [] arrayBetween2=StringUtils.substringsBetween(sb2.toString(), ini2, fin);
			//System.out.println("length2: "+arrayBetween2.length);
			/*int reservado=arrayBetween.length+arrayBetween2.length;
			String  [] arrayBetweens=new String[reservado];
			System.arraycopy(arrayBetween, 0, arrayBetweens, 0, arrayBetween.length);
			System.arraycopy(arrayBetween2, 0, arrayBetweens, arrayBetween.length, arrayBetween2.length);
			*/
			ArrayList<String> cadenasAReemplazarFirst=new ArrayList<String>();

			for (String cadena : arrayBetween) {
				 if(cadena.contains("["+id_red_social)){
					 cadenasAReemplazarFirst.add(inicioBusqueda + cadena + finBusqueda + "|"+cadena);
				 };
			 }
			 /*for (String cadena : arrayBetween2) {
				 System.out.println("--->cadena2: "+cadena);
				 if(cadena.contains("["+id_red_social)){
					 cadenasAReemplazarFirst.add(ini2 + cadena + fin + "|"+cadena);
				 };
			 }*/
			 
			 for (String string : cadenasAReemplazarFirst) {
				 rtfContenido=rtfContenido.replace(string.split("\\|")[0], string.split("\\|")[1]);
			}
		} catch (Exception e) {
			logger.error("Error limpiaRedSocial: ",e);
			return rtfContenido;
		}
		return rtfContenido;
	}

	public static void main(String [] args){
		try {
			
			
			//String codigo="3fbbd1ef449b4dfd858a6fa23a2e03b3?tweaks=android-enable-hls";
			String codigo="3fbbd1ef449b4dfd858a6fa23a2e03b3";
			codigo=codigo.substring(0,codigo.indexOf('?'));
			
			/*if(codigo.contains("?")){
				codigo=codigo.substring(0,codigo.indexOf('?'));
			}else
			System.out.println("No le ahcemos nada poreu no trae param");*/
			System.out.println("Codigo: "+codigo);
			
			
			
			
			
			
			
			
			
			
			
			String contenido=
					"<p dir=\"ltr\">Este fin de semana habr� <strong>horario especial en los sistemas de transporte p�blico </strong>de la Ciudad de M�xico como son el <strong>Metro, Metrob�s y Tren Suburbano</strong>, as� que planea tus actividades con tiempo para que no te quedes parado.</p>"+

					"<h3 dir=\"ltr\">�Que no te sorprendan!</h3>"+

					"<p dir=\"ltr\" style=\"text-align: justify;\">[giphy=http://gph.is/2cLPxb9,http://i.giphy.com/XOWWwrOqRjqpi.gif=giphy]</p>"+

					"<h3 dir=\"ltr\">Los horarios del Metro</h3>"+

					"<p dir=\"ltr\">Para el <strong>s�bado 24 de diciembre ser� de 06:00 a 23:00 horas</strong>, mientras que el domingo <strong>ser� de 07:00 a 00:00 horas.</strong></p>"+

					"<ul dir=\"ltr\">"+
					"	<li><strong>La noche del s�bado, los �ltimos trenes saldr�n de las terminales a las 22:30 horas para concluir su recorrido alrededor de las 23:00 horas.</strong></li>"+
					"</ul>"+

					"<p dir=\"ltr\">El domingo 25, los usuarios podr�n ingresar con bicicleta a las 12 l�neas de la red como parte del programa �Los domingos y d�as festivos, tu bici viaja en Metro�.</p>"+

					"<p dir=\"ltr\">[twitter=https://twitter.com/MetroCDMX/status/812082735883816961=twitter]</p>"+

					"<h3 dir=\"ltr\">El Metrob�s</h3>"+

					"<p dir=\"ltr\">Comenzar� a operar <strong>este s�bado a las 04:00 horas y el �ltimo cami�n saldr� a las 22:00 horas</strong> y el<strong> domingo, el servicio se brindar� de 05:00 a 00:00 horas.</strong></p>"+

					"<p dir=\"ltr\">[twitter=https://twitter.com/MetrobusCDMX/status/812312197548412929=twitter]</p>"+

					"<h3 dir=\"ltr\">Tren Suburbano</h3>"+

					"<p dir=\"ltr\">El horario del Tren Suburbano para el <strong>s�bado ser� de 06:00 a 23:00 horas</strong> con frecuencias de trenes de 10 a 15 minutos, mientras que <strong>el domingo</strong> su horario ser� el habitual, es decir, de <strong>07:00 a 00:30 horas</strong> con salidas cada 15 minutos.</p>"+

					"<p dir=\"ltr\">[twitter=https://twitter.com/Suburbanos/status/812064337150017537=twitter]</p>"+

					"<p dir=\"ltr\">A su vez, EcoParq inform� que este s�bado, los parqu�metros en Roma, Hip�dromo y Polanco operar�n en sus horarios regulares, es decir de 8:00 a 1:00 horas del d�a siguiente.</p>"+

					"<h3 dir=\"ltr\">Env�a tu denuncia a Uno TV:</h3>"+

					"<p dir=\"ltr\">Si tienes fotos o videos de inter�s, comp�rtelos con nosotros en el WhatsApp de Uno TV: 5562115131 o en nuestro Twitter <a href=\"https://twitter.com/UnoNoticias\" target=\"_blank\" title=\"\">@UnoNoticias</a>.</p>"+

					"<h3 dir=\"ltr\">Te puede interesar:</h3>"+

					"<ul dir=\"ltr\">"+
						"<li><a href=\"http://www.unotv.com/noticias/estados/distrito-federal/detalle/hoy-no-circula-queda-suspendido-para-el-24-de-diciembre-635050/\" target=\"_blank\" title=\"\">Hoy No Circula queda suspendido para el 24 de diciembre</a></li>"+
					"</ul>";				
			String ini="<p dir=\"ltr\" style=\"text-align: justify;\">";
			String ini2="<p dir=\"ltr\">";
			String fin="</p>";
			
			String code="";
			int i=4;
			double d=4.0;
			String s="hackermark";
			Scanner scan=new Scanner(System.in);
			
			 /* Declare second integer, double, and String variables. */
	        int i2=0;
	        double d2=0.0;
	        String cadena="";
	        /* Read and save an integer, double, and String to your variables.*/
	        // Note: If you have trouble reading the entire String, please go back and review the Tutorial closely.
	        i2=scan.nextInt();
	        d2=scan.nextDouble();
	        cadena=scan.nextLine();
	          
	        /* Print the sum of both integer variables on a new line. */
	        //System.out.println(i + i2);
	        /* Print the sum of the double variables on a new line. */
			//System.out.println(d + d2);
	        /* Concatenate and print the String variables on a new line; 
	        	the 's' variable above should be printed first. */
			
			//System.out.println("cadena: "+cadena);
	        //System.out.println(s + cadena);
	        
	        scan.close();
			//contenido=limpiaRedSocial(ini2, fin, "twitter", contenido);
			//contenido=limpiaRedSocial(ini, fin, "giphy", contenido);
			
			
 		
 		

			 
			 
			 
			 
			String rtfContenido="Los boxeadores profesionales, sin excluir a superestrellas como Floyd Mayweather o Manny Pacquiao, podr�an realizar su debut ol�mpico en R�o 2016 seg�n los planes del presidente de la AIBA, Asociaci�n Internacional de Boxeo adscrita al Comit� Ol�mpico Internacional, Wu Ching-kuo. Este, en el transcurso de una reuni�n de su Asociacion en Gran Breta�a, se�al� que los criterios de elegibilidad ol�mpica pueden ser modificados en breve. Este cambio ser� tratado en la misma, si bien luego debe ser ratificado por la Comisi�n Ejecutiva en caso de ser aprobados.Pocas dudas hay de que lo ser�n, sin embargo. Wu ha se�alado que el boxeo es \"el �nico deporte sin sus profesionales en los Juegos. El COI y las Federaciones queremos que est�n los mejores. De acuerdo con nuestros estatutos es posible realizar el cambio de forma inmediata. Es, adem�s, el 70 aniversario de la AIBA y queremos hacer algo especial\".La AIBA lleva tiempo, en un proceso acelerado por la llegada de Wu a la presidencia, tratando de entrar en el mundo del boxeo profesional. Seg�n algunas opiniones, tratando tambi�n de dar la batalla a las organizaciones puramente profesionales (WBA, WBO, WBC, IBF...) aprovechando para ello tambi�n su vinculaci�n al COI, dado que s�lo podr�an participar en los Juegos aquellos p�giles con licencia AIBA. As�, se ha eliminado la palabra 'amateur' de su nombre, se han eliminado el casco y la camiseta de muchos de sus eventos masculinos, y se ha establecido que no se considerar� profesional a quien haya disputado menos de 15 combates remunerados."+
					"Bar�a"+
					"<p dir=\"ltr\" style=\"text-align: justify;\"> aki va un parrafo veda</p>"+
					"Bar�a"+
					"FACEBOOK"+
					"[facebook=https://www.facebook.com/martavsilva10/photos/pb.100433703438266.-2207520000.1457207815./101605036654466/?type=3&theater=facebook]"+
					"INSTAGRAM"+
					"[instagram=https://www.instagram.com/p/BCP4DShq_0F/?taken-by=musaliya135=instagram]"+
					"Adem�s, la AIBA ha organizado las Series Mundiales, una competici�n por equipos con pagos a los p�giles.Esta medida significar�a, al menos en teoria, que se podr�an ver en acci�n ya en R�o 2016 a algunas de las grandes estrellas del boxeo. Floyd Mayweather podr�a volver a los Juegos a unir a los t�tulos mundiales en cinco categor�as y sus cientos de millones de d�lares ganados el oro en peso pluma que se escap� en Atlanta 1996, derrotado por el b�lgaro Serafim Todorov.Su rival en la pelea por el mundial welter que bati� r�cords de expectaci�n y econ�micos, el ahora controvertido Manny Pacquiao, tambi�n podr�a entonces volver a los Juegos con todas las de ley, tras haber sido abanderado de Filipinas en Pek�n 2008. En aquella ocasi�n fue elegido el nadador Miguel Molina, pero la presidenta Gloria Macapagal pidi� que se le confiriera el honor al boxeador, que se hab�a proclamado pocos meses antes campe�n mundial del peso ligero WBC.Otros boxeadores, como Mohamed Al�, George Foreman o Lennox Lewis, entre otros, pasaron por los Juegos antes de iniciar exitosas trayectorias profesionales."+
					"FACEBOOK 2"+
					"[facebook=https://web.facebook.com/barackobama/videos/10154182550046749/=facebook]"+
					"Sin embargo, no se ha hablado de un cambio de reglamentos para el boxeo ol�mpico, que consta de tres asaltos, mientras un combate profesional se compone de 12. Otras voces se�alan que ser�a injusto enfrentar en los combates ol�mpicos a profesionales expertos con j�venes que realizan sus primeras armas en el boxeo. Y finalmente, est� por ver que grandes y supermillonarias estrellas, que no acostumbran a dar un paso gratuitamente, se sientan lo bastante atra�das por el oro ol�mpico como para disputar unos combates sin remuneraci�n, al menos directa."+
					"TWITTER"+
					"[twitter=https://twitter.com/missyfranklin/status/703369208382431232=twitter]"+
					"Y ya por ultimo un lindo gif..."+
					"[giphy=http://giphy.com/gifs/sassy-batman-ZuM7gif8TCvqU,http://i.giphy.com/rgg2PJ6VJTyPC.gif=giphy]"+
					"TWITTER 2"+
					"[twitter=https://twitter.com/yelenaisinbaeva/status/636795867622731776=twitter]"+
					"M�s de Instagram"+
					"La judoca mexicana, Vanessa Zambotti, tendr� el control total del Instagram de Claro Sports.As� como lo lees, tendremos #UnD�aConVanessaZambotti, donde la atleta mexicana compartir� con los seguidores de Claro Sports su vida diaria, desde los entrenamientos y muchas cosas m�s que no puedes perderte."+ 
					"[instagram=https://www.instagram.com/p/BCqE0Ixhfy1/?taken-by=clarosports=instagram]"+
					"As� que no lo dudes, s�guenos en Instagram @ClaroSports, porque tendremos un lunes muy especial."+
					"[instagram=https://www.instagram.com/p/BCqXA_ABf3x/?taken-by=clarosports=instagram]"+
					"WIDGET"+
					"[widget-elecciones-eeuu]"+
					"<p dir=\"ltr\">Si tienes fotos o videos de inter�s comp�rtelos con nosotros en el <strong>WhatsApp </strong>de <strong>Uno TV</strong>: 5562115131 o en nuestro Twitter <strong><a target=\"_blank\" title=\"\" href=\"https://twitter.com/UnoNoticias\">@UnoNoticias</a>.</strong></p>"+
					"<p dir=\"ltr\" style=\"text-align: justify;\"> aki va otro parrafo veda</p>"+
					"�Quieres ver las fotos que est� compartiendo Vanessa desde nuestra cuenta? Da clic AQU�."+
					"Y ya por ultimo un lindo gif 2..."+
					"[giphy=http://giphy.com/gifs/superman-funny-wdh1SvEn0E06I,http://i.giphy.com/wdh1SvEn0E06I.gif=giphy]"+
					"<p dir=\"ltr\">Si tienes fotos o videos de inter�s comp�rtelos con nosotros en el <strong>WhatsApp </strong>de <strong>Uno TV</strong>: 5562115131 o en nuestro Twitter <strong><a target=\"_blank\" title=\"\" href=\"https://twitter.com/UnoNoticias\">@UnoNoticias 2</a>.</strong></p>";
			
			//System.out.println(getEmbedPost(rtfContenido));
			
			
			
			
			/*String fechaUTM="2015-09-24T18:32:30Z";
			TimeZone tz = TimeZone.getTimeZone("CST");
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			df.setTimeZone(tz);
			Date date = df.parse(fechaUTM);
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			System.out.println("date--->"+date);
			System.out.println(formatter.format(date));
			//"2016-03-17 17:44:58.81"
			
			/*SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        String dateInString = "2015-09-24 18:32:30";
	        Date date = formatter.parse(dateInString);
            System.out.println("Date--->"+date);
            
            TimeZone tz = TimeZone.getTimeZone("CST");
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			df.setTimeZone(tz);
			String fechaUTM=df.format(date);
			System.out.println("fechaUTM String--->"+fechaUTM); */
			
			
			
			
			
			//2016-12-01T14:32:13Z
		} catch (Exception e) {
			System.out.println("Error main: "+e.getLocalizedMessage());
			e.printStackTrace();
		}
	}
}
