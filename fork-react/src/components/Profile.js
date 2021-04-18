import TopBar from "./TopBar";
import Visited from "./Visited";
import Personal from "./Personal";
import {useSelector} from "react-redux";

/**
 * Renders profile page
 */
function Profile(props) {
  const user = useSelector(state => state.user);

  return (
    <>
      <TopBar to="/home" showOptions={false}/>
      <div className="profile">
        <Personal user={user} history={props.history}/>
        <Visited user={user}/>
      </div>
    </>
  );
}

export default Profile;