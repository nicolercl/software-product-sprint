// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.google.sps.data.Comment;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  private DatastoreService datastore;

  @Override
  public void init(){
    datastore = DatastoreServiceFactory.getDatastoreService();
  }

  private String convertToJsonUsingGson(ArrayList<Comment> comments) {
    String json = new Gson().toJson(comments);
    return json;
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json;");
    Query query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);
    PreparedQuery results = datastore.prepare(query);
    
    ArrayList<Comment> comments = new ArrayList<>();
    for (Entity entity : results.asIterable()) {
      long id = entity.getKey().getId();
      String user = (String) entity.getProperty("user");
      long timestamp = (long) entity.getProperty("timestamp");
      String content = (String) entity.getProperty("content");

      Comment comment = new Comment(id, user, timestamp, content);
      comments.add(comment);
    }
    String ret = convertToJsonUsingGson(comments);
    response.getWriter().println(ret);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String comment = request.getParameter("comment");
    String user = request.getParameter("user");
    long timestamp = System.currentTimeMillis();
    if (comment != null) {
      Entity commentEntity = new Entity("Comment");
      commentEntity.setProperty("user", user);
      commentEntity.setProperty("content", comment);
      commentEntity.setProperty("timestamp", timestamp);
      datastore.put(commentEntity);
    }
    response.sendRedirect("/");
  }
}
