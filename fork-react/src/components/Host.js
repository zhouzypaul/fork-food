import {useEffect, useRef, useState} from "react";
import TopBar from "./TopBar";
import { useSelector } from "react-redux";
import useUpdateUsers from "../hooks/useUpdateUsers"
import Bubble from "./Bubble";

function Host(props) {
  const [active, setActive] = useState([]);
  const roomCode = useRef(9999);
  const host = useRef(false);
  const user = useSelector(state => state.user);
  const userList = useUpdateUsers(roomCode, user);

  const roomProps = props.location.roomProps
  if (roomProps) {
    roomCode.current = roomProps.roomCode
    host.current = roomProps.isHost
  }
  console.log(userList)

  const populate = () => {
    if (userList === undefined) {
      return;
    }
    const bubbles = [];
    let i = 0;
    for (let user of userList['users']) {
      bubbles.push(<Bubble user={user} key={i++}/>);
    }
    setActive(bubbles);
  }

  useEffect(populate, [userList]);

  return (
    <>
      <TopBar to="/home" showOptions={true} />
      <div className="content">
        <div className="title-text">
          share the code
        </div>
        <div className="code">{roomCode.current}</div>
        {host.current ? <button className="primary-button">start</button>: <></>}
        <div className="joined">
          {active}
        </div>
      </div>
    </>
  );
}

export default Host;