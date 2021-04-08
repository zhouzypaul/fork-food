import { useRef, useState } from "react";
import TopBar from "./TopBar";
import { useSelector } from "react-redux";
import useUpdateUsers from "./useUpdateUsers"
import { useEffect } from "react";

function Host(props) {

  const roomCode = useRef(9999);
  const host = useRef(false);

  const roomProps = props.location.roomProps
  console.log(roomProps)
  if (roomProps) {
    roomCode.current = roomProps.roomCode
    host.current = roomProps.isHost
  }

  const user = useSelector(state => state.user);
  const userList = useUpdateUsers(roomCode, user);

  if (host.current) {
    return (
      <>
        <TopBar to="/home" showOptions={true} />
        <div className="content">
          <div className="title-text">
            share the code
          </div>
          <div className="code">{roomCode.current}</div>
          <button className="primary-button">start</button>
        </div>
        <div>{userList}</div>
      </>
    );
  } else {
    return (
      <>
        <TopBar to="/home" showOptions={true} />
        <div className="content">
          <div className="title-text">
            share the code
          </div>
          <div className="code">{roomCode.current}</div>
        </div>
        <div>{userList}</div>
      </>
    )
  }

}

export default Host;