import { useRef } from "react";
import TopBar from "./TopBar";

function Host(props) {

  const roomCode = useRef(9999);

  const roomProps = props.location.roomProps
  if (roomProps) {
    roomCode.current = roomProps.roomCode
  }


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
    </>
  );
}

export default Host;