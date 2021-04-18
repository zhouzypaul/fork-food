import {useDispatch} from "react-redux";
import {logout} from "../actions";
import {Link} from "react-router-dom";

/**
 * Renders personal information portion of profile.
 */
function Personal(props) {
  const dispatch = useDispatch();

  // logout user
  const exit = () => {
    dispatch(logout());
    sessionStorage.clear();
    props.history.push('/');
  }

  return (
    <div className="content" id="profile-section">
      <div className="title-text">@{props.user}</div>
      <br/>
      <Link to="/survey">
        <button className="primary-button">update preferences</button>
      </Link>
      <button className="secondary-button" onClick={exit}>log out</button>
    </div>
  );
}

export default Personal;