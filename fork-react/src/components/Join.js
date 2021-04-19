import TopBar from "./TopBar";
import {useState} from "react";
import Code from "./Code";
import axios from "axios";

const SERVER_URL = 'http://localhost:4567';

/**
 * Renders join page
 */
function Join(props) {
  // values same as room code
  const [values, setValues] = useState("");
  const [error, setError] = useState(" ");

  // checks if room exists and is valid
  const exists = () => {
    const toSend = {
      code: values,
    };

    let config = {
      headers: {
        'Content-Type': 'application/json',
        'Access-Control-Allow-Origin': '*',
      },
    };

    axios.post(`${SERVER_URL}/verifyCode`, toSend, config)
      .then((response) => {
        if (!response.data['started']) {
          props.history.push({
            pathname: `room/${values}`,
            roomProps: {
              roomCode: values,
              isHost: false
            }
          });
        } else {
          setError("invalid room code");
        }
      })

      .catch(function (error) {
        console.log(error);
      });
  }

  return (
    <>
      <TopBar showOptions={true} to="/home" />
      <div className="content">
        <div className="title-text">enter code</div>
        <Code change={setValues} go={exists}/>
        <div className="join-error">
          {error}
        </div>
        <button className="primary-button" onClick={exists}>join</button>
      </div>
    </>
  );
}

export default Join;