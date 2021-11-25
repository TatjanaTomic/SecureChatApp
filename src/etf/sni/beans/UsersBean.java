package etf.sni.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import etf.sni.dto.User;

public class UsersBean implements Serializable {

	private static final long serialVersionUID = -5362072391744772313L;
	private List<User> users;
	
	public UsersBean() {
		users = new ArrayList<>();
	}
	
	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}
	
	public void loginUser(User user) {
		if(!users.contains(user))
			users.add(user);
	}
	
	public void logoutUser(User user) {
		if(users.contains(user))
			users.remove(user);
	}

}
