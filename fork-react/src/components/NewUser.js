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

  const hasWhiteSpace = (s) => {
    return /\s/g.test(s);
  }

  const createUser = () => {
    let valid = true;
    if (password !== confirm) {
      valid = false;
      setError("passwords do not match");
    } else if (password.length < 8) {
      valid = false;
      setError("password must be at least 8 characters long");
    } else if (hasWhiteSpace(password)) {
      valid = false;
      setError("password can not contain white spaces");
    } else if (hasWhiteSpace(username)) {
      valid = false;
      setError("username can not contain white spaces");
    } else if (username.length === 0) {
      valid = false;
      setError("username can not be blank");
    }
    if (valid) {
      registerUser(username, password);
    }
  }


  const registerUser = (username, password) => {
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
        if (response.data["success"]) {
          dispatch(login(username));
          props.history.push('/survey');
          console.log("registered");
        } else {
          setError(response.data["err"]);
        }

      })
      .catch((error) => {
        console.log(error);
      });
  }

  const submit = (e) => {
    const key = e.key;
    if (key === "Enter") {
      createUser();
    }
  }

  return (
    <>
      <TopBar to="/" showOptions={false} />
      <div className="login">
        <div className="title-text">join fork</div>
        <div className="login-error">
          {error}
        </div>
        <TextBox initial="username" change={setUsername} onKeyDown={submit} type="text" />
        <TextBox initial="password" change={setPassword} onKeyDown={submit} type="password" />
        <TextBox initial="confirm password" change={setConfirm} onKeyDown={submit} type="password" />
        <button className="primary-button" onClick={createUser}>
          join
        </button>
      </div>
    </>
  );
}

export default NewUser;