import { Link } from "react-router-dom";
import Option from "./Option";
import Waiting from "./Waiting";
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

  const swipeProps = props.location.swipeProps;
  if (swipeProps) {
    roomId.current = swipeProps.roomId;
    id.current = swipeProps.id;
  }

  const user = useSelector(state => state.user);

  const [currentRestaurant, setCurrent] = useState(restaurants[counter.current]);

  // const { result } = useSwipeResults(roomId.current, user, socket.current);

  useEffect(() => {

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
    socket.current.onmessage = (msg) => {
      const data = JSON.parse(msg.data);
      console.log(data);
      switch (data.type) {
        case MESSAGE_TYPE.CONNECT:
          console.log("reached okay")
          // send a message with our username and room
          id.current = data.payload.id
          console.log(data)
          // send a message with our username and room
          sendInfo()
          break;
        case MESSAGE_TYPE.UPDATE:
          // check if we get a finished message then move to next page
          console.log("herehhhhh")
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

  }, [])

  console.log(counter.current)
  if (counter.current >= restaurants.length - 1) {
    // send done message
    console.log("dsfjsldjfkldsajfkskldj")
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
    }
  }

  return (
    <>
      <div className="content">
        <div className="choices">
          <button className="ex" onClick={() => sendDecision(0)}>&#x2715;</button>
          <Option restaurant={currentRestaurant} />
          <button className="check" onClick={() => sendDecision(1)}>&#x2713;</button>
        </div>
      </div>
      <div className="exit-home">
        <Link to="/home">
          <button className="secondary-button">
            leave
          </button>
        </Link>
      </div>
    </>
  );
}

export default Swipe;