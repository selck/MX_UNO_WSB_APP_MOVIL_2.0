package mx.com.amx.unotv.app.util;

import java.io.File;
import java.io.FileInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;

import org.apache.log4j.Logger;

import mx.com.amx.unotv.app.dto.ParametrosBuscadorDTO;
import mx.com.amx.unotv.app.dto.ParametrosDTO;

public class UtilMovil {
	
	public static Logger log = Logger.getLogger(UtilMovil.class);
	
	public static String getDateZoneTime(String fechaString){
		String fecha="";
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        Date date = formatter.parse(fechaString);
            
            TimeZone tz = TimeZone.getTimeZone("CST");
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			df.setTimeZone(tz);
			
			fecha=df.format(date);
		} catch (Exception e) {
			log.error("Error getDateZoneTime: ",e);
			return fechaString;
		}
		return fecha;
	}
	public static ParametrosBuscadorDTO obtenerPropiedadesBuscador(String properties) {
		ParametrosBuscadorDTO parametrosDTO = new ParametrosBuscadorDTO();		 
		try {	    		
			Properties propsTmp = new Properties();
			propsTmp.load(UtilMovil.class.getResourceAsStream( "/general.properties" ));
			String ambiente = propsTmp.getProperty("ambiente");
			String rutaProperties = propsTmp.getProperty(properties.replace("ambiente", ambiente));
			Properties props = new Properties();
			
			props.load(new FileInputStream(new File(rutaProperties)));				
			parametrosDTO.setDominio(props.getProperty("dominio"));
			parametrosDTO.setAmbiente(props.getProperty("ambiente"));
			parametrosDTO.setUrlBuscador(props.getProperty("urlBuscador"));
			parametrosDTO.setNumResultadosBusqueda(props.getProperty("numResultadosBusqueda"));
			
		} catch (Exception ex) {
			parametrosDTO = new ParametrosBuscadorDTO();
			log.error("No se encontro el Archivo de propiedades: ", ex);			
		}
		return parametrosDTO;
    }
	
	public static ParametrosDTO obtenerPropiedades(String properties) {
		ParametrosDTO parametrosDTO = new ParametrosDTO();		 
		try {	    		
			Properties propsTmp = new Properties();
			propsTmp.load(UtilMovil.class.getResourceAsStream( "/general.properties" ));
			String ambiente = propsTmp.getProperty("ambiente");
			//logger.info("ambiente: "+ambiente);
			String rutaProperties = propsTmp.getProperty(properties.replace("ambiente", ambiente));
			//logger.info("rutaProperties: "+rutaProperties);
			Properties props = new Properties();
			
			props.load(new FileInputStream(new File(rutaProperties)));				
			parametrosDTO.setAdsetcodeDeportes(props.getProperty("adsetcodeDeportes"));
			parametrosDTO.setAdsetcodeNoticias(props.getProperty("adsetcodeNoticias"));
			parametrosDTO.setPcodeDeportes(props.getProperty("pcodeDeportes"));
			parametrosDTO.setPcodeNoticias(props.getProperty("pcodeNoticias"));
			
		} catch (Exception ex) {
			parametrosDTO = new ParametrosDTO();
			log.error("No se encontro el Archivo de propiedades: ", ex);			
		}
		return parametrosDTO;
    }
}
