package mx.com.amx.unotv.app.vo;

import java.io.Serializable;

public class BusquedaVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String q;
	private String res_start;
	
	/**
	 * @return the q
	 */
	public String getQ() {
		return q;
	}
	/**
	 * @param q the q to set
	 */
	public void setQ(String q) {
		this.q = q;
	}
	/**
	 * @return the res_start
	 */
	public String getRes_start() {
		return res_start;
	}
	/**
	 * @param res_start the res_start to set
	 */
	public void setRes_start(String res_start) {
		this.res_start = res_start;
	}
	
	
}
