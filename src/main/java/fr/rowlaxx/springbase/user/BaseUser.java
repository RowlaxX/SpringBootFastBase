package fr.rowlaxx.springbase.user;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import fr.rowlaxx.springbase.core.BaseEntity;
import fr.rowlaxx.springbase.user.bannedreason.BannedReason;
import fr.rowlaxx.springbase.user.resetpassword.ResetPasswordCode;
import fr.rowlaxx.springbase.user.verification.VerificationCode;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.FetchType;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@MappedSuperclass
public abstract class BaseUser extends BaseEntity implements UserDetails {
	private static final long serialVersionUID = -8939910233142484381L;
	private static final int AUTH_SIZE = 64;
	
	@JsonIgnore
	@ElementCollection(fetch = FetchType.LAZY)
	@Column(length = AUTH_SIZE, nullable = false)
	private final Set<String> authorities = new HashSet<>();
	
	/*
	 * Credentials
	 */
	@JsonIgnore
	@Column(length = 64)
	private String password = null;
	
	@Column(length = 128, unique = true)
	private String email;
	
	@Column(length = 32, unique = true)
	private String phone;
	
	@JsonIgnore
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	private BannedReason bannedReason = null;
	
	@JsonIgnore
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	private ResetPasswordCode resetPasswordCode = null;
	
	/*
	 * Verification
	 */
	private boolean emailVerified = true;
	private boolean phoneVerified = false;
	
	@JsonIgnore
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	private VerificationCode emailVerificationCode = null;
	
	@JsonIgnore
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	private VerificationCode phoneVerificationCode = null;
	
	
	/*
	 * Roles / Permissions
	 */
	@Override
	public Set<GrantedAuthority> getAuthorities() {
		return authorities.stream()
				.map(a -> (GrantedAuthority)(() -> a))
				.collect(Collectors.toUnmodifiableSet());
	}

	
	
	
	/*
	 * Methods
	 */
	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return bannedReason == null || bannedReason.isExpired();
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
	
	@JsonInclude
	public String getType() {
		return getClass().getSimpleName();
	}
	
	public void setBannedReason(String description) {
		setBannedReason(description, null);
	}
	
	public void setBannedReason(String description, Duration duration) {
		var b = (bannedReason = new BannedReason());
		b.setDescription(description);
		b.setExpirationPeriod(duration);
	}
	
	@JsonInclude
	public abstract String getHomePageUrl();
}
