// © 2013 by IT-Power GmbH (http://www.it-power.org)
package org.itpower.pickscreens.web.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.itpower.lib.crosstable.CrossTableHitRatio;
import org.itpower.lib.crosstable.CrossTableHitRatio2JSONAdapter;
import org.itpower.lib.crosstable.CrossTableHitRatioView;
import org.itpower.lib.crosstable.CrossTableHitRatioViewNumber;
import org.itpower.lib.crosstable.CrossTableHitRatioViewPercentage;
import org.itpower.pickscreens.core.aspects.PICKScreensConfig;
import org.itpower.pickscreens.core.aspects.PICKScreensLog;
import org.itpower.pickscreens.core.control.CrossTableFactory;
import org.itpower.pickscreens.core.control.PICKScreensSynonyms;
import org.itpower.pickscreens.core.dao.DAOProxy;
import org.itpower.pickscreens.core.dao.PICKScreensQuery;
import org.itpower.pickscreens.core.dao.PICKScreensQueryDefault;
import org.itpower.pickscreens.core.model.Duplicate;
import org.itpower.pickscreens.core.model.Recipe;
import org.itpower.pickscreens.core.model.Screen;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * control all requests
 * 
 * @author Daniel Oltmanns <daniel.oltmanns@it-power.org>
 * @since 1.00-SNAPSHOT (03/01/2013)
 */
@Controller
public final class PICKScreensController {

  /**
   * page to admin root request
   * 
   * @param response
   * @return
   */
  @RequestMapping(value = "/", method = RequestMethod.GET)
  public String resolveHome(ModelMap model, HttpServletRequest request) {
    List<Screen> screens;
    if (this.showScreensFreeForPublicOnly(request)) {
      screens = DAOProxy.screendao().getAll(true);
    } else {
      screens = DAOProxy.screendao().getAll();
    }
    model.put("loggedIn", this.isAuth(request));
    model.put("screens", screens);
    model.put("screensAsJSON", this.getScreensAsJSON(screens));
    return "home";
  }

  /**
   * on login get requests just resolve home
   * 
   * @see #resolveHome(ModelMap, HttpServletRequest)
   * 
   * @since 02.05.2013
   * @param model
   * @param request
   * @return {@link #resolveHome(ModelMap, HttpServletRequest)}
   */
  @RequestMapping(value = "/login.html", method = RequestMethod.GET)
  public String printHomeOnGetLogin(ModelMap model, HttpServletRequest request) {
    return this.resolveHome(model, request);
  }

  /**
   * on logout get requests just resolve home
   * 
   * @see #resolveHome(ModelMap, HttpServletRequest)
   * 
   * @since 02.05.2013
   * @param model
   * @param request
   * @return {@link #resolveHome(ModelMap, HttpServletRequest)}
   */
  @RequestMapping(value = "/logout.html", method = RequestMethod.GET)
  public String printHomeOnGetLogout(ModelMap model, HttpServletRequest request) {
    return this.resolveHome(model, request);
  }

  /**
   * 
   * download the file of a screen on valid requests or {@link #resolveHome(ModelMap, HttpServletRequest)} if not auth user or file not present
   * anymore.
   * 
   * 
   * @since 02.05.2013
   * @param model
   * @param request
   * @param response
   * @return the file of a screen
   */
  @RequestMapping(value = "/download.xls", method = RequestMethod.GET)
  public String getDownload(ModelMap model, HttpServletRequest request, HttpServletResponse response) {
    // parameter is given and screen exists
    boolean responseDownload = request.getParameter("id") != null;
    Screen screen = null;
    File downloadFile = null;
    if (responseDownload) {
      try {
        int screenId = Integer.parseInt(request.getParameter("id"));
        screen = DAOProxy.screendao().getOne(screenId);
        responseDownload = screen != null;
      } catch (NumberFormatException e) {
        responseDownload = false;
      }
    }
    // check if user is allowed for downloading this screen
    if (responseDownload) {
      responseDownload = this.isAuth(request, screen);
    }
    // check if download file still exists
    if (responseDownload) {
      downloadFile = screen.getFile();
      responseDownload = downloadFile != null && downloadFile.canRead();
    }
    // download file
    if (responseDownload) {
      // ↘ force "save as" in browser
      response.setHeader("Content-Disposition", "attachment; filename=" + downloadFile.getName());
      // ↘ it is a pdf
      MimetypesFileTypeMap mftm = new MimetypesFileTypeMap();
      response.setContentType(mftm.getContentType(downloadFile));
      ServletOutputStream outputStream = null;
      FileInputStream inputStream = null;
      try {
        outputStream = response.getOutputStream();
        inputStream = new FileInputStream(downloadFile);
        int nob = IOUtils.copy(inputStream, outputStream);
        if (nob < 1) PICKScreensLog.error("fail to download: " + downloadFile.getAbsolutePath(), 1305021450);
      } catch (IOException e) {
        PICKScreensLog.exception(e, 1305021451);
      } finally {
        IOUtils.closeQuietly(inputStream);
        IOUtils.closeQuietly(outputStream);
      }
      return null;
    } else {
      return this.resolveHome(model, request);
    }
  }

  private boolean isAuth(HttpServletRequest request, Screen screen) {
    return screen.isFreeForPublic() == true || this.isAuth(request);
  }

  @RequestMapping(value = "/logout.html", method = RequestMethod.POST)
  public String logout(ModelMap model, HttpServletRequest request) {
    request.getSession().setAttribute("auth", false);
    model.put("logoutTry", true);
    return this.resolveHome(model, request);
  }

  @RequestMapping(value = "/login.html", method = RequestMethod.POST)
  public String login(ModelMap model, HttpServletRequest request) {
    boolean isAuth = false;
    if (request.getParameter("password") != null) {
      String password = PICKScreensConfig.globalProperty("login_password");
      isAuth = request.getParameter("password").equals(password);
    }
    if (isAuth) {
      request.getSession().setAttribute("auth", true);
    }
    model.put("loginTry", true);
    return this.resolveHome(model, request);
  }

  private JSONArray getScreensAsJSON(List<Screen> screens) {
    JSONArray result = new JSONArray();
    for (Screen screen : screens) {
      JSONObject screenAsJSON = new JSONObject();
      try {
        screenAsJSON.put("id", screen.getId());
        screenAsJSON.put("name", screen.getName());
        screenAsJSON.put("recipes", screen.getRecipes().size());
      } catch (JSONException e) {
        PICKScreensLog.exception(e, 1304171528);
      }
      result.put(screenAsJSON);
    }
    return result;
  }

  /**
   * return a json object with all recipes of given screen ids.
   * 
   * 
   * @since 23.04.2013
   * @param response
   * @param request containing the ids of screens to search in. set ids in parameter screens.
   */
  @RequestMapping(value = "/details-screens.json", method = RequestMethod.GET)
  public void getDetailsResult(HttpServletResponse response, HttpServletRequest request) {
    JSONObject result = new JSONObject();
    boolean succ = false;
    List<Integer> searchInScreens = this.getSearchInScreens(request);
    Boolean freeForPublic = this.showScreensFreeForPublicOnly(request) ? true : null;
    PICKScreensQuery pq = new PICKScreensQueryDefault(freeForPublic, searchInScreens);
    JSONArray rows = new JSONArray();
    int hits = 0;

    // build json
    try {
      rows = this.getRecipesAsJSON(pq);
      hits = rows.length();
      succ = true;
      result.put("hits", hits);
      result.put("succ", succ);
      result.put("rows", rows);
    } catch (JSONException e) {
      PICKScreensLog.exception(e, 1304231035);
    }
    // write result
    this.printJSON(result, response);
  }

  /**
   * return a cross table with an information of parallelism of screens in format.
   * 
   * <pre>
   * { "succ" : true,
   *   "rows" : {
   *     "num" : {@link CrossTableHitRatio2JSONAdapter#getAsJSON()},
   *     "percentage" :  {@link CrossTableHitRatio2JSONAdapter#getAsJSON()}
   *   }
   * }
   * </pre>
   * 
   * 
   * @since 23.04.2013
   * @param response
   * @param request
   */
  @RequestMapping(value = "/crosstable.json", method = RequestMethod.GET)
  public void getCrossTableResult(HttpServletResponse response, HttpServletRequest request) {
    JSONObject result = new JSONObject();
    boolean succ = false;
    List<Integer> searchInScreens = this.getSearchInScreens(request);
    JSONObject rows = new JSONObject();
    List<Screen> screens = DAOProxy.screendao().getScreens(searchInScreens);
    CrossTableFactory factory = new CrossTableFactory(screens);
    CrossTableHitRatio crossTable = factory.getCrossTableHitRatio();

    try {
      // create rows num
      CrossTableHitRatioView view = new CrossTableHitRatioViewNumber(crossTable);
      CrossTableHitRatio2JSONAdapter jsonAdapter = new CrossTableHitRatio2JSONAdapter(view);
      JSONObject rowsNum = jsonAdapter.getAsJSON();
      rows.put("num", rowsNum);

      // create rows percentage
      view = new CrossTableHitRatioViewPercentage(crossTable);
      jsonAdapter = new CrossTableHitRatio2JSONAdapter(view);
      JSONObject rowsPercentage = jsonAdapter.getAsJSON();
      rows.put("percentage", rowsPercentage);

      succ = true;
    } catch (JSONException e) {
      PICKScreensLog.exception(e, 1304261538);
    }

    // build json result
    try {
      result.put("succ", succ);
      result.put("rows", rows);
    } catch (JSONException e) {
      PICKScreensLog.exception(e, 1304231509);
    }

    // write result
    this.printJSON(result, response);
  }

  @RequestMapping(value = "/details-duplicates.json", method = RequestMethod.GET)
  public void getCrossTableDetails(HttpServletResponse response, HttpServletRequest request) {
    JSONObject result = new JSONObject();
    boolean succ = false;
    List<Integer> searchInScreens = this.getSearchInScreens(request);
    JSONArray rows = new JSONArray();
    List<Screen> screens = DAOProxy.screendao().getScreens(searchInScreens);
    if (screens.size() > 1) {
      List<Duplicate> duplicates = DAOProxy.screendao().getDuplicates(screens);
      for (Duplicate duplicate : duplicates) {
        rows.put(duplicate.toJSON(true));
      }
      succ = rows.length() == duplicates.size();
    }
    // build json result
    try {
      result.put("succ", succ);
      result.put("rows", rows);
    } catch (JSONException e) {
      PICKScreensLog.exception(e, 1304231509);
    }

    // write result
    this.printJSON(result, response);
  }

  @RequestMapping(value = "/substances.json", method = RequestMethod.GET)
  public void getSubstances(HttpServletResponse response, HttpServletRequest request) {
    JSONObject result = new JSONObject();
    boolean succ = false;
    JSONArray rows = new JSONArray();
    List<String> substances = DAOProxy.screendao().getAllSubstanceNames();
    try {
      for (String substance : substances) {
        JSONObject substanceJSON = new JSONObject();
        substanceJSON.put("substance_name", substance);
        List<String> synonyms = PICKScreensSynonyms.me().getSynonyms(substance);
        String synonymsView = synonyms != null && synonyms.size() > 0 ? StringUtils.join(synonyms, ", ") : "-";
        substanceJSON.put("synonyms", synonymsView);
        rows.put(substanceJSON);
      }
      succ = true;
    } catch (Exception e) {
      PICKScreensLog.exception(e, 1305061705);
    }
    // build json result
    try {
      result.put("succ", succ);
      result.put("rows", rows);
    } catch (JSONException e) {
      PICKScreensLog.exception(e, 1305061704);
    }

    // write result
    this.printJSON(result, response);
  }

  private void printJSON(JSONObject json, HttpServletResponse response) {
    PrintWriter pw = null;
    try {
      response.setHeader("Content-type", "application/json");
      pw = response.getWriter();
      // build result
      IOUtils.write(json.toString(), pw);
    } catch (IOException ex) {
      PICKScreensLog.exception(ex, 1304121337);
    } finally {
      IOUtils.closeQuietly(pw);
    }
  }

  private List<Integer> getSearchInScreens(HttpServletRequest request) {
    List<Integer> result = new ArrayList<Integer>();
    if (request.getParameter("screens") != null) {
      try {
        JSONArray tmps = new JSONArray(request.getParameter("screens"));
        for (int i = 0; i < tmps.length(); i++) {
          result.add(tmps.getInt(i));
        }
      } catch (JSONException e) {
        PICKScreensLog.exception("invalid request " + request.getParameter("screens"), e, 1304161201);
      }
    }
    return result;
  }

  /**
   * return a json object with all recipes of given screen ids and a specific query. the query is the input of the search field.
   * 
   * 
   * @since 23.04.2013
   * @param response
   * @param request containing the ids of screens to search in. set ids in parameter <code>screens</code> and the query in parameter
   *          <code>query</code>.
   */
  @RequestMapping(value = "/search.json", method = RequestMethod.GET)
  public void getSearchResult(HttpServletResponse response, HttpServletRequest request) {
    // container
    JSONObject result = new JSONObject();

    // values
    JSONArray rows = new JSONArray();
    boolean succ = false;
    int hits = 0;
    String query = request.getParameter("query");
    // build filter
    List<Integer> searchInScreens = this.getSearchInScreens(request);

    // catch result if query is valid
    try {
      if (query != null && query.isEmpty() == false) {
        Boolean freeForPublic = this.showScreensFreeForPublicOnly(request) ? true : null;
        PICKScreensQuery pq = new PICKScreensQueryDefault(freeForPublic, query, searchInScreens);
        rows = this.getRecipesAsJSON(pq);
        hits = rows.length();
        succ = true;
      }

      // build result
      result.put("hits", hits);
      result.put("succ", succ);
      result.put("rows", rows);
    } catch (JSONException e) {
      PICKScreensLog.exception(e, 1304121336);
    }

    // write result
    this.printJSON(result, response);
  }

  private JSONArray getRecipesAsJSON(PICKScreensQuery pq) {
    List<Recipe> recipes = DAOProxy.screendao().getRecipes(pq);
    JSONArray rows = new JSONArray();
    int id = 1; // = column #
    for (Recipe recipe : recipes) {
      JSONObject row = new JSONObject();
      try {
        row.put("id", id++);
        row.put("screen_name", recipe.getScreen().getName());
        row.put("condition_number", recipe.getNumber());
        row.put("salt1", recipe.getSalt1() == null ? "" : recipe.getSalt1().getAmountUnitName());
        row.put("salt2", recipe.getSalt2() == null ? "" : recipe.getSalt2().getAmountUnitName());
        row.put("buffer", recipe.getBuffer() == null ? "" : recipe.getBuffer().getAmountUnitName());
        row.put("precipitant1", recipe.getPrecipitant1() == null ? "" : recipe.getPrecipitant1().getAmountUnitName());
        row.put("precipitant2", recipe.getPrecipitant2() == null ? "" : recipe.getPrecipitant2().getAmountUnitName());
        rows.put(row);
      } catch (JSONException e) {
        PICKScreensLog.exception(e, 1304231055);
      }
    }
    return rows;
  }

  private boolean isAuth(HttpServletRequest request) {
    HttpSession session = request.getSession();
    return session != null && session.getAttribute("auth") != null && session.getAttribute("auth").equals(true);
  }

  private boolean showScreensFreeForPublicOnly(HttpServletRequest request) {
    return !this.isAuth(request);
  }
}