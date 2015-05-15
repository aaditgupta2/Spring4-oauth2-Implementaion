package com.ag.mvc;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.ag.dto.Login;
import com.ag.restclient.RestClient;

/**
 * The Class SecurityRestController.
 */
@Controller
public class SecurityRestController {

	/** The token services. */
	@Autowired
	@Qualifier("consumerTokenServices")
	private ConsumerTokenServices tokenServices;

	/**
	 * Login.
	 *
	 * @param body body
	 * @return response entity
	 */
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ResponseEntity<String> login(@RequestBody Login body) {
		HttpHeaders headers = new HttpHeaders();
		String response = "";
		try {
			RestClient restClient = new RestClient();
			response = restClient.getToken(populateInputMap(body.getUserName(), body.getPassword()));
		} catch (Exception ex) {
			ex.printStackTrace();
			response = "Auth fails";
		}
		return new ResponseEntity<String>(response, headers, HttpStatus.OK);
	}

	/**
	 * Logout.
	 *
	 * @param value value
	 * @return response entity
	 */
	@RequestMapping(value = "/rest/logout", method = RequestMethod.GET)
	public ResponseEntity<String> logout(@RequestParam("access_token") String value) {
		tokenServices.revokeToken(value);
		return new ResponseEntity<String>("DONE", new HttpHeaders(), HttpStatus.OK);
	}

	/**
	 * Populate input map.
	 *
	 * @param uName u name
	 * @param pasword pasword
	 * @return map
	 */
	private Map<String, String> populateInputMap(final String uName, final String pasword) {
		Map<String, String> m = new HashMap<String, String>();
		m.put("grant_type", "client_credentials");
		m.put("client_id", uName);
		m.put("client_secret", pasword);
		return m;
	}
}
