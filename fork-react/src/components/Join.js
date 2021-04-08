import TopBar from "./TopBar";
import { useRef, useState } from "react";
import Code from "./Code";

function Join() {
  const [values, setValues] = useState("");
  console.log(values);

  const conn = new WebSocket("ws://localhost:4567/socket");
  const [id, setId] = useState();
  const [waitingRoom, setWaitingRoom] = useState([]);

  conn.onmessage = (e) => {
    let data = JSON.parse(e["data"])
    if (data.type === 0) {
      setId(data.payload.id)
    } else {
      setWaitingRoom(data.payload.id)
    }
  };

  return (
    <>
      <TopBar showOptions={true} to="/home" />
      <div className="content">
        <Code change={setValues} />
        <br />
        <button className="primary-button">join</button>
      </div>
    </>
  );
}

export default Join;