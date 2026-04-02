package com.app.platform;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordEncoderBeanTest {

	@Test
	void encodeAndMatch_roundTrip() {
		PasswordEncoder encoder = new BCryptPasswordEncoder(10);
		String raw = "my-UTF8-密码";
		String hash = encoder.encode(raw);
		assertThat(hash).isNotBlank();
		assertThat(encoder.matches(raw, hash)).isTrue();
		assertThat(encoder.matches("wrong", hash)).isFalse();
	}
}
