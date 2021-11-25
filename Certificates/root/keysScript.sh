openssl genrsa -out root_private.key
openssl rsa -pubout -in root_private.key -inform PEM -out root_public.key -outform DER

