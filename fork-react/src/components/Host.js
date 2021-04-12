import { useRef, useState } from "react";
import TopBar from "./TopBar";
import { useSelector } from "react-redux";
import useUpdateUsers from "../hooks/useUpdateUsers"
import { useEffect } from "react";
import { Link } from "react-router-dom";

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
  const { users, restaurants, startSwiping } = useUpdateUsers(roomCode, user);

  // TODO: ternary operator
  if (host.current) {
    return (
      <>
        <TopBar to="/home" showOptions={true} />
        <div className="content">
          <div className="title-text">
            share the code
          </div>
          <div className="code">{roomCode.current}</div>
          <button className="primary-button" onClick={startSwiping}>start</button>
        </div>
        <div>{users}</div>
        <div>{restaurants}</div>
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
        <div>{users}</div>
      </>
    )
  }

}

export default Host;