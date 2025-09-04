package uy.com.fing.hicscan.hceanalysis.data.plainTextProcessor.impl.ctakes.dto;
import java.util.Map;

public class ApiResponse {
    private String status;
    private String message;
    private Map<String, String> data;

    public ApiResponse(String status, String message, Map<String, String> data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }
    // Getters y setters

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }
}


