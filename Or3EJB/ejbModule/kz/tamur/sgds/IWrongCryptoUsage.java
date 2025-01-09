/**
 * JacobGen generated file --- do not edit
 *
 * (http://www.sourceforge.net/projects/jacob-project */
package kz.tamur.sgds;

import com.jacob.com.*;

public class IWrongCryptoUsage extends Dispatch {

	public static final String componentName = "ClientDS.IWrongCryptoUsage";

	public IWrongCryptoUsage() {
		super(componentName);
	}

	/**
	* This constructor is used instead of a case operation to
	* turn a Dispatch object into a wider object - it must exist
	* in every wrapper class whose instances may be returned from
	* method calls wrapped in VT_DISPATCH Variants.
	*/
	public IWrongCryptoUsage(Dispatch d) {
		// take over the IDispatch pointer
		m_pDispatch = d.m_pDispatch;
		// null out the input's pointer
		d.m_pDispatch = 0;
	}

	public IWrongCryptoUsage(String compName) {
		super(compName);
	}

	/**
	 * Wrapper for calling the ActiveX-Method with input-parameter(s).
	 * @param sourcePath an input-parameter of type String
	 * @param lastParam an input-parameter of type String
	 */
	public void setSign(String sourcePath, String lastParam) {
		Dispatch.call(this, "SetSign", sourcePath, lastParam);
	}

	/**
	 * Wrapper for calling the ActiveX-Method with input-parameter(s).
	 * @param sourcePath an input-parameter of type String
	 * @param destPath an input-parameter of type String
	 * @param lastParam an input-parameter of type String
	 */
	public void verifySign(String sourcePath, String destPath, String lastParam) {
		Dispatch.call(this, "VerifySign", sourcePath, destPath, lastParam);
	}

	/**
	 * Wrapper for calling the ActiveX-Method and receiving the output-parameter(s).
	 * @param sourcePath an input-parameter of type String
	 * @param destPath an input-parameter of type String
	 * @param lastParam is an one-element array which sends the input-parameter
	 *                  to the ActiveX-Component and receives the output-parameter
	 */
	public void verifySign(String sourcePath, String destPath, String[] lastParam) {
		Variant vnt_lastParam = new Variant();
		if( lastParam == null || lastParam.length == 0 )
			vnt_lastParam.noParam();
		else
			vnt_lastParam.putStringRef(lastParam[0]);

		Dispatch.call(this, "VerifySign", sourcePath, destPath, vnt_lastParam);

		if( lastParam != null && lastParam.length > 0 )
			lastParam[0] = vnt_lastParam.toString();
	}

}
