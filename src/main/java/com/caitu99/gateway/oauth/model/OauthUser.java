package com.caitu99.gateway.oauth.model;


public class OauthUser {
    int id;
    String account;
	String nickname;
	String profileimg;
	String password;
	Integer login_count;
	Integer type;
	Integer client_id;

	public Integer getClient_id() {
		return client_id;
	}

	public void setClient_id(Integer client_id) {
		this.client_id = client_id;
	}

	public Integer getLogin_count() {
		return login_count;
	}

	public void setLogin_count(Integer login_count) {
		this.login_count = login_count;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getProfileimg() {
		return profileimg;
	}

	public void setProfileimg(String profileimg) {
		this.profileimg = profileimg;
	}


    
    public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
