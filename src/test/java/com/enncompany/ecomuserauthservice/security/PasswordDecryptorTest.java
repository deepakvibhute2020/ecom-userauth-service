package com.enncompany.ecomuserauthservice.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.Test;

class PasswordDecryptorTest {

  private static final String KEY = "ZWNvbS1sb2dpbi1rZXktMQ==";
  private final PasswordDecryptor decryptor = new PasswordDecryptor(KEY);

  static String encrypt(String keyB64, String plain) throws Exception {
    byte[] iv = new byte[12];
    new java.security.SecureRandom().nextBytes(iv);
    Cipher c = Cipher.getInstance("AES/GCM/NoPadding");
    c.init(
        Cipher.ENCRYPT_MODE,
        new SecretKeySpec(Base64.getDecoder().decode(keyB64), "AES"),
        new GCMParameterSpec(128, iv));
    byte[] ct = c.doFinal(plain.getBytes(StandardCharsets.UTF_8));
    byte[] out = new byte[iv.length + ct.length];
    System.arraycopy(iv, 0, out, 0, iv.length);
    System.arraycopy(ct, 0, out, iv.length, ct.length);
    return Base64.getEncoder().encodeToString(out);
  }

  @Test
  void decryptsValidPayload() throws Exception {
    assertThat(decryptor.decrypt(encrypt(KEY, "s3cret!"))).contains("s3cret!");
  }

  @Test
  void wrongKeyReturnsEmpty() throws Exception {
    String other = Base64.getEncoder().encodeToString("another-key-1234".getBytes());
    assertThat(decryptor.decrypt(encrypt(other, "pw"))).isEmpty();
  }

  @Test
  void invalidBase64ReturnsEmpty() {
    assertThat(decryptor.decrypt("***not base64***")).isEmpty();
  }

  @Test
  void tooShortPayloadReturnsEmpty() {
    assertThat(decryptor.decrypt(Base64.getEncoder().encodeToString(new byte[3]))).isEmpty();
  }
}
