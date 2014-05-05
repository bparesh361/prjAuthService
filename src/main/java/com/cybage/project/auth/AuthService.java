package com.cybage.project.auth;

import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class AuthService {

	public String getKey(String userName,String password) throws AuthException {
		String pwd = UserMap.userdata.get(userName);
		if(password==null){
			throw new AuthException("UserName Does Not Exist");
		}
		if(pwd==null || (!pwd.equals(password))){
			throw new AuthException("Password is Not Correct"); 
					
		}
		String key = UserMap.keydata.get(userName);
		if(key==null){
			String uuid = UUID.randomUUID().toString();
			UserMap.keydata.put(userName, uuid);
			return uuid;
		}
		return key;		
	}	
	
	public void register(String userName,String password){
		UserMap.userdata.put(userName, password);
	}
	
	public boolean validateKey(String key){
		for(String userName : UserMap.keydata.keySet()){
			String mapkey = UserMap.keydata.get(userName);
			if(mapkey.equals(key)){
				return true;
			}
		}
		return false;
	}
}
