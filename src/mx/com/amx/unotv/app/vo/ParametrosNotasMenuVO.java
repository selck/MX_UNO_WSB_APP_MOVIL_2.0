package mx.com.amx.unotv.app.vo;

import java.io.Serializable;

public class ParametrosNotasMenuVO implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String id_menu;
	private String tipo_menu;
	private String fecha;
	/**
	 * @return the id_menu
	 */
	public String getId_menu() {
		return id_menu;
	}
	/**
	 * @param id_menu the id_menu to set
	 */
	public void setId_menu(String id_menu) {
		this.id_menu = id_menu;
	}
	/**
	 * @return the tipo_menu
	 */
	public String getTipo_menu() {
		return tipo_menu;
	}
	/**
	 * @param tipo_menu the tipo_menu to set
	 */
	public void setTipo_menu(String tipo_menu) {
		this.tipo_menu = tipo_menu;
	}
	/**
	 * @return the fecha
	 */
	public String getFecha() {
		return fecha;
	}
	/**
	 * @param fecha the fecha to set
	 */
	public void setFecha(String fecha) {
		this.fecha = fecha;
	}
	
	

}
