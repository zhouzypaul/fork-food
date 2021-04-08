import TopBar from "./TopBar";
import { useState, useRef } from "react";
import { conn } from "../index.js";

function Host() {
  const code = Math.floor(1000 + Math.random() * 9000);

  const [id, setId] = useState();
  const waitingRoom = useRef([]);

  conn.onmessage = (e) => {
    let data = JSON.parse(e["data"])
    if (data.type === 0) {
      setId(data.payload.id)
    } else {
      waitingRoom.current = data.payload.id
    }
  };

  return (
    <>
      <TopBar to="/home" showOptions={true} />
      <div className="content">
        <div className="title-text">
          share the code
        </div>
        <div className="code">{code}</div>
        <button className="primary-button">start</button>
      </div>
      <div>{waitingRoom.current}</div>
    </>
  );
}

export default Host;