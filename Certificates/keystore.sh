keytool -genkey -keystore keystore.jks -alias tomcat -keyalg RSA -keysize 2048 -ext ExtendedKeyUsage=serverAuth
keytool -importcert -keystore keystore.jks -alias ca -file root/root.pem -trustcacerts
keytool -genkey -keystore truststore.jks -alias trust -keyalg RSA -keysize 2048 -ext ExtendedKeyUsage=clientAuth
keytool -importcert -keystore truststore.jks -alias ca -file root/root.pem -trustcacerts
