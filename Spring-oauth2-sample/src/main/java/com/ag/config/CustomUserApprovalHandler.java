package com.ag.config;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.approval.ApprovalStoreUserApprovalHandler;

/**
 * The Class CustomUserApprovalHandler.
 */
public class CustomUserApprovalHandler extends ApprovalStoreUserApprovalHandler {

	/** The use approval store. */
	private boolean useApprovalStore = true;

	/** The client details service. */
	private ClientDetailsService clientDetailsService;

	/* (non-Javadoc)
	 * @see org.springframework.security.oauth2.provider.approval.ApprovalStoreUserApprovalHandler#setClientDetailsService(org.springframework.security.oauth2.provider.ClientDetailsService)
	 */
	public void setClientDetailsService(ClientDetailsService clientDetailsService) {
		this.clientDetailsService = clientDetailsService;
		super.setClientDetailsService(clientDetailsService);
	}

	/**
	 * Sets use approval store.
	 *
	 * @param useApprovalStore the new use approval store
	 */
	public void setUseApprovalStore(boolean useApprovalStore) {
		this.useApprovalStore = useApprovalStore;
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.oauth2.provider.approval.ApprovalStoreUserApprovalHandler#checkForPreApproval(org.springframework.security.oauth2.provider.AuthorizationRequest, org.springframework.security.core.Authentication)
	 */
	@Override
	public AuthorizationRequest checkForPreApproval(AuthorizationRequest authorizationRequest, Authentication userAuthentication) {

		boolean approved = false;
		if (useApprovalStore) {
			authorizationRequest = super.checkForPreApproval(authorizationRequest, userAuthentication);
			approved = authorizationRequest.isApproved();
		} else {
			if (clientDetailsService != null) {
				Collection<String> requestedScopes = authorizationRequest.getScope();
				try {
					ClientDetails client = clientDetailsService.loadClientByClientId(authorizationRequest.getClientId());
					for (String scope : requestedScopes) {
						if (client.isAutoApprove(scope) || client.isAutoApprove("all")) {
							approved = true;
							break;
						}
					}
				} catch (ClientRegistrationException e) {
				}
			}
		}
		authorizationRequest.setApproved(approved);
		return authorizationRequest;
	}

}
