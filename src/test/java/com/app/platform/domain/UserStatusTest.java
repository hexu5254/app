package com.app.platform.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserStatusTest {

	@Test
	void fromCode_andAllowsLogin() {
		assertThat(UserStatus.fromCode((short) 1).allowsLogin()).isTrue();
		assertThat(UserStatus.fromCode((short) 2).allowsLogin()).isFalse();
		assertThatThrownBy(() -> UserStatus.fromCode((short) 99)).isInstanceOf(IllegalArgumentException.class);
	}
}
