import TopBar from "./TopBar";

function Settings() {
  return (
    <>
      <TopBar to="/home" showOptions={false}/>
      <div className="content">
        <div className="title-text">
          settings
        </div>
      </div>
      <button className="secondary-button" id="delete">
        delete account
      </button>
    </>
  );
}

export default Settings;