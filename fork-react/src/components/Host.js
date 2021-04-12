import { useRef, useState } from "react";
import TopBar from "./TopBar";
import { useSelector } from "react-redux";
import useUpdateUsers from "../hooks/useUpdateUsers"

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
  // naming convention little iffy here
  const { users, restaurants, sock, startSwiping } = useUpdateUsers(roomCode, user);
  console.log(restaurants.length)
  if (restaurants.length !== 0) {
    props.history.push({
      pathname: `/swipe`,
      swipeProps: {
        restaurants: restaurants,
        socket: sock
      }
    })
  }

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