package ws.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * The body returned from the request to the /auth/developer endpoint.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenResponseBody {
  /**
   * The generated token from the POST call
   */
  private String token;
}
