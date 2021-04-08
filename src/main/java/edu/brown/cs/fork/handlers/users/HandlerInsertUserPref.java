package edu.brown.cs.fork.handlers.users;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import edu.brown.cs.fork.Hub;
import org.json.JSONArray;
import org.json.JSONObject;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HandlerInsertUserPref implements Route {
  private static final Gson GSON = new Gson();

  public HandlerInsertUserPref() {  }

  @Override
  public Object handle(Request req, Response res) throws Exception {
    JSONObject data = new JSONObject(req.body());
    String id = data.getString("id");
    String colsToUpdateStr = data.getString("colsToUpdate");
    JSONArray arr = data.getJSONArray("types");
    String infoStr = data.getString("info");
    List<String> colsToUpdate = new ArrayList<>();
    List<String> info = new ArrayList<>();

    String pattern = "[^-]+";
    Pattern r = Pattern.compile(pattern);
    Matcher mCol = r.matcher(colsToUpdateStr);
    while (mCol.find()) {
      List<String> cols = Collections.singletonList(mCol.group());
      colsToUpdate.addAll(cols);
    }
    Matcher mInfo = r.matcher(infoStr);
    while (mInfo.find()) {
      List<String> inf = Collections.singletonList(mInfo.group());
      info.addAll(inf);
    }

    String err = "";
    boolean success = false;
    if (!Hub.getUserDB().isConnected()) {
      err = "ERROR: No database connected";
    } else {
//      success = Hub.getUserDB().upsertUserInfo(id, colsToUpdate, info);
      if (colsToUpdate.contains("categories")) {
        int categoriesIndex = colsToUpdate.indexOf("categories");
        String categoriesStr = info.get(categoriesIndex);
        colsToUpdate.set(categoriesIndex, "foodType");
        List<String> allTypes = new ArrayList<>();

        String patt = "[^,]+";
        Pattern compPatt = Pattern.compile(patt);
        Matcher matchPatt = compPatt.matcher(categoriesStr);
        while (matchPatt.find()) {
          List<String> types = Collections.singletonList(matchPatt.group());
          allTypes.addAll(types);
        }

        for (String type : allTypes) {
          info.set(categoriesIndex, type);
          success = Hub.getUserDB().insertUserPref(id, colsToUpdate, info);
        }
      }
    }
    Map<String, Object> variables = ImmutableMap.of("success", success, "err", err);

    return GSON.toJson(variables);
  }
}
