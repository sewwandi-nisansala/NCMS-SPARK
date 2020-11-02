package lk.sparkx.ncms.helpers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * Helper functions of API communication
 */
public class Http {
    /**
     * Set api response headers
     *
     * @param resp
     * @param status
     * @return
     */
    public static HttpServletResponse setResponse(HttpServletResponse resp, int status) {
        resp.setStatus(status);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("utf-8");

        return resp;
    }

    /**
     * Add data to API response
     *
     * @param writer  Writer
     * @param status  Status
     * @param message Message
     * @param data    Retrieved data
     * @param errors  Errors
     * @return
     */
    public static PrintWriter getWriter(PrintWriter writer, String status, String message, Object data, JsonArray errors) {
        JsonObject json = new JsonObject();
        json.addProperty("status", status);
        json.addProperty("message", message);
        if (data != null) {
            if (data instanceof JsonObject) {
                json.add("data", (JsonObject) data);
            } else {
                json.add("data", (JsonArray) data);
            }

        }
        if (errors != null) {
            json.add("errors", errors);
        }
        writer.print(json.toString());
        return writer;
    }
}
