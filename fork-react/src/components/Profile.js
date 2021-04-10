import TopBar from "./TopBar";
import Visited from "./Visited";
import Personal from "./Personal";

function Profile(props) {
  return (
    <>
      <TopBar to="/home" showOptions={false}/>
      <div className="profile">
        <Personal history={props.history}/>
        <Visited/>
      </div>
    </>
  );
}

export default Profile;