package org.example.terra;

import com.google.gson.annotations.SerializedName;

public class WebhookPayload {
  @SerializedName("status")
  private String status;

  @SerializedName("type")
  private String type;

  @SerializedName("user")
  private int terraUser;

  @SerializedName("message")
  private String message;

  @SerializedName("data")
  private String[] data;

  @SerializedName("widget_session_id")
  private String uuid;

  @SerializedName("reference_id")
  private String reference_id;

  @SerializedName("reason")
  private String reason;

  private static final String[] types = new String[]{"athlete", "body", "sleep", "menstruation", "user_reauth", "request_processing", "request_completed", "deauth"};



}
