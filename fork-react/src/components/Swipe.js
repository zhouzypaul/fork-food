import { Link } from "react-router-dom";
import Option from "./Option";
import { useRef, useState, useEffect } from "react";
import { useSelector } from "react-redux";

const MESSAGE_TYPE = {
  CONNECT: 0,
  UPDATE: 1,
  SEND: 2
};

function Swipe(props) {
  const counter = useRef(0);
  const restaurants = props.location.swipeProps.restaurants;
  const socket = useRef(props.location.swipeProps.socket);
  const id = useRef(99);
  const roomId = useRef(9999);
  const [waiting, setWaiting] = useState(false);

  const swipeProps = props.location.swipeProps;
  if (swipeProps) {
    roomId.current = swipeProps.roomId;
    id.current = swipeProps.id;
  }

  const user = useSelector(state => state.user);

  const [currentRestaurant, setCurrent] = useState(restaurants[counter.current]);

  useEffect(() => {
    if (counter.current >= restaurants.length) {
      // send done message
      console.log("done swiping");
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

    window.onbeforeunload = () => {
      return true;
    };
    return () => {
      window.onbeforeunload = null;
    }
  })

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

  useEffect(() => {
    socket.current.onmessage = (msg) => {
      const data = JSON.parse(msg.data);
      switch (data.type) {
        case MESSAGE_TYPE.CONNECT:
          console.log("reached okay")
          // send a message with our username and room
          id.current = data.payload.id
          // send a message with our username and room
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
    socket.current.send(JSON.stringify(message));

    if (++counter.current < restaurants.length) {
      setCurrent(restaurants[counter.current]);
    } else {
      setWaiting(true);
    }
  }

  return (
    <>
      <div className="content">
        {waiting ?
          <div className="title-text">
            waiting for others
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
      <div className="exit-home">
        <Link to="/home" className="links">
          <i className="material-icons-outlined md-48">exit_to_app</i>
        </Link>
      </div>
    </>
  );
}

export default Swipe;
