package edu.brown.cs.fork.sockets;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.IOException;

@WebSocket
public class Groups {
  private enum MESSAGE_TYPE {
    CONNECT,
    UPDATE,
    SEND
  }

  @OnWebSocketConnect
  public void connected(Session session) throws IOException {}

  @OnWebSocketClose
  public void closed(Session session, int statusCode, String reason) {}

  @OnWebSocketMessage
  public void message(Session session, String message) throws IOException {}
}
