import {useDispatch, useSelector} from "react-redux";
import {logout} from "../actions";
import {Link} from "react-router-dom";

function Personal(props) {
  const user = useSelector(state => state.user);
  const dispatch = useDispatch();
  const exit = () => {
    dispatch(logout());
    props.history.push('/');
  }
  return (
    <div className="content">
      <div className="title-text">profile</div>
      @{user}
      <br/>
      <Link to="/survey">
        <button className="primary-button">update preferences</button>
      </Link>
      <button className="secondary-button" onClick={exit}>log out</button>
    </div>
  );
}

export default Personal;