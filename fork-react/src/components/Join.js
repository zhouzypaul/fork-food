import TopBar from "./TopBar";
import {useEffect, useRef, useState} from "react";
import Code from "./Code";
import { Link } from "react-router-dom";
import axios from "axios";

const SERVER_URL = 'http://localhost:4567';

function Join(props) {
  // values same as room code
  const [values, setValues] = useState("");
  const [error, setError] = useState(" ");

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
        if (response.data['exists']) {
          props.history.push({
            pathname: `room${values}`,
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
        <Code change={setValues} />
        <div className="join-error">
          {error}
        </div>
        <button className="primary-button" onClick={exists}>join</button>
      </div>
    </>
  );
}

export default Join;