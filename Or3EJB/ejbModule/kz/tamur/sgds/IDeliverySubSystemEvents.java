/**
 * JacobGen generated file --- do not edit
 *
 * (http://www.sourceforge.net/projects/jacob-project */
package kz.tamur.sgds;

import com.jacob.com.*;

public class IDeliverySubSystemEvents extends Dispatch {

	public static final String componentName = "ClientDS.IDeliverySubSystemEvents";

	public IDeliverySubSystemEvents() {
		super(componentName);
	}

	/**
	* This constructor is used instead of a case operation to
	* turn a Dispatch object into a wider object - it must exist
	* in every wrapper class whose instances may be returned from
	* method calls wrapped in VT_DISPATCH Variants.
	*/
	public IDeliverySubSystemEvents(Dispatch d) {
		// take over the IDispatch pointer
		m_pDispatch = d.m_pDispatch;
		// null out the input's pointer
		d.m_pDispatch = 0;
	}

	public IDeliverySubSystemEvents(String compName) {
		super(compName);
	}

	/**
	 * Wrapper for calling the ActiveX-Method with input-parameter(s).
	 * @return the result is of type int
	 */
	public int onNewMessage() {
		return Dispatch.call(this, "OnNewMessage").toInt();
	}

}
