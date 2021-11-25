 package etf.sni.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Calendar;
import java.util.Date;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;
import org.bouncycastle.util.encoders.Base64;

import etf.sni.dao.UserDAO;
import etf.sni.dto.User;


public class RegistrationController {
	
	private static final String BC_PROVIDER = "BC";
    private static final String KEY_ALGORITHM = "RSA";
    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";
    private static final String KEYSTORE_TYPE = "PKCS12";
    private static final String KEYSTORE_PASS = "sigurnost";
    
    private static final String CERTS_PATH = "C:\\Users\\EC\\eclipse-workspace-2021-09\\SecureChatApp\\Certificates\\";
    
	private static RegistrationController registrationController;
	
	private RegistrationController() {
		File rootCert = new File(CERTS_PATH + "root\\root.pem");
		if(!rootCert.exists())
			generateRoot();
	}
	
	public static RegistrationController getRegistrationController() {
		if(registrationController == null)
			registrationController = new RegistrationController();
		return registrationController;
	}
	
	public boolean isUsernameUsed(String username) {
		return UserDAO.isUsernameUsed(username);
	}
	
	public boolean isMailAddressUsed(String email) {
		return UserDAO.isMailAddressUsed(email);
	}
	
	public boolean writeUserData(User user) {
		return UserDAO.insertUser(user);
	}
	
	public boolean sendGeneratedCertificate(String username) {
		String certName = username + ".pem";
		String certPath = CERTS_PATH + "userCerts\\" + certName;
		String email = UserDAO.getEmailByUsername(username);
		
		MailController mailController = MailController.getMailController();
		
		return mailController.sendMailWithAttachment(email, certName, certPath);
	}
	
	//Generisanje root sertifikata
	private void generateRoot() {
		
		try {
			Security.addProvider(new BouncyCastleProvider());
			
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DATE, 0);
			Date startDate = calendar.getTime(); //Pocetak validnosti sertifikata
			calendar.add(Calendar.YEAR, 2);
			Date endDate = calendar.getTime(); //Kraj validnosti, traje 2 godine
			BigInteger certSN = new BigInteger(Long.toString(new SecureRandom().nextLong()));
			
			RSAPrivateKey rootPrivateKey = loadPrivateKey(CERTS_PATH + "root\\root_private.key");
			PublicKey rootPublicKey = loadPublicKey(CERTS_PATH + "root\\root_public.key");
				
			String subjectName = "CN=" + "SecureChatApp";
			X500Name certSubject = new X500Name(subjectName);
			
			JcaContentSignerBuilder contentSignerBuilder = new JcaContentSignerBuilder(SIGNATURE_ALGORITHM).setProvider(BC_PROVIDER);
			ContentSigner contentSigner = contentSignerBuilder.build(rootPrivateKey);
			
			X509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(certSubject, certSN, startDate, endDate, certSubject, rootPublicKey);
			
			certBuilder.addExtension(Extension.basicConstraints, true, new BasicConstraints(true));
			certBuilder.addExtension(Extension.extendedKeyUsage, true, new ExtendedKeyUsage(new KeyPurposeId[]{KeyPurposeId.id_kp_serverAuth}));
			
			X509CertificateHolder certHolder = certBuilder.build(contentSigner);
			X509Certificate cert = new JcaX509CertificateConverter().setProvider(BC_PROVIDER).getCertificate(certHolder);
			
			writeCertToFile(cert, CERTS_PATH + "root\\" + "root" + ".pem");
			exportToPkcs12(cert, rootPrivateKey, "root", CERTS_PATH + "root\\" + "root" + ".pfx");
			
		} catch(Exception e) {
			e.printStackTrace();
		}	
	}

	public boolean generateCertificate(String username) {
		boolean result = false;
		
		try {
			Security.addProvider(new BouncyCastleProvider());
			
			X509Certificate rootCert = loadCertificate(CERTS_PATH + "root\\root.pem");
			RSAPrivateKey rootKey = loadPrivateKey(CERTS_PATH + "root\\root_private.key");
			X500Name rootCertIssuer = new X500Name("CN=SecureChatApp");
			
			KeyPair keyPair = generateUserKeyPair();
			
			JcaContentSignerBuilder contentSignerBuilder = new JcaContentSignerBuilder(SIGNATURE_ALGORITHM).setProvider(BC_PROVIDER);
			ContentSigner contentSigner = contentSignerBuilder.build(rootKey);
			
			String subjectName = "CN=" + username;
			X500Name certSubject = new X500Name(subjectName);
			PKCS10CertificationRequestBuilder reqBuilder = new JcaPKCS10CertificationRequestBuilder(certSubject, keyPair.getPublic());
			PKCS10CertificationRequest certReq = reqBuilder.build(contentSigner);
			
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DATE, -1);
			Date startDate = calendar.getTime(); //Pocetak validnosti sertifikata
			calendar.add(Calendar.YEAR, 2);
			Date endDate = calendar.getTime(); //Kraj validnosti, traje 2 godine
			
			BigInteger certSN = new BigInteger(Long.toString(new SecureRandom().nextLong()));
			
			X509v3CertificateBuilder certBuilder = new X509v3CertificateBuilder(rootCertIssuer, certSN, startDate, endDate, certReq.getSubject(), certReq.getSubjectPublicKeyInfo());
			certBuilder.addExtension(Extension.basicConstraints, true, new BasicConstraints(false));
			certBuilder.addExtension(Extension.extendedKeyUsage, true, new ExtendedKeyUsage(new KeyPurposeId[]{KeyPurposeId.id_kp_clientAuth}));
			
			X509CertificateHolder certHolder = certBuilder.build(contentSigner);
			X509Certificate cert = new JcaX509CertificateConverter().setProvider(BC_PROVIDER).getCertificate(certHolder);
			
			cert.verify(rootCert.getPublicKey(), BC_PROVIDER);
			writeCertToFile(cert, CERTS_PATH + "userCerts\\" + username + ".pem");
			exportToPkcs12(cert, keyPair.getPrivate(), username, CERTS_PATH + "userCerts\\" + username + ".pfx");
			
			result = true;
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private KeyPair generateUserKeyPair() throws Exception {
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM, BC_PROVIDER);
		keyPairGenerator.initialize(2048);
		KeyPair keyPair = keyPairGenerator.generateKeyPair();
		return keyPair;
	}
	
	private void exportToPkcs12(Certificate cert, PrivateKey privateKey, String alias, String filePath) throws Exception {
		FileOutputStream fs = new FileOutputStream(filePath);
		KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE, BC_PROVIDER);
		keyStore.load(null, null);
		keyStore.setKeyEntry(alias, privateKey, null, new Certificate[]{cert});
		keyStore.store(fs, KEYSTORE_PASS.toCharArray());
	}
	
	// Citanje javnog kljuca u DER formatu!
	private PublicKey loadPublicKey(String keyPath) throws Exception {
		byte[] keyBytes = Files.readAllBytes(Paths.get(keyPath));

	    X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
	    
	    KeyFactory kf = KeyFactory.getInstance("RSA");
	    return kf.generatePublic(spec);
	}
	
	private RSAPrivateKey loadPrivateKey(String keyPath) throws Exception {
		String privateKeyPEM = readKeyFile(keyPath);
    	privateKeyPEM = privateKeyPEM.replace("-----BEGIN RSA PRIVATE KEY-----\n", "");
    	privateKeyPEM = privateKeyPEM.replace("-----END RSA PRIVATE KEY-----", "");
    	
    	byte[] bytes = Base64.decode(privateKeyPEM);
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(bytes);
		RSAPrivateKey privateKey = (RSAPrivateKey) KeyFactory.getInstance(KEY_ALGORITHM).generatePrivate(keySpec);
		
		return privateKey;
	}
	
	private String readKeyFile(String keyPath) throws IOException {
		String keyContent = "";
		String line = "";
		
		BufferedReader br = new BufferedReader(new FileReader(keyPath));
		while((line = br.readLine()) != null ) {
			keyContent += line + "\n";
		}
		br.close();
		
		return keyContent;
	}
	
	private static void writeCertToFile(X509Certificate cert, String filePath) throws Exception {
		FileOutputStream fs = new FileOutputStream(filePath);
		byte[] bytes = cert.getEncoded();
		fs.write(bytes);
		fs.close();
	}
	
	public X509Certificate loadCertificate(String certPath) throws Exception {
		CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
		return (X509Certificate) certFactory.generateCertificate(new FileInputStream(certPath));
	}
	
}
