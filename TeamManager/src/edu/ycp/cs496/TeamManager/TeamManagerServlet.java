package edu.ycp.cs496.TeamManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.servlet.http.*;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import edu.ycp.TeamManager.Model.LoginData;
import edu.ycp.TeamManager.Model.User;
import edu.ycp.TeamManager.control.AddUser;
import edu.ycp.TeamManager.control.VerifyLogin;
import edu.ycp.cs496.util.HashLoginData;

@SuppressWarnings("serial")
public class TeamManagerServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		HttpSession session = req.getSession();
		String username = (String) session.getAttribute("user");
		
		// if user does not have active session, then deny access
		if(username == null){
			resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			resp.setContentType("text/plain");
			resp.getWriter().println("Session not active, please log in");
			return;
		}
		
		//if a username exists, then return welcome message
		resp.setStatus(HttpServletResponse.SC_OK);
		resp.setContentType("text/plain");
		resp.getWriter().println("Welcome " + username + " you were identified by session");
		
		/*
		resp.setStatus(HttpServletResponse.SC_OK);
		resp.setContentType("text/plain");
		resp.getWriter().println("Hello, world");
		*/
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		String action = req.getParameter("action");
		
		// post request must have an action
		if(action == null){
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			resp.getWriter().println("No action selected");
			return;
		}
		
		// attempt to login to system
		if(action.equals("login")){
			String username = req.getParameter("username");
			String password = req.getParameter("password");
			
			
			//sanitizes input
			username = Jsoup.clean(username, Whitelist.basic());
			password = Jsoup.clean(password, Whitelist.basic());
			
			
			LoginData data = new LoginData(username, password);
			
			VerifyLogin controller = new VerifyLogin();
			if(controller.verifyLogin(data)){
				
				//stores username in session
				HttpSession session= req.getSession();
				session.setAttribute("user", data.getUsername());
				session.setMaxInactiveInterval(30*60);
				
				// returns welcome message
				resp.setStatus(HttpServletResponse.SC_OK);
				resp.setContentType("text/plain");
				resp.getWriter().println("Welcome " + data.getUsername());
				
				return;
			}
			
			// unauthorized if login is not true
			resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			resp.setContentType("text/plain");
			resp.getWriter().println("Incorrect Login Credentials");
			
		}
		// makes a new user
		if(action.equals("newUser")){
			resp.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
			if(req.getParameter("password1").equals(req.getParameter("password2"))){
				
				// gets info for new account
				String username = req.getParameter("username");
				String password = req.getParameter("password1");
				String firstname = req.getParameter("firstname");
				String lastname = req.getParameter("lastname");
				String email = req.getParameter("email");
				
				
				// sanitizes input
				username = Jsoup.clean(username, Whitelist.basic());
				password = Jsoup.clean(password, Whitelist.basic());
				firstname = Jsoup.clean(firstname, Whitelist.basic());
				lastname = Jsoup.clean(lastname, Whitelist.basic());
				email = Jsoup.clean(email, Whitelist.basic());
				// creates new user
				 
				 
				User newuse = new User(username, firstname, lastname, email, HashLoginData.hashData(password), new LinkedList<String>(), new LinkedList<String>());
				AddUser controller = new AddUser();
				
				//adds new user to account
				boolean test = controller.addUser(newuse);
				//checks if user was added
				if(test){
					resp.setStatus(HttpServletResponse.SC_OK);
					resp.setContentType("text/plain");
					resp.getWriter().println("New User: " + newuse.getUsername() + " created");
				}else{
					resp.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
					resp.setContentType("text/plain");
					resp.getWriter().println("User Creation Failed");
				}
				
			}else{
				resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				resp.getWriter().println("passwords do not match");
				return;
			}
			
			return;

			//resp.getWriter().println("Creating a new user is not implemented yet");
		}
		
		//checkes the session
		if(action.equals("sessionCheck")){
			
			HttpSession session = req.getSession();
			String username = (String) session.getAttribute("user");
			
			// if user does not have active session, then deny access
			if(username == null){
				resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				resp.setContentType("text/plain");
				resp.getWriter().println("Session not active, please log in");
				return;
			}
			
			//if a username exists, then return welcome message
			resp.setStatus(HttpServletResponse.SC_OK);
			resp.setContentType("text/plain");
			resp.getWriter().println("Welcome " + username + " you were identified by session");
			
		}
		
		//logs user out
		if(action.equals("logout")){
			HttpSession session = req.getSession();
			session.invalidate();
			resp.getWriter().println("Session cleared");
		}
	}
}
