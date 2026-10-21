package com.enncompany.ecomuserauthservice.security;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Decrypts passwords sent as Base64(12-byte IV + AES-GCM ciphertext/tag). */
@Component
public class PasswordDecryptor {

  private static final int IV_LENGTH = 12;
  private static final int TAG_BITS = 128;

  private final SecretKeySpec key;

  public PasswordDecryptor(@Value("${app.security.password-key}") String base64Key) {
    this.key = new SecretKeySpec(Base64.getDecoder().decode(base64Key), "AES");
  }

  public Optional<String> decrypt(String encrypted) {
    try {
      byte[] data = Base64.getDecoder().decode(encrypted);
      Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
      cipher.init(
          Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_BITS, data, 0, IV_LENGTH));
      byte[] plain = cipher.doFinal(Arrays.copyOfRange(data, IV_LENGTH, data.length));
      return Optional.of(new String(plain, StandardCharsets.UTF_8));
    } catch (Exception e) {
      return Optional.empty();
    }
  }
}
