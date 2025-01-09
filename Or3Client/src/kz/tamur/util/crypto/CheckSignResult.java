package kz.tamur.util.crypto;

public class CheckSignResult {
	// Типы ошибок проверки эцп/сертификата
	public static final int ECP_AND_CERT_OK = 0;
	public static final int NO_ECP_FOUND = 10;
	public static final int ECP_DAMAGED = 11;

	public static final int CERT_EXPIRED = 1;
	public static final int CERT_NOT_YET_VALID = 2;
	public static final int CERT_SIGN_ERROR = 3;
	public static final int CERT_CRL_NO_ACCESS = 4;
	public static final int CERT_REVOKED = 5;
	public static final int CERT_NOT_FOR_SIGN = 6;
	public static final int CERT_NOT_FOR_AUTH = 6;
	public static final int CERT_OCSP_ERROR = 7;
	public static final int CERT_OTHER_ERROR = 8;
	public static final int NO_CERT_FOUND = 9;
	public static final int WRONG_PASSWORD = 12;
	public static final int WRONG_FILE_FORMAT = 13;

	// Типы физических лиц - владельцев подписи
	public static final int FL_SIMPLE = 0; // физ.лицо
	public static final int UL_HEAD = 1; // руководитель
	public static final int UL_DEPUTY = 2; // с правом подписи
	public static final int UL_FINANCE = 3; // фин. документы
	public static final int UL_HR = 4; // отдел кадров
	public static final int UL_EMPLOYEE = 5; // обычный сотрудник

	private boolean digiSignOK = false;
	private boolean certOK = false;
	private boolean certNew = false;
	private boolean certUCGO = false;
	private int certError = 0;
	private String signerDN = null;
	private String signerIIN = null;
	private String signerBIN = null;
	private int signerType = -1;

	private boolean forAuth = false;
	private boolean forSign = false;

	public boolean isOK() {
		return digiSignOK && certOK;
	}

	public boolean isDigiSignOK() {
		return digiSignOK;
	}

	public void setDigiSignOK(boolean digiSignOK) {
		this.digiSignOK = digiSignOK;
	}

	public boolean isCertOK() {
		return certOK;
	}

	public void setCertOK(boolean certOK) {
		this.certOK = certOK;
	}

	public boolean isCertNew() {
		return certNew;
	}

	public void setCertNew(boolean certNew) {
		this.certNew = certNew;
	}

	public boolean isCertUCGO() {
		return certUCGO;
	}

	public void setCertUCGO(boolean certUCGO) {
		this.certUCGO = certUCGO;
	}
	
	public boolean isForAuth() {
		return forAuth;
	}

	public void setForAuth(boolean forAuth) {
		this.forAuth = forAuth;
	}

	public boolean isForSign() {
		return forSign;
	}

	public void setForSign(boolean forSign) {
		this.forSign = forSign;
	}

	public int getCertError() {
		return certError;
	}

	public void setCertError(int certError) {
		this.certError = certError;
	}

	public String getSignerDN() {
		return signerDN;
	}

	public void setSignerDN(String signerDN) {
		this.signerDN = signerDN;
	}

	public String getSignerIIN() {
		return signerIIN;
	}

	public void setSignerIIN(String signerIIN) {
		this.signerIIN = signerIIN;
	}

	public String getSignerBIN() {
		return signerBIN;
	}

	public void setSignerBIN(String signerBIN) {
		this.signerBIN = signerBIN;
	}

	public int getSignerType() {
		return signerType;
	}

	public void setSignerType(int signerType) {
		this.signerType = signerType;
	}

	public String getErrorMessage(boolean auth) {
		if (certError == NO_ECP_FOUND)
			return "ЭЦП не найдена!";
		else if (!isDigiSignOK())
			return "ЭЦП нарушено";
		else if (!isCertOK()) {
			switch (certError) {
			case CERT_EXPIRED:
				return "Сертификат просрочен";
			case CERT_NOT_YET_VALID:
				return "Сертификат еще не действует";
			case CERT_SIGN_ERROR:
				return "Сертификат не подписан доверенным центром";
			case CERT_CRL_NO_ACCESS:
				return "Не доступен список отозванных сертификатов";
			case CERT_REVOKED:
				return "Сертификат отозван";
			case CERT_NOT_FOR_SIGN:
				return auth ? "Сертификат не предназначен для авторизации" : "Сертификат не предназначен для подписи";
			case CERT_OCSP_ERROR:
				return "Ошибка сервиса OCSP";
			case CERT_OTHER_ERROR:
				return "Неизвестная ошибка проверки сертификата";
			case NO_CERT_FOUND:
				return "Сертификат не найден";
			case WRONG_PASSWORD:
				return "Неверный пароль к закрытому ключу";
			case WRONG_FILE_FORMAT:
				return "Неверный формат файла";
			}
		}
		return "";
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("CheckSignResult:");
		sb.append("\nDigiSignOK: ").append(digiSignOK);
		sb.append("\nCertOK: ").append(certOK);
		sb.append("\nCertNew: ").append(certNew);
		sb.append("\nCertUCGO: ").append(certUCGO);
		sb.append("\nAuth: ").append(forAuth);
		sb.append("\nSign: ").append(forSign);
		
		sb.append("\nSignerType: ").append(signerType);
		sb.append("\nDN: ").append(signerDN);
		sb.append("\nBIN: ").append(signerBIN);
		sb.append("\nIIN: ").append(signerIIN);
		
		sb.append("\nError: ").append(certError);
		sb.append("\n").append(getErrorMessage(true));
		return sb.toString();
	}
}
