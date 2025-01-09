package kz.tamur.or3.client.plugins;

import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import kz.tamur.comps.OrFrame;
import kz.tamur.rt.Utils;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.rt.MainFrame;
import kz.tamur.rt.orlang.ClientOrLang;
import kz.tamur.rt.orlang.ClientPlugin;
import kz.tamur.util.Funcs;
import kz.zorsoft.KorganKLib;

public class KorganSigner implements ClientPlugin {

	private static KorganKLib klib = new KorganKLib();

	private static byte[] serial = null;
	private static byte[] pd = null;
	private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
	
	public boolean init() {
		if (serial == null) {
			SignerPanel sp = new SignerPanel();
			OrFrame frame = ClientOrLang.getFrame();
			Container parent = frame != null ? ((JPanel) frame
					.getPanel()).getTopLevelAncestor() : null;
			DesignerDialog dlg = (parent instanceof Dialog) ? new DesignerDialog(
					(Dialog) parent, "", sp) : new DesignerDialog(
					(Frame) parent, "", sp);
			dlg.setVisible(true);
			if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
				Device dev = sp.getDevice();
				if (dev != null) {
					serial = dev.serial;
					char[] chs = sp.getPassword();
					if (chs != null) {
						pd = new String(chs).getBytes();
					}
					return true;
				}
			}
			return false;
		}
		return true;
	}

	public byte[] exportPublicKey() throws Exception {

		if (!init())
			error("Устройство не выбрано.");
			

		int DeviceHandle = klib.nativeKZ_OpenDevice(serial);
		if (DeviceHandle == -1) {
			System.out.println(klib.nativeKZ_GetLastErrorMsg(klib
					.nativeKZ_GetLastLibraryError(DeviceHandle)));
			return null;
		}
		klib.nativeKZ_ActivateDevice(DeviceHandle, pd, 1, 1);

		byte pubkey[] = new byte[168];
		int retval = klib.nativeKZ_ExportSignY(DeviceHandle, pubkey);
		if (retval == 0) {
			klib.nativeKZ_CloseDevice(DeviceHandle);
			System.out.println(klib.nativeKZ_GetLastErrorMsg(klib
					.nativeKZ_GetLastLibraryError(DeviceHandle)));
			return null;
		}
		klib.nativeKZ_CloseDevice(DeviceHandle);
		return pubkey;
	}

	public byte[] sign(byte[] data) throws Exception {

		if (!init())
			error("Устройство не выбрано.");

		int DeviceHandle = klib.nativeKZ_OpenDevice(serial);

		if (DeviceHandle == -1) {
			error(klib.nativeKZ_GetLastErrorMsg(
					klib.nativeKZ_GetLastLibraryError(DeviceHandle)));
		}
		
		try {
			klib.nativeKZ_ActivateDevice(DeviceHandle, pd, 1, 1);
	
			byte[] hash = new byte[64];
			int retval = klib.nativeKZ_HashGost(DeviceHandle, data, data.length,
					hash);
			if (retval == 0) {
				error(klib.nativeKZ_GetLastErrorMsg(
						klib.nativeKZ_GetLastLibraryError(DeviceHandle)));
			}
	
			byte pSign[] = new byte[168];
	
			retval = klib.nativeKZ_SignHash(DeviceHandle, hash, 64, pSign);
			if (retval == 0) {
				error(klib.nativeKZ_GetLastErrorMsg(
						klib.nativeKZ_GetLastLibraryError(DeviceHandle)));
			}
			return pSign;
		} finally {
			klib.nativeKZ_CloseDevice(DeviceHandle);
		}
	}
	
	public byte[] sign(File file) throws Exception {
		return sign(Funcs.read(file));
	}


	public boolean verify(byte[] data, byte[] sign, byte[] pubkey) throws Exception {

		if (!init())
			error("Устройство не выбрано.");

		int DeviceHandle = klib.nativeKZ_OpenDevice(serial);

		if (DeviceHandle == -1) {
			error(klib.nativeKZ_GetLastErrorMsg(
					klib.nativeKZ_GetLastLibraryError(DeviceHandle)));
		}
		try {
	
			klib.nativeKZ_ActivateDevice(DeviceHandle, pd, 1, 1);
			
			byte[] hash = new byte[64];
			int retval = klib.nativeKZ_HashGost(DeviceHandle, data, data.length,
					hash);

			if (retval == 0) {
				error(klib.nativeKZ_GetLastErrorMsg(
						klib.nativeKZ_GetLastLibraryError(DeviceHandle)));
			}
	
			retval = klib.nativeKZ_NotarizeHash(DeviceHandle, hash, hash.length, sign, pubkey);
			if (retval == 0) {
				error(klib.nativeKZ_GetLastErrorMsg(
						klib.nativeKZ_GetLastLibraryError(DeviceHandle)));
			}
			return true;
		} finally {
			klib.nativeKZ_CloseDevice(DeviceHandle);
		}
	}

	public boolean verify(File file, File signFile, File pubkeyFile) throws Exception {
		byte[] data = Funcs.read(file);
		byte[] sign = Funcs.read(signFile);
		byte[] pubkey = Funcs.read(pubkeyFile);
		return verify(data, sign, pubkey);
	}
	
	private void error(String msg) throws Exception {
		serial = null;
		pd = null;
		throw new Exception(msg);
	}
	
	private class SignerPanel extends JPanel {

		private JComboBox cbDev = Utils.createCombo();
		private JPasswordField tfPasswd = Utils.createDesignerPasswordField();

		public SignerPanel() {
		        setOpaque(isOpaque);
			setLayout(new GridBagLayout());
			GridBagConstraints cnr = new GridBagConstraints();
			add(Utils.createLabel("Устройство"), cnr);
			cnr.gridy = 1;
			cnr.insets = new Insets(5, 5, 5, 5);
			cnr.anchor = GridBagConstraints.EAST;
			add(Utils.createLabel("Пароль"), cnr);
			cnr.gridx = 1;
			cnr.anchor = GridBagConstraints.CENTER;
			cnr.fill = GridBagConstraints.HORIZONTAL;
			add(tfPasswd, cnr);
			cnr.gridy = 0;
			add(cbDev, cnr);

			int curindex = 0;
			byte[] serial = new byte[8];
			byte[] pcSerial = new byte[32];
			while (klib.nativeKZ_FindNextDevice(serial, curindex) != 0) {
				klib.nativeKZ_SerialTopcSerial(serial, pcSerial);
				String value = new String(pcSerial);
				cbDev.addItem(new Device(serial.clone(), value));
				curindex = curindex + 1;
			}
		}

		public Device getDevice() {
			return (Device) cbDev.getSelectedItem();
		}

		public char[] getPassword() {
			return tfPasswd.getPassword();
		}
	}

	private static final class Device {

		public final byte[] serial;
		public final String title;

		public Device(byte[] serial, String title) {
			this.serial = serial;
			this.title = title;
		}

		@Override
		public String toString() {
			return title;
		}
	}

	public static void main(String[] args) throws Exception {
		KorganSigner signer = new KorganSigner();
		byte[] pkey = signer.exportPublicKey();
	}
}
