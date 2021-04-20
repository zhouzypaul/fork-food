import { useEffect, useRef, useState } from "react";
import TopBar from "./TopBar";
import { useSelector } from "react-redux";

import Bubble from "./Bubble";
import { Link } from "react-router-dom";

const MESSAGE_TYPE = {
  CONNECT: 0,
  UPDATE: 1,
  SEND: 2
};

const HOST_LATITUDE = 42.359335;
const HOST_LONGITUDE = -71.059709;

/**
 * Renders host page.
 */
function Host(props) {
  const roomCode = useRef(9999);
  const user = useSelector(state => state.user);
  const [users, setUsers] = useState([]);
  const socket = useRef(null);
  const id = useRef(0);
  const hostName = useRef("");
  const roomProps = props.location.roomProps;

  // if undefined go back home
  let host = false;
  if (roomProps) {
    roomCode.current = roomProps.roomCode;
    host = roomProps.isHost;
  } else {
    props.history.push("/home");
  }

  const [isHost, setHost] = useState(host);

  // generates warning before page unloads
  useEffect(() => {
    window.onbeforeunload = () => {
      return true;
    }
    return () => {
      window.onbeforeunload = null;
      if (props.history.location.pathname !== "/swipe") {
        closeSocket();
      }
    }
  }, []);

  // creates socket and handles messages
  useEffect(() => {
    socket.current = new WebSocket("ws://localhost:4567/socket");
    socket.current.onmessage = (msg) => {
      const data = JSON.parse(msg.data);
      switch (data.type) {
        case MESSAGE_TYPE.CONNECT:
          id.current = data.payload.id
          // send a message with username and room
          sendInfo()
          break;
        case MESSAGE_TYPE.UPDATE:
          // check if updating users or starting
          if (data.payload.type === "update_user") {
            hostName.current = data.payload.host;
            setHost(hostName.current === user);
            setUsers(data.payload.senderMessage.users);
          } else if (data.payload.type === "start") {
            props.history.push({
              pathname: `/swipe`,
              swipeProps: {
                restaurants: data.payload.senderMessage.restaurants,
                socket: socket.current,
                id: id.current,
                roomId: roomCode.current
              }
            });
          }
          break;
        default:
          console.log('Unknown message type!', data.type);
          break;
      }
    };

    socket.current.onclose = () => {
      // remove the users
      setUsers([]);
    }

  }, [])

  // sends user info to back end
  const sendInfo = () => {
    const message = {
      id: id.current,
      message: {
        type: "update_user",
        roomId: roomCode.current,
        username: user,
        host: isHost
      }
    };
    socket.current.send(JSON.stringify(message));
  }

  // request restaurants to start swiping
  const startSwiping = () => {
    const message = {
      id: id.current,
      message: {
        type: "start",
        username: user,
        roomId: roomCode.current,
        lat: HOST_LATITUDE,
        lon: HOST_LONGITUDE
      }
    };
    socket.current.send(JSON.stringify(message));
  }

  const closeSocket = () => {
    socket.current.close();
  }

  let i = 0;
  return (
    <>
      <TopBar to="/home" showOptions={true} />
      <div className="content">
        <div className="title-text">
          share the code
        </div>
        <div className="code">{roomCode.current}</div>
        {isHost ? <button className="primary-button" onClick={startSwiping}>start</button> : null}
        <div className="joined">
          {users.map(user => {return (<Bubble user={user} host={hostName.current === user} key={i++} />)})}
        </div>
      </div>
      <div className="exit-home" onClick={closeSocket}>
        <Link to="/home" className="links">
          <i className="material-icons-outlined md-48">exit_to_app</i>
        </Link>
      </div>
    </>
  );
}

export default Host;