import TextBox from "./TextBox";
import { useState } from "react";
import TopBar from "./TopBar";
import { useDispatch } from "react-redux";
import { login } from "../actions";
import axios from 'axios';

const SERVER_URL = 'http://localhost:4567';

function NewUser(props) {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [confirm, setConfirm] = useState("");
  const [error, setError] = useState("");
  const dispatch = useDispatch();

  const createUser = () => {
    if (password === confirm) {
      // register user in backend
      registerUser(username, password);

    } else {
      setError("Passwords do not match");
    }
  }

  const registerUser = (username, hash) => {
    const toSend = {
      // username and password
      username: username,
      password: password
    };
    let config = {
      headers: {
        'Content-Type': 'application/json',
        'Access-Control-Allow-Origin': '*',
      },
    };

    axios
      .post(`${SERVER_URL}/register`, toSend, config)
      .then((response) => {
        // if response is true, do this otherwise, say username already exists setError
        if (response.data["okay"]) {

          dispatch(login(username));
          props.history.push('/home');
          console.log("registered");
        } else {
          setError("Username already taken");
        }

      })
      .catch(function (error) {
        console.log(error);
      });
  }

  return (
    <>
      <TopBar to="/" showOptions={false} />
      <div className="login">
        <div className="title-text">join fork</div>
        <div className="login-error">
          {error}
        </div>
        <TextBox initial="username" change={setUsername} type="text" />
        <TextBox initial="password" change={setPassword} type="password" />
        <TextBox initial="confirm password" change={setConfirm} type="password" />
        <button className="primary-button" onClick={createUser}>
          join
        </button>
      </div>
    </>
  );
}

export default NewUser;