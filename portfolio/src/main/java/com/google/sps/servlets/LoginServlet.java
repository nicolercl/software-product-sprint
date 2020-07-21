package com.google.sps.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/")
public class LoginServlet extends HttpServlet {

  private final UserService mUserService = UserServiceFactory.getUserService();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html");

    if (mUserService.isUserLoggedIn()) {
      String userEmail = mUserService.getCurrentUser().getEmail();
      String urlToRedirectToAfterUserLogsOut = "/";
      String logoutUrl = mUserService.createLogoutURL(urlToRedirectToAfterUserLogsOut);

      response.getWriter().println("Hello " + userEmail + ".");
      response.getWriter().println("Leave a comment below or log out <a href=\"" + logoutUrl + 
      "\">here.</a>");
    } else {
      String urlToRedirectToAfterUserLogsIn = "/";
      String loginUrl = mUserService.createLoginURL(urlToRedirectToAfterUserLogsIn);
      response.getWriter().println("Hello stranger.");
      response.getWriter().println("Login <a href=\"" + loginUrl + "\">here</a> to leave a comment.");
    }
  }
}