/**
 * JacobGen generated file --- do not edit
 *
 * (http://www.sourceforge.net/projects/jacob-project */
package kz.tamur.sgds;

import com.jacob.com.*;

public class IDeliverySubSystem2 extends Dispatch {

	public static final String componentName = "ClientDS.IDeliverySubSystem2";

	public IDeliverySubSystem2() {
		super(componentName);
	}

	/**
	* This constructor is used instead of a case operation to
	* turn a Dispatch object into a wider object - it must exist
	* in every wrapper class whose instances may be returned from
	* method calls wrapped in VT_DISPATCH Variants.
	*/
	public IDeliverySubSystem2(Dispatch d) {
		// take over the IDispatch pointer
		m_pDispatch = d.m_pDispatch;
		// null out the input's pointer
		d.m_pDispatch = 0;
	}

	public IDeliverySubSystem2(String compName) {
		super(compName);
	}

	/**
	 * Wrapper for calling the ActiveX-Method with input-parameter(s).
	 * @param msg an input-parameter of type String
	 * @param lastParam an input-parameter of type String
	 */
	public void putMessage(String msg, String lastParam) {
		Dispatch.call(this, "PutMessage", msg, lastParam);
	}

	/**
	 * Wrapper for calling the ActiveX-Method and receiving the output-parameter(s).
	 * @param msg an input-parameter of type String
	 * @param lastParam is an one-element array which sends the input-parameter
	 *                  to the ActiveX-Component and receives the output-parameter
	 */
	public void putMessage(String msg, String[] lastParam) {
		Variant vnt_lastParam = new Variant();
		if( lastParam == null || lastParam.length == 0 )
			vnt_lastParam.noParam();
		else
			vnt_lastParam.putStringRef(lastParam[0]);

		Dispatch.call(this, "PutMessage", msg, vnt_lastParam);

		if( lastParam != null && lastParam.length > 0 )
			lastParam[0] = vnt_lastParam.toString();
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
	 * @param msg an input-parameter of type String
	 * @param lastParam an input-parameter of type String
	 */
	public void getMessage(String msg, String lastParam) {
		Dispatch.call(this, "GetMessage", msg, lastParam);
	}

	/**
	 * Wrapper for calling the ActiveX-Method and receiving the output-parameter(s).
	 * @param msg is an one-element array which sends the input-parameter
	 *            to the ActiveX-Component and receives the output-parameter
	 * @param lastParam is an one-element array which sends the input-parameter
	 *                  to the ActiveX-Component and receives the output-parameter
	 */
	public void getMessage(String[] msg, String[] lastParam) {
		Variant vnt_msg = new Variant();
		if( msg == null || msg.length == 0 )
			vnt_msg.noParam();
		else
			vnt_msg.putStringRef(msg[0]);

		Variant vnt_lastParam = new Variant();
		if( lastParam == null || lastParam.length == 0 )
			vnt_lastParam.noParam();
		else
			vnt_lastParam.putStringRef(lastParam[0]);

		Dispatch.call(this, "GetMessage", vnt_msg, vnt_lastParam);

		if( msg != null && msg.length > 0 )
			msg[0] = vnt_msg.toString();
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
	 * @param lastParam an input-parameter of type int
	 */
	public void initConnection(int lastParam) {
		Dispatch.call(this, "InitConnection", new Variant(lastParam));
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

	/**
	 * Wrapper for calling the ActiveX-Method with input-parameter(s).
	 * @param fPath an input-parameter of type String
	 * @param lastParam an input-parameter of type String
	 */
	public void getCMSToFile(String fPath, String lastParam) {
		Dispatch.call(this, "GetCMSToFile", fPath, lastParam);
	}

	/**
	 * Wrapper for calling the ActiveX-Method and receiving the output-parameter(s).
	 * @param fPath an input-parameter of type String
	 * @param lastParam is an one-element array which sends the input-parameter
	 *                  to the ActiveX-Component and receives the output-parameter
	 */
	public void getCMSToFile(String fPath, String[] lastParam) {
		Variant vnt_lastParam = new Variant();
		if( lastParam == null || lastParam.length == 0 )
			vnt_lastParam.noParam();
		else
			vnt_lastParam.putStringRef(lastParam[0]);

		Dispatch.call(this, "GetCMSToFile", fPath, vnt_lastParam);

		if( lastParam != null && lastParam.length > 0 )
			lastParam[0] = vnt_lastParam.toString();
	}

	/**
	 * Wrapper for calling the ActiveX-Method with input-parameter(s).
	 * @param fPath an input-parameter of type String
	 * @param lastParam an input-parameter of type String
	 */
	public void putCMSFromFile(String fPath, String lastParam) {
		Dispatch.call(this, "PutCMSFromFile", fPath, lastParam);
	}

	/**
	 * Wrapper for calling the ActiveX-Method and receiving the output-parameter(s).
	 * @param fPath an input-parameter of type String
	 * @param lastParam is an one-element array which sends the input-parameter
	 *                  to the ActiveX-Component and receives the output-parameter
	 */
	public void putCMSFromFile(String fPath, String[] lastParam) {
		Variant vnt_lastParam = new Variant();
		if( lastParam == null || lastParam.length == 0 )
			vnt_lastParam.noParam();
		else
			vnt_lastParam.putStringRef(lastParam[0]);

		Dispatch.call(this, "PutCMSFromFile", fPath, vnt_lastParam);

		if( lastParam != null && lastParam.length > 0 )
			lastParam[0] = vnt_lastParam.toString();
	}

	/**
	 * Wrapper for calling the ActiveX-Method with input-parameter(s).
	 * @param msg an input-parameter of type String
	 * @param lastParam an input-parameter of type String
	 */
	public void getBinMsg(String msg, String lastParam) {
		Dispatch.call(this, "GetBinMsg", msg, lastParam);
	}

	/**
	 * Wrapper for calling the ActiveX-Method and receiving the output-parameter(s).
	 * @param msg is an one-element array which sends the input-parameter
	 *            to the ActiveX-Component and receives the output-parameter
	 * @param lastParam is an one-element array which sends the input-parameter
	 *                  to the ActiveX-Component and receives the output-parameter
	 */
	public void getBinMsg(String[] msg, String[] lastParam) {
		Variant vnt_msg = new Variant();
		if( msg == null || msg.length == 0 )
			vnt_msg.noParam();
		else
			vnt_msg.putStringRef(msg[0]);

		Variant vnt_lastParam = new Variant();
		if( lastParam == null || lastParam.length == 0 )
			vnt_lastParam.noParam();
		else
			vnt_lastParam.putStringRef(lastParam[0]);

		Dispatch.call(this, "GetBinMsg", vnt_msg, vnt_lastParam);

		if( msg != null && msg.length > 0 )
			msg[0] = vnt_msg.toString();
		if( lastParam != null && lastParam.length > 0 )
			lastParam[0] = vnt_lastParam.toString();
	}

	/**
	 * Wrapper for calling the ActiveX-Method with input-parameter(s).
	 * @param msg an input-parameter of type String
	 * @param lastParam an input-parameter of type String
	 */
	public void putBinMsg(String msg, String lastParam) {
		Dispatch.call(this, "PutBinMsg", msg, lastParam);
	}

	/**
	 * Wrapper for calling the ActiveX-Method and receiving the output-parameter(s).
	 * @param msg an input-parameter of type String
	 * @param lastParam is an one-element array which sends the input-parameter
	 *                  to the ActiveX-Component and receives the output-parameter
	 */
	public void putBinMsg(String msg, String[] lastParam) {
		Variant vnt_lastParam = new Variant();
		if( lastParam == null || lastParam.length == 0 )
			vnt_lastParam.noParam();
		else
			vnt_lastParam.putStringRef(lastParam[0]);

        Variant vnt_msg = new Variant();
        vnt_msg.putStringRef(msg);

        Dispatch.call(this, "PutBinMsg", vnt_msg, vnt_lastParam);

		if( lastParam != null && lastParam.length > 0 )
			lastParam[0] = vnt_lastParam.toString();
	}

}
