package ws.models;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Websocket payload opcodes enum.
 */
public enum Opcode {
  HEARTBEAT(0),
  HEARTBEAT_ACK(1),
  HELLO(2),
  IDENTIFY(3),
  READY(4),
  DISPATCH(5),
  SUBMIT(6),
  REPLAY(7);

  @JsonValue
  private final int value;

  Opcode(final int value) {
    this.value = value;
  }
}
