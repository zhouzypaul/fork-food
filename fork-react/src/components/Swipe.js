import {Link} from "react-router-dom";
import Option from "./Option";
import { useRef, useState, useEffect } from "react";
import { useSelector } from "react-redux";
import Progress from "./Progress";

const MESSAGE_TYPE = {
  CONNECT: 0,
  UPDATE: 1,
  SEND: 2
};

/**
 * Renders swiping page.
 */
function Swipe(props) {
  const counter = useRef(0);
  const restaurants = useRef([]);
  const socket = useRef(undefined);
  const id = useRef(99);
  const roomId = useRef(9999);
  const [waiting, setWaiting] = useState(false);
  const swipeProps = props.location.swipeProps;
  const user = useSelector(state => state.user);

  // redirects if props are undefined
  if (swipeProps) {
    roomId.current = swipeProps.roomId;
    id.current = swipeProps.id;
    restaurants.current = swipeProps.restaurants;
    socket.current = swipeProps.socket;
  } else {
    props.history.push("/home");
  }

  const [currentRestaurant, setCurrent] = useState(restaurants.current[counter.current]);

  // warning before page unload
  useEffect(() => {
    window.onbeforeunload = () => {
      return true;
    }
    return () => {
      window.onbeforeunload = null;
      closeSocket();
    }
  }, []);

  const decide = (e) => {
    if (e.key === "ArrowRight") {
      sendDecision(1);
    } else if (e.key === "ArrowLeft") {
      sendDecision(0);
    }
  }

  useEffect(() => {
    window.onkeydown = decide;
    return () => {
      window.onkeydown = null;
    }
  });

  // sends message once done swiping
  useEffect(() => {
    if (waiting) {
      // send done message
      const message = {
        id: id.current,
        message: {
          type: "done",
          username: user,
          roomId: roomId.current
        }
      };
      socket.current.send(JSON.stringify(message));
    }
  }, [waiting, user])

  // sends user info
  const sendInfo = () => {
    const message = {
      id: id.current,
      message: {
        type: "update_user",
        roomId: roomId.current,
        username: user
      }
    };
    socket.current.send(JSON.stringify(message));
  }

  // responds to messages
  useEffect(() => {
    socket.current.onmessage = (msg) => {
      const data = JSON.parse(msg.data);
      switch (data.type) {
        case MESSAGE_TYPE.CONNECT:
          // send a message with our username and room
          id.current = data.payload.id
          sendInfo()
          break;
        case MESSAGE_TYPE.UPDATE:
          // check if we get a finished message then move to next page
          if (data.payload.type === "done") {
            props.history.push({
              pathname: `/result`,
              resultProps: {
                result: data.payload.senderMessage.result
              }
            })
          }
          break;
        default:
          console.log('Unknown message type!', data.type);
          break;
      }
      socket.current.onclose = () => {
        console.log("socket closed");
      }
    }

  }, [roomId])

  // sends individual decisions
  const sendDecision = (choice) => {
    const message = {
      id: id.current,
      message: {
        type: "swipe",
        username: user,
        resId: currentRestaurant.id,
        roomId: roomId.current,
        like: choice
      }
    };
    console.log(message);
    socket.current.send(JSON.stringify(message));

    // check if there are more restaurants to swipe on
    if (++counter.current < restaurants.current.length) {
      setCurrent(restaurants.current[counter.current]);
    } else {
      setWaiting(true);
    }
  }

  const closeSocket = () => {
    socket.current.close();
  }

  return (
    <>
      <div className="content">
        {waiting ?
          <div className="title-text">
            waiting
            <span className="d1">.</span>
            <span className="d2">.</span>
            <span className="d3">.</span>
          </div> :
          <div className="choices">
            <button className="ex" onClick={() => sendDecision(0)}>&#x2715;</button>
            <Option restaurant={currentRestaurant} />
            <button className="check" onClick={() => sendDecision(1)}>&#x2713;</button>
          </div>
        }
      </div>
      <Progress number={counter.current} total={restaurants.current.length}/>
      <div className="exit-home" onClick={closeSocket}>
        <Link to="/home" className="links">
          <i className="material-icons-outlined md-48">exit_to_app</i>
        </Link>
      </div>
    </>
  );
}

export default Swipe;
