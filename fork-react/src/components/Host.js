import { useEffect, useRef, useState } from "react";
import TopBar from "./TopBar";
import { useSelector } from "react-redux";

import Bubble from "./Bubble";
import {Link} from "react-router-dom";

const MESSAGE_TYPE = {
  CONNECT: 0,
  UPDATE: 1,
  SEND: 2
};

function Host(props) {
  const roomCode = useRef(9999);
  const host = useRef(false);
  const user = useSelector(state => state.user);


  const roomProps = props.location.roomProps;
  if (roomProps) {
    roomCode.current = roomProps.roomCode;
    host.current = roomProps.isHost;
  } else {
    props.history.push("/home");
  }

  useEffect(() => {
    window.onbeforeunload = () => {
      return true;
    }
    return () => {
      window.onbeforeunload = null;
    }
  })

  const [users, setUsers] = useState([]);

  const socket = useRef(null);
  const id = useRef(0);

  useEffect(() => {
    socket.current = new WebSocket("ws://localhost:4567/socket");
    socket.current.onmessage = (msg) => {
      const data = JSON.parse(msg.data);
      switch (data.type) {
        case MESSAGE_TYPE.CONNECT:
          id.current = data.payload.id
          // send a message with our username and room
          console.log("connected")
          sendInfo()
          break;
        case MESSAGE_TYPE.UPDATE:
          // check if we're updating users or doing something else
          if (data.payload.type === "update_user") {
            setUsers(data.payload.senderMessage.users);
            console.log("users set");
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
            console.log("start swiping");
          }
          break;
        default:
          console.log('Unknown message type!', data.type);
          break;
      }
    };

    socket.current.onclose = () => {
      //should remove the user
      setUsers()
      console.log("socket closed");
    }

  }, [])

  const sendInfo = () => {
    const message = {
      id: id.current,
      message: {
        type: "update_user",
        roomId: roomCode.current,
        username: user
      }
    };
    socket.current.send(JSON.stringify(message));
  }

  const startSwiping = () => {
    const message = {
      id: id.current,
      message: {
        type: "start",
        roomId: roomCode.current,
        lat: 42.359335,
        lon: -71.059709
      }
    };
    socket.current.send(JSON.stringify(message));
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
        {host.current ? <button className="primary-button" onClick={startSwiping}>start</button> : <></>}
        <div className="joined">
          {users.map((user) => {
            return (<Bubble user={user} key={i++} />)
          })}
        </div>
      </div>
      <div className="exit-home">
        <Link to="/home" className="links">
          <i className="material-icons-outlined md-48">exit_to_app</i>
        </Link>
      </div>
    </>
  );
}

export default Host;