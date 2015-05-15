package com.ag.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.TokenApprovalStore;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

/**
 * The Class OAuth2ServerConfig.
 */
@Configuration
public class OAuth2ServerConfig {

	/** The Constant RESOURCE_ID. */
	private static final String RESOURCE_ID = "mobile";

	/**
	 * The Class ResourceServerConfiguration.
	 */
	@Configuration
	@EnableResourceServer
	protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.springframework.security.oauth2.config.annotation.web.configuration
		 * .
		 * ResourceServerConfigurerAdapter#configure(org.springframework.security
		 * .
		 * oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer
		 * )
		 */
		@Override
		public void configure(ResourceServerSecurityConfigurer resources) {
			resources.resourceId(RESOURCE_ID).stateless(false);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.springframework.security.oauth2.config.annotation.web.configuration
		 * .
		 * ResourceServerConfigurerAdapter#configure(org.springframework.security
		 * .config.annotation.web.builders.HttpSecurity)
		 */
		@Override
		public void configure(HttpSecurity http) throws Exception {
			http.requestMatchers().antMatchers("/rest/**").and().authorizeRequests().antMatchers("/rest/employees").access("#oauth2.hasScope('read')").and().authorizeRequests()
					.antMatchers("/rest/logout").access("#oauth2.hasScope('read')");
		}

	}

	/**
	 * The Class AuthorizationServerConfiguration.
	 */
	@Configuration
	@EnableAuthorizationServer
	protected static class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

		/** The token store. */
		@Autowired
		private TokenStore tokenStore;

		/** The user approval handler. */
		@Autowired
		private UserApprovalHandler userApprovalHandler;

		/** The authentication manager. */
		@Autowired
		private AuthenticationManager authenticationManager;

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.springframework.security.oauth2.config.annotation.web.configuration
		 * .AuthorizationServerConfigurerAdapter#configure(org.springframework.
		 * security
		 * .oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer)
		 */
		@Override
		public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
			clients.inMemory().withClient("user1").resourceIds(RESOURCE_ID).authorizedGrantTypes("client_credentials").authorities("ROLE_CLIENT").scopes("read").secret("password1")
					.accessTokenValiditySeconds(200).and().withClient("user2").resourceIds(RESOURCE_ID).authorizedGrantTypes("client_credentials").authorities("ROLE_CLIENT").scopes("read")
					.secret("password2").accessTokenValiditySeconds(200);
		}

		/**
		 * Token store.
		 *
		 * @return token store
		 */
		@Bean
		public TokenStore tokenStore() {
			return new InMemoryTokenStore();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.springframework.security.oauth2.config.annotation.web.configuration
		 * .AuthorizationServerConfigurerAdapter#configure(org.springframework.
		 * security.oauth2.config.annotation.web.configurers.
		 * AuthorizationServerEndpointsConfigurer)
		 */
		@Override
		public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
			endpoints.tokenStore(tokenStore).userApprovalHandler(userApprovalHandler).authenticationManager(authenticationManager);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.springframework.security.oauth2.config.annotation.web.configuration
		 * .AuthorizationServerConfigurerAdapter#configure(org.springframework.
		 * security.oauth2.config.annotation.web.configurers.
		 * AuthorizationServerSecurityConfigurer)
		 */
		@Override
		public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
			oauthServer.allowFormAuthenticationForClients();
		}

	}

	/**
	 * The Class Stuff.
	 */
	protected static class Stuff {

		/** The client details service. */
		@Autowired
		private ClientDetailsService clientDetailsService;

		/** The token store. */
		@Autowired
		private TokenStore tokenStore;

		/**
		 * Approval store.
		 *
		 * @return approval store
		 * @throws Exception  exception
		 */
		@Bean
		public ApprovalStore approvalStore() throws Exception {
			TokenApprovalStore store = new TokenApprovalStore();
			store.setTokenStore(tokenStore);
			return store;
		}

		/**
		 * User approval handler.
		 *
		 * @return custom user approval handler
		 * @throws Exception  exception
		 */
		@Bean
		@Lazy
		@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
		public CustomUserApprovalHandler userApprovalHandler() throws Exception {
			CustomUserApprovalHandler handler = new CustomUserApprovalHandler();
			handler.setApprovalStore(approvalStore());
			handler.setRequestFactory(new DefaultOAuth2RequestFactory(clientDetailsService));
			handler.setClientDetailsService(clientDetailsService);
			handler.setUseApprovalStore(true);
			return handler;
		}
	}
}
