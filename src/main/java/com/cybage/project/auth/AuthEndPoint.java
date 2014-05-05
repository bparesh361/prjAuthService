package com.cybage.project.auth;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.cybage.project.auth.common.CommonConstants;
import com.cybage.project.schemas.AuthRequest;
import com.cybage.project.schemas.AuthResponse;
import com.cybage.project.schemas.MathSqrtRequest;
import com.cybage.project.schemas.MathSqrtResponse;
import com.cybage.project.schemas.RegisterRequest;
import com.cybage.project.schemas.RegisterResponse;
import com.cybage.project.thirdparty.schemas.MathRequest;
import com.cybage.project.thirdparty.schemas.MathResponse;

@Endpoint
public class AuthEndPoint implements ApplicationContextAware{

	@Autowired
	private AuthService authService;
	
	@Autowired
	private WebServiceTemplate webServiceTemplate;
	
	private ApplicationContext applicationContext;
	
	private static final Logger logger = Logger.getLogger(AuthEndPoint.class);
	
	
	@PayloadRoot(namespace=CommonConstants.NAMESPACE_URI,localPart="AuthRequest")
	@ResponsePayload
	public AuthResponse auth(@RequestPayload AuthRequest authReq) throws AuthException {
		String key = authService.getKey(authReq.getUsername(), authReq.getPassword());
		AuthResponse response = new AuthResponse();
		response.setKey(key);
		return response;
	}
	
	@PayloadRoot(namespace=CommonConstants.NAMESPACE_URI,localPart="RegisterRequest")
	@ResponsePayload
	public RegisterResponse register(@RequestPayload RegisterRequest registerRequest){		
		logger.info("--- Received Request for Registration ----" + registerRequest.getUsername() + " - Password : " + registerRequest.getPassword());
		authService.register(registerRequest.getUsername(), registerRequest.getPassword());
		RegisterResponse response = new RegisterResponse();
		response.setMsg(CommonConstants.SUCCESS_REGISTER);
		return response;
	}
	
	@PayloadRoot(namespace=CommonConstants.NAMESPACE_URI,localPart="MathSqrtRequest")
	@ResponsePayload
	public MathSqrtResponse register(@RequestPayload MathSqrtRequest mathSqrtRequest) throws AuthException {		
		logger.info("--- Received Request for Square Root ---- Key : " + mathSqrtRequest.getKey()+ " - Number : " + mathSqrtRequest.getNumber());
		boolean result = authService.validateKey(mathSqrtRequest.getKey());
		logger.info(" --- Validation of Key Result --- " + result);
		if(!result){
			throw new AuthException("Invalid Key");
		}
		MathRequest request = new MathRequest();
		request.setNumber(mathSqrtRequest.getNumber());
		logger.info(" - Calling Service - " + applicationContext.getBean(WebServiceTemplate.class));		
		MathResponse response = (MathResponse)webServiceTemplate.marshalSendAndReceive(request);
		logger.info(" - Response from third party Service  received - " + response.getNumber());
		MathSqrtResponse sqrtResponse = new MathSqrtResponse();
		sqrtResponse.setNumber(response.getNumber());
		return sqrtResponse;
	}

	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		
		this.applicationContext = applicationContext; 
		
	}
	
}
