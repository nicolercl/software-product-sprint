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

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  private final DatastoreService mDatastore = DatastoreServiceFactory.getDatastoreService();
  private final ArrayList<Comment> mTmpComments = new ArrayList<>();

  /** A comment in the portfolio page. */
  public final class Comment {

    private final long mId;
    private final String mUser;
    private final long mTimestamp;
    private final String mContent;

    public Comment(long id, String user, long timestamp, String content) {
      this.mId = id;
      this.mUser = user;
      this.mTimestamp = timestamp;
      this.mContent = content;
    }
  }

  private String convertToJsonUsingGson(ArrayList<Comment> comments) {
    String json = new Gson().toJson(comments);
    return json;
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json;");
    final Query query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);
    final PreparedQuery results = mDatastore.prepare(query);
    
    mTmpComments.clear();
    for (Entity entity : results.asIterable()) {
      long id = entity.getKey().getId();
      String user = (String) entity.getProperty("user");
      long timestamp = (long) entity.getProperty("timestamp");
      String content = (String) entity.getProperty("content");

      Comment comment = new Comment(id, user, timestamp, content);
      mTmpComments.add(comment);
    }
    String ret = convertToJsonUsingGson(mTmpComments);
    response.getWriter().println(ret);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String comment = request.getParameter("comment");
    String user = request.getParameter("user");
    long timestamp = System.currentTimeMillis();
    if (comment != null && comment != "") {
      Entity commentEntity = new Entity("Comment");
      if (user == null || user == "") user = "Anonymous"; 
      commentEntity.setProperty("user", user);
      commentEntity.setProperty("content", comment);
      commentEntity.setProperty("timestamp", timestamp);
      mDatastore.put(commentEntity);
    }
    response.sendRedirect("/");
  }
}
