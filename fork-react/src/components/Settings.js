import TopBar from "./TopBar";
import TextBox from "./TextBox";
import {useState} from "react";
import {useDispatch, useSelector} from "react-redux";
import {logout} from "../actions";
import axios from "axios";
import Popup from "./Popup";

const SERVER_URL = 'http://localhost:4567';

/**
 * Renders settings page.
 */
function Settings() {
  const [old, setOld] = useState("");
  const [newPassword, setNew] = useState("");
  const [confirm, setConfirm] = useState("");
  const [error, setError] = useState("");
  const dispatch = useDispatch();
  const user = useSelector(state => state.user);
  const [pop, setPop] = useState(false);
  const [deletePop, setDelete] = useState(false);
  const [password, setPassword] = useState("");

  // checks for white spaces globally
  const hasWhiteSpace = (s) => {
    return /\s/g.test(s);
  }

  // changes password on back end
  const changePassword = () => {
    let valid = true;
    if (newPassword === old) {
      valid = false;
      setError("old and new passwords must be different");
    } else if (newPassword !== confirm) {
      valid = false;
      setError("passwords do not match");
    } else if (newPassword.length < 8) {
      valid = false;
      setError("password must be at least 8 characters long");
    } else if (hasWhiteSpace(newPassword)) {
      valid = false;
      setError("password can not contain white spaces");
    }
    if (valid) {
      validPassword(change, old);
    }
  }

  // validates new password
  const validPassword = (func, password) => {
    const toSend = {
      username: user,
      password: password
    };

    let config = {
      headers: {
        'Content-Type': 'application/json',
        'Access-Control-Allow-Origin': '*',
      },
    };

    axios.post(`${SERVER_URL}/login`, toSend, config)
      .then((response) => {
        if (response.data["success"]) {
          func();
        } else {
          setError("incorrect password");
        }
      })

      .catch(function (error) {
        console.log(error);
      });
  }

  // changes password on back end
  const change = () => {
    const toSend = {
      username: user,
      password: newPassword
    };
    let config = {
      headers: {
        'Content-Type': 'application/json',
        'Access-Control-Allow-Origin': '*',
      },
    };

    axios.post(`${SERVER_URL}/updatePwd`, toSend, config)
      .then((response) => {
        if (response.data["success"]) {
          setError("");
          togglePop();
        } else {
          setError("failed to update password");
        }
      })
      .catch(function (error) {
        console.log(error);
      });
  }

  // changes password on enter
  const submit = (e) => {
    const key = e.key;
    if (key === "Enter") {
      changePassword();
    }
  }

  // deletes account
  const deleteAccount = () => {
    validPassword(dataDelete, password);
  }

  // deletes user data
  const dataDelete = () => {
    const toSend = {
      username: user
    };

    let config = {
      headers: {
        'Content-Type': 'application/json',
        'Access-Control-Allow-Origin': '*',
      },
    };

    axios.post(`${SERVER_URL}/deleteUser`, toSend, config)
      .then((response) => {
        if (response.data["success"]) {
          alert("Account deleted successfully.");
          dispatch(logout());
        } else {
          alert(response.data["err"].substr(7));
        }

      })
      .catch((error) => {
        console.log(error);
      });
  }

  // toggles popup
  const togglePop = () => {
    setPop(old => !old);
  }

  // toggles delete box
  const toggleDelete = () => {
    setDelete(old => !old);
  }

  // redirects home
  const goHome = () => {
    window.location.href = "/home";
  }

  const enterDelete = (e) => {
    if (e.key === "Enter") {
      deleteAccount();
    }
  }

  return (
    <>
      <TopBar to="/home" showOptions={false}/>
      <div className="content">
        <div className="title-text">
          settings
        </div>
        change password
        <div className="login-error">{error}</div>
        <TextBox initial="old password" change={setOld} onKeyDown={submit} type="password" />
        <TextBox initial="new password" change={setNew} onKeyDown={submit} type="password" />
        <TextBox initial="confirm password" change={setConfirm} onKeyDown={submit} type="password" />
        <button className="primary-button" onClick={changePassword}>
          save
        </button>
      </div>
      {pop ? <Popup message="password changed successfully" click={goHome} button="home" toggle={togglePop}/> : null}
      {deletePop ?
        <Popup message={
          <>
            <div>enter password to delete account permanently</div>
            <div className="login-error">{error}</div>
            <TextBox initial="password" change={setPassword} onKeyDown={enterDelete} type="password"/>
          </>
        } click={deleteAccount} button="enter" toggle={toggleDelete}/> : null}
      <button className="secondary-button" id="delete" onClick={toggleDelete}>
        delete account
      </button>
    </>
  );
}

export default Settings;