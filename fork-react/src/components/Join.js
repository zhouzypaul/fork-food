import TopBar from "./TopBar";
import { useRef, useState } from "react";
import Code from "./Code";
import { Link } from "react-router-dom";

function Join() {
  // values same as room code
  const [values, setValues] = useState("");
  console.log(values);

  return (
    <>
      <TopBar showOptions={true} to="/home" />
      <div className="content">
        <Code change={setValues} />
        <br />
        <Link to={`/join${values}`}>
          <button className="primary-button">join</button>
        </Link>

      </div>
    </>
  );
}

export default Join;