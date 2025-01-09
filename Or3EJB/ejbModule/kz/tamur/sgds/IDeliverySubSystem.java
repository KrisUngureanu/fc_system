/**
 * JacobGen generated file --- do not edit
 *
 * (http://www.sourceforge.net/projects/jacob-project */
package kz.tamur.sgds;

import com.jacob.com.*;

public class IDeliverySubSystem extends Dispatch {

	public static final String componentName = "ClientDS.IDeliverySubSystem";

	public IDeliverySubSystem() {
		super(componentName);
	}

	/**
	* This constructor is used instead of a case operation to
	* turn a Dispatch object into a wider object - it must exist
	* in every wrapper class whose instances may be returned from
	* method calls wrapped in VT_DISPATCH Variants.
	*/
	public IDeliverySubSystem(Dispatch d) {
		// take over the IDispatch pointer
		m_pDispatch = d.m_pDispatch;
		// null out the input's pointer
		d.m_pDispatch = 0;
	}

	public IDeliverySubSystem(String compName) {
		super(compName);
	}

	/**
	 * Wrapper for calling the ActiveX-Method with input-parameter(s).
	 * @param lastParam an input-parameter of type String
	 */
	public void putMessage(String lastParam) {
		Dispatch.call(this, "PutMessage", lastParam);
	}

	/**
	 * Wrapper for calling the ActiveX-Method with input-parameter(s).
	 */
	public void commitOnPut() {
		Dispatch.call(this, "CommitOnPut");
	}

	/**
	 * Wrapper for calling the ActiveX-Method with input-parameter(s).
	 */
	public void rollBackOnPut() {
		Dispatch.call(this, "RollBackOnPut");
	}

	/**
	 * Wrapper for calling the ActiveX-Method with input-parameter(s).
	 * @param lastParam an input-parameter of type String
	 */
	public void getMessage(String lastParam) {
		Dispatch.call(this, "GetMessage", lastParam);
	}

	/**
	 * Wrapper for calling the ActiveX-Method and receiving the output-parameter(s).
	 * @param lastParam is an one-element array which sends the input-parameter
	 *                  to the ActiveX-Component and receives the output-parameter
	 */
	public void getMessage(String[] lastParam) {
		Variant vnt_lastParam = new Variant();
		if( lastParam == null || lastParam.length == 0 )
			vnt_lastParam.noParam();
		else
			vnt_lastParam.putStringRef(lastParam[0]);

		Dispatch.call(this, "GetMessage", vnt_lastParam);

		if( lastParam != null && lastParam.length > 0 )
			lastParam[0] = vnt_lastParam.toString();
	}

	/**
	 * Wrapper for calling the ActiveX-Method with input-parameter(s).
	 */
	public void commitOnGet() {
		Dispatch.call(this, "CommitOnGet");
	}

	/**
	 * Wrapper for calling the ActiveX-Method with input-parameter(s).
	 */
	public void rollBackOnGet() {
		Dispatch.call(this, "RollBackOnGet");
	}

	/**
	 * Wrapper for calling the ActiveX-Method with input-parameter(s).
	 * @param lastParam an input-parameter of type byte
	 */
	public void initConnection(byte lastParam) {
		Dispatch.call(this, "InitConnection", lastParam);
	}

	/**
	 * Wrapper for calling the ActiveX-Method with input-parameter(s).
	 * @param appName an input-parameter of type String
	 * @param lastParam an input-parameter of type String
	 */
	public void appRegistration(String appName, String lastParam) {
		Dispatch.call(this, "AppRegistration", appName, lastParam);
	}

	/**
	 * Wrapper for calling the ActiveX-Method with input-parameter(s).
	 * @return the result is of type int
	 */
	public Variant getOutBoxMessages() {
		return Dispatch.get(this, "OutBoxMessages");
	}

	/**
	 * Wrapper for calling the ActiveX-Method with input-parameter(s).
	 * @return the result is of type int
	 */
	public Variant getInBoxMessages() {
		return Dispatch.get(this, "InBoxMessages");
	}

	/**
	 * Wrapper for calling the ActiveX-Method with input-parameter(s).
	 * @param lastParam an input-parameter of type String
	 */
	public void getDSName(String lastParam) {
		Dispatch.call(this, "GetDSName", lastParam);
	}

	/**
	 * Wrapper for calling the ActiveX-Method and receiving the output-parameter(s).
	 * @param lastParam is an one-element array which sends the input-parameter
	 *                  to the ActiveX-Component and receives the output-parameter
	 */
	public void getDSName(String[] lastParam) {
		Variant vnt_lastParam = new Variant();
		if( lastParam == null || lastParam.length == 0 )
			vnt_lastParam.noParam();
		else
			vnt_lastParam.putStringRef(lastParam[0]);

		Dispatch.call(this, "GetDSName", vnt_lastParam);

		if( lastParam != null && lastParam.length > 0 )
			lastParam[0] = vnt_lastParam.toString();
	}

}
